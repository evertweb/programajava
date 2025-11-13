# 🌲 Forestech CLI - Sistema de Gestión de Combustibles

> **Proyecto educativo progresivo de Java desde cero hasta conceptos avanzados**

[![Java Version](https://img.shields.io/badge/Java-17_LTS-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Build Tool](https://img.shields.io/badge/Maven-3.x-blue.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Educational-green.svg)]()

## 📖 Descripción

**Forestech CLI** es un sistema de gestión de inventario de combustibles diseñado para administrar movimientos de entrada/salida, vehículos, proveedores, facturas y control de stock en tiempo real.

Este proyecto tiene un enfoque **100% educativo**: cada línea de código está diseñada para enseñar conceptos fundamentales de Java, desde variables y bucles hasta transacciones JDBC y manejo de excepciones personalizadas.

### 🎯 Caso de Uso Real

Gestiona el flujo completo de combustible en una operación forestal:
- 📥 **ENTRADA**: Registro de compras a proveedores con factura
- 📤 **SALIDA**: Despacho a vehículos/maquinaria con validación de stock
- 📊 **Inventario**: Cálculo automático de stock disponible
- 🚜 **Vehículos**: Seguimiento de horómetro y últimas cargas
- 🧾 **Facturas**: Gestión de facturas con detalles (transacciones atómicas)

---

## ✨ Funcionalidades Implementadas

### ✅ Completadas (Fases 0-4)

| Módulo | Estado | Archivos Clave | Conceptos Aplicados |
|--------|--------|----------------|---------------------|
| **Modelos (POO)** | 100% | `models/*.java` | Clases, constructores, encapsulación, getters/setters |
| **Conexión BD** | 100% | `config/DatabaseConnection.java` | JDBC, Connection pooling, singleton |
| **CRUD Productos** | 100% | `services/ProductServices.java` | PreparedStatement, ResultSet, CRUD completo |
| **CRUD Movimientos** | 90% | `services/MovementServices.java` | Transacciones, validación de stock |
| **CRUD Vehículos** | 100% | `services/VehicleServices.java` | Relaciones FK, consultas con JOIN |
| **CRUD Proveedores** | 100% | `services/SupplierServices.java` | Operaciones básicas CRUD |
| **Gestión Facturas** | 100% | `services/FacturaServices.java` | Transacciones atómicas, batch insert |
| **Excepciones** | 100% | `exceptions/*.java` | Excepciones personalizadas, manejo de errores |
| **Helpers UI** | 100% | `helpers/*.java` | Utilidades de consola, formateo de datos |

### 🚧 En Desarrollo (Fase 6)

| Módulo | Estado | Descripción |
|--------|--------|-------------|
| **CLI Interactiva** | 10% | Menús interactivos con `AppController.java` (skeleton creado) |
| **Reportes** | 0% | Generación de reportes de inventario y movimientos |
| **Auditoria** | 0% | Registro de operaciones críticas |

---

## 🏗️ Arquitectura del Proyecto

```
Forestech CLI (Capas)
│
├─ 📦 PRESENTACIÓN (UI/CLI)
│  └─ AppController.java          ← Orquestador principal (en desarrollo)
│  └─ helpers/*.java               ← Utilidades de menú y display
│
├─ 💼 LÓGICA DE NEGOCIO (Services)
│  ├─ ProductServices.java         ← CRUD productos
│  ├─ MovementServices.java        ← CRUD movimientos + validación stock
│  ├─ VehicleServices.java         ← CRUD vehículos
│  ├─ SupplierServices.java        ← CRUD proveedores
│  └─ FacturaServices.java         ← Transacciones facturas con detalles
│
├─ 🗂️ PERSISTENCIA (Database)
│  └─ config/DatabaseConnection.java  ← Gestión de conexiones JDBC
│
├─ 📐 MODELOS (Entities)
│  ├─ Movement.java                ← Movimiento de combustible
│  ├─ Product.java                 ← Producto (combustibles)
│  ├─ Vehicle.java                 ← Vehículo/maquinaria
│  ├─ Supplier.java                ← Proveedor
│  ├─ Factura.java                 ← Factura de compra
│  └─ DetalleFactura.java          ← Detalle de factura (items)
│
├─ 🚨 EXCEPCIONES (Error Handling)
│  ├─ InsufficientStockException.java     ← Stock insuficiente para SALIDA
│  ├─ InvalidMovementException.java       ← Movimiento con datos inválidos
│  ├─ TransactionFailedException.java     ← Error en transacción
│  └─ DatabaseException.java              ← Error de conexión/BD
│
└─ 🛠️ UTILIDADES (Utils)
   └─ IdGenerator.java             ← Generación de IDs únicos (UUID)
```

### 🎨 Diagrama de Flujo de Datos

```
┌──────────────┐
│ Usuario CLI  │ (En desarrollo: AppController)
└──────┬───────┘
       │
       v
┌──────────────────────────────┐
│ BusinessRules (Validaciones) │
└──────────────┬───────────────┘
               │
               v
┌──────────────────────────────┐
│ Services (Lógica + CRUD)     │
│ - MovementServices           │
│ - ProductServices            │
│ - VehicleServices            │
└──────────────┬───────────────┘
               │
               v
┌──────────────────────────────┐
│ DatabaseConnection (JDBC)    │
└──────────────┬───────────────┘
               │
               v
┌──────────────────────────────┐
│ MySQL (Base de Datos)        │
│ - Movement (tabla)           │
│ - oil_products (tabla)       │
│ - vehicles (tabla)           │
│ - facturas (tabla)           │
└──────────────────────────────┘
```

---

## 📦 Estructura Detallada del Proyecto

```
forestechOil/
│
├─ forestech-cli-java/
│  └─ src/main/java/com/forestech/
│     │
│     ├─ Main.java                      ← Entry point (testing)
│     ├─ AppController.java             ← Orquestador CLI (skeleton)
│     ├─ AppConfig.java                 ← Constantes (IVA_RATE, etc.)
│     │
│     ├─ config/
│     │  └─ DatabaseConnection.java     ← Conexión MySQL
│     │
│     ├─ models/
│     │  ├─ Movement.java               ← Movimiento ENTRADA/SALIDA
│     │  ├─ Product.java                ← Producto (combustible)
│     │  ├─ Vehicle.java                ← Vehículo/maquinaria
│     │  ├─ Supplier.java               ← Proveedor
│     │  ├─ Factura.java                ← Factura de compra
│     │  └─ DetalleFactura.java         ← Detalle factura
│     │
│     ├─ services/
│     │  ├─ MovementServices.java       ← CRUD movimientos + validación stock
│     │  ├─ ProductServices.java        ← CRUD productos
│     │  ├─ VehicleServices.java        ← CRUD vehículos
│     │  ├─ SupplierServices.java       ← CRUD proveedores
│     │  └─ FacturaServices.java        ← Transacciones facturas
│     │
│     ├─ exceptions/
│     │  ├─ InsufficientStockException.java
│     │  ├─ InvalidMovementException.java
│     │  ├─ TransactionFailedException.java
│     │  └─ DatabaseException.java
│     │
│     ├─ helpers/
│     │  ├─ BannerMenu.java             ← Banners ASCII
│     │  ├─ MenuHelper.java             ← Menús de consola
│     │  ├─ DataDisplay.java            ← Formateo de datos
│     │  └─ InputHelper.java            ← Validación de entrada usuario
│     │
│     ├─ utils/
│     │  └─ IdGenerator.java            ← Generación de IDs (UUID)
│     │
│     └─ managers/ (Legacy - Fase 2.5)
│        └─ MovementManagers.java       ← Manager patrón (pre-JDBC)
│
├─ roadmaps/                            ← Guías educativas por fase
│  ├─ FASE_03.3_JDBC_CONEXION.md
│  ├─ FASE_04.1_INSERT_CREATE.md
│  ├─ FASE_04.2_SELECT_READ.md
│  ├─ FASE_04.3_UPDATE_MODIFICAR.md
│  ├─ FASE_04.4_DELETE_ELIMINAR.md
│  ├─ FASE_04.5_CONSOLIDACION_CRUD.md
│  ├─ FASE_04.6_TRANSACCIONES_JDBC.md
│  ├─ FASE_04.7_SOFT_DELETE_AUDITORIA.md
│  ├─ FASE_04.8_CONSULTAS_AVANZADAS.md
│  └─ FASE_05_LOGICA_NEGOCIO.md
│
├─ 01_recreate_tables_with_fk.sql       ← DDL: Crear tablas con FK
├─ 02_restore_data.sql                  ← DML: Insertar datos de prueba
├─ 03_add_suppliers_table.sql           ← DDL: Tabla proveedores
├─ CLAUDE.md                            ← Instrucciones para Claude Code
└─ README.md                            ← Este archivo
```

---

## 🚀 Cómo Ejecutar el Proyecto

### Pre-requisitos

- ☕ **Java 17** (LTS) instalado
- 🛠️ **Maven 3.x** instalado
- 🗄️ **MySQL 8.0** corriendo en localhost
- 🐧 Sistema operativo: Linux (WSL Ubuntu) o macOS

### 1️⃣ Clonar el repositorio

```bash
cd ~
git clone <url-del-repo>
cd forestechOil
```

### 2️⃣ Configurar la base de datos

```bash
# 1. Acceder a MySQL
mysql -u root -p

# 2. Crear la base de datos
CREATE DATABASE FORESTECHOIL;
USE FORESTECHOIL;

# 3. Ejecutar scripts en orden
source 01_recreate_tables_with_fk.sql;
source 02_restore_data.sql;
source 03_add_suppliers_table.sql;

# 4. Verificar tablas creadas
SHOW TABLES;
```

### 3️⃣ Configurar credenciales (si es necesario)

Edita `/forestech-cli-java/src/main/java/com/forestech/config/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/FORESTECHOIL";
private static final String USER = "root";
private static final String PASSWORD = "tu_password_aqui";  // Cambiar
```

### 4️⃣ Compilar y ejecutar

```bash
# Navegar al proyecto Maven
cd forestech-cli-java

# Limpiar y compilar
mvn clean compile

# Ejecutar Main.java (testing)
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

### 5️⃣ Salida Esperada

```
=== FORESTECH CLI - PRUEBAS DE SERVICIOS ===

1️⃣ Probando conexión...
✅ Conexión exitosa!
─────────────────────────────────────────
🗄️  Producto BD    : MySQL
📦 Versión BD     : 8.0.33
🔗 Driver JDBC    : MySQL Connector/J
📌 Versión Driver : mysql-connector-j-8.0.33
🏛️  Database       : FORESTECHOIL
👤 Usuario        : root@localhost
─────────────────────────────────────────

2️⃣ Consultando stock de ACPM (productId='1')...
📦 Stock actual: 500.0 galones

3️⃣ Intentando SALIDA de 1000 galones (debe fallar)...
❌ ERROR ESPERADO: Stock insuficiente. Disponible: 500.0, Solicitado: 1000.0
   Stock actual: 500.0
   Solicitado: 1000.0

4️⃣ Listando proveedores...
Supplier{id='SUPP-001', name='Distribuidora Petróleo S.A.', ...}

5️⃣ Listando facturas...
Total facturas: 3
Primera factura: Factura{numeroFactura='10734', ...}
  Detalles: 2

✅ TODAS LAS PRUEBAS COMPLETADAS
```

---

## 🗄️ Base de Datos

### Estructura de Tablas

```sql
-- Tabla principal de movimientos
CREATE TABLE Movement (
    id VARCHAR(50) PRIMARY KEY,
    movementType ENUM('ENTRADA', 'SALIDA') NOT NULL,
    product_id VARCHAR(50),
    vehicle_id VARCHAR(50),
    numero_factura VARCHAR(50),
    unidadDeMedida ENUM('GALON', 'GARRAFA', 'CUARTO', 'CANECA'),
    quantity DECIMAL(10,2) NOT NULL,
    unitPrice DECIMAL(10,2) NOT NULL,
    movementDate DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES oil_products(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (numero_factura) REFERENCES facturas(numero_factura)
);

-- Tabla de productos (combustibles)
CREATE TABLE oil_products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unidadDeMedida VARCHAR(20),
    priceXUnd DECIMAL(10,2)
);

-- Tabla de vehículos
CREATE TABLE vehicles (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    capacity DECIMAL(10,2),
    fuel_product_id VARCHAR(50),
    haveHorometer BOOLEAN,

    FOREIGN KEY (fuel_product_id) REFERENCES oil_products(id)
);

-- Tabla de facturas
CREATE TABLE facturas (
    numero_factura VARCHAR(50) PRIMARY KEY,
    fecha_emision DATE,
    fecha_vencimiento DATE,
    supplier_id VARCHAR(50),
    subtotal DECIMAL(12,2),
    iva DECIMAL(12,2),
    total DECIMAL(12,2),
    observaciones TEXT,
    forma_pago VARCHAR(50),
    cuenta_bancaria VARCHAR(50)
);

-- Tabla de detalles de factura
CREATE TABLE detalle_factura (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(50),
    producto VARCHAR(100),
    cantidad DECIMAL(10,2),
    precio_unitario DECIMAL(10,2),

    FOREIGN KEY (numero_factura) REFERENCES facturas(numero_factura)
);
```

### Conexión desde Java

```java
// DatabaseConnection.java
public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
}

// Uso en Services
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    // Operaciones JDBC
}
```

---

## 📚 Roadmaps de Aprendizaje

Este proyecto sigue una **metodología invertida**: primero se escribe código funcional, luego se documentan los roadmaps para que otros estudiantes aprendan por descubrimiento guiado.

### Fases Completadas

| Fase | Archivo | Conceptos | Estado |
|------|---------|-----------|--------|
| **Fase 0** | _(No documentada)_ | Setup y herramientas | ✅ 100% |
| **Fase 1** | _(No documentada)_ | Fundamentos Java | ✅ 100% |
| **Fase 2** | _(No documentada)_ | POO (clases, objetos) | ✅ 100% |
| **Fase 2.5** | _(No documentada)_ | Manager pattern, Collections | ✅ 100% |
| **Fase 3** | `FASE_03.3_JDBC_CONEXION.md` | JDBC, Connection | ✅ 100% |
| **Fase 4.1** | `FASE_04.1_INSERT_CREATE.md` | CREATE con PreparedStatement | ✅ 100% |
| **Fase 4.2** | `FASE_04.2_SELECT_READ.md` | READ con ResultSet | ✅ 100% |
| **Fase 4.3** | `FASE_04.3_UPDATE_MODIFICAR.md` | UPDATE con validación | ✅ 100% |
| **Fase 4.4** | `FASE_04.4_DELETE_ELIMINAR.md` | DELETE con integridad | ✅ 100% |
| **Fase 4.5** | `FASE_04.5_CONSOLIDACION_CRUD.md` | CRUD completo | ✅ 100% |
| **Fase 4.6** | `FASE_04.6_TRANSACCIONES_JDBC.md` | Transacciones ACID | ✅ 100% |
| **Fase 4.7** | `FASE_04.7_SOFT_DELETE_AUDITORIA.md` | Soft delete | ✅ 80% |
| **Fase 4.8** | `FASE_04.8_CONSULTAS_AVANZADAS.md` | JOINs, agregaciones | ✅ 70% |
| **Fase 5** | `FASE_05_LOGICA_NEGOCIO.md` | Business rules, excepciones | 🚧 60% |

### Fases Pendientes

| Fase | Descripción | Estado |
|------|-------------|--------|
| **Fase 6** | CLI Interactiva | 🚧 10% (AppController skeleton) |
| **Fase 7** | Manejo avanzado de excepciones | ⏳ 0% |
| **Fase 8** | Streams, Lambdas, Optional | ⏳ 0% |
| **Fase 9** | Testing con JUnit | ⏳ 0% |
| **Fase 10** | Refactoring y patrones de diseño | ⏳ 0% |

---

## 👨‍💻 Estado del Desarrollo

### Tabla de Completitud

| Paquete | Archivos | Completitud | TODOs Pendientes | Comentarios |
|---------|----------|-------------|------------------|-------------|
| `models` | 6 archivos | 100% | 0 | Modelos completos con constructores duales (crear/cargar) |
| `config` | 1 archivo | 100% | 0 | Conexión MySQL funcionando |
| `services` | 5 archivos | 90% | Ver abajo | CRUD completo, transacciones implementadas |
| `exceptions` | 4 archivos | 100% | 0 | Excepciones personalizadas completas |
| `helpers` | 4 archivos | 100% | 0 | Utilidades de consola completas |
| `utils` | 1 archivo | 100% | 0 | Generación de IDs con UUID |
| `managers` | 1 archivo | 100% (legacy) | 0 | Patrón manager pre-JDBC (educativo) |

### TODOs Activos en el Código

```java
// MovementServices.java
// TODO: Implementar getMovementsByVehicle(String vehicleId)
// TODO: Implementar getMovementsByDateRange(String inicio, String fin)

