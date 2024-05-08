package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.exporters.CSVExporter;
import com.justbelieveinmyself.portscanner.feeders.FeederCreator;
import com.justbelieveinmyself.portscanner.feeders.RangeFeederGUI;
import com.justbelieveinmyself.portscanner.fetchers.FilteredPortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.IPFetcher;
import com.justbelieveinmyself.portscanner.fetchers.PortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.WinMACFetcher;

public class ComponentRegistry {

    public void register(Injector i) {
        i.register(IPFetcher.class, WinMACFetcher.class, PortsFetcher.class, FilteredPortsFetcher.class);
        StateMachine stateMachine = new StateMachine();
        i.register(FeederCreator.class, new RangeFeederGUI());
        i.register(StateMachine.class, stateMachine);
        i.register(CSVExporter.class);
    }

}
