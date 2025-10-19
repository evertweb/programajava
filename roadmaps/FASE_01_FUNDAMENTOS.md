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

**📋 DIAGRAMA DE TAREAS:**

```
MovementCalculator.java
│
├── Método 1: isValidMovement
│   ├── Parámetros que debe recibir:
│   │   • String fuelType
│   │   • double quantity
│   │   • double unitPrice
│   │
│   ├── Condiciones a validar:
│   │   • fuelType NO puede ser null
│   │   • fuelType NO puede estar vacío ("")
│   │   • quantity debe ser MAYOR a 0
│   │   • unitPrice debe ser MAYOR a 0
│   │
│   ├── Tipo de retorno: boolean
│   └── Retorna: true si TODO es válido, false si hay AL MENOS un problema
│
├── Método 2: isBigPurchase
│   ├── Parámetros: double quantity
│   ├── Condición: quantity >= 100
│   ├── Tipo de retorno: boolean
│   └── Retorna: true si es compra grande, false si no
│
└── Método 3: requiresApproval
    ├── Parámetros:
    │   • String type (puede ser "ENTRADA" o "SALIDA")
    │   • double quantity
    │   • double total
    │
    ├── Lógica a implementar:
    │   Requiere aprobación SI:
    │   • El tipo es "SALIDA" Y (quantity > 100 O total > 500)
    │   
    │   Usa operadores:
    │   • && para "Y" (ambas condiciones deben cumplirse)
    │   • || para "O" (al menos una debe cumplirse)
    │   • .equals() para comparar Strings
    │
    ├── Tipo de retorno: boolean
    └── Retorna: true si requiere aprobación, false si no
```

**🧪 PRUEBAS EN Main.java:**

```
Crear casos de prueba para validar tu código:

Caso 1: Movimiento válido
├── Datos: fuelType="Diesel", quantity=100, unitPrice=3.45
└── Resultado esperado: isValidMovement retorna TRUE

Caso 2: Movimiento inválido (cantidad negativa)
├── Datos: fuelType="", quantity=-10, unitPrice=0
└── Resultado esperado: isValidMovement retorna FALSE

Caso 3: Compra grande
├── Datos: quantity=150
└── Resultado esperado: isBigPurchase retorna TRUE

Caso 4: Requiere aprobación
├── Datos: type="SALIDA", quantity=120, total=600
└── Resultado esperado: requiresApproval retorna TRUE
```

**💡 PISTAS DE IMPLEMENTACIÓN:**

1. **Para validar String no vacío:**
   - Primero verifica que no sea null
   - Luego verifica que su longitud sea mayor a 0
   - O usa el método `.isEmpty()`

2. **Para comparar Strings:**
   - ❌ NUNCA uses: `type == "ENTRADA"`
   - ✅ SIEMPRE usa: `type.equals("ENTRADA")`

3. **Para combinar condiciones:**
   - `&&` significa "ambas deben ser true"
   - `||` significa "al menos una debe ser true"
   - Usa paréntesis para agrupar: `(condicion1 || condicion2) && condicion3`

**✅ Resultado esperado:** 
- Validaciones funcionando correctamente
- Main.java sigue limpio (solo pruebas)
- Entiendes la diferencia entre `==` y `.equals()` para Strings
- Puedes explicar cuándo usar `&&` vs `||`

**🎯 AUTO-EVALUACIÓN:**
Antes de continuar, pregúntate:
- [ ] ¿Puedo explicar por qué `"Diesel" == "Diesel"` a veces falla?
- [ ] ¿Entiendo qué hace cada operador lógico?
- [ ] ¿Mis validaciones cubren TODOS los casos posibles?

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

**📋 DIAGRAMA DE TAREAS:**

