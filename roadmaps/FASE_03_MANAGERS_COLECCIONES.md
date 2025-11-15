# FASE 03: MANAGERS Y COLECCIONES EN MEMORIA
**Roadmap Retrospectivo - Análisis del Código Existente**

---

## Contexto de esta Fase

Esta fase documenta el **paso intermedio** entre trabajar con objetos individuales (Fase 02) y usar una base de datos (Fase 04). Analizaremos **MovementManagers**, una clase que gestiona una **colección en memoria** de movimientos usando `ArrayList`.

**Estado actual:** Esta clase fue creada con fines educativos y ha sido **reemplazada por MovementServices** (que usa JDBC). Sin embargo, su análisis es valioso porque enseña:

- ✅ **Colecciones Java** (List, ArrayList)
- ✅ **CRUD en memoria** (sin base de datos)
- ✅ **Defensive copying** (protección de datos)
- ✅ **Algoritmos de búsqueda y filtrado**
- ✅ **Iteración con for-each**

---

## Objetivos de Aprendizaje

Al analizar esta fase, reforzarás:

1. **List<T> y ArrayList<T>** (colecciones genéricas)
2. **Constructores con listas** (manejo de null)
3. **Defensive copying** (copias defensivas)
4. **CRUD sin base de datos** (add, get, find, filter)
5. **For-each loop** (iteración simplificada)
6. **Algoritmos de búsqueda lineal**
7. **Métodos de agregación** (sumar cantidades)
8. **Retorno de copias** vs referencias

---

## Arquitectura de la Fase 03

```
Fase 02                    Fase 03                     Fase 04
(Objetos individuales)     (Colecciones en memoria)    (Base de datos)

Movement m1                MovementManagers            MovementServices
Movement m2        →       ├── List<Movement>    →     ├── JDBC Connection
Movement m3                ├── addMovements()          ├── insertMovement()
                           ├── getAllMovements()       ├── getAllMovements()
                           ├── findById()              ├── getMovementById()
                           └── getMovementsByType()    └── getMovementsByType()
```

**Progresión del aprendizaje:**
1. **Fase 02:** Crear UN objeto Movement
2. **Fase 03:** Gestionar MUCHOS objetos Movement en memoria
3. **Fase 04:** Persistir objetos Movement en base de datos

---

## 1. CONCEPTO: COLECCIONES EN JAVA

### 1.1 ¿Qué es una Colección?

Una **colección** es un objeto que agrupa múltiples elementos:

**Sin colección (inmanejable):**
```java
Movement m1 = new Movement(...);
Movement m2 = new Movement(...);
Movement m3 = new Movement(...);
// ¿Cómo busco un movimiento por ID?
// ¿Cómo cuento cuántos movimientos tengo?
```

**Con colección (manejable):**
```java
List<Movement> movements = new ArrayList<>();
movements.add(m1);
movements.add(m2);
movements.add(m3);
// Ahora puedo iterar, buscar, filtrar fácilmente
```

---

### 1.2 List vs ArrayList

**List:** Interfaz (contrato, no implementación)
**ArrayList:** Clase concreta que implementa List

```java
// Declaración recomendada (tipo de interfaz):
List<Movement> movements = new ArrayList<>();
     ↑                            ↑
   Interfaz                Implementación concreta
```

**¿Por qué usar la interfaz?**
- **Flexibilidad:** Puedes cambiar la implementación sin cambiar el tipo:
  ```java
  List<Movement> movements = new ArrayList<>();     // Hoy
  List<Movement> movements = new LinkedList<>();    // Mañana (cambio fácil)
  ```

---

### 1.3 Generics: List<Movement>

**`<Movement>`** es un **tipo genérico**:

**Sin generics (Java antiguo, peligroso):**
```java
List movements = new ArrayList();  // ¡Puede contener CUALQUIER tipo!
movements.add(new Movement(...));
movements.add("Hola");             // ¡Compila! 🔴
movements.add(123);                // ¡Compila! 🔴

Movement m = (Movement) movements.get(0);  // Cast manual necesario
```

