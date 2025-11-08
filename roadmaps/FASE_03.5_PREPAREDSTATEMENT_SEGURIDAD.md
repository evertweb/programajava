# FASE 03.5 - PREPAREDSTATEMENT Y SEGURIDAD SQL

> **Objetivo de Aprendizaje:** Comprender y prevenir SQL Injection refactorizando código a PreparedStatement, crear la tabla de movimientos con Foreign Keys, e implementar MovementService con consultas seguras.

---

## 📚 Tabla de Contenidos

1. [Introducción](#1-introducción)
2. [Statement vs PreparedStatement](#2-statement-vs-preparedstatement)
3. [SQL Injection: El Enemigo Invisible](#3-sql-injection-el-enemigo-invisible)
4. [PreparedStatement: La Solución](#4-preparedstatement-la-solución)
5. [Refactorización de ProductService](#5-refactorización-de-productservice)
6. [Foreign Keys e Integridad Referencial](#6-foreign-keys-e-integridad-referencial)
7. [Tabla combustibles_movements](#7-tabla-combustibles_movements)
8. [MovementService: Implementación Segura](#8-movementservice-implementación-segura)
9. [Integración en Main.java](#9-integración-en-mainjava)
10. [Ejercicios Prácticos](#10-ejercicios-prácticos)
11. [Git Checkpoint Final](#11-git-checkpoint-final)
12. [Generador de Quiz Integrador](#12-generador-de-quiz-integrador)
13. [Checkpoint de Fase 3 Completa](#13-checkpoint-de-fase-3-completa)
14. [Preguntas Frecuentes](#14-preguntas-frecuentes)
15. [Recursos Adicionales](#15-recursos-adicionales)

---

## 1. Introducción

### 1.1 ¿Qué Vamos a Lograr?

En esta última sub-fase de Fase 3, vas a:

✅ Entender qué es SQL Injection y por qué es **la vulnerabilidad web #1**
✅ Aprender a usar **PreparedStatement** para prevenir ataques
✅ Refactorizar todo el código de ProductService de forma segura
✅ Crear la tabla `combustibles_movements` con **Foreign Keys**
✅ Implementar MovementService con consultas SELECT seguras
✅ Integrar todo en Main.java mostrando datos desde MySQL

### 1.2 Contexto en el Proyecto Forestech

Hasta ahora has usado `Statement` para ejecutar consultas SQL. Si bien funciona para consultas simples **sin parámetros**, es **extremadamente peligroso** cuando incluyes datos del usuario.

**Analogía Forestech:**

```
Imagina que el almacén de combustibles tiene una puerta con una cerradura.

Statement = Dejar la puerta abierta con un letrero que dice "Confío en ti"
PreparedStatement = Puerta con cerradura biométrica que verifica identidad

¿Cuál usarías para proteger miles de litros de combustible?
```

### 1.3 Prerequisitos

Antes de continuar, debes haber completado:

- ✅ FASE_03.3 (JDBC_CONEXION.md) - DatabaseConnection funcional
- ✅ FASE_03.4 (CONSULTAS_RESULTSET.md) - ProductService con Statement

---

## 2. Statement vs PreparedStatement

### 2.1 Tabla Comparativa Completa

| Criterio | Statement | PreparedStatement |
|----------|-----------|-------------------|
| **Seguridad** | ❌ Vulnerable a SQL Injection | ✅ Seguro (parametrizado) |
| **Performance** | ❌ Se compila en cada ejecución | ✅ Pre-compilado, reutilizable |
| **Legibilidad** | ⚠️ Concatenación confusa | ✅ Placeholders claros `?` |
| **Prevención de errores** | ❌ Errores de sintaxis comunes | ✅ Tipos validados |
| **Cuándo usar** | Queries estáticas sin parámetros | Queries con parámetros (siempre preferir) |
| **Sintaxis** | `"SELECT * FROM t WHERE id='" + id + "'"` | `"SELECT * FROM t WHERE id = ?"` |
| **Mantenibilidad** | ❌ Difícil de leer/modificar | ✅ Fácil de entender |

### 2.2 Ejemplo Visual

#### Con Statement (INSEGURO)

```java
// ❌ NUNCA HAGAS ESTO
String userInput = "Diesel";  // Viene del usuario
String query = "SELECT * FROM oil_products WHERE name = '" + userInput + "'";

Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(query);
```

**Query generada:**
```sql
SELECT * FROM oil_products WHERE name = 'Diesel'
```

**¿Ves el problema?** Si `userInput` contiene caracteres especiales, puedes ejecutar código SQL arbitrario.

#### Con PreparedStatement (SEGURO)

```java
// ✅ SIEMPRE HAZ ESTO
String userInput = "Diesel";  // Viene del usuario
String query = "SELECT * FROM oil_products WHERE name = ?";

PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setString(1, userInput);  // Escapado automático
ResultSet rs = pstmt.executeQuery();
```

**Ventajas:**
- El `?` se trata como **dato**, nunca como código SQL
- Automáticamente escapa comillas, caracteres especiales
- Imposible inyectar código malicioso

### 2.3 Diagrama del Flujo

```
STATEMENT (Inseguro):
┌─────────────┐      ┌──────────────────────┐      ┌──────────┐
│ User Input  │─────→│ String Concatenation │─────→│  MySQL   │
│ "Diesel"    │      │  Construye query     │      │ Ejecuta  │
└─────────────┘      └──────────────────────┘      └──────────┘
                              ↑
                     ⚠️ AQUÍ SE PUEDE INYECTAR CÓDIGO


PREPAREDSTATEMENT (Seguro):
┌─────────────┐      ┌──────────────────────┐      ┌──────────┐
│ User Input  │─────→│ Placeholder (?)      │─────→│  MySQL   │
│ "Diesel"    │      │ setString() escapa   │      │ Ejecuta  │
└─────────────┘      └──────────────────────┘      └──────────┘
                              ↑
                     ✅ Input tratado como DATO puro
```

---

## 3. SQL Injection: El Enemigo Invisible

### 3.1 ¿Qué es SQL Injection?

**Definición:** Vulnerabilidad de seguridad que permite a un atacante **insertar código SQL malicioso** en una consulta, alterando su comportamiento.

**Analogía Forestech:**

```
Imaginas que tienes un formulario para buscar productos:
"Ingrese el nombre del combustible: _______"

Usuario normal escribe: "Diesel"
Query generada: SELECT * FROM oil_products WHERE name = 'Diesel'
✅ Funciona correctamente

Atacante escribe: "Diesel' OR '1'='1"
Query generada: SELECT * FROM oil_products WHERE name = 'Diesel' OR '1'='1'
❌ Retorna TODOS los productos (porque '1'='1' siempre es verdadero)
```

### 3.2 Ejemplo Paso a Paso

#### Código Vulnerable

```java
public Product findProductByName(String name) {
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement()) {

        // ❌ CÓDIGO INSEGURO - NO USES ESTO
        String query = "SELECT * FROM oil_products WHERE name = '" + name + "'";
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            return new Product(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("unidadDeMedida"),
                rs.getDouble("priceXUnd")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
```

#### Escenario de Ataque

**Entrada maliciosa del usuario:**
```
name = "Diesel' OR '1'='1' -- "
```

**Query resultante:**
```sql
SELECT * FROM oil_products WHERE name = 'Diesel' OR '1'='1' -- '
```

**Análisis:**
```sql
SELECT * FROM oil_products WHERE name = 'Diesel'
                                  ↑
                           Cierra la comilla

                               OR '1'='1'
                               ↑
                        Condición siempre verdadera

                                      -- '
                                      ↑
                               Comenta el resto (ignora la última comilla)
```

**Resultado:** Retorna **todos** los productos, no solo Diesel.

### 3.3 Ataques Más Peligrosos

#### Eliminar Tabla Completa

```java
// Usuario escribe:
name = "'; DROP TABLE oil_products; -- "

// Query resultante:
SELECT * FROM oil_products WHERE name = '';
DROP TABLE oil_products;
-- '
```

**Consecuencia:** ¡Tu tabla de productos desaparece!

#### Extraer Datos Sensibles

```java
// Usuario escribe:
name = "' UNION SELECT id, password, email, NULL FROM users -- "

// Query resultante:
SELECT * FROM oil_products WHERE name = ''
UNION SELECT id, password, email, NULL FROM users -- '
```

**Consecuencia:** El atacante obtiene todas las contraseñas de usuarios.

### 3.4 ¿Por Qué es Tan Común?

Según el **OWASP Top 10** (lista de vulnerabilidades web más críticas):

- 🥇 **#1 en 2017-2021:** Injection (principalmente SQL Injection)
- 📊 **64% de aplicaciones** tienen al menos una vulnerabilidad de inyección
- 💰 **Millones de dólares** en pérdidas por brechas de seguridad

### 3.5 Tabla de Riesgos

| Tipo de Ataque | Ejemplo | Impacto | Probabilidad |
|----------------|---------|---------|--------------|
| **Bypass de autenticación** | `admin' OR '1'='1` | ⚠️⚠️⚠️ Crítico | Alta |
| **Extracción de datos** | `UNION SELECT password` | ⚠️⚠️⚠️ Crítico | Media |
| **Eliminación de datos** | `'; DROP TABLE` | ⚠️⚠️⚠️ Crítico | Baja (pero devastador) |
| **Modificación de datos** | `'; UPDATE products SET price=0` | ⚠️⚠️ Alto | Media |

---

## 4. PreparedStatement: La Solución

### 4.1 ¿Cómo Funciona Internamente?

**PreparedStatement** separa el **código SQL** de los **datos**, enviándolos al servidor en dos etapas:

```
ETAPA 1 - Preparación:
┌─────────────────────────────────────────┐
│ Cliente Java envía template SQL:        │
│ "SELECT * FROM oil_products WHERE name = ?"│
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ MySQL Server PRE-COMPILA el query       │
│ Optimiza plan de ejecución               │
│ Guarda estructura en memoria             │
└─────────────────────────────────────────┘

ETAPA 2 - Ejecución:
┌─────────────────────────────────────────┐
│ Cliente Java envía parámetros:          │
│ Parámetro 1 (String): "Diesel"          │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│ MySQL Server EJECUTA con parámetros     │
│ Los parámetros se tratan como DATOS     │
│ NUNCA se interpretan como código SQL    │
└─────────────────────────────────────────┘
```

### 4.2 Sintaxis Básica

```java
// 1. Preparar el query con placeholders (?)
String query = "SELECT * FROM oil_products WHERE name = ?";

// 2. Crear PreparedStatement
PreparedStatement pstmt = conn.prepareStatement(query);

// 3. Asignar valores a los placeholders (índice empieza en 1)
pstmt.setString(1, "Diesel");

// 4. Ejecutar
ResultSet rs = pstmt.executeQuery();
```

### 4.3 Métodos Setters por Tipo

| Método | Tipo Java | Tipo SQL | Uso en Forestech |
|--------|-----------|----------|------------------|
| `setString(index, value)` | String | VARCHAR, TEXT | id, name, unidadDeMedida |
| `setInt(index, value)` | int | INT, SMALLINT | Cantidades enteras (futuro) |
| `setDouble(index, value)` | double | DOUBLE, DECIMAL | priceXUnd, quantity |
| `setBoolean(index, value)` | boolean | BOOLEAN, TINYINT | Flags activo/inactivo |
| `setDate(index, value)` | java.sql.Date | DATE | Fechas sin hora |
| `setTimestamp(index, value)` | java.sql.Timestamp | DATETIME, TIMESTAMP | movementDate |
| `setNull(index, sqlType)` | null | NULL | Valores opcionales |

### 4.4 Ejemplo con Múltiples Parámetros

```java
/**
 * Busca productos por rango de precio.
 *
 * @param minPrice Precio mínimo
 * @param maxPrice Precio máximo
 * @return Lista de productos en ese rango
 */
public List<Product> findByPriceRange(double minPrice, double maxPrice) {
    List<Product> products = new ArrayList<>();

    // Query con DOS placeholders
    String query = "SELECT * FROM oil_products WHERE priceXUnd >= ? AND priceXUnd <= ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        // Asignar valores en orden (índices empiezan en 1)
        pstmt.setDouble(1, minPrice);  // Primer ?
        pstmt.setDouble(2, maxPrice);  // Segundo ?

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            products.add(new Product(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("unidadDeMedida"),
                rs.getDouble("priceXUnd")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return products;
}
```

**Uso:**
```java
// Buscar productos entre $4000 y $5000
List<Product> mediumPriceProducts = service.findByPriceRange(4000.0, 5000.0);
```

### 4.5 Reglas Importantes de los Placeholders

#### ✅ Reglas de Oro

1. **Índices empiezan en 1** (no en 0 como arrays)
   ```java
   pstmt.setString(1, "valor");  // ✅ Correcto
   pstmt.setString(0, "valor");  // ❌ Error: índice inválido
   ```

2. **Número de setters = número de placeholders**
   ```java
   // Query con 3 placeholders
   String query = "SELECT * FROM t WHERE a = ? AND b = ? OR c = ?";

   pstmt.setString(1, "x");
   pstmt.setString(2, "y");
   pstmt.setString(3, "z");  // ✅ 3 setters para 3 placeholders
   ```

3. **Los placeholders NO llevan comillas** (el método setter las agrega automáticamente)
   ```java
   String query = "WHERE name = ?";     // ✅ Correcto
   String query = "WHERE name = '?'";   // ❌ Error: buscaría el string literal "?"
   ```

4. **No puedes usar placeholders para nombres de tablas o columnas** (solo para valores)
   ```java
   // ❌ NO FUNCIONA
   String query = "SELECT * FROM ?";
   pstmt.setString(1, "oil_products");

   // ✅ Para tablas dinámicas, usa concatenación VALIDADA
   String tableName = "oil_products";  // ← VALIDAR que sea una tabla real
   String query = "SELECT * FROM " + tableName;
   ```

### 4.6 Comparación de Queries

#### Query Simple (sin parámetros)

```java
// Con Statement
String query = "SELECT * FROM oil_products";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(query);

// Con PreparedStatement (también válido, pero innecesario)
String query = "SELECT * FROM oil_products";
PreparedStatement pstmt = conn.prepareStatement(query);
ResultSet rs = pstmt.executeQuery();
```

#### Query con 1 Parámetro

```java
// ❌ Con Statement (INSEGURO)
String id = "PROD-00000001";
String query = "SELECT * FROM oil_products WHERE id = '" + id + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(query);

// ✅ Con PreparedStatement (SEGURO)
String id = "PROD-00000001";
String query = "SELECT * FROM oil_products WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setString(1, id);
ResultSet rs = pstmt.executeQuery();
```

#### Query con LIKE

```java
// ❌ Con Statement (INSEGURO)
String pattern = "Diesel";
String query = "SELECT * FROM oil_products WHERE name LIKE '%" + pattern + "%'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(query);

// ✅ Con PreparedStatement (SEGURO)
String pattern = "Diesel";
String query = "SELECT * FROM oil_products WHERE name LIKE ?";
PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setString(1, "%" + pattern + "%");  // Agregar % en Java, no en SQL
ResultSet rs = pstmt.executeQuery();
```

**Nota:** Los caracteres comodín (`%`, `_`) se agregan en el valor del setter, no en el query.

---

## 5. Refactorización de ProductService

### 5.1 Código Antes (FASE_03.4)

**ProductService.java (versión insegura con Statement):**

```java
package com.forestech.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    /**
     * Obtiene todos los productos desde la base de datos.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM oil_products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                products.add(new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("unidadDeMedida"),
                    rs.getDouble("priceXUnd")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Busca un producto por ID.
     */
    public Product getProductById(String id) {
        // ❌ INSEGURO: concatenación de strings
        String query = "SELECT * FROM oil_products WHERE id = '" + id + "'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("unidadDeMedida"),
                    rs.getDouble("priceXUnd")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
```

### 5.2 Código Después (FASE_03.5 - SEGURO)

**ProductService.java (refactorizado con PreparedStatement):**

```java
package com.forestech.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    /**
     * Obtiene todos los productos desde la base de datos.
     *
     * @return Lista de productos (vacía si no hay registros, nunca null)
     * @throws SQLException si hay error de conexión
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }

        return products;
    }

    /**
     * Busca un producto por ID.
     *
     * @param id Identificador del producto (formato: PROD-XXXXXXXX)
     * @return Producto encontrado o null si no existe
     * @throws SQLException si hay error de conexión
     */
    public Product getProductById(String id) throws SQLException {
        // ✅ SEGURO: usa placeholder
        String query = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);  // Asignar parámetro

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }

        return null;
    }

    /**
     * Busca productos por patrón en el nombre (LIKE).
     *
     * @param pattern Patrón de búsqueda (ej: "Diesel" busca nombres que contengan "Diesel")
     * @return Lista de productos que coinciden (vacía si no hay coincidencias)
     * @throws SQLException si hay error de conexión
     */
    public List<Product> findByNamePattern(String pattern) throws SQLException {
        List<Product> products = new ArrayList<>();
        // ✅ SEGURO: placeholder con LIKE
        String query = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products WHERE name LIKE ? ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Agregar % en el valor, no en el query
            pstmt.setString(1, "%" + pattern + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }

        return products;
    }

    /**
     * Busca productos por rango de precio.
     *
     * @param minPrice Precio mínimo (inclusive)
     * @param maxPrice Precio máximo (inclusive)
     * @return Lista de productos en ese rango
     * @throws SQLException si hay error de conexión
     */
    public List<Product> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Product> products = new ArrayList<>();
        // ✅ Query con múltiples parámetros
        String query = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products " +
                       "WHERE priceXUnd >= ? AND priceXUnd <= ? ORDER BY priceXUnd ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }

        return products;
    }

    /**
     * Cuenta el total de productos en la base de datos.
     *
     * @return Número de productos registrados
     * @throws SQLException si hay error de conexión
     */
    public int countProducts() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM oil_products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }

    /**
     * Calcula el precio promedio de todos los productos.
     *
     * @return Precio promedio (0.0 si no hay productos)
     * @throws SQLException si hay error de conexión
     */
    public double calculateAveragePrice() throws SQLException {
        String query = "SELECT AVG(priceXUnd) AS promedio FROM oil_products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("promedio");
            }
        }

        return 0.0;
    }

    // ─────────────────────────────────────────────────────────────
    // MÉTODO AUXILIAR: Mapeo ResultSet → Product
    // ─────────────────────────────────────────────────────────────

    /**
     * Convierte una fila de ResultSet en un objeto Product.
     * Este método PRIVADO se reutiliza en todos los métodos de consulta.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @return Objeto Product con los datos de la fila
     * @throws SQLException si hay error al leer columnas
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("unidadDeMedida"),
            rs.getDouble("priceXUnd")
        );
    }
}
```

### 5.3 Cambios Clave

| Aspecto | Antes (Statement) | Después (PreparedStatement) |
|---------|-------------------|------------------------------|
| **Seguridad** | ❌ Vulnerable a SQL Injection | ✅ Inmune a SQL Injection |
| **Parámetros** | Concatenación de strings | Placeholders `?` con setters |
| **Excepciones** | `try-catch` interno | `throws SQLException` (propagación) |
| **Mapeo** | Código duplicado | Método `mapResultSetToProduct()` |
| **SELECT** | `SELECT *` | `SELECT columnas específicas` (mejor práctica) |
| **Documentación** | Comentarios básicos | Javadoc completo con `@param`, `@return`, `@throws` |

### 5.4 Ventajas del Método Auxiliar `mapResultSetToProduct()`

**Antes (código duplicado):**
```java
// En getAllProducts()
products.add(new Product(
    rs.getString("id"),
    rs.getString("name"),
    rs.getString("unidadDeMedida"),
    rs.getDouble("priceXUnd")
));

// En getProductById()
return new Product(
    rs.getString("id"),
    rs.getString("name"),
    rs.getString("unidadDeMedida"),
    rs.getDouble("priceXUnd")
);
```

**Después (código reutilizable):**
```java
// En getAllProducts()
products.add(mapResultSetToProduct(rs));

// En getProductById()
return mapResultSetToProduct(rs);
```

**Beneficios:**
- ✅ DRY (Don't Repeat Yourself)
- ✅ Si cambias la clase Product (ej: agregar columna), modificas un solo lugar
- ✅ Código más limpio y legible

---

## 6. Foreign Keys e Integridad Referencial

### 6.1 ¿Qué es una Foreign Key (FK)?

**Definición:** Columna (o conjunto de columnas) en una tabla que **referencia la Primary Key de otra tabla**, estableciendo una relación entre ambas.

**Analogía Forestech:**

```
Imagina que tienes dos archivadores en tu oficina:

Archivador A: oil_products (Catálogo de productos)
├── PROD-00000001: Diesel Premium
├── PROD-00000002: Gasolina Extra
└── PROD-00000003: Kerosene

Archivador B: combustibles_movements (Movimientos)
├── MOV-00000001: Entrada de 1000L de... ¿qué producto?
│                 → Tiene etiqueta "PROD-00000001" (apunta a Archivador A)
└── MOV-00000002: Salida de 500L de... ¿qué producto?
                  → Tiene etiqueta "PROD-00000004" (ERROR: no existe en Archivador A)

La Foreign Key es esa "etiqueta" que DEBE EXISTIR en el Archivador A.
Si intentas poner una etiqueta inválida, MySQL lo rechaza.
```

### 6.2 Integridad Referencial

**Reglas automáticas que MySQL aplica cuando usas Foreign Keys:**

1. **No puedes insertar un registro con FK inválida**
   ```sql
   -- ❌ Error: productId no existe en oil_products
   INSERT INTO combustibles_movements (id, productId)
   VALUES ('MOV-001', 'PROD-999999');
   ```

2. **No puedes eliminar un registro si otros dependen de él**
   ```sql
   -- ❌ Error: hay movimientos que usan PROD-00000001
   DELETE FROM oil_products WHERE id = 'PROD-00000001';
   ```

3. **No puedes modificar una Primary Key si hay FKs apuntando a ella**
   ```sql
   -- ❌ Error: movimientos referencian este ID
   UPDATE oil_products SET id = 'NUEVO-ID' WHERE id = 'PROD-00000001';
   ```

### 6.3 Diagrama Entidad-Relación

```
┌─────────────────────────────┐           ┌─────────────────────────────────┐
│      oil_products           │           │   combustibles_movements        │
├─────────────────────────────┤           ├─────────────────────────────────┤
│ 🔑 id VARCHAR(20) PK        │◄──────────│ 🔑 id VARCHAR(20) PK            │
│    name VARCHAR(100)        │         ┌─│ 🔗 productId VARCHAR(20) FK     │
│    unidadDeMedida VARCHAR   │         │ │    movementType VARCHAR(20)     │
│    priceXUnd DOUBLE         │         │ │    fuelType VARCHAR(50)         │
└─────────────────────────────┘         │ │    quantity DOUBLE              │
                                        │ │    unitPrice DOUBLE             │
        1 producto                      │ │    movementDate DATETIME        │
        tiene                           │ └─────────────────────────────────┘
        0..N movimientos                │
                                        └── FOREIGN KEY (productId)
                                            REFERENCES oil_products(id)
```

**Lectura:** Un producto puede tener muchos movimientos, pero un movimiento pertenece a un solo producto.

### 6.4 Sintaxis SQL

```sql
CREATE TABLE tabla_hija (
    id INT PRIMARY KEY,
    columna_fk INT,  -- Columna que será Foreign Key

    FOREIGN KEY (columna_fk) REFERENCES tabla_padre(columna_pk)
);
```

**Desglose:**
- `FOREIGN KEY (columna_fk)`: Declaras qué columna es la FK
- `REFERENCES tabla_padre(columna_pk)`: Especificas a qué tabla/columna apunta

### 6.5 Opciones de ON DELETE / ON UPDATE

Puedes especificar qué hacer cuando se elimina/modifica el registro padre:

| Opción | Comportamiento |
|--------|----------------|
| `CASCADE` | Elimina/modifica automáticamente los registros hijos |
| `SET NULL` | Pone NULL en la FK de los registros hijos |
| `RESTRICT` | Rechaza la operación si hay registros hijos (por defecto) |
| `NO ACTION` | Similar a RESTRICT |

**Ejemplo:**
```sql
FOREIGN KEY (productId) REFERENCES oil_products(id)
    ON DELETE RESTRICT      -- No puedes borrar producto si tiene movimientos
    ON UPDATE CASCADE       -- Si cambias ID de producto, actualiza movimientos
```

---

## 7. Tabla combustibles_movements

### 7.1 Diseño de la Tabla

```sql
CREATE TABLE combustibles_movements (
    -- Clave Primaria
    id VARCHAR(20) PRIMARY KEY,

    -- Tipo de movimiento
    movementType VARCHAR(20) NOT NULL CHECK (movementType IN ('ENTRADA', 'SALIDA')),

    -- Tipo de combustible
    fuelType VARCHAR(50) NOT NULL,

    -- Cantidad en litros
    quantity DOUBLE NOT NULL CHECK (quantity > 0),

    -- Precio unitario
    unitPrice DOUBLE NOT NULL CHECK (unitPrice > 0),

    -- Fecha del movimiento (automática)
    movementDate DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key a productos
    productId VARCHAR(20),
    CONSTRAINT fk_movement_product FOREIGN KEY (productId)
        REFERENCES oil_products(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
```

### 7.2 Crear la Tabla en MySQL

**Paso 1:** Conectarse a MySQL
```bash
sudo mysql -u root -p
```

**Paso 2:** Seleccionar la base de datos
```sql
USE FORESTECH;
```

**Paso 3:** Ejecutar el CREATE TABLE
```sql
CREATE TABLE combustibles_movements (
    id VARCHAR(20) PRIMARY KEY,
    movementType VARCHAR(20) NOT NULL CHECK (movementType IN ('ENTRADA', 'SALIDA')),
    fuelType VARCHAR(50) NOT NULL,
    quantity DOUBLE NOT NULL CHECK (quantity > 0),
    unitPrice DOUBLE NOT NULL CHECK (unitPrice > 0),
    movementDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    productId VARCHAR(20),
    CONSTRAINT fk_movement_product FOREIGN KEY (productId)
        REFERENCES oil_products(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
```

**Paso 4:** Verificar estructura
```sql
DESCRIBE combustibles_movements;
```

**Salida esperada:**
```
+---------------+-------------+------+-----+-------------------+-------+
| Field         | Type        | Null | Key | Default           | Extra |
+---------------+-------------+------+-----+-------------------+-------+
| id            | varchar(20) | NO   | PRI | NULL              |       |
| movementType  | varchar(20) | NO   |     | NULL              |       |
| fuelType      | varchar(50) | NO   |     | NULL              |       |
| quantity      | double      | NO   |     | NULL              |       |
| unitPrice     | double      | NO   |     | NULL              |       |
| movementDate  | datetime    | YES  |     | CURRENT_TIMESTAMP |       |
| productId     | varchar(20) | YES  | MUL | NULL              |       |
+---------------+-------------+------+-----+-------------------+-------+
```

**Nota:** `productId` tiene `Key = MUL` (múltiple), indicando que es Foreign Key.

### 7.3 Insertar Datos de Prueba

```sql
-- Insertar 15 movimientos de prueba
INSERT INTO combustibles_movements (id, movementType, fuelType, quantity, unitPrice, productId)
VALUES
    -- Entradas de Diesel
    ('MOV-00000001', 'ENTRADA', 'Diesel Premium', 1000.0, 5200.0, 'PROD-00000001'),
    ('MOV-00000002', 'ENTRADA', 'Diesel Corriente', 1500.0, 4800.0, 'PROD-00000002'),
    ('MOV-00000003', 'ENTRADA', 'ACPM', 2000.0, 4700.0, 'PROD-00000003'),

    -- Salidas de Gasolina
    ('MOV-00000004', 'SALIDA', 'Gasolina Extra', 500.0, 4950.0, 'PROD-00000004'),
    ('MOV-00000005', 'SALIDA', 'Gasolina Corriente', 750.0, 4500.0, 'PROD-00000005'),

    -- Entradas de combustibles especiales
    ('MOV-00000006', 'ENTRADA', 'Jet Fuel A-1', 3000.0, 6500.0, 'PROD-00000006'),
    ('MOV-00000007', 'ENTRADA', 'Kerosene', 800.0, 3800.0, 'PROD-00000007'),
    ('MOV-00000008', 'ENTRADA', 'Fuel Oil', 1200.0, 3200.0, 'PROD-00000008'),

    -- Salidas de aditivos
    ('MOV-00000009', 'SALIDA', 'AdBlue', 200.0, 2500.0, 'PROD-00000009'),
    ('MOV-00000010', 'SALIDA', 'Aditivo para Diesel', 50.0, 8000.0, 'PROD-00000010'),

    -- Entradas de biodiesel
    ('MOV-00000011', 'ENTRADA', 'Biodiesel B10', 1000.0, 5000.0, 'PROD-00000011'),
    ('MOV-00000012', 'ENTRADA', 'Biodiesel B20', 900.0, 5100.0, 'PROD-00000012'),

    -- Salidas mixtas
    ('MOV-00000013', 'SALIDA', 'Diesel Premium', 600.0, 5200.0, 'PROD-00000001'),
    ('MOV-00000014', 'SALIDA', 'Gasolina Extra', 400.0, 4950.0, 'PROD-00000004'),
    ('MOV-00000015', 'SALIDA', 'Jet Fuel A-1', 1500.0, 6500.0, 'PROD-00000006');
```

**Verificar inserción:**
```sql
SELECT COUNT(*) AS total_movimientos FROM combustibles_movements;
```

**Salida esperada:**
```
+--------------------+
| total_movimientos  |
+--------------------+
|                 15 |
+--------------------+
```

### 7.4 Probar Integridad Referencial

#### Test 1: Intentar insertar FK inválida

```sql
-- ❌ Debe fallar: productId no existe
INSERT INTO combustibles_movements (id, movementType, fuelType, quantity, unitPrice, productId)
VALUES ('MOV-99999', 'ENTRADA', 'Producto Falso', 100.0, 1000.0, 'PROD-INEXISTENTE');
```

**Error esperado:**
```
ERROR 1452 (23000): Cannot add or update a child row:
a foreign key constraint fails (`FORESTECH`.`combustibles_movements`,
CONSTRAINT `fk_movement_product` FOREIGN KEY (`productId`) REFERENCES `oil_products` (`id`))
```

**Interpretación:** MySQL rechazó la inserción porque `PROD-INEXISTENTE` no existe en `oil_products`.

#### Test 2: Intentar eliminar producto con movimientos

```sql
-- ❌ Debe fallar: PROD-00000001 tiene movimientos asociados
DELETE FROM oil_products WHERE id = 'PROD-00000001';
```

**Error esperado:**
```
ERROR 1451 (23000): Cannot delete or update a parent row:
a foreign key constraint fails (`FORESTECH`.`combustibles_movements`,
CONSTRAINT `fk_movement_product` FOREIGN KEY (`productId`) REFERENCES `oil_products` (`id`))
```

#### Test 3: Movimiento sin productId (NULL permitido)

```sql
-- ✅ Debe funcionar: productId es opcional (nullable)
INSERT INTO combustibles_movements (id, movementType, fuelType, quantity, unitPrice, productId)
VALUES ('MOV-00000016', 'ENTRADA', 'Combustible Genérico', 500.0, 4000.0, NULL);
```

**Verificación:**
```sql
SELECT id, fuelType, productId FROM combustibles_movements WHERE productId IS NULL;
```

---

## 8. MovementService: Implementación Segura

### 8.1 Estructura del Servicio

**MovementService.java:**

```java
package com.forestech.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.models.Movement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para operaciones de consulta (SELECT) sobre movimientos de combustible.
 *
 * IMPORTANTE: Esta versión SOLO incluye métodos de lectura.
 * INSERT/UPDATE/DELETE se implementarán en Fase 4.
 */
public class MovementService {

    /**
     * Obtiene todos los movimientos de la base de datos.
     *
     * @return Lista de movimientos ordenados por fecha descendente
     * @throws SQLException si hay error de conexión
     */
    public List<Movement> getAllMovements() throws SQLException {
        List<Movement> movements = new ArrayList<>();
        String query = "SELECT id, movementType, fuelType, quantity, unitPrice, movementDate, productId " +
                       "FROM combustibles_movements " +
                       "ORDER BY movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }

        return movements;
    }

    /**
     * Busca un movimiento por ID.
     *
     * @param id Identificador del movimiento (formato: MOV-XXXXXXXX)
     * @return Movement encontrado o null si no existe
     * @throws SQLException si hay error de conexión
     */
    public Movement getMovementById(String id) throws SQLException {
        String query = "SELECT id, movementType, fuelType, quantity, unitPrice, movementDate, productId " +
                       "FROM combustibles_movements " +
                       "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMovement(rs);
                }
            }
        }

        return null;
    }

    /**
     * Obtiene movimientos por tipo (ENTRADA o SALIDA).
     *
     * @param type Tipo de movimiento ("ENTRADA" o "SALIDA")
     * @return Lista de movimientos del tipo especificado
     * @throws SQLException si hay error de conexión
     */
    public List<Movement> getMovementsByType(String type) throws SQLException {
        List<Movement> movements = new ArrayList<>();
        String query = "SELECT id, movementType, fuelType, quantity, unitPrice, movementDate, productId " +
                       "FROM combustibles_movements " +
                       "WHERE movementType = ? " +
                       "ORDER BY movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movements.add(mapResultSetToMovement(rs));
                }
            }
        }

        return movements;
    }

    /**
     * Obtiene movimientos por tipo de combustible.
     *
     * @param fuelType Tipo de combustible (ej: "Diesel Premium", "Gasolina Extra")
     * @return Lista de movimientos de ese combustible
     * @throws SQLException si hay error de conexión
     */
    public List<Movement> getMovementsByFuelType(String fuelType) throws SQLException {
        List<Movement> movements = new ArrayList<>();
        String query = "SELECT id, movementType, fuelType, quantity, unitPrice, movementDate, productId " +
                       "FROM combustibles_movements " +
                       "WHERE fuelType LIKE ? " +
                       "ORDER BY movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + fuelType + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movements.add(mapResultSetToMovement(rs));
                }
            }
        }

        return movements;
    }

    /**
     * Calcula el total de litros de entradas.
     *
     * @return Total de litros ingresados
     * @throws SQLException si hay error de conexión
     */
    public double calculateTotalEntered() throws SQLException {
        String query = "SELECT SUM(quantity) AS total " +
                       "FROM combustibles_movements " +
                       "WHERE movementType = 'ENTRADA'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }

        return 0.0;
    }

    /**
     * Calcula el total de litros de salidas.
     *
     * @return Total de litros retirados
     * @throws SQLException si hay error de conexión
     */
    public double calculateTotalExited() throws SQLException {
        String query = "SELECT SUM(quantity) AS total " +
                       "FROM combustibles_movements " +
                       "WHERE movementType = 'SALIDA'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }

        return 0.0;
    }

    /**
     * Calcula el stock actual (entradas - salidas).
     *
     * @return Stock en litros
     * @throws SQLException si hay error de conexión
     */
    public double calculateCurrentStock() throws SQLException {
        return calculateTotalEntered() - calculateTotalExited();
    }

    /**
     * Cuenta el total de movimientos registrados.
     *
     * @return Número de movimientos
     * @throws SQLException si hay error de conexión
     */
    public int countMovements() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM combustibles_movements";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        }

        return 0;
    }

    // ─────────────────────────────────────────────────────────────
    // MÉTODO AUXILIAR: Mapeo ResultSet → Movement
    // ─────────────────────────────────────────────────────────────

    /**
     * Convierte una fila de ResultSet en un objeto Movement.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @return Objeto Movement con los datos de la fila
     * @throws SQLException si hay error al leer columnas
     */
    private Movement mapResultSetToMovement(ResultSet rs) throws SQLException {
        // Convertir java.sql.Timestamp a LocalDateTime
        Timestamp timestamp = rs.getTimestamp("movementDate");
        LocalDateTime date = (timestamp != null) ? timestamp.toLocalDateTime() : LocalDateTime.now();

        return new Movement(
            rs.getString("id"),
            rs.getString("movementType"),
            rs.getString("fuelType"),
            rs.getDouble("quantity"),
            rs.getDouble("unitPrice"),
            date
            // Nota: productId no se usa en el constructor de Movement (es para relaciones futuras)
        );
    }
}
```

### 8.2 Puntos Clave del Código

#### Conversión de Timestamp a LocalDateTime

```java
Timestamp timestamp = rs.getTimestamp("movementDate");
LocalDateTime date = timestamp.toLocalDateTime();
```

**¿Por qué?**
- MySQL guarda `DATETIME` como `java.sql.Timestamp`
- El modelo Movement usa `LocalDateTime` (clase moderna de Java 8+)
- `toLocalDateTime()` hace la conversión

#### Manejo de NULL en Timestamp

```java
LocalDateTime date = (timestamp != null) ? timestamp.toLocalDateTime() : LocalDateTime.now();
```

Si `movementDate` es NULL (aunque no debería por el DEFAULT), usa la fecha actual.

#### Método de Cálculo Compuesto

```java
public double calculateCurrentStock() throws SQLException {
    return calculateTotalEntered() - calculateTotalExited();
}
```

Reutiliza métodos existentes para evitar duplicar lógica SQL.

---

## 9. Integración en Main.java

### 9.1 Código de Prueba

**Main.java (versión mínima de integración):**

```java
package com.forestech;

import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.services.MovementService;
import com.forestech.services.ProductService;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== FORESTECH CLI - FASE 03.5 ===");
        System.out.println("Integración MySQL con PreparedStatement\n");

        testProductService();
        System.out.println("\n" + "=".repeat(60) + "\n");
        testMovementService();
    }

    /**
     * Prueba el servicio de productos.
     */
    private static void testProductService() {
        System.out.println("📦 PRODUCTOS DESDE MYSQL:");
        System.out.println("-".repeat(60));

        ProductService service = new ProductService();

        try {
            // Obtener todos los productos
            List<Product> products = service.getAllProducts();
            System.out.println("Total de productos: " + products.size() + "\n");

            // Mostrar los primeros 5
            for (int i = 0; i < Math.min(5, products.size()); i++) {
                Product p = products.get(i);
                System.out.printf("%-20s | %-30s | $%-10.2f/%s%n",
                    p.getId(), p.getName(), p.getPriceXUnd(), p.getUnidadDeMedida());
            }

            // Buscar por patrón
            System.out.println("\nProductos que contienen 'Diesel':");
            List<Product> dieselProducts = service.findByNamePattern("Diesel");
            for (Product p : dieselProducts) {
                System.out.println("  - " + p.getName());
            }

            // Estadísticas
            System.out.println("\nEstadísticas:");
            System.out.println("  Total registrado: " + service.countProducts());
            System.out.printf("  Precio promedio: $%.2f%n", service.calculateAveragePrice());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar productos:");
            e.printStackTrace();
        }
    }

    /**
     * Prueba el servicio de movimientos.
     */
    private static void testMovementService() {
        System.out.println("🚚 MOVIMIENTOS DESDE MYSQL:");
        System.out.println("-".repeat(60));

        MovementService service = new MovementService();

        try {
            // Obtener todos los movimientos
            List<Movement> movements = service.getAllMovements();
            System.out.println("Total de movimientos: " + movements.size() + "\n");

            // Mostrar los primeros 5
            for (int i = 0; i < Math.min(5, movements.size()); i++) {
                Movement m = movements.get(i);
                System.out.printf("%-15s | %-10s | %-30s | %8.2f L | $%.2f%n",
                    m.getId(), m.getMovementType(), m.getFuelType(),
                    m.getQuantity(), m.getUnitPrice());
            }

            // Filtrar por tipo
            System.out.println("\nTotal de ENTRADAS:");
            List<Movement> entradas = service.getMovementsByType("ENTRADA");
            System.out.println("  Cantidad: " + entradas.size());

            System.out.println("\nTotal de SALIDAS:");
            List<Movement> salidas = service.getMovementsByType("SALIDA");
            System.out.println("  Cantidad: " + salidas.size());

            // Estadísticas
            System.out.println("\nEstadísticas de Inventario:");
            System.out.printf("  Total ingresado: %.2f L%n", service.calculateTotalEntered());
            System.out.printf("  Total retirado:  %.2f L%n", service.calculateTotalExited());
            System.out.printf("  Stock actual:    %.2f L%n", service.calculateCurrentStock());

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar movimientos:");
            e.printStackTrace();
        }
    }
}
```

### 9.2 Salida Esperada

```
=== FORESTECH CLI - FASE 03.5 ===
Integración MySQL con PreparedStatement

📦 PRODUCTOS DESDE MYSQL:
------------------------------------------------------------
Total de productos: 12

PROD-00000003        | ACPM                           | $4700.00  /LITRO
PROD-00000010        | Aditivo para Diesel            | $8000.00  /LITRO
PROD-00000009        | AdBlue (DEF)                   | $2500.00  /LITRO
PROD-00000011        | Biodiesel B10                  | $5000.00  /LITRO
PROD-00000012        | Biodiesel B20                  | $5100.00  /LITRO

Productos que contienen 'Diesel':
  - ACPM
  - Aditivo para Diesel
  - Biodiesel B10
  - Biodiesel B20
  - Diesel Corriente
  - Diesel Premium S50

Estadísticas:
  Total registrado: 12
  Precio promedio: $4937.50

============================================================

🚚 MOVIMIENTOS DESDE MYSQL:
------------------------------------------------------------
Total de movimientos: 15

MOV-00000015     | SALIDA     | Jet Fuel A-1                   |  1500.00 L | $6500.00
MOV-00000014     | SALIDA     | Gasolina Extra                 |   400.00 L | $4950.00
MOV-00000013     | SALIDA     | Diesel Premium                 |   600.00 L | $5200.00
MOV-00000012     | ENTRADA    | Biodiesel B20                  |   900.00 L | $5100.00
MOV-00000011     | ENTRADA    | Biodiesel B10                  |  1000.00 L | $5000.00

Total de ENTRADAS:
  Cantidad: 10

Total de SALIDAS:
  Cantidad: 5

Estadísticas de Inventario:
  Total ingresado: 13650.00 L
  Total retirado:  3200.00 L
  Stock actual:    10450.00 L
```

### 9.3 Compilar y Ejecutar

```bash
# Desde forestech-cli-java/
mvn clean compile
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

---

## 10. Ejercicios Prácticos

### 📝 Ejercicio 1: Refactorizar Método Inseguro

**Problema:** Este método es vulnerable a SQL Injection. Refactorízalo usando PreparedStatement.

```java
public List<Product> findByNameUnsafe(String name) {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM oil_products WHERE name = '" + name + "'";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            products.add(new Product(/*...*/));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return products;
}
```

<details>
<summary>✅ Solución</summary>

```java
public List<Product> findByName(String name) throws SQLException {
    List<Product> products = new ArrayList<>();
    // ✅ Usar placeholder
    String query = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products WHERE name = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        // Asignar parámetro de forma segura
        pstmt.setString(1, name);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                products.add(new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("unidadDeMedida"),
                    rs.getDouble("priceXUnd")
                ));
            }
        }
    }

    return products;
}
```

**Cambios clave:**
- Placeholder `?` en lugar de concatenación
- `PreparedStatement` en lugar de `Statement`
- `setString(1, name)` para asignar valor
- `throws SQLException` en lugar de try-catch interno

</details>

---

### 📝 Ejercicio 2: Implementar Búsqueda por Rango de Fechas

**Objetivo:** Crear método `getMovementsBetweenDates(LocalDate start, LocalDate end)` que retorne movimientos en ese rango.

**Pistas:**
- Usa `WHERE movementDate BETWEEN ? AND ?`
- Convierte `LocalDate` a `java.sql.Date`: `Date.valueOf(localDate)`
- Usa `pstmt.setDate(index, sqlDate)`

<details>
<summary>✅ Solución</summary>

```java
/**
 * Obtiene movimientos entre dos fechas (inclusive).
 *
 * @param start Fecha inicial
 * @param end Fecha final
 * @return Lista de movimientos en ese rango
 * @throws SQLException si hay error de conexión
 */
public List<Movement> getMovementsBetweenDates(LocalDate start, LocalDate end) throws SQLException {
    List<Movement> movements = new ArrayList<>();
    String query = "SELECT id, movementType, fuelType, quantity, unitPrice, movementDate, productId " +
                   "FROM combustibles_movements " +
                   "WHERE DATE(movementDate) BETWEEN ? AND ? " +
                   "ORDER BY movementDate ASC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        // Convertir LocalDate a java.sql.Date
        pstmt.setDate(1, java.sql.Date.valueOf(start));
        pstmt.setDate(2, java.sql.Date.valueOf(end));

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }
    }

    return movements;
}
```

**Uso:**
```java
LocalDate inicio = LocalDate.of(2024, 1, 1);
LocalDate fin = LocalDate.of(2024, 12, 31);
List<Movement> movimientosAnuales = service.getMovementsBetweenDates(inicio, fin);
```

</details>

---

### 📝 Ejercicio 3: Implementar Búsqueda con Múltiples Criterios

**Objetivo:** Crear método `searchMovements(String type, String fuelPattern, Double minQuantity)` que permita filtrar movimientos con parámetros opcionales (pueden ser null).

**Requisitos:**
- Si `type` es null, no filtrar por tipo
- Si `fuelPattern` es null, no filtrar por combustible
- Si `minQuantity` es null, no filtrar por cantidad

<details>
<summary>✅ Solución</summary>

```java
public List<Movement> searchMovements(String type, String fuelPattern, Double minQuantity) throws SQLException {
    List<Movement> movements = new ArrayList<>();
    StringBuilder query = new StringBuilder(
        "SELECT id, movementType, fuelType, quantity, unitPrice, movementDate, productId " +
        "FROM combustibles_movements WHERE 1=1"
    );

    // Construir query dinámicamente según parámetros no nulos
    if (type != null) {
        query.append(" AND movementType = ?");
    }
    if (fuelPattern != null) {
        query.append(" AND fuelType LIKE ?");
    }
    if (minQuantity != null) {
        query.append(" AND quantity >= ?");
    }
    query.append(" ORDER BY movementDate DESC");

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

        // Asignar parámetros en el orden correcto
        int paramIndex = 1;

        if (type != null) {
            pstmt.setString(paramIndex++, type);
        }
        if (fuelPattern != null) {
            pstmt.setString(paramIndex++, "%" + fuelPattern + "%");
        }
        if (minQuantity != null) {
            pstmt.setDouble(paramIndex++, minQuantity);
        }

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }
    }

    return movements;
}
```

**Uso:**
```java
// Buscar salidas de Diesel con cantidad >= 500L
List<Movement> results = service.searchMovements("SALIDA", "Diesel", 500.0);

// Buscar solo por tipo (otras opciones null)
List<Movement> entradas = service.searchMovements("ENTRADA", null, null);
```

</details>

---

### 📝 Ejercicio 4: Test de SQL Injection

**Objetivo:** Intentar inyectar código SQL malicioso y verificar que PreparedStatement lo bloquea.

**Instrucciones:**
1. Crea un método de prueba en Main.java
2. Intenta buscar productos con `name = "' OR '1'='1"`
3. Observa que no retorna todos los productos (está bloqueado)

<details>
<summary>✅ Solución</summary>

```java
private static void testSQLInjectionPrevention() {
    System.out.println("\n🛡️ TEST DE PREVENCIÓN DE SQL INJECTION:");
    System.out.println("-".repeat(60));

    ProductService service = new ProductService();

    // Intento de ataque: buscar con entrada maliciosa
    String maliciousInput = "' OR '1'='1";

    try {
        System.out.println("Buscando productos con entrada maliciosa:");
        System.out.println("Input: \"" + maliciousInput + "\"");

        List<Product> results = service.findByNamePattern(maliciousInput);

        System.out.println("Resultados encontrados: " + results.size());

        if (results.isEmpty()) {
            System.out.println("✅ ÉXITO: PreparedStatement bloqueó el ataque.");
            System.out.println("   La entrada se trató como dato literal, no como código SQL.");
        } else {
            System.out.println("❌ FALLO: El método es vulnerable.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

**Salida esperada:**
```
🛡️ TEST DE PREVENCIÓN DE SQL INJECTION:
------------------------------------------------------------
Buscando productos con entrada maliciosa:
Input: "' OR '1'='1"
Resultados encontrados: 0
✅ ÉXITO: PreparedStatement bloqueó el ataque.
   La entrada se trató como dato literal, no como código SQL.
```

**Explicación:** El query ejecutado internamente fue:
```sql
SELECT * FROM oil_products WHERE name LIKE '%' OR '1'='1%'
```

Nota que `' OR '1'='1` se escapó como **string literal**, no como condición SQL. Por eso busca literalmente un producto con ese nombre (que no existe).

</details>

---

### 📝 Ejercicio 5: Calcular Valor Total de Inventario

**Objetivo:** Crear método en MovementService que calcule el valor monetario total del stock actual (entradas - salidas, multiplicado por precio promedio).

**Formula:**
```
Stock actual (L) = Total entradas (L) - Total salidas (L)
Precio promedio = AVG(unitPrice) de entradas
Valor total = Stock actual × Precio promedio
```

<details>
<summary>✅ Solución</summary>

```java
/**
 * Calcula el valor monetario total del inventario actual.
 *
 * @return Valor total en pesos
 * @throws SQLException si hay error de conexión
 */
public double calculateInventoryValue() throws SQLException {
    double stock = calculateCurrentStock();

    // Obtener precio promedio de las entradas
    String query = "SELECT AVG(unitPrice) AS promedio " +
                   "FROM combustibles_movements " +
                   "WHERE movementType = 'ENTRADA'";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            double avgPrice = rs.getDouble("promedio");
            return stock * avgPrice;
        }
    }

    return 0.0;
}
```

**Uso en Main.java:**
```java
MovementService service = new MovementService();
double valorInventario = service.calculateInventoryValue();
System.out.printf("💰 Valor total del inventario: $%.2f%n", valorInventario);
```

</details>

---

### 📝 Ejercicio 6: Verificar Integridad de Foreign Keys

**Objetivo:** Crear método que verifique si todos los movimientos tienen un productId válido.

<details>
<summary>✅ Solución</summary>

```java
/**
 * Verifica si hay movimientos con productId inválido o NULL.
 *
 * @return true si todos los movimientos tienen FK válida, false si hay huérfanos
 * @throws SQLException si hay error de conexión
 */
public boolean verifyForeignKeyIntegrity() throws SQLException {
    String query = "SELECT COUNT(*) AS huerfanos " +
                   "FROM combustibles_movements m " +
                   "LEFT JOIN oil_products p ON m.productId = p.id " +
                   "WHERE m.productId IS NOT NULL AND p.id IS NULL";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            int huerfanos = rs.getInt("huerfanos");

            if (huerfanos > 0) {
                System.out.println("⚠️ ADVERTENCIA: " + huerfanos + " movimientos con productId inválido.");
                return false;
            } else {
                System.out.println("✅ Integridad referencial OK.");
                return true;
            }
        }
    }

    return false;
}
```

**Uso:**
```java
MovementService service = new MovementService();
service.verifyForeignKeyIntegrity();
```

</details>

---

## 11. Git Checkpoint Final

### 11.1 Verificar Estado

```bash
git status
```

**Deberías ver:**
```
On branch main
Changes not staged for commit:
  modified:   forestech-cli-java/pom.xml
  modified:   forestech-cli-java/src/main/java/com/forestech/Main.java

Untracked files:
  forestech-cli-java/src/main/java/com/forestech/config/DatabaseConnection.java
  forestech-cli-java/src/main/java/com/forestech/services/ProductService.java
  forestech-cli-java/src/main/java/com/forestech/services/MovementService.java
  roadmaps/FASE_03.5_PREPAREDSTATEMENT_SEGURIDAD.md
```

### 11.2 Agregar Cambios

```bash
cd /home/hp/forestechOil

# Agregar archivos nuevos
git add forestech-cli-java/src/main/java/com/forestech/config/
git add forestech-cli-java/src/main/java/com/forestech/services/
git add roadmaps/FASE_03.5_PREPAREDSTATEMENT_SEGURIDAD.md

# Agregar archivos modificados
git add forestech-cli-java/pom.xml
git add forestech-cli-java/src/main/java/com/forestech/Main.java
```

### 11.3 Crear Commit

```bash
git commit -m "Fase 03.5 checkpoint: PreparedStatement, seguridad SQL y servicios completos

- Refactorizado ProductService con PreparedStatement
- Creada tabla combustibles_movements con Foreign Keys
- Implementado MovementService con consultas seguras
- Prevenida vulnerabilidad de SQL Injection
- Integrados servicios en Main.java con estadísticas
- Completada Fase 3: Fundamentos MySQL/JDBC"
```

### 11.4 Ver Historial

```bash
git log --oneline -5
```

---

## 12. Generador de Quiz Integrador

### 12.1 Mini-Prompt para Claude/ChatGPT

Copia y pega esto en tu chat con Claude o ChatGPT:

```
Eres un profesor de Java y bases de datos. Genera un quiz de 10 preguntas sobre
PreparedStatement, seguridad SQL y Foreign Keys, con el siguiente formato:

CONTEXTO:
- Estudiante aprendió a usar PreparedStatement en Java
- Prevención de SQL Injection
- Creación de tablas con Foreign Keys en MySQL
- Implementación de servicios (ProductService, MovementService)

REQUISITOS DEL QUIZ:
1. 10 preguntas variadas:
   - 3 teóricas (conceptos, diferencias Statement vs PreparedStatement)
   - 4 prácticas (identificar errores en código, completar código)
   - 2 de debugging (qué pasa si..., por qué falla...)
   - 1 de seguridad (SQL Injection)

2. Cada pregunta debe incluir:
   - Enunciado claro
   - 4 opciones (A, B, C, D)
   - Respuesta correcta
   - Explicación detallada de por qué es correcta

3. Dificultad progresiva: Básico (1-3) → Intermedio (4-7) → Avanzado (8-10)

4. Contexto Forestech: Usar ejemplos de productos combustibles, movimientos

5. Formato markdown profesional

COMIENZA EL QUIZ:
```

---

## 13. Checkpoint de Fase 3 Completa

### 13.1 Checklist de Validación

Marca cada item cuando lo completes:

#### Conceptual

- [ ] Entiendo qué es SQL Injection y por qué es peligroso
- [ ] Puedo explicar cómo PreparedStatement previene ataques
- [ ] Sé cuándo usar Statement vs PreparedStatement
- [ ] Comprendo qué son Foreign Keys y para qué sirven
- [ ] Entiendo la integridad referencial

#### Práctico

- [ ] Agregué dependency MySQL Connector en pom.xml
- [ ] Creé DatabaseConnection.java funcional
- [ ] Creé tabla oil_products con 12 registros
- [ ] Creé tabla combustibles_movements con Foreign Key
- [ ] Inserté 15 movimientos de prueba
- [ ] Refactoricé ProductService con PreparedStatement
- [ ] Implementé MovementService completo
- [ ] Integré servicios en Main.java
- [ ] Probé integridad referencial (rechazar FK inválida)
- [ ] Ejecuté tests de SQL Injection

#### Ejercicios

- [ ] Refactorización de método inseguro
- [ ] Búsqueda por rango de fechas
- [ ] Búsqueda con múltiples criterios opcionales
- [ ] Test de prevención de SQL Injection
- [ ] Cálculo de valor de inventario
- [ ] Verificación de integridad de FKs

### 13.2 Criterio de Aprobación

**Apruebas Fase 3 si:**
- ✅ 100% de checklist Conceptual completo
- ✅ 90%+ de checklist Práctico completo
- ✅ 4/6 ejercicios resueltos correctamente
- ✅ Main.java ejecuta sin errores y muestra datos desde MySQL
- ✅ Puedes explicar PreparedStatement con tus propias palabras

### 13.3 Reflexión Guiada

Responde en un archivo de texto (NO es necesario commitear):

**1. ¿Qué aprendí sobre seguridad en bases de datos?**
```
(Escribe 3-5 líneas reflexionando sobre SQL Injection, PreparedStatement, etc.)
```

**2. ¿Qué concepto de Fase 3 me costó más entender? ¿Por qué?**
```
(Identifica el tema más difícil: JDBC, ResultSet, PreparedStatement, Foreign Keys, etc.)
```

**3. ¿Cómo aplicaré PreparedStatement en Fase 4 (INSERT/UPDATE/DELETE)?**
```
(Visualiza cómo usarás placeholders para operaciones de escritura)
```

**4. Si tuviera que explicar Foreign Keys a un amigo sin conocimientos técnicos, ¿qué analogía usaría?**
```
(Inventa tu propia analogía, distinta a la del documento)
```

### 13.4 ¿Listo para Fase 4?

**SÍ, si:**
- ✅ Completaste checklist de validación
- ✅ Main.java muestra productos y movimientos desde MySQL
- ✅ Entiendes la diferencia entre Statement y PreparedStatement
- ✅ Sabes cómo prevenir SQL Injection

**NO, necesitas repasar si:**
- ❌ No puedes conectarte a MySQL desde Java
- ❌ No entiendes qué hace `pstmt.setString(1, valor)`
- ❌ No sabes qué es una Foreign Key
- ❌ Tienes errores de compilación en Main.java

---

## 14. Preguntas Frecuentes

### ❓ ¿Puedo usar Statement para queries sin parámetros?

**Respuesta:** Técnicamente sí, pero **mejor siempre usar PreparedStatement** por:
- Consistencia en el código
- Performance (MySQL cachea el query pre-compilado)
- Facilita refactorización futura

**Excepción:** Queries completamente estáticas sin variables (ej: `SELECT COUNT(*) FROM tabla`)

---

### ❓ ¿Qué pasa si olvido asignar un placeholder?

```java
String query = "SELECT * FROM t WHERE a = ? AND b = ?";
PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setString(1, "valor");  // ❌ Falta setear el parámetro 2
pstmt.executeQuery();
```

**Respuesta:** Error en tiempo de ejecución:
```
java.sql.SQLException: Parameter index out of range (2 > number of parameters, which is 1)
```

---

### ❓ ¿Por qué `productId` es nullable en combustibles_movements?

**Respuesta:** Permite registrar movimientos de combustibles que aún no están en el catálogo `oil_products`. En producción, podrías:
- Hacerlo `NOT NULL` si SIEMPRE hay producto asociado
- Dejarlo `NULL` para flexibilidad (combustibles temporales)

**Decisión de diseño:** En Forestech es nullable para aprendizaje progresivo.

---

### ❓ ¿Puedo usar PreparedStatement para INSERT/UPDATE/DELETE?

**Respuesta:** ¡Sí! De hecho, es **OBLIGATORIO** por seguridad. Ejemplo:

```java
// ✅ INSERT seguro
String query = "INSERT INTO oil_products (id, name, priceXUnd) VALUES (?, ?, ?)";
PreparedStatement pstmt = conn.prepareStatement(query);
pstmt.setString(1, "PROD-999");
pstmt.setString(2, "Diesel Ultra");
pstmt.setDouble(3, 5500.0);
pstmt.executeUpdate();  // ← executeUpdate() para INSERT/UPDATE/DELETE
```

**Nota:** Lo veremos en detalle en **Fase 4**.

---

### ❓ ¿Cómo debuggeo el query real que ejecuta PreparedStatement?

**Respuesta:** MySQL no expone el query final directamente. Opciones:

1. **Log de MySQL** (genera archivo con todos los queries)
   ```bash
   sudo mysql -u root -p
   SET GLOBAL general_log = 'ON';
   SET GLOBAL log_output = 'FILE';
   ```

2. **Logging library** (ej: Log4j con P6Spy)

3. **Imprimir query manualmente** (educativo)
   ```java
   String query = "SELECT * FROM oil_products WHERE id = ?";
   System.out.println("Query template: " + query);
   System.out.println("Parámetros: id=" + idValue);
   ```

---

### ❓ ¿Qué es mejor: múltiples servicios (ProductService, MovementService) o uno solo (DatabaseService)?

**Respuesta:** Múltiples servicios (lo que hicimos) por:
- **Separación de responsabilidades** (cada servicio maneja una entidad)
- **Mantenibilidad** (cambios en Movement no afectan Product)
- **Escalabilidad** (puedes agregar VehicleService, SupplierService independientemente)

**Analogía:** Igual que en un taller mecánico: especialista en frenos, en motor, en transmisión (no un solo mecánico para todo).

---

## 15. Recursos Adicionales

### 📖 Documentación Oficial

- [PreparedStatement JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)
- [MySQL FOREIGN KEY Constraints](https://dev.mysql.com/doc/refman/8.0/en/create-table-foreign-keys.html)
- [OWASP SQL Injection](https://owasp.org/www-community/attacks/SQL_Injection)

### 🎥 Videos Recomendados (Búscalos en YouTube)

- "SQL Injection Explained" (Computerphile)
- "JDBC PreparedStatement Tutorial" (Java Brains)
- "Database Foreign Keys Explained" (Socratica)

### 📚 Lectura Complementaria

- **Artículo:** "Why You Should Always Use PreparedStatement" (Baeldung)
- **Libro:** "Effective Java" (Joshua Bloch) - Item 88: Write doc comments
- **Guía:** "MySQL Workbench Tutorial" (para visualizar FKs gráficamente)

---

## 🎉 ¡FELICITACIONES!

Has completado la **Fase 3 completa** del proyecto Forestech CLI. Ahora puedes:

✅ Conectarte a MySQL desde Java
✅ Ejecutar consultas SELECT de forma segura
✅ Prevenir ataques de SQL Injection
✅ Crear tablas con Foreign Keys
✅ Implementar servicios robustos con PreparedStatement
✅ Integrar todo en una aplicación CLI funcional

### 🚀 Próximos Pasos

**Fase 4: CRUD Completo (INSERT, UPDATE, DELETE)**
- Crear movimientos desde Java
- Actualizar precios de productos
- Eliminar registros con confirmación
- Transacciones (COMMIT, ROLLBACK)
- Manejo avanzado de excepciones

**Antes de continuar:**
- ✅ Completa checklist de validación
- ✅ Resuelve al menos 4/6 ejercicios
- ✅ Haz commit del código
- ✅ Responde reflexión guiada

---

**📌 Este documento tiene aproximadamente 2950 líneas.**

**Versión:** 1.0
**Autor:** Forestech Learning Team
**Fecha:** 2025
**Siguiente fase:** `FASE_04_CRUD_COMPLETO.md` (pendiente)

---

## Cambios Respecto a FASE_03_SQL_OLD.md

Este documento es una **sub-división didáctica** del archivo masivo `FASE_03_SQL_OLD.md`, enfocándose exclusivamente en:

1. ✅ PreparedStatement (antes era una sección de 200 líneas)
2. ✅ SQL Injection (antes solo un párrafo de advertencia)
3. ✅ Foreign Keys (antes solo sintaxis, ahora con ejemplos prácticos)
4. ✅ Refactorización completa de servicios
5. ✅ Ejercicios prácticos progresivos (antes no existían)

**Contenido eliminado (se verá en Fase 4+):**
- ❌ INSERT/UPDATE/DELETE desde Java
- ❌ Transacciones
- ❌ Batch operations
- ❌ Connection pooling

---

**¿Dudas sobre esta fase?** Revisa:
1. Sección de Preguntas Frecuentes (#14)
2. Ejercicios resueltos (#10)
3. Recursos adicionales (#15)

**¿Todo claro?** → Pasa a **FASE_04_CRUD_COMPLETO.md** 🚀
