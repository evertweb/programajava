# 🔧 FASE 2.5: CREACIÓN DEL PAQUETE `managers` Y CLASE `MovementManager`

> **CONTEXTO:** Este roadmap cierra el gap entre Fase 2 (POO) y Fase 3 (SQL). Es el componente que faltó explicar.

> **OBJETIVO GENERAL:** Aprender el patrón Manager y cómo gestionar colecciones de objetos de forma profesional.

---

## 🧠 Antes de empezar

- 📐 **Revisa tu estructura actual:** Debes tener `Movement.java` completo en el paquete `models/`
- 🎯 **Pregúntate:** "Si tengo 100 movimientos, ¿dónde los guardo? ¿Cómo los busco? ¿Cómo los filtro?"
- 📚 **Concepto nuevo:** Patrón Manager/Service - separa la lógica de gestión de colecciones
- 🐞 **Refuerza depuración:** Usarás breakpoints para ver cómo se llenan las listas
- 🔁 **Git loop:** Al completar cada checkpoint crea un commit claro
- ⏱️ **Tiempo estimado:** 2-3 horas

---

## 🤔 ¿POR QUÉ NECESITAMOS `MovementManager`?

### Problema Actual:
Ahora mismo tienes:
- ✅ `Movement.java` - Representa **UN** movimiento (atributos + getters/setters + cálculos)
- ✅ `Main.java` - Punto de entrada de la aplicación
- ❌ **FALTA:** ¿Quién se encarga de manejar **MÚLTIPLES** movimientos?

### Escenario Sin Manager (código desorganizado):
```
Main.java tendría que hacer TODO:
• Crear una List<Movement>
• Agregar movimientos uno por uno
• Buscar manualmente con bucles for
• Filtrar con más bucles for
• Calcular totales con más bucles for
• Resultado: Main.java de 500 líneas 😰
```

### Escenario Con Manager (código limpio):
```
Main.java solo coordina:
• manager.addMovement(...)
• manager.findById(...)
• manager.getMovementsByType(...)
• manager.calculateTotalEntered()
• Resultado: Main.java legible y conciso 😊
```

**Ventajas del patrón Manager:**
- ✅ Separación de responsabilidades (Main coordina, Manager gestiona)
- ✅ Código reutilizable (no repites búsquedas en múltiples lugares)
- ✅ Fácil de probar y mantener
- ✅ Preparado para escalar (agregar más funcionalidades sin tocar Main)

---

## 🏗️ ARQUITECTURA: ¿QUÉ ES UN "MANAGER"?

### 🎯 Patrón de Diseño: Manager (o Service)

**Definición:**
> Un Manager es una clase que **gestiona operaciones** sobre un conjunto de objetos del mismo tipo.

**🎓 Analogía del Mundo Real:**

