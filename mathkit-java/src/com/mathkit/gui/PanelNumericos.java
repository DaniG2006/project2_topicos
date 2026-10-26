package com.mathkit.gui;

import com.mathkit.Calculadora;
import com.mathkit.Calculadora.MetodoIntegral;
import com.mathkit.Calculadora.MetodoRaiz;
import com.mathkit.Expresion;

final class PanelNumericos extends PanelModulo {
    PanelNumericos(Calculadora c, Runnable alCambiar) {
        super("Numéricos", "Raíces, derivadas e integrales de f(x). Usa x, pi, e, sin, cos, sqrt, ln…");
        agregar(new Formulario("Encontrar raíz de f(x)",
                new String[]{"f(x)", "a (o x₀)", "b", "Método"},
                new String[]{"x^2 - 2", "1", "2", "Bisección|Newton|Secante"},
                v -> {
                    MetodoRaiz m = switch (v[3]) {
                        case "Newton" -> MetodoRaiz.NEWTON;
                        case "Secante" -> MetodoRaiz.SECANTE;
                        default -> MetodoRaiz.BISECCION;
                    };
                    return c.encontrarRaiz(Expresion.compilar(v[0]), Formato.num(v[1]), Formato.num(v[2]), m);
                }, alCambiar));
        agregar(new Formulario("Integral definida",
                new String[]{"f(x)", "a", "b", "Método"},
                new String[]{"sin(x)", "0", "pi", "Simpson|Trapecio"},
                v -> c.integrar(Expresion.compilar(v[0]), Formato.num(v[1]), Formato.num(v[2]),
                        v[3].equals("Trapecio") ? MetodoIntegral.TRAPECIO : MetodoIntegral.SIMPSON), alCambiar));
        agregar(new Formulario("Derivada en un punto",
                new String[]{"f(x)", "x"},
                new String[]{"x^3", "2"},
                v -> c.derivada(Expresion.compilar(v[0]), Formato.num(v[1])), alCambiar));
    }
}