**Con generics (Java moderno, seguro):**
```java
List<Movement> movements = new ArrayList<>();  // SOLO Movement
movements.add(new Movement(...));             // ✅
movements.add("Hola");                        // ❌ Error de compilación
movements.add(123);                           // ❌ Error de compilación

Movement m = movements.get(0);  // Sin cast, seguro
```

**Ventajas:**
- **Type safety:** Errores en compilación, no en runtime
- **Sin casts:** Código más limpio
- **Autocompletado:** El IDE sabe qué métodos tiene Movement

---

## 2. ANÁLISIS DE MOVEMENTMANAGERS.JAVA

### Archivo: `managers/MovementManagers.java`

#### 2.1 Atributos (Líneas 11)

```java
private List<Movement> movements;
```

**Análisis:**
- **`private`:** La lista NO es accesible directamente desde fuera
- **`List<Movement>`:** Solo puede contener objetos Movement
- **Sin inicialización aquí:** Se inicializa en los constructores

---

#### 2.2 Constructor con Parámetro - Defensive Copying (Líneas 13-24)

```java
/**
 * Constructor principal que recibe una lista puede ser la base de datos o si no crea una nueva
 * @param movements
 */
public MovementManagers(List<Movement> movements) {
    if (movements == null) {
        this.movements = new ArrayList<>();
    } else {
        this.movements = new ArrayList<>(movements);  // ← DEFENSIVE COPY
    }
}
```

**Análisis línea por línea:**

**Línea 18-19: Manejo de null**
```java
if (movements == null) {
    this.movements = new ArrayList<>();
```
- Si el usuario pasa `null`, crea una lista vacía
- Previene `NullPointerException`

**Línea 21: Defensive Copying**
```java
this.movements = new ArrayList<>(movements);
```

**⚠️ CONCEPTO CRÍTICO: ¿Por qué NO hacer esto?**

```java
// ❌ MAL - Referencia compartida (peligroso):
this.movements = movements;

// Problema:
List<Movement> externalList = new ArrayList<>();
MovementManagers manager = new MovementManagers(externalList);

externalList.add(someMovement);  // ¡Modifica la lista interna del manager!
externalList.clear();            // ¡Vacía el manager sin que se entere!
```

**✅ BIEN - Copia defensiva (seguro):**
```java
this.movements = new ArrayList<>(movements);

// Ahora:
List<Movement> externalList = new ArrayList<>();
MovementManagers manager = new MovementManagers(externalList);

externalList.add(someMovement);  // NO afecta al manager (listas separadas)
externalList.clear();            // NO afecta al manager
```

**Visualización:**

**Sin defensive copy:**
```
externalList ──┐
               ├──► [Movement1, Movement2, Movement3]
manager.movements ─┘   (misma referencia)
```

**Con defensive copy:**
```
externalList ──────► [Movement1, Movement2, Movement3]
                          ↓ (copia de elementos)
manager.movements ──► [Movement1, Movement2, Movement3]
                      (lista diferente, mismos movimientos)
```

**Sintaxis:**
```java
new ArrayList<>(movements)
               └─────────┘
              Pasa la lista existente al constructor de ArrayList
              → Crea UNA NUEVA lista con los mismos elementos
```

---

#### 2.3 Constructor por Defecto (Líneas 25-30)

```java
// Constructor por defecto.
// Crea la lista de movimientos vacía.
// Listo para empezar a agregar movimientos.
public MovementManagers() {
    this.movements = new ArrayList<>();
}
```

**Uso:**
```java
// Constructor con parámetro:
List<Movement> existingMovements = loadFromSomewhere();
MovementManagers manager1 = new MovementManagers(existingMovements);

// Constructor vacío:
MovementManagers manager2 = new MovementManagers();  // Lista vacía
manager2.addMovements("ENTRADA", "FUEL-123", "GALON", 50, 12000);
```

---

#### 2.4 Método addMovements() - CREATE (Líneas 35-50)

