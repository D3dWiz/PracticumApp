package me.yan.gui;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainFrame extends JFrame {
    Connection conn = null;
    PreparedStatement state = null;
    ResultSet result;

    public MainFrame() {
        initGUI();
        initLeftPanel();
        initRightPanel();
    }

    private void initGUI() {
        FlatAtomOneDarkIJTheme.setup();

        setTitle("PracticumApp");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        setPreferredSize(new Dimension(1024, 640));
        pack();
        setLocationRelativeTo(null);
        try {
            setLocationByPlatform(true);
            setVisible(true);
        } catch(Throwable ignoreAndContinue) {
        }
    }

    public void initLeftPanel() {
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 5, 5);
        JPanel leftPanel = new JPanel(layout);

        float[] hsb = Color.RGBtoHSB(68, 71, 89, null);
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.getHSBColor(hsb[0], hsb[1], hsb[2])));

        leftPanel.add(new JButton("Добавяне"));
        leftPanel.add(new JButton("Редактиране"));
        leftPanel.add(new JButton("Изтриване"));
        leftPanel.add(new JButton("Търсене"));
        leftPanel.add(new JButton("Обновяване"));

        add(leftPanel, BorderLayout.NORTH);
    }

    public void initRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());

        JTabbedPane jTabbedPane = getjTabbedPane();
        rightPanel.add(jTabbedPane, BorderLayout.WEST);

        add(rightPanel, BorderLayout.CENTER);
    }

    private static JTabbedPane getjTabbedPane() {
        JTabbedPane categories = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

        categories.addTab("Актьори", null);
        categories.addTab("Актьори", null);
        categories.addTab("Актьори", null);
        categories.addTab("Актьори", null);
        categories.addTab("Актьори", null);

        categories.addChangeListener(e -> {
            JTabbedPane tabs = (JTabbedPane) e.getSource();
            int index = tabs.getSelectedIndex();
            System.out.println("Tab: " + tabs.getTitleAt(index));
        });

        return categories;
    }
}
