package com.mathkit.gui;

import com.mathkit.Usuarios;
import com.mathkit.errores.AutenticacionException;
import com.mathkit.errores.MathKitException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

/** Ventana de inicio de sesión y registro, con el mismo diseño que la ventana principal. */
public final class VentanaLogin extends JFrame {

    public VentanaLogin(Usuarios usuarios, Consumer<String> alEntrar) {
        super("MathKit — Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(construirContenido(usuarios, usuario -> { alEntrar.accept(usuario); dispose(); }));
        setSize(860, 560);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /** Construye el contenido (separado para poder probarlo sin abrir ventana). */
    public static JComponent construirContenido(Usuarios usuarios, Consumer<String> alEntrar) {
        return new Contenido(usuarios, alEntrar);
    }

    private static final class Contenido extends JPanel {
        private final Usuarios usuarios;
        private final Consumer<String> alEntrar;
        private final JTextField campoUsuario = Tema.campo("");
        private final JPasswordField campoClave = Tema.campoClave();
        private final JPasswordField campoConfirmar = Tema.campoClave();
        private final JCheckBox mostrar = new JCheckBox("Mostrar contraseña");
        private final JLabel mensaje = Tema.etiqueta(" ", Tema.sans(Font.PLAIN, 13), Tema.ERROR);
        private final JPanel derecha = new JPanel(new GridBagLayout());
        private boolean registro = false;

        Contenido(Usuarios usuarios, Consumer<String> alEntrar) {
            super(new BorderLayout());
            this.usuarios = usuarios;
            this.alEntrar = alEntrar;
            derecha.setBackground(Color.WHITE);
            add(panelLateral(), BorderLayout.WEST);
            add(derecha, BorderLayout.CENTER);

            mostrar.setOpaque(false);
            mostrar.setFocusPainted(false);
            mostrar.setFont(Tema.sans(Font.PLAIN, 12));
            mostrar.setForeground(Tema.TEXTO_SEC);
            mostrar.addActionListener(e -> {
                char eco = mostrar.isSelected() ? (char) 0 : '\u2022';
                campoClave.setEchoChar(eco);
                campoConfirmar.setEchoChar(eco);
            });
            campoUsuario.addActionListener(e -> campoClave.requestFocusInWindow());
            campoClave.addActionListener(e -> { if (registro) campoConfirmar.requestFocusInWindow(); else enviar(); });
            campoConfirmar.addActionListener(e -> enviar());
            construirFormulario();
        }

        // ---------- Panel izquierdo oscuro ----------
        private JPanel panelLateral() {
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.setBackground(Tema.SIDEBAR);
            p.setBorder(new EmptyBorder(36, 36, 28, 36));
            p.setPreferredSize(new Dimension(360, 0));

            JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            logo.setOpaque(false);
            logo.add(new Insignia("\u2211", true, 44));
            JLabel nombre = Tema.etiqueta("MathKit", Tema.sans(Font.BOLD, 22), Color.WHITE);
            nombre.setBorder(new EmptyBorder(0, 12, 0, 0));
            logo.add(nombre);
            alinear(logo);
            p.add(logo);
            p.add(Tema.espacio(44));
            p.add(texto("Calcula, analiza", Tema.sans(Font.BOLD, 28), Color.WHITE));
            p.add(texto("y resuelve.", Tema.sans(Font.BOLD, 28), Color.WHITE));
            p.add(Tema.espacio(12));
            p.add(texto("Raíces, probabilidad, estadística", Tema.sans(Font.PLAIN, 14), Tema.TEXTO_SIDE));
            p.add(texto("y métodos numéricos en un solo lugar.", Tema.sans(Font.PLAIN, 14), Tema.TEXTO_SIDE));
            p.add(Tema.espacio(34));
            p.add(modulo("\u221Ax", "Básicas", "Raíces, potencias y factoriales"));
            p.add(Tema.espacio(14));
            p.add(modulo("P", "Probabilidad", "Combinatoria y distribuciones"));
            p.add(Tema.espacio(14));
            p.add(modulo("\u03C3", "Estadística", "Resumen y regresión lineal"));
            p.add(Tema.espacio(14));
            p.add(modulo("\u222B", "Numéricos", "Raíces, derivadas e integrales"));
            p.add(Box.createVerticalGlue());
            p.add(texto("Versión 1.0.0 · Java " + Runtime.version().feature(), Tema.sans(Font.PLAIN, 12), Tema.TEXTO_SIDE_SEC));
            return p;
        }

        private static JLabel texto(String t, Font f, Color c) {
            JLabel l = Tema.etiqueta(t, f, c);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        private static void alinear(JComponent c) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }

        private static JPanel modulo(String simbolo, String titulo, String detalle) {
            JPanel textos = new JPanel(new GridLayout(2, 1));
            textos.setOpaque(false);
            textos.add(Tema.etiqueta(titulo, Tema.sans(Font.BOLD, 14), Color.WHITE));
            textos.add(Tema.etiqueta(detalle, Tema.sans(Font.PLAIN, 12), Tema.TEXTO_SIDE_SEC));
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setOpaque(false);
            fila.add(new Insignia(simbolo, false, 34), BorderLayout.WEST);
            fila.add(textos, BorderLayout.CENTER);
            alinear(fila);
            return fila;
        }

        // ---------- Formulario (login o registro) ----------
        private void construirFormulario() {
            derecha.removeAll();
            JPanel f = new JPanel() {
                @Override public Dimension getPreferredSize() {
                    Dimension d = super.getPreferredSize();
                    return new Dimension(340, d.height);
                }
            };
            f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
            f.setOpaque(false);

            f.add(texto(registro ? "Crear cuenta" : "Iniciar sesión", Tema.sans(Font.BOLD, 24), Tema.TEXTO));
            f.add(Tema.espacio(2));
            f.add(texto(registro ? "Elige un usuario y una contraseña" : "Ingresa para usar MathKit",
                    Tema.sans(Font.PLAIN, 14), Tema.TEXTO_SEC));
            f.add(Tema.espacio(22));
            f.add(fila("Usuario", campoUsuario));
            f.add(Tema.espacio(12));
            f.add(fila("Contraseña", campoClave));
            if (registro) {
                f.add(Tema.espacio(12));
                f.add(fila("Confirmar contraseña", campoConfirmar));
            }
            f.add(Tema.espacio(8));
            mostrar.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.add(mostrar);
            f.add(Tema.espacio(4));
            mensaje.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.add(mensaje);
            f.add(Tema.espacio(10));

            Boton entrar = new Boton(registro ? "Crear cuenta y entrar" : "Entrar", Boton.Tipo.PRIMARIO);
            entrar.addActionListener(e -> enviar());
            JPanel filaBoton = new JPanel(new BorderLayout());
            filaBoton.setOpaque(false);
            filaBoton.add(entrar, BorderLayout.CENTER);
            alinear(filaBoton);
            f.add(filaBoton);
            f.add(Tema.espacio(16));

            JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            pie.setOpaque(false);
            pie.add(Tema.etiqueta(registro ? "¿Ya tienes cuenta?" : "¿No tienes cuenta?",
                    Tema.sans(Font.PLAIN, 13), Tema.TEXTO_SEC));
            Enlace cambiar = new Enlace(registro ? "Iniciar sesión" : "Crear cuenta", Tema.ACENTO, new Color(0x075F49));
            cambiar.addActionListener(e -> cambiarModo());
            pie.add(cambiar);
            alinear(pie);
            f.add(pie);

            derecha.add(f);
            derecha.revalidate();
            derecha.repaint();
        }

        private static JComponent fila(String etiqueta, JComponent campo) {
            JPanel p = Tema.conEtiqueta(etiqueta, campo);
            alinear(p);
            return p;
        }

        private void cambiarModo() {
            registro = !registro;
            mensaje.setText(" ");
            campoClave.setText("");
            campoConfirmar.setText("");
            construirFormulario();
            SwingUtilities.invokeLater(campoUsuario::requestFocusInWindow);
        }

        private void error(String texto) {
            mensaje.setText(texto);
        }

        private void enviar() {
            char[] clave = campoClave.getPassword();
            char[] confirmar = campoConfirmar.getPassword();
            try {
                String usuario = campoUsuario.getText().trim();
                if (usuario.isEmpty() || clave.length == 0)
                    throw new AutenticacionException("Escribe tu usuario y tu contraseña.");
                if (registro) {
                    if (!Arrays.equals(clave, confirmar))
                        throw new AutenticacionException("Las contraseñas no coinciden.");
                    usuarios.registrar(usuario, clave);
                } else if (!usuarios.autenticar(usuario, clave)) {
                    throw new AutenticacionException("Usuario o contraseña incorrectos.");
                }
                campoClave.setText("");
                campoConfirmar.setText("");
                alEntrar.accept(usuario.toLowerCase());
            } catch (MathKitException e) {
                error(e.getMessage());
            } finally {
                Arrays.fill(clave, '\0');
                Arrays.fill(confirmar, '\0');
            }
        }
    }
}
