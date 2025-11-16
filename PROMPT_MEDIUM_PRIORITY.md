# 🟡 MEDIUM PRIORITY REFACTORING - Forestech CLI

## 📋 CONTEXTO

Proyecto: **Forestech CLI** - Sistema de gestión de combustibles en Java 17 + Maven + MySQL
Branch: `modularizacion-con-raptor-mini`

---

## 🎯 TU MISIÓN: Completar 4 tareas MEDIUM PRIORITY

### ✅ YA COMPLETADO (NO TOCAR):
- ✅ DAO Layer + HikariCP + Controllers divididos (URGENT tasks)
- ✅ MovementCalculator + Validators + Service Interfaces + Environment Variables (HIGH tasks)

### ⚠️ TU TRABAJO (4 tareas MEDIUM):

---

## 📦 TAREA 9: Implement Builder Pattern (1.5 horas)

### Problema Actual:
Constructores complejos con muchos parámetros:
```java
// ❌ Difícil de leer y propenso a errores:
Movement movement = new Movement(
    "MOV-12345678",
    "ENTRADA",
    "FUE-98765432",
    null,
    "10734",
    "GALON",
    50.0,
    8500.0,
    LocalDateTime.now()
);
```

### Solución Requerida:
Implementar **Builder Pattern** para constructores complejos.

**1. Crear `MovementBuilder.java`:**
```java
package com.forestech.models.builders;

import com.forestech.models.Movement;
import com.forestech.utils.IdGenerator;
import java.time.LocalDateTime;

/**
 * Builder para construir objetos Movement de forma fluida y legible.
 * 
 * Ejemplo de uso:
 * <pre>
 * Movement entrada = new MovementBuilder()
 *     .type("ENTRADA")
 *     .product("FUE-12345678")
 *     .invoice("10734")
 *     .quantity(50.0)
 *     .unitPrice(8500.0)
 *     .build();
 * </pre>
 */
public class MovementBuilder {
    private String id;
    private String movementType;
    private String productId;
    private String vehicleId;
    private String numeroFactura;
    private String unidadDeMedida;
    private double quantity;
    private double unitPrice;
    private LocalDateTime createdAt;
    
    public MovementBuilder() {
        this.id = IdGenerator.generateMovementId();
        this.createdAt = LocalDateTime.now();
        this.unidadDeMedida = "GALON"; // Default
    }
    
    public MovementBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    public MovementBuilder type(String movementType) {
        this.movementType = movementType;
        return this;
    }
    
    public MovementBuilder product(String productId) {
        this.productId = productId;
        return this;
    }
    
    public MovementBuilder vehicle(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }
    
    public MovementBuilder invoice(String numeroFactura) {
        this.numeroFactura = numeroFactura;
        return this;
    }
    
    public MovementBuilder unit(String unidadDeMedida) {
        this.unidadDeMedida = unidadDeMedida;
        return this;
    }
    
    public MovementBuilder quantity(double quantity) {
        this.quantity = quantity;
        return this;
    }
    
    public MovementBuilder unitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }
    
    public MovementBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    /**
     * Construye el objeto Movement.
     * @return Movement configurado
     * @throws IllegalStateException si faltan campos obligatorios
     */
    public Movement build() {
        validateRequiredFields();
        
        Movement movement = new Movement(
            id,
            movementType,
            productId,
            vehicleId,
            numeroFactura,
            unidadDeMedida,
            quantity,
            unitPrice
        );
        movement.setCreatedAt(createdAt);
        return movement;
    }
    
    private void validateRequiredFields() {
        if (movementType == null) {
            throw new IllegalStateException("Movement type is required");
        }
        if (productId == null) {
            throw new IllegalStateException("Product ID is required");
        }
        if (quantity <= 0) {
            throw new IllegalStateException("Quantity must be greater than 0");
        }
        if (unitPrice <= 0) {
            throw new IllegalStateException("Unit price must be greater than 0");
        }
    }
}
```

**2. Crear también:**
- `ProductBuilder.java` (para Product)
- `VehicleBuilder.java` (para Vehicle)
- `SupplierBuilder.java` (para Supplier)