```
╔══════════════════════════════════════════════════════════╗
║         🏪 TIENDA DE ROPA (Gestión de Inventario)        ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  👕 Producto (Product)                                   ║
║  └─ Representa UNA prenda individual                    ║
║     • codigo: "CAM-001"                                  ║
║     • nombre: "Camisa Azul"                              ║
║     • precio: 50000                                      ║
║                                                          ║
║  📋 ProductManager (Gerente de Inventario)               ║
║  └─ Gestiona TODOS los productos:                       ║
║     • Agregar producto nuevo al inventario               ║
║     • Buscar producto por código                         ║
║     • Listar todos los productos                         ║
║     • Filtrar por categoría (camisas, pantalones)        ║
║     • Calcular valor total del inventario                ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

**En Forestech (tu proyecto):**
```
Movement = UN movimiento individual
MovementManager = Gerente que maneja TODOS los movimientos
```

**Prompts sugeridos:**
```text
"Dame más ejemplos de Manager en la vida real."
"¿Cuál es la diferencia entre Manager y Service? ¿Son lo mismo?"
"¿Por qué no simplemente uso métodos static en vez de un Manager?"
"¿En qué se diferencia un Manager de una clase Utils?"
```

---

## 📐 RESPONSABILIDADES DE `MovementManager`

### 🎯 Fase 2 (Actual - Sin Base de Datos):
```
MovementManager (en memoria)
│
├── Almacenar movimientos en ArrayList
├── Crear nuevos movimientos
├── Buscar por ID
├── Listar todos los movimientos
├── Filtrar por tipo (ENTRADA/SALIDA)
└── Calcular totales
```

### 🚀 Fase 3-4 (Futuro - Con Base de Datos):
```
MovementManager (con BD)
│
├── Conectar con MySQL
├── Guardar en BD (INSERT)
├── Leer de BD (SELECT)
├── Actualizar en BD (UPDATE)
├── Eliminar de BD (DELETE)
└── Convertir ResultSet → Movement
```

**📌 IMPORTANTE:** 
Por ahora (Fase 2.5), `MovementManager` trabajará **SOLO EN MEMORIA** con un `ArrayList<Movement>`. En Fase 3, agregaremos la conexión a MySQL.

---

## ✅ Checkpoint 2.5.1: Crear el Paquete `managers`

**Concepto clave:** Los paquetes organizan clases relacionadas. `managers` contendrá todas las clases que gestionan colecciones de objetos.

**📍 DÓNDE:** 
- **Crear paquete:** `managers` dentro de `com.forestech/`
- **Ubicación física:** `forestech-cli-java/src/main/java/com/forestech/managers/`

**🎯 PARA QUÉ:** 
Separar responsabilidades según el **patrón de capas**:

```
com.forestech/
├── models/        → Clases de datos (Movement, Vehicle, Supplier)
├── managers/      → Lógica de gestión (MovementManager, VehicleManager)
├── utils/         → Utilidades reutilizables (IdGenerator)
└── Main.java      → Coordinador principal
```

**Tareas paso a paso:**

### 1️⃣ Crear el paquete `managers`:

**En IntelliJ IDEA:**
1. En el panel izquierdo (Project), navega a:
   ```
   forestech-cli-java/src/main/java/com/forestech/
   ```
2. Clic derecho en `forestech` → **New** → **Package**
3. Escribe: `managers` (minúsculas)
4. Presiona Enter

**Verificación:**
- Debe aparecer una carpeta `managers/` al mismo nivel que `models/` y `utils/`

**✅ Resultado esperado:**
```
com.forestech/
├── models/
├── managers/   ← ¡NUEVO!
└── utils/
```

---

## ✅ Checkpoint 2.5.2: Crear la Clase `MovementManager`

**Concepto clave:** Esta clase será el "cerebro" que controla todos los movimientos de Forestech.

**📍 DÓNDE:** 
- **Crear archivo:** `MovementManager.java` en `managers/`
- **Ruta completa:** `forestech-cli-java/src/main/java/com/forestech/managers/MovementManager.java`

**🎯 ESTRUCTURA COMPLETA DE LA CLASE:**

```
MovementManager.java
│
├── 1. DECLARACIÓN DEL PAQUETE
│   └── package com.forestech.managers;
│
├── 2. IMPORTS
│   ├── import com.forestech.models.Movement;
│   └── import java.util.*;
│
├── 3. ATRIBUTO PRIVADO
│   └── private List<Movement> movements;
│       (ArrayList que almacena TODOS los movimientos)
│
├── 4. CONSTRUCTOR
│   └── public MovementManager() { ... }
│       Inicializa el ArrayList vacío
│
├── 5. MÉTODOS PRINCIPALES
│   ├── addMovement(...)         → Crear y agregar movimiento
│   ├── getAllMovements()        → Retornar lista completa
│   ├── findById(String id)      → Buscar por ID
│   ├── getMovementsByType(...)  → Filtrar por ENTRADA/SALIDA
│   └── getTotalMovements()      → Contar cuántos hay
│
└── 6. MÉTODOS DE CÁLCULO
    ├── calculateTotalEntered()  → Suma de todas las ENTRADAS
    ├── calculateTotalExited()   → Suma de todas las SALIDAS
    └── getCurrentStock()        → ENTRADAS - SALIDAS
