package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.ScannerDispatcherThread;
import com.justbelieveinmyself.portscanner.core.ScannerDispatcherThreadFactory;
import com.justbelieveinmyself.portscanner.core.ScanningResult;
import com.justbelieveinmyself.portscanner.core.ScanningResultCallback;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.feeders.FeederCreator;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.logging.Logger;

import static com.justbelieveinmyself.portscanner.core.state.ScanningState.*;
import static com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition.INIT;

public class StartStopScanning implements StateTransitionListener {
    private Logger LOG = Logger.getLogger(StartStopScanning.class.getSimpleName());
    private ScannerDispatcherThreadFactory scannerDispatcherThreadFactory;
    private ScannerDispatcherThread scannerThread;
    private ResultTable resultTable;
    private StateMachine stateMachine;
    private Button button;
    private Label resultLabel;
    private FeederCreator feederCreator;
    String[] buttonTexts = new String[ScanningState.values().length];

    public StartStopScanning(ScannerDispatcherThreadFactory scannerDispatcherThreadFactory, ResultTable resultTable, StateMachine stateMachine, Button button, FeederCreator feederCreator, Label resultLabel) {
        this.scannerDispatcherThreadFactory = scannerDispatcherThreadFactory;
        this.resultTable = resultTable;
        this.stateMachine = stateMachine;
        this.button = button;
        this.feederCreator = feederCreator;
        stateMachine.addTransitionListener(this);
        this.resultLabel = resultLabel;

        buttonTexts[IDLE.ordinal()] = "Начать";
        buttonTexts[SCANNING.ordinal()] = "Остановить";
        buttonTexts[STARTING.ordinal()] = buttonTexts[SCANNING.ordinal()];
        buttonTexts[RESTARTING.ordinal()] = buttonTexts[SCANNING.ordinal()];
        buttonTexts[STOPPING.ordinal()] = "Остановить!";
        buttonTexts[KILLING.ordinal()] = buttonTexts[STOPPING.ordinal()];

        ScanningState state = stateMachine.getState();
        button.setText(buttonTexts[state.ordinal()]);
    }

    public void nextState() {
        stateMachine.transitionToNext();
    }

    @Override
    public void transitionTo(ScanningState state, StateMachine.Transition transition) {
        if (transition == INIT) {
            return;
        }

        Platform.runLater(() -> {
            switch (state) {
                case IDLE:

                    resultLabel.setText("Готово!");
                    button.setDisable(false);
                    break;

                case STARTING:

                    if (transition != StateMachine.Transition.CONTINUE)
                        resultTable.getItems().clear();
                    try {
                        scannerThread = scannerDispatcherThreadFactory.createScannerThread(feederCreator.createFeeder(), createResultsCallback(state));
                        stateMachine.startScanning();
                    } catch (RuntimeException e) {
                        stateMachine.reset();
                        throw e;
                    }
                    break;

                case RESTARTING:

                    try {
                        scannerThread = scannerDispatcherThreadFactory.createScannerThread(feederCreator.createFeeder(), createResultsCallback(state));
                        stateMachine.startScanning();
                    } catch (RuntimeException e) {
                        stateMachine.reset();
                        throw e;
                    }
                    break;

                case SCANNING:

                    scannerThread.start();
                    resultLabel.setText("Создание потоков.."); //TODO: какой поток работает
                    break;

                case STOPPING:

                    resultLabel.setText("Ожидание потоков..");
                    break;

                case KILLING:

                    button.setDisable(true);
                    resultLabel.setText("Завершение потоков..");
                    break;
            }
            button.setText(buttonTexts[state.ordinal()]);
        });
    }

    private ScanningResultCallback createResultsCallback(ScanningState state) {
        return new ScanningResultCallback() {
            public void prepareForResults(ScanningResult result) {
                resultTable.addOrUpdateResultRow(result);
                LOG.info("Подготовка к получению результата: " + result.toString());
            }

            public void consumeResults(ScanningResult result) {
                resultTable.addOrUpdateResultRow(result);
                LOG.info("Получен результат: " + result.toString());
            }
        };
    }
}