# FASE 01: FUNDAMENTOS DE JAVA
**Roadmap Retrospectivo - Análisis del Código Existente**

---

## Contexto de esta Fase

Esta fase documenta los **fundamentos de Java** que utilizaste para construir las utilidades base del proyecto ForestechOil. En lugar de ser una guía teórica, este roadmap analiza **tu código real** línea por línea, explicando:

- ✅ **QUÉ** hiciste (clases helper, generadores, banners)
- ✅ **CÓMO** funciona el código (análisis detallado)
- ✅ **POR QUÉ** se diseñó así (decisiones de arquitectura)

---

## Objetivos de Aprendizaje

Al completar el análisis de esta fase, habrás reforzado:

1. **Variables y tipos de datos** (String, int, double, boolean)
2. **Métodos estáticos** (helpers reutilizables)
3. **Scanner** para entrada de usuario
4. **String manipulation** (substring, toUpperCase, format)
5. **UUID** para generación de IDs únicos
6. **Constantes** con `final static`

---

## Arquitectura de la Fase 01

```
com.forestech/
├── utils/
│   └── IdGenerator.java          # Generación de IDs únicos (UUID-based)
├── helpers/
│   ├── InputHelper.java          # Validación de entrada de usuario
│   ├── BannerMenu.java           # Display de headers ASCII
│   ├── MenuHelper.java           # Menús legacy (Fase 1)
│   └── DataDisplay.java          # Display de datos (legacy)
└── AppConfig.java                # Constantes globales del proyecto
```

---

## 1. GENERACIÓN DE IDs ÚNICOS

### Archivo: `utils/IdGenerator.java`

#### 1.1 Concepto: ¿Por qué necesitamos IDs?

En una aplicación de gestión, cada entidad (Movement, Product, Vehicle, Supplier) necesita un **identificador único** para:
- Diferenciar registros en la base de datos
- Facilitar búsquedas
- Mantener relaciones entre tablas (Foreign Keys)

#### 1.2 Análisis del Código

**Líneas 1-5: Declaración de la clase**
```java
package com.forestech.utils;

import java.util.UUID;

public class IdGenerator {
```

**Concepto clave:** Esta es una **clase de utilidad** (utility class):
- Solo contiene métodos `static`
- No se instancia (no tiene constructor público)
- Se llama directamente: `IdGenerator.generateMovementId()`

---

**Líneas 7-10: Método generateMovementId()**
```java
public static String generateMovementId() {
    return "MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}
```

**Análisis línea por línea:**

1. **`public static String`**: Método estático que retorna un String
2. **`UUID.randomUUID()`**: Genera un identificador único universal (ej: `a3f2c1d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6`)
3. **`.toString()`**: Convierte el UUID a String
4. **`.substring(0, 8)`**: Toma solo los primeros 8 caracteres (`a3f2c1d4`)
5. **`.toUpperCase()`**: Convierte a mayúsculas (`A3F2C1D4`)
6. **`"MOV-" +`**: Añade prefijo identificador del tipo de entidad

**Resultado final:** `MOV-A3F2C1D4`

---

**Ventajas de este diseño:**
- ✅ **Unicidad garantizada** por UUID
- ✅ **Legibilidad** por el prefijo descriptivo
- ✅ **Corto** (solo 8 caracteres alfanuméricos)
- ✅ **Trazabilidad** (sabes que MOV- es un Movement)

**Desventajas:**
- ❌ **No cronológico** (UUIDs son aleatorios)
- ❌ **No secuencial** (dificulta saber orden de creación)

---

**Líneas 12-15: Método con timestamp (alternativa)**
```java
public static String generateMovementIdWithTimestamp() {
    long timestamp = System.currentTimeMillis() / 1000;
    String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    return "MOV-" + timestamp + "-" + randomPart;
}
```

**Innovación:** Este método SÍ es cronológico porque incluye el timestamp.

**Resultado:** `MOV-1234567890-AB12`

**Cuándo usar cada uno:**
- `generateMovementId()`: Para IDs simples sin importar orden temporal
- `generateMovementIdWithTimestamp()`: Para auditoría o cuando el orden de creación es importante