```

---

### 🛠️ PASO 1: Crear el archivo y estructura básica

**Tareas:**

1. **Crear la clase:**
   - Clic derecho en `managers/` → New → Java Class
   - Nombre: `MovementManager`
   - Presiona Enter

2. **Declarar el paquete (ya debe estar automático):**
   
   Verifica que la primera línea sea:
   ```
   package com.forestech.managers;
   ```

3. **Agregar los imports necesarios:**
   
   **TÚ debes escribir** los imports para:
   - La clase `Movement` del paquete `models`
   - Las interfaces `ArrayList` y `List` de `java.util`
   
   **Pista de sintaxis:**
   ```
   import paquete.Clase;
   ```

   **Pregunta de comprensión:** 
   - ¿Por qué necesitamos importar `Movement`?
   - ¿Para qué usaremos `ArrayList` y `List`?

4. **Declarar la clase:**
   
   Estructura básica de una clase pública:
   ```
   public class NOMBRE_CLASE {
       // El contenido va aquí
   }
   ```
   
   **TÚ debes escribir:** Reemplaza `NOMBRE_CLASE` con el nombre correcto.

**✅ Checkpoint:** Compila con `mvn clean compile` - debe compilar sin errores.

---

### 🛠️ PASO 2: Declarar el atributo privado

**Concepto:** `MovementManager` necesita un lugar para almacenar TODOS los movimientos. Usaremos un `ArrayList`.

**🎓 Analogía:**
```
MovementManager = Archivador de documentos
ArrayList<Movement> = Carpeta donde guardas todos los documentos
Movement = Cada documento individual
```

**Tarea:**

Dentro de la clase, **TÚ debes agregar** el atributo:

```
Estructura de un atributo:
[modificador acceso] [tipo] [nombre];

Ejemplo:
private List<Movement> movements;
```

**Preguntas de comprensión:**
1. ¿Por qué es `private` y no `public`?
2. ¿Por qué usamos `List<Movement>` en vez de `Movement[]` (arreglo)?
3. ¿Qué significa `<Movement>` entre los signos menor y mayor?

**💡 Reflexiona antes de continuar:**
- **Encapsulamiento:** Solo `MovementManager` debe poder acceder directamente a la lista
- **Flexibilidad:** `List` es dinámica (crece automáticamente), los arreglos son fijos
- **Genéricos:** `<Movement>` indica que la lista solo puede contener objetos de tipo `Movement`

---

### 🛠️ PASO 3: Crear el constructor

**Concepto:** El constructor inicializa el `ArrayList` cuando creas un `MovementManager`.

**🎓 Analogía:**
```
new MovementManager() 
    ↓
Comprar un archivador nuevo y ponerle una carpeta vacía
```

**Tarea:**

**TÚ debes agregar** el constructor después del atributo:

```
Estructura de un constructor:
public NOMBRE_CLASE() {
    // Inicialización de atributos
}

Dentro del constructor, inicializa la lista:
this.movements = new ArrayList<>();
```

**Preguntas de comprensión:**
1. ¿Por qué el constructor se llama igual que la clase?
2. ¿Qué pasaría si NO inicializamos `movements` en el constructor?
3. ¿Por qué usamos `new ArrayList<>()` y no `new List<>()`?

**💡 Reflexiona:**
1. **Regla de Java:** Los constructores siempre tienen el mismo nombre que la clase
2. **NullPointerException:** Si intentas usar `movements.add(...)` sin inicializar, la app crashea
3. **Interfaces vs Clases:** `List` es una interfaz (no se puede instanciar), `ArrayList` es la implementación concreta

**✅ Checkpoint:** Compila de nuevo. Debe seguir sin errores.

---

### 🛠️ PASO 4: Método `addMovement()` - Crear y agregar movimiento

**Concepto:** Este método recibe los datos de un movimiento, crea el objeto `Movement` y lo agrega a la lista.

**🎓 Flujo:**
```
Usuario → addMovement("ENTRADA", "Diesel", 100, 5000)
          ↓
          1. Crear: new Movement("ENTRADA", "Diesel", 100, 5000)
          ↓
          2. Agregar a lista: movements.add(nuevoMovement)
          ↓
          3. Retornar: el objeto creado
```

**Diagrama de tarea:**

```
Método addMovement
│
├── Firma del método:
│   • Tipo de retorno: Movement (porque retornamos el objeto creado)
│   • Nombre: addMovement
│   • Parámetros: (String movementType, String fuelType, double quantity, double unitPrice)
│
├── Lógica interna (3 pasos):
│   1. Crear nuevo objeto Movement con los parámetros
│   2. Agregar ese objeto a la lista 'movements'
│   3. Retornar el objeto creado
│
└── Javadoc (comentario de documentación):
    Explica qué hace, qué recibe, qué retorna
