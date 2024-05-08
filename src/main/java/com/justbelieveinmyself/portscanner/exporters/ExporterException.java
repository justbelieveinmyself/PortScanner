package com.justbelieveinmyself.portscanner.exporters;

public class ExporterException extends RuntimeException {
    public ExporterException() {
        super();
    }

    public ExporterException(String message) {
        super(message);
    }

    public ExporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
