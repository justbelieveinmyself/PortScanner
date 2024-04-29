package com.justbelieveinmyself.portscanner.fetchers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FetcherRegistry {
    private Map<String, Fetcher> registeredFetchers;

    public FetcherRegistry() {
        registeredFetchers = Collections.unmodifiableMap(loadSelectedFetchers());
    }

    private Map<String, Fetcher> loadSelectedFetchers() {
        Map<String, Fetcher> selectedFetchers = new LinkedHashMap<>();
        selectedFetchers.put(MACFetcher.ID, new WinMACFetcher());
        selectedFetchers.put(PortsFetcher.ID, new PortsFetcher());
        selectedFetchers.put(FilteredPortsFetcher.ID, new FilteredPortsFetcher());
        return selectedFetchers;
    }

    /**
     * @return коллекцию всех существующих сборщиков
     */
    public Collection<Fetcher> getRegisteredFetchers() {
        return registeredFetchers.values();
    }

}
