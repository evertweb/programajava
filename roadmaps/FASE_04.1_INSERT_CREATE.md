# 🛠️ FASE 4.1: INSERT - Crear Movimientos (CREATE)

> **Duración estimada:** 3-4 horas de estudio activo
> **Dificultad:** ⭐⭐ (Fundamental)
> **Prerequisitos:** FASE 03 completa (JDBC + ResultSet)

---

## 🎯 Objetivos de Aprendizaje

Al completar este checkpoint, serás capaz de:

- [ ] Explicar la diferencia entre `executeQuery()` y `executeUpdate()` sin consultar apuntes
- [ ] Escribir un método INSERT usando `PreparedStatement` con placeholders `?`
- [ ] Prevenir SQL Injection mediante el uso correcto de parámetros
- [ ] Interpretar el valor de retorno de `executeUpdate()` (rowsAffected)
- [ ] Mapear un objeto Java (`Movement`) a una instrucción SQL INSERT
- [ ] Depurar errores comunes de inserción (parámetros, tipos, constraints)
- [ ] Usar `try-with-resources` para gestión automática de recursos JDBC
- [ ] Verificar manualmente inserciones en SQL Server Management Studio

---

## 📚 Requisitos Previos

Antes de comenzar, asegúrate de tener:

- ✅ **FASE 03 completada:** Conexión JDBC funcional (`DatabaseConnection.java`)
- ✅ **Modelo Movement.java:** Clase con campos `id`, `movementType`, `fuelType`, `quantity`, `unitPrice`, `movementDate`
- ✅ **Tabla SQL creada:** `combustibles_movements` en SQL Server con las columnas correspondientes
- ✅ **Conceptos SQL:** Entender la sintaxis básica de `INSERT INTO tabla (columnas) VALUES (valores)`
- ✅ **Conceptos Java:** Conocer métodos estáticos, try-catch, tipos de datos primitivos vs objetos

**Si falta alguno de estos requisitos, detente aquí y completa los pasos previos.**

---

## 🧠 Active Recall - Activación de Conocimientos Previos

Antes de ver código, responde mentalmente estas preguntas (no escribas todavía):

### ❓ Pregunta 1: JDBC Básico
**Sin mirar apuntes:** ¿Cuál es el orden correcto de los pasos para ejecutar una consulta SQL desde Java?

<details>
<summary>🤔 Espacio para pensar (30 segundos)...</summary>

**✅ Respuesta:**
1. Obtener `Connection` desde `DatabaseConnection.getConnection()`
2. Preparar la consulta SQL en un String
3. Crear `PreparedStatement` con `conn.prepareStatement(sql)`
4. Configurar parámetros con `setString()`, `setInt()`, etc.
5. Ejecutar con `executeQuery()` (SELECT) o `executeUpdate()` (INSERT/UPDATE/DELETE)
6. Procesar resultados (si aplica)
7. Cerrar recursos (automático con try-with-resources)

</details>

---

### ❓ Pregunta 2: SQL Injection
**Sin mirar apuntes:** ¿Por qué este código es peligroso?

```java
String name = userInput; // Usuario ingresa: O'Reilly
String sql = "INSERT INTO suppliers (name) VALUES ('" + name + "')";
```

<details>
<summary>🤔 Espacio para pensar (30 segundos)...</summary>

**✅ Respuesta:**
La comilla simple `'` en `O'Reilly` rompe la sintaxis SQL, generando:
```sql
INSERT INTO suppliers (name) VALUES ('O'Reilly')
                                        ^ SQL roto
```

**Peor aún,** un atacante podría ingresar:
```
'; DROP TABLE suppliers; --
```

Generando:
```sql
INSERT INTO suppliers (name) VALUES (''); DROP TABLE suppliers; --')
                                           ↑ Código malicioso ejecutado
```

**Solución:** Usar `PreparedStatement` con placeholders `?` que escapan caracteres especiales automáticamente.

</details>

---

### ❓ Pregunta 3: Métodos JDBC
**Sin mirar apuntes:** ¿Cuál es la diferencia entre estos dos métodos?

```java
ResultSet rs = stmt.executeQuery(sql);   // Opción A
int rows = stmt.executeUpdate(sql);       // Opción B
```

<details>
<summary>🤔 Espacio para pensar (30 segundos)...</summary>

**✅ Respuesta:**

| Método | Propósito | Retorna | Operaciones |
|--------|-----------|---------|-------------|
| `executeQuery()` | Consultar datos | `ResultSet` (tabla de resultados) | `SELECT` |
| `executeUpdate()` | Modificar datos | `int` (filas afectadas) | `INSERT`, `UPDATE`, `DELETE` |

**Regla nemotécnica:**
- **Query** = pregunta → obtienes respuesta (ResultSet)
- **Update** = modificar → obtienes confirmación (número de cambios)

</details>

---

### ❓ Pregunta 4: Try-with-resources
**Sin mirar apuntes:** ¿Qué hace esta sintaxis especial?

```java
try (Connection conn = DatabaseConnection.getConnection()) {
    // Código que usa conn
}  // ← ¿Qué pasa aquí?
```

<details>
<summary>🤔 Espacio para pensar (30 segundos)...</summary>

**✅ Respuesta:**
Al salir del bloque `try` (ya sea por éxito o por excepción), los recursos declarados en los paréntesis se cierran **automáticamente** en orden inverso.

Equivalente manual:
```java
Connection conn = null;
try {
    conn = DatabaseConnection.getConnection();
    // Código
} finally {
    if (conn != null) {
        conn.close();  // ← try-with-resources hace esto automáticamente
    }
}
```

**Ventajas:**
- Código más limpio
- Imposible olvidar cerrar recursos
- Manejo correcto de excepciones durante el cierre

</details>

---

### ❓ Pregunta 5: Placeholders
**Sin mirar apuntes:** ¿Cómo se mapean los valores en esta query?

```java
String sql = "INSERT INTO movements (id, type, fuel) VALUES (?, ?, ?)";
pstmt.setString(1, "M001");
pstmt.setString(2, "ENTRADA");
pstmt.setString(3, "Diesel");
```

<details>
<summary>🤔 Espacio para pensar (30 segundos)...</summary>

**✅ Respuesta:**

```
INSERT INTO movements (id,      type,        fuel     ) VALUES (?,       ?,          ?)
                       ↓         ↓            ↓                ↓         ↓           ↓
                       M001      ENTRADA      Diesel           1         2           3

pstmt.setString(1, "M001")      → primer ?  (columna id)
pstmt.setString(2, "ENTRADA")   → segundo ? (columna type)
pstmt.setString(3, "Diesel")    → tercer ?  (columna fuel)
```

**Importante:** Los índices empiezan en **1** (no en 0 como arrays).

</details>

---

