package com.justbelieveinmyself.portscanner;

import javafx.beans.property.SimpleStringProperty;

public class Device {
    private final SimpleStringProperty ip;
    private final SimpleStringProperty macAddress;
    private final SimpleStringProperty ports;
    private final SimpleStringProperty filteredPorts;

    public Device(String ip, String macAddress, String ports, String filteredPorts) {
        this.ip = new SimpleStringProperty(ip);
        this.macAddress = new SimpleStringProperty(macAddress);
        this.ports = new SimpleStringProperty(ports);
        this.filteredPorts = new SimpleStringProperty(filteredPorts);
    }

    public String getIp() {
        return ip.get();
    }

    public SimpleStringProperty ipProperty() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip.set(ip);
    }

    public String getMacAddress() {
        return macAddress.get();
    }

    public SimpleStringProperty macAddressProperty() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress.set(macAddress);
    }

    public String getPorts() {
        return ports.get();
    }

    public SimpleStringProperty portsProperty() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports.set(ports);
    }

    public String getFilteredPorts() {
        return filteredPorts.get();
    }

    public SimpleStringProperty filteredPortsProperty() {
        return filteredPorts;
    }

    public void setFilteredPorts(String filteredPorts) {
        this.filteredPorts.set(filteredPorts);
    }

    @Override
    public String toString() {
        return "IP-адрес - " + ip.get() +
                "\nMAC-адрес - " + macAddress.get() +
                "\nОткрытые порты - " + ports.get() +
                "\nФильтрованный порт - " + filteredPorts.get();
    }

}
