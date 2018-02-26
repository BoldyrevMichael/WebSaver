package ru.geekbrains.websaver.client;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {

    ClientCore clientCore;

    FileTableModel(ClientCore clientCore) {
        super();
        this.clientCore = clientCore;
    }

    @Override
    public int getRowCount() {
        if (clientCore.clientFilesList != null) {
            return clientCore.clientFilesList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (clientCore.clientFilesList != null) {
            switch (columnIndex) {
                case 0:
                    return (rowIndex + 1);
                case 1:
                    return clientCore.clientFilesList.get(rowIndex).getName();
                case 2:
                    return (double) clientCore.clientFilesList.get(rowIndex).length() / 1000;
                case 3:
                    return clientCore.lastModifTimes.get(rowIndex);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "№";
            case 1:
                return "Name";
            case 2:
                return "Size, kB";
            case 3:
                return "Last modified";
            default:
                return "unnamed";
        }
    }
}
