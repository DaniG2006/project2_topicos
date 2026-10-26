package com.mathkit.errores;

/** El método numérico no convergió. */
public class ConvergenciaException extends MathKitException {
    public ConvergenciaException(String mensaje) { super(mensaje); }
}
