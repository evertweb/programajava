# FASE 03.4 - CONSULTAS SELECT Y RESULTSET

> **Objetivo de Aprendizaje:** Ejecutar consultas SELECT desde Java usando Statement, navegar ResultSet con cursor, extraer datos con métodos getXxx(), mapear filas de MySQL a objetos Java, y crear una clase de servicio ProductService con operaciones CRUD de lectura.

---

## 📚 Tabla de Contenidos

1. [Introducción: Del SQL Manual a Java Automático](#1-introducción-del-sql-manual-a-java-automático)
2. [Statement: Ejecutor de Queries SQL](#2-statement-ejecutor-de-queries-sql)
3. [ResultSet: El Cursor de Resultados](#3-resultset-el-cursor-de-resultados)
4. [Anatomía del Cursor: next() y Posicionamiento](#4-anatomía-del-cursor-next-y-posicionamiento)
5. [Métodos getXxx(): Extrayendo Datos por Tipo](#5-métodos-getxxx-extrayendo-datos-por-tipo)
6. [Acceso por Nombre vs Índice de Columna](#6-acceso-por-nombre-vs-índice-de-columna)
7. [Mapeo Manual: ResultSet → Objeto Product](#7-mapeo-manual-resultset-→-objeto-product)
8. [Patrón de Servicio: Separación de Responsabilidades](#8-patrón-de-servicio-separación-de-responsabilidades)
9. [Creando el Package services/](#9-creando-el-package-services)
10. [Implementando ProductService.java](#10-implementando-productservicejava)
11. [Método getAllProducts(): Lista Completa](#11-método-getallproducts-lista-completa)
12. [Método getProductById(): Búsqueda por Clave Primaria](#12-método-getproductbyid-búsqueda-por-clave-primaria)
13. [Método findByNamePattern(): Búsqueda con LIKE](#13-método-findbynamepattern-búsqueda-con-like)
14. [Método countProducts(): Conteo con COUNT()](#14-método-countproducts-conteo-con-count)
15. [Integración con Main.java: Mostrando Datos](#15-integración-con-mainjava-mostrando-datos)
16. [Ejercicios Prácticos](#16-ejercicios-prácticos)
17. [Git Checkpoint](#17-git-checkpoint)
18. [Generador de Quiz de Validación](#18-generador-de-quiz-de-validación)
19. [Checkpoint de Fase](#19-checkpoint-de-fase)

---

## 1. Introducción: Del SQL Manual a Java Automático

### Lo que Hicimos en Fases Anteriores

**Fase 03.1 y 03.2:** Ejecutamos queries SQL **manualmente** desde la terminal de MySQL:

```bash
# Terminal WSL
sudo mysql -u root -p
```

```sql
-- Consultas manuales
mysql> SELECT * FROM oil_products;
mysql> SELECT id, name, priceXUnd FROM oil_products WHERE priceXUnd > 5000;
mysql> SELECT COUNT(*) FROM oil_products;
```

**Salida:** Tabla formateada en la terminal de MySQL.

```
+------------------+-------------------------+--------------+---------------+
| id               | name                    | priceXUnd    | unidadDeMed.. |
+------------------+-------------------------+--------------+---------------+
| PROD-00000001    | Diesel Premium S50      | 5200.00      | LITRO         |
| PROD-00000002    | Diesel Corriente        | 4800.00      | LITRO         |
+------------------+-------------------------+--------------+---------------+
```

### La Pregunta de Esta Fase

**¿Cómo ejecutamos estas queries DESDE Java y procesamos los resultados?**

```
┌─────────────────────────────────────────────────────────────┐
│  Main.java                                                  │
│    ↓                                                         │
│  ProductService.getAllProducts()                            │
│    ↓                                                         │
│  Connection conn = DatabaseConnection.getConnection()       │
│    ↓                                                         │
│  Statement stmt = conn.createStatement()                    │
│    ↓                                                         │
│  ResultSet rs = stmt.executeQuery("SELECT * FROM ...")      │
│    ↓                                                         │
│  while (rs.next()) {                                        │
│      String id = rs.getString("id");                        │
│      String name = rs.getString("name");                    │
│      // Crear objeto Product...                             │
│  }                                                           │
│    ↓                                                         │
│  return List<Product>                                       │
└─────────────────────────────────────────────────────────────┘
```

**Resultado:** Una lista de objetos `Product` que podemos usar en Java:

```java
List<Product> products = productService.getAllProducts();
for (Product p : products) {
    System.out.println(p.getName() + " - $" + p.getPriceXUnd());
}
```

---

## 2. Statement: Ejecutor de Queries SQL

### ¿Qué es un Statement?

**Statement** es una **interfaz de JDBC** (`java.sql.Statement`) que representa una "declaración SQL" que se ejecuta contra la base de datos.

**Analogía Forestech:** Como un **mensajero** que lleva instrucciones escritas (queries SQL) al servidor MySQL y trae de vuelta las respuestas.

```
┌───────────────────────────────────────────────────────────┐
│  ANALOGÍA: MENSAJERO SQL                                  │
│                                                            │
│  Aplicación Java  ←──→  Statement (mensajero)  ←──→  MySQL│
│                                                            │
│  "Dame todos los     Lleva mensaje      Ejecuta SELECT   │
│   productos"         "SELECT * ..."     y retorna filas   │
│                                                            │
│  Recibe List<Prod> ←── Traduce filas    Filas en binario │
│                         a ResultSet                        │
└───────────────────────────────────────────────────────────┘
```

### Tipos de Statement en JDBC

| Tipo | Uso | Cuándo Usarlo |
|------|-----|---------------|
| **`Statement`** | Queries SQL **estáticas** (sin parámetros) | Queries simples, fijas (ej: `SELECT * FROM oil_products`) |
| **`PreparedStatement`** | Queries SQL **parametrizadas** (con `?`) | Queries con valores dinámicos (Fase 03.5) |
| **`CallableStatement`** | Llamadas a **stored procedures** | Procedures complejos (Fase avanzada) |

**En Fase 03.4:** Usaremos **Statement** para queries estáticas simples.

**En Fase 03.5:** Migraremos a **PreparedStatement** por seguridad (prevenir SQL Injection).

### Creando un Statement

```java
// SIEMPRE dentro de un try-with-resources
try (Connection conn = DatabaseConnection.getConnection();
     Statement stmt = conn.createStatement()) {

    // Usar stmt...

} // stmt.close() y conn.close() automáticos
```

**Método de Connection:**

```java
public interface Connection {
    Statement createStatement() throws SQLException;
    // Otros métodos...
}
```

**Importante:**
- Un `Statement` está **asociado** a una `Connection` específica
- Si cierras la `Connection`, el `Statement` queda inválido
- Debes cerrar el `Statement` después de usarlo (try-with-resources lo hace automáticamente)

### Métodos Principales de Statement

| Método | Retorno | Uso |
|--------|---------|-----|
| `executeQuery(String sql)` | `ResultSet` | Ejecuta **SELECT** (retorna filas) |
| `executeUpdate(String sql)` | `int` | Ejecuta **INSERT/UPDATE/DELETE** (retorna filas afectadas) - Fase 4 |
| `execute(String sql)` | `boolean` | Ejecuta **cualquier** SQL (menos común) |
| `close()` | `void` | Cierra el statement (try-with-resources lo hace) |

**En Fase 03.4:** Solo usaremos `executeQuery()` para SELECT.

### Ejemplo Básico

```java
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicQueryExample {
    public static void main(String[] args) {
        // Try-with-resources para gestión automática de recursos
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products")) {

            // Procesar resultados (próxima sección)
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

**Flujo:**
1. `conn.createStatement()` → Crea Statement
2. `stmt.executeQuery(sql)` → Envía SELECT a MySQL
3. MySQL procesa query y retorna filas
4. Driver convierte filas en `ResultSet`
5. Tu código itera sobre ResultSet
6. Try-with-resources cierra automáticamente rs → stmt → conn

---

## 3. ResultSet: El Cursor de Resultados

### ¿Qué es un ResultSet?

**ResultSet** es una **interfaz de JDBC** (`java.sql.ResultSet`) que representa el **conjunto de filas** retornado por una query SELECT.

**Analogía Forestech:** Como un **libro de registro** con un **marcador (cursor)** que señala la fila actual que estás leyendo.

```
┌─────────────────────────────────────────────────────────┐
│  RESULTSET = LIBRO DE REGISTRO CON CURSOR              │
│                                                          │
│  ┌──────────────────────────────────────────────┐      │
│  │ Cursor Position: ANTES DE LA PRIMERA FILA   │      │
│  │                  ↓                            │      │
│  ├──────────────────────────────────────────────┤      │
│  │ FILA 1: PROD-001 | Diesel Premium | 5200.00 │      │
│  ├──────────────────────────────────────────────┤      │
│  │ FILA 2: PROD-002 | Diesel Corrte  | 4800.00 │      │
│  ├──────────────────────────────────────────────┤      │
│  │ FILA 3: PROD-003 | Gasolina Extra | 4950.00 │      │
│  ├──────────────────────────────────────────────┤      │
│  │ FILA 4: PROD-004 | Kerosene       | 3800.00 │      │
│  └──────────────────────────────────────────────┘      │
│                                                          │
│  rs.next() → Mueve cursor a FILA 1                      │
│  rs.getString("name") → "Diesel Premium"                │
│  rs.next() → Mueve cursor a FILA 2                      │
│  rs.getString("name") → "Diesel Corrte"                 │
│  ...                                                     │
└─────────────────────────────────────────────────────────┘
```

### Características Clave de ResultSet

#### 1. Cursor Virtual

Un **cursor** es un puntero a la fila actual. Características:

- **Posición inicial:** ANTES de la primera fila (no apunta a ninguna fila válida)
- **Avance:** Se mueve con `rs.next()`
- **Unidireccional (default):** Solo avanza hacia adelante (no puedes retroceder)
- **Forward-only:** Después de pasar una fila, no puedes volver a ella

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products");

// Cursor está ANTES de fila 1
// rs.getString("name") ← ERROR: No hay fila actual

rs.next(); // Mueve a fila 1
// Ahora sí puedes acceder: rs.getString("name") → "Diesel Premium"

rs.next(); // Mueve a fila 2
rs.next(); // Mueve a fila 3
rs.next(); // Si no hay más filas, retorna false
```

#### 2. Lectura Secuencial

**No puedes acceder directamente** a la fila 5 sin pasar por las filas 1-4:

```java
// ❌ NO PUEDES HACER ESTO (con ResultSet default):
rs.goToRow(5); // No existe este método

// ✅ DEBES HACER ESTO:
while (rs.next()) {
    // Procesar cada fila secuencialmente
}
```

#### 3. Tipado por Columna

Cada columna tiene un **tipo de dato** específico. Usas métodos `getXxx()` según el tipo:

| Tipo MySQL | Método ResultSet | Tipo Java |
|------------|------------------|-----------|
| `VARCHAR` | `rs.getString()` | `String` |
| `INT` | `rs.getInt()` | `int` |
| `BIGINT` | `rs.getLong()` | `long` |
| `DOUBLE` | `rs.getDouble()` | `double` |
| `DECIMAL` | `rs.getBigDecimal()` | `BigDecimal` |
| `DATE` | `rs.getDate()` | `java.sql.Date` |
| `DATETIME` | `rs.getTimestamp()` | `java.sql.Timestamp` |
| `BOOLEAN` | `rs.getBoolean()` | `boolean` |

#### 4. Cierre Necesario

**ResultSet debe cerrarse** después de usarlo (consume memoria):

```java
// ✅ BIEN: Try-with-resources
try (Connection conn = ...;
     Statement stmt = ...;
     ResultSet rs = stmt.executeQuery("...")) {
    // Usar rs
} // rs.close() automático

// ❌ MAL: No cerrar
ResultSet rs = stmt.executeQuery("...");
// Usar rs...
// ¿Olvidaste cerrar? ← Memory leak
```

**Nota:** Cuando cierras un `Statement`, su `ResultSet` se cierra automáticamente. Pero es **buena práctica** declararlo explícitamente en try-with-resources.

---

## 4. Anatomía del Cursor: next() y Posicionamiento

### Método next(): El Avance del Cursor

```java
public boolean next() throws SQLException
```

**Comportamiento:**

1. Intenta mover el cursor a la **siguiente fila**
2. Si hay una fila disponible:
   - Mueve el cursor
   - Retorna `true`
3. Si NO hay más filas:
   - Cursor se posiciona **después de la última fila**
   - Retorna `false`

### Diagrama de Ejecución

```
Estado Inicial:
┌─────────────────────┐
│ Cursor → [ANTES]    │
├─────────────────────┤
│ FILA 1: Diesel      │
│ FILA 2: Gasolina    │
│ FILA 3: Kerosene    │
└─────────────────────┘

rs.next() → true (mueve a FILA 1)
┌─────────────────────┐
│ FILA 1: Diesel  ←──── Cursor
├─────────────────────┤
│ FILA 2: Gasolina    │
│ FILA 3: Kerosene    │
└─────────────────────┘

rs.next() → true (mueve a FILA 2)
┌─────────────────────┐
│ FILA 1: Diesel      │
├─────────────────────┤
│ FILA 2: Gasolina ←──── Cursor
├─────────────────────┤
│ FILA 3: Kerosene    │
└─────────────────────┘

rs.next() → true (mueve a FILA 3)
┌─────────────────────┐
│ FILA 1: Diesel      │
│ FILA 2: Gasolina    │
├─────────────────────┤
│ FILA 3: Kerosene ←──── Cursor
└─────────────────────┘

rs.next() → false (no hay FILA 4)
┌─────────────────────┐
│ FILA 1: Diesel      │
│ FILA 2: Gasolina    │
│ FILA 3: Kerosene    │
├─────────────────────┤
│ Cursor → [DESPUÉS]  │
└─────────────────────┘
```

### Patrón Típico: while (rs.next())

**El patrón más común** para iterar sobre todas las filas:

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products");

while (rs.next()) {
    // Este bloque se ejecuta UNA VEZ por cada fila
    String id = rs.getString("id");
    String name = rs.getString("name");
    double price = rs.getDouble("priceXUnd");

    System.out.println(id + " - " + name + " - $" + price);
}
```

**Flujo de Ejecución:**

```
1. while (rs.next())  → Intenta mover a fila 1 → true → Entra al bloque
   - Procesa fila 1: PROD-001, Diesel Premium, 5200.0

2. while (rs.next())  → Intenta mover a fila 2 → true → Entra al bloque
   - Procesa fila 2: PROD-002, Diesel Corrte, 4800.0

3. while (rs.next())  → Intenta mover a fila 3 → true → Entra al bloque
   - Procesa fila 3: PROD-003, Gasolina Extra, 4950.0

4. while (rs.next())  → Intenta mover a fila 4 → false → Sale del bucle
```

### ¿Qué Pasa con ResultSet Vacío?

```java
// Query que no retorna filas
ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products WHERE priceXUnd > 99999");

while (rs.next()) {
    // Este bloque NUNCA se ejecuta
    System.out.println("Nunca se imprime");
}

// Continúa aquí sin errores
System.out.println("Query ejecutada, 0 filas encontradas");
```

**Comportamiento:**
- `rs.next()` retorna `false` inmediatamente (no hay filas)
- El bucle `while` no se ejecuta ninguna vez
- **No hay excepción** (un ResultSet vacío es válido)

### Error Común: Leer Antes de next()

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products");

// ❌ ERROR: Cursor está ANTES de la primera fila
String name = rs.getString("name"); // SQLException: Before start of result set

// ✅ CORRECTO: Primero mover el cursor
if (rs.next()) {
    String name = rs.getString("name"); // Ahora sí funciona
}
```

**Mensaje de error:**

```
java.sql.SQLException: Before start of result set
```

**Causa:** Intentaste acceder a datos sin llamar a `rs.next()` primero.

---

## 5. Métodos getXxx(): Extrayendo Datos por Tipo

### Familia de Métodos getXxx()

**ResultSet** proporciona métodos **tipados** para extraer valores de columnas:

```java
// Sintaxis general
TipoJava valor = rs.getTipoJava(identificadorColumna);
```

**Identificador de columna:** Puede ser:
- **Nombre de columna** (String): `rs.getString("name")` ✅ Preferido
- **Índice de columna** (int): `rs.getString(2)` ⚠️ Menos legible

### Tabla Completa de Getters

| Método | Tipo Java | Tipos MySQL Compatibles | Ejemplo |
|--------|-----------|-------------------------|---------|
| `getString(col)` | `String` | `VARCHAR`, `CHAR`, `TEXT`, cualquier tipo convertible | `rs.getString("name")` |
| `getInt(col)` | `int` | `INT`, `SMALLINT`, `TINYINT` | `rs.getInt("quantity")` |
| `getLong(col)` | `long` | `BIGINT`, `INT` | `rs.getLong("bigId")` |
| `getDouble(col)` | `double` | `DOUBLE`, `FLOAT`, `DECIMAL` | `rs.getDouble("priceXUnd")` |
| `getFloat(col)` | `float` | `FLOAT`, `DOUBLE` | `rs.getFloat("ratio")` |
| `getBigDecimal(col)` | `BigDecimal` | `DECIMAL`, `NUMERIC` | `rs.getBigDecimal("precisePrice")` |
| `getBoolean(col)` | `boolean` | `BOOLEAN`, `TINYINT(1)`, `BIT` | `rs.getBoolean("isActive")` |
| `getDate(col)` | `java.sql.Date` | `DATE` | `rs.getDate("createdDate")` |
| `getTime(col)` | `java.sql.Time` | `TIME` | `rs.getTime("createdTime")` |
| `getTimestamp(col)` | `java.sql.Timestamp` | `DATETIME`, `TIMESTAMP` | `rs.getTimestamp("movementDate")` |
| `getBytes(col)` | `byte[]` | `BLOB`, `BINARY`, `VARBINARY` | `rs.getBytes("imageData")` |
| `getObject(col)` | `Object` | Cualquier tipo (genérico) | `rs.getObject("anyColumn")` |

### Detalles de Métodos Usados en Forestech

#### getString(String columnLabel)

**Uso:** Extraer columnas `VARCHAR`, `CHAR`, `TEXT`.

```java
String id = rs.getString("id");           // "PROD-00000001"
String name = rs.getString("name");       // "Diesel Premium S50"
String unit = rs.getString("unidadDeMedida"); // "LITRO"
```

**Conversión automática:** `getString()` puede convertir **cualquier tipo** a String:

```java
// MySQL: priceXUnd DOUBLE → getString lo convierte a String
String priceStr = rs.getString("priceXUnd"); // "5200.0"

// Pero es mejor usar getDouble():
double price = rs.getDouble("priceXUnd"); // 5200.0 (como número)
```

**Valores NULL:**

```java
String description = rs.getString("description");
if (description == null) {
    System.out.println("Sin descripción");
}
```

#### getDouble(String columnLabel)

**Uso:** Extraer columnas numéricas decimales (`DOUBLE`, `FLOAT`, `DECIMAL`).

```java
double price = rs.getDouble("priceXUnd"); // 5200.0
```

**Importante:** Si la columna es `NULL`, retorna `0.0` (no lanza excepción).

```java
// Columna priceXUnd es NULL en la fila actual
double price = rs.getDouble("priceXUnd"); // 0.0

// Para distinguir NULL de 0.0, usar wasNull():
double price = rs.getDouble("priceXUnd");
if (rs.wasNull()) {
    System.out.println("El precio es NULL (no es 0.0)");
}
```

#### getInt(String columnLabel)

**Uso:** Extraer columnas enteras (`INT`, `SMALLINT`, `TINYINT`).

```java
int quantity = rs.getInt("quantity"); // 1000
```

**Valores NULL:** Retorna `0` (usa `wasNull()` para distinguir).

#### getTimestamp(String columnLabel)

**Uso:** Extraer columnas `DATETIME` o `TIMESTAMP`.

```java
import java.sql.Timestamp;
import java.time.LocalDateTime;

Timestamp ts = rs.getTimestamp("movementDate");
// Convertir a java.time (Java 8+):
LocalDateTime dateTime = ts.toLocalDateTime();
```

**Tabla oil_products no tiene columnas DATETIME:** Pero lo usaremos en `combustibles_movements` (Fase 03.5).

#### getBoolean(String columnLabel)

**Uso:** Extraer columnas booleanas (`BOOLEAN`, `TINYINT(1)`).

```java
boolean isActive = rs.getBoolean("active");
```

**Conversión:**
- MySQL `TINYINT(1)`: 0 → `false`, 1 → `true`
- MySQL `BOOLEAN`: alias de `TINYINT(1)`
- Cualquier número ≠ 0 → `true`, 0 → `false`

### Método wasNull(): Detectar NULL

Cuando una columna es `NULL` en MySQL, los getters retornan **valores default**:

| Método | Valor si columna es NULL |
|--------|--------------------------|
| `getString()` | `null` (referencia Java null) |
| `getInt()` | `0` |
| `getDouble()` | `0.0` |
| `getBoolean()` | `false` |
| `getDate()` | `null` |

**Problema:** ¿Cómo distinguir un `0` real de un `NULL`?

**Solución:** `wasNull()`

```java
double price = rs.getDouble("priceXUnd");

if (rs.wasNull()) {
    System.out.println("El precio es NULL (sin definir)");
} else {
    System.out.println("El precio es: " + price);
}
```

**Orden importante:**

```java
// ✅ CORRECTO: wasNull() DESPUÉS del getter
double price = rs.getDouble("priceXUnd");
if (rs.wasNull()) { /* ... */ }

// ❌ INCORRECTO: wasNull() ANTES del getter (siempre false)
if (rs.wasNull()) { /* Nunca entra aquí */ }
double price = rs.getDouble("priceXUnd");
```

---

## 6. Acceso por Nombre vs Índice de Columna

### Dos Formas de Acceder a Columnas

#### Opción 1: Por Nombre de Columna (Recomendado ✅)

```java
String id = rs.getString("id");
String name = rs.getString("name");
double price = rs.getDouble("priceXUnd");
String unit = rs.getString("unidadDeMedida");
```

**Ventajas:**
- ✅ **Legible:** Sabes exactamente qué columna estás leyendo
- ✅ **Resistente a cambios:** Si cambias el `SELECT`, el código sigue funcionando
- ✅ **No importa el orden:** `SELECT name, id, price` vs `SELECT id, name, price`

**Desventajas:**
- ⚠️ Ligeramente más lento (busca columna por nombre internamente)

#### Opción 2: Por Índice de Columna

```java
String id = rs.getString(1);    // Primera columna
String name = rs.getString(2);  // Segunda columna
double price = rs.getDouble(3); // Tercera columna
String unit = rs.getString(4);  // Cuarta columna
```

**IMPORTANTE:** **Los índices empiezan en 1, NO en 0** (diferente de arrays Java).

```java
// ❌ ERROR: Los índices de ResultSet NO son 0-indexed
String id = rs.getString(0); // SQLException: Column index out of range

// ✅ CORRECTO: Empiezan en 1
String id = rs.getString(1); // Primera columna
```

**Ventajas:**
- ✅ Ligeramente más rápido (acceso directo sin búsqueda)

**Desventajas:**
- ❌ **Poco legible:** `rs.getString(2)` no dice qué columna es
- ❌ **Frágil:** Si cambias el orden en `SELECT`, el código se rompe

### Comparación con Ejemplo

**Query:**

```sql
SELECT id, name, priceXUnd, unidadDeMedida
FROM oil_products
WHERE id = 'PROD-00000001';
```

**Mapeo por Nombre:**

```java
while (rs.next()) {
    String id = rs.getString("id");               // Columna "id"
    String name = rs.getString("name");           // Columna "name"
    double price = rs.getDouble("priceXUnd");     // Columna "priceXUnd"
    String unit = rs.getString("unidadDeMedida"); // Columna "unidadDeMedida"
}
```

**Mapeo por Índice:**

```java
while (rs.next()) {
    String id = rs.getString(1);    // Columna 1: id
    String name = rs.getString(2);  // Columna 2: name
    double price = rs.getDouble(3); // Columna 3: priceXUnd
    String unit = rs.getString(4);  // Columna 4: unidadDeMedida
}
```

### Escenario: Cambias la Query

**Nueva query (orden diferente):**

```sql
SELECT name, priceXUnd, id, unidadDeMedida
FROM oil_products;
```

**Código por Nombre (✅ sigue funcionando):**

```java
String id = rs.getString("id");     // ✅ Encuentra "id" sin importar posición
String name = rs.getString("name"); // ✅ OK
```

**Código por Índice (❌ se rompe):**

```java
String id = rs.getString(1);    // ❌ Ahora índice 1 es "name", no "id"
String name = rs.getString(2);  // ❌ Ahora índice 2 es "priceXUnd"
// Resultados incorrectos!!!
```

### Recomendación para Forestech

**✅ Siempre usar acceso por NOMBRE:**

```java
// ✅ BIEN: Legible y robusto
String id = rs.getString("id");
String name = rs.getString("name");

// ❌ EVITAR: Solo en casos muy específicos de optimización extrema
String id = rs.getString(1);
String name = rs.getString(2);
```

---

## 7. Mapeo Manual: ResultSet → Objeto Product

### El Problema: Dos Mundos Diferentes

**Mundo MySQL:**

```sql
+------------------+-------------------------+------------+---------------+
| id               | name                    | priceXUnd  | unidadDeMed.. |
+------------------+-------------------------+------------+---------------+
| PROD-00000001    | Diesel Premium S50      | 5200.00    | LITRO         |
+------------------+-------------------------+------------+---------------+
```

**Mundo Java:**

```java
public class Product {
    private String id;
    private String name;
    private double priceXUnd;
    private String unidadDeMedida;

    // Constructor, getters, setters...
}
```

**Pregunta:** ¿Cómo convertir una **fila de MySQL** en un **objeto Product de Java**?

**Respuesta:** **Mapeo manual** (extraer columnas con getters y pasarlas al constructor).

### Pseudocódigo del Mapeo

```
FUNCIÓN mapearFilaAProduct(ResultSet rs):
    1. Extraer valor de columna "id" → variable idStr
    2. Extraer valor de columna "name" → variable nameStr
    3. Extraer valor de columna "priceXUnd" → variable priceDouble
    4. Extraer valor de columna "unidadDeMedida" → variable unitStr
    5. Crear nuevo objeto Product con esos 4 valores
    6. Retornar el objeto Product
FIN FUNCIÓN
```

### Código Java Completo del Mapeo

```java
// Asumiendo que rs está posicionado en una fila válida (después de rs.next())

// Paso 1-4: Extraer columnas
String id = rs.getString("id");
String name = rs.getString("name");
double priceXUnd = rs.getDouble("priceXUnd");
String unidadDeMedida = rs.getString("unidadDeMedida");

// Paso 5: Crear objeto Product usando el constructor completo
Product product = new Product(id, name, unidadDeMedida, priceXUnd);

// Paso 6: Ahora 'product' es un objeto Java con los datos de la fila MySQL
```

**Resultado:** Objeto `product` con:

```
product.getId()              → "PROD-00000001"
product.getName()            → "Diesel Premium S50"
product.getPriceXUnd()       → 5200.0
product.getUnidadDeMedida()  → "LITRO"
```

### Mapeo en un Loop: Construyendo una Lista

**Objetivo:** Convertir **todas las filas** de un ResultSet en una `List<Product>`.

```java
public List<Product> getAllProducts() throws SQLException {
    // 1. Crear lista vacía para acumular productos
    List<Product> products = new ArrayList<>();

    // 2. Query SQL
    String sql = "SELECT id, name, priceXUnd, unidadDeMedida FROM oil_products";

    // 3. Try-with-resources
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        // 4. Iterar sobre todas las filas
        while (rs.next()) {
            // 5. Mapear fila actual a objeto Product
            String id = rs.getString("id");
            String name = rs.getString("name");
            double priceXUnd = rs.getDouble("priceXUnd");
            String unidadDeMedida = rs.getString("unidadDeMedida");

            Product product = new Product(id, name, unidadDeMedida, priceXUnd);

            // 6. Agregar objeto a la lista
            products.add(product);
        }

    } // Cierre automático de recursos

    // 7. Retornar lista completa
    return products;
}
```

**Flujo de Ejecución:**

```
Iteración 1:
  rs.next() → true (fila 1)
  Mapeo → Product("PROD-00001", "Diesel Premium", "LITRO", 5200.0)
  products.add(producto) → Lista: [producto1]

Iteración 2:
  rs.next() → true (fila 2)
  Mapeo → Product("PROD-00002", "Diesel Corrte", "LITRO", 4800.0)
  products.add(producto) → Lista: [producto1, producto2]

Iteración 3:
  rs.next() → true (fila 3)
  Mapeo → Product("PROD-00003", "Gasolina Extra", "LITRO", 4950.0)
  products.add(producto) → Lista: [producto1, producto2, producto3]

...

Iteración N+1:
  rs.next() → false (no más filas)
  Sale del while

Retorna lista con N productos
```

### Mapeo de un Solo Objeto (Búsqueda por ID)

**Caso:** Query que retorna **máximo 1 fila** (ej: búsqueda por clave primaria).

```java
public Product getProductById(String id) throws SQLException {
    String sql = "SELECT id, name, priceXUnd, unidadDeMedida " +
                 "FROM oil_products WHERE id = '" + id + "'";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        // Si hay fila, mapear y retornar
        if (rs.next()) {
            String idDb = rs.getString("id");
            String name = rs.getString("name");
            double priceXUnd = rs.getDouble("priceXUnd");
            String unidadDeMedida = rs.getString("unidadDeMedida");

            return new Product(idDb, name, unidadDeMedida, priceXUnd);
        }

        // Si no hay fila, retornar null
        return null;
    }
}
```

**Diferencia con getAllProducts():**

| Aspecto | getAllProducts() | getProductById() |
|---------|------------------|------------------|
| Loop | `while (rs.next())` (todas las filas) | `if (rs.next())` (máximo 1 fila) |
| Retorno | `List<Product>` | `Product` (puede ser `null`) |
| Cantidad esperada | 0 a N productos | 0 o 1 producto |

**Importante:** `getProductById()` retorna `null` si no encuentra el producto. El código que llama debe verificar:

```java
Product product = service.getProductById("PROD-99999");

if (product == null) {
    System.out.println("Producto no encontrado");
} else {
    System.out.println("Encontrado: " + product.getName());
}
```

---

## 8. Patrón de Servicio: Separación de Responsabilidades

### Arquitectura en Capas

Hasta ahora, hemos mezclado todo en `Main.java`:
- Lógica de negocio
- Acceso a datos
- Presentación (System.out)

**Problema:** Código difícil de mantener, testear y reutilizar.

**Solución:** **Patrón de Capas** (Layered Architecture).

```
┌─────────────────────────────────────────────────────────────┐
│                 ARQUITECTURA EN 4 CAPAS                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  CAPA 1: PRESENTACIÓN (Main.java, ConsoleMenu)    │    │
│  │  - Interacción con usuario                         │    │
│  │  - Muestra datos (System.out.println)              │    │
│  │  - Captura input (Scanner)                         │    │
│  └──────────────────────┬─────────────────────────────┘    │
│                         ↓ llama a                           │
│  ┌────────────────────────────────────────────────────┐    │
│  │  CAPA 2: SERVICIO (ProductService, MovementSvc)   │    │
│  │  - Lógica de negocio                               │    │
│  │  - Validaciones                                    │    │
│  │  - Orquestación de operaciones                     │    │
│  └──────────────────────┬─────────────────────────────┘    │
│                         ↓ usa                               │
│  ┌────────────────────────────────────────────────────┐    │
│  │  CAPA 3: ACCESO A DATOS (DatabaseConnection)      │    │
│  │  - Conexión a BD                                   │    │
│  │  - Ejecución de queries SQL                        │    │
│  │  - Mapeo ResultSet → Objetos                       │    │
│  └──────────────────────┬─────────────────────────────┘    │
│                         ↓ accede                            │
│  ┌────────────────────────────────────────────────────┐    │
│  │  CAPA 4: BASE DE DATOS (MySQL Server)             │    │
│  │  - Almacenamiento físico                           │    │
│  │  - Procesamiento de queries                        │    │
│  │  - Integridad referencial                          │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Responsabilidades de Cada Capa

#### CAPA 1: Presentación (UI)

**Archivos:**
- `Main.java`
- `helpers/MenuHelper.java`
- `helpers/DataDisplay.java`

**Responsabilidad:**
- Mostrar menús
- Capturar input del usuario
- Formatear salida
- **NO contiene lógica de negocio ni SQL**

**Ejemplo:**

```java
// Main.java
public static void main(String[] args) {
    ProductService service = new ProductService();

    try {
        List<Product> products = service.getAllProducts();

        // Solo presentación
        System.out.println("=== PRODUCTOS DISPONIBLES ===");
        for (Product p : products) {
            System.out.printf("%-20s $%.2f%n", p.getName(), p.getPriceXUnd());
        }

    } catch (SQLException e) {
        System.err.println("Error al cargar productos: " + e.getMessage());
    }
}
```

#### CAPA 2: Servicio (Business Logic)

**Archivos:**
- `services/ProductService.java`
- `services/MovementService.java` (Fase 03.5)

**Responsabilidad:**
- Operaciones de negocio (CRUD)
- Validaciones
- Transformaciones de datos
- Contiene **SQL y mapeo a objetos**

**Ejemplo:**

```java
// ProductService.java
public class ProductService {
    public List<Product> getAllProducts() throws SQLException {
        // Contiene SQL y mapeo
        String sql = "SELECT * FROM oil_products";
        // ...
        return products;
    }

    public Product getProductById(String id) throws SQLException {
        // Validación de negocio
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID no puede ser vacío");
        }
        // SQL y mapeo
        // ...
    }
}
```

#### CAPA 3: Acceso a Datos (Data Access)

**Archivos:**
- `config/DatabaseConnection.java`

**Responsabilidad:**
- Gestionar conexiones a BD
- Proporcionar conexiones reutilizables

**En Fase 03.4:** ProductService.java hace acceso a datos directamente (sin capa separada).

**En Fase 5+:** Crearemos DAOs (Data Access Objects) para separar aún más.

#### CAPA 4: Base de Datos

**Archivos:**
- MySQL Server (externo a la aplicación)

**Responsabilidad:**
- Almacenar datos
- Procesar queries
- Garantizar integridad

### Ventajas del Patrón en Capas

| Ventaja | Descripción |
|---------|-------------|
| **Separación de Concerns** | Cada capa tiene una responsabilidad clara |
| **Mantenibilidad** | Cambios en UI no afectan lógica de negocio |
| **Testabilidad** | Puedes testear servicios sin UI |
| **Reutilización** | ProductService puede usarse desde Main, API REST, tests, etc. |
| **Escalabilidad** | Fácil agregar nuevas capas (ej: caché) |

### Ejemplo Comparativo

**❌ SIN Patrón de Servicio (todo en Main.java):**

```java
public static void main(String[] args) {
    // Mezcla presentación + lógica + acceso a datos
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products")) {

        System.out.println("=== PRODUCTOS ===");
        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            double price = rs.getDouble("priceXUnd");
            String unit = rs.getString("unidadDeMedida");

            Product p = new Product(id, name, unit, price);
            System.out.println(p.getName() + " - $" + p.getPriceXUnd());
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

**Problemas:**
- Si necesitas productos en otro lugar, duplicas código
- Difícil testear sin ejecutar Main
- SQL mezclado con presentación

**✅ CON Patrón de Servicio:**

```java
// ProductService.java
public class ProductService {
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM oil_products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("unidadDeMedida"),
                    rs.getDouble("priceXUnd")
                );
                products.add(p);
            }
        }
        return products;
    }
}

// Main.java (solo presentación)
public static void main(String[] args) {
    ProductService service = new ProductService();

    try {
        List<Product> products = service.getAllProducts();

        System.out.println("=== PRODUCTOS ===");
        for (Product p : products) {
            System.out.println(p.getName() + " - $" + p.getPriceXUnd());
        }

    } catch (SQLException e) {
        System.err.println("Error: " + e.getMessage());
    }
}
```

**Ventajas:**
- `getAllProducts()` se puede reutilizar desde cualquier lugar
- Fácil testear `ProductService` sin Main
- Main.java es más limpio y legible

---

## 9. Creando el Package services/

### Estructura Actual del Proyecto

```
src/main/java/com/forestech/
├── Main.java
├── AppConfig.java
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Product.java
├── managers/
│   └── MovementManagers.java
├── utils/
│   └── IdGenerator.java
└── helpers/
    ├── MenuHelper.java
    ├── DataDisplay.java
    ├── InputHelper.java
    └── BannerMenu.java
```

### Creando el Package services/

```bash
cd /home/hp/forestechOil/forestech-cli-java/src/main/java/com/forestech
mkdir services
```

**Estructura después:**

```
src/main/java/com/forestech/
├── config/
├── models/
├── managers/
├── services/         ← NUEVO
│   └── ProductService.java
├── utils/
└── helpers/
```

### Diferencia: managers/ vs services/

**Pregunta:** ¿Por qué crear `services/` si ya existe `managers/`?

| Aspecto | `managers/` (Fase 2.5) | `services/` (Fase 03.4) |
|---------|------------------------|-------------------------|
| **Datos** | En memoria (`ArrayList`) | En base de datos (MySQL) |
| **Persistencia** | ❌ Temporal (se pierde al cerrar) | ✅ Permanente |
| **Tecnología** | Java Collections | JDBC, SQL |
| **Propósito** | Aprendizaje POO, colecciones | Acceso a BD real |

**En Fase 03.4:**
- `managers/MovementManagers.java` → Quedará **obsoleto** (usaba ArrayList)
- `services/ProductService.java` → **Nueva forma** de trabajar (usa MySQL)

**En Fase 4+:**
- Migraremos toda la lógica de managers a services
- `managers/` dejará de usarse

---

## 10. Implementando ProductService.java

### Código Completo de ProductService.java

Crea el archivo: `/home/hp/forestechOil/forestech-cli-java/src/main/java/com/forestech/services/ProductService.java`

```java
package com.forestech.services;

// Imports de modelos
import com.forestech.models.Product;
import com.forestech.config.DatabaseConnection;

// Imports de JDBC
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Imports de Collections
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar operaciones CRUD de productos de combustible.
 *
 * Capa de servicio que encapsula la lógica de negocio y acceso a datos
 * de la tabla oil_products en MySQL.
 *
 * OPERACIONES SOPORTADAS (Fase 03.4):
 * - SELECT: getAllProducts(), getProductById(), findByNamePattern(), countProducts()
 *
 * OPERACIONES FUTURAS (Fase 04):
 * - INSERT: addProduct()
 * - UPDATE: updateProduct()
 * - DELETE: deleteProduct()
 *
 * @author Forestech Team
 * @version 1.0 - Fase 03.4
 */
public class ProductService {

    // ========== MÉTODOS DE LECTURA (SELECT) ==========

    /**
     * Obtiene todos los productos de combustible desde la base de datos.
     *
     * Ejecuta: SELECT id, name, priceXUnd, unidadDeMedida FROM oil_products
     * Mapea cada fila del ResultSet a un objeto Product.
     *
     * @return Lista de todos los productos. Si no hay productos, retorna lista vacía (nunca null).
     * @throws SQLException Si hay error de conexión o ejecución de query.
     */
    public List<Product> getAllProducts() throws SQLException {
        // Lista para acumular productos
        List<Product> products = new ArrayList<>();

        // Query SQL (seleccionar todas las columnas necesarias)
        String sql = "SELECT id, name, priceXUnd, unidadDeMedida FROM oil_products";

        // Try-with-resources: cierra automáticamente conn, stmt, rs
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Iterar sobre todas las filas del resultado
            while (rs.next()) {
                // Mapeo: ResultSet → Product
                Product product = mapResultSetToProduct(rs);
                products.add(product);
            }

        } // Cierre automático de recursos

        return products; // Retorna lista (vacía si no hay productos)
    }

    /**
     * Busca un producto por su ID (clave primaria).
     *
     * Ejecuta: SELECT ... WHERE id = 'PROD-XXXXXXXX'
     *
     * IMPORTANTE: En Fase 03.5 migraremos a PreparedStatement para prevenir SQL Injection.
     * Por ahora, usamos Statement con concatenación (solo para aprendizaje).
     *
     * @param id ID del producto a buscar (ej: "PROD-00000001")
     * @return Objeto Product si se encuentra, null si no existe.
     * @throws SQLException Si hay error de BD.
     * @throws IllegalArgumentException Si id es null o vacío.
     */
    public Product getProductById(String id) throws SQLException {
        // Validación de entrada
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto no puede ser null o vacío");
        }

        // Query con WHERE (concatenación - NO SEGURO, mejoraremos en Fase 03.5)
        String sql = "SELECT id, name, priceXUnd, unidadDeMedida " +
                     "FROM oil_products " +
                     "WHERE id = '" + id + "'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Si hay resultado, mapear y retornar
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }

            // Si no hay resultado, retornar null
            return null;

        } // Cierre automático
    }

    /**
     * Busca productos cuyo nombre coincida con un patrón (búsqueda parcial).
     *
     * Ejecuta: SELECT ... WHERE name LIKE '%patrón%'
     *
     * Ejemplo:
     *   findByNamePattern("Diesel") → Retorna "Diesel Premium", "Diesel Corriente", etc.
     *
     * @param pattern Patrón de búsqueda (ej: "Diesel", "Gasolina")
     * @return Lista de productos que coinciden. Lista vacía si no hay coincidencias.
     * @throws SQLException Si hay error de BD.
     */
    public List<Product> findByNamePattern(String pattern) throws SQLException {
        List<Product> products = new ArrayList<>();

        // Validación de entrada
        if (pattern == null || pattern.trim().isEmpty()) {
            return products; // Retornar lista vacía (no lanzar excepción)
        }

        // Query con LIKE (concatenación - NO SEGURO, mejoraremos en Fase 03.5)
        String sql = "SELECT id, name, priceXUnd, unidadDeMedida " +
                     "FROM oil_products " +
                     "WHERE name LIKE '%" + pattern + "%'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                products.add(product);
            }

        } // Cierre automático

        return products;
    }

    /**
     * Cuenta el total de productos en la base de datos.
     *
     * Ejecuta: SELECT COUNT(*) FROM oil_products
     *
     * @return Cantidad total de productos (0 si la tabla está vacía).
     * @throws SQLException Si hay error de BD.
     */
    public int countProducts() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM oil_products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // COUNT(*) siempre retorna 1 fila con el conteo
            if (rs.next()) {
                return rs.getInt("total");
            }

            // Esto nunca debería pasar, pero por seguridad:
            return 0;

        } // Cierre automático
    }

    // ========== MÉTODOS PRIVADOS HELPER ==========

    /**
     * Mapea una fila del ResultSet a un objeto Product.
     *
     * Asume que el ResultSet está posicionado en una fila válida (después de rs.next()).
     * Extrae las columnas: id, name, priceXUnd, unidadDeMedida.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return Objeto Product con los datos de la fila.
     * @throws SQLException Si hay error al acceder a columnas.
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        // Extraer valores de columnas
        String id = rs.getString("id");
        String name = rs.getString("name");
        double priceXUnd = rs.getDouble("priceXUnd");
        String unidadDeMedida = rs.getString("unidadDeMedida");

        // Crear y retornar objeto Product
        return new Product(id, name, unidadDeMedida, priceXUnd);
    }
}
```

### Desglose del Código

#### Método getAllProducts()

**Propósito:** Obtener todos los productos de la tabla `oil_products`.

**Flujo:**

1. Crear lista vacía: `List<Product> products = new ArrayList<>();`
2. Definir query: `SELECT id, name, priceXUnd, unidadDeMedida FROM oil_products`
3. Abrir conexión, crear statement, ejecutar query
4. Iterar sobre ResultSet:
   - Mapear cada fila a `Product`
   - Agregar a lista
5. Cerrar recursos (automático)
6. Retornar lista

**Retorno:**
- Lista con N productos (N = cantidad de filas en tabla)
- Lista vacía `[]` si no hay productos (nunca retorna `null`)

#### Método getProductById(String id)

**Propósito:** Buscar un producto específico por su clave primaria.

**Diferencias con getAllProducts():**

| Aspecto | getAllProducts() | getProductById() |
|---------|------------------|------------------|
| Query | `SELECT * ...` | `SELECT * ... WHERE id = '...'` |
| Resultados esperados | 0 a N productos | 0 o 1 producto |
| Loop | `while (rs.next())` | `if (rs.next())` |
| Retorno | `List<Product>` | `Product` (o `null`) |

**Validación de entrada:**

```java
if (id == null || id.trim().isEmpty()) {
    throw new IllegalArgumentException("ID no puede ser null o vacío");
}
```

**¿Por qué lanzar excepción?** Porque llamar a `getProductById(null)` es un **error de programación**, no un caso válido.

**Retorno `null`:**

```java
if (rs.next()) {
    return mapResultSetToProduct(rs); // Producto encontrado
}
return null; // Producto NO encontrado
```

**Uso:**

```java
Product product = service.getProductById("PROD-00000001");

if (product == null) {
    System.out.println("Producto no encontrado");
} else {
    System.out.println("Encontrado: " + product.getName());
}
```

#### Método findByNamePattern(String pattern)

**Propósito:** Búsqueda parcial por nombre (ej: "Diesel" encuentra "Diesel Premium", "Diesel Corriente").

**Query con LIKE:**

```sql
SELECT * FROM oil_products WHERE name LIKE '%Diesel%'
```

**Comportamiento del `%`:**
- `%Diesel` → Termina en "Diesel" (ej: "Petroleum Diesel")
- `Diesel%` → Empieza con "Diesel" (ej: "Diesel Premium")
- `%Diesel%` → Contiene "Diesel" en cualquier parte ✅ (más flexible)

**Retorno:**
- Lista con coincidencias (puede estar vacía)
- Nunca retorna `null`

**Diferencia con getProductById():**

```java
// getProductById() → null si no encuentra
Product p = service.getProductById("PROD-99999");
if (p == null) { /* No existe */ }

// findByNamePattern() → lista vacía si no encuentra
List<Product> products = service.findByNamePattern("Inexistente");
if (products.isEmpty()) { /* No hay coincidencias */ }
```

#### Método countProducts()

**Propósito:** Contar total de productos sin traer todos los datos.

**Query con COUNT():**

```sql
SELECT COUNT(*) AS total FROM oil_products
```

**Alias `AS total`:** Permite acceder al resultado con `rs.getInt("total")`.

**Retorno:**
- `int` con el conteo
- `0` si la tabla está vacía

**Ventaja sobre `getAllProducts().size()`:**

```java
// ❌ INEFICIENTE: Trae todos los productos solo para contar
List<Product> products = service.getAllProducts();
int count = products.size(); // Consumió memoria y red para nada

// ✅ EFICIENTE: Solo cuenta en MySQL
int count = service.countProducts(); // Rápido, bajo consumo de red
```

#### Método Privado mapResultSetToProduct()

**Propósito:** Evitar duplicación de código de mapeo.

**Antes (duplicado):**

```java
// En getAllProducts():
while (rs.next()) {
    String id = rs.getString("id");
    String name = rs.getString("name");
    // ... (5 líneas)
    products.add(new Product(...));
}

// En getProductById():
if (rs.next()) {
    String id = rs.getString("id");
    String name = rs.getString("name");
    // ... (5 líneas duplicadas)
    return new Product(...);
}
```

**Después (reutilizado):**

```java
// En getAllProducts():
while (rs.next()) {
    products.add(mapResultSetToProduct(rs)); // 1 línea
}

// En getProductById():
if (rs.next()) {
    return mapResultSetToProduct(rs); // 1 línea
}

// Método helper (usado por ambos):
private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
    return new Product(
        rs.getString("id"),
        rs.getString("name"),
        rs.getString("unidadDeMedida"),
        rs.getDouble("priceXUnd")
    );
}
```

**Ventajas:**
- ✅ Sin duplicación (DRY: Don't Repeat Yourself)
- ✅ Cambios centralizados (si agregas columna, cambias 1 lugar)
- ✅ Más legible

---

## 11. Método getAllProducts(): Lista Completa

*[Ya cubierto en sección 10, aquí agregamos ejemplos de uso]*

### Uso desde Main.java

```java
package com.forestech;

import com.forestech.services.ProductService;
import com.forestech.models.Product;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProductService service = new ProductService();

        try {
            // Obtener todos los productos
            List<Product> products = service.getAllProducts();

            // Verificar si hay productos
            if (products.isEmpty()) {
                System.out.println("No hay productos en la base de datos.");
                return;
            }

            // Mostrar productos
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║          CATÁLOGO DE PRODUCTOS DE COMBUSTIBLE             ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");

            for (Product p : products) {
                System.out.printf("║ %-18s │ %-25s │ $%8.2f/%-6s ║%n",
                    p.getId(),
                    p.getName(),
                    p.getPriceXUnd(),
                    p.getUnidadDeMedida());
            }

            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println("Total de productos: " + products.size());

        } catch (SQLException e) {
            System.err.println("❌ ERROR al obtener productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**Salida esperada:**

```
╔════════════════════════════════════════════════════════════╗
║          CATÁLOGO DE PRODUCTOS DE COMBUSTIBLE             ║
╠════════════════════════════════════════════════════════════╣
║ PROD-00000001      │ Diesel Premium S50            │ $ 5200.00/LITRO  ║
║ PROD-00000002      │ Diesel Corriente              │ $ 4800.00/LITRO  ║
║ PROD-00000003      │ ACPM                          │ $ 4700.00/LITRO  ║
║ PROD-00000004      │ Gasolina Extra 95 Octanos     │ $ 4950.00/LITRO  ║
║ PROD-00000005      │ Gasolina Corriente 87 Oct..   │ $ 4500.00/LITRO  ║
║ PROD-00000006      │ Jet Fuel A-1                  │ $ 6500.00/LITRO  ║
║ PROD-00000007      │ Kerosene                      │ $ 3800.00/LITRO  ║
╚════════════════════════════════════════════════════════════╝
Total de productos: 7
```

---

## 12. Método getProductById(): Búsqueda por Clave Primaria

*[Ya cubierto en sección 10, aquí agregamos ejemplos avanzados]*

### Uso Avanzado: Validación y Manejo de Errores

```java
public static void buscarProductoPorId(String id) {
    ProductService service = new ProductService();

    try {
        // Buscar producto
        Product product = service.getProductById(id);

        // Verificar si existe
        if (product == null) {
            System.out.println("⚠️ No se encontró producto con ID: " + id);
            return;
        }

        // Mostrar detalles
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║       DETALLES DEL PRODUCTO                ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.printf("║ ID               : %-20s ║%n", product.getId());
        System.out.printf("║ Nombre           : %-20s ║%n", product.getName());
        System.out.printf("║ Precio           : $%-19.2f ║%n", product.getPriceXUnd());
        System.out.printf("║ Unidad de Medida : %-20s ║%n", product.getUnidadDeMedida());
        System.out.println("╚════════════════════════════════════════════╝\n");

    } catch (IllegalArgumentException e) {
        System.err.println("❌ Error de validación: " + e.getMessage());

    } catch (SQLException e) {
        System.err.println("❌ Error de base de datos: " + e.getMessage());
        e.printStackTrace();
    }
}

// Uso:
public static void main(String[] args) {
    buscarProductoPorId("PROD-00000001"); // ✅ Existe
    buscarProductoPorId("PROD-99999");    // ⚠️ No existe
    buscarProductoPorId(null);            // ❌ IllegalArgumentException
}
```

---

## 13. Método findByNamePattern(): Búsqueda con LIKE

*[Ya cubierto en sección 10, aquí agregamos uso práctico]*

### Uso: Búsqueda Interactiva

```java
import java.util.Scanner;

public static void buscarProductosInteractivo() {
    ProductService service = new ProductService();
    Scanner scanner = new Scanner(System.in);

    System.out.print("Ingrese término de búsqueda (nombre de producto): ");
    String pattern = scanner.nextLine();

    try {
        List<Product> products = service.findByNamePattern(pattern);

        if (products.isEmpty()) {
            System.out.println("❌ No se encontraron productos con: \"" + pattern + "\"");
            return;
        }

        System.out.println("\n✅ Se encontraron " + products.size() + " producto(s):\n");

        for (Product p : products) {
            System.out.printf("  • %-30s → $%.2f/%s%n",
                p.getName(),
                p.getPriceXUnd(),
                p.getUnidadDeMedida());
        }

    } catch (SQLException e) {
        System.err.println("❌ Error: " + e.getMessage());
    }
}
```

**Ejemplo de ejecución:**

```
Ingrese término de búsqueda (nombre de producto): Diesel

✅ Se encontraron 3 producto(s):

  • Diesel Premium S50              → $5200.00/LITRO
  • Diesel Corriente                → $4800.00/LITRO
  • ACPM                            → $4700.00/LITRO
```

---

## 14. Método countProducts(): Conteo con COUNT()

*[Ya cubierto en sección 10, aquí agregamos uso práctico]*

### Uso: Dashboard de Estadísticas

```java
public static void mostrarEstadisticasProductos() {
    ProductService service = new ProductService();

    try {
        int totalProductos = service.countProducts();
        List<Product> productos = service.getAllProducts();

        // Calcular precio promedio
        double sumaPrecios = 0.0;
        double precioMin = Double.MAX_VALUE;
        double precioMax = Double.MIN_VALUE;

        for (Product p : productos) {
            sumaPrecios += p.getPriceXUnd();
            if (p.getPriceXUnd() < precioMin) precioMin = p.getPriceXUnd();
            if (p.getPriceXUnd() > precioMax) precioMax = p.getPriceXUnd();
        }

        double precioPromedio = (totalProductos > 0) ? (sumaPrecios / totalProductos) : 0.0;

        // Mostrar dashboard
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║       ESTADÍSTICAS DE PRODUCTOS               ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.printf("║ Total de productos   : %-20d ║%n", totalProductos);
        System.out.printf("║ Precio promedio      : $%-19.2f ║%n", precioPromedio);
        System.out.printf("║ Precio más bajo      : $%-19.2f ║%n", precioMin);
        System.out.printf("║ Precio más alto      : $%-19.2f ║%n", precioMax);
        System.out.println("╚═══════════════════════════════════════════════╝\n");

    } catch (SQLException e) {
        System.err.println("❌ Error: " + e.getMessage());
    }
}
```

---

## 15. Integración con Main.java: Mostrando Datos

### Main.java Completo con Menú

```java
package com.forestech;

import com.forestech.services.ProductService;
import com.forestech.models.Product;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static ProductService productService = new ProductService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir newline

            switch (opcion) {
                case 1 -> listarTodosLosProductos();
                case 2 -> buscarProductoPorId();
                case 3 -> buscarProductosPorNombre();
                case 4 -> mostrarEstadisticas();
                case 5 -> {
                    System.out.println("¡Hasta luego!");
                    salir = true;
                }
                default -> System.out.println("❌ Opción inválida");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║     FORESTECH CLI - PRODUCTOS         ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ 1. Listar todos los productos         ║");
        System.out.println("║ 2. Buscar producto por ID             ║");
        System.out.println("║ 3. Buscar productos por nombre        ║");
        System.out.println("║ 4. Estadísticas de productos          ║");
        System.out.println("║ 5. Salir                              ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("Seleccione opción: ");
    }

    private static void listarTodosLosProductos() {
        try {
            List<Product> products = productService.getAllProducts();

            if (products.isEmpty()) {
                System.out.println("⚠️ No hay productos registrados.");
                return;
            }

            System.out.println("\n═══ LISTADO DE PRODUCTOS ═══\n");
            for (Product p : products) {
                System.out.printf("%-20s | %-30s | $%8.2f/%s%n",
                    p.getId(),
                    p.getName(),
                    p.getPriceXUnd(),
                    p.getUnidadDeMedida());
            }
            System.out.println("\nTotal: " + products.size() + " productos");

        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void buscarProductoPorId() {
        System.out.print("Ingrese ID del producto: ");
        String id = scanner.nextLine();

        try {
            Product product = productService.getProductById(id);

            if (product == null) {
                System.out.println("❌ Producto no encontrado: " + id);
            } else {
                System.out.println("\n✅ Producto encontrado:");
                System.out.println("  ID:     " + product.getId());
                System.out.println("  Nombre: " + product.getName());
                System.out.println("  Precio: $" + product.getPriceXUnd());
                System.out.println("  Unidad: " + product.getUnidadDeMedida());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void buscarProductosPorNombre() {
        System.out.print("Ingrese término de búsqueda: ");
        String pattern = scanner.nextLine();

        try {
            List<Product> products = productService.findByNamePattern(pattern);

            if (products.isEmpty()) {
                System.out.println("❌ Sin resultados para: \"" + pattern + "\"");
            } else {
                System.out.println("\n✅ Encontrados " + products.size() + " producto(s):\n");
                for (Product p : products) {
                    System.out.printf("  • %-30s → $%.2f%n", p.getName(), p.getPriceXUnd());
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void mostrarEstadisticas() {
        try {
            int total = productService.countProducts();
            System.out.println("\n📊 Total de productos en catálogo: " + total);

        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
```

---

## 16. Ejercicios Prácticos

### Ejercicio 1: Implementar getProductsByPriceRange() (Intermedio)

**Objetivo:** Agregar un método que filtre productos por rango de precio.

**Firma del método:**

```java
/**
 * Obtiene productos cuyo precio esté en el rango [minPrice, maxPrice].
 *
 * @param minPrice Precio mínimo (inclusivo)
 * @param maxPrice Precio máximo (inclusivo)
 * @return Lista de productos en el rango
 * @throws SQLException Si hay error de BD
 * @throws IllegalArgumentException Si minPrice > maxPrice
 */
public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) throws SQLException {
    // Tu implementación aquí
}
```

**Tareas:**
1. Agregar el método a `ProductService.java`
2. Validar que `minPrice <= maxPrice`
3. Usar query SQL: `WHERE priceXUnd BETWEEN ? AND ?` (por ahora sin PreparedStatement, usa concatenación)
4. Probar desde `Main.java` con rango `4000.0` a `5000.0`

<details>
<summary>✅ Solución</summary>

```java
public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) throws SQLException {
    // Validación
    if (minPrice > maxPrice) {
        throw new IllegalArgumentException("Precio mínimo no puede ser mayor que precio máximo");
    }

    List<Product> products = new ArrayList<>();

    // Query con BETWEEN
    String sql = "SELECT id, name, priceXUnd, unidadDeMedida " +
                 "FROM oil_products " +
                 "WHERE priceXUnd BETWEEN " + minPrice + " AND " + maxPrice;

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            products.add(mapResultSetToProduct(rs));
        }
    }

    return products;
}
```

**Uso en Main.java:**

```java
try {
    List<Product> products = service.getProductsByPriceRange(4000.0, 5000.0);
    System.out.println("Productos entre $4000 y $5000:");
    for (Product p : products) {
        System.out.println("  " + p.getName() + " - $" + p.getPriceXUnd());
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

**Salida esperada:**

```
Productos entre $4000 y $5000:
  Diesel Corriente - $4800.0
  ACPM - $4700.0
  Gasolina Extra 95 Octanos - $4950.0
  Gasolina Corriente 87 Octanos - $4500.0
```
</details>

---

### Ejercicio 2: Implementar getMostExpensiveProduct() (Avanzado)

**Objetivo:** Obtener el producto con el precio más alto usando ORDER BY y LIMIT.

**Firma del método:**

```java
/**
 * Obtiene el producto más caro del catálogo.
 *
 * @return Producto más caro, o null si no hay productos
 * @throws SQLException Si hay error de BD
 */
public Product getMostExpensiveProduct() throws SQLException {
    // Tu implementación aquí
}
```

**Pistas:**
- Query: `SELECT * FROM oil_products ORDER BY priceXUnd DESC LIMIT 1`
- `ORDER BY priceXUnd DESC` → Ordena de mayor a menor
- `LIMIT 1` → Solo trae la primera fila

<details>
<summary>✅ Solución</summary>

```java
public Product getMostExpensiveProduct() throws SQLException {
    String sql = "SELECT id, name, priceXUnd, unidadDeMedida " +
                 "FROM oil_products " +
                 "ORDER BY priceXUnd DESC " +
                 "LIMIT 1";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            return mapResultSetToProduct(rs);
        }

        return null; // No hay productos
    }
}
```

**Uso:**

```java
Product expensive = service.getMostExpensiveProduct();
if (expensive != null) {
    System.out.println("Producto más caro: " + expensive.getName() + " - $" + expensive.getPriceXUnd());
}
```

**Salida esperada:**

```
Producto más caro: Jet Fuel A-1 - $6500.0
```
</details>

---

### Ejercicio 3: Probar Manejo de ResultSet Vacío (Básico)

**Objetivo:** Verificar comportamiento cuando una query no retorna filas.

**Tareas:**
1. Llama a `getProductById("PROD-99999")` (ID inexistente)
2. Imprime el resultado
3. Verifica que NO se lance excepción (retorna `null`)
4. Llama a `findByNamePattern("NoExiste")`
5. Verifica que retorna lista vacía (no `null`)

<details>
<summary>✅ Solución</summary>

```java
public static void main(String[] args) {
    ProductService service = new ProductService();

    // Test 1: getProductById con ID inexistente
    try {
        Product p = service.getProductById("PROD-99999");
        if (p == null) {
            System.out.println("✅ Test 1 OK: getProductById retorna null para ID inexistente");
        }
    } catch (SQLException e) {
        System.out.println("❌ Test 1 FAIL: Se lanzó excepción");
    }

    // Test 2: findByNamePattern sin coincidencias
    try {
        List<Product> products = service.findByNamePattern("NoExiste");
        if (products.isEmpty()) {
            System.out.println("✅ Test 2 OK: findByNamePattern retorna lista vacía");
        }
        if (products != null) {
            System.out.println("✅ Test 2 OK: findByNamePattern NO retorna null");
        }
    } catch (SQLException e) {
        System.out.println("❌ Test 2 FAIL: Se lanzó excepción");
    }
}
```

**Salida esperada:**

```
✅ Test 1 OK: getProductById retorna null para ID inexistente
✅ Test 2 OK: findByNamePattern retorna lista vacía
✅ Test 2 OK: findByNamePattern NO retorna null
```
</details>

---

### Ejercicio 4: Debugging - Identificar Error en Código (Avanzado)

**Objetivo:** Encontrar y corregir errores en código con ResultSet.

**Código con errores:**

```java
public Product getFirstProduct() throws SQLException {
    String sql = "SELECT * FROM oil_products LIMIT 1";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        // ERROR AQUÍ ↓
        String name = rs.getString("name");
        return new Product("", name, "", 0.0);
    }
}
```

**Tareas:**
1. Identifica el error
2. Explica por qué falla
3. Corrige el código

<details>
<summary>✅ Solución</summary>

**Error:** Intentar leer del ResultSet sin llamar a `rs.next()` primero.

**Línea problemática:**

```java
String name = rs.getString("name"); // SQLException: Before start of result set
```

**Explicación:** El cursor está ANTES de la primera fila. Debes moverlo con `rs.next()`.

**Código corregido:**

```java
public Product getFirstProduct() throws SQLException {
    String sql = "SELECT * FROM oil_products LIMIT 1";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        // ✅ CORRECCIÓN: Llamar a rs.next() primero
        if (rs.next()) {
            return mapResultSetToProduct(rs);
        }

        return null; // No hay productos
    }
}
```
</details>

---

### Ejercicio 5: Implementar Método Completo desde Cero (Desafío)

**Objetivo:** Escribir un método completo que obtenga productos ordenados por nombre alfabéticamente.

**Especificación:**

```java
/**
 * Obtiene todos los productos ordenados alfabéticamente por nombre (A-Z).
 *
 * @return Lista de productos ordenados por nombre
 * @throws SQLException Si hay error de BD
 */
public List<Product> getAllProductsSortedByName() throws SQLException {
    // Implementar desde cero
}
```

**Requisitos:**
- Query debe incluir `ORDER BY name ASC`
- Retornar lista vacía si no hay productos (no `null`)
- Usar try-with-resources
- Reutilizar `mapResultSetToProduct()`

<details>
<summary>✅ Solución Completa</summary>

```java
public List<Product> getAllProductsSortedByName() throws SQLException {
    List<Product> products = new ArrayList<>();

    String sql = "SELECT id, name, priceXUnd, unidadDeMedida " +
                 "FROM oil_products " +
                 "ORDER BY name ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            products.add(mapResultSetToProduct(rs));
        }
    }

    return products;
}
```

**Uso:**

```java
List<Product> products = service.getAllProductsSortedByName();
System.out.println("Productos ordenados alfabéticamente:");
for (Product p : products) {
    System.out.println("  " + p.getName());
}
```

**Salida esperada:**

```
Productos ordenados alfabéticamente:
  ACPM
  Diesel Corriente
  Diesel Premium S50
  Gasolina Corriente 87 Octanos
  Gasolina Extra 95 Octanos
  Jet Fuel A-1
  Kerosene
```
</details>

---

## 17. Git Checkpoint

### Confirmar Cambios de Fase 03.4

**Archivos modificados/creados:**

```bash
cd /home/hp/forestechOil/forestech-cli-java
git status
```

**Esperado:**

```
modified:   src/main/java/com/forestech/Main.java
new file:   src/main/java/com/forestech/services/ProductService.java
```

### Comandos Git

```bash
# Agregar archivos
git add src/main/java/com/forestech/Main.java
git add src/main/java/com/forestech/services/ProductService.java

# Commit descriptivo
git commit -m "Fase 03.4: Implementar consultas SELECT y ResultSet

- Crear package com.forestech.services
- Implementar ProductService.java con métodos:
  * getAllProducts() - SELECT * con mapeo a List<Product>
  * getProductById(String) - SELECT con WHERE por PK
  * findByNamePattern(String) - SELECT con LIKE
  * countProducts() - SELECT COUNT(*)
  * mapResultSetToProduct() - Mapeo reutilizable
- Modificar Main.java con menú interactivo
- Documentar uso de Statement y ResultSet
- Ejercicios prácticos completados

Checkpoint: Fase 03.4 completada ✅"

# Ver log
git log --oneline -5
```

---

## 18. Generador de Quiz de Validación

### Prompt para Claude/ChatGPT

Copia este prompt para generar tu quiz personalizado:

```
Eres un profesor de Java y SQL especializado en JDBC. Genera un quiz de validación para un estudiante que completó la FASE 03.4 del proyecto Forestech (Consultas SELECT y ResultSet).

CONTEXTO DEL ESTUDIANTE:
- Implementó ProductService.java con 4 métodos de consulta
- Aprendió a usar Statement y executeQuery()
- Domina ResultSet: next(), getString(), getDouble(), etc.
- Sabe mapear ResultSet a objetos Product
- Entiende diferencia entre retornar null vs lista vacía

TEMAS A EVALUAR:
1. Método executeQuery() vs executeUpdate()
2. Cursor de ResultSet y posicionamiento con next()
3. Métodos getXxx() y correspondencia con tipos MySQL
4. Acceso por nombre vs índice de columna
5. Mapeo manual ResultSet → Object
6. Manejo de ResultSet vacío (null vs lista vacía)
7. Try-with-resources con Statement y ResultSet
8. Queries SELECT con WHERE, LIKE, ORDER BY, LIMIT, COUNT()

FORMATO DEL QUIZ:
- 10 preguntas en total
- Tipos:
  * 3 opción múltiple (conceptos teóricos)
  * 3 completar código (implementaciones con blancos)
  * 2 debugging (identificar y corregir errores)
  * 2 implementación completa (escribir método desde cero)
- Dificultad progresiva: básico → intermedio → avanzado
- Explicaciones detalladas con ejemplos de Forestech

EJEMPLO DE PREGUNTA:

**Pregunta 3 (Intermedio - Completar Código)**

Completa el método que busca productos por un rango de precio:

\`\`\`java
public List<Product> getProductsByPriceRange(double min, double max) throws SQLException {
    List<Product> products = new ArrayList<>();

    String sql = "SELECT * FROM oil_products WHERE priceXUnd _________ " + min + " _________ " + max;

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = _________.executeQuery(sql)) {

        _________ (rs._________) {
            products.add(mapResultSetToProduct(rs));
        }
    }

    return products;
}
\`\`\`

BLANCOS A COMPLETAR:
1. Operador SQL para rango: _________
2. Palabra SQL para rango: _________
3. Objeto que ejecuta query: _________
4. Tipo de loop: _________
5. Método para avanzar cursor: _________

<details>
<summary>✅ Respuesta Correcta</summary>

\`\`\`java
String sql = "SELECT * FROM oil_products WHERE priceXUnd BETWEEN " + min + " AND " + max;

try (Connection conn = DatabaseConnection.getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery(sql)) {

    while (rs.next()) {
        products.add(mapResultSetToProduct(rs));
    }
}
\`\`\`

**Explicación:**
1. `BETWEEN` es el operador SQL para rangos inclusivos
2. `AND` conecta los valores min y max del rango
3. `stmt` es el Statement creado por Connection
4. `while` itera sobre todas las filas (vs `if` para máximo 1 fila)
5. `next()` mueve el cursor a la siguiente fila y retorna boolean

**Alternativa sin BETWEEN:**
\`\`\`sql
WHERE priceXUnd >= " + min + " AND priceXUnd <= " + max
\`\`\`
</details>

---

GENERA EL QUIZ COMPLETO (10 PREGUNTAS) SIGUIENDO ESTE FORMATO.
```

---

## 19. Checkpoint de Fase

### Checklist de Validación Conceptual

Marca cada ítem que puedas explicar sin mirar documentación:

**Statement y Ejecución de Queries:**
- [ ] Entiendo qué es un Statement y para qué sirve
- [ ] Sé la diferencia entre executeQuery() y executeUpdate()
- [ ] Puedo crear un Statement desde una Connection
- [ ] Entiendo por qué Statement debe cerrarse (try-with-resources)

**ResultSet y Navegación:**
- [ ] Entiendo qué es un ResultSet y cómo funciona el cursor
- [ ] Sé que el cursor empieza ANTES de la primera fila
- [ ] Entiendo cómo funciona rs.next() (retorno boolean, movimiento)
- [ ] Puedo escribir un while (rs.next()) de memoria
- [ ] Entiendo la diferencia entre while y if con rs.next()

**Extracción de Datos:**
- [ ] Conozco los métodos getXxx() principales (getString, getInt, getDouble)
- [ ] Sé qué tipo Java corresponde a cada tipo MySQL
- [ ] Entiendo la diferencia entre acceso por nombre vs índice
- [ ] Sé que los índices de columna empiezan en 1 (no 0)
- [ ] Entiendo cómo usar wasNull() para detectar NULL

**Mapeo y Patrones:**
- [ ] Puedo mapear una fila de ResultSet a un objeto manualmente
- [ ] Entiendo el patrón de construcción de List<Product> desde ResultSet
- [ ] Sé cuándo retornar null vs lista vacía
- [ ] Entiendo el beneficio de métodos helper como mapResultSetToProduct()

**Patrón de Servicio:**
- [ ] Entiendo qué es una capa de servicio y por qué es útil
- [ ] Sé separar presentación (Main) de lógica de negocio (Service)
- [ ] Puedo implementar métodos CRUD de lectura en un Service

### Checklist de Validación Práctica

Marca cada ítem completado:

**Implementación:**
- [ ] Creé el package com.forestech.services
- [ ] Implementé ProductService.java completo
- [ ] Implementé getAllProducts()
- [ ] Implementé getProductById()
- [ ] Implementé findByNamePattern()
- [ ] Implementé countProducts()
- [ ] Implementé mapResultSetToProduct() reutilizable
- [ ] Modifiqué Main.java con menú interactivo

**Ejercicios:**
- [ ] Ejercicio 1: getProductsByPriceRange()
- [ ] Ejercicio 2: getMostExpensiveProduct()
- [ ] Ejercicio 3: Probé manejo de ResultSet vacío
- [ ] Ejercicio 4: Debugging - encontré error con rs.next()
- [ ] Ejercicio 5: getAllProductsSortedByName()

**Git y Validación:**
- [ ] Commit de cambios con mensaje descriptivo
- [ ] Generé quiz con Claude/ChatGPT
- [ ] Respondí las 10 preguntas
- [ ] Obtuve al menos 7/10 correctas

### Criterio de Aprobación

**Fase 03.4 está COMPLETA si:**
1. ✅ Al menos **13 de 15 ítems** del checklist conceptual marcados
2. ✅ Al menos **12 de 15 ítems** del checklist práctico marcados
3. ✅ Quiz aprobado con 7/10 o más

### Reflexión Guiada (Obligatoria)

1. **¿Cuál fue el concepto más confuso de ResultSet? ¿Cómo lo superaste?**

   _[Tu respuesta. Ej: "El cursor invisible me confundía. Dibujar un diagrama del ResultSet con flechas ayudó."]_

2. **Explica en tus palabras: ¿Por qué rs.next() retorna boolean?**

   _[Tu respuesta. Intenta explicarlo sin mirar documentación.]_

3. **¿Cuándo usarías getProductById() que retorna null vs getAllProducts() que retorna lista vacía?**

   _[Tu respuesta. Piensa en casos de uso reales.]_

4. **Compara el código sin ProductService (todo en Main) vs con ProductService. ¿Qué ventajas ves?**

   _[Tu respuesta.]_

5. **¿Qué método agregarías a ProductService que no esté implementado? ¿Por qué sería útil?**

   _[Tu respuesta. Ej: "getProductsByCategoryAndPrice() para filtros combinados."]_

### Próxima Fase: FASE_03.5_PREPAREDSTATEMENT_SEGURIDAD.md

**¿Qué aprenderás?**
- **SQL Injection:** Qué es, cómo ocurre, por qué es peligroso
- **PreparedStatement:** Queries parametrizadas seguras
- **Refactorización:** Migrar ProductService de Statement a PreparedStatement
- **Foreign Keys:** Crear tabla `combustibles_movements` con FK
- **MovementService:** Implementar servicio de movimientos
- **Integración completa:** Main.java mostrando productos y movimientos desde BD

**Prerequisitos:**
- ✅ Fase 03.4 completada al 100%
- ✅ ProductService funcionando sin errores
- ✅ Comprensión sólida de ResultSet y mapeo

---

## 📚 Recursos Adicionales

**Documentación Oficial:**
- [ResultSet (JavaDoc)](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)
- [Statement (JavaDoc)](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html)
- [JDBC Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html)

**Lecturas Recomendadas:**
- Patrón DAO (Data Access Object) - Fase 5+
- ORM (Hibernate, JPA) - Alternativas a JDBC manual - Fase 8+

---

## ❓ Preguntas Frecuentes

**P: ¿Por qué no usar `SELECT *` en vez de listar columnas?**

R: `SELECT *` funciona, pero listar columnas explícitamente es mejor:
- ✅ **Más claro:** Sabes exactamente qué columnas esperas
- ✅ **Más eficiente:** No traes columnas que no usas
- ✅ **Más robusto:** Si agregas columnas a la tabla, tu código no se rompe

---

**P: ¿Puedo retroceder el cursor de ResultSet?**

R: Por defecto NO (ResultSet es "forward-only"). Pero puedes crear un ResultSet con scroll:

```java
Statement stmt = conn.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY
);

ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products");

rs.next();  // Fila 1
rs.next();  // Fila 2
rs.previous(); // ✅ Vuelve a fila 1
rs.first(); // ✅ Salta a primera fila
rs.last();  // ✅ Salta a última fila
```

**Nota:** En Forestech no lo necesitamos (procesamos filas secuencialmente).

---

**P: ¿Qué pasa si modifico la BD mientras itero sobre ResultSet?**

R: Depende del tipo de ResultSet:
- **TYPE_SCROLL_INSENSITIVE:** No ve cambios (snapshot de cuando ejecutaste query)
- **TYPE_SCROLL_SENSITIVE:** Ve cambios en tiempo real (no todos los drivers lo soportan)

Por defecto usamos TYPE_FORWARD_ONLY + CONCUR_READ_ONLY (inmutable, unidireccional).

---

**P: ¿Por qué ProductService lanza SQLException en vez de capturarla?**

R: **Propagación de excepciones:** El servicio NO sabe cómo manejar errores de BD (¿mostrar mensaje? ¿logging? ¿reintentar?). Esa decisión es de la capa de presentación (Main.java).

```java
// ✅ BIEN: Service propaga SQLException
public List<Product> getAllProducts() throws SQLException {
    // ...
}

// Main.java decide qué hacer
try {
    List<Product> products = service.getAllProducts();
} catch (SQLException e) {
    System.err.println("Error: " + e.getMessage()); // O logging, o retry, etc.
}
```

---

**¡Felicitaciones por completar la Fase 03.4! 🎉**

Ahora puedes ejecutar queries SELECT desde Java y mapear resultados a objetos. Estás a un paso de completar la Fase 3 completa (solo falta Fase 03.5: PreparedStatement y seguridad).

---

**Forestech CLI - Fase 03.4**
Versión: 1.0
Última actualización: Enero 2025
