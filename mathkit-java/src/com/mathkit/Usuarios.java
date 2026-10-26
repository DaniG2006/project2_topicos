package com.mathkit;

import com.mathkit.errores.AutenticacionException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;

/**
 * Registro e inicio de sesión de usuarios.
 * Las contraseñas nunca se guardan: se almacena sal + hash PBKDF2-HMAC-SHA256.
 * Archivo por defecto: ~/.mathkit/usuarios.properties (o la ruta de -Dmathkit.usuarios).
 */
public final class Usuarios {
    private static final int ITERACIONES = 65_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_CLAVE = 256;

    private final Path archivo;
    private final Properties datos = new Properties();

    public Usuarios() { this(rutaPorDefecto()); }

    public Usuarios(Path archivo) {
        this.archivo = archivo;
        cargar();
    }

    private static Path rutaPorDefecto() {
        String ruta = System.getProperty("mathkit.usuarios");
        return ruta != null ? Path.of(ruta) : Path.of(System.getProperty("user.home"), ".mathkit", "usuarios.properties");
    }

    private static String normalizar(String usuario) {
        return usuario == null ? "" : usuario.trim().toLowerCase(Locale.ROOT);
    }

    public synchronized void registrar(String usuario, char[] clave) {
        String u = normalizar(usuario);
        if (!u.matches("[a-z0-9_]{3,20}"))
            throw new AutenticacionException("El usuario debe tener de 3 a 20 letras (sin tildes), números o _.");
        if (clave == null || clave.length < 6)
            throw new AutenticacionException("La contraseña debe tener al menos 6 caracteres.");
        if (datos.containsKey(u))
            throw new AutenticacionException("Ese usuario ya existe.");
        byte[] sal = new byte[BYTES_SAL];
        new SecureRandom().nextBytes(sal);
        byte[] hash = derivar(clave, sal, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder();
        datos.setProperty(u, ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash));
        guardar();
    }

    public synchronized boolean autenticar(String usuario, char[] clave) {
        if (clave == null || clave.length == 0) return false;
        String registro = datos.getProperty(normalizar(usuario));
        if (registro == null) {                       // mismo costo que un intento real
            derivar(clave, new byte[BYTES_SAL], ITERACIONES);
            return false;
        }
        try {
            String[] p = registro.split("\\$");
            byte[] sal = Base64.getDecoder().decode(p[1]);
            byte[] esperado = Base64.getDecoder().decode(p[2]);
            return MessageDigest.isEqual(esperado, derivar(clave, sal, Integer.parseInt(p[0])));
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] derivar(char[] clave, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(clave, sal, iteraciones, BITS_CLAVE);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new AutenticacionException("No se pudo procesar la contraseña.");
        } finally {
            spec.clearPassword();
        }
    }

    private void cargar() {
        if (!Files.exists(archivo)) return;
        try (Reader r = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            datos.load(r);
        } catch (IOException e) {
            datos.clear();
        }
    }

    private void guardar() {
        try {
            if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
            try (Writer w = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
                datos.store(w, "MathKit - usuarios (sal + hash, sin contraseñas)");
            }
        } catch (IOException e) {
            datos.clear();
            cargar();
            throw new AutenticacionException("No se pudo guardar el usuario en " + archivo + ".");
        }
    }
}
