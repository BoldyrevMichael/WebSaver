package ru.geekbrains.websaver.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientController implements ActionListener {

    ClientGUI clientGUI;

    public void addClient(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == clientGUI.btnEntrance) {
            register();
        } else if (src == clientGUI.btnEntrance) {
            login();
        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    private void register() {
        SwingUtilities.invokeLater(() -> {
            clientGUI.welcomePanel.setVisible(false);
            clientGUI.registrPanel.setVisible(true);
        });
    }

    private void login() {
        SwingUtilities.invokeLater(() -> {
            clientGUI.welcomePanel.setVisible(false);
            clientGUI.entrancePanel.setVisible(true);
        });
    }
}
