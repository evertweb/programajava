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
   ```
   Servicios existentes:
   ├── MovementService
   │   ├── Métodos: createEntryMovement(), createExitMovement(), getAllMovements(), etc.
   │   └── Clasifica: ¿Cuáles son públicos de negocio vs privados/auxiliares?
   ├── VehicleService
   │   ├── Métodos a analizar
   │   └── (Aplicar mismo análisis)
   ├── SupplierService
   │   └── (Aplicar mismo análisis)
   └── InventoryService
       └── (Aplicar mismo análisis)
   
   PREGUNTA: ¿Cuál es la diferencia entre un método de negocio y uno auxiliar?
   PISTA: Los métodos de negocio son los que otros componentes necesitan llamar.
   ```

2. **Crear `IMovementService` interfaz:**
   ```
   Archivo: interfaces/IMovementService.java
   ├── Tipo: interface (no class)
   ├── Visibilidad: public
   ├── Métodos a declarar (TÚ identificas cuáles van aquí):
   │   ├── Crear movimiento de entrada
   │   ├── Crear movimiento de salida
   │   ├── Obtener todos los movimientos
   │   ├── Buscar por tipo
   │   ├── Buscar por rango de fechas
   │   └── Obtener por ID
   └── Documentación: Cada método con Javadoc
       └── PISTA: Usa /** */ para comentarios Javadoc
   
   PREGUNTA: ¿Cuál es la firma correcta de createEntryMovement()?
   PISTA: Mira qué parámetros y excepciones espera MovementService.
   ```

3. **Hacer que `MovementService` implemente `IMovementService`:**
   ```
   Pasos:
   ├── 1. Cambiar firma de clase:
   │   └── de: public class MovementService
   │       a:  public class MovementService implements IMovementService
   ├── 2. Para CADA método de la interfaz:
   │   ├── Agregue anotación: @Override
   │   └── Verifica que firmas coincidan exactamente
   └── 3. Compilar y corregir errores
       └── PISTA: Si no compila, revisa que cada método tenga el @Override
   
   PREGUNTA: ¿Qué pasa si la firma de un método NO coincide con la interfaz?
   PISTA: El compilador dirá "no implementa todos los métodos de IMovementService"
   ```

4. **Crear interfaces para otros servicios:**
   ```
   Repite el patrón para:
   ├── IVehicleService (interfaces/IVehicleService.java)
   ├── ISupplierService (interfaces/ISupplierService.java)
   └── IInventoryService (interfaces/IInventoryService.java)
   
   PREGUNTA: ¿Qué métodos públicos tiene VehicleService?
   ACCIÓN: Crea la interfaz con esos métodos (sin implementar).
   ```

5. **Refactorizar `ConsoleMenu` para usar interfaces:**
   ```
   Cambios necesarios:
   ├── Atributos: Declara con tipo interfaz, no implementación
   │   ├── ANTES: private MovementService movementService;
   │   ├── DESPUÉS: private IMovementService movementService;
   │   └── (Repite para otros servicios)
   ├── Constructor: Acepta interfaces como parámetros
   │   ├── parámetro 1: IMovementService
   │   ├── parámetro 2: IVehicleService
   │   ├── parámetro 3: ISupplierService
   │   └── parámetro 4: IInventoryService
   └── Asignación: Los parámetros se asignan a los atributos (this.x = x)
   
   PREGUNTA: ¿Por qué cambiar a interfaces si funciona igual?
   PISTA: Porque ahora ConsoleMenu NO conoce si es MovementService, 
          MovementServicePostgres, o MovementServiceMock.
   ```

6. **Actualizar `Main.java`:**
   ```
   Flujo actual:
   ├── 1. Crear instancias de servicios (implementaciones concretas)
   │   ├── IMovementService ms = new MovementService();
   │   ├── IVehicleService vs = new VehicleService();
   │   └── (ídem otros servicios)
   ├── 2. Pasar al menú como interfaces
   │   └── ConsoleMenu menu = new ConsoleMenu(ms, vs, ss, is);
   └── 3. El menú solo ve las interfaces
   
   ACCIÓN: Realiza estos cambios en tu Main actual.
   PREGUNTA: ¿Qué diferencia hay entre new MovementService() y new IMovementService()?
   PISTA: No puedes instanciar interfaces, solo implementaciones.
   ```

7. **OPCIONAL - Factory Pattern (opcional, Fase 9):**
   ```
   Patrón Factory:
   ├── Propósito: Centralizar creación de objetos
   ├── Ubicación: Crear com/forestech/factory/ServiceFactory.java
   ├── Método: public static IMovementService createMovementService()
   └── Uso: IMovementService ms = ServiceFactory.createMovementService();
   
   VENTAJA: Cambiar implementación en UN solo lugar
   DESVENTAJA: Puede ser over-engineering en Fase 8 (hazlo solo si entiendes)
   ```

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
   ```
   Análisis de servicios:
   ├── MovementService
   │   ├── Métodos públicos (negocios)
   │   ├── Métodos privados/auxiliares
   │   └── ¿Código duplicado?
   ├── VehicleService
   │   ├── Métodos públicos
   │   ├── Métodos privados
   │   └── ¿Código duplicado?
   └── SupplierService, InventoryService (ídem)
   
   TAREA: Abre 3 servicios lado a lado y busca:
   - ¿Todos obtienen conexión igual?
   - ¿Todos cierran conexión igual?
   - ¿Todos manejan SQLException igual?
   - ¿Todos loggean con System.err.println?
   
   PREGUNTA: ¿Qué código ves repetido?
   PISTA: Si copias-pegaste algo entre servicios, va en BaseService.
   ```

