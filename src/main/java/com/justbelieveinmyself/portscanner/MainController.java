package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.*;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.feeders.FeederCreator;
import com.justbelieveinmyself.portscanner.feeders.RangeFeederGUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Logger;

import static com.justbelieveinmyself.portscanner.MainApplication.showAlertMessage;

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

    @FXML
    private Label hostnameLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label threadsLabel;

    private static Injector injector = new Injector();

    {
        injector.register(Button.class, startButton);
        injector.register(ResultTable.class, resultTable);
        injector.register(Label.class, resultLabel);
        new ComponentRegistry().register(injector);
    }

    public static Injector getInjector() {
        return injector;
    }

    private StateMachine stateMachine;
    private StartStopScanning startStopScanning;
    private FeederCreator feederCreator = injector.require(FeederCreator.class);

    @FXML
    void startStopScanning(ActionEvent event) {
        startStopScanning.nextState();
    }

    @FXML
    void initialize() {
        stateMachine = injector.require(StateMachine.class);
        resultTable.initialize(injector.require(ScanningResultList.class), stateMachine);
        if (feederCreator instanceof RangeFeederGUI) {
            RangeFeederGUI rangeFeederGUI = (RangeFeederGUI) feederCreator;
            rangeFeederGUI.init(startIPInput, endIPInput, hostnameLabel);
        }

        this.startStopScanning = new StartStopScanning(injector.require(ScannerDispatcherThreadFactory.class), resultTable, stateMachine, startButton, feederCreator, resultLabel, progressBar, threadsLabel);
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
            settingsStage.setScene(scene);
            settingsStage.setResizable(false);
            settingsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showStatistics(ActionEvent event) {
        resultTable.showResultsWindow();
    }

    @FXML
    private void showDetail(ActionEvent event) {
        try {
            resultTable.handleDetailsAction(event);
        } catch (UserErrorException e) {
            showAlertMessage(e.getMessage());
        }

    }

    @FXML
    private void rescan(ActionEvent event) {
        try {
            resultTable.handleRescanAction(event);
        } catch (UserErrorException e) {
            showAlertMessage(e.getMessage());
        }
    }

    @FXML
    private void copy(ActionEvent event) {
        try {
            resultTable.handleCopyAction(event);
        } catch (UserErrorException e) {
            showAlertMessage(e.getMessage());
        }
    }

    @FXML
    private void delete(ActionEvent event) {
        try {
            resultTable.handleDeleteAction(event);
        } catch (UserErrorException e) {
            showAlertMessage(e.getMessage());
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void showAboutProgram() {
        String info = """
                Программа "Сканер сетевых портов" представляет собой инструмент для анализа и 
                проверки безопасности компьютерных сетей. С ее помощью вы сможете быстро и эффективно 
                сканировать сетевые узлы на предмет открытых портов, а также определить доступные
                сетевые службы и приложения.
                                
                Основные возможности программы включают:
                 
                Сканирование портов: Быстрое и точное определение открытых портов на целевых узлах сети, 
                что позволяет выявить потенциальные уязвимости и риски безопасности.
                Анализ сетевых служб: Определение доступных сетевых служб и приложений, 
                работающих на открытых портах.
                Гибкие настройки сканирования: Возможность настройки параметров сканирования, включая выбор 
                диапазона портов, количество потоков и тайм-ауты соединения.
                Интуитивно понятный интерфейс: Простой и понятный интерфейс пользователя делает использование 
                программы удобным и доступным даже для начинающих пользователей.
                Экспорт результатов: Возможность сохранения результатов сканирования для последующего анализа и отчетности.
                Программа предоставляет надежный инструмент для обеспечения безопасности сети, обнаружения 
                уязвимостей и мониторинга сетевой активности.
                """;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText(info);

        alert.showAndWait();
    }

}