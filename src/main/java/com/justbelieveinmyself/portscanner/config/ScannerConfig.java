package com.justbelieveinmyself.portscanner.config;

/**
 * Хранит конфигурацию сканера
 */
public class ScannerConfig {
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
        maxThreads = 50;
        threadDelay = 20;
        skipBroadcastAddresses = true;
        portTimeout = 2000;
        portString = "440-450";
        useRequestedPorts = true;
        notAvailableText = "Значения не доступны (нет результатов)";
        notScannedText = "Не сканировано";
    }

    private static class ConfigHolder {
        static final ScannerConfig INSTANCE = new ScannerConfig();
    }

    public static ScannerConfig getConfig() {
        return ConfigHolder.INSTANCE;
    }

}
