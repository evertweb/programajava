# FASE 03.1 - FUNDAMENTOS DE BASES DE DATOS Y SQL

> **Objetivo de Aprendizaje:** Comprender qué son las bases de datos relacionales, su estructura fundamental, y realizar operaciones básicas en MySQL para crear y explorar la base de datos del proyecto Forestech.

---

## 📚 Tabla de Contenidos

1. [¿Qué es una Base de Datos Relacional?](#1-qué-es-una-base-de-datos-relacional)
2. [Analogía Forestech: La Biblioteca de Combustibles](#2-analogía-forestech-la-biblioteca-de-combustibles)
3. [Componentes Fundamentales](#3-componentes-fundamentales)
4. [Diagrama Entidad-Relación del Proyecto](#4-diagrama-entidad-relación-del-proyecto)
5. [Instalación de MySQL en WSL](#5-instalación-de-mysql-en-wsl)
6. [Comandos Básicos de MySQL](#6-comandos-básicos-de-mysql)
7. [Creando la Base de Datos FORESTECH](#7-creando-la-base-de-datos-forestech)
8. [Técnica Metacognitiva: Mapa Mental](#8-técnica-metacognitiva-mapa-mental)
9. [Ejercicios Prácticos](#9-ejercicios-prácticos)
10. [Generador de Quiz de Validación](#10-generador-de-quiz-de-validación)
11. [Checkpoint de Fase](#11-checkpoint-de-fase)

---

## 1. ¿Qué es una Base de Datos Relacional?

### Definición Simple

Una **base de datos relacional** es un sistema organizado para almacenar, gestionar y recuperar información de manera estructurada, donde los datos se organizan en **tablas** relacionadas entre sí mediante **claves**.

### ¿Por qué las necesitamos en Forestech?

Actualmente, en las Fases 1-2 del proyecto, hemos trabajado con:
- Variables temporales (`ArrayList<Movement>`)
- Objetos en memoria (`Vehicle`, `Supplier`, `Product`)
- Datos que **se pierden** al cerrar la aplicación

**Problema:** Cada vez que ejecutamos `Main.java`, los datos desaparecen. No hay persistencia.

**Solución:** Una base de datos almacena los datos en disco de forma permanente, permitiendo:
- ✅ Persistencia de movimientos de combustible
- ✅ Consultas rápidas (filtrar, ordenar, buscar)
- ✅ Integridad de datos (validaciones automáticas)
- ✅ Acceso concurrente (múltiples usuarios si fuera necesario)

---

## 2. Analogía Forestech: La Biblioteca de Combustibles

Imagina que Forestech tiene una **biblioteca física** donde guardamos toda la información del negocio:

```
┌─────────────────────────────────────────────────────────────┐
│              🏛️  BIBLIOTECA FORESTECH                       │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  ESTANTE 1   │  │  ESTANTE 2   │  │  ESTANTE 3   │      │
│  │  Productos   │  │  Movimientos │  │  Vehículos   │      │
│  ├──────────────┤  ├──────────────┤  ├──────────────┤      │
│  │ 📕 Diesel    │  │ 📗 MOV-001   │  │ 📘 ABC-123   │      │
│  │ 📕 Gasolina  │  │ 📗 MOV-002   │  │ 📘 XYZ-789   │      │
│  │ 📕 Kerosene  │  │ 📗 MOV-003   │  │ 📘 LMN-456   │      │
│  │ ...          │  │ ...          │  │ ...          │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                              │
│  📋 Catálogo Central: Relaciona estantes entre sí           │
└─────────────────────────────────────────────────────────────┘
```

### Traducción a Bases de Datos

| Concepto Biblioteca | Concepto Base de Datos | En Forestech |
|---------------------|------------------------|--------------|
| 🏛️ **Biblioteca completa** | **Base de Datos** | `FORESTECH` |
| 📚 **Estante específico** | **Tabla** | `oil_products`, `combustibles_movements` |
| 📕 **Libro individual** | **Fila (Row/Registro)** | Un producto: "Diesel Premium, $5000/L" |
| 📄 **Páginas del libro** | **Columnas (Campos)** | `id`, `name`, `priceXUnd`, `unidadDeMedida` |
| 🔗 **Referencia entre libros** | **Clave Foránea (FK)** | `productId` en `movements` → `id` en `products` |
| 🏷️ **Código único del libro** | **Clave Primaria (PK)** | `id = "PROD-12345678"` |

**Ejemplo Concreto:**

```sql
-- ESTANTE "oil_products" (Tabla)
┌─────────────────┬─────────────────┬─────────────┬──────────────┐
│ id (PK)         │ name            │ priceXUnd   │ unidadDeMed. │  ← COLUMNAS
├─────────────────┼─────────────────┼─────────────┼──────────────┤
│ PROD-00001      │ Diesel Premium  │ 5000.00     │ LITRO        │  ← FILA 1
│ PROD-00002      │ Gasolina Corrte │ 4500.00     │ LITRO        │  ← FILA 2
│ PROD-00003      │ Kerosene        │ 3800.00     │ LITRO        │  ← FILA 3
└─────────────────┴─────────────────┴─────────────┴──────────────┘
```

Cada **fila** es como un "libro" con información completa de un producto.

---

## 3. Componentes Fundamentales

### 3.1 Base de Datos (Database)

Es el **contenedor principal** que agrupa todas las tablas relacionadas con un sistema.

```
FORESTECH (Base de Datos)
├── oil_products
├── combustibles_movements
├── combustibles_vehicles
├── combustibles_suppliers
└── combustibles_inventory
```

**Regla:** Una aplicación Java puede conectarse a múltiples bases de datos, pero generalmente usa una principal.

### 3.2 Tabla (Table)

Estructura que almacena datos en **filas y columnas**, similar a una hoja de cálculo Excel pero con reglas estrictas.

**Características:**
- Nombre único dentro de la BD
- Define columnas con tipos de datos específicos
- Cada fila representa una entidad única

### 3.3 Fila / Registro (Row / Record)

Una **instancia concreta** de la entidad que representa la tabla.

**Ejemplo en Java vs MySQL:**

```java
// Java (Fase 2) - Objeto en memoria
Movement mov1 = new Movement("MOV-0001", "ENTRADA", "Diesel", 1000, 5000);
```

```sql
-- MySQL - Fila en tabla
INSERT INTO combustibles_movements VALUES
('MOV-0001', 'ENTRADA', 'Diesel', 1000, 5000, '2025-01-15');
```

### 3.4 Columna / Campo (Column / Field)

Un **atributo específico** de la entidad. Define:
- **Nombre** (`id`, `name`, `quantity`)
- **Tipo de dato** (`VARCHAR`, `INT`, `DOUBLE`, `DATE`)
- **Restricciones** (`NOT NULL`, `UNIQUE`)

**Comparación con Java:**

```java
// Atributos de clase = Columnas de tabla
public class Product {
    private String id;           // VARCHAR(20)
    private String name;         // VARCHAR(100)
    private double priceXUnd;    // DOUBLE
    private String unidadDeMed;  // VARCHAR(20)
}
```

### 3.5 Clave Primaria (Primary Key - PK)

Columna(s) que **identifican de manera única** cada fila de la tabla.

**Propiedades:**
- ✅ Siempre tiene valor (`NOT NULL`)
- ✅ Nunca se repite (`UNIQUE`)
- ✅ No cambia con el tiempo (inmutable)

```sql
CREATE TABLE oil_products (
    id VARCHAR(20) PRIMARY KEY,  -- ← PK: identifica cada producto
    name VARCHAR(100)
);
```

**Analogía:** Como el código de barras de un producto en un supermercado. Dos productos pueden tener el mismo nombre, pero nunca el mismo código.

### 3.6 Clave Foránea (Foreign Key - FK)

Columna que **establece una relación** con la PK de otra tabla.

```sql
-- Tabla movements referencia a tabla products
combustibles_movements
├── id (PK)
├── movementType
├── quantity
└── productId (FK) ───┐
                      │
                      ↓
oil_products          │
├── id (PK) ←─────────┘
├── name
└── priceXUnd
```

**Regla de Integridad:** No puedes crear un movimiento con `productId = "PROD-99999"` si ese producto no existe en `oil_products`.

---

## 4. Diagrama Entidad-Relación del Proyecto

Representación simplificada de las tablas principales de Forestech y sus relaciones:

```
┌─────────────────────────┐
│   oil_products          │
│─────────────────────────│
│ 🔑 id (PK)              │
│   name                  │
│   unidadDeMedida        │
│   priceXUnd             │
└───────┬─────────────────┘
        │
        │ 1:N (Un producto puede estar en muchos movimientos)
        │
        ↓
┌───────────────────────────────┐
│ combustibles_movements        │
│───────────────────────────────│
│ 🔑 id (PK)                    │
│   movementType                │
│   fuelType                    │
│   quantity                    │
│   unitPrice                   │
│   movementDate                │
│ 🔗 productId (FK) → products  │
└───────────────────────────────┘

┌─────────────────────────┐       ┌─────────────────────────┐
│ combustibles_vehicles   │       │ combustibles_suppliers  │
│─────────────────────────│       │─────────────────────────│
│ 🔑 id (PK)              │       │ 🔑 id (PK)              │
│   licensePlate          │       │   name                  │
│   brand                 │       │   nit                   │
│   model                 │       │   phoneNumber           │
│   fuelType              │       │   email                 │
│   tankCapacity          │       │   address               │
└─────────────────────────┘       └─────────────────────────┘
```

**Leyenda:**
- 🔑 = Clave Primaria (PK)
- 🔗 = Clave Foránea (FK)
- 1:N = Relación uno a muchos

**Explicación de Relaciones:**

1. **products → movements (1:N)**
   - Un producto (ej: Diesel) puede aparecer en muchos movimientos
   - Cada movimiento solo pertenece a un producto

---

## 5. Instalación de MySQL en WSL

### 5.1 Verificar si MySQL está instalado

Abre tu terminal WSL y ejecuta:

```bash
mysql --version
```

**Posibles resultados:**

- ✅ **Muestra versión:** `mysql  Ver 8.0.39 for Linux on x86_64`
  → MySQL ya está instalado. Salta al paso 5.3.

- ❌ **Error:** `mysql: command not found`
  → Necesitas instalarlo. Continúa al paso 5.2.

### 5.2 Instalación paso a paso (Ubuntu/Debian en WSL)

#### Paso 1: Actualizar repositorios

```bash
sudo apt update
```

**¿Qué hace?** Descarga la lista actualizada de paquetes disponibles.

#### Paso 2: Instalar servidor MySQL

```bash
sudo apt install mysql-server -y
```

**Espera:** Puede tomar 2-5 minutos dependiendo de tu conexión.

#### Paso 3: Verificar instalación

```bash
mysql --version
```

Deberías ver algo como:
`mysql  Ver 8.0.39 for Linux on x86_64 (MySQL Community Server - GPL)`

### 5.3 Iniciar el servicio MySQL

MySQL no se inicia automáticamente en WSL. Debes activarlo cada vez que abras WSL:

```bash
sudo service mysql start
```

**Salida esperada:**
`* Starting MySQL database server mysqld`

**Verificar estado:**

```bash
sudo service mysql status
```

**Salida exitosa:**
`* MySQL is running`

### 5.4 Configuración inicial (solo primera vez)

#### Ejecutar script de seguridad (Opcional pero recomendado)

```bash
sudo mysql_secure_installation
```

**Preguntas que te hará:**

1. **Validate Password Component?** → Presiona `N` (no es necesario para desarrollo local)
2. **Set root password?** → Presiona `Y` y elige una contraseña simple (ej: `root123`)
3. **Remove anonymous users?** → `Y`
4. **Disallow root login remotely?** → `Y`
5. **Remove test database?** → `Y`
6. **Reload privilege tables?** → `Y`

**Importante:** Guarda la contraseña que elegiste. La necesitarás para conectarte.

### 5.5 Acceso inicial como root

```bash
sudo mysql -u root -p
```

**Desglose del comando:**
- `sudo` = Ejecutar con privilegios de administrador
- `mysql` = Cliente MySQL
- `-u root` = Usuario (user) root
- `-p` = Solicitar contraseña (password)

**Salida esperada:**

```
Enter password: ******* (escribe tu contraseña)
Welcome to the MySQL monitor.  Commands end with ; or \g.
...
mysql>
```

**¡Éxito!** Ahora estás dentro del cliente MySQL.

Para salir:

```sql
exit;
```

---

## 6. Comandos Básicos de MySQL

Una vez dentro del cliente MySQL (`mysql>`), puedes ejecutar comandos SQL.

### 6.1 Listar todas las bases de datos

```sql
SHOW DATABASES;
```

**Salida típica:**

```
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
4 rows in set (0.00 sec)
```

**Explicación:**
- Estas son BDs del sistema MySQL (no las toques)
- Aún no existe nuestra BD `FORESTECH`

### 6.2 Crear una base de datos

```sql
CREATE DATABASE nombre_bd;
```

**Reglas:**
- Nombres sin espacios (usa `_` o `camelCase`)
- Evita caracteres especiales
- Por convención: MAYÚSCULAS o snake_case

**Ejemplo:**

```sql
CREATE DATABASE FORESTECH;
```

**Salida:**
`Query OK, 1 row affected (0.01 sec)`

### 6.3 Seleccionar una base de datos

Antes de crear tablas o consultar datos, debes **indicar en qué BD trabajarás**:

```sql
USE nombre_bd;
```

**Ejemplo:**

```sql
USE FORESTECH;
```

**Salida:**
`Database changed`

**Analogía:** Es como decir "Abre el libro de la biblioteca llamado FORESTECH". Ahora todas las tablas que crees/consultes serán dentro de esta BD.

### 6.4 Listar tablas de la BD actual

```sql
SHOW TABLES;
```

**Salida esperada (ahora):**
`Empty set (0.00 sec)`

Porque aún no hemos creado tablas. Eso viene en **Fase 03.2**.

### 6.5 Ver estructura de una tabla

Cuando existan tablas, puedes ver sus columnas con:

```sql
DESCRIBE nombre_tabla;
-- o abreviado:
DESC nombre_tabla;
```

**Ejemplo (futuro):**

```sql
DESC oil_products;
```

**Salida (ejemplo futuro):**

```
+----------------+--------------+------+-----+---------+-------+
| Field          | Type         | Null | Key | Default | Extra |
+----------------+--------------+------+-----+---------+-------+
| id             | varchar(20)  | NO   | PRI | NULL    |       |
| name           | varchar(100) | NO   |     | NULL    |       |
| unidadDeMedida | varchar(20)  | YES  |     | NULL    |       |
| priceXUnd      | double       | YES  |     | NULL    |       |
+----------------+--------------+------+-----+---------+-------+
```

### 6.6 Eliminar una base de datos (¡Cuidado!)

```sql
DROP DATABASE nombre_bd;
```

**⚠️ ADVERTENCIA:** Este comando **borra TODO** (tablas, datos, relaciones). No pide confirmación.

**Uso seguro:** Solo en desarrollo cuando quieres empezar de cero.

---

## 7. Creando la Base de Datos FORESTECH

### 7.1 Paso a paso completo

Abre terminal WSL y sigue esta secuencia:

#### 1. Iniciar MySQL

```bash
sudo service mysql start
```

#### 2. Conectarse como root

```bash
sudo mysql -u root -p
```

Ingresa tu contraseña.

#### 3. Verificar BDs existentes

```sql
SHOW DATABASES;
```

Confirma que `FORESTECH` no existe aún.

#### 4. Crear la base de datos

```sql
CREATE DATABASE FORESTECH;
```

**Salida esperada:**
`Query OK, 1 row affected (0.01 sec)`

#### 5. Verificar creación

```sql
SHOW DATABASES;
```

**Deberías ver:**

```
+--------------------+
| Database           |
+--------------------+
| FORESTECH          |  ← ¡Nuevo!
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```

#### 6. Seleccionar la BD

```sql
USE FORESTECH;
```

**Salida:**
`Database changed`

#### 7. Confirmar que está vacía

```sql
SHOW TABLES;
```

**Salida:**
`Empty set (0.00 sec)`

**Perfecto.** La BD existe y está lista para recibir tablas en la siguiente fase.

#### 8. Salir del cliente MySQL

```sql
exit;
```

---

## 8. Técnica Metacognitiva: Mapa Mental

### ¿Qué es la Metacognición?

Es **"pensar sobre cómo piensas"**. En aprendizaje de programación, significa:
- Reflexionar sobre qué entiendes y qué no
- Conectar conceptos nuevos con conocimientos previos
- Visualizar relaciones entre ideas

### Mapa Mental: Fundamentos de Bases de Datos

Crea este diagrama en papel o herramientas como Excalidraw, draw.io, o incluso en texto:

```
                    ┌──────────────────────┐
                    │   BASE DE DATOS      │
                    │    (FORESTECH)       │
                    └──────────┬───────────┘
                               │
                               │ contiene
                               ↓
                    ┌──────────────────────┐
                    │      TABLAS          │
                    │  (oil_products,      │
                    │   movements, etc.)   │
                    └──────────┬───────────┘
                               │
                 ┌─────────────┴─────────────┐
                 │                           │
                 ↓                           ↓
      ┌──────────────────┐        ┌──────────────────┐
      │      FILAS        │        │    COLUMNAS      │
      │   (Registros)     │        │    (Campos)      │
      │                   │        │                  │
      │ Cada producto     │        │  id (PK)         │
      │ individual        │        │  name            │
      │                   │        │  priceXUnd       │
      │ Ej: Diesel        │        │  unidadDeMedida  │
      └───────────────────┘        └──────────────────┘
                                            │
                                            │ tipos
                                            ↓
                                   ┌─────────────────┐
                                   │  TIPOS DE DATOS │
                                   │                 │
                                   │  VARCHAR        │
                                   │  INT            │
                                   │  DOUBLE         │
                                   │  DATE           │
                                   └─────────────────┘
```

### Ejercicio: Expande tu Mapa Mental

Agrega estos conceptos con tus propias palabras:
1. **Primary Key (PK)** → Relación con "id" único
2. **Foreign Key (FK)** → Cómo conecta tablas
3. **Comandos SQL básicos** → CREATE DATABASE, USE, SHOW TABLES

**Técnica:** Usa colores diferentes para:
- 🟦 Conceptos estructurales (BD, tabla, fila)
- 🟩 Tipos de datos
- 🟨 Comandos SQL
- 🟥 Restricciones (PK, FK, NOT NULL)

---

## 9. Ejercicios Prácticos

### 📝 Ejercicio 1: Listar Bases de Datos

**Objetivo:** Familiarizarse con el cliente MySQL.

**Pasos:**
1. Inicia MySQL: `sudo service mysql start`
2. Conéctate: `sudo mysql -u root -p`
3. Ejecuta: `SHOW DATABASES;`

**Documenta:**
- Captura de pantalla o copia la salida
- ¿Cuántas bases de datos ves?
- ¿Reconoces alguna?

**Resultado esperado:**

```
+--------------------+
| Database           |
+--------------------+
| FORESTECH          |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)
```

---

### 📝 Ejercicio 2: Crear BD de Prueba

**Objetivo:** Practicar creación y eliminación de BDs.

**Pasos:**
1. Estando en MySQL, ejecuta:
   ```sql
   CREATE DATABASE prueba_forestech;
   ```
2. Verifica creación:
   ```sql
   SHOW DATABASES;
   ```
3. Elimina la BD de prueba:
   ```sql
   DROP DATABASE prueba_forestech;
   ```
4. Confirma eliminación:
   ```sql
   SHOW DATABASES;
   ```

**Reflexión:**
- ¿Qué mensaje aparece al crear la BD?
- ¿Qué sucede si intentas crear una BD con el mismo nombre dos veces?

**Prueba esto:**

```sql
CREATE DATABASE prueba_forestech;
CREATE DATABASE prueba_forestech;  -- ¿Qué error da?
```

**Error esperado:**
`ERROR 1007 (HY000): Can't create database 'prueba_forestech'; database exists`

**Limpieza:**

```sql
DROP DATABASE prueba_forestech;
```

---

### 📝 Ejercicio 3: Navegar entre Bases de Datos

**Objetivo:** Entender el comando `USE`.

**Pasos:**
1. Selecciona la BD del sistema:
   ```sql
   USE mysql;
   ```
2. Lista las tablas:
   ```sql
   SHOW TABLES;
   ```
   Verás muchas tablas del sistema (user, db, tables_priv, etc.)

3. Cambia a FORESTECH:
   ```sql
   USE FORESTECH;
   ```
4. Lista tablas:
   ```sql
   SHOW TABLES;
   ```
   Debería estar vacío.

**Pregunta clave:**
¿Por qué necesitamos `USE` antes de trabajar con tablas?

**Respuesta:**
Porque MySQL necesita saber **en qué biblioteca buscar el estante**. Sin `USE`, no sabe dónde crear/buscar las tablas.

---

### 📝 Ejercicio 4: Explorar Metadatos de BD

**Objetivo:** Usar comandos avanzados de información.

**Comando 1: Ver BD actual**

```sql
SELECT DATABASE();
```

**Salida:**

```
+------------+
| DATABASE() |
+------------+
| FORESTECH  |
+------------+
```

**Comando 2: Ver versión de MySQL**

```sql
SELECT VERSION();
```

**Salida:**

```
+-----------+
| VERSION() |
+-----------+
| 8.0.39    |
+-----------+
```

**Comando 3: Ver fecha/hora del servidor**

```sql
SELECT NOW();
```

**Salida:**

```
+---------------------+
| NOW()               |
+---------------------+
| 2025-01-15 14:32:05 |
+---------------------+
```

**Documenta:** Anota tu versión de MySQL y fecha actual.

---

### 📝 Ejercicio 5: Simulación de Error Común

**Objetivo:** Aprender a leer mensajes de error.

**Escenario:** Intentar usar una tabla sin seleccionar BD.

**Pasos:**
1. Sal de cualquier BD:
   ```sql
   USE mysql;
   ```
2. Intenta crear una tabla en FORESTECH sin usar `USE`:
   ```sql
   CREATE TABLE oil_products (id VARCHAR(20));
   ```

**Error esperado:**
`ERROR: No database selected`

**Solución:**

```sql
USE FORESTECH;
CREATE TABLE oil_products (id VARCHAR(20));
```

**Ahora sí funcionará.** (Aunque eliminaremos esta tabla para crearla correctamente en Fase 03.2)

**Limpieza:**

```sql
DROP TABLE oil_products;
```

**Reflexión escrita:**
- ¿Qué aprendiste sobre la importancia de `USE`?
- ¿Cómo interpretas los mensajes de error de MySQL?

---

## 10. Generador de Quiz de Validación

### 📋 Mini-Prompt para Claude/ChatGPT

Copia y pega este prompt en Claude o ChatGPT para generar un quiz personalizado:

---

**PROMPT:**

```
Genera un quiz en formato markdown estructurado con 5 preguntas sobre fundamentos de bases de datos MySQL, diseñado para un estudiante que acaba de completar la Fase 03.1 del proyecto Forestech (aprendizaje de Java desde cero).

Contexto del estudiante:
- Ha creado la base de datos FORESTECH
- Conoce comandos básicos: SHOW DATABASES, USE, CREATE DATABASE
- Entiende conceptos: tabla, fila, columna, primary key
- Está aprendiendo en español

Estructura requerida para cada pregunta:

## Pregunta X: [Título descriptivo]

**Contexto/Escenario:** [Si aplica, situación práctica relacionada con Forestech]

**Pregunta:** [Enunciado claro]

**Opciones:**
a) [Opción A]
b) [Opción B]
c) [Opción C]
d) [Opción D]

**Respuesta Correcta:** [Letra]

**Explicación:**
[Explicación detallada de 3-5 líneas que incluya:
- Por qué la respuesta correcta es válida
- Por qué las incorrectas fallan
- Concepto clave reforzado
- Analogía con Forestech si es posible]

---

Temas específicos de las 5 preguntas:

1. **Diferencia entre Base de Datos y Tabla** (Opción múltiple)
   - Debe incluir la analogía biblioteca-estante
   - Opciones que confundan tabla con fila o columna

2. **Qué es una Primary Key** (Verdadero/Falso con justificación)
   - Afirmaciones sobre unicidad, obligatoriedad, inmutabilidad
   - Incluir ejemplo con tabla oil_products

3. **Comando para crear Base de Datos** (Completar código)
   - Proporcionar comando incompleto con espacios en blanco
   - Opciones de sintaxis correcta vs errores comunes

4. **Propósito del comando USE** (Opción múltiple)
   - Escenario: "¿Qué sucede si ejecutas CREATE TABLE sin antes usar USE?"
   - Opciones que confundan con otros comandos

5. **Tipos de datos básicos MySQL** (Matching/Emparejar)
   - Columna A: VARCHAR, INT, DOUBLE, DATE
   - Columna B: Descripciones de casos de uso en Forestech
   - Formato de respuesta: "1-C, 2-A, 3-D, 4-B"

Generación adicional:
- Incluye al final una sección "## Autoevaluación" con escala:
  - 5/5: Excelente comprensión, listo para Fase 03.2
  - 3-4/5: Buen progreso, revisar temas específicos
  - 0-2/5: Repasar Fase 03.1 antes de continuar

- Agrega "## Recursos de Refuerzo" con 3 sugerencias de ejercicios adicionales si el estudiante falló preguntas específicas.
```

---

### Ejemplo de Uso

1. Copia el prompt anterior
2. Pégalo en Claude.ai o ChatGPT
3. Revisa el quiz generado
4. Responde las preguntas sin mirar el archivo
5. Compara tus respuestas con las correctas
6. Lee las explicaciones incluso si acertaste

**Objetivo:** Validar tu comprensión antes de avanzar a **Fase 03.2** (SQL DDL/DML).

---

## 11. Checkpoint de Fase

### ✅ Validación de Conocimientos

Antes de continuar a la Fase 03.2, debes poder responder SÍ a estas afirmaciones:

1. **Conceptual:**
   - [ ] Entiendo la diferencia entre Base de Datos, Tabla, Fila y Columna
   - [ ] Puedo explicar la analogía biblioteca-estantes-libros-páginas
   - [ ] Sé qué es una Primary Key y por qué es importante
   - [ ] Comprendo la relación entre Foreign Keys (aunque aún no las hemos usado)

2. **Práctico:**
   - [ ] He instalado MySQL en WSL exitosamente
   - [ ] Puedo iniciar el servicio: `sudo service mysql start`
   - [ ] Puedo conectarme: `sudo mysql -u root -p`
   - [ ] He creado la BD FORESTECH
   - [ ] Sé usar: `SHOW DATABASES`, `USE`, `SHOW TABLES`

3. **Metacognitivo:**
   - [ ] Creé un mapa mental con al menos 4 conceptos conectados
   - [ ] Completé los 5 ejercicios prácticos
   - [ ] Documenté mi proceso (capturas/anotaciones)
   - [ ] Respondí el quiz generado con ≥3/5 respuestas correctas

### 🎯 Criterio de Aprobación

**Mínimo requerido:** 10/12 checkboxes marcados.

**Si tienes menos:**
1. Revisa las secciones donde tienes dudas
2. Repite los ejercicios prácticos
3. Genera otro quiz con el prompt y reintenta
4. Pregunta a Claude específicamente sobre conceptos confusos

### 📝 Reflexión Final (Obligatoria)

Responde estas 3 preguntas en un archivo `fase_03.1_reflexion.txt` o en tu cuaderno:

1. **¿Qué fue lo más difícil de esta fase?**
   - Instalación técnica, comandos SQL, conceptos teóricos, etc.

2. **¿Cómo se relaciona la BD FORESTECH con el código Java de Fase 2?**
   - Piensa en `ArrayList<Movement>` vs tabla `combustibles_movements`

3. **¿Qué esperas aprender en Fase 03.2 (Crear tablas)?**
   - Define al menos 2 objetivos personales

### 🚀 Siguiente Paso

Una vez completado el checkpoint:

```bash
cd /home/hp/forestechOil
git add roadmaps/FASE_03.1_FUNDAMENTOS_BD_SQL.md
git commit -m "fase 3.1 checkpoint: fundamentos de BD y MySQL completados"
```

**Continúa a:** `FASE_03.2_SQL_DDL_DML_BASICO.md`

---

## 📚 Recursos Adicionales (Opcionales)

### Documentación Oficial

- [MySQL 8.0 Reference Manual](https://dev.mysql.com/doc/refman/8.0/en/) (en inglés)
- [Tutorial de MySQL en W3Schools](https://www.w3schools.com/mysql/) (en inglés, muy visual)

### Videos Recomendados (YouTube)

Busca tutoriales en español sobre:
- "Introducción a bases de datos relacionales"
- "Comandos básicos MySQL para principiantes"
- "Instalación MySQL en Ubuntu/WSL"

**Tiempo sugerido:** 15-30 min de video complementario.

### Herramientas Visuales

- **MySQL Workbench:** Cliente gráfico para MySQL (opcional, fase avanzada)
- **DBeaver:** Alternativa multiplataforma gratuita
- **dbdiagram.io:** Crear diagramas ER online

**Por ahora:** Usa solo la terminal para fortalecer comandos.

---

## ❓ Preguntas Frecuentes

### 1. "¿Por qué uso `sudo` con `mysql`?"

En WSL, MySQL se ejecuta con permisos de sistema. `sudo` te da acceso como administrador.

**Alternativa (configuración avanzada):** Crear un usuario MySQL sin privilegios de root (Fase posterior).

### 2. "Olvidé mi contraseña de root MySQL"

**Solución:**

```bash
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'nueva_contraseña';
FLUSH PRIVILEGES;
exit;
```

### 3. "MySQL dice 'Can't connect to local MySQL server'"

**Causa:** El servicio no está iniciado.

**Solución:**

```bash
sudo service mysql start
sudo service mysql status  # Verificar
```

### 4. "¿Puedo usar otro nombre en vez de FORESTECH?"

Técnicamente sí, pero **no es recomendable** porque las fases siguientes asumen ese nombre. Mantén `FORESTECH` para consistencia.

### 5. "¿Necesito aprender todos los comandos SQL ahora?"

**No.** Esta fase solo cubre fundamentos. Aprenderás progresivamente:
- Fase 03.2: CREATE TABLE, INSERT, SELECT
- Fase 03.3: JDBC (conexión Java-MySQL)
- Fases posteriores: UPDATE, DELETE, JOIN, transacciones

---

## 🎓 Conclusión

**¡Felicitaciones!** Has completado la Fase 03.1. Ahora tienes:

✅ Comprensión sólida de qué son las bases de datos relacionales
✅ MySQL instalado y funcionando en tu WSL
✅ La base de datos FORESTECH creada y lista
✅ Habilidad para navegar entre BDs con comandos básicos
✅ Fundamentos teóricos para crear tablas en la siguiente fase

**Recuerda:** La base de datos es solo una **herramienta de persistencia**. El corazón de Forestech sigue siendo tu lógica de negocio en Java (clases `Movement`, `Vehicle`, managers, etc.). Ahora vamos a conectar ambos mundos.

**Próximo hito:** Crear la tabla `oil_products` con tipos de datos, constraints, e insertar los primeros productos combustibles.

---

**Tiempo estimado de Fase 03.1:** 2-3 horas (incluyendo instalación, ejercicios y quiz)

**Autor:** Material didáctico del Proyecto Forestech CLI
**Versión:** 1.0
**Última actualización:** Enero 2025
