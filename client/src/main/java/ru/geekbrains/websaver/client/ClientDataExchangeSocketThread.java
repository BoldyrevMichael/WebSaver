package ru.geekbrains.websaver.client;


import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;

import java.net.Socket;

public class ClientDataExchangeSocketThread extends DataExchangeSocketThread {
    public ClientDataExchangeSocketThread(DataExchangeSocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }
}
