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
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.justbelieveinmyself.portscanner.MainApplication.showAlertMessage;

public class ResultTable extends TableView implements StateTransitionListener {
    private final Logger LOG = Logger.getLogger(ResultTable.class.getName());
    private ObservableList<Device> items = FXCollections.observableArrayList();
    private ScanningResultList scanningResults;
    private StateMachine stateMachine;
    ContextMenu contextMenu;

    public ResultTable() {
        super();

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        contextMenu = new ContextMenu();

        MenuItem detailsItem = new MenuItem("Детали");
        MenuItem rescanItem = new MenuItem("Пересканировать");
        MenuItem copyItem = new MenuItem("Скопировать");
        MenuItem deleteItem = new MenuItem("Удалить");
        contextMenu.getItems().setAll(detailsItem, rescanItem, copyItem, deleteItem);

        setItems(items);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setRowFactory(tv -> {
            TableRow<Device> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    public void initialize(ScanningResultList scanningResults, StateMachine stateMachine) {
        this.scanningResults = scanningResults;
        this.stateMachine = stateMachine;
        stateMachine.addTransitionListener(this);
        contextMenu.getItems().get(0).setOnAction(this::handleDetailsAction);
        contextMenu.getItems().get(1).setOnAction(this::handleRescanAction);
        contextMenu.getItems().get(2).setOnAction(this::handleCopyAction);
        contextMenu.getItems().get(3).setOnAction(this::handleDeleteAction);
    }

    public void handleRescanAction(Event event) {
        this.checkSelection();
        try {
            stateMachine.rescan();
        } catch (IllegalStateException e) {
            showAlertMessage("Дождитесь окончания сканирования!");
        }
    }

    public void handleCopyAction(Event event) {
        this.checkSelection();
        String itemsAsString = (String) getSelectionModel().getSelectedItems()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(itemsAsString);
        Clipboard.getSystemClipboard().setContent(clipboardContent);

    }

    public void handleDeleteAction(Event event) {
        this.checkSelection();
        ObservableList<Integer> selectedIndices = getSelectionModel().getSelectedIndices();
        int[] indexes = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
        scanningResults.remove(indexes);
        items.removeAll(getSelectionModel().getSelectedItems());
    }

    public void handleDetailsAction(Event event) {
        this.checkSelection();
        Device selectedItems = (Device) getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Детали IP-адреса");
        alert.setHeaderText(null);
        alert.setContentText(selectedItems.toString());

        alert.showAndWait();
    }

    @Override
    public void transitionTo(ScanningState state, Transition transition) {
        if (transition == Transition.COMPLETE && ScannerConfig.getConfig().showInfo) {
            showResultsWindow();
        }
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
                resultsStage.setResizable(false);
                resultsStage.showAndWait();
            } catch (Exception e) {
                String message;
                Throwable cause = e.getCause();
                if (cause instanceof InvocationTargetException i) {
                    message = i.getTargetException().getMessage();
                } else {
                    message = e.getMessage();
                }
                showAlertMessage(message);
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