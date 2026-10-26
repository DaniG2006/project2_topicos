package com.mathkit.gui;

import com.mathkit.Calculadora;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

/** Panel derecho con las operaciones realizadas (la más reciente arriba). */
final class PanelHistorial extends JPanel {
    private final Calculadora calc;
    private final DefaultListModel<String> modelo = new DefaultListModel<>();
    private final CardLayout cartas = new CardLayout();
    private final JPanel centro = new JPanel(cartas);
    private Runnable alBorrar = () -> {};

    PanelHistorial(Calculadora calc) {
        super(new BorderLayout());
        this.calc = calc;
        setBackground(Tema.PANEL_HIST);
        setBorder(new MatteBorder(0, 1, 0, 0, Tema.BORDE));
        setPreferredSize(new Dimension(262, 0));

        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.setBorder(new EmptyBorder(16, 14, 12, 14));
        cab.add(Tema.etiqueta("Historial", Tema.sans(Font.BOLD, 14), Tema.TEXTO), BorderLayout.WEST);
        Boton borrar = new Boton("Borrar", Boton.Tipo.SECUNDARIO, true);
        borrar.addActionListener(e -> { calc.limpiarHistorial(); refrescar(); alBorrar.run(); });
        cab.add(borrar, BorderLayout.EAST);
        add(cab, BorderLayout.NORTH);

        JList<String> lista = new JList<>(modelo);
        lista.setFixedCellWidth(240);
        lista.setBackground(Tema.PANEL_HIST);
        lista.setFocusable(false);
        lista.setSelectionModel(new DefaultListSelectionModel() {
            @Override public void setSelectionInterval(int a, int b) { /* sin selección */ }
        });
        lista.setCellRenderer((l, valor, i, sel, foco) -> {
            JPanel p = new JPanel(new GridLayout(2, 1));
            p.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDE), new EmptyBorder(8, 12, 8, 12)));
            p.setBackground(i == 0 ? Tema.ACENTO_SUAVE : Tema.PANEL_HIST);
            int k = valor.lastIndexOf(" = ");
            String op = k < 0 ? valor : valor.substring(0, k);
            String res = k < 0 ? " " : "= " + valor.substring(k + 3);
            p.add(Tema.etiqueta(op, Tema.mono(Font.PLAIN, 12), Tema.TEXTO));
            p.add(Tema.etiqueta(res, Tema.mono(Font.PLAIN, 12), Tema.TEXTO_SEC));
            return p;
        });
        JScrollPane sp = new JScrollPane(lista, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(new MatteBorder(1, 0, 0, 0, Tema.BORDE));
        sp.getViewport().setBackground(Tema.PANEL_HIST);
        Tema.estilizarScroll(sp);

        JLabel vacio = Tema.etiqueta("Aún no hay operaciones.", Tema.sans(Font.PLAIN, 13), Tema.TEXTO_SEC);
        vacio.setHorizontalAlignment(SwingConstants.CENTER);
        vacio.setVerticalAlignment(SwingConstants.TOP);
        vacio.setBorder(new EmptyBorder(24, 12, 0, 12));
        centro.setOpaque(false);
        centro.add(sp, "lista");
        centro.add(vacio, "vacio");
        add(centro, BorderLayout.CENTER);
        refrescar();
    }

    void alBorrar(Runnable r) { this.alBorrar = r; }

    void refrescar() {
        modelo.clear();
        List<String> h = calc.getHistorial();
        for (int i = h.size() - 1; i >= 0; i--) modelo.addElement(h.get(i));
        cartas.show(centro, modelo.isEmpty() ? "vacio" : "lista");
    }
}