```
MenuHelper.java
│
├── Método 1: displayMainMenu
│   ├── Modificador: public static
│   ├── Tipo de retorno: void (no retorna nada, solo imprime)
│   ├── Parámetros: ninguno
│   │
│   └── Debe imprimir:
│       ┌─────────────────────────────────────┐
│       │   ========== MENÚ PRINCIPAL ==========   │
│       │   1. Registrar Entrada de Combustible    │
│       │   2. Registrar Salida de Combustible     │
│       │   3. Ver Inventario                      │
│       │   4. Salir                                │
│       │   ====================================   │
│       └─────────────────────────────────────┘
│
├── Método 2: processMenuOption
│   ├── Modificador: public static
│   ├── Tipo de retorno: void
│   ├── Parámetros: int option
│   │
│   ├── Usa estructura SWITCH (no if-else)
│   │
│   └── Casos a manejar:
│       ├── case 1: Imprimir "→ Módulo de ENTRADA seleccionado"
│       ├── case 2: Imprimir "→ Módulo de SALIDA seleccionado"
│       ├── case 3: Imprimir "→ Módulo de INVENTARIO seleccionado"
│       ├── case 4: Imprimir "→ Saliendo del sistema..."
│       └── default: Imprimir "❌ Opción inválida"
│       
│       ⚠️ IMPORTANTE: No olvides 'break;' después de cada case
│
└── Método 3: validateMovementType
    ├── Modificador: public static
    ├── Tipo de retorno: void
    ├── Parámetros: String type
    │
    ├── Usa estructura IF-ELSE (no switch)
    │
    └── Lógica:
        ├── Si type.equals("ENTRADA") → Imprimir "✓ Inventario AUMENTARÁ"
        ├── Si type.equals("SALIDA") → Imprimir "✓ Inventario DISMINUIRÁ"
        └── Si es otro valor → Imprimir "✗ Tipo de movimiento inválido"
        
        ⚠️ RECUERDA: Usa .equals() NO uses ==
```

**🧪 PRUEBAS EN Main.java:**

```
En el método main(), escribe código para probar MenuHelper:

Prueba 1: Mostrar el menú
├── Llamar a: MenuHelper.displayMainMenu()
└── Resultado esperado: Ver el menú formateado en consola

Prueba 2: Simular selección de opción 1
├── Crear variable: int opcionUsuario = 1;
├── Llamar a: MenuHelper.processMenuOption(opcionUsuario)
└── Resultado esperado: Ver "→ Módulo de ENTRADA seleccionado"

Prueba 3: Probar opción inválida
├── Llamar a: MenuHelper.processMenuOption(99)
└── Resultado esperado: Ver "❌ Opción inválida"

Prueba 4: Validar tipo de movimiento
├── Llamar a: MenuHelper.validateMovementType("ENTRADA")
├── Llamar a: MenuHelper.validateMovementType("SALIDA")
├── Llamar a: MenuHelper.validateMovementType("INVALIDO")
└── Resultado esperado: Ver los tres mensajes correspondientes
```

**💡 PISTAS DE IMPLEMENTACIÓN:**

1. **¿Cuándo usar switch vs if-else?**
   - **Switch:** Cuando comparas UNA variable contra múltiples valores constantes (números, Strings)
   - **If-else:** Para condiciones más complejas (rangos, operadores lógicos)

2. **Estructura básica de switch:**
   ```java
   switch(variable) {
       case valor1:
           // código
           break;  // ← NO OLVIDES ESTO
       case valor2:
           // código
           break;
       default:
           // código para casos no contemplados
   }
   ```

3. **¿Qué pasa si olvidas `break`?**
   - El código "cae" al siguiente case (efecto cascada)
   - Esto casi siempre es un error (salvo casos muy específicos)

**✅ Resultado esperado:** 
- Ver menú formateado
- Ver procesamiento de opciones funcionando
- Main.java sigue corto (menos de 25 líneas para estas pruebas)
- Todo organizado por responsabilidades

