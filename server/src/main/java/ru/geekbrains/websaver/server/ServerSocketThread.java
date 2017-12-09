package ru.geekbrains.websaver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {
    private final int port;
    private final ServerSocketThreadListener eventListener;
    private final int timeout;

    public ServerSocketThread(String name, int port, int timeout, ServerSocketThreadListener eventListener) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.eventListener = eventListener;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartServerSocketThread(this);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(timeout);
            eventListener.onReadyServerSocketThread(this, serverSocket);
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    eventListener.onTimeOutAccept(this, serverSocket);
                    continue;
                }
                eventListener.onAcceptedSocket(this, serverSocket, socket);
            }
        } catch (IOException e) {
            eventListener.onExceptionServerSocketThread(this, e);
        } finally {
            eventListener.onStopServerSocketThread(this);
        }
    }
}