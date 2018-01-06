package ru.geekbrains.websaver.server;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;

import java.net.Socket;

public class ServerDataExchangeSocketThread extends DataExchangeSocketThread {
    public ServerDataExchangeSocketThread(DataExchangeSocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }
}