**🎓 Si respondiste 4 o más correctamente:** ¡Excelente! Tienes la base sólida necesaria.
**🎓 Si respondiste menos de 4:** Revisa FASE 03 antes de continuar.

---

## 📖 Teoría: INSERT en JDBC

### ¿Qué es INSERT?

**INSERT** es la operación CRUD que **crea nuevos registros** en una tabla de base de datos. En el contexto de Forestech CLI:

```
Java (memoria temporal)              SQL Server (persistencia permanente)
┌─────────────────────┐              ┌─────────────────────────┐
│ Movement object     │              │ combustibles_movements  │
│ ├─ id: "MOV-001"    │   INSERT     │ ┌─────────────────────┐ │
│ ├─ type: "ENTRADA"  │ ─────────→   │ │ MOV-001 | ENTRADA |│ │
│ ├─ fuel: "Diesel"   │              │ │ Diesel  | 1000.0  │ │ │
│ └─ quantity: 1000.0 │              │ └─────────────────────┘ │
└─────────────────────┘              └─────────────────────────┘
      (se pierde al                       (persiste para siempre)
       cerrar app)
```

---

### ¿Por qué necesitamos INSERT?

**Antes de INSERT (solo SELECT):**
- ✅ Podías leer datos existentes en la BD
- ❌ No podías crear nuevos datos desde la aplicación
- ❌ Los objetos creados en Java se perdían al cerrar el programa
- ❌ No podías registrar operaciones de los usuarios

**Después de INSERT (CRUD completo):**
- ✅ Los datos creados en Java se guardan permanentemente
- ✅ Múltiples usuarios pueden agregar datos a la misma BD
- ✅ Los datos sobreviven al cierre de la aplicación
- ✅ Puedes auditar quién creó qué y cuándo

---

### ¿Cuándo usar INSERT?

| Escenario | Ejemplo Forestech | ¿Usar INSERT? |
|-----------|-------------------|---------------|
| Usuario registra carga de combustible | Camión recibe 5000L de Diesel | ✅ Sí |
| Sistema genera ID automático | Crear MOV-XXXXXXXX para nuevo movimiento | ✅ Sí |
| Importar datos desde Excel | Cargar 100 proveedores desde archivo | ✅ Sí (múltiples) |
| Usuario consulta inventario | Ver cuánto combustible queda | ❌ No (usar SELECT) |
| Usuario modifica cantidad | Corregir 1000L → 1200L | ❌ No (usar UPDATE) |
| Usuario elimina movimiento erróneo | Borrar registro duplicado | ❌ No (usar DELETE) |

---

### Anatomía de un INSERT con PreparedStatement

```java
// 1️⃣ Definir la query SQL con placeholders
String sql = "INSERT INTO combustibles_movements (id, movementType, fuelType, quantity) " +
             "VALUES (?, ?, ?, ?)";
//                     ↑   ↑   ↑   ↑
//                     1   2   3   4  ← Índices de los placeholders

// 2️⃣ Crear PreparedStatement
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

    // 3️⃣ Asignar valores a cada placeholder
    pstmt.setString(1, movement.getId());           // ? número 1
    pstmt.setString(2, movement.getMovementType()); // ? número 2
    pstmt.setString(3, movement.getFuelType());     // ? número 3
    pstmt.setDouble(4, movement.getQuantity());     // ? número 4

    // 4️⃣ Ejecutar la inserción
    int rowsAffected = pstmt.executeUpdate();
    //  ↑
    //  └─ Número de filas insertadas (debería ser 1)

    // 5️⃣ Verificar resultado
    if (rowsAffected == 1) {
        System.out.println("✅ Movimiento creado exitosamente");
        return true;
    } else {
        System.out.println("❌ No se insertó ninguna fila");
        return false;
    }

} catch (SQLException e) {
    // 6️⃣ Manejar errores
    System.err.println("Error al insertar: " + e.getMessage());
    return false;
}
```

---

### Diagrama de Flujo: Java → JDBC → SQL Server

```
┌──────────────────────────────────────────────────────────────────┐
│                          JAVA APPLICATION                        │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 1. Usuario crea objeto Movement                        │     │
│  │    Movement m = new Movement("ENTRADA", "Diesel", ...);│     │
│  └────────────────────────────────────────────────────────┘     │
│                              ↓                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 2. Llama a MovementService.createMovement(m)           │     │
│  └────────────────────────────────────────────────────────┘     │
│                              ↓                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 3. Service obtiene Connection y crea PreparedStatement │     │
│  │    - Define SQL con placeholders (?)                   │     │
│  │    - Mapea valores del objeto a parámetros             │     │
│  └────────────────────────────────────────────────────────┘     │
│                              ↓                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 4. Ejecuta pstmt.executeUpdate()                       │     │
│  └────────────────────────────────────────────────────────┘     │
└──────────────────┬───────────────────────────────────────────────┘
                   │
                   │ JDBC Driver (mssql-jdbc-X.X.X.jar)
                   ↓
┌──────────────────────────────────────────────────────────────────┐
│                         SQL SERVER                               │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 5. Recibe query compilada (sin inyección SQL)          │     │
│  │    INSERT INTO combustibles_movements VALUES (...)     │     │
│  └────────────────────────────────────────────────────────┘     │
│                              ↓                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 6. Valida constraints (PK, FK, NOT NULL, tipos)        │     │
│  └────────────────────────────────────────────────────────┘     │
│                              ↓                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 7. Inserta la fila en la tabla física                  │     │
│  │    combustibles_movements                              │     │
│  │    ┌──────────────────────────────────────┐            │     │
│  │    │ MOV-001 | ENTRADA | Diesel | 1000.0 │            │     │
│  │    └──────────────────────────────────────┘            │     │
│  └────────────────────────────────────────────────────────┘     │
│                              ↓                                   │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 8. Retorna rowsAffected = 1 (éxito)                    │     │
│  └────────────────────────────────────────────────────────┘     │
└──────────────────┬───────────────────────────────────────────────┘
                   │
                   ↓
┌──────────────────────────────────────────────────────────────────┐
│                          JAVA APPLICATION                        │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 9. Service verifica rowsAffected == 1                  │     │
│  │    - Retorna true (éxito)                              │     │
│  │    - Imprime mensaje de confirmación                   │     │
│  └────────────────────────────────────────────────────────┘     │
└──────────────────────────────────────────────────────────────────┘
```

---

### Tabla Comparativa: Statement vs PreparedStatement

| Característica | Statement (❌ Antiguo) | PreparedStatement (✅ Moderno) |
|----------------|------------------------|-------------------------------|
| **SQL Injection** | Vulnerable | Protegido automáticamente |
| **Sintaxis** | Concatenación de strings | Placeholders `?` |
| **Rendimiento** | Compila cada vez | Pre-compilado (reutilizable) |
| **Legibilidad** | Difícil con muchos parámetros | Clara y ordenada |
| **Tipos de datos** | Manual (escapar strings) | Automático (`setString`, `setInt`) |
| **Uso recomendado** | Nunca | Siempre para queries dinámicas |

