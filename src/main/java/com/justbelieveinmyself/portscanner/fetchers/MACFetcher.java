package com.justbelieveinmyself.portscanner.fetchers;

import com.justbelieveinmyself.portscanner.core.ScanningSubject;

import java.util.regex.Pattern;

public abstract class MACFetcher extends AbstractFetcher {
    public static final String ID = "fetcher.mac";
    static final Pattern macAddressPattern = Pattern.compile("([a-fA-F0-9]{1,2}[-:]){5}[a-fA-F0-9]{1,2}");
    String separator = ":";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public final String scan(ScanningSubject subject) {
        String mac = (String) subject.getParameter(ID);
        if (mac == null) mac = resolveMAC(subject);
        subject.setParameter(ID, mac);
        return replaceSeparator(mac);
    }

    protected abstract String resolveMAC(ScanningSubject subject);

    static String bytesToMAC(byte[] bytes) {
        StringBuilder mac = new StringBuilder();
        for (byte b : bytes) {
            mac.append(String.format("%02x", b)).append(":");
        }
        if (mac.length() > 0) mac.deleteCharAt(mac.length() - 1);
        return mac.toString();
    }

    String replaceSeparator(String mac) {
        return mac != null ? mac.replace(":", separator) : null;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

}