```

**Tarea:**

**TÚ debes escribir:**

1. El javadoc (comentario con `/** */`) explicando:
   - Qué hace el método
   - Qué significan los parámetros (`@param`)
   - Qué retorna (`@return`)

2. La firma del método (tipo retorno, nombre, parámetros)

3. Dentro del método:
   ```
   // Paso 1: Crear el objeto Movement
   Movement newMovement = new Movement(...parámetros...);
   
   // Paso 2: Agregarlo a la lista
   movements.add(newMovement);
   
   // Paso 3: Retornarlo
   return newMovement;
   ```

**Preguntas de comprensión:**
1. ¿Por qué retornamos `Movement` en vez de `void`?
2. ¿Cuándo se genera el ID del movimiento?
3. ¿Qué pasa si llamo `addMovement(null, null, -100, 0)`?

**💡 Reflexiona:**
1. **Útil para el caller:** Puede obtener el ID generado inmediatamente
2. **En el constructor de Movement:** `IdGenerator.generateMovementId()` se ejecuta automáticamente
3. **Validar datos:** En Fase 7 (Manejo de Errores) agregaremos validaciones con excepciones

---

### 🛠️ PASO 5: Método `getAllMovements()` - Obtener todos

**Concepto:** Retorna la lista completa de movimientos (útil para mostrar en pantalla).

**Diagrama de tarea:**

```
Método getAllMovements
│
├── Firma:
│   • Tipo de retorno: List<Movement>
│   • Nombre: getAllMovements
│   • Parámetros: ninguno
│
├── Lógica:
│   └── Retornar el atributo 'movements'
│
└── Javadoc:
    Explica que retorna todos los movimientos almacenados
```

**Tarea:**

**TÚ debes escribir** este método súper simple:
- Javadoc explicando qué hace
- Firma del método
- Un solo `return` con el atributo `movements`

**Pregunta de comprensión:**
- ¿Por qué este método es tan simple?

**💡 Respuesta:**
- **Delegación:** No necesita lógica compleja, solo exponer el atributo privado de forma controlada

---

### 🛠️ PASO 6: Método `findById()` - Buscar por ID

**Concepto:** Recorre la lista buscando un movimiento con el ID especificado.

**🎓 Algoritmo de búsqueda lineal:**
```
findById("MOV-ABC123")
    ↓
    Para cada Movement en la lista:
        ¿Este Movement tiene id = "MOV-ABC123"?
        ├─ Sí → Retornar ese Movement (salir del método)
        └─ No → Continuar con el siguiente
    ↓
    Si termina el bucle sin encontrar → Retornar null
```

**Diagrama de tarea:**

```
Método findById
│
├── Firma:
│   • Tipo de retorno: Movement (el objeto encontrado, o null)
│   • Nombre: findById
│   • Parámetros: (String id)
│
├── Lógica (bucle for-each):
│   └── for (Movement movement : movements) {
│           if (id del movement coincide con el parámetro id) {
│               return ese movement;
│           }
│       }
│       return null;  // Si llega aquí, no encontró nada
│
└── Javadoc:
    Explica que busca por ID y retorna null si no existe
```

**Tarea:**

**TÚ debes escribir:**

1. Javadoc con `@param` y `@return`

2. La firma del método

3. Bucle for-each:
   ```
   Sintaxis for-each:
   for (TipoElemento variableLocal : coleccion) {
       // Usar variableLocal aquí
   }
   ```

4. Dentro del bucle, comparar IDs:
   ```
   if (movement.getId().equals(id)) {
       return movement;
   }
   ```

5. Después del bucle: `return null;`

**Preguntas de comprensión:**
1. ¿Por qué usamos `equals()` en vez de `==` para comparar Strings?
2. ¿Qué significa `for (Movement movement : movements)`?
3. ¿Por qué retornamos `null` si no encontramos nada?

**💡 Reflexiona:**
1. **Comparación de contenido:** `==` compara referencias (ubicación en memoria), `equals()` compara el texto real
2. **For-each loop:** Sintaxis simplificada que recorre cada elemento automáticamente
3. **Convención Java:** `null` indica "no encontrado" (en Fase 8 aprenderás `Optional<Movement>` como alternativa más segura)

---

### 🛠️ PASO 7: Método `getMovementsByType()` - Filtrar por tipo

**Concepto:** Retorna solo los movimientos que sean "ENTRADA" o "SALIDA".

**🎓 Algoritmo de filtrado:**
```
getMovementsByType("ENTRADA")
    ↓
    1. Crear lista vacía llamada 'result'
    ↓
    2. Para cada Movement en 'movements':
       ¿Este Movement es tipo "ENTRADA"?
       ├─ Sí → Agregarlo a 'result'
       └─ No → Ignorarlo y continuar
    ↓
    3. Retornar 'result'
