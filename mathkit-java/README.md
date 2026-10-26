# mathkit (Java)

Librería modular de matemáticas. Java 17+ (usa `record` y `switch` con flechas).

| Clase | Contenido |
|---|---|
| `Basicas` | raíz n-ésima, potencia, factorial (BigInteger), MCD, MCM, primos |
| `Probabilidad` | permutaciones, combinaciones, Bayes, binomial, Poisson, normal |
| `Estadistica` | media, mediana, moda, varianza, desviación, correlación, regresión |
| `Numericos` | bisección, Newton-Raphson, secante, derivada, trapecio, Simpson, Euler |
| `Expresion` | evalúa texto como `x^2 - 2` o `sin(x)/x` en una función f(x) |
| `gui.*` | ventana de escritorio (Swing): `MathKitApp`, `VentanaPrincipal` y paneles |
| `Calculadora` | fachada que importa todo lo anterior y guarda historial |
| `errores.*` | `MathKitException`, `DominioException`, `ConvergenciaException`, `DatosInsuficientesException` |

## Ventana de escritorio
Ejecuta `MathKitApp` (clase `com.mathkit.gui.MathKitApp`). En VS Code: `F5` con la configuración
**MathKit (ventana)**. Desde terminal:
```bash
java -Dfile.encoding=UTF-8 -cp out com.mathkit.gui.MathKitApp
```
Módulos: Básicas, Probabilidad, Estadística y Numéricos, con historial a la derecha.
En Numéricos puedes escribir funciones como `x^2 - 2`, `sin(x)`, `sqrt(x)+ln(x)` o límites como `pi`.

## Inicio de sesión
Al ejecutar `MathKitApp` aparece primero el login (`VentanaLogin`):
- **Primera vez:** pulsa *Crear cuenta*, elige un usuario (3 a 20 letras sin tildes, números o `_`) y una contraseña de 6 o más caracteres.
- **Después:** escribe tu usuario y contraseña y pulsa *Entrar* (o `Enter`).
- **Cerrar sesión:** enlace al pie del menú lateral; vuelve al login.

Las contraseñas **no se guardan**: solo la sal y el hash PBKDF2-HMAC-SHA256 en
`~/.mathkit/usuarios.properties` (cambia la ruta con `-Dmathkit.usuarios=ruta`).
Clases: `Usuarios` (lógica, sin interfaz) y `gui.VentanaLogin` (diseño).

## Abrir en VS Code
1. Instala un **JDK 17 o superior** (no solo el JRE) y VS Code.
2. Abre la carpeta: `File > Open Folder...` y elige `mathkit-java`
   (o desde terminal: `code mathkit-java`).
3. Acepta instalar la extensión recomendada **Extension Pack for Java**.
4. Abre `Demo.java` y pulsa **Run** sobre `main`, o usa `F5` / el panel *Run and Debug*
   (hay dos configuraciones: *Demo (Calculadora)* y *Pruebas MathKit*).
5. Compilar con `Ctrl+Shift+B` (tarea *Compilar*).

## Compilar y ejecutar (terminal)
```bash
mkdir out
javac -encoding UTF-8 -d out $(find src test -name "*.java")
java -Dfile.encoding=UTF-8 -cp out com.mathkit.Demo
java -cp out com.mathkit.PruebasMathKit
```

## Generar un .jar reutilizable
```bash
jar cf mathkit.jar -C out com/mathkit
```
Luego, en otro proyecto: `javac -cp mathkit.jar MiClase.java`

## Uso
```java
import com.mathkit.*;

Calculadora calc = new Calculadora();
calc.raiz(144, 2);                       // 12.0
Estadistica.media(new double[]{1, 2, 3}); // 2.0
```


## Operaciones sin la clase `Math`

Se agregó `CalculosInternos`, que implementa aproximaciones propias para potencia, raíces, exponencial, logaritmo y funciones trigonométricas, además de utilidades como valor absoluto, mínimo y máximo. Las clases del proyecto ya no invocan métodos de `java.lang.Math`. Las constantes π y e se definen dentro de `CalculosInternos`. Estas implementaciones didácticas pueden tener diferencias de precisión frente a bibliotecas matemáticas estándar.
