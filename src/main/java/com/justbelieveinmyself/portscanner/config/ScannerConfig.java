package com.justbelieveinmyself.portscanner.config;

import java.util.prefs.Preferences;

/**
 * Хранит конфигурацию сканера
 */
public class ScannerConfig {
    Preferences preferences;
    public int maxThreads;
    public int threadDelay;
    public boolean skipBroadcastAddresses;
    public int portTimeout;
    public String portString;
    public boolean useRequestedPorts;
    public String notAvailableText;
    public String notScannedText;
    public boolean askConfirmation;
    public boolean showInfo;

    ScannerConfig() {
        preferences = Preferences.userNodeForPackage(ScannerConfig.class);
        loadConfig();
    }

    private void loadConfig() {
        maxThreads = preferences.getInt("maxThreads", 50);
        threadDelay = preferences.getInt("threadDelay", 20);
        skipBroadcastAddresses = preferences.getBoolean("skipBroadcastAddresses", true);
        askConfirmation = preferences.getBoolean("askConfirmation", true);
        showInfo = preferences.getBoolean("showInfo", true);
        portTimeout = preferences.getInt("portTimeout", 2000);
        portString = preferences.get("portString", "440-450");
        useRequestedPorts = preferences.getBoolean("useRequestedPorts", true);
        notAvailableText = preferences.get("notAvailableText", "Значения не доступны");
        notScannedText = preferences.get("notScannedText", "Не сканировано");
    }

    public void saveConfig() {
        preferences.putInt("maxThreads", maxThreads);
        preferences.putInt("threadDelay", threadDelay);
        preferences.putBoolean("skipBroadcastAddresses", skipBroadcastAddresses);
        preferences.putBoolean("askConfirmation", askConfirmation);
        preferences.putBoolean("showInfo", showInfo);
        preferences.putInt("portTimeout", portTimeout);
        preferences.put("portString", portString);
        preferences.put("notAvailableText", notAvailableText);
        preferences.put("notScannedText", notScannedText);
    }

    private static class ConfigHolder {
        static final ScannerConfig INSTANCE = new ScannerConfig();
    }

    public static ScannerConfig getConfig() {
        return ConfigHolder.INSTANCE;
    }

}
//TODO tostring device, show detials in maincontroller