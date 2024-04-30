package com.justbelieveinmyself.portscanner.feeders;

import javafx.scene.control.TextField;

public class RangeFeederGUI extends AbstractFeederGUI {
    private TextField startIP;
    private TextField endIP;

    @Override
    public Feeder createFeeder() {
        return new RangeFeeder(startIP.getText(), endIP.getText());
    }

    public void init(TextField startIP, TextField endIP) {
        this.startIP = startIP;
        this.endIP = endIP;
    }

}
