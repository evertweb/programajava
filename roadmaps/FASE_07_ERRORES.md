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

**Tareas paso a paso:**

1. **Crear `InsufficientInventoryException`:**
   - Extender `Exception` (es checked porque el programador DEBE manejarla)
   - Atributos: `String fuelType`, `double required`, `double available`, `String location`
   - Constructor que reciba estos parámetros
   - Sobrescribir `getMessage()` para retornar mensaje claro:
     ```
     Stock insuficiente de [fuelType] en [location]: 
     Requerido: [required]L, Disponible: [available]L, Faltante: [required-available]L
     ```
   - Agregar getters para los atributos (por si alguien necesita los valores)

2. **Crear `InvalidMovementException`:**
   - Extender `Exception`
   - Atributo: `String reason` (razón específica de invalidez)
   - Constructor que reciba el mensaje
   - Constructor alternativo que reciba mensaje Y causa (exception chaining)
   - Ejemplos de uso:
     - "Cantidad debe ser mayor a cero"
     - "Horómetro nuevo (1200) debe ser mayor al actual (1500)"
     - "Proveedor con ID [uuid] no existe"
     - "Precio unitario no puede ser negativo"

3. **Crear `DatabaseConnectionException`:**
   - Extender `RuntimeException` (es unchecked porque es un problema de infraestructura, no de lógica)
   - Atributo: `String dbUrl`
   - Constructor que reciba mensaje, causa (SQLException original) y URL
   - `getMessage()` retorna:
     ```
     Error de conexión a base de datos [dbUrl]: [mensaje causa]
     Verifica que el servidor esté disponible y las credenciales sean correctas.
     ```
   - Exception chaining: guardar la SQLException original con `super(message, cause)`

4. **Modificar `MovementService.createExitMovement()`:**
   - ANTES de la transacción, consultar stock disponible
   - Si `available < movement.getQuantity()`, lanzar:
     ```java
     throw new InsufficientInventoryException(
         movement.getFuelType(),
         movement.getQuantity(),
         available,
         movement.getLocation()
     );
     ```
   - Declarar que el método lanza esta excepción: `throws InsufficientInventoryException`

5. **Modificar validaciones en servicios:**
   - En lugar de retornar `false` o imprimir errores, lanzar `InvalidMovementException`
   - Ejemplo en `createEntryMovement()`: si `quantity <= 0`, lanzar:
     ```java
     throw new InvalidMovementException("Cantidad debe ser mayor a cero: " + quantity);
     ```
   - Ejemplo en `createExitMovement()`: si `newHorometro <= currentHorometro`, lanzar:
     ```java
     throw new InvalidMovementException(
         "Horómetro nuevo (" + newHorometro + ") debe ser mayor al actual (" + currentHorometro + ")"
     );
     ```

6. **Modificar `DatabaseConnection.getConnection()`:**
   - Envolver el `try-catch` de SQLException
   - Si falla, lanzar `DatabaseConnectionException`:
     ```java
     catch (SQLException e) {
         throw new DatabaseConnectionException(
             "No se pudo establecer conexión",
             e,  // causa original
             DB_URL
         );
     }
     ```

7. **Actualizar `ConsoleMenu` para capturar excepciones:**
   - En `registerExitWizard()`, envolver la llamada al servicio:
     ```java
     try {
         movementService.createExitMovement(movement, vehicleId);
         System.out.println("✅ Salida registrada exitosamente");
     } catch (InsufficientInventoryException e) {
         System.out.println("❌ " + e.getMessage());
         // NO mostrar stack trace al usuario
     } catch (InvalidMovementException e) {
         System.out.println("❌ Datos inválidos: " + e.getMessage());
     }
     ```

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

**Tareas paso a paso:**

1. **Auditar código actual:**
   - Busca TODOS los lugares donde usas `Connection.getConnection()`
   - Busca TODOS los `PreparedStatement` y `ResultSet`
   - Anota cuáles NO tienen `close()` explícito o try-with-resources

2. **Convertir a try-with-resources (métodos de consulta):**
   - **ANTES:**
     ```java
     Connection conn = null;
     PreparedStatement stmt = null;
     ResultSet rs = null;
     try {
         conn = DatabaseConnection.getConnection();
         stmt = conn.prepareStatement("SELECT ...");
         rs = stmt.executeQuery();
         // procesar resultados
     } catch (SQLException e) {
         // manejar error
     } finally {
         if (rs != null) rs.close();
         if (stmt != null) stmt.close();
         if (conn != null) conn.close();
     }
     ```
   - **DESPUÉS:**
     ```java
     String query = "SELECT ...";
     try (Connection conn = DatabaseConnection.getConnection();
          PreparedStatement stmt = conn.prepareStatement(query);
          ResultSet rs = stmt.executeQuery()) {
         
         // procesar resultados
         
     } catch (SQLException e) {
         throw new DatabaseConnectionException("Error al consultar", e, DB_URL);
     }
     // close() se llama automáticamente en orden inverso: rs, stmt, conn
     ```

