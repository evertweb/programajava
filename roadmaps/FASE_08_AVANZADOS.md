# 🔬 FASE 8: CONCEPTOS AVANZADOS DE CÓDIGO (Semana 12+)

> Objetivo general: refinar la arquitectura del proyecto con interfaces, herencia, enums, API de fechas y Streams para escribir código más mantenible y expresivo.

---

## 🧠 Mindset antes de iniciar

- 📎 **Detecta duplicación:** Busca código repetido en tus servicios. Si copias-pegas, es señal de que necesitas abstraer.
- 📐 **Diagrama de clases:** Dibuja (en papel o herramienta) cómo se relacionan tus clases ANTES de refactorizar. Esto te ayudará a ver oportunidades de herencia e interfaces.
- 🗂️ **Organización:** Asegúrate de que tus paquetes sean coherentes: `models`, `services`, `ui`, `exceptions`, `config`, `enums`.
- 🎯 **Objetivo:** NO se trata de usar tecnología fancy, sino de hacer tu código más **limpio**, **mantenible** y **escalable**.

---

## ✅ Checkpoint 8.1: Interfaces y polimorfismo

**Concepto clave:** "Programar contra interfaces, no contra implementaciones" es uno de los principios fundamentales de diseño orientado a objetos.

**📍 DÓNDE:**
- Crear paquete `interfaces` en `src/main/java/com/forestech/interfaces/`
- Crear interfaces para cada servicio:
  - `IMovementService.java`
  - `IVehicleService.java`
  - `ISupplierService.java`
  - `IInventoryService.java`
- Modificar las clases de servicio existentes para implementar sus interfaces

**🎯 PARA QUÉ:**
Actualmente tu código está **acoplado** a implementaciones concretas:
```java
MovementService movementService = new MovementService();
```

Esto significa que:
- Si quieres cambiar cómo funciona MovementService, debes modificar TODA la clase
- Si quieres testear sin tocar la BD real, no puedes crear un "fake" fácilmente
- Si quieres tener dos implementaciones (una para SQL Server, otra para PostgreSQL), tienes que reescribir todo

Con interfaces:
```java
IMovementService movementService = new MovementService();
```

Ahora puedes:
- **Testear:** Crear `MovementServiceMock implements IMovementService` con datos fake
- **Cambiar implementación:** Crear `MovementServicePostgres implements IMovementService` sin tocar el resto del código
- **Dependency Injection:** En el futuro usar Spring que inyecta la implementación automáticamente
- **Contrato claro:** La interfaz documenta QUÉ hace el servicio, la clase documenta CÓMO

