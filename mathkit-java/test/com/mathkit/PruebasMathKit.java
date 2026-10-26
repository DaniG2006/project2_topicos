package com.mathkit;

import com.mathkit.errores.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigInteger;
import java.util.List;

/** Pruebas sin dependencias: ejecutar con `java -cp out com.mathkit.PruebasMathKit`. */
public class PruebasMathKit {
    static int ok = 0, fallos = 0;

    static void igual(String nombre, double esperado, double real, double tol) {
        if (com.mathkit.CalculosInternos.abs(esperado - real) <= tol) ok++;
        else { fallos++; System.out.println("FALLO " + nombre + ": esperado " + esperado + ", real " + real); }
    }
    static void cierto(String nombre, boolean cond) {
        if (cond) ok++; else { fallos++; System.out.println("FALLO " + nombre); }
    }
    static void lanza(String nombre, Class<? extends Exception> tipo, Runnable r) {
        try { r.run(); fallos++; System.out.println("FALLO " + nombre + ": no lanzó excepción"); }
        catch (Exception e) { cierto(nombre, tipo.isInstance(e)); }
    }

    public static void main(String[] args) {
        // Básicas
        cierto("5!", Basicas.factorial(5).equals(BigInteger.valueOf(120)));
        cierto("25! exacto", Basicas.factorial(25).toString().equals("15511210043330985984000000"));
        igual("raiz cubica -8", -2, Basicas.raiz(-8, 3), 1e-12);
        cierto("mcm", Basicas.mcm(4, 6) == 12);
        cierto("mcd", Basicas.mcd(48, 18) == 6);
        cierto("primo", Basicas.esPrimo(97) && !Basicas.esPrimo(91));
        lanza("raiz par negativa", DominioException.class, () -> Basicas.raiz(-1));

        // Probabilidad
        cierto("C(5,2)", Probabilidad.combinaciones(5, 2).intValue() == 10);
        cierto("P(5,2)", Probabilidad.permutaciones(5, 2).intValue() == 20);
        igual("cdf(0)", 0.5, Probabilidad.normalCdf(0, 0, 1), 1e-12);
        igual("cdf(1.96)", 0.9750, Probabilidad.normalCdf(1.96, 0, 1), 1e-4);
        double suma = 0;
        for (int k = 0; k <= 5; k++) suma += Probabilidad.binomial(k, 5, 0.3);
        igual("binomial suma 1", 1, suma, 1e-12);
        igual("poisson(2; 3)", 0.2240, Probabilidad.poisson(2, 3), 1e-4);

        // Estadística
        double[] d = {2, 4, 4, 4, 5, 5, 7, 9};
        igual("media", 5, Estadistica.media(d), 1e-12);
        igual("desv poblacional", 2, Estadistica.desviacion(d, false), 1e-12);
        cierto("moda", Estadistica.moda(d).equals(List.of(4.0)));
        var r = Estadistica.regresionLineal(new double[]{1, 2, 3}, new double[]{2, 4, 6});
        igual("pendiente", 2, r.pendiente(), 1e-12);
        igual("r2", 1, r.r2(), 1e-12);
        lanza("sin datos", DatosInsuficientesException.class, () -> Estadistica.media(new double[0]));

        // Numéricos
        double raiz2 = com.mathkit.CalculosInternos.sqrt(2);
        igual("biseccion", raiz2, Numericos.biseccion(x -> x * x - 2, 1, 2), 1e-7);
        igual("newton", raiz2, Numericos.newtonRaphson(x -> x * x - 2, 1), 1e-7);
        igual("secante", raiz2, Numericos.secante(x -> x * x - 2, 1, 2), 1e-7);
        igual("simpson", 2, Numericos.simpson(CalculosInternos::sin, 0, com.mathkit.CalculosInternos.PI, 1000), 1e-6);
        igual("euler", com.mathkit.CalculosInternos.E, Numericos.euler((x, y) -> y, 0, 1, 1, 100000), 1e-3);
        lanza("biseccion mal intervalo", DominioException.class, () -> Numericos.biseccion(x -> x * x + 1, 0, 1));

        // Expresion
        igual("expr 2+3*4", 14, Expresion.evaluar("2+3*4"), 1e-12);
        igual("expr 2^3^2", 512, Expresion.evaluar("2^3^2"), 1e-12);
        igual("expr -2^2", -4, Expresion.evaluar("-2^2"), 1e-12);
        igual("expr sqrt+abs", 7, Expresion.evaluar("sqrt(16)+abs(-3)"), 1e-12);
        igual("expr 2pi", 2 * com.mathkit.CalculosInternos.PI, Expresion.evaluar("2pi"), 1e-12);
        igual("expr 1e-3", 0.001, Expresion.evaluar("1e-3"), 1e-15);
        igual("expr f(3)", 7, Expresion.compilar("x^2 - 2").applyAsDouble(3), 1e-12);
        igual("expr 3x", 6, Expresion.compilar("3x").applyAsDouble(2), 1e-12);
        lanza("expr incompleta", DominioException.class, () -> Expresion.compilar("2+"));
        lanza("expr parentesis", DominioException.class, () -> Expresion.compilar("(1+2"));
        lanza("expr nombre", DominioException.class, () -> Expresion.compilar("foo"));

        // CalculosInternos (valores de referencia conocidos)
        double piv = 3.141592653589793;
        igual("atan 1", 0.7853981633974483, CalculosInternos.atan(1), 1e-14);
        igual("atan 0.5", 0.4636476090008061, CalculosInternos.atan(0.5), 1e-14);
        igual("atan -3", -1.2490457723982544, CalculosInternos.atan(-3), 1e-14);
        igual("asin 0.5", 0.5235987755982989, CalculosInternos.asin(0.5), 1e-14);
        igual("acos 0.5", 1.0471975511965979, CalculosInternos.acos(0.5), 1e-14);
        igual("sqrt 1e300", 1, CalculosInternos.sqrt(1e300) / 1e150, 1e-12);
        igual("cbrt 1e300", 1, CalculosInternos.cbrt(1e300) / 1e100, 1e-12);
        igual("cbrt -27", -3, CalculosInternos.cbrt(-27), 1e-14);
        igual("raiz -27,3 exacta", -3, Basicas.raiz(-27, 3), 0);
        igual("sin 1000", 0.8268795405320025, CalculosInternos.sin(1000), 1e-13);
        igual("sin 1e7", 0.4205477931907825, CalculosInternos.sin(1e7), 1e-9);
        igual("cos pi", -1, CalculosInternos.cos(piv), 1e-14);
        igual("exp 10", 22026.465794806718, CalculosInternos.exp(10), 1e-9);
        igual("log 12345.678", 9.421061321291832, CalculosInternos.log(12345.678), 1e-13);
        igual("pow 2^2.5", 5.656854249492381, CalculosInternos.pow(2, 2.5), 1e-13);
        igual("rint 2.5 (par)", 2, CalculosInternos.rint(2.5), 0);
        igual("rint 3.5 (par)", 4, CalculosInternos.rint(3.5), 0);

        // Usuarios (login)
        try {
            Path tmp = Files.createTempFile("mathkit", ".properties");
            Files.delete(tmp);
            Usuarios us = new Usuarios(tmp);
            us.registrar("Ana_01", "secreta1".toCharArray());
            cierto("login correcto", us.autenticar("ana_01", "secreta1".toCharArray()));
            cierto("login ignora mayúsculas del usuario", us.autenticar("ANA_01", "secreta1".toCharArray()));
            cierto("login clave incorrecta", !us.autenticar("ana_01", "otra1234".toCharArray()));
            cierto("login usuario inexistente", !us.autenticar("nadie", "secreta1".toCharArray()));
            cierto("login clave vacía", !us.autenticar("ana_01", new char[0]));
            lanza("usuario repetido", AutenticacionException.class, () -> us.registrar("ana_01", "secreta1".toCharArray()));
            lanza("clave corta", AutenticacionException.class, () -> us.registrar("pepe", "123".toCharArray()));
            lanza("usuario inválido", AutenticacionException.class, () -> us.registrar("a b", "secreta1".toCharArray()));
            cierto("no guarda la clave en claro", !Files.readString(tmp).contains("secreta1"));
            cierto("persiste en disco", new Usuarios(tmp).autenticar("ana_01", "secreta1".toCharArray()));
            Files.delete(tmp);
        } catch (java.io.IOException e) {
            fallos++;
            System.out.println("FALLO usuarios: " + e.getMessage());
        }

        System.out.println("Pruebas OK: " + ok + " | Fallos: " + fallos);
        if (fallos > 0) System.exit(1);
    }
}
