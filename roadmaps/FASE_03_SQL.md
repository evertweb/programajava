# 🔌 FASE 3: CONEXIÓN A MYSQL LOCAL EN WSL (Semana 5)

> Objetivo general: instalar MySQL en tu entorno WSL (Ubuntu), comprender JDBC, conectar Java con MySQL y ejecutar las primeras consultas.

---

## 🧠 Antes de empezar

- 💿 **Instalar MySQL en WSL:** Instalarás MySQL Server en tu Ubuntu dentro de WSL
- 📚 **Fundamentos SQL:** Practicarás consultas básicas directamente en MySQL:
  - Consultas básicas `SELECT`, `INSERT`, `UPDATE`, `DELETE`
  - Conceptos de tablas, columnas, tipos de datos, PK/FK, normalización ligera
  - Cláusulas `WHERE`, `ORDER BY`, `GROUP BY`
- 🛠️ **Comandos básicos MySQL:** Aprenderás a crear bases de datos, tablas y gestionar usuarios
- 📝 Documenta en `JAVA_LEARNING_LOG.md` las consultas manuales que ejecutaste y resultados
- 🧪 Practica consultas con `FORESTECH` para tener contexto cuando programes
- 🔁 **Git loop:** al completar cada checkpoint crea un commit con mensaje claro (`git commit -m "fase3 checkpoint 3.0"`).
- 🎯 **ORGANIZACIÓN CLARA:** Introduciremos nuevos paquetes (`config`, `services`) para mantener la arquitectura profesional
- ✍️ **APRENDIZAJE ACTIVO:** Recibirás DIRECTIVAS, no código completo. TÚ escribirás y entenderás cada línea.

---

## ⚡ IMPORTANTE: Estrategia de Bases de Datos en Forestech

### 🎯 **DOS BASES DE DATOS, DOS FASES**

En este proyecto usarás **DOS bases de datos diferentes** en momentos distintos:

```
┌───────────────────────────────────────────────────┐
│ 🎓 FASE 3-5: APRENDIZAJE                          │
│                                                   │
│ Base de datos: MySQL                             │
│ Ubicación: WSL (localhost)                       │
│ Puerto: 3306                                     │
│                                                   │
│ ✅ Gratis (100%)                                  │
│ ✅ Local (sin internet)                           │
│ ✅ Control total                                  │
│ ✅ Experimentos sin miedo                         │
│ ✅ Velocidad máxima                               │
└───────────────────────────────────────────────────┘
                        ↓
          (Al terminar Fase 5: CRUD completo)
                        ↓
┌───────────────────────────────────────────────────┐
│ 🚀 FASE 6+: PRODUCCIÓN                            │
│                                                   │
│ Base de datos: SQL Server                        │
│ Ubicación: DigitalOcean (remoto)                 │
│ Host: 24.199.89.134                              │
│ Puerto: 1433                                     │
│                                                   │
│ ✅ Acceso desde cualquier lugar                   │
│ ✅ Datos persistentes en la nube                  │
│ ✅ Base de datos profesional                      │
│ ✅ Proyecto "real" y escalable                    │
└───────────────────────────────────────────────────┘
```

### 🤔 **¿Por qué empezar con MySQL y no directo con SQL Server?**

**Razones pedagógicas:**

1. **Instalación más simple:**
   - MySQL en WSL: `sudo apt install mysql-server` → Listo en 2 minutos
   - SQL Server en Windows: Descarga pesada (varios GB), configuración compleja

2. **Sin costos ni dependencias:**
   - MySQL: Gratis, funciona offline
   - SQL Server remoto: Requiere internet, eventual costo de servidor

3. **Ambiente de práctica seguro:**
   - Puedes borrar, recrear, experimentar sin consecuencias
   - No afectas datos "reales" en producción

4. **Sintaxis SQL 95% idéntica:**
   - Lo que aprendas en MySQL funciona en SQL Server
   - Solo cambiarán detalles menores (tipos de datos específicos)

5. **Migración sencilla:**
   - Solo cambiarás `DatabaseConnection.java` (URL, driver, credenciales)
   - El resto del código será IDÉNTICO

### 🎓 **Analogía:**

```
MySQL en WSL (Fase 3-5):
→ Cuaderno de práctica donde haces borradores
→ Cometes errores, tachas, corriges
→ Nadie lo ve, es TU espacio de aprendizaje

SQL Server remoto (Fase 6+):
→ Cuaderno oficial donde presentas el trabajo final
→ Datos organizados, persistentes, accesibles
→ Tu proyecto "en producción"
```

### 📋 **Guía de Migración (Fase 6)**

Cuando llegue el momento, la migración será sencilla:

**Cambio 1: Driver JDBC en pom.xml**
```xml
<!-- De: -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>

<!-- A: -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
</dependency>
```

**Cambio 2: DatabaseConnection.java**
```java
// De:
private static final String URL = "jdbc:mysql://localhost:3306/FORESTECH";

// A:
private static final String URL = "jdbc:sqlserver://24.199.89.134:1433;databaseName=DBforestech";
```

**¡Eso es todo!** El resto del código (Services, Models, Managers) NO cambia.

### ⚠️ **IMPORTANTE para Fase 3:**

**En esta Fase 3 trabajarás EXCLUSIVAMENTE con MySQL local.**

Si ves alguna referencia a:
- SQL Server
- DigitalOcean
- Puerto 1433
- SQL Server Management Studio

→ **IGNÓRALA por ahora.** Son notas para el futuro.

**Tu enfoque:**
1. Instalar MySQL en WSL ✅
2. Conectar Java con MySQL ✅
3. Dominar JDBC con queries básicas ✅
4. Prepararte para Fase 4 (CRUD) ✅

---

## 📦 ESTRUCTURA AL TERMINAR FASE 2

Antes de empezar Fase 3, debes tener esta estructura de tu proyecto:

```
com.forestech/
├── Main.java
├── AppConfig.java
├── MenuHelper.java
├── DataDisplay.java
├── InputHelper.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   └── Supplier.java
└── managers/
    └── MovementManager.java
```

**Verifica que esto esté completo antes de continuar.**

---

## ✅ Checkpoint 3.0: Instalar MySQL en WSL y Crear Base de Datos

**Concepto clave:** MySQL es un sistema de gestión de bases de datos relacional de código abierto. Es una de las bases de datos más populares del mundo y perfecta para aprender.

**📍 DÓNDE:** 
- **Instalación:** En tu entorno WSL (Ubuntu)
- **Herramienta:** MySQL Command Line Client
- **Base de datos:** FORESTECH (la crearemos)

**🎯 PARA QUÉ:** 
- ✅ **Tener control total:** Base de datos en tu entorno local, sin depender de servicios externos
- ✅ **Aprender sin límites:** Puedes crear, modificar y eliminar sin restricciones
- ✅ **Velocidad:** Sin latencia de red, todo es instantáneo
- ✅ **Gratuito:** MySQL Community Edition es totalmente gratis
- ✅ **Portabilidad:** Fácil de respaldar y restaurar
- ✅ **Integración con WSL:** Todo tu proyecto en un mismo entorno

**🎓 Analogía:**
- **Base de datos remota (DigitalOcean):** Arrendar un depósito lejos de tu casa
- **Base de datos local en WSL:** Tener tu propio depósito en casa (acceso 24/7, sin pagar renta)

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre instalar MySQL en Windows vs WSL?"
"¿Qué es el puerto 3306 y por qué MySQL lo usa?"
"Explícame qué es un usuario 'root' en MySQL."
"¿Puedo acceder a MySQL de WSL desde Windows?"
```

---

### 📥 PASO 1: Instalar MySQL en WSL (Ubuntu)

**Tareas:**

1. **Abrir terminal de Ubuntu (WSL):**
   - Abre Windows Terminal
   - Selecciona la pestaña de Ubuntu (o abre "Ubuntu" desde el menú inicio)

2. **Actualizar repositorios de paquetes:**
   ```bash
   sudo apt update
   ```
   - Esto actualiza la lista de paquetes disponibles
   - **Pregunta:** ¿Por qué es importante actualizar antes de instalar?

3. **Instalar MySQL Server:**
   ```bash
   sudo apt install mysql-server -y
   ```
   - Esto instalará MySQL Server y sus dependencias
   - Tamaño aproximado de descarga: ~30-40 MB
   - **Pregunta:** ¿Qué significa `sudo` y por qué es necesario?

4. **Verificar que MySQL se instaló correctamente:**
   ```bash
   mysql --version
   ```
   - Resultado esperado: `mysql  Ver 8.0.xx for Linux on x86_64`
   - Si ves la versión, ¡la instalación fue exitosa!

5. **Iniciar el servicio MySQL:**
   ```bash
   sudo service mysql start
   ```
   - Resultado esperado: `* Starting MySQL database server mysqld`
   - **Pregunta:** ¿Qué es un "servicio" en Linux?

6. **Verificar que el servicio está corriendo:**
   ```bash
   sudo service mysql status
   ```
   - Deberías ver algo como: `* MySQL is running`
   - Si ves "stopped", ejecuta de nuevo: `sudo service mysql start`

**✅ Resultado esperado:** 
- MySQL Server instalado en WSL
- Servicio MySQL corriendo
- Comando `mysql` disponible en terminal

---

### 🔐 PASO 2: Configurar Seguridad de MySQL

**Concepto clave:** Por defecto, MySQL en Ubuntu viene con configuración básica. Necesitamos configurar el usuario root con contraseña.

**Tareas:**

1. **Acceder a MySQL como root (sin contraseña inicial):**
   ```bash
   sudo mysql
   ```
   - Esto te conecta a MySQL usando autenticación del sistema
   - Deberías ver el prompt: `mysql>`

2. **Establecer contraseña para el usuario root:**
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'tu_contraseña';
   ```
   - **Reemplaza `tu_contraseña`** por una contraseña FÁCIL DE RECORDAR
   - Ejemplo: `'root123'` o `'forestech2025'`
   - ⚠️ **IMPORTANTE:** Anota esta contraseña, la usarás en Java
   - Resultado esperado: `Query OK, 0 rows affected`

3. **Aplicar los cambios:**
   ```sql
   FLUSH PRIVILEGES;
   ```
   - Esto recarga los permisos de usuarios
   - Resultado esperado: `Query OK, 0 rows affected`

4. **Salir de MySQL:**
   ```sql
   EXIT;
   ```
   - O presiona `Ctrl + D`

5. **Probar nueva autenticación:**
   ```bash
   mysql -u root -p
   ```
   - Te pedirá la contraseña que estableciste
   - Ingresa tu contraseña
   - Si entras correctamente, ¡funcionó!
   - Sal de nuevo con `EXIT;`

**✅ Resultado esperado:** 
- Usuario root configurado con contraseña
- Puedes conectarte con: `mysql -u root -p`

**🎓 Nota sobre WSL:**
- A diferencia de Windows, en WSL no hay "Windows Service" automático
- Cada vez que reinicies WSL, necesitarás iniciar MySQL con: `sudo service mysql start`
- **Tip:** Puedes agregar este comando a tu `~/.bashrc` para que se inicie automáticamente

### 🗄️ PASO 3: Conceptos Básicos de MySQL

**Antes de crear la base de datos, entiende estos conceptos:**

**🎓 Jerarquía en MySQL:**
```
MySQL Server (el servicio que corre en WSL)
│
├── Base de datos 1: FORESTECH
│   ├── Tabla: combustibles_products
│   ├── Tabla: combustibles_movements
│   └── Tabla: combustibles_vehicles
│
├── Base de datos 2: mysql (sistema)
└── Base de datos 3: sys (sistema)
```

