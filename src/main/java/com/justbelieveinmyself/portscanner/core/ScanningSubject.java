package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.config.Config;
import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.*;

import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.matchingAddress;

public class ScanningSubject {
    ScannerConfig config;
    private InetAddress address;
    private NetworkInterface netIf;
    private InterfaceAddress ifAddr;
    private List<Integer> requestedPorts;
    //    private Map<String, Object> params;
//    private ResultType resultType = ResultType.UNKNOWN; ?
    private boolean isAborted = false;
//    int adaptedPortTimeout = -1;

    public ScanningSubject(InetAddress address) {
        this(address, InetAddressUtils.getInterface(address));
    }


    public ScanningSubject(InetAddress address, NetworkInterface netIf) {
        this(address, netIf, matchingAddress(netIf, address.getClass()));
    }

    public ScanningSubject(InetAddress address, NetworkInterface netIf, InterfaceAddress ifAddr) {
        this.address = address;
        this.netIf = netIf;
        this.ifAddr = ifAddr;
        this.config = Config.getConfig().forScanner();
    }

    public boolean isLocal() {
        return ifAddr != null;
    }

    public boolean isLocalHost() {
        return address.equals(ifAddr.getAddress());
    }

    public void abortAddressScanning() {
        this.isAborted = true;
    }

    public boolean isAnyPortRequested() {
        return requestedPorts != null;
    }

    public Iterator<Integer> requestedPortsIterator() {
        return requestedPorts == null ? null : requestedPorts.iterator();
    }

    public void addRequestedPort(Integer requestedPort) {
        if (requestedPorts == null) {
            requestedPorts = new ArrayList<>();
        }
        requestedPorts.add(requestedPort);
    }

    public InetAddress getAddress() {
        return address;
    }

    public boolean isAddressAborted() {
        return isAborted;
    }

    public NetworkInterface getInterface() {
        return netIf;
    }

    public InterfaceAddress getIfAddr() {
        return ifAddr;
    }

    //getAdaptedPortTimeout?

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(address.getHostAddress());
        if (requestedPorts != null) {
            sb.append(':');
            for (Integer port : requestedPorts) {
                sb.append(port).append(',');
            }
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

}
