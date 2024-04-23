package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.feeders.Feeder;
import com.justbelieveinmyself.portscanner.fetchers.Fetcher;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistry;
import com.justbelieveinmyself.portscanner.fetchers.MACFetcher;
import com.justbelieveinmyself.portscanner.core.values.NotAvailable;
import com.justbelieveinmyself.portscanner.core.values.NotScanned;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Функциональность сканера заключена в этом классе
 * Для выполнения сканирование используется список сборщиков (fetchers)
 */
public class Scanner {
    private static final Logger LOG = Logger.getLogger(Scanner.class.getName());
    private FetcherRegistry fetcherRegistry;
    private Map<Long, Fetcher> activeFetchers = new ConcurrentHashMap<>();

    public Scanner(FetcherRegistry fetcherRegistry) {
        this.fetcherRegistry = fetcherRegistry;
    }

    /**
     * Запускает все зарегистрированные сборщики для текущего IP адреса
     * @param subject содержит IP-адрес для сканирования
     * @param result  будет содержать результаты
     */
    public void scan(ScanningSubject subject, ScanningResult result) {
        int fetcherIndex = 0;
        boolean isScanningInterrupted = false;
        for (Fetcher fetcher : fetcherRegistry.getRegisteredFetchers()) {
            Object value = NotScanned.VALUE;
            try {
                activeFetchers.put(Thread.currentThread().getId(), fetcher);
                if (!subject.isAddressAborted() && !isScanningInterrupted) {
                    value = fetcher.scan(subject);

                    isScanningInterrupted = Thread.currentThread().isInterrupted();
                    if (value == null)
                        value = isScanningInterrupted ? NotScanned.VALUE : NotAvailable.VALUE;
                }
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "", e);
            }

            result.setValue(fetcherIndex, value);
            fetcherIndex++;
        }
        result.setMac((String) subject.getParameter(MACFetcher.ID));
        activeFetchers.remove(Thread.currentThread().getId());

        result.setType(subject.getResultType());
    }

    public void interrupt(Thread thread) {
        Fetcher fetcher = activeFetchers.get(thread.getId());
        if (fetcher != null) fetcher.cleanUp();
    }

    /**
     * Инициализирует все для сканирования
     */
    public void init(Feeder feeder) {
        for (Fetcher fetcher : fetcherRegistry.getRegisteredFetchers()) {
            fetcher.init(feeder);
        }
    }

    /**
     * Очистка после сканирования
     */
    public void cleanUp() {
        activeFetchers.clear();
        for (Fetcher fetcher : fetcherRegistry.getRegisteredFetchers()) {
            fetcher.cleanUp();
        }
    }

}
