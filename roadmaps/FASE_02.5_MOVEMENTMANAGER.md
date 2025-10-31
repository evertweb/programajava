# 🔧 FASE 2.5: CREACIÓN DEL PAQUETE `managers` Y CLASE `MovementManager`

> **CONTEXTO:** Este roadmap cierra el gap entre Fase 2 (POO) y Fase 3 (SQL). Es el componente que faltó explicar.

> **OBJETIVO GENERAL:** Aprender el patrón Manager y cómo gestionar colecciones de objetos de forma profesional.

---

## 🧠 Antes de empezar

**⚠️ IMPORTANTE: Prerequisitos que DEBES dominar**

Esta fase combina conceptos de Fase 1 y Fase 2. Si alguno de estos puntos NO te queda claro, **DETENTE** y refuérzalo primero.

### ✅ CHECKLIST DE PREREQUISITOS:

#### De Fase 1 (Fundamentos):
- [ ] **Variables y tipos:** Declarar `String`, `int`, `double`
- [ ] **Métodos:** Crear métodos con parámetros y `return`
- [ ] **Modificadores:** Entender `public` vs `private`
- [ ] **Control de flujo:** Usar `if`, `for`, `for-each`
- [ ] **Comparaciones:** Usar `.equals()` para Strings (NO `==`)

#### De Fase 2 (POO Básica):
- [ ] **Clases:** Crear clase con atributos privados
- [ ] **Constructores:** Inicializar atributos con `this`
- [ ] **Getters:** Obtener valores de atributos privados
- [ ] **Objetos:** Instanciar con `new`
- [ ] **Encapsulamiento:** Saber POR QUÉ usar `private`

#### De Fase 2.6 (Collections):
- [ ] **ArrayList:** Declarar `ArrayList<TipoDeDato>`
- [ ] **Inicialización:** Usar `new ArrayList<>()`
- [ ] **Agregar:** Método `.add(elemento)`
- [ ] **Tamaño:** Método `.size()`
- [ ] **Recorrer:** Usar `for-each` sobre la lista
- [ ] **Interfaz vs Implementación:** Diferencia entre `List` y `ArrayList`

### 🆕 CONCEPTOS NUEVOS QUE APRENDERÁS AQUÍ:

- 🏗️ **Patrón Manager/Service:** Clase que "gestiona" colecciones de otros objetos
- 📝 **Javadoc:** Documentación profesional con `/** */`, `@param`, `@return`
- 🔄 **Métodos que retornan objetos:** No solo `void`, ahora retornan instancias
- 🎯 **Separación de responsabilidades:** Main coordina, Manager gestiona
- 🔍 **Algoritmos básicos:** Búsqueda lineal, filtrado, acumulación

---

### 🤔 ¿SIENTES QUE OLVIDASTE ALGO?

**Es NORMAL.** Cuando pasas de conceptos simples a combinarlos, puede parecer que "olvidaste" lo anterior. En realidad, necesitas **reforzar** esos conceptos.

**Si tienes dudas con algún prerequisito:**

1. **NO continúes** hasta aclararlo
2. **Pregunta específicamente:** "No entiendo qué es un getter" (no "no entiendo nada")
3. **Practica** ese concepto aislado primero
4. **Regresa** aquí cuando te sientas seguro

**Recuerda:** Aprender programación es como construir una casa. Si los cimientos (Fase 1-2) están débiles, el segundo piso (Fase 2.5) se sentirá inestable.

---

### 📋 ESTRUCTURA DE ESTA FASE:

```
Checkpoint 2.5.1: Crear paquete 'managers/'
Checkpoint 2.5.2: Clase MovementManager completa
  ├─ Paso 1-3: Estructura básica (package, imports, constructor)
  ├─ Paso 4: addMovement() - Crear y agregar
  ├─ Paso 5: getAllMovements() - Obtener todos
  ├─ Paso 6: findById() - Buscar por ID
  ├─ Paso 7: getMovementsByType() - Filtrar por tipo
  ├─ Paso 8: getTotalMovements() - Contar
  └─ Paso 9: Métodos de cálculo (totales y stock)
Checkpoint 2.5.3: Probar en Main.java
Checkpoint 2.5.4: Debugging y validación
```

**Cada paso incluye:**
- 🎓 Analogía del mundo real
- 📋 Diagrama detallado
- 🔍 Pseudocódigo (NO código para copiar)
- 🎯 Ejemplo de ejecución
- 💭 Preguntas para reflexionar
- 📝 Tarea específica

---

## 🎯 FILOSOFÍA DE ESTA FASE:

> **"No te daré código para copiar. Te daré la LÓGICA para que TÚ escribas el código."**

**Lo que SÍ encontrarás:**
- ✅ Explicaciones detalladas
- ✅ Pseudocódigo con la lógica
- ✅ Diagramas paso a paso
- ✅ Ejemplos de ejecución

**Lo que NO encontrarás:**
- ❌ Código Java completo listo para copiar
- ❌ Snippets completos (excepto casos muy específicos)

**¿Por qué?** Porque copiar código NO te enseña a programar. Escribirlo TÚ mismo sí.

---

### 🗺️ MAPA CONCEPTUAL: ¿Cómo se conectan los conocimientos?

