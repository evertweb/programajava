# 🛡️ FASE 7: MANEJO DE ERRORES Y EXCEPCIONES (Semana 11)

> Objetivo general: fortalecer la resiliencia del sistema con excepciones personalizadas, logging y manejo adecuado de recursos.

---

## 🧠 Mindset antes de iniciar

- 📚 **Jerarquía de excepciones:** Dibuja el árbol completo de excepciones Java (Throwable → Exception/Error → RuntimeException vs checked). Entiéndelo antes de crear las tuyas.
- 📝 **Auditoría de errores:** Revisa TODO tu código actual y anota en `JAVA_LEARNING_LOG.md` dónde aparecen `SQLException`, `NullPointerException`, o simplemente se ignoran errores.
- 🧪 **Pruebas destructivas:** Prepárate para romper tu aplicación deliberadamente. Desconecta la BD, ingresa datos inválidos, intenta salidas sin stock. Anota qué pasa.
- 🎯 **Objetivo:** NO se trata de evitar errores (imposible), sino de **controlarlos elegantemente**.

---

## ✅ Checkpoint 7.1: Excepciones personalizadas para Forestech

**Concepto clave:** Las excepciones estándar de Java (SQLException, IllegalArgumentException) son genéricas. Necesitas excepciones que hablen el lenguaje de tu negocio.

**📍 DÓNDE:**
- Crear paquete `exceptions` en `src/main/java/com/forestech/exceptions/`
- Crear tres clases:
  - `InsufficientInventoryException.java`
  - `InvalidMovementException.java`
  - `DatabaseConnectionException.java`

**🎯 PARA QUÉ:**
Cuando algo sale mal en Forestech, necesitas comunicar:
- **Qué** falló (no hay stock, datos inválidos, BD caída)
- **Cuánto** falta (requeriste 100L pero solo hay 45L)
- **Qué hacer** (contacta al administrador, registra una entrada primero, revisa los datos)

`SQLException` te dice "algo falló en la BD" pero no dice SI es porque:
- No hay conexión
- La tabla no existe
- Violaste una constraint
- Se cayó el servidor

Con excepciones personalizadas:
- `InsufficientInventoryException` dice: "Quieres sacar 100L pero solo hay 45L"
- `InvalidMovementException` dice: "El horómetro nuevo (1200) es menor al actual (1500)"
- `DatabaseConnectionException` dice: "No puedo conectar a la BD en [url]. Verifica que SQL Server esté corriendo"

**🔗 CONEXIÓN FUTURA:**
- En Fase 8, usarás estas excepciones con validaciones más sofisticadas
- En Fase 9, el logger SLF4J registrará cada excepción con contexto completo
- En Fase 10, podrás generar reportes de errores para detectar patrones
- Si haces API REST en el futuro, estas excepciones se mapean a HTTP status codes (400, 404, 500)

**Prompts sugeridos:**
```text
"¿Cuándo debo extender Exception (checked) versus RuntimeException (unchecked)?"
"¿Cómo paso información adicional en una excepción personalizada usando atributos?"
"¿Es mejor capturar SQLException y relanzar mi excepción personalizada o dejar que SQLException se propague?"
"Explícame el concepto de 'exception chaining' con getCause()."
```

**Estructura a implementar:**

```
InsufficientInventoryException (extends Exception)
├── Atributos (4):
│   ├── String fuelType
│   ├── double required
│   ├── double available
│   └── String location
├── Constructor(fuelType, required, available, location)
├── sobrescribir getMessage():
│   "Stock insuficiente de [fuelType] en [location]:
│    Requerido: [required]L, Disponible: [available]L, Faltante: [diferencia]L"
└── getters para acceder a los atributos

InvalidMovementException (extends Exception)
├── Atributos (1):
│   └── String reason
├── Constructor(reason)
├── Constructor alternativo(reason, Throwable cause) ← exception chaining
└── Heredar getMessage() de Exception

DatabaseConnectionException (extends RuntimeException)
├── Atributos (1):
│   └── String dbUrl
├── Constructor(message, SQLException cause, dbUrl)
├── sobrescribir getMessage():
│   "Error de conexión a [dbUrl]: [cause.getMessage()]
│    Verifica servidor y credenciales"
└── usar super(message, cause) para chaining
```

**Tareas paso a paso:**