// AppController.java
// TODO: Implementar mostrarMenuPrincipal()
// TODO: Implementar procesarOpcion(int opcion)
// TODO: Implementar métodos CRUD interactivos para cada entidad
```

### Ejemplos de Código Funcional

#### Insertar Movimiento con Validación de Stock

```java
// Crear movimiento de SALIDA (valida stock automáticamente)
Movement salida = new Movement(
    "SALIDA", "1", "VEH-12345678", null,
    "GALON", 50.0, 8500.0
);

try {
    MovementServices.insertMovement(salida);
    System.out.println("✅ Movimiento registrado");
} catch (InsufficientStockException e) {
    System.out.println("❌ Stock insuficiente:");
    System.out.println("   Disponible: " + e.getStockActual());
    System.out.println("   Solicitado: " + e.getCantidadSolicitada());
} catch (DatabaseException e) {
    System.out.println("❌ Error de BD: " + e.getMessage());
}
```

#### Transacción Atómica (Factura con Detalles)

```java
// Crear factura con múltiples detalles (todo o nada)
Factura factura = new Factura("10735", LocalDate.now(), ...);

List<DetalleFactura> detalles = List.of(
    new DetalleFactura(0, "10735", "ACPM", 500.0, 8500.0),
    new DetalleFactura(0, "10735", "Gasolina Corriente", 300.0, 9200.0)
);

