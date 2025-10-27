# 🔌 FASE 3: CONEXIÓN A MYSQL LOCAL (Semana 5)

> Objetivo general: instalar MySQL en tu PC, comprender JDBC, conectar Java con MySQL y ejecutar las primeras consultas.

---

## 🧠 Antes de empezar

- 💿 **Instalar MySQL:** Primero instalarás MySQL Server en tu computadora local
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

## ✅ Checkpoint 3.0: Instalar MySQL y Crear Base de Datos

**Concepto clave:** MySQL es un sistema de gestión de bases de datos relacional de código abierto. Es una de las bases de datos más populares del mundo y perfecta para aprender.

**📍 DÓNDE:** 
- **Instalación:** En tu computadora local (Windows)
- **Herramienta:** MySQL Command Line Client o MySQL Workbench
- **Base de datos:** FORESTECH (la crearemos)

**🎯 PARA QUÉ:** 
- ✅ **Tener control total:** Base de datos en tu PC, sin depender de servicios externos
- ✅ **Aprender sin límites:** Puedes crear, modificar y eliminar sin restricciones
- ✅ **Velocidad:** Sin latencia de red, todo es instantáneo
- ✅ **Gratuito:** MySQL Community Edition es totalmente gratis
- ✅ **Portabilidad:** Fácil de respaldar y restaurar

**🎓 Analogía:**
- **Base de datos remota (DigitalOcean):** Arrendar un depósito lejos de tu casa
- **Base de datos local (MySQL):** Tener tu propio depósito en casa (acceso 24/7, sin pagar renta)

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre MySQL Server y MySQL Workbench?"
"¿Qué es el puerto 3306 y por qué MySQL lo usa?"
"Explícame qué es un usuario 'root' en MySQL."
"¿Qué diferencia hay entre MySQL y SQL Server?"
```

---

### 📥 PASO 1: Descargar e Instalar MySQL

**Tareas:**

1. **Descargar MySQL Community Server:**
   - Ve a: https://dev.mysql.com/downloads/mysql/
   - Selecciona: **Windows (x86, 64-bit), MySQL Installer MSI**
   - Descarga: **mysql-installer-community** (versión más reciente)
   - Tamaño aproximado: 450 MB

2. **Ejecutar el instalador:**
   - Doble clic en el archivo descargado
   - Tipo de instalación: Selecciona **"Developer Default"**
     - Esto instala: MySQL Server, MySQL Workbench, MySQL Shell, conectores
   - **Pregunta:** ¿Por qué "Developer Default" y no "Server only"?

3. **Configuración del servidor:**
   
   a) **Tipo y red:**
      - Config Type: **Development Computer**
      - Port: **3306** (default, déjalo así)
      - ✅ Marca: "Open Windows Firewall port for network access"
   
   b) **Autenticación:**
      - Método: **Use Strong Password Encryption** (recomendado)
   
   c) **Contraseña root:**
      - Usuario: **root** (viene por defecto)
      - Password: Elige una contraseña FÁCIL DE RECORDAR
      - Ejemplo: `root123` o `forestech2025`
      - ⚠️ **IMPORTANTE:** Anota esta contraseña, la usarás en Java
   
   d) **Windows Service:**
      - ✅ Marca: "Configure MySQL Server as a Windows Service"
      - Service Name: **MySQL80** (o la versión que instalaste)
      - ✅ Marca: "Start the MySQL Server at System Startup"
      - **Pregunta:** ¿Por qué es útil que inicie automáticamente?
   
   e) **Aplicar configuración:**
      - Clic en "Execute" y espera a que termine
      - Deberías ver ✅ en todos los pasos

4. **Verificar instalación:**
   
   **Opción A - Por Command Line:**
   - Abre CMD (Símbolo del sistema)
   - Ejecuta:
     ```
     mysql --version
     ```
   - Resultado esperado: `mysql  Ver 8.x.x for Win64 on x86_64`
   
   **Opción B - Por Workbench:**
   - Abre MySQL Workbench
   - Deberías ver una conexión "Local instance MySQL80"
   - Haz clic para conectar
   - Ingresa tu contraseña root

**✅ Resultado esperado:** 
- MySQL Server instalado y corriendo
- Puedes conectarte con usuario root
- MySQL Workbench instalado y funcional

---

### 🗄️ PASO 2: Conceptos Básicos de MySQL

**Antes de crear la base de datos, entiende estos conceptos:**

**🎓 Jerarquía en MySQL:**
```
MySQL Server (el servicio que corre en tu PC)
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

### 🏗️ PASO 3: Crear Base de Datos FORESTECH

**Tareas (TÚ ejecutas cada comando):**

1. **Abrir MySQL Command Line Client:**
   - Busca en el menú inicio: "MySQL Command Line Client"
   - O abre CMD y ejecuta: `mysql -u root -p`
   - Ingresa tu contraseña root

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

### 📋 PASO 4: Crear Tabla de Productos

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

**🎓 Tipos de datos más comunes:**

