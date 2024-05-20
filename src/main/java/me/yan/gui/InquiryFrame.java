package me.yan.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import me.yan.model.DataModel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static me.yan.model.DBUtils.getConnection;

public class InquiryFrame extends JFrame {

    public InquiryFrame() {
        setTitle("Справка");
        setMinimumSize(new Dimension(320, 240));
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, pref:grow",
                "p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu");
        setLayout(layout);

        CellConstraints cc = new CellConstraints();
        add(new JLabel("Заглавие:"), cc.xy(1, 1));
        JTextField titleField = new JTextField();
        add(titleField, cc.xy(3, 1));

        add(new JLabel("Автор:"), cc.xy(1, 3));
        JTextField authorField = new JTextField();
        add(authorField, cc.xy(3, 3));

        add(new JLabel("Жанр:"), cc.xy(1, 5));
        JTextField genreField = new JTextField();
        add(genreField, cc.xy(3, 5));

        add(new JLabel("Издателство:"), cc.xy(1, 7));
        JTextField publisherField = new JTextField();
        add(publisherField, cc.xy(3, 7));

        JButton searchButton = new JButton("Търсене");
        add(searchButton, cc.xyw(1, 9, 3));

        searchButton.addActionListener(e -> search(titleField.getText(), authorField.getText(), genreField.getText(), publisherField.getText()));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static PreparedStatement getPreparedStatement() throws SQLException {
        String query = "SELECT b.Title AS Заглавие, a.Name AS Автор, g.Name AS Жанр, p.Name AS Издателство " +
                "FROM Books b " +
                "LEFT JOIN Books_Genres bg ON b.BookID = bg.BookID " +
                "LEFT JOIN Genres g ON bg.GenreID = g.GenreID " +
                "LEFT JOIN Books_Authors ba ON b.BookID = ba.BookID " +
                "LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID " +
                "LEFT JOIN Books_Publishers bp ON b.BookID = bp.BookID " +
                "LEFT JOIN Publishers p ON bp.PublisherID = p.PublisherID " +
                "WHERE b.Title LIKE ? AND a.Name LIKE ? AND g.Name LIKE ? AND p.Name LIKE ?";

        Connection conn = getConnection();
        return conn.prepareStatement(query);
    }

    private void search(String titleField, String authorField, String genreField, String publisherField) {
        try {
            PreparedStatement stmt = getPreparedStatement();
            stmt.setString(1, "%" + titleField + "%");
            stmt.setString(2, "%" + authorField + "%");
            stmt.setString(3, "%" + genreField + "%");
            stmt.setString(4, "%" + publisherField + "%");

            ResultSet rs = stmt.executeQuery();
            DataModel model = new DataModel(rs);
            JTable table = new JTable(model);
            JDialog dialog = new JDialog();
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(table), BorderLayout.CENTER);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialog.setMinimumSize(new Dimension(640, 480));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