1. **Crear la clase `InsufficientInventoryException`:**
   - ¿Dónde? En nuevo paquete `src/main/java/com/forestech/exceptions/`
   - ¿Qué extiende? `Exception` (checked - el programador DEBE manejarla)
   - ¿Por qué Exception y no RuntimeException? Porque es un error recuperable del negocio
   - Estructura: atributos privados + constructor + getters + mensaje descriptivo
   - 💡 PISTA: El `getMessage()` debe construir el string con los 4 atributos

2. **Crear la clase `InvalidMovementException`:**
   - ¿Dónde? Mismo paquete `exceptions`
   - ¿Qué extiende? `Exception` (checked)
   - ¿Cuántos constructores? Dos: uno recibe solo `reason`, otro recibe `reason + Throwable cause`
   - 💡 PISTA: Para el segundo constructor, usa `super(reason, cause)` para exception chaining
   - Ejemplos de `reason`:
     - "Cantidad debe ser mayor a cero: -50"
     - "Horómetro nuevo (1200) debe ser mayor al actual (1500)"
     - "Proveedor con ID xyz no existe"

3. **Crear la clase `DatabaseConnectionException`:**
   - ¿Dónde? Mismo paquete `exceptions`
   - ¿Qué extiende? `RuntimeException` (unchecked - es problema de infraestructura, no de lógica)
   - ¿Por qué RuntimeException? No es un error del usuario, es del sistema
   - Constructor recibe: `String message`, `SQLException cause`, `String dbUrl`
   - `getMessage()` debe incluir la URL para debugging
   - 💡 PISTA: Usa `super(message, cause)` y guarda `dbUrl` en atributo privado

4. **En `MovementService.createExitMovement()`:**
   - ANTES de la transacción, consulta el stock disponible
   - Si `available < movement.getQuantity()`, lanza `InsufficientInventoryException`
   - Especifica excepción en firma: `throws InsufficientInventoryException`
   - ¿Cómo construir la excepción? Necesitas pasar fuelType, required, available, location

5. **En métodos de validación:**
   - REEMPLAZA `return false` con `throw new InvalidMovementException("razón")`
   - Ejemplos:
     - Si cantidad ≤ 0: lanza excepción con mensaje claro
     - Si horómetro nuevo ≤ actual: lanza excepción con ambos valores
     - Si recurso no existe: lanza excepción con el ID buscado

6. **En `DatabaseConnection.getConnection()`:**
   - El try-catch de SQLException ya existe
   - En el catch, EN LUGAR DE printStackTrace(), lanza `DatabaseConnectionException`
   - Pasarle: mensaje + SQLException original (cause) + DB_URL

7. **En los wizards de menú (`ConsoleMenu`):**
   - En `registerExitWizard()` y similares, cambia el try-catch
   - Antes capturaban `SQLException`
   - Ahora van a capturar `InsufficientInventoryException`, `InvalidMovementException`, etc.
   - Usa `handleException(Exception e)` (lo verás en Checkpoint 7.3)
   - 💡 PISTA: Las excepciones personalizadas ya traen mensajes útiles en `getMessage()`

**✅ Resultado esperado:**
- Cuando intentas una salida sin stock, ves:
  ```
  ❌ Stock insuficiente de Diesel en Bodega Central:
  Requerido: 100.0L, Disponible: 45.0L, Faltante: 55.0L
  ```
- Cuando ingresas horómetro inválido, ves:
  ```
  ❌ Datos inválidos: Horómetro nuevo (1200) debe ser mayor al actual (1500)
  ```
- NO ves stack traces técnicos a menos que sea un error inesperado

**💡 Checked vs Unchecked:**
- **Checked (extends Exception):** Errores recuperables que el programador DEBE manejar. Ejemplo: `InsufficientInventoryException` - puedes mostrar mensaje y dejar que el usuario cancele.
- **Unchecked (extends RuntimeException):** Errores de programación o infraestructura. Ejemplo: `DatabaseConnectionException` - no tiene sentido que cada método lo declare porque es algo que afecta a TODO el sistema.

**⚠️ PELIGRO: No tragues excepciones**
NUNCA hagas:
```java
catch (Exception e) {
    // Silencio...
}
```
Siempre loggea o relanza. Un error silencioso es el peor tipo de error.

**🔍 Depuración:**
Para ver la cadena completa de causas, usa:
```java
Throwable cause = e.getCause();
while (cause != null) {
    System.err.println("Causado por: " + cause);
    cause = cause.getCause();
}
```