**🎯 AUTO-EVALUACIÓN:**
Antes de continuar, pregúntate:
- [x] ¿Entiendo la diferencia entre switch e if-else?
- [x] ¿Sé por qué es importante el `break` en switch?
- [x] ¿Puedo explicar por qué uso `.equals()` en vez de `==`?
- [x] ¿Mi código está en MenuHelper, NO en Main?

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

**📋 DIAGRAMA DE TAREAS:**

```
DataDisplay.java
│
├── Método 1: showFuelTypes
│   ├── Modificador: public static
│   ├── Tipo de retorno: void
│   ├── Parámetros: ninguno
│   │
│   ├── Paso 1: Crear un array de String llamado fuelTypes
│   │   └── Valores: {"Diesel", "Gasolina 93", "Gasolina 95", "ACPM"}
│   │
│   ├── Paso 2: Imprimir encabezado
│   │   └── Texto: "\n=== TIPOS DE COMBUSTIBLE ==="
│   │
│   ├── Paso 3: Usar bucle FOR CLÁSICO
│   │   ├── Variable de control: int i
│   │   ├── Condición: i < fuelTypes.length
│   │   ├── Incremento: i++
│   │   └── Dentro del bucle: Imprimir (i + 1) + ". " + fuelTypes[i]
│   │
│   └── Formato de salida esperado:
│       === TIPOS DE COMBUSTIBLE ===
│       1. Diesel
│       2. Gasolina 93
│       3. Gasolina 95
│       4. ACPM
│       
│       💡 PISTA: Usa (i + 1) porque los arrays empiezan en 0
│                  pero quieres mostrar números desde 1
│
├── Método 2: showMenuWithForEach
│   ├── Modificador: public static
│   ├── Tipo de retorno: void
│   ├── Parámetros: String[] options
│   │
│   ├── Paso 1: Crear variable contador
│   │   └── int counter = 1
│   │
│   ├── Paso 2: Usar bucle FOR-EACH (NO for clásico)
│   │   ├── Sintaxis: for (String option : options)
│   │   ├── Dentro del bucle:
│   │   │   ├── Imprimir counter + ". " + option
│   │   │   └── Incrementar counter++
│   │
│   └── Ejemplo de salida (si recibe ["Entrada", "Salida"]):
│       1. Entrada
│       2. Salida
│       
│       💡 PISTA: for-each NO tiene índice automático, por eso
│                  necesitas el counter manual
│
└── Método 3: simulateProcessing
    ├── Modificador: public static
    ├── Tipo de retorno: void
    ├── Parámetros: int totalMovements
    │
    ├── Paso 1: Imprimir encabezado
    │   └── Texto: "\n=== PROCESANDO MOVIMIENTOS ==="
    │
    ├── Paso 2: Crear variable para contar
    │   └── int processed = 0
    │
    ├── Paso 3: Usar bucle WHILE (NO for)
    │   ├── Condición: processed < totalMovements
    │   ├── Dentro del bucle:
    │   │   ├── Incrementar: processed++
    │   │   └── Imprimir: "Procesando movimiento #" + processed
    │   │
    │   └── ⚠️ IMPORTANTE: Si olvidas el processed++, tendrás
    │                      un loop infinito
    │
    ├── Paso 4: Después del bucle
    │   └── Imprimir: "✓ Total procesado: " + processed
    │
    └── Ejemplo de salida (si recibe totalMovements=3):
        === PROCESANDO MOVIMIENTOS ===
        Procesando movimiento #1
        Procesando movimiento #2
        Procesando movimiento #3
        ✓ Total procesado: 3
```

**🧪 PRUEBAS EN Main.java:**

```
Escribir código de prueba en el método main():

Prueba 1: Tipos de combustible
├── Llamar a: DataDisplay.showFuelTypes()
└── Resultado esperado: Ver lista numerada del 1 al 4

Prueba 2: Menú personalizado
├── Crear array: String[] menuOptions = {"Registrar Entrada", "Registrar Salida", "Ver Inventario", "Salir"}
├── Llamar a: DataDisplay.showMenuWithForEach(menuOptions)
└── Resultado esperado: Ver lista numerada del 1 al 4

Prueba 3: Simular procesamiento
├── Llamar a: DataDisplay.simulateProcessing(5)
└── Resultado esperado: Ver 5 líneas de "Procesando movimiento #X"
```

