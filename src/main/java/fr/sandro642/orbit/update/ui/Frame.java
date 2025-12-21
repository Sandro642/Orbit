package fr.sandro642.orbit.update.ui;

import fr.sandro642.orbit.update.Version;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class Frame extends JFrame {

    private static final Frame INSTANCE = new Frame();

    private JLabel statusLabel;
    private JProgressBar progressBar;
    private LoadingCircle loadingCircle;

    private Frame() {
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        Color darkBg = new Color(25, 25, 25);
        Color barColor = new Color(35, 35, 35);
        Color accentColor = new Color(0, 150, 255);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(darkBg);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        setContentPane(mainPanel);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(barColor);
        titleBar.setPreferredSize(new Dimension(400, 35));

        JLabel titleLabel = new JLabel("Orbit Updater", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(barColor);
        bottomBar.setPreferredSize(new Dimension(400, 45));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel versionLabel = createClickableLabel("V" + Version.VERSION, "https://votre-site.com");
        JLabel helpLabel = createClickableLabel("Need help?", "https://votre-aide.com");

        bottomBar.add(helpLabel, BorderLayout.WEST);
        bottomBar.add(versionLabel, BorderLayout.EAST);

        loadingCircle = new LoadingCircle();
        statusLabel = new JLabel("Initialisation...", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        progressBar = new JProgressBar(0, 5);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(300, 8));
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setForeground(accentColor);
        progressBar.setBorderPainted(false);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        centerPanel.add(loadingCircle, gbc);
        gbc.insets = new Insets(0, 0, 15, 0);
        centerPanel.add(statusLabel, gbc);
        centerPanel.add(progressBar, gbc);

        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);
    }

    private JLabel createClickableLabel(String text, String url) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(150, 150, 150));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(new Color(150, 150, 150));
            }
        });
        return label;
    }

    public void textComponent(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }

    public void ProgressValue(int value) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(value));
    }

    public void init() {
        setVisible(true);
    }

    public void kill() {
        SwingUtilities.invokeLater(this::dispose);
    }

    public static Frame getFrameSingleton() {
        return INSTANCE;
    }


    class LoadingCircle extends JPanel {
        private int angle = 0;

        public LoadingCircle() {
            setPreferredSize(new Dimension(80, 80));
            setOpaque(false);
            Timer timer = new Timer(16, e -> {
                angle = (angle + 5) % 360;
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(4));

            g2.setColor(new Color(60, 60, 60));
            g2.drawOval(10, 10, 60, 60);

            g2.setColor(new Color(0, 150, 255));
            g2.drawArc(10, 10, 60, 60, -angle, 100);
        }
    }
}