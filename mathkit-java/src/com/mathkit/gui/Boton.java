package com.mathkit.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Botón redondeado, primario (verde) o secundario (borde). */
class Boton extends JButton {
    enum Tipo { PRIMARIO, SECUNDARIO }

    private final Tipo tipo;
    private final int alto;

    Boton(String texto, Tipo tipo) { this(texto, tipo, false); }

    Boton(String texto, Tipo tipo, boolean compacto) {
        super(texto);
        this.tipo = tipo;
        this.alto = compacto ? 28 : 38;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Tema.sans(compacto ? Font.PLAIN : Font.BOLD, compacto ? 12 : 13));
        setForeground(tipo == Tipo.PRIMARIO ? Color.WHITE : Tema.TEXTO);
        setBorder(new EmptyBorder(0, compacto ? 12 : 22, 0, compacto ? 12 : 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, alto);
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean sobre = getModel().isRollover(), presionado = getModel().isPressed();
        Color fondo = tipo == Tipo.PRIMARIO
                ? (presionado ? new Color(0x075F49) : sobre ? new Color(0x096B53) : Tema.ACENTO)
                : (presionado ? Tema.BORDE : sobre ? Tema.TARJETA : Color.WHITE);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        if (tipo == Tipo.SECUNDARIO) {
            g2.setColor(Tema.BORDE_FUERTE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
