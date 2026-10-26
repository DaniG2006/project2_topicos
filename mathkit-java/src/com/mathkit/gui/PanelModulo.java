package com.mathkit.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Base de los paneles de módulo: título, subtítulo y contenido apilado. */
abstract class PanelModulo extends JPanel implements Scrollable {
    PanelModulo(String titulo, String subtitulo) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(22, 30, 18, 30));
        JLabel t = Tema.etiqueta(titulo, Tema.sans(Font.BOLD, 24), Tema.TEXTO);
        JLabel s = Tema.etiqueta(subtitulo, Tema.sans(Font.PLAIN, 14), Tema.TEXTO_SEC);
        t.setAlignmentX(LEFT_ALIGNMENT);
        s.setAlignmentX(LEFT_ALIGNMENT);
        add(t);
        add(Tema.espacio(2));
        add(s);
        add(Tema.espacio(16));
    }

    protected void agregar(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        add(c);
        add(Tema.espacio(14));
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
