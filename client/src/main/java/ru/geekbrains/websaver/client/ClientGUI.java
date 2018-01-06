package ru.geekbrains.websaver.client;

import ru.geekbrains.websaver.common.DefaultGUIExceptionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI extends JFrame implements ActionListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "WebSaver";

    private final JPanel welcomePanel = new JPanel(new BorderLayout());
    private final JButton btnEntrance = new JButton("Войти");
    private final JButton btnRegistr = new JButton("Зарегистрироваться");
    private final JLabel labWelcome = new JLabel("Welcome to WebSaver!!!");

    private final JPanel registrPanel = new JPanel(new GridLayout(3, 2));
    private final JLabel labRegLogin = new JLabel("Придумайте логин: ");
    private final JTextField fieldRegLogin = new JTextField("логин");
    private final JLabel labRegPass = new JLabel("Придумайте пароль: ");
    private final JPasswordField fieldRegPass = new JPasswordField("пароль");
    private final JLabel labRegRepPass = new JLabel("Повторите пароль: ");
    private final JPasswordField fieldRegRepPass = new JPasswordField("пароль повторно");

    private final JPanel entrancePanel = new JPanel(new GridLayout(2, 2));
    private final JLabel labEntrLogin = new JLabel("Введите логин: ");
    private final JTextField fieldEntrLogin = new JTextField("логин");
    private final JLabel labEntrPass = new JLabel("Введите пароль: ");
    private final JPasswordField fieldEntrPass = new JPasswordField("пароль");

    private final JPanel bottonPanel = new JPanel(new GridLayout());
    private final JButton btnAddFiles = new JButton("Добавить Файл(ы)");
    private final JButton btnDelFiles = new JButton("Удалить Файл(ы)");
    private final JButton btnSyncFiles = new JButton("Синхронизировать Файл(ы)");

    private final JTable listOfFilesTable = new JTable();

    private ClientGUI() {
        Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setTitle(TITLE);
        setLocationRelativeTo(null);

        registrPanel.add(labRegLogin);
        registrPanel.add(fieldRegLogin);
        registrPanel.add(labRegPass);
        registrPanel.add(fieldRegPass);
        registrPanel.add(labRegRepPass);
        registrPanel.add(fieldRegRepPass);
        registrPanel.setVisible(false);
        add(registrPanel);

        entrancePanel.add(labEntrLogin);
        entrancePanel.add(fieldEntrLogin);
        entrancePanel.add(labEntrPass);
        entrancePanel.add(fieldEntrPass);
        entrancePanel.setVisible(false);
        add(entrancePanel);

        bottonPanel.add(btnAddFiles);
        bottonPanel.add(btnDelFiles);
        bottonPanel.add(btnSyncFiles);
        bottonPanel.setPreferredSize(new Dimension(150, 0));
        bottonPanel.setVisible(false);
        add(bottonPanel,BorderLayout.EAST);

        listOfFilesTable.setVisible(false);
        add(listOfFilesTable,BorderLayout.CENTER);

        welcomePanel.add(labWelcome, BorderLayout.NORTH);
        welcomePanel.add(btnEntrance, BorderLayout.WEST);
        welcomePanel.add(btnRegistr, BorderLayout.EAST);
        add(welcomePanel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
