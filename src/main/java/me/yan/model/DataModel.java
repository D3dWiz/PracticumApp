package me.yan.model;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import static me.yan.model.DBUtils.getResultSet;

public class DataModel extends AbstractTableModel {
    private ArrayList<Object> data = new ArrayList<>();
    private ResultSet result;
    private int rowCount;
    private int columnCount;

    public DataModel(ResultSet rs) throws Exception {
        setRS(rs);
    }

    public void setRS(ResultSet rs) throws Exception {
        data = new ArrayList<>();
        result = rs;
        ResultSetMetaData metaData = rs.getMetaData();
        rowCount = 0;
        columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int j = 0; j < columnCount; j++) {
                row[j] = rs.getObject(j + 1);
            }
            data.add(row);
            rowCount++;
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = (Object[]) data.get(rowIndex);
        return row[columnIndex];
    }

    public String getColumnName(int columnIndex) {
        try {
            ResultSetMetaData metaData = result.getMetaData();
            return metaData.getColumnLabel(columnIndex + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateResultSet(String currentRS) {
        try {
            setRS(getResultSet(currentRS));
            fireTableDataChanged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateResultSet(ResultSet currentRS) {
        try {
            setRS(currentRS);
            fireTableDataChanged();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