---

**Líneas 17-27: Generadores para otras entidades**
```java
public static String generateSupplierId() {
    return "PROV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}

public static String generateFuelId() {
    return "FUEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}

public static String generateVehicleId() {
    return "VEH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
}
```

**Patrón de diseño:** Cada entidad tiene su propio método generador con prefijo único:
- `PROV-`: Proveedores (Suppliers)
- `FUEL-`: Productos (Combustibles)
- `VEH-`: Vehículos

**Ventaja del patrón:** Al ver un ID como `VEH-F3A2C1D4`, inmediatamente sabes que es un vehículo.

---

### 1.3 Uso en el Proyecto

Este generador se usa en los **constructores CREATE** de los modelos:

**Ejemplo en Movement.java:92**
```java
this.id = IdGenerator.generateMovementId();
```

**Ejemplo en Product.java:25**
```java
this.id = IdGenerator.generateFuelId();
```

---

### 1.4 Checkpoint de Verificación ✅

Para verificar que comprendes IdGenerator:

1. Explica con tus palabras: ¿Por qué usamos `UUID.randomUUID()` en lugar de un contador `int`?
2. Ejecuta en tu mente: ¿Qué retorna `generateSupplierId()`? (Formato exacto)
3. Compara: ¿Cuál es la diferencia entre `generateMovementId()` y `generateMovementIdWithTimestamp()`?

---

## 2. VALIDACIÓN DE ENTRADA DE USUARIO

### Archivo: `helpers/InputHelper.java`

#### 2.1 Concepto: ¿Por qué necesitamos validar entradas?

Cuando un usuario escribe en la consola, pueden ocurrir errores:
- Usuario escribe "abc" cuando esperas un número
- Usuario presiona Enter sin escribir nada
- Scanner deja basura en el buffer

**InputHelper** centraliza la validación para evitar código repetitivo.

---

#### 2.2 Análisis del Código

**Líneas 1-8: Importaciones y Scanner estático**
```java
package com.forestech.helpers;

import java.util.Scanner;

public class InputHelper {
    private static Scanner scanner = new Scanner(System.in);
}
```

**Decisión de diseño:** Scanner es `static` porque:
- Solo necesitas UNA instancia para toda la aplicación
- Se comparte entre todos los métodos
- Evita crear múltiples Scanners (memoria innecesaria)

**⚠️ PROBLEMA DE DISEÑO:**
- El Scanner nunca se cierra (`scanner.close()`)
- En aplicaciones CLI es aceptable, pero en aplicaciones grandes podría causar resource leak

---

**Líneas 10-14: readInt() - Leer enteros**
```java
public static int readInt(String prompt) {
    System.out.print(prompt);
    int value = scanner.nextInt();
    scanner.nextLine(); // Limpiar buffer
    return value;
}
```

**Análisis línea por línea:**

1. **`System.out.print(prompt)`**: Muestra el mensaje al usuario (ej: "Ingrese cantidad: ")
2. **`scanner.nextInt()`**: Lee el número entero
3. **`scanner.nextLine()`**: **CRÍTICO** - Limpia el salto de línea (\n) que queda en el buffer
4. **`return value`**: Retorna el número leído

**¿Por qué limpiar el buffer?**

Sin `scanner.nextLine()`:
```
Usuario escribe: 5[ENTER]
nextInt() lee: 5
Buffer queda: [ENTER]
Próximo readString() lee: [ENTER] (¡vacío!)
```

Con `scanner.nextLine()`:
```
Usuario escribe: 5[ENTER]
nextInt() lee: 5
nextLine() consume: [ENTER]
Buffer queda: (vacío)
Próximo readString() funciona correctamente
```

---

**Líneas 16-20: readDouble() - 🐛 BUG CRÍTICO DETECTADO**
```java
public static double readDouble(String prompt) {
    System.out.print(prompt);
    double value = scanner.nextInt(); // ❌ ERROR: debería ser nextDouble()
    scanner.nextLine();
    return value;
}
```

**🔴 BUG:** La línea 18 usa `nextInt()` en lugar de `nextDouble()`

