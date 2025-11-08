# FASE 03.3 - CONEXIÓN JDBC A MYSQL

> **Objetivo de Aprendizaje:** Comprender la arquitectura JDBC, configurar las dependencias de Maven necesarias, crear una clase de conexión a MySQL reutilizable, y dominar el patrón try-with-resources para gestionar recursos de bases de datos de forma segura.

---

## 📚 Tabla de Contenidos

1. [Introducción: Conectando Java con MySQL](#1-introducción-conectando-java-con-mysql)
2. [¿Qué es JDBC?](#2-qué-es-jdbc)
3. [Arquitectura JDBC: Los 4 Componentes](#3-arquitectura-jdbc-los-4-componentes)
4. [Drivers JDBC: El Puente entre Java y MySQL](#4-drivers-jdbc-el-puente-entre-java-y-mysql)
5. [Agregando la Dependencia MySQL en Maven](#5-agregando-la-dependencia-mysql-en-maven)
6. [Anatomía de un Connection String](#6-anatomía-de-un-connection-string)
7. [Creando el Package config/](#7-creando-el-package-config)
8. [Implementando DatabaseConnection.java](#8-implementando-databaseconnectionjava)
9. [Try-With-Resources: Gestión Automática de Recursos](#9-try-with-resources-gestión-automática-de-recursos)
10. [Probando la Conexión](#10-probando-la-conexión)
11. [Tabla de Errores Comunes y Soluciones](#11-tabla-de-errores-comunes-y-soluciones)
12. [Ejercicios Prácticos](#12-ejercicios-prácticos)
13. [Git Checkpoint](#13-git-checkpoint)
14. [Generador de Quiz de Validación](#14-generador-de-quiz-de-validación)
15. [Checkpoint de Fase](#15-checkpoint-de-fase)

---

## 1. Introducción: Conectando Java con MySQL

### El Problema que Resolvemos

En las fases anteriores (0-2), todo el código Java trabajaba con datos **en memoria**:

```java
// Fase 2: ArrayList en memoria
List<Movement> movements = new ArrayList<>();
movements.add(new Movement("MOV-001", "ENTRADA", "Diesel", 1000, 5000));

// Problema: Al cerrar la aplicación, movements se pierde
```

En **Fase 03.1 y 03.2**, aprendimos a:
- Crear una base de datos MySQL (`FORESTECH`)
- Crear tablas (`oil_products`)
- Insertar datos con `INSERT`
- Consultar datos con `SELECT`

Pero todo esto lo hicimos **manualmente** desde la terminal MySQL:

```bash
# Terminal WSL - MySQL CLI
mysql> SELECT * FROM oil_products;
```

### La Pregunta Crítica

**¿Cómo ejecutamos estas consultas SQL DESDE Java?**

```
┌─────────────────────┐        ¿?         ┌─────────────────────┐
│   Aplicación Java   │  <──────────────>  │   MySQL Database    │
│   (Main.java)       │                   │   (FORESTECH)       │
└─────────────────────┘                   └─────────────────────┘
```

**Respuesta:** Usando **JDBC (Java Database Connectivity)**.

---

## 2. ¿Qué es JDBC?

### Definición Oficial

**JDBC (Java Database Connectivity)** es una API estándar de Java que permite a las aplicaciones:
- Conectarse a bases de datos relacionales (MySQL, PostgreSQL, Oracle, SQL Server, etc.)
- Ejecutar consultas SQL
- Procesar resultados
- Manejar transacciones

### Analogía Forestech: El Traductor Universal

Imagina que necesitas comunicarte con proveedores internacionales:

```
┌──────────────────────────────────────────────────────────┐
│              FORESTECH INTERNATIONAL                     │
│                                                           │
│  Gerente (Java)  <──>  Traductor (JDBC)  <──>  Proveedor│
│                                                           │
│  "Quiero 1000L     Convierte a      "SELECT * FROM       │
│   de Diesel"       lenguaje SQL      oil_products        │
│                                       WHERE name=..."     │
│                    Traduce de        Retorna filas        │
│  Procesa respuesta vuelta a Java     de MySQL            │
└──────────────────────────────────────────────────────────┘
```

- **Gerente (Java):** Tu código (`Main.java`)
- **Traductor (JDBC):** Librería que traduce llamadas Java a SQL
- **Proveedor (MySQL):** Base de datos que almacena los datos

### JDBC es una Especificación

**Importante:** JDBC define **QUÉ** se puede hacer (interfaces), pero cada fabricante de base de datos implementa **CÓMO** se hace (drivers específicos).

```
JDBC API (java.sql.*)
  ↓ Implementado por:
  ├─→ MySQL Connector/J (para MySQL)
  ├─→ PostgreSQL JDBC Driver (para PostgreSQL)
  ├─→ Oracle JDBC Driver (para Oracle)
  └─→ Microsoft JDBC Driver (para SQL Server)
```

**En Forestech:** Usaremos **MySQL Connector/J** (el driver oficial de MySQL).

---

## 3. Arquitectura JDBC: Los 4 Componentes

### Diagrama de Arquitectura Completo

```
┌───────────────────────────────────────────────────────────────────┐
│                     ARQUITECTURA JDBC                             │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌───────────────────────────────────────────────────────┐       │
│  │  1️⃣  APLICACIÓN JAVA (Main.java, Services)           │       │
│  │      - Lógica de negocio                              │       │
│  │      - Llamadas JDBC API (Connection, Statement...)   │       │
│  └──────────────────────┬────────────────────────────────┘       │
│                         │                                         │
│                         ↓ usa interfaces de                      │
│  ┌───────────────────────────────────────────────────────┐       │
│  │  2️⃣  JDBC API (java.sql.*)                            │       │
│  │      - Interfaces estándar:                           │       │
│  │        · Connection                                   │       │
│  │        · Statement / PreparedStatement                │       │
│  │        · ResultSet                                    │       │
│  │      - Clase concreta: DriverManager                  │       │
│  └──────────────────────┬────────────────────────────────┘       │
│                         │                                         │
│                         ↓ coordina                               │
│  ┌───────────────────────────────────────────────────────┐       │
│  │  3️⃣  JDBC DRIVER (MySQL Connector/J)                  │       │
│  │      - Implementación específica de MySQL             │       │
│  │      - Traduce llamadas Java → Protocolo MySQL        │       │
│  │      - JAR: mysql-connector-j-8.0.33.jar              │       │
│  └──────────────────────┬────────────────────────────────┘       │
│                         │                                         │
│                         ↓ comunica con                           │
│  ┌───────────────────────────────────────────────────────┐       │
│  │  4️⃣  MYSQL DATABASE (Servidor MySQL)                  │       │
│  │      - Servidor corriendo en localhost:3306           │       │
│  │      - Base de datos FORESTECH                        │       │
│  │      - Tablas: oil_products, combustibles_movements   │       │
│  └───────────────────────────────────────────────────────┘       │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### Explicación de Cada Capa

#### 1️⃣ Aplicación Java

Tu código que necesita interactuar con la base de datos.

```java
// Ejemplo de código en tu aplicación
Connection conn = DriverManager.getConnection(url, user, password);
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products");
```

#### 2️⃣ JDBC API (java.sql.*)

Conjunto de **interfaces y clases** estándar de Java:

| Componente | Tipo | Responsabilidad |
|------------|------|-----------------|
| `DriverManager` | Clase concreta | Gestiona drivers, crea conexiones |
| `Connection` | Interface | Representa conexión activa a BD |
| `Statement` | Interface | Ejecuta SQL estático |
| `PreparedStatement` | Interface | Ejecuta SQL parametrizado (Fase 03.5) |
| `ResultSet` | Interface | Representa resultados de SELECT |
| `SQLException` | Clase de excepción | Errores de BD |

**Clave:** Como son **interfaces**, no saben hablar directamente con MySQL. Necesitan un driver.

#### 3️⃣ JDBC Driver (MySQL Connector/J)

**Implementación concreta** de las interfaces JDBC para MySQL.

```
MySQL Connector/J
├─ com.mysql.cj.jdbc.Driver            (implementa java.sql.Driver)
├─ com.mysql.cj.jdbc.ConnectionImpl    (implementa java.sql.Connection)
├─ com.mysql.cj.jdbc.StatementImpl     (implementa java.sql.Statement)
└─ com.mysql.cj.jdbc.ResultSetImpl     (implementa java.sql.ResultSet)
```

**Analogía:** Como un adaptador de enchufes. JDBC API es el enchufe estándar, el driver es el adaptador específico para MySQL.

#### 4️⃣ MySQL Database

El servidor de base de datos que escucha en `localhost:3306` y procesa las consultas SQL.

### Flujo Completo de una Query

```
PASO 1: Tu código llama a JDBC API
Main.java → Connection conn = DriverManager.getConnection(...);

PASO 2: DriverManager busca el driver adecuado
DriverManager → "¿Quién puede conectarse a jdbc:mysql://...?"
                MySQL Connector/J responde: "¡Yo!"

PASO 3: Driver establece conexión TCP/IP
MySQL Connector/J → Conecta socket a localhost:3306
                    Autentica con user/password

PASO 4: Driver retorna implementación de Connection
MySQL Connector/J → Retorna ConnectionImpl (que implementa Connection)

PASO 5: Tu código ejecuta query
Main.java → stmt.executeQuery("SELECT * FROM oil_products");

PASO 6: Driver traduce y envía al servidor
MySQL Connector/J → Serializa query en protocolo MySQL
                    Envía por socket

PASO 7: MySQL procesa y retorna filas
MySQL Server → Ejecuta SELECT, retorna filas binarias

PASO 8: Driver empaqueta resultados
MySQL Connector/J → Crea ResultSetImpl con las filas
                    Tu código itera con rs.next()
```

---

## 4. Drivers JDBC: El Puente entre Java y MySQL

### ¿Qué es un Driver JDBC?

Un **driver JDBC** es una librería (archivo `.jar`) que:
- Implementa las interfaces de `java.sql.*` para una BD específica
- Sabe cómo comunicarse con esa BD a nivel de red (protocolo nativo)
- Traduce objetos Java ↔ Tipos de datos de la BD

### Tipos de Drivers JDBC (Histórico)

Existen 4 tipos de drivers, pero solo el **Tipo 4** se usa hoy:

| Tipo | Nombre | Descripción | Uso Actual |
|------|--------|-------------|------------|
| Tipo 1 | JDBC-ODBC Bridge | Usa ODBC (Windows) | ❌ Obsoleto desde Java 8 |
| Tipo 2 | Native-API Driver | Usa librerías nativas de BD | ❌ Poco portable |
| Tipo 3 | Network Protocol Driver | Middleware intermedio | ❌ Casi no se usa |
| Tipo 4 | **Pure Java Driver** | **100% Java, directo a BD** | ✅ **Estándar actual** |

### MySQL Connector/J (Tipo 4)

```
mysql-connector-j-8.0.33.jar
├─ 100% código Java
├─ No requiere instalación nativa
├─ Portable (funciona en Windows, Linux, macOS)
├─ Se comunica directamente con MySQL Server via TCP/IP (puerto 3306)
└─ Compatible con MySQL 5.7, 8.0, 8.1+
```

**Ventajas:**
- ✅ Portable (mismo JAR en cualquier OS)
- ✅ No necesita configuración del sistema operativo
- ✅ Fácil distribución (solo agregar JAR al proyecto)

**En Forestech:** Usaremos **mysql-connector-j 8.0.33**, compatible con MySQL 8.x en WSL.

### ¿Por qué No Necesitamos "Instalar" el Driver?

En lenguajes como Python (con `pip install mysql-connector-python`), instalas drivers globalmente.

En Java con Maven, **Maven descarga el JAR automáticamente** cuando agregas la dependency en `pom.xml`. El JAR se guarda en:

```
~/.m2/repository/com/mysql/mysql-connector-j/8.0.33/
```

Maven incluye automáticamente este JAR en el classpath al compilar/ejecutar.

---

## 5. Agregando la Dependencia MySQL en Maven

### ¿Qué es una Dependency en Maven?

**Maven** es una herramienta de **gestión de proyectos** que:
- Gestiona dependencias (librerías externas)
- Compila código
- Ejecuta tests
- Empaqueta aplicaciones

Una **dependency** es una librería externa que tu proyecto necesita. Se declara en `pom.xml`.

### Estructura del pom.xml Actual

Abre tu archivo `/home/hp/forestechOil/forestech-cli-java/pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Identificación del proyecto -->
    <groupId>com.forestech</groupId>
    <artifactId>forestech-cli</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!-- Configuración de Java 17 -->
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <!-- Sección de dependencias -->
    <dependencies>
        <!-- Aquí agregaremos MySQL Connector/J -->

        <!-- JUnit Jupiter para testing (futuro) -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### Agregando MySQL Connector/J

**PASO 1:** Abre `pom.xml` con tu editor de texto favorito (VSCode, nano, vim).

**PASO 2:** Dentro de la sección `<dependencies>`, agrega:

```xml
<dependencies>
    <!-- MySQL Connector/J - Driver JDBC para MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- JUnit Jupiter para testing (futuro) -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Anatomía de una Dependency

```xml
<dependency>
    <groupId>com.mysql</groupId>           <!-- Organización/empresa -->
    <artifactId>mysql-connector-j</artifactId>  <!-- Nombre del producto -->
    <version>8.0.33</version>                    <!-- Versión específica -->
</dependency>
```

**Componentes:**

| Elemento | Valor | Significado |
|----------|-------|-------------|
| `groupId` | `com.mysql` | Organización propietaria (MySQL/Oracle) |
| `artifactId` | `mysql-connector-j` | Nombre del artefacto (JAR) |
| `version` | `8.0.33` | Versión del driver (lanzada mayo 2023) |

**Coordenadas Maven:** Juntos forman una **coordenada única** en el repositorio Maven Central:

```
com.mysql:mysql-connector-j:8.0.33
```

### ¿De Dónde Descarga Maven el JAR?

**Maven Central Repository:** [https://repo.maven.apache.org/maven2/](https://repo.maven.apache.org/maven2/)

```
Flujo de descarga:
1. Lees la dependency en pom.xml
2. Maven busca en cache local (~/.m2/repository)
3. Si no existe, descarga de Maven Central
4. Guarda en ~/.m2/repository/com/mysql/mysql-connector-j/8.0.33/
5. Incluye el JAR en el classpath automáticamente
```

### Descargando la Dependency

**PASO 3:** En la terminal, dentro de `forestech-cli-java/`, ejecuta:

```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
```

**Salida esperada:**

```
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.forestech:forestech-cli >-------------------
[INFO] Building forestech-cli 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
Downloading from central: https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.pom
Downloaded from central: https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.pom (3.2 kB)
Downloading from central: https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
Downloaded from central: https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar (2.5 MB)
[INFO]
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ forestech-cli ---
[INFO] Deleting /home/hp/forestechOil/forestech-cli-java/target
[INFO]
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ forestech-cli ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 10 source files to /home/hp/forestechOil/forestech-cli-java/target/classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**¡Listo!** Maven descargó el driver MySQL y está disponible para tu código.

### Verificando la Descarga

```bash
ls -lh ~/.m2/repository/com/mysql/mysql-connector-j/8.0.33/
```

**Salida esperada:**

```
total 2.5M
-rw-r--r-- 1 user user 2.5M jan 15 10:30 mysql-connector-j-8.0.33.jar
-rw-r--r-- 1 user user 3.2K jan 15 10:30 mysql-connector-j-8.0.33.pom
```

El archivo `mysql-connector-j-8.0.33.jar` contiene todas las clases del driver.

### Nota sobre Versiones

**¿Por qué 8.0.33 y no 9.x?**

- MySQL 8.0 es la versión LTS (Long Term Support) más estable
- Compatible con Java 8+
- Driver 8.0.33 funciona con MySQL 5.7, 8.0, 8.1, 8.2, 8.3

**Si usas MySQL 5.7 o anterior:** Cambia a version 5.1.x:

```xml
<version>5.1.49</version>
```

**En Forestech:** Usamos MySQL 8.0 en WSL, por lo tanto `8.0.33` es perfecto.

---

## 6. Anatomía de un Connection String

### ¿Qué es un Connection String?

Un **connection string** (cadena de conexión) es una URL especial que especifica:
- **Qué base de datos** usar (MySQL, PostgreSQL, etc.)
- **Dónde está** (host, puerto)
- **A qué database** conectarse
- **Parámetros opcionales** (encoding, timezone, SSL, etc.)

### Estructura General

```
jdbc:<subprotocol>://<host>:<port>/<database>?<parameters>
```

### Connection String para Forestech

```
jdbc:mysql://localhost:3306/FORESTECH
```

### Desglose Detallado

```
jdbc:mysql://localhost:3306/FORESTECH
└─┬─┘ └──┬──┘ └───┬───┘└─┬─┘└────┬────┘
  │      │        │      │       │
  │      │        │      │       └─→ 5. Nombre de la base de datos
  │      │        │      └─────────→ 4. Puerto (3306 = default MySQL)
  │      │        └────────────────→ 3. Host (localhost = esta máquina)
  │      └─────────────────────────→ 2. Subprotocolo (mysql)
  └────────────────────────────────→ 1. Protocolo (jdbc)
```

### Explicación de Cada Componente

#### 1. Protocolo: `jdbc`

Indica que es una conexión JDBC. **Siempre** empieza con `jdbc:`.

#### 2. Subprotocolo: `mysql`

Identifica el tipo de base de datos:
- `mysql` → MySQL
- `postgresql` → PostgreSQL
- `oracle:thin` → Oracle
- `sqlserver` → Microsoft SQL Server

**En Forestech:** Usamos `mysql`.

#### 3. Host: `localhost`

**Host** = Dirección del servidor MySQL.

| Valor | Significado |
|-------|-------------|
| `localhost` | Servidor en la misma máquina (127.0.0.1) |
| `192.168.1.100` | Servidor en red local con IP específica |
| `forestech-db.example.com` | Servidor remoto con dominio |

**En Forestech:** Como MySQL está en WSL en la misma máquina, usamos `localhost`.

**Nota para WSL:** A veces `localhost` no funciona. Alternativas:
- `127.0.0.1` (IP localhost explícita)
- `$(hostname).local` (nombre de host WSL)

#### 4. Puerto: `3306`

**Puerto TCP** donde MySQL escucha conexiones.

| BD | Puerto Default |
|----|----------------|
| MySQL | 3306 |
| PostgreSQL | 5432 |
| SQL Server | 1433 |
| Oracle | 1521 |

**En Forestech:** MySQL usa el puerto default `3306`.

**Si MySQL está en otro puerto:** Cambia el número:

```
jdbc:mysql://localhost:3307/FORESTECH  # Puerto personalizado
```

#### 5. Database: `FORESTECH`

Nombre de la **base de datos específica** dentro del servidor MySQL.

Un servidor MySQL puede tener múltiples bases de datos:

```
MySQL Server (localhost:3306)
├── mysql (base de datos del sistema)
├── information_schema (metadatos)
├── FORESTECH ← Conectamos aquí
└── otra_aplicacion
```

**Importante:** El database `FORESTECH` debe existir antes de conectarte. Lo creamos en Fase 03.1:

```sql
CREATE DATABASE FORESTECH;
```

### Parámetros Opcionales (Query String)

Puedes agregar parámetros adicionales después de `?`:

```
jdbc:mysql://localhost:3306/FORESTECH?useSSL=false&serverTimezone=UTC
```

**Parámetros comunes:**

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| `useSSL` | `false` / `true` | Desactiva SSL (para desarrollo local) |
| `serverTimezone` | `UTC` / `America/Bogota` | Zona horaria del servidor |
| `characterEncoding` | `utf8mb4` | Encoding de caracteres (español: tildes, ñ) |
| `allowPublicKeyRetrieval` | `true` | Permite autenticación con caching_sha2_password |

**Para Forestech (conexión local):**

```java
// Versión simple (suficiente para desarrollo)
String url = "jdbc:mysql://localhost:3306/FORESTECH";

// Versión con parámetros (recomendado para evitar warnings)
String url = "jdbc:mysql://localhost:3306/FORESTECH?useSSL=false&serverTimezone=UTC";
```

### Conexiones a Diferentes Ambientes

```java
// Desarrollo local (WSL)
jdbc:mysql://localhost:3306/FORESTECH

// Producción (DigitalOcean - futuro)
jdbc:mysql://24.199.89.134:1433/DBforestech

// Servidor de pruebas
jdbc:mysql://test-server.forestech.local:3306/FORESTECH_TEST
```

**En Fase 3:** Usaremos solo la versión local.

---

## 7. Creando el Package config/

### Organización del Código

Hasta ahora, la estructura del proyecto es:

```
src/main/java/com/forestech/
├── Main.java
├── AppConfig.java
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

### Creando el Package config/

Vamos a crear un nuevo package para **configuración de infraestructura**:

```bash
cd /home/hp/forestechOil/forestech-cli-java/src/main/java/com/forestech
mkdir config
```

**Propósito del package config/:**
- Configuración de conexión a base de datos
- Constantes de configuración (URLs, credenciales, etc.)
- Utilidades de configuración

**Estructura después de crear config/:**

```
src/main/java/com/forestech/
├── config/           ← NUEVO
│   └── DatabaseConnection.java
├── models/
├── managers/
└── ...
```

### Separación de Responsabilidades

| Package | Responsabilidad | Ejemplo |
|---------|-----------------|---------|
| `models/` | Entidades de dominio | `Product`, `Movement` |
| `config/` | **Configuración e infraestructura** | **`DatabaseConnection`** |
| `services/` | Lógica de negocio (Fase 03.4) | `ProductService` |
| `helpers/` | Utilidades de interfaz de usuario | `MenuHelper` |
| `utils/` | Utilidades generales | `IdGenerator` |

---

## 8. Implementando DatabaseConnection.java

### Objetivo de la Clase

`DatabaseConnection.java` será una **clase utilitaria** que:
- Encapsula los detalles de conexión (URL, user, password)
- Proporciona un método estático `getConnection()` para obtener una conexión
- Proporciona un método `testConnection()` para verificar conectividad

### Patrón de Diseño: Utility Class

```java
// Utility Class = Clase con solo métodos estáticos
public class DatabaseConnection {
    // No se instancia
    private DatabaseConnection() {}

    // Métodos estáticos
    public static Connection getConnection() { ... }
}

// Uso:
Connection conn = DatabaseConnection.getConnection(); // No necesitas "new"
```

### Código Completo de DatabaseConnection.java

Crea el archivo: `/home/hp/forestechOil/forestech-cli-java/src/main/java/com/forestech/config/DatabaseConnection.java`

```java
package com.forestech.config;

// Imports de JDBC (java.sql.*)
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos MySQL.
 *
 * Proporciona métodos estáticos para obtener conexiones a la BD FORESTECH,
 * encapsulando los detalles de configuración (URL, usuario, contraseña).
 *
 * @author Forestech Team
 * @version 1.0 - Fase 03.3
 */
public class DatabaseConnection {

    // ========== CONSTANTES DE CONFIGURACIÓN ==========

    /**
     * URL de conexión JDBC a MySQL.
     * Formato: jdbc:mysql://host:port/database
     */
    private static final String URL = "jdbc:mysql://localhost:3306/FORESTECH";

    /**
     * Usuario de MySQL.
     * En producción, esto debería venir de un archivo de configuración externo.
     */
    private static final String USER = "root";

    /**
     * Contraseña de MySQL.
     * IMPORTANTE: En producción, NUNCA hardcodear contraseñas.
     * Usar variables de entorno o archivos .properties en .gitignore.
     */
    private static final String PASSWORD = "tu_password_aqui"; // CAMBIAR POR TU PASSWORD

    // ========== CONSTRUCTOR PRIVADO ==========

    /**
     * Constructor privado para prevenir instanciación.
     * Esta es una utility class (solo métodos estáticos).
     */
    private DatabaseConnection() {
        throw new IllegalStateException("Utility class - No debe instanciarse");
    }

    // ========== MÉTODOS PÚBLICOS ==========

    /**
     * Obtiene una nueva conexión a la base de datos FORESTECH.
     *
     * Este método crea una conexión "fresca" cada vez que se llama.
     * Es responsabilidad del código que llama cerrar la conexión después de usarla.
     *
     * RECOMENDACIÓN: Usar try-with-resources para cierre automático:
     * <pre>
     * try (Connection conn = DatabaseConnection.getConnection()) {
     *     // Usar conn...
     * } // Se cierra automáticamente
     * </pre>
     *
     * @return Objeto Connection conectado a la BD FORESTECH
     * @throws SQLException Si hay error de conexión (BD no disponible, credenciales incorrectas, etc.)
     */
    public static Connection getConnection() throws SQLException {
        try {
            // DriverManager busca el driver adecuado (MySQL Connector/J)
            // y retorna una implementación de Connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;

        } catch (SQLException e) {
            // Re-lanzamos la excepción con más contexto
            System.err.println("❌ ERROR: No se pudo conectar a la base de datos FORESTECH");
            System.err.println("   URL: " + URL);
            System.err.println("   Usuario: " + USER);
            System.err.println("   Mensaje: " + e.getMessage());
            throw e; // Propaga la excepción al código que llamó
        }
    }

    /**
     * Prueba la conexión a la base de datos e imprime información del servidor.
     *
     * Útil para debugging y verificar que la configuración es correcta.
     * Imprime:
     * - Nombre del producto de la BD (MySQL)
     * - Versión de MySQL
     * - Nombre de la base de datos
     * - Usuario conectado
     *
     * @throws SQLException Si hay error de conexión
     */
    public static void testConnection() throws SQLException {
        System.out.println("🔌 Probando conexión a MySQL...\n");

        // Try-with-resources: conn se cierra automáticamente al salir del bloque
        try (Connection conn = getConnection()) {

            // DatabaseMetaData: interfaz que proporciona información sobre la BD
            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("✅ Conexión exitosa!");
            System.out.println("─────────────────────────────────────────");
            System.out.println("🗄️  Producto BD    : " + metaData.getDatabaseProductName());
            System.out.println("📦 Versión BD     : " + metaData.getDatabaseProductVersion());
            System.out.println("🔗 Driver JDBC    : " + metaData.getDriverName());
            System.out.println("📌 Versión Driver : " + metaData.getDriverVersion());
            System.out.println("🏛️  Database       : " + conn.getCatalog());
            System.out.println("👤 Usuario        : " + metaData.getUserName());
            System.out.println("─────────────────────────────────────────\n");

        } // Connection se cierra automáticamente aquí
    }
}
```

### Desglose del Código

#### Constantes de Configuración

```java
private static final String URL = "jdbc:mysql://localhost:3306/FORESTECH";
private static final String USER = "root";
private static final String PASSWORD = "tu_password_aqui";
```

- `private`: Solo esta clase puede acceder (encapsulación)
- `static`: Pertenece a la clase, no a instancias
- `final`: No se puede modificar (constante)

**🔒 Seguridad:**

```java
// ❌ MAL: Hardcodear password en código (lo haremos solo para aprendizaje)
private static final String PASSWORD = "mypassword123";

// ✅ BIEN: Usar variables de entorno (producción)
private static final String PASSWORD = System.getenv("DB_PASSWORD");

// ✅ BIEN: Usar archivo de configuración .properties (producción)
Properties props = new Properties();
props.load(new FileInputStream("config.properties"));
String password = props.getProperty("db.password");
```

**En Fase 3:** Usaremos hardcoded por simplicidad didáctica. En Fase 5+, migraremos a configuración externa.

#### Constructor Privado

```java
private DatabaseConnection() {
    throw new IllegalStateException("Utility class - No debe instanciarse");
}
```

**Propósito:** Prevenir que alguien haga `new DatabaseConnection()`.

```java
// Esto generará error:
DatabaseConnection db = new DatabaseConnection(); // ❌ Constructor privado
```

**¿Por qué?** Porque todos los métodos son estáticos, no necesitas instancia.

#### Método getConnection()

```java
public static Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
    return conn;
}
```

**DriverManager.getConnection()** hace:
1. Itera sobre los drivers JDBC registrados (MySQL Connector/J se auto-registra)
2. Pregunta a cada driver: "¿Puedes manejar esta URL?"
3. MySQL Connector/J responde "Sí" al ver `jdbc:mysql://`
4. DriverManager llama a `com.mysql.cj.jdbc.Driver.connect(URL, USER, PASSWORD)`
5. El driver establece conexión TCP/IP a `localhost:3306`
6. Autentica con `USER` y `PASSWORD`
7. Retorna un objeto `ConnectionImpl` (implementa `Connection`)

**Declaración `throws SQLException`:**

```java
public static Connection getConnection() throws SQLException
```

**Significado:** Este método **puede lanzar** una excepción de tipo `SQLException`.

**¿Por qué?** Porque la conexión puede fallar:
- MySQL no está corriendo
- Password incorrecto
- Database no existe
- Red no disponible

**El código que llame a `getConnection()` debe manejar la excepción:**

```java
// Opción 1: Propagar la excepción
public void someMethod() throws SQLException {
    Connection conn = DatabaseConnection.getConnection();
}

// Opción 2: Capturar la excepción
public void someMethod() {
    try {
        Connection conn = DatabaseConnection.getConnection();
    } catch (SQLException e) {
        System.out.println("Error de conexión: " + e.getMessage());
    }
}
```

#### Método testConnection()

```java
public static void testConnection() throws SQLException {
    try (Connection conn = getConnection()) {
        DatabaseMetaData metaData = conn.getMetaData();
        System.out.println("Versión MySQL: " + metaData.getDatabaseProductVersion());
    }
}
```

**DatabaseMetaData:** Interfaz que proporciona **metadatos** (información sobre la BD):

| Método | Retorna | Ejemplo |
|--------|---------|---------|
| `getDatabaseProductName()` | Nombre de la BD | `"MySQL"` |
| `getDatabaseProductVersion()` | Versión | `"8.0.35"` |
| `getDriverName()` | Nombre del driver | `"MySQL Connector/J"` |
| `getDriverVersion()` | Versión del driver | `"mysql-connector-j-8.0.33"` |
| `getUserName()` | Usuario conectado | `"root@localhost"` |
| `getURL()` | URL de conexión | `"jdbc:mysql://localhost:3306/FORESTECH"` |
| `getCatalog()` | Database actual | `"FORESTECH"` |

**Uso del método:**

```java
// En Main.java
public static void main(String[] args) {
    try {
        DatabaseConnection.testConnection();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

---

## 9. Try-With-Resources: Gestión Automática de Recursos

### El Problema: Memory Leaks con Conexiones

#### Escenario Sin Try-With-Resources

```java
public void badExample() throws SQLException {
    Connection conn = DatabaseConnection.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products");

    // Si hay excepción aquí, los recursos NO se cierran ❌
    while (rs.next()) {
        String name = rs.getString("name");
        System.out.println(name);
    }

    // PROBLEMA: ¿Y si olvidamos cerrar?
    // rs.close();
    // stmt.close();
    // conn.close();
}
```

**Problemas:**
1. **Memory leak:** Recursos no cerrados consumen memoria
2. **Connection pool exhaustion:** MySQL tiene límite de conexiones concurrentes (default: 151)
3. **File descriptor leak:** Cada conexión abierta consume un descriptor de archivo del SO

**Consecuencias:**

```
Aplicación ejecuta badExample() 150 veces
  → 150 conexiones abiertas en MySQL
  → MySQL rechaza conexión 151 con error:
     "Too many connections"
  → Aplicación colapsa ❌
```

#### Intento Manual de Cierre

```java
public void manualCloseExample() throws SQLException {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        conn = DatabaseConnection.getConnection();
        stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT * FROM oil_products");

        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }

    } catch (SQLException e) {
        e.printStackTrace();

    } finally {
        // Cierre manual en orden inverso
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
```

**Problemas:**
- ✅ Funciona, pero es **verboso** (30 líneas vs 5 líneas)
- ⚠️ Fácil de olvidar algún `close()`
- ⚠️ Código difícil de leer

### La Solución: Try-With-Resources (Java 7+)

**Sintaxis:**

```java
try (TipoRecurso recurso = inicialización) {
    // Usar recurso
} // recurso.close() se llama automáticamente
```

**Ejemplo con Connection:**

```java
public void goodExample() throws SQLException {
    try (Connection conn = DatabaseConnection.getConnection()) {
        // Usar conn...
    } // conn.close() automático aquí
}
```

### Anatomía del Try-With-Resources

```java
try (Connection conn = DatabaseConnection.getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products")) {

    while (rs.next()) {
        System.out.println(rs.getString("name"));
    }

} // Cierre automático en orden INVERSO: rs → stmt → conn
```

**Orden de cierre:**

```
1. Se cierra rs.close()     (último declarado)
2. Se cierra stmt.close()   (segundo)
3. Se cierra conn.close()   (primero declarado)
```

**Este orden es correcto:** ResultSet depende de Statement, Statement depende de Connection.

### Comparación Visual

```
┌────────────────────────────────────────────────────────────────┐
│          COMPARACIÓN: MANUAL vs TRY-WITH-RESOURCES             │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  MANUAL (35 líneas)            TRY-WITH-RESOURCES (5 líneas)   │
│  ───────────────────           ──────────────────────────────  │
│  Connection conn = null;       try (Connection conn =          │
│  try {                             DatabaseConnection           │
│      conn = DB.getConn();              .getConnection()) {     │
│      // usar conn                  // usar conn                │
│  } finally {                       }                            │
│      if (conn != null) {                                        │
│          try {                    ✅ Cierre automático          │
│              conn.close();        ✅ Más legible                │
│          } catch (SQLException e) ✅ Menos bugs                 │
│              e.printStackTrace();                               │
│          }                                                      │
│      }                                                          │
│  }                                                              │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

### Requisito: Implementar AutoCloseable

**¿Qué recursos se pueden usar en try-with-resources?**

Solo clases que implementan **`AutoCloseable`** o **`Closeable`**:

```java
public interface AutoCloseable {
    void close() throws Exception;
}
```

**Recursos JDBC que implementan AutoCloseable:**

| Clase | Cierre Necesario | Consecuencia si no se cierra |
|-------|------------------|------------------------------|
| `Connection` | ✅ Sí | Memory leak, exhaustión de conexiones |
| `Statement` | ✅ Sí | Memory leak menor |
| `PreparedStatement` | ✅ Sí | Memory leak menor |
| `ResultSet` | ⚠️ Opcional | Se cierra automáticamente al cerrar Statement (pero es buena práctica) |

### Excepciones Durante el Cierre

**¿Qué pasa si `close()` lanza excepción?**

```java
try (Connection conn = DatabaseConnection.getConnection()) {
    throw new SQLException("Error durante uso"); // Excepción 1
} // conn.close() también lanza SQLException     // Excepción 2
```

**Comportamiento:**

1. Java lanza la **primera excepción** (`"Error durante uso"`)
2. La **segunda excepción** se agrega como **"suppressed exception"**
3. Puedes acceder a ella con `e.getSuppressed()`

```java
try {
    try (Connection conn = DatabaseConnection.getConnection()) {
        throw new SQLException("Error principal");
    }
} catch (SQLException e) {
    System.out.println("Excepción principal: " + e.getMessage());

    // Excepciones suprimidas (si las hay)
    for (Throwable suppressed : e.getSuppressed()) {
        System.out.println("Excepción suprimida: " + suppressed.getMessage());
    }
}
```

### Múltiples Recursos en Try-With-Resources

```java
// Declarar múltiples recursos separados por punto y coma
try (Connection conn = DatabaseConnection.getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT * FROM oil_products")) {

    // Usar los 3 recursos
    while (rs.next()) {
        System.out.println(rs.getString("name"));
    }

} // Cierre automático de los 3 en orden inverso
```

**Java 9+:** También puedes usar variables ya inicializadas:

```java
Connection conn = DatabaseConnection.getConnection();

// Java 9+: Variable ya inicializada dentro del try
try (conn) {
    // Usar conn
} // conn.close() automático
```

### Regla Mnemotécnica

```
┌─────────────────────────────────────────────────────┐
│  "Si tiene close(), usa try-with-resources"         │
│                                                      │
│  Connection     ✅ Tiene close() → try-with-res     │
│  Statement      ✅ Tiene close() → try-with-res     │
│  ResultSet      ✅ Tiene close() → try-with-res     │
│  FileInputStream ✅ Tiene close() → try-with-res    │
│  Scanner        ✅ Tiene close() → try-with-res     │
│  String         ❌ No tiene close() → No aplica      │
│  ArrayList      ❌ No tiene close() → No aplica      │
└─────────────────────────────────────────────────────┘
```

---

## 10. Probando la Conexión

### Preparativos

**PASO 1:** Asegúrate de que MySQL está corriendo en WSL:

```bash
# Verificar estado
sudo service mysql status

# Si no está corriendo, iniciar:
sudo service mysql start
```

**Salida esperada:**

```
 * MySQL Community Server 8.0.35 is running
```

**PASO 2:** Verifica que la base de datos FORESTECH existe:

```bash
sudo mysql -u root -p
```

```sql
SHOW DATABASES;
-- Debe aparecer FORESTECH

USE FORESTECH;
SHOW TABLES;
-- Debe aparecer oil_products (creada en Fase 03.2)
```

**PASO 3:** Anota tu contraseña de MySQL. Edita `DatabaseConnection.java`:

```java
private static final String PASSWORD = "tu_password_real"; // CAMBIAR
```

### Modificando Main.java para Probar

Abre `/home/hp/forestechOil/forestech-cli-java/src/main/java/com/forestech/Main.java`:

```java
package com.forestech;

import com.forestech.config.DatabaseConnection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   FORESTECH CLI - TEST DE CONEXIÓN    ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Probar conexión a MySQL
        try {
            DatabaseConnection.testConnection();
            System.out.println("✅ Sistema listo para trabajar con MySQL\n");

        } catch (SQLException e) {
            System.err.println("\n❌ ERROR CRÍTICO: No se pudo conectar a MySQL");
            System.err.println("Verifica que:");
            System.err.println("  1. MySQL está corriendo: sudo service mysql start");
            System.err.println("  2. La base de datos FORESTECH existe");
            System.err.println("  3. El password en DatabaseConnection.java es correcto\n");

            // Imprimir stack trace completo para debugging
            e.printStackTrace();
        }
    }
}
```

### Compilando y Ejecutando

```bash
cd /home/hp/forestechOil/forestech-cli-java

# Compilar
mvn clean compile

# Ejecutar
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

### Salida Esperada (Conexión Exitosa)

```
╔════════════════════════════════════════╗
║   FORESTECH CLI - TEST DE CONEXIÓN    ║
╚════════════════════════════════════════╝

🔌 Probando conexión a MySQL...

✅ Conexión exitosa!
─────────────────────────────────────────
🗄️  Producto BD    : MySQL
📦 Versión BD     : 8.0.35-0ubuntu0.22.04.1
🔗 Driver JDBC    : MySQL Connector/J
📌 Versión Driver : mysql-connector-j-8.0.33 (Revision: 5a32e9f8bc1c460bb13977d08508a7fa5c1b2f6f)
🏛️  Database       : FORESTECH
👤 Usuario        : root@localhost
─────────────────────────────────────────

✅ Sistema listo para trabajar con MySQL
```

**¡Excelente!** La conexión funciona correctamente.

---

## 11. Tabla de Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| **`Communications link failure`** | MySQL no está corriendo | `sudo service mysql start` |
| **`Access denied for user 'root'@'localhost'`** | Contraseña incorrecta | Verificar password en `DatabaseConnection.java` |
| **`Unknown database 'FORESTECH'`** | Base de datos no existe | `CREATE DATABASE FORESTECH;` en MySQL CLI |
| **`No suitable driver found`** | Dependency no agregada o mal configurada | Verificar `pom.xml` y ejecutar `mvn clean compile` |
| **`java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`** | JAR del driver no está en classpath | Ejecutar `mvn clean compile` para descargar dependency |
| **`The server time zone value 'XXX' is unrecognized`** | MySQL no tiene zona horaria configurada | Agregar `?serverTimezone=UTC` al connection string |
| **`Public Key Retrieval is not allowed`** | Autenticación caching_sha2_password en MySQL 8 | Agregar `?allowPublicKeyRetrieval=true` al connection string |
| **`java.net.ConnectException: Connection refused`** | Puerto incorrecto o MySQL no escucha en 3306 | Verificar puerto con `sudo netstat -tlnp \| grep mysql` |
| **`Too many connections`** | Conexiones no cerradas (memory leak) | Usar try-with-resources para cerrar conexiones |
| **`NullPointerException` al usar Connection** | Conexión no se estableció (excepción ignorada) | Verificar que `getConnection()` no retorna null |

### Detalles de Errores Críticos

#### 1. Communications Link Failure

**Mensaje completo:**

```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
The driver has not received any packets from the server.
```

**Diagnóstico:**

```bash
# Verificar si MySQL está corriendo
sudo service mysql status

# Ver procesos MySQL
ps aux | grep mysql

# Verificar puerto 3306
sudo netstat -tlnp | grep 3306
```

**Solución:**

```bash
sudo service mysql start
```

#### 2. Access Denied

**Mensaje completo:**

```
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

**Causas posibles:**
- Password incorrecto en `DatabaseConnection.java`
- Usuario `root` no tiene permisos desde `localhost`
- MySQL usa autenticación no compatible

**Solución 1:** Verificar password:

```bash
# Probar login manual
sudo mysql -u root -p
# Ingresar password
```

**Solución 2:** Resetear password de MySQL:

```bash
sudo mysql
```

```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'nuevo_password';
FLUSH PRIVILEGES;
EXIT;
```

#### 3. Unknown Database

**Mensaje completo:**

```
java.sql.SQLSyntaxErrorException: Unknown database 'FORESTECH'
```

**Solución:**

```bash
sudo mysql -u root -p
```

```sql
CREATE DATABASE FORESTECH;
SHOW DATABASES; -- Verificar que aparece FORESTECH
EXIT;
```

#### 4. No Suitable Driver Found

**Mensaje completo:**

```
java.sql.SQLException: No suitable driver found for jdbc:mysql://localhost:3306/FORESTECH
```

**Causa:** Maven no descargó el JAR o no está en el classpath.

**Solución:**

```bash
# Limpiar y recompilar
mvn clean compile

# Verificar que el JAR se descargó
ls ~/.m2/repository/com/mysql/mysql-connector-j/8.0.33/

# Si no existe, descargar manualmente
mvn dependency:resolve
```

#### 5. Server Time Zone Error

**Mensaje completo:**

```
java.sql.SQLException: The server time zone value 'XXXST' is unrecognized or represents more than one time zone.
```

**Solución:** Agregar `serverTimezone` al connection string:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/FORESTECH?serverTimezone=UTC";
```

**Alternativa:** Configurar zona horaria en MySQL:

```sql
SET GLOBAL time_zone = '+00:00';
```

---

## 12. Ejercicios Prácticos

### Ejercicio 1: Conexión Básica (Básico)

**Objetivo:** Crear una conexión exitosa y verificar metadatos.

**Tareas:**
1. Completa el password en `DatabaseConnection.java`
2. Ejecuta `testConnection()` desde `Main.java`
3. Captura una captura de pantalla de la salida exitosa

**Código:**

```java
public static void main(String[] args) {
    try {
        DatabaseConnection.testConnection();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

<details>
<summary>✅ Verificación</summary>

Debes ver algo similar a:

```
✅ Conexión exitosa!
🗄️  Producto BD    : MySQL
📦 Versión BD     : 8.0.35-xxx
🏛️  Database       : FORESTECH
```

Si ves esto, ¡ejercicio completado! ✅
</details>

---

### Ejercicio 2: Manejo de Errores - Puerto Incorrecto (Intermedio)

**Objetivo:** Observar y entender el error cuando el puerto es incorrecto.

**Tareas:**
1. Modifica temporalmente `DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3307/FORESTECH"; // Puerto incorrecto
   ```
2. Ejecuta la aplicación
3. Analiza el stack trace
4. Identifica qué tipo de excepción se lanza
5. Restaura el puerto correcto (`3306`)

<details>
<summary>✅ Solución y Análisis</summary>

**Salida esperada:**

```
❌ ERROR: No se pudo conectar a la base de datos FORESTECH
   URL: jdbc:mysql://localhost:3307/FORESTECH
   Usuario: root
   Mensaje: Communications link failure

com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Análisis:**

- **Tipo de excepción:** `CommunicationsException` (subclase de `SQLException`)
- **Causa:** MySQL escucha en 3306, pero intentamos conectar a 3307
- **Equivalente:** Como llamar a un teléfono con número incorrecto

**Lección:** Los errores de red generan `CommunicationsException`.

**Restaurar configuración:**

```java
private static final String URL = "jdbc:mysql://localhost:3306/FORESTECH"; // ✅ Correcto
```
</details>

---

### Ejercicio 3: Manejo de Errores - Password Inválido (Intermedio)

**Objetivo:** Simular autenticación fallida.

**Tareas:**
1. Modifica temporalmente el password:
   ```java
   private static final String PASSWORD = "password_incorrecto_123";
   ```
2. Ejecuta la aplicación
3. Identifica el mensaje de error específico
4. Restaura el password correcto

<details>
<summary>✅ Solución y Análisis</summary>

**Salida esperada:**

```
❌ ERROR: No se pudo conectar a la base de datos FORESTECH
   URL: jdbc:mysql://localhost:3306/FORESTECH
   Usuario: root
   Mensaje: Access denied for user 'root'@'localhost' (using password: YES)

java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

**Análisis:**

- **Tipo de excepción:** `SQLException` (no `CommunicationsException`)
- **Causa:** Conexión de red exitosa, pero autenticación rechazada
- **Mensaje clave:** `"Access denied"` + `"using password: YES"` indica que se envió un password pero es incorrecto

**Si viera `"using password: NO"`:**
- Significaría que el password es `null` o vacío

**Lección:** Errores de autenticación generan `SQLException` con mensaje "Access denied".

**Restaurar configuración:**

```java
private static final String PASSWORD = "tu_password_correcto"; // ✅ Correcto
```
</details>

---

### Ejercicio 4: Método closeConnection() Personalizado (Avanzado)

**Objetivo:** Crear un método helper para cerrar conexiones de forma segura, imprimiendo logs.

**Tareas:**
1. Agrega este método a `DatabaseConnection.java`:

```java
/**
 * Cierra una conexión de forma segura, suprimiendo excepciones.
 * Imprime un mensaje de confirmación.
 *
 * @param conn Conexión a cerrar (puede ser null)
 */
public static void closeConnection(Connection conn) {
    if (conn != null) {
        try {
            conn.close();
            System.out.println("🔒 Conexión cerrada correctamente");
        } catch (SQLException e) {
            System.err.println("⚠️ Warning: Error al cerrar conexión: " + e.getMessage());
        }
    } else {
        System.out.println("ℹ️ Conexión ya estaba null, no se cerró nada");
    }
}
```

2. Prueba el método en `Main.java`:

```java
public static void main(String[] args) {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        System.out.println("✅ Conexión obtenida: " + conn);

    } catch (SQLException e) {
        System.err.println("❌ Error de conexión: " + e.getMessage());

    } finally {
        // Cerrar usando el método personalizado
        DatabaseConnection.closeConnection(conn);
    }
}
```

<details>
<summary>✅ Solución Completa y Análisis</summary>

**Código completo del método:**

```java
public static void closeConnection(Connection conn) {
    if (conn != null) {
        try {
            conn.close();
            System.out.println("🔒 Conexión cerrada correctamente");
        } catch (SQLException e) {
            System.err.println("⚠️ Warning: Error al cerrar conexión: " + e.getMessage());
            // NO re-lanzamos la excepción porque estamos en un método de limpieza
            // Simplemente logueamos el error
        }
    } else {
        System.out.println("ℹ️ Conexión ya estaba null, no se cerró nada");
    }
}
```

**Salida esperada:**

```
✅ Conexión obtenida: com.mysql.cj.jdbc.ConnectionImpl@1a2b3c4d
🔒 Conexión cerrada correctamente
```

**Análisis:**

1. **Check de null:** Previene `NullPointerException`
2. **Try-catch:** Captura excepciones durante el cierre (raras pero posibles)
3. **No re-lanza excepción:** En métodos de limpieza, usualmente solo logueamos el error
4. **Log visible:** Ayuda a debugging

**Comparación con try-with-resources:**

```java
// Con método personalizado (14 líneas)
Connection conn = null;
try {
    conn = DatabaseConnection.getConnection();
    // Usar conn
} catch (SQLException e) {
    e.printStackTrace();
} finally {
    DatabaseConnection.closeConnection(conn);
}

// Con try-with-resources (4 líneas) ✅ MÁS SIMPLE
try (Connection conn = DatabaseConnection.getConnection()) {
    // Usar conn
} // Cierre automático
```

**Conclusión:** El método `closeConnection()` es útil para situaciones especiales (logs, lógica custom), pero **try-with-resources es preferido** en el 99% de casos.
</details>

---

### Ejercicio 5: Obtener URL de Conexión desde Connection (Avanzado)

**Objetivo:** Explorar métodos de `DatabaseMetaData` para extraer información de la conexión.

**Tareas:**
1. Crea un método `printConnectionDetails()` en `DatabaseConnection.java`:

```java
/**
 * Imprime detalles completos de la conexión en formato tabla.
 *
 * @param conn Conexión activa
 * @throws SQLException Si hay error al obtener metadatos
 */
public static void printConnectionDetails(Connection conn) throws SQLException {
    DatabaseMetaData meta = conn.getMetaData();

    System.out.println("\n╔════════════════════════════════════════════════════════════╗");
    System.out.println("║          DETALLES DE CONEXIÓN MYSQL                       ║");
    System.out.println("╠════════════════════════════════════════════════════════════╣");

    System.out.printf("║ %-25s : %-30s ║%n", "URL", meta.getURL());
    System.out.printf("║ %-25s : %-30s ║%n", "Usuario", meta.getUserName());
    System.out.printf("║ %-25s : %-30s ║%n", "Producto", meta.getDatabaseProductName());
    System.out.printf("║ %-25s : %-30s ║%n", "Versión Producto", meta.getDatabaseProductVersion());
    System.out.printf("║ %-25s : %-30s ║%n", "Driver", meta.getDriverName());
    System.out.printf("║ %-25s : %-30s ║%n", "Versión Driver", meta.getDriverVersion());
    System.out.printf("║ %-25s : %-30s ║%n", "Catálogo (Database)", conn.getCatalog());
    System.out.printf("║ %-25s : %-30s ║%n", "Auto-commit", conn.getAutoCommit());
    System.out.printf("║ %-25s : %-30s ║%n", "Read-only", conn.isReadOnly());
    System.out.printf("║ %-25s : %-30s ║%n", "Cerrada", conn.isClosed());

    System.out.println("╚════════════════════════════════════════════════════════════╝\n");
}
```

2. Prueba en `Main.java`:

```java
public static void main(String[] args) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        DatabaseConnection.printConnectionDetails(conn);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

<details>
<summary>✅ Solución Completa y Salida Esperada</summary>

**Salida esperada:**

```
╔════════════════════════════════════════════════════════════╗
║          DETALLES DE CONEXIÓN MYSQL                       ║
╠════════════════════════════════════════════════════════════╣
║ URL                       : jdbc:mysql://localhost:3306/FORESTECH ║
║ Usuario                   : root@localhost                        ║
║ Producto                  : MySQL                                 ║
║ Versión Producto          : 8.0.35-0ubuntu0.22.04.1              ║
║ Driver                    : MySQL Connector/J                     ║
║ Versión Driver            : mysql-connector-j-8.0.33             ║
║ Catálogo (Database)       : FORESTECH                             ║
║ Auto-commit               : true                                  ║
║ Read-only                 : false                                 ║
║ Cerrada                   : false                                 ║
╚════════════════════════════════════════════════════════════╝
```

**Análisis de Propiedades:**

| Propiedad | Valor | Significado |
|-----------|-------|-------------|
| `Auto-commit` | `true` | Cada query se confirma automáticamente (Fase 5: cambiaremos esto) |
| `Read-only` | `false` | Podemos hacer INSERT/UPDATE/DELETE |
| `Cerrada` | `false` | Conexión activa (dentro del try-with-resources) |

**Métodos Útiles de Connection:**

```java
// Información
conn.getCatalog()          // Database actual: "FORESTECH"
conn.getSchema()           // Schema (MySQL no usa mucho)
conn.isValid(2)            // Test ping con timeout 2 segundos
conn.isClosed()            // true si está cerrada

// Configuración
conn.setAutoCommit(false)  // Desactivar auto-commit (transacciones manuales)
conn.setReadOnly(true)     // Modo solo lectura (optimización)
conn.setCatalog("OTRO_DB") // Cambiar a otra base de datos
```

**Experimento:** Prueba `isValid()`:

```java
Connection conn = DatabaseConnection.getConnection();
System.out.println("¿Conexión válida? " + conn.isValid(2)); // true

conn.close();
System.out.println("¿Conexión válida? " + conn.isValid(2)); // false (cerrada)
```
</details>

---

## 13. Git Checkpoint

### Momento de Confirmar Cambios

Has completado la **Fase 03.3: Conexión JDBC**. Es momento de guardar tu progreso en Git.

### Archivos Modificados/Creados

```bash
# Ver estado actual
cd /home/hp/forestechOil/forestech-cli-java
git status
```

**Archivos esperados:**

```
modified:   pom.xml
modified:   src/main/java/com/forestech/Main.java
new file:   src/main/java/com/forestech/config/DatabaseConnection.java
```

### Comandos Git

```bash
# 1. Agregar archivos al staging area
git add pom.xml
git add src/main/java/com/forestech/Main.java
git add src/main/java/com/forestech/config/DatabaseConnection.java

# 2. Crear commit descriptivo
git commit -m "Fase 03.3: Implementar conexión JDBC a MySQL

- Agregar dependency mysql-connector-j 8.0.33 en pom.xml
- Crear package com.forestech.config
- Implementar DatabaseConnection.java con:
  * Constantes de configuración (URL, USER, PASSWORD)
  * Método getConnection() usando DriverManager
  * Método testConnection() con DatabaseMetaData
- Modificar Main.java para probar conexión
- Documentar try-with-resources en comentarios

Checkpoint: Fase 03.3 completada ✅"

# 3. Ver historial de commits
git log --oneline -5
```

### Documentar tu Trabajo

Crea un archivo de notas temporales (no commitear):

```bash
nano /home/hp/forestechOil/forestech-cli-java/NOTAS_FASE_03_3.md
```

**Contenido sugerido:**

```markdown
# Notas de Aprendizaje - Fase 03.3

## Fecha: [TU FECHA]

## Conceptos Aprendidos

1. **JDBC Architecture:**
   - Java App → JDBC API → Driver → MySQL Server
   - DriverManager coordina drivers
   - MySQL Connector/J implementa interfaces JDBC

2. **Connection String:**
   jdbc:mysql://localhost:3306/FORESTECH
   - Protocolo: jdbc:mysql
   - Host/Puerto: localhost:3306
   - Database: FORESTECH

3. **Try-With-Resources:**
   - Cierre automático de recursos AutoCloseable
   - Sintaxis: try (Resource r = init) { ... }
   - Orden de cierre: inverso a declaración

4. **DatabaseMetaData:**
   - Información sobre BD (versión, producto, usuario)
   - Útil para debugging y logging

## Dificultades Encontradas

- [Ej: Olvidé cambiar el password en DatabaseConnection.java → Access Denied]
- [Ej: MySQL no estaba corriendo → Communications link failure]

## Tiempo Invertido

- Lectura de teoría: [X horas]
- Implementación: [X horas]
- Ejercicios: [X horas]
- Total: [X horas]

## Próximos Pasos

- Fase 03.4: Ejecutar SELECT y mapear ResultSet a objetos Product
```

### Push a Repositorio Remoto (Opcional)

Si tienes un repositorio en GitHub/GitLab:

```bash
git push origin main
```

---

## 14. Generador de Quiz de Validación

### Instrucciones de Uso

Copia el siguiente prompt en **Claude AI** o **ChatGPT** para generar un quiz personalizado que valide tu comprensión de esta fase:

```
Eres un profesor de Java especializado en bases de datos. Genera un quiz de validación para un estudiante que acaba de completar la FASE 03.3 del proyecto Forestech (Conexión JDBC a MySQL).

CONTEXTO DEL ESTUDIANTE:
- Aprendiendo Java desde cero (principiante)
- Acaba de implementar DatabaseConnection.java
- Agregó dependency MySQL Connector/J en Maven
- Probó conexión exitosa a MySQL
- Aprendió try-with-resources

TEMAS A EVALUAR:
1. Arquitectura JDBC (4 componentes)
2. Anatomy of a connection string
3. Propósito de DriverManager
4. Diferencia entre java.sql.Connection (interface) vs implementación del driver
5. Try-with-resources vs cierre manual
6. Debugging de errores comunes (Communications link failure, Access denied, Unknown database)
7. Orden de cierre de recursos
8. Qué es AutoCloseable

FORMATO DEL QUIZ:
- 10 preguntas en total
- Mezcla de tipos:
  * 4 preguntas de opción múltiple (4 opciones, 1 correcta)
  * 3 preguntas de completar código (código con blancos para llenar)
  * 2 preguntas de debugging (identificar error en código y corregir)
  * 1 pregunta de implementación corta (escribir método completo)

- Dificultad progresiva: fácil → intermedio → avanzado
- IMPORTANTE: Incluir explicación detallada de por qué cada respuesta es correcta/incorrecta

EJEMPLO DE FORMATO:

**Pregunta 1 (Fácil - Opción Múltiple)**

¿Cuál es el propósito de DriverManager en JDBC?

A) Gestionar la memoria de la JVM
B) Coordinar drivers JDBC y crear conexiones
C) Ejecutar queries SQL
D) Cerrar conexiones automáticamente

<details>
<summary>✅ Respuesta Correcta</summary>

**B) Coordinar drivers JDBC y crear conexiones**

**Explicación:**
DriverManager es la clase de java.sql.* que mantiene una lista de drivers JDBC registrados. Cuando llamas a DriverManager.getConnection(url, user, password), itera sobre los drivers preguntando "¿Puedes manejar esta URL?". El driver que responde afirmativamente (en nuestro caso, MySQL Connector/J) es usado para crear la conexión.

**Por qué las otras son incorrectas:**
- A) Incorrecto: Gestión de memoria es trabajo del Garbage Collector, no de DriverManager.
- C) Incorrecto: Ejecutar queries es trabajo de Statement/PreparedStatement, no DriverManager.
- D) Incorrecto: Try-with-resources cierra conexiones automáticamente, no DriverManager.
</details>

---

GENERA EL QUIZ COMPLETO SIGUIENDO ESTE FORMATO.
```

### Criterio de Aprobación

**Para considerar la Fase 03.3 aprobada:**
- ✅ Responder correctamente **al menos 7 de 10 preguntas** del quiz generado
- ✅ Comprender la explicación de las respuestas incorrectas
- ✅ Completar los 5 ejercicios prácticos de la sección anterior

**Si no apruebas:**
- Revisa las secciones donde fallaste
- Re-lee las explicaciones
- Intenta los ejercicios nuevamente
- Genera otro quiz para validar mejora

---

## 15. Checkpoint de Fase

### Checklist de Validación Conceptual

Marca cada ítem que puedas explicar sin mirar la documentación:

**Arquitectura JDBC:**
- [ ] Puedo dibujar el diagrama de 4 capas de JDBC (Java App → JDBC API → Driver → MySQL)
- [ ] Entiendo qué es un driver JDBC y por qué necesitamos uno
- [ ] Sé por qué MySQL Connector/J es "Tipo 4" (Pure Java)
- [ ] Entiendo la diferencia entre interface (Connection) e implementación (ConnectionImpl)

**Maven y Dependencias:**
- [ ] Sé qué es una dependency en Maven
- [ ] Entiendo la estructura groupId:artifactId:version
- [ ] Sé dónde Maven descarga los JARs (~/.m2/repository)
- [ ] Puedo agregar una nueva dependency en pom.xml

**Connection String:**
- [ ] Puedo descomponer `jdbc:mysql://localhost:3306/FORESTECH` y explicar cada parte
- [ ] Entiendo qué es un host y un puerto
- [ ] Sé para qué sirven los parámetros opcionales (?useSSL=false)

**Gestión de Recursos:**
- [ ] Entiendo qué es un memory leak con conexiones
- [ ] Sé qué es la interfaz AutoCloseable
- [ ] Puedo escribir try-with-resources de memoria
- [ ] Entiendo el orden de cierre (inverso a declaración)
- [ ] Sé cuándo usar try-with-resources vs cierre manual

**Debugging:**
- [ ] Puedo diagnosticar "Communications link failure"
- [ ] Puedo diagnosticar "Access denied"
- [ ] Puedo diagnosticar "Unknown database"
- [ ] Sé cómo usar DatabaseMetaData para debugging

### Checklist de Validación Práctica

Marca cada ítem que hayas completado exitosamente:

**Implementación:**
- [ ] Agregué mysql-connector-j en pom.xml
- [ ] Creé el package com.forestech.config
- [ ] Implementé DatabaseConnection.java completo
- [ ] Probé getConnection() exitosamente
- [ ] Probé testConnection() y vi metadatos de MySQL
- [ ] Modifiqué Main.java para probar la conexión

**Ejercicios:**
- [ ] Ejercicio 1: Conexión básica exitosa
- [ ] Ejercicio 2: Probé puerto incorrecto y analicé error
- [ ] Ejercicio 3: Probé password incorrecto y analicé error
- [ ] Ejercicio 4: Implementé closeConnection() personalizado
- [ ] Ejercicio 5: Implementé printConnectionDetails()

**Git:**
- [ ] Commit de todos los cambios de Fase 03.3
- [ ] Mensaje de commit descriptivo
- [ ] (Opcional) Push a repositorio remoto

**Quiz:**
- [ ] Generé el quiz con Claude/ChatGPT
- [ ] Respondí las 10 preguntas
- [ ] Obtuve al menos 7/10 correctas

### Criterio de Aprobación de Fase

**Fase 03.3 está COMPLETA si:**
1. ✅ Al menos **12 de 14 ítems** del checklist conceptual marcados
2. ✅ Al menos **10 de 13 ítems** del checklist práctico marcados
3. ✅ Quiz aprobado con 7/10 o más

**Si no cumples los criterios:**
- Identifica tus áreas débiles (conceptual vs práctica)
- Revisa esas secciones específicas
- Intenta nuevamente los ejercicios problemáticos
- Genera un nuevo quiz enfocado en tus debilidades

### Reflexión Guiada (Obligatoria)

Responde estas preguntas en un archivo de texto o en tu cuaderno:

1. **¿Qué concepto de JDBC te resultó más difícil de entender? ¿Por qué?**

   _[Tu respuesta aquí. Ej: "Try-with-resources me costó porque no entendía por qué se cerraban en orden inverso. Después de dibujar un diagrama de dependencias (ResultSet depende de Statement), tuvo sentido."]_

2. **Antes de esta fase, ¿cómo pensabas que Java se conectaba a MySQL?**

   _[Tu respuesta aquí. Ej: "Pensaba que era magia negra 😅. Ahora entiendo que hay un driver JAR que traduce llamadas Java a protocolo MySQL."]_

3. **¿Cuál fue el error más frustrante que enfrentaste? ¿Cómo lo resolviste?**

   _[Tu respuesta aquí. Ej: "Communications link failure. Resulta que MySQL no estaba corriendo en WSL. sudo service mysql start lo solucionó."]_

4. **En tus propias palabras, explica qué hace `DriverManager.getConnection()`**

   _[Tu respuesta aquí. Intenta explicarlo como si le hablaras a alguien que no sabe programación.]_

5. **¿Cómo aplicarás try-with-resources en Fase 03.4 cuando uses Statement y ResultSet?**

   _[Tu respuesta aquí. Ej: "Declararé Connection, Statement y ResultSet dentro del try(...) separados por punto y coma, para que se cierren automáticamente."]_

### Próxima Fase: FASE_03.4_CONSULTAS_RESULTSET.md

**¿Qué aprenderás?**
- Ejecutar SELECT desde Java con Statement
- Navegar ResultSet con cursor (.next())
- Mapear filas de MySQL a objetos Product
- Crear ProductService.java con métodos:
  - `getAllProducts()`
  - `getProductById(String)`
  - `getProductsByType(String)`

**Prerequisitos antes de avanzar:**
- ✅ Fase 03.3 completada al 100%
- ✅ Conexión a MySQL funcionando sin errores
- ✅ Tabla `oil_products` poblada con datos (Fase 03.2)
- ✅ Comprensión sólida de try-with-resources

---

## 📚 Recursos Adicionales

### Documentación Oficial

- **JDBC Basics (Oracle):** [https://docs.oracle.com/javase/tutorial/jdbc/basics/](https://docs.oracle.com/javase/tutorial/jdbc/basics/)
- **MySQL Connector/J Developer Guide:** [https://dev.mysql.com/doc/connector-j/en/](https://dev.mysql.com/doc/connector-j/en/)
- **Try-With-Resources (Oracle):** [https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)

### Lecturas Recomendadas (Opcional)

- **Connection Pooling:** ¿Por qué no crear 100 conexiones individuales? (Fase 6+)
- **Transacciones ACID:** Propiedades de transacciones (Fase 5)
- **SQL Injection:** Cómo evitar ataques (Fase 03.5)

---

## ❓ Preguntas Frecuentes (FAQ)

**P: ¿Por qué usamos VARCHAR para IDs en vez de INT AUTO_INCREMENT?**

R: Decisión pedagógica para mantener consistencia con `IdGenerator.java` (Fase 2). IDs legibles como `"PROD-12345678"` son más fáciles de debuggear para un estudiante. En producción, INT AUTO_INCREMENT es más eficiente.

---

**P: ¿Debo cerrar manualmente el ResultSet si ya uso try-with-resources con Connection?**

R: Técnicamente, cuando cierras un Statement, su ResultSet asociado se cierra automáticamente. Sin embargo, es **buena práctica** declarar ResultSet en el try-with-resources para ser explícito:

```java
// ✅ Buena práctica (explícito)
try (Connection conn = getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("...")) {
    // ...
}

// ⚠️ Funciona pero menos explícito
try (Connection conn = getConnection();
     Statement stmt = conn.createStatement()) {
    ResultSet rs = stmt.executeQuery("..."); // Se cierra al cerrar stmt
}
```

---

**P: ¿Por qué no usar un ORM (Hibernate, JPA) en vez de JDBC puro?**

R: ORMs son poderosos pero abstraen demasiado para aprendizaje. En Fase 3, necesitas entender:
- Cómo se ejecutan queries SQL
- Cómo se mapean filas a objetos manualmente
- Qué es un ResultSet

Una vez domines JDBC (Fases 3-5), migrar a un ORM será trivial. **Aprende los fundamentos primero.**

---

**P: Mi WSL no puede resolver "localhost". ¿Qué hago?**

R: Usa `127.0.0.1` en vez de `localhost`:

```java
private static final String URL = "jdbc:mysql://127.0.0.1:3306/FORESTECH";
```

O averigua el hostname de WSL:

```bash
hostname
# Ej: DESKTOP-ABC123

# Usar en connection string:
# jdbc:mysql://DESKTOP-ABC123.local:3306/FORESTECH
```

---

**P: ¿Puedo tener múltiples conexiones abiertas simultáneamente?**

R: Sí, pero cada una consume recursos (memoria, file descriptors, slot en MySQL). MySQL tiene un límite (default: 151 conexiones concurrentes). Para aplicaciones con alta concurrencia, usas **connection pooling** (Fase 6+).

---

**P: ¿Qué pasa si llamo a `getConnection()` 1000 veces sin cerrar?**

R: **Memory leak** masivo. Eventualmente:
1. MySQL rechazará conexiones nuevas ("Too many connections")
2. La JVM consumirá demasiada memoria
3. El SO se quedará sin file descriptors

**Solución:** Siempre usar try-with-resources.

---

**P: ¿Por qué `testConnection()` funciona pero `getConnection()` en Main.java falla?**

R: Probable race condition si MySQL está iniciando. Agrega un retry:

```java
public static Connection getConnection() throws SQLException {
    int intentos = 3;
    for (int i = 1; i <= intentos; i++) {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            if (i == intentos) throw e; // Último intento, propagar excepción

            System.out.println("Intento " + i + " falló. Reintentando en 1 seg...");
            Thread.sleep(1000);
        }
    }
    throw new SQLException("No se pudo conectar después de " + intentos + " intentos");
}
```

---

## 🎯 Resumen de la Fase

Has completado **Fase 03.3: Conexión JDBC a MySQL**. Ahora puedes:

✅ Explicar la arquitectura JDBC de 4 capas
✅ Agregar dependencias Maven en `pom.xml`
✅ Crear una clase de conexión reutilizable (`DatabaseConnection.java`)
✅ Obtener conexiones usando `DriverManager`
✅ Gestionar recursos con try-with-resources
✅ Diagnosticar y resolver errores comunes de conexión
✅ Extraer metadatos con `DatabaseMetaData`

**Próximo paso:** Ejecutar queries SELECT y mapear resultados a objetos Java en **FASE_03.4_CONSULTAS_RESULTSET.md**.

---

**¡Felicitaciones por completar la Fase 03.3! 🎉**

Tómate un descanso antes de continuar con Fase 03.4. La conexión a MySQL es un hito importante en tu viaje de aprendizaje Java.

---

**Forestech CLI - Fase 03.3**
Versión: 1.0
Última actualización: Enero 2025
