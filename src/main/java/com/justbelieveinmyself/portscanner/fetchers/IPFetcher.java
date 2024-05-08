package com.justbelieveinmyself.portscanner.fetchers;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.values.InetAddressHolder;

public class IPFetcher extends AbstractFetcher{
    public static final String ID = "IP";
    @Override
    public String getName() {
        return getId();
    }

    @Override
    public Object scan(ScanningSubject subject) {
        return new InetAddressHolder(subject.getAddress());
    }

    @Override
    public String getId() {
        return ID;
    }

}
