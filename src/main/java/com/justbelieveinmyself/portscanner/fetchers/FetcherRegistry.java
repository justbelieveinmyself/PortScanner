package com.justbelieveinmyself.portscanner.fetchers;

import java.util.*;
import java.util.prefs.Preferences;

public class FetcherRegistry {
    static final String PREFERENCE_SELECTED_FETCHERS = "selectedFetchers";
    private final Preferences preferences;
    private Map<String, Fetcher> registeredFetchers;
    private Map<String, Fetcher> selectedFetchers;
    private List<FetcherRegistryUpdateListener> updateListeners = new ArrayList<>();

    public FetcherRegistry(List<Fetcher> fetchers, Preferences preferences) {
        this.preferences = preferences;

        registeredFetchers = createFetchersMap(fetchers);

        loadSelectedFetchers(preferences);
    }

    private Map<String, Fetcher> createFetchersMap(List<Fetcher> fetchers) {
        Map<String, Fetcher> registeredFetchers = new LinkedHashMap<>(fetchers.size());
        for (Fetcher fetcher : fetchers) {
            registeredFetchers.put(fetcher.getFullName(), fetcher); // or id?
        }

        return Collections.unmodifiableMap(registeredFetchers);
    }

    private void loadSelectedFetchers(Preferences preferences) {
        String fetcherPrefValue = preferences.get(PREFERENCE_SELECTED_FETCHERS, null);
        if (fetcherPrefValue == null) {
            selectedFetchers = new LinkedHashMap<>();
            //TODO: добавить сборщиков по умолчанию
        } else {
            String[] fetcherPrefs = fetcherPrefValue.split("###");
            selectedFetchers = new LinkedHashMap<>(registeredFetchers.size());

            for (String fetcherPref : fetcherPrefs) {
                Fetcher fetcher = registeredFetchers.get(fetcherPref);

                if (fetcher != null) {
                    selectedFetchers.put(fetcherPref, fetcher);
                }
            }
        }
    }

    private void saveSelectedFetchers(Preferences preferences) {
        StringBuilder sb = new StringBuilder();
        for (String fetcherName : selectedFetchers.keySet()) {
            sb.append(fetcherName).append("###");
        }
        String value = sb.toString();
        if (value.endsWith("###")) value = value.substring(0, value.length() - 3);

        preferences.put(PREFERENCE_SELECTED_FETCHERS, value);
    }

    /**
     * Добавляет слушателя для наблюдений за событиями FetcherRegistry (изменение выбранных сборщиков)
     */
    public void addListener(FetcherRegistryUpdateListener listener) {
        updateListeners.add(listener);
    }

    /**
     * @return коллекцию выбранных сборщиков
     */
    public Collection<Fetcher> getRegisteredFetchers() {
        return registeredFetchers.values();
    }

    /**
     * Ищет по айди выбранный сборщик
     * @return индекс, если сборщик найден, или -1
     */
    public int getSelectedFetcherIndex(String id) {
        int index = 0;
        for (Fetcher fetcher : selectedFetchers.values()) {
            if (id.equals(fetcher.getFullName())) {
                return index;
            }
            index++;
        }
        return -1;
    }

}