```java
/**
 * → Crear, agregar movimiento a la lista y retornar el movimiento
 *
 * @param movementType tipo de movimiento Entrada o Salida
 * @param productType  tipo de combustible (ACPM, GASOLINA, 20W50, 15W40, VALVULINA, LIGA, GRASA)
 * @param quantity     cantidad de combustible
 * @param price        precio por galon
 * @return retorna el objecto movimiento
 */
public Movement addMovements(String movementType, String productId, String unidadDeMedida,
                             double quantity, double price) {
    // Usar el nuevo constructor con productId, vehicleId=null, numeroFactura=null
    Movement newMovement = new Movement(movementType, productId, null, null, unidadDeMedida, quantity, price);
    movements.add(newMovement);
    return newMovement;
}
```

**Análisis:**

**Línea 46:** Crea un nuevo Movement usando el constructor CREATE (Fase 02)
- ID se genera automáticamente
- `vehicleId` = null (no se especifica vehículo)
- `numeroFactura` = null (no se especifica factura)

**Línea 47:** `movements.add(newMovement)`
- Agrega el movimiento a la lista interna
- `add()` es un método de ArrayList

**Línea 48:** Retorna el movimiento creado
- Útil para mostrar confirmación al usuario
- Permite encadenar operaciones

**Uso en código:**
```java
MovementManagers manager = new MovementManagers();

Movement m1 = manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
System.out.println("Creado: " + m1.getId());  // Creado: MOV-A3F2C1D4

Movement m2 = manager.addMovements("SALIDA", "FUEL-123", "GALON", 20, 12500);
System.out.println("Total movimientos: " + manager.getTotalMovements());  // Total movimientos: 2
```

---

#### 2.5 Método getAllMovements() - READ ALL (Líneas 54-62)

```java
/**
 * Obtengo todos los movimientos almacenados en la lista para después hacer operaciones
 * con esta como mostrarlo
 *
 * @return todos los movimientos
 */
public List<Movement> getAllMovements() {
    return movements;
}
```

**⚠️ PROBLEMA DE DISEÑO:**

Este método retorna la **referencia directa** a la lista interna:

```java
MovementManagers manager = new MovementManagers();
manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);

List<Movement> lista = manager.getAllMovements();
lista.clear();  // ¡Vacía la lista interna del manager! 🔴

System.out.println(manager.getTotalMovements());  // 0 (¡lista vacía!)
```

**✅ CORRECCIÓN RECOMENDADA:**
```java
public List<Movement> getAllMovements() {
    return new ArrayList<>(movements);  // Retornar copia defensiva
}
```

**Nota:** Esto será corregido en futuros refactorings (ejercicio de Fase 03).

---

#### 2.6 Método findById() - BÚSQUEDA LINEAL (Líneas 67-82)

```java
/**
 * Busca movimiento por su id
 *
 * @param id id a buscar
 * @return retorna el movimiento que coincide con el id
 */
public Movement findById(String id) {
    for (Movement m : movements) {
        if (m.getId().equals(id)) {
            return m;
        }
    }
    return null;
}
```

**Análisis línea por línea:**

**Línea 74: For-each loop**
```java
for (Movement m : movements) {
     ↓         ↓      ↓
   variable  "en"  colección
```

**Lectura:** "Para cada Movement `m` EN `movements`"

**Equivalente con for tradicional:**
```java
for (int i = 0; i < movements.size(); i++) {
    Movement m = movements.get(i);
    if (m.getId().equals(id)) {
        return m;
    }
}
```

**Ventajas del for-each:**
- Más legible (menos código)
- Sin índices (menos propenso a errores)
- Funciona con cualquier colección

---

**Línea 76: Comparación de Strings**
```java
if (m.getId().equals(id)) {
```

**⚠️ IMPORTANTE: NO usar `==` para Strings**

```java
// ❌ MAL (compara referencias):
if (m.getId() == id) {

// ✅ BIEN (compara contenido):
if (m.getId().equals(id)) {
```

