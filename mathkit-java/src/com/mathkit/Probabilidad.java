package com.mathkit;

import com.mathkit.errores.DominioException;
import java.math.BigInteger;

/** Combinatoria y distribuciones de probabilidad. */
public final class Probabilidad {
    private Probabilidad() {}

    private static void validarNR(int n, int r) {
        if (r < 0 || r > n) throw new DominioException("Se requiere 0 <= r <= n.");
    }

    public static BigInteger permutaciones(int n, int r) {
        validarNR(n, r);
        BigInteger res = BigInteger.ONE;
        for (int i = 0; i < r; i++) res = res.multiply(BigInteger.valueOf(n - i));
        return res;
    }

    public static BigInteger combinaciones(int n, int r) {
        validarNR(n, r);
        int k = com.mathkit.CalculosInternos.min(r, n - r);
        BigInteger res = BigInteger.ONE;
        for (int i = 1; i <= k; i++)
            res = res.multiply(BigInteger.valueOf(n - k + i)).divide(BigInteger.valueOf(i));
        return res;
    }

    public static double probabilidadSimple(long favorables, long posibles) {
        if (posibles <= 0 || favorables < 0 || favorables > posibles)
            throw new DominioException("Valores inválidos para casos favorables/posibles.");
        return (double) favorables / posibles;
    }

    /** P(A|B) = P(A∩B) / P(B). */
    public static double condicional(double pAyB, double pB) {
        if (pB <= 0) throw new DominioException("P(B) debe ser mayor que 0.");
        return pAyB / pB;
    }

    /** P(A|B) = P(B|A)·P(A) / P(B). */
    public static double bayes(double pBdadoA, double pA, double pB) {
        if (pB <= 0) throw new DominioException("P(B) debe ser mayor que 0.");
        return pBdadoA * pA / pB;
    }

    /** P(X = k) para X ~ Binomial(n, p). */
    public static double binomial(int k, int n, double p) {
        if (p < 0 || p > 1 || k < 0 || k > n)
            throw new DominioException("Parámetros inválidos para la binomial.");
        return combinaciones(n, k).doubleValue() * com.mathkit.CalculosInternos.pow(p, k) * com.mathkit.CalculosInternos.pow(1 - p, n - k);
    }

    /** P(X = k) para X ~ Poisson(λ). */
    public static double poisson(int k, double lambda) {
        if (k < 0 || lambda <= 0) throw new DominioException("Se requiere k >= 0 y λ > 0.");
        double termino = com.mathkit.CalculosInternos.exp(-lambda);
        for (int i = 1; i <= k; i++) termino *= lambda / i;
        return termino;
    }

    public static double normalPdf(double x, double mu, double sigma) {
        if (sigma <= 0) throw new DominioException("σ debe ser positivo.");
        double z = (x - mu) / sigma;
        return com.mathkit.CalculosInternos.exp(-0.5 * z * z) / (sigma * com.mathkit.CalculosInternos.sqrt(2 * com.mathkit.CalculosInternos.PI));
    }

    public static double normalCdf(double x, double mu, double sigma) {
        if (sigma <= 0) throw new DominioException("σ debe ser positivo.");
        return 0.5 * (1 + erf((x - mu) / (sigma * com.mathkit.CalculosInternos.sqrt(2))));
    }

    /** Función de error. Java no la incluye; serie sin cancelación (términos positivos). */
    static double erf(double x) {
        double ax = com.mathkit.CalculosInternos.abs(x);
        if (ax > 6) return com.mathkit.CalculosInternos.signum(x);
        double suma = 0, termino = ax;
        for (int n = 0; n < 500; n++) {
            suma += termino;
            termino *= 2 * ax * ax / (2 * n + 3);
            if (termino < 1e-17 * suma) break;
        }
        double r = 2 / com.mathkit.CalculosInternos.sqrt(com.mathkit.CalculosInternos.PI) * com.mathkit.CalculosInternos.exp(-ax * ax) * suma;
        return x < 0 ? -r : r;
    }
}
