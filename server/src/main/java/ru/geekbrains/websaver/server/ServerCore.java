package ru.geekbrains.websaver.server;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, DataExchangeSocketThreadListener {

    private static Connection connection;
    private final Vector<DataExchangeSocketThread> clients = new Vector<>();

    public static void main(String[] args) {
        ServerCore serverCore = new ServerCore();
        initDB();
        ServerSocketThread serverSocketThread = new ServerSocketThread("ServerSocketThread", 8189, 2000, serverCore);
    }

    private static void initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:ClientsOfWebSaver.db");
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS Clients (\n" +
                    "    idOfClient      INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                    "    login           TEXT UNIQUE ON CONFLICT FAIL,\n" +
                    "    password        TEXT,\n" +
                    "    volumeOfData    DECIMAL(14,2),\n" +
                    "    numberOfFiles   INTEGER;");
            stmt.execute("CREATE TABLE IF NOT EXISTS timeOfVisits (\n" +
                    "    idOfVisit       INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                    "    loginOfClient   INTEGER REFERENCES Clients(login) ON DELETE CASCADE,\n" +
                    "    timeOfLastVisit INTEGER);");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    void register() {


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
    public void onReceiveMsg(DataExchangeSocketThread dataExchangeSocketThread, Socket socket, Object msg) {

    }
}
