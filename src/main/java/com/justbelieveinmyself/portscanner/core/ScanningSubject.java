package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.ScanningResult.ResultType;
import com.justbelieveinmyself.portscanner.core.net.PingResult;
import com.justbelieveinmyself.portscanner.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.*;

import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.matchingAddress;

/**
 * ScanningSubject представляет один отсканированный объект
 * IP адрес и любые доп. параметры, которые могут использоваться
 * для кэширования промежуточных данных между различными сборщиками
 */
public class ScanningSubject {
    ScannerConfig config;
    private InetAddress address;
    private NetworkInterface netIf;
    private InterfaceAddress ifAddr;
    private List<Integer> requestedPorts;
    private Map<String, Object> params;
    private ResultType resultType = ResultType.UNKNOWN;
    private boolean isAborted = false;
    int adaptedPortTimeout = -1;

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
        this.config = ScannerConfig.getConfig();
        this.params = new HashMap<>();
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

    public boolean hasParameter(String name) {
        return params.containsKey(name);
    }

    public Object getParameter(String name) {
        return params.get(name);
    }

    public void setParameter(String name, Object value) {
        params.put(name, value);
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

    public ResultType getResultType() {
        return resultType;
    }

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    public int getAdaptedPortTimeout() {
        // если уже рассчитан раннее
        if (adaptedPortTimeout > 0) {
            return adaptedPortTimeout;
        }

        // попытка настроить тайм-аут, если доступны результаты пингования
        PingResult pingResult = (PingResult) getParameter("pinger");
        if (pingResult != null) {
            adaptedPortTimeout = Math.min(Math.max(pingResult.getLongestTime() * 3, config.minPortTimeout), config.portTimeout);
            return adaptedPortTimeout;
        }

        // если нет результатов пингования возвращаем обычный тайм-аут
        return config.portTimeout;
    }

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