3. **Caso especial: transacciones:**
   - En transacciones NO puedes usar try-with-resources para Connection porque necesitas control manual de `commit()`/`rollback()`
   - Patrón recomendado:
     ```java
     Connection conn = null;
     try {
         conn = DatabaseConnection.getConnection();
         conn.setAutoCommit(false);
         
         // Operación 1 (usa try-with-resources para PreparedStatement)
         try (PreparedStatement stmt1 = conn.prepareStatement("INSERT ...")) {
             stmt1.setString(1, value);
             stmt1.executeUpdate();
         }
         
         // Operación 2
         try (PreparedStatement stmt2 = conn.prepareStatement("UPDATE ...")) {
             stmt2.setDouble(1, quantity);
             stmt2.executeUpdate();
         }
         
         conn.commit();
         
     } catch (SQLException e) {
         if (conn != null) {
             try {
                 conn.rollback();
             } catch (SQLException rollbackEx) {
                 // Loggear pero no ocultar la excepción original
             }
         }
         throw new DatabaseConnectionException("Transacción fallida", e, DB_URL);
     } finally {
         if (conn != null) {
             try {
                 conn.setAutoCommit(true);  // Restaurar estado
                 conn.close();
             } catch (SQLException e) {
                 // Loggear
             }
         }
     }
     ```

4. **Orden de cierre:**
   - SIEMPRE cerrar en orden inverso a la creación:
     1. ResultSet (si existe)
     2. PreparedStatement
     3. Connection
   - try-with-resources hace esto automáticamente

5. **Auditar y refactorizar cada servicio:**
   - `MovementService`: convertir todos los métodos de consulta
   - `VehicleService`: convertir todos los métodos
   - `SupplierService`: convertir todos los métodos
   - `InventoryService`: convertir todos los métodos
   - Mantener patrón manual solo en transacciones

6. **Logging en catch:**
   - Mientras no tengas SLF4J, usa `System.err.println()` para errores:
     ```java
     catch (SQLException e) {
         System.err.println("[ERROR] " + LocalDateTime.now() + " - " + e.getMessage());
         e.printStackTrace(System.err);
         throw new DatabaseConnectionException("...", e, DB_URL);
     }
     ```
   - Documenta que esto se reemplazará con logger en Fase 9

7. **Pruebas de fuga de recursos:**
   - Crea un loop que ejecute 100 operaciones seguidas
   - Monitorea conexiones abiertas en SQL Server:
     ```sql
     SELECT * FROM sys.dm_exec_connections
     WHERE session_id > 50;
     ```
   - Después del loop, deberías tener 0 o muy pocas conexiones abiertas

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

**Tareas paso a paso:**

1. **Crear método `handleException(Exception e)` en ConsoleMenu:**
   - Método privado que recibe cualquier Exception
   - Analiza el tipo de excepción con `instanceof`
   - Construye mensaje apropiado para el usuario
   - Loggea detalles técnicos

2. **Implementación del handler:**
   ```java
   private void handleException(Exception e) {
       System.out.println("\n" + "=".repeat(50));
       
       // Clasificar por tipo
       if (e instanceof InsufficientInventoryException) {
           InsufficientInventoryException iie = (InsufficientInventoryException) e;
           System.out.println("❌ ERROR: Stock insuficiente");
           System.out.println(e.getMessage());
           System.out.println("\n💡 Sugerencia: Registre una entrada de combustible primero.");
           
       } else if (e instanceof InvalidMovementException) {
           System.out.println("❌ ERROR: Datos inválidos");
           System.out.println(e.getMessage());
           System.out.println("\n💡 Sugerencia: Verifique los datos ingresados.");
           
       } else if (e instanceof DatabaseConnectionException) {
           System.out.println("❌ ERROR CRÍTICO: Problema de conexión a BD");
           System.out.println("No se pudo conectar a la base de datos.");
           System.out.println("\n💡 Sugerencia: Contacte al administrador del sistema.");
           
       } else if (e instanceof SQLException) {
           System.out.println("❌ ERROR DE BASE DE DATOS");
           System.out.println("Ocurrió un problema al acceder a la base de datos.");
           // NO mostrar SQLException completo al usuario
           
       } else {
           // Error inesperado
           System.out.println("❌ ERROR INESPERADO");
           System.out.println("Ocurrió un problema inesperado: " + e.getClass().getSimpleName());
           System.out.println("\n💡 Sugerencia: Contacte al administrador.");
       }
       
       // Logging técnico (no visible para usuario)
       System.err.println("[ERROR] " + LocalDateTime.now() + " - " + e.getClass().getName() + ": " + e.getMessage());
       if (e.getCause() != null) {
           System.err.println("  Causa: " + e.getCause());
       }
       
       System.out.println("=".repeat(50));
       System.out.println("\nPresione ENTER para continuar...");
       scanner.nextLine();
   }
   ```

3. **Usar el handler en todos los wizards:**
   - **ANTES:**
     ```java
     try {
         movementService.createExitMovement(movement, vehicleId);
         System.out.println("✅ Salida registrada");
     } catch (InsufficientInventoryException e) {
         System.out.println("❌ " + e.getMessage());
     } catch (SQLException e) {
         System.out.println("❌ Error de BD: " + e);
     }
     ```
   - **DESPUÉS:**
     ```java
     try {
         movementService.createExitMovement(movement, vehicleId);
         System.out.println("✅ Salida registrada exitosamente");
     } catch (Exception e) {
         handleException(e);
     }
     ```

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