**🎓 Comandos básicos que aprenderás:**

| Comando | Para qué sirve |
|---------|----------------|
| `SHOW DATABASES;` | Ver todas las bases de datos |
| `CREATE DATABASE nombre;` | Crear una nueva base de datos |
| `USE nombre;` | Seleccionar una base de datos para trabajar |
| `SHOW TABLES;` | Ver todas las tablas de la BD actual |
| `DESCRIBE tabla;` | Ver estructura de una tabla |
| `SELECT * FROM tabla;` | Ver todos los datos de una tabla |

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre una base de datos y una tabla?"
"¿Por qué necesito usar USE antes de crear tablas?"
"¿Qué significa el ; al final de cada comando SQL?"
```

---

### 🏗️ PASO 4: Crear Base de Datos FORESTECH

**Tareas (TÚ ejecutas cada comando):**

1. **Abrir MySQL desde terminal WSL:**
   ```bash
   mysql -u root -p
   ```
   - Ingresa tu contraseña cuando te la pida
   - Deberías ver el prompt: `mysql>`

2. **Ver bases de datos existentes:**
   ```sql
   SHOW DATABASES;
   ```
   - Resultado esperado: Verás mysql, information_schema, performance_schema, sys
   - **Pregunta:** ¿Qué son estas bases de datos del sistema?

3. **Crear base de datos FORESTECH:**
   ```sql
   CREATE DATABASE FORESTECH;
   ```
   - Resultado esperado: `Query OK, 1 row affected`

4. **Verificar que se creó:**
   ```sql
   SHOW DATABASES;
   ```
   - Resultado esperado: Ahora deberías ver FORESTECH en la lista

5. **Seleccionar FORESTECH para trabajar:**
   ```sql
   USE FORESTECH;
   ```
   - Resultado esperado: `Database changed`
   - **Pregunta:** ¿Por qué necesito hacer USE?

6. **Verificar que no tiene tablas (está vacía):**
   ```sql
   SHOW TABLES;
   ```
   - Resultado esperado: `Empty set (0.00 sec)`

**✅ Resultado esperado:** 
- Base de datos FORESTECH creada
- Actualmente seleccionada (puedes confirmar con `SELECT DATABASE();`)
- Sin tablas (las crearemos en el siguiente paso)

---

### 📋 PASO 5: Crear Tabla de Productos

**Concepto clave:** Una tabla es como una hoja de Excel con columnas (campos) y filas (registros).

**Diagrama de la tabla combustibles_products:**
```
combustibles_products
├── id (VARCHAR(10), PRIMARY KEY) - Identificador único
├── name (VARCHAR(100)) - Nombre del producto
├── type (VARCHAR(50)) - Tipo (Diesel, Gasolina, etc.)
└── unit (VARCHAR(20)) - Unidad de medida (litros, galones)
```

**Tareas (TÚ ejecutas):**

1. **Crear la tabla combustibles_products:**
   
   ```sql
   CREATE TABLE combustibles_products (
       id VARCHAR(10) PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       type VARCHAR(50) NOT NULL,
       unit VARCHAR(20) NOT NULL
   );
   ```
   
   - Resultado esperado: `Query OK, 0 rows affected`
   - **Pregunta:** ¿Qué significa PRIMARY KEY? ¿Por qué id es PRIMARY KEY?
   - **Pregunta:** ¿Qué hace NOT NULL?

2. **Verificar estructura de la tabla:**
   ```sql
   DESCRIBE combustibles_products;
   ```
   
   - Resultado esperado:
   ```
   +-------+--------------+------+-----+---------+-------+
   | Field | Type         | Null | Key | Default | Extra |
   +-------+--------------+------+-----+---------+-------+
   | id    | varchar(10)  | NO   | PRI | NULL    |       |
   | name  | varchar(100) | NO   |     | NULL    |       |
   | type  | varchar(50)  | NO   |     | NULL    |       |
   | unit  | varchar(20)  | NO   |     | NULL    |       |
   +-------+--------------+------+-----+---------+-------+
   ```

3. **Insertar datos de prueba:**
   
   ```sql
   INSERT INTO combustibles_products (id, name, type, unit) VALUES
   ('P001', 'Diesel Regular', 'Diesel', 'litros'),
   ('P002', 'Gasolina 93', 'Gasolina', 'litros'),
   ('P003', 'Gasolina 95', 'Gasolina', 'litros');
   ```
   
   - Resultado esperado: `Query OK, 3 rows affected`
   - **Pregunta:** ¿Por qué van entre comillas simples ' los valores?

4. **Verificar que se insertaron:**
   ```sql
   SELECT * FROM combustibles_products;
   ```
   
   - Resultado esperado:
   ```
   +------+-----------------+----------+---------+
   | id   | name            | type     | unit    |
   +------+-----------------+----------+---------+
   | P001 | Diesel Regular  | Diesel   | litros  |
   | P002 | Gasolina 93     | Gasolina | litros  |
   | P003 | Gasolina 95     | Gasolina | litros  |
   +------+-----------------+----------+---------+
   ```

5. **Practicar consultas básicas (TÚ ejecutas cada una):**
   
   a) **Filtrar por tipo:**
   ```sql
   SELECT * FROM combustibles_products WHERE type = 'Diesel';
   ```
   
   b) **Ordenar por nombre:**
   ```sql
   SELECT * FROM combustibles_products ORDER BY name;
   ```
   
   c) **Contar productos:**
   ```sql
   SELECT COUNT(*) FROM combustibles_products;
   ```
   
   d) **Actualizar un producto:**
   ```sql
   UPDATE combustibles_products SET unit = 'galones' WHERE id = 'P001';
   SELECT * FROM combustibles_products WHERE id = 'P001';
   ```
   
   e) **Volver a litros:**
   ```sql
   UPDATE combustibles_products SET unit = 'litros' WHERE id = 'P001';
   ```
   
   **Pregunta:** ¿Qué pasaría si olvidas el WHERE en un UPDATE?

**✅ Resultado esperado:** 
- Tabla combustibles_products creada con 4 columnas
- 3 productos de prueba insertados
- Entiendes las consultas básicas SELECT, INSERT, UPDATE
- Estás listo para conectar desde Java

---

### 🛠️ PASO 5: Comandos Útiles para el Día a Día

**Guarda estos comandos, los usarás constantemente:**

**Ver qué base de datos estás usando:**
```sql
SELECT DATABASE();
```

**Cambiar a otra base de datos:**
```sql
USE nombre_base_datos;
```

**Ver todas las tablas:**
```sql
SHOW TABLES;
```

**Ver estructura de tabla:**
```sql
DESCRIBE nombre_tabla;
```

**Eliminar todos los datos de tabla (¡CUIDADO!):**
```sql
DELETE FROM nombre_tabla;
```

**Eliminar tabla completa (¡CUIDADO!):**
```sql
DROP TABLE nombre_tabla;
```

**Eliminar base de datos (¡MUCHO CUIDADO!):**
```sql
DROP DATABASE nombre_base_datos;
```

**Salir de MySQL:**
```sql
EXIT;
```
o
```sql
QUIT;
```

---

### 💡 Conceptos Clave de MySQL

**🎓 Tipos de datos SQL - Guía Completa**

**Analogía:** Los tipos de datos son como **contenedores específicos** para diferentes cosas:
- No guardarías agua en una caja de cartón (necesitas una botella)
- No guardarías un libro en un vaso (necesitas un estante)
- Cada dato necesita el "contenedor" correcto en la base de datos

**Tabla completa de tipos de datos:**

| Tipo SQL | Para qué sirve | Tamaño/Límite | Ejemplo en Java | Ejemplo de uso en Forestech |
|----------|----------------|---------------|-----------------|------------------------------|
| **TIPOS NUMÉRICOS** |||||
| `INT` | Números enteros | -2,147,483,648 a 2,147,483,647 | `int` | Cantidad de productos: 100 |
| `BIGINT` | Números enteros grandes | Hasta 19 dígitos | `long` | ID de transacción: 9223372036854775807 |
| `DOUBLE` | Números decimales | Hasta 15 decimales | `double` | Cantidad litros: 150.75 |
| `DECIMAL(p,s)` | Números decimales exactos | p=total dígitos, s=decimales | `BigDecimal` | Dinero: DECIMAL(10,2) → 99999999.99 |
| **TIPOS DE TEXTO** |||||
| `VARCHAR(n)` | Texto variable hasta n caracteres | 1 a 65,535 caracteres | `String` | Nombre producto: VARCHAR(100) → "Diesel Regular" |
| `CHAR(n)` | Texto fijo de n caracteres | Siempre ocupa n caracteres | `String` | Código país: CHAR(2) → "CL" |
| `TEXT` | Texto largo | Hasta 65,535 caracteres | `String` | Descripción extensa, comentarios |
| **TIPOS DE FECHA/HORA** |||||
| `DATE` | Solo fecha | YYYY-MM-DD | `LocalDate` | Fecha: 2025-01-15 |
| `DATETIME` | Fecha y hora | YYYY-MM-DD HH:MM:SS | `LocalDateTime` | Movimiento: 2025-01-15 14:30:00 |
| `TIMESTAMP` | Marca de tiempo | Se actualiza automáticamente | `Timestamp` | Última modificación registrada |
| **TIPOS BOOLEANOS** |||||
| `BOOLEAN` | Verdadero/Falso | 0 (false) o 1 (true) | `boolean` | Activo: TRUE/FALSE |

**🎯 REGLAS para elegir el tipo correcto:**

**1. Para IDs únicos:**
- ¿Usas formato texto como "MOV-12345"? → `VARCHAR(20)`
- ¿Usas números secuenciales (1, 2, 3...)? → `INT` con AUTO_INCREMENT
- ¿Usas UUIDs largos? → `VARCHAR(36)`

**2. Para nombres y descripciones:**
- ¿Texto corto conocido (nombre producto, tipo)? → `VARCHAR(100)`
- ¿Texto largo o desconocido (comentarios, observaciones)? → `TEXT`
- ¿Texto de longitud fija (códigos ISO, placas)? → `CHAR(n)`

**3. Para cantidades:**
- ¿Números enteros (cantidad productos, unidades)? → `INT`
- ¿Números con decimales (litros, kilogramos)? → `DOUBLE`
- ¿Números que pueden ser negativos? → Usa INT o DOUBLE (permiten negativos)

**4. Para dinero (¡MUY IMPORTANTE!):**
- ⚠️ **NUNCA uses DOUBLE para dinero** (tiene errores de redondeo)
- ✅ **SIEMPRE usa DECIMAL(10,2)** (exacto, sin errores de redondeo)
- Ejemplo: precio total = 1234.56 → DECIMAL(10,2)

**5. Para fechas:**
- ¿Solo necesitas la fecha? → `DATE`
- ¿Necesitas fecha Y hora específica? → `DATETIME`
- ¿Quieres registrar automáticamente modificaciones? → `TIMESTAMP`

**Ejemplo práctico: Tabla combustibles_movements**

```sql
CREATE TABLE combustibles_movements (
    -- ID único del movimiento (formato: "MOV-XXXXXXXX")
    id VARCHAR(20) PRIMARY KEY,

    -- Tipo de movimiento (solo "ENTRADA" o "SALIDA")
    movement_type VARCHAR(10) NOT NULL,

    -- Cantidad en litros (puede tener decimales: 150.75)
    quantity DOUBLE NOT NULL,

    -- Precio por litro (¡DINERO! Usa DECIMAL para exactitud)
    price_per_unit DECIMAL(10,2) NOT NULL,

    -- Total del movimiento (cantidad × precio)
    total_amount DECIMAL(12,2) NOT NULL,

    -- Fecha y hora exacta del movimiento
    movement_date DATETIME DEFAULT NOW(),

    -- ID del vehículo relacionado
    vehicle_id VARCHAR(20),

    -- ¿Está activo este registro?
    is_active BOOLEAN DEFAULT TRUE
);
```

**🎓 Explicación detallada:**

**¿Por qué VARCHAR(20) para el ID?**
- Porque usamos formato: "MOV-12345678" (texto, no número)
- Si usáramos INT, no podríamos tener el prefijo "MOV-"
- VARCHAR(20) permite hasta 20 caracteres (tenemos espacio de sobra)

**¿Por qué DOUBLE para quantity?**
- Porque los litros pueden tener decimales: 150.75 litros
- INT solo permite enteros (150, 151, 152...) → perdemos precisión

**¿Por qué DECIMAL para precios?**
- Ejemplo con DOUBLE (INCORRECTO):
  ```
  3.10 + 0.05 = 3.1499999999999 ❌ (error de redondeo)
  ```
- Ejemplo con DECIMAL (CORRECTO):
  ```
  3.10 + 0.05 = 3.15 ✅ (exacto)
  ```

**¿Por qué DATETIME y no DATE?**
- Porque queremos saber la hora exacta del movimiento
- DATE solo guarda: 2025-01-15
- DATETIME guarda: 2025-01-15 14:30:00

**¿Por qué BOOLEAN para is_active?**
- Más claro que INT (TRUE/FALSE vs 0/1)
- Ocupa menos espacio (1 byte)
- Se lee mejor en las queries: `WHERE is_active = TRUE`

**🔍 Comparación: Tipos de datos en acción**

```sql
-- ❌ INCORRECTO
CREATE TABLE products (
    id INT,                    -- ❌ No podemos usar "P001"
    name CHAR(10),             -- ❌ "Diesel Regular" tiene 14 caracteres
    price DOUBLE,              -- ❌ Errores de redondeo con dinero
    created_date VARCHAR(50)   -- ❌ No puedes hacer cálculos con texto
);