**Ejemplo Statement (❌ NO USAR):**
```java
String sql = "INSERT INTO movements VALUES ('" + id + "', '" + type + "')";
stmt.executeUpdate(sql);  // ☠️ Vulnerable a SQL Injection
```

**Ejemplo PreparedStatement (✅ USAR SIEMPRE):**
```java
String sql = "INSERT INTO movements VALUES (?, ?)";
pstmt.setString(1, id);
pstmt.setString(2, type);
pstmt.executeUpdate();  // ✅ Seguro
```

---

### SQL Injection: Ataque Real

**Escenario:** Usuario malicioso ingresa este texto como "tipo de combustible":

```
Diesel'); DROP TABLE combustibles_movements; --
```

**Con Statement (vulnerable):**
```java
String fuelType = userInput;  // Valor malicioso arriba
String sql = "INSERT INTO movements (fuelType) VALUES ('" + fuelType + "')";
// Query resultante:
// INSERT INTO movements (fuelType) VALUES ('Diesel'); DROP TABLE combustibles_movements; --')
//                                                      ↑
//                                        ¡Código malicioso ejecutado!
```

**Resultado:** Tu tabla `combustibles_movements` queda **eliminada completamente**.

---

**Con PreparedStatement (protegido):**
```java
String fuelType = userInput;  // Mismo valor malicioso
String sql = "INSERT INTO movements (fuelType) VALUES (?)";
pstmt.setString(1, fuelType);
// Query resultante:
// INSERT INTO movements (fuelType) VALUES ('Diesel''); DROP TABLE combustibles_movements; --')
//                                          ↑
//                        Caracteres especiales escapados automáticamente
```

**Resultado:** Se inserta literalmente el string `Diesel'); DROP TABLE combustibles_movements; --` como texto (inofensivo).

---

## 💬 Técnica de Feynman: Explícalo con Tus Palabras

**Instrucciones:**
Sin mirar el código o teoría anterior, completa estos espacios en blanco con tus propias palabras (como si se lo explicaras a un compañero de clase):

---

### Ejercicio 1: ¿Qué hace PreparedStatement?

> PreparedStatement es una herramienta que me permite ________________________________
>
> en vez de ________________________________.
>
> La ventaja principal es que previene ________________________________
>
> porque ________________________________.

<details>
<summary>✅ Ver ejemplo de respuesta</summary>

**Ejemplo de respuesta válida:**

> PreparedStatement es una herramienta que me permite **ejecutar queries SQL con valores dinámicos de forma segura** en vez de **concatenar strings directamente**. La ventaja principal es que previene **ataques de SQL Injection** porque **los valores se escapan automáticamente y se tratan siempre como datos, nunca como código SQL**.

</details>

---

### Ejercicio 2: ¿Qué retorna executeUpdate()?

> Cuando llamo a `pstmt.executeUpdate()`, el método me retorna ________________________________
>
> Este número representa ________________________________.
>
> En el caso de un INSERT, si todo sale bien, debería retornar ________________________________
>
> porque ________________________________.

<details>
<summary>✅ Ver ejemplo de respuesta</summary>

**Ejemplo de respuesta válida:**

> Cuando llamo a `pstmt.executeUpdate()`, el método me retorna **un número entero (int)**. Este número representa **cuántas filas de la base de datos fueron afectadas por la operación**. En el caso de un INSERT, si todo sale bien, debería retornar **1** porque **estoy insertando exactamente una fila nueva**.

</details>

---

### Ejercicio 3: ¿Por qué los índices empiezan en 1?

> En PreparedStatement, cuando uso `setString(1, valor)`, el índice empieza en 1 en vez de 0 porque ________________________________.
>
> Si mi query tiene 5 placeholders (?), los índices válidos son ________________________________.

<details>
<summary>✅ Ver ejemplo de respuesta</summary>

**Ejemplo de respuesta válida:**

> En PreparedStatement, cuando uso `setString(1, valor)`, el índice empieza en 1 en vez de 0 porque **JDBC sigue el estándar SQL (no las convenciones de arrays de Java), y en SQL las columnas se numeran desde 1**. Si mi query tiene 5 placeholders (?), los índices válidos son **1, 2, 3, 4, 5**.

</details>

---

**🎓 Si puedes explicar estos conceptos con fluidez, has comprendido la teoría.**

---

## 👨‍💻 Implementación Paso a Paso

### Paso 1: Crear la clase MovementService

**📍 Ubicación:** `forestech-cli-java/src/main/java/com/forestech/services/MovementService.java`

---

#### ❓ Active Recall: Antes de escribir código

**Pregunta:** ¿Qué elementos debe tener la clase MovementService?

<details>
<summary>🤔 Piensa 30 segundos antes de expandir...</summary>

**✅ Elementos necesarios:**
1. Modificador `public` (accesible desde Main)
2. Nombre `MovementService`
3. Constructor privado (es una utility class, no instanciable)
4. Imports necesarios (Connection, PreparedStatement, SQLException, Movement)
5. Método `public static boolean createMovement(Movement movement)`

</details>

---

#### 📝 Código esqueleto (TÚ completas los métodos)

```java
package com.forestech.services;

// 1️⃣ Imports necesarios (BOILERPLATE PERMITIDO)
import com.forestech.config.DatabaseConnection;
import com.forestech.models.Movement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Servicio para gestionar operaciones CRUD de movimientos de combustible.
 *
 * @author Tu Nombre
 * @version 1.0
 * @since Fase 4.1
 */
public class MovementService {

    // 2️⃣ Constructor privado (evita instanciación)
    private MovementService() {
        // Esta clase solo tiene métodos estáticos
    }

    // 3️⃣ Método createMovement (TÚ IMPLEMENTAS EN EL SIGUIENTE PASO)

}
```

---

### Paso 2: Implementar el método createMovement()

**❓ Active Recall:** Antes de mirar la solución, responde:

1. ¿Qué parámetros necesita el método?
2. ¿Qué tipo de retorno debe tener?
3. ¿Qué recursos JDBC necesitas crear?
4. ¿Cuántos placeholders (?) necesita la query?

<details>
<summary>🤔 Piensa 2 minutos antes de expandir...</summary>

**✅ Respuestas:**
1. **Parámetro:** `Movement movement` (el objeto a guardar)
2. **Retorno:** `boolean` (true si insertó, false si falló)
3. **Recursos:** `Connection` y `PreparedStatement` (con try-with-resources)
4. **Placeholders:** 6 (id, movementType, fuelType, quantity, unitPrice, movementDate)

</details>

---

#### 📝 Esqueleto del método (COMPLETA LAS SECCIONES MARCADAS)

