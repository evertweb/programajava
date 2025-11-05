# ⚠️ ARCHIVADO: FASE 3 (versión anterior)

Este documento queda como referencia histórica. La versión reestructurada y vigente de la Fase 3 está aquí:

→ FASE 3 (REWORK): `FASE_03_SQL.md`

— Continúa leyendo bajo tu propio criterio si necesitas consultar detalles antiguos.

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

**Diagrama de la tabla oil_products:**
```
oil_products
├── id (VARCHAR(10), PRIMARY KEY) - Identificador único (ej: FUEL-001)
├── name (VARCHAR(100)) - Nombre del producto (ej: Diesel Premium)
├── unidadDeMedida (VARCHAR(20)) - Unidad de medida (Litros, Galones)
└── priceXUnd (DOUBLE) - Precio por unidad (ej: 5200.50)
```

**Contexto Forestech:**
- `id`: Identificador único generado automáticamente (formato FUEL-XXXXXXXX)
- `name`: Nombre comercial del combustible
- `unidadDeMedida`: Cómo se vende (Litros para clientes locales, Galones para exportación)
- `priceXUnd`: Precio por litro/galón en pesos chilenos

**Tareas (TÚ ejecutas):**

1. **Crear la tabla oil_products:**
   
   ```sql
   CREATE TABLE oil_products (
       id VARCHAR(10) PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       unidadDeMedida VARCHAR(20) NOT NULL,
       priceXUnd DOUBLE NOT NULL
   );
   ```
   
   - Resultado esperado: `Query OK, 0 rows affected`
   - **Pregunta:** ¿Qué significa PRIMARY KEY? ¿Por qué id es PRIMARY KEY?
   - **Pregunta:** ¿Qué hace NOT NULL?

2. **Verificar estructura de la tabla:**
   ```sql
   DESCRIBE oil_products;
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

3. **Insertar datos de prueba (combustibles de Forestech):**
   
   ```sql
   INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd) VALUES
   ('FUEL-001', 'Diesel Regular', 'Litros', 5200.00),
   ('FUEL-002', 'Gasolina 97 Premium', 'Galones', 22500.00),
   ('FUEL-003', 'Gasolina 93', 'Litros', 6800.00);
   ```
   
   - Resultado esperado: `Query OK, 3 rows affected`
   - **Pregunta:** ¿Por qué van entre comillas simples ' los valores?

4. **Verificar que se insertaron:**
   ```sql
   SELECT * FROM oil_products;
   ```
   
   - Resultado esperado:
   ```
   +----------+---------------------+----------------+------------+
   | id       | name                | unidadDeMedida | priceXUnd  |
   +----------+---------------------+----------------+------------+
   | FUEL-001 | Diesel Regular      | Litros         | 5200.00    |
   | FUEL-002 | Gasolina 97 Premium | Galones        | 22500.00   |
   | FUEL-003 | Gasolina 93         | Litros         | 6800.00    |
   +----------+---------------------+----------------+------------+
   ```

5. **Practicar consultas básicas (TÚ ejecutas cada una):**
   
   a) **Filtrar por unidad de medida:**
   ```sql
   SELECT * FROM oil_products WHERE unidadDeMedida = 'Litros';
   ```
   
   b) **Ordenar por precio (más barato primero):**
   ```sql
   SELECT * FROM oil_products ORDER BY priceXUnd ASC;
   ```
   
   c) **Contar productos:**
   ```sql
   SELECT COUNT(*) FROM oil_products;
   ```
   
   d) **Actualizar precio de un producto:**
   ```sql
   UPDATE oil_products SET priceXUnd = 5500.00 WHERE id = 'FUEL-001';
   SELECT * FROM oil_products WHERE id = 'FUEL-001';
   ```
   
   e) **Revertir precio:**
   ```sql
   UPDATE oil_products SET priceXUnd = 5200.00 WHERE id = 'FUEL-001';
   ```
   
   **Pregunta:** ¿Qué pasaría si olvidas el WHERE en un UPDATE?

**✅ Resultado esperado:** 
- Tabla oil_products creada con 4 columnas (id, name, unidadDeMedida, priceXUnd)
- 3 combustibles de prueba insertados con precios reales
- Entiendes las consultas básicas SELECT, INSERT, UPDATE
- Conoces la estructura real que usarás en Forestech
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
CREATE TABLE product (
    id INT,                    -- ❌ No podemos usar "P001"
    name CHAR(10),             -- ❌ "Diesel Regular" tiene 14 caracteres
    price DOUBLE,              -- ❌ Errores de redondeo con dinero
    created_date VARCHAR(50)   -- ❌ No puedes hacer cálculos con texto
);

-- ✅ CORRECTO
CREATE TABLE product (
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
                                           (apunta al id de product)
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
| **FOREIGN KEY** | Relacionar tablas | `FOREIGN KEY (product_id) REFERENCES product(id)` | Cuando vinculas con otra tabla |
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
│   └── Products.java (YA EXISTE - se actualiza con nuevo constructor)
├── managers/
│   └── MovementManager.java
└── services/
    └── ProductServices.java (NUEVO - acceso a BD)
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
- **Crear archivo:** `ProductServices.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
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
ProductServices.java (acceso a BD)
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
- **ProductServices**: Bibliotecario que busca libros en el sistema
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
ProductServices.java → getAllProducts()
│
├── Tipo retorno: void (por ahora)
├── Modificadores: public static
├── Sin parámetros
├── Manejo: try-catch
│
├── 1. Definir query SQL
│   └── SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products
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
│   │   ├── Extraer: rs.getString("unidadDeMedida")
│   │   ├── Extraer: rs.getDouble("priceXUnd")
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

2. **Crear la clase ProductServices:**
   - Clic derecho en `services` → New → Java Class → "ProductServices"
   - Constructor privado (es una Utility Class)

3. **Imports necesarios (TÚ agregas):**
   - De DatabaseConnection: `com.forestech.config.DatabaseConnection`
   - De SQL: `Connection`, `Statement`, `ResultSet`, `SQLException` de `java.sql`

4. **Implementar getAllProducts() (TÚ lo escribes):**

   a) **Definir query:**
      - Variable: `String sql`
      - Valor: `"SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products"`
   
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
        - `rs.getString("unidadDeMedida")`
        - `rs.getDouble("priceXUnd")`
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
     SELECT * FROM oil_products;
     ```
   - Si no existe, créala o ajusta el nombre en la query
   - Verifica que haya al menos 1-2 productos para probar

6. **Probar en Main.java:**
   - Agrega (donde corresponda):
     ```java
     System.out.println("\n=== LECTURA DE PRODUCTOS ===");
     ProductServices.getAllProducts();
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
  │   └── ProductServices.java (NUEVO)
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

## ✅ Checkpoint 3.4: Mapear ResultSet a Objetos Products

**Concepto clave:** Convertir filas de la BD (ResultSet) en objetos Java te permite trabajar con POO en vez de datos planos.

**📍 DÓNDE:** 
- **Usar clase existente:** `Products.java` en `forestech-cli-java/src/main/java/com/forestech/models/` (YA LA TIENES CREADA ✅)
- **Modificar:** `Products.java` (agregar constructor adicional para cargar desde BD)
- **Crear:** `ProductServices.java` (método para retornar lista de objetos)
- **Main.java:** Para PROBAR la lectura de objetos

**🎯 PARA QUÉ:** 
En Checkpoint 3.3 solo imprimiste datos. Eso está bien para aprender JDBC, pero tiene limitaciones:
- ❌ No puedes manipular los datos después de leerlos (no hay objetos)
- ❌ No puedes pasar los datos a otras funciones
- ❌ No aplicas POO (solo trabajas con Strings sueltos)
- ❌ No puedes agregar comportamiento (cálculos, validaciones)

Con objetos Products:
- ✅ **POO completa:** Cada producto es un objeto con atributos y métodos
- ✅ **Reutilización:** Puedes pasar la lista de productos a otras funciones
- ✅ **Mantenibilidad:** Si agregas atributos, solo cambias la clase Products
- ✅ **Comportamiento:** Puedes agregar métodos como `calculateStock()`, `isLowStock()`, etc.

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Crearás métodos CRUD completos y los mapearás desde `ProductServices`
- **Fase 5:** Los managers trabajarán con listas de objetos (no con ResultSet directo)
- **Fase 6:** Mostrarás productos en el menú usando objetos
- **Fase 9:** Generarás reportes procesando listas de objetos

**🎓 Analogía:**
```
ResultSet (Checkpoint 3.3):  
"Tengo estos datos: Diesel, Galones, 8500..."
→ Solo strings sueltos, como papeles desordenados

Products (Checkpoint 3.4):
Products p = new Products("FUEL-001", "Diesel", "Galones", 8500.0);
→ Objeto estructurado, como una carpeta organizada
```

**⚠️ IMPORTANTE - Ya tienes la clase Products:**
- ✅ Ya creaste `Products.java` en fases anteriores
- ✅ Ya tiene constructor que GENERA ID automático (para crear nuevos)
- 🆕 FALTA: Constructor que RECIBE ID (para cargar desde BD)

**¿Por qué necesitas DOS constructores?**
```
Escenario 1: Usuario crea producto nuevo desde menú
→ Usa constructor sin ID (genera automáticamente)
→ Products p = new Products("Diesel", "Galones", 8500.0);

Escenario 2: Cargas producto existente desde BD
→ Usa constructor con ID (ya existe en BD)
→ Products p = new Products("FUEL-001", "Diesel", "Galones", 8500.0);
```

Esto se llama **Sobrecarga de Constructores (Constructor Overloading)**.

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre imprimir desde ResultSet vs crear objetos?"
"¿Por qué es mejor retornar List<Products> que imprimir directamente?"
"¿Cómo mapeo cada columna del ResultSet a un atributo del objeto?"
"¿Qué es un ArrayList y cuándo usarlo?"
"¿Por qué necesito dos constructores en Products?"
"¿Qué es sobrecarga de constructores?"
```

**Tareas paso a paso:**

1. **Crear la clase Product en models/:**
   
   **Atributos que debe tener (TÚ defines como privados):**
   - id (String)
   - name (String)
   - unidadDeMedida (String) - ej: "Litros", "Galones"
   - priceXUnd (double) - precio por unidad
   
   **Pregunta guía:** ¿Por qué Product va en `models/` y no en `services/`?

**Diagrama de estructura - Clase Products y mapeo ResultSet:**

```
Products.java (en models/) - YA EXISTE ✅
│
├── Atributos privados (YA LOS TIENES)
│   ├── String id
│   ├── String name
│   ├── String unidadDeMedida
│   └── double priceXUnd
│
├── Constructor 1 - CREAR nuevo (YA LO TIENES)
│   └── Products(name, unidadDeMedida, priceXUnd)
│       └── Genera ID automático con IdGenerator
│
├── Constructor 2 - CARGAR desde BD (FALTA - LO AGREGARÁS)
│   └── Products(id, name, unidadDeMedida, priceXUnd)
│       └── Usa el ID que recibe (no genera nuevo)
│
├── Getters (YA LOS TIENES)
│   ├── getId()
│   ├── getName()
│   ├── getUnidadDeMedida()
│   └── getPriceXUnd()
│
└── toString() (YA LO TIENES)
    ├── @Override
    └── Formato: Tabla bonita con datos del producto

