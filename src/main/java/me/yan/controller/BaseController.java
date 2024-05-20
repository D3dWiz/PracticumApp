package me.yan.controller;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.sql.*;

import static me.yan.model.DBUtils.getConnection;

public class BaseController {
    protected static Connection conn;

    protected static void initDialog(JDialog dialog) {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, pref:grow",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu");
        dialog.setLayout(layout);

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

    }

    protected static int getOrCreate(String table, String value) throws SQLException {
        PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM " + table + " WHERE " + "Name" + " = ?");
        selectStmt.setString(1, value);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(table.substring(0, table.length() - 1) + "ID");
        } else {
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO " + table + " (Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, value);
            insertStmt.executeUpdate();

            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create " + table + " with name: " + value);
            }
        }
    }

    protected static void checkAndDelete(String table, String junctionTable, int bookId) throws SQLException {
        String otherIdColumn = junctionTable.substring(junctionTable.split("_")[0].length() + 1, junctionTable.length() - 1) + "ID";

        PreparedStatement otherIdStmt = conn.prepareStatement("SELECT " + otherIdColumn + " FROM " + junctionTable + " WHERE BookID = ?");
        otherIdStmt.setInt(1, bookId);
        ResultSet rs = otherIdStmt.executeQuery();
        if (rs.next()) {
            int otherId = rs.getInt(1);

            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM " + junctionTable + " WHERE " + otherIdColumn + " = ? AND BookID <> ?");
            checkStmt.setInt(1, otherId);
            checkStmt.setInt(2, bookId);
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next() && checkRs.getInt(1) == 0) {
                PreparedStatement deleteJunctionStmt = conn.prepareStatement("DELETE FROM " + junctionTable + " WHERE " + otherIdColumn + " = ?");
                deleteJunctionStmt.setInt(1, otherId);
                deleteJunctionStmt.executeUpdate();

                PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM " + table + " WHERE " + table.substring(0, table.length() - 1) + "ID = ?");
                deleteStmt.setInt(1, otherId);
                deleteStmt.executeUpdate();
            }
        }
    }

    protected static void updateJunction(String table, int bookId, int newOtherId) throws SQLException {
        String otherIdColumn = table.substring(table.split("_")[0].length() + 1, table.length() - 1) + "ID";
        PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + " SET " + otherIdColumn + " = ? WHERE BookID = ?");
        stmt.setInt(1, newOtherId);
        stmt.setInt(2, bookId);
        stmt.executeUpdate();
    }

    protected static void insertIntoJunction(String table, int bookId, int otherId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + " (BookID, " + table.substring(table.split("_")[0].length() + 1, table.length() - 1) + "ID) VALUES (?, ?)");
        stmt.setInt(1, bookId);
        stmt.setInt(2, otherId);
        stmt.executeUpdate();
    }
}
