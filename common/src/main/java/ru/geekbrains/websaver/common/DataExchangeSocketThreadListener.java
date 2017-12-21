package ru.geekbrains.websaver.common;

import java.net.Socket;

public interface DataExchangeSocketThreadListener {

    void onStartDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket);

    void onReadyDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket);

    void onExceptionDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket, Exception e);

    void onStopDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket);
}
