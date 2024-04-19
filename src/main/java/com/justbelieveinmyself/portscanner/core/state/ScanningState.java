package com.justbelieveinmyself.portscanner.core.state;

/**
 * Все возможные состояния сканера
 */
public enum ScanningState {
    IDLE, STARTING, SCANNING, STOPPING, KILLING, RESTARTING;

    ScanningState next() {
        switch (this) {
            case IDLE:
                return STARTING;
            case STARTING:
                return SCANNING;
            case SCANNING:
                return STOPPING;
            case STOPPING:
                return KILLING;
            case RESTARTING:
                return RESTARTING;
            default:
                return null;
        }
    }

}