**Impacto:**
- Si usuario escribe `15.5`, el programa crasheará con `InputMismatchException`
- Si usuario escribe `15`, funciona pero se pierde precisión decimal

**✅ Corrección requerida:**
```java
double value = scanner.nextDouble(); // Cambiar nextInt() por nextDouble()
```

---

**Líneas 22-26: readString() - Leer texto**
```java
public static String readString(String prompt) {
    System.out.print(prompt);
    return scanner.nextLine();
}
```

**Nota:** Este método SÍ está correcto. `nextLine()` lee toda la línea hasta el Enter.

---

**Líneas 28-43: readFuelType() - Legacy method**
```java
public static String readFuelType() {
    System.out.println("\n=== SELECCIONE TIPO DE COMBUSTIBLE ===");
    System.out.println("1. Gasolina Corriente");
    System.out.println("2. Gasolina Extra");
    // ... más opciones

    int option = readInt("Opción: ");

    switch (option) {
        case 1: return "Gasolina Corriente";
        case 2: return "Gasolina Extra";
        // ... más casos
        default: return "Desconocido";
    }
}
```

**Estado actual:** Este método parece legacy (no usado en AppController actual).

**Razón:** El proyecto ahora usa `ProductServices` para seleccionar productos desde la base de datos, no tipos hardcodeados.

---

### 2.3 Uso en el Proyecto

InputHelper se usa extensivamente en **AppController.java**:

**Ejemplo en AppController.java:145**
```java
double cantidad = InputHelper.readDouble("Cantidad: ");
```

**Ejemplo en AppController.java:189**
```java
String nombreProducto = InputHelper.readString("Nombre del producto: ");
```

---

### 2.4 Ejercicio de Corrección 🔧

**TAREA:** Corrige el bug en `readDouble()`

1. Abre `forestech-cli-java/src/main/java/com/forestech/helpers/InputHelper.java`
2. Ve a la línea 18
3. Cambia `scanner.nextInt()` por `scanner.nextDouble()`
4. Compila: `mvn clean compile`
5. Verifica que no hay errores

**Verificación:**
```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
# Debería compilar sin errores
```

---

### 2.5 Checkpoint de Verificación ✅

1. ¿Por qué necesitamos `scanner.nextLine()` después de `nextInt()`?
2. ¿Qué pasa si escribes "15.5" en `readInt()` sin corregir?
3. ¿Por qué Scanner es `static` en esta clase?
4. ¿Qué método usarías para leer el nombre de un proveedor?

---

## 3. CONFIGURACIÓN GLOBAL DEL PROYECTO

### Archivo: `AppConfig.java`

#### 3.1 Concepto: Constantes vs Variables

**Constante:** Valor que NUNCA cambia durante la ejecución
**Variable:** Valor que PUEDE cambiar

**Ventajas de usar constantes:**
- ✅ Un solo lugar para cambiar valores (ej: IVA)
- ✅ Evita "magic numbers" en el código
- ✅ Autocompletado del IDE
- ✅ Previene errores de tipeo

---

#### 3.2 Análisis del Código

**Líneas 1-5: Declaración de constantes**
```java
package com.forestech;

public class AppConfig {
    public static final String PROJECT_NAME = "Gestion e inventario de combustibles FORESTECH";
    public static final String DATABASE = "DBforestech";
    public static final int CURRENT_YEAR = 2025;
    public static final double VERSION = 1.0;
    public static final boolean ACTIVE = true;
    public static final double IVA_RATE = 0.19; // 19% IVA Colombia
}
```

**Análisis de modificadores:**

- **`public`**: Accesible desde cualquier clase
- **`static`**: Pertenece a la clase, no a instancias
- **`final`**: No se puede modificar después de la asignación

**Combinación `public static final`:**
```
AppConfig.IVA_RATE  ←  Se accede directamente desde la clase
          ↑                Sin necesidad de instanciar
          No se puede cambiar
```

---

#### 3.3 Uso en el Proyecto

**Ejemplo 1: Cálculo de IVA en Movement.java:138**
```java
public double getIva() {
    return getSubtotalvalue() * AppConfig.IVA_RATE;
}
```