**⏱️ Tiempo estimado:** 4-5 horas

---

## ✅ Checkpoint 7.2: try-with-resources y cierre adecuado de recursos

**Concepto clave:** Los recursos (Connection, PreparedStatement, ResultSet) deben cerrarse SIEMPRE, incluso si hay errores. Java 7+ ofrece try-with-resources para automatizar esto.

**📍 DÓNDE:**
- Todos los métodos en las clases de servicio (`MovementService`, `VehicleService`, `SupplierService`, `InventoryService`)
- Métodos DAO (Data Access Objects) si los creaste
- Cualquier lugar donde uses `Connection`, `PreparedStatement`, `ResultSet`

**🎯 PARA QUÉ:**
Si NO cierras recursos:
- **Memory leaks:** Conexiones abiertas consumen memoria
- **Connection pool exhaustion:** Si tienes 10 conexiones máximo y no las cierras, la 11ª operación quedará esperando forever
- **Locks en BD:** Conexiones abiertas pueden mantener locks en tablas
- **Servidor inestable:** SQL Server eventualmente rechazará nuevas conexiones

Los recursos DEBEN cerrarse incluso si:
- Ocurre una excepción
- Haces `return` anticipado
- Olvidas llamar a `close()`

try-with-resources garantiza que `close()` se llame automáticamente.

**🔗 CONEXIÓN FUTURA:**
- En Fase 8, refactorizarás el manejo de conexiones a una clase base
- En Fase 10, usarás HikariCP (connection pool) que también requiere cerrar conexiones correctamente
- Si no cierras recursos ahora, el pool se agotará en minutos en producción

**Prompts sugeridos:**
```text
"¿Cómo funciona try-with-resources internamente en Java?"
"¿Qué pasa si una excepción ocurre en el bloque try Y en el close()?"
"¿Puedo usar try-with-resources con mis propias clases? ¿Qué interfaz debo implementar?"
"Muéstrame la diferencia entre try-with-resources y try-catch-finally manual."
```

**Estructura try-with-resources:**

```
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement stmt = conn.prepareStatement(query);
     ResultSet rs = stmt.executeQuery()) {
    
    // Tu código aquí
    
} catch (SQLException e) {
    // Manejar error
}
// recursos cerrados automáticamente: rs → stmt → conn (orden inverso)
```

**Estructura para transacciones (manual):**

```
Connection conn = null;
try {
    conn = DatabaseConnection.getConnection();
    conn.setAutoCommit(false);  ← desactiva autocommit
    
    try (PreparedStatement stmt = ...) {
        // operación 1
    }
    
    try (PreparedStatement stmt = ...) {
        // operación 2
    }
    
    conn.commit();  ← confirma TODO
    
} catch (SQLException e) {
    if (conn != null) {
        try {
            conn.rollback();  ← revierte TODO
        } catch (SQLException rollbackEx) {
            // loggear ambas excepciones
        }
    }
    // lanzar excepción personalizada
} finally {
    if (conn != null) {
        try {
            conn.setAutoCommit(true);  ← restaurar
            conn.close();
        } catch (SQLException e) {
            // loggear
        }
    }
}
```

**Tareas paso a paso:**

1. **Auditar código actual:**
   - ¿Dónde usas `Connection.getConnection()`? Anota todos los lugares
   - ¿Cuáles tienen try-catch-finally? 
   - ¿Cuáles NO tienen cierre explícito?
   - 💡 PISTA: Busca `PreparedStatement` y `ResultSet` en tus servicios

2. **Convertir métodos de consulta a try-with-resources:**
   - Métodos que LEEN de BD (SELECT)
   - Ejemplo: `getMovementById()`, `listAllMovements()`, `getInventoryByLocation()`
   - 📋 Lista: En cada servicio, identifica cuáles son consultas vs transacciones
   - ¿Cómo identificarlas? Si solo hacen SELECT, es consulta
   - 💡 PISTA: try-with-resources cierra automáticamente en orden inverso: ResultSet → PreparedStatement → Connection

3. **Métodos de lectura (SELECT):**
   - Estructura: try (conn, stmt, rs)
   - Si ocurre SQLException, lanza `DatabaseConnectionException`
   - Cierre automático garantizado
   - Patrón más limpio y seguro