2. **Crear `BaseService` clase abstracta:**
   ```
   Ubicación: com/forestech/base/BaseService.java
   ├── Tipo: abstract class
   ├── Visibilidad: public abstract
   ├── Métodos abstractos (IMPLEMENTA el usuario):
   │   └── protected abstract String getServiceName();
   │       └── Propósito: identificar el servicio en logs
   │
   ├── Métodos concretos heredables (ESPECIFICA el usuario):
   │   ├── protected Connection getConnection() throws DatabaseConnectionException
   │   │   └── Obtiene conexión, maneja SQLException
   │   │
   │   ├── protected void logInfo(String message)
   │   │   └── Imprime: [INFO] [fecha] [ServicioName] mensaje
   │   │
   │   ├── protected void logError(String message, Exception e)
   │   │   └── Imprime: [ERROR] [fecha] [ServicioName] mensaje + causa
   │   │
   │   ├── protected void validateNotNull(Object obj, String fieldName)
   │   │   └── Lanza InvalidMovementException si obj es null
   │   │
   │   └── protected void validatePositive(double value, String fieldName)
   │       └── Lanza InvalidMovementException si value <= 0
   │
   └── 💡 PISTA: Todos estos métodos YA ESCRIBISTE, solo en 4 servicios distintos
   
   PREGUNTA: ¿Por qué abstract y no class normal?
   PISTA: Porque BaseService nunca se instancia directamente, solo sus subclases.
   ```

3. **Hacer que `MovementService` herede de `BaseService`:**
   ```
   Cambios de firma:
   ├── ANTES: public class MovementService implements IMovementService
   └── DESPUÉS: public class MovementService extends BaseService implements IMovementService
   
   Reemplazos en el código (busca y reemplaza):
   ├── Todo: new DatabaseConnectionException() 
   │   → Primero haz: logError(...), luego throws
   │
   ├── Todo: System.out.println("[INFO]...")
   │   → Reemplaza con: logInfo("...")
   │
   ├── Todo: System.err.println("[ERROR]...")
   │   → Reemplaza con: logError("mensaje", exception)
   │
   └── Todo: if (obj == null) throw new InvalidMovementException()
       → Reemplaza con: validateNotNull(obj, "fieldName")
   
   RESULTADO: El código de MovementService será más corto.
   ```

4. **Repite para otros servicios:**
   ```
   Aplica el mismo patrón:
   ├── VehicleService extends BaseService implements IVehicleService
   ├── SupplierService extends BaseService implements ISupplierService
   └── InventoryService extends BaseService implements IInventoryService
   
   ACCIÓN: Para cada servicio:
   - Agrega extends BaseService
   - Implementa getServiceName()
   - Reemplaza getConnection() → heredado de BaseService
   - Reemplaza System.out/err.println → heredados logInfo/logError
   - Reemplaza validaciones manuales → heredadas validateNotNull/validatePositive
   ```

