package com.justbelieveinmyself.portscanner.core.net;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;

import java.io.IOException;

/**
 * Pinger'ы проверяют живы ли хосты
 */
public interface Pinger extends AutoCloseable{

    @Override
    default void close() throws Exception {}

    /**
     * Отправляет указанное количество запросов и ожидает ответов
     * @param count количество запросов, которое нужно выполнить
     */
    PingResult ping(ScanningSubject subject, int count) throws IOException;

}