4. **Convertir métodos de modificación a try-with-resources:**
   - Métodos que ESCRIBEN en BD (INSERT, UPDATE, DELETE)
   - Ejemplo: `registerMovement()`, `updateInventory()`, `createSupplier()`
   - Pero NO son transacciones (no dependen de otra operación)
   - Estructura: try (conn, stmt) - sin ResultSet
   - 💡 PISTA: `executeUpdate()` devuelve int (cantidad de filas afectadas), no ResultSet

5. **Transacciones multi-paso (caso especial):**
   - ¿Cuáles de tus métodos hacen múltiples operaciones?
   - Ejemplo: `createExitMovement()` (insert movimiento + update stock)
   - Para transacciones, NO usar try-with-resources en Connection
   - Usar patrón manual con `setAutoCommit(false)`, `commit()`, `rollback()`
   - PERO usar try-with-resources para CADA PreparedStatement
   - 💡 PISTA: Si falla paso 2, rollback revierte AMBAS operaciones

6. **Orden de cierre garantizado:**
   - try-with-resources cierra en orden INVERSO a la creación
   - 📋 Orden correcto:
     1. ResultSet (último creado → primero cerrado)
     2. PreparedStatement
     3. Connection (primero creado → último cerrado)
   - 💡 PISTA: Java lo hace automáticamente, NO lo hagas manualmente

7. **Logging en catch:**
   - Sistema.err → información técnica
   - Incluir: timestamp, mensaje, causa
   - NO ocultar excepciones, siempre loguear
   - Luego lanzar excepción personalizada para que ConsoleMenu la maneje
   - 💡 PISTA: Este logging será reemplazado con SLF4J en Fase 9

8. **Refactorizar cada servicio:**
   - Listar TODOS los métodos de `MovementService`
   - Identificar: ¿es consulta, modificación o transacción?
   - Aplicar patrón correcto según tipo
   - Repetir para `VehicleService`, `SupplierService`, `InventoryService`
   - 💡 PISTA: Haz una matriz en JAVA_LEARNING_LOG.md

**✅ Resultado esperado:**
- Ningún recurso queda abierto después de operaciones
- Puedes ejecutar 1000 operaciones sin agotar conexiones
- Si ocurre error, los recursos se cierran igual
- El código es más limpio y legible

**💡 AutoCloseable:**
Cualquier clase que implemente `AutoCloseable` puede usarse con try-with-resources. Si creas tus propias clases que manejan recursos, implementa esta interfaz.

**⚠️ RECUERDA: Finally SIEMPRE se ejecuta:**
Incluso si haces `return` en el try, el `finally` se ejecuta antes. Esto garantiza limpieza de recursos.

**🔍 Depuración de leaks:**
Si sospechas que hay conexiones abiertas, agrega logging:
```java
System.out.println("[DEBUG] Abriendo conexión: " + conn.hashCode());
// ...
System.out.println("[DEBUG] Cerrando conexión: " + conn.hashCode());
```
Debe haber un "Cerrando" por cada "Abriendo".

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 7.3: Manejo centralizado de errores en la UI

**Concepto clave:** En lugar de tener try-catch repetido en cada opción del menú, centraliza el manejo de errores en un solo lugar.

**📍 DÓNDE:**
- Clase `ConsoleMenu` en `src/main/java/com/forestech/ui/ConsoleMenu.java`
- Crear método `handleException(Exception e)`
- Usarlo en todos los wizards y opciones del menú

**🎯 PARA QUÉ:**
Actualmente, cada wizard maneja errores de forma diferente:
- Algunos muestran el mensaje completo de la excepción
- Otros muestran stack trace
- Algunos no manejan ciertos tipos de error
- Los mensajes no son consistentes

Con manejo centralizado:
- **Consistencia:** Todos los errores se presentan igual
- **Mantenibilidad:** Cambias el formato en UN solo lugar
- **Separación:** La lógica de errores está separada de la lógica de negocio
- **Logging:** Registras errores técnicos sin mostrarlos al usuario
- **Resiliencia:** El menú NO se cae, muestra el error y continúa

**🔗 CONEXIÓN FUTURA:**
- En Fase 9, este método usará SLF4J para logging profesional
- En Fase 10, podrás generar reportes de errores frecuentes
- Si migras a API REST, tendrás un patrón similar con `@ExceptionHandler`

