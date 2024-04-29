package com.justbelieveinmyself.portscanner.core.values;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;

/**
 * Значение для отображения в списке результатов, означает что текущее значение неизвестно
 * потому что он ещё не сканирован
 */
public class NotScanned extends Empty {
    public static final NotScanned VALUE = new NotScanned();

    private NotScanned() {}

    @Override
    public String toString() {
        return ScannerConfig.getConfig().notScannedText;
    }

    @Override
    public int compareTo(Object that) {
        // N/S > N/A
        if (that == NotAvailable.VALUE) return sortDirection;
        return super.compareTo(that);
    }

}
