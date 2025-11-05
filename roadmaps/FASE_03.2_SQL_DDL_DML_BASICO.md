# FASE 03.2 - SQL DDL Y DML BÁSICO

> **Objetivo de Aprendizaje:** Crear tablas con tipos de datos y constraints apropiados, insertar registros múltiples, y realizar consultas SELECT con filtros, ordenamiento y funciones de agregación en MySQL.

---

## 📚 Tabla de Contenidos

1. [Introducción: DDL vs DML](#1-introducción-ddl-vs-dml)
2. [Tipos de Datos en MySQL](#2-tipos-de-datos-en-mysql)
3. [Constraints (Restricciones)](#3-constraints-restricciones)
4. [Creando la Tabla oil_products](#4-creando-la-tabla-oil_products)
5. [INSERT: Insertando Datos](#5-insert-insertando-datos)
6. [SELECT: Consultando Datos](#6-select-consultando-datos)
7. [Filtros con WHERE](#7-filtros-con-where)
8. [Ordenamiento con ORDER BY](#8-ordenamiento-con-order-by)
9. [Búsquedas con LIKE](#9-búsquedas-con-like)
10. [Funciones de Agregación](#10-funciones-de-agregación)
11. [Ejercicios Prácticos Progresivos](#11-ejercicios-prácticos-progresivos)
12. [Git Checkpoint](#12-git-checkpoint)
13. [Generador de Quiz de Validación](#13-generador-de-quiz-de-validación)
14. [Checkpoint de Fase](#14-checkpoint-de-fase)

---

## 1. Introducción: DDL vs DML

### ¿Qué es DDL?

**DDL = Data Definition Language** (Lenguaje de Definición de Datos)

Comandos que **definen la estructura** de la base de datos:
- `CREATE TABLE` - Crear tablas
- `ALTER TABLE` - Modificar estructura de tabla
- `DROP TABLE` - Eliminar tabla
- `TRUNCATE TABLE` - Vaciar tabla (mantiene estructura)

**Analogía Forestech:** DDL es como **diseñar el estante** de la biblioteca: cuántos cajones tiene, qué tipo de libros almacena, qué etiquetas lleva.

### ¿Qué es DML?

**DML = Data Manipulation Language** (Lenguaje de Manipulación de Datos)

Comandos que **trabajan con los datos** dentro de las tablas:
- `INSERT` - Insertar registros
- `SELECT` - Consultar registros
- `UPDATE` - Modificar registros (Fase posterior)
- `DELETE` - Eliminar registros (Fase posterior)

**Analogía Forestech:** DML es como **gestionar los libros**: agregar nuevos libros, buscar libros específicos, actualizar información, remover libros.

### Flujo Típico

```
1. DDL: CREATE TABLE oil_products (...);     ← Creas el estante
2. DML: INSERT INTO oil_products VALUES (...); ← Agregas libros
3. DML: SELECT * FROM oil_products;          ← Consultas qué libros hay
4. DML: UPDATE oil_products SET ...;         ← Actualizas info (Fase 4)
5. DML: DELETE FROM oil_products WHERE ...;  ← Quitas libros (Fase 4)
```

---

## 2. Tipos de Datos en MySQL

### 2.1 Categorías Principales

MySQL organiza los tipos de datos en 5 categorías:

```
┌─────────────────────────────────────────────────────────┐
│              TIPOS DE DATOS MYSQL                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  📝 TEXTO          🔢 NUMÉRICOS       📅 FECHA/HORA     │
│  - CHAR            - INT              - DATE            │
│  - VARCHAR         - BIGINT           - DATETIME        │
│  - TEXT            - DOUBLE           - TIMESTAMP       │
│  - LONGTEXT        - DECIMAL          - TIME            │
│                    - FLOAT            - YEAR            │
│                                                          │
│  ✅ BOOLEANOS      💾 BINARIOS                          │
│  - BOOLEAN         - BLOB                               │
│  - TINYINT(1)      - LONGBLOB                           │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Tipos de Texto: VARCHAR vs TEXT

#### VARCHAR(n)

**Definición:** Cadena de caracteres de longitud **variable** con máximo `n` caracteres.

```sql
VARCHAR(50)  -- Máximo 50 caracteres
VARCHAR(255) -- Máximo 255 caracteres
```

**Características:**
- ✅ Búsquedas rápidas (puede indexarse eficientemente)
- ✅ Ideal para campos cortos: nombres, emails, IDs
- ⚠️ Límite máximo: 65,535 caracteres totales por fila
- 💾 Almacena solo lo que usas + 1-2 bytes de overhead

**Ejemplo Forestech:**

```sql
name VARCHAR(100)         -- Nombre de producto: "Diesel Premium S50"
licensePlate VARCHAR(10)  -- Placa vehículo: "ABC-123"
email VARCHAR(100)        -- Email proveedor: "ventas@petromax.com"
```

#### TEXT

**Definición:** Cadena de caracteres para textos largos (hasta 65,535 caracteres).

```sql
TEXT          -- Hasta ~65 KB
MEDIUMTEXT    -- Hasta ~16 MB
LONGTEXT      -- Hasta ~4 GB
```

**Características:**
- ✅ Para contenido extenso: descripciones, comentarios, logs
- ❌ No puede tener valor DEFAULT
- ❌ Búsquedas más lentas que VARCHAR
- ❌ No se puede indexar completamente (solo prefijos)

**Cuándo usar TEXT:**

```sql
-- ❌ MAL: Usar TEXT para nombre corto
productName TEXT

-- ✅ BIEN: Usar VARCHAR para nombre corto
productName VARCHAR(100)

-- ✅ BIEN: Usar TEXT para descripción larga
productDescription TEXT
```

**En Forestech:** Por ahora **no usaremos TEXT**. Todos los campos son cortos y usan VARCHAR.

### 2.3 Comparativa VARCHAR(n) con Diferentes Tamaños

| Tipo | Almacenamiento | Uso en Forestech | Ejemplo |
|------|----------------|------------------|---------|
| `VARCHAR(10)` | Hasta 10 chars + 1 byte | Placas de vehículos | `"ABC-123"` |
| `VARCHAR(20)` | Hasta 20 chars + 1 byte | IDs de productos | `"PROD-12345678"` |
| `VARCHAR(50)` | Hasta 50 chars + 1 byte | Nombres cortos | `"Diesel"` |
| `VARCHAR(100)` | Hasta 100 chars + 2 bytes | Nombres completos | `"Diesel Premium Ultra S50"` |
| `VARCHAR(200)` | Hasta 200 chars + 2 bytes | Direcciones | `"Calle 123 #45-67 Bogotá"` |
| `VARCHAR(255)` | Hasta 255 chars + 2 bytes | Emails, URLs | `"contacto@forestech.com"` |

**Regla práctica:** Usa el tamaño **más pequeño que necesites** + 20-30% de margen.

### 2.4 Tipos Numéricos Enteros

#### INT vs BIGINT

| Tipo | Rango | Bytes | Uso en Forestech |
|------|-------|-------|------------------|
| `TINYINT` | -128 a 127 | 1 | Flags booleanos (0/1) |
| `SMALLINT` | -32,768 a 32,767 | 2 | Cantidades pequeñas |
| `INT` | -2,147,483,648 a 2,147,483,647 | 4 | IDs numéricos, contadores |
| `BIGINT` | -9 quintillones a 9 quintillones | 8 | IDs de sistemas masivos |

**En Forestech:**
- Usamos **VARCHAR para IDs** (`"PROD-12345678"`) por legibilidad
- Usaríamos **INT** si fueran IDs numéricos autoincreméntales

**Ejemplo comparativo:**

```sql
-- Opción 1: ID como VARCHAR (usamos esto)
id VARCHAR(20) PRIMARY KEY

-- Opción 2: ID como INT autoincrementado (común en otras apps)
id INT AUTO_INCREMENT PRIMARY KEY
```

**Ventajas de VARCHAR para IDs en Forestech:**
- ✅ Legible: `"PROD-00001"` vs `1`
- ✅ Prefijos descriptivos: `MOV-`, `VEH-`, `SUP-`
- ✅ Consistente con nuestro `IdGenerator.java`

**Desventajas:**
- ❌ Ocupa más espacio (20 bytes vs 4 bytes)
- ❌ Comparaciones ligeramente más lentas

**Decisión:** Para aprendizaje, preferimos **legibilidad sobre optimización prematura**.

### 2.5 Tipos Numéricos Decimales

#### DOUBLE vs DECIMAL

| Aspecto | DOUBLE | DECIMAL(p, s) |
|---------|--------|---------------|
| **Tipo** | Punto flotante | Punto fijo |
| **Precisión** | Aproximada (~15 dígitos) | Exacta (hasta 65 dígitos) |
| **Almacenamiento** | 8 bytes | Variable según precisión |
| **Velocidad** | Más rápido | Más lento |
| **Uso típico** | Cálculos científicos | Dinero, finanzas |

**Sintaxis DECIMAL:**

```sql
DECIMAL(p, s)
-- p = precision (total de dígitos)
-- s = scale (dígitos decimales)
```

**Ejemplos:**

```sql
-- DECIMAL(10, 2) - Hasta 99,999,999.99
priceXUnd DECIMAL(10, 2)  -- Precio: 5000.50

-- DECIMAL(15, 3) - Mayor precisión para cantidad en litros
quantity DECIMAL(15, 3)   -- Cantidad: 1234567890.125
```

**Comparación práctica:**

```sql
-- DOUBLE: Aproximación
SELECT 0.1 + 0.2;  -- Resultado: 0.30000000000000004

-- DECIMAL: Exactitud
SELECT CAST(0.1 AS DECIMAL(10,2)) + CAST(0.2 AS DECIMAL(10,2));
-- Resultado: 0.30
```

**En Forestech:**

Por ahora usaremos **DOUBLE** por simplicidad, ya que:
- Fase de aprendizaje (precisión extrema no es crítica)
- Facilita mapeo directo con Java `double`
- Suficientemente preciso para litros y precios educativos

**Fase futura (producción):** Cambiaríamos a `DECIMAL(10, 2)` para precios y `DECIMAL(15, 3)` para cantidades.

### 2.6 Tipos de Fecha y Hora

#### DATE vs DATETIME vs TIMESTAMP

| Tipo | Formato | Rango | Uso |
|------|---------|-------|-----|
| `DATE` | YYYY-MM-DD | 1000-01-01 a 9999-12-31 | Fechas sin hora |
| `DATETIME` | YYYY-MM-DD HH:MM:SS | 1000-01-01 00:00:00 a 9999-12-31 23:59:59 | Fechas con hora específica |
| `TIMESTAMP` | YYYY-MM-DD HH:MM:SS | 1970-01-01 00:00:01 a 2038-01-19 03:14:07 | Fechas con hora UTC |

**Diferencia clave DATETIME vs TIMESTAMP:**

```sql
-- DATETIME: Almacena literalmente lo que ingresas
INSERT INTO movements (movementDate) VALUES ('2025-01-15 10:30:00');
-- Se guarda exactamente: 2025-01-15 10:30:00

-- TIMESTAMP: Convierte a UTC automáticamente
-- (Si tu zona horaria es UTC-5):
INSERT INTO movements (movementDate) VALUES ('2025-01-15 10:30:00');
-- Se guarda en UTC: 2025-01-15 15:30:00
```

**En Forestech:**

```sql
movementDate DATETIME  -- Usaremos DATETIME por simplicidad
```

**Valores especiales:**

```sql
-- Fecha/hora actual al insertar
createdAt DATETIME DEFAULT CURRENT_TIMESTAMP

-- Actualiza automáticamente al modificar
updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

### 2.7 Tabla Resumen para Forestech

| Campo Forestech | Tipo MySQL | Razón |
|-----------------|------------|-------|
| `id` (productos, movimientos) | `VARCHAR(20)` | IDs legibles con prefijo |
| `name` | `VARCHAR(100)` | Nombres de productos |
| `unidadDeMedida` | `VARCHAR(20)` | "LITRO", "GALON", "BARRIL" |
| `priceXUnd` | `DOUBLE` | Precio por unidad (simplicidad educativa) |
| `quantity` | `DOUBLE` | Cantidad en litros |
| `movementType` | `VARCHAR(20)` | "ENTRADA", "SALIDA" |
| `fuelType` | `VARCHAR(50)` | "Diesel", "Gasolina" |
| `movementDate` | `DATETIME` | Fecha y hora del movimiento |
| `licensePlate` | `VARCHAR(10)` | Placa vehículo "ABC-123" |
| `email` | `VARCHAR(100)` | Email de proveedor |
| `phoneNumber` | `VARCHAR(20)` | Teléfono con código país |

---

## 3. Constraints (Restricciones)

### ¿Qué son los Constraints?

**Definición:** Reglas que MySQL **hace cumplir automáticamente** para garantizar la integridad de los datos.

**Analogía Forestech:** Son como las políticas de la biblioteca:
- "Todo libro debe tener código único" → PRIMARY KEY
- "No se permiten libros sin título" → NOT NULL
- "El precio debe ser positivo" → CHECK

### 3.1 PRIMARY KEY (PK)

**Propósito:** Identificar de manera única cada fila de la tabla.

**Propiedades automáticas:**
1. ✅ `NOT NULL` - No puede ser vacío
2. ✅ `UNIQUE` - No puede repetirse
3. ✅ Crea índice automático (búsquedas rápidas)

**Sintaxis:**

```sql
-- Opción 1: Definir en la columna
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100)
);

-- Opción 2: Definir al final (preferida para múltiples PKs)
CREATE TABLE oil_products (
    id VARCHAR(20),
    name VARCHAR(100),
    PRIMARY KEY (id)
);
```

**PK Compuesta (múltiples columnas):**

```sql
-- Ejemplo (no lo usaremos ahora)
CREATE TABLE product_prices (
    productId VARCHAR(20),
    effectiveDate DATE,
    price DOUBLE,
    PRIMARY KEY (productId, effectiveDate)
);
```

**En Forestech:**

```sql
id VARCHAR(20) PRIMARY KEY  -- Todos nuestros IDs son PK
```

### 3.2 NOT NULL

**Propósito:** Campo obligatorio, no puede dejarse vacío.

```sql
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,  -- ← Obligatorio
    priceXUnd DOUBLE              -- ← Opcional (puede ser NULL)
);
```

**Comportamiento:**

```sql
-- ✅ VÁLIDO
INSERT INTO oil_products (id, name, priceXUnd)
VALUES ('PROD-001', 'Diesel', 5000.0);

-- ✅ VÁLIDO (priceXUnd acepta NULL)
INSERT INTO oil_products (id, name)
VALUES ('PROD-002', 'Gasolina');

-- ❌ ERROR: name es NOT NULL
INSERT INTO oil_products (id, priceXUnd)
VALUES ('PROD-003', 4500.0);
-- ERROR 1364: Field 'name' doesn't have a default value
```

**Cuándo usar NOT NULL:**
- ✅ Campos críticos para identificación (`name`, `licensePlate`)
- ✅ Campos que siempre deben tener valor lógico
- ❌ Campos opcionales que pueden desconocerse inicialmente

### 3.3 UNIQUE

**Propósito:** Garantiza que no haya valores duplicados (pero permite NULL).

```sql
CREATE TABLE combustibles_vehicles (
    id VARCHAR(20) PRIMARY KEY,
    licensePlate VARCHAR(10) UNIQUE,  -- ← No puede repetirse
    brand VARCHAR(50)
);
```

**Diferencia con PRIMARY KEY:**

| Aspecto | PRIMARY KEY | UNIQUE |
|---------|-------------|--------|
| Valores NULL | No permite | Permite NULL |
| Cantidad por tabla | Solo 1 PK | Múltiples UNIQUE |
| Crea índice | Sí (clustered) | Sí (non-clustered) |

**Ejemplo:**

```sql
-- ✅ VÁLIDO
INSERT INTO combustibles_vehicles VALUES ('VEH-001', 'ABC-123', 'Toyota');
INSERT INTO combustibles_vehicles VALUES ('VEH-002', 'XYZ-789', 'Mazda');

-- ❌ ERROR: licensePlate duplicada
INSERT INTO combustibles_vehicles VALUES ('VEH-003', 'ABC-123', 'Honda');
-- ERROR 1062: Duplicate entry 'ABC-123' for key 'licensePlate'

-- ✅ VÁLIDO: Múltiples NULL permitidos en UNIQUE
INSERT INTO combustibles_vehicles VALUES ('VEH-004', NULL, 'Ford');
INSERT INTO combustibles_vehicles VALUES ('VEH-005', NULL, 'Nissan');
```

### 3.4 DEFAULT

**Propósito:** Valor automático si no se proporciona al insertar.

```sql
CREATE TABLE combustibles_movements (
    id VARCHAR(20) PRIMARY KEY,
    movementType VARCHAR(20) NOT NULL,
    quantity DOUBLE DEFAULT 0.0,           -- ← Valor por defecto
    movementDate DATETIME DEFAULT CURRENT_TIMESTAMP  -- ← Fecha actual
);
```

**Ejemplo:**

```sql
-- Sin especificar quantity ni movementDate
INSERT INTO combustibles_movements (id, movementType)
VALUES ('MOV-001', 'ENTRADA');

-- Resultado almacenado:
-- id: MOV-001
-- movementType: ENTRADA
-- quantity: 0.0 (aplicó DEFAULT)
-- movementDate: 2025-01-15 14:30:00 (hora actual)
```

**Valores especiales útiles:**

```sql
DEFAULT CURRENT_TIMESTAMP        -- Fecha/hora actual
DEFAULT 'LITRO'                  -- String literal
DEFAULT 0                        -- Numérico
DEFAULT TRUE                     -- Booleano
```

### 3.5 CHECK (MySQL 8.0+)

**Propósito:** Validar que el valor cumpla una condición lógica.

```sql
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    priceXUnd DOUBLE CHECK (priceXUnd > 0),  -- ← Precio debe ser positivo
    unidadDeMedida VARCHAR(20) CHECK (unidadDeMedida IN ('LITRO', 'GALON', 'BARRIL'))
);
```

**Ejemplo de error:**

```sql
-- ❌ ERROR: Precio negativo
INSERT INTO oil_products VALUES ('PROD-001', 'Diesel', -5000.0, 'LITRO');
-- ERROR 3819: Check constraint 'oil_products_chk_1' is violated

-- ✅ VÁLIDO
INSERT INTO oil_products VALUES ('PROD-001', 'Diesel', 5000.0, 'LITRO');
```

**Nota:** CHECK es relativamente nuevo en MySQL (8.0.16+). Si usas versiones anteriores, la validación debe hacerse en Java.

**En Forestech:** Usaremos CHECK opcionalmente en ejercicios avanzados.

### 3.6 AUTO_INCREMENT (No lo usaremos)

Para referencia, así se crean IDs numéricos automáticos:

```sql
CREATE TABLE example (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);

INSERT INTO example (name) VALUES ('Item 1');
-- id se genera automáticamente: 1

INSERT INTO example (name) VALUES ('Item 2');
-- id: 2
```

**Forestech usa IDs personalizados** generados en Java (`IdGenerator.java`), así que **no usaremos AUTO_INCREMENT**.

---

## 4. Creando la Tabla oil_products

### 4.1 Diseño de la Tabla

Basándonos en la clase `Product.java` de Fase 2:

```java
// Fase 2 - Clase Java
public class Product {
    private String id;              // VARCHAR(20)
    private String name;            // VARCHAR(100)
    private String unidadDeMedida;  // VARCHAR(20)
    private double priceXUnd;       // DOUBLE
}
```

**Traducción a SQL:**

```sql
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unidadDeMedida VARCHAR(20) NOT NULL DEFAULT 'LITRO',
    priceXUnd DOUBLE NOT NULL
);
```

### 4.2 Paso a Paso de Creación

#### 1. Conectarse a MySQL

```bash
sudo service mysql start
sudo mysql -u root -p
```

#### 2. Seleccionar la BD FORESTECH

```sql
USE FORESTECH;
```

**Salida:**
`Database changed`

#### 3. Verificar que no existan tablas previas

```sql
SHOW TABLES;
```

**Salida esperada:**
`Empty set (0.00 sec)`

#### 4. Crear la tabla

```sql
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unidadDeMedida VARCHAR(20) NOT NULL DEFAULT 'LITRO',
    priceXUnd DOUBLE NOT NULL
);
```

**Salida:**
`Query OK, 0 rows affected (0.03 sec)`

#### 5. Verificar creación

```sql
SHOW TABLES;
```

**Salida:**

```
+---------------------+
| Tables_in_FORESTECH |
+---------------------+
| oil_products        |
+---------------------+
1 row in set (0.00 sec)
```

#### 6. Ver estructura de la tabla

```sql
DESCRIBE oil_products;
-- o abreviado:
DESC oil_products;
```

**Salida:**

```
+----------------+--------------+------+-----+---------+-------+
| Field          | Type         | Null | Key | Default | Extra |
+----------------+--------------+------+-----+---------+-------+
| id             | varchar(20)  | NO   | PRI | NULL    |       |
| name           | varchar(100) | NO   |     | NULL    |       |
| unidadDeMedida | varchar(20)  | NO   |     | LITRO   |       |
| priceXUnd      | double       | NO   |     | NULL    |       |
+----------------+--------------+------+-----+---------+-------+
4 rows in set (0.01 sec)
```

**Interpretación de columnas:**

- **Field:** Nombre de la columna
- **Type:** Tipo de dato
- **Null:** `NO` = NOT NULL, `YES` = acepta NULL
- **Key:** `PRI` = Primary Key, `UNI` = Unique, `MUL` = Foreign Key
- **Default:** Valor por defecto si existe
- **Extra:** Atributos especiales (AUTO_INCREMENT, etc.)

### 4.3 Comando Completo con Comentarios

```sql
CREATE TABLE oil_products (
    -- Identificador único del producto (generado por IdGenerator.java)
    id VARCHAR(20) PRIMARY KEY,

    -- Nombre del producto (ej: "Diesel Premium S50")
    name VARCHAR(100) NOT NULL,

    -- Unidad de medida (LITRO, GALON, BARRIL)
    -- Por defecto LITRO si no se especifica
    unidadDeMedida VARCHAR(20) NOT NULL DEFAULT 'LITRO',

    -- Precio por unidad en pesos colombianos
    priceXUnd DOUBLE NOT NULL
);
```

**Nota:** MySQL permite comentarios con `--` (línea) o `/* */` (bloque).

### 4.4 Modificar Tabla (Si Cometiste un Error)

Si creaste la tabla incorrectamente, tienes 2 opciones:

#### Opción 1: Eliminar y recrear (tabla vacía)

```sql
DROP TABLE oil_products;

CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unidadDeMedida VARCHAR(20) NOT NULL DEFAULT 'LITRO',
    priceXUnd DOUBLE NOT NULL
);
```

#### Opción 2: Modificar estructura (tabla con datos)

```sql
-- Agregar columna
ALTER TABLE oil_products ADD COLUMN description TEXT;

-- Modificar columna
ALTER TABLE oil_products MODIFY COLUMN name VARCHAR(150);

-- Eliminar columna
ALTER TABLE oil_products DROP COLUMN description;

-- Renombrar columna
ALTER TABLE oil_products RENAME COLUMN priceXUnd TO unitPrice;
```

**Por ahora:** Si la tabla está vacía, usa DROP TABLE y recréala. Es más simple.

---

## 5. INSERT: Insertando Datos

### 5.1 Sintaxis Básica

```sql
INSERT INTO nombre_tabla (columna1, columna2, columna3)
VALUES (valor1, valor2, valor3);
```

**Ejemplo:**

```sql
INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd)
VALUES ('PROD-00000001', 'Diesel', 'LITRO', 5000.0);
```

**Salida:**
`Query OK, 1 row affected (0.01 sec)`

### 5.2 INSERT sin Especificar Columnas

Si insertas **todas las columnas en orden**, puedes omitir la lista:

```sql
INSERT INTO oil_products
VALUES ('PROD-00000002', 'Gasolina Corriente', 'LITRO', 4500.0);
```

**⚠️ Riesgo:** Si la estructura de la tabla cambia (agregan columnas), este INSERT fallará.

**Recomendación:** Siempre especifica las columnas por claridad.

### 5.3 INSERT Múltiple (Recomendado)

Puedes insertar múltiples filas en un solo comando:

```sql
INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd)
VALUES
    ('PROD-00000001', 'Diesel Premium S50', 'LITRO', 5200.0),
    ('PROD-00000002', 'Diesel Corriente', 'LITRO', 4800.0),
    ('PROD-00000003', 'Gasolina Extra', 'LITRO', 4900.0),
    ('PROD-00000004', 'Gasolina Corriente', 'LITRO', 4500.0),
    ('PROD-00000005', 'Kerosene', 'LITRO', 3800.0);
```

**Ventajas:**
- ✅ Más rápido (una sola transacción)
- ✅ Menos comandos
- ✅ Más legible

### 5.4 INSERT con Valores Parciales

Gracias al DEFAULT en `unidadDeMedida`, podemos omitirlo:

```sql
INSERT INTO oil_products (id, name, priceXUnd)
VALUES ('PROD-00000006', 'ACPM', 4700.0);
-- unidadDeMedida se establece automáticamente en 'LITRO'
```

**Verificar:**

```sql
SELECT * FROM oil_products WHERE id = 'PROD-00000006';
```

**Salida:**

```
+----------------+------+----------------+-----------+
| id             | name | unidadDeMedida | priceXUnd |
+----------------+------+----------------+-----------+
| PROD-00000006  | ACPM | LITRO          |    4700.0 |
+----------------+------+----------------+-----------+
```

### 5.5 Poblando la Tabla con Productos Reales de Forestech

Ejecuta este bloque completo para insertar 12 productos combustibles:

```sql
INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd)
VALUES
    -- Diesel
    ('PROD-00000001', 'Diesel Premium S50', 'LITRO', 5200.00),
    ('PROD-00000002', 'Diesel Corriente', 'LITRO', 4800.00),
    ('PROD-00000003', 'ACPM (Diesel Automotor)', 'LITRO', 4700.00),

    -- Gasolina
    ('PROD-00000004', 'Gasolina Extra 95 Octanos', 'LITRO', 4950.00),
    ('PROD-00000005', 'Gasolina Corriente 87 Octanos', 'LITRO', 4500.00),
    ('PROD-00000006', 'Gasolina Premium 98 Octanos', 'LITRO', 5300.00),

    -- Otros combustibles
    ('PROD-00000007', 'Kerosene', 'LITRO', 3800.00),
    ('PROD-00000008', 'Jet Fuel A1 (Aviación)', 'LITRO', 6500.00),
    ('PROD-00000009', 'Fuel Oil No. 6', 'BARRIL', 320000.00),
    ('PROD-00000010', 'GLP (Gas Licuado Petróleo)', 'GALON', 2200.00),

    -- Biocombustibles
    ('PROD-00000011', 'Biodiesel B10', 'LITRO', 4600.00),
    ('PROD-00000012', 'Etanol Carburante E10', 'LITRO', 3900.00);
```

**Salida:**
`Query OK, 12 rows affected (0.02 sec)`

### 5.6 Verificar Inserción

```sql
SELECT COUNT(*) AS total_productos FROM oil_products;
```

**Salida:**

```
+-----------------+
| total_productos |
+-----------------+
|              12 |
+-----------------+
```

### 5.7 Errores Comunes al Insertar

#### Error 1: Duplicate PRIMARY KEY

```sql
-- Primera inserción OK
INSERT INTO oil_products VALUES ('PROD-001', 'Diesel', 'LITRO', 5000.0);

-- Segunda inserción FALLA
INSERT INTO oil_products VALUES ('PROD-001', 'Gasolina', 'LITRO', 4500.0);
```

**Error:**
`ERROR 1062 (23000): Duplicate entry 'PROD-001' for key 'PRIMARY'`

**Solución:** Cambia el ID a uno único.

#### Error 2: NOT NULL Violation

```sql
INSERT INTO oil_products (id, unidadDeMedida, priceXUnd)
VALUES ('PROD-013', 'LITRO', 4000.0);
-- Falta 'name' que es NOT NULL
```

**Error:**
`ERROR 1364 (HY000): Field 'name' doesn't have a default value`

**Solución:** Incluye todos los campos NOT NULL.

#### Error 3: Tipo de Dato Incorrecto

```sql
INSERT INTO oil_products VALUES ('PROD-014', 'Diesel', 'LITRO', 'cinco mil');
-- priceXUnd debe ser DOUBLE, no string
```

**Error:**
`ERROR 1366 (HY000): Incorrect double value: 'cinco mil' for column 'priceXUnd'`

**Solución:** Usa valor numérico: `5000.0`

---

## 6. SELECT: Consultando Datos

### 6.1 Sintaxis Básica

```sql
SELECT columnas FROM tabla;
```

### 6.2 SELECT * (Todas las columnas)

```sql
SELECT * FROM oil_products;
```

**Salida (primeras 3 filas):**

```
+----------------+-------------------------------+----------------+-----------+
| id             | name                          | unidadDeMedida | priceXUnd |
+----------------+-------------------------------+----------------+-----------+
| PROD-00000001  | Diesel Premium S50            | LITRO          |    5200.0 |
| PROD-00000002  | Diesel Corriente              | LITRO          |    4800.0 |
| PROD-00000003  | ACPM (Diesel Automotor)       | LITRO          |    4700.0 |
...
+----------------+-------------------------------+----------------+-----------+
12 rows in set (0.00 sec)
```

**⚠️ Recomendación:** En aplicaciones reales, evita `SELECT *` por:
- Consume más ancho de banda
- Rompe código si agregas columnas
- Menos legible

**Mejor práctica:**

```sql
SELECT id, name, priceXUnd FROM oil_products;
```

### 6.3 SELECT Columnas Específicas

```sql
SELECT name, priceXUnd FROM oil_products;
```

**Salida:**

```
+-------------------------------+-----------+
| name                          | priceXUnd |
+-------------------------------+-----------+
| Diesel Premium S50            |    5200.0 |
| Gasolina Extra 95 Octanos     |    4950.0 |
...
+-------------------------------+-----------+
```

### 6.4 SELECT con Alias (AS)

Renombra columnas en el resultado (no cambia la tabla):

```sql
SELECT
    name AS producto,
    priceXUnd AS precio,
    unidadDeMedida AS unidad
FROM oil_products;
```

**Salida:**

```
+-------------------------------+--------+--------+
| producto                      | precio | unidad |
+-------------------------------+--------+--------+
| Diesel Premium S50            | 5200.0 | LITRO  |
...
+-------------------------------+--------+--------+
```

**Útil para:**
- Traducir nombres técnicos a amigables
- Preparar datos para reportes
- Compatibilidad con código Java que espera nombres específicos

### 6.5 SELECT con Cálculos

```sql
SELECT
    name,
    priceXUnd,
    priceXUnd * 1.19 AS precioConIVA
FROM oil_products;
```

**Salida:**

```
+-------------------------------+-----------+---------------+
| name                          | priceXUnd | precioConIVA  |
+-------------------------------+-----------+---------------+
| Diesel Premium S50            |    5200.0 |       6188.0  |
| Gasolina Corriente 87 Octanos |    4500.0 |       5355.0  |
...
+-------------------------------+-----------+---------------+
```

**Operadores aritméticos:**
- `+` Suma
- `-` Resta
- `*` Multiplicación
- `/` División
- `%` Módulo (resto)

### 6.6 SELECT DISTINCT (Valores Únicos)

```sql
SELECT DISTINCT unidadDeMedida FROM oil_products;
```

**Salida:**

```
+----------------+
| unidadDeMedida |
+----------------+
| LITRO          |
| BARRIL         |
| GALON          |
+----------------+
3 rows in set (0.00 sec)
```

**Uso:** Obtener lista de valores únicos sin repeticiones.

---

## 7. Filtros con WHERE

### 7.1 Sintaxis

```sql
SELECT columnas FROM tabla WHERE condicion;
```

### 7.2 Operadores de Comparación

| Operador | Significado | Ejemplo |
|----------|-------------|---------|
| `=` | Igual | `priceXUnd = 5000.0` |
| `!=` o `<>` | Diferente | `unidadDeMedida != 'LITRO'` |
| `>` | Mayor que | `priceXUnd > 5000.0` |
| `<` | Menor que | `priceXUnd < 4500.0` |
| `>=` | Mayor o igual | `priceXUnd >= 4800.0` |
| `<=` | Menor o igual | `priceXUnd <= 5200.0` |

### 7.3 Ejemplos Básicos

#### Filtrar por precio exacto

```sql
SELECT * FROM oil_products WHERE priceXUnd = 4500.0;
```

**Salida:**

```
+----------------+-------------------------------+----------------+-----------+
| id             | name                          | unidadDeMedida | priceXUnd |
+----------------+-------------------------------+----------------+-----------+
| PROD-00000005  | Gasolina Corriente 87 Octanos | LITRO          |    4500.0 |
+----------------+-------------------------------+----------------+-----------+
```

#### Filtrar por rango de precio

```sql
SELECT name, priceXUnd
FROM oil_products
WHERE priceXUnd > 5000.0;
```

**Salida:**

```
+-------------------------------+-----------+
| name                          | priceXUnd |
+-------------------------------+-----------+
| Diesel Premium S50            |    5200.0 |
| Gasolina Premium 98 Octanos   |    5300.0 |
| Jet Fuel A1 (Aviación)        |    6500.0 |
| Fuel Oil No. 6                |  320000.0 |
+-------------------------------+-----------+
```

#### Filtrar por unidad de medida

```sql
SELECT name, unidadDeMedida
FROM oil_products
WHERE unidadDeMedida = 'BARRIL';
```

**Salida:**

```
+----------------+----------------+
| name           | unidadDeMedida |
+----------------+----------------+
| Fuel Oil No. 6 | BARRIL         |
+----------------+----------------+
```

### 7.4 Operadores Lógicos (AND, OR, NOT)

#### AND (Ambas condiciones deben cumplirse)

```sql
SELECT name, priceXUnd, unidadDeMedida
FROM oil_products
WHERE priceXUnd > 4000.0 AND unidadDeMedida = 'LITRO';
```

**Salida:** Productos en litros con precio mayor a 4000.

```
+-------------------------------+-----------+----------------+
| name                          | priceXUnd | unidadDeMedida |
+-------------------------------+-----------+----------------+
| Diesel Premium S50            |    5200.0 | LITRO          |
| Diesel Corriente              |    4800.0 | LITRO          |
| ACPM (Diesel Automotor)       |    4700.0 | LITRO          |
| Gasolina Extra 95 Octanos     |    4950.0 | LITRO          |
| Gasolina Corriente 87 Octanos |    4500.0 | LITRO          |
| Gasolina Premium 98 Octanos   |    5300.0 | LITRO          |
| Biodiesel B10                 |    4600.0 | LITRO          |
| Etanol Carburante E10         |    3900.0 | LITRO          | ← Este NO aparece (< 4000)
...
+-------------------------------+-----------+----------------+
```

#### OR (Al menos una condición debe cumplirse)

```sql
SELECT name, priceXUnd
FROM oil_products
WHERE priceXUnd < 4000.0 OR priceXUnd > 6000.0;
```

**Salida:** Productos muy baratos o muy caros.

```
+------------------------+-----------+
| name                   | priceXUnd |
+------------------------+-----------+
| Kerosene               |    3800.0 |
| Jet Fuel A1 (Aviación) |    6500.0 |
| Fuel Oil No. 6         |  320000.0 |
| Etanol Carburante E10  |    3900.0 |
+------------------------+-----------+
```

#### NOT (Niega la condición)

```sql
SELECT name, unidadDeMedida
FROM oil_products
WHERE NOT unidadDeMedida = 'LITRO';
-- Equivalente a: WHERE unidadDeMedida != 'LITRO'
```

**Salida:**

```
+-------------------------+----------------+
| name                    | unidadDeMedida |
+-------------------------+----------------+
| Fuel Oil No. 6          | BARRIL         |
| GLP (Gas Licuado Petró) | GALON          |
+-------------------------+----------------+
```

#### Combinación con Paréntesis

```sql
SELECT name, priceXUnd, unidadDeMedida
FROM oil_products
WHERE (priceXUnd > 5000.0 AND unidadDeMedida = 'LITRO')
   OR unidadDeMedida = 'BARRIL';
```

**Salida:** Productos en litros caros O cualquier producto en barriles.

### 7.5 BETWEEN (Rango Inclusivo)

```sql
SELECT name, priceXUnd
FROM oil_products
WHERE priceXUnd BETWEEN 4500.0 AND 5000.0;
-- Equivalente a: priceXUnd >= 4500.0 AND priceXUnd <= 5000.0
```

**Salida:**

```
+-------------------------------+-----------+
| name                          | priceXUnd |
+-------------------------------+-----------+
| Diesel Corriente              |    4800.0 |
| ACPM (Diesel Automotor)       |    4700.0 |
| Gasolina Extra 95 Octanos     |    4950.0 |
| Gasolina Corriente 87 Octanos |    4500.0 |
| Biodiesel B10                 |    4600.0 |
+-------------------------------+-----------+
```

### 7.6 IN (Coincide con Lista de Valores)

```sql
SELECT name, unidadDeMedida
FROM oil_products
WHERE unidadDeMedida IN ('BARRIL', 'GALON');
-- Equivalente a: unidadDeMedida = 'BARRIL' OR unidadDeMedida = 'GALON'
```

**Salida:**

```
+-------------------------+----------------+
| name                    | unidadDeMedida |
+-------------------------+----------------+
| Fuel Oil No. 6          | BARRIL         |
| GLP (Gas Licuado Petró) | GALON          |
+-------------------------+----------------+
```

### 7.7 IS NULL / IS NOT NULL

```sql
-- Crear columna opcional para este ejemplo
ALTER TABLE oil_products ADD COLUMN description VARCHAR(255);

-- Insertar producto con descripción NULL
INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd)
VALUES ('PROD-00000013', 'Producto Sin Descripción', 'LITRO', 4000.0);

-- Buscar productos sin descripción
SELECT name FROM oil_products WHERE description IS NULL;
```

**Salida:**

```
+-------------------------------+
| name                          |
+-------------------------------+
| Diesel Premium S50            |
| Diesel Corriente              |
... (todos los productos)
| Producto Sin Descripción      |
+-------------------------------+
```

**Importante:** Para comparar NULL, usa `IS NULL` o `IS NOT NULL`, nunca `= NULL`.

```sql
-- ❌ INCORRECTO (siempre retorna 0 filas)
SELECT * FROM oil_products WHERE description = NULL;

-- ✅ CORRECTO
SELECT * FROM oil_products WHERE description IS NULL;
```

**Limpieza del ejemplo:**

```sql
DELETE FROM oil_products WHERE id = 'PROD-00000013';
ALTER TABLE oil_products DROP COLUMN description;
```

---

## 8. Ordenamiento con ORDER BY

### 8.1 Sintaxis

```sql
SELECT columnas FROM tabla ORDER BY columna [ASC|DESC];
```

- `ASC` = Ascendente (menor a mayor) - **por defecto**
- `DESC` = Descendente (mayor a menor)

### 8.2 Ordenar por Precio (Ascendente)

```sql
SELECT name, priceXUnd
FROM oil_products
ORDER BY priceXUnd ASC;
-- o simplemente: ORDER BY priceXUnd
```

**Salida:**

```
+-------------------------------+-----------+
| name                          | priceXUnd |
+-------------------------------+-----------+
| Kerosene                      |    3800.0 |
| Etanol Carburante E10         |    3900.0 |
| GLP (Gas Licuado Petróleo)    |    2200.0 |
| Gasolina Corriente 87 Octanos |    4500.0 |
| Biodiesel B10                 |    4600.0 |
| ACPM (Diesel Automotor)       |    4700.0 |
| Diesel Corriente              |    4800.0 |
| Gasolina Extra 95 Octanos     |    4950.0 |
| Diesel Premium S50            |    5200.0 |
| Gasolina Premium 98 Octanos   |    5300.0 |
| Jet Fuel A1 (Aviación)        |    6500.0 |
| Fuel Oil No. 6                |  320000.0 |
+-------------------------------+-----------+
```

### 8.3 Ordenar por Precio (Descendente)

```sql
SELECT name, priceXUnd
FROM oil_products
ORDER BY priceXUnd DESC;
```

**Salida:** El más caro primero (Fuel Oil No. 6).

### 8.4 Ordenar Alfabéticamente por Nombre

```sql
SELECT name FROM oil_products ORDER BY name ASC;
```

**Salida:**

```
+-------------------------------+
| name                          |
+-------------------------------+
| ACPM (Diesel Automotor)       |
| Biodiesel B10                 |
| Diesel Corriente              |
| Diesel Premium S50            |
| Etanol Carburante E10         |
| Fuel Oil No. 6                |
| Gasolina Corriente 87 Octanos |
| Gasolina Extra 95 Octanos     |
| Gasolina Premium 98 Octanos   |
| GLP (Gas Licuado Petróleo)    |
| Jet Fuel A1 (Aviación)        |
| Kerosene                      |
+-------------------------------+
```

### 8.5 Ordenar por Múltiples Columnas

```sql
SELECT name, unidadDeMedida, priceXUnd
FROM oil_products
ORDER BY unidadDeMedida ASC, priceXUnd DESC;
```

**Lógica:**
1. Primero ordena por `unidadDeMedida` alfabéticamente
2. Dentro de cada unidad, ordena por `priceXUnd` de mayor a menor

**Salida:**

```
+-------------------------------+----------------+-----------+
| name                          | unidadDeMedida | priceXUnd |
+-------------------------------+----------------+-----------+
| Fuel Oil No. 6                | BARRIL         |  320000.0 |  ← Único BARRIL
| GLP (Gas Licuado Petróleo)    | GALON          |    2200.0 |  ← Único GALON
| Jet Fuel A1 (Aviación)        | LITRO          |    6500.0 |  ← LITRO más caro
| Gasolina Premium 98 Octanos   | LITRO          |    5300.0 |
| Diesel Premium S50            | LITRO          |    5200.0 |
| Gasolina Extra 95 Octanos     | LITRO          |    4950.0 |
| Diesel Corriente              | LITRO          |    4800.0 |
| ACPM (Diesel Automotor)       | LITRO          |    4700.0 |
| Biodiesel B10                 | LITRO          |    4600.0 |
| Gasolina Corriente 87 Octanos | LITRO          |    4500.0 |
| Etanol Carburante E10         | LITRO          |    3900.0 |
| Kerosene                      | LITRO          |    3800.0 |  ← LITRO más barato
+-------------------------------+----------------+-----------+
```

### 8.6 LIMIT (Limitar Resultados)

```sql
-- Top 3 productos más caros
SELECT name, priceXUnd
FROM oil_products
ORDER BY priceXUnd DESC
LIMIT 3;
```

**Salida:**

```
+-------------------------+-----------+
| name                    | priceXUnd |
+-------------------------+-----------+
| Fuel Oil No. 6          |  320000.0 |
| Jet Fuel A1 (Aviación)  |    6500.0 |
| Gasolina Premium 98 Oct |    5300.0 |
+-------------------------+-----------+
```

**LIMIT con OFFSET (Paginación):**

```sql
-- Saltar los primeros 3 resultados y mostrar los siguientes 5
SELECT name, priceXUnd
FROM oil_products
ORDER BY priceXUnd DESC
LIMIT 5 OFFSET 3;
-- Sintaxis alternativa: LIMIT 3, 5
```

**Uso común:** Paginación en aplicaciones web (página 1, página 2, etc.).

---

## 9. Búsquedas con LIKE

### 9.1 Sintaxis y Comodines

```sql
SELECT columnas FROM tabla WHERE columna LIKE patron;
```

**Comodines:**
- `%` = Cualquier cantidad de caracteres (incluyendo cero)
- `_` = Exactamente un carácter

### 9.2 Buscar Productos que Contienen "Diesel"

```sql
SELECT name FROM oil_products WHERE name LIKE '%Diesel%';
```

**Salida:**

```
+-------------------------+
| name                    |
+-------------------------+
| Diesel Premium S50      |
| Diesel Corriente        |
| ACPM (Diesel Automotor) |
| Biodiesel B10           |
+-------------------------+
```

**Explicación:** `%Diesel%` coincide con cualquier texto antes y después de "Diesel".

### 9.3 Buscar Productos que Empiezan con "Gasolina"

```sql
SELECT name FROM oil_products WHERE name LIKE 'Gasolina%';
```

**Salida:**

```
+-------------------------------+
| name                          |
+-------------------------------+
| Gasolina Extra 95 Octanos     |
| Gasolina Corriente 87 Octanos |
| Gasolina Premium 98 Octanos   |
+-------------------------------+
```

### 9.4 Buscar Productos que Terminan con "E10"

```sql
SELECT name FROM oil_products WHERE name LIKE '%E10';
```

**Salida:**

```
+-----------------------+
| name                  |
+-----------------------+
| Biodiesel B10         |
| Etanol Carburante E10 |
+-----------------------+
```

### 9.5 Buscar con Patrón de Longitud Exacta

```sql
-- Buscar IDs con formato PROD-0000000X (último dígito cualquiera)
SELECT id, name FROM oil_products WHERE id LIKE 'PROD-0000000_';
```

**Salida:**

```
+----------------+-------------------------------+
| id             | name                          |
+----------------+-------------------------------+
| PROD-00000001  | Diesel Premium S50            |
| PROD-00000002  | Diesel Corriente              |
| PROD-00000003  | ACPM (Diesel Automotor)       |
| PROD-00000004  | Gasolina Extra 95 Octanos     |
| PROD-00000005  | Gasolina Corriente 87 Octanos |
| PROD-00000006  | Gasolina Premium 98 Octanos   |
| PROD-00000007  | Kerosene                      |
| PROD-00000008  | Jet Fuel A1 (Aviación)        |
| PROD-00000009  | Fuel Oil No. 6                |
+----------------+-------------------------------+
```

**Nota:** `PROD-00000010`, `PROD-00000011`, `PROD-00000012` no coinciden porque tienen 2 dígitos al final.

### 9.6 Case Sensitivity

Por defecto, LIKE en MySQL es **case-insensitive** (no distingue mayúsculas/minúsculas):

```sql
SELECT name FROM oil_products WHERE name LIKE '%diesel%';
-- Encuentra "Diesel", "diesel", "DIESEL"
```

**Para búsqueda case-sensitive:**

```sql
SELECT name FROM oil_products WHERE name LIKE BINARY '%Diesel%';
-- Solo encuentra "Diesel" con D mayúscula
```

### 9.7 Combinar LIKE con AND/OR

```sql
-- Productos que contienen "Gasolina" O "Diesel" en el nombre
SELECT name FROM oil_products
WHERE name LIKE '%Gasolina%' OR name LIKE '%Diesel%';
```

**Salida:**

```
+-------------------------------+
| name                          |
+-------------------------------+
| Diesel Premium S50            |
| Diesel Corriente              |
| ACPM (Diesel Automotor)       |
| Gasolina Extra 95 Octanos     |
| Gasolina Corriente 87 Octanos |
| Gasolina Premium 98 Octanos   |
| Biodiesel B10                 |
+-------------------------------+
```

---

## 10. Funciones de Agregación

### 10.1 ¿Qué son las Funciones de Agregación?

Funciones que **operan sobre un conjunto de filas** y retornan un **único valor**.

**Funciones principales:**
- `COUNT()` - Contar filas
- `SUM()` - Sumar valores
- `AVG()` - Promedio
- `MAX()` - Valor máximo
- `MIN()` - Valor mínimo

### 10.2 COUNT() - Contar Registros

#### Contar total de productos

```sql
SELECT COUNT(*) AS total_productos FROM oil_products;
```

**Salida:**

```
+-----------------+
| total_productos |
+-----------------+
|              12 |
+-----------------+
```

#### Contar productos por unidad de medida

```sql
SELECT COUNT(*) AS productos_en_litros
FROM oil_products
WHERE unidadDeMedida = 'LITRO';
```

**Salida:**

```
+---------------------+
| productos_en_litros |
+---------------------+
|                  10 |
+---------------------+
```

#### COUNT(columna) vs COUNT(*)

```sql
-- COUNT(*): cuenta todas las filas
SELECT COUNT(*) FROM oil_products;

-- COUNT(columna): cuenta filas donde columna NO es NULL
SELECT COUNT(name) FROM oil_products;
-- Si todos los name son NOT NULL, resultado idéntico
```

### 10.3 SUM() - Suma de Valores

```sql
-- Suma total de precios (no tiene sentido real, solo ejemplo)
SELECT SUM(priceXUnd) AS suma_precios FROM oil_products;
```

**Salida:**

```
+--------------+
| suma_precios |
+--------------+
|    379050.00 |
+--------------+
```

**Uso real:** Sumar cantidades de movimientos (lo veremos en Fase 03.5).

### 10.4 AVG() - Promedio

```sql
SELECT AVG(priceXUnd) AS precio_promedio FROM oil_products;
```

**Salida:**

```
+-----------------+
| precio_promedio |
+-----------------+
|     31587.50    |
+-----------------+
```

**Redondear con ROUND():**

```sql
SELECT ROUND(AVG(priceXUnd), 2) AS precio_promedio_redondeado
FROM oil_products;
```

**Salida:**

```
+----------------------------+
| precio_promedio_redondeado |
+----------------------------+
|                   31587.50 |
+----------------------------+
```

### 10.5 MAX() y MIN()

```sql
-- Producto más caro
SELECT MAX(priceXUnd) AS precio_maximo FROM oil_products;

-- Producto más barato
SELECT MIN(priceXUnd) AS precio_minimo FROM oil_products;
```

**Salida:**

```
+---------------+
| precio_maximo |
+---------------+
|    320000.00  |
+---------------+

+---------------+
| precio_minimo |
+---------------+
|      2200.00  |
+---------------+
```

**Obtener el nombre del producto más caro:**

```sql
SELECT name, priceXUnd
FROM oil_products
WHERE priceXUnd = (SELECT MAX(priceXUnd) FROM oil_products);
```

**Salida:**

```
+----------------+-----------+
| name           | priceXUnd |
+----------------+-----------+
| Fuel Oil No. 6 |  320000.0 |
+----------------+-----------+
```

**Nota:** Esta es una subconsulta (subquery), tema avanzado que veremos brevemente.

### 10.6 GROUP BY (Agrupar Resultados)

**Propósito:** Agrupar filas que comparten un valor común y aplicar funciones de agregación a cada grupo.

```sql
SELECT
    unidadDeMedida,
    COUNT(*) AS cantidad_productos,
    AVG(priceXUnd) AS precio_promedio
FROM oil_products
GROUP BY unidadDeMedida;
```

**Salida:**

```
+----------------+---------------------+-----------------+
| unidadDeMedida | cantidad_productos  | precio_promedio |
+----------------+---------------------+-----------------+
| LITRO          |                  10 |         4885.00 |
| BARRIL         |                   1 |       320000.00 |
| GALON          |                   1 |         2200.00 |
+----------------+---------------------+-----------------+
```

**Explicación:**
1. Agrupa productos por `unidadDeMedida`
2. Para cada grupo, cuenta productos y calcula precio promedio

**Regla importante:** Si usas `GROUP BY`, todas las columnas en `SELECT` deben ser:
- Parte del `GROUP BY`, O
- Dentro de una función de agregación

```sql
-- ❌ INCORRECTO: name no está en GROUP BY ni en función
SELECT unidadDeMedida, name, COUNT(*)
FROM oil_products
GROUP BY unidadDeMedida;

-- ✅ CORRECTO: Solo columnas agrupadas y agregadas
SELECT unidadDeMedida, COUNT(*) AS total
FROM oil_products
GROUP BY unidadDeMedida;
```

### 10.7 HAVING (Filtrar Grupos)

Similar a `WHERE`, pero para **filtrar después de agrupar**.

```sql
-- Unidades de medida con más de 1 producto
SELECT
    unidadDeMedida,
    COUNT(*) AS cantidad
FROM oil_products
GROUP BY unidadDeMedida
HAVING COUNT(*) > 1;
```

**Salida:**

```
+----------------+----------+
| unidadDeMedida | cantidad |
+----------------+----------+
| LITRO          |       10 |
+----------------+----------+
```

**Diferencia WHERE vs HAVING:**

```sql
-- WHERE: filtra ANTES de agrupar
SELECT unidadDeMedida, COUNT(*) AS total
FROM oil_products
WHERE priceXUnd > 5000.0  -- ← Filtra productos primero
GROUP BY unidadDeMedida;

-- HAVING: filtra DESPUÉS de agrupar
SELECT unidadDeMedida, COUNT(*) AS total
FROM oil_products
GROUP BY unidadDeMedida
HAVING COUNT(*) > 5;  -- ← Filtra grupos
```

---

## 11. Ejercicios Prácticos Progresivos

### 📝 Ejercicio 1: SELECT Básico

**Objetivo:** Practicar consultas simples.

#### Parte A: Listar nombres y precios

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name, priceXUnd FROM oil_products;
```
</details>

#### Parte B: Calcular precio con IVA (19%)

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT
    name,
    priceXUnd AS precio_base,
    ROUND(priceXUnd * 1.19, 2) AS precio_con_iva
FROM oil_products;
```
</details>

---

### 📝 Ejercicio 2: Filtros con WHERE

**Objetivo:** Practicar condiciones de filtrado.

#### Parte A: Productos en LITRO con precio entre 4500 y 5000

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name, priceXUnd, unidadDeMedida
FROM oil_products
WHERE unidadDeMedida = 'LITRO'
  AND priceXUnd BETWEEN 4500.0 AND 5000.0;
```
</details>

#### Parte B: Productos que NO son en LITRO

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name, unidadDeMedida
FROM oil_products
WHERE unidadDeMedida != 'LITRO';
-- O: WHERE NOT unidadDeMedida = 'LITRO'
```
</details>

---

### 📝 Ejercicio 3: ORDER BY

**Objetivo:** Practicar ordenamiento.

#### Parte A: Top 5 productos más caros

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name, priceXUnd
FROM oil_products
ORDER BY priceXUnd DESC
LIMIT 5;
```
</details>

#### Parte B: Productos ordenados alfabéticamente (Z-A)

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name FROM oil_products ORDER BY name DESC;
```
</details>

---

### 📝 Ejercicio 4: LIKE

**Objetivo:** Practicar búsquedas por patrón.

#### Parte A: Productos que contienen "Premium"

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name FROM oil_products WHERE name LIKE '%Premium%';
```
</details>

#### Parte B: Productos cuyo nombre empieza con vocal (A, E, I)

**Pista:** Usa múltiples LIKE con OR.

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT name FROM oil_products
WHERE name LIKE 'A%'
   OR name LIKE 'E%'
   OR name LIKE 'I%';
```
</details>

---

### 📝 Ejercicio 5: COUNT

**Objetivo:** Practicar conteo de registros.

#### Parte A: ¿Cuántos productos hay por cada unidad de medida?

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT
    unidadDeMedida,
    COUNT(*) AS total_productos
FROM oil_products
GROUP BY unidadDeMedida;
```
</details>

#### Parte B: ¿Cuántos productos tienen precio menor a 5000?

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT COUNT(*) AS productos_economicos
FROM oil_products
WHERE priceXUnd < 5000.0;
```
</details>

---

### 📝 Ejercicio 6: AVG y Agrupación

**Objetivo:** Practicar promedios y GROUP BY.

#### Parte A: Precio promedio por unidad de medida

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT
    unidadDeMedida,
    ROUND(AVG(priceXUnd), 2) AS precio_promedio
FROM oil_products
GROUP BY unidadDeMedida;
```
</details>

#### Parte B: Precio promedio solo de productos en LITRO

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT ROUND(AVG(priceXUnd), 2) AS precio_promedio_litros
FROM oil_products
WHERE unidadDeMedida = 'LITRO';
```
</details>

---

### 📝 Ejercicio 7: Query Compleja Integradora

**Objetivo:** Combinar múltiples conceptos.

**Enunciado:** Crear una consulta que muestre:
- Nombre del producto
- Precio con IVA (19%)
- Solo productos que:
  - Contienen "Gasolina" o "Diesel" en el nombre
  - Precio base mayor a 4000
- Ordenados por precio con IVA descendente
- Limitado a 5 resultados

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT
    name AS producto,
    priceXUnd AS precio_base,
    ROUND(priceXUnd * 1.19, 2) AS precio_con_iva
FROM oil_products
WHERE (name LIKE '%Gasolina%' OR name LIKE '%Diesel%')
  AND priceXUnd > 4000.0
ORDER BY precio_con_iva DESC
LIMIT 5;
```
</details>

**Salida esperada:**

```
+-------------------------------+-------------+----------------+
| producto                      | precio_base | precio_con_iva |
+-------------------------------+-------------+----------------+
| Gasolina Premium 98 Octanos   |    5300.00  |        6307.00 |
| Diesel Premium S50            |    5200.00  |        6188.00 |
| Gasolina Extra 95 Octanos     |    4950.00  |        5890.50 |
| Diesel Corriente              |    4800.00  |        5712.00 |
| ACPM (Diesel Automotor)       |    4700.00  |        5593.00 |
+-------------------------------+-------------+----------------+
```

---

### 📝 Ejercicio 8: Investigación Autónoma

**Objetivo:** Aprender a explorar funciones nuevas.

#### Parte A: Función CONCAT()

Investiga cómo funciona `CONCAT()` y crea una query que muestre:
`"El producto [nombre] cuesta $[precio] por [unidad]"`

**Ejemplo de salida:**
`"El producto Diesel Premium S50 cuesta $5200.0 por LITRO"`

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT CONCAT(
    'El producto ',
    name,
    ' cuesta $',
    priceXUnd,
    ' por ',
    unidadDeMedida
) AS descripcion_completa
FROM oil_products;
```
</details>

#### Parte B: Función UPPER() y LOWER()

Muestra nombres de productos en MAYÚSCULAS y precios.

```sql
-- Tu query aquí
```

<details>
<summary>💡 Solución</summary>

```sql
SELECT UPPER(name) AS nombre_mayusculas, priceXUnd
FROM oil_products;
```
</details>

---

## 12. Git Checkpoint

### 12.1 Documentar Ejercicios

Antes de hacer commit, crea un archivo con las queries que ejecutaste:

```bash
cd /home/hp/forestechOil
touch fase_03.2_queries_ejecutadas.sql
```

**Contenido del archivo:**

```sql
-- FASE 03.2 - Queries Ejecutadas
-- Fecha: 2025-01-15
-- Alumno: [Tu nombre]

-- Creación de tabla
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unidadDeMedida VARCHAR(20) NOT NULL DEFAULT 'LITRO',
    priceXUnd DOUBLE NOT NULL
);

-- Inserción de datos
INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd)
VALUES
    ('PROD-00000001', 'Diesel Premium S50', 'LITRO', 5200.00),
    ('PROD-00000002', 'Diesel Corriente', 'LITRO', 4800.00),
    -- ... (resto de productos)
;

-- Ejercicio 1: SELECT Básico
SELECT name, priceXUnd FROM oil_products;

-- Ejercicio 2: Filtros
SELECT name, priceXUnd, unidadDeMedida
FROM oil_products
WHERE unidadDeMedida = 'LITRO'
  AND priceXUnd BETWEEN 4500.0 AND 5000.0;

-- ... (resto de ejercicios)

-- Reflexión:
-- Lo que más me costó: [Completa]
-- Lo que mejor entendí: [Completa]
-- Preguntas pendientes: [Completa]
```

### 12.2 Comandos Git

```bash
# Estado actual
git status

# Agregar archivos
git add roadmaps/FASE_03.2_SQL_DDL_DML_BASICO.md
git add fase_03.2_queries_ejecutadas.sql

# Commit
git commit -m "fase 3.2 checkpoint: DDL y DML básico completados - tabla oil_products creada con 12 productos"

# Ver historial
git log --oneline -5
```

---

## 13. Generador de Quiz de Validación

### 📋 Mini-Prompt para Claude/ChatGPT

```
Genera un quiz práctico en formato markdown con 6 preguntas SQL sobre Fase 03.2 del proyecto Forestech (DDL y DML básico).

Contexto del estudiante:
- Ha creado la tabla oil_products (id, name, unidadDeMedida, priceXUnd)
- Ha insertado 12 productos combustibles
- Conoce: CREATE TABLE, INSERT, SELECT, WHERE, ORDER BY, LIKE, COUNT, AVG
- Está aprendiendo Java desde cero, SQL es nuevo

Estructura requerida para cada pregunta:

## Pregunta X: [Título]

**Nivel:** [Básico | Intermedio | Avanzado]

**Escenario/Contexto:** [Si aplica]

**Enunciado:** [Descripción clara de lo que debe hacer]

**Tu Query/Respuesta:**
```sql
-- Espacio para que el estudiante escriba
```

**Query/Respuesta Correcta:**
```sql
-- Solución con comentarios explicativos
```

**Explicación Detallada:**
[Paso a paso qué hace cada parte de la query, concepto clave aprendido, errores comunes a evitar]

---

Temas específicos de las 6 preguntas:

1. **Escribir query SELECT con filtros específicos** (Intermedio)
   - Filtrar productos tipo "Diesel" (LIKE) con precio > 4500
   - Ordenar por precio descendente
   - Mostrar solo nombre y precio

2. **Completar query con COUNT y GROUP BY** (Intermedio)
   - Query incompleta que cuenta productos por unidad de medida
   - Faltan partes del GROUP BY y alias

3. **Identificar error en INSERT** (Básico)
   - Query de INSERT con error (falta NOT NULL, PK duplicada, tipo incorrecto)
   - Pedir que identifiquen el error y corrijan

4. **Crear query con AVG y ROUND** (Básico)
   - Calcular precio promedio de productos en LITRO
   - Redondear a 2 decimales
   - Usar alias descriptivo

5. **Completar CREATE TABLE con constraints** (Intermedio)
   - Proporcionar CREATE TABLE incompleto
   - Faltan: PRIMARY KEY, NOT NULL, DEFAULT
   - Pedir que completen basándose en reglas de negocio

6. **Query compleja integradora** (Avanzado)
   - Combinar: WHERE con múltiples condiciones, LIKE, BETWEEN, ORDER BY, LIMIT
   - Escenario realista de Forestech (ej: "productos entre X y Y precio que contengan Z palabra, ordenados alfabéticamente, top 3")

Generación adicional:
- Incluir sección "## Conceptos Clave Reforzados" al final del quiz listando los 5 conceptos más importantes
- Agregar "## Autoevaluación" con criterios:
  - 6/6: Excelente dominio, listo para Fase 03.3 (JDBC)
  - 4-5/6: Buen progreso, revisar temas específicos fallados
  - 0-3/6: Rehacer ejercicios de Fase 03.2, practicar más queries
```

---

## 14. Checkpoint de Fase

### ✅ Validación de Conocimientos

Antes de continuar a **Fase 03.3 (JDBC)**, debes poder responder SÍ a:

#### Conceptual

- [ ] Entiendo la diferencia entre DDL y DML
- [ ] Sé cuándo usar VARCHAR vs TEXT
- [ ] Comprendo qué es PRIMARY KEY y por qué es obligatoria
- [ ] Entiendo NOT NULL, UNIQUE, DEFAULT
- [ ] Sé qué hacen COUNT, AVG, MAX, MIN
- [ ] Comprendo cuándo usar WHERE vs HAVING

#### Práctico

- [ ] Creé exitosamente la tabla oil_products
- [ ] Inserté 12+ productos con INSERT múltiple
- [ ] Ejecuté queries SELECT con filtros complejos
- [ ] Usé ORDER BY con múltiples columnas
- [ ] Practiqué LIKE con diferentes patrones
- [ ] Calculé agregaciones con COUNT/AVG
- [ ] Agrupé resultados con GROUP BY

#### Ejercicios

- [ ] Completé los 8 ejercicios prácticos
- [ ] Documenté mis queries en archivo .sql
- [ ] Hice commit del checkpoint en Git

### 🎯 Criterio de Aprobación

**Mínimo requerido:** 16/19 checkboxes + 4/6 en el quiz generado.

### 📝 Reflexión Final (Obligatoria)

Agrega al final de `fase_03.2_queries_ejecutadas.sql`:

```sql
/*
REFLEXIÓN FASE 03.2

1. ¿Qué concepto fue más difícil de entender?
   Respuesta: [...]

2. ¿Cómo se relaciona CREATE TABLE con la clase Product.java de Fase 2?
   Respuesta: [...]

3. ¿Qué ventajas ves en usar BD vs ArrayList en Java?
   Respuesta: [...]

4. ¿Qué esperas aprender en Fase 03.3 (Conectar Java con MySQL)?
   Respuesta: [...]
*/
```

### 🚀 Siguiente Paso

Una vez completado:

```bash
git commit -m "fase 3.2 completada: tabla oil_products operativa con queries DDL/DML"
```

**Continúa a:** `FASE_03.3_JDBC_CONEXION.md`

---

## 📚 Recursos Adicionales

### Comparativa Rápida de Tipos

```sql
-- String corto
VARCHAR(50)    -- Nombre producto

-- String largo
TEXT           -- Descripción extensa (raro en Forestech)

-- Número entero
INT            -- Contador de movimientos

-- Número decimal
DOUBLE         -- Precio, cantidad (usamos esto)
DECIMAL(10,2)  -- Precio en producción (más exacto)

-- Fecha/hora
DATE           -- Solo fecha: 2025-01-15
DATETIME       -- Fecha y hora: 2025-01-15 14:30:00
```

### Comandos Útiles de MySQL

```sql
-- Ver todas las BDs
SHOW DATABASES;

-- Cambiar a BD
USE FORESTECH;

-- Ver tablas de la BD actual
SHOW TABLES;

-- Ver estructura de tabla
DESC oil_products;

-- Ver query de creación de tabla
SHOW CREATE TABLE oil_products;

-- Eliminar todos los registros (mantiene estructura)
TRUNCATE TABLE oil_products;

-- Eliminar tabla completa
DROP TABLE oil_products;
```

### Atajos de Terminal MySQL

- `Ctrl + L` - Limpiar pantalla
- `\c` - Cancelar query en proceso
- `\q` o `exit` - Salir de MySQL
- `↑` / `↓` - Navegar historial de comandos

---

## ❓ Preguntas Frecuentes

### 1. "¿Por qué VARCHAR(20) para ID si solo usamos 15 caracteres?"

Margen de seguridad. Si en el futuro cambiamos formato de ID, no romperemos la tabla.

### 2. "¿Puedo usar AUTO_INCREMENT en vez de generar IDs en Java?"

Sí, técnicamente funciona:

```sql
id INT AUTO_INCREMENT PRIMARY KEY
```

**Pero perdemos:**
- Legibilidad (`1` vs `"PROD-00000001"`)
- Prefijos descriptivos (`MOV-`, `VEH-`)
- Control sobre formato desde Java

**Decisión:** Mantenemos IDs VARCHAR generados en Java.

### 3. "¿DOUBLE o DECIMAL para precios?"

**Fase aprendizaje (ahora):** DOUBLE (más simple)
**Producción (futuro):** DECIMAL(10, 2) (exactitud financiera)

### 4. "Error: Unknown database 'forestech'"

MySQL es case-sensitive en Linux. Verifica el nombre exacto:

```sql
SHOW DATABASES;  -- ¿Es FORESTECH o forestech?
USE FORESTECH;   -- Usa exactamente como aparece
```

### 5. "¿Puedo eliminar un producto insertado por error?"

Sí, con DELETE (lo aprenderemos en Fase 4):

```sql
DELETE FROM oil_products WHERE id = 'PROD-00000013';
```

**Por ahora:** Si insertaste mal, ejecuta:

```sql
DROP TABLE oil_products;
-- Y vuelve a crear e insertar todo
```

---

## 🎓 Conclusión

**¡Felicitaciones!** Has completado la Fase 03.2. Ahora tienes:

✅ Comprensión sólida de tipos de datos MySQL
✅ Habilidad para crear tablas con constraints apropiados
✅ Dominio de INSERT, SELECT, WHERE, ORDER BY, LIKE
✅ Conocimiento de funciones de agregación (COUNT, AVG, MAX, MIN)
✅ Tabla `oil_products` poblada y operativa

**Próximo hito:** Conectar Java con MySQL usando JDBC para que `ProductService.java` lea productos desde la base de datos en lugar de memoria.

---

**Tiempo estimado de Fase 03.2:** 3-4 horas (incluyendo ejercicios y quiz)

**Autor:** Material didáctico del Proyecto Forestech CLI
**Versión:** 1.0
**Última actualización:** Enero 2025