```java
/**
 * Inserta un nuevo movimiento de combustible en la base de datos.
 *
 * @param movement Objeto Movement con los datos a insertar
 * @return true si la inserción fue exitosa, false si falló
 */
public static boolean createMovement(Movement movement) {

    // PASO 2.1: Definir la query SQL con placeholders
    String sql = "INSERT INTO combustibles_movements " +
                 "(id, movementType, fuelType, quantity, unitPrice, movementDate) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    //           ↑   ↑            ↑         ↑         ↑          ↑
    //           1   2            3         4         5          6

    // PASO 2.2: Ejecutar la inserción con try-with-resources
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // PASO 2.3: Asignar valores a cada placeholder
        // TODO: TÚ COMPLETAS AQUÍ (ver instrucciones abajo)

        // PASO 2.4: Ejecutar la inserción
        // TODO: TÚ COMPLETAS AQUÍ (ver instrucciones abajo)

        // PASO 2.5: Verificar resultado
        // TODO: TÚ COMPLETAS AQUÍ (ver instrucciones abajo)

    } catch (SQLException e) {
        // PASO 2.6: Manejar errores
        System.err.println("❌ Error al insertar movimiento: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
```

---

#### 📋 Instrucciones detalladas para completar

**PASO 2.3: Asignar valores a placeholders**

Usa los métodos del objeto `movement` para obtener cada valor:

```java
// Placeholder 1: id (String)
pstmt.setString(1, movement.getId());

// Placeholder 2: movementType (String) - Ej: "ENTRADA" o "SALIDA"
// TODO: Escribe aquí el código


// Placeholder 3: fuelType (String) - Ej: "Diesel", "Gasolina Corriente"
// TODO: Escribe aquí el código


// Placeholder 4: quantity (double) - Ej: 1000.0
// PISTA: Usa pstmt.setDouble() en vez de setString()
// TODO: Escribe aquí el código


// Placeholder 5: unitPrice (double) - Ej: 3.5
// TODO: Escribe aquí el código


// Placeholder 6: movementDate (String) - Formato: "2025-01-07 14:30:00"
// TODO: Escribe aquí el código

```

---

**PASO 2.4: Ejecutar la inserción**

```java
// Ejecutar la query y obtener el número de filas afectadas
// PISTA: Usa executeUpdate(), NO executeQuery()
// TODO: int rowsAffected = ...?
```

---

**PASO 2.5: Verificar resultado**

```java
// Verificar si se insertó exactamente 1 fila
// TODO: Completa esta lógica
if (/* condición de éxito */) {
    System.out.println("✅ Movimiento creado exitosamente:");
    System.out.println("   ID: " + movement.getId());
    System.out.println("   Tipo: " + movement.getMovementType());
    System.out.println("   Combustible: " + movement.getFuelType());
    System.out.println("   Cantidad: " + movement.getQuantity() + "L");
    return true;  // Éxito
} else {
    System.out.println("❌ No se insertó ninguna fila (posible error silencioso)");
    return false;  // Falló sin excepción
}
```

---

#### ✅ Solución completa (solo consulta después de intentar)

<details>
<summary>💡 Ver solución completa del método</summary>

```java
public static boolean createMovement(Movement movement) {

    String sql = "INSERT INTO combustibles_movements " +
                 "(id, movementType, fuelType, quantity, unitPrice, movementDate) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // Asignar valores a placeholders
        pstmt.setString(1, movement.getId());
        pstmt.setString(2, movement.getMovementType());
        pstmt.setString(3, movement.getFuelType());
        pstmt.setDouble(4, movement.getQuantity());
        pstmt.setDouble(5, movement.getUnitPrice());
        pstmt.setString(6, movement.getMovementDate());

        // Ejecutar la inserción
        int rowsAffected = pstmt.executeUpdate();

        // Verificar resultado
        if (rowsAffected == 1) {
            System.out.println("✅ Movimiento creado exitosamente:");
            System.out.println("   ID: " + movement.getId());
            System.out.println("   Tipo: " + movement.getMovementType());
            System.out.println("   Combustible: " + movement.getFuelType());
            System.out.println("   Cantidad: " + movement.getQuantity() + "L");
            return true;
        } else {
            System.out.println("❌ No se insertó ninguna fila (posible error silencioso)");
            return false;
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al insertar movimiento: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
```

</details>

---

### Paso 3: Probar en Main.java

**📍 Ubicación:** `forestech-cli-java/src/main/java/com/forestech/Main.java`

---

#### ❓ Active Recall: Antes de escribir el código de prueba

**Pregunta:** ¿Qué pasos necesitas para probar el método createMovement()?

<details>
<summary>🤔 Piensa 1 minuto antes de expandir...</summary>

**✅ Pasos necesarios:**
1. Importar `MovementService` y `Movement`
2. Crear un objeto `Movement` con datos de prueba realistas
3. Llamar a `MovementService.createMovement(movement)`
4. Verificar el resultado (true/false)
5. Imprimir mensaje de confirmación
6. Verificar manualmente en SQL Server Management Studio

</details>

---

#### 📝 Código de prueba (COMPLETA LAS SECCIONES MARCADAS)

Agrega este código al final del método `main()` en `Main.java`:

```java
System.out.println("\n" + "=".repeat(60));
System.out.println("   PRUEBA DE INSERCIÓN DE MOVIMIENTO (FASE 4.1)");
System.out.println("=".repeat(60));

// PASO 3.1: Crear objeto Movement con datos de prueba
// PISTA: Usa el constructor que creaste en Fase 2
// Datos sugeridos:
//   - Tipo: "ENTRADA"
//   - Combustible: "Diesel"
//   - Cantidad: 5000.0
//   - Precio unitario: 3.75
// TODO: Movement testMovement = new Movement(...);


// PASO 3.2: Intentar insertar el movimiento
// TODO: boolean success = MovementService.createMovement(testMovement);


// PASO 3.3: Verificar resultado
// TODO: if (success) { ... } else { ... }


System.out.println("=".repeat(60));
System.out.println("💡 VERIFICA MANUALMENTE EN SQL SERVER:");
System.out.println("   SELECT * FROM combustibles_movements ORDER BY movementDate DESC");
System.out.println("=".repeat(60));
```

---

#### ✅ Solución completa (solo consulta después de intentar)

<details>
<summary>💡 Ver solución completa del código de prueba</summary>

