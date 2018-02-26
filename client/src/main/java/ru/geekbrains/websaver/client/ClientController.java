package ru.geekbrains.websaver.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ClientController implements ActionListener {

    private ClientGUI clientGUI;

    ClientController(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == clientGUI.btnShowRegistrWindow) {
            showRegistrWindow();
        } else if (src == clientGUI.btnShowEntranceWindow) {
            showEntranceWindow();
        } else if (src == clientGUI.btnReg) {
            clientGUI.clientCore.registr(clientGUI.fieldRegLogin.getText(), new String(clientGUI.fieldRegPass.getPassword()), new String(clientGUI.fieldRegRepPass.getPassword()));
            SwingUtilities.invokeLater(() -> {
                clientGUI.fieldRegLogin.setEditable(false);
                clientGUI.fieldRegPass.setEditable(false);
                clientGUI.fieldRegRepPass.setEditable(false);
                clientGUI.btnReg.setVisible(false);
            });
        } else if (src == clientGUI.btnLogin) {
            SwingUtilities.invokeLater(() -> {
                clientGUI.fieldEntrLogin.setEditable(false);
                clientGUI.fieldEntrPass.setEditable(false);
                clientGUI.btnLogin.setVisible(false);
            });
            clientGUI.clientCore.login(clientGUI.fieldEntrLogin.getText(), new String(clientGUI.fieldEntrPass.getPassword()));
        } else if (src == clientGUI.btnAddFiles) {
            JFileChooser filechooser = new JFileChooser();
            int ret = filechooser.showOpenDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = filechooser.getSelectedFile();
                clientGUI.clientCore.addFile(file);
            }
        } else if (src == clientGUI.btnDelFiles) {

        } else if (src == clientGUI.btnSaveFiles) {

        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    private void showRegistrWindow() {
        SwingUtilities.invokeLater(() -> clientGUI.cardLayout.show(clientGUI.cardPanel, "registrPanel"));
    }

    private void showEntranceWindow() {
        SwingUtilities.invokeLater(() -> clientGUI.cardLayout.show(clientGUI.cardPanel, "entrancePanel"));
    }

    void onLoginError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Ошибка входа: ", JOptionPane.WARNING_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            clientGUI.fieldEntrLogin.setEditable(true);
            clientGUI.fieldEntrPass.setEditable(true);
            clientGUI.btnLogin.setVisible(true);
        });
    }

    void onRegOk(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Успешная регистрация: ", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            clientGUI.fieldRegLogin.setEditable(true);
            clientGUI.fieldRegPass.setEditable(true);
            clientGUI.fieldRegRepPass.setEditable(true);
            clientGUI.btnReg.setVisible(true);
            clientGUI.cardLayout.show(clientGUI.cardPanel, "welcomePanel");
        });
    }

    void onRegError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Ошибка регистрации: ", JOptionPane.WARNING_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            clientGUI.fieldRegLogin.setEditable(true);
            clientGUI.fieldRegPass.setEditable(true);
            clientGUI.fieldRegRepPass.setEditable(true);
            clientGUI.btnReg.setVisible(true);
        });
    }

    void onGetList() {
        clientGUI.fileTableModel.fireTableDataChanged();
        SwingUtilities.invokeLater(() -> {
            clientGUI.cardLayout.show(clientGUI.cardPanel, "mainPanel");
        });
    }
}
