package com.justbelieveinmyself.portscanner.core.fetchers;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.net.WinIpHlpDll;
import com.justbelieveinmyself.portscanner.core.net.WinIpHlpDll.IpAddrByVal;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.net.Inet4Address;
import java.net.InetAddress;

import static com.justbelieveinmyself.portscanner.core.net.WinIpHlpDll.dll;

public class WinMACFetcher extends MACFetcher{

    @Override
    protected String resolveMAC(ScanningSubject subject) {
        if (!(subject.getAddress() instanceof Inet4Address)) return null;

        Pointer pmac = new Memory(8);
        Pointer plen = new Memory(4);
        plen.setInt(0, 8);

        int result = dll.SendARP(toIpAddr(subject.getAddress()), 0, pmac, plen);

        if (result != 0) return null;

        byte[] bytes = pmac.getByteArray(0, plen.getInt(0));
        return bytesToMAC(bytes);
    }

    public static IpAddrByVal toIpAddr(InetAddress address) {
        IpAddrByVal addr = new IpAddrByVal();
        addr.bytes = address.getAddress();
        return addr;
    }

}