Mapeo: ResultSet fila → Objeto Products
│
├── Extraer: rs.getString("id") → String id
├── Extraer: rs.getString("name") → String name
├── Extraer: rs.getString("unidad_medida") → String unidadDeMedida
├── Extraer: rs.getDouble("price_x_und") → double priceXUnd
│
└── new Products(id, name, unidadDeMedida, priceXUnd) → Objeto creado
    └── Usar constructor con 4 parámetros (el nuevo)
```

**Tareas paso a paso:**

1. **Revisar tu clase Products existente:**
   - Abre `models/Products.java`
   - Verifica que tienes los atributos: id, name, unidadDeMedida, priceXUnd
   - Verifica que tienes constructor de 3 parámetros (genera ID automático)
   - Verifica que tienes getters y toString()
   - **Pregunta reflexiva:** ¿Qué hace el constructor actual con el ID?

2. **PENSAR antes de codificar - Sobrecarga de Constructores:**
   
   **Reflexiona sobre estas preguntas:**
   - ¿Qué diferencia hay entre crear un producto nuevo vs cargar uno existente de la BD?
   - Tu constructor actual genera ID automático. ¿Sirve eso para cargar productos desde BD?
   - ¿Qué información necesitas recibir para recrear un producto que ya existe?
   - ¿Puede una clase tener dos constructores?
   
   **Pseudocódigo del nuevo constructor:**
   ```
   CONSTRUCTOR Products con 4 parámetros:
       RECIBE como parámetros: id, name, unidadDeMedida, priceXUnd
       
       ASIGNAR cada parámetro al atributo correspondiente:
           this.id = id recibido (NO generar con IdGenerator)
           this.name = name recibido
           this.unidadDeMedida = unidadDeMedida recibido
           this.priceXUnd = priceXUnd recibido
   
   NOTA: NO llames a IdGenerator aquí
         El ID ya viene de la base de datos
   ```
   
   **Ubicación:** Escribe el nuevo constructor justo después del constructor de 3 parámetros
   
   **Pregunta clave:** ¿Cómo sabe Java cuál constructor usar si ambos se llaman "Products"?
   
   **Pista:** Java distingue por el número y tipo de parámetros

3. **AHORA SÍ - Escribe el constructor:**
   - TÚ lo escribes siguiendo el pseudocódigo
   - Usa `this` para asignar parámetros a atributos
   - NO uses `IdGenerator`
   - Agrega comentario explicando para qué sirve este constructor

4. **Verificar compilación:**
   - Guarda el archivo
   - Verifica que no hay errores rojos en el IDE
   - **Pregunta:** ¿Por qué Java permite dos constructores con el mismo nombre?
   - **Término técnico:** Esto se llama "Constructor Overloading"

5. **Prueba rápida en Main.java (antes de ProductServices):**
   
   **Pseudocódigo de la prueba:**
   ```
   EN Main.java:
   
   CREAR objeto con constructor de 3 parámetros (genera ID):
       Products nuevo = new Products("Gasolina", "Galones", 10000.0)
       IMPRIMIR "ID generado: " + nuevo.getId()
   
   CREAR objeto con constructor de 4 parámetros (recibe ID):
       Products cargado = new Products("FUEL-999", "Diesel", "Galones", 8500.0)
       IMPRIMIR "ID recibido: " + cargado.getId()
   
   COMPARAR ambos IDs
   ```
   
   - TÚ escribes este código de prueba
   - Ejecuta y verifica los resultados
   - **Pregunta:** ¿Qué diferencia ves en los IDs de ambos objetos?
   - **Explicación esperada:** El primero tiene formato FUEL-XXXX generado, el segundo es "FUEL-999" exacto

6. **NO continuar a ProductServices todavía:**
   - Primero asegúrate de entender completamente la sobrecarga de constructores
   - Explica con tus palabras: ¿Por qué necesitamos dos constructores?
   - Confirma que ambas pruebas funcionan correctamente

7. **Crear ProductServices.java (TÚ creas el archivo):**
   - Clic derecho en `services` → New → Java Class → "ProductServices"
   - Usa el nombre correcto: `ProductServices` (plural, como ya lo tienes en tu estructura)
   - **Pregunta:** ¿Por qué va en `services/` y no en `models/`?

8. **Estructura básica de ProductServices (Pseudocódigo):**
   
   ```
   CLASE ProductServices:
       
       MÉTODO getAllProducts():
           RETORNA: List<Products>
           
           PSEUDOCÓDIGO:
           1. CREAR lista vacía de Products
           2. OBTENER conexión a BD
           3. CREAR consulta SQL "SELECT * FROM product"
           4. EJECUTAR query
           5. MIENTRAS haya filas en ResultSet:
               a. EXTRAER datos de cada columna
               b. CREAR objeto Products con constructor de 4 parámetros
               c. AGREGAR objeto a la lista
           6. CERRAR recursos
           7. RETORNAR lista
           
           EN CASO DE ERROR:
               - IMPRIMIR error
               - RETORNAR lista vacía
   ```
   
   **Antes de escribir el código real:**
   - Lee el pseudocódigo línea por línea
   - Identifica qué partes ya sabes hacer (de Checkpoint 3.3)
   - Identifica qué es nuevo (crear objetos, lista)
   - **Pregunta:** ¿Por qué retornamos lista vacía en vez de null?

9. **Escribir ProductServices.getAllProducts() - PASO A PASO:**
   
   a) **Firma del método:**
      ```java
      // TÚ escribes:
      public static List<Products> getAllProducts() {
          // ... código aquí
      }
      ```
      **Pregunta:** ¿Por qué `static`? ¿Por qué `List<Products>`?
   
   b) **Imports necesarios:**
      - `java.sql.*` (para Connection, PreparedStatement, ResultSet)
      - `java.util.List` y `java.util.ArrayList`
      - `com.forestech.models.Product`
      - `com.forestech.config.DatabaseConnection`
   
   c) **Crear lista al inicio del método:**
      ```java
      // TÚ escribes:
      List<Products> product = new ArrayList<>();
      ```
      **Pregunta:** ¿Qué es `ArrayList`? ¿Por qué `<Products>`?
   
   d) **Bloque try-with-resources (como en Checkpoint 3.3):**
      ```java
      // TÚ escribes la estructura:
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM product");
           ResultSet rs = pstmt.executeQuery()) {
          
          // ... código del while aquí
          
      } catch (SQLException e) {
          // ... manejo de error
      }
      ```
   
   e) **Dentro del while - MAPEO (LA PARTE NUEVA):**
      
      **Pseudocódigo detallado:**
      ```
      MIENTRAS rs.next():
          // Paso 1: Extraer datos de cada columna
          VARIABLE id = rs.getString("id")
          VARIABLE name = rs.getString("name")
          VARIABLE unidadDeMedida = rs.getString("unidad_medida")
          VARIABLE priceXUnd = rs.getDouble("price_x_und")
          
          // Paso 2: Crear objeto con constructor de 4 parámetros
          VARIABLE product = new Products(id, name, unidadDeMedida, priceXUnd)
          
          // Paso 3: Agregar a la lista
          product.add(product)
      ```
      
      - TÚ escribes este código en Java
      - **Pregunta:** ¿Por qué usamos el constructor de 4 parámetros aquí?
      - **Pregunta:** ¿Qué hace `product.add(product)`?
   
   f) **Después del while:**
      ```java
      // TÚ escribes:
      return product;
      ```
      **Pregunta:** ¿Qué contiene `product` en este punto?
   
   g) **En el catch:**
      ```java
      // TÚ escribes:
      catch (SQLException e) {
          System.err.println("Error al obtener productos: " + e.getMessage());
          return new ArrayList<>();  // Lista vacía
      }
      ```
      **Pregunta:** ¿Por qué retornar lista vacía y no `null`?

10. **Probar en Main.java - PASO A PASO:**
    
    **Pseudocódigo de la prueba:**
    ```
    EN Main.java:
    
    1. LLAMAR al servicio:
       VARIABLE product = ProductServices.getAllProducts()
    
    2. VERIFICAR si está vacía:
       SI product.isEmpty():
           IMPRIMIR "No hay productos"
           RETORNAR
    
    3. IMPRIMIR encabezado:
       IMPRIMIR "=== PRODUCTOS DESDE BD ==="
    
    4. RECORRER lista:
       PARA CADA product EN product:
           IMPRIMIR product (llama toString() automáticamente)
    
    5. IMPRIMIR total:
       IMPRIMIR "Total: " + product.size() + " productos"
    ```
    
    - TÚ escribes este código en Java
    - **Pregunta:** ¿Qué es `product.size()`?
    - **Pregunta:** ¿Cómo funciona el `for-each` loop?

11. **Ejecutar y verificar:**
    - Ejecuta Main.java
    - Deberías ver los productos con el formato del `toString()`
    - Verifica que el total coincida con los registros en la BD
    - **Depuración:** Pon breakpoint en la línea `product.add(product)` y ve cómo crece la lista

12. **Depuración obligatoria (para aprender):**
    - Breakpoint en `while (rs.next())`
    - Breakpoint donde creas el objeto `new Products(...)`
    - Ejecuta en modo debug
    - Observa:
      * Valores del ResultSet
      * Objeto Products recién creado
      * Cómo crece el ArrayList
      * Contenido final de la lista

**✅ Resultado esperado:** 
- Clase `Products` ACTUALIZADA con segundo constructor (4 parámetros) ✅
- Constructor original de 3 parámetros sigue funcionando (genera ID)
- Constructor nuevo de 4 parámetros recibe ID (para cargar de BD)
- `ProductServices.java` creado con método `getAllProducts()` que retorna `List<Products>`
- Main.java imprime los productos usando objetos (no ResultSet directo)
- Ver en consola la lista de productos con formato del `toString()` de Products
- Comprendes qué es sobrecarga de constructores y por qué se usa
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java (MODIFICADO - pruebas)
  ├── config/
  │   └── DatabaseConnection.java
  ├── models/
  │   ├── Movement.java
  │   ├── Vehicle.java
  │   ├── Supplier.java
  │   └── Products.java (ACTUALIZADO - nuevo constructor)
  └── services/
      └── ProductServices.java (NUEVO - getAllProducts con objetos)
  ```

**💡 Concepto clave - Separación de responsabilidades:**

```
ANTES (Checkpoint 3.3):
ProductServices → Lee BD → Imprime directamente
❌ Service tiene 2 responsabilidades (leer Y mostrar)

AHORA (Checkpoint 3.4):
ProductServices → Lee BD → Retorna List<Products>
Main.java → Recibe lista → Imprime
✅ Cada clase tiene UNA responsabilidad
```

