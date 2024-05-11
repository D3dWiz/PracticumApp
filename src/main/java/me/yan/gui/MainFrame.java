package me.yan.gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

import java.awt.*;

public class MainFrame extends JFrame {
    String bookResultSet = "SELECT ID, TITLE AS Заглавие, GENRE AS Жанр, AUTHOR AS Автор, PUBLISHER AS Издател, PUBLISH_DATE AS Издадена, ADD_DATE AS Добавена, TAKEN AS Взета, TAKEN_BY AS Взета_от, TAKEN_DATE AS Взета_на FROM books";
    String authorResultSet = "SELECT ID, NAME AS Име, BOOKS AS Книги FROM authors";
    String publisherResultSet = "SELECT ID, NAME AS Издателство, BOOKS AS Книги FROM publishers";
    String currentResultSet = bookResultSet;

    public MainFrame() {
        initGUI();
        initTopPanel();
        initLeftPanel();
    }

    private void initGUI() {
        FlatMacDarkLaf.setup();

        setTitle("Library Manager");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(1240, 640));
        pack();
        setLocationRelativeTo(null);
        try {
            setLocationByPlatform(true);
            setVisible(true);
        } catch(Throwable ignoreAndContinue) {
        }
    }

    public void initTopPanel() {
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 5, 5);
        JPanel topPanel = new JPanel(layout);

        float[] hsb = Color.RGBtoHSB(68, 71, 89, null);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.getHSBColor(hsb[0], hsb[1], hsb[2])));

        JButton addBookButton = new JButton("Добавяне");
        JButton editButton = new JButton("Редактиране");
        JButton deleteButton = new JButton("Изтриване");
        JButton searchButton = new JButton("Търсене");
        JButton refreshButton = new JButton("Обновяване");

        addBookButton.addActionListener(e -> {
            System.out.println("Add book button clicked");
        });

        editButton.addActionListener(e -> {
            System.out.println("Edit button clicked");
        });

        deleteButton.addActionListener(e -> {
            System.out.println("Delete button clicked");
        });

        searchButton.addActionListener(e -> {
            System.out.println("Search button clicked");
        });

        refreshButton.addActionListener(e -> {
            System.out.println("Refresh button clicked");
        });

        topPanel.add(addBookButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
    }

    public void initLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());

        JTabbedPane jTabbedPane = getjTabbedPane();
        leftPanel.add(jTabbedPane, BorderLayout.WEST);

        add(leftPanel, BorderLayout.CENTER);
    }

    private JTabbedPane getjTabbedPane() {
        JTabbedPane categories = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

        categories.addTab("Книги", new DataPanel(bookResultSet));
        categories.addTab("Автори", new DataPanel(authorResultSet));
        categories.addTab("Издателства", new DataPanel(publisherResultSet));

        categories.addChangeListener(e -> {
            JTabbedPane tabs = (JTabbedPane) e.getSource();
            int index = tabs.getSelectedIndex();
            if (tabs.getTitleAt(index).equals("Книги")) {
                currentResultSet = bookResultSet;
            } else if (tabs.getTitleAt(index).equals("Автори")) {
                currentResultSet = authorResultSet;
            } else if (tabs.getTitleAt(index).equals("Издателства")) {
                currentResultSet = publisherResultSet;
            }
        });

        return categories;
    }


}
