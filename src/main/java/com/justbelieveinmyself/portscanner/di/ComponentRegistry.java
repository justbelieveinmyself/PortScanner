package com.justbelieveinmyself.portscanner.di;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.fetchers.FilteredPortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.PortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.WinMACFetcher;

import java.util.prefs.Preferences;

public class ComponentRegistry {

    public void register(Injector i) {
        i.register(Preferences.class, Preferences.userRoot());
        i.register(WinMACFetcher.class, PortsFetcher.class, FilteredPortsFetcher.class);
    }

}
