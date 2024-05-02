package com.justbelieveinmyself.portscanner.core;

import java.net.InetAddress;

public interface ScanningProgressCallback {

    void updateProgress(InetAddress currentAddress, int runningThreads, int percentageComplete);

}
