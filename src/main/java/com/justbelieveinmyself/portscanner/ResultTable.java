package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.ScanningResult;
import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.UserErrorException;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.core.values.NumericRangeList;
import com.justbelieveinmyself.portscanner.fetchers.FilteredPortsFetcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ResultTable extends TableView implements StateTransitionListener {
    private final Logger LOG = Logger.getLogger(ResultTable.class.getName());
    private ObservableList<Device> items = FXCollections.observableArrayList();
    private ScanningResultList scanningResults;

    public ResultTable() {
        super();
        setItems(items);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void initialize(ScanningResultList scanningResults, StateMachine stateMachine) {
        this.scanningResults = scanningResults;
        stateMachine.addTransitionListener(this);
    }

    @Override
    public void transitionTo(ScanningState state, Transition transition) {
        if (transition == Transition.COMPLETE && ScannerConfig.getConfig().askConfirmation) {
            showResultsWindow();
        }
        setCursor(state == ScanningState.IDLE ? Cursor.DEFAULT : Cursor.WAIT);
    }

    public void showResultsWindow() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("results-view.fxml"));
                Parent root = loader.load();
                Stage resultsStage = new Stage();

                resultsStage.initModality(Modality.APPLICATION_MODAL);
                resultsStage.setTitle("Статистика сканирования");
                Scene scene = new Scene(root);
                resultsStage.setScene(scene);
                resultsStage.initStyle(StageStyle.UTILITY);
                resultsStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addOrUpdateResultRow(final ScanningResult result) {
        if (scanningResults.isRegistered(result)) { //обновляем существующий
            int index = scanningResults.update(result);
            updateResult(index, result);
            refresh();
        } else { //добавляем новый
            //получаем индекс последнего
            int index = items.size();
            scanningResults.registerAtIndex(index, result); //регистрируем
            addResult(result);
        }
    }

    public ScanningResultList getScanningResults() {
        return scanningResults;
    }

    public void addResult(ScanningResult result) {
        String macAddress = result.getMac() == null ? "Ожидание.." : result.getMac();
        String ports = "Ожидание..";
        String filteredPorts = "Ожидание..";

        items.add(new Device(result.getAddress().getHostAddress(), macAddress, ports, filteredPorts));
    }

    public void updateResult(int index, ScanningResult result) {
        List<?> values = result.getValues();
        String macAddress = values.get(0).toString();
        String ports = values.get(1).toString();
        String filteredPorts = values.get(2).toString();

        Device device = items.get(index);
        device.setIp(result.getAddress().getHostAddress());
        device.setMacAddress(macAddress);
        device.setPorts(ports);
        device.setFilteredPorts(filteredPorts);
    }

    public void resetSelection() {
        items.clear();
        scanningResults.clear();
    }

    public void checkSelection() {
        if (getItems().size() <= 0) {
            throw new UserErrorException("Нет результатов");
        } else if (getSelectionModel().getSelectedItems().size() <= 0) {
            throw new UserErrorException("Выберите результаты!");
        }
    }

}
//TODO: удаления строк из результатов, копирование результатов