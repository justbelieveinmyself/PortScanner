package com.justbelieveinmyself.portscanner.config;

/**
 * Хранит конфигурацию сканера
 */
public class ScannerConfig {
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

    ScannerConfig() {
        maxThreads = 50;
        threadDelay = 20;
        boolean scanDeadHosts = false;
        pingTimeout = 2000;
        pingCount = 3;
        skipBroadcastAddresses = true;
        portTimeout = 2000;
//        adaptPortTimeout ?
        minPortTimeout = 100;
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