```
┌─────────────────────────────────────────────────────────────┐
│  FASE 1: FUNDAMENTOS                                        │
│  ─────────────────────                                      │
│  • Variables, tipos, operadores                             │
│  • Métodos con parámetros y return                          │
│  • if, for, for-each                                        │
│  • public vs private                                        │
│                                                             │
│         ↓  LO USAS AQUÍ  ↓                                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  FASE 2: POO BÁSICA                                         │
│  ──────────────                                             │
│  • Clase Movement (UNO)                                     │
│    └─ Atributos privados                                    │
│    └─ Constructor                                           │
│    └─ Getters/Setters                                       │
│                                                             │
│  • Crear objetos con 'new'                                  │
│                                                             │
│         ↓  LO EXPANDES AQUÍ  ↓                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  FASE 2.6: COLLECTIONS                                      │
│  ─────────────────────                                      │
│  • ArrayList<Movement> (MUCHOS)                             │
│  • Agregar con .add()                                       │
│  • Recorrer con for-each                                    │
│  • Buscar con if dentro del bucle                           │
│                                                             │
│         ↓  LO ORGANIZAS AQUÍ  ↓                             │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  FASE 2.5: MANAGER (ESTA FASE) ⭐                           │
│  ──────────────────────────────                             │
│                                                             │
│  MovementManager = Clase que AGRUPA funcionalidad           │
│                                                             │
│  ┌──────────────────────────────────────────┐              │
│  │ private List<Movement> movements;        │              │
│  │    ↑                                     │              │
│  │    Guarda TODOS los movimientos          │              │
│  │                                          │              │
│  │ + addMovement()     → Crear y agregar    │              │
│  │ + findById()        → Buscar (for-each)  │              │
│  │ + getMovementsByType() → Filtrar (if)    │              │
│  │ + calculateTotal()  → Sumar (acumulador) │              │
│  └──────────────────────────────────────────┘              │
│                                                             │
│  COMBINA: Clases + Objetos + ArrayList + Algoritmos         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Interpretación:**
- Fase 1: Herramientas básicas (martillo, clavos)
- Fase 2: Crear un ladrillo (Movement)
- Fase 2.6: Crear una pila de ladrillos (ArrayList)
- **Fase 2.5: Crear el arquitecto que gestiona la construcción (Manager)** ⭐

---

### 🎓 ANALOGÍA COMPLETA - La Planta Forestech:

```
╔════════════════════════════════════════════════════════════╗
║                    🏭 PLANTA FORESTECH                     ║
╠════════════════════════════════════════════════════════════╣
║                                                            ║
║  📄 Movement (Fase 2)                                      ║
║  └─ UNA orden de trabajo individual                       ║
║     "Entrada de 1000 litros de Diesel"                    ║
║                                                            ║
║  📚 ArrayList<Movement> (Fase 2.6)                         ║
║  └─ ARCHIVADOR con todas las órdenes                      ║
║     [MOV-001, MOV-002, MOV-003, ...]                       ║
║                                                            ║
║  👨‍💼 MovementManager (Fase 2.5) ⭐                          ║
║  └─ JEFE DE OPERACIONES que:                               ║
║     • Registra nuevas órdenes (addMovement)                ║
║     • Busca órdenes específicas (findById)                 ║
║     • Filtra por tipo (getMovementsByType)                 ║
║     • Calcula totales (calculateTotal)                     ║
║     • Conoce el stock actual (getCurrentStock)             ║
║                                                            ║
║  Main.java (Fase 6)                                        ║
║  └─ GERENTE GENERAL que coordina todo                      ║
║     "Oye Jefe de Operaciones, registra esta entrada"      ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

---

- 📐 **Revisa tu estructura actual:** Debes tener `Movement.java` completo en el paquete `models/`
- 🎯 **Pregúntate:** "Si tengo 100 movimientos, ¿dónde los guardo? ¿Cómo los busco? ¿Cómo los filtro?"
- 📚 **Concepto nuevo:** Patrón Manager/Service - separa la lógica de gestión de colecciones
- 🐞 **Refuerza depuración:** Usarás breakpoints para ver cómo se llenan las listas
- 🔁 **Git loop:** Al completar cada checkpoint crea un commit claro
- ⏱️ **Tiempo estimado:** 2-3 horas

---

## 🔗 CONEXIÓN CON FASE 2: de ArrayList en `Main` a `MovementManager`

### ¿Qué ya dominas?
- ✅ Crear objetos `Movement` con constructores y getters/setters
- ✅ Trabajar con `ArrayList<Movement>` directamente dentro de `Main.java`
- ✅ Recorrer la lista con `for` y `for-each`, filtrar y acumular totales (Checkpoint 2.6)

### ¿Qué cambia en Fase 2.5?
- 🔁 **Refactor:** Mueve la lógica de listas desde `Main.java` hacia una clase dedicada (`MovementManager`)
- � **Nueva idea:** Aprenderás el patrón Manager/Service para mantener responsabilidades separadas
- 🚪 **Preparación:** Deja listo el camino para conectar con la base de datos en Fase 3

### Roadmap express
```
Fase 2.6 → Practicas ArrayList en Main.java
Fase 2.5 → Encapsulas esa lista dentro de MovementManager
Fase 3   → MovementManager hablará con la base de datos
```

Antes de continuar, asegúrate de que los ejercicios de ArrayList en `Main.java` te resulten cómodos. Si todavía cuestan, vuelve a practicarlos un rato y regresa aquí cuando te sientas listo. 💪

---

## �🤔 ¿POR QUÉ NECESITAMOS `MovementManager`?

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

**🎓 Analogía del Mundo Real (ligada a Forestech):**

```
╔══════════════════════════════════════════════════════════╗
║        ⛽ PLANTA FORESTECH (Gestión de Combustible)       ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  �️ Movimiento (Movement)                                ║
║  └─ Representa UNA operación puntual                     ║
║     • id: "MOV-001"                                      ║
║     • tipo: "ENTRADA" o "SALIDA"                        ║
║     • combustible: "Diésel"                              ║
║     • cantidad: 1200 litros                              ║
║                                                          ║
║  📋 MovementManager (Jefe de Patio)                      ║
║  └─ Coordina TODAS las operaciones del depósito:         ║
║     • Registrar cada descarga o carga                     ║
║     • Buscar un movimiento por su folio                  ║
║     • Listar todo lo que ocurrió en el turno              ║
║     • Filtrar por tipo para auditorías (entradas/salidas) ║
║     • Calcular stock disponible en los tanques            ║
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

### 🧠 Recordatorio rápido: `List` vs `ArrayList`
- `List<Movement>` 👉 interfaz que representa "una lista de movimientos" (no importa cómo esté implementada)
- `ArrayList<Movement>` 👉 implementación concreta basada en un arreglo dinámico
- **Regla práctica:** declara atributos y parámetros usando la interfaz (`List`) y crea la instancia con la implementación (`new ArrayList<>()`). Así podrás cambiar la implementación en el futuro sin tocar el resto del código.

> Pregunta para ti: ¿qué ocurriría si mañana te piden usar `LinkedList`? Con esta estrategia solo cambiarías la línea del `new`.

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

---

#### 🎓 Analogía del mundo real:

```
Imagina una oficina de registro de vehículos:

1. LLEGA el ciudadano con datos: "Quiero registrar un auto Toyota, modelo 2024"
   ↓
2. EMPLEADO crea la ficha: Toma una hoja, escribe los datos, genera folio automático
   ↓
3. EMPLEADO archiva: Mete la ficha en el archivador
   ↓
4. EMPLEADO entrega: Le da al ciudadano una copia con el folio

