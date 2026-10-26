package com.mathkit.gui;

import com.mathkit.Usuarios;

import javax.swing.SwingUtilities;

/** Punto de entrada de la aplicación de escritorio: primero el login y luego la ventana principal. */
public final class MathKitApp {
    private MathKitApp() {}

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(MathKitApp::mostrarLogin);
    }

    static void mostrarLogin() {
        new VentanaLogin(new Usuarios(), MathKitApp::mostrarPrincipal).setVisible(true);
    }

    static void mostrarPrincipal(String usuario) {
        VentanaPrincipal[] ventana = new VentanaPrincipal[1];
        ventana[0] = new VentanaPrincipal(usuario, () -> {
            mostrarLogin();          // se abre el login antes de cerrar, para que la aplicación no termine
            ventana[0].dispose();
        });
        ventana[0].setVisible(true);
    }
}
