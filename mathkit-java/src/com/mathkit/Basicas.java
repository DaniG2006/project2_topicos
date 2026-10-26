package com.mathkit;

import com.mathkit.errores.DominioException;
import java.math.BigInteger;

/** Operaciones básicas: potencia, raíz, factorial, MCD, MCM y primos. */
public final class Basicas {
    private Basicas() {}

    public static double potencia(double base, double exponente) {
        if (base == 0 && exponente < 0)
            throw new DominioException("0 no puede elevarse a un exponente negativo.");
        return com.mathkit.CalculosInternos.pow(base, exponente);
    }

    /** Raíz n-ésima real. Admite índice impar con radicando negativo. */
    public static double raiz(double valor, int indice) {
        if (indice == 0) throw new DominioException("El índice de la raíz no puede ser 0.");
        if (valor < 0) {
            if (indice % 2 == 0)
                throw new DominioException("Raíz par de un número negativo no es real.");
            return -com.mathkit.CalculosInternos.raiz(-valor, indice);
        }
        return com.mathkit.CalculosInternos.raiz(valor, indice);
    }

    public static double raiz(double valor) { return raiz(valor, 2); }

    /** Factorial exacto con BigInteger (sin desbordamiento). */
    public static BigInteger factorial(int n) {
        if (n < 0) throw new DominioException("El factorial requiere un entero >= 0.");
        BigInteger r = BigInteger.ONE;
        for (int i = 2; i <= n; i++) r = r.multiply(BigInteger.valueOf(i));
        return r;
    }

    public static long mcd(long a, long b) {
        a = com.mathkit.CalculosInternos.abs(a); b = com.mathkit.CalculosInternos.abs(b);
        while (b != 0) { long t = a % b; a = b; b = t; }
        return a;
    }

    public static long mcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        return com.mathkit.CalculosInternos.abs(a / mcd(a, b) * b);
    }

    public static boolean esPrimo(long n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        for (long i = 3; i * i <= n; i += 2) if (n % i == 0) return false;
        return true;
    }
}