**Ventaja:** Si el IVA cambia en Colombia (ej: a 21%), solo cambias una línea en AppConfig.

**Ejemplo 2: Banner en BannerMenu.java**
```java
System.out.println("PROYECTO: " + AppConfig.PROJECT_NAME);
System.out.println("AÑO: " + AppConfig.CURRENT_YEAR);
```

---

#### 3.4 Convención de Nombres

**Constantes en Java:**
- Todo en MAYÚSCULAS
- Palabras separadas por guión bajo `_`
- Ejemplos: `IVA_RATE`, `PROJECT_NAME`, `MAX_RETRIES`

**Variables normales:**
- camelCase
- Ejemplos: `productName`, `totalWithIva`, `fechaEmision`

---

### 3.5 Checkpoint de Verificación ✅

1. ¿Qué pasa si intentas hacer `AppConfig.IVA_RATE = 0.21;`? (¿Compila?)
2. Si el IVA pasa a 21%, ¿cuántos archivos tienes que modificar?
3. ¿Por qué `CURRENT_YEAR` es `int` y no `String`?

---

## 4. BANNERS Y HEADERS ASCII

### Archivo: `helpers/BannerMenu.java`

#### 4.1 Concepto: User Experience en CLI

Aunque es una aplicación de consola, la experiencia de usuario importa:
- Headers claros separan secciones
- Información del proyecto al inicio
- Feedback visual al usuario

---

#### 4.2 Análisis del Código

**Método header()**
```java
public static void header() {
    System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
    System.out.println("║          🛢️  FORESTECH OIL - GESTIÓN DE COMBUSTIBLES 🛢️       ║");
    System.out.println("╠══════════════════════════════════════════════════════════════╣");
    System.out.println("║  Proyecto: " + AppConfig.PROJECT_NAME);
    System.out.println("║  Base de Datos: " + AppConfig.DATABASE);
    System.out.println("║  Año: " + AppConfig.CURRENT_YEAR + " | Versión: " + AppConfig.VERSION);
    System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
}
```

**Técnicas utilizadas:**
1. **Box drawing characters:** ╔ ═ ╗ ║ ╚ ╝
2. **Emojis:** 🛢️ (barril de petróleo)
3. **Interpolación de constantes:** `AppConfig.PROJECT_NAME`

---

#### 4.3 Uso en el Proyecto

**Llamado en AppController.java:33**
```java
public void mostrarBienvenida() {
    BannerMenu.header();
    System.out.println("Bienvenido al sistema de gestión de combustibles");
}
```

---

## 5. HELPERS LEGACY (MenuHelper y DataDisplay)

### 5.1 MenuHelper.java - Estado: Legacy/Deprecated

Este archivo contiene la **primera versión del menú** (Fase 1):

**Métodos principales:**
- `displayMenu()`: Muestra menú simple con opciones 1-5
- `processMenuOption(int)`: Switch case básico

**Estado actual:** Reemplazado por `AppController.java` que tiene menús anidados más sofisticados.

**Decisión de diseño:** Se mantiene en el codebase como referencia del progreso de aprendizaje (de menú simple a arquitectura MVC).

---

### 5.2 DataDisplay.java - Estado: Legacy

**Método único:**
```java
public static void showFuelTypes() {
    System.out.println("=== TIPOS DE COMBUSTIBLE ===");
    System.out.println("- Gasolina Corriente");
    System.out.println("- Gasolina Extra");
    // ... más tipos hardcodeados
}
```

**Estado actual:** Obsoleto. Ahora se usan `ProductServices.getAllProducts()` para obtener productos dinámicamente desde la base de datos.

---

## 6. RESUMEN DE LA FASE 01

### 6.1 Conceptos Clave Implementados

| Concepto | Archivo | Líneas Clave |
|----------|---------|--------------|
| Métodos estáticos | IdGenerator.java | 7-27 |
| UUID y String manipulation | IdGenerator.java | 8 |
| Scanner y buffer management | InputHelper.java | 13 |
| Constantes con final | AppConfig.java | 4-9 |
| Box drawing characters | BannerMenu.java | 3-10 |

