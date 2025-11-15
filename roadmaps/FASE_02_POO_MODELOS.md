# FASE 02: PROGRAMACIÓN ORIENTADA A OBJETOS - MODELOS DE DOMINIO
**Roadmap Retrospectivo - Análisis del Código Existente**

---

## Contexto de esta Fase

Esta fase documenta el corazón del proyecto ForestechOil: las **clases de dominio** que representan las entidades del negocio. Este roadmap analiza **tu implementación real** de:

- ✅ **Movement** - Movimientos de combustible (ENTRADA/SALIDA)
- ✅ **Product** - Catálogo de productos (combustibles)
- ✅ **Vehicle** - Flota de vehículos
- ✅ **Supplier** - Proveedores
- ✅ **Factura** y **DetalleFactura** - Sistema de facturación

Exploraremos cómo aplicaste conceptos de POO:
- **Encapsulación** (private fields + getters/setters)
- **Inmutabilidad** (final fields)
- **Sobrecarga de constructores** (CREATE vs LOAD)
- **Validaciones** en setters
- **Métodos de negocio** (cálculos de IVA, subtotales)
- **Foreign Keys** para relaciones entre entidades

---

## Objetivos de Aprendizaje

Al analizar esta fase, reforzarás:

1. **Clases y objetos** (blueprint vs instance)
2. **Encapsulación** (private + public)
3. **Constructores sobrecargados** (múltiples firmas)
4. **Fields final** (inmutabilidad)
5. **Getters y setters** (acceso controlado)
6. **Validación de datos** (business rules)
7. **Foreign Keys** (relaciones entre entidades)
8. **Override toString()** (representación textual)
9. **@NotNull annotations** (documentación de contratos)
10. **Métodos de cálculo** (lógica de negocio en el modelo)

---

## Arquitectura de Modelos

```
com.forestech.models/
├── Movement.java          # Entidad principal: movimientos de combustible
│   ├── Foreign Keys: productId, vehicleId, numeroFactura
│   └── Métodos de negocio: getSubtotalvalue(), getIva(), getTotalWithIva()
│
├── Product.java           # Catálogo de productos (combustibles)
│   └── Referenciado por: Movement, Vehicle
│
├── Vehicle.java           # Flota de vehículos
│   ├── Foreign Key: fuelProductId
│   └── Referenciado por: Movement
│
├── Supplier.java          # Proveedores de combustible
│   └── Referenciado por: Factura
│
├── Factura.java           # Cabecera de facturas
│   ├── Foreign Key: supplierId
│   └── Relacionado con: DetalleFactura (1:N)
│
└── DetalleFactura.java    # Líneas de detalle de factura
    └── Foreign Key: numeroFactura
```

---

## 1. CLASE MOVEMENT - NÚCLEO DEL SISTEMA

### Archivo: `models/Movement.java`

#### 1.1 Concepto: ¿Qué es un Movement?

Un **Movement** representa una transacción de combustible:
- **ENTRADA:** Compra de combustible a proveedores (registrada con factura)
- **SALIDA:** Consumo de combustible por vehículos

Es la entidad **más importante** del sistema porque:
- Controla el inventario (stock = ENTRADAS - SALIDAS)
- Registra el historial de consumo por vehículo
- Vincula facturas de compra con productos

---

#### 1.2 Análisis de Atributos (Líneas 11-23)

```java
// ============================================================================
// ATRIBUTOS
// ============================================================================

private final String id;
private String movementType;
private String productId;        // FK → oil_products.id
private String vehicleId;        // FK → vehicles.id (NULL para ENTRADA)
private String numeroFactura;    // FK → facturas.numero_factura (NULL para SALIDA)
private String unidadDeMedida;
private double quantity;
private double unitPrice;
private final String movementDate;
```

**Decisiones de diseño clave:**

1. **`private final String id;`**
   - **`private`:** No se puede acceder directamente desde fuera de la clase
   - **`final`:** No se puede modificar después de la asignación (inmutable)
   - **Razón:** El ID identifica únicamente al movimiento y nunca debe cambiar

2. **`private final String movementDate;`**
   - **`final`:** La fecha de creación no puede modificarse (auditoría)
   - **Tipo `String`:** Almacena el `LocalDateTime.now().toString()` para facilidad de JDBC

3. **Foreign Keys opcionales:**
   - **`vehicleId`:** Solo para SALIDAS (indica qué vehículo consumió)
   - **`numeroFactura`:** Solo para ENTRADAS (vincula con la factura de compra)

**Tabla de validez de Foreign Keys:**

| Tipo de Movement | vehicleId | numeroFactura |
|------------------|-----------|---------------|
| ENTRADA          | NULL      | NOT NULL      |
| SALIDA           | NOT NULL  | NULL          |

---

#### 1.3 Constructor CREATE (Líneas 29-52)

**Propósito:** Crear movimientos **nuevos** (genera ID automáticamente)