**💡 Concepto clave - Sobrecarga de Constructores:**

```
Tu clase Products ahora tiene DOS constructores:

Constructor 1 (3 parámetros - CREAR nuevo):
Products(name, unidadDeMedida, priceXUnd)
├── Genera ID automático con IdGenerator
└── Uso: Cuando el usuario crea un producto desde el menú

Constructor 2 (4 parámetros - CARGAR existente):
Products(id, name, unidadDeMedida, priceXUnd)
├── Usa el ID que recibe (no genera)
└── Uso: Cuando cargas productos desde la BD

Java distingue cuál usar por el NÚMERO de parámetros:
new Products("Diesel", "Galones", 8500.0)        → Llama constructor 1
new Products("FUEL-001", "Diesel", "Galones", 8500.0) → Llama constructor 2
```

**💡 Concepto clave - Mapeo ResultSet → Objeto:**

```
ResultSet (fila de BD):          Objeto Java:
┌──────────────────────────┐    ┌────────────────────────────┐
│ id           | "FUEL-001"│ →  │ Products                   │
│ name         | "Diesel"  │ →  │ - id: "FUEL-001"           │
│ unidad_medida| "Galones" │ →  │ - name: "Diesel"           │
│ price_x_und  | 8500.0    │ →  │ - unidadDeMedida: "Galones"│
└──────────────────────────┘    │ - priceXUnd: 8500.0        │
                                └────────────────────────────┘

Proceso en código:
while (rs.next()) {
    String id = rs.getString("id");              // Extraer
    String name = rs.getString("name");          // Extraer
    String um = rs.getString("unidad_medida");   // Extraer
    double price = rs.getDouble("price_x_und");  // Extraer
    
    Products p = new Products(id, name, um, price);  // Crear objeto
    product.add(p);                                  // Agregar a lista
}

Este proceso se llama "mapeo" o "binding"
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| Constructor con orden incorrecto | Parámetros en orden diferente al ResultSet | Verifica el orden: id, name, unidadDeMedida, priceXUnd |
| Usa constructor equivocado | Llamas al de 3 parámetros en vez del de 4 | En ProductServices usa `new Products(id, name, um, price)` (4 parámetros) |
| NullPointerException al usar lista | Olvidaste inicializar ArrayList | `new ArrayList<>()` al inicio del método |
| Lista vacía pero hay datos en BD | No agregas productos a la lista en el while | Verifica `.add(product)` dentro del while |
| "Cannot find symbol Products" | Falta import | Agrega `import com.forestech.models.Product;` |
| Nombres de columnas incorrectos | SQL usa nombres diferentes | Verifica en DBeaver los nombres exactos de las columnas |

**🔍 Comparación lado a lado:**

**Checkpoint 3.3 (solo imprimir):**
```java
while (rs.next()) {
    String id = rs.getString("id");
    String name = rs.getString("name");
    System.out.println("ID: " + id + ", Name: " + name);
}
// ❌ Los datos desaparecen después del while
// ❌ No puedes reutilizar los datos
// ❌ No puedes pasar los datos a otras funciones
```

**Checkpoint 3.4 (crear objetos):**
```java
List<Products> product = new ArrayList<>();

while (rs.next()) {
    String id = rs.getString("id");
    String name = rs.getString("name");
    String um = rs.getString("unidad_medida");
    double price = rs.getDouble("price_x_und");
    
    Products p = new Products(id, name, um, price);  // Crear objeto
    product.add(p);                                  // Guardar en lista
}

return product;  // ✅ Datos persisten como objetos reutilizables
                  // ✅ Puedes pasarlos a otras funciones
                  // ✅ Puedes procesarlos, filtrarlos, ordenarlos
```

---

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 3.5: Búsqueda por Unidad de Medida con PreparedStatement

**Concepto clave:** PreparedStatement es más seguro y eficiente que Statement porque previene SQL Injection y compila la query una sola vez.

**📍 DÓNDE:** 
- **Modificar:** `ProductServices.java` (agregar método nuevo)
- **Main.java:** Para PROBAR el filtro por unidad de medida

**🎯 PARA QUÉ:** 
En Checkpoint 3.3 y 3.4 usaste queries fijas (sin parámetros) que retornaban TODOS los productos. En el mundo real de Forestech necesitas:

**Caso de uso concreto:**
- ✅ **Usuario:** "Muéstrame todos los combustibles que se venden por **Galones**"
- ✅ **Usuario:** "Necesito ver qué productos tenemos en **Litros**"
- ✅ **Sistema:** Debe filtrar dinámicamente según la unidad de medida elegida

**¿Por qué es importante en Forestech?**
- Los combustibles se venden en diferentes unidades (Litros, Galones, Barriles)
- Los clientes preguntan precios según su unidad preferida
- Los reportes deben separar ventas por unidad de medida
- Facilita inventario (agrupar productos por cómo se miden)

**⚠️ PELIGRO: Concatenación de strings (❌ NUNCA HACER ESTO):**

```java
// ❌ CÓDIGO VULNERABLE (NO USAR)
String unidadUsuario = scanner.nextLine();  // Usuario ingresa: Litros
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = '" + unidadUsuario + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// ☠️ PROBLEMA: Si un usuario malicioso ingresa:
//    unidadUsuario = "Litros' OR '1'='1"
// ☠️ La query se convierte en:
//    SELECT * FROM oil_products WHERE unidadDeMedida = 'Litros' OR '1'='1'
// ☠️ Resultado: Retorna TODOS los productos (SQL INJECTION)
// ☠️ En producción, esto podría exponer datos confidenciales o borrar la BD
```

**✅ SOLUCIÓN SEGURA: PreparedStatement**

```java
// ✅ CÓDIGO SEGURO (USAR SIEMPRE)
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, unidadUsuario);  // Automáticamente escapa caracteres especiales
ResultSet rs = pstmt.executeQuery();

// ✅ VENTAJAS:
// 1. Seguridad: Aunque el usuario ingrese "Litros' OR '1'='1",
//    PreparedStatement lo trata como string literal (busca exactamente eso)
// 2. Velocidad: MySQL compila la query UNA VEZ, luego solo cambia parámetros
// 3. Legibilidad: El código es más claro (sin concatenación confusa)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** TODOS los INSERT/UPDATE/DELETE usarán PreparedStatement
- **Fase 5:** Queries con múltiples parámetros y JOINs
- **Fase 6:** El menú de Forestech usará este método para filtros dinámicos
- **Fase 9:** Reportes con rangos de fechas (parámetros múltiples)

**🎓 Analogía - ¿Por qué PreparedStatement es seguro?**

```
Concatenación (inseguro):
┌────────────────────────────────────────────┐
│ Carta escrita a mano cada vez              │
│                                            │
│ "Buscar productos donde unidad = Litros"  │
│                                            │
│ Problema: Si el usuario escribe algo raro │
│ (como "Litros' OR '1'='1"), la carta se   │
│ convierte en dos instrucciones diferentes  │
└────────────────────────────────────────────┘

PreparedStatement (seguro):
┌────────────────────────────────────────────┐
│ Formulario con espacios en blanco          │
│                                            │
│ "Buscar productos donde unidad = [____]"  │
│                                            │
│ Ventaja: El espacio en blanco SOLO acepta │
│ un valor literal. No importa qué escribas, │
│ siempre se trata como texto a buscar      │
└────────────────────────────────────────────┘
```

**Prompts sugeridos:**
```text
"¿Qué es SQL Injection y cómo PreparedStatement lo previene?"
"¿Por qué los parámetros de PreparedStatement se marcan con '?'?"
"¿Qué diferencia hay entre setString(), setInt() y setDouble()?"
"¿Por qué PreparedStatement es más rápido que Statement?"
"Muéstrame un ejemplo real de SQL Injection en Forestech."
"¿Qué pasa si olvido llamar a pstmt.setString() antes de executeQuery()?"
```

**Diagrama de estructura - Método getProductsByUnidadDeMedida():**

```
ProductServices.java
│
└── Método: getProductsByUnidadDeMedida(String unidadDeMedida)
    │
    ├── Tipo retorno: List<Products>
    ├── Modificadores: public static
    ├── Parámetro: String unidadDeMedida (ej: "Litros", "Galones")
    │
    ├── PASO 1: Crear lista vacía
    │   └── List<Products> productos = new ArrayList<>();
    │
    ├── PASO 2: Definir query SQL con parámetro
    │   └── String sql = "SELECT id, name, unidadDeMedida, priceXUnd 
    │                      FROM oil_products 
    │                      WHERE unidadDeMedida = ?"
    │       └── "?" es PLACEHOLDER para el parámetro (posición 1)
    │
    ├── PASO 3: try-with-resources
    │   ├── Connection conn = DatabaseConnection.getConnection()
    │   ├── PreparedStatement pstmt = conn.prepareStatement(sql)
    │   │   └── Compila la query (MySQL la optimiza)
    │   └── ResultSet rs = pstmt.executeQuery()
    │       └── Ejecuta query con parámetro ya configurado
    │
    ├── PASO 4: Configurar parámetro ANTES de executeQuery()
    │   └── pstmt.setString(1, unidadDeMedida)
    │       ├── "1" = posición del primer "?" en la query
    │       ├── unidadDeMedida = valor a insertar
    │       └── setString() escapa automáticamente comillas y caracteres especiales
    │
    ├── PASO 5: Recorrer ResultSet
    │   └── while (rs.next())
    │       ├── Extraer: id, name, unidadDeMedida, priceXUnd
    │       ├── Crear: new Products(id, name, unidadDeMedida, priceXUnd)
    │       └── Agregar: productos.add(producto)
    │
    ├── PASO 6: Retornar lista
    │   └── return productos (vacía si no se encontró nada)
    │
    └── PASO 7: catch (SQLException e)
        ├── Imprimir error
        └── return new ArrayList<>() (lista vacía, NO null)
```

**Tareas paso a paso:**

**🎯 Objetivo:** Crear método `getProductsByUnidadDeMedida()` que filtre combustibles por su unidad de medida.

---

### **PASO 1: Pensar ANTES de codificar (Pseudocódigo)**

**Antes de escribir código Java, diseña la solución en lenguaje natural:**

```
PSEUDOCÓDIGO del método getProductsByUnidadDeMedida:

ENTRADA: String unidadDeMedida (ej: "Litros", "Galones")
SALIDA: List<Products> (lista de productos con esa unidad)

PROCESO:
1. CREAR lista vacía de Products
2. DEFINIR query SQL:
   "SELECT id, name, unidadDeMedida, priceXUnd 
    FROM oil_products 
    WHERE unidadDeMedida = ?"
   
3. OBTENER conexión a la base de datos
4. PREPARAR la query (compilar en MySQL)
5. CONFIGURAR el parámetro "?" con el valor recibido
6. EJECUTAR la query

7. MIENTRAS haya filas en el resultado:
   a. EXTRAER datos de la fila actual
   b. CREAR objeto Products con esos datos
   c. AGREGAR objeto a la lista

8. CERRAR recursos (automático con try-with-resources)
9. RETORNAR lista

EN CASO DE ERROR:
   - IMPRIMIR mensaje de error
   - RETORNAR lista vacía (no null)
```

