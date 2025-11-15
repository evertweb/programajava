# DATABASE SCHEMA REFERENCE - FORESTECHOIL
> **IMPORTANTE:** Este archivo se actualiza automáticamente consultando la base de datos real.
> Última actualización: 2025-11-14

## 🔗 CLAVES FORÁNEAS ACTUALES (FOREIGN KEYS)

| Tabla Hija       | Columna FK        | Tabla Padre       | Columna Padre | Relación |
|------------------|-------------------|-------------------|---------------|----------|
| **Movement**     | product_id        | oil_products      | id            | N:1      |
| **Movement**     | vehicle_id        | vehicles          | id            | N:1      |
| **Movement**     | numero_factura    | facturas          | numero_factura| N:1      |
| **detalle_factura** | numero_factura | facturas          | numero_factura| N:1      |
| **facturas**     | supplier_id       | suppliers         | id            | N:1      |
| **vehicles**     | fuel_product_id   | oil_products      | id            | N:1      |

---

## 📊 ESTRUCTURA COMPLETA DE TABLAS

### 1. **oil_products** (Tabla maestra sin dependencias)
| Columna        | Tipo          | Nulo | Llave | Default |
|----------------|---------------|------|-------|---------|
| id             | varchar(255)  | NO   | PRI   | NULL    |
| name           | varchar(100)  | NO   | MUL   | NULL    |
| unidadDeMedida | varchar(15)   | NO   | MUL   | NULL    |
| priceXUnd      | decimal(10,2) | NO   |       | NULL    |

**Formato ID:** `FUE-XXXXXXXX` (generado por Java)

---

### 2. **suppliers** (Tabla maestra sin dependencias)
| Columna     | Tipo          | Nulo | Llave | Default             |
|-------------|---------------|------|-------|---------------------|
| id          | varchar(255)  | NO   | PRI   | NULL                |
| name        | varchar(150)  | NO   | MUL   | NULL                |
| nit         | varchar(20)   | NO   | UNI   | NULL                |
| telephone   | varchar(20)   | YES  |       | NULL                |
| email       | varchar(100)  | YES  |       | NULL                |
| address     | varchar(255)  | YES  |       | NULL                |
| created_at  | timestamp     | YES  |       | CURRENT_TIMESTAMP   |

**Formato ID:** `SUP-XXXXXXXX` (generado por Java)
**Restricción:** `nit` es ÚNICO

---

### 3. **vehicles** (Depende de: oil_products)
| Columna          | Tipo         | Nulo | Llave | Default | FK              |
|------------------|--------------|------|-------|---------|-----------------|
| id               | varchar(255) | NO   | PRI   | NULL    |                 |
| name             | varchar(50)  | NO   | MUL   | NULL    |                 |
| category         | varchar(255) | YES  | MUL   | NULL    |                 |
| capacity         | double       | YES  |       | NULL    |                 |
| fuelType         | varchar(10)  | YES  |       | NULL    | (DEPRECADO)     |
| fuel_product_id  | varchar(255) | YES  | MUL   | NULL    | → oil_products.id |
| haveHorometer    | tinyint(1)   | YES  |       | 0       |                 |

**Formato ID:** `VEH-XXXXXXXX` (generado por Java)
**FK:** `fuel_product_id` → `oil_products.id`

---

### 4. **facturas** (Depende de: suppliers)
| Columna            | Tipo          | Nulo | Llave | Default | FK               |
|--------------------|---------------|------|-------|---------|------------------|
| numero_factura     | varchar(20)   | NO   | PRI   | NULL    |                  |
| supplier_id        | varchar(255)  | YES  | MUL   | NULL    | → suppliers.id   |
| fecha_emision      | date          | NO   | MUL   | NULL    |                  |
| fecha_vencimiento  | date          | NO   |       | NULL    |                  |
| cliente_nombre     | varchar(150)  | YES  |       | NULL    | (DEPRECADO)      |
| cliente_nit        | varchar(20)   | YES  | MUL   | NULL    | (DEPRECADO)      |
| subtotal           | decimal(12,2) | NO   |       | NULL    |                  |
| iva                | decimal(12,2) | NO   |       | NULL    |                  |
| total              | decimal(12,2) | NO   |       | NULL    |                  |
| observaciones      | text          | YES  |       | NULL    |                  |
| forma_pago         | varchar(50)   | YES  |       | NULL    |                  |
| cuenta_bancaria    | varchar(50)   | YES  |       | NULL    |                  |

**PK:** `numero_factura` (formato libre, ej: "FACT-001")
**FK:** `supplier_id` → `suppliers.id`
**Columnas deprecadas:** `cliente_nombre`, `cliente_nit` (usar `supplier_id`)

---