```java
/**
 * Constructor para CREAR nuevos movimientos (genera ID automático).
 * Usa productId (FK) que apunta a oil_products.id
 *
 * @param movementType   Tipo: "ENTRADA" o "SALIDA"
 * @param productId      FK → oil_products.id (obligatorio)
 * @param vehicleId      FK → vehicles.id (opcional, NULL para ENTRADA)
 * @param numeroFactura  FK → facturas.numero_factura (opcional, NULL para SALIDA)
 * @param unidadDeMedida Unidad: GALON, GARRAFA, CUARTO, CANECA
 * @param quantity       Cantidad movida (debe ser > 0)
 * @param unitPrice      Precio unitario
 */
public Movement(String movementType, String productId, String vehicleId, String numeroFactura,
                String unidadDeMedida, double quantity, double unitPrice) {
    this.id = IdGenerator.generateMovementId();  // ← ID AUTO-GENERADO
    this.movementType = movementType;
    this.productId = productId;
    this.vehicleId = vehicleId;
    this.numeroFactura = numeroFactura;
    this.unidadDeMedida = unidadDeMedida;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.movementDate = LocalDateTime.now().toString();  // ← TIMESTAMP AUTO
}
```

**Análisis línea por línea:**

**Línea 43:** `this.id = IdGenerator.generateMovementId();`
- Llama al generador de IDs (Fase 01)
- Resultado: `MOV-A3F2C1D4`
- **`this.id`:** Asigna al field `id` de ESTA instancia

**Línea 51:** `this.movementDate = LocalDateTime.now().toString();`
- `LocalDateTime.now()`: Obtiene fecha/hora actual (ej: 2025-01-14T15:30:45.123)
- `.toString()`: Convierte a String para JDBC
- Resultado: `"2025-01-14T15:30:45.123456"`

**¿Cuándo usar este constructor?**

En **AppController.java** al registrar movimientos:

```java
// Ejemplo de uso (AppController.java:145-155)
Movement newMovement = new Movement(
    "ENTRADA",                 // movementType
    selectedProductId,         // productId (FK)
    null,                      // vehicleId (NULL porque es ENTRADA)
    numeroFactura,             // numeroFactura (asociada a la compra)
    "GALON",                   // unidadDeMedida
    15.5,                      // quantity
    12000.0                    // unitPrice
);
```

---

#### 1.4 Constructor LOAD (Líneas 54-80)

**Propósito:** Cargar movimientos **existentes** desde la base de datos (usa ID ya existente)

```java
/**
 * Constructor para CARGAR desde la base de datos (usa ID existente).
 * Este constructor es usado por MovementServices al hacer SELECT.
 *
 * @param id             ID existente del movimiento
 * @param movementType   Tipo: "ENTRADA" o "SALIDA"
 * @param productId      FK → oil_products.id
 * @param vehicleId      FK → vehicles.id (puede ser NULL)
 * @param numeroFactura  FK → facturas.numero_factura (puede ser NULL)
 * @param unidadDeMedida Unidad de medida
 * @param quantity       Cantidad
 * @param unitPrice      Precio unitario
 * @param movementDate   Fecha del movimiento (String)
 */
public Movement(String id, String movementType, String productId,
                String vehicleId, String numeroFactura, String unidadDeMedida,
                double quantity, double unitPrice, String movementDate) {
    this.id = id;  // ← USA EL ID EXISTENTE (no genera nuevo)
    this.movementType = movementType;
    this.productId = productId;
    this.vehicleId = vehicleId;
    this.numeroFactura = numeroFactura;
    this.unidadDeMedida = unidadDeMedida;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.movementDate = movementDate;  // ← USA LA FECHA EXISTENTE
}
```

**Diferencia clave con Constructor CREATE:**

| Constructor | ID | movementDate | Uso |
|-------------|----|--------------|----|
| CREATE (7 params) | Auto-generado | `LocalDateTime.now()` | Crear nuevos movimientos |
| LOAD (9 params) | Pasado como parámetro | Pasado como parámetro | Cargar desde BD |

**¿Cuándo usar este constructor?**

En **MovementServices.java** al hacer SELECT:

```java
// Ejemplo de uso (MovementServices.java:75-83)
private Movement mapResultSetToMovement(ResultSet rs) throws SQLException {
    return new Movement(
        rs.getString("id"),              // ← ID existente de la BD
        rs.getString("movementType"),
        rs.getString("product_id"),
        rs.getString("vehicle_id"),
        rs.getString("numero_factura"),
        rs.getString("unidadDeMedida"),
        rs.getDouble("quantity"),
        rs.getDouble("unitPrice"),
        rs.getString("movementDate")     // ← Fecha existente de la BD
    );
}
```

---

#### 1.5 Constructor Vacío (Líneas 82-95)

**Propósito:** Testing o inicialización de herramientas