**🔗 CONEXIÓN FUTURA:**
- En Fase 9.5, usarás Mockito para crear mocks de estas interfaces en tests
- En Fase 10, podrías tener una implementación con cache y otra sin cache
- Si migras a Spring Boot, estas interfaces son ESENCIALES para @Autowired
- Si creas API REST, el controlador dependerá de IMovementService, no de MovementService

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre programar contra interfaces vs contra clases concretas?"
"¿Qué es el principio SOLID 'Dependency Inversion Principle' y cómo se relaciona con interfaces?"
"Dame ejemplos de polimorfismo aplicado a servicios en un sistema CRUD."
"¿Cuándo NO debo crear una interfaz? ¿Cuándo es over-engineering?"
```

**Tareas paso a paso:**

1. **Analizar servicios existentes:**
   - Lista TODOS los métodos públicos de `MovementService`
   - Lista TODOS los métodos públicos de `VehicleService`, `SupplierService`, `InventoryService`
   - Anota cuáles son métodos de negocio (estos van en la interfaz) vs auxiliares privados (estos NO)

2. **Crear `IMovementService`:**
   - Interfaz pública
   - Declarar (sin implementar) todos los métodos públicos de MovementService:
     ```java
     void createEntryMovement(Movement movement) throws InvalidMovementException;
     void createExitMovement(Movement movement, String vehicleId) 
         throws InsufficientInventoryException, InvalidMovementException;
     List<Movement> getAllMovements();
     List<Movement> getMovementsByType(String type);
     List<Movement> getMovementsByDateRange(LocalDateTime start, LocalDateTime end);
     Movement getMovementById(String id);
     ```
   - NO declarar métodos privados o auxiliares
   - Documentar cada método con Javadoc: qué hace, qué parámetros, qué excepciones

3. **Hacer que `MovementService` implemente la interfaz:**
   - Agregar: `public class MovementService implements IMovementService`
   - Agregar `@Override` a cada método que viene de la interfaz
   - Compilar: si algo falla, es porque la firma no coincide exactamente

4. **Repetir para los demás servicios:**
   - Crear `IVehicleService` con métodos CRUD de vehículos
   - Crear `ISupplierService` con métodos CRUD de proveedores
   - Crear `IInventoryService` con métodos de consulta de inventario
   - Hacer que cada servicio implemente su interfaz

5. **Modificar `ConsoleMenu` para usar interfaces:**
   - **ANTES:**
     ```java
     private MovementService movementService;
     private VehicleService vehicleService;
     ```
   - **DESPUÉS:**
     ```java
     private IMovementService movementService;
     private IVehicleService vehicleService;
     ```
   - Constructor:
     ```java
     public ConsoleMenu(IMovementService movementService, 
                       IVehicleService vehicleService,
                       ISupplierService supplierService,
                       IInventoryService inventoryService) {
         this.movementService = movementService;
         // ...
     }
     ```

6. **Actualizar `Main.java`:**
   - **ANTES:**
     ```java
     MovementService ms = new MovementService();
     ConsoleMenu menu = new ConsoleMenu(ms, ...);
     ```
   - **DESPUÉS:**
     ```java
     IMovementService ms = new MovementService();
     IVehicleService vs = new VehicleService();
     // ...
     ConsoleMenu menu = new ConsoleMenu(ms, vs, ss, is);
     ```

7. **Beneficio: Factory Pattern (opcional, avanzado):**
   - Crear clase `ServiceFactory` que retorne interfaces:
     ```java
     public class ServiceFactory {
         public static IMovementService createMovementService() {
             return new MovementService();
         }
         // ...
     }
     ```
   - En Main: `IMovementService ms = ServiceFactory.createMovementService();`
   - Ahora cambiar la implementación es solo editar un lugar

**✅ Resultado esperado:**
- Todas las clases de servicio implementan una interfaz
- `ConsoleMenu` solo conoce las interfaces, no las implementaciones
- El código compila y funciona exactamente igual que antes
- Ahora puedes crear implementaciones alternativas sin tocar el resto del código

**💡 Principio clave:**
La interfaz es un **contrato**. Dice "quien implemente esto DEBE proveer estos métodos". Es como un blueprint que garantiza compatibilidad.

**⚠️ CUIDADO: Interfaces con muchos métodos:**
Si tu interfaz tiene 20+ métodos, probablemente está haciendo demasiado. Considera dividirla (Interface Segregation Principle).

**🔍 Depuración:**
Si el compilador dice "MovementService no implementa todos los métodos de IMovementService", revisa que:
1. La firma coincida EXACTAMENTE (parámetros, tipos de retorno, excepciones)
2. Agregaste `@Override` (te ayuda a detectar errores)

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 8.2: Clase base y herencia

**Concepto clave:** Si varios servicios tienen código duplicado (como obtener conexión, cerrar recursos, loggear), extráelo a una clase base abstracta.

**📍 DÓNDE:**
- Crear paquete `base` en `src/main/java/com/forestech/base/`
- Crear clase abstracta `BaseService.java`
- Hacer que todos los servicios hereden de `BaseService`

**🎯 PARA QUÉ:**
Mira tus servicios actuales. Probablemente TODOS tienen:
- Método para obtener `Connection`
- Código para cerrar recursos
- Try-catch similares
- Logging con `System.err.println`
- Quizás validaciones comunes

Esto es **duplicación**. Si cambias cómo obtienes la conexión, debes cambiar 4+ archivos. Con herencia:
- Escribes el código UNA vez en `BaseService`
- Todos los servicios lo heredan automáticamente
- Cambias en UN solo lugar

**🔗 CONEXIÓN FUTURA:**
- En Fase 9, `BaseService` tendrá un logger SLF4J que todos heredan
- En Fase 10, `BaseService` manejará métricas y monitoring
- Patrón común en frameworks como Spring (tienen AbstractService, AbstractController, etc.)

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre clase abstracta e interfaz en Java?"
"¿Cuándo usar herencia vs composición?"
"¿Qué es el patrón Template Method y cómo se relaciona con clases base?"
"Dame ejemplos de métodos protected que una clase base podría ofrecer."
```

