# 📝 PROMPTS PARA ACTUALIZAR CADA FASE

> **Instrucciones:** Copia y pega cada prompt completo en una sesión nueva de Copilot. El LLM tendrá TODO el contexto necesario para actualizar esa fase específica.

> **🚨 IMPORTANTE:** Antes de usar estos prompts, lee `REGLAS_ACTUALIZACION_FASES.md` que contiene las reglas críticas sobre cómo escribir contenido didáctico sin dar código completo listo para copiar/pegar.

---


## 🎯 PROMPT PARA FASE 5 - LÓGICA DE NEGOCIO

```
Actualiza FASE_05_LOGICA_NEGOCIO.md siguiendo la organización establecida.

**ESTRUCTURA AL TERMINAR FASE 4:**
```
com.forestech/
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
│   └── Product.java
└── services/
    ├── MovementService.java (CRUD básico)
    ├── VehicleService.java
    ├── SupplierService.java
    └── ProductService.java
```

**LO QUE NECESITO PARA FASE 5:**

1. **Checkpoint 5.1 - Movimiento de ENTRADA con transacción:**
   - **Modificar:** `MovementService.java`
   - Nuevo método: createEntryMovement(Movement movement) - con transacción
   - Operación 1: INSERT en combustibles_movements
   - Operación 2: UPDATE/INSERT en combustibles_inventory (sumar cantidad)
   - Usar connection.setAutoCommit(false), commit(), rollback()
   - **Main.java:** probar entrada y verificar inventario actualizado

2. **Checkpoint 5.2 - Movimiento de SALIDA con validación:**
   - **Modificar:** `MovementService.java`
   - Nuevo método: createExitMovement(Movement movement, String vehicleId)
   - Validar stock ANTES de la transacción
   - Operación 1: INSERT movimiento
   - Operación 2: UPDATE inventory (restar)
   - Operación 3: UPDATE vehicle (horómetro, horas)
   - **Main.java:** intentar salida sin stock (debe rechazar) y con stock (debe funcionar)

3. **Checkpoint 5.3 - Consultar y mostrar inventario:**
   - **Crear:** `InventoryService.java` en `services/`
   - **Crear (opcional):** `InventoryRecord.java` en `models/` (para mapear resultados)
   - Métodos:
     - getCurrentInventory() → List<InventoryRecord>
     - getInventoryByLocation(String location) → List<InventoryRecord>
     - getLowStockItems(double threshold) → List<InventoryRecord>
   - **Modificar:** `DataDisplay.java` - agregar método showInventoryTable()
   - **Main.java:** consultar y mostrar inventario formateado

4. **Checkpoint 5.4 - Validaciones de negocio:**
   - **Crear paquete:** `validators` → `com.forestech.validators`
   - **Crear:** `MovementValidator.java` en ese paquete
   - Métodos estáticos:
     - validateEntryData(Movement) → List<String> errores
     - validateExitData(Movement, double availableStock) → List<String>
     - validateVehicleData(Vehicle) → List<String>
   - **Modificar:** Services para usar estos validadores
   - **Main.java:** probar validaciones (datos válidos e inválidos)

5. **Checkpoint 5.5 - Cálculos de consumo:**
   - **Crear:** `ConsumptionCalculator.java` en paquete `utils`
   - Métodos estáticos:
     - calculateConsumptionRate(Vehicle vehicle, List<Movement> exits) → double
     - estimateNextRefill(Vehicle vehicle) → LocalDate
     - calculateAveragePrice(List<Movement> movements) → double
   - **Main.java:** generar estadísticas de consumo

**ESTRUCTURA ESPERADA AL TERMINAR FASE 5:**
```
com.forestech/
├── Main.java
├── AppConfig.java
├── MenuHelper.java
├── DataDisplay.java (extendido con tablas de inventario)
├── InputHelper.java
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   ├── Product.java
│   └── InventoryRecord.java (NUEVO)
├── services/
│   ├── MovementService.java (con transacciones y validaciones)
│   ├── VehicleService.java
│   ├── SupplierService.java
│   ├── ProductService.java
│   └── InventoryService.java (NUEVO)
├── validators/
│   └── MovementValidator.java (NUEVO)
└── utils/
    └── ConsumptionCalculator.java (NUEVO)
```

**CONCEPTOS CLAVE A EXPLICAR:**
- Transacciones ACID
- Rollback vs Commit
- Validaciones en capas (validator → service → BD)
- Separación: validators (reglas) vs services (persistencia)
- Utils para lógica sin estado

**ACCIÓN:**
Actualiza FASE_05_LOGICA_NEGOCIO.md completo.
```

---

## 🎯 PROMPT PARA FASE 6 - UI

```
Actualiza FASE_06_UI.md siguiendo la organización de código establecida.

**ESTRUCTURA AL TERMINAR FASE 5:**
```
com.forestech/
├── Main.java
├── AppConfig.java
├── MenuHelper.java (simple, sin bucles)
├── DataDisplay.java
├── InputHelper.java
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   ├── Product.java
│   └── InventoryRecord.java
├── services/
│   ├── MovementService.java
│   ├── VehicleService.java
│   ├── SupplierService.java
│   ├── ProductService.java
│   └── InventoryService.java
├── validators/
│   └── MovementValidator.java
└── utils/
    └── ConsumptionCalculator.java
