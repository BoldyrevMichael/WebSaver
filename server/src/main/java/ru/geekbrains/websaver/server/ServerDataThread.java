package ru.geekbrains.websaver.server;

import java.io.*;
import java.net.Socket;

public class ServerDataThread extends Thread {
    private final ServerDataThreadListener eventListener;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ServerDataThread(ServerDataThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartServerDataThread(this, socket);
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            eventListener.onReadyServerDataThread(this, socket);
            while (!isInterrupted()) {

            }
        } catch (IOException e) {
            eventListener.onExceptionServerDataThread(this, socket, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                eventListener.onExceptionServerDataThread(this, socket, e);
            }
            eventListener.onStopServerDataThread(this, socket);
        }
    }

    /*public synchronized void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionServerDataThread(this, socket, e);
            close();
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExceptionServerDataThread(this, socket, e);
        }
    }*/
}
