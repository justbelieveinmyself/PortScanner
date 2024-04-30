package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.ScanningResult;
import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.state.ScanningState;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.core.state.StateMachine.Transition;
import com.justbelieveinmyself.portscanner.core.state.StateTransitionListener;
import com.justbelieveinmyself.portscanner.core.values.NumericRangeList;
import com.justbelieveinmyself.portscanner.fetchers.FilteredPortsFetcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableView;

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
        LOG.info("ResultTable construct");
    }

    public void initialize(ScanningResultList scanningResults, StateMachine stateMachine) {
        LOG.info("ResultTable initialized");
        this.scanningResults = scanningResults;
        stateMachine.addTransitionListener(this);
    }

    @Override
    public void transitionTo(ScanningState state, Transition transition) {
        setCursor(state == ScanningState.IDLE ? Cursor.DEFAULT : Cursor.WAIT);
    }

    public void addOrUpdateResultRow(final ScanningResult result) {

        if (scanningResults.isRegistered(result)) { //обновляем существующий
            int index = scanningResults.update(result);
            updateResult(index, result);
            //clear?
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
        String macAddress = result.getMac() == null? "Ожидание.." : result.getMac();
        String ports = "Ожидание..";
        String filteredPorts = "Ожидание..";

        items.add(new Device(result.getAddress().toString(), macAddress, ports, filteredPorts));
    }

    public void updateResult(int index, ScanningResult result) {
        List<?> values = result.getValues();
        String macAddress = values.get(0).toString();
        String ports = values.get(1).toString();
        String filteredPorts = values.get(2).toString();

        Device device = items.get(index);
        device.setIp(result.getAddress().toString());
        device.setMacAddress(macAddress);
        device.setPorts(ports);
        device.setFilteredPorts(filteredPorts);
    }

    public void removeResult(int index) {
        items.remove(index);
    }

}
