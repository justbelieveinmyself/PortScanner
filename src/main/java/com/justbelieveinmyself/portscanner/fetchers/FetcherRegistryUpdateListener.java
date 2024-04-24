package com.justbelieveinmyself.portscanner.fetchers;

/**
 * Интерфейс для реакции на изменения FetcherRegistry
 */
public interface FetcherRegistryUpdateListener {

    /**
     * Этот метод вызывается когда список выбранных сборщиков изменился
     */
    void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry);

}
