package com.mathkit.gui;

import com.mathkit.Calculadora;

final class PanelProbabilidad extends PanelModulo {
    PanelProbabilidad(Calculadora c, Runnable alCambiar) {
        super("Probabilidad", "Combinatoria y distribuciones");
        agregar(new Formulario("Combinaciones C(n, r)", new String[]{"n", "r"}, new String[]{"52", "5"},
                v -> c.combinaciones(Formato.entero(v[0]), Formato.entero(v[1])), alCambiar));
        agregar(new Formulario("Binomial P(X = k)", new String[]{"k", "n", "p"}, new String[]{"3", "10", "0.5"},
                v -> c.binomial(Formato.entero(v[0]), Formato.entero(v[1]), Formato.num(v[2])), alCambiar));
        agregar(new Formulario("Poisson P(X = k)", new String[]{"k", "λ"}, new String[]{"2", "3"},
                v -> c.poisson(Formato.entero(v[0]), Formato.num(v[1])), alCambiar));
        agregar(new Formulario("Normal acumulada P(Z ≤ x)", new String[]{"x", "μ", "σ"}, new String[]{"1.96", "0", "1"},
                v -> c.normalAcumulada(Formato.num(v[0]), Formato.num(v[1]), Formato.num(v[2])), alCambiar));
    }
}
