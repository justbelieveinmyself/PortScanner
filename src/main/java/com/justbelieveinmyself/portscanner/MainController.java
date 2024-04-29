package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.*;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.feeders.Feeder;
import com.justbelieveinmyself.portscanner.feeders.RangeFeeder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class MainController {
    private final Logger LOG = Logger.getLogger(MainController.class.getName());

    @FXML
    private Button startButton;

    private Injector injector = new Injector();

    {
        new ComponentRegistry().register(injector);
    }

    private StateMachine stateMachine;

    @FXML
    private TextField endIPInput;

    @FXML
    private TextField startIPInput;

    @FXML
    private ResultTable ipTableView;

    @FXML
    private Label resultLabel;

    @FXML
    void startScanning(ActionEvent event) {
        LOG.info("Starting scan..");
        RangeFeeder rangeFeeder = new RangeFeeder(startIPInput.getText(), endIPInput.getText());

        new StartStopScanning(injector.require(ScannerDispatcherThreadFactory.class),
                ipTableView, stateMachine, startButton, rangeFeeder, resultLabel).scan();
    }

    @FXML
    void test(ActionEvent event) {
        LOG.info("Test");
    }

    @FXML
    void initialize() {
        stateMachine = injector.require(StateMachine.class);
        ipTableView.initialize(injector.require(ScanningResultList.class), stateMachine);
        startIPInput.setText("192.168.0.0");
        endIPInput.setText("192.168.0.255");

    }


    public static class StartStopScanning implements StateTransitionListener {
        private Logger LOG = Logger.getLogger(StartStopScanning.class.getSimpleName());
        private ScannerDispatcherThreadFactory scannerDispatcherThreadFactory;
        private ScannerDispatcherThread scannerThread;
        private ResultTable resultTable;
        private StateMachine stateMachine;
        private Button button;
        private Label resultLabel;
        private Feeder feeder;

        StartStopScanning(ScannerDispatcherThreadFactory scannerDispatcherThreadFactory, ResultTable resultTable, StateMachine stateMachine, Button button, RangeFeeder rangeFeeder, Label resultLabel) {
            this.scannerDispatcherThreadFactory = scannerDispatcherThreadFactory;
            this.resultTable = resultTable;
            this.stateMachine = stateMachine;
            this.button = button;
            this.feeder = rangeFeeder;
            stateMachine.addTransitionListener(this);
            button.setDisable(true);
            this.resultLabel = resultLabel;
        }

        public void scan() {
            stateMachine.transitionToNext();
        }

        @Override
        public void transitionTo(ScanningState state, Transition transition) {
            Platform.runLater(() -> {
                switch (state) {
                    case IDLE:
                        resultLabel.setText("Готово!");
                        button.setDisable(false);
                        break;
                    case STARTING:
                    case RESTARTING:
                        if (transition != Transition.CONTINUE)
                            resultTable.getItems().clear();
                        try {
                            scannerThread = scannerDispatcherThreadFactory.createScannerThread(feeder, createResultsCallback(state));
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

                button.setText("Стоп");
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

}

