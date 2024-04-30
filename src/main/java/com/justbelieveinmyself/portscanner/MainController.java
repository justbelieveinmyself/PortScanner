package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.*;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.feeders.FeederCreator;
import com.justbelieveinmyself.portscanner.feeders.RangeFeederGUI;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Logger;

public class MainController {
    private final Logger LOG = Logger.getLogger(MainController.class.getName());

    @FXML
    private Button startButton;

    @FXML
    private TextField endIPInput;

    @FXML
    private TextField startIPInput;

    @FXML
    private ResultTable resultTable;

    @FXML
    private Label resultLabel;

    private Injector injector = new Injector();

    {
        injector.register(Button.class, startButton);
        injector.register(ResultTable.class, resultTable);
        injector.register(Label.class, resultLabel);
        new ComponentRegistry().register(injector);
    }

    private StateMachine stateMachine;
    private StartStopScanning startStopScanning;
    private FeederCreator feederCreator = injector.require(FeederCreator.class);

    @FXML
    void startStopScanning(ActionEvent event) {
        LOG.info("Starting scan..");

        startStopScanning.nextState();
    }

    @FXML
    void initialize() {
        stateMachine = injector.require(StateMachine.class);
        resultTable.initialize(injector.require(ScanningResultList.class), stateMachine);
        startIPInput.setText("192.168.0.0");
        endIPInput.setText("192.168.0.255");
        if (feederCreator instanceof RangeFeederGUI) {
            RangeFeederGUI rangeFeederGUI = (RangeFeederGUI) feederCreator;
            rangeFeederGUI.init(startIPInput, endIPInput);
        }

        this.startStopScanning = new StartStopScanning(injector.require(ScannerDispatcherThreadFactory.class), resultTable, stateMachine, startButton, feederCreator, resultLabel);
        injector.register(StartStopScanning.class, startStopScanning);
    }

    @FXML
    private void openSettings(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings-view.fxml"));
            Parent root = loader.load();
            Stage settingsStage = new Stage();

            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.setTitle("Настройки");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
            settingsStage.initStyle(StageStyle.UTILITY);
            settingsStage.setScene(scene);
            settingsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
//TODO: пофиксить создание потоками больше строк, чем их обрабатывают
