package ru.geekbrains.websaver.client;

import javax.swing.*;

public class ClientGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }

}
