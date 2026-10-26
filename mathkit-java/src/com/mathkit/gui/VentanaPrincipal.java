package com.mathkit.gui;

import com.mathkit.Calculadora;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/** Ventana principal de MathKit. */
public final class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() { this(null, null); }

    public VentanaPrincipal(String usuario, Runnable alCerrarSesion) {
        super("MathKit — Calculadora matemática");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(construirContenido(usuario, alCerrarSesion));
        setSize(1120, 700);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
    }

    /** Construye todo el contenido (separado para poder probarlo sin abrir ventana). */
    public static JComponent construirContenido() { return construirContenido(null, null); }

    public static JComponent construirContenido(String usuario, Runnable alCerrarSesion) {
        Calculadora calc = new Calculadora();

        JLabel contador = Tema.etiqueta("0 operaciones en el historial", Tema.sans(Font.PLAIN, 12), new Color(0x3C4650));
        PanelHistorial historial = new PanelHistorial(calc);
        Runnable refrescar = () -> {
            historial.refrescar();
            int n = calc.getHistorial().size();
            contador.setText(n + (n == 1 ? " operación" : " operaciones") + " en el historial");
        };
        historial.alBorrar(refrescar);

        String[] nombres = {"Básicas", "Probabilidad", "Estadística", "Numéricos"};
        String[] simbolos = {"√x", "P", "σ", "∫"};
        JComponent[] paneles = {
                new PanelBasicas(calc, refrescar), new PanelProbabilidad(calc, refrescar),
                new PanelEstadistica(calc, refrescar), new PanelNumericos(calc, refrescar)};
        calc.limpiarHistorial();
        refrescar.run();

        CardLayout cartas = new CardLayout();
        JPanel centro = new JPanel(cartas);
        centro.setBackground(Color.WHITE);
        for (int i = 0; i < paneles.length; i++) {
            JScrollPane sp = new JScrollPane(paneles[i]);
            sp.setBorder(null);
            sp.getViewport().setBackground(Color.WHITE);
            Tema.estilizarScroll(sp);
            centro.add(sp, nombres[i]);
        }

        // Barra lateral
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(Tema.SIDEBAR);
        lateral.setBorder(new EmptyBorder(18, 12, 18, 12));
        lateral.setPreferredSize(new Dimension(216, 0));
        JLabel mod = Tema.etiqueta("Módulos", Tema.sans(Font.PLAIN, 12), Tema.TEXTO_SIDE_SEC);
        mod.setBorder(new EmptyBorder(0, 12, 8, 0));
        mod.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(mod);
        ItemNav[] items = new ItemNav[nombres.length];
        for (int i = 0; i < items.length; i++) {
            final int k = i;
            items[i] = new ItemNav(simbolos[i], nombres[i]);
            items[i].addActionListener(e -> {
                cartas.show(centro, nombres[k]);
                for (ItemNav n : items) n.activo(n == items[k]);
            });
            lateral.add(items[i]);
            lateral.add(Box.createVerticalStrut(4));
        }
        lateral.add(Box.createVerticalGlue());
        if (usuario != null) {
            JLabel sesion = Tema.etiqueta("Sesión: " + usuario, Tema.sans(Font.PLAIN, 12), Tema.TEXTO_SIDE);
            sesion.setBorder(new EmptyBorder(0, 12, 2, 0));
            sesion.setAlignmentX(Component.LEFT_ALIGNMENT);
            lateral.add(sesion);
            if (alCerrarSesion != null) {
                Enlace salir = new Enlace("Cerrar sesión", Tema.TEXTO_SIDE_SEC, Color.WHITE);
                salir.setBorder(new EmptyBorder(2, 12, 2, 0));
                salir.setAlignmentX(Component.LEFT_ALIGNMENT);
                salir.addActionListener(e -> alCerrarSesion.run());
                lateral.add(salir);
            }
            lateral.add(Box.createVerticalStrut(12));
        }
        JLabel version = Tema.etiqueta("Versión 1.0.0 · Java " + Runtime.version().feature(),
                Tema.sans(Font.PLAIN, 12), Tema.TEXTO_SIDE_SEC);
        version.setBorder(new EmptyBorder(0, 12, 0, 0));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(version);

        // Barra de estado
        JPanel estado = new JPanel(new BorderLayout());
        estado.setBackground(Tema.BARRA);
        estado.setBorder(new javax.swing.border.CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(0xD0D8DF)), new EmptyBorder(0, 16, 0, 16)));
        estado.setPreferredSize(new Dimension(0, 28));
        estado.add(Tema.etiqueta("Listo", Tema.sans(Font.PLAIN, 12), new Color(0x3C4650)), BorderLayout.WEST);
        estado.add(contador, BorderLayout.EAST);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(lateral, BorderLayout.WEST);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(historial, BorderLayout.EAST);
        raiz.add(estado, BorderLayout.SOUTH);
        items[2].doClick();
        return raiz;
    }

    /** Elemento del menú lateral: insignia con símbolo + nombre. */
    private static final class ItemNav extends JButton {
        private final String simbolo;
        private boolean activo;

        ItemNav(String simbolo, String texto) {
            super(texto);
            this.simbolo = simbolo;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setFont(Tema.sans(Font.PLAIN, 14));
            setForeground(Tema.TEXTO_SIDE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setPreferredSize(new Dimension(192, 48));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        }

        void activo(boolean a) {
            activo = a;
            setFont(Tema.sans(a ? Font.BOLD : Font.PLAIN, 14));
            setForeground(a ? Color.WHITE : Tema.TEXTO_SIDE);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (activo || getModel().isRollover()) {
                g2.setColor(activo ? Tema.SIDEBAR_ITEM : new Color(0x1D2A37));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
            g2.setColor(activo ? Tema.ACENTO : Tema.SIDEBAR_ITEM);
            g2.fillRoundRect(10, 9, 30, 30, 10, 10);
            g2.setFont(Tema.mono(Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(0xE6EBF0));
            g2.drawString(simbolo, 10 + (30 - fm.stringWidth(simbolo)) / 2, 9 + (30 - fm.getHeight()) / 2 + fm.getAscent());
            g2.setFont(getFont());
            fm = g2.getFontMetrics();
            g2.setColor(getForeground());
            g2.drawString(getText(), 52, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
