package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.feeders.Feeder;
import com.justbelieveinmyself.portscanner.fetchers.Fetcher;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistry;

import java.net.InetAddress;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

/**
 * Содержит результаты сканирований
 */
public class ScanningResultList implements Iterable<ScanningResult> {
    private static final int RESULT_LIST_INITIAL_SIZE = 1024;
    private FetcherRegistry fetcherRegistry;
    /** Выбранные сборщики закэшированы здесь, потому что они могут измениться в ходе работы */
    private List<Fetcher> selectedFetchers = emptyList();

    private List<ScanningResult> resultList = new ArrayList<>(RESULT_LIST_INITIAL_SIZE);
    private Map<InetAddress, Integer> resultIndexes = new HashMap<>(RESULT_LIST_INITIAL_SIZE);

    /** Информация о сборщике, который использовался сканированием */
    private String feederInfo;
    private String feederName;
    /** Информация и статистика о последнем скане */
    ScanInfo info;

    private ScanningResultComparator resultComparator = new ScanningResultComparator();

    public ScanningResultList(FetcherRegistry fetcherRegistry) {
        this.fetcherRegistry = fetcherRegistry;
    }

    public ScanningResultList(FetcherRegistry fetcherRegistry, StateMachine stateMachine) {
        this(fetcherRegistry);
        stateMachine.addTransitionListener(new StopScanningListener());
    }

    public synchronized void initNewScan(Feeder feeder) {
        selectedFetchers = new ArrayList<>(fetcherRegistry.getRegisteredFetchers());

        this.feederInfo = feeder.getInfo();
        this.feederName = feeder.getName();

        this.info = new ScanInfo();
    }

    public ScanInfo getScanInfo() {
        return info;
    }

    public boolean areResultsAvailable() {
        return !resultList.isEmpty();
    }

    public boolean isInfoAvailable() {
        return info != null;
    }

    public String getFeederInfo() {
        return feederInfo;
    }

    public String getFeederName() {
        return feederName;
    }

    public synchronized ScanningResult createResult(InetAddress address) {
        info.numScanned++;
        Integer index = resultIndexes.get(address);
        if (index == null) {
            return new ScanningResult(address, fetcherRegistry.getRegisteredFetchers().size());
        }
        return resultList.get(index);
    }

    public synchronized void registerAtIndex(int index, ScanningResult result) {
        if (resultIndexes.put(result.getAddress(), index) != null) {
            throw new IllegalStateException(result.getAddress() + " is already registered in the list");
        }

        result.resultList = this;
        resultList.add(index, result);

        if (result.isReady()) {
            updateStatistics(result);
        }
    }

    public synchronized boolean isRegistered(ScanningResult result) {
        return resultIndexes.containsKey(result.getAddress());
    }

    public synchronized int update(ScanningResult result) {
        if (result.isReady()) {
            updateStatistics(result);
        }

        return resultIndexes.get(result.getAddress());
    }

    public synchronized void clear() {
        resultList.clear();
        resultIndexes.clear();
    }

    public synchronized ScanningResult getResult(int index) {
        return resultList.get(index);
    }

    public synchronized void remove(int[] indexes) {

        List<ScanningResult> newList = new ArrayList<>(RESULT_LIST_INITIAL_SIZE);
        Map<InetAddress, Integer> newMap = new HashMap<>(RESULT_LIST_INITIAL_SIZE);
        for (int i = 0; i < resultList.size(); i++) {
            if (Arrays.binarySearch(indexes, i) < 0) {
                newList.add(resultList.get(i));
                newMap.put(resultList.get(i).getAddress(), newList.size()-1);
            }
        }
        resultList = newList;
        resultIndexes = newMap;
    }

    public synchronized void sort(int columnIndex, boolean ascending) {
        resultComparator.byIndex(columnIndex, ascending);
        resultList.sort(resultComparator);

        resultIndexes = new HashMap<>(RESULT_LIST_INITIAL_SIZE);
        for (int i = 0; i < resultList.size(); i++) {
            resultIndexes.put(resultList.get(i).getAddress(), i);
        }
    }

    private void updateStatistics(ScanningResult result) {
        if (info == null) {
            return;
        }
        if (result.getType() == ScanningResult.ResultType.ALIVE) {
            info.numAlive++;
        } else if (result.getType() == ScanningResult.ResultType.WITH_PORTS) {
            info.numAlive++;
            info.numWithPorts++;
        }
    }

    public int getFetcherIndex(String fetcherId) {
        int index = 0;
        for (Fetcher fetcher : getFetchers()) {
            if (fetcherId.equals(fetcher.getId())) return index;
            index++;
        }
        return -1;
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
            return scanFinished && !scanAborted;
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
