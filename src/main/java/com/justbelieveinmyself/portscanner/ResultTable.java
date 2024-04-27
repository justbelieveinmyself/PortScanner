package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistry;
import javafx.scene.Cursor;
import javafx.scene.control.TableView;

import java.util.logging.Logger;

public class ResultTable extends TableView implements StateTransitionListener {
    private final Logger LOG = Logger.getLogger(ResultTable.class.getName());
    private ScanningResultList scanningResults;
    public ResultTable() {
        super();
        LOG.info("ResultTable construct");
    }

    public void initialize(FetcherRegistry fetcherRegistry, ScanningResultList scanningResults, StateMachine stateMachine) {
        LOG.info("ResultTable initialized");
        stateMachine.addTransitionListener(this);
    }

    @Override
    public void transitionTo(ScanningState state, Transition transition) {
        setCursor(state == ScanningState.IDLE ? Cursor.DEFAULT : Cursor.WAIT);
    }

/*    @Override
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
    }*/



}
