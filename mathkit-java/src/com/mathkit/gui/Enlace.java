package com.mathkit.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Botón de solo texto (como un enlace) que cambia de color al pasar el cursor. */
final class Enlace extends JButton {
    Enlace(String texto, Color normal, Color sobre) {
        super(texto);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Tema.sans(Font.BOLD, 13));
        setForeground(normal);
        setBorder(new EmptyBorder(2, 2, 2, 2));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addChangeListener(e -> setForeground(getModel().isRollover() ? sobre : normal));
    }
}
