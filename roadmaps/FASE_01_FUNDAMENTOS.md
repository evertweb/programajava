# 🚀 FASE 1: FUNDAMENTOS DE JAVA (Semanas 1-2)

> Objetivo general: dominar la sintaxis base de Java y los patrones de control que usarás durante todo el proyecto Forestech CLI.

---

## 🧠 Antes de empezar

- 🐞 **Rutina de depuración:** en cada checkpoint detente al menos una vez para ejecutar el código en modo debug, inspeccionar variables y usar `step over`/`step into`.
- 🗒️ **Notas rápidas:** al cierre de cada checkpoint anota en `JAVA_LEARNING_LOG.md` qué entendiste, qué dudas persisten y qué probarás mañana.
- 🔁 **Git loop:** al completar cada checkpoint crea un commit con mensaje claro (`git commit -m "fase1 checkpoint 1.2"`).
- 🎯 **NUEVO - Organización desde el inicio:** Cada checkpoint te dirá **EXACTAMENTE** en qué clase escribir el código para evitar acumulación en Main.java

---

## ✅ Checkpoint 1.1: Primer Programa y Clase de Configuración

**Concepto clave:** En Java el método `main(String[] args)` es el punto de entrada obligatorio. Las constantes de configuración deben estar separadas del código de ejecución.

**📍 DÓNDE:** 
- **Archivo 1:** `Main.java` en `forestech-cli-java/src/main/java/com/forestech/`
- **Archivo 2 (NUEVO):** `AppConfig.java` en `forestech-cli-java/src/main/java/com/forestech/`

**🎯 PARA QUÉ:** 
Desde el inicio aprenderás a **separar responsabilidades**:
- **Main.java**: Solo el método `main()` que arranca la aplicación y llama a otros métodos
- **AppConfig.java**: Las constantes de configuración del sistema (nombre, versión, año, etc.)

Esto evita que Main.java se llene de código mezclado y te prepara para la POO en Fase 2.

**🔗 CONEXIÓN FUTURA:** 
- El `main()` luego invocará el menú principal (Fase 6)
- AppConfig se convertirá en una clase de configuración más completa (Fase 8)
- Aprenderás que separar datos de lógica es un principio fundamental

**Prompts sugeridos:**
```text
"Estoy en Checkpoint 1.1. Explícame con analogía qué es el método main()."
"¿Qué significa cada palabra en public static void main(String[] args)?"
"¿Por qué es buena práctica separar constantes en otra clase?"
```

**Tareas paso a paso:**

1. **Crear AppConfig.java:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Java Class → "AppConfig"
   - Declarar como clase pública
   - Declarar constantes públicas estáticas:
     ```java
     public static final String APP_NAME = "Forestech CLI - Gestión de Combustibles";
     public static final String VERSION = "1.0";
     public static final int YEAR = 2025;
     public static final String DATABASE = "FORESTECH";
     ```
   - **¿Por qué `public static final`?** Lo aprenderás en detalle en Fase 2, por ahora entiende:
     - `public`: Otras clases pueden ver estas constantes
     - `static`: No necesitas crear un objeto AppConfig para usarlas
     - `final`: El valor NUNCA cambiará (es constante)

2. **Crear/Verificar Main.java:**
   - Debe tener el método `main(String[] args)`
   - **SOLO ESCRIBE EN MAIN:** Líneas que imprimen información usando las constantes de AppConfig
   - Ejemplo:
     ```java
     System.out.println("=================================");
     System.out.println("Bienvenido a " + AppConfig.APP_NAME);
     System.out.println("Versión: " + AppConfig.VERSION);
     System.out.println("=================================");
     ```

3. **Compilar desde terminal:**
   - Navega a `forestech-cli-java/`
   - Ejecuta: `mvn clean compile`

4. **Ejecutar:**
   - Desde IntelliJ: clic derecho en Main.java → Run
   - Desde terminal: `mvn exec:java -Dexec.mainClass="com.forestech.Main"`

