package com.justbelieveinmyself.portscanner.core;

public interface ScanningResultCallback {

    void prepareForResults(ScanningResult result);

    void consumeResults(ScanningResult result);

}