5. **OPCIONAL - Template Method Pattern:**
   ```
   Patrón Template Method (solo si quieres ir más allá):
   ├── Ubicación: En BaseService.java
   ├── Idea: Método genérico para ejecutar queries
   ├── Nombre: protected <T> T executeQuery(QueryExecutor<T> executor)
   │   └── Maneja: obtener conexión, catch SQLException, logging
   │
   └── Los servicios usan:
       public List<Movement> getAllMovements() {
           return executeQuery(conn -> {
               // Solo la lógica de query, sin manejo de conexión
               try (PreparedStatement stmt = conn.prepareStatement(...)) {
                   // ...
               }
           });
       }
   
   VENTAJA: Aún menos duplicación.
   DESVENTAJA: Puede ser complejo si no entiendas functional interfaces.
   RECOMENDACIÓN: Déjalo para después de dominar Streams (Checkpoint 8.5).
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
   ```
   Ubicación: enums/MovementType.java
   ├── Tipo: enum (palabra clave enum, no class)
   ├── Constantes: ENTRADA, SALIDA
   ├── Atributos (TÚ agrega):
   │   ├── String description (ejemplo: "Entrada", "Salida")
   │   └── String icon (ejemplo: "📥", "📤")
   │
   ├── Constructor (TÚ implementas):
   │   └── MovementType(String desc, String icon) { ... }
   │
   ├── Métodos públicos (TÚ implementas):
   │   ├── String getDescription()
   │   ├── String getIcon()
   │   ├── static MovementType fromString(String text)
   │   │   └── Convierte "ENTRADA" o "Entrada" a enum
   │   │   └── Lanza IllegalArgumentException si no existe
   │   └── String toString()
   │       └── Retorna: icon + " " + description
   │
   └── PISTA: Mira cómo está hecho FuelType más abajo, sigue el patrón.
   
   PREGUNTA: ¿Por qué necesitas el método fromString() si tienes valueOf()?
   PISTA: valueOf(String) es case-sensitive. fromString() puede aceptar "entrada" o "Entrada".
   ```

2. **Crear `FuelType` enum (estructura similar):**
   ```
   Ubicación: enums/FuelType.java
   ├── Constantes: DIESEL, GASOLINA_93, GASOLINA_95, GASOLINA_97, MEZCLA
   ├── Atributos:
   │   ├── String description
   │   ├── String icon
   │   └── double density (densidad en kg/L, ej: 0.85 para diesel)
   │
   ├── Constructor: FuelType(String desc, String icon, double density)
   ├── Getters: getDescription(), getIcon(), getDensity()
   ├── Método utilitario: static fromString(String text)
   └── toString(): retorna icon + " " + description
   
   PREGUNTA: ¿Para qué sirve el atributo density?
   PISTA: En Fase 9 podrías calcular peso = volumen * densidad.
   ```

3. **Enums opcionales (si quieres practicar más):**
   ```
   Crea estos si tienes tiempo:
   ├── VehicleCategory (EXCAVADORA, CAMION, GRUA, TRACTOR)
   ├── VehicleStatus (OPERATIVO, EN_MANTENIMIENTO, FUERA_DE_SERVICIO)
   └── LocationType (BODEGA_A, BODEGA_B, TERRENO, etc.)
   
   Para cada uno:
   - Define constantes
   - Agrega al menos 1-2 atributos (description, icon, capacity, etc.)
   - Constructor
   - Getters
   - fromString()
   - toString()
   ```

4. **Modificar clase `Movement`:**
   ```
   Cambios en src/main/java/com/forestech/Movement.java:
   ├── ANTES: private String type;
   └── DESPUÉS: private MovementType type;
   
   ├── ANTES: private String fuelType;
   └── DESPUÉS: private FuelType fuelType;
   
   ├── Actualizar getter y setter
   │   - getType() retorna MovementType (no String)
   │   - setType(MovementType type)
   │
   ├── Actualizar constructor (si acepta parámetros)
   │   - Parámetro: MovementType type
   │   - Parámetro: FuelType fuelType
   │
   └── toString() (si existe): usa type.getDescription(), fuel.getIcon(), etc.
   
   PREGUNTA: ¿Qué cambias en el getter/setter?
   PISTA: Solo el tipo, la lógica es igual.
   ```

5. **Refactorizar servicios para usar enums:**
   ```
   En MovementService.createEntryMovement():
   ├── Validación con enum:
   │   if (movement.getType() != MovementType.ENTRADA) {
   │       throw new InvalidMovementException("...");
   │   }
   │
   ├── En SQL, guardar el name():
   │   stmt.setString(1, movement.getType().name()); // "ENTRADA"
   │
   └── En SQL, leer y convertir a enum:
       String typeStr = rs.getString("type");
       movement.setType(MovementType.valueOf(typeStr));
   
   PREGUNTA: ¿Cuál es la diferencia entre name() y toString()?
   PISTA: name() retorna "ENTRADA", toString() retorna "📥 Entrada".
   ```

6. **Actualizar `ConsoleMenu` para mostrar enums:**
   ```
   Menú de selección de combustible:
   ├── Mostrar constantes del enum:
   │   for (FuelType fuel : FuelType.values()) {
   │       System.out.println(i++ + ". " + fuel); // Usa toString()
   │   }
   │
   ├── Leer opción del usuario:
   │   int option = scanner.nextInt();
   │   FuelType selected = FuelType.values()[option - 1];
   │
   └── Asignar a objeto:
       movement.setFuelType(selected);
   
   PREGUNTA: ¿Por qué usas values()[index] en lugar de valueOf()?
   PISTA: values() retorna array, valueOf() requiere String exacto.
   ```

7. **Conversión segura de String a enum:**
   ```
   Situación: Lees datos de archivo, API o BD que vienen como String.
   
   ❌ FORMA ARRIESGADA:
   MovementType type = MovementType.valueOf(input); // Puede fallar
   
   ✅ FORMA SEGURA (usa el método fromString del enum):
   try {
       MovementType type = MovementType.fromString(input);
       // usar type
   } catch (IllegalArgumentException e) {
       logError("Tipo inválido: " + input, e);
       // Manejar error, pedir re-entrada, etc.
   }
   
   PREGUNTA: ¿Cuándo usas valueOf() vs fromString()?
   PISTA: valueOf() si SABES que el string es exacto. 
          fromString() si viene de usuario/archivo (entrada externa).
   ```

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