**3. Actualizar Controllers para usar builders:**
```java
// En MovementController.registrarEntrada():
Movement entrada = new MovementBuilder()
    .type("ENTRADA")
    .product(productoSeleccionado.getId())
    .invoice(numeroFactura)
    .quantity(cantidad)
    .unitPrice(precioUnitario)
    .build();

MovementServices.insertMovement(entrada);
```

---

## 📦 TAREA 10: Replace System.out.println with SLF4J (2 horas)

### Problema Actual:
Todo el código usa `System.out.println` (~500+ ocurrencias):
```java
System.out.println("✅ Movimiento insertado: " + id);
System.out.println("❌ Error: " + e.getMessage());
```

**Problemas:**
- No se pueden controlar niveles de log (INFO, DEBUG, ERROR)
- No se puede redirigir a archivos
- No hay timestamps automáticos
- Difícil debuggear en producción

### Solución Requerida:

**1. Agregar dependencias SLF4J al `pom.xml`:**
```xml
<dependencies>
    <!-- SLF4J API -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    
    <!-- Logback (implementación de SLF4J) -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
    </dependency>
</dependencies>
```

**2. Crear `src/main/resources/logback.xml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Console Appender (salida por consola) -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender (salida a archivo) -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/forestech.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/forestech-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <!-- Nivel de log para paquetes específicos -->
    <logger name="com.forestech.services" level="DEBUG" />
    <logger name="com.forestech.dao" level="DEBUG" />
    <logger name="com.zaxxer.hikari" level="WARN" />
    
</configuration>
```

**3. Actualizar TODAS las clases:**

**Buscar y reemplazar sistemáticamente:**

```java
// ❌ ANTES:
public class MovementServices {
    public static void insertMovement(Movement movement) {
        System.out.println("✅ Movimiento insertado: " + movement.getId());
        System.err.println("❌ Error: " + e.getMessage());
    }
}

// ✅ DESPUÉS:
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovementServices {
    private static final Logger logger = LoggerFactory.getLogger(MovementServices.class);
    
    public static void insertMovement(Movement movement) {
        logger.info("Movimiento insertado exitosamente: {}", movement.getId());
        logger.error("Error al insertar movimiento: {}", e.getMessage(), e);
    }
}
```

**Niveles de log a usar:**
- `logger.debug()` - Información detallada para debugging
- `logger.info()` - Operaciones normales (inserciones, consultas exitosas)
- `logger.warn()` - Advertencias (stock bajo, validaciones fallidas)
- `logger.error()` - Errores críticos (excepciones, fallos de BD)

**Archivos a actualizar (todos los .java):**
- Services (MovementServices, ProductServices, etc.)
- DAOs (MovementDAO, ProductDAO, etc.)
- Controllers (MovementController, ProductController, etc.)
- HikariCPDataSource
- AppController

**Ejemplo completo:**
```java
package com.forestech.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovementServices implements IMovementService {
    private static final Logger logger = LoggerFactory.getLogger(MovementServices.class);
    private static final MovementDAO movementDAO = new MovementDAO();
    
    @Override
    public void insertMovement(Movement movement) throws DatabaseException, ValidationException {
        logger.debug("Iniciando inserción de movimiento: {}", movement.getId());
        
        try {
            MovementValidator.validate(movement);
            logger.debug("Validación exitosa para movimiento: {}", movement.getId());
            
            movementDAO.insert(movement);
            logger.info("Movimiento insertado exitosamente: {} - Tipo: {}, Cantidad: {}",
                       movement.getId(), movement.getMovementType(), movement.getQuantity());
        } catch (ValidationException e) {
            logger.warn("Validación fallida para movimiento: {} - Razón: {}",
                       movement.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error crítico al insertar movimiento: {}", movement.getId(), e);
            throw new DatabaseException("Error al insertar movimiento", e);
        }
    }
}
```

---

## 📦 TAREA 11: Convert Magic Strings to Enums (1 hora)

### Problema Actual:
Strings literales esparcidos por todo el código:
```java
if (movement.getMovementType().equals("ENTRADA")) { ... }
if (product.getUnidadDeMedida().equals("GALON")) { ... }
if (vehicle.getCategory().equals("CAMION")) { ... }
```

