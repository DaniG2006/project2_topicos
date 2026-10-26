package com.mathkit.errores;

/** El valor está fuera del dominio de la operación (ej. raíz par de un negativo). */
public class DominioException extends MathKitException {
    public DominioException(String mensaje) { super(mensaje); }
}
