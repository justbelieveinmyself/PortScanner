package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.*;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.feeders.FeederCreator;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.InetAddress;
import java.util.logging.Logger;

import static com.justbelieveinmyself.portscanner.core.state.ScanningState.*;
import static com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition.INIT;

public class StartStopScanning implements StateTransitionListener, ScanningProgressCallback {
    private Logger LOG = Logger.getLogger(StartStopScanning.class.getSimpleName());
    private ScannerDispatcherThreadFactory scannerDispatcherThreadFactory;
    private ScannerDispatcherThread scannerThread;
    private ResultTable resultTable;
    private StateMachine stateMachine;
    private Button button;
    private Label resultLabel;
    private ProgressBar progressBar;
    private Label threadsLabel;
    private FeederCreator feederCreator;
    String[] buttonTexts = new String[ScanningState.values().length];

    public StartStopScanning(ScannerDispatcherThreadFactory scannerDispatcherThreadFactory, ResultTable resultTable, StateMachine stateMachine, Button button, FeederCreator feederCreator, Label resultLabel, ProgressBar progressBar, Label threadsLabel) {
        this.scannerDispatcherThreadFactory = scannerDispatcherThreadFactory;
        this.resultTable = resultTable;
        this.progressBar = progressBar;
        this.threadsLabel = threadsLabel;
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
        if (stateMachine.inState(IDLE)) {
            if (!preScanChecks())
                return;
        }
        stateMachine.transitionToNext();
    }

    private boolean preScanChecks() {
        if (ScannerConfig.getConfig().askConfirmation && !resultTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Новое сканирование");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите сбросить результаты предыдущего сканирования?");
            alert.initStyle(StageStyle.DECORATED);

            ButtonType confirmButton = new ButtonType("Подтвердить");
            ButtonType cancelButton = new ButtonType("Отмена");
            alert.getButtonTypes().setAll(confirmButton, cancelButton);

            alert.showAndWait();
            return alert.getResult() == confirmButton;
        }

        return true;
    }

    @Override
    public void transitionTo(ScanningState state, StateMachine.Transition transition) {
        if (transition == INIT) {
            return;
        }

        Platform.runLater(() -> {
            switch (state) {
                case IDLE:

                    updateProgress(null, 0, 0);
                    resultLabel.setText("Готово!");
                    button.setDisable(false);
                    break;

                case STARTING:

                    if (transition != StateMachine.Transition.CONTINUE) {
                        resultTable.resetSelection();
                    }

                    try {
                        scannerThread = scannerDispatcherThreadFactory.createScannerThread(feederCreator.createFeeder(), createResultsCallback(state), StartStopScanning.this);
                        stateMachine.startScanning();
                    } catch (RuntimeException e) {
                        stateMachine.reset();
                        throw e;
                    }
                    break;

                case RESTARTING:

                    try {
                        scannerThread = scannerDispatcherThreadFactory.createScannerThread(feederCreator.createRescanFeeder(resultTable.getSelectionModel().getSelectedItems()), createResultsCallback(state), StartStopScanning.this);
                        stateMachine.startScanning();
                    } catch (RuntimeException e) {
                        stateMachine.reset();
                        throw e;
                    }
                    break;

                case SCANNING:

                    scannerThread.start();
                    resultLabel.setText("Создание потоков..");
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

    public void updateProgress(final InetAddress currentAddress, final int runningThreads, final int percentageComplete) {
        Platform.runLater(() -> {
            Stage stage = (Stage) button.getScene().getWindow();
            if (currentAddress != null) {
                stage.setTitle("Scanner " + currentAddress.getHostAddress());
            } else {
                stage.setTitle("Scanner");
            }
            threadsLabel.setText("Потоков: " + runningThreads);
            progressBar.setProgress((double) percentageComplete / 100);


        });
    }

}