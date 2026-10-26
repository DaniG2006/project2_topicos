package com.mathkit;

/**
 * Operaciones matemáticas implementadas por MathKit sin llamar métodos de java.lang.Math.
 * Se conserva la API original; se corrigieron atan/asin/acos, sqrt y cbrt con números
 * grandes, y la reducción de ángulos de sin/cos.
 */
public final class CalculosInternos {
    private CalculosInternos() {}

    public static final double PI = 3.1415926535897932384626433832795;
    public static final double E = 2.7182818284590452353602874713527;
    private static final double LN2 = 0.6931471805599453;
    private static final double LN10 = 2.302585092994046;
    // 2π partido en dos (Cody-Waite): ALTO tiene pocos bits, así k·ALTO es exacto.
    private static final double DOS_PI_ALTO = 6.28318530693650245668;
    private static final double DOS_PI_BAJO = 2.43084020260247689972e-10;

    public static double abs(double x) { return x < 0 ? -x : x; }
    public static long abs(long x) { return x < 0 ? -x : x; }
    public static int min(int a, int b) { return a < b ? a : b; }
    public static double min(double a, double b) { return a < b ? a : b; }
    public static double max(double a, double b) { return a > b ? a : b; }
    public static long round(double x) { return (long) (x >= 0 ? x + 0.5 : x - 0.5); }
    public static double signum(double x) { return x > 0 ? 1 : x < 0 ? -1 : x; }

    public static double floor(double x) {
        if (Double.isNaN(x) || abs(x) >= 4.5e15) return x;
        long l = (long) x;
        return (x < 0 && x != l) ? l - 1 : l;
    }

    /** Redondeo al entero más cercano; en empate, al par (igual que rint). */
    public static double rint(double x) {
        double f = floor(x), d = x - f;
        if (d < 0.5) return f;
        if (d > 0.5) return f + 1;
        return (f % 2 == 0) ? f : f + 1;
    }

    // ---------- Potencias y raíces ----------
    private static double potenciaEntera(double base, long n) {
        boolean neg = n < 0;
        long m = neg ? -n : n;
        double r = 1, b = base;
        while (m > 0) { if ((m & 1) == 1) r *= b; b *= b; m >>= 1; }
        return neg ? 1 / r : r;
    }

    public static double pow(double base, double exp) {
        if (exp == 0) return 1;
        if (Double.isNaN(base) || Double.isNaN(exp)) return Double.NaN;
        if (exp == (long) exp && abs(exp) < 9e15) return potenciaEntera(base, (long) exp);
        if (base <= 0) return Double.NaN;
        return exp(log(base) * exp);
    }

    public static double sqrt(double x) {
        if (Double.isNaN(x) || x < 0) return Double.NaN;
        if (x == 0 || Double.isInfinite(x)) return x;
        int k = 0;
        while (x >= 4) { x /= 4; k++; }
        while (x < 0.25) { x *= 4; k--; }
        double r = 1;
        for (int i = 0; i < 8; i++) r = (r + x / r) / 2;
        for (; k > 0; k--) r *= 2;
        for (; k < 0; k++) r /= 2;
        return r;
    }

    public static double cbrt(double x) {
        if (x == 0 || Double.isNaN(x) || Double.isInfinite(x)) return x;
        if (x < 0) return -cbrt(-x);
        int k = 0;
        while (x >= 8) { x /= 8; k++; }
        while (x < 0.125) { x *= 8; k--; }
        double r = 1;
        for (int i = 0; i < 12; i++) r = (2 * r + x / (r * r)) / 3;
        for (; k > 0; k--) r *= 2;
        for (; k < 0; k++) r /= 2;
        return r;
    }

    /** Raíz n-ésima real: valor inicial con exp/log y refinamiento por Newton. */
    public static double raiz(double a, int n) {
        if (n == 0 || Double.isNaN(a)) return Double.NaN;
        if (n < 0) return 1 / raiz(a, -n);
        if (a < 0) return n % 2 == 0 ? Double.NaN : -raiz(-a, n);
        if (a == 0 || Double.isInfinite(a) || n == 1) return a;
        if (n == 2) return sqrt(a);
        if (n == 3) return cbrt(a);
        double g = exp(log(a) / n);
        for (int i = 0; i < 4; i++) {
            double p = potenciaEntera(g, n - 1);
            if (p == 0 || Double.isInfinite(p)) break;
            g = ((n - 1) * g + a / p) / n;
        }
        return g;
    }

    // ---------- Exponencial y logaritmos ----------
    public static double exp(double x) {
        if (Double.isNaN(x)) return x;
        if (x > 709) return Double.POSITIVE_INFINITY;
        if (x < -745) return 0;
        long k = (long) floor(x / LN2 + 0.5);
        double r = x - k * LN2, term = 1, sum = 1;
        for (int i = 1; i <= 30; i++) { term *= r / i; sum += term; if (abs(term) < 1e-17 * abs(sum)) break; }
        return sum * potenciaEntera(2, k);
    }

    public static double log(double x) {
        if (Double.isNaN(x) || x < 0) return Double.NaN;
        if (x == 0) return Double.NEGATIVE_INFINITY;
        if (Double.isInfinite(x)) return x;
        int k = 0;
        while (x > 1.5) { x /= 2.0; k++; }
        while (x < 0.75) { x *= 2.0; k--; }
        double y = (x - 1) / (x + 1), y2 = y * y, term = y, sum = 0;
        for (int n = 1; n < 200; n += 2) { double add = term / n; sum += add; if (abs(add) < 1e-17) break; term *= y2; }
        return 2 * sum + k * LN2;
    }

    public static double log10(double x) { return log(x) / LN10; }

    // ---------- Trigonometría ----------
    private static double reduce(double x) {
        double k = floor(x / (2 * PI) + 0.5);
        return (x - k * DOS_PI_ALTO) - k * DOS_PI_BAJO;
    }

    public static double sin(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        x = reduce(x);
        double t = x, s = x;
        for (int n = 1; n < 30; n++) { t *= -x * x / ((2 * n) * (2 * n + 1)); s += t; }
        return s;
    }

    public static double cos(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return Double.NaN;
        x = reduce(x);
        double t = 1, s = 1;
        for (int n = 1; n < 30; n++) { t *= -x * x / ((2 * n - 1) * (2 * n)); s += t; }
        return s;
    }

    public static double tan(double x) { return sin(x) / cos(x); }

    /** atan: se lleva x a [0, 1], se reduce dos veces a la mitad del ángulo y se usa la serie de Taylor. */
    public static double atan(double x) {
        if (Double.isNaN(x)) return x;
        if (x < 0) return -atan(-x);
        if (x > 1) return PI / 2 - atan(1 / x);
        x = x / (1 + sqrt(1 + x * x));
        x = x / (1 + sqrt(1 + x * x));
        double x2 = x * x, t = x, s = 0;
        for (int n = 0; n < 25; n++) { s += t / (2 * n + 1); t *= -x2; }
        return 4 * s;
    }

    public static double asin(double x) {
        if (Double.isNaN(x) || abs(x) > 1) return Double.NaN;
        if (x == 1) return PI / 2;
        if (x == -1) return -PI / 2;
        return atan(x / sqrt(1 - x * x));
    }

    public static double acos(double x) {
        if (Double.isNaN(x) || abs(x) > 1) return Double.NaN;
        return PI / 2 - asin(x);
    }
}