-- ✅ CORRECTO
CREATE TABLE products (
    id VARCHAR(10),            -- ✅ Permite "P001", "P002"...
    name VARCHAR(100),         -- ✅ Suficiente espacio para nombres largos
    price DECIMAL(10,2),       -- ✅ Exacto para dinero
    created_date DATETIME      -- ✅ Tipo correcto para fechas
);
```

**⚠️ Errores comunes al elegir tipos:**

| Error | Consecuencia | Solución |
|-------|--------------|----------|
| Usar VARCHAR muy corto (ej: VARCHAR(10) para nombres) | Datos se truncan: "Christopher" → "Christophe" | Usa VARCHAR(100) para nombres |
| Usar INT para IDs alfanuméricos | No puedes guardar "MOV-001" | Usa VARCHAR(20) |
| Usar DOUBLE para dinero | 10.10 + 0.05 = 10.149999 (error) | Usa DECIMAL(10,2) |
| Usar TEXT para todo | Consumo excesivo de memoria | Usa VARCHAR(n) si conoces el tamaño |
| Olvidar decimales en cantidad | 150.75 litros → 150 litros (pierdes 0.75) | Usa DOUBLE para cantidades con decimales |

**Pregunta para reflexionar:**
¿Qué pasaría si usas VARCHAR(5) para guardar un ID con formato "MOV-12345678"? (Pista: cuenta los caracteres)

---

### 📚 Fundamentos SQL que DEBES Entender

**Antes de conectar Java con MySQL, domina estos conceptos fundamentales:**

---

#### 1️⃣ ¿Qué es una Base de Datos Relacional?

**🎓 Analogía: Una biblioteca organizada**

```
Base de Datos = La biblioteca completa
│
├── Tablas = Estantes diferentes (ficción, ciencia, historia)
│   │
│   ├── Filas (registros) = Libros individuales en el estante
│   │
│   └── Columnas (campos) = Datos de cada libro (título, autor, año)
│
└── Relaciones = Referencias entre estantes ("ver también...")
```

**Ejemplo con Forestech:**

```
Base de Datos: FORESTECH
│
├── Tabla: combustibles_products (Estante de productos)
│   ├── Fila 1: [P001, Diesel Regular, Diesel, litros]
│   ├── Fila 2: [P002, Gasolina 93, Gasolina, litros]
│   └── Columnas: id, name, type, unit
│
├── Tabla: combustibles_movements (Estante de movimientos)
│   ├── Fila 1: [MOV-001, ENTRADA, 100.5, P001, ...]
│   ├── Fila 2: [MOV-002, SALIDA, 50.0, P002, ...]
│   └── Columnas: id, type, quantity, product_id, ...
│
└── Tabla: combustibles_vehicles (Estante de vehículos)
    ├── Fila 1: [VEH-001, Camión Volvo, ABC-123, ...]
    └── Columnas: id, model, plate, ...
```

**¿Por qué "relacional"?**
- Las tablas se **relacionan** entre sí mediante IDs
- Un movimiento tiene un `product_id` que apunta a un producto específico
- Un movimiento tiene un `vehicle_id` que apunta a un vehículo específico

---

#### 2️⃣ PRIMARY KEY (Llave Primaria)

**¿Qué es?** Un campo que identifica de forma **ÚNICA** cada fila de la tabla.

**🎓 Analogía:**
```
PRIMARY KEY = Cédula de identidad de una persona
- No puede haber dos personas con la misma cédula
- No puede estar vacía (todos tienen cédula)
- Sirve para identificar a alguien sin ambigüedad
```

**Ejemplo visual:**

```
Tabla: combustibles_products

┌──────────┬──────────────────┬──────────┬────────┐
│ id (PK)  │ name             │ type     │ unit   │
├──────────┼──────────────────┼──────────┼────────┤
│ P001     │ Diesel Regular   │ Diesel   │ litros │ ← Esta fila es única por P001
│ P002     │ Gasolina 93      │ Gasolina │ litros │ ← Esta fila es única por P002
│ P003     │ Gasolina 95      │ Gasolina │ litros │ ← Esta fila es única por P003
└──────────┴──────────────────┴──────────┴────────┘
         ↑
    PRIMARY KEY
```

**Sintaxis SQL:**

```sql
CREATE TABLE combustibles_products (
    id VARCHAR(10) PRIMARY KEY,  -- ← Esto define la PK
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL
);
```

**Reglas de PRIMARY KEY:**
- ✅ Debe ser **ÚNICA** (no puede haber duplicados)
- ✅ **NO puede ser NULL** (obligatoria)
- ✅ Solo puede haber **UNA** primary key por tabla
- ✅ Generalmente es el campo `id`
- ✅ Se usa para identificar filas sin ambigüedad

**❌ Intentos inválidos:**

```sql
-- ❌ Intento 1: Insertar ID duplicado
INSERT INTO combustibles_products VALUES ('P001', 'Diesel', 'Diesel', 'litros');
INSERT INTO combustibles_products VALUES ('P001', 'Otro', 'Otro', 'litros');
-- ERROR: Duplicate entry 'P001' for key 'PRIMARY'

-- ❌ Intento 2: Insertar ID NULL
INSERT INTO combustibles_products VALUES (NULL, 'Diesel', 'Diesel', 'litros');
-- ERROR: Column 'id' cannot be null
```

**Pregunta:** ¿Por qué en Forestech usamos VARCHAR(10) para el ID y no INT?

---

#### 3️⃣ FOREIGN KEY (Llave Foránea)

**¿Qué es?** Un campo que **referencia** la PRIMARY KEY de **OTRA** tabla.

**🎓 Analogía:**
```
FOREIGN KEY = Referencia en un libro a otro libro
- "Ver también: Libro en estante C, posición 5"
- Crea una conexión entre dos datos relacionados
```

**Ejemplo visual:**

```
Tabla: combustibles_products          Tabla: combustibles_movements
┌──────────┬─────────────┐            ┌────────────┬────────────┬────────────┐
│ id (PK)  │ name        │            │ id (PK)    │ product_id │ quantity   │
├──────────┼─────────────┤            │            │    (FK)    │            │
│ P001     │ Diesel      │ ◄──────────┼ MOV-001    │ P001       │ 100.5      │
│ P002     │ Gasolina 93 │ ◄──────────┼ MOV-002    │ P002       │ 50.0       │
│ P003     │ Gasolina 95 │            │ MOV-003    │ P001       │ 75.0       │
└──────────┴─────────────┘            └────────────┴────────────┴────────────┘
                                                        ↑
                                                   FOREIGN KEY
                                           (apunta al id de products)
```

**Sintaxis SQL:**

```sql
CREATE TABLE combustibles_movements (
    id VARCHAR(20) PRIMARY KEY,
    movement_type VARCHAR(10) NOT NULL,
    quantity DOUBLE NOT NULL,
    product_id VARCHAR(10),  -- ← Esta será la FK

    -- Definición de la FOREIGN KEY:
    FOREIGN KEY (product_id) REFERENCES combustibles_products(id)
    --           ↑                                           ↑
    --    Campo local                              Campo en otra tabla
);
```

**¿Para qué sirve?**

1. **Mantener integridad de datos:**
   ```sql
   -- ❌ Esto fallará porque P999 no existe
   INSERT INTO combustibles_movements
   VALUES ('MOV-100', 'ENTRADA', 100.0, 'P999');
   -- ERROR: Cannot add or update a child row:
   -- a foreign key constraint fails
   ```

2. **Relacionar datos entre tablas:**
   ```sql
   -- ✅ Buscar todos los movimientos de Diesel
   SELECT m.id, m.quantity, p.name
   FROM combustibles_movements m
   JOIN combustibles_products p ON m.product_id = p.id
   WHERE p.type = 'Diesel';
   ```

3. **Evitar datos huérfanos:**
   - No puedes eliminar un producto si hay movimientos que lo referencian
   - Protege la consistencia de la base de datos

**Ejemplo completo con múltiples FKs:**

```sql
CREATE TABLE combustibles_movements (
    id VARCHAR(20) PRIMARY KEY,
    movement_type VARCHAR(10) NOT NULL,
    quantity DOUBLE NOT NULL,

    -- FK 1: Referencia a productos
    product_id VARCHAR(10),
    FOREIGN KEY (product_id) REFERENCES combustibles_products(id),

    -- FK 2: Referencia a vehículos
    vehicle_id VARCHAR(20),
    FOREIGN KEY (vehicle_id) REFERENCES combustibles_vehicles(id),

    -- FK 3: Referencia a proveedores
    supplier_id VARCHAR(20),
    FOREIGN KEY (supplier_id) REFERENCES combustibles_suppliers(id)
);
```

**Pregunta:** ¿Qué pasaría si intentas eliminar un producto que tiene 50 movimientos asociados?

---

#### 4️⃣ NOT NULL (Campo Obligatorio)

**¿Qué es?** Una restricción que **NO permite valores vacíos** en ese campo.

**🎓 Analogía:**
```
NOT NULL = Campos obligatorios en un formulario
- Nombre: __________ (obligatorio)
- Email: __________ (obligatorio)
- Teléfono: __________ (opcional)
```

**Ejemplo:**

```sql
CREATE TABLE combustibles_products (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,      -- ← Obligatorio
    type VARCHAR(50) NOT NULL,       -- ← Obligatorio
    description TEXT                 -- ← Opcional (puede estar vacío)
);
```

**Diferencia visual:**

```
Tabla: combustibles_products

