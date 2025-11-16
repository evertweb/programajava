# 🟠 HIGH PRIORITY REFACTORING - Forestech CLI

## 📋 CONTEXTO

Proyecto: **Forestech CLI** - Sistema de gestión de combustibles en Java 17 + Maven + MySQL
Branch: `modularizacion-con-raptor-mini`
Estado actual: **Refactoring URGENT completado (DAO layer, HikariCP, Controllers divididos)**

---

## 🎯 TU MISIÓN: Completar 4 tareas HIGH PRIORITY

### ✅ YA COMPLETADO (NO TOCAR):
- DAO Layer (GenericDAO + 4 DAOs)
- HikariCP connection pooling
- AppController dividido en 5 controllers
- Services refactorizados usando DAOs

### ⚠️ TU TRABAJO (4 tareas HIGH):

---

## 📦 TAREA 5: Completar MovementCalculator Service (30 min)

### Problema Actual:
El modelo `Movement.java` tiene **lógica de negocio** mezclada con datos:
```java
// ❌ Movement.java contiene cálculos (MALO):
public double getTotalValue() {
    return this.quantity * this.unitPrice;
}

public double getIva() {
    return getSubtotalvalue() * AppConfig.IVA_RATE;
}

public double getTotalWithIva() {
    return getSubtotalvalue() + getIva();
}
```

### Solución Requerida:
1. **Mover toda la lógica de cálculo** de `Movement.java` a `MovementCalculator.java`
2. **Dejar Movement como POJO puro** (solo atributos + getters/setters)
3. **Actualizar `MovementCalculator.java`** con métodos modernos:

```java
package com.forestech.services;

import com.forestech.models.Movement;
import com.forestech.AppConfig;

/**
 * Servicio encargado de cálculos de negocio para movimientos.
 * Separación de responsabilidades: Movement = datos, MovementCalculator = lógica.
 */
public class MovementCalculator {
    
    /**
     * Calcula el subtotal (cantidad × precio unitario).
     */
    public static double calculateSubtotal(Movement movement) {
        return movement.getQuantity() * movement.getUnitPrice();
    }
    
    /**
     * Calcula el IVA sobre un movimiento.
     */
    public static double calculateIVA(Movement movement) {
        return calculateSubtotal(movement) * AppConfig.IVA_RATE;
    }
    
    /**
     * Calcula el total con IVA incluido.
     */
    public static double calculateTotalWithIVA(Movement movement) {
        double subtotal = calculateSubtotal(movement);
        double iva = calculateIVA(movement);
        return subtotal + iva;
    }
    
    /**
     * Valida si un movimiento es válido.
     */
    public static boolean isValidMovement(Movement movement) {
        return movement.getQuantity() > 0 && 
               movement.getUnitPrice() > 0 &&
               movement.getProductId() != null;
    }
    
    /**
     * Determina si es una compra grande (>= 100 unidades).
     */
    public static boolean isBigPurchase(Movement movement) {
        return movement.getQuantity() >= 100;
    }
    
    /**
     * Determina si requiere aprobación gerencial.
     */
    public static boolean requiresApproval(Movement movement) {
        double total = calculateTotalWithIVA(movement);
        return movement.getMovementType().equals("SALIDA") && 
               (movement.getQuantity() > 100 || total > 500);
    }
}
```

4. **Actualizar todos los lugares que usen los métodos eliminados**:
   - Buscar: `movement.getTotalValue()`
   - Reemplazar por: `MovementCalculator.calculateTotalWithIVA(movement)`

---

## 📦 TAREA 6: Crear Validation Layer (1 hora)

### Problema Actual:
Las validaciones están **esparcidas** en Services, Controllers y Models.

### Solución Requerida:
Crear paquete `validators/` con validadores especializados:

