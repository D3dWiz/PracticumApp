package me.yan.controller;

import com.jgoodies.forms.layout.CellConstraints;
import me.yan.gui.MainFrame;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static me.yan.model.DBUtils.getConnection;

public class GenreController extends BaseController {
    private static final CellConstraints cc = new CellConstraints();
    private static JDialog dialog;
    private static String genre;

    public static void generateFields() {
        dialog.add(new JLabel("Жанр: "), cc.xy(1, 1));
        JTextField genreField = new JTextField();
        genreField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                genre = genreField.getText();
            }
        });
        dialog.add(genreField, cc.xy(3, 1));
    }

    public static void addGenreDialog() {
        dialog = new JDialog();
        dialog.setTitle("Добави жанр");
        initDialog(dialog);
        generateFields();

        JButton addButton = new JButton("Добави");
        addButton.addActionListener(e -> {
            try {
                int genreId = getOrCreate("Genres", genre);
                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(addButton, cc.xyw(1, 3, 3));
    }

    public static void editGenreDialog() {
        dialog = new JDialog();
        dialog.setTitle("Промени жанр");
        initDialog(dialog);
        generateFields();

        JButton editButton = new JButton("Промени");
        editButton.addActionListener(e -> {
            try {
                int genreId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);

                PreparedStatement genreStmt = conn.prepareStatement("UPDATE Genres SET Name = ? WHERE GenreID = ?");
                genreStmt.setString(1, genre);
                genreStmt.setInt(2, genreId);
                genreStmt.executeUpdate();

                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(editButton, cc.xyw(1, 3, 3));
    }

    public static void deleteGenre() {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да изтриете този жанр?\nИзтриването ще доведе до рекурсивно изтриване на всички записи свързани с него!", "Изтриване", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        } else {
            int genreId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);
            PreparedStatement genreStmt;
            try {
                PreparedStatement bookStmt = conn.prepareStatement("SELECT BookID FROM Books_Genres WHERE GenreID = ?");
                bookStmt.setInt(1, genreId);
                ResultSet rs = bookStmt.executeQuery();

                while (rs.next()) {
                    int bookId = rs.getInt("BookID");
                    BookController.deleteBook(bookId);
                }

                genreStmt = conn.prepareStatement("DELETE FROM Genres WHERE GenreID = ?");
                genreStmt.setInt(1, genreId);
                genreStmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            MainFrame.refreshTable();
        }
    }

    public static void searchGenreDialog() {
        dialog = new JDialog();
        dialog.setTitle("Търси жанр");
        initDialog(dialog);
        generateFields();

        JButton searchButton = new JButton("Търси");
        searchButton.addActionListener(e -> {
            String query = "SELECT Genres.Name as Жанр, Books.Title as Заглавие " +
                    "FROM Genres " +
                    "JOIN Books_Genres ON Genres.GenreID = Books_Genres.GenreID " +
                    "JOIN Books ON Books_Genres.BookID = Books.BookID " +
                    "WHERE Genres.Name LIKE ?";

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, "%" + genre + "%");
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