**Problemas:**
- Typos no detectados en compilación
- Difícil refactorizar
- No hay autocompletado
- Fácil equivocarse

### Solución Requerida:

**1. Crear `enums/` package:**

```java
package com.forestech.enums;

/**
 * Tipos de movimiento de combustible.
 */
public enum MovementType {
    ENTRADA("ENTRADA", "Entrada de combustible desde proveedor"),
    SALIDA("SALIDA", "Salida de combustible a vehículo");
    
    private final String code;
    private final String description;
    
    MovementType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MovementType fromCode(String code) {
        for (MovementType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid movement type: " + code);
    }
}
```

```java
package com.forestech.enums;

/**
 * Unidades de medida para combustibles.
 */
public enum MeasurementUnit {
    GALON("GALON", "Galón"),
    LITRO("LITRO", "Litro"),
    BARRIL("BARRIL", "Barril");
    
    private final String code;
    private final String displayName;
    
    MeasurementUnit(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static MeasurementUnit fromCode(String code) {
        for (MeasurementUnit unit : values()) {
            if (unit.code.equalsIgnoreCase(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid measurement unit: " + code);
    }
}
```

```java
package com.forestech.enums;

/**
 * Categorías de vehículos.
 */
public enum VehicleCategory {
    CAMION("CAMION", "Camión"),
    CAMIONETA("CAMIONETA", "Camioneta"),
    TRACTOR("TRACTOR", "Tractor"),
    EXCAVADORA("EXCAVADORA", "Excavadora"),
    VOLQUETA("VOLQUETA", "Volqueta"),
    MOTONIVELADORA("MOTONIVELADORA", "Motoniveladora");
    
    private final String code;
    private final String displayName;
    
    VehicleCategory(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static VehicleCategory fromCode(String code) {
        for (VehicleCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle category: " + code);
    }
}
```

**2. Actualizar Models para usar enums:**

```java
// Movement.java
public class Movement {
    private MovementType movementType;  // En lugar de String
    private MeasurementUnit unidadDeMedida;  // En lugar de String
    
    // Getters/setters usando enums
    public MovementType getMovementType() {
        return movementType;
    }
    
    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
    
    // Métodos de compatibilidad para BD (guardar como String)
    public String getMovementTypeCode() {
        return movementType != null ? movementType.getCode() : null;
    }
    
    public void setMovementTypeFromCode(String code) {
        this.movementType = MovementType.fromCode(code);
    }
}
```

**3. Actualizar DAOs para convertir String ↔ Enum:**

```java
// MovementDAO.java
@Override
public void insert(Movement movement) throws SQLException {
    String sql = "INSERT INTO Movement (id, movementType, ...) VALUES (?, ?, ...)";
    
    try (Connection conn = HikariCPDataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, movement.getId());
        stmt.setString(2, movement.getMovementTypeCode());  // Enum → String
        // ...
    }
}

private Movement mapResultSetToMovement(ResultSet rs) throws SQLException {
    Movement m = new Movement();
    m.setId(rs.getString("id"));
    m.setMovementTypeFromCode(rs.getString("movementType"));  // String → Enum
    // ...
    return m;
}
```

**4. Actualizar Validators:**

```java
private static void validateMovementType(MovementType type) throws ValidationException {
    if (type == null) {
        throw new ValidationException("Movement type cannot be null");
    }
    // No necesita más validación, el enum garantiza valores válidos
}
```

**5. Actualizar Controllers:**

```java
// En lugar de:
if (tipoInput.equals("1")) {
    tipo = "ENTRADA";
}

// Ahora:
MovementType tipo = tipoInput.equals("1") ? MovementType.ENTRADA : MovementType.SALIDA;
```

---

## 📦 TAREA 12: Standardize to English-only Naming (1.5 horas)

### Problema Actual:
Código mezclado español/inglés:
```java
private String unidadDeMedida;  // Español
private double unitPrice;        // Inglés
private String numeroFactura;    // Español
private String vehicleId;        // Inglés
```

### Solución Requerida:

**IMPORTANTE:** Esta es una tarea **delicada** porque afecta Base de Datos.

