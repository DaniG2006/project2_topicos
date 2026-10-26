package com.mathkit.errores;

/** Error de registro o inicio de sesión (datos inválidos, usuario repetido, etc.). */
public class AutenticacionException extends MathKitException {
    public AutenticacionException(String mensaje) { super(mensaje); }
}
