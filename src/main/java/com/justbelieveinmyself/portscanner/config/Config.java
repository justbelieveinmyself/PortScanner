package com.justbelieveinmyself.portscanner.config;

import java.util.UUID;
import java.util.prefs.Preferences;

/**
 * Синглтон класс, хранящий предпочтения программы
 */
public final class Config {
    private Preferences preferences;
    public String uuid;
    private ScannerConfig scannerConfig;
//    private FavouritesConfig favouritesConfig;

    Config() {
        preferences = Preferences.userRoot().node("portscanner");
        scannerConfig = new ScannerConfig(preferences);
//        favouritesConfig = new FavouritesConfig(preferences);
        uuid = preferences.get("uuid", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            preferences.put("uuid", uuid);
        }
    }

    private static class ConfigHolder {
        static final Config INSTANCE = new Config();
    }

    public static Config getConfig() {
        return ConfigHolder.INSTANCE;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public ScannerConfig forScanner() {
        return scannerConfig;
    }

    public String getUuid() {
        return uuid;
    }

}
