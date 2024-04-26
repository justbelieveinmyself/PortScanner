package com.justbelieveinmyself.portscanner.config;

import java.util.prefs.Preferences;

/**
 * Хранит конфигурацию сканера
 */
public class ScannerConfig {
    private Preferences preferences;

    public int maxThreads;
    public int threadDelay;
    public boolean scanDeadHosts;
    public String selectedPinger;
    public int pingTimeout;
    public int pingCount;
    public boolean skipBroadcastAddresses;
    public int portTimeout;
//    public boolean adaptPortTimeout;
    public int minPortTimeout;
    public String portString;
    public boolean useRequestedPorts;
    public String notAvailableText;
    public String notScannedText;

    public ScannerConfig(Preferences preferences) {
        this.preferences = preferences;

        maxThreads = preferences.getInt("maxThreads", 50);
        threadDelay = preferences.getInt("threadDelay", 20);
        boolean scanDeadHosts = preferences.getBoolean("scanDeadHosts", false);
        //selectedPinger? java>windows
        pingTimeout = preferences.getInt("pingTimeout", 2000);
        pingCount = preferences.getInt("pingCount", 3);
        skipBroadcastAddresses = preferences.getBoolean("skipBroadcastAddresses", true);
        portTimeout = preferences.getInt("portTimeout", 2000);
//        adaptPortTimeout ?
        minPortTimeout = preferences.getInt("minPortTimeout", 100);
        portString = preferences.get("portString", "80,443,8080");
        useRequestedPorts = preferences.getBoolean("userRequestedPorts", true);
        notAvailableText = "Not available";
        notScannedText = "Not scanned";
    }

    public void store() {
//        preferences.putInt("maxThreads", maxThreads);
        preferences.putInt("threadDelay", threadDelay);
        preferences.putBoolean("scanDeadHosts", scanDeadHosts);
//        preferences.put("selectedPinger", selectedPinger);
        preferences.putInt("pingTimeout", pingTimeout);
        preferences.putInt("pingCount", pingCount);
        preferences.putBoolean("skipBroadcastAddresses", skipBroadcastAddresses);
        preferences.putInt("portTimeout", portTimeout);
//        preferences.putBoolean("adaptPortTimeout", adaptPortTimeout);
        preferences.putInt("minPortTimeout", minPortTimeout);
        preferences.put("portString", portString);
        preferences.putBoolean("useRequestedPorts", useRequestedPorts);
        preferences.put("notAvailableText", notAvailableText);
        preferences.put("notScannedText", notScannedText);
    }
}
