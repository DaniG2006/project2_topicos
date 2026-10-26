package com.mathkit.gui;

import com.mathkit.Expresion;
import com.mathkit.errores.DominioException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Conversión entre texto de la interfaz y valores numéricos. */
final class Formato {
    private Formato() {}

    static double num(String texto) { return Expresion.evaluar(texto); }

    static int entero(String texto) {
        double d = num(texto);
        if (d != com.mathkit.CalculosInternos.rint(d) || com.mathkit.CalculosInternos.abs(d) > Integer.MAX_VALUE)
            throw new DominioException("Se requiere un número entero: " + texto.trim() + ".");
        return (int) d;
    }

    static double[] lista(String texto) {
        if (texto == null || texto.isBlank()) throw new DominioException("Ingresa al menos un dato.");
        String[] partes = texto.trim().split("\\s*[,;]\\s*");
        double[] r = new double[partes.length];
        for (int i = 0; i < partes.length; i++) r[i] = num(partes[i]);
        return r;
    }

    static String de(Object o) {
        if (o instanceof Double d) return numero(d);
        if (o instanceof List<?> l) return l.stream().map(Formato::de).collect(Collectors.joining(", "));
        return String.valueOf(o);
    }

    static String numero(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return String.valueOf(v);
        if (v == 0) return "0";
        double a = com.mathkit.CalculosInternos.abs(v);
        if (a >= 1e12 || a < 1e-4) return String.format(Locale.ROOT, "%.6e", v);
        return new BigDecimal(v).round(new MathContext(8)).stripTrailingZeros().toPlainString();
    }
}