addMovement hace exactamente eso:
- Recibe los datos del movimiento
- Crea el objeto Movement (con ID automático)
- Lo guarda en la lista
- Devuelve el objeto creado (para que sepas el ID)
```

---

#### 📋 DIAGRAMA DETALLADO - Anatomía del método:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: addMovement                                           ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  📝 JAVADOC (Documentación)                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /** Tres asteriscos abren un bloque de documentación     │ ║
║  │  * Descripción: Qué hace este método                     │ ║
║  │  * @param nombreParam1 - Descripción parámetro 1         │ ║
║  │  * @param nombreParam2 - Descripción parámetro 2         │ ║
║  │  * @return Qué cosa devuelve el método                   │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA DEL MÉTODO (La declaración)                          ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ [público] [tipo que retorna] nombreMetodo([parámetros])  │ ║
║  │                                                           │ ║
║  │ Ejemplo descompuesto:                                    │ ║
║  │ • public        → Accesible desde fuera                  │ ║
║  │ • Movement      → Retorna UN objeto de tipo Movement     │ ║
║  │ • addMovement   → Nombre descriptivo                     │ ║
║  │ • (String tipo, String combustible, ...)                 │ ║
║  │   └─ Los datos que necesita recibir                      │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 CUERPO DEL MÉTODO (La lógica)                              ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ {  // Abre llave                                         │ ║
║  │                                                           │ ║
║  │   PASO 1: Crear el objeto nuevo                          │ ║
║  │   ─────────────────────────────                          │ ║
║  │   DECLARA variable tipo Movement                         │ ║
║  │   ASIGNA nuevo objeto usando constructor de Movement     │ ║
║  │   (El constructor genera el ID automáticamente)          │ ║
║  │                                                           │ ║
║  │   PASO 2: Agregar a la colección                         │ ║
║  │   ────────────────────────────                           │ ║
║  │   LLAMA al método .add() de la lista 'movements'         │ ║
║  │   PASA como argumento el objeto creado en Paso 1         │ ║
║  │                                                           │ ║
║  │   PASO 3: Devolver el objeto                             │ ║
║  │   ────────────────────────                               │ ║
║  │   USA return para devolver el objeto                     │ ║
║  │   (Así quien llamó el método puede obtener el ID)        │ ║
║  │                                                           │ ║
║  │ }  // Cierra llave                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### 🔍 PSEUDOCÓDIGO DETALLADO:

```
PSEUDOCÓDIGO:

DOCUMENTAR método con javadoc:
  DESCRIBIR: "Este método crea un movimiento y lo almacena"
  PARA CADA parámetro:
    AGREGAR línea @param explicando qué es
  AGREGAR línea @return explicando qué devuelve

DECLARAR método público que retorna tipo Movement llamado addMovement:
  RECIBE parámetros:
    - movementType de tipo String (puede ser "ENTRADA" o "SALIDA")
    - fuelType de tipo String (tipo de combustible)
    - quantity de tipo double (cantidad en litros)
    - unitPrice de tipo double (precio por litro)
  
  DENTRO del método:
  
    // --- PASO 1: CREAR ---
    DECLARAR variable local llamada 'newMovement' de tipo Movement
    ASIGNAR a esa variable un NUEVO objeto Movement usando:
      - Constructor de Movement que recibe 4 parámetros
      - Pasar los 4 parámetros recibidos en el método
      - (Internamente Movement generará su propio ID)
    
    // --- PASO 2: GUARDAR ---
    TOMAR el atributo 'movements' (nuestra lista)
    LLAMAR su método .add()
    PASAR como argumento 'newMovement'
    (Ahora la lista tiene un elemento más)
    
    // --- PASO 3: DEVOLVER ---
    RETORNAR la variable 'newMovement'
    (El código que llamó este método recibirá el objeto)

FIN del método
```

---

#### 🎯 EJEMPLO DE USO (Contexto):

```
ESCENARIO: Usuario registra entrada de 1000 litros de Diesel

ANTES de llamar addMovement:
  - La lista 'movements' tiene 0 elementos
  - No existe ningún objeto Movement para ese registro

DURANTE la llamada addMovement("ENTRADA", "Diesel", 1000.0, 5000.0):
  PASO 1: Se crea objeto Movement
    → id = "MOV-001" (generado automáticamente)
    → movementType = "ENTRADA"
    → fuelType = "Diesel"
    → quantity = 1000.0
    → unitPrice = 5000.0
    
  PASO 2: Se agrega a la lista
    → movements.size() cambia de 0 a 1
    
  PASO 3: Se retorna el objeto
    → Quien llamó el método recibe el objeto completo

DESPUÉS de ejecutar:
  - La lista 'movements' tiene 1 elemento
  - El objeto Movement existe en memoria
  - El método devolvió el objeto con su ID generado
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

**Antes de escribir el código, responde mentalmente:**

1. **¿Por qué el método retorna `Movement` y no `void`?**
   - Pista: ¿Qué información podría necesitar quien llama este método?
   - Respuesta: Para obtener el ID generado automáticamente

2. **¿Cuándo se genera el ID del movimiento?**
   - Pista: ¿Dónde está el código de generación de ID?
   - Respuesta: En el constructor de Movement, no en este método

3. **¿Qué pasa si alguien llama `addMovement(null, null, -100, 0)`?**
   - Pista: ¿Hay validaciones actualmente?
   - Respuesta: Por ahora no hay validaciones (las agregaremos en Fase 7)

4. **¿Por qué creamos una variable `newMovement` en vez de agregarlo directamente?**
   - Pista: ¿Qué necesitamos hacer después de crearlo?
   - Respuesta: Necesitamos guardarlo Y retornarlo, no solo una cosa

---

#### 📝 TU TAREA AHORA:

**NO copies código. En su lugar:**

1. **ABRE** el archivo `MovementManager.java`

2. **ESCRIBE** el Javadoc:
   - Usa `/**` para abrir
   - Describe qué hace el método
   - Agrega `@param` para CADA parámetro
   - Agrega `@return` explicando qué devuelve
   - Cierra con `*/`

3. **ESCRIBE** la firma del método:
   - Piensa: ¿Qué modificador de acceso? (public)
   - Piensa: ¿Qué retorna? (Movement)
   - Piensa: ¿Qué parámetros necesita? (4 parámetros)

4. **ESCRIBE** el cuerpo siguiendo el pseudocódigo:
   - Paso 1: Declarar + crear objeto
   - Paso 2: Agregar a lista
   - Paso 3: Retornar

5. **COMPILA** con `mvn clean compile`

6. **SI HAY ERRORES:** No los veas como fracasos, son pistas de aprendizaje

---

#### 🆘 AYUDAS PUNTUALES (solo si te atoras):

**Si no sabes cómo:**
- Escribir Javadoc → Pregunta: "¿Cómo escribo un Javadoc?"
- Declarar la firma → Pregunta: "¿Cuál es la sintaxis de la firma?"
- Crear el objeto → Pregunta: "¿Cómo instancio Movement?"
- Usar .add() → Pregunta: "¿Cómo agrego a un ArrayList?"

**No pidas código completo. Pide explicaciones específicas.** 🎯

---

### 🛠️ PASO 5: Método `getAllMovements()` - Obtener todos

**Concepto:** Retorna la lista completa de movimientos (útil para mostrar en pantalla).

---

#### 🎓 Analogía del mundo real:

```
Imagina el archivador de documentos:

📁 Archivador (movements) → tiene muchos documentos dentro

Jefe pregunta: "Dame TODOS los documentos"
Empleado (getAllMovements): "Aquí está el archivador completo"

No necesita buscar, filtrar ni ordenar.
Solo entrega TODO lo que tiene.
```

---