```java
/**
 * Constructor vacío para herramientas/testing.
 */
public Movement() {
    this.id = IdGenerator.generateMovementId();
    this.movementDate = LocalDateTime.now().toString();
    this.movementType = null;
    this.productId = null;
    this.vehicleId = null;
    this.numeroFactura = null;
    this.unidadDeMedida = null;
    this.quantity = 0.0;
    this.unitPrice = 0.0;
}
```

**Nota:** Este constructor genera ID y fecha, pero deja los demás campos en `null` o `0.0`.

**Uso típico:** Frameworks de testing que requieren constructor sin parámetros.

---

#### 1.6 Getters y Setters - Encapsulación

**Concepto: ¿Por qué no hacer los fields públicos?**

❌ **Mala práctica:**
```java
public String movementType;  // Acceso directo sin control

// En cualquier parte del código:
movement.movementType = "COMPRA";  // ¡Error! No es un tipo válido
```

✅ **Buena práctica:**
```java
private String movementType;  // Field privado

public void setMovementType(String movementType) {
    if (movementType.equals("ENTRADA") || movementType.equals("SALIDA")) {
        this.movementType = movementType;
    } else {
        System.out.println("TIPO DE MOVIMIENTO NO VALIDO");
    }
}

// En cualquier parte del código:
movement.setMovementType("COMPRA");  // Muestra error: "TIPO DE MOVIMIENTO NO VALIDO"
```

**Beneficios de la encapsulación:**
1. **Validación centralizada** (un solo lugar para la lógica)
2. **Protección de invariantes** (reglas de negocio se respetan)
3. **Flexibilidad futura** (puedes cambiar la implementación interna sin afectar el código externo)

---

**Análisis de setMovementType() - Líneas 110-116:**

```java
public void setMovementType(@NotNull String movementType) {
    if (movementType.equals("ENTRADA") || movementType.equals("SALIDA")) {
        this.movementType = movementType;
    } else {
        System.out.println("TIPO DE MOVIMIENTO NO VALIDO");
    }
}
```

**Línea 110:** `@NotNull`
- Anotación de JetBrains (no es validación en runtime)
- **Documentación de contrato:** Indica que este parámetro no debe ser null
- El IDE advertirá si intentas pasar `null`

**Líneas 111-112:** Validación
- Solo acepta `"ENTRADA"` o `"SALIDA"`
- Cualquier otro valor se rechaza

**⚠️ Oportunidad de mejora:**
```java
// Actualmente solo imprime mensaje, pero NO lanza excepción
// Mejor práctica:
if (!movementType.equals("ENTRADA") && !movementType.equals("SALIDA")) {
    throw new IllegalArgumentException("Tipo inválido: " + movementType);
}
```

---

**Análisis de setUnidadDeMedida() - Líneas 181-191:**

```java
public void setUnidadDeMedida(String unidad) {
    // Validar que sea una unidad válida
    if (unidad.equals("GALON") ||
        unidad.equals("GARRAFA") ||
        unidad.equals("CUARTO") ||
        unidad.equals("CANECA")) {
        this.unidadDeMedida = unidad;
    } else {
        throw new IllegalArgumentException("Unidad de medida no válida: " + unidad);
    }
}
```

**Mejor implementación:** Este setter SÍ lanza excepción (coherente con buenas prácticas).

**📌 Recomendación de refactorización futura:**
```java
// Crear un ENUM en lugar de Strings (Fase 9)
public enum UnidadMedida {
    GALON, GARRAFA, CUARTO, CANECA
}

private UnidadMedida unidadDeMedida;

public void setUnidadDeMedida(UnidadMedida unidad) {
    this.unidadDeMedida = unidad;  // ¡No necesita validación!
}
```

---

**Análisis de setQuantity() - Líneas 197-205:**

```java
public void setQuantity(double quantity) {
    if (quantity > 0) {
        this.quantity = quantity;
    } else {
        System.out.println("CANTIDAD NO VALIDA");
    }
}
```

**Regla de negocio:** No se permiten cantidades negativas o cero.

**Casos de uso:**
- `setQuantity(15.5)` → ✅ Asigna 15.5
- `setQuantity(-5)` → ❌ Imprime error
- `setQuantity(0)` → ❌ Imprime error

---

#### 1.7 Métodos de Negocio - Cálculos de IVA

**Concepto:** Los modelos no son solo "contenedores de datos", también tienen **lógica de negocio**.

**Líneas 224-236: Métodos de cálculo**

```java
public double getSubtotalvalue() {
    return this.quantity * this.unitPrice;
}

public double getIva() {
    return getSubtotalvalue() * AppConfig.IVA_RATE;
}

public double getTotalWithIva() {
    return getSubtotalvalue() + getIva();
}
```

**Análisis detallado:**

**1. getSubtotalvalue():**
- Calcula: cantidad × precio unitario
- Ejemplo: 10 galones × $12,000 = $120,000

