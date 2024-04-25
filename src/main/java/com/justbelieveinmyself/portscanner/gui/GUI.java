package com.justbelieveinmyself.portscanner.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private JTextField portInputField;
    private JTextField ipInputField1;
    private JTextField ipInputField2;
    private JButton scanButton;
    private JButton stopButton;
    private JTextArea resultArea;
    private volatile boolean scanning;

    public GUI() {
        setTitle("Port Scanner");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        JLabel portLabel = new JLabel("Порты (через запятую):");
        portInputField = new JTextField();
        JLabel ipLabel1 = new JLabel("IP Адрес 1:");
        ipInputField1 = new JTextField();
        JLabel ipLabel2 = new JLabel("IP Адрес 2:");
        ipInputField2 = new JTextField();
        inputPanel.add(portLabel);
        inputPanel.add(portInputField);
        inputPanel.add(ipLabel1);
        inputPanel.add(ipInputField1);
        inputPanel.add(ipLabel2);
        inputPanel.add(ipInputField2);

        JPanel buttonPanel = new JPanel();
        scanButton = new JButton("Сканировать");
        stopButton = new JButton("Остановить");
        stopButton.setEnabled(false);
        buttonPanel.add(scanButton);
        buttonPanel.add(stopButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        add(mainPanel);

        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanPorts();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopScan();
            }
        });
    }

    private void scanPorts() {
        String[] ports = portInputField.getText().split(",");
        String ip1 = ipInputField1.getText();
        String ip2 = ipInputField2.getText();

        scanning = true;
        scanButton.setEnabled(false);
        stopButton.setEnabled(true);

        new Thread(new Runnable() {
            @Override
            public void run() {

                scanning = false;
                scanButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        }).start();
    }

    private void stopScan() {
        scanning = false;
    }
}