```java
package com.forestech.validators;

import com.forestech.models.Movement;
import com.forestech.exceptions.ValidationException;

/**
 * Validador centralizado para movimientos.
 */
public class MovementValidator {
    
    /**
     * Valida un movimiento antes de insertar/actualizar.
     * @throws ValidationException si hay errores de validación
     */
    public static void validate(Movement movement) throws ValidationException {
        validateNotNull(movement);
        validateProductId(movement.getProductId());
        validateQuantity(movement.getQuantity());
        validateUnitPrice(movement.getUnitPrice());
        validateMovementType(movement.getMovementType());
        validateBusinessRules(movement);
    }
    
    private static void validateNotNull(Movement movement) throws ValidationException {
        if (movement == null) {
            throw new ValidationException("Movement cannot be null");
        }
    }
    
    private static void validateProductId(String productId) throws ValidationException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new ValidationException("Product ID is required");
        }
        if (!productId.startsWith("FUE-")) {
            throw new ValidationException("Invalid Product ID format. Must start with FUE-");
        }
    }
    
    private static void validateQuantity(double quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }
        if (quantity > 10000) {
            throw new ValidationException("Quantity too large. Maximum: 10,000 units");
        }
    }
    
    private static void validateUnitPrice(double unitPrice) throws ValidationException {
        if (unitPrice <= 0) {
            throw new ValidationException("Unit price must be greater than 0");
        }
    }
    
    private static void validateMovementType(String type) throws ValidationException {
        if (type == null || (!type.equals("ENTRADA") && !type.equals("SALIDA"))) {
            throw new ValidationException("Movement type must be ENTRADA or SALIDA");
        }
    }
    
    private static void validateBusinessRules(Movement movement) throws ValidationException {
        // ENTRADA debe tener numeroFactura
        if (movement.getMovementType().equals("ENTRADA") && movement.getNumeroFactura() == null) {
            throw new ValidationException("ENTRADA movements require invoice number");
        }
        
        // SALIDA debe tener vehicleId
        if (movement.getMovementType().equals("SALIDA") && movement.getVehicleId() == null) {
            throw new ValidationException("SALIDA movements require vehicle ID");
        }
    }
}
```

**Crear también:**
- `ProductValidator.java` (valida name, precio, unidad de medida)
- `VehicleValidator.java` (valida name, category, capacity)
- `SupplierValidator.java` (valida NIT, name, email)

**Crear excepción personalizada:**
```java
package com.forestech.exceptions;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
```

**Actualizar Services** para usar validadores:
```java
// En MovementServices.insertMovement():
public static void insertMovement(Movement movement) throws DatabaseException, ValidationException {
    // Validar primero
    MovementValidator.validate(movement);
    
    // Luego insertar
    movementDAO.insert(movement);
}
```

---

## 📦 TAREA 7: Crear Service Interfaces (30 min)

### Problema Actual:
Services no tienen interfaces, dificulta testing y desacoplamiento.

### Solución Requerida:
Crear paquete `services/interfaces/`:

```java
package com.forestech.services.interfaces;

import com.forestech.exceptions.DatabaseException;
import com.forestech.exceptions.ValidationException;
import com.forestech.models.Movement;
import java.util.List;

/**
 * Interfaz para servicios de movimientos.
 * Permite testing con mocks y desacoplamiento.
 */
public interface IMovementService {
    
    void insertMovement(Movement movement) throws DatabaseException, ValidationException;
    
    List<Movement> getAllMovements() throws DatabaseException;
    
    Movement getMovementById(String movementId) throws DatabaseException;
    
    List<Movement> getMovementsByType(String type) throws DatabaseException;
    
    List<Movement> getMovementsByProduct(String productId) throws DatabaseException;
    
    List<Movement> getMovementsByVehicle(String vehicleId) throws DatabaseException;
    
    double getProductStock(String productId) throws DatabaseException;
    
    void updateMovement(String id, double newQuantity, double newUnitPrice) throws DatabaseException;
    
    boolean deleteMovement(String movementId) throws DatabaseException;
}
```

**Hacer que Services implementen interfaces:**
```java
public class MovementServices implements IMovementService {
    // ... implementación existente
}
```

**Crear también:**
- `IProductService.java`
- `IVehicleService.java`
- `ISupplierService.java`

---

## 📦 TAREA 8: Environment Variables (15 min)

### Problema Actual:
Credenciales hardcodeadas en `HikariCPDataSource.java`:
```java
config.setJdbcUrl("jdbc:mysql://localhost:3306/FORESTECHOIL");
config.setUsername("root");
config.setPassword("hp");
```

### Solución Requerida:

**1. Crear archivo `src/main/resources/application.properties`:**
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/FORESTECHOIL
db.username=root
db.password=hp

# HikariCP Pool Configuration
hikari.minimum-idle=5
hikari.maximum-pool-size=20
hikari.connection-timeout=30000
hikari.idle-timeout=600000
hikari.max-lifetime=1800000
```

**2. Crear clase para leer propiedades:**
```java
package com.forestech.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carga configuración desde application.properties.
 */
