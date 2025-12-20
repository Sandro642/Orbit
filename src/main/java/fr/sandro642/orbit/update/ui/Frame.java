package fr.sandro642.orbit.update.ui;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private static Frame INSTANCE = new Frame();

    public void init() {
        JFrame frame = new JFrame();

        ImageIcon logo = new ImageIcon("");

        frame.setSize(500, 720);
        frame.setTitle("Orbit Updater");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static Frame getFrameSingleton() {
        return INSTANCE;
    }
}