**2. getIva():**
- Calcula: subtotal × 19%
- Usa la constante `AppConfig.IVA_RATE` (Fase 01)
- Ejemplo: $120,000 × 0.19 = $22,800

**3. getTotalWithIva():**
- Calcula: subtotal + IVA
- Ejemplo: $120,000 + $22,800 = $142,800

**Ventaja del diseño:**
```java
// Sin métodos de negocio (código repetitivo en todas partes):
double subtotal = movement.getQuantity() * movement.getUnitPrice();
double iva = subtotal * 0.19;  // ❌ Magic number
double total = subtotal + iva;

// Con métodos de negocio (una línea, sin duplicación):
double total = movement.getTotalWithIva();  // ✅ Claro y reutilizable
```

**Uso en AppController.java:**
```java
// Ejemplo (AppController.java:158)
System.out.println("Subtotal: $" + newMovement.getSubtotalvalue());
System.out.println("IVA (19%): $" + newMovement.getIva());
System.out.println("TOTAL: $" + newMovement.getTotalWithIva());
```

---

#### 1.8 Foreign Keys - Relaciones entre Entidades

**Líneas 118-174: Getters y setters para llaves foráneas**

```java
/**
 * Obtiene el ID del producto asociado a este movimiento.
 *
 * @return ID del producto (FK → oil_products.id)
 */
public String getProductId() {
    return productId;
}

/**
 * Establece el ID del producto para este movimiento.
 *
 * @param productId ID del producto (debe existir en oil_products)
 */
public void setProductId(String productId) {
    this.productId = productId;
}
```

**Concepto: Foreign Key (Llave Foránea)**

Una FK es un campo que **referencia** el ID de otra tabla:

```
Movement                    Product
┌────────────────┐         ┌────────────────┐
│ id             │         │ id             │◄────┐
│ productId      │─────────┼────────────────┘     │
│ quantity       │         │ name           │     │
│ ...            │         │ price          │     │
└────────────────┘         └────────────────┘     │
                                                  │
                           Movement.productId hace referencia a Product.id
```

**Integridad referencial:**
- No puedes crear un Movement con `productId = "FUEL-XXXX"` si ese producto no existe en la tabla `oil_products`
- La base de datos lo rechazará con un error de FK constraint

**Ejemplo de uso:**
```java
// Crear un Movement que consume "Gasolina Extra" (ID: FUEL-12345678)
Movement salida = new Movement(
    "SALIDA",
    "FUEL-12345678",  // ← FK: debe existir en oil_products
    "VEH-87654321",   // ← FK: debe existir en vehicles
    null,             // numeroFactura (NULL para salidas)
    "GALON",
    10.0,
    13500.0
);
```

---

#### 1.9 Override toString() - Representación Textual

**Líneas 239-254:**

```java
@Override
public String toString() {
    return "Movement{" +
            "id='" + id + '\'' +
            ", movementType='" + movementType + '\'' +
            ", productId='" + productId + '\'' +
            ", vehicleId='" + vehicleId + '\'' +
            ", numeroFactura='" + numeroFactura + '\'' +
            ", unidadDeMedida='" + unidadDeMedida + '\'' +
            ", quantity=" + quantity +
            ", unitPrice=" + unitPrice +
            ", movementDate='" + movementDate + '\'' +
            '}';
}
```

**Concepto: ¿Por qué override toString()?**

**Sin override:**
```java
Movement m = new Movement(...);
System.out.println(m);
// Output: com.forestech.models.Movement@15db9742  ← Dirección de memoria (inútil)
```

**Con override:**
```java
Movement m = new Movement(...);
System.out.println(m);
// Output: Movement{id='MOV-12345678', movementType='ENTRADA', ...}  ← Datos útiles
```

**`@Override`:**
- Anotación que indica que estás sobrescribiendo un método de la clase padre (`Object`)
- El compilador verifica que el método exista en la clase padre
- Previene errores de tipeo (ej: escribir `tostring()` en lugar de `toString()`)

---

### 1.10 Checkpoint de Verificación - Movement ✅

1. **¿Por qué `id` y `movementDate` son `final`?**
2. **¿Cuál es la diferencia entre el constructor CREATE (7 params) y LOAD (9 params)?**
3. **¿Qué pasa si haces `movement.setQuantity(-5)`?**
4. **¿Cómo se calcula el IVA de un movimiento?**
5. **¿Qué es `productId` y hacia dónde apunta?**
6. **¿Puede una ENTRADA tener vehicleId?**
7. **¿Por qué `setUnidadDeMedida()` lanza excepción pero `setMovementType()` solo imprime mensaje?**

---

## 2. CLASE PRODUCT - CATÁLOGO DE COMBUSTIBLES

### Archivo: `models/Product.java`

#### 2.1 Concepto: ¿Qué es un Product?

Un **Product** representa un tipo de combustible en el catálogo:
- Gasolina Corriente
- Gasolina Extra
- ACPM (Diesel)
- Aceite 2T
- Etc.