┌──────────┬─────────────────┬──────────┬─────────────────────┐
│ id (PK)  │ name (NOT NULL) │ type     │ description (NULL)  │
├──────────┼─────────────────┼──────────┼─────────────────────┤
│ P001     │ Diesel Regular  │ Diesel   │ Combustible diésel  │ ✅
│ P002     │ Gasolina 93     │ Gasolina │ NULL                │ ✅
│ P003     │ NULL            │ Diesel   │ Descripción...      │ ❌ ERROR
└──────────┴─────────────────┴──────────┴─────────────────────┘
                  ↑
            NO puede estar vacío
```

**Ejemplo de intento inválido:**

```sql
-- ❌ Intentar insertar sin nombre (campo NOT NULL)
INSERT INTO combustibles_products (id, type)
VALUES ('P004', 'Diesel');
-- ERROR: Field 'name' doesn't have a default value

-- ✅ Insertar con nombre
INSERT INTO combustibles_products (id, name, type)
VALUES ('P004', 'Diesel Premium', 'Diesel');
-- Query OK, 1 row affected
```

**¿Cuándo usar NOT NULL?**

✅ **Usa NOT NULL cuando:**
- El campo es esencial para la entidad (nombre, tipo, cantidad)
- Sin ese dato, el registro no tiene sentido
- Quieres prevenir datos incompletos

❌ **NO uses NOT NULL cuando:**
- El campo es realmente opcional (comentarios, observaciones)
- Se llenará después (campos calculados, fechas de finalización)
- Puede no aplicar en algunos casos

**Ejemplo aplicado a Forestech:**

```sql
CREATE TABLE combustibles_movements (
    -- Campos obligatorios (NOT NULL)
    id VARCHAR(20) PRIMARY KEY,
    movement_type VARCHAR(10) NOT NULL,   -- Siempre debe tener tipo
    quantity DOUBLE NOT NULL,             -- Siempre debe tener cantidad
    movement_date DATETIME NOT NULL,      -- Siempre debe tener fecha

    -- Campos opcionales (pueden ser NULL)
    vehicle_id VARCHAR(20),               -- Puede no tener vehículo asociado
    supplier_id VARCHAR(20),              -- Solo aplica en ENTRADA
    notes TEXT,                           -- Comentarios opcionales

    FOREIGN KEY (vehicle_id) REFERENCES combustibles_vehicles(id),
    FOREIGN KEY (supplier_id) REFERENCES combustibles_suppliers(id)
);
```

**Pregunta:** En la tabla `combustibles_movements`, ¿qué campos de Forestech crees que deberían ser NOT NULL y por qué?

---

#### 5️⃣ DEFAULT (Valor por Defecto)

**¿Qué es?** Un valor que se asigna automáticamente si no proporcionas ninguno.

**Ejemplo:**

```sql
CREATE TABLE combustibles_movements (
    id VARCHAR(20) PRIMARY KEY,
    quantity DOUBLE NOT NULL,
    movement_date DATETIME DEFAULT NOW(),      -- ← Fecha actual automática
    is_active BOOLEAN DEFAULT TRUE             -- ← TRUE por defecto
);
```

**Uso práctico:**

```sql
-- Si no especificas movement_date, se usa NOW()
INSERT INTO combustibles_movements (id, quantity)
VALUES ('MOV-001', 100.5);
-- movement_date se guardará con la fecha/hora actual

-- Si no especificas is_active, se usa TRUE
SELECT * FROM combustibles_movements WHERE id = 'MOV-001';
-- Resultado: is_active = TRUE (por defecto)
```

---

### 🎯 Resumen de Conceptos Fundamentales

| Concepto | Propósito | Ejemplo | Cuándo usarlo |
|----------|-----------|---------|---------------|
| **PRIMARY KEY** | Identificar fila única | `id VARCHAR(10) PRIMARY KEY` | Siempre (toda tabla necesita una) |
| **FOREIGN KEY** | Relacionar tablas | `FOREIGN KEY (product_id) REFERENCES products(id)` | Cuando vinculas con otra tabla |
| **NOT NULL** | Campo obligatorio | `name VARCHAR(100) NOT NULL` | Datos esenciales |
| **DEFAULT** | Valor automático | `is_active BOOLEAN DEFAULT TRUE` | Valores predeterminados comunes |

**Ejercicio mental:**

Diseña en tu mente la tabla `combustibles_vehicles`:
- ¿Qué campos debería tener?
- ¿Cuál sería la PRIMARY KEY?
- ¿Qué campos deberían ser NOT NULL?
- ¿Necesita alguna FOREIGN KEY?

---

## 🎯 ESTRUCTURA AL TERMINAR FASE 3

Al finalizar esta fase tendrás:

```
com.forestech/
├── Main.java
├── AppConfig.java
├── MenuHelper.java
├── DataDisplay.java
├── InputHelper.java
├── config/
│   └── DatabaseConnection.java (NUEVO - conexión centralizada)
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Product.java (NUEVO)
├── managers/
│   └── MovementManager.java
└── services/
    └── ProductService.java (NUEVO - acceso a BD)
```

**Filosofía de organización:**
- **config/**: Configuraciones técnicas (conexión a BD, credenciales)
- **models/**: POJOs (Plain Old Java Objects) - clases con datos, sin lógica de BD
- **services/**: Clases que interactúan con la BD (SELECT, INSERT, UPDATE, DELETE)
- **managers/**: Lógica de negocio que usa services y models
- **Main.java**: Solo pruebas y punto de entrada

---

## ✅ Checkpoint 3.1: Configurar JDBC Driver de MySQL

**Concepto clave:** JDBC es la API estándar de Java para comunicarse con bases de datos.

**📍 DÓNDE:** 
- **Archivo:** `pom.xml` en la raíz del proyecto `forestech-cli-java/`
- **Terminal WSL:** Para ejecutar Maven
- **Main.java:** NO tocar (todavía no usaremos la BD)

**🎯 PARA QUÉ:** 
Sin el driver JDBC de MySQL (Connector/J), Java no puede "hablar" con tu base de datos. Es como tener un teléfono sin SIM card.

El driver JDBC:
- ✅ **Traduce** las llamadas de Java a comandos que MySQL entiende
- ✅ **Gestiona** la conexión de red entre tu aplicación y el servidor MySQL
- ✅ **Maneja** el protocolo de comunicación específico de MySQL

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Usarás este driver para INSERT, UPDATE, DELETE de movimientos
- **Fase 5:** Ejecutarás queries complejas con JOINs entre tablas
- **Fase 9:** Generarás reportes consultando datos históricos

**🎓 Analogía:**
- **Tu aplicación Java**: Turista que solo habla español
- **MySQL**: Local que solo habla alemán
- **Driver JDBC (Connector/J)**: Traductor que permite la comunicación

**Prompts sugeridos:**
```text
"Explícame con analogía cómo funciona JDBC como puente entre Java y MySQL."
"¿Dónde guarda Maven las dependencias y cómo puedo verificarlo?"
"¿Qué diferencia hay entre JDBC (API) y el driver de MySQL (implementación)?"
"¿Por qué cada base de datos (MySQL, PostgreSQL, SQL Server) necesita su propio driver?"
"¿Qué es mysql-connector-j y por qué reemplazó a mysql-connector-java?"
```

**Diagrama de tareas - Configurar JDBC Driver:**

```
pom.xml
│
└── Sección <dependencies>
    │
    └── Nueva dependencia <dependency>
        ├── <groupId>: com.mysql
        ├── <artifactId>: mysql-connector-j
        └── <version>: 8.0.33 (o superior)

Propósito: Esto permite que Maven descargue el MySQL Connector/J
```

**Tareas paso a paso (TÚ completas cada una):**

1. **Abrir `pom.xml`** en el editor
   - Localiza la sección `<dependencies>`
   - Si no existe, debes crearla dentro de `<project>` (antes de `</project>`)

2. **Agregar la dependencia de MySQL:**
   - Estructura de una dependencia:
     ```xml
     <dependency>
         <groupId>...</groupId>
         <artifactId>...</artifactId>
         <version>...</version>
     </dependency>
     ```
   - **TÚ completas:** Coloca el groupId, artifactId y version para MySQL Connector/J
   - **Datos necesarios:**
     - groupId: `com.mysql`
     - artifactId: `mysql-connector-j`
     - version: `8.0.33` (o la más reciente)
   - **Pregunta:** ¿Por qué cambió de `mysql-connector-java` a `mysql-connector-j`?

3. **Guardar** el archivo pom.xml

4. **Descargar la dependencia desde terminal WSL:**
   - Abre terminal de Ubuntu
   - Navega al proyecto:
     ```bash
     cd ~/forestechOil/forestech-cli-java/
     ```
   - Ejecuta Maven:
     ```bash
     mvn clean install
     ```
   - Deberías ver líneas como: `Downloading from central: https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/...`

5. **Verificar descarga exitosa:**
   
   **Por terminal WSL:**
   ```bash
   ls ~/.m2/repository/com/mysql/mysql-connector-j/
   ```
   - Resultado esperado: Carpeta con tu versión (ej: `8.0.33/`)
   - Dentro debe haber un archivo JAR: `mysql-connector-j-8.0.33.jar`

6. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```
   - Resultado esperado: `BUILD SUCCESS`

**✅ Resultado esperado:** 
- Maven descarga el driver MySQL sin errores
- El proyecto compila exitosamente con `mvn clean compile`
- Puedes ver el JAR del driver en tu repositorio local Maven (`~/.m2/repository/`)
- En IntelliJ, la dependencia aparece en el árbol de Maven

**💡 Concepto clave:** Las dependencias de Maven se descargan UNA VEZ y se reutilizan en todos tus proyectos. Por eso se guardan en `~/.m2/repository/` (repositorio local compartido).

**⚠️ PROBLEMAS COMUNES:**

| Problema | Causa | Solución |
|----------|-------|----------|
| "Could not resolve dependencies" | Sin internet o Maven no configurado | Verifica conexión y `mvn -version` |
| "BUILD FAILURE" | Error en pom.xml | Verifica sintaxis XML (etiquetas cerradas) |
| Dependencia no aparece en IntelliJ | Cache desactualizado | Reimport: clic derecho en proyecto → Maven → Reload Project |

**⏱️ Tiempo estimado:** 20 minutos

---

## ✅ Checkpoint 3.2: Clase `DatabaseConnection`

**Concepto clave:** Centralizar la lógica de conexión a BD en una clase específica evita duplicar código y facilita mantenimiento.

**📍 DÓNDE:** 
- **Crear paquete nuevo:** `config` dentro de `com.forestech/`
- **Crear archivo:** `DatabaseConnection.java` en `forestech-cli-java/src/main/java/com/forestech/config/`
- **Main.java:** Solo para PROBAR la conexión (1 línea de prueba)

**🎯 PARA QUÉ:** 
Sin esta clase, cada vez que necesites conectarte a la BD tendrías que:
- ❌ Escribir la URL, usuario y contraseña en CADA servicio
- ❌ Repetir la lógica de `DriverManager.getConnection()` en 10+ lugares
- ❌ Si cambias la contraseña, modificar 10+ archivos
- ❌ Dificultar pruebas (no puedes cambiar fácilmente a BD de prueba)

