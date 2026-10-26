package com.mathkit.errores;

/** No hay datos suficientes para calcular el resultado. */
public class DatosInsuficientesException extends MathKitException {
    public DatosInsuficientesException(String mensaje) { super(mensaje); }
}
