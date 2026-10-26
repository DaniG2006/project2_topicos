package com.mathkit;

import com.mathkit.Calculadora.*;
import com.mathkit.errores.MathKitException;

public class Demo {
    public static void main(String[] args) {
        Calculadora calc = new Calculadora();
        System.out.println("Raíz cúbica de -27: " + calc.raiz(-27, 3));
        System.out.println("2^10: " + calc.potencia(2, 10));
        System.out.println("25!: " + calc.factorial(25));
        System.out.println("C(52,5): " + calc.combinaciones(52, 5));
        System.out.printf("P(X=3), Bin(10, 0.5): %.4f%n", calc.binomial(3, 10, 0.5));
        System.out.printf("P(Z<=1.96): %.4f%n", calc.normalAcumulada(1.96, 0, 1));
        System.out.println("Resumen: " + calc.resumen(4, 8, 8, 15, 16, 23, 42));
        var reg = calc.regresion(new double[]{1, 2, 3, 4, 5}, new double[]{2.1, 4.0, 6.2, 7.9, 10.1});
        System.out.printf("Regresión: m=%.3f b=%.3f R²=%.4f%n", reg.pendiente(), reg.intercepto(), reg.r2());
        System.out.println("Raíz de x²-2 (Newton): "
                + calc.encontrarRaiz(x -> x * x - 2, 1, 2, MetodoRaiz.NEWTON));
        System.out.printf("∫ sen(x) de 0 a π: %.6f%n",
                calc.integrar(CalculosInternos::sin, 0, com.mathkit.CalculosInternos.PI, MetodoIntegral.SIMPSON));
        try {
            calc.raiz(-4, 2);
        } catch (MathKitException e) {
            System.out.println("Error controlado: " + e.getMessage());
        }
        System.out.println("Operaciones en historial: " + calc.getHistorial().size());
    }
}
