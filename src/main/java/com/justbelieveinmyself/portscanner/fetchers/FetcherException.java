package com.justbelieveinmyself.portscanner.fetchers;

import com.justbelieveinmyself.portscanner.core.UserErrorException;

public class FetcherException extends UserErrorException {
    public FetcherException(String message, Throwable cause) {
        super(message, cause);
    }
}