**Ejemplo del problema:**
```java
String id1 = new String("MOV-12345678");
String id2 = new String("MOV-12345678");

id1 == id2         // false (diferentes objetos)
id1.equals(id2)    // true (mismo contenido)
```

---

**Línea 77-80: Retorno temprano**
```java
return m;  // Sale del método inmediatamente al encontrar
```

**Flujo del algoritmo:**
```
findById("MOV-B")

movements: [MOV-A, MOV-B, MOV-C]
            ↓
Iteración 1: ¿MOV-A == MOV-B? No, continuar
Iteración 2: ¿MOV-B == MOV-B? Sí, return MOV-B (sale)
(Iteración 3 nunca se ejecuta)
```

**Línea 80: Retorno null**
```java
return null;  // Si el for termina sin encontrar nada
```

**Implicación:** El código que llama debe manejar `null`:
```java
Movement found = manager.findById("MOV-XXXX");
if (found != null) {
    System.out.println("Encontrado: " + found);
} else {
    System.out.println("No existe ese ID");
}
```

---

#### 2.7 Método getMovementsByType() - FILTRADO (Líneas 86-104)

```java
/**
 * Filtrar movimientos por ENTRADA/SALIDA
 *
 * @param typeOfMovement tipo de movimiento a filtrar puede ser entrada o salida
 * @return El o los tipos de movimiento filtrados
 */
public List<Movement> getMovementsByType(String typeOfMovement) {
    List<Movement> result = new ArrayList<>();

    for (Movement m : movements) {
        if (m.getMovementType().equals(typeOfMovement)) {
            result.add(m);
        }
    }
    return new ArrayList<>(result);
}
```

**Análisis:**

**Línea 94: Lista temporal**
```java
List<Movement> result = new ArrayList<>();
```
- Crea una lista vacía para almacenar los resultados filtrados

**Líneas 96-102: Iteración y filtrado**
```java
for (Movement m : movements) {
    if (m.getMovementType().equals(typeOfMovement)) {
        result.add(m);
    }
}
```
- Recorre TODOS los movimientos
- Solo agrega a `result` los que coinciden con el tipo

**Visualización:**
```
movements: [MOV-A(ENTRADA), MOV-B(SALIDA), MOV-C(ENTRADA), MOV-D(SALIDA)]
                  ↓                              ↓
getMovementsByType("ENTRADA")
                  ↓                              ↓
result:    [MOV-A(ENTRADA),           MOV-C(ENTRADA)]
```

**Línea 103: Defensive copy al retornar**
```java
return new ArrayList<>(result);
```

**Pregunta:** ¿Por qué no simplemente `return result;`?

**Respuesta:** Paranoia defensiva (aunque innecesaria aquí):
```java
// Si retornaras la referencia directa:
List<Movement> entradas = manager.getMovementsByType("ENTRADA");
entradas.clear();  // Solo afectaría a 'result', no a 'movements'
// (En realidad, no hay problema porque 'result' es local)

// Pero es buena práctica ser consistente con defensive copying
```

---

#### 2.8 Método getTotalMovements() - CONTEO (Líneas 108-117)

```java
/**
 * Obtener total de movimientos cantidad no valor
 * @return Total de movimientos
 */
public int getTotalMovements() {
    return movements.size();
}
```

**Simple pero útil:**
- `size()` es un método de List
- Retorna el número de elementos en la lista

**Uso:**
```java
MovementManagers manager = new MovementManagers();
System.out.println(manager.getTotalMovements());  // 0

manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
System.out.println(manager.getTotalMovements());  // 1

manager.addMovements("SALIDA", "FUEL-123", "GALON", 20, 12500);
System.out.println(manager.getTotalMovements());  // 2
```

---

#### 2.9 Método calculateTotalEntered() - AGREGACIÓN (Líneas 122-137)

```java
/**
 * Calcula el total de movimientos tipo entrada
 *
 * @return total movimiento tipo entrada
 */
public int calculeTotalEntered() {
    int totalEntradas = 0;
    for (Movement m : movements) {
        if (m.getMovementType().equals("ENTRADA")) {
            totalEntradas += (int) m.getQuantity();
        }
    }
    return totalEntradas;
}
```

