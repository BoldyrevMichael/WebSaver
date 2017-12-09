package ru.geekbrains.websaver.server;

import java.net.Socket;

public interface ServerDataThreadListener {

    void onStartServerDataThread(ServerDataThread serverDataThread, Socket socket);

    void onReadyServerDataThread(ServerDataThread serverDataThread, Socket socket);

    void onExceptionServerDataThread(ServerDataThread serverDataThread, Socket socket, Exception e);

    void onStopServerDataThread(ServerDataThread serverDataThread, Socket socket);
}