**Preguntas reflexivas ANTES de codificar:**
- ¿Por qué retornar `List<Products>` en vez de `void`?
- ¿Por qué usar `?` en el SQL en vez de concatenar el valor directamente?
- ¿Por qué retornar lista vacía en caso de error y no `null`?
- ¿Qué pasaría si olvidas llamar a `pstmt.setString()` antes de `executeQuery()`?

**Responde estas preguntas mentalmente o por escrito antes de continuar.**

---

### **PASO 2: Implementar en ProductServices.java (TÚ lo escribes)**

**a) Ubicación:**
   - Abre `com.forestech.services.ProductServices.java`
   - Agrega el nuevo método después de `getAllProducts()`

**b) Imports necesarios:**
   ```java
   // Ya deberías tenerlos de Checkpoint 3.4, pero verifica:
   import java.sql.Connection;
   import java.sql.PreparedStatement;  // ← NUEVO (si no lo tienes)
   import java.sql.ResultSet;
   import java.sql.SQLException;
   import java.util.ArrayList;
   import java.util.List;
   import com.forestech.models.Products;
   import com.forestech.config.DatabaseConnection;
   ```

**c) Firma del método:**
   ```java
   // TÚ escribes:
   public static List<Products> getProductsByUnidadDeMedida(String unidadDeMedida) {
       // ... tu código aquí
   }
   ```
   
   **Pregunta:** ¿Por qué `static`? ¿Por qué `List<Products>` y no `void`?

**d) Cuerpo del método (TÚ completas cada sección):**

**Sección 1 - Crear lista vacía:**
```java
// TÚ escribes:
List<Products> productos = new ArrayList<>();
```

**Sección 2 - Definir query SQL:**
```java
// TÚ escribes:
String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
             "FROM oil_products " +
             "WHERE unidadDeMedida = ?";
```
**Pregunta:** ¿Qué representa el símbolo `?` en la query?

**Sección 3 - try-with-resources:**
```java
// TÚ escribes:
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
    // PASO CRÍTICO: Configurar parámetro ANTES de executeQuery
    pstmt.setString(1, unidadDeMedida);
    //             ↑                ↑
    //          posición del "?"   valor a insertar
    
    // TÚ escribes: Ejecutar query
    ResultSet rs = pstmt.executeQuery();
    
    // TÚ escribes: Recorrer resultados
    while (rs.next()) {
        // PASO 1: Extraer datos (TÚ completas)
        String id = rs.getString("id");
        String name = rs.getString("name");
        String unidad = rs.getString("unidadDeMedida");
        double price = rs.getDouble("priceXUnd");
        
        // PASO 2: Crear objeto (TÚ completas)
        Products producto = new Products(id, name, unidad, price);
        
        // PASO 3: Agregar a lista (TÚ completas)
        productos.add(producto);
    }
    
} catch (SQLException e) {
    // TÚ escribes: Manejo de error
    System.err.println("Error al buscar productos por unidad de medida: " + e.getMessage());
}

// TÚ escribes: Retornar lista
return productos;
```

**Preguntas críticas:**
- ¿Por qué `pstmt.setString(1, ...)` va ANTES de `executeQuery()`?
- ¿Qué pasa si hay 2 "?" en la query? ¿Cómo los configuras?
- ¿Por qué usar `rs.getString("name")` y no `rs.getString(2)` (por índice)?

---

### **PASO 3: Compilar y verificar errores**

```bash
cd ~/forestechOil/forestech-cli-java/
mvn clean compile
```

**Resultado esperado:** `BUILD SUCCESS`

**Si hay errores:**
- Verifica imports (especialmente `PreparedStatement`)
- Verifica sintaxis del try-with-resources
- Verifica que `Products` tenga constructor de 4 parámetros (Checkpoint 3.4)

---

### **PASO 4: Probar en Main.java (TÚ escribes el código de prueba)**

**a) Ubicación:**
   - Abre `com.forestech.Main.java`
   - En el método `main()`, agrega las pruebas

**b) Prueba 1 - Buscar combustibles por "Litros":**

**Pseudocódigo de la prueba:**
```
IMPRIMIR encabezado "=== PRODUCTOS EN LITROS ==="

LLAMAR a ProductServices.getProductsByUnidadDeMedida("Litros")
GUARDAR resultado en variable productos

SI productos.isEmpty():
   IMPRIMIR "No hay productos en Litros"
SINO:
   PARA CADA producto EN productos:
       IMPRIMIR producto (usa toString() automático)
   FIN PARA
   IMPRIMIR "Total: " + productos.size() + " productos"
FIN SI
```

**TÚ conviertes este pseudocódigo a Java:**
```java
// TÚ escribes en Main.java:
System.out.println("\n=== PRODUCTOS EN LITROS ===");
List<Products> productosLitros = ProductServices.getProductsByUnidadDeMedida("Litros");

if (productosLitros.isEmpty()) {
    System.out.println("No hay productos en Litros");
} else {
    for (Products p : productosLitros) {
        System.out.println(p);
    }
    System.out.println("\nTotal: " + productosLitros.size() + " productos");
}
```

**c) Prueba 2 - Buscar combustibles por "Galones":**
```java
// TÚ escribes (similar a Prueba 1):
System.out.println("\n=== PRODUCTOS EN GALONES ===");
List<Products> productosGalones = ProductServices.getProductsByUnidadDeMedida("Galones");
// ... resto del código similar
```

**d) Prueba 3 - Buscar unidad que NO existe:**
```java
// TÚ escribes:
System.out.println("\n=== PRODUCTOS EN BARRILES (no existe) ===");
List<Products> productosBarriles = ProductServices.getProductsByUnidadDeMedida("Barriles");

if (productosBarriles.isEmpty()) {
    System.out.println("✅ Correcto: No hay productos en Barriles");
} else {
    System.out.println("❌ Error: Se encontraron productos (no debería)");
}
```

**Pregunta:** ¿Por qué esta prueba 3 es importante?

---

### **PASO 5: Ejecutar y verificar resultados**

```bash
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

**Resultado esperado:**
```
=== PRODUCTOS EN LITROS ===
┌─────────────┬─────────────────┬─────────────────┬──────────────┐
│ ID          │ Nombre          │ Unidad Medida   │ Precio/Und   │
├─────────────┼─────────────────┼─────────────────┼──────────────┤
│ FUEL-001    │ Diesel Premium  │ Litros          │ $5,200.00    │
│ FUEL-003    │ Gasolina 93     │ Litros          │ $6,800.00    │
└─────────────┴─────────────────┴─────────────────┴──────────────┘

Total: 2 productos

=== PRODUCTOS EN GALONES ===
┌─────────────┬─────────────────┬─────────────────┬──────────────┐
│ ID          │ Nombre          │ Unidad Medida   │ Precio/Und   │
├─────────────┼─────────────────┼─────────────────┼──────────────┤
│ FUEL-002    │ Gasolina 97     │ Galones         │ $22,500.00   │
└─────────────┴─────────────────┴─────────────────┴──────────────┘

Total: 1 producto

=== PRODUCTOS EN BARRILES (no existe) ===
✅ Correcto: No hay productos en Barriles
```

---

### **PASO 6: Depuración obligatoria - Entiende SQL Injection**

**Objetivo:** Ver cómo PreparedStatement previene SQL Injection.

**a) Configurar breakpoints en ProductServices:**
   - Breakpoint en la línea: `pstmt.setString(1, unidadDeMedida);`
   - Breakpoint en la línea: `ResultSet rs = pstmt.executeQuery();`

**b) Modificar Main.java temporalmente:**
```java
// Intenta un valor "malicioso"
String inputMalicioso = "Litros' OR '1'='1";
System.out.println("\n=== PRUEBA SQL INJECTION ===");
List<Products> resultado = ProductServices.getProductsByUnidadDeMedida(inputMalicioso);
System.out.println("Productos encontrados: " + resultado.size());
```

**c) Ejecutar en modo debug:**
   - Cuando llegue al breakpoint en `setString()`:
     - Inspecciona el valor de `unidadDeMedida`
     - Observa que contiene: `"Litros' OR '1'='1"`
   
   - Cuando llegue a `executeQuery()`:
     - En el panel de Variables, evalúa: `pstmt.toString()`
     - Verás algo como:
       ```
       SELECT ... WHERE unidadDeMedida = 'Litros\' OR \'1\'=\'1\''
       ```
     - **Nota:** Las comillas simples están ESCAPADAS (`\'`)
     - MySQL las trata como texto literal (no como código SQL)

**d) Continuar ejecución:**
   - Resultado: `Productos encontrados: 0`
   - **Explicación:** PreparedStatement buscó literalmente un producto con unidad `"Litros' OR '1'='1"` (que no existe)
   - ✅ **SQL Injection BLOQUEADO**

**e) Comparación con concatenación insegura:**

**❌ CÓDIGO VULNERABLE (solo para demostración, NO agregues a tu proyecto):**
```java
// ☠️ NO USAR EN PRODUCCIÓN
String unidadDeMedida = "Litros' OR '1'='1";
String sqlInseguro = "SELECT * FROM oil_products WHERE unidadDeMedida = '" + unidadDeMedida + "'";

// Query resultante:
// SELECT * FROM oil_products WHERE unidadDeMedida = 'Litros' OR '1'='1'
//                                                              ↑
//                                                   Cierra la comilla prematuramente
//                                                   OR '1'='1' se ejecuta como código SQL
// Resultado: Retorna TODOS los productos (vulnerabilidad crítica)
```

**Pregunta final:** ¿Por qué PreparedStatement es OBLIGATORIO cuando trabajas con input del usuario?

---

### **PASO 7: Experimentos adicionales (opcional)**

**a) Prueba con diferentes valores:**
```java
// Mayúsculas/minúsculas
ProductServices.getProductsByUnidadDeMedida("litros");  // ¿Funciona?
ProductServices.getProductsByUnidadDeMedida("LITROS");  // ¿Funciona?

// Espacios
ProductServices.getProductsByUnidadDeMedida(" Litros "); // ¿Funciona?

// Caracteres especiales
ProductServices.getProductsByUnidadDeMedida("Litros/Galones"); // ¿Funciona?
```

**b) Agrega logging para entender el flujo:**
```java
// En ProductServices.getProductsByUnidadDeMedida(), después de setString():
System.out.println("DEBUG: Buscando productos con unidad: " + unidadDeMedida);
System.out.println("DEBUG: Query preparada: " + pstmt.toString());

// Después del while:
System.out.println("DEBUG: Encontrados " + productos.size() + " productos");
```

**Pregunta:** ¿Cómo afecta MySQL mayúsculas/minúsculas en las comparaciones? (Depende de la configuración de la BD)

---