**💡 COMPARACIÓN DE BUCLES - Cuándo usar cada uno:**

```
┌─────────────────────────────────────────────────────────────────┐
│ FOR CLÁSICO                                                      │
├─────────────────────────────────────────────────────────────────┤
│ Úsalo cuando:                                                    │
│ • Necesitas el ÍNDICE (posición) del elemento                   │
│ • Quieres recorrer al revés                                     │
│ • Necesitas saltar elementos (i += 2)                           │
│                                                                  │
│ Estructura:                                                      │
│   for (int i = 0; i < array.length; i++) {                     │
│       // array[i] accede al elemento en posición i             │
│   }                                                              │
│                                                                  │
│ Ejemplo Forestech: Mostrar movimientos numerados                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ FOR-EACH                                                         │
├─────────────────────────────────────────────────────────────────┤
│ Úsalo cuando:                                                    │
│ • SOLO necesitas el VALOR, no la posición                       │
│ • Código más simple y legible                                   │
│ • NO necesitas modificar el array                               │
│                                                                  │
│ Estructura:                                                      │
│   for (String elemento : array) {                              │
│       // elemento ya contiene el valor                         │
│   }                                                              │
│                                                                  │
│ Ejemplo Forestech: Imprimir todos los tipos de combustible      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ WHILE                                                            │
├─────────────────────────────────────────────────────────────────┤
│ Úsalo cuando:                                                    │
│ • NO sabes cuántas veces se repetirá                            │
│ • La condición depende de algo que cambia en el bucle           │
│ • Necesitas mayor flexibilidad                                  │
│                                                                  │
│ Estructura:                                                      │
│   while (condicion) {                                           │
│       // código                                                 │
│       // ⚠️ Debes actualizar la condición                      │
│   }                                                              │
│                                                                  │
│ Ejemplo Forestech: Procesar movimientos hasta completar todos   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ DO-WHILE                                                         │
├─────────────────────────────────────────────────────────────────┤
│ Úsalo cuando:                                                    │
│ • Necesitas ejecutar el código AL MENOS UNA VEZ                 │
│ • Luego verificar si se debe repetir                            │
│                                                                  │
│ Estructura:                                                      │
│   do {                                                          │
│       // código se ejecuta primero                             │
│   } while (condicion);                                          │
│                                                                  │
│ Ejemplo Forestech: Mostrar menú al menos una vez                │
└─────────────────────────────────────────────────────────────────┘
```

**🚨 ERRORES COMUNES:**

```
❌ ERROR 1: Loop infinito en while
int i = 0;
while (i < 10) {
    System.out.println("Hola");
    // Olvidaste i++, nunca termina
}

✅ CORRECTO:
int i = 0;
while (i < 10) {
    System.out.println("Hola");
    i++;  // Eventualmente i será 10 y saldrá del bucle
}

---

❌ ERROR 2: Acceder fuera del array
String[] tipos = {"Diesel", "Gasolina"};
for (int i = 0; i <= tipos.length; i++) {  // ← <= está mal
    System.out.println(tipos[i]);  // Error en tipos[2]
}

✅ CORRECTO:
for (int i = 0; i < tipos.length; i++) {  // ← < es correcto
    System.out.println(tipos[i]);
}

---

❌ ERROR 3: Modificar array en for-each
for (String tipo : tipos) {
    tipo = "NUEVO";  // Esto NO modifica el array
}

✅ CORRECTO: Usa for clásico si necesitas modificar
for (int i = 0; i < tipos.length; i++) {
    tipos[i] = "NUEVO";  // Esto SÍ modifica
}
```

