package ru.geekbrains.websaver.common;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class DataExchangeSocketThread extends Thread {
    private final DataExchangeSocketThreadListener eventListener;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public DataExchangeSocketThread(DataExchangeSocketThreadListener eventListener, String name, Socket socket) {
        super(name);
        this.eventListener = eventListener;
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        eventListener.onStartDataExchangeSocketThread(this, socket);
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            eventListener.onReadyDataExchangeSocketThread(this, socket);
            while (!isInterrupted()) {
                Object parcel = in.readObject();
                if (parcel instanceof byte[]) {
                    System.out.println("В Рид МСГ " + Arrays.toString((byte[]) parcel));
                }
                if (parcel instanceof String) {
                    System.out.println("В Рид МСГ " + parcel);
                }
                eventListener.onReceiveMsg(this, socket, parcel);
            }
        } catch (ClassNotFoundException | IOException e) {
            eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
        } finally {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
            }
            eventListener.onStopDataExchangeSocketThread(this, socket);
        }
    }

    public void sendMsg(Object msg) {
        try {
            if (msg instanceof byte[]) {
                System.out.println("В Сенд МСГ " + Arrays.toString((byte[]) msg));
            }
            if (msg instanceof String) {
                System.out.println("В Сенд МСГ " + msg);
            }
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            eventListener.onExceptionDataExchangeSocketThread(this, socket, e);
            close();
        }
    }

    public void close() {
        interrupt();
    }
}
