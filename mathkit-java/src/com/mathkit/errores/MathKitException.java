package com.mathkit.errores;

/** Excepción base de la librería (no verificada). */
public class MathKitException extends RuntimeException {
    public MathKitException(String mensaje) { super(mensaje); }
}
