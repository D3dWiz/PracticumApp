package me.yan.model;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class DataModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = 1L;
    private ResultSet result;
    private int rowCount;
    private int columnCount;
    private final ArrayList<Object> data=new ArrayList<Object>();

    public DataModel(ResultSet rs) throws Exception
    {
        setRS(rs);
    }

    public void setRS(ResultSet rs) throws Exception {
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
        Object[] row=(Object[]) data.get(rowIndex);
        return row[columnIndex];
    }

    public String getColumnName(int columnIndex){
        try{
            ResultSetMetaData metaData=result.getMetaData();
            return metaData.getColumnLabel(columnIndex + 1);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