**✅ Resultado esperado:** 
- ✅ Método `getProductsByUnidadDeMedida()` implementado en ProductServices
- ✅ Retorna lista de productos filtrados por unidad de medida
- ✅ Main.java prueba el método con diferentes valores (Litros, Galones, valor inexistente)
- ✅ Entiendes la diferencia crítica entre concatenación (insegura) y PreparedStatement (segura)
- ✅ Comprendes cómo PreparedStatement previene SQL Injection
- ✅ Sabes usar `pstmt.setString(posición, valor)` correctamente
- ✅ Puedes explicar por qué los índices de parámetros empiezan en 1 (no en 0)

**Estructura actualizada:**
```
com.forestech/
├── Main.java (MODIFICADO - pruebas de filtrado)
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Products.java
└── services/
    └── ProductServices.java 
        ├── getAllProducts()                      ← Checkpoint 3.4
        └── getProductsByUnidadDeMedida(String)   ← Checkpoint 3.5 (NUEVO)
```

---

### **💡 Conceptos Clave - PreparedStatement en profundidad**

#### **1. ¿Por qué los índices empiezan en 1 y no en 0?**

```java
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = ?";
pstmt.setString(1, "Litros");  // ¿Por qué 1 y no 0?
```

**Respuesta:** 
- JDBC sigue la convención de SQL estándar (no de arrays de Java)
- En SQL, las columnas y parámetros se numeran desde 1
- Es consistente con `rs.getString(1)` que también empieza en 1

**Comparación:**
```
Arrays Java:         JDBC/SQL:
arr[0] → primer     pstmt.setString(1, ...) → primer parámetro
arr[1] → segundo    pstmt.setString(2, ...) → segundo parámetro
arr[2] → tercero    pstmt.setString(3, ...) → tercer parámetro
```

---

#### **2. Tipos de setters en PreparedStatement**

```java
// Según el tipo de columna en MySQL:
pstmt.setString(1, "Litros");      // VARCHAR, TEXT, CHAR
pstmt.setInt(2, 100);              // INT, SMALLINT, TINYINT
pstmt.setDouble(3, 5200.50);       // DOUBLE, FLOAT
pstmt.setBigDecimal(4, precio);    // DECIMAL (recomendado para dinero)
pstmt.setBoolean(5, true);         // BOOLEAN, TINYINT(1)
pstmt.setDate(6, sqlDate);         // DATE
pstmt.setTimestamp(7, timestamp);  // DATETIME, TIMESTAMP

// CRÍTICO: El tipo Java debe coincidir con el tipo MySQL
```

**Ejemplo aplicado a Forestech:**
```java
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = ? AND priceXUnd > ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "Litros");    // unidadDeMedida es VARCHAR
pstmt.setDouble(2, 5000.0);      // priceXUnd es DOUBLE
```

---

#### **3. Múltiples parámetros en una query**

**Ejemplo: Filtrar por unidad Y rango de precio:**
```java
String sql = "SELECT * FROM oil_products " +
             "WHERE unidadDeMedida = ? " +
             "AND priceXUnd BETWEEN ? AND ?";
             //                     ↑       ↑
             //                   param2  param3
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "Litros");    // Primer ?
pstmt.setDouble(2, 5000.0);      // Segundo ?
pstmt.setDouble(3, 8000.0);      // Tercer ?
```

**Regla de oro:** El orden de `setXxx()` debe coincidir con el orden de los `?` en la query.

---

#### **4. ¿Qué pasa si olvidas configurar un parámetro?**

```java
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = ? AND priceXUnd > ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "Litros");  // Configuras el primer ?
// ❌ Olvidaste configurar el segundo ?
pstmt.executeQuery();          // ☠️ EXPLOTA AQUÍ
```

**Error que verás:**
```
java.sql.SQLException: Parameter index out of range (2 > number of parameters, which is 1).
```

**Solución:** Siempre configura TODOS los parámetros antes de `executeQuery()`.

---

#### **5. PreparedStatement vs Statement - Comparación técnica**

**Ejemplo concreto con Forestech:**

**❌ Statement (concatenación insegura):**
```java
String unidad = "Litros' OR '1'='1";  // Input malicioso del usuario
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = '" + unidad + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// Query real enviada a MySQL:
// SELECT * FROM oil_products WHERE unidadDeMedida = 'Litros' OR '1'='1'
//                                                              ↑
//                                                   Inyección SQL exitosa
// Resultado: Retorna TODOS los productos (vulnerabilidad crítica)
```

**✅ PreparedStatement (seguro):**
```java
String unidad = "Litros' OR '1'='1";  // Mismo input malicioso
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, unidad);
ResultSet rs = pstmt.executeQuery();

// Query real enviada a MySQL:
// SELECT * FROM oil_products WHERE unidadDeMedida = 'Litros\' OR \'1\'=\'1\''
//                                                            ↑         ↑
//                                                   Comillas escapadas
// Resultado: Busca literalmente ese string (0 resultados, seguro)
```

**¿Cómo escapa PreparedStatement?**
- Convierte `'` en `\'` (MySQL lo interpreta como carácter, no como delimitador)
- Convierte `"` en `\"`
- Escapa caracteres especiales: `\`, `%`, `_`
- Todo sucede automáticamente al llamar `setString()`

---

#### **6. Ventaja de rendimiento - Compilación única**

**Con Statement:**
```java
for (String unidad : Arrays.asList("Litros", "Galones", "Barriles")) {
    String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = '" + unidad + "'";
    Statement stmt = conn.createStatement();
    stmt.executeQuery(sql);  // MySQL compila CADA query (lento)
}
// Total: 3 compilaciones
```

**Con PreparedStatement:**
```java
String sql = "SELECT * FROM oil_products WHERE unidadDeMedida = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);  // Compila UNA VEZ

for (String unidad : Arrays.asList("Litros", "Galones", "Barriles")) {
    pstmt.setString(1, unidad);
    pstmt.executeQuery();  // Reutiliza query ya compilada (rápido)
}
// Total: 1 compilación + 3 ejecuciones
```

**Diferencia de rendimiento:**
- En queries simples: 10-20% más rápido
- En queries complejas o bucles: 50-300% más rápido
- En producción con miles de queries: Diferencia crítica

---

### **⚠️ Errores comunes y soluciones**

| Error | Causa | Solución |
|-------|-------|----------|
| `Parameter index out of range` | Olvidaste configurar un `?` | Verifica que llamas `setXxx()` para cada `?` |
| `Invalid parameter index` | Usaste índice incorrecto | Los índices empiezan en 1 (no en 0) |
| `Column 'unidadDeMedida' not found` | Nombre de columna incorrecto | Verifica nombres exactos en tu tabla MySQL |
| `SQLException: Connection is closed` | Conexión cerrada antes de tiempo | Usa try-with-resources correctamente |
| No encuentra resultados esperados | Mayúsculas/minúsculas no coinciden | MySQL puede ser case-sensitive (depende de configuración) |
| `ClassCastException` | Usaste getter incorrecto | `getString()` para VARCHAR, `getDouble()` para DOUBLE, etc. |

---

### **🔍 Depuración avanzada - Ver la query compilada**

**En el debugger o código temporal:**
```java
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "Litros");

// Inspecciona la query compilada (solo para debug)
System.out.println("DEBUG Query: " + pstmt.toString());

// Salida (puede variar según driver):
// com.mysql.cj.jdbc.ClientPreparedStatement: 
// SELECT * FROM oil_products WHERE unidadDeMedida = 'Litros'
```

**Advertencia:** `pstmt.toString()` NO es estándar JDBC. Funciona en MySQL Connector/J pero puede no funcionar con otros drivers.

---

### **📊 Tabla comparativa final**

| Característica | Statement | PreparedStatement |
|----------------|-----------|-------------------|
| **Seguridad** | ❌ Vulnerable a SQL Injection | ✅ Seguro contra SQL Injection |
| **Velocidad** | ❌ Compila cada vez | ✅ Compila una vez, reutiliza |
| **Legibilidad** | ❌ Concatenación confusa | ✅ Query limpia con `?` |
| **Uso con parámetros** | ❌ Requiere concatenación manual | ✅ Método dedicado `setXxx()` |
| **Escapado de caracteres** | ❌ Manual (propenso a errores) | ✅ Automático |
| **Cuándo usarlo** | Solo queries fijas sin parámetros | SIEMPRE con input del usuario |
| **En producción** | ⚠️ Solo para queries estáticas | ✅ OBLIGATORIO para queries dinámicas |

---

### **🎯 Preguntas de reflexión final**

Responde estas preguntas mentalmente o por escrito para validar tu comprensión:

1. **Seguridad:**
   - ¿Qué es SQL Injection y cómo PreparedStatement lo previene?
   - ¿Por qué es peligroso concatenar strings para crear queries SQL?
   - Inventa un ejemplo de SQL Injection aplicado a Forestech.

2. **Uso de PreparedStatement:**
   - ¿Por qué los índices de parámetros empiezan en 1 y no en 0?
   - ¿Qué pasa si olvidas llamar a `setString()` antes de `executeQuery()`?
   - ¿Puedes reutilizar un PreparedStatement con diferentes parámetros?

3. **Tipos de datos:**
   - ¿Qué método usas para una columna VARCHAR? ¿Y para DOUBLE?
   - ¿Por qué `setBigDecimal()` es mejor que `setDouble()` para dinero?

4. **Rendimiento:**
   - ¿Por qué PreparedStatement es más rápido que Statement?
   - ¿En qué escenarios la diferencia de velocidad es más notable?

5. **Casos de uso:**
   - ¿Cuándo está bien usar Statement (si es que hay casos)?
   - ¿PreparedStatement es obligatorio para TODAS las queries?

**Si puedes responder todas estas preguntas con confianza, ¡dominas el Checkpoint 3.5!** 🎉

---

**⏱️ Tiempo estimado:** 3-4 horas (incluye implementación, pruebas y depuración)

---

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
SQL: "SELECT * FROM product WHERE type = ? AND price > ?"
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
PreparedStatement: SELECT * FROM product WHERE id = 'P001'
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

## ✅ Checkpoint 3.6: Búsqueda por Rango de Precio (Múltiples Parámetros)

**Concepto clave:** Usar múltiples parámetros en PreparedStatement para queries más complejas con operadores de comparación (BETWEEN, >, <, etc.).

**📍 DÓNDE:** 
- **Modificar:** `ProductServices.java` (agregar nuevo método)
- **Main.java:** Para PROBAR los rangos de precio

**🎯 PARA QUÉ:** 
En Checkpoint 3.5 usaste UN parámetro (unidad de medida). En el mundo real de Forestech necesitas filtrar con MÚLTIPLES condiciones:

**Casos de uso concretos:**
- ✅ **Usuario:** "Muéstrame combustibles que cuesten entre $5,000 y $8,000 por litro"
- ✅ **Usuario:** "Necesito ver productos económicos (menos de $6,000)"
- ✅ **Gerente:** "Quiero analizar productos en rango de precio medio ($6,000-$10,000)"
- ✅ **Sistema:** Filtrar combustibles asequibles para clientes con presupuesto limitado

**¿Por qué es importante en Forestech?**
- Los clientes tienen presupuestos diferentes (taxis vs empresas de transporte)
- Los gerentes analizan ventas por segmento de precio
- El sistema puede recomendar alternativas más económicas
- Facilita negociaciones comerciales ("tenemos opciones entre X y Y pesos")

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Filtrar movimientos por rango de fechas con dos parámetros
- **Fase 5:** Reportes complejos con 3+ parámetros (fecha inicio, fecha fin, tipo, vehículo)
- **Fase 6:** Menú interactivo donde el usuario ingresa mínimo y máximo
- **Fase 9:** Dashboard con análisis de ventas por rangos configurables

**🎓 Analogía:**
```
UN parámetro (Checkpoint 3.5):
"Muéstrame productos en Litros"
→ Filtro simple: condición = valor