**Estrategia de migración:**

**1. Identificar todos los campos a renombrar:**

```
Models:
├── Movement
│   ├── unidadDeMedida → measurementUnit
│   ├── numeroFactura → invoiceNumber
│   └── (mantener productId, vehicleId - ya en inglés)
│
├── Product
│   ├── unidadDeMedida → measurementUnit
│   └── priceXUnd → unitPrice
│
├── Vehicle
│   ├── (ya están en inglés)
│
└── Supplier
    └── (ya están en inglés)
```

**2. Opción A - Migración gradual (RECOMENDADA):**

Mantener compatibilidad con BD actual:

```java
public class Movement {
    // Nuevo nombre (interno en Java)
    private MeasurementUnit measurementUnit;
    
    // Métodos para BD (mantienen nombres viejos)
    public String getUnidadDeMedida() {
        return measurementUnit != null ? measurementUnit.getCode() : null;
    }
    
    public void setUnidadDeMedida(String unidad) {
        this.measurementUnit = MeasurementUnit.fromCode(unidad);
    }
    
    // Nuevos métodos (Java naming)
    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }
    
    public void setMeasurementUnit(MeasurementUnit unit) {
        this.measurementUnit = unit;
    }
}
```

**3. Opción B - Migración completa (requiere cambios en BD):**

Si quieres renombrar columnas en BD:

```sql
-- Script de migración
ALTER TABLE Movement 
    CHANGE COLUMN unidadDeMedida measurementUnit VARCHAR(50);
    
ALTER TABLE Movement 
    CHANGE COLUMN numeroFactura invoiceNumber VARCHAR(50);
    
ALTER TABLE oil_products
    CHANGE COLUMN unidadDeMedida measurementUnit VARCHAR(50);
    
ALTER TABLE oil_products
    CHANGE COLUMN priceXUnd unitPrice DECIMAL(10,2);
```

Luego actualizar DAOs:

```java
// MovementDAO.java
String sql = "INSERT INTO Movement (id, movementType, measurementUnit, invoiceNumber, ...) " +
             "VALUES (?, ?, ?, ?, ...)";
```

**4. Renombrar métodos en Services:**

```java
// ANTES:
getProductsByUnidadDeMedida(String unidad)

// DESPUÉS:
getProductsByMeasurementUnit(MeasurementUnit unit)
```

**5. Actualizar Controllers:**

Cambiar todos los nombres de variables y métodos a inglés:

```java
// ANTES:
String unidadMedida = InputHelper.readString("Unidad de medida: ");
double precioUnitario = InputHelper.readDouble("Precio unitario: ");

// DESPUÉS:
MeasurementUnit measurementUnit = selectMeasurementUnit();
double unitPrice = InputHelper.readDouble("Unit price: ");
```

**6. Actualizar mensajes UI (OPCIONAL):**

Decidir si mantener español en UI pero inglés en código:

```java
// Código en inglés, mensajes en español:
System.out.println("Unidad de medida: " + movement.getMeasurementUnit().getDisplayName());

// O todo en inglés:
System.out.println("Measurement unit: " + movement.getMeasurementUnit().getCode());
```

---

## ✅ CRITERIOS DE ACEPTACIÓN

Al terminar estas 4 tareas:

1. ✅ Existen 4 Builders en `models/builders/`
2. ✅ Controllers usan builders para crear objetos
3. ✅ 0 ocurrencias de `System.out.println` en código de negocio
4. ✅ Todos los logs usan SLF4J
5. ✅ `logback.xml` configurado correctamente
6. ✅ Logs se escriben en `logs/forestech.log`
7. ✅ Existen 3+ enums en `enums/`
8. ✅ Models usan enums en lugar de strings
9. ✅ 0 magic strings en validaciones
10. ✅ Naming consistente (todo inglés o strategy clara)
11. ✅ Proyecto compila: `mvn clean compile`
12. ✅ Tests pasan (si existen): `mvn test`

---

## 📁 ESTRUCTURA FINAL ESPERADA