**Prompts sugeridos:**
```text
"¿Qué es el patrón de diseño 'Exception Handler' o 'Error Handler'?"
"¿Cómo clasifico excepciones por severidad (info, warning, error, critical)?"
"¿Qué información debo loggear vs qué mostrar al usuario?"
"Dame ejemplos de mensajes de error amigables para usuarios no técnicos."
```

**Estructura del método `handleException(Exception e)`:**

```
Método: handleException(Exception e)
├── Acceso: private
├── Tipo retorno: void
├── Lógica (TÚ implementas):
│
├── Paso 1: Mostrar separador visual
│   └── System.out.println("\n" + "=".repeat(50));
│
├── Paso 2: Clasificar excepción con instanceof
│   ├── SI es InsufficientInventoryException:
│   │   ├── Mostrar: "❌ ERROR: Stock insuficiente"
│   │   ├── Mostrar: e.getMessage() (ya tiene formato)
│   │   └── Mostrar: "💡 Sugerencia: Registre entrada primero."
│   │
│   ├── SI es InvalidMovementException:
│   │   ├── Mostrar: "❌ ERROR: Datos inválidos"
│   │   ├── Mostrar: e.getMessage()
│   │   └── Mostrar: "💡 Sugerencia: Verifique los datos ingresados."
│   │
│   ├── SI es DatabaseConnectionException:
│   │   ├── Mostrar: "❌ ERROR CRÍTICO: Problema de conexión"
│   │   ├── Mostrar: "No se pudo conectar a la BD."
│   │   └── Mostrar: "💡 Sugerencia: Contacte al administrador."
│   │
│   ├── SI es SQLException:
│   │   ├── Mostrar: "❌ ERROR DE BASE DE DATOS"
│   │   ├── Mostrar: "Ocurrió un problema al acceder a la BD."
│   │   └── NO mostrar mensaje técnico completo
│   │
│   └── SI es otro Exception:
│       ├── Mostrar: "❌ ERROR INESPERADO"
│       ├── Mostrar: nombre de clase: e.getClass().getSimpleName()
│       └── Mostrar: "💡 Sugerencia: Contacte al administrador."
│
├── Paso 3: Logging técnico (System.err - NO visible para usuario)
│   ├── Mostrar: timestamp + clase excepción + mensaje
│   ├── SI tiene causa (getCause() != null):
│   │   └── Mostrar: "  Causado por: " + causa
│   └── Ejemplo: "[ERROR] 2025-10-18T14:30:45 - SQLException: Connection timeout"
│
├── Paso 4: Mostrar separador final
│   └── System.out.println("=".repeat(50));
│
└── Paso 5: Pausa antes de continuar
    └── System.out.println("\nPresione ENTER para continuar...");
    └── scanner.nextLine();
```

**Tabla de excepciones y sus mensajes:**

| Excepción | Clasificación | Mensaje Usuario | Log Técnico | Sugerencia |
|-----------|--------------|-----------------|-------------|-----------|
| InsufficientInventoryException | Recuperable | Stock insuficiente + cantidades | Completo + timestamp | Registre entrada |
| InvalidMovementException | Recuperable | Datos inválidos + razón | Completo | Verifique datos |
| DatabaseConnectionException | Crítico | Problema de conexión | SQLException original + URL | Contacte admin |
| SQLException | Sistema | Error de BD genérico | Completo | Contacte admin |
| Exception (genérico) | Inesperado | Error inesperado + clase | Completo + stack | Contacte admin |

**Tareas paso a paso:**

1. **Crear método `handleException(Exception e)`:**
   - ¿Dónde? En clase `ConsoleMenu`
   - ¿Acceso? `private`
   - ¿Por qué private? Solo esta clase lo necesita
   - 💡 PISTA: Usa `if (e instanceof TipoExcepcion)` para clasificar

2. **Paso 1 - Clasificar excepciones:**
   - Usa múltiples `if-else if` con `instanceof`
   - Orden importante: ESPECÍFICAS primero (InsufficientInventoryException), GENÉRICAS después (Exception)
   - 💡 PISTA: Si pones `Exception` primero, nunca llegarás a las específicas

3. **Paso 2 - Mensajes para usuario:**
   - NUNCA muestres stack trace al usuario final
   - NUNCA muestres detalles técnicos como SQL o rutas
   - SIEMPRE muestra algo útil que pueda hacer
   - 💡 PISTA: Las excepciones personalizadas ya traen `getMessage()` formateado