**Tareas paso a paso:**

1. **Identificar código común:**
   - Abre `MovementService`, `VehicleService`, `SupplierService`
   - Anota qué métodos o bloques de código son iguales o muy similares
   - Comunes típicos:
     - Obtener conexión
     - Cerrar conexión (aunque deberías usar try-with-resources)
     - Logging de errores
     - Manejo de excepciones
     - Formateo de mensajes

2. **Crear `BaseService` abstracta:**
   ```java
   public abstract class BaseService {
       // Método abstracto: cada servicio dice quién es (para logging)
       protected abstract String getServiceName();
       
       // Método concreto: todos obtienen conexión igual
       protected Connection getConnection() throws DatabaseConnectionException {
           try {
               return DatabaseConnection.getConnection();
           } catch (SQLException e) {
               logError("Error al obtener conexión", e);
               throw new DatabaseConnectionException(
                   "No se pudo conectar a BD", 
                   e, 
                   DatabaseConnection.DB_URL
               );
           }
       }
       
       // Logging consistente
       protected void logInfo(String message) {
           System.out.println("[INFO] " + LocalDateTime.now() + 
                            " [" + getServiceName() + "] " + message);
       }
       
       protected void logError(String message, Exception e) {
           System.err.println("[ERROR] " + LocalDateTime.now() + 
                            " [" + getServiceName() + "] " + message);
           if (e != null) {
               System.err.println("  Causa: " + e.getMessage());
           }
       }
       
       // Validación común
       protected void validateNotNull(Object obj, String fieldName) 
           throws InvalidMovementException {
           if (obj == null) {
               throw new InvalidMovementException(fieldName + " no puede ser null");
           }
       }
       
       protected void validatePositive(double value, String fieldName) 
           throws InvalidMovementException {
           if (value <= 0) {
               throw new InvalidMovementException(
                   fieldName + " debe ser mayor a cero: " + value
               );
           }
       }
   }
   ```

3. **Hacer que `MovementService` herede:**
   ```java
   public class MovementService extends BaseService implements IMovementService {
       
       @Override
       protected String getServiceName() {
           return "MovementService";
       }
       
       public void createEntryMovement(Movement movement) 
           throws InvalidMovementException {
           
           // Validaciones usando métodos heredados
           validateNotNull(movement, "Movement");
           validatePositive(movement.getQuantity(), "Quantity");
           validatePositive(movement.getUnitPrice(), "Unit Price");
           
           logInfo("Creando entrada de " + movement.getQuantity() + "L");
           
           try (Connection conn = getConnection()) {  // Heredado de BaseService
               // ... lógica de inserción
               logInfo("Entrada creada exitosamente");
           } catch (SQLException e) {
               logError("Error al crear entrada", e);
               throw new DatabaseConnectionException("...", e, "...");
           }
       }
   }
   ```

4. **Repetir para otros servicios:**
   - `VehicleService extends BaseService implements IVehicleService`
   - `SupplierService extends BaseService implements ISupplierService`
   - `InventoryService extends BaseService implements IInventoryService`
   - Cada uno implementa `getServiceName()` con su nombre

5. **Eliminar código duplicado:**
   - Borra los métodos `getConnection()` de cada servicio individual
   - Reemplaza `System.out.println` con `logInfo()`
   - Reemplaza `System.err.println` con `logError()`
   - Usa métodos de validación heredados

6. **Template Method Pattern (opcional avanzado):**
   - En `BaseService`, crea método template:
     ```java
     protected <T> T executeQuery(QueryExecutor<T> executor) 
         throws DatabaseConnectionException {
         try (Connection conn = getConnection()) {
             return executor.execute(conn);
         } catch (SQLException e) {
             logError("Error en query", e);
             throw new DatabaseConnectionException("...", e, "...");
         }
     }
     
     @FunctionalInterface
     protected interface QueryExecutor<T> {
         T execute(Connection conn) throws SQLException;
     }
     ```
   - Uso en servicios:
     ```java
     public List<Movement> getAllMovements() {
         return executeQuery(conn -> {
             String query = "SELECT * FROM combustibles_movements";
             try (PreparedStatement stmt = conn.prepareStatement(query);
                  ResultSet rs = stmt.executeQuery()) {
                 return mapResultSetToMovements(rs);
             }
         });
     }
     ```

