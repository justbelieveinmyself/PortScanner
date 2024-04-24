package com.justbelieveinmyself.portscanner.di;

public class InjectorException extends RuntimeException {
    public InjectorException() {
        super();
    }

    public InjectorException(String message) {
        super(message);
    }

    public InjectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectorException(Throwable cause) {
        super(cause);
    }

}
