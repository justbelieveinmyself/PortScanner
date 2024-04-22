package com.justbelieveinmyself.portscanner.core.feeders;

public class FeederException extends RuntimeException {

    public FeederException(String message) {
        super(message);
    }

    public FeederException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeederException(Throwable cause) {
        super(cause);
    }

}