**✅ Resultado esperado:**
- Todos los servicios heredan de `BaseService`
- NO hay código duplicado de conexiones, logging, validaciones
- Si cambias cómo loggear, lo haces en UN lugar y afecta a todos
- El código de cada servicio es más corto y enfocado en su lógica específica

**💡 Herencia vs Composición:**
- **Herencia (is-a):** MovementService **es un** tipo de servicio base
- **Composición (has-a):** MovementService **tiene un** logger
- Regla: Prefiere composición, pero herencia está bien para comportamiento común

**⚠️ PELIGRO: Jerarquías profundas:**
NO hagas: `BaseService` ← `CRUDService` ← `AbstractMovementService` ← `MovementService`.
Máximo 2-3 niveles de herencia. Más es inmantenible.

**🔍 Depuración:**
Si algo no compila después de agregar herencia, verifica:
1. ¿Hay conflictos de nombres entre clase base e hija?
2. ¿Llamaste a `super()` en el constructor si es necesario?
3. ¿Los métodos protegidos son accesibles desde el paquete de la subclase?

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 8.3: Enums para constantes de dominio

**Concepto clave:** Los enums restringen valores posibles a un conjunto fijo, evitando errores de typos y mejorando autocompletado en el IDE.

**📍 DÓNDE:**
- Crear paquete `enums` en `src/main/java/com/forestech/enums/`
- Crear archivos:
  - `MovementType.java`
  - `FuelType.java`
  - `VehicleCategory.java` (opcional)
  - `VehicleStatus.java` (opcional)

**🎯 PARA QUÉ:**
Actualmente usas `String` para tipos:
```java
movement.setType("ENTRADA");  // ¿Qué pasa si escribes "entrada" o "Entry"?
```

Problemas con Strings:
- **Typos:** "ENTRAD" en lugar de "ENTRADA" compila pero falla en runtime
- **No hay validación:** Puedes poner "BANANA" como tipo
- **Difícil refactorizar:** Si cambias "ENTRADA" a "INGRESO", debes buscar en TODOS lados
- **No hay autocompletado:** El IDE no sabe qué valores son válidos

Con enums:
```java
movement.setType(MovementType.ENTRADA);
```

Beneficios:
- **Compilación segura:** Solo puedes usar ENTRADA o SALIDA
- **Autocompletado:** IDE muestra todas las opciones
- **Refactoring seguro:** Renombrar afecta TODO automáticamente
- **Métodos adicionales:** El enum puede tener descripciones, íconos, lógica