public class ConfigLoader {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }
    
    public static String get(String key) {
        return properties.getProperty(key);
    }
    
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
```

**3. Actualizar `HikariCPDataSource.java`:**
```java
config.setJdbcUrl(ConfigLoader.get("db.url"));
config.setUsername(ConfigLoader.get("db.username"));
config.setPassword(ConfigLoader.get("db.password"));
config.setMinimumIdle(Integer.parseInt(ConfigLoader.get("hikari.minimum-idle", "5")));
config.setMaximumPoolSize(Integer.parseInt(ConfigLoader.get("hikari.maximum-pool-size", "20")));
```

**4. Crear `.env.example`:**
```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/FORESTECHOIL
DB_USERNAME=root
DB_PASSWORD=your_password_here
```

---

## ✅ CRITERIOS DE ACEPTACIÓN

Al terminar estas 4 tareas, debes poder:

1. ✅ `Movement.java` NO tiene métodos de cálculo (solo getters/setters)
2. ✅ `MovementCalculator` maneja TODOS los cálculos
3. ✅ Existen 4 validadores en `validators/`
4. ✅ Todas las validaciones están centralizadas
5. ✅ Existen 4 interfaces en `services/interfaces/`
6. ✅ Services implementan sus interfaces
7. ✅ `application.properties` existe con configuración
8. ✅ `ConfigLoader` carga propiedades correctamente
9. ✅ NO hay credenciales hardcodeadas en código
10. ✅ Proyecto compila con `mvn clean compile`

---

## 📁 ESTRUCTURA FINAL ESPERADA

```
src/main/java/com/forestech/
├── validators/
│   ├── MovementValidator.java    ✅ NUEVO
│   ├── ProductValidator.java     ✅ NUEVO
│   ├── VehicleValidator.java     ✅ NUEVO
│   └── SupplierValidator.java    ✅ NUEVO
│
├── services/
│   ├── interfaces/
│   │   ├── IMovementService.java ✅ NUEVO
│   │   ├── IProductService.java  ✅ NUEVO
│   │   ├── IVehicleService.java  ✅ NUEVO
│   │   └── ISupplierService.java ✅ NUEVO
│   ├── MovementServices.java     ✅ ACTUALIZAR (implements IMovementService)
│   ├── ProductServices.java      ✅ ACTUALIZAR (implements IProductService)
│   ├── VehicleServices.java      ✅ ACTUALIZAR (implements IVehicleService)
│   └── SupplierServices.java     ✅ ACTUALIZAR (implements ISupplierService)
│
├── config/
│   ├── ConfigLoader.java         ✅ NUEVO
│   └── HikariCPDataSource.java   ✅ ACTUALIZAR (usar ConfigLoader)
│
├── exceptions/
│   └── ValidationException.java  ✅ NUEVO
│
└── MovementCalculator.java       ✅ ACTUALIZAR (métodos modernos)

src/main/resources/
└── application.properties         ✅ NUEVO
```

---

## 🚀 ORDEN DE EJECUCIÓN RECOMENDADO

1. **Crear ValidationException** (5 min)
2. **Completar MovementCalculator** (30 min)
3. **Crear ConfigLoader + application.properties** (15 min)
4. **Actualizar HikariCPDataSource** (10 min)
5. **Crear 4 Validators** (40 min)
6. **Crear 4 Service Interfaces** (20 min)
7. **Actualizar Services para implementar interfaces** (20 min)
8. **Compilar y probar** (20 min)

**TOTAL: ~2.5 horas**

---

## ⚠️ NOTAS IMPORTANTES

1. **NO toques DAOs, Controllers o AppController** - ya están refactorizados
2. **Mantén compatibilidad** - no rompas funcionalidad existente
3. **Compila frecuentemente** - `mvn clean compile` después de cada tarea
4. **Usa los mismos estilos** - sigue convenciones del proyecto
5. **Documenta con JavaDoc** - todas las clases públicas

---

## 🎯 AL TERMINAR

Ejecuta:
```bash
cd /home/hp/forestechOil/forestech-cli-java
mvn clean compile
mvn test  # Si hay tests
```

Y confirma que:
- ✅ 0 errores de compilación
- ✅ Validadores funcionan
- ✅ Services usan validadores
- ✅ Configuración se carga desde properties
- ✅ MovementCalculator maneja todos los cálculos

---

**¡Manos a la obra! 🚀**
