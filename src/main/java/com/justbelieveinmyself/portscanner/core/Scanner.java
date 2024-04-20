package com.justbelieveinmyself.portscanner.core;

import java.util.logging.Logger;

/**
 * Функциональность сканера заключена в этом классе
 * Для выполнения сканирование используется список сборщиков (fetchers)
 */
public class Scanner {
    private static final Logger LOG = Logger.getLogger(Scanner.class.getName());

    /**
     * Выполняет все
     * @param subject содержит IP-адрес для сканирования
     * @param result будет содержать результаты
     */
    public void scan(ScanningSubject subject, ScanningResult result) {
        int fetcherIndex = 0;
        boolean isScanningInterrupted = false;
    }
}
