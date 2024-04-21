package com.justbelieveinmyself.portscanner.core.fetchers;

import com.justbelieveinmyself.portscanner.config.Config;
import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.feeders.Feeder;

public abstract class AbstractFetcher implements Fetcher{

    @Override
    public String getFullName() {
        return "";
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public void init() {

    }

    @Override
    public void cleanUp() {

    }

}