**Análisis:**

**Patrón acumulador:**
```java
int totalEntradas = 0;  // 1. Inicializar acumulador
for (Movement m : movements) {  // 2. Iterar colección
    if (condición) {  // 3. Filtrar
        totalEntradas += valor;  // 4. Acumular
    }
}
return totalEntradas;  // 5. Retornar resultado
```

**Línea 132: Cast a int**
```java
totalEntradas += (int) m.getQuantity();
                 ↑
              Conversión explícita de double a int
```

**Efecto:**
```java
double quantity = 15.75;
int intQuantity = (int) quantity;  // 15 (se trunca, no redondea)
```

**⚠️ PÉRDIDA DE PRECISIÓN:**
- Si quantity = 15.75, se suma solo 15
- Si quantity = 99.99, se suma solo 99

**Mejor práctica:** Usar `double` para el acumulador:
```java
public double calculeTotalEntered() {
    double totalEntradas = 0.0;
    for (Movement m : movements) {
        if (m.getMovementType().equals("ENTRADA")) {
            totalEntradas += m.getQuantity();  // Sin cast
        }
    }
    return totalEntradas;
}
```

---

#### 2.10 Método calculateTotalExited() - 🐛 BUG DETECTADO (Líneas 141-156)

```java
/**
 * Calcula el total de movimientos tipo salida
 * @return total movimiento tipo salida
 */
public int calculateTotalExited() {
    int totalExcited = 0;
    for (Movement m : movements) {
        if (m.getMovementType().equals("SALIDAS")) {  // 🔴 BUG
            totalExcited += m.getQuantity();
        }
    }
    return totalExcited;
}
```

**🔴 BUG CRÍTICO: Línea 148**
```java
if (m.getMovementType().equals("SALIDAS")) {  // ❌ "SALIDAS" (plural)
```

**Problema:**
- El tipo correcto es `"SALIDA"` (singular)
- Este método **SIEMPRE retorna 0** porque nunca encuentra coincidencias

**✅ Corrección:**
```java
if (m.getMovementType().equals("SALIDA")) {  // Sin 'S'
```

**Impacto:**
```java
MovementManagers manager = new MovementManagers();
manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
manager.addMovements("SALIDA", "FUEL-123", "GALON", 20, 12500);

int entradas = manager.calculeTotalEntered();  // 100 ✅
int salidas = manager.calculateTotalExited();  // 0 ❌ (debería ser 20)
double stock = manager.getCurrentStock();      // 100 ❌ (debería ser 80)
```

---

#### 2.11 Método getCurrentStock() - CÁLCULO COMPUESTO (Líneas 160-166)

```java
/**
 * Calcula el stock actual de combustible
 * @return stock actual
 */
public double getCurrentStock() {
    return calculeTotalEntered() - calculateTotalExited();
}
```

**Lógica de negocio:**
```
Stock Actual = Total ENTRADAS - Total SALIDAS
```

**Ejemplo:**
```
ENTRADAS: 500 galones
SALIDAS:  200 galones
STOCK:    300 galones
```

**⚠️ Afectado por el bug anterior:**
Debido al bug en `calculateTotalExited()`, este método retorna valores incorrectos.

---

### 2.12 Checkpoint de Verificación - MovementManagers ✅

1. **¿Qué es defensive copying y por qué es importante?**
2. **¿Cuál es la diferencia entre `List<Movement>` y `ArrayList<Movement>`?**
3. **¿Por qué usar for-each en lugar de for tradicional?**
4. **¿Qué retorna `findById()` si no encuentra el ID?**
5. **¿Por qué NO usar `==` para comparar Strings?**
6. **¿Cuál es el bug en `calculateTotalExited()` y cómo afecta al sistema?**
7. **¿Por qué `getAllMovements()` debería retornar una copia defensiva?**

---

