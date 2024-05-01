package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.core.UserErrorException;

public class FeederException extends UserErrorException {
    public FeederException(String message) {
        super(message);
    }
}
