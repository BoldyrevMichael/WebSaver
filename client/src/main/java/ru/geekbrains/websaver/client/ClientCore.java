package ru.geekbrains.websaver.client;

import ru.geekbrains.websaver.common.DataExchangeSocketThread;
import ru.geekbrains.websaver.common.DataExchangeSocketThreadListener;
import ru.geekbrains.websaver.common.Messages;
import ru.geekbrains.websaver.common.NetworkProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientCore implements DataExchangeSocketThreadListener {

    private ClientController clientController;
    private ClientDataExchangeSocketThread clientDataExchangeSocketThread;
    List<File> clientFilesList;
    List<String> lastModifTimes;
    byte[] buffer = new byte[1024];
    byte[] lastBuffer;
    long numberOfBuffers;

    ClientCore(ClientController clientController) {
        connect();
        this.clientController = clientController;
    }

    private void connect() {
        try {
            Socket socket = new Socket(NetworkProperties.HOST_NAME, NetworkProperties.PORT);
            clientDataExchangeSocketThread = new ClientDataExchangeSocketThread(this, "ClientDataExchangeSocketThread", socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registr(String login, String pass, String repPass) {
        clientDataExchangeSocketThread.sendMsg(Messages.getRegistrRequest(login, pass, repPass));
    }

    void login(String login, String pass) {
        Formatter fmt = new Formatter();
        Calendar calendar = Calendar.getInstance();
        clientDataExchangeSocketThread.sendMsg(Messages.getLoginRequest(login, pass, fmt.format("%tY.%tm.%td %tT %tA", calendar, calendar, calendar, calendar, calendar).toString()));
        fmt.close();
    }

    void addFile(File file) {
        if (clientFilesList.contains(file)) {
            return;
        }
        clientFilesList.add(file);
        numberOfBuffers = file.length() / buffer.length;
        clientDataExchangeSocketThread.sendMsg(file);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            for (long i = 0; i < numberOfBuffers; i++) {
                fileInputStream.read(buffer, 0, buffer.length);
                //System.out.println(Arrays.toString(buffer));
                clientDataExchangeSocketThread.sendMsg(buffer);
                clientDataExchangeSocketThread.sendMsg(Messages.getDivide());
            }
            if (fileInputStream.available() > 0) {
                lastBuffer = new byte[fileInputStream.available()];
                fileInputStream.read(lastBuffer, 0, lastBuffer.length);
                //System.out.println(Arrays.toString(lastBuffer));
                clientDataExchangeSocketThread.sendMsg(lastBuffer);
            }
            lastBuffer = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Список файлов клиента в методе add" + clientFilesList);
    }

    @Override
    public void onReceiveMsg(DataExchangeSocketThread dataExchangeSocketThread, Socket socket, Object parcel) {
        if (parcel instanceof String) {
            String[] tokens = ((String) parcel).split(Messages.DELIMITER);
            String type = tokens[0];
            switch (type) {
                case Messages.LOGIN_OK:
                    dataExchangeSocketThread.sendMsg(Messages.getFiles(tokens[1]));
                    break;
                case Messages.LOGIN_ERROR:
                    clientController.onLoginError(tokens[1]);
                    break;
                case Messages.REGISTR_OK:
                    clientController.onRegOk(tokens[1]);
                    break;
                case Messages.REGISTR_ERROR:
                    clientController.onRegError(tokens[1]);
                    break;
                case Messages.DEL_OK:
                    onDelOkCore(tokens[1], tokens[2]);
                    break;
                case Messages.DEL_ERROR:
                    clientController.onDelError(tokens[1]);
                    break;
                case Messages.RECEIVE_OK:
                    lastModifTimes = getLastModifTimes(clientFilesList);
                    clientController.onReceiveOk(tokens[1], tokens[2]);
                    break;
                case Messages.RECEIVE_ERROR:
                    onReceiveErrorCore(tokens[1], tokens[2]);
                    break;
                default:
                    throw new RuntimeException("Unknown message type: " + type);
            }
        } else if (parcel instanceof List) {
            clientFilesList = (List<File>) parcel;
            lastModifTimes = getLastModifTimes(clientFilesList);
            clientController.onGetList();
        }
    }

    @Override
    public void onStartDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket) {
        System.out.println("На клиенте начал работу поток для обмена данными с сервером " + socket + "..");
    }

    @Override
    public void onReadyDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket) {
        System.out.println("На клиенте поток для обмена данными с сервером " + socket + " подготовлен к передаче данных..");
    }

    @Override
    public void onExceptionDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket, Exception e) {
        System.out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
    }

    @Override
    public void onStopDataExchangeSocketThread(DataExchangeSocketThread dataExchangeSocketThread, Socket socket) {
        System.out.println("На клиенте завершил работу поток для обмена данными с клиентом " + socket + ".");
    }

    private List<String> getLastModifTimes(List<File> list) {
        List<String> lastModifTimes = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        for (File file : list) {
            Formatter fmt = new Formatter();
            calendar.setTimeInMillis(file.lastModified());
            lastModifTimes.add(fmt.format("%tY.%tm.%td %tT %tA", calendar, calendar, calendar, calendar, calendar).toString());
            fmt.close();
        }
        return lastModifTimes;
    }

    void onDelOkCore(String msg, String fileName) {
        Iterator<File> iterator = clientFilesList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getName().equals(fileName)) {
                iterator.remove();
            }
        }
        lastModifTimes = getLastModifTimes(clientFilesList);
        clientController.onDelOkController(msg);
    }

    void onReceiveErrorCore(String msg, String fileName) {
        Iterator<File> iterator = clientFilesList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getName().equals(fileName)) {
                iterator.remove();
            }
        }
        clientController.onReceiveErrorController(msg);
    }
}
