package me.yan.controller;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.jgoodies.forms.layout.CellConstraints;
import me.yan.gui.MainFrame;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

import static me.yan.model.DBUtils.getConnection;

public class BookController extends BaseController {
    private static final CellConstraints cc = new CellConstraints();
    private static JDialog dialog;
    private static String title;
    private static String author;
    private static String genre;
    private static String publisher;
    private static String publishYear;
    private static String addDate;

    public static void generateFields() {
        // TITLE
        dialog.add(new JLabel("Заглавие: "), cc.xy(1, 1));
        JTextField titleField = new JTextField();
        titleField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                title = titleField.getText();
            }
        });
        dialog.add(titleField, cc.xy(3, 1));
        // AUTHOR
        dialog.add(new JLabel("Автор: "), cc.xy(1, 3));
        JComboBox<String> authorBox = new JComboBox<>();
        authorBox.setEditable(true);
        authorBox.addActionListener(e -> author = Objects.requireNonNull(authorBox.getSelectedItem()).toString());

        try {
            conn = getConnection();
            PreparedStatement authorStmt = conn.prepareStatement("SELECT Name FROM Authors");
            ResultSet authorRs = authorStmt.executeQuery();
            while (authorRs.next()) {
                authorBox.addItem(authorRs.getString("Name"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        dialog.add(authorBox, cc.xy(3, 3));
        // GENRE
        dialog.add(new JLabel("Жанр: "), cc.xy(1, 5));
        JComboBox<String> genreBox = new JComboBox<>();
        genreBox.setEditable(true);
        genreBox.addActionListener(e -> genre = Objects.requireNonNull(genreBox.getSelectedItem()).toString());

        try {
            conn = getConnection();
            PreparedStatement genreStmt = conn.prepareStatement("SELECT Name FROM Genres");
            ResultSet genresRs = genreStmt.executeQuery();
            while (genresRs.next()) {
                genreBox.addItem(genresRs.getString("Name"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        dialog.add(genreBox, cc.xy(3, 5));
        // PUBLISHER
        dialog.add(new JLabel("Издателство: "), cc.xy(1, 7));
        JComboBox publisherBox = new JComboBox();
        publisherBox.setEditable(true);
        publisherBox.addActionListener(e -> publisher = Objects.requireNonNull(publisherBox.getSelectedItem()).toString());

        try {
            conn = getConnection();
            PreparedStatement publisherStmt = conn.prepareStatement("SELECT Name FROM Publishers");
            ResultSet publisherRs = publisherStmt.executeQuery();
            while (publisherRs.next()) {
                publisherBox.addItem(publisherRs.getString("Name"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        dialog.add(publisherBox, cc.xy(3, 7));

        dialog.add(new JLabel("Година: "), cc.xy(1, 9));
        JTextField yearField = new JTextField();
        yearField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                publishYear = yearField.getText();
            }
        });
        dialog.add(yearField, cc.xy(3, 9));

        DatePicker datePicker = new DatePicker();
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        datePicker.setSettings(dateSettings);
        datePicker.addDateChangeListener(event -> addDate = datePicker.getDateStringOrEmptyString());
        dialog.add(new JLabel("Добавена: "), cc.xy(1, 11));
        dialog.add(datePicker, cc.xy(3, 11));

        if (MainFrame.currentPanel.getTable().getSelectedRow() != -1) {
            titleField.setText((String) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 1));
            authorBox.setSelectedItem(MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 2));
            genreBox.setSelectedItem(MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 3));
            publisherBox.setSelectedItem(MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 4));
            yearField.setText(String.valueOf(MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 5)));
            datePicker.setDate(LocalDate.parse(String.valueOf(MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 6))));
        }
    }

    public static void addBookDialog() {
        dialog = new JDialog();
        dialog.setTitle("Добави книга");
        initDialog(dialog);
        generateFields();

        JButton addButton = new JButton("Добави");
        addButton.addActionListener(e -> {
            try {
                int authorId = getOrCreate("Authors", author);
                int genreId = getOrCreate("Genres", genre);
                int publisherId = getOrCreate("Publishers", publisher);

                PreparedStatement bookStmt = conn.prepareStatement("INSERT INTO Books (Title, PublishYear, AddDate) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                bookStmt.setString(1, title);
                bookStmt.setInt(2, Integer.parseInt(publishYear));
                bookStmt.setDate(3, Date.valueOf(addDate));
                bookStmt.executeUpdate();

                ResultSet rs = bookStmt.getGeneratedKeys();
                if (rs.next()) {
                    int bookId = rs.getInt(1);

                    insertIntoJunction("Books_Authors", bookId, authorId);
                    insertIntoJunction("Books_Genres", bookId, genreId);
                    insertIntoJunction("Books_Publishers", bookId, publisherId);
                }

                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(addButton, cc.xyw(1, 15, 3));
    }

    public static void editBookDialog() {
        dialog = new JDialog();
        dialog.setTitle("Промени книга");
        initDialog(dialog);
        generateFields();


        JButton editButton = new JButton("Промени");
        editButton.addActionListener(e -> {
            try {
                int bookId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);

                int newAuthorId = getOrCreate("Authors", author);
                int newGenreId = getOrCreate("Genres", genre);
                int newPublisherId = getOrCreate("Publishers", publisher);

                PreparedStatement bookStmt = conn.prepareStatement("UPDATE Books SET Title = ?, PublishYear = ?, AddDate = ? WHERE BookID = ?");
                bookStmt.setString(1, title);
                bookStmt.setInt(2, Integer.parseInt(publishYear));
                bookStmt.setDate(3, Date.valueOf(addDate));
                bookStmt.setInt(4, bookId);
                bookStmt.executeUpdate();

                updateJunction("Books_Authors", bookId, newAuthorId);
                updateJunction("Books_Genres", bookId, newGenreId);
                updateJunction("Books_Publishers", bookId, newPublisherId);

                MainFrame.refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        dialog.add(editButton, cc.xyw(1, 15, 3));
    }

    public static void deleteBook() {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да изтриете тази книга?", "Изтриване", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int bookId = (Integer) MainFrame.currentPanel.getDataModel().getValueAt(MainFrame.currentPanel.getTable().getSelectedRow(), 0);
            recursiveDeletion(bookId);
        }
    }

    public static void deleteBook(int bookId) {
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        recursiveDeletion(bookId);
    }

    private static void recursiveDeletion(int bookId) {
        PreparedStatement bookStmt;
        try {
            checkAndDelete("Authors", "Books_Authors", bookId);
            checkAndDelete("Genres", "Books_Genres", bookId);
            checkAndDelete("Publishers", "Books_Publishers", bookId);

            bookStmt = conn.prepareStatement("DELETE FROM Books WHERE BookID = ?");
            bookStmt.setInt(1, bookId);
            bookStmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        MainFrame.refreshTable();
    }

    public static void searchBookDialog() {
        dialog = new JDialog();
        dialog.setTitle("Търси книга");
        initDialog(dialog);
        generateFields();

        JButton searchButton = new JButton("Търси");
        searchButton.addActionListener(e -> {
            String query = "SELECT Books.Title as Заглавие, Authors.Name as Автор, Genres.Name as Жанр, Publishers.Name as Издателство, Books.PublishYear AS Година, Books.AddDate AS Добавена " +
                    "FROM Books " +
                    "LEFT JOIN Books_Authors ON Books.BookID = Books_Authors.BookID " +
                    "LEFT JOIN Authors ON Books_Authors.AuthorID = Authors.AuthorID " +
                    "LEFT JOIN Books_Genres ON Books.BookID = Books_Genres.BookID " +
                    "LEFT JOIN Genres ON Books_Genres.GenreID = Genres.GenreID " +
                    "LEFT JOIN Books_Publishers ON Books.BookID = Books_Publishers.BookID " +
                    "LEFT JOIN Publishers ON Books_Publishers.PublisherID = Publishers.PublisherID " +
                    "WHERE Books.Title LIKE ? AND Genres.Name LIKE ? AND Authors.Name LIKE ? AND Publishers.Name LIKE ? AND Books.PublishYear LIKE ? AND Books.AddDate LIKE ?" +
                    "GROUP BY Books.BookID";

            PreparedStatement stmt;
            try {
                stmt = conn.prepareStatement(query);
                stmt.setString(1, "%" + title + "%");
                stmt.setString(2, "%" + genre + "%");
                stmt.setString(3, "%" + author + "%");
                stmt.setString(4, "%" + publisher + "%");
                stmt.setString(5, "%" + publishYear + "%");
                stmt.setString(6, "%" + addDate + "%");
                ResultSet resultSet = stmt.executeQuery();
                MainFrame.currentPanel.getDataModel().updateResultSet(resultSet);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        dialog.add(searchButton, cc.xyw(1, 15, 3));
    }
}