## 3. EVOLUCIÓN: MANAGERS → SERVICES

### 3.1 ¿Por qué MovementManagers fue reemplazado?

**Limitaciones de Managers (en memoria):**
- ❌ **Datos volátiles:** Al cerrar la aplicación, se pierden todos los movimientos
- ❌ **Sin persistencia:** No hay respaldo de los datos
- ❌ **Capacidad limitada:** ArrayList consume RAM (miles de movimientos = problemas)
- ❌ **Sin concurrencia:** Si dos usuarios usan la app, tienen datos separados

**Ventajas de Services (base de datos):**
- ✅ **Persistencia:** Datos guardados en MySQL
- ✅ **Escalabilidad:** Millones de registros sin problema
- ✅ **Integridad:** Foreign keys, transacciones
- ✅ **Compartido:** Múltiples usuarios acceden a los mismos datos

---

### 3.2 Comparación de APIs

| Operación | MovementManagers (Fase 03) | MovementServices (Fase 04) |
|-----------|----------------------------|----------------------------|
| CREATE | `addMovements(...)` | `insertMovement(Movement)` |
| READ ALL | `getAllMovements()` | `getAllMovements()` |
| READ ONE | `findById(String)` | `getMovementById(String)` |
| FILTER | `getMovementsByType(String)` | `getMovementsByType(String)` |
| COUNT | `getTotalMovements()` | `getTotalMovements()` |
| DELETE | ❌ No implementado | `deleteMovement(String)` |
| UPDATE | ❌ No implementado | `updateMovement(...)` |

**Nota:** La API es similar intencionalmente (facilita la transición).

---

### 3.3 Uso Histórico en el Proyecto

**Main.java original (Fase 2.5):**
```java
public static void main(String[] args) {
    // Crear manager con lista vacía
    MovementManagers manager = new MovementManagers();

    // Agregar movimientos de prueba
    manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
    manager.addMovements("SALIDA", "FUEL-123", "GALON", 20, 12500);

    // Mostrar stock
    System.out.println("Stock actual: " + manager.getCurrentStock());
}
```

**Main.java actual (Fase 07):**
```java
public static void main(String[] args) {
    AppController app = new AppController();
    app.iniciar();  // Usa MovementServices internamente
}
```

---

## 4. EJERCICIOS DE CORRECCIÓN Y MEJORA

### 4.1 Ejercicio 1: Corregir el bug en calculateTotalExited() 🔧

**Archivo:** `forestech-cli-java/src/main/java/com/forestech/managers/MovementManagers.java`

**Línea 148:** Cambiar `"SALIDAS"` por `"SALIDA"`

**Antes:**
```java
if (m.getMovementType().equals("SALIDAS")) {
```

**Después:**
```java
if (m.getMovementType().equals("SALIDA")) {
```

**Verificación:**
```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
# Debería compilar sin errores
```

---

### 4.2 Ejercicio 2: Agregar defensive copy a getAllMovements() 🔧

**Línea 61:** Modificar el retorno

**Antes:**
```java
public List<Movement> getAllMovements() {
    return movements;
}
```

**Después:**
```java
public List<Movement> getAllMovements() {
    return new ArrayList<>(movements);  // Retornar copia
}
```

**Justificación:** Prevenir modificaciones externas a la lista interna.

---

### 4.3 Ejercicio 3: Cambiar tipo de retorno a double en métodos de cálculo 🔧

**Líneas 128 y 145:**

**Antes:**
```java
public int calculeTotalEntered() {
    int totalEntradas = 0;
    // ...
    totalEntradas += (int) m.getQuantity();  // Cast innecesario
}
```

**Después:**
```java
public double calculeTotalEntered() {
    double totalEntradas = 0.0;
    // ...
    totalEntradas += m.getQuantity();  // Sin cast
}
```

**Repetir para `calculateTotalExited()`.**

---

### 4.4 Ejercicio 4: Crear método deleteById() 🎯

**Agregar después de findById():**

