package com.mathkit.gui;

import com.mathkit.Calculadora;
import com.mathkit.Estadistica;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Map;

final class PanelEstadistica extends PanelModulo {
    private static final String[] NOMBRES = {"Datos (n)", "Media", "Mediana", "Moda", "Desviación estándar", "Rango"};
    private static final String[] CLAVES = {"n", "media", "mediana", "moda", "desviacion", "rango"};

    private final Calculadora calc;
    private final Runnable alCambiar;
    private final JTextField datos = Tema.campo("4, 8, 8, 15, 16, 23, 42");
    private final JTextField campoX = Tema.campo("1, 2, 3, 4, 5");
    private final JTextField campoY = Tema.campo("2.1, 4.0, 6.2, 7.9, 10.1");
    private final JLabel[] valores = new JLabel[6];
    private final JLabel mensaje = Tema.etiqueta(" ", Tema.sans(Font.PLAIN, 13), Tema.ERROR);
    private final JLabel ecuacion = Tema.etiqueta(" ", Tema.mono(Font.BOLD, 13), Tema.ACENTO);
    private final GraficaRegresion grafica = new GraficaRegresion();

    PanelEstadistica(Calculadora calc, Runnable alCambiar) {
        super("Estadística", "Resumen descriptivo y regresión lineal");
        this.calc = calc;
        this.alCambiar = alCambiar;

        // Fila de datos
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.add(Tema.conEtiqueta("Datos, separados por coma", datos), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        Boton calcular = new Boton("Calcular", Boton.Tipo.PRIMARIO);
        Boton limpiar = new Boton("Limpiar", Boton.Tipo.SECUNDARIO);
        calcular.addActionListener(e -> resumir());
        datos.addActionListener(e -> resumir());
        limpiar.addActionListener(e -> {
            datos.setText("");
            for (JLabel v : valores) v.setText("–");
            mensaje.setText(" ");
        });
        botones.add(calcular);
        botones.add(limpiar);
        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(botones, BorderLayout.SOUTH);
        fila.add(sur, BorderLayout.EAST);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, fila.getPreferredSize().height));
        agregar(fila);
        mensaje.setAlignmentX(LEFT_ALIGNMENT);
        add(mensaje);
        add(Tema.espacio(2));

        // Tarjetas de resultados
        JPanel rejilla = new JPanel(new GridLayout(2, 3, 12, 12));
        rejilla.setOpaque(false);
        for (int i = 0; i < 6; i++) {
            Tarjeta t = new Tarjeta(new BorderLayout(0, 6));
            valores[i] = Tema.etiqueta("–", Tema.mono(Font.BOLD, 24), Tema.TEXTO);
            t.add(Tema.etiqueta(NOMBRES[i], Tema.sans(Font.PLAIN, 12), Tema.TEXTO_SEC), BorderLayout.NORTH);
            t.add(valores[i], BorderLayout.CENTER);
            rejilla.add(t);
        }
        rejilla.setMaximumSize(new Dimension(Integer.MAX_VALUE, rejilla.getPreferredSize().height));
        agregar(rejilla);

        // Regresión
        Tarjeta reg = new Tarjeta(new BorderLayout(0, 8));
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        cab.add(Tema.etiqueta("Regresión lineal", Tema.sans(Font.BOLD, 14), Tema.TEXTO), BorderLayout.WEST);
        cab.add(ecuacion, BorderLayout.EAST);
        reg.add(cab, BorderLayout.NORTH);
        reg.add(grafica, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout(10, 0));
        pie.setOpaque(false);
        JPanel xy = new JPanel(new GridBagLayout());
        xy.setOpaque(false);
        GridBagConstraints gx = new GridBagConstraints();
        gx.fill = GridBagConstraints.BOTH;
        campoX.setPreferredSize(new Dimension(10, campoX.getPreferredSize().height));
        campoY.setPreferredSize(new Dimension(10, campoY.getPreferredSize().height));
        gx.weightx = 1.0;
        xy.add(Tema.conEtiqueta("X", campoX), gx);
        gx.gridx = 1; gx.weightx = 1.6; gx.insets = new Insets(0, 10, 0, 0);
        xy.add(Tema.conEtiqueta("Y", campoY), gx);
        Boton ajustar = new Boton("Ajustar", Boton.Tipo.SECUNDARIO);
        ajustar.addActionListener(e -> ajustar());
        JPanel sur2 = new JPanel(new BorderLayout());
        sur2.setOpaque(false);
        sur2.add(ajustar, BorderLayout.SOUTH);
        pie.add(xy, BorderLayout.CENTER);
        pie.add(sur2, BorderLayout.EAST);
        reg.add(pie, BorderLayout.SOUTH);
        agregar(reg);

        resumir();
        ajustar();
    }

    private void resumir() {
        try {
            Map<String, Object> r = calc.resumen(Formato.lista(datos.getText()));
            for (int i = 0; i < 6; i++) valores[i].setText(Formato.de(r.get(CLAVES[i])));
            mensaje.setText(" ");
            alCambiar.run();
        } catch (Exception e) {
            error(e);
        }
    }

    private void ajustar() {
        try {
            double[] x = Formato.lista(campoX.getText()), y = Formato.lista(campoY.getText());
            Estadistica.Regresion r = calc.regresion(x, y);
            ecuacion.setText(String.format(Locale.ROOT, "y = %.3fx %s %.3f  ·  R² = %.4f",
                    r.pendiente(), r.intercepto() < 0 ? "-" : "+", com.mathkit.CalculosInternos.abs(r.intercepto()), r.r2()));
            grafica.datos(x, y, r.pendiente(), r.intercepto());
            mensaje.setText(" ");
            alCambiar.run();
        } catch (Exception e) {
            error(e);
        }
    }

    private void error(Exception e) {
        mensaje.setText(e instanceof NumberFormatException ? "Revisa los valores: deben ser números."
                : e.getMessage() == null ? "Operación no válida." : e.getMessage());
    }
}