| Tipo SQL | Para qué sirve | Ejemplo |
|----------|----------------|---------|
| `INT` | Números enteros | 42, -10, 0 |
| `DOUBLE` | Números decimales | 3.14, -0.5 |
| `VARCHAR(n)` | Texto corto (máx n caracteres) | "Diesel", "Juan" |
| `TEXT` | Texto largo | Descripciones, comentarios |
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

## ✅ Checkpoint 3.1: Configurar JDBC Driver

**Concepto clave:** JDBC es la API estándar de Java para comunicarse con bases de datos.

**📍 DÓNDE:** 
- **Archivo:** `pom.xml` en la raíz del proyecto `forestech-cli-java/`
- **Terminal:** Para ejecutar Maven
- **Main.java:** NO tocar (todavía no usaremos la BD)

**🎯 PARA QUÉ:** 
Sin el driver JDBC de SQL Server, Java no puede "hablar" con tu base de datos. Es como tener un teléfono sin SIM card.

El driver JDBC:
- ✅ **Traduce** las llamadas de Java a comandos que SQL Server entiende
- ✅ **Gestiona** la conexión de red entre tu aplicación y el servidor
- ✅ **Maneja** el protocolo de comunicación específico de SQL Server (TDS - Tabular Data Stream)

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Usarás este driver para INSERT, UPDATE, DELETE de movimientos
- **Fase 5:** Ejecutarás queries complejas con JOINs entre tablas
- **Fase 9:** Generarás reportes consultando datos históricos

**🎓 Analogía:**
- **Tu aplicación Java**: Turista que solo habla español
- **SQL Server**: Local que solo habla alemán
- **Driver JDBC**: Traductor que permite la comunicación

**Prompts sugeridos:**
```text
"Explícame con analogía cómo funciona JDBC como puente entre Java y SQL Server."
"¿Dónde guarda Maven las dependencias y cómo puedo verificarlo?"
"¿Qué diferencia hay entre JDBC (API) y el driver de SQL Server (implementación)?"
"¿Por qué cada base de datos (MySQL, PostgreSQL, SQL Server) necesita su propio driver?"
```

**Diagrama de tareas - Configurar JDBC Driver:**

```
pom.xml
│
└── Sección <dependencies>
    │
    └── Nueva dependencia <dependency>
        ├── <groupId>: com.microsoft.sqlserver
        ├── <artifactId>: mssql-jdbc
        └── <version>: 12.8.1.jre11 (o superior)

Propósito: Esto permite que Maven descargue el driver de SQL Server
```

**Tareas paso a paso (TÚ completas cada una):**

1. **Abrir `pom.xml`** en el editor
   - Localiza la sección `<dependencies>`
   - Si no existe, debes crearla dentro de `<project>` (antes de `</project>`)

2. **Agregar la dependencia:**
   - Estructura de una dependencia:
     ```
     <dependency>
         <groupId>...</groupId>
         <artifactId>...</artifactId>
         <version>...</version>
     </dependency>
     ```
   - **TÚ completas:** Coloca el groupId, artifactId y version para el driver de SQL Server
   - **Pregunta:** ¿Por qué `jre11` en la versión? ¿Qué pasa si tu proyecto usa Java 17?

3. **Guardar** el archivo pom.xml

4. **Descargar la dependencia desde terminal:**
   - Comando a ejecutar:
     ```bash
     cd forestech-cli-java/
     mvn clean install
     ```
   - Deberías ver líneas en la salida mencionando la descarga del JAR

5. **Verificar descarga exitosa:**
   
   **Opción A - Por terminal:**
   - Ejecuta:
     ```
     ls ~/.m2/repository/com/microsoft/sqlserver/mssql-jdbc/
     ```
   - Resultado esperado: Carpeta con tu versión (ej: `12.8.1.jre11/`)
   
   **Opción B - En IntelliJ:**
   - Ve a: View → Tool Windows → Maven
   - Expande: Dependencies → compile
   - Busca: `com.microsoft.sqlserver:mssql-jdbc`
   - Resultado esperado: Debes verlo en la lista

6. **Compilar el proyecto:**
   - Ejecuta:
     ```
     mvn clean compile
     ```
   - Resultado esperado: Compilación exitosa SIN errores

**✅ Resultado esperado:** 
- Maven descarga el driver sin errores
- El proyecto compila exitosamente con `mvn clean compile`
- Puedes ver el JAR del driver en tu repositorio local Maven
- En IntelliJ, la dependencia aparece en el árbol de Maven

**💡 Concepto clave:** Las dependencias de Maven se descargan UNA VEZ y se reutilizan en todos tus proyectos. Por eso se guardan en `~/.m2/repository/` (repositorio local compartido).

**⚠️ PROBLEMAS COMUNES:**

| Problema | Causa | Solución |
|----------|-------|----------|
| "Could not resolve dependencies" | Sin internet o proxy | Verifica conexión y configuración de Maven |
| "Unsupported class version" | Driver para Java superior | Usa versión jre11 o jre8 |
| Dependencia no aparece en IntelliJ | Cache desactualizado | Reimport: clic derecho en proyecto → Maven → Reload Project |