Con DatabaseConnection:
- ✅ **Centralizas** la configuración (URL, usuario, contraseña) en UN SOLO lugar
- ✅ **Reutilizas** el método de conexión desde cualquier servicio
- ✅ **Facilitas cambios** (si cambias de BD, solo modificas un archivo)
- ✅ **Pruebas más simples** (puedes cambiar a BD de prueba fácilmente)
- ✅ **Separación de responsabilidades** (config vs lógica de negocio)

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Todos los servicios (MovementService, VehicleService) usarán `DatabaseConnection.getConnection()`
- **Fase 5:** Agregarás manejo de transacciones aquí (commit/rollback)
- **Fase 8:** Migrarás credenciales a archivo externo (`application.properties`)
- **Fase 10:** Configurarás diferentes conexiones para dev/producción

**🎓 Analogía:**
- **DatabaseConnection**: Recepcionista del hotel que gestiona las llaves de todas las habitaciones
- **Servicios (ProductService, etc.)**: Huéspedes que piden su llave a la recepcionista
- **Sin DatabaseConnection**: Cada huésped tendría que fabricar su propia llave (caos total)

**Prompts sugeridos:**
```text
"¿Qué es una connection string y cuáles son sus partes?"
"¿Por qué es mala práctica hardcodear credenciales en el código?"
"Explícame qué hace try-with-resources y por qué es importante para conexiones."
"¿Qué es un constructor privado y para qué sirve?"
"¿Por qué DatabaseConnection tiene métodos static?"
"¿Qué diferencia hay entre la URL de MySQL y la de SQL Server?"
```

**Diagrama de estructura - Clase DatabaseConnection:**

```
DatabaseConnection.java
│
├── Constructor privado (patrón Utility Class)
│   └── Sin parámetros, cuerpo vacío
│
├── Constantes de configuración (private static final)
│   ├── URL → "jdbc:mysql://localhost:3306/FORESTECH"
│   ├── USER → "root"
│   └── PASSWORD → Tu contraseña configurada en MySQL
│
├── Método: getConnection()
│   ├── Tipo retorno: Connection
│   ├── Modificadores: public static
│   ├── Excepciones: throws SQLException
│   ├── Lógica:
│   │   1. Usar DriverManager.getConnection(URL, USER, PASSWORD)
│   │   2. Retornar el objeto Connection obtenido
│   │
│   └── Import necesario:
│       - java.sql.Connection
│       - java.sql.DriverManager
│       - java.sql.SQLException
│
└── Método: testConnection()
    ├── Tipo retorno: void
    ├── Modificadores: public static
    ├── Manejo de excepciones: try-catch (NO throws)
    │
    ├── Sección try (TÚ escribes):
    │   ├── Obtener conexión con try-with-resources
    │   ├── Obtener metadata: conn.getMetaData()
    │   └── Imprimir nombre BD: metadata.getDatabaseProductName()
    │
    └── Sección catch (TÚ escribes):
        ├── Imprimir mensaje de error
        └── Mostrar: e.getMessage()
```

**Tareas paso a paso:**

1. **Crear el paquete `config`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "config"
   - **Pregunta:** ¿Por qué las configuraciones técnicas van en un paquete separado?

2. **Crear la clase DatabaseConnection:**
   - Clic derecho en `config` → New → Java Class → "DatabaseConnection"
   - Declarar como clase pública

3. **Constructor privado (es una Utility Class):**
   - **TÚ implementas:** constructor sin parámetros
   - **Pregunta:** ¿Por qué un constructor privado? ¿Qué pasaría si fuera público?

4. **Declarar constantes de configuración (TÚ las escribes):**
   - Tres constantes: URL, USER, PASSWORD
   - Modificadores: `private static final`
   - **URL formato MySQL:** `jdbc:mysql://localhost:3306/FORESTECH`
   - **USER:** `"root"`
   - **PASSWORD:** Tu contraseña (la que configuraste en PASO 2)
   - **Pregunta:** ¿Qué significa cada parte de la URL? 
     - `jdbc` = Protocolo Java Database Connectivity
     - `mysql` = Driver específico (MySQL)
     - `localhost` = Servidor (tu PC en WSL)
     - `3306` = Puerto por defecto de MySQL
     - `FORESTECH` = Nombre de la base de datos

5. **Implementar getConnection() (TÚ lo escribes):**
   - Usar `DriverManager.getConnection(URL, USER, PASSWORD)`
   - Retornar la Connection obtenida
   - Agregar los tres imports de java.sql
   - Firma completa:
     ```java
     public static Connection getConnection() throws SQLException {
         // Tu código aquí
     }
     ```

6. **Implementar testConnection() (TÚ lo escribes):**
   
   a) **Estructura try-with-resources:**
      ```java
      try (Connection conn = getConnection()) {
          // Tu código aquí
      } catch (SQLException e) {
          // Tu código aquí
      }
      ```
   
   b) **En el try:**
      - Extrae metadata: `conn.getMetaData()`
      - Imprime nombre de BD: `metadata.getDatabaseProductName()`
      - Imprime versión: `metadata.getDatabaseProductVersion()`
      - Imprime mensaje de éxito
   
   c) **En el catch:**
      - Imprime error: `e.getMessage()`
   
   d) **Pregunta:** ¿Por qué try-with-resources vs try-finally manual?

7. **Asegurarte que MySQL está corriendo en WSL:**
   - Abre terminal Ubuntu
   - Ejecuta:
     ```bash
     sudo service mysql status
     ```
   - Si está "stopped", inicia el servicio:
     ```bash
     sudo service mysql start
     ```

8. **Probar en Main.java:**
   - Agrega esta línea en el método main (al inicio, como prueba):
     ```java
     DatabaseConnection.testConnection();
     ```
   - Compila:
     ```bash
     mvn clean compile
     ```
   - Ejecuta:
     ```bash
     mvn exec:java -Dexec.mainClass="com.forestech.Main"
     ```

**✅ Resultado esperado:** 
- Ver mensaje "✅ Conexión exitosa a MySQL" en consola
- Ver versión de MySQL (ej: "8.0.39-0ubuntu0.22.04.1")
- Si falla, ver mensaje de error claro indicando el problema específico
- Archivo DatabaseConnection.java en el paquete `config`
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java (NUEVO)
  └── models/
      └── ...
  ```

**💡 Concepto clave - try-with-resources:**
```java
SIN try-with-resources (antiguo):
Connection conn = null;
try {
    conn = getConnection();
    // usar conexión
} finally {
    if (conn != null) conn.close();  // Código repetitivo
}

CON try-with-resources (moderno):
try (Connection conn = getConnection()) {
    // usar conexión
}  // ✅ Se cierra automáticamente, incluso si hay excepción
```

**⚠️ PREREQUISITOS:**
- MySQL debe estar corriendo en WSL (`sudo service mysql status`)
- El puerto 3306 debe estar abierto (por defecto lo está)
- La base de datos FORESTECH debe existir
- Usuario/contraseña deben ser correctos

**🔍 Depuración - Errores comunes:**

| Error | Causa probable | Solución |
|-------|---------------|----------|
| "Communications link failure" | MySQL no está corriendo | `sudo service mysql start` en WSL |
| "Access denied for user" | Contraseña incorrecta | Verifica PASSWORD en DatabaseConnection |
| "Unknown database 'FORESTECH'" | No creaste la BD | Ejecuta: `CREATE DATABASE FORESTECH;` |
| "No suitable driver found" | Driver no descargado | Ejecuta: `mvn clean install` |
| "Connection refused" | Puerto incorrecto | Verifica que sea 3306 en la URL |

**⏱️ Tiempo estimado:** 45 minutos

---

## ✅ Checkpoint 3.3: Primera query `SELECT` con Service

**Concepto clave:** Los Services son clases especializadas en interactuar con la base de datos. Separan la lógica de acceso a datos de la lógica de negocio.

**📍 DÓNDE:** 
- **Crear paquete nuevo:** `services` dentro de `com.forestech/`
- **Crear archivo:** `ProductService.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
- **Main.java:** Para PROBAR el servicio (llamar al método)

**🎯 PARA QUÉ:**
Hasta ahora solo probaste la conexión. Ahora necesitas:
- ✅ **Leer datos** de las tablas existentes en MySQL
- ✅ **Ejecutar queries SQL** desde Java
- ✅ **Procesar resultados** con ResultSet
- ✅ **Separar responsabilidades** (Service se encarga de BD, no el Manager ni Main)

**Arquitectura en capas:**
```
Main.java (prueba)
    ↓ llama a
ProductService.java (acceso a BD)
    ↓ usa
DatabaseConnection.java (obtiene conexión)
    ↓ conecta con
MySQL (base de datos en WSL)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Crearás `MovementService` con INSERT, UPDATE, DELETE siguiendo este patrón
- **Fase 5:** Los Services retornarán listas de objetos (no solo imprimirán)
- **Fase 6:** El menú interactivo llamará a Services para mostrar datos al usuario
- **Fase 9:** Services generarán reportes con queries complejas

**🎓 Analogía:**
- **ProductService**: Bibliotecario que busca libros en el sistema
- **DatabaseConnection**: Llave de acceso a la biblioteca
- **MySQL en WSL**: Los estantes con todos los libros
- **Main.java**: Persona que le pide al bibliotecario "muéstrame todos los productos"

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre Statement y PreparedStatement?"
"Explícame como cursor qué hace ResultSet al moverse con rs.next()."
"¿Por qué necesito try-with-resources para Connection, Statement y ResultSet?"
"¿Qué hace rs.getString() vs rs.getInt() vs rs.getDouble()?"
"¿Dónde va la lógica SQL: en Service o en Manager? ¿Por qué?"
```

**Diagrama de estructura - Método getAllProducts():**

```
ProductService.java → getAllProducts()
│
├── Tipo retorno: void (por ahora)
├── Modificadores: public static
├── Sin parámetros
├── Manejo: try-catch
│
├── 1. Definir query SQL
│   └── SELECT id, name, type, unit FROM combustibles_products
│
├── 2. Usar try-with-resources anidado
│   ├── Connection conn = DatabaseConnection.getConnection()
│   ├── Statement stmt = conn.createStatement()
│   └── ResultSet rs = stmt.executeQuery(sql)
│
├── 3. Ciclo de lectura de datos
│   ├── while (rs.next())  ← TÚ implementas
│   │   ├── Extraer: rs.getString("id")
│   │   ├── Extraer: rs.getString("name")
│   │   ├── Extraer: rs.getString("type")
│   │   ├── Extraer: rs.getString("unit")
│   │   └── Imprimir datos con formato
│   │
│   └── Contar filas procesadas
│
├── 4. Después del while
│   └── Imprimir total de productos
│
└── 5. catch (SQLException e)
    ├── Imprimir mensaje de error
    └── Imprimir: e.getMessage()
```

**Tareas paso a paso:**

1. **Crear el paquete `services`:**
   - Clic derecho en `com.forestech` → New → Package → "services"

2. **Crear la clase ProductService:**
   - Clic derecho en `services` → New → Java Class → "ProductService"
   - Constructor privado (es una Utility Class)

3. **Imports necesarios (TÚ agregas):**
   - De DatabaseConnection: `com.forestech.config.DatabaseConnection`
   - De SQL: `Connection`, `Statement`, `ResultSet`, `SQLException` de `java.sql`

