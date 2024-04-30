package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.util.InetAddressUtils;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

public abstract class AbstractFeederGUI implements FeederCreator {
    protected Feeder feeder;

    @Override
    public String getFeederId() {
        return feeder.getId();
    }

    @Override
    public String getFeederName() {
        return feeder.getName();
    }

    private static final Object localResolveLock = new Object();
    private static String localName;
    private static InterfaceAddress localInterface;

    protected void asyncFillLocalHostInfo(final Text hostnameText, final Text ipText) {
        new Thread(() -> {
            synchronized (localResolveLock) {
                if (localInterface == null) {
                    localInterface = InetAddressUtils.getLocalInterface();
                    try {
                        localName = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        localName = localInterface.getAddress().getHostName();
                    }
                }
                Platform.runLater(() -> {
                    if ("".equals(hostnameText.getText())) {
                        hostnameText.setText(localName);
                    }
                    if ("".equals(ipText.getText())) {
                        ipText.setText(localInterface.getAddress().getHostAddress());
                    }
                });
            }
        });
    }

}
