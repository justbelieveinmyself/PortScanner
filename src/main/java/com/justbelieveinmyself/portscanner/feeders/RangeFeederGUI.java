package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.Device;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import static com.justbelieveinmyself.portscanner.util.InetAddressUtils.*;

public class RangeFeederGUI extends AbstractFeederGUI {
    private Logger LOG = Logger.getLogger(RangeFeederGUI.class.getSimpleName());
    private TextField startIP;
    private TextField endIP;
    private Label hostnameLabel;
    private Feeder lastFeeder;

    @Override
    public Feeder createFeeder() {
        lastFeeder = new RangeFeeder(startIP.getText(), endIP.getText());
        return lastFeeder;
    }

    @Override
    public Feeder createRescanFeeder(List<Device> selectedItems) {
        String[] addresses = new String[selectedItems.size()];
        for (int i = 0; i < selectedItems.size(); i++) {
            addresses[i] = selectedItems.get(i).getIp();
        }
        return new RescanFeeder(lastFeeder, addresses);
    }

    public void init(TextField startIP, TextField endIP, Label hostnameLabel) {
        this.startIP = startIP;
        this.endIP = endIP;
        this.hostnameLabel = hostnameLabel;
        asyncFillLocalHostInfo(hostnameLabel, startIP);
    }

    @Override
    protected void afterLocalHostInfoFilled(InterfaceAddress ifAddr) {
        InetAddress address = ifAddr.getAddress();
        if (!address.isLoopbackAddress()) {
            updateStartEndWithNetMask(address, "/" + ifAddr.getNetworkPrefixLength());
        }
    }

    private void updateStartEndWithNetMask(InetAddress ip, String netmaskString) {
        try {
            InetAddress netmask = parseNetmask(netmaskString);
            startIP.setText(startRangeByNetmask(ip, netmask).getHostAddress());
            String hostAddress = endRangeByNetmask(ip, netmask).getHostAddress();
            endIP.setText(hostAddress);
        } catch (UnknownHostException e) {
            LOG.info(e.toString());
        }
    }

}
