package com.mathkit;

import com.mathkit.errores.ConvergenciaException;
import com.mathkit.errores.DominioException;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/** Métodos numéricos: raíces, derivación, integración y EDOs. */
public final class Numericos {
    private Numericos() {}

    private static final double TOL = 1e-10;
    private static final int MAX_ITER = 200;

    public static double biseccion(DoubleUnaryOperator f, double a, double b) {
        if (f.applyAsDouble(a) * f.applyAsDouble(b) > 0)
            throw new DominioException("f(a) y f(b) deben tener signos opuestos.");
        for (int i = 0; i < MAX_ITER; i++) {
            double c = (a + b) / 2, fc = f.applyAsDouble(c);
            if (com.mathkit.CalculosInternos.abs(fc) < TOL || (b - a) / 2 < TOL) return c;
            if (f.applyAsDouble(a) * fc < 0) b = c; else a = c;
        }
        throw new ConvergenciaException("Bisección no convergió.");
    }

    public static double newtonRaphson(DoubleUnaryOperator f, double x0) {
        return newtonRaphson(f, x -> derivada(f, x), x0);
    }

    public static double newtonRaphson(DoubleUnaryOperator f, DoubleUnaryOperator df, double x0) {
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double d = df.applyAsDouble(x);
            if (d == 0) throw new ConvergenciaException("Derivada nula; Newton-Raphson falla.");
            double xn = x - f.applyAsDouble(x) / d;
            if (com.mathkit.CalculosInternos.abs(xn - x) < TOL) return xn;
            x = xn;
        }
        throw new ConvergenciaException("Newton-Raphson no convergió.");
    }

    public static double secante(DoubleUnaryOperator f, double x0, double x1) {
        for (int i = 0; i < MAX_ITER; i++) {
            double f0 = f.applyAsDouble(x0), f1 = f.applyAsDouble(x1);
            if (f1 == f0) throw new ConvergenciaException("División por cero en la secante.");
            double x2 = x1 - f1 * (x1 - x0) / (f1 - f0);
            if (com.mathkit.CalculosInternos.abs(x2 - x1) < TOL) return x2;
            x0 = x1; x1 = x2;
        }
        throw new ConvergenciaException("Secante no convergió.");
    }

    /** Derivada por diferencia central. */
    public static double derivada(DoubleUnaryOperator f, double x) {
        double h = 1e-6;
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x - h)) / (2 * h);
    }

    public static double trapecio(DoubleUnaryOperator f, double a, double b, int n) {
        if (n < 1) throw new DominioException("n debe ser >= 1.");
        double h = (b - a) / n, s = 0.5 * (f.applyAsDouble(a) + f.applyAsDouble(b));
        for (int i = 1; i < n; i++) s += f.applyAsDouble(a + i * h);
        return s * h;
    }

    public static double simpson(DoubleUnaryOperator f, double a, double b, int n) {
        if (n < 2 || n % 2 != 0) throw new DominioException("n debe ser par y >= 2.");
        double h = (b - a) / n, s = f.applyAsDouble(a) + f.applyAsDouble(b);
        for (int i = 1; i < n; i++) s += (i % 2 == 1 ? 4 : 2) * f.applyAsDouble(a + i * h);
        return s * h / 3;
    }

    /** Resuelve y' = f(x, y) con Euler y devuelve y(xFinal). */
    public static double euler(DoubleBinaryOperator f, double x0, double y0, double xFinal, int pasos) {
        double h = (xFinal - x0) / pasos, x = x0, y = y0;
        for (int i = 0; i < pasos; i++) { y += h * f.applyAsDouble(x, y); x += h; }
        return y;
    }
}