4. **Implementar getAllProducts() (TÚ lo escribes):**

   a) **Definir query:**
      - Variable: `String sql`
      - Valor: `"SELECT id, name, type, unit FROM combustibles_products"`
   
   b) **Estructura try-with-resources:**
      - Patrón con múltiples recursos:
        ```
        try (Connection conn = ...;
             Statement stmt = ...;
             ResultSet rs = ...) {
        ```
   
   c) **Obtener cada recurso:**
      - Connection: `DatabaseConnection.getConnection()`
      - Statement: `conn.createStatement()`
      - ResultSet: `stmt.executeQuery(sql)`
   
   d) **Dentro del try:**
      - Imprime encabezado: `"=== PRODUCTOS EN BD ==="`
      - Recorre ResultSet con `while (rs.next())`
      - **En cada iteración extrae:**
        - `rs.getString("id")`
        - `rs.getString("name")`
        - `rs.getString("type")`
        - `rs.getString("unit")`
      - Imprime los datos con formato legible
      - Cuenta cuántas filas procesaste
   
   e) **Después del while:**
      - Imprime el total
   
   f) **En el catch:**
      - Imprime error y el mensaje: `e.getMessage()`
   
   **Pregunta clave:** ¿Por qué `rs.next()` retorna `boolean`? ¿Qué sucede cuando no hay más filas?

5. **Verificar tabla en MySQL:**
   - Ejecuta manualmente en MySQL desde terminal WSL:
     ```bash
     mysql -u root -p
     USE FORESTECH;
     SELECT * FROM combustibles_products;
     ```
   - Si no existe, créala o ajusta el nombre en la query
   - Verifica que haya al menos 1-2 productos para probar

6. **Probar en Main.java:**
   - Agrega (donde corresponda):
     ```
     System.out.println("\n=== LECTURA DE PRODUCTOS ===");
     ProductService.getAllProducts();
     ```
   - Compila: `mvn clean compile`
   - Ejecuta: `mvn exec:java -Dexec.mainClass="com.forestech.Main"`

7. **Depuración obligatoria:**
   - Coloca breakpoint en el `while (rs.next())`
   - Ejecuta en debug
   - Observa cómo se recorren las filas
   - Verifica los valores en el panel de variables

**✅ Resultado esperado:** 
- Ver lista de productos de la BD en consola con formato claro
- Al final, mensaje indicando cuántos productos se listaron (ej: "Total: 3 productos")
- No errores de conexión ni SQL
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java
  ├── services/
  │   └── ProductService.java (NUEVO)
  └── models/
      └── ...
  ```

**💡 Concepto clave - Ciclo de vida JDBC:**

```
1. Connection conn = DatabaseConnection.getConnection()
   → Abre canal de comunicación con MySQL

2. Statement stmt = conn.createStatement()
   → Crea "mensajero" que llevará tu query

3. ResultSet rs = stmt.executeQuery("SELECT ...")
   → Ejecuta query y obtiene cursor sobre los resultados

4. while (rs.next()) { ... }  
   → Recorre filas una por una

5. try-with-resources cierra automáticamente:
   rs.close() → stmt.close() → conn.close()
   → Libera recursos (CRÍTICO para no agotar conexiones)
```

**💡 Concepto clave - ResultSet como cursor:**

```
ResultSet es como un puntero que apunta a UNA fila a la vez:

Inicio: [antes de la primera fila]
rs.next() → [fila 1] → retorna true
rs.next() → [fila 2] → retorna true
rs.next() → [fila 3] → retorna true
rs.next() → [después de última fila] → retorna FALSE

Extraer datos de la fila actual:
String id = rs.getString("id");
String name = rs.getString("name");
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| NullPointerException en rs.getString() | Columna no existe en la query | Verifica nombre exacto en SELECT |
| No imprime nada (while no se ejecuta) | Tabla vacía | Inserta datos de prueba en MySQL |
| "Invalid object name 'combustibles_products'" | Tabla no existe | Crea la tabla o usa el nombre correcto |
| Muchas conexiones abiertas | No cerrar recursos | Usa try-with-resources (cierra automático) |

**🔍 Depuración - Entender el flujo:**

Coloca breakpoints en:
1. Antes de `try` - ver que la query está bien formada
2. Dentro del `while` - ver cada fila que se procesa
3. Después del `while` - ver el total
4. En el `catch` - ver el error si falla

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✅ Checkpoint 3.4: Mapear ResultSet a Objetos Product

**Concepto clave:** Convertir filas de la BD (ResultSet) en objetos Java te permite trabajar con POO en vez de datos planos.

**📍 DÓNDE:** 
- **Crear archivo:** `Product.java` en `forestech-cli-java/src/main/java/com/forestech/models/`
- **Modificar:** `ProductService.java` (cambiar método para que retorne objetos)
- **Main.java:** Para PROBAR la lectura de objetos

**🎯 PARA QUÉ:** 
En Checkpoint 3.3 solo imprimiste datos. Eso está bien para aprender JDBC, pero tiene limitaciones:
- ❌ No puedes manipular los datos después de leerlos (no hay objetos)
- ❌ No puedes pasar los datos a otras funciones
- ❌ No aplicas POO (solo trabajas con Strings sueltos)
- ❌ No puedes agregar comportamiento (cálculos, validaciones)

Con objetos Product:
- ✅ **POO completa:** Cada producto es un objeto con atributos y métodos
- ✅ **Reutilización:** Puedes pasar la lista de productos a otras funciones
- ✅ **Mantenibilidad:** Si agregas atributos, solo cambias la clase Product
- ✅ **Comportamiento:** Puedes agregar métodos como `calculateStock()`, `isLowStock()`, etc.

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Crearás `Movement.java` mejorado y lo mapearás desde `MovementService`
- **Fase 5:** Los managers trabajarán con listas de objetos (no con ResultSet directo)
- **Fase 6:** Mostrarás productos en el menú usando objetos
- **Fase 9:** Generarás reportes procesando listas de objetos

**🎓 Analogía:**
```
ResultSet (Checkpoint 3.3):  
"Tengo estos datos: Diesel, 1000, litros..."
→ Solo strings sueltos, como papeles desordenados

Product (Checkpoint 3.4):
Product p = new Product("Diesel", 1000, "litros");
→ Objeto estructurado, como una carpeta organizada
```

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre imprimir desde ResultSet vs crear objetos?"
"¿Por qué es mejor retornar List<Product> que imprimir directamente?"
"¿Cómo mapeo cada columna del ResultSet a un atributo del objeto?"
"¿Qué es un ArrayList y cuándo usarlo?"
```

**Tareas paso a paso:**

1. **Crear la clase Product en models/:**
   
   **Atributos que debe tener (TÚ defines como privados):**
   - id (String)
   - name (String)
   - type (String) - ej: "Diesel", "Gasolina 93"
   - unit (String) - ej: "litros", "galones"
   
   **Pregunta guía:** ¿Por qué Product va en `models/` y no en `services/`?

**Diagrama de estructura - Clase Product y mapeo ResultSet:**

```
Product.java (en models/)
│
├── Atributos privados
│   ├── String id
│   ├── String name
│   ├── String type
│   └── String unit
│
├── Constructor Product(id, name, type, unit)
│   └── Asigna parámetros a atributos con this
│
├── Getters (Fase 2 ya lo sabes)
│   ├── getId()
│   ├── getName()
│   ├── getType()
│   └── getUnit()
│
└── toString()
    ├── @Override
    └── Formato: Product{id='...', name='...', ...}

Mapeo: ResultSet fila → Objeto Product
│
├── Extraer: rs.getString("id") → String id
├── Extraer: rs.getString("name") → String name
├── Extraer: rs.getString("type") → String type
├── Extraer: rs.getString("unit") → String unit
│
└── new Product(id, name, type, unit) → Objeto creado
```

**Tareas paso a paso:**

1. **Crear clase Product en models/:**
   - Clic derecho en `models` → New → Java Class → "Product"
   - **Pregunta:** ¿Por qué Product va en `models/` y no en `services/`?

2. **Crear atributos privados (TÚ los escribes):**
   - id (String)
   - name (String)
   - type (String)
   - unit (String)

3. **Crear constructor con 4 parámetros (TÚ lo escribes):**
   - Recibe: id, name, type, unit
   - Asigna cada uno a su atributo con `this`
   - **Pregunta:** ¿Es necesario constructor sin parámetros? ¿Por qué?

4. **Crear getters (Fase 2 - ya lo sabes):**
   - `getId()`
   - `getName()`
   - `getType()`
   - `getUnit()`

5. **Crear toString() (TÚ lo escribes):**
   - Usa `@Override`
   - Retorna String con formato legible:
     ```
     Product{id='P001', name='Diesel', type='Combustible', unit='litros'}
     ```
   - **Pregunta:** ¿Para qué sirve toString()? ¿Cuándo se llama?

6. **Modificar ProductService.getAllProducts() (TÚ lo refactorizas):**
   
   a) **Cambiar firma:**
      - De: `void getAllProducts()`
      - A: `List<Product> getAllProducts()`
   
   b) **Agregar imports:**
      - `java.util.List`
      - `java.util.ArrayList`
      - `com.forestech.models.Product`
   
   c) **Crear lista al inicio:**
      ```
      List<Product> products = new ArrayList<>()`
      ```
   
   d) **Dentro del while (TÚ implementas):**
      - Extrae columnas: id, name, type, unit del ResultSet
      - Crea objeto: `new Product(...)`
      - Agrega a lista: `products.add(product)`
   
   e) **Después del while:**
      - Retorna: `products`
   
   f) **En el catch:**
      - Retorna: `new ArrayList<>()` (lista vacía)
   
   **Pregunta:** ¿Por qué retornar lista vacía y no null?

7. **Probar en Main.java (TÚ escribes):**
   
   a) **Llamar al servicio:**
      ```
      List<Product> products = ProductService.getAllProducts();
      ```
   
   b) **Verificar si está vacía:**
      - if (products.isEmpty()) → Mostrar mensaje
   
   c) **Si no está vacía, recorrer:**
      ```
      for (Product p : products) {
          // Imprime cada producto usando toString()
      }
      ```
   
   d) **Mostrar total al final**
   
   **Pregunta:** ¿Qué diferencia hay entre este for y el tradicional con índice?

8. **Depuración obligatoria:**
   - Breakpoint donde creas el objeto Product
   - Ejecuta en debug
   - Inspecciona el objeto recién creado
   - Ve cómo se llena el ArrayList
   - Verifica que tenga todos los productos al final

**✅ Resultado esperado:** 
- Clase Product creada en `models/` con atributos, constructor, getters y toString()
- ProductService.getAllProducts() retorna `List<Product>` en vez de void
- Main.java imprime los productos usando los objetos (no ResultSet directo)
- Ver en consola la lista de productos con formato del toString()
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java
  ├── models/
  │   ├── Movement.java
  │   ├── Vehicle.java
  │   ├── Supplier.java
  │   └── Product.java (NUEVO)
  └── services/
      └── ProductService.java (MODIFICADO)
  ```

**💡 Concepto clave - Separación de responsabilidades:**

```
ANTES (Checkpoint 3.3):
ProductService → Lee BD → Imprime directamente
❌ Service tiene 2 responsabilidades (leer Y mostrar)

