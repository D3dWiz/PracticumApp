package me.yan.controller;

import com.jgoodies.forms.layout.CellConstraints;
import me.yan.gui.MainFrame;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static me.yan.model.DBUtils.getConnection;

public class PublisherController extends BaseController {
    private static final CellConstraints cc = new CellConstraints();
    private static JDialog dialog;
    private static String publisher;

    public static void generateFields() {
        dialog.add(new JLabel("Издателство: "), cc.xy(1, 1));
        JTextField publisherField = new JTextField();
        publisherField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                publisher = publisherField.getText();
            }
        });
        dialog.add(publisherField, cc.xy(3, 1));
    }

    public static void addPublisherDialog() {
        dialog = new JDialog();
        dialog.setTitle("Добави издателство");
        initDialog(dialog);
        generateFields();

        JButton addButton = new JButton("Добави");
        addButton.addActionListener(e -> {
            try {
                int publisherId = getOrCreate("Publishers", publisher);
                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(addButton, cc.xyw(1, 3, 3));
    }

    public static void editPublisherDialog() {
        dialog = new JDialog();
        dialog.setTitle("Промени издателство");
        initDialog(dialog);
        generateFields();

        JButton editButton = new JButton("Промени");
        editButton.addActionListener(e -> {
            try {
                int publisherId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);

                PreparedStatement publisherStmt = conn.prepareStatement("UPDATE Publishers SET Name = ? WHERE PublisherID = ?");
                publisherStmt.setString(1, publisher);
                publisherStmt.setInt(2, publisherId);
                publisherStmt.executeUpdate();

                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(editButton, cc.xyw(1, 3, 3));
    }

    public static void deletePublisher() {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да изтриете това издателство?\nИзтриването ще доведе до рекурсивно изтриване на всички записи свързани с него!", "Изтриване", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        } else {
            int publisherId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);
            PreparedStatement publisherStmt;
            try {
                PreparedStatement bookStmt = conn.prepareStatement("SELECT BookID FROM Books_Publishers WHERE PublisherID = ?");
                bookStmt.setInt(1, publisherId);
                ResultSet rs = bookStmt.executeQuery();

                while (rs.next()) {
                    int bookId = rs.getInt("BookID");
                    BookController.deleteBook(bookId);
                }

                publisherStmt = conn.prepareStatement("DELETE FROM Publishers WHERE PublisherID = ?");
                publisherStmt.setInt(1, publisherId);
                publisherStmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            MainFrame.refreshTable();
        }
    }

    public static void searchPublisherDialog() {
        dialog = new JDialog();
        dialog.setTitle("Търси издателство");
        initDialog(dialog);
        generateFields();

        JButton searchButton = new JButton("Търси");
        searchButton.addActionListener(e -> {
            String query = "SELECT Publishers.Name as Издателство, Books.Title as Заглавие " +
                    "FROM Publishers " +
                    "JOIN Books_Publishers ON Publishers.PublisherID = Books_Publishers.PublisherID " +
                    "JOIN Books ON Books_Publishers.BookID = Books.BookID " +
                    "WHERE Publishers.Name LIKE ?";

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, "%" + publisher + "%");
                ResultSet resultSet = stmt.executeQuery();
                if (!resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Няма резултати!");
                    return;
                }
                MainFrame.currentPanel.getDataModel().updateResultSet(resultSet);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        dialog.add(searchButton, cc.xyw(1, 3, 3));
    }
}