```java
System.out.println("\n" + "=".repeat(60));
System.out.println("   PRUEBA DE INSERCIÓN DE MOVIMIENTO (FASE 4.1)");
System.out.println("=".repeat(60));

// Crear objeto Movement con datos de prueba
Movement testMovement = new Movement(
    "ENTRADA",           // movementType
    "Diesel",            // fuelType
    5000.0,              // quantity
    3.75                 // unitPrice
);

// Intentar insertar el movimiento
boolean success = MovementService.createMovement(testMovement);

// Verificar resultado
if (success) {
    System.out.println("\n🎉 PRUEBA EXITOSA: El movimiento fue insertado correctamente");
} else {
    System.out.println("\n❌ PRUEBA FALLIDA: Revisa los errores arriba");
}

System.out.println("=".repeat(60));
System.out.println("💡 VERIFICA MANUALMENTE EN SQL SERVER:");
System.out.println("   SELECT * FROM combustibles_movements ORDER BY movementDate DESC");
System.out.println("=".repeat(60));
```

</details>

---

### Paso 4: Compilar y ejecutar

```bash
# 1. Limpiar compilaciones anteriores
mvn clean

# 2. Compilar el proyecto
mvn compile

# 3. Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

---

### Paso 5: Verificar en SQL Server Management Studio

1. Abre SQL Server Management Studio
2. Conéctate al servidor (localhost o 24.199.89.134)
3. Ejecuta esta query:

```sql
SELECT TOP 10 *
FROM combustibles_movements
ORDER BY movementDate DESC;
```

4. Verifica que aparece tu movimiento con:
   - ID generado (formato MOV-XXXXXXXX)
   - Tipo: "ENTRADA"
   - Combustible: "Diesel"
   - Cantidad: 5000.0
   - Precio: 3.75
   - Fecha actual

---

## 🔍 Depuración de Errores Comunes

### Error 1: `java.sql.SQLException: Parameter index out of range (X > number of parameters, which is Y)`

**Causa:** Intentaste asignar un valor a un placeholder que no existe.

**Ejemplo de código roto:**
```java
String sql = "INSERT INTO movements (id, type) VALUES (?, ?)";  // Solo 2 placeholders
pstmt.setString(1, "M001");
pstmt.setString(2, "ENTRADA");
pstmt.setString(3, "Diesel");  // ❌ No existe el placeholder 3
```

**Solución:**
1. Cuenta los `?` en tu query SQL
2. Verifica que usas índices del 1 al N (donde N = número de `?`)
3. Asegúrate de que cada columna en el INSERT tenga su `?` correspondiente

---

### Error 2: `java.sql.SQLException: Column count doesn't match value count at row 1`

**Causa:** El número de columnas en el INSERT no coincide con el número de valores.

**Ejemplo de código roto:**
```java
String sql = "INSERT INTO movements (id, type, fuel) VALUES (?, ?)";  // 3 columnas, 2 valores
```

**Solución:**
```java
String sql = "INSERT INTO movements (id, type, fuel) VALUES (?, ?, ?)";  // 3 columnas, 3 valores
```

---

### Error 3: `java.sql.SQLException: Cannot insert the value NULL into column 'X'`

**Causa:** Olvidaste asignar un valor a un placeholder, y esa columna tiene la restricción `NOT NULL`.

**Ejemplo de código roto:**
```java
String sql = "INSERT INTO movements (id, type, quantity) VALUES (?, ?, ?)";
pstmt.setString(1, "M001");
pstmt.setString(2, "ENTRADA");
// ❌ Olvidaste: pstmt.setDouble(3, 1000.0);
int rows = pstmt.executeUpdate();  // Lanza SQLException
```

**Solución:**
Asigna valores a **todos** los placeholders antes de ejecutar:
```java
pstmt.setString(1, "M001");
pstmt.setString(2, "ENTRADA");
pstmt.setDouble(3, 1000.0);  // ✅ Ahora sí
```

---

### Error 4: `java.sql.SQLException: Violation of PRIMARY KEY constraint`

**Causa:** Intentaste insertar un ID que ya existe en la tabla.

**Ejemplo:**
```sql
-- Ya existe en la BD:
INSERT INTO movements VALUES ('MOV-001', ...)

-- Intentas insertar nuevamente:
INSERT INTO movements VALUES ('MOV-001', ...)  -- ❌ PK duplicada
```

**Solución:**
Asegúrate de que cada nuevo movimiento tenga un ID único. Verifica tu método `IdGenerator.generateMovementId()`.

---

### Error 5: `rowsAffected = 0` (sin excepción)

**Causa:** La query se ejecutó correctamente, pero no insertó ninguna fila (raro, pero posible con triggers o constraints silenciosos).

**Depuración:**
1. Imprime la query completa antes de ejecutar:
   ```java
   System.out.println("SQL: " + pstmt.toString());
   ```

2. Copia la query y ejecútala manualmente en SQL Server Management Studio

3. Verifica que la tabla existe y no tiene triggers que bloqueen inserciones

---

### Error 6: `java.sql.SQLSyntaxErrorException: Invalid column name 'X'`

**Causa:** El nombre de la columna en la query no existe en la tabla.

**Ejemplo:**
```java
String sql = "INSERT INTO movements (movementTipo, ...) VALUES (?, ...)";
//                                    ↑ Nombre incorrecto (debería ser movementType)
```

**Solución:**
Verifica los nombres exactos de las columnas ejecutando:
```sql
EXEC sp_columns combustibles_movements;
```

---

## 🧪 Práctica Deliberada

### Nivel 1: Guiado (con ayuda)

**Objetivo:** Insertar un movimiento de tipo "SALIDA" (combustible que sale del inventario).

**Instrucciones:**
1. Crea un nuevo objeto Movement con estos datos:
   - Tipo: "SALIDA"
   - Combustible: "Gasolina Corriente"
   - Cantidad: 2500.0
   - Precio unitario: 4.20

2. Llama a `MovementService.createMovement()` con el objeto creado

3. Verifica en consola que el mensaje muestra "✅ Movimiento creado exitosamente"

4. Verifica en SQL Server que aparece el nuevo registro

**Código esqueleto:**
```java
Movement salidaMovement = new Movement(
    "______",           // TODO: Tipo
    "______",           // TODO: Combustible
    ______,             // TODO: Cantidad
    ______              // TODO: Precio
);

boolean resultado = MovementService.createMovement(salidaMovement);

if (resultado) {
    System.out.println("✅ Salida registrada");
} else {
    System.out.println("❌ Error al registrar salida");
}
```

<details>
<summary>✅ Ver solución</summary>

```java
Movement salidaMovement = new Movement(
    "SALIDA",
    "Gasolina Corriente",
    2500.0,
    4.20
);

boolean resultado = MovementService.createMovement(salidaMovement);

if (resultado) {
    System.out.println("✅ Salida registrada");
} else {
    System.out.println("❌ Error al registrar salida");
}
```

</details>

---

### Nivel 2: Semi-guiado (con pistas)

**Objetivo:** Insertar 3 movimientos diferentes en una sola ejecución del programa.

