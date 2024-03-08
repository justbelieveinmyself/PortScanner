package com.justbelieveinmyself;

import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        InetAddress address = InetAddress.getLoopbackAddress();
        PortScanner portScanner = new PortScanner(address);
        portScanner.scan();
    }
}