AHORA (Checkpoint 3.4):
ProductService → Lee BD → Retorna List<Product>
Main.java → Recibe lista → Imprime
✅ Cada clase tiene UNA responsabilidad
```

**💡 Concepto clave - Mapeo ResultSet → Objeto:**

```
ResultSet (fila de BD):     Objeto Java:
┌─────────────────┐        ┌────────────────────┐
│ id     | "P001" │   →    │ Product            │
│ name   | "Diesel"│   →    │ - id: "P001"       │
│ type   | "Comb." │   →    │ - name: "Diesel"   │
│ unit   | "litros"│   →    │ - type: "Comb."    │
└─────────────────┘        │ - unit: "litros"   │
                           └────────────────────┘

Este proceso se llama "mapeo" o "binding"
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| Constructor con orden incorrecto | Parámetros en orden diferente al ResultSet | Verifica el orden: id, name, type, unit |
| NullPointerException al usar lista | Olvidaste inicializar ArrayList | `new ArrayList<>()` al inicio |
| Lista vacía pero hay datos en BD | No agregas productos a la lista en el while | Verifica `.add(product)` |
| "Cannot find symbol Product" | Product no está en models/ | Verifica el paquete y el import |

**🔍 Comparación lado a lado:**

**Checkpoint 3.3 (solo imprimir):**
```java
while (rs.next()) {
    String id = rs.getString("id");
    System.out.println("ID: " + id);
}
// Los datos desaparecen después del while
```

**Checkpoint 3.4 (crear objetos):**
```java
while (rs.next()) {
    String id = rs.getString("id");
    Product p = new Product(id, ...);
    products.add(p);
}
return products;
// Los datos persisten como objetos reutilizables
```

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 3.5: Queries Parametrizadas con PreparedStatement

**Concepto clave:** PreparedStatement es más seguro y eficiente que Statement porque previene SQL Injection y compila la query una sola vez.

**📍 DÓNDE:** 
- **Modificar:** `ProductService.java` (agregar nuevos métodos)
- **Main.java:** Para PROBAR los filtros

**🎯 PARA QUÉ:** 
En Checkpoint 3.3 y 3.4 usaste queries fijas (sin parámetros). Pero en el mundo real necesitas:
- ✅ **Filtrar por ID:** "Dame el producto con id='P001'"
- ✅ **Filtrar por tipo:** "Dame todos los productos de tipo 'Diesel'"
- ✅ **Búsquedas dinámicas:** El usuario decide qué buscar

**Problema con concatenación de strings (❌ NUNCA HACER ESTO):**
```java
String id = userInput;
String sql = "SELECT * FROM products WHERE id = '" + id + "'";
// ☠️ SI userInput = "' OR '1'='1" → SQL INJECTION
// ☠️ Ejecutaría: SELECT * FROM products WHERE id = '' OR '1'='1'
// ☠️ Retornaría TODOS los productos (vulnerabilidad de seguridad)
```

**Solución con PreparedStatement (✅ CORRECTO):**
```java
String sql = "SELECT * FROM products WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, userInput);
// ✅ El valor se escapa automáticamente (seguro contra SQL Injection)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** TODOS los INSERT/UPDATE/DELETE usarán PreparedStatement
- **Fase 5:** Queries con múltiples parámetros y JOINs
- **Fase 6:** Filtros dinámicos desde el menú del usuario
- **Fase 9:** Reportes con rangos de fechas (parámetros múltiples)

**🎓 Analogía:**
- **Statement:** Carta escrita a mano cada vez (lenta, insegura)
- **PreparedStatement:** Formulario con espacios en blanco (rápido, seguro)

**Prompts sugeridos:**
```text
"¿Qué es SQL Injection y cómo PreparedStatement lo previene?"
"¿Por qué los parámetros de PreparedStatement se marcan con '?'?"
"¿Qué diferencia hay entre setString(), setInt() y setDouble()?"
"¿Por qué PreparedStatement es más rápido que Statement?"
"Muéstrame un ejemplo de SQL Injection sin PreparedStatement."
```

**Diagrama de estructura - PreparedStatement:**

```
PreparedStatement para búsquedas seguras
│
├── Estructura general:
│   ├── Query SQL con "?" en lugar de valores
│   ├── conn.prepareStatement(sql)
│   ├── pstmt.setXxx(posición, valor)
│   └── pstmt.executeQuery()
│
├── Método: getProductById(String id)
│   ├── Tipo retorno: Product (o null si no existe)
│   ├── Query: "SELECT ... WHERE id = ?"
│   │   └── "?" es posición 1
│   ├── Configurar: pstmt.setString(1, id)
│   ├── if (rs.next()): Crear y retornar Product
│   └── else: Retornar null
│
├── Método: getProductsByType(String type)
│   ├── Tipo retorno: List<Product>
│   ├── Query: "SELECT ... WHERE type = ?"
│   ├── Configurar: pstmt.setString(1, type)
│   ├── while (rs.next()): Agregar productos a lista
│   └── Retornar lista (vacía si nada encontrado)
│
└── Seguridad contra SQL Injection
    ├── ❌ Concatenación: "SELECT * WHERE id = '" + id + "'"
    │   → Vulnerable si id = "' OR '1'='1"
    ├── ✅ PreparedStatement: "SELECT * WHERE id = ?"
    │   → Seguro, los valores se escapan automáticamente
    └── Ventaja: También más rápido (compila una sola vez)
```

**Tareas paso a paso:**

1. **Agregar método `getProductById()` en ProductService (TÚ lo escribes):**
   
   a) **Firma del método:**
      - `public static Product getProductById(String id)`
   
   b) **Query SQL con parámetro:**
      ```
      SELECT id, name, type, unit FROM combustibles_products WHERE id = ?
      ```
   
   c) **Imports necesarios:**
      ```
      import java.sql.PreparedStatement;
      ```
   
   d) **Estructura try-with-resources:**
      ```
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql))
      ```
   
   e) **Configurar parámetro:**
      - Usa: `pstmt.setString(1, id)`
      - El "1" es la posición del primer "?" en la query
      - **Pregunta:** ¿Por qué empieza en 1 y no en 0?
   
   f) **Ejecutar y procesar:**
      - `ResultSet rs = pstmt.executeQuery()`
      - if (rs.next()) → Crear y retornar Product
      - else → Retornar null
   
   g) **En el catch:**
      - Retornar null

2. **Agregar método `getProductsByType()` (TÚ lo escribes):**
   
   a) **Firma:**
      - `public static List<Product> getProductsByType(String type)`
   
   b) **Query SQL:**
      ```
      SELECT id, name, type, unit FROM combustibles_products WHERE type = ?
      ```
   
   c) **Similar a getProductById() pero retorna lista:**
      - Crear lista vacía al inicio
      - Configurar parámetro: `pstmt.setString(1, type)`
      - Recorrer ResultSet con while
      - Crear objetos Product y agregarlos a la lista
      - Retornar lista

3. **OPCIONAL - Método `searchProducts()` con 2 parámetros:**
   
   **Desafío:** Crea un método que busque por nombre Y tipo:
   
   a) **Firma:**
      ```
      public static List<Product> searchProducts(String namePattern, String type)
      ```
   
   b) **Query SQL con 2 parámetros:**
      ```
      SELECT id, name, type, unit FROM combustibles_products 
      WHERE name LIKE ? AND type = ?
      ```
   
   c) **Configurar parámetros:**
      ```
      pstmt.setString(1, "%" + namePattern + "%");
      pstmt.setString(2, type);
      ```
   
   d) **Pregunta:** ¿Qué hace el "%" en SQL LIKE?

4. **Probar en Main.java (TÚ lo escribes):**
   
   a) **Prueba 1 - Buscar por ID:**
      ```
      Product product = ProductService.getProductById("P001");
      
      if (product != null) {
          System.out.println(product);
      } else {
          System.out.println("Producto no encontrado");
      }
      ```
   
   b) **Prueba 2 - Buscar por tipo:**
      ```
      List<Product> dieselProducts = ProductService.getProductsByType("Diesel");
      
      for (Product p : dieselProducts) {
          System.out.println(p);
      }
      System.out.println("Total: " + dieselProducts.size());
      ```
   
   c) **Prueba 3 (opcional) - Búsqueda múltiple:**
      ```
      List<Product> results = ProductService.searchProducts("Gas", "Combustible");
      ```

5. **Depuración obligatoria - Entiende SQL Injection:**
   
   a) **Coloca breakpoint** después de configurar el parámetro
   b) **En el debugger**, evalúa: `pstmt.toString()`
      - Verás cómo se ve la query compilada
   c) **Intenta pasar un valor "malicioso":**
      ```
      ProductService.getProductById("' OR '1'='1");
      ```
   d) **Verifica:** PreparedStatement lo maneja de forma segura (escapa comillas)
   e) **Pregunta:** ¿Qué pasaría con Statement + concatenación de strings?

6. **Comparación lado a lado - Aprende la diferencia:**
   
   **❌ INSEGURO (NO hacer esto):**
   ```
   String userInput = "' OR '1'='1";
   String sql = "SELECT * FROM products WHERE id = '" + userInput + "'";
   // Query real: SELECT * FROM products WHERE id = '' OR '1'='1'
   // ☠️ Retorna TODOS (SQL Injection exitoso)
   ```
   
   **✅ SEGURO (Usa esto siempre):**
   ```
   String userInput = "' OR '1'='1";
   String sql = "SELECT * FROM products WHERE id = ?";
   pstmt = conn.prepareStatement(sql);
   pstmt.setString(1, userInput);
   // ✅ Busca literalmente un ID con ese valor (seguro)
   ```

**✅ Resultado esperado:** 
- Método `getProductById()` que retorna un solo producto o null
- Método `getProductsByType()` que retorna lista filtrada
- Main.java prueba ambos métodos exitosamente
- Entiendes la diferencia entre Statement y PreparedStatement
- Sabes por qué PreparedStatement es obligatorio para queries con parámetros
- Estructura completa de Fase 3:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java
  ├── models/
  │   ├── Movement.java
  │   ├── Vehicle.java
  │   ├── Supplier.java
  │   └── Product.java
  ├── managers/
  │   └── MovementManager.java
  └── services/
      └── ProductService.java (con getAllProducts, getProductById, getProductsByType)
  ```

**💡 Concepto clave - Índices de parámetros:**

```
SQL: "SELECT * FROM products WHERE type = ? AND price > ?"
                                          ↑              ↑
                                       índice 1      índice 2

Java:
pstmt.setString(1, "Diesel");    // Configura primer ?
pstmt.setDouble(2, 3.0);         // Configura segundo ?
```

**💡 Concepto clave - Tipos de setters:**