**Requerimientos:**
- Movimiento 1: ENTRADA de Diesel, 10000L, $3.50
- Movimiento 2: SALIDA de Gasolina Extra, 1500L, $5.00
- Movimiento 3: ENTRADA de Gasolina Corriente, 8000L, $4.10

**Pistas:**
- Crea un array o lista de Movement
- Itera sobre la colección llamando a createMovement() para cada uno
- Cuenta cuántos se insertaron exitosamente vs cuántos fallaron
- Imprime un resumen al final

**No mires la solución hasta que lo intentes 10 minutos.**

<details>
<summary>✅ Ver solución</summary>

```java
// Crear array de movimientos de prueba
Movement[] movimientos = {
    new Movement("ENTRADA", "Diesel", 10000.0, 3.50),
    new Movement("SALIDA", "Gasolina Extra", 1500.0, 5.00),
    new Movement("ENTRADA", "Gasolina Corriente", 8000.0, 4.10)
};

int exitosos = 0;
int fallidos = 0;

System.out.println("\n=== INSERCIÓN MÚLTIPLE ===");
for (int i = 0; i < movimientos.length; i++) {
    System.out.println("\nInsertando movimiento " + (i + 1) + "...");
    boolean success = MovementService.createMovement(movimientos[i]);

    if (success) {
        exitosos++;
    } else {
        fallidos++;
    }
}

System.out.println("\n=== RESUMEN ===");
System.out.println("✅ Exitosos: " + exitosos);
System.out.println("❌ Fallidos: " + fallidos);
System.out.println("📊 Total: " + movimientos.length);
```

</details>

---

### Nivel 3: Autónomo (sin ayuda)

**Objetivo:** Crear un método auxiliar `createMultipleMovements(List<Movement> movements)` que:

1. Reciba una lista de movimientos
2. Intente insertar cada uno
3. Retorne el número de inserciones exitosas
4. Imprima un resumen detallado al final

**Especificaciones:**
- Firma del método: `public static int createMultipleMovements(List<Movement> movements)`
- Ubicación: Dentro de `MovementService.java`
- Debe manejar listas vacías sin fallar
- Debe continuar insertando aunque uno falle

**Prueba:**
```java
List<Movement> batch = new ArrayList<>();
batch.add(new Movement("ENTRADA", "Diesel", 5000.0, 3.60));
batch.add(new Movement("ENTRADA", "Gasolina Extra", 3000.0, 5.20));
batch.add(new Movement("SALIDA", "Diesel", 1200.0, 3.60));

int insertados = MovementService.createMultipleMovements(batch);
System.out.println("Total insertados: " + insertados);
```

**No mires la solución hasta completarlo por tu cuenta.**

<details>
<summary>✅ Ver solución</summary>

```java
/**
 * Inserta múltiples movimientos en la base de datos.
 *
 * @param movements Lista de movimientos a insertar
 * @return Número de movimientos insertados exitosamente
 */
public static int createMultipleMovements(List<Movement> movements) {
    if (movements == null || movements.isEmpty()) {
        System.out.println("⚠️ La lista de movimientos está vacía");
        return 0;
    }

    int exitosos = 0;
    int fallidos = 0;

    System.out.println("\n=== INSERCIÓN MÚLTIPLE ===");
    System.out.println("Total a insertar: " + movements.size());

    for (int i = 0; i < movements.size(); i++) {
        Movement m = movements.get(i);
        System.out.println("\n[" + (i + 1) + "/" + movements.size() + "] Insertando " +
                          m.getMovementType() + " de " + m.getFuelType() + "...");

        boolean success = createMovement(m);

        if (success) {
            exitosos++;
        } else {
            fallidos++;
        }
    }

    System.out.println("\n=== RESUMEN DE INSERCIÓN MÚLTIPLE ===");
    System.out.println("✅ Exitosos: " + exitosos);
    System.out.println("❌ Fallidos: " + fallidos);
    System.out.println("📊 Tasa de éxito: " + (exitosos * 100.0 / movements.size()) + "%");

    return exitosos;
}
```

</details>

---

### Nivel 4: Desafío (avanzado)

**Objetivo:** Implementar un método `createMovementWithRetry()` que:

1. Intente insertar un movimiento
2. Si falla por PK duplicada, genere un nuevo ID y reintente
3. Reintente máximo 3 veces
4. Retorne `true` si algún intento tuvo éxito, `false` si todos fallaron

**Especificaciones:**
- Firma: `public static boolean createMovementWithRetry(Movement movement, int maxRetries)`
- Si el error NO es de PK duplicada, no reintentar (retornar false inmediatamente)
- Usar `IdGenerator.generateMovementId()` para generar nuevos IDs
- Imprimir en consola cada intento

**Pista para detectar PK duplicada:**
```java
catch (SQLException e) {
    if (e.getMessage().contains("PRIMARY KEY") || e.getMessage().contains("Violation")) {
        // Es un error de PK duplicada, reintentar
    } else {
        // Es otro tipo de error, no reintentar
    }
}
```

**Este ejercicio requiere reflexión profunda. Intenta al menos 30 minutos antes de ver la solución.**

<details>
<summary>✅ Ver solución</summary>

```java
import com.forestech.utils.IdGenerator;

/**
 * Inserta un movimiento con reintentos automáticos en caso de PK duplicada.
 *
 * @param movement Movimiento a insertar
 * @param maxRetries Número máximo de reintentos
 * @return true si se insertó exitosamente, false si todos los intentos fallaron
 */
public static boolean createMovementWithRetry(Movement movement, int maxRetries) {
    int attempt = 0;

    while (attempt < maxRetries) {
        attempt++;
        System.out.println("🔄 Intento " + attempt + " de " + maxRetries + "...");

        String sql = "INSERT INTO combustibles_movements " +
                     "(id, movementType, fuelType, quantity, unitPrice, movementDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movement.getId());
            pstmt.setString(2, movement.getMovementType());
            pstmt.setString(3, movement.getFuelType());
            pstmt.setDouble(4, movement.getQuantity());
            pstmt.setDouble(5, movement.getUnitPrice());
            pstmt.setString(6, movement.getMovementDate());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("✅ Movimiento insertado exitosamente en intento " + attempt);
                return true;
            }

        } catch (SQLException e) {
            // Detectar si es error de PK duplicada
            if (e.getMessage().contains("PRIMARY KEY") ||
                e.getMessage().contains("Violation") ||
                e.getMessage().contains("duplicate")) {

                System.out.println("⚠️ ID duplicado detectado: " + movement.getId());

                // Generar nuevo ID solo si no es el último intento
                if (attempt < maxRetries) {
                    String nuevoId = IdGenerator.generateMovementId();
                    System.out.println("🔄 Generando nuevo ID: " + nuevoId);
                    // Aquí necesitarías un setter para ID (actualmente es final)
                    // Por ahora, este método es conceptual
                } else {
                    System.out.println("❌ Máximo de reintentos alcanzado");
                }

            } else {
                // Otro tipo de error, no reintentar
                System.err.println("❌ Error no recuperable: " + e.getMessage());
                return false;
            }
        }
    }

    System.out.println("❌ No se pudo insertar el movimiento después de " + maxRetries + " intentos");
    return false;
}
```

