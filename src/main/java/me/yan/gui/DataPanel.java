package me.yan.gui;

import me.yan.model.DataModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.ResultSet;

import static me.yan.model.DBUtils.getResultSet;

public class DataPanel extends JPanel {
    private final JTable table;
    private final DataModel dataModel;
    ResultSet result;

    public DataPanel(String resultSetString) {
        try {
            result = getResultSet(resultSetString);
            dataModel = new DataModel(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        table = new JTable(dataModel);

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        table.setRowHeight(30);
        table.setShowGrid(true);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(table.getTableHeader());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        configureColumnSize(table);
        add(scrollPane);
    }

    public JTable getTable() {
        return table;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void configureColumnSize(JTable table) {
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
