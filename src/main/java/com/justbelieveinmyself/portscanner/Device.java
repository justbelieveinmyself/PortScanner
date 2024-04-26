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

    // Геттеры для свойств
    public String getIp() {
        return ip.get();
    }

    public void setIp(String ip) {
        this.ip.set(ip);
    }

    public String getMacAddress() {
        return macAddress.get();
    }

    public void setMacAddress(String macAddress) {
        this.macAddress.set(macAddress);
    }

    public String getPorts() {
        return ports.get();
    }

    public void setPorts(String ports) {
        this.ports.set(ports);
    }

    public String getFilteredPorts() {
        return filteredPorts.get();
    }

    public void setFilteredPorts(String filteredPorts) {
        this.filteredPorts.set(filteredPorts);
    }
}