**Nota:** Este ejercicio revela una limitación del diseño actual: el campo `id` en Movement es `final`, por lo que no puedes cambiarlo después de crear el objeto. En una implementación real, considerarías:
- Hacer `id` mutable (no recomendado)
- Generar el ID justo antes del INSERT (en vez de en el constructor)
- Usar auto-increment en SQL Server (no requiere generar ID desde Java)

</details>

---

## 🔄 Interleaving: Comparación SELECT vs INSERT

### Tabla Comparativa

| Aspecto | SELECT (Fase 3) | INSERT (Fase 4.1) |
|---------|-----------------|-------------------|
| **Propósito** | Leer datos existentes | Crear nuevos datos |
| **Método JDBC** | `executeQuery()` | `executeUpdate()` |
| **Retorna** | `ResultSet` (tabla de datos) | `int` (filas afectadas) |
| **Modifica BD** | ❌ No | ✅ Sí |
| **SQL Injection** | Peligroso con Statement | Protegido con PreparedStatement |
| **Placeholders** | Opcionales | Obligatorios para valores dinámicos |
| **Requiere mapeo** | ResultSet → Objeto Java | Objeto Java → Parámetros SQL |
| **Manejo de errores** | SQLException (conexión, sintaxis) | SQLException + constraints (PK, FK, NOT NULL) |
| **Reversibilidad** | Siempre reversible (solo lectura) | Irreversible (sin ROLLBACK manual) |

---

### Código lado a lado

**SELECT:**
```java
String sql = "SELECT * FROM combustibles_movements WHERE movementType = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, "ENTRADA");

    ResultSet rs = pstmt.executeQuery();  // ← Retorna tabla de datos

    while (rs.next()) {
        // Mapear ResultSet → Objeto Java
        String id = rs.getString("id");
        String type = rs.getString("movementType");
        // ...
        Movement m = new Movement(type, fuel, quantity, price);
    }
}
```

**INSERT:**
```java
String sql = "INSERT INTO combustibles_movements (id, movementType, ...) VALUES (?, ?, ...)";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    // Mapear Objeto Java → Parámetros SQL
    pstmt.setString(1, movement.getId());
    pstmt.setString(2, movement.getMovementType());
    // ...

    int rowsAffected = pstmt.executeUpdate();  // ← Retorna número de cambios

    if (rowsAffected == 1) {
        System.out.println("✅ Insertado");
    }
}
```

---

### Ejercicio de Interleaving

**Instrucciones:** Sin mirar el código arriba, completa esta tabla desde la memoria:

| Pregunta | SELECT | INSERT |
|----------|--------|--------|
| ¿Qué método ejecutas? | `executeQuery()` | ? |
| ¿Qué tipo de dato retorna? | `ResultSet` | ? |
| ¿Modifica la base de datos? | No | ? |
| ¿Cuándo usas placeholders `?`? | ? | ? |

<details>
<summary>✅ Ver respuestas</summary>

| Pregunta | SELECT | INSERT |
|----------|--------|--------|
| ¿Qué método ejecutas? | `executeQuery()` | `executeUpdate()` |
| ¿Qué tipo de dato retorna? | `ResultSet` | `int` |
| ¿Modifica la base de datos? | No | Sí |
| ¿Cuándo usas placeholders `?`? | Para filtros dinámicos (WHERE) | Para todos los valores a insertar |

</details>

---

## 📊 Autoevaluación (Metacognition)

### Escala de Confianza

Para cada concepto, califica tu nivel de confianza del 1 al 5:

| Concepto | 1 (no entiendo) | 2 (confuso) | 3 (más o menos) | 4 (confiado) | 5 (puedo enseñarlo) |
|----------|----------------|-------------|-----------------|--------------|---------------------|
| Diferencia entre executeQuery() y executeUpdate() | ☐ | ☐ | ☐ | ☐ | ☐ |
| Uso de placeholders `?` | ☐ | ☐ | ☐ | ☐ | ☐ |
| Prevención de SQL Injection | ☐ | ☐ | ☐ | ☐ | ☐ |
| Interpretación de rowsAffected | ☐ | ☐ | ☐ | ☐ | ☐ |
| Try-with-resources | ☐ | ☐ | ☐ | ☐ | ☐ |
| Mapeo de objetos Java a SQL | ☐ | ☐ | ☐ | ☐ | ☐ |
| Depuración de errores de inserción | ☐ | ☐ | ☐ | ☐ | ☐ |

---

### Reflexión Escrita

Tómate 5 minutos para responder estas preguntas (escribe en papel o archivo de texto):

1. **¿Qué concepto te pareció más difícil de entender?** ¿Por qué crees que fue difícil?

2. **¿Qué analogía o ejemplo te ayudó más a comprender INSERT?**

3. **Si tuvieras que explicarle PreparedStatement a un compañero, ¿qué le dirías en 3 frases?**

4. **¿Qué error cometiste durante la implementación?** ¿Qué aprendiste de ese error?

5. **¿En qué situación de Forestech CLI usarías INSERT en el futuro?**

---

### Plan de Acción

Marca con ✅ según corresponda:

- [ ] **Todos mis conceptos están en nivel 4 o 5** → Avanza a FASE_04.2_SELECT_READ.md
- [ ] **Tengo 1-2 conceptos en nivel 3** → Repasa esos temas específicos durante 30 minutos, luego avanza
- [ ] **Tengo 3 o más conceptos en nivel 1-2** → Repite los ejercicios de práctica deliberada antes de avanzar
- [ ] **No logré insertar ningún movimiento exitosamente** → Solicita ayuda de un instructor o compañero

---

## 📅 Plan de Repaso (Spaced Repetition)

La memoria funciona mejor con repasos espaciados en el tiempo. Programa estas actividades:

### 📌 Día 1 (hoy, después de completar este archivo)

- [ ] Ejecuta el proyecto y verifica que puedes insertar 3 movimientos diferentes
- [ ] Explica en voz alta (o por escrito) qué hace cada línea del método `createMovement()`
- [ ] Crea un mini-resumen de 1 página con tus propias palabras

---

### 📌 Día 3 (dentro de 3 días)

- [ ] Sin mirar código, escribe desde cero la firma del método `createMovement()`
- [ ] Enumera los 6 pasos del flujo de INSERT (Java → JDBC → SQL Server → Retorno)
- [ ] Responde las 5 preguntas de Active Recall del inicio SIN mirar las respuestas
- [ ] Crea 1 movimiento de prueba nuevo y verifica que se inserte correctamente

