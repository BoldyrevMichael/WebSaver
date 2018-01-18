package ru.geekbrains.websaver.client;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTableModel extends AbstractTableModel {

    private List<File> filesList;
    private List<String> lastModifTimes;

    FileTableModel(ClientCore clientCore) {
        super();
        filesList = clientCore.clientFilesList;
        lastModifTimes = clientCore.lastModifTimes;
    }

    @Override
    public int getRowCount() {
        if (filesList != null) {
            return filesList.size() + 1;
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
        if (filesList != null) {
            switch (columnIndex) {
                case 0:
                    return (rowIndex + 1);
                case 1:
                    return filesList.get(rowIndex).getName();
                case 2:
                    return (double) filesList.get(rowIndex).length() / 1000;
                case 3:
                    return lastModifTimes.get(rowIndex);
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