MÚLTIPLES parámetros (Checkpoint 3.6):
"Muéstrame productos entre $5,000 y $8,000"
→ Filtro compuesto: valor1 ≤ condición ≤ valor2
```

**Prompts sugeridos:**
```text
"¿Cómo configuro dos parámetros en una misma query?"
"¿Qué diferencia hay entre BETWEEN y usar >= y <=?"
"¿Por qué los índices de setDouble() deben coincidir con el orden de los '?'?"
"¿Puedo tener parámetros de diferentes tipos (String y double) en la misma query?"
"¿Qué pasa si min > max en el rango? ¿Debo validar en Java o en SQL?"
```

---

### **Diagrama de estructura - Método getProductsByPriceRange()**

```
ProductServices.java
│
└── Método: getProductsByPriceRange(double minPrice, double maxPrice)
    │
    ├── Tipo retorno: List<Products>
    ├── Modificadores: public static
    ├── Parámetros: 
    │   ├── double minPrice (ej: 5000.0)
    │   └── double maxPrice (ej: 8000.0)
    │
    ├── PASO 1: Validación de parámetros (opcional pero recomendado)
    │   └── if (minPrice > maxPrice) → intercambiar o retornar lista vacía
    │
    ├── PASO 2: Crear lista vacía
    │   └── List<Products> productos = new ArrayList<>();
    │
    ├── PASO 3: Query SQL con DOS parámetros
    │   └── "SELECT id, name, unidadDeMedida, priceXUnd 
    │        FROM oil_products 
    │        WHERE priceXUnd BETWEEN ? AND ?"
    │       └── Primer "?" = minPrice (posición 1)
    │       └── Segundo "?" = maxPrice (posición 2)
    │
    ├── PASO 4: try-with-resources
    │   ├── Connection conn = DatabaseConnection.getConnection()
    │   ├── PreparedStatement pstmt = conn.prepareStatement(sql)
    │   └── ResultSet rs = pstmt.executeQuery()
    │
    ├── PASO 5: Configurar AMBOS parámetros
    │   ├── pstmt.setDouble(1, minPrice)   ← Configura primer ?
    │   └── pstmt.setDouble(2, maxPrice)   ← Configura segundo ?
    │       └── CRÍTICO: El orden debe coincidir con los ? en la query
    │
    ├── PASO 6: Recorrer ResultSet (igual que Checkpoint 3.5)
    │   └── while (rs.next())
    │       ├── Extraer datos
    │       ├── Crear objeto Products
    │       └── Agregar a lista
    │
    ├── PASO 7: Retornar lista
    │   └── return productos (vacía si no se encontró nada)
    │
    └── PASO 8: catch (SQLException e)
        ├── Imprimir error
        └── return new ArrayList<>() (lista vacía, NO null)
```

---

### **Tareas paso a paso:**

**🎯 Objetivo:** Crear método que filtre combustibles por rango de precio usando dos parámetros.

---

#### **PASO 1: Pensar ANTES de codificar (Pseudocódigo)**

```
PSEUDOCÓDIGO del método getProductsByPriceRange:

ENTRADA: 
   - double minPrice (precio mínimo, ej: 5000.0)
   - double maxPrice (precio máximo, ej: 8000.0)

SALIDA: 
   - List<Products> (productos en ese rango de precio)

VALIDACIÓN PREVIA (opcional):
   SI minPrice > maxPrice:
       OPCIÓN A: Intercambiar valores (temp = min; min = max; max = temp)
       OPCIÓN B: Retornar lista vacía con mensaje de error
       OPCIÓN C: Lanzar IllegalArgumentException

PROCESO:
1. CREAR lista vacía de Products

2. DEFINIR query SQL con DOS parámetros:
   "SELECT id, name, unidadDeMedida, priceXUnd 
    FROM oil_products 
    WHERE priceXUnd BETWEEN ? AND ?"
   
   NOTA: BETWEEN es inclusivo (incluye los límites)
         Equivale a: priceXUnd >= ? AND priceXUnd <= ?

3. OBTENER conexión a BD
4. PREPARAR la query
5. CONFIGURAR primer parámetro (?) con minPrice
6. CONFIGURAR segundo parámetro (?) con maxPrice
7. EJECUTAR query

8. MIENTRAS haya filas:
   a. EXTRAER datos
   b. CREAR objeto Products
   c. AGREGAR a lista

9. RETORNAR lista

EN CASO DE ERROR:
   - IMPRIMIR mensaje
   - RETORNAR lista vacía
```

**Preguntas reflexivas:**
- ¿Por qué usar `BETWEEN` en vez de `>= AND <=`? ¿Son equivalentes?
- ¿Qué pasa si ambos parámetros son iguales (min = max = 6000)?
- ¿Es necesario validar que minPrice < maxPrice antes de la query?
- ¿Qué pasa si paso valores negativos?

---

#### **PASO 2: Implementar en ProductServices.java**

**a) Ubicación:**
   - Abre `ProductServices.java`
   - Agrega después de `getProductsByUnidadDeMedida()`

**b) Firma del método:**
```java
// TÚ escribes:
public static List<Products> getProductsByPriceRange(double minPrice, double maxPrice) {
    // ... tu código aquí
}
```

**c) Validación opcional (recomendada):**
```java
// TÚ decides si agregas esto:
if (minPrice > maxPrice) {
    System.err.println("Advertencia: minPrice > maxPrice. Intercambiando valores...");
    double temp = minPrice;
    minPrice = maxPrice;
    maxPrice = temp;
}

// O simplemente retornar vacío:
if (minPrice > maxPrice) {
    System.err.println("Error: minPrice no puede ser mayor que maxPrice");
    return new ArrayList<>();
}
```

**Pregunta:** ¿Cuál opción prefieres y por qué?

**d) Crear lista vacía:**
```java
List<Products> productos = new ArrayList<>();
```

**e) Query SQL con DOS parámetros:**
```java
// TÚ escribes:
String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
             "FROM oil_products " +
             "WHERE priceXUnd BETWEEN ? AND ?";
             //                       ↑     ↑
             //                    param1  param2
```

**Alternativa equivalente (sin BETWEEN):**
```java
// También válido:
String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
             "FROM oil_products " +
             "WHERE priceXUnd >= ? AND priceXUnd <= ?";
```

**Pregunta:** ¿Cuál prefieres? `BETWEEN` es más legible, pero ambos funcionan igual.

**f) try-with-resources y configuración de parámetros:**
```java
// TÚ escribes:
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
    // CRÍTICO: Configurar AMBOS parámetros en el orden correcto
    pstmt.setDouble(1, minPrice);   // Primer ? en la query
    pstmt.setDouble(2, maxPrice);   // Segundo ? en la query
    
    // TÚ escribes: Ejecutar query
    ResultSet rs = pstmt.executeQuery();
    
    // TÚ escribes: Recorrer resultados (igual que Checkpoint 3.5)
    while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String unidad = rs.getString("unidadDeMedida");
        double price = rs.getDouble("priceXUnd");
        
        Products producto = new Products(id, name, unidad, price);
        productos.add(producto);
    }
    
} catch (SQLException e) {
    System.err.println("Error al buscar productos por rango de precio: " + e.getMessage());
}

return productos;
```

**Pregunta crítica:** ¿Qué pasa si inviertes el orden de `setDouble()`? 
```java
pstmt.setDouble(1, maxPrice);  // ❌ INCORRECTO
pstmt.setDouble(2, minPrice);  // ❌ INCORRECTO
// Resultado: Buscaría productos con precio entre max y min (invertido)
```

---

#### **PASO 3: Compilar y verificar**

```bash
cd ~/forestechOil/forestech-cli-java/
mvn clean compile
```

**Resultado esperado:** `BUILD SUCCESS`

---

#### **PASO 4: Probar en Main.java**

**a) Prueba 1 - Rango normal:**

**Pseudocódigo:**
```
IMPRIMIR "=== COMBUSTIBLES ENTRE $5,000 Y $8,000 ==="

LLAMAR getProductsByPriceRange(5000.0, 8000.0)
GUARDAR en variable productos

SI productos.isEmpty():
   IMPRIMIR "No hay productos en ese rango"
SINO:
   PARA CADA producto:
       IMPRIMIR producto
   IMPRIMIR "Total: " + productos.size()
```

**TÚ escribes en Main.java:**
```java
System.out.println("\n=== COMBUSTIBLES ENTRE $5,000 Y $8,000 ===");
List<Products> rangoMedio = ProductServices.getProductsByPriceRange(5000.0, 8000.0);

if (rangoMedio.isEmpty()) {
    System.out.println("No hay productos en ese rango");
} else {
    for (Products p : rangoMedio) {
        System.out.println(p);
    }
    System.out.println("\nTotal: " + rangoMedio.size() + " productos");
}
```

**b) Prueba 2 - Productos económicos (menos de $6,000):**
```java
// TÚ escribes:
System.out.println("\n=== COMBUSTIBLES ECONÓMICOS (hasta $6,000) ===");
List<Products> economicos = ProductServices.getProductsByPriceRange(0.0, 6000.0);
// ... mismo código de impresión
```

**c) Prueba 3 - Productos premium (más de $10,000):**
```java
// TÚ escribes:
System.out.println("\n=== COMBUSTIBLES PREMIUM (desde $10,000) ===");
List<Products> premium = ProductServices.getProductsByPriceRange(10000.0, 999999.0);
// Nota: 999999.0 como "infinito" práctico
```

**d) Prueba 4 - Rango invertido (para probar validación):**
```java
// TÚ escribes:
System.out.println("\n=== PRUEBA: RANGO INVERTIDO ===");
List<Products> invertido = ProductServices.getProductsByPriceRange(8000.0, 5000.0);
// ¿Qué sucede? Depende de tu validación en PASO 2
```

**e) Prueba 5 - Valor exacto (min = max):**
```java
// TÚ escribes:
System.out.println("\n=== COMBUSTIBLES DE EXACTAMENTE $6,800 ===");
List<Products> exacto = ProductServices.getProductsByPriceRange(6800.0, 6800.0);
// BETWEEN incluye los límites, así que debería funcionar
```

---

#### **PASO 5: Ejecutar y verificar resultados**

```bash
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

