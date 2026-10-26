package com.mathkit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Tarjeta con título, campos, botón "Calcular" y resultado.
 * Un valor con "|" (ej. "Newton|Secante") se muestra como lista desplegable.
 */
final class Formulario extends Tarjeta {
    interface Accion { Object ejecutar(String[] valores) throws Exception; }

    private final JLabel resultado = Tema.etiqueta(" ", Tema.mono(Font.BOLD, 14), Tema.ACENTO);
    private final List<Supplier<String>> lectores = new ArrayList<>();
    private final Accion accion;
    private final Runnable alCambiar;

    Formulario(String titulo, String[] etiquetas, String[] valores, Accion accion, Runnable alCambiar) {
        super(new BorderLayout(0, 12));
        this.accion = accion;
        this.alCambiar = alCambiar;

        JPanel cabecera = new JPanel(new BorderLayout(16, 0));
        cabecera.setOpaque(false);
        cabecera.add(Tema.etiqueta(titulo, Tema.sans(Font.BOLD, 14), Tema.TEXTO), BorderLayout.WEST);
        cabecera.add(resultado, BorderLayout.CENTER);
        resultado.setHorizontalAlignment(SwingConstants.RIGHT);
        add(cabecera, BorderLayout.NORTH);

        JPanel fila = new JPanel(new GridBagLayout());
        fila.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        for (int i = 0; i < etiquetas.length; i++) {
            JComponent comp;
            boolean esCombo = valores[i].contains("|");
            if (valores[i].contains("|")) {
                JComboBox<String> cb = Tema.combo(valores[i].split("\\|"));
                lectores.add(() -> (String) cb.getSelectedItem());
                comp = cb;
            } else {
                JTextField tf = Tema.campo(valores[i]);
                tf.addActionListener(e -> calcular());
                lectores.add(tf::getText);
                comp = tf;
            }
            gc.gridx = i;
            gc.weightx = esCombo ? 0 : etiquetas[i].startsWith("f(x)") ? 2.0 : 1.0;
            gc.insets = new Insets(0, i == 0 ? 0 : 10, 0, 0);
            JPanel celda = Tema.conEtiqueta(etiquetas[i], comp);
            celda.setPreferredSize(new Dimension(esCombo ? 132 : 10, celda.getPreferredSize().height));
            fila.add(celda, gc);
        }
        Boton b = new Boton("Calcular", Boton.Tipo.PRIMARIO);
        b.addActionListener(e -> calcular());
        gc.gridx = etiquetas.length;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.SOUTH;
        gc.insets = new Insets(0, 14, 0, 0);
        fila.add(b, gc);
        add(fila, BorderLayout.CENTER);
    }

    private void calcular() {
        String[] v = new String[lectores.size()];
        for (int i = 0; i < v.length; i++) v[i] = lectores.get(i).get();
        try {
            String t = Formato.de(accion.ejecutar(v));
            resultado.setForeground(Tema.ACENTO);
            resultado.setText("= " + t);
            resultado.setToolTipText(t);
            alCambiar.run();
        } catch (Exception e) {
            String m = e.getMessage() == null ? "Operación no válida." : e.getMessage();
            if (e instanceof NumberFormatException) m = "Revisa los valores: deben ser números.";
            resultado.setForeground(Tema.ERROR);
            resultado.setText(m);
            resultado.setToolTipText(m);
        }
    }
}
