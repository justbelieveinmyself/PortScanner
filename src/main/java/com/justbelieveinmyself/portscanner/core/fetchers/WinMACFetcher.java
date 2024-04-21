package com.justbelieveinmyself.portscanner.core.fetchers;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.net.Inet4Address;

public class WinMACFetcher extends MACFetcher{

    @Override
    protected String resolveMAC(ScanningSubject subject) {
        if (!(subject.getAddress() instanceof Inet4Address)) return null;

        Pointer pmac = new Memory(8);
        Pointer plen = new Memory(4);
        plen.setInt(0, 8);

        //TODO: continue
        return null;
    }

}