#### 📋 DIAGRAMA DETALLADO:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: getAllMovements (GETTER de la colección)             ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  🎯 PROPÓSITO: Dar acceso de LECTURA a la lista completa      ║
║                                                                ║
║  📝 JAVADOC                                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /**                                                       │ ║
║  │  * Descripción corta de qué devuelve                     │ ║
║  │  * @return Qué tipo de colección y qué contiene          │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA                                                      ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ public List<Movement> getAllMovements()                  │ ║
║  │   │      │              │               │                │ ║
║  │   │      │              │               └─ Sin parámetros│ ║
║  │   │      │              └─ Nombre descriptivo            │ ║
║  │   │      └─ Retorna una lista de Movement                │ ║
║  │   └─ Accesible públicamente                              │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 CUERPO (¡Súper simple!)                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ {                                                         │ ║
║  │   RETORNAR el atributo 'movements'                       │ ║
║  │ }                                                         │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### 🔍 PSEUDOCÓDIGO:

```
PSEUDOCÓDIGO:

DOCUMENTAR con javadoc:
  DESCRIBIR: "Obtiene todos los movimientos almacenados"
  AGREGAR @return: "Lista completa de movimientos"

DECLARAR método público que retorna List<Movement> llamado getAllMovements:
  SIN parámetros
  
  DENTRO del método:
    RETORNAR el atributo 'movements'
    (Es una sola línea: return movements;)

FIN del método
```

---

#### 🎯 EJEMPLO DE USO:

```
ESCENARIO: Mostrar todos los movimientos en pantalla

ESTADO de MovementManager:
  - movements contiene 3 elementos:
    [0] Movement(id="MOV-001", type="ENTRADA", ...)
    [1] Movement(id="MOV-002", type="SALIDA", ...)
    [2] Movement(id="MOV-003", type="ENTRADA", ...)

LLAMADA: manager.getAllMovements()

RETORNA: La lista completa con los 3 elementos

EL CÓDIGO QUE LLAMÓ puede entonces:
  - Recorrer con for-each
  - Mostrar en pantalla
  - Pasarla a otro método
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

**Antes de escribir:**

1. **¿Por qué este método es tan simple?**
   - Respuesta: No necesita lógica compleja, solo expone el atributo

2. **¿Por qué no hacemos el atributo `movements` público directamente?**
   - Respuesta: **Encapsulamiento** - controlamos el acceso
   - Si el atributo fuera público, cualquiera podría hacer `manager.movements = null`

3. **¿Retorna una copia o la lista original?**
   - Respuesta: Por ahora, la original (en Fase 8 aprenderás a retornar copias)

4. **¿Qué pasa si la lista está vacía?**
   - Respuesta: Retorna un ArrayList vacío (size = 0), no null

---

#### 📝 TU TAREA:

1. **ESCRIBE** el Javadoc (breve)
2. **ESCRIBE** la firma del método
3. **ESCRIBE** el cuerpo (una sola línea con return)
4. **COMPILA** para verificar

---

### 🛠️ PASO 6: Método `findById()` - Buscar por ID

**Concepto:** Recorre la lista buscando un movimiento con el ID especificado.

---

#### 🎓 Analogía del mundo real:

```
Imagina buscar un documento en el archivador:

Jefe: "Necesito el expediente MOV-ABC123"
Empleado (findById):
  1. Abre el archivador
  2. Toma el primer documento, lee su folio
     ¿Es MOV-ABC123? No → Toma el siguiente
  3. Lee el folio del segundo
     ¿Es MOV-ABC123? Sí → ¡Lo encontró! Se lo entrega al jefe
  
Si revisa todos y no encuentra:
  "Lo siento, ese expediente no existe" (retorna null)
```

---

#### 📋 DIAGRAMA DETALLADO - Algoritmo de Búsqueda:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: findById (Búsqueda Lineal)                           ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  🎯 CONCEPTO: Búsqueda secuencial elemento por elemento       ║
║                                                                ║
║  📝 JAVADOC                                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /**                                                       │ ║
║  │  * Busca un movimiento específico por su ID              │ ║
║  │  * @param id - El identificador único del movimiento     │ ║
║  │  * @return El objeto Movement si existe, null si no      │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA                                                      ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ public Movement findById(String id)                      │ ║
║  │   │       │         │       │                            │ ║
║  │   │       │         │       └─ Parámetro: ID a buscar   │ ║
║  │   │       │         └─ Nombre descriptivo               │ ║
║  │   │       └─ Retorna UN Movement o null                 │ ║
║  │   └─ Acceso público                                      │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 LÓGICA DEL ALGORITMO                                       ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ PARA CADA movimiento en la lista 'movements':           │ ║
║  │   │                                                       │ ║
║  │   ├─ OBTENER el id de ese movimiento                    │ ║
║  │   │  (usando el getter getId())                          │ ║
║  │   │                                                       │ ║
║  │   ├─ COMPARAR ese id con el parámetro recibido          │ ║
║  │   │  (usando .equals() para comparar Strings)           │ ║
║  │   │                                                       │ ║
║  │   └─ SI SON IGUALES:                                     │ ║
║  │       └─ RETORNAR ese movimiento inmediatamente         │ ║
║  │          (sale del método, no sigue buscando)           │ ║
║  │                                                           │ ║
║  │ SI TERMINA EL BUCLE sin haber encontrado:                │ ║
║  │   └─ RETORNAR null                                       │ ║
║  │      (significa "no existe en la colección")            │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### 🔍 PSEUDOCÓDIGO DETALLADO:

```
PSEUDOCÓDIGO:

DOCUMENTAR con javadoc:
  DESCRIBIR: "Busca un movimiento por su identificador único"
  AGREGAR @param id: "El ID del movimiento a buscar"
  AGREGAR @return: "El objeto Movement si se encuentra, null en caso contrario"

DECLARAR método público que retorna Movement llamado findById:
  RECIBE parámetro 'id' de tipo String
  
  DENTRO del método:
  
    // --- BÚSQUEDA CON FOR-EACH ---
    PARA CADA elemento en 'movements':
      (Usa un for-each, la variable temporal podría llamarse 'movement')
      
      // Obtener el ID del movimiento actual
      DECLARAR variable temporal para guardar el id del movimiento actual
      ASIGNAR el resultado de llamar movement.getId()
      
      // Comparar IDs
      SI el id del movimiento actual ES IGUAL AL parámetro 'id':
        (Usa .equals() para comparar Strings, NO uses ==)
        
        ENTONCES:
          RETORNAR el movimiento actual
          (Esto termina el método inmediatamente)
      
      FIN del SI
    
    FIN del FOR-EACH
    
    // Si llegamos aquí, significa que no se encontró
    RETORNAR null

FIN del método
```

---

#### 🎯 EJEMPLO DE EJECUCIÓN PASO A PASO:

```
ESCENARIO: Buscar el movimiento "MOV-002"

ESTADO INICIAL de movements:
  [0] Movement(id="MOV-001", type="ENTRADA", ...)
  [1] Movement(id="MOV-002", type="SALIDA", ...)
  [2] Movement(id="MOV-003", type="ENTRADA", ...)

LLAMADA: manager.findById("MOV-002")

EJECUCIÓN:
  Iteración 1:
    - movement = movements[0]
    - movement.getId() devuelve "MOV-001"
    - ¿"MOV-001".equals("MOV-002")? NO
    - Continúa al siguiente
  
  Iteración 2:
    - movement = movements[1]
    - movement.getId() devuelve "MOV-002"
    - ¿"MOV-002".equals("MOV-002")? SÍ ✅
    - return movement (termina el método)
  
  (Iteración 3 nunca se ejecuta)

RETORNA: El objeto Movement con id="MOV-002"

---

CASO 2: Buscar un ID que NO existe

LLAMADA: manager.findById("MOV-999")

EJECUCIÓN:
  Iteración 1: "MOV-001" != "MOV-999" → Continúa
  Iteración 2: "MOV-002" != "MOV-999" → Continúa
  Iteración 3: "MOV-003" != "MOV-999" → Continúa
  
  Fin del for-each, no se encontró nada

RETORNA: null
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

**Antes de escribir:**

1. **¿Por qué retornamos `Movement` y no `boolean`?**
   - Respuesta: Queremos el objeto completo, no solo saber si existe

2. **¿Por qué usamos `.equals()` y no `==` para comparar Strings?**
   - Respuesta: `==` compara referencias de memoria, `.equals()` compara contenido
   - Ejemplo: `"MOV-001" == "MOV-001"` puede ser false (diferentes objetos)
   - Ejemplo: `"MOV-001".equals("MOV-001")` siempre es true (mismo contenido)

3. **¿Por qué retornamos `null` y no lanzamos una excepción?**
   - Respuesta: No encontrar un elemento es un caso normal, no un error
   - Fase 7: Aprenderás cuándo usar excepciones vs null

4. **¿Por qué el método termina al encontrar el primer match?**
   - Respuesta: Los IDs son únicos, no puede haber dos movimientos con el mismo ID

5. **¿Qué hace quien llama este método con el resultado?**
   - Respuesta: Debe validar si es null antes de usarlo
   - Ejemplo: `if (movement != null) { ... }`

---

#### 🔧 CONCEPTOS TÉCNICOS CLAVE:

**1. FOR-EACH (repaso rápido):**
```
SINTAXIS GENERAL:
  for (TipoDeDato nombreVariable : colección) {
    // Usar nombreVariable aquí
  }

EN ESTE CASO:
  for (Movement movement : movements) {
    // 'movement' es cada elemento individual
  }
```

**2. COMPARACIÓN DE STRINGS:**
```
❌ INCORRECTO:
  if (movement.getId() == id)

✅ CORRECTO:
  if (movement.getId().equals(id))
```

**3. RETURN DENTRO DE BUCLES:**
```
Cuando haces 'return' dentro de un for:
  - El método TERMINA inmediatamente
  - El bucle NO continúa
  - Es útil para búsquedas (no necesitas seguir buscando)
```

---

#### 📝 TU TAREA:

**Recuerda: NO copies código, ESCRIBE siguiendo el pseudocódigo**

1. **ESCRIBE** el Javadoc

2. **ESCRIBE** la firma del método

3. **ESCRIBE** el bucle for-each:
   - Sintaxis: `for (tipo variable : colección)`
   - Piensa: ¿Qué tipo? ¿Qué colección?

4. **DENTRO DEL BUCLE:**
   - Obtén el ID del movimiento actual (¿qué getter usas?)
   - Compara con el parámetro (¿qué método de String usas?)
   - Si coincide, retorna

5. **DESPUÉS DEL BUCLE:**
   - Retorna null (caso: no se encontró)

6. **COMPILA** y verifica

---

#### 🆘 AYUDAS PUNTUALES:

**Si no sabes cómo:**
- Escribir for-each → Pregunta: "¿Sintaxis del for-each?"
- Comparar Strings → Pregunta: "¿Cómo comparo Strings en Java?"
- Retornar dentro del bucle → Pregunta: "¿Puedo hacer return dentro de un for?"

**No pidas el código completo.** 🎯

---

### 🛠️ PASO 7: Método `getMovementsByType()` - Filtrar por tipo

**Concepto:** Retorna solo los movimientos que coincidan con el tipo solicitado ("ENTRADA" o "SALIDA").

---

#### 🎓 Analogía del mundo real:

```
Imagina el archivador con documentos de varios tipos:

Auditor: "Dame todos los documentos tipo ENTRADA"
Empleado (getMovementsByType):
  1. Toma una carpeta nueva y vacía
  2. Revisa documento por documento:
     - Si es ENTRADA → Lo COPIA a la carpeta nueva
     - Si es SALIDA → Lo deja donde está, no lo toca
  3. Entrega la carpeta nueva con solo las ENTRADAS

IMPORTANTE: Los documentos originales quedan intactos
```

---

#### 📋 DIAGRAMA DETALLADO - Algoritmo de Filtrado:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: getMovementsByType (Filtrado)                        ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  🎯 CONCEPTO: Crear una nueva lista con solo los que cumplen  ║
║               una condición                                    ║
║                                                                ║
║  📝 JAVADOC                                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /**                                                       │ ║
║  │  * Filtra movimientos por su tipo                        │ ║
║  │  * @param type - "ENTRADA" o "SALIDA"                    │ ║
║  │  * @return Lista con solo los movimientos de ese tipo    │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA                                                      ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ public List<Movement> getMovementsByType(String type)    │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 LÓGICA (3 pasos)                                           ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │                                                           │ ║
║  │  PASO 1: CREAR lista temporal para resultados           │ ║
║  │  ────────────────────────────────────────                │ ║
║  │  DECLARAR variable 'result' tipo List<Movement>          │ ║
║  │  INICIALIZAR con new ArrayList<>()                       │ ║
║  │  (Empieza vacía, iremos agregando elementos)            │ ║
║  │                                                           │ ║
║  │  PASO 2: FILTRAR elementos                               │ ║
║  │  ───────────────────                                     │ ║
║  │  PARA CADA movement en movements:                        │ ║
║  │    │                                                      │ ║
║  │    ├─ OBTENER el tipo de ese movement                   │ ║
║  │    │  (usando getMovementType())                         │ ║
║  │    │                                                      │ ║
║  │    ├─ COMPARAR con el parámetro 'type'                  │ ║
║  │    │  (usando .equals())                                 │ ║
║  │    │                                                      │ ║
║  │    └─ SI COINCIDE:                                       │ ║
║  │        └─ AGREGAR ese movement a 'result'               │ ║
║  │           (result.add(movement))                         │ ║
║  │                                                           │ ║
║  │  FIN del FOR-EACH                                        │ ║
║  │                                                           │ ║
║  │  PASO 3: DEVOLVER la lista filtrada                      │ ║
║  │  ────────────────────────────────                        │ ║
║  │  RETORNAR 'result'                                       │ ║
║  │  (Puede estar vacía si no hubo coincidencias)           │ ║
║  │                                                           │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### 🔍 PSEUDOCÓDIGO DETALLADO:

```
PSEUDOCÓDIGO:

DOCUMENTAR con javadoc:
  DESCRIBIR: "Filtra y retorna movimientos de un tipo específico"
  AGREGAR @param type: "Tipo de movimiento a filtrar (ENTRADA o SALIDA)"
  AGREGAR @return: "Lista con los movimientos que coinciden con el tipo"

DECLARAR método público que retorna List<Movement> llamado getMovementsByType:
  RECIBE parámetro 'type' de tipo String
  
  DENTRO del método:
  
    // --- PASO 1: PREPARAR CONTENEDOR ---
    DECLARAR variable 'result' de tipo List<Movement>
    INICIALIZAR con new ArrayList<>()
    (Esta lista acumulará los movimientos filtrados)
    
    // --- PASO 2: FILTRAR ---
    PARA CADA movement en la lista 'movements':
      
      // Obtener el tipo del movimiento actual
      OBTENER movement.getMovementType()
      
      // Comparar con el parámetro
      SI ese tipo ES IGUAL AL parámetro 'type':
        (Usa .equals() para comparar Strings)
        
        ENTONCES:
          AGREGAR 'movement' a la lista 'result'
          (result.add(movement))
      
      FIN del SI
      // Si no coincide, simplemente continúa con el siguiente
    
    FIN del FOR-EACH
    
    // --- PASO 3: DEVOLVER RESULTADO ---
    RETORNAR 'result'
    (Puede tener 0, 1, o muchos elementos)

FIN del método
```

---

#### 🎯 EJEMPLO DE EJECUCIÓN:

```
ESTADO INICIAL de movements:
  [0] Movement(id="MOV-001", type="ENTRADA", quantity=1000)
  [1] Movement(id="MOV-002", type="SALIDA", quantity=500)
  [2] Movement(id="MOV-003", type="ENTRADA", quantity=800)
  [3] Movement(id="MOV-004", type="SALIDA", quantity=200)

LLAMADA: manager.getMovementsByType("ENTRADA")

EJECUCIÓN PASO A PASO:

  1. Se crea 'result' = [] (vacía)
  
  2. FOR-EACH comienza:
  
     Iteración 1 (MOV-001):
       - movement.getMovementType() = "ENTRADA"
       - ¿"ENTRADA".equals("ENTRADA")? SÍ ✅
       - result.add(movement)
       - result ahora = [MOV-001]
     
     Iteración 2 (MOV-002):
       - movement.getMovementType() = "SALIDA"
       - ¿"SALIDA".equals("ENTRADA")? NO
       - No se agrega, result sigue = [MOV-001]
     
     Iteración 3 (MOV-003):
       - movement.getMovementType() = "ENTRADA"
       - ¿"ENTRADA".equals("ENTRADA")? SÍ ✅
       - result.add(movement)
       - result ahora = [MOV-001, MOV-003]
     
     Iteración 4 (MOV-004):
       - movement.getMovementType() = "SALIDA"
       - ¿"SALIDA".equals("ENTRADA")? NO
       - No se agrega, result sigue = [MOV-001, MOV-003]
  
  3. Se retorna 'result'

RETORNA: Lista con 2 elementos [MOV-001, MOV-003]

---

CASO ESPECIAL: No hay coincidencias

LLAMADA: manager.getMovementsByType("TRANSFERENCIA")

RESULTADO: Lista vacía [] (NO null, es una lista pero con size=0)
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

1. **¿Por qué creamos una nueva lista `result` en vez de modificar `movements`?**
   - Respuesta: **Inmutabilidad** - nunca alteramos la colección original
   - Si modificáramos `movements`, perderíamos datos

2. **¿Qué pasa si el tipo no existe (ej: "TRANSFERENCIA")?**
   - Respuesta: Retorna lista vacía (size=0), no null

3. **¿Esta lista es una copia de los objetos o referencias?**
   - Respuesta: Son **referencias** a los mismos objetos
   - Si modificas un Movement en la lista retornada, afecta el original

4. **¿Por qué usamos `List<Movement>` y no solo `Movement[]`?**
   - Respuesta: No sabemos cuántos elementos habrá, List es dinámico

---

#### 📝 TU TAREA:

1. **ESCRIBE** el Javadoc
2. **ESCRIBE** la firma del método
3. **DECLARA** la lista `result` vacía
4. **ESCRIBE** el for-each con el if de filtrado
5. **RETORNA** result
6. **COMPILA**

---

### 🛠️ PASO 8: Método `getTotalMovements()` - Contar total

**Concepto:** Retorna cuántos movimientos hay almacenados.

---

#### 🎓 Analogía del mundo real:

```
Jefe: "¿Cuántos documentos tenemos en total?"
Empleado (getTotalMovements):
  "Déjame contar... tenemos 42 documentos"
  
No necesita leer los documentos, solo contarlos.
```

---

#### 📋 DIAGRAMA DETALLADO:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: getTotalMovements (Contador simple)                  ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  🎯 PROPÓSITO: Obtener la cantidad de elementos                ║
║                                                                ║
║  📝 JAVADOC                                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /**                                                       │ ║
║  │  * Obtiene el número total de movimientos                │ ║
║  │  * @return Cantidad de movimientos almacenados           │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA                                                      ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ public int getTotalMovements()                           │ ║
║  │   │     │        │                                       │ ║
║  │   │     │        └─ Nombre descriptivo                   │ ║
║  │   │     └─ Retorna un número entero                     │ ║
║  │   └─ Acceso público                                      │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 CUERPO (¡Una sola línea!)                                  ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ {                                                         │ ║
║  │   RETORNAR movements.size()                              │ ║
║  │ }                                                         │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### 🔍 PSEUDOCÓDIGO:

```
PSEUDOCÓDIGO:

DOCUMENTAR con javadoc:
  DESCRIBIR: "Calcula el total de movimientos almacenados"
  AGREGAR @return: "Número entero con la cantidad"

DECLARAR método público que retorna int llamado getTotalMovements:
  SIN parámetros
  
  DENTRO del método:
    LLAMAR al método .size() de la lista 'movements'
    RETORNAR ese valor

FIN del método
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

1. **¿Por qué retornamos `int` y no `long`?**
   - Respuesta: Un `int` puede almacenar hasta 2 mil millones, más que suficiente

2. **¿Qué retorna si la lista está vacía?**
   - Respuesta: 0 (no null, no error)

3. **¿Por qué no usamos un bucle for para contar?**
   - Respuesta: ArrayList ya implementa ese conteo internamente con `.size()`

---

#### 📝 TU TAREA:

1. **ESCRIBE** el Javadoc (muy breve)
2. **ESCRIBE** la firma del método (retorna int, sin parámetros)
3. **RETORNA** movements.size() en una línea
4. **COMPILA**

---

### 🛠️ PASO 9: Métodos de Cálculo - Totales y Stock

**Concepto:** Métodos que calculan valores agregados recorriendo la colección.

---

#### 🎓 Analogía del mundo real:

```
Contador en la planta de combustible:

Tarea 1: ¿Cuánto combustible ENTRÓ en total?
  - Revisa cada entrada
  - Suma las cantidades
  - Retorna el total

Tarea 2: ¿Cuánto combustible SALIÓ en total?
  - Revisa cada salida
  - Suma las cantidades
  - Retorna el total

Tarea 3: ¿Cuánto stock ACTUAL tenemos?
  - Total entrado MENOS total salido
```

---

### 🛠️ PASO 9.1: Método `calculateTotalEntered()` - Suma de ENTRADAS

---

#### 📋 DIAGRAMA DETALLADO - Patrón Acumulador:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: calculateTotalEntered (Acumulador con filtro)        ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  🎯 PATRÓN: Acumulador - Suma condicional                      ║
║                                                                ║
║  📝 JAVADOC                                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /**                                                       │ ║
║  │  * Calcula la suma total de combustible en ENTRADAS      │ ║
║  │  * @return Cantidad total en litros que ha entrado       │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA                                                      ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ public double calculateTotalEntered()                    │ ║
║  │   │      │           │                                   │ ║
║  │   │      │           └─ Nombre descriptivo              │ ║
║  │   │      └─ Retorna número decimal (litros)             │ ║
║  │   └─ Acceso público                                      │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 LÓGICA (Patrón clásico de acumulación)                     ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │                                                           │ ║
║  │  PASO 1: INICIALIZAR acumulador                          │ ║
║  │  ───────────────────────────                             │ ║
║  │  DECLARAR variable 'total' tipo double                   │ ║
║  │  INICIALIZAR en 0.0                                      │ ║
║  │  (Aquí iremos sumando los valores)                       │ ║
║  │                                                           │ ║
║  │  PASO 2: RECORRER y ACUMULAR                             │ ║
║  │  ────────────────────────                                │ ║
║  │  PARA CADA movement en movements:                        │ ║
║  │    │                                                      │ ║
║  │    ├─ PREGUNTAR: ¿Es de tipo "ENTRADA"?                 │ ║
║  │    │  (usar getMovementType() y .equals())              │ ║
║  │    │                                                      │ ║
║  │    └─ SI ES ENTRADA:                                     │ ║
║  │        │                                                  │ ║
║  │        ├─ OBTENER la cantidad del movement              │ ║
║  │        │  (usar getQuantity())                           │ ║
║  │        │                                                  │ ║
║  │        └─ SUMAR esa cantidad a 'total'                  │ ║
║  │           (total = total + cantidad)                     │ ║
║  │           (o usar: total += cantidad)                    │ ║
║  │                                                           │ ║
║  │  FIN del FOR-EACH                                        │ ║
║  │                                                           │ ║
║  │  PASO 3: DEVOLVER resultado                              │ ║
║  │  ────────────────────────                                │ ║
║  │  RETORNAR 'total'                                        │ ║
║  │  (La suma acumulada de todas las entradas)              │ ║
║  │                                                           │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### � PSEUDOCÓDIGO DETALLADO:

```
PSEUDOCÓDIGO:

DOCUMENTAR con javadoc:
  DESCRIBIR: "Calcula el total de litros que han entrado"
  AGREGAR @return: "Suma de todas las cantidades de tipo ENTRADA"

DECLARAR método público que retorna double llamado calculateTotalEntered:
  SIN parámetros
  
  DENTRO del método:
  
    // --- PASO 1: INICIALIZAR ACUMULADOR ---
    DECLARAR variable 'total' de tipo double
    INICIALIZAR en 0.0
    
    // --- PASO 2: RECORRER Y SUMAR ---
    PARA CADA movement en la lista 'movements':
      
      // Verificar si es una ENTRADA
      SI movement.getMovementType() ES IGUAL A "ENTRADA":
        (Usa .equals() para comparar)
        
        ENTONCES:
          OBTENER la cantidad del movement
          SUMAR esa cantidad a 'total'
          (total = total + movement.getQuantity())
          (o: total += movement.getQuantity())
      
      FIN del SI
      // Las SALIDAS se ignoran automáticamente
    
    FIN del FOR-EACH
    
    // --- PASO 3: RETORNAR SUMA ---
    RETORNAR 'total'

FIN del método
```

---

#### 🎯 EJEMPLO DE EJECUCIÓN:

```
ESTADO INICIAL de movements:
  [0] Movement(type="ENTRADA", quantity=1000.0)
  [1] Movement(type="SALIDA", quantity=500.0)
  [2] Movement(type="ENTRADA", quantity=800.0)
  [3] Movement(type="ENTRADA", quantity=200.0)

LLAMADA: manager.calculateTotalEntered()

EJECUCIÓN PASO A PASO:

  1. total = 0.0
  
  2. FOR-EACH:
  
     Iteración 1:
       - ¿type == "ENTRADA"? SÍ ✅
       - total = 0.0 + 1000.0 = 1000.0
     
     Iteración 2:
       - ¿type == "ENTRADA"? NO
       - Se ignora, total sigue = 1000.0
     
     Iteración 3:
       - ¿type == "ENTRADA"? SÍ ✅
       - total = 1000.0 + 800.0 = 1800.0
     
     Iteración 4:
       - ¿type == "ENTRADA"? SÍ ✅
       - total = 1800.0 + 200.0 = 2000.0
  
  3. return 2000.0

RETORNA: 2000.0 (litros totales de entradas)
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

1. **¿Por qué inicializamos `total` en 0 y no en null?**
   - Respuesta: Es una suma, el elemento neutro de la suma es 0

2. **¿Qué pasa si no hay ninguna ENTRADA?**
   - Respuesta: Retorna 0.0 (correcto, no ha entrado nada)

3. **¿Por qué usamos `double` y no `int`?**
   - Respuesta: Litros pueden tener decimales (1500.5 litros)

---

#### 📝 TU TAREA:

1. **ESCRIBE** el Javadoc
2. **ESCRIBE** la firma del método
3. **DECLARA** e inicializa `total` en 0.0
4. **ESCRIBE** el for-each con if y acumulación
5. **RETORNA** total
6. **COMPILA**

---

### 🛠️ PASO 9.2: Método `calculateTotalExited()` - Suma de SALIDAS

**Concepto:** Exactamente igual que `calculateTotalEntered()` pero filtrando por "SALIDA".

---

#### 🔍 PSEUDOCÓDIGO:

```
PSEUDOCÓDIGO:

ESTE MÉTODO ES CASI IDÉNTICO AL ANTERIOR

CAMBIOS:
  - Nombre del método: calculateTotalExited
  - Javadoc: "Calcula el total de litros que han salido"
  - Filtro: SI movement.getMovementType() ES IGUAL A "SALIDA"
            (En vez de "ENTRADA")

TODO LO DEMÁS ES IGUAL:
  - Mismo patrón acumulador
  - Misma estructura
  - Mismo tipo de retorno (double)
```

---

#### 📝 TU TAREA:

**Copia mentalmente la estructura de `calculateTotalEntered()` y adapta:**

1. Nombre del método
2. Javadoc
3. El filtro del if (cambiar "ENTRADA" por "SALIDA")

**Tip:** Este es un buen momento para practicar refactorización. Nota cómo dos métodos tienen casi el mismo código pero con un pequeño cambio. En Fase 8 aprenderás cómo eliminar esta duplicación.

---

### 🛠️ PASO 9.3: Método `getCurrentStock()` - Stock actual

**Concepto:** Calcular la diferencia entre entradas y salidas.

---

#### 🎓 Analogía:

```
Stock actual = Lo que entró - Lo que salió

Ejemplo:
  Entradas: 5000 litros
  Salidas:  2000 litros
  Stock:    3000 litros (lo que queda en los tanques)
```

---

#### 📋 DIAGRAMA DETALLADO:

```
╔════════════════════════════════════════════════════════════════╗
║  MÉTODO: getCurrentStock (Reutilización de métodos)           ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  🎯 CONCEPTO: Método que LLAMA a otros métodos                 ║
║               (Composición de funcionalidad)                   ║
║                                                                ║
║  📝 JAVADOC                                                    ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ /**                                                       │ ║
║  │  * Calcula el stock actual de combustible                │ ║
║  │  * Formula: Total entradas - Total salidas               │ ║
║  │  * @return Cantidad disponible en litros                 │ ║
║  │  */                                                       │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🔧 FIRMA                                                      ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │ public double getCurrentStock()                          │ ║
║  └──────────────────────────────────────────────────────────┘ ║
║                                                                ║
║  🧠 LÓGICA (Reutilización - DRY: Don't Repeat Yourself)        ║
║  ┌──────────────────────────────────────────────────────────┐ ║
║  │                                                           │ ║
║  │  OPCIÓN 1: Llamar métodos existentes (RECOMENDADO)      │ ║
║  │  ───────────────────────────────────────                 │ ║
║  │  OBTENER total de entradas usando calculateTotalEntered()│ ║
║  │  OBTENER total de salidas usando calculateTotalExited()  │ ║
║  │  CALCULAR diferencia (entradas - salidas)                │ ║
║  │  RETORNAR ese resultado                                  │ ║
║  │                                                           │ ║
║  │  OPCIÓN 2: Todo en una línea (más conciso)              │ ║
║  │  ───────────────────────────────────                     │ ║
║  │  RETORNAR calculateTotalEntered() - calculateTotalExited()│ ║
║  │                                                           │ ║
║  └──────────────────────────────────────────────────────────┘ ║
╚════════════════════════════════════════════════════════════════╝
```

---

#### 🔍 PSEUDOCÓDIGO:

```
PSEUDOCÓDIGO:

DOCUMENTAR con javadoc:
  DESCRIBIR: "Calcula el stock actual (entradas menos salidas)"
  AGREGAR @return: "Cantidad disponible en litros"

DECLARAR método público que retorna double llamado getCurrentStock:
  SIN parámetros
  
  DENTRO del método:
  
    OPCIÓN A (Paso a paso - didáctico):
      DECLARAR variable 'totalEntered' tipo double
      ASIGNAR resultado de LLAMAR calculateTotalEntered()
      
      DECLARAR variable 'totalExited' tipo double
      ASIGNAR resultado de LLAMAR calculateTotalExited()
      
      DECLARAR variable 'stock' tipo double
      ASIGNAR totalEntered - totalExited
      
      RETORNAR stock
    
    OPCIÓN B (Directo - profesional):
      RETORNAR calculateTotalEntered() - calculateTotalExited()

FIN del método

NOTA: Ambas opciones son correctas
      Opción A es más clara para aprender
      Opción B es más concisa
```

---

#### 🎯 EJEMPLO DE USO:

```
ESTADO:
  movements = [
    Movement(type="ENTRADA", quantity=5000.0),
    Movement(type="SALIDA", quantity=1200.0),
    Movement(type="ENTRADA", quantity=3000.0),
    Movement(type="SALIDA", quantity=800.0)
  ]

LLAMADA: manager.getCurrentStock()

EJECUCIÓN:
  1. calculateTotalEntered() → 5000.0 + 3000.0 = 8000.0
  2. calculateTotalExited() → 1200.0 + 800.0 = 2000.0
  3. stock = 8000.0 - 2000.0 = 6000.0

RETORNA: 6000.0 litros disponibles
```

---

#### 💭 PREGUNTAS PARA REFLEXIONAR:

1. **¿Por qué NO recorremos movements de nuevo?**
   - Respuesta: **Reutilización** - Ya tenemos métodos que hacen eso
   - Principio DRY: Don't Repeat Yourself

2. **¿Qué ventaja tiene llamar a otros métodos?**
   - Respuesta: Si cambia la lógica de cálculo, solo modificas UN lugar

3. **¿Puede el stock ser negativo?**
   - Respuesta: Matemáticamente sí (salió más de lo que entró)
   - En Fase 7: Agregarás validaciones para evitar esto

---

#### 📝 TU TAREA:

1. **ESCRIBE** el Javadoc
2. **ESCRIBE** la firma del método
3. **ELIGE** una opción:
   - Opción A: Paso a paso (3-4 líneas)
   - Opción B: Una sola línea con return
4. **COMPILA**

---

### ✅ CHECKPOINT: Compila y valida MovementManager completo

**Has terminado de implementar todos los métodos de MovementManager.** 🎉

**ANTES de continuar al siguiente paso:**

1. **COMPILA** el proyecto completo:
   ```
   mvn clean compile
   ```

2. **VERIFICA** que no hay errores

3. **REVISA** tu código:
   - ¿Todos los métodos tienen Javadoc?
   - ¿Los nombres son descriptivos?
   - ¿Usas .equals() para comparar Strings?
   - ¿El código está indentado correctamente?

4. **COMMIT** en Git:
   ```
   git add .
   git commit -m "Fase 2.5: Implementación completa de MovementManager"
   ```

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

## 🪞 Reflexión: antes y después de `MovementManager`

| Aspecto | Fase 2.6 (todo en `Main`) | Fase 2.5 (con Manager) |
|---------|---------------------------|-------------------------|
| Código repetido | Bucles y filtros copiados en varios lugares | Métodos reutilizables dentro del manager |
| Legibilidad | `Main.java` crece y se vuelve difícil de seguir | `Main.java` delega y permanece compacto |
| Preparación para BD | Difícil de adaptar | Lista para reemplazar ArrayList por consultas SQL |
| Depuración | Breakpoints dispersos | Puedes depurar la lógica de colección en una sola clase |

Tómate un minuto para responder (en voz alta o en tu cuaderno):
1. ¿Qué responsabilidad dejó de tener `Main.java` gracias a `MovementManager`?
2. ¿Qué método del manager te resultó más natural de implementar y por qué?
3. ¿Qué parte volverías a repasar si quisieras sentirte aún más seguro antes de ir a Fase 3?

Registrar estas respuestas en tu `JAVA_LEARNING_LOG.md` te ayudará a consolidar el aprendizaje. 🚀

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

