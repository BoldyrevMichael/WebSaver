package ru.geekbrains.websaver.client;

import ru.geekbrains.websaver.common.DefaultGUIExceptionHandler;
import ru.geekbrains.websaver.common.GBC;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    private final ClientController clientController = new ClientController(this);
    final ClientCore clientCore = new ClientCore(clientController);
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "WebSaver";

    private final JPanel welcomePanel = new JPanel(new BorderLayout(0, 200));
    private final JPanel welcomePanelbuttonPanel = new JPanel(new FlowLayout());
    final JButton btnShowEntranceWindow = new JButton("Вход");
    final JButton btnShowRegistrWindow = new JButton("Регистрация");
    private final JLabel labWelcome = new JLabel("Welcome to WebSaver!!!", JLabel.CENTER);

    private final JPanel registrPanel = new JPanel(new GridBagLayout());
    private final JLabel labRegLogin = new JLabel("Придумайте логин: ", JLabel.RIGHT);
    final JTextField fieldRegLogin = new JTextField("логин");
    private final JLabel labRegPass = new JLabel("Придумайте пароль: ", JLabel.RIGHT);
    final JPasswordField fieldRegPass = new JPasswordField("пароль");
    private final JLabel labRegRepPass = new JLabel("Повторите пароль: ", JLabel.RIGHT);
    final JPasswordField fieldRegRepPass = new JPasswordField("пароль");
    final JButton btnReg = new JButton("Зарегистрироваться");

    private final JPanel entrancePanel = new JPanel(new GridBagLayout());
    private final JLabel labEntrLogin = new JLabel("Введите логин: ", JLabel.RIGHT);
    final JTextField fieldEntrLogin = new JTextField("логин");
    private final JLabel labEntrPass = new JLabel("Введите пароль: ", JLabel.RIGHT);
    final JPasswordField fieldEntrPass = new JPasswordField("пароль");
    final JButton btnLogin = new JButton("Войти");

    private final JPanel mainPanal = new JPanel(new BorderLayout());
    private final JPanel buttonPanel = new JPanel(new GridBagLayout());
    final JButton btnAddFiles = new JButton("Добавить Файл(ы)");
    final JButton btnDelFiles = new JButton("Удалить Файл(ы)");
    final JButton btnGetFiles = new JButton("Скачать Файл(ы)");
    final JTable listOfFilesTable = new JTable();
    private final JScrollPane scrollListOfFilesTable = new JScrollPane(listOfFilesTable);

    final CardLayout cardLayout = new CardLayout();
    final JPanel cardPanel = new JPanel(cardLayout);

    private ClientGUI() {

        Thread.setDefaultUncaughtExceptionHandler(new DefaultGUIExceptionHandler());
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(620, 400));
        setTitle(TITLE);
        setLocationRelativeTo(null);

        buttonPanel.add(btnAddFiles, new GBC(0, 0).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(20));
        buttonPanel.add(btnDelFiles, new GBC(0, 1).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(20));
        buttonPanel.add(btnGetFiles, new GBC(0, 2).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(20));
        buttonPanel.setPreferredSize(new Dimension(200, 0));
        mainPanal.add(buttonPanel, BorderLayout.EAST);
        mainPanal.add(scrollListOfFilesTable, BorderLayout.CENTER);

        registrPanel.add(labRegLogin, new GBC(0, 0).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 10));
        fieldRegLogin.setPreferredSize(new Dimension(250, 30));
        registrPanel.add(fieldRegLogin, new GBC(1, 0).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        registrPanel.add(labRegPass, new GBC(0, 1).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 10));
        fieldRegPass.setPreferredSize(new Dimension(250, 30));
        registrPanel.add(fieldRegPass, new GBC(1, 1).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        registrPanel.add(labRegRepPass, new GBC(0, 2).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 10));
        fieldRegRepPass.setPreferredSize(new Dimension(250, 30));
        registrPanel.add(fieldRegRepPass, new GBC(1, 2).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        registrPanel.add(btnReg, new GBC(1, 3).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(10, 0, 10, 0));

        entrancePanel.add(labEntrLogin, new GBC(0, 0).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 10));
        fieldEntrLogin.setPreferredSize(new Dimension(250, 30));
        entrancePanel.add(fieldEntrLogin, new GBC(1, 0).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        entrancePanel.add(labEntrPass, new GBC(0, 1).setFill(GBC.NONE).setAnchor(GBC.EAST).setInsets(10, 0, 10, 10));
        fieldEntrPass.setPreferredSize(new Dimension(250, 30));
        entrancePanel.add(fieldEntrPass, new GBC(1, 1).setFill(GBC.NONE).setAnchor(GBC.WEST).setInsets(10, 0, 10, 0));
        entrancePanel.add(btnLogin, new GBC(1, 2).setFill(GBC.NONE).setAnchor(GBC.CENTER).setInsets(10, 0, 10, 0));

        labWelcome.setFont(new Font("Dialog", Font.BOLD, 50));
        welcomePanel.add(labWelcome, BorderLayout.NORTH);
        welcomePanelbuttonPanel.add(btnShowEntranceWindow);
        welcomePanelbuttonPanel.add(btnShowRegistrWindow);
        welcomePanel.add(welcomePanelbuttonPanel, BorderLayout.CENTER);

        cardPanel.add(welcomePanel, "welcomePanel");
        cardPanel.add(registrPanel, "registrPanel");
        cardPanel.add(entrancePanel, "entrancePanel");
        cardPanel.add(mainPanal, "mainPanel");

        add(cardPanel);

        cardLayout.show(cardPanel, "welcomePanel");

        btnShowRegistrWindow.addActionListener(clientController);
        btnShowEntranceWindow.addActionListener(clientController);
        btnAddFiles.addActionListener(clientController);
        btnDelFiles.addActionListener(clientController);
        btnGetFiles.addActionListener(clientController);
        btnReg.addActionListener(clientController);
        btnLogin.addActionListener(clientController);

        setVisible(true);
    }
}