**✅ Resultado esperado:** 
- Ver mensaje de bienvenida con datos de AppConfig
- Dos archivos Java creados y organizados
- Compilación y ejecución exitosa

**💡 Concepto clave:** Main.java debe ser CORTO y SIMPLE. Las constantes van en AppConfig, la lógica irá en otras clases.

**⏱️ Tiempo estimado:** 1-2 horas

---

## ✅ Checkpoint 1.2: Variables y Cálculos Básicos de Movimientos

**Concepto clave:** Diferencia entre tipos primitivos (int, double, boolean) y de referencia (String); realizar cálculos financieros básicos.

**📍 DÓNDE:** 
- **Crear nueva clase:** `MovementCalculator.java` en `forestech-cli-java/src/main/java/com/forestech/`
- **Main.java:** Solo para PROBAR el MovementCalculator (llamar a sus métodos)

**🎯 PARA QUÉ:** 
En lugar de acumular variables y cálculos en Main, crearás una clase especializada en cálculos de movimientos:
- **MovementCalculator**: Métodos que calculan subtotal, IVA, total
- **Main**: Solo prueba estos métodos

Esto te enseña desde YA que cada clase tiene una **responsabilidad específica**.

**🔗 CONEXIÓN FUTURA:** 
- En Fase 2, MovementCalculator se integrará con la clase Movement (POO)
- Los cálculos se volverán métodos de instancia (aprenderás la diferencia)
- Esta lógica se usará en transacciones reales (Fase 4-5)

**Prompts sugeridos:**
```text
"¿Por qué existe int e Integer? Dame ejemplos con movimientos de combustible."
"¿Cuándo usar double vs int para cantidades en Forestech?"
"¿Qué significa 'static' en un método? (nivel básico)"
```

**Tareas paso a paso:**

1. **Crear MovementCalculator.java:**
   - Clase pública
   - Declarar constante: `public static final double IVA_RATE = 0.19;`

2. **Crear método estático `calculateSubtotal`:**
   ```java
   public static double calculateSubtotal(double quantity, double unitPrice) {
       // Por ahora solo métodos estáticos (aprenderás POO en Fase 2)
       return quantity * unitPrice;
   }
   ```

3. **Crear método estático `calculateIVA`:**
   ```java
   public static double calculateIVA(double subtotal) {
       return subtotal * IVA_RATE;
   }
   ```

4. **Crear método estático `calculateTotal`:**
   ```java
   public static double calculateTotal(double subtotal, double iva) {
       return subtotal + iva;
   }
   ```

5. **Crear método estático `printMovementSummary`:**
   - Recibe: tipo de combustible, cantidad, precio unitario
   - Imprime resumen formateado con todos los cálculos
   - Usa los métodos anteriores para calcular

6. **En Main.java (método main):**
   ```java
   // SOLO PRUEBAS - no acumules lógica aquí
   System.out.println("\n=== PRUEBA DE CÁLCULOS ===");
   MovementCalculator.printMovementSummary("Diesel", 100.0, 3.45);
   ```

**✅ Resultado esperado:** 
- Ver resumen de movimiento con subtotal, IVA y total calculados
- Main.java sigue corto (menos de 30 líneas)
- Los cálculos están organizados en MovementCalculator

**💡 Concepto clave:** Aunque aún no entiendes POO completa, ya estás aplicando el principio de "cada clase tiene una tarea específica".

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 1.3: Validaciones y Operadores Lógicos

**Concepto clave:** Usar operadores lógicos (&&, ||, !) para validar datos antes de procesarlos.

**📍 DÓNDE:** 
- **Clase existente:** `MovementCalculator.java` (agregar nuevos métodos)
- **Main.java:** Solo para probar las validaciones

