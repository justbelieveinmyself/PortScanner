package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.gui.GUI;

public class Main {

    public static void main(String[] args) {
        GUI gui = null;
        try {
            gui = new GUI();
        } catch (Exception e) {}
    }

}