```

**Diagrama de tarea:**

```
Método getMovementsByType
│
├── Firma:
│   • Tipo de retorno: List<Movement>
│   • Nombre: getMovementsByType
│   • Parámetros: (String type)
│
├── Lógica:
│   1. Crear nueva lista vacía: result = new ArrayList<>();
│   2. Bucle for-each por movements:
│      if (movement.getMovementType().equals(type))
│          result.add(movement);
│   3. return result;
│
└── Javadoc:
    Explica que filtra por tipo ("ENTRADA" o "SALIDA")
```

**Tarea:**

**TÚ debes escribir:**
1. Javadoc con `@param` y `@return`
2. Firma del método
3. Crear lista vacía de resultado
4. Bucle for-each con condición if
5. Retornar la lista filtrada

**Pregunta de comprensión:**
- ¿Por qué creamos una nueva lista `result` en vez de modificar `movements`?

**💡 Respuesta:**
- **Inmutabilidad:** No queremos alterar la lista original, solo retornar una versión filtrada. El atributo `movements` debe mantener TODOS los movimientos siempre.

---

### 🛠️ PASO 8: Método `getTotalMovements()` - Contar total

**Concepto:** Retorna cuántos movimientos hay en total.

**Diagrama de tarea:**

```
Método getTotalMovements
│
├── Firma:
│   • Tipo de retorno: int
│   • Nombre: getTotalMovements
│   • Parámetros: ninguno
│
├── Lógica:
│   └── return movements.size();
│       (ArrayList tiene método .size() que retorna cantidad de elementos)
│
└── Javadoc:
    Explica que retorna la cantidad total
```

**Tarea:**

**TÚ debes escribir** este método súper simple que usa `.size()` de ArrayList.

**Reflexión:** 
Este es un método "delegador" - delega la funcionalidad a la implementación de `ArrayList`. No necesitas bucles ni contadores manuales.

---

### 🛠️ PASO 9: Métodos de cálculo - Totales y stock

**Concepto:** Métodos que calculan valores agregados (sumas, diferencias).

#### 9.1 - Calcular total de ENTRADAS:

**🎓 Algoritmo:**
```
calculateTotalEntered()
    ↓
    1. Variable acumulador: total = 0
    ↓
    2. Para cada Movement en movements:
       ¿Es tipo "ENTRADA"?
       └─ Sí → total += movement.getQuantity()
    ↓
    3. Retornar total
```

**Diagrama de tarea:**

```
Método calculateTotalEntered
│
├── Firma:
│   • Tipo de retorno: double
│   • Nombre: calculateTotalEntered
│   • Parámetros: ninguno
│
├── Lógica (patrón acumulador):
│   double total = 0;
│   for (cada movement) {
│       if (es tipo "ENTRADA") {
│           total += movement.getQuantity();
│       }
│   }
│   return total;
│
└── Javadoc:
    Explica que suma todas las cantidades de ENTRADAS
```

**Tarea: TÚ debes escribir** este método siguiendo el diagrama.

---

#### 9.2 - Calcular total de SALIDAS:

**Diagrama de tarea:**

```
Método calculateTotalExited
│
├── Firma:
│   • Tipo de retorno: double
│   • Nombre: calculateTotalExited
│   • Parámetros: ninguno
│
├── Lógica (idéntica a calculateTotalEntered pero con "SALIDA"):
│   double total = 0;
│   for (cada movement) {
│       if (es tipo "SALIDA") {
│           total += movement.getQuantity();
│       }
│   }
│   return total;
│
└── Javadoc:
    Explica que suma todas las cantidades de SALIDAS
```

**Tarea: TÚ debes escribir** este método (casi idéntico al anterior, cambia solo el filtro).

---

#### 9.3 - Calcular stock actual:

**Concepto:** Stock = ENTRADAS - SALIDAS

**Diagrama de tarea:**

```
Método getCurrentStock
│
├── Firma:
│   • Tipo de retorno: double
│   • Nombre: getCurrentStock
│   • Parámetros: ninguno
│
├── Lógica (reutilización de métodos):
│   return calculateTotalEntered() - calculateTotalExited();
│
└── Javadoc:
    Explica que calcula stock actual (diferencia entre entradas y salidas)
