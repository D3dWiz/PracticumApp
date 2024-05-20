package me.yan.controller;

import com.jgoodies.forms.layout.CellConstraints;
import me.yan.gui.MainFrame;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static me.yan.model.DBUtils.getConnection;

public class AuthorController extends BaseController {
    private static final CellConstraints cc = new CellConstraints();
    private static JDialog dialog;
    private static String author;

    public static void generateFields() {
        dialog.add(new JLabel("Автор: "), cc.xy(1, 1));
        JTextField authorField = new JTextField();
        authorField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                author = authorField.getText();
            }
        });
        dialog.add(authorField, cc.xy(3, 1));
    }

    public static void addAuthorDialog() {
        dialog = new JDialog();
        dialog.setTitle("Добави автор");
        initDialog(dialog);
        generateFields();

        JButton addButton = new JButton("Добави");
        addButton.addActionListener(e -> {
            try {
                int authorId = getOrCreate("Authors", author);
                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(addButton, cc.xyw(1, 3, 3));
    }

    public static void editAuthorDialog() {
        dialog = new JDialog();
        dialog.setTitle("Промени автор");
        initDialog(dialog);
        generateFields();

        JButton editButton = new JButton("Промени");
        editButton.addActionListener(e -> {
            try {
                int authorId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);

                PreparedStatement authorStmt = conn.prepareStatement("UPDATE Authors SET Name = ? WHERE AuthorID = ?");
                authorStmt.setString(1, author);
                authorStmt.setInt(2, authorId);
                authorStmt.executeUpdate();

                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(editButton, cc.xyw(1, 3, 3));
    }

    public static void deleteAuthor() {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да изтриете този автор?\nИзтриването ще доведе до рекурсивно изтриване на всички записи свързани с него!", "Изтриване", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        } else {
            int authorId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);
            PreparedStatement authorStmt;
            try {
                PreparedStatement bookStmt = conn.prepareStatement("SELECT BookID FROM Books_Authors WHERE AuthorID = ?");
                bookStmt.setInt(1, authorId);
                ResultSet rs = bookStmt.executeQuery();

                while (rs.next()) {
                    int bookId = rs.getInt("BookID");
                    BookController.deleteBook(bookId);
                }

                authorStmt = conn.prepareStatement("DELETE FROM Authors WHERE AuthorID = ?");
                authorStmt.setInt(1, authorId);
                authorStmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            MainFrame.refreshTable();
        }
    }

    public static void searchAuthorDialog() {
        dialog = new JDialog();
        dialog.setTitle("Търси автор");
        initDialog(dialog);
        generateFields();

        JButton searchButton = new JButton("Търси");
        searchButton.addActionListener(e -> {
            String query = "SELECT Authors.Name as Автор, Books.Title as Заглавие " +
                    "FROM Authors " +
                    "JOIN Books_Authors ON Authors.AuthorID = Books_Authors.AuthorID " +
                    "JOIN Books ON Books_Authors.BookID = Books.BookID " +
                    "WHERE Authors.Name LIKE ?";

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, "%" + author + "%");
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