### 5. **Movement** (Depende de: oil_products, vehicles, facturas)
| Columna          | Tipo          | Nulo | Llave | Default             | FK                      |
|------------------|---------------|------|-------|---------------------|-------------------------|
| id               | varchar(15)   | NO   | PRI   | NULL                |                         |
| movementType     | varchar(10)   | NO   | MUL   | NULL                |                         |
| productType      | varchar(100)  | YES  |       | NULL                | (DEPRECADO)             |
| product_id       | varchar(255)  | YES  | MUL   | NULL                | → oil_products.id       |
| vehicle_id       | varchar(255)  | YES  | MUL   | NULL                | → vehicles.id           |
| numero_factura   | varchar(20)   | YES  | MUL   | NULL                | → facturas.numero_factura |
| unidadDeMedida   | varchar(50)   | NO   |       | NULL                |                         |
| quantity         | decimal(10,2) | NO   |       | NULL                |                         |
| unitPrice        | decimal(10,2) | NO   |       | NULL                |                         |
| movementDate     | datetime      | NO   | MUL   | CURRENT_TIMESTAMP   |                         |

**Formato ID:** `MOV-XXXXXXXX` (generado por Java)
**FKs:**
- `product_id` → `oil_products.id`
- `vehicle_id` → `vehicles.id` (solo para SALIDA)
- `numero_factura` → `facturas.numero_factura` (solo para ENTRADA)

**Columna deprecada:** `productType` (usar `product_id`)

---

### 6. **detalle_factura** (Depende de: facturas)
| Columna          | Tipo          | Nulo | Llave | Default | FK                      |
|------------------|---------------|------|-------|---------|-------------------------|
| id_detalle       | int           | NO   | PRI   | NULL    | (AUTO_INCREMENT)        |
| numero_factura   | varchar(20)   | NO   | MUL   | NULL    | → facturas.numero_factura |
| producto         | varchar(200)  | NO   |       | NULL    |                         |
| cantidad         | decimal(10,2) | NO   |       | NULL    |                         |
| precio_unitario  | decimal(12,2) | NO   |       | NULL    |                         |

**PK:** `id_detalle` (autoincremental)
**FK:** `numero_factura` → `facturas.numero_factura`

---

## 🛡️ REGLAS DE INTEGRIDAD REFERENCIAL

### ❌ RESTRICT (No se puede eliminar si tiene referencias)
- **oil_products:** No se puede eliminar si hay `Movement` o `vehicles` que lo referencian
- **suppliers:** No se puede eliminar si hay `facturas` que lo referencian

### ✅ SET NULL (Se permite eliminar, FK queda en NULL)
- **vehicles:** Si se elimina, `Movement.vehicle_id` se pone en NULL
- **facturas:** Si se elimina, `Movement.numero_factura` se pone en NULL

### 🔄 CASCADE (Actualización automática)
- **ON UPDATE CASCADE:** Todas las FKs se actualizan automáticamente si cambia el ID padre

### 🗑️ CASCADE (Eliminación en cascada)
- **detalle_factura:** Si se elimina una factura, se eliminan automáticamente todos sus detalles

---

## ⚠️ VALIDACIONES CRÍTICAS EN JAVA

### Antes de insertar un `Movement`:
```java
// 1. Validar que product_id existe en oil_products
Product product = productService.findById(productId);
if (product == null) {
    throw new IllegalArgumentException("Producto no existe: " + productId);
}

// 2. Si es SALIDA, validar que vehicle_id existe
if (movementType.equals("SALIDA") && vehicleId != null) {
    Vehicle vehicle = vehicleService.findById(vehicleId);
    if (vehicle == null) {
        throw new IllegalArgumentException("Vehículo no existe: " + vehicleId);
    }
}

// 3. Si es ENTRADA, validar que numero_factura existe
if (movementType.equals("ENTRADA") && numeroFactura != null) {
    Factura factura = facturaService.findByNumero(numeroFactura);
    if (factura == null) {
        throw new IllegalArgumentException("Factura no existe: " + numeroFactura);
    }
}
```

### Antes de insertar un `Vehicle`:
```java
// Validar que fuel_product_id existe en oil_products
if (fuelProductId != null) {
    Product product = productService.findById(fuelProductId);
    if (product == null) {
        throw new IllegalArgumentException("Producto de combustible no existe: " + fuelProductId);
    }
}
```

### Antes de insertar una `Factura`:
```java
// Validar que supplier_id existe en suppliers
if (supplierId != null) {
    Supplier supplier = supplierService.findById(supplierId);
    if (supplier == null) {
        throw new IllegalArgumentException("Proveedor no existe: " + supplierId);
    }
}
```

---

## 📝 COMANDOS ÚTILES PARA VERIFICACIÓN

### Verificar FKs desde MySQL:
```bash
mysql -u root -p'hp' -e "
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'FORESTECHOIL'
  AND REFERENCED_TABLE_NAME IS NOT NULL;"
```

### Ver estructura de una tabla:
```bash
mysql -u root -p'hp' -e "DESCRIBE FORESTECHOIL.Movement;"
```

---

## 🎯 REGLA DE ORO

**SIEMPRE consulta la base de datos real con MySQL, NO confíes en archivos .sql antiguos.**

Esta documentación se genera desde la base de datos real y debe actualizarse cuando cambien las tablas.
