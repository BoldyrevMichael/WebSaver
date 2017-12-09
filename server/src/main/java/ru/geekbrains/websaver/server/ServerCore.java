package ru.geekbrains.websaver.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerCore implements ServerSocketThreadListener, ServerDataThreadListener {

    private final Vector<ServerDataThread> clients = new Vector<>();

    public static void main(String[] args) {
        ServerCore serverCore = new ServerCore();
        ServerSocketThread serverSocketThread = new ServerSocketThread("ServerSocketThread",8189,2000,serverCore);
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
        new ServerDataThread(this, threadName, socket);
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
    public synchronized void onStartServerDataThread(ServerDataThread serverDataThread, Socket socket) {
        System.out.println("На сервере начал работу поток для обмена данными с клиентом " + socket + "..");
    }

    @Override
    public synchronized void onReadyServerDataThread(ServerDataThread serverDataThread, Socket socket) {
        System.out.println("На сервере поток для обмена данными с клиентом " + socket + " подготовлен к передаче данных..");
        clients.add(serverDataThread);
    }

    @Override
    public synchronized void onExceptionServerDataThread(ServerDataThread serverDataThread, Socket socket, Exception e) {
        System.out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    @Override
    public synchronized void onStopServerDataThread(ServerDataThread serverDataThread, Socket socket) {
        System.out.println("На сервере завершил работу поток для обмена данными с клиентом " + socket + ".");
        clients.remove(serverDataThread);
    }


}
