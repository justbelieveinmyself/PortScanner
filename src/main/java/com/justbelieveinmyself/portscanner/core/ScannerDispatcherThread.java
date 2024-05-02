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
import java.util.logging.Logger;

import static com.justbelieveinmyself.portscanner.core.state.ScanningState.KILLING;
import static com.justbelieveinmyself.portscanner.core.state.ScanningState.SCANNING;
import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.isLikelyBroadcast;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScannerDispatcherThread extends Thread implements ThreadFactory, StateTransitionListener {
    private final Logger LOG = Logger.getLogger(ScannerDispatcherThread.class.getSimpleName());
    private static final long UI_UPDATE_INTERVAL_MS = 150;

    private ScannerConfig config;
    private Scanner scanner;
    private StateMachine stateMachine;
    private ScanningResultList scanningResultList;
    private Feeder feeder;

    private AtomicInteger numActiveThreads = new AtomicInteger();
    ThreadGroup threadGroup;
    ExecutorService threadPool;

    private ScanningResultCallback resultsCallback;
    private ScanningProgressCallback progressCallback;

    public ScannerDispatcherThread(Feeder feeder, Scanner scanner, StateMachine stateMachine, ScanningResultList scanningResults, ScanningResultCallback resultsCallback, ScanningProgressCallback progressCallback) {
        setName(getClass().getSimpleName());
        this.config = ScannerConfig.getConfig();
        this.stateMachine = stateMachine;
        this.threadGroup = new ThreadGroup(getName());
        this.threadPool = Executors.newFixedThreadPool(config.maxThreads, this);
        this.resultsCallback = resultsCallback;
        this.progressCallback = progressCallback;

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
                LOG.info("Попытка сканирования, текущее состояние: " + stateMachine.getState());
                while (feeder.hasNext() && stateMachine.inState(SCANNING)) {
                    //небольшая задержка перед созданием потока
                    Thread.sleep(config.threadDelay);

                    if ((numActiveThreads.intValue() < config.maxThreads)) {
                        subject = feeder.next();

                        if (config.skipBroadcastAddresses && isLikelyBroadcast(subject.getAddress(), subject.getIfAddr())) {
                            continue;
                        }

                        ScanningResult result = scanningResultList.createResult(subject.getAddress());
                        resultsCallback.prepareForResults(result);

                        AddressScannerTask scanningTask = new AddressScannerTask(subject, result);
                        threadPool.execute(scanningTask);
                    }

                    long now = System.currentTimeMillis();
                    if (now - lastNotifyTime >= UI_UPDATE_INTERVAL_MS && subject != null) {
                        lastNotifyTime = now;
                        progressCallback.updateProgress(subject.getAddress(), numActiveThreads.intValue(), feeder.percentageComplete());
                    }
                }
            } catch (InterruptedException e) {
                //окончание цикла
            }

            stateMachine.stop();

            threadPool.shutdown();

            try {
                while (!threadPool.awaitTermination(UI_UPDATE_INTERVAL_MS, MILLISECONDS)) {
                    progressCallback.updateProgress(null, numActiveThreads.intValue(), 100);
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
            numActiveThreads.incrementAndGet();
        }

        @Override
        public void run() {
            Thread.currentThread().setName(getClass().getSimpleName() + ": " + subject);

            try {
                scanner.scan(subject, result);
                resultsCallback.consumeResults(result);
            } finally {
                numActiveThreads.decrementAndGet();
            }

        }

    }

}