---

### 6.2 Patrones de Diseño Identificados

1. **Utility Class Pattern:** Clases con solo métodos estáticos (IdGenerator, InputHelper)
2. **Constants Class Pattern:** Clase con solo constantes públicas (AppConfig)
3. **Separation of Concerns:** Helpers separados por responsabilidad (Input vs Display vs Generation)

---

### 6.3 Bugs y Mejoras Pendientes

| Bug/Mejora | Archivo | Línea | Prioridad | Corrección |
|------------|---------|-------|-----------|------------|
| readDouble usa nextInt | InputHelper.java | 18 | 🔴 ALTA | Cambiar a nextDouble() |
| Scanner nunca se cierra | InputHelper.java | 6 | 🟡 MEDIA | Agregar método cleanup() |
| Credenciales hardcoded | DatabaseConnection.java | N/A | 🟡 MEDIA | Usar config.properties |

---

### 6.4 Próximos Pasos

Con estos fundamentos sólidos, la **Fase 02** construye sobre ellos para crear:
- **Clases de dominio** (Movement, Product, Vehicle, Supplier)
- **Encapsulación** con getters/setters
- **Constructores sobrecargados** (CREATE vs LOAD)
- **Métodos de negocio** (cálculos de IVA, subtotales)

---

### 6.5 Ejercicio Final de la Fase 01 🎯

**Tarea completa:**

1. **Corrige el bug en InputHelper.java** (readDouble)
2. **Compila el proyecto:** `mvn clean compile`
3. **Ejecuta:** `mvn exec:java -Dexec.mainClass="com.forestech.Main"`
4. **Prueba registrar una ENTRADA** y verifica que la cantidad decimal funciona
5. **Commit tus cambios:**
   ```bash
   git add forestech-cli-java/src/main/java/com/forestech/helpers/InputHelper.java
   git commit -m "Fix bug: readDouble() ahora usa nextDouble() correctamente"
   ```

---

### 6.6 Autoevaluación ✅

Responde sin mirar el código:

1. ¿Qué formato tiene un ID de vehículo generado con IdGenerator?
2. ¿Por qué AppConfig.IVA_RATE es `final`?
3. ¿Qué hace `scanner.nextLine()` después de `nextInt()`?
4. ¿Cuál es la diferencia entre `public static` y `public static final`?
5. ¿Por qué MenuHelper.java está obsoleto?

**Si respondiste 4/5 correctamente:** ✅ Listo para Fase 02
**Si respondiste menos de 4:** 🔄 Repasa las secciones marcadas

---

## 7. RECURSOS Y REFERENCIAS

### 7.1 Archivos Clave para Revisar

- `forestech-cli-java/src/main/java/com/forestech/utils/IdGenerator.java`
- `forestech-cli-java/src/main/java/com/forestech/helpers/InputHelper.java`
- `forestech-cli-java/src/main/java/com/forestech/AppConfig.java`
- `forestech-cli-java/src/main/java/com/forestech/helpers/BannerMenu.java`

### 7.2 Comandos de Compilación

```bash
cd /home/hp/forestechOil/forestech-cli-java

# Compilar
mvn clean compile

# Ejecutar
mvn exec:java -Dexec.mainClass="com.forestech.Main"

# Ver estructura del proyecto
tree src/main/java/com/forestech
```

### 7.3 Documentación Java Relevante

- **Scanner:** https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Scanner.html
- **UUID:** https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html
- **String methods:** substring, toUpperCase, format

---

**🎓 Fase 01 Completada**

Has construido los cimientos del proyecto ForestechOil. Estos helpers y utilities te permiten:
- ✅ Generar IDs únicos para todas las entidades
- ✅ Leer entrada de usuario de forma segura
- ✅ Mantener configuración centralizada
- ✅ Mostrar información clara al usuario

**Siguiente:** [FASE_02_POO_MODELOS.md](./FASE_02_POO_MODELOS.md) - Análisis de las clases de dominio (Movement, Product, Vehicle, Supplier, Factura)
