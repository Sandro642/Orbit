package fr.sandro642.orbit.app.ui;

import fr.sandro642.orbit.app.core.Candle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;

public class ChartPanel extends JPanel {

    // Couleurs TradingView
    private final Color BG = new Color(13, 16, 23);
    private final Color UP = new Color(38, 166, 154);
    private final Color DOWN = new Color(239, 83, 80);
    private final Color GRID = new Color(28, 31, 42);
    private final Color MA_COLOR = new Color(33, 150, 243);
    private final Color RSI_COLOR = new Color(156, 39, 176);

    private List<Candle> candles = List.of();

    public void setData(List<Candle> candles) {
        this.candles = candles;
        repaint();
    }

    private Point mousePos = new Point(-1, -1);

    public ChartPanel() {
        setOpaque(true);
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) { mousePos = e.getPoint(); repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG); g2.fillRect(0, 0, getWidth(), getHeight());

        if (candles.isEmpty()) return;

        int w = getWidth() - 70;
        int totalH = getHeight();
        int mainH = (int) (totalH * 0.7); // 70% pour le prix
        int rsiH = totalH - mainH - 40;   // 30% pour le RSI
        int rsiYStart = mainH + 20;

        double maxP = candles.stream().mapToDouble(c -> c.h).max().orElse(1);
        double minP = candles.stream().mapToDouble(c -> c.l).min().orElse(0);
        double rangeP = maxP - minP;
        double step = (double) w / candles.size();

        // GRILLE & AXES
        g2.setColor(GRID);
        g2.drawLine(0, rsiYStart, w, rsiYStart);
        for(int i=0; i<=5; i++) {
            int y = mainH - (i * mainH / 5);
            g2.drawLine(0, y, w, y);
            g2.drawString(String.format("%.2f", minP + (rangeP * i / 5.0)), w + 5, y + 5);
        }

        Path2D.Double smaPath = new Path2D.Double();
        Path2D.Double rsiPath = new Path2D.Double();
        boolean firstSMA = true, firstRSI = true;

        for (int i = 0; i < candles.size(); i++) {
            Candle c = candles.get(i);
            int x = (int) (i * step);
            int midX = x + (int)step/2;

            // DESSIN BOUGIES
            int yH = (int) (mainH - ((c.h - minP) / rangeP) * mainH);
            int yL = (int) (mainH - ((c.l - minP) / rangeP) * mainH);
            int yO = (int) (mainH - ((c.o - minP) / rangeP) * mainH);
            int yC = (int) (mainH - ((c.c - minP) / rangeP) * mainH);

            g2.setColor(c.c >= c.o ? UP : DOWN);
            g2.drawLine(midX, yH, midX, yL);
            g2.fillRect(x + 1, Math.min(yO, yC), Math.max(1, (int)step - 2), Math.max(2, Math.abs(yO - yC)));

            // PATHS POUR INDICATEURS
            if (c.sma > 0) {
                int ySMA = (int) (mainH - ((c.sma - minP) / rangeP) * mainH);
                if (firstSMA) { smaPath.moveTo(midX, ySMA); firstSMA = false; } else smaPath.lineTo(midX, ySMA);
            }
            if (c.rsi > 0) {
                int yRSI = rsiYStart + rsiH - (int)(c.rsi * rsiH / 100.0);
                if (firstRSI) { rsiPath.moveTo(midX, yRSI); firstRSI = false; } else rsiPath.lineTo(midX, yRSI);
            }

            // CROSSHAIR & TOOLTIP
            if (mousePos.x >= x && mousePos.x < x + step) {
                g2.setColor(new Color(255,255,255, 30));
                g2.drawLine(midX, 0, midX, totalH);
                g2.setColor(Color.WHITE);
                g2.drawString(String.format("P: %.2f | SMA: %.2f | RSI: %.1f", c.c, c.sma, c.rsi), 20, 30);
            }
        }

        // RENDU FINAL INDICATEURS
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(MA_COLOR); g2.draw(smaPath);
        g2.setColor(RSI_COLOR); g2.draw(rsiPath);

        // RSI Overbought/Oversold lines
        g2.setColor(new Color(156, 39, 176, 50));
        g2.drawLine(0, rsiYStart + (int)(0.3 * rsiH), w, rsiYStart + (int)(0.3 * rsiH)); // 70 line
        g2.drawLine(0, rsiYStart + (int)(0.7 * rsiH), w, rsiYStart + (int)(0.7 * rsiH)); // 30 line
    }
}