**✅ Resultado esperado:** 
- Ver listas formateadas correctamente
- Entender cuándo usar cada tipo de bucle
- Saber cómo acceder a elementos de un array
- Main.java sigue corto (menos de 15 líneas de pruebas)

**🎯 AUTO-EVALUACIÓN:**
Antes de continuar, pregúntate:
- [ ] ¿Puedo explicar cuándo usar for vs for-each vs while?
- [ ] ¿Entiendo por qué los arrays empiezan en índice 0?
- [ ] ¿Sé cómo evitar un loop infinito en while?
- [ ] ¿Entiendo qué es `array.length` y por qué NO lleva paréntesis?
- [ ] ¿Puedo explicar por qué `i < array.length` y no `i <= array.length`?

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

**📋 DIAGRAMA DE TAREAS:**

```
InputHelper.java
│
├── Configuración inicial de la clase
│   ├── Import necesario: import java.util.Scanner;
│   │
│   └── Declarar variable estática de clase:
│       private static Scanner scanner = new Scanner(System.in);
│       
│       💡 EXPLICACIÓN:
│       • private: Solo esta clase puede acceder al scanner
│       • static: Compartido entre todos los métodos (no necesitas crear objeto)
│       • Scanner(System.in): Captura entrada desde el teclado
│
├── Método 1: readInt
│   ├── Modificador: public static
│   ├── Tipo de retorno: int
│   ├── Parámetros: String prompt (mensaje a mostrar al usuario)
│   │
│   ├── Lógica paso a paso:
│   │   1. Imprimir el prompt (sin salto de línea, usa print no println)
│   │   2. Leer un int usando scanner.nextInt()
│   │   3. ⚠️ CRÍTICO: Llamar a scanner.nextLine() para limpiar buffer
│   │   4. Retornar el valor leído
│   │
│   └── ¿Por qué limpiar el buffer?
│       Cuando el usuario escribe "5[ENTER]", nextInt() lee el "5"
│       pero deja el "[ENTER]" en el buffer. Si luego usas nextLine(),
│       capturará ese ENTER vacío en lugar de esperar nueva entrada.
│
├── Método 2: readDouble
│   ├── Modificador: public static
│   ├── Tipo de retorno: double
│   ├── Parámetros: String prompt
│   │
│   ├── Lógica paso a paso:
│   │   1. Imprimir el prompt
│   │   2. Leer un double usando scanner.nextDouble()
│   │   3. ⚠️ CRÍTICO: Llamar a scanner.nextLine() para limpiar buffer
│   │   4. Retornar el valor leído
│   │
│   └── Mismo problema de buffer que readInt()
│
├── Método 3: readString
│   ├── Modificador: public static
│   ├── Tipo de retorno: String
│   ├── Parámetros: String prompt
│   │
│   ├── Lógica paso a paso:
│   │   1. Imprimir el prompt
│   │   2. Leer una línea completa usando scanner.nextLine()
│   │   3. Retornar directamente (nextLine NO deja basura en buffer)
│   │
│   └── 💡 PISTA: Este método NO necesita limpiar buffer porque
│                  nextLine() consume todo incluyendo el ENTER
│
└── Método 4: readFuelType
    ├── Modificador: public static
    ├── Tipo de retorno: String
    ├── Parámetros: ninguno
    │
    ├── Lógica paso a paso:
    │   1. Llamar a DataDisplay.showFuelTypes() para mostrar opciones
    │   2. Llamar a readInt("Seleccione tipo de combustible: ")
    │   3. Usar SWITCH para convertir número a nombre de combustible:
    │       ├── case 1: retornar "Diesel"
    │       ├── case 2: retornar "Gasolina 93"
    │       ├── case 3: retornar "Gasolina 95"
    │       ├── case 4: retornar "ACPM"
    │       └── default: retornar "Diesel" (opción por defecto)
    │
    └── 💡 NOTA: Reutiliza readInt() en lugar de duplicar código
```