```java
/**
 * Elimina un movimiento por su ID
 *
 * @param id ID del movimiento a eliminar
 * @return true si se eliminó, false si no se encontró
 */
public boolean deleteById(String id) {
    Movement found = findById(id);
    if (found != null) {
        movements.remove(found);
        return true;
    }
    return false;
}
```

**Concepto nuevo: `remove(Object)`**
- Método de List que elimina el primer objeto que coincida con `equals()`
- Retorna `true` si eliminó algo, `false` si no encontró nada

**Uso:**
```java
manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
System.out.println("Total: " + manager.getTotalMovements());  // 1

manager.deleteById("MOV-XXXXXXXX");
System.out.println("Total: " + manager.getTotalMovements());  // 0
```

---

### 4.5 Ejercicio 5: Crear método updateQuantity() 🎯

**Agregar después de deleteById():**

```java
/**
 * Actualiza la cantidad de un movimiento existente
 *
 * @param id ID del movimiento
 * @param newQuantity Nueva cantidad
 * @return true si se actualizó, false si no se encontró
 */
public boolean updateQuantity(String id, double newQuantity) {
    Movement found = findById(id);
    if (found != null) {
        found.setQuantity(newQuantity);
        return true;
    }
    return false;
}
```

**Uso:**
```java
Movement m = manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
System.out.println("Cantidad inicial: " + m.getQuantity());  // 100.0

manager.updateQuantity(m.getId(), 150.0);
System.out.println("Cantidad actualizada: " + m.getQuantity());  // 150.0
```

---

## 5. CONCEPTO AVANZADO: SHALLOW COPY VS DEEP COPY

### 5.1 ¿Qué copia realmente defensive copying?

**Cuando haces:**
```java
this.movements = new ArrayList<>(movements);
```

**Solo copias la lista, NO los objetos Movement:**

```
Original:
movements ──► [ref1, ref2, ref3]
                 ↓     ↓     ↓
              [Mov1, Mov2, Mov3]

Después de defensive copy:
this.movements ──► [ref1, ref2, ref3]  ← Nueva lista, mismas referencias
                     ↓     ↓     ↓
                  [Mov1, Mov2, Mov3]  ← Objetos compartidos
```

**Implicación:**
```java
List<Movement> original = new ArrayList<>();
Movement m1 = new Movement("ENTRADA", "FUEL-123", null, null, "GALON", 100, 12500);
original.add(m1);

MovementManagers manager = new MovementManagers(original);

// Modificar el objeto original:
m1.setQuantity(200);  // ¡Modifica el objeto dentro del manager también! 🔴

System.out.println(manager.getAllMovements().get(0).getQuantity());  // 200
```

**Solución (no implementada, avanzado):** Deep copy
```java
public MovementManagers(List<Movement> movements) {
    this.movements = new ArrayList<>();
    for (Movement m : movements) {
        // Crear NUEVA instancia con mismos valores:
        this.movements.add(new Movement(
            m.getId(), m.getMovementType(), m.getProductId(), // ...
        ));
    }
}
```

**Nota:** Para este proyecto, shallow copy es suficiente porque Movement tiene `final String id` (inmutable).

---

## 6. RESUMEN DE LA FASE 03

### 6.1 Conceptos de Colecciones Implementados

| Concepto | Ejemplo en el Código |
|----------|----------------------|
| **List vs ArrayList** | `List<Movement> movements = new ArrayList<>()` |
| **Generics** | `<Movement>` para type safety |
| **Defensive copying** | `new ArrayList<>(movements)` en constructor |
| **For-each loop** | `for (Movement m : movements)` |
| **Búsqueda lineal** | `findById()` itera hasta encontrar |
| **Filtrado** | `getMovementsByType()` agrega a result |
| **Acumulador** | `calculeTotalEntered()` suma cantidades |
| **Comparación de Strings** | `.equals()` en lugar de `==` |

---

### 6.2 Bugs Identificados

