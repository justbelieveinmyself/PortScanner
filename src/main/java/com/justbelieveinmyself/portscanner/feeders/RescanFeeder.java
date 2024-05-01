package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RescanFeeder extends AbstractFeeder{
    private Feeder originalFeeder;
    private List<InetAddress> address;
    int current;

    public RescanFeeder(Feeder originalFeeder, String... ips) {
        this.originalFeeder = originalFeeder;
        initAddresses(ips);
    }


    @Override
    public boolean hasNext() {
        return current < address.size();
    }

    @Override
    public ScanningSubject next() {
        return originalFeeder.subject(address.get(current++));
    }

    @Override
    public int percentageComplete() {
        return current * 100 / address.size();
    }

    @Override
    public String getInfo() {
        return originalFeeder.getInfo();
    }

    @Override
    public String getId() {
        return originalFeeder.getId();
    }

    @Override
    public String getName() {
        return originalFeeder.getName();
    }

    private int initAddresses(String... ips) {
        if (ips.length == 0) {
            throw  new IllegalArgumentException("IP-адреса не выбраны");
        }
        try {
            address = new ArrayList<>(ips.length);
            for (String ip : ips) {
                address.add(InetAddress.getByName(ip));
            }
        } catch (UnknownHostException e) {
            throw new FeederException("malformedIP: " + Arrays.toString(ips));
        }
        return ips.length;
    }

    @Override
    public boolean isLocalNetwork() {
        return originalFeeder.isLocalNetwork();
    }
}