Es una entidad **más simple** que Movement porque no tiene relaciones complejas.

---

#### 2.2 Análisis Completo

```java
package com.forestech.models;

import com.forestech.utils.IdGenerator;

public class Product {
    private final String id;
    private String name;
    private String unidadDeMedida;
    private double priceXUnd;

    // Constructor para CREAR nuevos productos (genera ID automático)
    public Product(String name, String unidadDeMedida, double priceXUnd) {
        this.id = IdGenerator.generateFuelId();  // ← FUEL-XXXXXXXX
        this.name = name;
        this.unidadDeMedida = unidadDeMedida;
        this.priceXUnd = priceXUnd;
    }

    // Constructor para CARGAR productos desde la BD (usa ID existente)
    public Product(String id, String name, String unidadDeMedida, double priceXUnd) {
        this.id = id;  // ← ID existente
        this.name = name;
        this.unidadDeMedida = unidadDeMedida;
        this.priceXUnd = priceXUnd;
    }

    // Getters y setters (líneas 27-53)
    // ...

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", unidadDeMedida='" + unidadDeMedida + '\'' +
                ", priceXUnd=" + priceXUnd +
                '}';
    }
}
```

**Características clave:**

1. **Simplicidad:** Solo 4 atributos (id, name, unidadDeMedida, priceXUnd)
2. **Patrón consistente:** Mismo diseño que Movement (final id, constructores CREATE/LOAD)
3. **Sin validación en setters:** Confía en la capa de servicios para validar

**⚠️ Observación:**
- Los setters NO validan (ej: `setName("")` aceptaría nombre vacío)
- La validación debería estar en `ProductServices.insertProduct()`

---

#### 2.3 Uso en el Proyecto

**En Movement.java:**
```java
private String productId;  // ← FK hacia Product.id
```

**En Vehicle.java:**
```java
private String fuelProductId;  // ← FK hacia Product.id
```

**En AppController.java:**
```java
List<Product> productos = ProductServices.getAllProducts();
// Usuario selecciona de la lista
String selectedProductId = productos.get(index).getId();
```

---

### 2.4 Checkpoint de Verificación - Product ✅

1. **¿Qué formato tiene el ID de un producto?**
2. **¿Cuántos constructores tiene Product y para qué sirve cada uno?**
3. **¿Qué entidades referencian a Product mediante FK?**

---

## 3. CLASE VEHICLE - FLOTA DE VEHÍCULOS

### Archivo: `models/Vehicle.java`

#### 3.1 Concepto: ¿Qué es un Vehicle?

Un **Vehicle** representa una máquina que consume combustible:
- Camiones
- Excavadoras
- Motosierras
- Generadores eléctricos
- Etc.

Cada vehículo tiene:
- Un **tipo de combustible asignado** (FK a Product)
- Una **capacidad del tanque** (litros)
- Un **horómetro** opcional (mide horas de trabajo, no kilómetros)

---

#### 3.2 Análisis de Atributos (Líneas 6-14)

```java
private final String id;
private String name;
private String category;
private double capacity;
private String fuelProductId;  // FK → oil_products.id
private boolean haveHorometer;
```

**Nuevos conceptos:**

1. **`boolean haveHorometer;`**
   - Tipo de dato: `true` o `false`
   - Indica si el vehículo tiene horómetro (medidor de horas de operación)
   - Getter automático en Java: `isHaveHorometer()` (nota el prefijo `is` en lugar de `get`)

2. **`double capacity;`**
   - Capacidad del tanque en litros
   - Permite validar que las salidas no excedan la capacidad física del vehículo

---

#### 3.3 Constructores (Líneas 20-56)

**Constructor CREATE:**
```java
public Vehicle(String name, String category, double capacity, String fuelProductId, boolean haveHorometer) {
    this.id = IdGenerator.generateVehicleId();  // ← VEH-XXXXXXXX
    this.name = name;
    this.category = category;
    this.capacity = capacity;
    this.fuelProductId = fuelProductId;
    this.haveHorometer = haveHorometer;
}
```

**Ejemplo de uso:**
```java
Vehicle excavadora = new Vehicle(
    "Excavadora CAT 320",    // name
    "Excavadora",             // category
    200.0,                    // capacity (200 litros)
    "FUEL-12345678",          // fuelProductId (ACPM)
    true                      // haveHorometer (sí tiene)
);
```

---

#### 3.4 toString() con Formato ASCII (Líneas 107-119)

**Innovación:** Vehicle tiene un `toString()` visualmente atractivo con box drawing:

```java
@Override
public String toString() {
    return "┌─────────────────────────────────────────────────────┐\n" +
            "│              📋 DETALLE DEL VEHICULO               │\n" +
            "├────────────────────────────────────────────────────┤\n" +
            "│ 🆔 ID:                " + id + "\n" +
            "│ 📌 Nombre:            " + name + "\n" +
            "│ ⛽ Combustible ID:     " + fuelProductId + "\n" +
            "│ 📦 Categoría:         " + category + "\n" +
            "│ 💾 Capacidad (lts):   " + capacity + "\n" +
            "│ ⏱️  Horómetro:         " + (haveHorometer ? "Sí" : "No") + "\n" +
            "└─────────────────────────────────────────────────────┘";
}
```