4. **Paso 3 - Logging técnico:**
   - Usa `System.err.println()` para errores
   - Incluye: timestamp, clase de excepción, mensaje
   - Si tiene `getCause() != null`, muestra la causa original
   - 💡 PISTA: Este será reemplazado con SLF4J en Fase 9

5. **Paso 4 - Integrar con wizards:**
   - ANTES: cada wizard tiene try-catch duplicado
   - DESPUÉS: cada wizard tiene try-catch simple que llama `handleException(e)`
   - Ejemplo:
     ```
     try {
         movementService.createExitMovement(...);
         System.out.println("✅ Salida registrada exitosamente");
     } catch (Exception e) {
         handleException(e);
     }
     ```
   - 💡 PISTA: Captura Exception genérico porque tus excepciones son hijas de Exception

6. **Paso 5 - Proteger el loop principal:**
   - En `start()`, el while(true) que muestra menú debe estar protegido
   - Cualquier error NO debería romper el programa
   - Capturar `InputMismatchException` por separado (mal input del usuario)
   - Capturar `Exception` genérico para errores inesperados
   - 💡 PISTA: El menú debe SIEMPRE volver a mostrar las opciones

7. **Paso 6 - Método para confirmar salida:**
   - Crear método `private boolean confirmExit()`
   - Pregunta: "¿Está seguro que desea salir? (S/N):"
   - Retorna true si responde "S" o "s"
   - Retorna false si responde otra cosa
   - En salida verdadera, mostrar: "👋 Gracias por usar FORESTECH CLI"

8. **Documento de referencia:**
   - En JAVA_LEARNING_LOG.md, crea tabla de excepciones
   - Columnas: Nombre | Cuándo ocurre | Mensaje usuario | Qué hacer usuario
   - Esto te ayuda a diseñar mejores excepciones futurasamente

4. **Proteger el loop principal:**
   - En `start()`, el while(true) del menú principal debe tener try-catch:
     ```java
     while (true) {
         try {
             displayMainMenu();
             int option = scanner.nextInt();
             scanner.nextLine(); // limpiar buffer
             
             switch (option) {
                 case 1:
                     handleMovementsMenu();
                     break;
                 // ...
                 case 0:
                     if (confirmExit()) {
                         break;
                     }
                     continue;
                 default:
                     System.out.println("❌ Opción inválida");
             }
         } catch (InputMismatchException e) {
             System.out.println("❌ Por favor ingrese un número válido");
             scanner.nextLine(); // limpiar buffer
         } catch (Exception e) {
             // Cualquier otro error no debería romper el menú
             handleException(e);
         }
     }
     ```

5. **Crear método de confirmación de salida:**
   ```java
   private boolean confirmExit() {
       System.out.print("¿Está seguro que desea salir? (S/N): ");
       String response = scanner.nextLine().trim().toUpperCase();
       if (response.equals("S")) {
           System.out.println("\n👋 Gracias por usar FORESTECH CLI");
           return true;
       }
       return false;
   }
   ```

6. **Agregar severidad a los logs (preparación para SLF4J):**
   - INFO: Operaciones exitosas
   - WARN: Errores recuperables (datos inválidos, stock insuficiente)
   - ERROR: Errores de sistema (BD caída, SQL exception)
   - Por ahora usa prefijos: `[INFO]`, `[WARN]`, `[ERROR]`

7. **Documentar cómo agregar nuevas excepciones:**
   - En `JAVA_LEARNING_LOG.md`, documenta:
     - Cómo crear nueva excepción personalizada
     - Cómo agregarla al handler
     - Qué mensaje mostrar al usuario vs qué loggear

**✅ Resultado esperado:**
- Cuando ocurre cualquier error, el usuario ve un mensaje claro y profesional
- El menú NUNCA se cae, siempre vuelve al menú principal
- Los errores técnicos se registran en System.err (visible en consola del desarrollador)
- Todos los errores se presentan con formato consistente
- El usuario recibe sugerencias de qué hacer ante cada tipo de error

**💡 Principio de separación:**
- **Usuario:** Ve mensaje amigable + sugerencia de acción
- **Log técnico:** Ve excepción completa + stack trace + timestamp
- **Desarrollador:** Puede depurar con información técnica sin confundir al usuario

