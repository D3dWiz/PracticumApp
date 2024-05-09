package me.yan.gui;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        initGUI();
        final JPanel gui = new JPanel(new BorderLayout(5,5));

        JPanel categories = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 3,3));

        categories.add( new JLabel("Categories: "), FlowLayout.LEFT);
        categories.add( new JButton("Foo"), FlowLayout.CENTER);
        gui.add(categories, BorderLayout.NORTH);


    }
    public void initGUI() {
        FlatDraculaIJTheme.setup();

        setTitle("PracticumApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1024, 640));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        try {
            // 1.6+
            setLocationByPlatform(true);
            setMinimumSize(getSize());
        } catch(Throwable ignoreAndContinue) {
        }
    }
}
