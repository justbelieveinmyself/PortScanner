package com.justbelieveinmyself.portscanner.gui;

import com.justbelieveinmyself.portscanner.Device;
import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.fetchers.Fetcher;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistry;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistryUpdateListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultTable extends TableView implements FetcherRegistryUpdateListener, StateTransitionListener {
    private final Logger LOG = Logger.getLogger(ResultTable.class.getName());
    private ScanningResultList scanningResults;

    public ResultTable(FetcherRegistry fetcherRegistry, ScanningResultList scanningResultList, StateMachine stateMachine) {
        super();
        getSelectionModel().getSelectedItems().addListener((ListChangeListener) change -> LOG.info("CHANGE lsit"));
        stateMachine.addTransitionListener(this);
        fetcherRegistry.addListener(this);
        handleUpdateOfSelectedFetchers(fetcherRegistry);
    }

    @Override
    public void transitionTo(ScanningState state, Transition transition) {
        setCursor(state == ScanningState.IDLE ? Cursor.DEFAULT : Cursor.WAIT);
    }

    @Override
    public void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry) {
        LOG.info("Update in result table!");
        getItems().clear();
        getColumns().clear();

        TableColumn<Device, String> ipColumn = new TableColumn<>("IP");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        getColumns().add(ipColumn);

        for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {

            TableColumn<Device, String> column = new TableColumn<>(fetcher.getFullName());
            column.setCellValueFactory(new PropertyValueFactory<>(fetcher.getId()));
            getColumns().add(column);

        }
    }



}
