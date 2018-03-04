package ru.geekbrains.websaver.server;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;

import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ServerDataExchangeSocketThread extends DataExchangeSocketThread {
    public List<File> serverClientFilesList;
    public String clientDirectory;
    File receivedFile;
    public ServerDataExchangeSocketThread(DataExchangeSocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }
}
