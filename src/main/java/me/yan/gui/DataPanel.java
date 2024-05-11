package me.yan.gui;

import me.yan.model.DBUtils;
import me.yan.model.DataModel;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataPanel extends JPanel {
    ResultSet result;
    Connection conn=null;
    PreparedStatement state=null;

    public DataPanel(String resultSetString) {
        try {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            conn= DBUtils.getConnection();
            state=conn.prepareStatement(resultSetString);
            result=state.executeQuery();
            JTable table = new JTable(new DataModel(result));
            table.setRowHeight(30);
            table.setShowGrid(true);
            configureColumnSize(table);

            TableColumnModel columnModel = table.getColumnModel();
            columnModel.removeColumn( columnModel.getColumn(0) );

            add(table.getTableHeader());
            add(table);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void configureColumnSize(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            double width = table.getTableHeader().getHeaderRect(column).getWidth();
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300)
                width = 300;
            columnModel.getColumn(column).setPreferredWidth((int) width);
        }
    }
}
