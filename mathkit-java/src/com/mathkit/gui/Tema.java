package com.mathkit.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Colores, tipografías y fábricas de controles compartidas. */
final class Tema {
    private Tema() {}

    static final Color SIDEBAR = new Color(0x16202A);
    static final Color SIDEBAR_ITEM = new Color(0x243342);
    static final Color TEXTO_SIDE = new Color(0xB8C4CF);
    static final Color TEXTO_SIDE_SEC = new Color(0x8FA0B0);
    static final Color ACENTO = new Color(0x0B7A5E);
    static final Color ACENTO_SUAVE = new Color(0xE3F3EE);
    static final Color TARJETA = new Color(0xF3F6F8);
    static final Color BORDE = new Color(0xD9E0E6);
    static final Color BORDE_FUERTE = new Color(0xAEB9C3);
    static final Color TEXTO = new Color(0x16202A);
    static final Color TEXTO_SEC = new Color(0x5B6670);
    static final Color ERROR = new Color(0xB3261E);
    static final Color PANEL_HIST = new Color(0xFAFBFC);
    static final Color BARRA = new Color(0xE9EEF2);

    static Font sans(int estilo, float tam) { return new Font(Font.SANS_SERIF, estilo, 12).deriveFont(tam); }
    static Font mono(int estilo, float tam) { return new Font(Font.MONOSPACED, estilo, 12).deriveFont(tam); }

    static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente);
        l.setForeground(color);
        return l;
    }

    static JTextField campo(String texto) {
        JTextField f = new JTextField(texto);
        estilizar(f);
        return f;
    }

    static JPasswordField campoClave() {
        JPasswordField f = new JPasswordField();
        f.setEchoChar('\u2022');
        estilizar(f);
        return f;
    }

    private static void estilizar(JTextField f) {
        f.setFont(mono(Font.PLAIN, 13));
        f.setForeground(TEXTO);
        f.setBackground(Color.WHITE);
        f.setCaretColor(ACENTO);
        borde(f, BORDE_FUERTE);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { borde(f, ACENTO); }
            @Override public void focusLost(FocusEvent e) { borde(f, BORDE_FUERTE); }
        });
    }

    static JComboBox<String> combo(String[] opciones) {
        JComboBox<String> c = new JComboBox<>(opciones);
        c.setFont(sans(Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setForeground(TEXTO);
        c.setUI(new BasicComboBoxUI() {
            @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean foco) {
                g.setColor(Color.WHITE);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
            @Override protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(TEXTO_SEC);
                        int cx = getWidth() / 2, cy = getHeight() / 2;
                        g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                        g2.dispose();
                    }
                };
                b.setBorder(new EmptyBorder(0, 0, 0, 0));
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setPreferredSize(new Dimension(28, 0));
                return b;
            }
        });
        c.setBorder(new LineBorder(BORDE_FUERTE, 1, true));
        c.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foco) {
                super.getListCellRendererComponent(l, v, i, sel, foco);
                setBorder(new EmptyBorder(6, 12, 6, 8));
                setFont(sans(Font.PLAIN, 13));
                if (!sel) { setBackground(Color.WHITE); setForeground(TEXTO); }
                return this;
            }
        });
        return c;
    }

    /** Barra de desplazamiento delgada y sin botones. */
    static void estilizarScroll(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            private JButton cero() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
            @Override protected JButton createDecreaseButton(int o) { return cero(); }
            @Override protected JButton createIncreaseButton(int o) { return cero(); }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDE_FUERTE);
                g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8);
                g2.dispose();
            }
        });
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getVerticalScrollBar().setUnitIncrement(16);
    }

    private static void borde(JComponent c, Color color) {
        c.setBorder(new CompoundBorder(new LineBorder(color, 1, true), new EmptyBorder(8, 12, 8, 12)));
    }

    static JPanel conEtiqueta(String texto, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(etiqueta(texto, sans(Font.PLAIN, 12), TEXTO_SEC), BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    static Component espacio(int alto) {
        JComponent f = (JComponent) Box.createVerticalStrut(alto);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }
}
