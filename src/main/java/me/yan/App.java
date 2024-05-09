package me.yan;

import javax.swing.SwingUtilities;
import me.yan.gui.MainFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { new MainFrame(); }
        });
    }
}