try {
    FacturaServices.createFacturaWithDetails(factura, detalles);
    System.out.println("✅ Factura creada con " + detalles.size() + " detalles");
} catch (TransactionFailedException e) {
    System.out.println("❌ Transacción revertida: " + e.getMessage());
}
```

---

## 🔧 Próximos Pasos

### Corto Plazo (Fase 6)

- [ ] Implementar `AppController` con menú principal interactivo
- [ ] Crear módulo de menús para cada entidad (Productos, Vehículos, Movimientos)
- [ ] Añadir validación de entrada del usuario con `InputHelper`
- [ ] Integrar todos los Services en la CLI

### Mediano Plazo (Fases 7-8)

- [ ] Crear jerarquía de excepciones personalizadas
- [ ] Implementar logging con SLF4J
- [ ] Refactorizar usando Streams y Lambdas (Java 8+)
- [ ] Añadir módulo de reportes con estadísticas

### Largo Plazo (Fases 9-10)

- [ ] Escribir tests unitarios con JUnit 5
- [ ] Implementar patrón Repository
- [ ] Migrar a SQL Server en DigitalOcean
- [ ] Crear API REST con Spring Boot (opcional)

---

## 📖 Recursos Adicionales

### Documentación Interna

- `CLAUDE.md` → Instrucciones para Claude Code (asistente IA)
- `roadmaps/` → Guías educativas paso a paso
- `*.sql` → Scripts de creación y carga de datos

### Conceptos Clave Aplicados

1. **POO**: Clases, objetos, encapsulación, herencia (parcial)
2. **JDBC**: Connection, PreparedStatement, ResultSet, try-with-resources
3. **Transacciones**: setAutoCommit(false), commit(), rollback()
4. **Excepciones**: try-catch-finally, excepciones personalizadas
5. **Patrones**: Singleton (DatabaseConnection), Manager (legacy)
6. **SQL**: DDL (CREATE TABLE), DML (INSERT/UPDATE/DELETE), DQL (SELECT)

### Enlaces Útiles

- [Documentación Java 17](https://docs.oracle.com/en/java/javase/17/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/)
- [Maven en 5 minutos](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

---

## 🙏 Créditos y Licencia

**Proyecto:** Forestech CLI
**Propósito:** Educativo (aprendizaje de Java desde cero)
**Autor:** Estudiante de Java
**Asistencia:** Claude Code (Anthropic)
**Licencia:** Uso educativo libre

**Filosofía del proyecto:**
> "Este proyecto prioriza el APRENDIZAJE sobre la velocidad. Cada concepto se introduce gradualmente, con ejemplos contextualizados y documentación exhaustiva. El código es verbose a propósito, para facilitar la comprensión."

---

## 📞 Soporte

Si estás siguiendo este proyecto como guía de aprendizaje:

1. Lee el archivo `CLAUDE.md` para entender la metodología
2. Sigue los roadmaps en orden (Fase 3 → Fase 4 → Fase 5)
3. Ejecuta los tests en `Main.java` para verificar cada concepto
4. Consulta los comentarios en el código (están en español)

**Nota:** Este README refleja el estado REAL del código al 2025-11-13. No se han documentado funcionalidades que no existan en el proyecto.