| Bug | Archivo | Línea | Severidad | Corrección |
|-----|---------|-------|-----------|------------|
| "SALIDAS" en lugar de "SALIDA" | MovementManagers.java | 148 | 🔴 ALTA | Cambiar a "SALIDA" |
| getAllMovements() sin defensive copy | MovementManagers.java | 61 | 🟡 MEDIA | Retornar `new ArrayList<>(movements)` |
| Cast a int pierde precisión | MovementManagers.java | 132 | 🟡 MEDIA | Cambiar a `double` |

---

### 6.3 Progresión del Aprendizaje

```
Fase 01: Fundamentos
         ↓
Fase 02: Crear UN objeto Movement
         ↓
Fase 03: Gestionar MUCHOS objetos en memoria ← ESTÁS AQUÍ
         ↓
Fase 04: Persistir objetos en base de datos
         ↓
Fase 05: Lógica de negocio compleja
```

---

### 6.4 Ejercicio Final de la Fase 03 🎯

**Tarea completa:**

1. **Corrige los 3 bugs identificados** en MovementManagers.java
2. **Implementa `deleteById()`** siguiendo el ejemplo del ejercicio 4.4
3. **Compila:** `mvn clean compile`
4. **Crea un test manual en Main.java:**
   ```java
   MovementManagers manager = new MovementManagers();

   // Agregar movimientos
   Movement m1 = manager.addMovements("ENTRADA", "FUEL-123", "GALON", 100, 12500);
   Movement m2 = manager.addMovements("SALIDA", "FUEL-123", "GALON", 20, 12500);

   // Verificar cálculos
   System.out.println("Entradas: " + manager.calculeTotalEntered());  // 100
   System.out.println("Salidas: " + manager.calculateTotalExited());  // 20
   System.out.println("Stock: " + manager.getCurrentStock());         // 80

   // Probar búsqueda
   Movement found = manager.findById(m1.getId());
   System.out.println("Encontrado: " + (found != null));  // true

   // Probar eliminación
   boolean deleted = manager.deleteById(m1.getId());
   System.out.println("Eliminado: " + deleted);  // true
   System.out.println("Total: " + manager.getTotalMovements());  // 1
   ```

5. **Commit tus cambios:**
   ```bash
   git add forestech-cli-java/src/main/java/com/forestech/managers/MovementManagers.java
   git commit -m "Fix bugs en MovementManagers: SALIDA typo, defensive copy, precision"
   ```

---

### 6.5 Autoevaluación ✅

1. **¿Qué diferencia hay entre `List` y `ArrayList`?**
2. **¿Por qué usar generics `<Movement>`?**
3. **¿Qué es defensive copying y cuándo aplicarlo?**
4. **¿Qué hace `movements.add(newMovement)`?**
5. **¿Por qué for-each es mejor que for tradicional para iterar?**
6. **¿Qué retorna `findById()` si no encuentra?**
7. **¿Cuál era el bug en `calculateTotalExited()`?**
8. **¿Por qué MovementManagers fue reemplazado por MovementServices?**

**Si respondiste 7/8 correctamente:** ✅ Listo para Fase 04
**Si respondiste menos de 7:** 🔄 Repasa las secciones de colecciones y defensive copying

---

## 7. PRÓXIMOS PASOS

Con el manejo de colecciones dominado, la **Fase 04** introduce:
- **JDBC** (Java Database Connectivity)
- **DatabaseConnection** (conexión a MySQL)
- **PreparedStatement** (queries parametrizadas)
- **ResultSet** (lectura de resultados)
- **Try-with-resources** (manejo automático de recursos)

---

**🎓 Fase 03 Completada**

Has aprendido el manejo de colecciones en Java:
- ✅ List<T> y ArrayList<T>
- ✅ Defensive copying
- ✅ CRUD en memoria
- ✅ For-each loops
- ✅ Búsqueda y filtrado
- ✅ Métodos de agregación
- ✅ Transición de Managers a Services

**Siguiente:** [FASE_04_CONEXION_JDBC.md](./FASE_04_CONEXION_JDBC.md) - Conexión a base de datos con JDBC
