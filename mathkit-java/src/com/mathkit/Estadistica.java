package com.mathkit;

import com.mathkit.errores.DatosInsuficientesException;
import com.mathkit.errores.DominioException;
import java.util.*;

/** Estadística descriptiva, correlación y regresión lineal. */
public final class Estadistica {
    private Estadistica() {}

    /** Resultado de y = pendiente·x + intercepto. */
    public record Regresion(double pendiente, double intercepto, double r2) {}

    private static void validar(double[] d, int minimo) {
        if (d == null || d.length < minimo)
            throw new DatosInsuficientesException("Se requieren al menos " + minimo + " datos.");
    }

    public static double media(double[] d) {
        validar(d, 1);
        double s = 0;
        for (double x : d) s += x;
        return s / d.length;
    }

    public static double mediana(double[] d) {
        validar(d, 1);
        double[] s = d.clone();
        Arrays.sort(s);
        int m = s.length / 2;
        return s.length % 2 == 1 ? s[m] : (s[m - 1] + s[m]) / 2;
    }

    public static List<Double> moda(double[] d) {
        validar(d, 1);
        Map<Double, Integer> conteo = new TreeMap<>();
        for (double x : d) conteo.merge(x, 1, Integer::sum);
        int max = Collections.max(conteo.values());
        List<Double> res = new ArrayList<>();
        conteo.forEach((k, v) -> { if (v == max) res.add(k); });
        return res;
    }

    public static double varianza(double[] d, boolean muestral) {
        validar(d, muestral ? 2 : 1);
        double mu = media(d), s = 0;
        for (double x : d) s += (x - mu) * (x - mu);
        return s / (muestral ? d.length - 1 : d.length);
    }

    public static double desviacion(double[] d, boolean muestral) {
        return com.mathkit.CalculosInternos.sqrt(varianza(d, muestral));
    }

    public static double rango(double[] d) {
        validar(d, 1);
        return Arrays.stream(d).max().getAsDouble() - Arrays.stream(d).min().getAsDouble();
    }

    public static double covarianza(double[] x, double[] y) {
        if (x.length != y.length) throw new DominioException("x e y deben tener la misma longitud.");
        validar(x, 2);
        double mx = media(x), my = media(y), s = 0;
        for (int i = 0; i < x.length; i++) s += (x[i] - mx) * (y[i] - my);
        return s / (x.length - 1);
    }

    public static double correlacion(double[] x, double[] y) {
        double sx = desviacion(x, true), sy = desviacion(y, true);
        if (sx == 0 || sy == 0)
            throw new DominioException("La correlación no está definida con varianza 0.");
        return covarianza(x, y) / (sx * sy);
    }

    public static Regresion regresionLineal(double[] x, double[] y) {
        double vx = varianza(x, true);
        if (vx == 0) throw new DominioException("x no puede ser constante.");
        double m = covarianza(x, y) / vx;
        double b = media(y) - m * media(x);
        double r = correlacion(x, y);
        return new Regresion(m, b, r * r);
    }
}
