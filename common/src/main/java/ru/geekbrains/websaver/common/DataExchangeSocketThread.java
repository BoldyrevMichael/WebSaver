package ru.geekbrains.websaver.common;

import java.io.*;
import java.net.Socket;

public class DataExchangeSocketThread extends Thread {
    private final DataExchangeSocketThreadListener eventListener;
    private final Socket socket;

    public DataExchangeSocketThread(DataExchangeSocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartDataExchangeSocketThread(this, socket);
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())){
            eventListener.onReadyDataExchangeSocketThread(this, socket);
            while (!isInterrupted()) {

            }
        }catch (IOException e) {
            eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
            }
            eventListener.onStopDataExchangeSocketThread(this, socket);
        }
    }

    /*public synchronized void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
            close();
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
        }
    }*/
}
