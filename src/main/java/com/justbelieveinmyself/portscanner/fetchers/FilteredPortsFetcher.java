package com.justbelieveinmyself.portscanner.fetchers;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.values.NotScanned;
import com.justbelieveinmyself.portscanner.core.values.NumericRangeList;

import java.util.SortedSet;

/**
 * Использует результат сканирования PortsFetcher'a для отображения фильтрованных портов
 */
public class FilteredPortsFetcher extends PortsFetcher {

    public FilteredPortsFetcher(ScannerConfig config) {
        super(config);
    }

    @Override
    public String getId() {
        return "fetcher.ports.filtered";
    }

    @Override
    public Object scan(ScanningSubject subject) {
        boolean portsScanned = scanPorts(subject);

        if (!portsScanned) {
            return NotScanned.VALUE;
        }

        SortedSet<Integer> filteredPorts = getFilteredPorts(subject);
        return !filteredPorts.isEmpty() ? new NumericRangeList(filteredPorts, displayAsRange) : null;
    }

}