---

### 📌 Día 7 (dentro de 1 semana)

- [ ] Implementa el ejercicio de Nivel 3 (createMultipleMovements) desde cero sin consultar la solución
- [ ] Explícale a alguien (o a una patito de goma) qué es SQL Injection y cómo prevenirlo
- [ ] Compara tu código actual con la solución del archivo y anota diferencias
- [ ] Lee tu reflexión escrita del día 1 y actualízala con nuevos aprendizajes

---

## 🐛 Mini-Quiz de Depuración: Código Roto

### Ejercicio Bonus: Encuentra los 5 errores

Este código tiene exactamente **5 errores**. Encuéntralos todos antes de ejecutar:

```java
public static boolean createMovement(Movement movement) {

    String sql = "INSERT INTO combustibles_movements " +
                 "(id, movementType, fuelType, quantity) " +  // Error 1 oculto aquí
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(0, movement.getId());              // Error 2
        pstmt.setString(2, movement.getMovementType());
        pstmt.setString(3, movement.getFuelType());
        pstmt.setDouble(4, movement.getQuantity());
        pstmt.setDouble(5, movement.getUnitPrice());
        pstmt.setString(6, movement.getMovementDate());

        ResultSet rs = pstmt.executeQuery();               // Error 3

        if (rs.getInt(1) == 1) {                          // Error 4
            System.out.println("✅ Movimiento creado");
            return true;
        }

    } catch (SQLException e) {
        System.out.println(e);                            // Error 5
        return false;
    }

    return false;
}
```

<details>
<summary>✅ Ver solución con explicación</summary>

**Error 1:** La query declara 4 columnas pero tiene 6 placeholders `?`
```java
// ❌ Incorrecto:
"(id, movementType, fuelType, quantity) VALUES (?, ?, ?, ?, ?, ?)"
//  4 columnas                                   6 placeholders

// ✅ Correcto:
"(id, movementType, fuelType, quantity, unitPrice, movementDate) VALUES (?, ?, ?, ?, ?, ?)"
//  6 columnas                                                            6 placeholders
```

**Error 2:** Los índices de PreparedStatement empiezan en 1, no en 0
```java
// ❌ Incorrecto:
pstmt.setString(0, movement.getId());

// ✅ Correcto:
pstmt.setString(1, movement.getId());
```

**Error 3:** INSERT usa `executeUpdate()`, no `executeQuery()`
```java
// ❌ Incorrecto:
ResultSet rs = pstmt.executeQuery();

// ✅ Correcto:
int rowsAffected = pstmt.executeUpdate();
```

**Error 4:** `executeUpdate()` retorna `int`, no `ResultSet`
```java
// ❌ Incorrecto:
if (rs.getInt(1) == 1) {

// ✅ Correcto:
if (rowsAffected == 1) {
```

**Error 5:** Debes imprimir el mensaje de la excepción, no el objeto completo
```java
// ❌ Incorrecto:
System.out.println(e);

// ✅ Mejor:
System.err.println("Error: " + e.getMessage());
e.printStackTrace();
```

</details>

---

## ✅ Checklist de Salida

Marca cada ítem cuando puedas hacerlo **sin consultar apuntes**:

### Conceptos Teóricos
- [ ] Puedo explicar la diferencia entre `executeQuery()` y `executeUpdate()` en 2 frases
- [ ] Puedo dibujar el diagrama de flujo Java → JDBC → SQL Server sin mirar
- [ ] Puedo explicar qué es SQL Injection y cómo PreparedStatement lo previene
- [ ] Entiendo por qué los índices de placeholders empiezan en 1 (no en 0)

### Implementación Práctica
- [ ] Escribí el método `createMovement()` completo en MovementService.java
- [ ] El método compila sin errores
- [ ] Inserté exitosamente al menos 3 movimientos de prueba
- [ ] Verifiqué manualmente en SQL Server que los datos son correctos

### Depuración
- [ ] Usé breakpoints para inspeccionar el objeto Movement antes de insertar
- [ ] Verifiqué el valor de `rowsAffected` en el debugger
- [ ] Capturé y analicé al menos 1 SQLException

### Ejercicios
- [ ] Completé el ejercicio de Nivel 1 (Guiado)
- [ ] Completé el ejercicio de Nivel 2 (Semi-guiado)
- [ ] Intenté el ejercicio de Nivel 3 (Autónomo) al menos 15 minutos
- [ ] (Opcional) Resolví el ejercicio de Nivel 4 (Desafío)

### Metacognición
- [ ] Completé la autoevaluación de confianza (1-5 por concepto)
- [ ] Respondí las 5 preguntas de reflexión escrita
- [ ] Programé los repasos del día 3 y día 7 en mi calendario

---

**🎓 Si marcaste al menos 12 de los 15 ítems principales, estás listo para FASE_04.2_SELECT_READ.md**

---

## 📚 Recursos Adicionales

### Documentación Oficial

- [Documentación de PreparedStatement (Oracle)](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)
- [JDBC Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/jdbc/)
- [SQL Server JDBC Driver (Microsoft)](https://docs.microsoft.com/en-us/sql/connect/jdbc/)

### Videos Recomendados (español)

- "JDBC PreparedStatement explicado en 10 minutos" - Canal: Programación ATS
- "Prevención de SQL Injection en Java" - Canal: TodoCode
- "INSERT con JDBC paso a paso" - Canal: Codificandolo

### Artículos Complementarios

- [OWASP SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)
- [Baeldung: JDBC PreparedStatement](https://www.baeldung.com/java-prepared-statement)

---

## ➡️ Próximo Paso

**Archivo siguiente:** `FASE_04.2_SELECT_READ.md`

**Vista previa:** En el siguiente checkpoint aprenderás a:
- Ejecutar queries SELECT con `executeQuery()`
- Recorrer un `ResultSet` con `while (rs.next())`
- Mapear filas de la BD a objetos Java
- Implementar métodos de lectura: `getAllMovements()`, `getMovementsByType()`, `getMovementById()`
- Aplicar filtros dinámicos con placeholders en cláusulas WHERE

**Prerequisito:** Debes poder insertar movimientos exitosamente antes de continuar.

---

**📌 Última actualización:** 2025-01-07
**✍️ Creado por:** Claude Code (Asistente de aprendizaje de Forestech CLI)
**📖 Parte de:** FASE 4 - CRUD con JDBC

---

## 🙏 Retroalimentación

Si algo en este archivo no quedó claro, o si tienes sugerencias para mejorar la explicación pedagógica, anótalo aquí para discutir con tu instructor o mentor:

```
[Espacio para tus notas]










```

---

**🎉 ¡Felicitaciones por completar FASE 4.1!** Ahora puedes crear datos persistentes en SQL Server desde Java. Este es un hito fundamental en tu aprendizaje de CRUD.