**⏱️ Tiempo estimado:** 30 minutos

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
```

**Diagrama de estructura - Clase DatabaseConnection:**

```
DatabaseConnection.java
│
├── Constructor privado (patrón Utility Class)
│   └── Sin parámetros, cuerpo vacío
│
├── Constantes de configuración (private static final)
│   ├── URL → "jdbc:sqlserver://localhost:1433;databaseName=FORESTECH"
│   ├── USER → Tu usuario SQL Server (ej: "sa")
│   └── PASSWORD → Tu contraseña SQL Server
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
   - **URL formato:** `jdbc:sqlserver://localhost:1433;databaseName=FORESTECH`
   - **Pregunta:** ¿Qué significa cada parte de la URL? Investiga: `jdbc`, `sqlserver`, `1433`, `databaseName`

5. **Implementar getConnection() (TÚ lo escribes):**
   - Usar `DriverManager.getConnection(URL, USER, PASSWORD)`
   - Retornar la Connection obtenida
   - Agregar los tres imports de java.sql

6. **Implementar testConnection() (TÚ lo escribes):**
   
   a) **Estructura try-with-resources:**
      - Patrón:
        ```
        try (Connection conn = getConnection()) {
            // Tu código aquí
        } catch (SQLException e) {
            // Tu código aquí
        }
        ```
   
   b) **En el try:**
      - Extrae metadata: `conn.getMetaData()`
      - Imprime nombre de BD: `metadata.getDatabaseProductName()`
      - Imprime mensaje de éxito
   
   c) **En el catch:**
      - Imprime error: `e.getMessage()`
   
   d) **Pregunta:** ¿Por qué try-with-resources vs try-finally manual?

7. **Probar en Main.java:**
   - Agrega esta línea (donde corresponda):
     ```
     DatabaseConnection.testConnection();
     ```
   - Compila: `mvn clean compile`
   - Ejecuta: `mvn exec:java -Dexec.mainClass="com.forestech.Main"`

**✅ Resultado esperado:** 
- Ver mensaje "✅ Conexión exitosa a FORESTECH (o el nombre de tu BD)" en consola
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
```
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
}  // ✅ Se cierra automáticamente
```

**⚠️ PREREQUISITOS:**
- SQL Server debe estar corriendo (verifica con SQL Server Management Studio)
- El puerto 1433 debe estar abierto
- La base de datos FORESTECH debe existir
- Usuario/contraseña deben ser correctos

**🔍 Depuración - Errores comunes:**

| Error | Causa probable | Solución |
|-------|---------------|----------|
| "Cannot connect to server" | SQL Server no está corriendo | Inicia el servicio SQL Server |
| "Login failed for user" | Credenciales incorrectas | Verifica usuario/contraseña |
| "Database 'FORESTECH' does not exist" | BD no creada | Crea la BD en SQL Server Management Studio |
| "Connection timed out" | Firewall o puerto cerrado | Verifica firewall y que puerto 1433 esté abierto |
| "No suitable driver found" | Driver no descargado | Ejecuta `mvn clean install` |

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 3.3: Primera query `SELECT` con Service

**Concepto clave:** Los Services son clases especializadas en interactuar con la base de datos. Separan la lógica de acceso a datos de la lógica de negocio.

**📍 DÓNDE:** 
- **Crear paquete nuevo:** `services` dentro de `com.forestech/`
- **Crear archivo:** `ProductService.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
- **Main.java:** Para PROBAR el servicio (llamar al método)

**🎯 PARA QUÉ:** 
Hasta ahora solo probaste la conexión. Ahora necesitas:
- ✅ **Leer datos** de las tablas existentes en SQL Server
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
SQL Server (base de datos)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Crearás `MovementService` con INSERT, UPDATE, DELETE siguiendo este patrón
- **Fase 5:** Los Services retornarán listas de objetos (no solo imprimirán)
- **Fase 6:** El menú interactivo llamará a Services para mostrar datos al usuario
- **Fase 9:** Services generarán reportes con queries complejas

**🎓 Analogía:**
- **ProductService**: Bibliotecario que busca libros en el sistema
- **DatabaseConnection**: Llave de acceso a la biblioteca
- **SQL Server**: Los estantes con todos los libros
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

5. **Verificar tabla en SQL Server:**
   - Ejecuta manualmente en SQL Server Management Studio:
     ```
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
   → Abre canal de comunicación con SQL Server

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
| No imprime nada (while no se ejecuta) | Tabla vacía | Inserta datos de prueba en SQL Server |
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
| "Cannot connect to server" | SQL Server no corriendo | Inicia el servicio SQL Server |
| "Login failed for user" | Credenciales incorrectas | Verifica usuario/contraseña en DatabaseConnection |
| "Database does not exist" | BD no creada | Crea BD FORESTECH en SQL Server Management Studio |
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
- Podrás **crear movimientos** desde tu aplicación (no solo desde SQL Server)
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