**🧪 FLUJO COMPLETO EN Main.java:**

```
El método main() debe orquestar el flujo interactivo completo:

┌─────────────────────────────────────────────────────────────┐
│ PASO 1: Bienvenida                                           │
├─────────────────────────────────────────────────────────────┤
│ • Imprimir línea de separación: "================================="
│ • Imprimir mensaje usando: AppConfig.APP_NAME
│ • Imprimir otra línea de separación
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ PASO 2: Mostrar y capturar opción del menú                  │
├─────────────────────────────────────────────────────────────┤
│ • Llamar a MenuHelper.displayMainMenu()
│ • Llamar a InputHelper.readInt("Seleccione una opción: ")
│   └── Guardar en variable: int option
│ • Llamar a MenuHelper.processMenuOption(option)
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ PASO 3: SI eligió opción 1 o 2 (Entrada o Salida)          │
├─────────────────────────────────────────────────────────────┤
│ Condición: if (option == 1 || option == 2)
│
│ Dentro del if:
│ 1. Imprimir encabezado: "\n=== CAPTURA DE DATOS ==="
│
│ 2. Capturar tipo de combustible:
│    String fuelType = InputHelper.readFuelType()
│
│ 3. Capturar cantidad:
│    double quantity = InputHelper.readDouble("Ingrese cantidad (litros): ")
│
│ 4. Capturar precio:
│    double unitPrice = InputHelper.readDouble("Ingrese precio unitario: $")
│
│ 5. Validar los datos:
│    if (MovementCalculator.isValidMovement(fuelType, quantity, unitPrice)) {
│        • Imprimir: "\n✓ Datos válidos"
│        • Llamar a: MovementCalculator.printMovementSummary(...)
│    } else {
│        • Imprimir: "\n✗ Datos inválidos"
│    }
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ PASO 4: Despedida                                            │
├─────────────────────────────────────────────────────────────┤
│ • Imprimir: "\n=== FIN DEL PROGRAMA ==="
└─────────────────────────────────────────────────────────────┘
```

**🚨 EL PROBLEMA DEL BUFFER - Explicación Visual:**

```
Situación: Usuario escribe "5" y presiona ENTER

┌─────────────────────────────────────────────┐
│ Buffer de entrada después de nextInt():     │
├─────────────────────────────────────────────┤
│ nextInt() lee → "5"                          │
│ Queda en buffer → "\n" (el ENTER)           │
└─────────────────────────────────────────────┘

❌ SI NO LIMPIAS EL BUFFER:
Scanner scanner = new Scanner(System.in);
int edad = scanner.nextInt();        // Usuario escribe: 5[ENTER]
String nombre = scanner.nextLine();  // ¡Lee el ENTER vacío!
// nombre queda como "" (String vacío)

✅ SI LIMPIAS EL BUFFER:
Scanner scanner = new Scanner(System.in);
int edad = scanner.nextInt();        // Usuario escribe: 5[ENTER]
scanner.nextLine();                  // ← Consume el ENTER que quedó
String nombre = scanner.nextLine();  // Ahora SÍ espera nueva entrada
// nombre queda con lo que el usuario escriba

┌──────────────────────────────────────────────────────────┐
│ REGLA DE ORO                                              │
├──────────────────────────────────────────────────────────┤
│ Después de nextInt() o nextDouble():                     │
│ • SIEMPRE llama a nextLine() para limpiar               │
│                                                           │
│ Después de nextLine():                                   │
│ • NO necesitas limpiar (ya consumió todo)               │
└──────────────────────────────────────────────────────────┘
```

**💡 PATRÓN PROFESIONAL LOGRADO:**