```java
pstmt.setString(1, "texto");     // Para VARCHAR, TEXT
pstmt.setInt(2, 100);            // Para INT
pstmt.setDouble(3, 3.45);        // Para DECIMAL, FLOAT, DOUBLE
pstmt.setBoolean(4, true);       // Para BOOLEAN, TINYINT(1)
pstmt.setDate(5, sqlDate);       // Para DATE
pstmt.setTimestamp(6, timestamp);// Para DATETIME, TIMESTAMP

// IMPORTANTE: El tipo en Java debe coincidir con el tipo en MySQL
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| "Invalid parameter index" | Índice incorrecto en setString() | Verifica que usas 1, 2, 3... según el orden de los "?" |
| "Parameter not set" | Olvidaste configurar un "?" | Cada "?" debe tener su pstmt.setXxx() correspondiente |
| NullPointerException | Retornaste null y no validaste | Siempre verifica `if (product != null)` antes de usar |
| Query no encuentra datos | Valor exacto no coincide | Verifica mayúsculas/minúsculas, espacios, etc. |

**🔍 Depuración avanzada - Ver query compilada:**

En el debugger, evalúa:
```java
pstmt.toString()
```
Verás algo como:
```
PreparedStatement: SELECT * FROM products WHERE id = 'P001'
```
Esto te muestra cómo se ve la query con los parámetros ya sustituidos.

**📊 Comparación completa:**

| Característica | Statement | PreparedStatement |
|----------------|-----------|-------------------|
| Seguridad | ❌ Vulnerable a SQL Injection | ✅ Seguro contra SQL Injection |
| Velocidad | ❌ Más lento (compila cada vez) | ✅ Más rápido (compila una vez) |
| Legibilidad | ❌ Concatenación confusa | ✅ Query limpia con "?" |
| Uso típico | Solo queries sin parámetros | Queries con parámetros |
| En producción | ❌ NO usar con input del usuario | ✅ SIEMPRE usar con input del usuario |

**⏱️ Tiempo estimado:** 3-4 horas

---

---

## ✏️ Refuerzos adicionales de la fase

**Conceptos importantes a dominar:**

1. **try-with-resources:**
   - ¿Por qué es mejor que try-finally?
   - ¿Qué interfaces deben implementar los recursos? (AutoCloseable)
   - ¿Qué pasa si hay excepción durante el cierre?
   - ¿Puedes anidar múltiples recursos en un solo try?

2. **SQLException:**
   - ¿Qué información contiene? (mensaje, SQLState, código de error)
   - ¿Cómo extraer el código de error específico? (`e.getErrorCode()`)
   - ¿Cuáles son los errores más comunes? (timeout, credenciales, tabla no existe)
   - ¿Cómo diferenciar error de conexión vs error de SQL?

3. **Arquitectura JDBC:**
   - DriverManager → Connection → Statement/PreparedStatement → ResultSet
   - ¿Para qué sirve cada componente?
   - ¿Cuándo se cierra cada uno?
   - ¿Por qué es importante cerrar en orden inverso al que se abrieron?

4. **ResultSet como cursor:**
   - ¿Por qué empieza ANTES de la primera fila?
   - ¿Qué retorna rs.next() cuando no hay más filas?
   - ¿Puedes moverte hacia atrás en un ResultSet normal?
   - ¿Qué pasa si intentas rs.getString() antes del primer rs.next()?

5. **Mapeo ResultSet → Objetos:**
   - ¿Por qué es mejor retornar objetos que imprimir directamente?
   - ¿Dónde va la lógica de mapeo: en Service o en un Mapper separado?
   - ¿Qué haces si una columna puede ser NULL?

6. **PreparedStatement vs Statement:**
   - ¿Cómo previene SQL Injection?
   - ¿Por qué es más rápido?
   - ¿Cuándo está bien usar Statement?
   - ¿Puedes reutilizar un PreparedStatement para múltiples ejecuciones?

**Ejercicios de refuerzo:**

1. **Refactorizar VehicleService:**
   - Crea `Vehicle.java` en models (siguiendo patrón de Product)
   - Crea `VehicleService.java` en services con:
     - `getAllVehicles()` - retorna List<Vehicle>
     - `getVehicleById(String id)` - retorna Vehicle
     - `getVehiclesByType(String type)` - retorna List<Vehicle>

2. **Refactorizar SupplierService:**
   - Crea `Supplier.java` en models
   - Crea `SupplierService.java` en services con métodos similares

3. **Queries más complejas:**
   - En ProductService, agrega `getProductsWithStock(double minStock)`
   - Practica con ORDER BY, LIMIT

4. **Manejo de errores:**
   - Crea método helper `handleSQLException(SQLException e)` que imprima:
     - Mensaje descriptivo
     - SQLState
     - Error code
   - Úsalo en todos los catch de tus services

5. **Mejora de DatabaseConnection:**
   - Agrega método `testConnectionWithTimeout(int seconds)`
   - Experimenta con connection pooling (avanzado)

**Desafíos adicionales:**

1. **Logging básico:**
   - En vez de `System.out.println()`, investiga java.util.logging
   - Agrega logs en DatabaseConnection (conexión abierta/cerrada)

2. **Configuración externa:**
   - Investiga `Properties` en Java
   - Mueve credenciales a un archivo `.properties`

3. **Transacciones básicas:**
   - Investiga `conn.setAutoCommit(false)`
   - Prueba commit/rollback manual

---

## 🧾 Checklist de salida de Fase 3

**Configuración y conexión:**
- [ ] Agregué correctamente la dependencia JDBC en pom.xml
- [ ] Puedo explicar qué hace el driver JDBC y por qué lo necesito
- [ ] Mi clase DatabaseConnection centraliza la configuración de BD
- [ ] Probé la conexión exitosamente con testConnection()
- [ ] Entiendo la diferencia entre URL, usuario y contraseña en la connection string

**Lectura de datos (SELECT):**
- [ ] Ejecuté mi primera query SELECT y vi resultados en consola
- [ ] Entiendo el flujo: Connection → Statement → executeQuery → ResultSet → rs.next()
- [ ] Sé usar try-with-resources para cerrar conexiones automáticamente
- [ ] Puedo explicar qué es ResultSet y cómo funciona como cursor

**Mapeo a objetos:**
- [ ] Creé la clase Product en models/ con atributos privados
- [ ] Implementé constructor, getters y toString() para Product
- [ ] Modifiqué ProductService para retornar List<Product> en vez de void
- [ ] Entiendo la diferencia entre imprimir desde ResultSet vs retornar objetos

**Queries parametrizadas:**
- [ ] Implementé getProductById() usando PreparedStatement
- [ ] Implementé getProductsByType() con filtro dinámico
- [ ] Entiendo qué es SQL Injection y cómo PreparedStatement lo previene
- [ ] Sé usar pstmt.setString(), pstmt.setInt(), pstmt.setDouble()
- [ ] Puedo explicar por qué PreparedStatement es obligatorio con input del usuario

**Manejo de errores:**
- [ ] Puedo explicar qué es SQLException y cómo manejarla
- [ ] Sé interpretar los mensajes de error comunes (conexión, credenciales, tabla no existe)
- [ ] Implemento try-catch correctamente en todos los métodos de servicio

**Arquitectura y organización:**
- [ ] Entiendo la separación: config/ vs models/ vs services/
- [ ] Mi código sigue el patrón: Service retorna datos, Main los muestra
- [ ] DatabaseConnection se usa en todos los Services (no repito credenciales)

**Documentación:**
- [ ] Documenté en JAVA_LEARNING_LOG.md los aprendizajes y problemas encontrados
- [ ] Actualicé JAVA_NEXT_STEPS.md con dudas y siguiente objetivo

**Estructura final del proyecto:**
```
forestech-cli-java/
├── pom.xml (con dependencia JDBC)
└── src/main/java/com/forestech/
    ├── Main.java
    ├── AppConfig.java
    ├── MenuHelper.java
    ├── DataDisplay.java
    ├── InputHelper.java
    ├── config/
    │   └── DatabaseConnection.java
    ├── models/
    │   ├── Movement.java
    │   ├── Vehicle.java
    │   ├── Supplier.java
    │   └── Product.java (NUEVO)
    ├── managers/
    │   └── MovementManager.java
    └── services/
        └── ProductService.java (NUEVO)
```

**⚠️ Problemas comunes y soluciones:**

| Problema | Posible causa | Solución |
|----------|--------------|----------|
| "No suitable driver found" | Driver no descargado | Ejecuta `mvn clean install` |
| "Cannot connect to server" | MySQL no está corriendo en WSL | Ejecuta `sudo service mysql start` |
| "Access denied for user" | Credenciales incorrectas | Verifica usuario/contraseña en DatabaseConnection |
| "Unknown database 'FORESTECH'" | BD no creada | Crea BD FORESTECH con `CREATE DATABASE FORESTECH;` |
| NullPointerException en Service | Olvidaste inicializar lista/objeto | Revisa que creas `new ArrayList<>()` o retornas objeto |
| "Column not found" | Nombre incorrecto en ResultSet | Verifica que coincida con SELECT (mayúsculas/minúsculas) |
| SQL Injection (vulnerable) | Usaste concatenación + Statement | SIEMPRE usa PreparedStatement con parámetros |

---

## 🚀 Próximo paso: FASE 4 - Operaciones CRUD

En la siguiente fase aprenderás a:
- ✅ **Insertar datos (INSERT)** desde objetos Java a la BD usando PreparedStatement
- ✅ **Actualizar registros (UPDATE)** con validaciones y manejo de errores
- ✅ **Eliminar datos (DELETE)** con precauciones (soft delete vs hard delete)
- ✅ **Transacciones** para operaciones que deben ser atómicas (todo o nada)
- ✅ **MovementService completo** con todas las operaciones CRUD

**¿Por qué CRUD es importante?**

Ahora solo LEES datos (operación R de CRUD). Con las operaciones CREATE, UPDATE y DELETE completas:
- Podrás **crear movimientos** desde tu aplicación (no solo desde MySQL manualmente)
- Podrás **editar movimientos** si hay errores en los datos
- Podrás **eliminar movimientos** incorrectos o duplicados
- Forestech será una aplicación **completamente funcional** (no solo de consulta)

**Diferencia clave vs Fase 3:**
```
FASE 3 (solo lectura):
Usuario → Main → ProductService.getAllProducts() → BD (SELECT)
                                                 ↓
                                          Retorna datos

FASE 4 (escritura completa):
Usuario → Main → MovementService.insertMovement(movement) → BD (INSERT)
                                                          ↓
                                                   Guarda datos

Usuario → Main → MovementService.updateMovement(movement) → BD (UPDATE)
                                                          ↓
                                                   Modifica datos

Usuario → Main → MovementService.deleteMovement(id) → BD (DELETE)
                                                     ↓
                                                  Elimina datos
```

**Estructura que tendrás al terminar Fase 4:**
```
com.forestech/
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java (mejorado con validaciones)
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Product.java
└── services/
    ├── ProductService.java (SELECT)
    ├── MovementService.java (CRUD completo - NUEVO)
    ├── VehicleService.java (CRUD completo - NUEVO)
    └── SupplierService.java (CRUD completo - NUEVO)
```

**⏱️ Tiempo estimado Fase 4:** 8-12 horas distribuidas en 1-2 semanas

**⏱️ Tiempo total Fase 3:** 11-14 horas distribuidas en 1 semana

---

## 📚 Recursos adicionales

**Documentación oficial:**
- [JDBC Tutorial de Oracle](https://docs.oracle.com/javase/tutorial/jdbc/)
- [SQL Server JDBC Driver Documentation](https://learn.microsoft.com/en-us/sql/connect/jdbc/)

**Videos recomendados:**
- Búsqueda: "JDBC Java Tutorial" (conceptos básicos)
- Búsqueda: "PreparedStatement vs Statement" (seguridad)
- Búsqueda: "SQL Injection examples" (entender vulnerabilidades)

**Práctica adicional:**
- [SQLZoo](https://sqlzoo.net/) - Practica queries SQL interactivas
- [HackerRank SQL](https://www.hackerrank.com/domains/sql) - Ejercicios SQL progresivos

**Conceptos relacionados para investigar:**
- Connection pooling (HikariCP, C3P0)
- ORM (Object-Relational Mapping) - Hibernate, JPA
- DAO Pattern (Data Access Object)
- Repository Pattern

**¡Recuerda documentar todo en JAVA_LEARNING_LOG.md!** 📝
