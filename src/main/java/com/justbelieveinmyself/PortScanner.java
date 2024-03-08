package com.justbelieveinmyself;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class PortScanner {
    public static final Logger log = Logger.getLogger(PortScanner.class.getName());
    private List<Integer> openPorts = new ArrayList<>();
    private List<Integer> closedPorts = new ArrayList<>();
    private final InetAddress inetAddress;
    private int minPort = 0;
    private int maxPort = 30000;

    public void scan() {
        for (int port = minPort; port < maxPort; port++) {
            System.out.println("Port №" + port);
            try (ServerSocket ss = new ServerSocket()) {
                ss.bind(new InetSocketAddress(inetAddress, port));
                openPorts.add(port);
            } catch (IOException e) {
                closedPorts.add(port);
            }
        }
        System.out.println("Open ports: " + openPorts.size());
        System.out.println("Closed ports: " + closedPorts.size());
        System.out.print("[");
        for (Integer closedPort : closedPorts) {
            System.out.print(" " + closedPort + " ");
        }
        System.out.println("]");
    }

    public PortScanner(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
}
