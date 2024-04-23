package com.justbelieveinmyself.portscanner.fetchers;

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