**Resultado esperado (ejemplo con datos ficticios):**
```
=== COMBUSTIBLES ENTRE $5,000 Y $8,000 ===
┌─────────────┬──────────────────┬─────────────────┬──────────────┐
│ ID          │ Nombre           │ Unidad Medida   │ Precio/Und   │
├─────────────┼──────────────────┼─────────────────┼──────────────┤
│ FUEL-001    │ Diesel Regular   │ Litros          │ $5,200.00    │
│ FUEL-003    │ Gasolina 93      │ Litros          │ $6,800.00    │
│ FUEL-005    │ Diesel Bio       │ Litros          │ $7,500.00    │
└─────────────┴──────────────────┴─────────────────┴──────────────┘

Total: 3 productos
```

---

#### **PASO 6: Depuración - Entender múltiples parámetros**

**a) Configurar breakpoints:**
   - En la línea: `pstmt.setDouble(1, minPrice);`
   - En la línea: `pstmt.setDouble(2, maxPrice);`
   - En la línea: `ResultSet rs = pstmt.executeQuery();`

**b) Ejecutar en modo debug con:**
```java
ProductServices.getProductsByPriceRange(5000.0, 8000.0);
```

**c) En cada breakpoint, inspecciona:**
   - Valores de `minPrice` y `maxPrice`
   - Evalúa: `pstmt.toString()` para ver la query con parámetros
   - Verifica que el orden de `setDouble()` coincide con los `?` en la query

**d) En el ResultSet:**
   - Observa cuántas filas retorna
   - Verifica que todos los `priceXUnd` están entre 5000 y 8000

---

### **💡 Conceptos Clave - Múltiples parámetros**

#### **1. Orden de parámetros ES CRÍTICO**

```java
String sql = "WHERE priceXUnd BETWEEN ? AND ?";
                                      ↑1    ↑2

// ✅ CORRECTO:
pstmt.setDouble(1, 5000.0);  // Primer ?
pstmt.setDouble(2, 8000.0);  // Segundo ?
// Query real: WHERE priceXUnd BETWEEN 5000.0 AND 8000.0

// ❌ INCORRECTO (orden invertido):
pstmt.setDouble(1, 8000.0);  // Primer ?
pstmt.setDouble(2, 5000.0);  // Segundo ?
// Query real: WHERE priceXUnd BETWEEN 8000.0 AND 5000.0
// Resultado: 0 productos (porque 8000 > 5000, rango inválido)
```

---

#### **2. BETWEEN es inclusivo**

```sql
-- BETWEEN incluye ambos límites:
WHERE priceXUnd BETWEEN 5000 AND 8000
-- Equivale a:
WHERE priceXUnd >= 5000 AND priceXUnd <= 8000

-- Ejemplos:
priceXUnd = 5000  → ✅ SÍ incluye (límite inferior)
priceXUnd = 6500  → ✅ SÍ incluye (en medio)
priceXUnd = 8000  → ✅ SÍ incluye (límite superior)
priceXUnd = 4999  → ❌ NO incluye (fuera del rango)
priceXUnd = 8001  → ❌ NO incluye (fuera del rango)
```

---

#### **3. Mezclar tipos de parámetros**

Puedes combinar `String`, `double`, `int`, etc. en la misma query:

```java
String sql = "SELECT * FROM oil_products " +
             "WHERE unidadDeMedida = ? AND priceXUnd BETWEEN ? AND ?";
             //                     ↑1                ↑2     ↑3

PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "Litros");   // Primer ? (String)
pstmt.setDouble(2, 5000.0);     // Segundo ? (double)
pstmt.setDouble(3, 8000.0);     // Tercer ? (double)
```

**Regla:** Usa el método `setXxx()` que coincida con el tipo de columna en MySQL.

---

### **✅ Resultado esperado:**

- ✅ Método `getProductsByPriceRange()` implementado con dos parámetros
- ✅ Query SQL usa `BETWEEN ? AND ?` correctamente
- ✅ `pstmt.setDouble()` configurado en el orden correcto
- ✅ Main.java prueba diferentes rangos (medio, económico, premium)
- ✅ Entiendes cómo configurar múltiples parámetros en PreparedStatement
- ✅ Comprendes que el orden de `setXxx()` debe coincidir con los `?`
- ✅ Sabes que `BETWEEN` es inclusivo (incluye los límites)

**Estructura actualizada:**
```
com.forestech/services/ProductServices.java
├── getAllProducts()                           ← Checkpoint 3.4
├── getProductsByUnidadDeMedida(String)        ← Checkpoint 3.5
└── getProductsByPriceRange(double, double)    ← Checkpoint 3.6 (NUEVO)
```

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✅ Checkpoint 3.7: Listado Ordenado por Precio (ORDER BY)

**Concepto clave:** La cláusula `ORDER BY` permite ordenar resultados ascendente (ASC) o descendente (DESC) según una o más columnas.

**📍 DÓNDE:** 
- **Modificar:** `ProductServices.java` (agregar nuevo método)
- **Main.java:** Para PROBAR diferentes ordenamientos

**🎯 PARA QUÉ:** 
Hasta ahora recuperaste datos sin orden específico (MySQL los retorna según el orden de inserción o índices). En Forestech necesitas:

**Casos de uso concretos:**
- ✅ **Usuario:** "Muéstrame combustibles ordenados del más barato al más caro"
- ✅ **Usuario:** "Quiero ver primero las opciones más caras (para transporte premium)"
- ✅ **Gerente:** "Lista todos los productos ordenados por precio para análisis de competitividad"
- ✅ **Sistema:** Mostrar recomendaciones empezando por las más económicas

**¿Por qué es importante en Forestech?**
- Los clientes buscan mejores precios (ordenar de menor a mayor)
- Las empresas de lujo buscan combustibles premium (ordenar de mayor a menor)
- Los reportes requieren datos organizados para análisis
- Facilita comparaciones de precios entre productos

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Listar movimientos ordenados por fecha (más recientes primero)
- **Fase 5:** Reportes con ordenamiento múltiple (por fecha y cantidad)
- **Fase 6:** Menú donde el usuario elige el criterio de ordenamiento
- **Fase 9:** Dashboard con top 10 productos más vendidos (ORDER BY + LIMIT)

**🎓 Analogía:**
```
Sin ORDER BY:
Libros en una estantería sin orden específico
→ Difícil encontrar lo que buscas

Con ORDER BY:
Libros ordenados alfabéticamente por título
→ Fácil ubicar cualquier libro
```

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre ORDER BY ASC y DESC?"
"¿Puedo ordenar por múltiples columnas? ¿Cómo?"
"¿Qué pasa si la columna de ordenamiento tiene valores NULL?"
"¿ORDER BY acepta parámetros con '?' en PreparedStatement?"
"¿Puedo combinar WHERE con ORDER BY en la misma query?"
```

---

### **Diagrama de estructura - Método getAllProductsOrderByPrice()**

```
ProductServices.java
│
└── Método: getAllProductsOrderByPrice(String order)
    │
    ├── Tipo retorno: List<Products>
    ├── Modificadores: public static
    ├── Parámetro: String order (valores válidos: "ASC" o "DESC")
    │
    ├── PASO 1: Validar parámetro order
    │   └── Si order no es "ASC" ni "DESC" → usar "ASC" por defecto
    │       └── Previene SQL Injection si el valor viene del usuario
    │
    ├── PASO 2: Crear lista vacía
    │   └── List<Products> productos = new ArrayList<>();
    │
    ├── PASO 3: Query SQL con ORDER BY dinámico
    │   └── String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
    │                     "FROM oil_products " +
    │                     "ORDER BY priceXUnd " + order;
    │       └── ⚠️ CUIDADO: No uses "?" para ORDER BY (explicación abajo)
    │
    ├── PASO 4: try-with-resources (sin parámetros PreparedStatement)
    │   ├── Connection conn = DatabaseConnection.getConnection()
    │   ├── Statement stmt = conn.createStatement()
    │   │   └── Usamos Statement (no PreparedStatement) porque no hay "?"
    │   └── ResultSet rs = stmt.executeQuery(sql)
    │
    ├── PASO 5: Recorrer ResultSet
    │   └── while (rs.next())
    │       ├── Extraer datos
    │       ├── Crear objeto Products
    │       └── Agregar a lista
    │
    ├── PASO 6: Retornar lista
    │   └── return productos (ordenada según el parámetro)
    │
    └── PASO 7: catch (SQLException e)
        └── return new ArrayList<>()
```

---

### **⚠️ IMPORTANTE: ¿Por qué NO usar PreparedStatement aquí?**

**Problema con ORDER BY y parámetros:**

```sql
-- ❌ ESTO NO FUNCIONA:
SELECT * FROM oil_products ORDER BY priceXUnd ?
pstmt.setString(1, "ASC");

-- MySQL interpreta "ASC" como un STRING LITERAL, no como palabra clave
-- Resultado: Error de sintaxis o comportamiento inesperado
```

**Soluciones:**

**Opción A - Validar y concatenar (usaremos esta):**
```java
// Validar primero (previene SQL Injection)
if (!order.equals("ASC") && !order.equals("DESC")) {
    order = "ASC";  // Valor por defecto seguro
}

// Ahora es seguro concatenar
String sql = "SELECT * FROM oil_products ORDER BY priceXUnd " + order;
```

**Opción B - Switch/case para construir query completa:**
```java
String sql;
switch (order.toUpperCase()) {
    case "DESC":
        sql = "SELECT * FROM oil_products ORDER BY priceXUnd DESC";
        break;
    case "ASC":
    default:
        sql = "SELECT * FROM oil_products ORDER BY priceXUnd ASC";
        break;
}
```

**Pregunta:** ¿Por qué Opción A es más flexible que Opción B?

---

### **Tareas paso a paso:**

#### **PASO 1: Pensar ANTES de codificar (Pseudocódigo)**

```
PSEUDOCÓDIGO del método getAllProductsOrderByPrice:

ENTRADA: String order (puede ser "ASC", "DESC", o cualquier cosa)

SALIDA: List<Products> (ordenada por priceXUnd)

PROCESO:
1. VALIDAR el parámetro order:
   SI order NO es "ASC" Y NO es "DESC":
       ASIGNAR order = "ASC" (valor por defecto seguro)
   FIN SI
   
   NOTA: Esto previene SQL Injection si order viene del usuario
         Ej: Si usuario ingresa "ASC; DROP TABLE oil_products;"
             La validación lo cambia a "ASC" (seguro)

2. CREAR lista vacía de Products

3. CONSTRUIR query SQL dinámicamente:
   "SELECT id, name, unidadDeMedida, priceXUnd 
    FROM oil_products 
    ORDER BY priceXUnd " + order
   
   NOTA: Aquí NO usamos "?" porque ORDER BY no acepta parámetros

4. OBTENER conexión
5. CREAR Statement (no PreparedStatement, porque no hay "?")
6. EJECUTAR query