**⚠️ CUIDADO: No expongas información sensible:**
NUNCA muestres al usuario:
- Credenciales de BD
- Rutas del filesystem
- Stack traces completos
- Queries SQL

**🔍 Depuración:**
Para testing, puedes agregar un modo DEBUG que muestre más información:
```java
private static final boolean DEBUG_MODE = true; // Cambiar a false en producción

if (DEBUG_MODE) {
    e.printStackTrace(System.err);
}
```

**⏱️ Tiempo estimado:** 3-4 horas

---

## 🧪 Refuerzos y pruebas sugeridas

**Tabla de pruebas de errores:**

Crea en `JAVA_LEARNING_LOG.md`:

| Error Provocado | Excepción Lanzada | Mensaje al Usuario | Log Técnico | ¿Manejado? |
|-----------------|-------------------|-------------------|--------------|------------|
| Salida sin stock | InsufficientInventoryException | Stock insuficiente + cantidades | Exception completa + timestamp | ✅ |
| Horómetro menor | InvalidMovementException | Datos inválidos + valores | Exception + causa | ✅ |
| BD desconectada | DatabaseConnectionException | Problema de conexión | SQLException + URL | ✅ |
| Cantidad negativa | InvalidMovementException | Cantidad debe ser > 0 | Exception | ✅ |
| Proveedor inexistente | InvalidMovementException | Proveedor no existe | Exception + ID | ❓ |

**Pruebas destructivas:**

1. **Desconectar SQL Server:**
   - Detener el servicio de SQL Server
   - Intentar registrar movimiento
   - Verificar que muestra mensaje claro
   - Verificar que el menú no se cae

2. **Datos inválidos:**
   - Ingresar cantidad negativa
   - Ingresar precio negativo
   - Ingresar horómetro menor al actual
   - Cada uno debe mostrar mensaje específico

3. **Stock insuficiente:**
   - Registrar salida de 1000L cuando solo hay 100L
   - Verificar mensaje con cantidades exactas
   - Verificar que el inventario NO cambió

4. **Simular excepción inesperada:**
   - Temporalmente lanza `new RuntimeException("Test")` en algún método
   - Verifica que el handler genérico la captura
   - Verifica que el menú continúa funcionando

**Ejercicio de refactoring:**
Crea una clase `ErrorLogger` separada con métodos estáticos:
- `ErrorLogger.log(Exception e)` - para logging técnico
- `ErrorLogger.getUserMessage(Exception e)` - retorna mensaje para usuario
- Usa esta clase desde `ConsoleMenu.handleException()`

---

## ✅ Checklist de salida de Fase 7

- [ ] He creado al menos 3 excepciones personalizadas con información de contexto
- [ ] Todos los servicios lanzan excepciones personalizadas en lugar de SQLException genérico
- [ ] Todos los recursos JDBC se cierran con try-with-resources o finally
- [ ] El método `handleException()` maneja todos los tipos de error de forma centralizada
- [ ] El menú principal NO se cae ante ningún error
- [ ] Los mensajes al usuario son claros y NO muestran detalles técnicos
- [ ] Los errores técnicos se registran en System.err con timestamp
- [ ] Documenté todos los tipos de error y su manejo en JAVA_LEARNING_LOG.md
- [ ] Probé desconectar la BD y el sistema responde elegantemente
- [ ] Probé errores de validación y muestran mensajes útiles

**🎯 Desafío final:**
Implementa un sistema de "retry" que, ante `DatabaseConnectionException`, intente reconectar automáticamente 3 veces con delay de 2 segundos entre intentos antes de fallar definitivamente.

---

## 🚀 Próximo paso: FASE 8 - Conceptos Avanzados de Código

En la siguiente fase aprenderás a:
- Programar contra interfaces en lugar de implementaciones concretas
- Crear clases base abstractas para eliminar duplicación
- Usar enums para constantes de dominio (MovementType, FuelType)
- Manejar fechas con `java.time` API en lugar de Strings
- Usar Streams y lambdas para operaciones sobre colecciones

**¿Por qué es importante?** Tu código actual funciona, pero a medida que crece, la duplicación y el acoplamiento se vuelven problemáticos. Fase 8 te enseña patrones profesionales para código mantenible y escalable.

**⏱️ Tiempo total Fase 7:** 10-13 horas distribuidas en 1 semana
