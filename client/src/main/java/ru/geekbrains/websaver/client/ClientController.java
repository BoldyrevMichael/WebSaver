package ru.geekbrains.websaver.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientController implements ActionListener {

    private ClientGUI clientGUI;

    ClientController(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == clientGUI.btnRegistr) {
            register();
        } else if (src == clientGUI.btnEntrance) {
            login();
        } else if (src == clientGUI.btnReg) {

        } else if (src == clientGUI.btnIngoing) {

        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    private void register() {
        SwingUtilities.invokeLater(() -> clientGUI.cardLayout.show(clientGUI.cardPanel, "registrPanel"));
    }

    private void login() {
        SwingUtilities.invokeLater(() -> clientGUI.cardLayout.show(clientGUI.cardPanel, "entrancePanel"));
    }
}