7. MIENTRAS haya filas:
   a. EXTRAER datos
   b. CREAR objeto Products
   c. AGREGAR a lista

8. RETORNAR lista (ya ordenada por MySQL)

EN CASO DE ERROR:
   - IMPRIMIR error
   - RETORNAR lista vacía
```

**Preguntas reflexivas:**
- ¿Por qué validar `order` antes de concatenar en el SQL?
- ¿Qué pasaría sin validación si el usuario ingresa `"ASC; DELETE FROM oil_products"`?
- ¿Por qué usamos `Statement` y no `PreparedStatement` aquí?

---

#### **PASO 2: Implementar en ProductServices.java**

**a) Ubicación:**
   - Agrega después de `getProductsByPriceRange()`

**b) Firma del método:**
```java
// TÚ escribes:
public static List<Products> getAllProductsOrderByPrice(String order) {
    // ... tu código aquí
}
```

**c) Validación del parámetro (CRÍTICA para seguridad):**
```java
// TÚ escribes:
if (!order.equalsIgnoreCase("ASC") && !order.equalsIgnoreCase("DESC")) {
    System.out.println("Orden inválido. Usando ASC por defecto.");
    order = "ASC";
}
```

**Pregunta:** ¿Por qué `equalsIgnoreCase()` en vez de `equals()`?
**Respuesta:** Para aceptar "asc", "Asc", "ASC", "desc", "Desc", "DESC" (más flexible)

**d) Crear lista vacía:**
```java
List<Products> productos = new ArrayList<>();
```

**e) Query SQL con ORDER BY:**
```java
// TÚ escribes:
String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
             "FROM oil_products " +
             "ORDER BY priceXUnd " + order;
             //                  ↑
             //            Concatenación segura (ya validamos)
```

**f) try-with-resources con Statement:**
```java
// TÚ escribes:
try (Connection conn = DatabaseConnection.getConnection();
     Statement stmt = conn.createStatement();
     //        ↑
     // Statement (no PreparedStatement) porque no hay "?"
     ResultSet rs = stmt.executeQuery(sql)) {
    
    // TÚ escribes: Recorrer resultados
    while (rs.next()) {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String unidad = rs.getString("unidadDeMedida");
        double price = rs.getDouble("priceXUnd");
        
        Products producto = new Products(id, name, unidad, price);
        productos.add(producto);
    }
    
} catch (SQLException e) {
    System.err.println("Error al obtener productos ordenados: " + e.getMessage());
}

return productos;
```

---

#### **PASO 3: Compilar**

```bash
mvn clean compile
```

---

#### **PASO 4: Probar en Main.java**

**a) Prueba 1 - Orden ascendente (más barato primero):**
```java
// TÚ escribes:
System.out.println("\n=== COMBUSTIBLES: MÁS BARATO PRIMERO ===");
List<Products> ascendente = ProductServices.getAllProductsOrderByPrice("ASC");

for (Products p : ascendente) {
    System.out.println(p);
}
```

**b) Prueba 2 - Orden descendente (más caro primero):**
```java
// TÚ escribes:
System.out.println("\n=== COMBUSTIBLES: MÁS CARO PRIMERO ===");
List<Products> descendente = ProductServices.getAllProductsOrderByPrice("DESC");

for (Products p : descendente) {
    System.out.println(p);
}
```

**c) Prueba 3 - Validación de orden inválido:**
```java
// TÚ escribes:
System.out.println("\n=== PRUEBA: ORDEN INVÁLIDO ===");
List<Products> invalido = ProductServices.getAllProductsOrderByPrice("RANDOM");
// Debería usar ASC por defecto
```

---

#### **PASO 5: Ejecutar y verificar**

```bash
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

**Resultado esperado:**
```
=== COMBUSTIBLES: MÁS BARATO PRIMERO ===
┌─────────────┬──────────────────┬─────────────────┬──────────────┐
│ FUEL-001    │ Diesel Regular   │ Litros          │ $5,200.00    │  ← Más barato
│ FUEL-004    │ Gasolina 91      │ Litros          │ $5,900.00    │
│ FUEL-003    │ Gasolina 93      │ Litros          │ $6,800.00    │
│ FUEL-002    │ Gasolina 97      │ Galones         │ $22,500.00   │
└─────────────┴──────────────────┴─────────────────┴──────────────┘

=== COMBUSTIBLES: MÁS CARO PRIMERO ===
┌─────────────┬──────────────────┬─────────────────┬──────────────┐
│ FUEL-002    │ Gasolina 97      │ Galones         │ $22,500.00   │  ← Más caro
│ FUEL-003    │ Gasolina 93      │ Litros          │ $6,800.00    │
│ FUEL-004    │ Gasolina 91      │ Litros          │ $5,900.00    │
│ FUEL-001    │ Diesel Regular   │ Litros          │ $5,200.00    │  ← Más barato
└─────────────┴──────────────────┴─────────────────┴──────────────┘
```

---

### **💡 Conceptos Clave - ORDER BY**

#### **1. ASC vs DESC**

```sql
-- ASC (Ascending = Ascendente):
ORDER BY priceXUnd ASC
-- Resultado: 5200, 5900, 6800, 22500
-- Más pequeño → Más grande

-- DESC (Descending = Descendente):
ORDER BY priceXUnd DESC
-- Resultado: 22500, 6800, 5900, 5200
-- Más grande → Más pequeño

-- Si omites ASC/DESC, por defecto es ASC:
ORDER BY priceXUnd      -- Equivale a: ORDER BY priceXUnd ASC
```

---

#### **2. Ordenamiento por múltiples columnas**

```sql
-- Primero por unidadDeMedida, luego por priceXUnd:
ORDER BY unidadDeMedida ASC, priceXUnd DESC

-- Resultado:
Galones: 22500, 21000  (descendente por precio)
Litros: 6800, 5900, 5200  (descendente por precio)
```

---

#### **3. Combinar WHERE con ORDER BY**

```java
// Ejemplo: Productos en Litros, ordenados por precio
String sql = "SELECT * FROM oil_products " +
             "WHERE unidadDeMedida = 'Litros' " +
             "ORDER BY priceXUnd ASC";

// Orden de cláusulas SQL (IMPORTANTE):
// SELECT ... FROM ... WHERE ... ORDER BY ... LIMIT
```

---

### **✅ Resultado esperado:**

- ✅ Método `getAllProductsOrderByPrice(String order)` implementado
- ✅ Validación de parámetro `order` funciona correctamente
- ✅ Usa `Statement` (no `PreparedStatement`) porque no hay `?`
- ✅ Main.java prueba orden ascendente y descendente
- ✅ Entiendes por qué ORDER BY no puede usar `?` en PreparedStatement
- ✅ Comprendes la diferencia entre ASC (menor→mayor) y DESC (mayor→menor)

**Estructura final ProductServices:**
```
com.forestech/services/ProductServices.java
├── getAllProducts()                        ← Checkpoint 3.4
├── getProductsByUnidadDeMedida(String)     ← Checkpoint 3.5
├── getProductsByPriceRange(double, double) ← Checkpoint 3.6
└── getAllProductsOrderByPrice(String)      ← Checkpoint 3.7 (NUEVO)
```

**⏱️ Tiempo estimado:** 2 horas

---

## ⚠️ IMPORTANTE: Límite de Fase 3 - ¿Dónde están UPDATE y DELETE?

### **🛑 EN ESTA FASE 3 SOLO VISTE LECTURA (SELECT)**

**Lo que aprendiste en Fase 3:**
- ✅ Checkpoint 3.4: SELECT básico (todos los productos)
- ✅ Checkpoint 3.5: SELECT con 1 parámetro (filtro por unidad)
- ✅ Checkpoint 3.6: SELECT con 2 parámetros (rango de precio)
- ✅ Checkpoint 3.7: SELECT con ORDER BY (ordenamiento)

**Lo que NO viste (por diseño pedagógico):**
- ❌ **INSERT** - Crear nuevos registros
- ❌ **UPDATE** - Modificar registros existentes
- ❌ **DELETE** - Eliminar registros

---

### **🎯 ¿Por qué esta separación?**

**Razón pedagógica:**

Fase 3 se enfoca en **ENTENDER cómo funciona JDBC** con la operación más segura (SELECT):
- ✅ Aprendiste conexión a BD sin riesgo de modificar datos
- ✅ Practicaste PreparedStatement con queries de solo lectura
- ✅ Entendiste ResultSet sin preocuparte por corromper datos
- ✅ Experimentaste sin miedo ("si falla, no perdiste nada")

**Fase 4 agregará las operaciones de ESCRITURA con responsabilidad:**
- ⚠️ INSERT - Crea datos permanentes (debes validar correctamente)
- ⚠️ UPDATE - Modifica datos existentes (un error puede arruinar registros)
- ⚠️ DELETE - Elimina datos (irreversible sin backup)

---

### **📅 ¿Cuándo verás UPDATE y DELETE?**

**FASE 4: Operaciones CRUD Completas**

Estructura de Fase 4:
```
Checkpoint 4.1: INSERT - Crear productos desde Java
Checkpoint 4.2: UPDATE - Modificar productos existentes
Checkpoint 4.3: DELETE - Eliminar productos (soft delete vs hard delete)
Checkpoint 4.4: Transacciones - Operaciones atómicas (todo o nada)
Checkpoint 4.5: MovementService completo con CRUD
```

---

### **🎓 Analogía: Aprender a conducir**

```
FASE 3 (SELECT - solo lectura):
→ Sentarte en un auto estacionado
→ Practicar con el volante, pedales, palanca
→ Entender cómo funciona SIN MOVER EL AUTO
→ Seguro, sin riesgo de choque

FASE 4 (INSERT/UPDATE/DELETE):
→ Encender el motor y CONDUCIR
→ Ahora tus acciones tienen consecuencias reales
→ Debes manejar con responsabilidad
→ Un error puede causar "accidentes" (datos corruptos)
```

---

### **✅ Checklist antes de pasar a Fase 4**

Verifica que dominas estos conceptos de Fase 3:

- [ ] Entiendo qué es JDBC y para qué sirve
- [ ] Sé conectarme a MySQL con DatabaseConnection
- [ ] Puedo escribir queries SELECT básicos
- [ ] Entiendo qué es PreparedStatement y por qué es seguro
- [ ] Sé usar `setString()`, `setDouble()` con parámetros
- [ ] Entiendo cómo funciona ResultSet como cursor
- [ ] Puedo mapear ResultSet a objetos Java
- [ ] Comprendo la diferencia entre Statement y PreparedStatement
- [ ] Sé prevenir SQL Injection con PreparedStatement
- [ ] Puedo usar múltiples parámetros en una query
- [ ] Entiendo ORDER BY y sus limitaciones con "?"
- [ ] Sé combinar WHERE con ORDER BY

**Si marcaste TODOS los checkboxes, ¡estás listo para Fase 4!** 🚀

**Si hay dudas, REVISA antes de continuar. Fase 4 construye sobre estos fundamentos.**

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
