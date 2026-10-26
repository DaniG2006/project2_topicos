package com.mathkit;

import com.mathkit.errores.DominioException;
import java.util.function.DoubleUnaryOperator;

/**
 * Compila texto como "x^2 - 2" o "sin(x)/x" en una función f(x).
 * Soporta + - * / ^, paréntesis, multiplicación implícita (2x),
 * constantes pi y e, y funciones sin cos tan asin acos atan exp ln log sqrt abs cbrt.
 */
public final class Expresion {
    private Expresion() {}

    public static DoubleUnaryOperator compilar(String texto) {
        if (texto == null || texto.isBlank()) throw new DominioException("La expresión está vacía.");
        Parser p = new Parser(texto.replaceAll("\\s+", ""));
        DoubleUnaryOperator f = p.expresion();
        if (p.pos < p.s.length()) throw p.error("símbolo inesperado '" + p.s.charAt(p.pos) + "'");
        return f;
    }

    /** Evalúa una expresión sin variable, por ejemplo "pi/2". */
    public static double evaluar(String texto) {
        return compilar(texto).applyAsDouble(0);
    }

    private static final class Parser {
        final String s;
        int pos = 0;

        Parser(String s) { this.s = s; }

        DominioException error(String m) { return new DominioException("Expresión inválida: " + m + "."); }

        boolean toma(char c) {
            if (pos < s.length() && s.charAt(pos) == c) { pos++; return true; }
            return false;
        }

        DoubleUnaryOperator expresion() {
            DoubleUnaryOperator izq = termino();
            while (true) {
                final DoubleUnaryOperator a = izq;
                if (toma('+')) { final DoubleUnaryOperator b = termino(); izq = x -> a.applyAsDouble(x) + b.applyAsDouble(x); }
                else if (toma('-')) { final DoubleUnaryOperator b = termino(); izq = x -> a.applyAsDouble(x) - b.applyAsDouble(x); }
                else return izq;
            }
        }

        DoubleUnaryOperator termino() {
            DoubleUnaryOperator izq = unario();
            while (true) {
                final DoubleUnaryOperator a = izq;
                if (toma('*')) { final DoubleUnaryOperator b = unario(); izq = x -> a.applyAsDouble(x) * b.applyAsDouble(x); }
                else if (toma('/')) { final DoubleUnaryOperator b = unario(); izq = x -> a.applyAsDouble(x) / b.applyAsDouble(x); }
                else if (pos < s.length() && (Character.isLetter(s.charAt(pos)) || s.charAt(pos) == '(')) {
                    final DoubleUnaryOperator b = unario();
                    izq = x -> a.applyAsDouble(x) * b.applyAsDouble(x);
                } else return izq;
            }
        }

        DoubleUnaryOperator unario() {
            if (toma('-')) { final DoubleUnaryOperator a = unario(); return x -> -a.applyAsDouble(x); }
            if (toma('+')) return unario();
            return potencia();
        }

        DoubleUnaryOperator potencia() {
            final DoubleUnaryOperator base = atomo();
            if (toma('^')) {
                final DoubleUnaryOperator exp = unario();
                return x -> com.mathkit.CalculosInternos.pow(base.applyAsDouble(x), exp.applyAsDouble(x));
            }
            return base;
        }

        double numero(String t) {
            try { return Double.parseDouble(t); }
            catch (NumberFormatException e) { throw error("número mal escrito '" + t + "'"); }
        }

        DoubleUnaryOperator atomo() {
            if (pos >= s.length()) throw error("falta un valor al final");
            char c = s.charAt(pos);
            if (Character.isDigit(c) || c == '.') {
                int ini = pos;
                while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
                if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                    int q = pos + 1;
                    if (q < s.length() && (s.charAt(q) == '+' || s.charAt(q) == '-')) q++;
                    if (q < s.length() && Character.isDigit(s.charAt(q))) {
                        pos = q;
                        while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
                    }
                }
                final double v = numero(s.substring(ini, pos));
                return x -> v;
            }
            if (toma('(')) {
                DoubleUnaryOperator f = expresion();
                if (!toma(')')) throw error("falta cerrar un paréntesis");
                return f;
            }
            if (Character.isLetter(c)) {
                int ini = pos;
                while (pos < s.length() && Character.isLetter(s.charAt(pos))) pos++;
                String nombre = s.substring(ini, pos).toLowerCase();
                if (toma('(')) {
                    DoubleUnaryOperator arg = expresion();
                    if (!toma(')')) throw error("falta cerrar un paréntesis");
                    return funcion(nombre, arg);
                }
                switch (nombre) {
                    case "x": return x -> x;
                    case "pi": return x -> com.mathkit.CalculosInternos.PI;
                    case "e": return x -> com.mathkit.CalculosInternos.E;
                    default: throw error("nombre desconocido '" + nombre + "'");
                }
            }
            throw error("símbolo inesperado '" + c + "'");
        }

        DoubleUnaryOperator funcion(String n, DoubleUnaryOperator a) {
            switch (n) {
                case "sin": return x -> com.mathkit.CalculosInternos.sin(a.applyAsDouble(x));
                case "cos": return x -> com.mathkit.CalculosInternos.cos(a.applyAsDouble(x));
                case "tan": return x -> com.mathkit.CalculosInternos.tan(a.applyAsDouble(x));
                case "asin": return x -> com.mathkit.CalculosInternos.asin(a.applyAsDouble(x));
                case "acos": return x -> com.mathkit.CalculosInternos.acos(a.applyAsDouble(x));
                case "atan": return x -> com.mathkit.CalculosInternos.atan(a.applyAsDouble(x));
                case "exp": return x -> com.mathkit.CalculosInternos.exp(a.applyAsDouble(x));
                case "ln": return x -> com.mathkit.CalculosInternos.log(a.applyAsDouble(x));
                case "log": return x -> com.mathkit.CalculosInternos.log10(a.applyAsDouble(x));
                case "sqrt": return x -> com.mathkit.CalculosInternos.sqrt(a.applyAsDouble(x));
                case "abs": return x -> com.mathkit.CalculosInternos.abs(a.applyAsDouble(x));
                case "cbrt": return x -> com.mathkit.CalculosInternos.cbrt(a.applyAsDouble(x));
                default: throw error("función desconocida '" + n + "'");
            }
        }
    }
}
