package ru.geekbrains.websaver.server;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;
import ru.geekbrains.websaver.common.Messages;
import ru.geekbrains.websaver.common.NetworkProperties;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, DataExchangeSocketThreadListener {

    private Connection connection;
    private PreparedStatement ps;
    private List<File> serverFilesList = new ArrayList<>();
    private final Vector<DataExchangeSocketThread> clients = new Vector<>();

    public static void main(String[] args) {
        ServerCore serverCore = new ServerCore();
        serverCore.initDB();
        new ServerSocketThread("ServerSocketThread", NetworkProperties.PORT, 10000, serverCore);
    }

    private void initDB() {
        try {
            connect();
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Clients (\n" +
                    "    idOfClient      INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    login           TEXT UNIQUE ON CONFLICT FAIL,\n" +
                    "    password        TEXT,\n" +
                    "    volumeOfData    DECIMAL(14,2),\n" +
                    "    numberOfFiles   INTEGER);");
            stmt.execute("CREATE TABLE IF NOT EXISTS timeOfVisits (\n" +
                    "    idOfVisit       INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    loginOfClient   INTEGER REFERENCES Clients(login) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                    "    timeOfLastVisit INTEGER);");
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        System.out.println("Сервер начал работу..");
    }

    @Override
    public void onReadyServerSocketThread(ServerSocketThread thread, ServerSocket serverSocket) {
        System.out.println("Создан серверный сокет..");
    }

    @Override
    public void onTimeOutAccept(ServerSocketThread thread, ServerSocket serverSocket) {
        System.out.println("Время ожидания подключения клиента вышло..");
    }

    @Override
    public void onAcceptedSocket(ServerSocketThread thread, ServerSocket serverSocket, Socket socket) {
        System.out.println("Клиент " + socket + " присоединился..");
        String threadName = "Socket thread: " + socket.getInetAddress() + ":" + socket.getPort();
        new ServerDataExchangeSocketThread(this, threadName, socket);
    }

    @Override
    public void onExceptionServerSocketThread(ServerSocketThread thread, Exception e) {
        System.out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        System.out.println("Сервер завершил работу.");
    }

    @Override
    public synchronized void onStartDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket) {
        System.out.println("На сервере начал работу поток для обмена данными с клиентом " + socket + "..");
    }

    @Override
    public synchronized void onReadyDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket) {
        System.out.println("На сервере поток для обмена данными с клиентом " + socket + " подготовлен к передаче данных..");
        clients.add(dataExchangeSocketThread);
    }

    @Override
    public synchronized void onExceptionDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket, Exception e) {
        System.out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    @Override
    public synchronized void onStopDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket) {
        System.out.println("На сервере завершил работу поток для обмена данными с клиентом " + socket + ".");
        clients.remove(dataExchangeSocketThread);
    }

    @Override
    public void onReceiveMsg(DataExchangeSocketThread dataExchangeSocketThread, Socket socket, Object parcel) {
        if (parcel instanceof String) {
            String[] tokens = ((String) parcel).split(Messages.DELIMITER);
            String type = tokens[0];
            switch (type) {
                case Messages.LOGIN_REQUEST:
                    synchronized (this) {
                        try {
                            connect();
                            ps = connection.prepareStatement("SELECT * FROM Clients WHERE login = ? AND password = ?");
                            ps.setString(1, tokens[1]);
                            ps.setString(2, tokens[2]);
                            ResultSet res1 = ps.executeQuery();
                            if (res1.next()) {
                                ps = connection.prepareStatement("INSERT INTO timeOfVisits (loginOfClient, timeOfLastVisit) VALUES (?,?);");
                                ps.setString(1, tokens[1]);
                                ps.setString(2, tokens[3]);
                                ps.executeUpdate();
                                dataExchangeSocketThread.sendMsg(Messages.getLoginOk());
                            } else {
                                dataExchangeSocketThread.sendMsg(Messages.getLoginError("Пользователь с таким логином не зарегистрирован!"));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            disconnect();
                        }
                    }
                    break;
                case Messages.REGISTR_REQUEST:
                    if (!tokens[2].equals(tokens[3])) {
                        Messages.getRegistrError("Пароли не совпадают!");
                    } else {
                        synchronized (this) {
                            try {
                                connect();
                                ps = connection.prepareStatement("SELECT * FROM Clients WHERE login = ?");
                                ps.setString(1, tokens[1]);
                                ResultSet res1 = ps.executeQuery();
                                if (!res1.next()) {
                                    ps = connection.prepareStatement("INSERT INTO Clients (login, password, volumeOfData, numberOfFiles) VALUES (?,?,?,?);");
                                    ps.setString(1, tokens[1]);
                                    ps.setString(2, tokens[2]);
                                    ps.setDouble(3, 0);
                                    ps.setInt(4, 0);
                                    ps.executeUpdate();
                                    dataExchangeSocketThread.sendMsg(Messages.getRegistrOk("Вы успешно зарегистрировались!"));
                                } else {
                                    dataExchangeSocketThread.sendMsg(Messages.getRegistrError("Пользователь с таким логином уже зарегистрирован!"));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } finally {
                                disconnect();
                            }
                        }
                    }
                    break;
                case Messages.GET_FILES:
                    dataExchangeSocketThread.sendMsg(serverFilesList);
                    break;
                default:
                    throw new RuntimeException("Unknown message type: " + type);
            }
        } else if (parcel instanceof File) {
            File receivedFile = (File) parcel;
            System.out.println("Получен файл " + receivedFile.getName());
            if (serverFilesList.contains(receivedFile)) {
                serverFilesList.remove(receivedFile);
                System.out.println("Количество объектов после удаления " + serverFilesList.size());
            } else {
                serverFilesList.add(receivedFile);
                System.out.println("Количество объектов после добавления " + serverFilesList.size());
            }
        }
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ClientsOfWebSaver.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