```

**LO QUE NECESITO PARA FASE 6:**

1. **Checkpoint 6.1 - Menú principal interactivo:**
   - **Crear paquete:** `ui` → `com.forestech.ui`
   - **Crear:** `ConsoleMenu.java` en ese paquete
   - Evoluciona de MenuHelper.java pero con:
     - Bucle while(true)
     - Atributo Scanner
     - Método start() que inicia el menú
     - Método displayMainMenu()
     - Manejo de InputMismatchException
   - **Modificar:** `Main.java` - ahora solo crea ConsoleMenu y llama start()

2. **Checkpoint 6.2 - Wizard para ENTRADA:**
   - **Modificar:** `ConsoleMenu.java`
   - Método: registerEntryWizard()
   - Pasos:
     1. Seleccionar proveedor (lista de BD)
     2. Seleccionar combustible
     3. Ingresar cantidad
     4. Ingresar precio
     5. Seleccionar ubicación
     6. Confirmar resumen
     7. Guardar con MovementService.createEntryMovement()
   - Integrar InputHelper para captura de datos
   - Usar DataDisplay para mostrar listas

3. **Checkpoint 6.3 - Wizard para SALIDA:**
   - **Modificar:** `ConsoleMenu.java`
   - Método: registerExitWizard()
   - Pasos adicionales vs entrada:
     1. Seleccionar vehículo
     2. Verificar stock disponible
     3. Ingresar horómetro nuevo
     4. Confirmar y validar
     5. Guardar con MovementService.createExitMovement()

4. **Checkpoint 6.4 - Ver inventario formateado:**
   - **Modificar:** `ConsoleMenu.java`
   - Método: displayInventory()
   - Llamar InventoryService.getCurrentInventory()
   - Usar DataDisplay.showInventoryTable() para formatear
   - Opciones: ver todo, filtrar por ubicación, ver solo bajo stock

5. **Checkpoint 6.5 - Submenús de gestión:**
   - **Modificar:** `ConsoleMenu.java`
   - Métodos:
     - handleVehiclesMenu() - CRUD de vehículos
     - handleSuppliersMenu() - CRUD de proveedores
     - handleProductsMenu() - CRUD de productos
   - Cada submenú con opciones: listar, crear, editar, eliminar, volver

6. **Checkpoint 6.6 - Integración completa:**
   - **Modificar:** `ConsoleMenu.java`
   - Conectar todos los submenús
   - Agregar confirmaciones antes de operaciones destructivas
   - Manejo de errores con try-catch
   - Mensajes claros de éxito/error
   - **Modificar:** `Main.java` - debe quedar en menos de 10 líneas

**ESTRUCTURA ESPERADA AL TERMINAR FASE 6:**
```
com.forestech/
├── Main.java (SOLO: crear ConsoleMenu y llamar start())
├── AppConfig.java
├── MenuHelper.java (DEPRECATED - migrado a ConsoleMenu)
├── DataDisplay.java (extendido con más formateos)
├── InputHelper.java (integrado con ConsoleMenu)
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   ├── Product.java
│   └── InventoryRecord.java
├── services/
│   ├── MovementService.java
│   ├── VehicleService.java
│   ├── SupplierService.java
│   ├── ProductService.java
│   └── InventoryService.java
├── validators/
│   └── MovementValidator.java
├── utils/
│   └── ConsumptionCalculator.java
└── ui/
    └── ConsoleMenu.java (NUEVO - menú completo interactivo)
```

**PUNTOS CLAVE:**
- ConsoleMenu es el controlador principal de la UI
- Main.java queda MÍNIMO (solo bootstrap)
- InputHelper y DataDisplay son helpers de ConsoleMenu
- Cada wizard valida antes de llamar al Service
- Manejo de excepciones en la capa UI (no propagar al usuario)

**ACCIÓN:**
Actualiza FASE_06_UI.md completo con este enfoque.
```

---

## 🎯 PROMPT PARA FASE 7 - MANEJO DE ERRORES

```
Actualiza FASE_07_ERRORES.md siguiendo la organización de código.

**ESTRUCTURA AL TERMINAR FASE 6:**
```
com.forestech/
├── Main.java (<10 líneas)
├── AppConfig.java
├── DataDisplay.java
├── InputHelper.java
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   ├── Product.java
│   └── InventoryRecord.java
├── services/
│   ├── MovementService.java
│   ├── VehicleService.java
│   ├── SupplierService.java
│   ├── ProductService.java
│   └── InventoryService.java
├── validators/
│   └── MovementValidator.java
├── utils/
│   └── ConsumptionCalculator.java
└── ui/
    └── ConsoleMenu.java
