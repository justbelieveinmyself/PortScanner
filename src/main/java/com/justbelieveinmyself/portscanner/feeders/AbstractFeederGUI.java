package com.justbelieveinmyself.portscanner.feeders;

import com.justbelieveinmyself.portscanner.util.InetAddressUtils;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

public abstract class AbstractFeederGUI implements FeederCreator {
    protected Feeder feeder;

    private static final Object localResolveLock = new Object();
    private static String localName;
    private static InterfaceAddress localInterface;

    protected void asyncFillLocalHostInfo(final Label hostnameText, final TextField ipText) {
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
                        afterLocalHostInfoFilled(localInterface);
                    }
                });
            }
        }).start();
    }

    protected void afterLocalHostInfoFilled(InterfaceAddress localInterface) {

    }

}