```
Organización del proyecto después de Checkpoint 1.6:

┌──────────────────┐
│    Main.java     │  ← ORQUESTADOR (coordina el flujo)
└────────┬─────────┘
         │
         ├─→ AppConfig.java          (constantes del sistema)
         ├─→ MovementCalculator.java (lógica de cálculos)
         ├─→ MenuHelper.java         (lógica del menú)
         ├─→ DataDisplay.java        (visualización de datos)
         └─→ InputHelper.java        (captura de inputs) ← NUEVO

Principio aplicado: SEPARACIÓN DE RESPONSABILIDADES
• Cada clase tiene UNA tarea específica
• Main NO tiene lógica, solo llama a otros
• Fácil de mantener y extender
```

**✅ Resultado esperado:**
- El programa muestra el menú y ESPERA tu input real
- Puedes escribir números y texto
- Se validan y procesan los datos correctamente
- Main.java es corto y claro (menos de 40 líneas)
- Entiendes el problema del buffer y cómo solucionarlo

**🎯 AUTO-EVALUACIÓN:**
Antes de continuar, pregúntate:
- [X] ¿Puedo explicar qué es el buffer y por qué causa problemas?
- [X] ¿Sé cuándo necesito limpiar el buffer y cuándo no?
- [X] ¿Entiendo por qué uso `print()` vs `println()` para prompts?
- [X] ¿Puedo explicar por qué el Scanner es `static` en InputHelper?
- [X] ¿Entiendo cómo Main.java orquesta el flujo sin tener lógica?

**🔍 DEPURACIÓN - Si el programa "salta" preguntas:**

```
SÍNTOMA: El programa no espera que escribas algo, continúa solo

DIAGNÓSTICO:
┌─────────────────────────────────────────────────┐
│ Olvidaste scanner.nextLine() después de:        │
│ • scanner.nextInt()                             │
│ • scanner.nextDouble()                          │
└─────────────────────────────────────────────────┘

SOLUCIÓN:
1. Revisa TODOS los lugares donde usas nextInt() o nextDouble()
2. Agrega scanner.nextLine() inmediatamente después
3. Compila y prueba de nuevo
```

**⏱️ Tiempo estimado:** 3 horas



## 🧾 Checklist de salida de Fase 1

Antes de pasar a Fase 2, verifica que puedes responder SÍ a todo:

**Comprensión de conceptos:**
- [x] Puedo explicar con mis palabras qué es el método `main()` y por qué es necesario
- [x] Entiendo la diferencia entre `int` e `Integer`, `double` y `Double`
- [x] Sé cuándo usar `final` y qué significa "inmutable"
- [x] Puedo explicar por qué `==` no funciona bien con Strings
- [x] Entiendo cuándo usar `if/else` vs `switch`
- [x] Sé la diferencia entre `while` y `do-while`
- [x] Entiendo qué es el buffer de Scanner y cómo limpiarlo
- [x] Entiendo por qué separé el código en múltiples clases

**Habilidades prácticas:**
- [x] Compilé y ejecuté código desde terminal con Maven
- [x] Usé el debugger al menos 3 veces con breakpoints
- [x] Creé commits de Git por cada checkpoint completado
- [x] Documenté aprendizajes en `JAVA_LEARNING_LOG.md`

**Estructura del proyecto:**
- [x] Tengo Main.java CORTO (menos de 40 líneas)
- [x] Tengo AppConfig.java con las constantes
- [x] Tengo MovementCalculator.java con los cálculos
- [x] Tengo MenuHelper.java con la lógica del menú
- [x] Tengo DataDisplay.java con visualización de datos
- [x] Tengo InputHelper.java con captura de inputs
- [x] **NINGUNA** de mis clases tiene más de 100 líneas
- [x] Mi código NO tiene duplicación (DRY principle)

**Entregable funcional:**
- [x] Mi programa muestra un menú interactivo
- [x] Captura datos del usuario con Scanner
- [x] Si elige opción 1 o 2, pide datos del movimiento
- [x] Valida que los datos sean correctos
- [x] Calcula y muestra el total con IVA
- [x] Se cierra sin errores

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
