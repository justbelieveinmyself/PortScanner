package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.Scanner;
import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.ScanningSubject;
import com.justbelieveinmyself.portscanner.core.state.StateMachine;
import com.justbelieveinmyself.portscanner.di.ComponentRegistry;
import com.justbelieveinmyself.portscanner.di.Injector;
import com.justbelieveinmyself.portscanner.feeders.RangeFeeder;
import com.justbelieveinmyself.portscanner.fetchers.FetcherRegistry;
import com.justbelieveinmyself.portscanner.fetchers.PortsFetcher;
import com.justbelieveinmyself.portscanner.fetchers.WinMACFetcher;
import com.justbelieveinmyself.portscanner.gui.ResultTable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class MainController {
    private final Logger LOG = Logger.getLogger(MainController.class.getName());
    private Injector injector = new Injector();
    {
        new ComponentRegistry().register(injector);
    }
    @FXML
    private TextField endIPInput;

    @FXML
    private TextField startIPInput;

    @FXML
    private TableView<Device> ipTableView = new ResultTable(injector.require(FetcherRegistry.class), injector.require(ScanningResultList.class), injector.require(StateMachine.class));

    @FXML
    void startScanning(ActionEvent event) {
        LOG.info("Starting scan..");
        int a = 10;
        ipTableView.getItems().add(new Device("192.168.1.2", "00:11:22:33:44:55", "80,443", "80"));
        Scanner scanner = injector.require(Scanner.class);
        RangeFeeder rangeFeeder = new RangeFeeder(startIPInput.getText(), endIPInput.getText());
    }

    @FXML
    void test(ActionEvent event) {

        ObservableList<TableColumn<Device, ?>> columns = ipTableView.getColumns();
        int a=  10;
    }

    @FXML
    void initialize() {
        startIPInput.setText("192.168.0.0");
        endIPInput.setText("192.168.0.255");

        ipTableView.getItems().add(new Device("192.168.1.1", "00:11:22:33:44:55", "80,443", "80"));
        ipTableView.getItems().add(new Device("192.168.1.2", "00:11:22:33:44:55", "80,443", "80"));
    }

}
