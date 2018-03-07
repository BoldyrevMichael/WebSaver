package ru.geekbrains.websaver.server;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;
import ru.geekbrains.websaver.common.Messages;
import ru.geekbrains.websaver.common.NetworkProperties;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

public class ServerCore implements ServerSocketThreadListener, DataExchangeSocketThreadListener {

    private Connection connection;
    private PreparedStatement ps;
    private final Vector<DataExchangeSocketThread> clients = new Vector<>();

    public static void main(String[] args) {
        ServerCore serverCore = new ServerCore();
        serverCore.initDB();
        new ServerSocketThread("ServerSocketThread", NetworkProperties.PORT, 1200000, serverCore);
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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
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
        ServerDataExchangeSocketThread serverDataExchangeSocketThread = (ServerDataExchangeSocketThread) dataExchangeSocketThread;
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
                                serverDataExchangeSocketThread.clientDirectory = getClientDirectory(tokens[1]).toString();
                                dataExchangeSocketThread.sendMsg(Messages.getLoginOk(tokens[1]));
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
                        dataExchangeSocketThread.sendMsg(Messages.getRegistrError("Пароли не совпадают!"));
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
                                    getClientDirectory(tokens[1]).mkdirs();
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
                    serverDataExchangeSocketThread.serverClientFilesList = getServerClientFilesList(tokens[1]);
                    dataExchangeSocketThread.sendMsg(serverDataExchangeSocketThread.serverClientFilesList);
                    break;
                case Messages.DIVIDE:

                    break;
                default:
                    throw new RuntimeException("Unknown message type: " + type);
            }
        } else if (parcel instanceof File) {
            serverDataExchangeSocketThread.receivedFile = (File) parcel;
            if (serverDataExchangeSocketThread.serverClientFilesList.contains(serverDataExchangeSocketThread.receivedFile)) {
                if (serverDataExchangeSocketThread.serverClientFilesList.get(serverDataExchangeSocketThread.serverClientFilesList.indexOf(serverDataExchangeSocketThread.receivedFile)).delete()) {
                    serverDataExchangeSocketThread.serverClientFilesList.remove(serverDataExchangeSocketThread.receivedFile);
                    System.out.println("Количество объектов после удаления " + serverDataExchangeSocketThread.serverClientFilesList.size());
                    serverDataExchangeSocketThread.sendMsg(Messages.getDelOk("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " удалён.", serverDataExchangeSocketThread.receivedFile.getName()));
                    serverDataExchangeSocketThread.receivedFile = null;
                } else {
                    System.out.println("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " не был удалён.");
                    serverDataExchangeSocketThread.sendMsg(Messages.getDelError("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " не был удалён."));
                    serverDataExchangeSocketThread.receivedFile = null;
                }
            } else {
                try {
                    serverDataExchangeSocketThread.fileOutputStream = new FileOutputStream(new File(serverDataExchangeSocketThread.clientDirectory + "\\" + serverDataExchangeSocketThread.receivedFile.getName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (serverDataExchangeSocketThread.receivedFile.length() == 0) {
                    try {
                        System.out.println("Перед закрытием файлового потока");
                        serverDataExchangeSocketThread.fileOutputStream.close();
                    } catch (IOException e) {
                        serverDataExchangeSocketThread.sendMsg(Messages.getReceiveError("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " не был сохранён."));
                        e.printStackTrace();
                    }
                    serverDataExchangeSocketThread.serverClientFilesList.add(serverDataExchangeSocketThread.receivedFile);
                    System.out.println("Получен файл " + serverDataExchangeSocketThread.receivedFile.getName());
                    serverDataExchangeSocketThread.sendMsg(Messages.getReceiveOk("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " сохранён.", serverDataExchangeSocketThread.receivedFile.getName()));
                    serverDataExchangeSocketThread.receivedFile = null;
                    System.out.println("Количество объектов после добавления " + serverDataExchangeSocketThread.serverClientFilesList.size());
                } else {
                    serverDataExchangeSocketThread.numberOfBuffers = serverDataExchangeSocketThread.receivedFile.length() / serverDataExchangeSocketThread.buffer.length;
                    System.out.println("количество буферов до if " + serverDataExchangeSocketThread.numberOfBuffers);
                    System.out.println("размер файла до if " + serverDataExchangeSocketThread.receivedFile.length());
                    System.out.println("остаток от деления до if" + serverDataExchangeSocketThread.receivedFile.length() % serverDataExchangeSocketThread.buffer.length);
                    if (serverDataExchangeSocketThread.receivedFile.length() % serverDataExchangeSocketThread.buffer.length != 0) {
                        serverDataExchangeSocketThread.numberOfBuffers++;
                        System.out.println("количество буферов в if " + serverDataExchangeSocketThread.numberOfBuffers);
                    }
                }
            }
        } else if (parcel instanceof byte[]) {
            System.out.println("Имя файла  в получении байт" + serverDataExchangeSocketThread.receivedFile.getName());
            if (serverDataExchangeSocketThread.receivedFile != null) {
                System.out.println("Количество буферов в получении byte[]" + serverDataExchangeSocketThread.numberOfBuffers);
                if ((serverDataExchangeSocketThread.numberOfBuffers > 0) && (serverDataExchangeSocketThread.flagOfEndWrite)) {
                    serverDataExchangeSocketThread.flagOfEndWrite = false;
                    try {
//                        System.out.println(Arrays.toString((byte[]) parcel));
                        serverDataExchangeSocketThread.fileOutputStream.write((byte[]) parcel);
                        serverDataExchangeSocketThread.fileOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        serverDataExchangeSocketThread.sendMsg(Messages.getReceiveError("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " не был сохранён."));
                    }
                    serverDataExchangeSocketThread.numberOfBuffers--;
                    serverDataExchangeSocketThread.flagOfEndWrite = true;
                    if (serverDataExchangeSocketThread.numberOfBuffers == 0) {
                        try {
                            System.out.println("Перед закрытием файлового потока");
                            serverDataExchangeSocketThread.fileOutputStream.close();
                        } catch (IOException e) {
                            serverDataExchangeSocketThread.sendMsg(Messages.getReceiveError("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " не был сохранён."));
                            e.printStackTrace();
                        }
                        serverDataExchangeSocketThread.serverClientFilesList.add(serverDataExchangeSocketThread.receivedFile);
                        System.out.println("Получен файл " + serverDataExchangeSocketThread.receivedFile.getName());
                        serverDataExchangeSocketThread.sendMsg(Messages.getReceiveOk("Файл " + serverDataExchangeSocketThread.receivedFile.getName() + " сохранён.", serverDataExchangeSocketThread.receivedFile.getName()));
                        serverDataExchangeSocketThread.receivedFile = null;
                        System.out.println("Количество объектов после добавления " + serverDataExchangeSocketThread.serverClientFilesList.size());
                    }
                }
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

    private File getClientDirectory(String login) {
        int i;
        File f;
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream("server\\target\\classes\\BeginningOfClientsDir.properties")) {
            while ((i = fileInputStream.read()) != -1) {
                stringBuilder.append((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder.append("\\\\").append(login);
        f = new File(stringBuilder.toString());
        return f;
    }

    private ArrayList<File> getServerClientFilesList(String login) {
        int i;
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream("server\\target\\classes\\BeginningOfClientsDir.properties")) {
            while ((i = fileInputStream.read()) != -1) {
                stringBuilder.append((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder.append("\\\\").append(login);
        File clientDir = new File(stringBuilder.toString());
        File[] clientFiles = clientDir.listFiles();
        List<File> list = Arrays.asList(clientFiles);
        System.out.println(list);
        return new ArrayList<>(list);
    }
}