**🎯 PARA QUÉ:** 
Antes de calcular o guardar datos, debes **validar** que sean correctos:
- Cantidad > 0
- Precio > 0
- Tipo de combustible válido
- Combinaciones lógicas (ej: si es entrada Y es Diesel Y cantidad > 50)

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre / y % ? Dame ejemplos con Forestech."
"¿Por qué debo usar .equals() en vez de == para comparar Strings?"
"Explícame los operadores && y || con ejemplos de validaciones."
```

**Tareas paso a paso:**

1. **En MovementCalculator.java, agregar método:**
   ```java
   public static boolean isValidMovement(String fuelType, double quantity, double unitPrice) {
       // Valida que todos los datos sean correctos
       // Retorna true si es válido, false si no
   }
   ```

2. **Agregar método:**
   ```java
   public static boolean isBigPurchase(double quantity) {
       return quantity >= 100;
   }
   ```

3. **Agregar método con lógica compleja:**
   ```java
   public static boolean requiresApproval(String type, double quantity, double total) {
       // Combina varias condiciones con && y ||
       // Ej: requiere aprobación si es SALIDA Y (cantidad > 100 O total > 500)
   }
   ```

4. **En Main.java, probar:**
   ```java
   // Prueba varios casos
   boolean valid1 = MovementCalculator.isValidMovement("Diesel", 100, 3.45);
   boolean valid2 = MovementCalculator.isValidMovement("", -10, 0);
   
   System.out.println("Movimiento 1 válido: " + valid1);
   System.out.println("Movimiento 2 válido: " + valid2);
   ```

**✅ Resultado esperado:** 
- Validaciones funcionando correctamente
- Main.java sigue limpio (solo pruebas)
- Entiendes la diferencia entre `==` y `.equals()` para Strings

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 1.4: Control de Flujo y Menú Básico

**Concepto clave:** Usar if/else y switch para tomar decisiones; crear un menú de opciones.

**📍 DÓNDE:** 
- **Crear nueva clase:** `MenuHelper.java` en `forestech-cli-java/src/main/java/com/forestech/`
- **Main.java:** Solo para invocar el menú

**🎯 PARA QUÉ:** 
El menú es el corazón de tu aplicación CLI. En lugar de ponerlo en Main, crearás MenuHelper:
- **MenuHelper**: Métodos para mostrar opciones y procesar elecciones
- **Main**: Solo llama a MenuHelper

**🔗 CONEXIÓN FUTURA:** 
- En Fase 6, MenuHelper evolucionará a ConsoleMenu con bucles y Scanner
- Por ahora solo simulas el menú con variables

**Prompts sugeridos:**
```text
"¿Cuándo debo preferir switch sobre múltiples if-else?"
"Muéstrame con ejemplo por qué comparar Strings con == puede fallar."
"¿Qué pasa si olvido el 'break' en un case de switch?"
```

**Tareas paso a paso:**

1. **Crear MenuHelper.java:**
   ```java
   public class MenuHelper {
       
       public static void displayMainMenu() {
           // Imprime las opciones del menú
           System.out.println("\n========== MENÚ PRINCIPAL ==========");
           System.out.println("1. Registrar Entrada de Combustible");
           System.out.println("2. Registrar Salida de Combustible");
           System.out.println("3. Ver Inventario");
           System.out.println("4. Salir");
           System.out.println("====================================");
       }
       
       public static void processMenuOption(int option) {
           // Usa switch para procesar la opción
           switch(option) {
               case 1:
                   System.out.println("→ Módulo de ENTRADA seleccionado");
                   break;
               case 2:
                   System.out.println("→ Módulo de SALIDA seleccionado");
                   break;
               case 3:
                   System.out.println("→ Módulo de INVENTARIO seleccionado");
                   break;
               case 4:
                   System.out.println("→ Saliendo del sistema...");
                   break;
               default:
                   System.out.println("❌ Opción inválida");
           }
       }
   }
   ```

2. **Crear método para validar tipo de movimiento:**
   ```java
   public static void validateMovementType(String type) {
       // Usa if/else para validar
       if (type.equals("ENTRADA")) {
           System.out.println("✓ Inventario AUMENTARÁ");
       } else if (type.equals("SALIDA")) {
           System.out.println("✓ Inventario DISMINUIRÁ");
       } else {
           System.out.println("✗ Tipo de movimiento inválido");
       }
   }
   ```

3. **En Main.java:**
   ```java
   // Mostrar y simular menú
   MenuHelper.displayMainMenu();
   
   // Simular selección de usuario (sin Scanner aún)
   int opcionUsuario = 1;
   MenuHelper.processMenuOption(opcionUsuario);
   
   // Validar tipo de movimiento
   MenuHelper.validateMovementType("ENTRADA");
   ```

**✅ Resultado esperado:** 
- Ver menú formateado
- Ver procesamiento de opciones
- Main.java tiene menos de 20 líneas
- Todo organizado por responsabilidades

**💡 Recuerda:** Usa `.equals()` para comparar Strings, NUNCA `==`

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 1.5: Bucles y Arrays de Datos

**Concepto clave:** Repetir operaciones con for, while, do-while; almacenar múltiples valores en arrays.

**📍 DÓNDE:** 
- **Crear nueva clase:** `DataDisplay.java` en `forestech-cli-java/src/main/java/com/forestech/`
- **Main.java:** Solo para invocar los métodos de visualización

**🎯 PARA QUÉ:** 
Necesitas mostrar listas de datos (productos, opciones, resultados). DataDisplay se encargará de:
- Mostrar arrays formateados
- Iterar sobre colecciones
- Formatear tablas simples

**Prompts sugeridos:**
```text
"Compárame for, while y do-while con ejemplos de la vida real."
"¿Qué es un índice de array y por qué empieza en 0?"
"¿Cuándo usar for clásico vs for-each?"
```

**Tareas paso a paso:**

1. **Crear DataDisplay.java:**
   ```java
   public class DataDisplay {
       
       public static void showFuelTypes() {
           String[] fuelTypes = {"Diesel", "Gasolina 93", "Gasolina 95", "ACPM"};
           
           System.out.println("\n=== TIPOS DE COMBUSTIBLE ===");
           for (int i = 0; i < fuelTypes.length; i++) {
               System.out.println((i + 1) + ". " + fuelTypes[i]);
           }
       }
       
       public static void showMenuWithForEach(String[] options) {
           int counter = 1;
           for (String option : options) {
               System.out.println(counter + ". " + option);
               counter++;
           }
       }
       
       public static void simulateProcessing(int totalMovements) {
           System.out.println("\n=== PROCESANDO MOVIMIENTOS ===");
           int processed = 0;
           while (processed < totalMovements) {
               processed++;
               System.out.println("Procesando movimiento #" + processed);
           }
           System.out.println("✓ Total procesado: " + processed);
       }
   }
   ```

2. **En Main.java:**
   ```java
   // Mostrar tipos de combustible
   DataDisplay.showFuelTypes();
   
   // Mostrar menú con array
   String[] menuOptions = {"Registrar Entrada", "Registrar Salida", "Ver Inventario", "Salir"};
   DataDisplay.showMenuWithForEach(menuOptions);
   
   // Simular procesamiento
   DataDisplay.simulateProcessing(5);
   ```

**✅ Resultado esperado:** 
- Ver listas formateadas
- Entender la diferencia entre los tipos de bucles
- Main.java sigue corto

**⚠️ CUIDADO:** En while, siempre asegúrate de que la condición eventualmente sea false (evita loops infinitos)

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✅ Checkpoint 1.6: Entrada de Usuario con Scanner

**Concepto clave:** Convertir tu aplicación de estática (datos hardcodeados) a interactiva (usuario ingresa datos).

**📍 DÓNDE:** 
- **Crear nueva clase:** `InputHelper.java` en `forestech-cli-java/src/main/java/com/forestech/`
- **Main.java:** Solo para demostrar el flujo interactivo completo

**🎯 PARA QUÉ:** 
Esta clase centralizará TODA la captura de datos del usuario:
- **InputHelper**: Métodos para leer números, texto, validar entradas
- **Main**: Solo orquesta el flujo (muestra menú → lee opción → procesa)

Esto evita repetir código de Scanner en todas partes y maneja el problema del buffer en UN SOLO LUGAR.

**🔗 CONEXIÓN FUTURA:** 
- En Fase 6, InputHelper se integrará con ConsoleMenu
- En Fase 7, agregarás manejo de excepciones aquí
- Este patrón de "clase helper para inputs" es profesional

**Prompts sugeridos:**
```text
"Explícame paso a paso qué hace new Scanner(System.in)."
"¿Por qué necesito scanner.nextLine() después de nextInt()? Explícame el buffer."
"¿Cómo valido que el usuario ingresó un número y no texto?"
```

**Tareas paso a paso:**

1. **Crear InputHelper.java:**
   ```java
   import java.util.Scanner;
   
   public class InputHelper {
       private static Scanner scanner = new Scanner(System.in);
       
       public static int readInt(String prompt) {
           System.out.print(prompt);
           int value = scanner.nextInt();
           scanner.nextLine(); // LIMPIAR BUFFER - MUY IMPORTANTE
           return value;
       }
       
       public static double readDouble(String prompt) {
           System.out.print(prompt);
           double value = scanner.nextDouble();
           scanner.nextLine(); // LIMPIAR BUFFER
           return value;
       }
       
       public static String readString(String prompt) {
           System.out.print(prompt);
           return scanner.nextLine();
       }
       
       public static String readFuelType() {
           DataDisplay.showFuelTypes();
           int option = readInt("Seleccione tipo de combustible: ");
           
           switch(option) {
               case 1: return "Diesel";
               case 2: return "Gasolina 93";
               case 3: return "Gasolina 95";
               case 4: return "ACPM";
               default: return "Diesel"; // Por defecto
           }
       }
   }
   ```

2. **En Main.java, crear flujo completo interactivo:**
   ```java
   public static void main(String[] args) {
       // Bienvenida
       System.out.println("=================================");
       System.out.println("Bienvenido a " + AppConfig.APP_NAME);
       System.out.println("=================================");
       
       // Mostrar menú
       MenuHelper.displayMainMenu();
       
       // Leer opción del usuario
       int option = InputHelper.readInt("Seleccione una opción: ");
       MenuHelper.processMenuOption(option);
       
       // Si eligió opción 1 o 2, capturar datos de movimiento
       if (option == 1 || option == 2) {
           System.out.println("\n=== CAPTURA DE DATOS ===");
           
           String fuelType = InputHelper.readFuelType();
           double quantity = InputHelper.readDouble("Ingrese cantidad (litros): ");
           double unitPrice = InputHelper.readDouble("Ingrese precio unitario: $");
           
           // Validar
           if (MovementCalculator.isValidMovement(fuelType, quantity, unitPrice)) {
               System.out.println("\n✓ Datos válidos");
               MovementCalculator.printMovementSummary(fuelType, quantity, unitPrice);
           } else {
               System.out.println("\n✗ Datos inválidos");
           }
       }
       
       System.out.println("\n=== FIN DEL PROGRAMA ===");
   }
   ```

**✅ Resultado esperado:** 
- El programa muestra el menú y ESPERA tu input
- Puedes ingresar datos reales
- Se calculan y muestran los resultados
- Main.java es ORQUESTADOR (llama a otros, no hace todo él mismo)

**💡 PATRÓN PROFESIONAL logrado:**
```
Main.java → solo coordina el flujo
AppConfig → constantes del sistema  
MovementCalculator → lógica de cálculos
MenuHelper → lógica del menú
DataDisplay → visualización de datos
InputHelper → captura de inputs
```

**⚠️ PROBLEMA COMÚN - EL BUFFER:**
Cuando usas `nextInt()` o `nextDouble()`, el Enter que presionas queda en el buffer. Si después usas `nextLine()`, este captura ese Enter vacío. **Solución:** Siempre usa un `nextLine()` extra después de leer números (ya implementado en InputHelper).

**🔍 Depuración:** 
Si el programa salta preguntas, el problema es el buffer. Verifica que cada `nextInt()` y `nextDouble()` tengan su `nextLine()` después.

**⏱️ Tiempo estimado:** 3 horas

---

## 🧾 Checklist de salida de Fase 1

Antes de pasar a Fase 2, verifica que puedes responder SÍ a todo:

**Comprensión de conceptos:**
- [ ] Puedo explicar con mis palabras qué es el método `main()` y por qué es necesario
- [ ] Entiendo la diferencia entre `int` e `Integer`, `double` y `Double`
- [ ] Sé cuándo usar `final` y qué significa "inmutable"
- [ ] Puedo explicar por qué `==` no funciona bien con Strings
- [ ] Entiendo cuándo usar `if/else` vs `switch`
- [ ] Sé la diferencia entre `while` y `do-while`
- [ ] Entiendo qué es el buffer de Scanner y cómo limpiarlo
- [ ] Entiendo por qué separé el código en múltiples clases

**Habilidades prácticas:**
- [ ] Compilé y ejecuté código desde terminal con Maven
- [ ] Usé el debugger al menos 3 veces con breakpoints
- [ ] Creé commits de Git por cada checkpoint completado
- [ ] Documenté aprendizajes en `JAVA_LEARNING_LOG.md`

**Estructura del proyecto:**
- [ ] Tengo Main.java CORTO (menos de 40 líneas)
- [ ] Tengo AppConfig.java con las constantes
- [ ] Tengo MovementCalculator.java con los cálculos
- [ ] Tengo MenuHelper.java con la lógica del menú
- [ ] Tengo DataDisplay.java con visualización de datos
- [ ] Tengo InputHelper.java con captura de inputs
- [ ] **NINGUNA** de mis clases tiene más de 100 líneas
- [ ] Mi código NO tiene duplicación (DRY principle)

**Entregable funcional:**
- [ ] Mi programa muestra un menú interactivo
- [ ] Captura datos del usuario con Scanner
- [ ] Si elige opción 1 o 2, pide datos del movimiento
- [ ] Valida que los datos sean correctos
- [ ] Calcula y muestra el total con IVA
- [ ] Se cierra sin errores

**🎯 Auto-evaluación:**
Abre Main.java. Si tiene MÁS de 40 líneas o ves mucha lógica ahí, algo está mal. Main debe ser solo el "director de orquesta" que llama a otros.

**📸 Evidencia:**
Toma screenshots de:
1. Tu programa funcionando con el menú interactivo
2. La estructura de carpetas en IntelliJ mostrando las 6 clases
3. Guárdalos como `fase1-menu-funcionando.png` y `fase1-estructura-proyecto.png`

---

## 🚀 Próximo paso: FASE 2 - Programación Orientada a Objetos

En la siguiente fase aprenderás a:
- Convertir MovementCalculator en la clase Movement con POO real
- Crear objetos y almacenarlos en ArrayList
- Usar constructores, getters y setters
- Entender encapsulamiento y `this`
- **IMPORTANTE:** Muchas de las clases que creaste en Fase 1 evolucionarán

**¿Qué pasará con tu código de Fase 1?**
- **AppConfig**: Se quedará igual (bien hecho)
- **MovementCalculator**: Se convertirá en métodos de la clase Movement
- **MenuHelper**: Se convertirá en ConsoleMenu (Fase 6)
- **InputHelper**: Se integrará con ConsoleMenu
- **DataDisplay**: Se convertirá en métodos de visualización más sofisticados

**¿Perdiste tiempo en Fase 1?** ¡NO! Aprendiste:
1. A separar responsabilidades ANTES de aprender POO
2. Que el código debe estar organizado (no todo en main)
3. Los fundamentos de Java sin la complejidad de objetos
4. A pensar en "qué hace cada archivo" (base de POO)

**⏱️ Tiempo total Fase 1:** 12-15 horas distribuidas en 1-2 semanas
