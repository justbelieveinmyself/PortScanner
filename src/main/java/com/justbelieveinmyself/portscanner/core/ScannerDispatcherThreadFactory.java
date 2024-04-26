package com.justbelieveinmyself.portscanner.core;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.feeders.Feeder;

public class ScannerDispatcherThreadFactory {
    private ScanningResultList scanningResults;
    private Scanner scanner;
    private StateMachine stateMachine;
    private ScannerConfig scannerConfig;

    public ScannerDispatcherThreadFactory(ScanningResultList scanningResults, Scanner scanner, StateMachine stateMachine, ScannerConfig scannerConfig) {
        this.scanningResults = scanningResults;
        this.scanner = scanner;
        this.stateMachine = stateMachine;
        this.scannerConfig = scannerConfig;
    }

    public ScannerDispatcherThread createScannerThread(Feeder feeder) {
        return new ScannerDispatcherThread(feeder, scanner, stateMachine, scanningResults, scannerConfig);
    }

}
