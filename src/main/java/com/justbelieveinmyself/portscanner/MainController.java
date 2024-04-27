package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.ScannerDispatcherThread;
import com.justbelieveinmyself.portscanner.core.ScannerDispatcherThreadFactory;
import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.feeders.Feeder;
import com.justbelieveinmyself.portscanner.feeders.RangeFeeder;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistry;
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
        ipTableView.getItems().add(new Device("192.168.1.2", "00:11:22:33:44:55", "80,443", "80"));
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
        ipTableView.initialize(injector.require(FetcherRegistry.class), injector.require(ScanningResultList.class), injector.require(StateMachine.class));
        stateMachine = new StateMachine();
        startIPInput.setText("192.168.0.0");
        endIPInput.setText("192.168.0.255");

        ipTableView.getItems().add(new Device("192.168.1.1", "00:11:22:33:44:55", "80,443", "80"));
    }


    public static class StartStopScanning implements StateTransitionListener {
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
                resultTable.getItems().clear();
            try {
                scannerThread = scannerDispatcherThreadFactory.createScannerThread(feeder);
                stateMachine.startScanning();

            } catch (RuntimeException e) {
                stateMachine.reset();
                throw e;
            }
        }

        @Override
        public void transitionTo(ScanningState state, StateMachine.Transition transition) {
            switch (state) {
                case IDLE:
                    button.setDisable(false);
                    break;
                case STARTING:

                case RESTARTING:
                    if (transition != StateMachine.Transition.CONTINUE)
                        resultTable.getItems().clear();
                    try {
                        scannerThread = scannerDispatcherThreadFactory.createScannerThread(feeder);
                        stateMachine.startScanning();

                    } catch (RuntimeException e) {
                        stateMachine.reset();
                        throw e;
                    }
                    break;
                case SCANNING:
                    scannerThread.start();
                    break;
                case STOPPING:
                    resultLabel.setText("Ожидания потоков");
                    break;
                case KILLING:
                    button.setDisable(true);
                    resultLabel.setText("Завершение потоков");
                    break;
            }
            button.setText("пук");
        }
    }
}