**Output real:**
```
┌─────────────────────────────────────────────────────┐
│              📋 DETALLE DEL VEHICULO               │
├────────────────────────────────────────────────────┤
│ 🆔 ID:                VEH-A3F2C1D4
│ 📌 Nombre:            Excavadora CAT 320
│ ⛽ Combustible ID:     FUEL-12345678
│ 📦 Categoría:         Excavadora
│ 💾 Capacidad (lts):   200.0
│ ⏱️  Horómetro:         Sí
└─────────────────────────────────────────────────────┘
```

**Concepto nuevo: Operador ternario**

**Línea 117:**
```java
"│ ⏱️  Horómetro:         " + (haveHorometer ? "Sí" : "No") + "\n"
                                     ↑
                            Operador ternario
```

**Sintaxis:**
```java
condicion ? valorSiTrue : valorSiFalse
```

**Equivalente con if-else:**
```java
String horometerText;
if (haveHorometer) {
    horometerText = "Sí";
} else {
    horometerText = "No";
}
```

**Ventaja:** Código más conciso para asignaciones simples.

---

### 3.5 Checkpoint de Verificación - Vehicle ✅

1. **¿Qué diferencia hay entre un vehículo con horómetro y uno sin horómetro?**
2. **¿Por qué Vehicle tiene un FK a Product?**
3. **¿Qué retorna `isHaveHorometer()` si el vehículo SÍ tiene horómetro?**
4. **Explica el operador ternario en la línea 117**

---

## 4. PATRÓN CONSTRUCTORES CREATE vs LOAD

### 4.1 Concepto: ¿Por qué dos constructores?

**Problema:** Una clase necesita crearse en **dos contextos diferentes**:

1. **Crear nuevos registros** (INSERT en BD)
   - Necesitas generar ID automáticamente
   - Necesitas timestamp actual
   - Usuario NO proporciona estos datos

2. **Cargar registros existentes** (SELECT desde BD)
   - Usas el ID que ya existe en la BD
   - Usas el timestamp que ya existe en la BD
   - Estos datos vienen de ResultSet

---

### 4.2 Comparación de Firmas

**Todas las clases siguen este patrón:**

| Clase | Constructor CREATE | Constructor LOAD |
|-------|-------------------|------------------|
| Movement | 7 params (sin id, sin date) | 9 params (con id, con date) |
| Product | 3 params (sin id) | 4 params (con id) |
| Vehicle | 5 params (sin id) | 6 params (con id) |
| Supplier | 5 params (sin id) | 6 params (con id) |

**Ejemplo visual:**

```java
// ========== MOVEMENT ==========

// CREATE (usuario crea un nuevo movimiento)
Movement nuevo = new Movement(
    "ENTRADA",           // 1
    "FUEL-12345678",     // 2
    null,                // 3
    "F-001",             // 4
    "GALON",             // 5
    100.0,               // 6
    12000.0              // 7
);  // ID y fecha se generan automáticamente

// LOAD (cargar desde BD)
Movement existente = new Movement(
    "MOV-A3F2C1D4",      // 1 (ID existente)
    "ENTRADA",           // 2
    "FUEL-12345678",     // 3
    null,                // 4
    "F-001",             // 5
    "GALON",             // 6
    100.0,               // 7
    12000.0,             // 8
    "2025-01-14T15:30"   // 9 (fecha existente)
);
```

---

### 4.3 Uso en Services

**En MovementServices.java:**

```java
// Método insertMovement() usa constructor CREATE
public void insertMovement(Movement movement) {
    // El movement ya viene con ID generado por el constructor CREATE
    String sql = "INSERT INTO Movement VALUES (?, ?, ?, ...)";
    stmt.setString(1, movement.getId());  // ← ID ya generado
    // ...
}

// Método getAllMovements() usa constructor LOAD
private Movement mapResultSetToMovement(ResultSet rs) {
    return new Movement(
        rs.getString("id"),  // ← ID desde BD
        // ... más campos desde BD
    );
}
```

---

### 4.4 Checkpoint de Verificación - Patrón CREATE/LOAD ✅

1. **¿Cuántos parámetros tiene el constructor CREATE de Product?**
2. **¿Qué parámetros adicionales tiene el constructor LOAD de Movement vs CREATE?**
3. **¿Por qué Movement no tiene un setter para `id`?**

---

## 5. FACTURA Y DETALLEFACTURA - SISTEMA DE FACTURACIÓN

### 5.1 Concepto: Relación 1:N (Uno a Muchos)