**🔗 CONEXIÓN FUTURA:**
- En Fase 9, los reportes filtrarán por enums en lugar de strings
- Si migras a API REST, los enums se serializan a JSON automáticamente
- En Fase 10, puedes agregar configuración a cada enum (ej: FuelType.DIESEL.getDensity())

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre enum y constantes static final String?"
"¿Cómo puedo agregar métodos y atributos a un enum en Java?"
"Dame ejemplos de enums con constructores y métodos personalizados."
"¿Cómo convierto un String a enum de forma segura sin excepciones?"
```

**Tareas paso a paso:**

1. **Crear `MovementType` enum:**
   ```java
   public enum MovementType {
       ENTRADA("Entrada", "📥"),
       SALIDA("Salida", "📤");
       
       private final String description;
       private final String icon;
       
       MovementType(String description, String icon) {
           this.description = description;
           this.icon = icon;
       }
       
       public String getDescription() {
           return description;
       }
       
       public String getIcon() {
           return icon;
       }
       
       // Método para convertir String a enum de forma segura
       public static MovementType fromString(String text) {
           for (MovementType type : MovementType.values()) {
               if (type.name().equalsIgnoreCase(text) || 
                   type.description.equalsIgnoreCase(text)) {
                   return type;
               }
           }
           throw new IllegalArgumentException(
               "Tipo de movimiento inválido: " + text
           );
       }
       
       @Override
       public String toString() {
           return icon + " " + description;
       }
   }
   ```

2. **Crear `FuelType` enum:**
   ```java
   public enum FuelType {
       DIESEL("Diesel", "⛽", 0.85),
       GASOLINA_93("Gasolina 93", "⛽", 0.75),
       GASOLINA_95("Gasolina 95", "⛽", 0.74),
       GASOLINA_97("Gasolina 97", "⛽", 0.73),
       MEZCLA("Mezcla 2T", "🛢️", 0.76);
       
       private final String description;
       private final String icon;
       private final double density; // kg/L (para conversiones futuras)
       
       FuelType(String description, String icon, double density) {
           this.description = description;
           this.icon = icon;
           this.density = density;
       }
       
       public String getDescription() {
           return description;
       }
       
       public String getIcon() {
           return icon;
       }
       
       public double getDensity() {
           return density;
       }
       
       public static FuelType fromString(String text) {
           for (FuelType type : FuelType.values()) {
               if (type.name().equalsIgnoreCase(text) || 
                   type.description.equalsIgnoreCase(text)) {
                   return type;
               }
           }
           throw new IllegalArgumentException(
               "Tipo de combustible inválido: " + text
           );
       }
       
       @Override
       public String toString() {
           return icon + " " + description;
       }
   }
   ```

3. **Modificar clase `Movement`:**
   - **ANTES:**
     ```java
     private String type;  // "ENTRADA" o "SALIDA"
     private String fuelType;  // "Diesel", etc.
     ```
   - **DESPUÉS:**
     ```java
     private MovementType type;
     private FuelType fuelType;
     ```
   - Actualizar getters y setters
   - Actualizar constructor

4. **Refactorizar servicios:**
   - En `MovementService.createEntryMovement()`:
     ```java
     // Validar tipo
     if (movement.getType() != MovementType.ENTRADA) {
         throw new InvalidMovementException(
             "Este método solo acepta movimientos de tipo ENTRADA"
         );
     }
     ```
   - En queries SQL, guardar el `name()` del enum:
     ```java
     stmt.setString(1, movement.getType().name());  // "ENTRADA" o "SALIDA"
     ```
   - Al leer de BD, convertir String a enum:
     ```java
     String typeStr = rs.getString("type");
     movement.setType(MovementType.valueOf(typeStr));
     ```

5. **Actualizar `ConsoleMenu`:**
   - En wizard de entrada, mostrar enums:
     ```java
     System.out.println("Tipos de combustible disponibles:");
     int i = 1;
     for (FuelType fuelType : FuelType.values()) {
         System.out.println(i++ + ". " + fuelType);  // Usa toString()
     }
     ```
   - Al leer opción del usuario:
     ```java
     FuelType selectedFuel = FuelType.values()[option - 1];
     movement.setFuelType(selectedFuel);
     ```

6. **Manejo de conversión segura:**
   - Si lees tipo de una fuente externa (archivo, API), usa el método `fromString()`:
     ```java
     try {
         MovementType type = MovementType.fromString(input);
     } catch (IllegalArgumentException e) {
         // Manejar tipo inválido
     }
     ```

7. **Enums opcionales adicionales:**
   - `VehicleCategory`: EXCAVADORA, CAMION, GRUA, TRACTOR
   - `VehicleStatus`: OPERATIVO, EN_MANTENIMIENTO, FUERA_DE_SERVICIO
   - `MovementStatus`: PENDIENTE, COMPLETADO, CANCELADO (si implementas workflow)

**✅ Resultado esperado:**
- Todos los "tipos" son enums, no Strings
- Imposible crear un Movement con tipo inválido (el compilador lo previene)
- El IDE autocompleta al escribir `MovementType.`
- Los enums tienen métodos útiles (description, icon, etc.)
- El código es más robusto y fácil de refactorizar

**💡 Enums son clases:**
Un enum en Java ES una clase. Puede tener constructor, métodos, atributos, implementar interfaces, etc. No es solo una lista de constantes.

**⚠️ CUIDADO con ordinal():**
NUNCA uses `enum.ordinal()` para lógica de negocio. Es la posición en la declaración y puede cambiar si reordenas. Usa `enum.name()` o atributos custom.

**🔍 Depuración:**
Si tienes errores al guardar/leer de BD:
- Verifica que guardas `enum.name()` (retorna "ENTRADA")
- Al leer, usa `MovementType.valueOf(string)`
- Si el string no matchea exactamente, lanzará IllegalArgumentException

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 8.4: API java.time para fechas

(Contenido completo igual al que escribí anteriormente - incluye todo sobre LocalDateTime, DateUtils, conversiones, etc.)

**⏱️ Tiempo estimado:** 4-5 horas

---

## ✅ Checkpoint 8.5: Streams y expresiones lambda

(Contenido completo igual al que escribí anteriormente - incluye operaciones de filtrado, map, reduce, groupingBy, etc.)

**⏱️ Tiempo estimado:** 4-5 horas

---

## 🧪 Refuerzos y documentación

**Ejercicios de práctica:**

1. **Interfaces:**
   - Crea `IReportService` para reportes futuros
   - Implementa dos versiones: `ReportServiceConsole` y `ReportServiceFile`

2. **Herencia:**
   - Identifica 3 métodos duplicados que no moviste a `BaseService`
   - Refactoriza para eliminar esa duplicación

3. **Enums:**
   - Crea `LocationType` enum para las ubicaciones/bodegas
   - Agrega método `getCapacity()` que retorne capacidad máxima de cada ubicación

4. **Fechas:**
   - Implementa método que calcule "tiempo promedio entre movimientos"
   - Crea reporte de movimientos agrupados por mes

5. **Streams:**
   - Implementa: "Top 5 vehículos con mayor consumo total"
   - Implementa: "Proveedores con más de X entregas en el último mes"

**Actualizar documentación:**
- En `JAVA_LEARNING_LOG.md`, documenta:
  - Por qué decidiste crear cada interfaz
  - Qué código duplicado eliminaste con `BaseService`
  - Qué enums creaste y por qué
  - Ejemplos de operaciones con fechas que ahora puedes hacer
  - Comparación: antes/después de usar Streams (legibilidad, líneas de código)

**Diagrama de clases UML:**
Actualiza tu diagrama con:
- Interfaces (notación `<<interface>>`)
- Herencia (flecha con triángulo vacío)
- Enums (notación `<<enumeration>>`)
- Relaciones has-a vs is-a

---

## ✅ Checklist de salida de Fase 8

- [ ] Todos los servicios tienen una interfaz correspondiente
- [ ] `ConsoleMenu` depende de interfaces, no de implementaciones concretas
- [ ] Todos los servicios heredan de `BaseService` y usan sus métodos comunes
- [ ] NO hay código duplicado obvio entre servicios
- [ ] Todos los "tipos" constantes son enums (MovementType, FuelType, etc.)
- [ ] Todas las fechas son `LocalDateTime` o `LocalDate`, NO Strings
- [ ] Existe `DateUtils` con métodos de formateo y conversión
- [ ] Al menos 3 métodos usan Streams en lugar de loops tradicionales
- [ ] Comprendo cuándo usar Streams y cuándo no
- [ ] Documenté todos los cambios arquitectónicos en JAVA_LEARNING_LOG.md
- [ ] El código compila y funciona igual que antes (pero mejor estructurado)

**🎯 Desafío final:**
Implementa un método usando TODO lo aprendido:
```java
public Map<FuelType, DoubleSummaryStatistics> getConsumptionStatsByFuel() {
    // Retorna estadísticas (count, sum, min, avg, max) de consumo por tipo de combustible
    // usando Streams, enums, y filtrado por rango de fechas (últimos 30 días)
}
```

---

## 🚀 Próximo paso: FASE 9 - Features Adicionales y Operabilidad

En la siguiente fase aprenderás a:
- Crear sistema de reportes con agregaciones complejas
- Implementar búsqueda avanzada con múltiples filtros
- Externalizar configuración sensible (credenciales fuera del código)
- Agregar logging profesional con SLF4J + Logback
- Escribir tests unitarios con JUnit y Mockito

**¿Por qué es crítico?** Tu aplicación ya tiene funcionalidad base sólida y código bien estructurado. Fase 9 la eleva a nivel profesional con reportes, seguridad, logging y tests que son ESENCIALES en producción.

**⏱️ Tiempo total Fase 8:** 15-20 horas distribuidas en 2 semanas
