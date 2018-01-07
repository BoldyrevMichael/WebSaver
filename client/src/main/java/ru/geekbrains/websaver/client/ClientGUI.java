package ru.geekbrains.websaver.client;

import ru.geekbrains.websaver.common.DefaultGUIExceptionHandler;
import ru.geekbrains.websaver.common.GBC;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {

    public static void main(String[] args) {
        ClientController clientController = new ClientController();
        SwingUtilities.invokeLater(() -> {
            new ClientGUI(clientController);
        });
    }

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "WebSaver";

    final JPanel welcomePanel = new JPanel(new BorderLayout(0, 200));
    private final JPanel welcomePanelbottonPanel = new JPanel(new FlowLayout());
    final JButton btnEntrance = new JButton("Войти");
    final JButton btnRegistr = new JButton("Зарегистрироваться");
    private final JLabel labWelcome = new JLabel("Welcome to WebSaver!!!", JLabel.CENTER);

    final JPanel registrPanel = new JPanel(new GridBagLayout());
    private final JLabel labRegLogin = new JLabel("Придумайте логин: ", JLabel.RIGHT);
    final JTextField fieldRegLogin = new JTextField("логин");
    private final JLabel labRegPass = new JLabel("Придумайте пароль: ", JLabel.RIGHT);
    final JPasswordField fieldRegPass = new JPasswordField("пароль");
    private final JLabel labRegRepPass = new JLabel("Повторите пароль: ", JLabel.RIGHT);
    final JPasswordField fieldRegRepPass = new JPasswordField("пароль");

    final JPanel entrancePanel = new JPanel(new GridBagLayout());
    private final JLabel labEntrLogin = new JLabel("Введите логин: ", JLabel.RIGHT);
    JTextField fieldEntrLogin = new JTextField("логин");
    private final JLabel labEntrPass = new JLabel("Введите пароль: ", JLabel.RIGHT);
    final JPasswordField fieldEntrPass = new JPasswordField("пароль");

    final JPanel bottonPanel = new JPanel(new GridBagLayout());
    final JButton btnAddFiles = new JButton("Добавить Файл(ы)");
    final JButton btnDelFiles = new JButton("Удалить Файл(ы)");
    final JButton btnSyncFiles = new JButton("Синхронизировать Файл(ы)");

    final JTable listOfFilesTable = new JTable();

    private ClientGUI(ClientController clientController) {
        clientController.addClient(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(620, 400));
        setTitle(TITLE);
        setLocationRelativeTo(null);

        bottonPanel.add(btnAddFiles, new GBC(0, 0).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(20));
        bottonPanel.add(btnDelFiles, new GBC(0, 1).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(20));
        bottonPanel.add(btnSyncFiles, new GBC(0, 2).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(20));
        bottonPanel.setPreferredSize(new Dimension(200, 0));
        bottonPanel.setVisible(false);
        add(bottonPanel, BorderLayout.EAST);

        JScrollPane scrollListOfFilesTable = new JScrollPane(listOfFilesTable);
        scrollListOfFilesTable.setVisible(false);
        add(listOfFilesTable, BorderLayout.CENTER);

        registrPanel.add(labRegLogin, new GBC(0, 0).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 0));
        fieldRegLogin.setPreferredSize(new Dimension(250, 30));
        registrPanel.add(fieldRegLogin, new GBC(1, 0).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        registrPanel.add(labRegPass, new GBC(0, 1).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 0));
        fieldRegPass.setPreferredSize(new Dimension(250, 30));
        registrPanel.add(fieldRegPass, new GBC(1, 1).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        registrPanel.add(labRegRepPass, new GBC(0, 2).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 0));
        fieldRegRepPass.setPreferredSize(new Dimension(250, 30));
        registrPanel.add(fieldRegRepPass, new GBC(1, 2).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        registrPanel.setVisible(false);
        add(registrPanel);

        entrancePanel.add(labEntrLogin, new GBC(0, 0).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 0));
        fieldEntrLogin.setPreferredSize(new Dimension(250, 30));
        entrancePanel.add(fieldEntrLogin, new GBC(1, 0).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        entrancePanel.add(labEntrPass, new GBC(0, 1).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 0));
        fieldEntrPass.setPreferredSize(new Dimension(250, 30));
        entrancePanel.add(fieldEntrPass, new GBC(1, 1).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        entrancePanel.setVisible(false);
        add(entrancePanel);

        labWelcome.setFont(new Font("Dialog", Font.BOLD, 50));
        welcomePanel.add(labWelcome, BorderLayout.NORTH);
        welcomePanelbottonPanel.add(btnEntrance);
        welcomePanelbottonPanel.add(btnRegistr);
        welcomePanel.add(welcomePanelbottonPanel, BorderLayout.CENTER);
        add(welcomePanel, BorderLayout.CENTER);

        btnRegistr.addActionListener(clientController);
        btnEntrance.addActionListener(clientController);
        btnAddFiles.addActionListener(clientController);
        btnDelFiles.addActionListener(clientController);
        btnSyncFiles.addActionListener(clientController);

        setVisible(true);
    }
}