```

**Tarea: TÚ debes escribir** este método de UNA SOLA LÍNEA.

**Pregunta de comprensión:**
- ¿Por qué `getCurrentStock()` no tiene bucle `for`?

**💡 Respuesta:**
- **Reutilización de código (DRY - Don't Repeat Yourself):** Ya tenemos métodos que calculan entradas y salidas. No tiene sentido repetir esos bucles aquí. Simplemente llamamos a los métodos existentes y restamos.

---

## ✅ Checkpoint 2.5.3: Verificar que Compile

**Tarea:**

Abre una terminal en la raíz del proyecto y ejecuta:

```cmd
cd C:\Users\evert\Documents\github\forestech-cli-java
mvn clean compile
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
```

**Si hay errores:**
1. Lee el mensaje de error cuidadosamente
2. Revisa que todos los imports estén correctos
3. Verifica que los nombres de métodos coincidan
4. Asegúrate de cerrar todas las llaves `{}`

---

## ✅ Checkpoint 2.5.4: Probar `MovementManager` en `Main.java`

**Concepto:** Ahora que `MovementManager` existe, probémoslo creando un programa de prueba.

**📍 DÓNDE:** 
- Archivo: `Main.java`

**🎯 OBJETIVO:**
Escribir un programa que pruebe TODOS los métodos de `MovementManager` para verificar que funcionen correctamente.

**Estructura del programa de prueba:**

```
Programa de Prueba
│
├── 1. CREAR EL MANAGER
│   └── Instanciar MovementManager
│
├── 2. AGREGAR MOVIMIENTOS
│   ├── Agregar 2-3 ENTRADAS diferentes
│   └── Agregar 1-2 SALIDAS diferentes
│
├── 3. PROBAR getAllMovements()
│   └── Mostrar cuántos movimientos hay en total
│
├── 4. PROBAR findById()
│   ├── Buscar un movimiento que SÍ existe
│   └── Mostrar que lo encontró
│
├── 5. PROBAR getMovementsByType()
│   ├── Filtrar solo ENTRADAS
│   ├── Filtrar solo SALIDAS
│   └── Mostrar cuántos hay de cada tipo
│
└── 6. PROBAR MÉTODOS DE CÁLCULO
    ├── calculateTotalEntered()
    ├── calculateTotalExited()
    └── getCurrentStock()
```

**Tarea:**

En `Main.java`, **TÚ debes escribir:**

1. **Imports necesarios:**
   ```
   import com.forestech.managers.MovementManager;
   import com.forestech.models.Movement;
   import java.util.List;
   ```

2. **Banner de inicio:**
   ```
   Imprime un título bonito como:
   "🧪 TEST: MovementManager"
   ```

3. **Crear el manager:**
   ```
   MovementManager manager = new MovementManager();
   Imprimir: "✅ MovementManager creado"
   ```

4. **Agregar movimientos de prueba:**
   ```
   Llamar manager.addMovement(...) al menos 3 veces con datos diferentes:
   - 2 ENTRADAS (diferentes tipos de combustible)
   - 1 SALIDA
   
   Guardar el objeto retornado en variables (mov1, mov2, mov3)
   Para cada uno, imprimir su ID generado
   ```

5. **Obtener todos:**
   ```
   List<Movement> all = manager.getAllMovements();
   Imprimir: "Total: " + all.size() + " movimientos"
   ```

6. **Buscar por ID:**
   ```
   Usar el ID de mov1 (que guardaste antes)
   Movement found = manager.findById(mov1.getId());
   if (found != null) {
       Imprimir: "Encontrado: " + found.getFuelType()
   }
   ```

7. **Filtrar por tipo:**
   ```
   List<Movement> entradas = manager.getMovementsByType("ENTRADA");
   Imprimir: "Total ENTRADAS: " + entradas.size()
   
   List<Movement> salidas = manager.getMovementsByType("SALIDA");
   Imprimir: "Total SALIDAS: " + salidas.size()
   ```

8. **Cálculos:**
   ```
   Imprimir:
   - "Total litros ingresados: " + manager.calculateTotalEntered()
   - "Total litros despachados: " + manager.calculateTotalExited()
   - "Stock actual: " + manager.getCurrentStock()
   ```

9. **Mensaje final:**
   ```
   "✅ TODAS LAS PRUEBAS PASARON"
   ```

**Ejecutar:**

```cmd
cd C:\Users\evert\Documents\github\forestech-cli-java
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

