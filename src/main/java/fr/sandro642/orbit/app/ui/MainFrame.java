package fr.sandro642.orbit.app.ui;

import fr.sandro642.orbit.service.OrbitService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final OrbitService service = new OrbitService();
    private final ChartPanel chartPanel = new ChartPanel();

    public MainFrame() {
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);

        JTextField urlField = new JTextField("orbit://bitcoin/30", 20);
        urlField.addActionListener(e -> {
            String[] p = urlField.getText().replace("orbit://", "").split("/");
            service.fetchCandles(p[0], p[1]).subscribe(chartPanel::setData);
        });

        add(urlField, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }
}
