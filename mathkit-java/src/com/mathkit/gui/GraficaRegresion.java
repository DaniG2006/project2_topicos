package com.mathkit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/** Dispersión de puntos con su recta de regresión. */
final class GraficaRegresion extends JComponent {
    private double[] x = new double[0], y = new double[0];
    private double m, b;

    GraficaRegresion() {
        setPreferredSize(new Dimension(400, 124));
        setOpaque(false);
    }

    void datos(double[] x, double[] y, double m, double b) {
        this.x = x; this.y = y; this.m = m; this.b = b;
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        if (x.length == 0) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final int izq = 40, der = 12, arr = 10, aba = 24;
        final int w = getWidth() - izq - der, h = getHeight() - arr - aba;

        double xmin = Double.MAX_VALUE, xmax = -Double.MAX_VALUE, ymin = Double.MAX_VALUE, ymax = -Double.MAX_VALUE;
        for (int i = 0; i < x.length; i++) {
            xmin = com.mathkit.CalculosInternos.min(xmin, x[i]); xmax = com.mathkit.CalculosInternos.max(xmax, x[i]);
            ymin = com.mathkit.CalculosInternos.min(ymin, y[i]); ymax = com.mathkit.CalculosInternos.max(ymax, y[i]);
        }
        ymin = com.mathkit.CalculosInternos.min(ymin, m * xmin + b); ymax = com.mathkit.CalculosInternos.max(ymax, m * xmax + b);
        if (xmax == xmin) { xmax += 1; xmin -= 1; }
        if (ymax == ymin) { ymax += 1; ymin -= 1; }
        double dx = (xmax - xmin) * 0.08, dy = (ymax - ymin) * 0.12;
        final double x0 = xmin - dx, x1 = xmax + dx, y0 = ymin - dy, y1 = ymax + dy;

        g2.setFont(Tema.sans(Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i <= 3; i++) {
            double v = y0 + (y1 - y0) * i / 3;
            int py = arr + h - (int) com.mathkit.CalculosInternos.round(h * (v - y0) / (y1 - y0));
            g2.setColor(Tema.BORDE);
            g2.drawLine(izq, py, izq + w, py);
            g2.setColor(Tema.TEXTO_SEC);
            String s = String.format(Locale.ROOT, "%.1f", v);
            g2.drawString(s, izq - 6 - fm.stringWidth(s), py + 4);
        }
        for (int i = 0; i <= 4; i++) {
            double v = x0 + (x1 - x0) * i / 4;
            int px = izq + (int) com.mathkit.CalculosInternos.round(w * (v - x0) / (x1 - x0));
            String s = String.format(Locale.ROOT, "%.1f", v);
            g2.setColor(Tema.TEXTO_SEC);
            g2.drawString(s, px - fm.stringWidth(s) / 2, arr + h + 17);
        }

        g2.setColor(Tema.ACENTO);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(izq, arr + h - (int) com.mathkit.CalculosInternos.round(h * ((m * x0 + b) - y0) / (y1 - y0)),
                izq + w, arr + h - (int) com.mathkit.CalculosInternos.round(h * ((m * x1 + b) - y0) / (y1 - y0)));

        g2.setColor(Tema.TEXTO);
        for (int i = 0; i < x.length; i++) {
            int px = izq + (int) com.mathkit.CalculosInternos.round(w * (x[i] - x0) / (x1 - x0));
            int py = arr + h - (int) com.mathkit.CalculosInternos.round(h * (y[i] - y0) / (y1 - y0));
            g2.fillOval(px - 4, py - 4, 9, 9);
        }
        g2.dispose();
    }
}
