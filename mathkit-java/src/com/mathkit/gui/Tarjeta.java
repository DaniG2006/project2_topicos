package com.mathkit.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Panel con fondo gris claro, borde fino y esquinas redondeadas. */
class Tarjeta extends JPanel {
    Tarjeta(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(14, 16, 14, 16));
        setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Tema.TARJETA);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.setColor(Tema.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
    }

    @Override public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
