package com.mathkit.gui;

import javax.swing.*;
import java.awt.*;

/** Cuadro redondeado con un símbolo matemático (logo y lista de módulos). */
final class Insignia extends JComponent {
    private final String simbolo;
    private final boolean activa;
    private final int tam;

    Insignia(String simbolo, boolean activa, int tam) {
        this.simbolo = simbolo;
        this.activa = activa;
        this.tam = tam;
        setOpaque(false);
        setPreferredSize(new Dimension(tam, tam));
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int y = (getHeight() - tam) / 2;
        g2.setColor(activa ? Tema.ACENTO : Tema.SIDEBAR_ITEM);
        g2.fillRoundRect(0, y, tam, tam, tam / 3, tam / 3);
        g2.setFont(Tema.mono(Font.BOLD, tam * 0.47f));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(activa ? Color.WHITE : new Color(0xE6EBF0));
        g2.drawString(simbolo, (tam - fm.stringWidth(simbolo)) / 2, y + (tam - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
