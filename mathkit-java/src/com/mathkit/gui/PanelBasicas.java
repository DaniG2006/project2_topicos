package com.mathkit.gui;

import com.mathkit.Calculadora;
import com.mathkit.errores.DominioException;

final class PanelBasicas extends PanelModulo {
    PanelBasicas(Calculadora c, Runnable alCambiar) {
        super("Básicas", "Raíces, potencias, factoriales, MCD y MCM");
        agregar(new Formulario("Raíz n-ésima", new String[]{"Valor", "Índice"}, new String[]{"144", "2"},
                v -> c.raiz(Formato.num(v[0]), Formato.entero(v[1])), alCambiar));
        agregar(new Formulario("Potencia", new String[]{"Base", "Exponente"}, new String[]{"2", "10"},
                v -> c.potencia(Formato.num(v[0]), Formato.num(v[1])), alCambiar));
        agregar(new Formulario("Factorial", new String[]{"n"}, new String[]{"25"}, v -> {
            int n = Formato.entero(v[0]);
            if (n > 5000) throw new DominioException("En esta ventana n debe ser 5000 o menos.");
            return c.factorial(n);
        }, alCambiar));
        agregar(new Formulario("MCD y MCM", new String[]{"a", "b"}, new String[]{"48", "18"}, v -> {
            long a = Formato.entero(v[0]), b = Formato.entero(v[1]);
            return "MCD " + c.mcd(a, b) + "  ·  MCM " + c.mcm(a, b);
        }, alCambiar));
    }
}