**Resultado esperado:**

```
╔═══════════════════════════════════════╗
║   🧪 TEST: MovementManager            ║
╚═══════════════════════════════════════╝

✅ MovementManager creado

📝 Agregando movimientos...
   → MOV-XXXXXXXX agregado
   → MOV-YYYYYYYY agregado
   → MOV-ZZZZZZZZ agregado

📋 Todos los movimientos:
   Total: 3 movimientos

🔍 Buscar por ID: MOV-XXXXXXXX
   ✅ Encontrado: Diesel

🔽 Filtrar ENTRADAS:
   Total ENTRADAS: 2

🔼 Filtrar SALIDAS:
   Total SALIDAS: 1

💰 CÁLCULOS:
   Total litros ingresados: 1750.0
   Total litros despachados: 500.0
   Stock actual: 1250.0

✅ TODAS LAS PRUEBAS PASARON
```

**Si ves resultados similares (los IDs serán diferentes), ¡LO LOGRASTE! 🎉**

**Depuración recomendada:**

Coloca breakpoints en:
- La línea donde creas el manager
- Dentro del método `addMovement()` de MovementManager
- Dentro del bucle de `findById()`

Ejecuta en modo debug y observa:
- Cómo se inicializa el ArrayList vacío
- Cómo cada `add()` agrega elementos a la lista
- Cómo el bucle recorre la lista elemento por elemento

---

## 🎓 CONCEPTOS APRENDIDOS EN ESTA FASE

### ✅ Conceptos de POO:
- **Manager Pattern:** Clase que gestiona colecciones de objetos
- **Encapsulamiento:** Atributo `movements` es privado
- **Responsabilidad única:** Cada clase tiene un propósito claro

### ✅ Conceptos de Java:
- **Genéricos:** `List<Movement>` - colección tipada
- **ArrayList:** Implementación de `List` dinámica
- **For-each loop:** `for (Movement m : movements)`
- **Return types:** Métodos que retornan objetos vs primitivos

### ✅ Buenas prácticas:
- **Javadoc:** Comentarios `/** */` que documentan métodos
- **Nombres descriptivos:** `addMovement`, `findById`, etc.
- **Separación de responsabilidades:** Manager vs Model

---

## 🎯 PRÓXIMOS PASOS

### Ahora que tienes `MovementManager`:

1. **Fase 3:** Conectar a MySQL
   - `MovementManager` cambiará para usar base de datos
   - En vez de `ArrayList`, leeremos de `SELECT`
   - En vez de `.add()`, usaremos `INSERT`

2. **Fase 4:** Implementar CRUD completo
   - Métodos `update()`, `delete()`
   - Sincronización con BD

3. **Fase 5:** Lógica de negocio avanzada
   - Validaciones complejas
   - Reportes y estadísticas

---

## 📝 RESUMEN RÁPIDO

### ¿Qué hiciste hoy?

✅ Creaste el paquete `managers/`
✅ Creaste la clase `MovementManager`
✅ Implementaste 9 métodos de gestión:
   - `addMovement()` - Crear y agregar
   - `getAllMovements()` - Listar todos
   - `findById()` - Buscar por ID
   - `getMovementsByType()` - Filtrar
   - `getTotalMovements()` - Contar
   - `calculateTotalEntered()` - Suma entradas
   - `calculateTotalExited()` - Suma salidas
   - `getCurrentStock()` - Stock actual
✅ Probaste todo en `Main.java`

### ¿Qué aprendiste?

🎓 Qué es un Manager y por qué usarlo
🎓 Cómo trabajar con `ArrayList<T>`
🎓 Algoritmos de búsqueda y filtrado
🎓 Métodos de cálculo agregado
🎓 Separación de responsabilidades

---

## 🎉 ¡FELICIDADES!

Ahora sí estás listo para la **Fase 3: Conexión a MySQL**. 

Ya no necesitas almacenar movimientos solo en memoria - en la próxima fase los guardarás permanentemente en una base de datos real.

**Commit sugerido:**
```cmd
git add .
git commit -m "fase 2.5: crear paquete managers y clase MovementManager"
git push
```

---

**¿Listo para Fase 3?** 🚀

