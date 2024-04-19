package com.justbelieveinmyself.portscanner.core.values;

import com.justbelieveinmyself.portscanner.config.Config;

/**
 * Значение для отображения в списке результатов, означает что текущее значение неизвестно
 * потому что не доступен
 */
public class NotAvailable extends Empty {
    public static final NotAvailable VALUE = new NotAvailable();

    private NotAvailable() {}

    @Override
    public String toString() {
        return Config.getConfig().forScanner().notAvailableText;
    }

    @Override
    public int compareTo(Object that) {
        // N/A < N/S
        if (that == NotScanned.VALUE) {
            return -sortDirection;
        }
        return super.compareTo(that);
    }
}