**Factura** es la cabecera (header) de una factura de compra:
- Número de factura
- Fechas de emisión y vencimiento
- Proveedor (FK)
- Totales (subtotal, IVA, total)

**DetalleFactura** son las líneas de detalle (items):
- Cada factura tiene MÚLTIPLES líneas
- Cada línea tiene: producto, cantidad, precio

**Relación:**
```
Factura (1)  ────< DetalleFactura (N)

Factura F-001
├── Detalle 1: Gasolina Extra, 100 galones, $13,500
├── Detalle 2: ACPM, 200 galones, $12,000
└── Detalle 3: Aceite 2T, 50 litros, $8,000
```

---

### 5.2 Análisis de Factura.java

**Atributos clave:**
```java
private final String numeroFactura;  // PK (Primary Key, no FK)
private LocalDate fechaEmision;
private LocalDate fechaVencimiento;
private String supplierId;  // FK → suppliers.id
private double subtotal;
private double iva;
private double total;
private String observaciones;
private String formaPago;      // "Efectivo", "Transferencia", etc.
private String cuentaBancaria;
```

**Novedad: LocalDate**

Anteriormente usábamos `String` para fechas, pero `LocalDate` es más apropiado:
- Solo representa fecha (sin hora): `2025-01-14`
- Métodos útiles: `plusDays(30)`, `isBefore()`, `isAfter()`
- Compatible con JDBC: `PreparedStatement.setDate()`

**Constructores:**

**CREATE:**
```java
public Factura(String numeroFactura, LocalDate fechaEmision, LocalDate fechaVencimiento,
               String supplierId, String observaciones, String formaPago, String cuentaBancaria) {
    this.numeroFactura = numeroFactura;
    this.fechaEmision = fechaEmision;
    this.fechaVencimiento = fechaVencimiento;
    this.supplierId = supplierId;
    this.subtotal = 0.0;  // ← Inicializado en 0, se calcula después
    this.iva = 0.0;
    this.total = 0.0;
    this.observaciones = observaciones;
    this.formaPago = formaPago;
    this.cuentaBancaria = cuentaBancaria;
}
```

**Nota:** Los totales se inicializan en 0 porque se calcularán sumando los `DetalleFactura`.

---

### 5.3 Análisis de DetalleFactura.java

**Atributos:**
```java
private int idDetalle;           // Autoincrement en BD
private String numeroFactura;    // FK → facturas.numero_factura
private String producto;         // Nombre del producto (NO es FK, es copia)
private double cantidad;
private double precioUnitario;
```

**⚠️ Decisión de diseño controversial:**

**Línea:** `private String producto;`

Esto NO es una FK, es una **copia del nombre del producto**.

**Razón:** Si el producto cambia de nombre o se elimina de la BD, el detalle de la factura debe mantener el nombre original (auditoría).

**Alternativa (no implementada):**
```java
private String productId;  // FK → oil_products.id
```

**Ventaja de la copia:** Inmutabilidad histórica
**Desventaja de la copia:** Datos duplicados, inconsistencias si hay typos

---

### 5.4 Método de Cálculo en DetalleFactura

```java
public double getSubtotal() {
    return cantidad * precioUnitario;
}
```

Similar a Movement, el detalle calcula su propio subtotal.

---

### 5.5 Checkpoint de Verificación - Facturación ✅

1. **¿Qué diferencia hay entre `numeroFactura` en Factura y `productId` en Movement?** (Pista: PK vs FK)
2. **¿Por qué Factura tiene subtotal = 0 en el constructor CREATE?**
3. **¿Qué ventaja tiene usar `LocalDate` en lugar de `String` para fechas?**
4. **¿Por qué DetalleFactura almacena el nombre del producto como String y no como FK?**

---

## 6. RESUMEN DE LA FASE 02

### 6.1 Conceptos POO Implementados

| Concepto | Ejemplos en el Código |
|----------|----------------------|
| **Encapsulación** | `private` fields + `public` getters/setters |
| **Inmutabilidad** | `final String id`, `final String movementDate` |
| **Sobrecarga de constructores** | CREATE (7 params) vs LOAD (9 params) |
| **Validación en setters** | `setMovementType()`, `setUnidadDeMedida()`, `setQuantity()` |
| **Métodos de negocio** | `getSubtotalvalue()`, `getIva()`, `getTotalWithIva()` |
| **Foreign Keys** | `productId`, `vehicleId`, `numeroFactura`, `supplierId`, `fuelProductId` |
| **Override** | `toString()` en todas las clases |
| **Annotations** | `@NotNull`, `@Override` |
| **Tipos de datos** | `String`, `double`, `boolean`, `LocalDate`, `LocalDateTime` |
| **Operador ternario** | `haveHorometer ? "Sí" : "No"` |

---

### 6.2 Jerarquía de Complejidad

**Simples (pocos atributos, sin FK complejas):**
- Product (4 atributos)

