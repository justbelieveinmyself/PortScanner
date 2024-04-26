package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.feeders.Feeder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.justbelieveinmyself.portscanner.core.state.ScanningState.KILLING;
import static com.justbelieveinmyself.portscanner.core.state.ScanningState.SCANNING;
import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.isLikelyBroadcast;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScannerDispatcherThread extends Thread implements ThreadFactory, StateTransitionListener {
    private static final long UI_UPDATE_INTERVAL_MS = 150;

    private ScannerConfig config;
    private Scanner scanner;
    private StateMachine stateMachine;
    private ScanningResultList scanningResultList;
    private Feeder feeder;

    private AtomicInteger numActiveThreads = new AtomicInteger();
    ThreadGroup threadGroup;
    ExecutorService threadPool;

    public ScannerDispatcherThread(Feeder feeder, Scanner scanner, StateMachine stateMachine, ScanningResultList scanningResults, ScannerConfig scannerConfig) {
        setName(getClass().getSimpleName());
        this.config = scannerConfig;
        this.stateMachine = stateMachine;
        this.threadGroup = new ThreadGroup(getName());
        this.threadPool = Executors.newFixedThreadPool(config.maxThreads, this);

        // сделаем процессом, чтоб при закрытии программы пользователем JVM автоматически прерывала
        setDaemon(true);

        this.feeder = feeder;
        this.scanner = scanner;
        this.scanningResultList = scanningResults;
        try {
            this.scanningResultList.initNewScan(feeder);
            scanner.init(feeder);
        } catch (RuntimeException e) {
            stateMachine.reset();
            throw e;
        }
    }

    @Override
    public void run() {
        try {
            stateMachine.addTransitionListener(this);
            long lastNotifyTime = 0;

            try {
                ScanningSubject subject = null;
                while (feeder.hasNext() && stateMachine.inState(SCANNING)) {
                    //небольшая задержка перед созданием потока
                    Thread.sleep(config.threadDelay);

                    if ((numActiveThreads.intValue() < config.maxThreads)) {
                        subject = feeder.next();

                        if (config.skipBroadcastAddresses && isLikelyBroadcast(subject.getAddress(), subject.getIfAddr())) {
                            continue;
                        }

                        ScanningResult result = scanningResultList.createResult(subject.getAddress());

                        AddressScannerTask scanningTask = new AddressScannerTask(subject, result);
                        threadPool.execute(scanningTask);
                    }

                    long now = System.currentTimeMillis();
                    if (now - lastNotifyTime >= UI_UPDATE_INTERVAL_MS && subject != null) {
                        lastNotifyTime = now;
                    }
                }
            } catch (InterruptedException e) {
                //окончание цикла
            }

            stateMachine.stop();

            threadPool.shutdown();

            try {
                while (!threadPool.awaitTermination(UI_UPDATE_INTERVAL_MS, MILLISECONDS)) {
                    //update progress?
                }
            } catch (InterruptedException e) {
                //окончание цикла
            }
            scanner.cleanUp();

            stateMachine.complete();
        } finally {
            stateMachine.removeTransitionListener(this);
        }
    }

    @Override
    public void transitionTo(ScanningState state, StateMachine.Transition transition) {
        if (state == KILLING) {
            threadGroup.interrupt();
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(threadGroup, r) {
            {
                setDaemon(true);
            }
            @Override
            public void interrupt() {
                scanner.interrupt(this);
                super.interrupt();
            }
        };
    }

    class AddressScannerTask implements Runnable {
        private ScanningSubject subject;
        private ScanningResult result;

        public AddressScannerTask(ScanningSubject subject, ScanningResult result) {
            this.subject = subject;
            this.result = result;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(getClass().getSimpleName() + ": " + subject);

            try {
                scanner.scan(subject, result);
                //consume results?
            } finally {
                numActiveThreads.decrementAndGet();
            }

        }

    }

}
