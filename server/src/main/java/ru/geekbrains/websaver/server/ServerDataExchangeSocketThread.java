package ru.geekbrains.websaver.server;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.List;

class ServerDataExchangeSocketThread extends DataExchangeSocketThread {
    List<File> serverClientFilesList;
    String clientDirectory;
    File receivedFile;
    FileOutputStream fileOutputStream;
    byte[] buffer = new byte[1024];
    long numberOfBuffers;
    boolean flagOfEndWrite = true;

    ServerDataExchangeSocketThread(DataExchangeSocketThreadListener eventListener, String name, Socket socket) {
        super(eventListener, name, socket);
    }
}
