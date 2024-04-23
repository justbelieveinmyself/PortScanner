package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.fetchers.Fetcher;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;

import java.net.InetAddress;
import java.util.*;
import java.util.function.Consumer;

/**
 * Содержит результаты сканирований
 */
public class ScanningResultList implements Iterable<ScanningResult> {
    private static final int RESULT_LIST_INITIAL_SIZE = 1024;
    /** Выбранные сборщики закэшированы здесь, потому что они могут измениться в ходе работы */
    private List<Fetcher> selectedFetchers = Collections.emptyList();

    private List<ScanningResult> resultList = new ArrayList<>(RESULT_LIST_INITIAL_SIZE);
    private Map<InetAddress, Integer> resultIndexes = new HashMap<>(RESULT_LIST_INITIAL_SIZE);

    /** Информация о сборщике, который использовался сканированием */
    private String feederInfo;
    private String feederName;
    /** Информация и статистика о последнем скане */
    ScanInfo info;

    private ScanningResultComparator resultComparator = new ScanningResultComparator();

    public ScanningResultList() {
        //TODO: registry?
    }

    public ScanningResultList(StateMachine stateMachine) {
        this();
        stateMachine.addTransitionListener(new StopScanningListener());
    }

    /**
     * @return итератор результатов сканирования (не синхронизированный)
     */
    @Override
    public synchronized Iterator<ScanningResult> iterator() {
        return resultList.iterator();
    }


    @Override
    public void forEach(Consumer<? super ScanningResult> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<ScanningResult> spliterator() {
        return Iterable.super.spliterator();
    }

    public List<Fetcher> getFetchers() {
        return selectedFetchers;
    }

    public static class ScanInfo {
        protected boolean scanFinished;
        protected boolean scanAborted;

        protected long startTime = System.currentTimeMillis();
        protected long endTime;
        protected int numScanned;
        protected int numAlive;
        protected int numWithPorts;

        /**
         * @return итоговое время сканирования, в миллисекундах
         * Если сканирование не окончено, то возвращает время прошедшее с начала сканирования
         */
        public long getScanTime() {
            long endTime = this.endTime;
            if (endTime == 0) {
                endTime = System.currentTimeMillis();
            }
            return endTime - startTime;
        }

        /**
         * @return итоговое количество сканированных хостов
         */
        public int getHostCount() {
            return numScanned;
        }

        /**
         * @return количество живых хостов
         */
        public int getAliveCount() {
            return numAlive;
        }

        /**
         * @return количество хостов с открытыми проверяемыми портами
         */
        public int getWithPortsCount() {
            return numWithPorts;
        }

        /**
         * @return true, если сканирование прошло успешно (не прервано)
         */
        public boolean isCompletedNormally() {
            return scanAborted && !scanAborted;
        }

    }

    class StopScanningListener implements StateTransitionListener {

        @Override
        public void transitionTo(ScanningState state, Transition transition) {
            synchronized (ScanningResultList.this) {
                if (transition == Transition.COMPLETE && state == ScanningState.IDLE) {
                    info.endTime = System.currentTimeMillis();
                    info.scanFinished = true;
                } else if (state == ScanningState.KILLING) {
                    info.scanAborted = true;
                }
            }
        }

    }

}
