package com.justbelieveinmyself.portscanner.fetchers;

import java.util.*;

public class FetcherRegistry {
    private Map<String, Fetcher> registeredFetchers;

    public FetcherRegistry(List<Fetcher> fetchers) {
        registeredFetchers = createFetchersMap(fetchers);
    }

    private Map<String, Fetcher> createFetchersMap(List<Fetcher> fetchers) {
        Map<String, Fetcher> fetcherMap = new LinkedHashMap<>(fetchers.size());
        for (Fetcher fetcher : fetchers) {
            fetcherMap.put(fetcher.getId(), fetcher);
        }
        return Collections.unmodifiableMap(fetcherMap);
    }

    /**
     * @return коллекцию всех существующих сборщиков
     */
    public Collection<Fetcher> getRegisteredFetchers() {
        return registeredFetchers.values();
    }

}