```

**LO QUE NECESITO PARA FASE 7:**

1. **Checkpoint 7.1 - Excepciones personalizadas:**
   - **Crear paquete:** `exceptions` → `com.forestech.exceptions`
   - **Crear excepciones:**
     - `InsufficientInventoryException.java` (extends Exception)
       - Atributos: fuelType, required, available, location
       - getMessage() customizado
     - `InvalidMovementException.java` (extends Exception)
       - Atributo: reason
       - Constructor con mensaje y con causa
     - `DatabaseConnectionException.java` (extends RuntimeException)
       - Atributo: dbUrl
       - Constructor con mensaje, causa y URL
   - **Modificar:** 
     - `MovementService.java` - lanzar excepciones en vez de retornar false
     - `DatabaseConnection.java` - lanzar DatabaseConnectionException
   - **Modificar:** `ConsoleMenu.java` - capturar y mostrar mensajes legibles

2. **Checkpoint 7.2 - try-with-resources:**
   - **Modificar:** Todos los Services
   - Reemplazar try-catch-finally manual por try-with-resources
   - Pattern:
     ```java
     try (Connection conn = DatabaseConnection.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {
         // operaciones
     } catch (SQLException e) {
         // manejo
     }
     ```
   - Eliminar bloques finally con .close() manual

3. **Checkpoint 7.3 - Validación de inputs en InputHelper:**
   - **Modificar:** `InputHelper.java`
   - Métodos con try-catch para InputMismatchException:
     - readIntSafe(String prompt, int min, int max) → int
     - readDoubleSafe(String prompt, double min) → double
     - readStringNotEmpty(String prompt) → String
   - Bucles de reintento si input es inválido
   - Mensajes claros de error

4. **Checkpoint 7.4 - Logging básico:**
   - **Modificar:** `AppConfig.java` - agregar flag DEBUG
   - **Crear:** `Logger.java` en `utils/`
   - Métodos estáticos:
     - logInfo(String message)
     - logError(String message, Exception e)
     - logDebug(String message) - solo si DEBUG=true
   - **Modificar:** Services - usar Logger en vez de System.out/err

5. **Checkpoint 7.5 - Manejo de errores en UI:**
   - **Modificar:** `ConsoleMenu.java`
   - Envolver todas las operaciones críticas en try-catch
   - Mostrar mensajes legibles al usuario
   - NO mostrar stack traces al usuario final
   - Loggear errores técnicos con Logger
   - Opción de "reintentar" o "volver al menú"

**ESTRUCTURA ESPERADA AL TERMINAR FASE 7:**
```
com.forestech/
├── Main.java
├── AppConfig.java (con flag DEBUG)
├── DataDisplay.java
├── InputHelper.java (con validaciones robustas)
├── config/
│   └── DatabaseConnection.java (con excepciones personalizadas)
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   ├── Product.java
│   └── InventoryRecord.java
├── services/
│   ├── MovementService.java (lanza excepciones, usa try-with-resources)
│   ├── VehicleService.java
│   ├── SupplierService.java
│   ├── ProductService.java
│   └── InventoryService.java
├── validators/
│   └── MovementValidator.java
├── utils/
│   ├── ConsumptionCalculator.java
│   └── Logger.java (NUEVO)
├── exceptions/
│   ├── InsufficientInventoryException.java (NUEVO)
│   ├── InvalidMovementException.java (NUEVO)
│   └── DatabaseConnectionException.java (NUEVO)
└── ui/
    └── ConsoleMenu.java (manejo robusto de errores)
```

**CONCEPTOS CLAVE:**
- Checked vs Unchecked exceptions
- Exception chaining con getCause()
- try-with-resources para AutoCloseable
- Separación: excepciones técnicas vs mensajes de usuario
- Logging para debugging sin contaminar consola

**FILOSOFÍA:**
- Las excepciones hablan el lenguaje del negocio
- El usuario ve mensajes claros, no stack traces
- Los errores técnicos se loggean para el desarrollador
- Recursos SIEMPRE se cierran (try-with-resources)

**ACCIÓN:**
Actualiza FASE_07_ERRORES.md completo.
```

---

## 📋 INSTRUCCIONES DE USO

### Para actualizar cada fase:

1. **Abre una sesión NUEVA** de GitHub Copilot
2. **Copia el prompt completo** de la fase que quieres actualizar
3. **Pega y envía**
4. El LLM tendrá TODO el contexto necesario
5. Revisará el archivo y lo actualizará completo

### Orden recomendado:

1. ✅ **FASE 1** - Ya actualizada
2. **FASE 2** - POO (usar primer prompt)
3. **FASE 3** - SQL (usar segundo prompt)
4. **FASE 4** - CRUD (usar tercer prompt)
5. **FASE 5** - Lógica de Negocio (usar cuarto prompt)
6. **FASE 6** - UI (usar quinto prompt)
7. **FASE 7** - Manejo de Errores (usar sexto prompt)

### Ventajas de este enfoque:

- ✅ Cada sesión es independiente
- ✅ El prompt incluye TODO el contexto necesario
- ✅ No necesitas explicar el problema cada vez
- ✅ Mantiene la filosofía de enseñanza
- ✅ Estructura progresiva y coherente

---

**Última actualización:** 2025-01-17
**Creado para:** Proyecto Forestech CLI - Aprendizaje Java desde cero

