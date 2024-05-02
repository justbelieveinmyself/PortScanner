package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.feeders.Feeder;

public class ScannerDispatcherThreadFactory {
    private ScanningResultList scanningResults;
    private Scanner scanner;
    private StateMachine stateMachine;

    public ScannerDispatcherThreadFactory(ScanningResultList scanningResults, Scanner scanner, StateMachine stateMachine) {
        this.scanningResults = scanningResults;
        this.scanner = scanner;
        this.stateMachine = stateMachine;
    }

    public ScannerDispatcherThread createScannerThread(Feeder feeder, ScanningResultCallback resultsCallback, ScanningProgressCallback progressCallback) {
        return new ScannerDispatcherThread(feeder, scanner, stateMachine, scanningResults, resultsCallback, progressCallback);
    }

}
