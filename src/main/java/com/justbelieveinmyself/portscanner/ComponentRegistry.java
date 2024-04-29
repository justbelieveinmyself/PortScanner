package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.fetchers.FilteredPortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.PortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.WinMACFetcher;

public class ComponentRegistry {

    public void register(Injector i) {
        i.register(WinMACFetcher.class, PortsFetcher.class, FilteredPortsFetcher.class);
        StateMachine stateMachine = new StateMachine();
        i.register(StateMachine.class, stateMachine);
    }

}
