package com.mathkit;

import com.mathkit.errores.MathKitException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/** Fachada para el usuario final: importa los módulos y guarda un historial. */
public class Calculadora {

    public enum MetodoRaiz { BISECCION, NEWTON, SECANTE }
    public enum MetodoIntegral { SIMPSON, TRAPECIO }

    private final List<String> historial = new ArrayList<>();

    public List<String> getHistorial() { return Collections.unmodifiableList(historial); }

    private <T> T registrar(String operacion, T resultado) {
        historial.add(operacion + " = " + resultado);
        return resultado;
    }

    // --- Básicas ---
    public double raiz(double valor, int indice) {
        return registrar("raiz(" + valor + ", " + indice + ")", Basicas.raiz(valor, indice));
    }
    public double potencia(double base, double exp) {
        return registrar(base + "^" + exp, Basicas.potencia(base, exp));
    }
    public BigInteger factorial(int n) {
        return registrar(n + "!", Basicas.factorial(n));
    }

    public long mcd(long a, long b) {
        return registrar("MCD(" + a + "," + b + ")", Basicas.mcd(a, b));
    }
    public long mcm(long a, long b) {
        return registrar("MCM(" + a + "," + b + ")", Basicas.mcm(a, b));
    }

    // --- Probabilidad ---
    public BigInteger combinaciones(int n, int r) {
        return registrar("C(" + n + "," + r + ")", Probabilidad.combinaciones(n, r));
    }
    public double binomial(int k, int n, double p) {
        return registrar("Binomial(k=" + k + ", n=" + n + ", p=" + p + ")", Probabilidad.binomial(k, n, p));
    }
    public double poisson(int k, double lambda) {
        return registrar("Poisson(k=" + k + ", λ=" + lambda + ")", Probabilidad.poisson(k, lambda));
    }
    public double normalAcumulada(double x, double mu, double sigma) {
        return registrar("Φ(" + x + "; μ=" + mu + ", σ=" + sigma + ")", Probabilidad.normalCdf(x, mu, sigma));
    }

    // --- Estadística ---
    public Map<String, Object> resumen(double... datos) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("n", datos.length);
        r.put("media", Estadistica.media(datos));
        r.put("mediana", Estadistica.mediana(datos));
        r.put("moda", Estadistica.moda(datos));
        r.put("desviacion", Estadistica.desviacion(datos, true));
        r.put("rango", Estadistica.rango(datos));
        return registrar("resumen(" + Arrays.toString(datos) + ")", r);
    }
    public Estadistica.Regresion regresion(double[] x, double[] y) {
        return registrar("regresionLineal", Estadistica.regresionLineal(x, y));
    }

    // --- Numéricos ---
    public double encontrarRaiz(DoubleUnaryOperator f, double a, double b, MetodoRaiz metodo) {
        double r = switch (metodo) {
            case BISECCION -> Numericos.biseccion(f, a, b);
            case NEWTON -> Numericos.newtonRaphson(f, a);
            case SECANTE -> Numericos.secante(f, a, b);
        };
        return registrar("raiz de f por " + metodo, r);
    }
    public double derivada(DoubleUnaryOperator f, double x) {
        return registrar("f'(" + x + ")", Numericos.derivada(f, x));
    }
    public double integrar(DoubleUnaryOperator f, double a, double b, MetodoIntegral metodo) {
        double r = metodo == MetodoIntegral.SIMPSON
                ? Numericos.simpson(f, a, b, 1000) : Numericos.trapecio(f, a, b, 1000);
        return registrar("∫[" + a + "," + b + "] f (" + metodo + ")", r);
    }

    public void limpiarHistorial() { historial.clear(); }
}
