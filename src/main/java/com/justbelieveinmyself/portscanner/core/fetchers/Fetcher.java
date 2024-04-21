package com.justbelieveinmyself.portscanner.core.fetchers;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.feeders.Feeder;
import com.justbelieveinmyself.portscanner.core.values.NotAvailable;
import com.justbelieveinmyself.portscanner.core.values.NotScanned;

/**
 * Интерфейс для всех IP сборщиков
 *
 * Сборщик отвечает за сбор определенного типа информации о предоставленном объекте
 * сканирования.
 *
 * Сборщики собирают актуальную информацию о каждом сканируемом IP адресе
 *
 * Реализации должны быть потокобезопасными и не иметь состояния
 */
public interface Fetcher extends Cloneable {
    
    /**
     * @return полное имя
     */
    String getFullName();

    /**
     * @return информацию о сборщике
     */
    String getInfo();

    /**
     * Проводит сборку
     * @param subject сканируемый объект, содержит IP адрес
     * @return извлеченные данные (String в большинстве случаев), null в случае ошибки
     * Специальные значения, такие как {@link NotAvailable} или {@link NotScanned}
     */
    Object scan(ScanningSubject subject);

    /**
     * Вызывается перед началом сканированием для проведения каких-то действий по инициализации
     */
    default void init(Feeder feeder) {
        init();
    }

    default void init() {}

    /**
     * Вызывается после завершения сканирования для выполнения очистки
     */
    void cleanUp();

    /**
     * @return уникальный идентификатор
     */
    String getId();
    // getName() ?
}