```
src/main/java/com/forestech/
├── models/
│   ├── builders/
│   │   ├── MovementBuilder.java    ✅ NUEVO
│   │   ├── ProductBuilder.java     ✅ NUEVO
│   │   ├── VehicleBuilder.java     ✅ NUEVO
│   │   └── SupplierBuilder.java    ✅ NUEVO
│   ├── Movement.java               ✅ ACTUALIZAR (enums, English)
│   ├── Product.java                ✅ ACTUALIZAR (enums, English)
│   ├── Vehicle.java                ✅ ACTUALIZAR (enums, English)
│   └── Supplier.java               ✅ ACTUALIZAR (English)
│
├── enums/
│   ├── MovementType.java           ✅ NUEVO
│   ├── MeasurementUnit.java        ✅ NUEVO
│   └── VehicleCategory.java        ✅ NUEVO
│
├── services/
│   ├── MovementServices.java       ✅ ACTUALIZAR (logger, enums, English)
│   ├── ProductServices.java        ✅ ACTUALIZAR (logger, enums, English)
│   ├── VehicleServices.java        ✅ ACTUALIZAR (logger, enums, English)
│   └── SupplierServices.java       ✅ ACTUALIZAR (logger, English)
│
├── dao/
│   ├── MovementDAO.java            ✅ ACTUALIZAR (logger, enum conversion)
│   ├── ProductDAO.java             ✅ ACTUALIZAR (logger, enum conversion)
│   ├── VehicleDAO.java             ✅ ACTUALIZAR (logger, enum conversion)
│   └── SupplierDAO.java            ✅ ACTUALIZAR (logger)
│
├── controllers/
│   ├── MovementController.java     ✅ ACTUALIZAR (logger, builders, enums)
│   ├── ProductController.java      ✅ ACTUALIZAR (logger, builders, enums)
│   ├── VehicleController.java      ✅ ACTUALIZAR (logger, builders, enums)
│   ├── SupplierController.java     ✅ ACTUALIZAR (logger, builders)
│   └── ReportController.java       ✅ ACTUALIZAR (logger, enums)
│
└── AppController.java              ✅ ACTUALIZAR (logger)

src/main/resources/
└── logback.xml                     ✅ NUEVO

pom.xml                             ✅ ACTUALIZAR (SLF4J dependencies)
```

---

## 🚀 ORDEN DE EJECUCIÓN RECOMENDADO

1. **Crear Enums** (30 min)
2. **Actualizar Models para usar Enums** (30 min)
3. **Actualizar DAOs para convertir Enums** (20 min)
4. **Compilar y probar enums** (10 min)
5. **Agregar SLF4J al pom.xml** (5 min)
6. **Crear logback.xml** (10 min)
7. **Reemplazar System.out en Services/DAOs** (40 min)
8. **Reemplazar System.out en Controllers** (30 min)
9. **Probar logging** (10 min)
10. **Crear Builders** (40 min)
11. **Actualizar Controllers para usar Builders** (30 min)
12. **Standardizar naming a English** (1 hora)
13. **Compilar y probar todo** (30 min)

**TOTAL: ~5-6 horas**

---

## ⚠️ NOTAS IMPORTANTES

1. **Hacer commits frecuentes** - después de cada tarea mayor
2. **Mantener compatibilidad** - no romper funcionalidad existente
3. **Probar después de cada cambio** - `mvn clean compile`
4. **Revisar logs** - verificar que `logs/forestech.log` se crea
5. **Backup de BD** - antes de migrar columnas (si eliges Opción B en Tarea 12)

---

## 🎯 AL TERMINAR

Ejecuta:
```bash
cd /home/hp/forestechOil/forestech-cli-java

# Compilar
mvn clean compile

# Ejecutar y verificar logs
mvn exec:java

# Verificar archivo de log
cat logs/forestech.log

# Tests (si existen)
mvn test
```

Confirma que:
- ✅ 0 errores de compilación
- ✅ 0 `System.out.println` en código de negocio
- ✅ Logs aparecen en consola Y archivo
- ✅ Builders funcionan correctamente
- ✅ Enums previenen valores inválidos
- ✅ Naming consistente (inglés)
- ✅ `logs/forestech.log` se genera con formato correcto

---

**¡Adelante con la refactorización final! 🚀**
