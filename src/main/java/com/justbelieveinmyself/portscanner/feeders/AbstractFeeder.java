package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;

import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.getInterface;
import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.matchingAddress;

public abstract class AbstractFeeder implements Feeder {
    private NetworkInterface netIf;
    private InterfaceAddress ifAddr;

    protected void initInterfaces(InetAddress ip) {
        this.netIf = getInterface(ip);
        this.ifAddr = matchingAddress(netIf, ip.getClass());
    }

    @Override
    public boolean isLocalNetwork() {
        return ifAddr != null;
    }

    @Override
    public ScanningSubject subject(InetAddress ip) {
        return new ScanningSubject(ip, netIf, ifAddr);
    }

    @Override
    public String toString() {
        return getId() + ": " + getInfo();
    }

}
