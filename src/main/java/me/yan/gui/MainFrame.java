package me.yan.gui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import me.yan.controller.AuthorController;
import me.yan.controller.BookController;
import me.yan.controller.GenreController;
import me.yan.controller.PublisherController;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class MainFrame extends JFrame {
    private static final String bookResultSet = "SELECT b.BookID, b.Title Заглавие, a.Name AS Автор, g.Name AS Жанр, " +
            "p.Name AS Издателство, b.PublishYear AS Година, b.AddDate AS Добавена " +
            "FROM Books b " +
            "LEFT JOIN Books_Genres bg ON b.BookID = bg.BookID " +
            "LEFT JOIN Genres g ON bg.GenreID = g.GenreID " +
            "LEFT JOIN Books_Authors ba ON b.BookID = ba.BookID " +
            "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID " +
            "LEFT JOIN Books_Publishers bp ON b.BookID = bp.BookID " +
            "LEFT JOIN Publishers p ON bp.PublisherID = p.PublisherID";
    private static final String genreResultSet = "SELECT g.GenreID, g.Name AS Жанр, GROUP_CONCAT(b.Title SEPARATOR ', ') AS Книги FROM genres g" +
            " LEFT JOIN books_genres bg ON g.GenreID = bg.GenreID" +
            " LEFT JOIN books b ON bg.BookID = b.BookID" +
            " GROUP BY g.GenreID";
    private static final String authorResultSet = "SELECT a.AuthorID, a.Name AS Автор, GROUP_CONCAT(b.Title SEPARATOR ', ') AS Книги FROM authors a" +
            " LEFT JOIN books_authors ba ON a.AuthorID = ba.AuthorID" +
            " LEFT JOIN books b ON ba.BookID = b.BookID" +
            " GROUP BY a.AuthorID";
    private static final String publisherResultSet = "SELECT p.PublisherID, p.Name AS Издателство, GROUP_CONCAT(b.Title SEPARATOR ', ') AS Книги FROM publishers p" +
            " LEFT JOIN books_publishers bp ON p.PublisherID = bp.PublisherID" +
            " LEFT JOIN books b ON bp.BookID = b.BookID" +
            " GROUP BY p.PublisherID";
    public static String currentTable = "books";
    public static DataPanel currentPanel;
    public static String currentResultSet = bookResultSet;
    private static DataPanel bookPanel;
    private static DataPanel authorPanel;
    private static DataPanel publisherPanel;
    private static DataPanel genrePanel;

    public MainFrame() {
        initUI();
        initTopPanel();
        initLeftPanel();

        setLocationRelativeTo(null);
        pack();
        try {
            setLocationByPlatform(true);
            setVisible(true);
        } catch (Throwable ignoreAndContinue) {
        }
    }

    public static void refreshTable() {
        currentPanel.getDataModel().updateResultSet(currentResultSet);
        bookPanel.invalidate();
        authorPanel.invalidate();
        publisherPanel.invalidate();
        genrePanel.invalidate();
    }

    private void initUI() {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#ba63e2"));
        FlatMacLightLaf.setup();


        setTitle("Мениджър за библиотекa");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(1240, 640));
    }

    private void initTopPanel() {
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 5, 5);
        JPanel topPanel = new JPanel(layout);

        float[] hsb = Color.RGBtoHSB(68, 71, 89, null);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.getHSBColor(hsb[0], hsb[1], hsb[2])));

        JButton addBookButton = new JButton("Добавяне");
        JButton editButton = new JButton("Редактиране");
        JButton deleteButton = new JButton("Изтриване");
        JButton searchButton = new JButton("Търсене");
        JButton inquiryButton = new JButton("Справка");
        JButton refreshButton = new JButton("Обновяване");

        addBookButton.addActionListener(e -> {
            switch (currentTable) {
                case "books" -> BookController.addBookDialog();
                case "authors" -> AuthorController.addAuthorDialog();
                case "genres" -> GenreController.addGenreDialog();
                case "publishers" -> PublisherController.addPublisherDialog();
            }
        });

        editButton.addActionListener(e -> {
            if (currentPanel.getTable().getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Моля, изберете запис от таблицата!", "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            switch (currentTable) {
                case "books" -> BookController.editBookDialog();
                case "authors" -> AuthorController.editAuthorDialog();
                case "genres" -> GenreController.editGenreDialog();
                case "publishers" -> PublisherController.editPublisherDialog();
            }
        });

        deleteButton.addActionListener(e -> {
            if (currentPanel.getTable().getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Моля, изберете запис от таблицата!", "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            switch (currentTable) {
                case "books" -> BookController.deleteBook();
                case "authors" -> AuthorController.deleteAuthor();
                case "genres" -> GenreController.deleteGenre();
                case "publishers" -> PublisherController.deletePublisher();
            }
        });

        searchButton.addActionListener(e -> {
            switch (currentTable) {
                case "books" -> BookController.searchBookDialog();
                case "authors" -> AuthorController.searchAuthorDialog();
                case "genres" -> GenreController.searchGenreDialog();
                case "publishers" -> PublisherController.searchPublisherDialog();
            }
        });

        refreshButton.addActionListener(e -> refreshTable());

        inquiryButton.addActionListener((e -> new InquiryFrame()));

        topPanel.add(addBookButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(inquiryButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());

        JTabbedPane jTabbedPane = getCategoriesTabbedPane();
        leftPanel.add(jTabbedPane, BorderLayout.WEST);

        add(leftPanel, BorderLayout.CENTER);
    }

    private JTabbedPane getCategoriesTabbedPane() {
        JTabbedPane categories = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

        bookPanel = new DataPanel(bookResultSet);
        authorPanel = new DataPanel(authorResultSet);
        publisherPanel = new DataPanel(publisherResultSet);
        genrePanel = new DataPanel(genreResultSet);
        currentPanel = bookPanel;

        categories.addTab("Книги", bookPanel);
        categories.addTab("Автори", authorPanel);
        categories.addTab("Издателства", publisherPanel);
        categories.addTab("Жанрове", genrePanel);

        categories.addChangeListener(e -> {
            refreshTable();
            JTabbedPane tabs = (JTabbedPane) e.getSource();
            int index = tabs.getSelectedIndex();
            switch (tabs.getTitleAt(index)) {
                case "Книги" -> {
                    currentResultSet = bookResultSet;
                    currentPanel = bookPanel;
                    currentTable = "books";
                }
                case "Автори" -> {
                    currentResultSet = authorResultSet;
                    currentPanel = authorPanel;
                    currentTable = "authors";
                }
                case "Издателства" -> {
                    currentResultSet = publisherResultSet;
                    currentPanel = publisherPanel;
                    currentTable = "publishers";
                }
                case "Жанрове" -> {
                    currentResultSet = genreResultSet;
                    currentPanel = genrePanel;
                    currentTable = "genres";
                }
            }
        });

        return categories;
    }
}