**Intermedias (FK únicas, validaciones básicas):**
- Vehicle (6 atributos, 1 FK)
- Supplier (6 atributos, 0 FK)

**Complejas (múltiples FK, validaciones, métodos de negocio):**
- Movement (9 atributos, 3 FK, cálculos de IVA)
- Factura (10 atributos, 1 FK, relación 1:N)

---

### 6.3 Diagrama de Relaciones

```
                    ┌─────────────┐
                    │   Product   │
                    │ (FUEL-XXX)  │
                    └──────┬──────┘
                           │
                ┌──────────┼──────────┐
                │                     │
                ▼                     ▼
        ┌─────────────┐       ┌─────────────┐
        │  Movement   │       │   Vehicle   │
        │ (MOV-XXX)   │       │ (VEH-XXX)   │
        └──────┬──────┘       └──────┬──────┘
               │                     │
               └──────────┬──────────┘
                          │
                          ▼
                  (Movement vincula
                   Product + Vehicle)

┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  Supplier   │◄──────│   Factura   │──────>│DetalleFactura│
│ (PROV-XXX)  │       │ (F-XXX)     │       │ (1, 2, 3...) │
└─────────────┘       └─────────────┘       └─────────────┘
                            │
                            ▼
                      ┌─────────────┐
                      │  Movement   │
                      │ (ENTRADA)   │
                      └─────────────┘
```

---

### 6.4 Oportunidades de Refactorización

**Fase 9 (futura):**

1. **Convertir Strings a Enums:**
   ```java
   // Actualmente:
   String movementType = "ENTRADA";

   // Mejor:
   enum MovementType { ENTRADA, SALIDA }
   MovementType movementType = MovementType.ENTRADA;
   ```

2. **Consistencia en manejo de errores:**
   - `setUnidadDeMedida()` lanza `IllegalArgumentException` ✅
   - `setMovementType()` solo imprime mensaje ❌
   - Unificar a lanzar excepciones

3. **Validación en constructores:**
   - Actualmente: Constructores no validan
   - Mejor: Validar en constructor para garantizar objetos válidos desde la creación

---

### 6.5 Ejercicio Final de la Fase 02 🎯

**Tarea:**

1. **Crea manualmente un objeto Movement** en Main.java:
   ```java
   Movement entrada = new Movement(
       "ENTRADA",
       "FUEL-12345678",
       null,
       "F-001",
       "GALON",
       50.0,
       12500.0
   );
   System.out.println(entrada);
   System.out.println("Subtotal: " + entrada.getSubtotalvalue());
   System.out.println("IVA: " + entrada.getIva());
   System.out.println("Total: " + entrada.getTotalWithIva());
   ```

2. **Compila y ejecuta:**
   ```bash
   cd /home/hp/forestechOil/forestech-cli-java
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.forestech.Main"
   ```

3. **Verifica que el output muestre:**
   - Movement con ID generado (MOV-XXXXXXXX)
   - Subtotal: 625000.0 (50 × 12500)
   - IVA: 118750.0 (625000 × 0.19)
   - Total: 743750.0

4. **Experimenta con validaciones:**
   ```java
   entrada.setQuantity(-5);  // ¿Qué pasa?
   entrada.setUnidadDeMedida("LITRO");  // ¿Qué pasa?
   ```

---

### 6.6 Autoevaluación ✅

1. **¿Qué hace el modificador `final` en un field?**
2. **¿Cuál es la diferencia entre constructor CREATE y LOAD?**
3. **¿Por qué Movement tiene 3 Foreign Keys?**
4. **¿Qué retorna `movement.getIva()` si el subtotal es $100,000?**
5. **¿Qué es el patrón de encapsulación y por qué es útil?**
6. **¿Qué hace `@Override` antes de un método?**
7. **¿Qué tipo de relación existe entre Factura y DetalleFactura?**

**Si respondiste 6/7 correctamente:** ✅ Listo para Fase 03
**Si respondiste menos de 6:** 🔄 Repasa las secciones de Movement y constructores

---

## 7. PRÓXIMOS PASOS

Con los modelos completamente implementados, la **Fase 03** introduce:
- **Colecciones en memoria** (List<Movement>)
- **MovementManagers** (CRUD sin base de datos)
- **Defensive copying** (protección de listas)
- **Búsquedas y filtros** en colecciones

---

**🎓 Fase 02 Completada**

Has construido las entidades fundamentales del sistema ForestechOil:
- ✅ Movement con cálculos de IVA
- ✅ Product como catálogo
- ✅ Vehicle con horómetro
- ✅ Supplier para proveedores
- ✅ Factura + DetalleFactura para facturación
- ✅ Foreign Keys para relaciones
- ✅ Validaciones en setters
- ✅ Métodos de negocio en modelos

**Siguiente:** [FASE_03_MANAGERS_COLECCIONES.md](./FASE_03_MANAGERS_COLECCIONES.md) - Gestión de colecciones con MovementManagers
