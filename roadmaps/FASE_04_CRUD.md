# 🛠️ FASE 4: OPERACIONES CRUD (Semanas 6-7)

> Objetivo general: dominar operaciones completas de base de datos (CREATE, READ, UPDATE, DELETE) usando PreparedStatement con validaciones y buenas prácticas de seguridad.

---

## 🧠 Antes de empezar

- 📚 **Fundamentos SQL CRUD:** Repasa en SQL Server Management Studio:
  - INSERT INTO con múltiples columnas y valores
  - SELECT con WHERE y diferentes tipos de filtros
  - UPDATE con WHERE (¡NUNCA sin WHERE!)
  - DELETE con WHERE y validación de integridad referencial
- 📝 **Documenta tus pruebas SQL:** Ejecuta manualmente cada operación en SQL Server y anota los resultados en `JAVA_LEARNING_LOG.md`
- 🔐 **Seguridad:** Investiga qué es SQL Injection y por qué PreparedStatement es obligatorio
- 🔁 **Git loop:** Al completar cada checkpoint crea un commit (`git commit -m "fase4 checkpoint 4.1"`)
- 🎯 **CRUD COMPLETO:** Crearás services para cada entidad siguiendo un patrón consistente
- ✍️ **APRENDIZAJE ACTIVO:** Recibirás DIRECTIVAS y especificaciones. TÚ escribirás el código y entenderás cada operación.

---

## 📦 ESTRUCTURA AL TERMINAR FASE 3

Antes de empezar Fase 4, debes tener esta estructura:

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
    └── ProductService.java (solo SELECT)
```

**Verifica que ProductService ya tenga:**
- `getAllProducts()` - retorna List<Product>
- `getProductById(String id)` - retorna Product con PreparedStatement
- `getProductsByType(String type)` - retorna List<Product> filtrada

---

## 🎯 ESTRUCTURA AL TERMINAR FASE 4

Al finalizar esta fase tendrás:

```
com.forestech/
├── Main.java (pruebas CRUD completas)
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
├── managers/
│   └── MovementManager.java (DEPRECATED - ahora se usa MovementService)
└── services/
    ├── MovementService.java (NUEVO - CRUD completo)
    ├── VehicleService.java (NUEVO - CRUD completo)
    ├── SupplierService.java (NUEVO - CRUD completo)
    └── ProductService.java (EXTENDIDO - ahora con INSERT, UPDATE, DELETE)
```

**Filosofía de la fase:**
- **Services completos:** Cada service tendrá las 4 operaciones CRUD
- **Validaciones previas:** Verificar existencia antes de UPDATE/DELETE
- **Integridad referencial:** No permitir DELETE si hay relaciones
- **PreparedStatement siempre:** Para TODAS las operaciones (no solo SELECT)
- **Manejo de errores:** try-catch apropiado en cada método
- **Retornos consistentes:** boolean para operaciones de escritura, objetos/listas para lectura

---

## ✅ Checkpoint 4.1: Insertar movimientos (INSERT)

**Concepto clave:** PreparedStatement con placeholders (?) previene SQL Injection y permite insertar datos de forma segura desde objetos Java a la base de datos.

**📍 DÓNDE:** 
- **Crear archivo:** `MovementService.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
- **Modificar:** `Main.java` para PROBAR la inserción

**🎯 PARA QUÉ:** 
Hasta Fase 3 solo LEES datos (SELECT). Ahora necesitas CREAR datos:
- ✅ **Persistir movimientos** creados en Java hacia SQL Server
- ✅ **Registrar operaciones** del usuario en la aplicación
- ✅ **Evitar perder datos** cuando cierras la aplicación
- ✅ **Completar el ciclo de datos:** crear objetos Java → guardar en BD → recuperar después

**Diferencia con Fase 3:**
```
FASE 3:
BD (datos ya existen) → SELECT → Java (lee y muestra)

FASE 4:
Java (crea objeto Movement) → INSERT → BD (guarda permanentemente)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 5:** Agregarás validaciones de negocio antes de insertar (verificar inventario disponible)
- **Fase 6:** El menú interactivo llamará a createMovement() cuando el usuario registre operaciones
- **Fase 7:** Manejarás errores específicos (duplicados, violaciones de FK)
- **Fase 9:** Los movimientos insertados aparecerán en reportes y análisis

**🎓 Analogía:**
- **SELECT (Fase 3):** Leer un libro de la biblioteca
- **INSERT (Fase 4):** Escribir un nuevo libro y agregarlo a la biblioteca

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre executeQuery() y executeUpdate()?"
"Explícame paso a paso cómo PreparedStatement previene SQL Injection."
"¿Por qué executeUpdate() retorna un int? ¿Qué significa ese número?"
"¿En qué orden se asignan los valores a los placeholders (?)?"
"¿Qué pasa si olvido asignar un valor a un placeholder?"
```

**Tareas paso a paso:**

1. **Crear la clase MovementService:**
   
   - Clic derecho en paquete `services` → New → Java Class → "MovementService"
   - Declarar como clase pública
   - Constructor privado (es una utility class con métodos estáticos por ahora)
   
   **Pregunta guía:** ¿Por qué usamos métodos estáticos en vez de crear instancias?

2. **Imports necesarios (boilerplate permitido):**
   
   ```java
   import com.forestech.config.DatabaseConnection;
   import com.forestech.models.Movement;
   import java.sql.Connection;
   import java.sql.PreparedStatement;
   import java.sql.SQLException;
   ```

3. **Crear método `createMovement(Movement movement)`:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `boolean` (true si se insertó, false si falló)
   - Parámetros: `Movement movement` (el objeto a guardar)
   
   **Pregunta guía:** ¿Por qué retornar boolean en vez de void?

4. **Definir la query SQL INSERT:**
   
   **Especificaciones (TÚ debes escribir la query):**
   - INSERT INTO la tabla `combustibles_movements`
   - Columnas a insertar: id, movementType, fuelType, quantity, unitPrice, movementDate
   - Usa placeholders (?) para TODOS los valores
   - NO uses concatenación de strings
   
   **Formato:**
   ```java
   String sql = "INSERT INTO tabla (col1, col2, col3) VALUES (?, ?, ?)";
   ```
   
   **Pregunta guía:** ¿Por qué usar placeholders en vez de concatenar valores directamente?
   
   **⚠️ PELIGRO - SQL Injection:**
   ```java
   // ❌ NUNCA HACER ESTO:
   String sql = "INSERT INTO movements VALUES ('" + movement.getId() + "')";
   // ☠️ Vulnerable a SQL Injection
   
   // ✅ SIEMPRE HACER ESTO:
   String sql = "INSERT INTO movements VALUES (?)";
   pstmt.setString(1, movement.getId());
   // ✅ Seguro contra SQL Injection
   ```

5. **Implementar la lógica de inserción:**
   
   **Estructura del método (TÚ completas la implementación):**
   
   a) Usar try-with-resources para Connection y PreparedStatement:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          // Configurar parámetros aquí
          
      } catch (SQLException e) {
          // Manejo de error aquí
      }
      ```
   
   b) Configurar los parámetros del PreparedStatement:
      - Usa los getters del objeto Movement para obtener los valores
      - Asigna cada valor al placeholder correspondiente usando:
        - `pstmt.setString(indice, valor)` para Strings
        - `pstmt.setDouble(indice, valor)` para doubles
      - El índice empieza en 1 (no en 0)
      
   **Ejemplo del primer parámetro:**
   ```java
   pstmt.setString(1, movement.getId());  // Primer ? (id)
   ```
   
   **TÚ debes configurar los demás:**
   - Segundo ? (movementType)
   - Tercero ? (fuelType)
   - Cuarto ? (quantity)
   - Quinto ? (unitPrice)
   - Sexto ? (movementDate)
   
   **Pregunta guía:** ¿Qué pasa si el orden de los setString no coincide con el orden de los ? en la query?
   
   c) Ejecutar la inserción:
      ```java
      int rowsAffected = pstmt.executeUpdate();
      ```
      
   **Pregunta guía:** ¿Por qué usamos executeUpdate() y no executeQuery()?
   
   d) Verificar el resultado:
      - Si `rowsAffected == 1`: inserción exitosa → retornar true
      - Si `rowsAffected == 0`: no se insertó nada → imprimir mensaje y retornar false
      - Imprime mensaje de confirmación con los datos insertados
   
   e) En el bloque catch:
      - Imprimir mensaje de error descriptivo
      - Imprimir el mensaje de la excepción: `e.getMessage()`
      - Retornar false

6. **Probar en Main.java:**
   
   **Crear prueba de inserción:**
   
   ```java
   System.out.println("\n=== PRUEBA DE INSERCIÓN DE MOVEMENT ===");
   
   // TÚ debes escribir:
   // 1. Crear un objeto Movement con datos de prueba
   //    - Usa el constructor que ya existe en Movement.java
   //    - Ejemplo: tipo "ENTRADA", combustible "Diesel", cantidad 1000.0
   // 2. Llamar a MovementService.createMovement(movement)
   // 3. Verificar el resultado (true/false)
   // 4. Imprimir mensaje según el resultado
   ```
   
   **Verificación manual en SQL Server:**
   - Abre SQL Server Management Studio
   - Ejecuta: `SELECT * FROM combustibles_movements ORDER BY movementDate DESC`
   - Verifica que tu movimiento aparece con los datos correctos

7. **Depuración obligatoria:**
   
   **Coloca breakpoints en:**
   - Línea donde creas el PreparedStatement
   - Línea donde ejecutas executeUpdate()
   - Línea dentro del catch (para ver errores)
   
   **En el debugger:**
   - Inspecciona el objeto `movement` antes de insertar
   - Evalúa `pstmt.toString()` para ver la query con valores
   - Verifica el valor de `rowsAffected` después de executeUpdate()
   - Si falla, revisa el mensaje de la SQLException

**✅ Resultado esperado:** 
- Método `createMovement()` implementado en MovementService.java
- Main.java crea un Movement y lo guarda exitosamente
- Ver mensaje de confirmación en consola con los datos insertados
- Verificar en SQL Server que el registro existe con los datos correctos
- Si hay error, ver mensaje descriptivo del problema
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java (con prueba de inserción)
  ├── config/
  │   └── DatabaseConnection.java
  ├── models/
  │   └── Movement.java
  └── services/
      ├── ProductService.java
      └── MovementService.java (NUEVO)
  ```

**💡 Concepto clave - executeUpdate() vs executeQuery():**

```
executeQuery():
- Usado para SELECT (consultas que RETORNAN datos)
- Retorna ResultSet con los datos
- NO modifica la base de datos

executeUpdate():
- Usado para INSERT, UPDATE, DELETE (operaciones que MODIFICAN datos)
- Retorna int (número de filas afectadas)
- SÍ modifica la base de datos
```

**💡 Concepto clave - Orden de parámetros:**

```
SQL: "INSERT INTO movements (id, type, fuel) VALUES (?, ?, ?)"
                                                      ↑   ↑   ↑
                                                      1   2   3

Java:
pstmt.setString(1, movement.getId());      // → Primer ?
pstmt.setString(2, movement.getType());    // → Segundo ?
pstmt.setString(3, movement.getFuel());    // → Tercer ?

❌ SI CAMBIAS EL ORDEN, los datos se guardan en las columnas incorrectas
```

**💡 Concepto clave - try-with-resources:**

```
¿Por qué usar try-with-resources?

SIN try-with-resources (antiguo):
Connection conn = null;
PreparedStatement pstmt = null;
try {
    conn = DatabaseConnection.getConnection();
    pstmt = conn.prepareStatement(sql);
    // ...
} finally {
    if (pstmt != null) pstmt.close();  // Código repetitivo
    if (conn != null) conn.close();
}

CON try-with-resources (moderno):
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    // ...
}  // ✅ Se cierran automáticamente en orden inverso

Ventajas:
- Más conciso y legible
- Cierre garantizado incluso si hay excepción
- Orden de cierre automático (último abierto, primero cerrado)
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| "Parameter index out of range" | Índice incorrecto o placeholders mal contados | Cuenta los ? en tu query y verifica índices 1, 2, 3... |
| "Column count doesn't match" | Diferente número de columnas vs valores | Verifica que cada columna en INSERT tenga su ? correspondiente |
| "Cannot insert NULL" | No asignaste valor a un placeholder | Verifica que llamaste setString/setDouble para TODOS los ? |
| rowsAffected = 0 | Query no falló pero no insertó | Verifica que la tabla existe y no hay constraints que bloqueen |
| SQLException sin mensaje claro | Error en la query SQL | Imprime la query completa y pruébala en SQL Server Management Studio |

**🔍 Depuración - Ver query compilada:**

En el debugger, evalúa:
```java
pstmt.toString()
```

Verás algo como:
```
PreparedStatement: INSERT INTO movements VALUES ('M001', 'ENTRADA', 'Diesel', 1000.0, ...)
```

Esto te muestra cómo se ve la query con los parámetros ya sustituidos. Puedes copiarla y probarla directamente en SQL Server.

**📊 Flujo completo de INSERT:**

```
1. Java crea objeto Movement
   Movement m = new Movement("ENTRADA", "Diesel", 1000.0, 3.5);

2. Pasa el objeto a MovementService
   boolean success = MovementService.createMovement(m);

3. MovementService extrae datos del objeto
   pstmt.setString(1, m.getId());
   pstmt.setString(2, m.getType());
   ... etc

4. executeUpdate() envía la query a SQL Server
   int rows = pstmt.executeUpdate();

5. SQL Server procesa el INSERT
   - Valida constraints (PK, FK, NOT NULL)
   - Inserta la fila si todo es válido

6. Retorna número de filas afectadas
   rows = 1 (éxito) o 0 (no se insertó)

7. MovementService retorna boolean
   return rows == 1;
```

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 4.2: Consultas filtradas (SELECT WHERE)

**Concepto clave:** Combinar PreparedStatement parametrizado con mapeo de ResultSet a objetos permite crear queries flexibles y seguras que retornan listas de datos filtrados.

**📍 DÓNDE:** 
- **Modificar:** `MovementService.java` (agregar nuevos métodos de lectura)
- **Main.java:** Para PROBAR los filtros

**🎯 PARA QUÉ:** 
En Checkpoint 4.1 insertaste datos. Ahora necesitas consultarlos de forma inteligente:
- ❌ **NO siempre quieres TODOS los movimientos** (pueden ser miles)
- ✅ **Filtrar por tipo:** Solo ENTRADAS o solo SALIDAS
- ✅ **Filtrar por combustible:** Solo movimientos de Diesel
- ✅ **Ordenar resultados:** Los más recientes primero
- ✅ **Búsquedas específicas:** Movimientos de una fecha concreta

**Diferencia con ProductService de Fase 3:**
```
ProductService (Fase 3):
- Leías productos que ya existían en la BD
- Solo SELECT, sin filtros complejos

MovementService (Fase 4):
- Lees movimientos que TÚ insertaste con Java
- SELECT con WHERE, ORDER BY, múltiples filtros
- Patrón completo de mapeo ResultSet → List<Objeto>
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 5:** Calcularás inventario actual consultando movimientos con estos métodos
- **Fase 6:** El menú mostrará movimientos filtrados por tipo, fecha o combustible
- **Fase 7:** Validarás datos consultando movimientos existentes
- **Fase 9:** Generarás reportes consultando por rangos de fechas

**🎓 Analogía:**
- **getAllMovements():** "Tráeme todos los libros de la biblioteca"
- **getMovementsByType("ENTRADA"):** "Tráeme solo los libros de ciencia ficción"
- **getMovementsByFuelType("Diesel"):** "Tráeme solo los libros del autor Asimov"

**Prompts sugeridos:**
```text
"¿Cómo mapeo un ResultSet a un objeto Movement paso a paso?"
"¿Por qué es mejor retornar List<Movement> que imprimir directamente en el Service?"
"¿Qué hago si la consulta no retorna resultados? ¿Retorno null o lista vacía?"
"¿Cómo recorro un ResultSet y creo objetos al mismo tiempo?"
"¿Por qué usar ORDER BY en la query y no ordenar en Java?"
```

**Tareas paso a paso:**

1. **Crear método `getAllMovements()`:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `List<Movement>`
   - Parámetros: ninguno
   - Retorna lista vacía si no hay movimientos
   
   **Pregunta guía:** ¿Por qué empezamos con getAllMovements() antes de los filtros?

2. **Implementación de getAllMovements():**
   
   **Estructura del método (TÚ la implementas):**
   
   a) Crear lista vacía al inicio:
      ```java
      List<Movement> movements = new ArrayList<>();
      ```
      
   b) Definir query SQL:
      - SELECT de todas las columnas necesarias
      - FROM combustibles_movements
      - ORDER BY movementDate DESC (más recientes primero)
      - SIN WHERE (queremos todos)
      
   c) Imports necesarios:
      ```java
      import java.util.List;
      import java.util.ArrayList;
      ```
   
   d) Usar try-with-resources:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql);
           ResultSet rs = pstmt.executeQuery()) {
          
          // Código de mapeo aquí
          
      } catch (SQLException e) {
          // Manejo de error
      }
      ```
   
   **Pregunta guía:** ¿Por qué ResultSet también va en try-with-resources?
   
   e) Recorrer ResultSet y mapear a objetos:
      ```java
      while (rs.next()) {
          // TÚ debes implementar:
          // 1. Extraer cada columna del ResultSet
          // 2. Crear objeto Movement con esos datos
          // 3. Agregar el objeto a la lista
      }
      ```
   
   **Ejemplo de extracción de columnas:**
   ```java
   String id = rs.getString("id");
   String type = rs.getString("movementType");
   // ... continúa con las demás columnas
   ```
   
   **Pregunta guía:** ¿Los nombres en getString() deben coincidir con los nombres en la query SELECT?
   
   f) Retornar la lista (puede estar vacía):
      ```java
      return movements;
      ```
   
   g) En el catch:
      - Imprimir mensaje de error
      - Retornar lista vacía (NO null)
      
   **Pregunta guía:** ¿Por qué retornar lista vacía y no null cuando hay error?

3. **Crear método `getMovementsByType(String type)`:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `List<Movement>`
   - Parámetros: `String type` ("ENTRADA" o "SALIDA")
   - Retorna lista filtrada o vacía
   
   **Implementación (TÚ la haces):**
   
   a) Query SQL con WHERE:
      ```java
      String sql = "SELECT ... FROM combustibles_movements WHERE movementType = ? ORDER BY movementDate DESC";
      ```
   
   b) Configurar parámetro:
      - Después de crear PreparedStatement
      - Antes de executeQuery()
      - Usa `pstmt.setString(1, type)`
   
   c) Resto del código igual a getAllMovements():
      - Recorrer ResultSet con while
      - Extraer columnas y crear objetos Movement
      - Agregar a lista
      - Retornar lista

4. **Crear método `getMovementsByFuelType(String fuelType)`:**
   
   **Especificaciones:**
   - Similar a getMovementsByType()
   - Filtra por columna `fuelType` en vez de `movementType`
   
   **Query SQL:**
   ```java
   String sql = "SELECT ... FROM combustibles_movements WHERE fuelType = ? ORDER BY movementDate DESC";
   ```
   
   **Desafío:** Implementa este método siguiendo el patrón de getMovementsByType(). ¿Ves el patrón común?

5. **OPCIONAL - Método con múltiples filtros:**
   
   **Desafío avanzado:** Crea un método que filtre por tipo Y combustible:
   
   ```java
   public static List<Movement> searchMovements(String type, String fuelType)
   ```
   
   **Query SQL:**
   ```java
   String sql = "SELECT ... FROM combustibles_movements 
                 WHERE movementType = ? AND fuelType = ? 
                 ORDER BY movementDate DESC";
   ```
   
   **Configurar parámetros:**
   ```java
   pstmt.setString(1, type);      // Primer ?
   pstmt.setString(2, fuelType);  // Segundo ?
   ```

6. **Probar en Main.java:**
   
   **Prueba 1 - Listar todos:**
   ```java
   System.out.println("\n=== TODOS LOS MOVIMIENTOS ===");
   List<Movement> allMovements = MovementService.getAllMovements();
   
   // TÚ debes escribir:
   // - Verificar si la lista está vacía
   // - Recorrer con for-each e imprimir cada movimiento
   // - Mostrar total de movimientos al final
   ```
   
   **Pregunta guía:** ¿Cómo verificas si una lista está vacía? ¿usas `== null` o `.isEmpty()`?
   
   **Prueba 2 - Filtrar por tipo:**
   ```java
   System.out.println("\n=== MOVIMIENTOS DE ENTRADA ===");
   List<Movement> entradas = MovementService.getMovementsByType("ENTRADA");
   
   // TÚ debes escribir el código para imprimir
   ```
   
   **Prueba 3 - Filtrar por combustible:**
   ```java
   System.out.println("\n=== MOVIMIENTOS DE DIESEL ===");
   List<Movement> dieselMovements = MovementService.getMovementsByFuelType("Diesel");
   
   // TÚ debes escribir el código para imprimir
   ```
   
   **Prueba 4 (opcional) - Búsqueda múltiple:**
   ```java
   List<Movement> results = MovementService.searchMovements("ENTRADA", "Diesel");
   ```

7. **Depuración obligatoria:**
   
   **Coloca breakpoints en:**
   - Línea `while (rs.next())` - ver cada iteración
   - Línea donde creas el objeto Movement - inspeccionar valores
   - Línea `movements.add(movement)` - ver cómo crece la lista
   
   **En el debugger:**
   - Inspecciona el ResultSet en cada iteración
   - Ve cómo cambian los valores de las columnas
   - Verifica que los objetos Movement se crean correctamente
   - Cuenta cuántas iteraciones hace el while
   - Verifica el tamaño final de la lista

**✅ Resultado esperado:** 
- Método `getAllMovements()` retorna List<Movement> con todos los registros
- Método `getMovementsByType()` retorna solo movimientos del tipo especificado
- Método `getMovementsByFuelType()` retorna solo movimientos del combustible especificado
- Main.java imprime correctamente todas las listas
- Listas vacías cuando no hay resultados (NO null, NO excepción)
- Movimientos ordenados por fecha descendente (más recientes primero)
- Estructura actualizada:
  ```
  com.forestech/
  └── services/
      └── MovementService.java
          ├── createMovement(Movement) → boolean
          ├── getAllMovements() → List<Movement> (NUEVO)
          ├── getMovementsByType(String) → List<Movement> (NUEVO)
          └── getMovementsByFuelType(String) → List<Movement> (NUEVO)
  ```

**💡 Concepto clave - Retornar lista vacía vs null:**

```java
❌ MAL (retornar null):
public static List<Movement> getAllMovements() {
    try {
        // ...
        if (no hay resultados) {
            return null;  // ❌ Peligroso
        }
    }
}

// En Main.java:
List<Movement> movements = service.getAllMovements();
for (Movement m : movements) {  // ☠️ NullPointerException si movements es null
    System.out.println(m);
}

✅ BIEN (retornar lista vacía):
public static List<Movement> getAllMovements() {
    List<Movement> movements = new ArrayList<>();
    try {
        // ...
        // Si no hay resultados, la lista queda vacía
    }
    return movements;  // ✅ Siempre retorna lista (puede estar vacía)
}

// En Main.java:
List<Movement> movements = service.getAllMovements();
for (Movement m : movements) {  // ✅ Si está vacía, no entra al for (no falla)
    System.out.println(m);
}

// O mejor aún:
if (movements.isEmpty()) {
    System.out.println("No hay movimientos");
} else {
    for (Movement m : movements) {
        System.out.println(m);
    }
}
```

**💡 Concepto clave - Patrón de mapeo ResultSet → Objeto:**

```java
while (rs.next()) {  // Avanza a la siguiente fila
    // 1. Extraer valores de la fila actual
    String id = rs.getString("id");
    String type = rs.getString("movementType");
    double qty = rs.getDouble("quantity");
    // ... etc
    
    // 2. Crear objeto con esos valores
    Movement movement = new Movement(id, type, qty, ...);
    
    // 3. Agregar a la lista
    movements.add(movement);
}
// Al terminar, movements contiene todos los objetos
```

**💡 Concepto clave - Try-with-resources múltiple:**

```java
try (Connection conn = ...;           // Se cierra 3ro (orden inverso)
     PreparedStatement pstmt = ...;   // Se cierra 2do
     ResultSet rs = ...) {            // Se cierra 1ro (último abierto)
    
    // Usar recursos aquí
    
}  // Cierre automático en orden inverso

Equivalente a:
try {
    Connection conn = ...;
    try {
        PreparedStatement pstmt = ...;
        try {
            ResultSet rs = ...;
            // usar recursos
        } finally {
            rs.close();
        }
    } finally {
        pstmt.close();
    }
} finally {
    conn.close();
}

¡Mucho más simple con try-with-resources!
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| "Column not found" | Nombre incorrecto en rs.getString() | Verifica nombres exactos en SELECT (mayúsculas/minúsculas) |
| Lista siempre vacía | while nunca entra | Verifica que hay datos en la BD con query manual |
| NullPointerException | Retornaste null en vez de lista vacía | Siempre inicializa `new ArrayList<>()` al inicio |
| Datos incorrectos en objetos | Orden incorrecto al extraer columnas | Verifica que extraes las columnas en el orden del constructor |
| "ResultSet closed" | Intentas usar rs fuera del try | ResultSet solo es válido dentro del try-with-resources |

**🔍 Comparación: getAllMovements vs getMovementsByType:**

```java
// getAllMovements() - SIN filtro:
String sql = "SELECT ... FROM movements ORDER BY date DESC";
// NO hay PreparedStatement con parámetros (puede usar Statement)
// Retorna TODOS los registros

// getMovementsByType() - CON filtro:
String sql = "SELECT ... FROM movements WHERE type = ? ORDER BY date DESC";
pstmt.setString(1, type);  // Configura el filtro
// Retorna solo registros que coinciden con el filtro
```

**📊 Flujo completo de consulta filtrada:**

```
1. Main.java llama al método con parámetro
   List<Movement> entradas = MovementService.getMovementsByType("ENTRADA");

2. MovementService prepara query con placeholder
   String sql = "... WHERE type = ?";

3. Configura el parámetro
   pstmt.setString(1, "ENTRADA");

4. Ejecuta query
   ResultSet rs = pstmt.executeQuery();

5. SQL Server procesa el WHERE y retorna filas filtradas
   Solo filas donde type = 'ENTRADA'

6. while (rs.next()) recorre cada fila filtrada
   Crea objetos Movement y los agrega a la lista

7. Retorna la lista completa
   Main.java puede recorrerla e imprimirla
```

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 4.3: Actualizar vehículos (UPDATE)

**Concepto clave:** UPDATE modifica datos existentes en la base de datos. SIEMPRE debe incluir WHERE para especificar qué registros actualizar, y validar existencia previa para evitar operaciones fallidas silenciosas.

**📍 DÓNDE:** 
- **Crear archivo:** `VehicleService.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
- **Modificar:** `Main.java` para PROBAR la actualización

**🎯 PARA QUÉ:** 
Los datos del mundo real cambian constantemente:
- ✅ **Corregir errores** de captura (placa mal escrita, modelo incorrecto)
- ✅ **Actualizar información** que evoluciona (cambio de placas, reasignación de categoría)
- ✅ **Modificar estado** de los registros (activar/desactivar vehículos)
- ✅ **Mantener datos actualizados** sin perder el historial de ID y relaciones

**¿Por qué UPDATE y no DELETE + INSERT?**
```
❌ DELETE + INSERT:
- Pierdes el ID original (rompes relaciones FK)
- Pierdes el historial de auditoría
- Más operaciones = más lento
- Mayor riesgo de errores

✅ UPDATE:
- Mantiene el ID (preserva relaciones)
- Modifica solo los campos necesarios
- Una sola operación atómica
- Menos riesgo de inconsistencias
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 5:** Actualizarás inventarios calculados después de cada movimiento
- **Fase 6:** El menú permitirá editar vehículos/productos desde la interfaz
- **Fase 7:** Implementarás validación de permisos (quién puede actualizar)
- **Fase 8:** Registrarás auditoría completa (qué cambió, cuándo, quién)

**🎓 Analogía:**
- **UPDATE:** Corregir un error en un documento con corrector (mantiene el documento original)
- **DELETE + INSERT:** Romper el documento y crear uno nuevo (pierdes el original y su historia)

**Prompts sugeridos:**
```text
"¿Qué pasa si ejecuto UPDATE sin WHERE? Muéstrame un ejemplo peligroso."
"¿Cómo verifico que un registro existe antes de actualizar?"
"¿Qué significa que executeUpdate() retorne 0 vs 1 vs >1?"
"¿Por qué necesito el ID como parámetro separado si el objeto Vehicle ya lo tiene?"
"¿Cómo puedo ver qué datos tenía antes de actualizar (para auditoría)?"
```

**Tareas paso a paso:**

1. **Crear la clase VehicleService:**
   
   - Clic derecho en paquete `services` → New → Java Class → "VehicleService"
   - Declarar como clase pública
   - Constructor privado (utility class con métodos estáticos)
   
   **Imports necesarios:**
   ```java
   import com.forestech.config.DatabaseConnection;
   import com.forestech.models.Vehicle;
   import java.sql.Connection;
   import java.sql.PreparedStatement;
   import java.sql.ResultSet;
   import java.sql.SQLException;
   ```

2. **Crear método helper `getVehicleById(String id)`:**
   
   **¿Por qué empezar con este método?**
   - Necesitas verificar existencia antes de actualizar
   - También es útil para consultas generales
   - Sigue el patrón de ProductService.getProductById()
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `Vehicle` (un solo objeto o null)
   - Parámetros: `String id`
   - Retorna null si no existe
   
   **Implementación (TÚ la haces):**
   
   a) Query SQL con WHERE:
      ```java
      String sql = "SELECT id, plate, model, category FROM combustibles_vehicles WHERE id = ?";
      ```
   
   b) Usar try-with-resources:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          // Configurar parámetro y ejecutar
          
      } catch (SQLException e) {
          // Manejo de error
      }
      ```
   
   c) Configurar parámetro y ejecutar:
      ```java
      pstmt.setString(1, id);
      ResultSet rs = pstmt.executeQuery();
      ```
   
   **Pregunta guía:** ¿Por qué ResultSet NO va en el try-with-resources aquí?
   
   d) Verificar si existe y mapear:
      ```java
      if (rs.next()) {
          // Extraer columnas
          // Crear objeto Vehicle
          // Retornar objeto
      }
      return null;  // No existe
      ```
   
   e) En el catch, retornar null

3. **Crear método `updateVehicle(String id, Vehicle vehicle)`:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `boolean` (true si actualizó, false si falló)
   - Parámetros: 
     - `String id` - El ID del vehículo a actualizar
     - `Vehicle vehicle` - Objeto con los NUEVOS datos
   
   **Pregunta guía:** ¿Por qué recibir el ID como parámetro separado si Vehicle ya tiene un ID?
   
   **Respuesta:** Para evitar que alguien intente cambiar el ID de un registro. El ID es inmutable, solo actualizamos los demás campos.

4. **Validar existencia ANTES de actualizar:**
   
   **Especificaciones (TÚ implementas):**
   
   a) Al inicio del método, verificar existencia:
      ```java
      Vehicle existingVehicle = getVehicleById(id);
      if (existingVehicle == null) {
          // Imprimir mensaje: "No existe vehículo con ID: " + id
          // Retornar false
      }
      ```
   
   **Pregunta guía:** ¿Por qué NO simplemente ejecutar UPDATE y ver si rowsAffected es 0?
   
   **Respuesta:** Porque UPDATE con 0 filas afectadas NO genera error. Si no validas, el usuario no sabrá si falló porque el ID no existe o por otro motivo.

5. **Construir query UPDATE:**
   
   **Especificaciones (TÚ escribes la query):**
   - UPDATE tabla `combustibles_vehicles`
   - SET las columnas a actualizar: plate, model, category
   - WHERE id = ?
   - Usa placeholders (?) para TODOS los valores
   
   **Formato:**
   ```java
   String sql = "UPDATE tabla SET col1 = ?, col2 = ?, col3 = ? WHERE id = ?";
   ```
   
   **⚠️ PELIGRO CRÍTICO - UPDATE sin WHERE:**
   ```sql
   -- ❌ SIN WHERE (DESASTRE):
   UPDATE combustibles_vehicles SET plate = 'ABC123';
   -- ☠️ ACTUALIZA TODAS LAS FILAS con la misma placa
   -- ☠️ Pierdes TODOS los datos originales
   -- ☠️ No hay forma fácil de recuperarlos
   
   -- ✅ CON WHERE (CORRECTO):
   UPDATE combustibles_vehicles SET plate = 'ABC123' WHERE id = 'V001';
   -- ✅ Solo actualiza el vehículo V001
   ```
   
   **Pregunta guía:** Si olvidas WHERE, ¿SQL Server te avisa del error? ¿O ejecuta la query destruyendo datos?

6. **Implementar la lógica de UPDATE:**
   
   **Estructura del método (TÚ completas):**
   
   a) Usar try-with-resources:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          // Configurar parámetros aquí
          
      } catch (SQLException e) {
          // Manejo de error
      }
      ```
   
   b) Configurar los parámetros:
      - Usa los getters del objeto `vehicle` para los NUEVOS valores
      - El ÚLTIMO parámetro es el ID (para el WHERE)
      
   **Ejemplo:**
   ```java
   pstmt.setString(1, vehicle.getPlate());      // Nuevo valor para plate
   pstmt.setString(2, vehicle.getModel());      // Nuevo valor para model
   pstmt.setString(3, vehicle.getCategory());   // Nuevo valor para category
   pstmt.setString(4, id);                      // WHERE id = ?
   ```
   
   **Pregunta guía:** ¿Qué pasa si confundes el orden y pones el ID en el primer placeholder?
   
   c) Ejecutar UPDATE:
      ```java
      int rowsAffected = pstmt.executeUpdate();
      ```
   
   d) Verificar resultado:
      - Si `rowsAffected == 1`: actualización exitosa → imprimir confirmación y retornar true
      - Si `rowsAffected == 0`: no se actualizó (raro porque ya validaste existencia) → retornar false
      - Si `rowsAffected > 1`: ¡PROBLEMA! Tu WHERE no es único → imprimir warning
      
   **Pregunta guía:** ¿En qué caso rowsAffected podría ser > 1?
   
   e) En el catch:
      - Imprimir mensaje de error descriptivo
      - Imprimir `e.getMessage()`
      - Retornar false

7. **Probar en Main.java:**
   
   **Prueba completa (TÚ escribes el código):**
   
   ```java
   System.out.println("\n=== PRUEBA DE UPDATE DE VEHICLE ===");
   
   // Paso 1: Insertar un vehículo de prueba (usa constructor de Vehicle)
   // - Crea objeto Vehicle con datos iniciales
   // - Usa VehicleService.createVehicle() si ya existe (si no, créalo ahora)
   // - Verifica que se insertó
   
   // Paso 2: Mostrar datos originales
   // - Usa VehicleService.getVehicleById()
   // - Imprime los datos
   
   // Paso 3: Modificar el vehículo
   // - Crea NUEVO objeto Vehicle con MISMOS datos excepto lo que quieres cambiar
   //   Ejemplo: cambiar placa de "ABC123" a "XYZ789"
   // - Llama a VehicleService.updateVehicle(id, vehicleModificado)
   // - Verifica el resultado (true/false)
   
   // Paso 4: Verificar cambio en BD
   // - Vuelve a consultar con getVehicleById()
   // - Imprime los nuevos datos
   // - Compara: ¿cambió la placa? ¿el resto se mantuvo igual?
   
   // Paso 5: Intentar actualizar un ID que NO existe
   // - Llama a updateVehicle("ID_INEXISTENTE", vehicle)
   // - Debe retornar false con mensaje claro
   ```
   
   **Verificación manual en SQL Server:**
   ```sql
   -- Antes de ejecutar Main.java:
   SELECT * FROM combustibles_vehicles WHERE id = 'V001';
   
   -- Después de ejecutar:
   SELECT * FROM combustibles_vehicles WHERE id = 'V001';
   -- ¿Cambió la placa? ¿Los demás campos se mantienen?
   ```

8. **Depuración obligatoria:**
   
   **Coloca breakpoints en:**
   - Línea de validación: `if (existingVehicle == null)`
   - Línea donde configuras parámetros con setString
   - Línea de executeUpdate()
   - Línea de verificación: `if (rowsAffected == 1)`
   
   **En el debugger:**
   - Inspecciona `existingVehicle` - ¿tiene los datos originales?
   - Inspecciona el objeto `vehicle` - ¿tiene los nuevos datos?
   - Evalúa `pstmt.toString()` - ¿la query tiene los valores correctos?
   - Verifica `rowsAffected` - ¿es exactamente 1?
   - Consulta la BD después de ejecutar - ¿los datos cambiaron?

**✅ Resultado esperado:** 
- Método `getVehicleById()` que retorna Vehicle o null
- Método `updateVehicle()` que actualiza datos existentes
- Validación previa que rechaza IDs inexistentes con mensaje claro
- Main.java demuestra el flujo completo: consultar → modificar → verificar
- Los datos en SQL Server cambian correctamente
- Estructura actualizada:
  ```
  com.forestech/
  └── services/
      ├── MovementService.java (INSERT + SELECT)
      └── VehicleService.java (NUEVO)
          ├── getVehicleById(String) → Vehicle
          └── updateVehicle(String, Vehicle) → boolean
  ```

**💡 Concepto clave - Anatomía de UPDATE:**

```sql
UPDATE tabla           -- Qué tabla modificar
SET col1 = ?,         -- Qué columnas cambiar y a qué valores
    col2 = ?,
    col3 = ?
WHERE id = ?;         -- CRÍTICO: Qué filas modificar

Correspondencia en PreparedStatement:
pstmt.setString(1, newValue1);  // col1 = ?
pstmt.setString(2, newValue2);  // col2 = ?
pstmt.setString(3, newValue3);  // col3 = ?
pstmt.setString(4, idValue);    // WHERE id = ?
```

**💡 Concepto clave - Validación previa vs posterior:**

```java
❌ SIN validación previa:
public static boolean updateVehicle(String id, Vehicle v) {
    // Ejecuta UPDATE directamente
    int rows = pstmt.executeUpdate();
    if (rows == 0) {
        System.out.println("No se actualizó");  // ¿Por qué? ¿No existe o hubo error?
        return false;
    }
    return true;
}

✅ CON validación previa:
public static boolean updateVehicle(String id, Vehicle v) {
    // Verifica existencia primero
    if (getVehicleById(id) == null) {
        System.out.println("❌ Error: No existe vehículo con ID " + id);
        return false;  // Razón clara del fallo
    }
    
    // Ejecuta UPDATE
    int rows = pstmt.executeUpdate();
    if (rows == 1) {
        System.out.println("✅ Vehículo actualizado exitosamente");
        return true;
    }
    
    System.out.println("⚠️ Advertencia: Se esperaba actualizar 1 fila, se afectaron " + rows);
    return false;
}
```

**💡 Concepto clave - executeUpdate() retorna int:**

```java
int rowsAffected = pstmt.executeUpdate();

Valores posibles:
- 0: No se modificó ninguna fila
  * WHERE no coincidió con ningún registro
  * O los valores nuevos son idénticos a los existentes
  
- 1: Se modificó exactamente UNA fila (esperado en UPDATE con PK)
  * La operación fue exitosa
  
- >1: Se modificaron MÚLTIPLES filas
  * WHERE no especificó un registro único
  * ⚠️ PROBLEMA: Probablemente actualizaste más de lo que querías
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| UPDATE sin WHERE destruye todos los datos | Olvidaste WHERE en la query | SIEMPRE verifica que tu query UPDATE tenga WHERE |
| rowsAffected = 0 pero el ID existe | Valores nuevos = valores viejos | Normal, SQL Server detecta que no hay cambio real |
| rowsAffected > 1 | WHERE no es único (ej: WHERE category = ?) | WHERE debe usar columna UNIQUE (típicamente PK) |
| "Column not found" al mapear | getVehicleById() tiene error en nombres | Verifica nombres exactos de columnas en SELECT |
| NullPointerException | No validaste que getVehicleById() retornó null | Siempre verifica `if (vehicle == null)` antes de usar |

**🔍 Comparación: INSERT vs UPDATE:**

```java
// INSERT - Crea NUEVO registro:
String sql = "INSERT INTO vehicles (id, plate, model) VALUES (?, ?, ?)";
pstmt.setString(1, newId);      // ID nuevo (no existe aún)
pstmt.setString(2, plate);
pstmt.setString(3, model);
// Si el ID ya existe → Error de violación de PK

// UPDATE - Modifica registro EXISTENTE:
String sql = "UPDATE vehicles SET plate = ?, model = ? WHERE id = ?";
pstmt.setString(1, newPlate);   // Nuevos valores
pstmt.setString(2, newModel);
pstmt.setString(3, existingId); // ID existente (ya en la BD)
// Si el ID no existe → rowsAffected = 0 (no error, solo no hace nada)
```

**📊 Flujo completo de UPDATE:**

```
1. Main.java llama con ID y objeto con nuevos datos
   boolean success = VehicleService.updateVehicle("V001", vehicleModificado);

2. VehicleService valida existencia
   Vehicle existing = getVehicleById("V001");
   if (existing == null) → return false;

3. Prepara query UPDATE con placeholders
   "UPDATE vehicles SET plate = ?, model = ? WHERE id = ?"

4. Configura parámetros (nuevos valores + ID para WHERE)
   pstmt.setString(1, vehicleModificado.getPlate());
   pstmt.setString(2, vehicleModificado.getModel());
   pstmt.setString(3, "V001");

5. Ejecuta UPDATE
   int rowsAffected = pstmt.executeUpdate();

6. SQL Server procesa UPDATE
   - Busca registro con id = 'V001' (WHERE)
   - Modifica plate y model con nuevos valores
   - Retorna 1 (una fila modificada)

7. Java verifica resultado
   if (rowsAffected == 1) → return true;

8. Main.java recibe confirmación y puede consultar para verificar
   Vehicle updated = VehicleService.getVehicleById("V001");
   // Debe tener los nuevos datos
```

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 4.4: Eliminar proveedores (DELETE)

**Concepto clave:** DELETE es la operación más peligrosa en SQL. Requiere validación estricta de integridad referencial para evitar romper relaciones entre tablas y pérdida irreversible de datos.

**📍 DÓNDE:** 
- **Crear archivo:** `SupplierService.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
- **Modificar:** `Main.java` para PROBAR la eliminación

**🎯 PARA QUÉ:** 
Aunque eliminar datos parece simple, es extremadamente delicado:
- ❌ **Irreversible:** Una vez borrado, no hay "Ctrl+Z" fácil
- ❌ **Rompe relaciones:** Si borras un proveedor que tiene movimientos, esos movimientos quedan "huérfanos"
- ❌ **Pérdida de auditoría:** El historial desaparece completamente
- ✅ **Necesario a veces:** Duplicados, datos de prueba, registros erróneos sin dependencias

**¿DELETE real o Soft Delete?**
```
DELETE real (Hard Delete):
- Borra físicamente el registro de la BD
- No se puede recuperar (sin backup)
- Libera espacio en disco
- Uso: Datos de prueba, duplicados, info sin valor histórico

Soft Delete (marcado lógico):
- Actualiza un campo isActive = false o deletedAt = NOW()
- El registro sigue en la BD pero "invisible"
- Puede recuperarse fácilmente
- Conserva historial para auditoría
- Uso: PRODUCCIÓN (casi siempre preferido)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 5:** Implementarás soft delete como regla de negocio estándar
- **Fase 6:** El menú mostrará solo registros activos (isActive = true)
- **Fase 7:** Agregarás confirmación "¿Estás seguro?" antes de eliminar
- **Fase 8:** Registrarás quién eliminó, cuándo y por qué (auditoría completa)
- **Fase 9:** Los reportes filtrarán registros eliminados

**🎓 Analogía:**
- **DELETE real:** Quemar un documento en papel (irreversible, destrucción total)
- **Soft delete:** Archivar un documento en "Inactivos" (reversible, mantiene historial)

**Prompts sugeridos:**
```text
"Explícame qué es integridad referencial y por qué un DELETE puede romperla."
"¿Qué es un DELETE en cascada (ON DELETE CASCADE) y cuándo usarlo?"
"¿Cuáles son las ventajas y desventajas de soft delete vs hard delete?"
"¿Cómo sé si un proveedor tiene movimientos asociados antes de eliminarlo?"
"Si elimino un proveedor con movimientos, ¿qué pasa con esos movimientos?"
```

**Tareas paso a paso:**

1. **Crear la clase SupplierService:**
   
   - Clic derecho en paquete `services` → New → Java Class → "SupplierService"
   - Declarar como clase pública
   - Constructor privado
   
   **Imports necesarios:**
   ```java
   import com.forestech.config.DatabaseConnection;
   import com.forestech.models.Supplier;
   import java.sql.Connection;
   import java.sql.PreparedStatement;
   import java.sql.ResultSet;
   import java.sql.SQLException;
   ```

2. **Crear método helper `supplierExists(String id)`:**
   
   **¿Por qué crear este método primero?**
   - Verificar existencia antes de DELETE
   - Reutilizable para otras validaciones
   - Más eficiente que getSupplierById() (solo retorna boolean)
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `boolean`
   - Parámetros: `String id`
   - Retorna true si existe, false si no
   
   **Implementación (TÚ la haces):**
   
   a) Query SQL eficiente:
      ```java
      String sql = "SELECT COUNT(*) FROM combustibles_suppliers WHERE id = ?";
      ```
   
   **Pregunta guía:** ¿Por qué usar COUNT(*) en vez de SELECT *?
   
   b) Usar try-with-resources y configurar parámetro:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          pstmt.setString(1, id);
          ResultSet rs = pstmt.executeQuery();
          
          // Procesar resultado aquí
          
      } catch (SQLException e) {
          // Manejo de error
      }
      ```
   
   c) Procesar ResultSet:
      ```java
      if (rs.next()) {
          int count = rs.getInt(1);  // Primera columna (COUNT(*))
          return count > 0;
      }
      return false;
      ```
   
   **Pregunta guía:** ¿Por qué `rs.getInt(1)` y no `rs.getInt("COUNT(*)")`?
   
   d) En el catch, retornar false

3. **Crear método `countRelatedMovements(String supplierId)`:**
   
   **CRÍTICO para integridad referencial:**
   Este método verifica cuántos movimientos están asociados al proveedor.
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `int` (número de movimientos asociados)
   - Parámetros: `String supplierId`
   - Retorna 0 si no hay movimientos, >0 si hay dependencias
   
   **Implementación (TÚ la haces):**
   
   a) Query SQL que cuenta movimientos:
      ```java
      String sql = "SELECT COUNT(*) FROM combustibles_movements WHERE supplierId = ?";
      ```
   
   b) Ejecutar query (similar a supplierExists):
      - Configurar parámetro con el supplierId
      - Ejecutar executeQuery()
      - Extraer el count con rs.getInt(1)
      - Retornar el count
   
   c) En el catch, retornar 0 (asume que no hay movimientos si hay error)
   
   **Pregunta guía:** ¿Es seguro retornar 0 en el catch? ¿O debería retornar -1 para indicar error?

4. **Crear método `deleteSupplier(String id)`:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `boolean` (true si eliminó, false si falló o se rechazó)
   - Parámetros: `String id`
   
   **Pregunta guía:** ¿Por qué este método puede fallar aunque no haya error de SQL?

5. **Implementar validaciones previas (CRÍTICO):**
   
   **Estructura del método (TÚ completas):**
   
   a) Validación 1 - Verificar existencia:
      ```java
      if (!supplierExists(id)) {
          System.out.println("❌ Error: No existe proveedor con ID " + id);
          return false;
      }
      ```
   
   b) Validación 2 - Verificar integridad referencial:
      ```java
      int relatedMovements = countRelatedMovements(id);
      if (relatedMovements > 0) {
          System.out.println("❌ No se puede eliminar: El proveedor tiene " 
                           + relatedMovements + " movimiento(s) asociado(s)");
          System.out.println("💡 Sugerencia: Elimina o reasigna los movimientos primero, "
                           + "o considera desactivar en vez de eliminar.");
          return false;
      }
      ```
   
   **Pregunta guía:** ¿Qué pasaría si eliminas el proveedor sin esta validación?
   
   **Respuesta:** Los movimientos quedarían con un supplierId que no existe (datos huérfanos), rompiendo la integridad referencial. Las queries con JOIN fallarían o mostrarían datos inconsistentes.
   
   c) Si pasa ambas validaciones, proceder con DELETE:
      ```java
      System.out.println("✅ Validaciones pasadas. Procediendo a eliminar...");
      ```

6. **Ejecutar DELETE:**
   
   **Implementación (TÚ la haces):**
   
   a) Query SQL DELETE:
      ```java
      String sql = "DELETE FROM combustibles_suppliers WHERE id = ?";
      ```
   
   **⚠️ PELIGRO MÁXIMO - DELETE sin WHERE:**
   ```sql
   -- ❌ SIN WHERE (CATÁSTROFE):
   DELETE FROM combustibles_suppliers;
   -- ☠️ BORRA TODAS LAS FILAS DE LA TABLA
   -- ☠️ Sin confirmación, sin aviso
   -- ☠️ Pérdida total de datos
   -- ☠️ Solo recuperable con backup (si existe)
   
   -- ✅ CON WHERE (SEGURO):
   DELETE FROM combustibles_suppliers WHERE id = 'S001';
   -- ✅ Solo borra el proveedor S001
   ```
   
   b) Usar try-with-resources:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          // Configurar parámetro y ejecutar
          
      } catch (SQLException e) {
          // Manejo de error
      }
      ```
   
   c) Configurar parámetro y ejecutar:
      ```java
      pstmt.setString(1, id);
      int rowsAffected = pstmt.executeUpdate();
      ```
   
   **Pregunta guía:** ¿Por qué usar executeUpdate() y no executeQuery()?
   
   d) Verificar resultado:
      ```java
      if (rowsAffected == 1) {
          System.out.println("✅ Proveedor eliminado exitosamente");
          return true;
      } else {
          System.out.println("⚠️ Advertencia: Se esperaba eliminar 1 fila, se afectaron " + rowsAffected);
          return false;
      }
      ```
   
   e) En el catch:
      - Imprimir mensaje de error
      - Imprimir `e.getMessage()`
      - Retornar false

7. **OPCIONAL - Implementar soft delete alternativo:**
   
   **Desafío avanzado:** Crea un método `deactivateSupplier(String id)` que:
   - NO elimina el registro
   - Actualiza un campo `isActive = false` o `status = 'INACTIVE'`
   - Conserva toda la información para auditoría
   
   **Ventajas del soft delete:**
   - Reversible (puede reactivarse)
   - Mantiene integridad referencial intacta
   - Conserva historial completo
   - Mejor para producción
   
   **Query SQL:**
   ```java
   String sql = "UPDATE combustibles_suppliers SET isActive = 0 WHERE id = ?";
   // O: "UPDATE ... SET status = 'INACTIVE', deletedAt = GETDATE() WHERE id = ?"
   ```
   
   **Pregunta guía:** ¿Cuándo usarías soft delete vs hard delete en Forestech?

8. **Probar en Main.java:**
   
   **Prueba completa (TÚ escribes el código):**
   
   ```java
   System.out.println("\n=== PRUEBA DE DELETE DE SUPPLIER ===");
   
   // Preparación: Insertar datos de prueba
   // 1. Crear Supplier S999 de prueba
   // 2. Crear Movement M999 asociado a S999
   // 3. Verificar que ambos existen en la BD
   
   // Prueba 1: Intentar eliminar proveedor CON movimientos (debe rechazar)
   System.out.println("\n--- Caso 1: Proveedor con movimientos ---");
   // boolean result1 = SupplierService.deleteSupplier("S999");
   // Debe retornar false
   // Debe mostrar mensaje: "tiene X movimiento(s) asociado(s)"
   
   // Prueba 2: Eliminar el movimiento asociado
   System.out.println("\n--- Caso 2: Eliminando movimiento asociado ---");
   // MovementService.deleteMovement("M999");  // Si este método existe
   // O manualmente en SQL Server
   
   // Prueba 3: Ahora eliminar el proveedor SIN movimientos (debe permitir)
   System.out.println("\n--- Caso 3: Proveedor sin movimientos ---");
   // boolean result2 = SupplierService.deleteSupplier("S999");
   // Debe retornar true
   // Debe mostrar: "Proveedor eliminado exitosamente"
   
   // Prueba 4: Intentar eliminar ID inexistente
   System.out.println("\n--- Caso 4: ID inexistente ---");
   // boolean result3 = SupplierService.deleteSupplier("SXXX");
   // Debe retornar false
   // Debe mostrar: "No existe proveedor con ID SXXX"
   
   // Verificación: Consultar en SQL Server
   // SELECT * FROM combustibles_suppliers WHERE id = 'S999';
   // No debe retornar resultados
   ```
   
   **Verificación manual:**
   ```sql
   -- Antes de ejecutar Main.java:
   SELECT * FROM combustibles_suppliers WHERE id = 'S999';
   SELECT * FROM combustibles_movements WHERE supplierId = 'S999';
   
   -- Después de ejecutar:
   SELECT * FROM combustibles_suppliers WHERE id = 'S999';
   -- No debe retornar nada (el proveedor fue eliminado)
   ```

9. **Depuración obligatoria:**
   
   **Coloca breakpoints en:**
   - Línea de validación: `if (!supplierExists(id))`
   - Línea de validación: `if (relatedMovements > 0)`
   - Línea de executeUpdate() del DELETE
   - Línea de verificación: `if (rowsAffected == 1)`
   
   **En el debugger:**
   - Verifica el valor de `supplierExists()` - ¿retorna true/false correctamente?
   - Inspecciona `relatedMovements` - ¿cuenta correctamente?
   - Evalúa `pstmt.toString()` - ¿la query DELETE tiene el ID correcto?
   - Verifica `rowsAffected` - ¿es 1 después de eliminar?
   - Consulta la BD después - ¿el registro desapareció?

**✅ Resultado esperado:** 
- Método `supplierExists()` verifica existencia eficientemente
- Método `countRelatedMovements()` cuenta dependencias
- Método `deleteSupplier()` con validaciones completas:
  - Rechaza IDs inexistentes
  - Rechaza proveedores con movimientos asociados
  - Elimina solo cuando es seguro
- Mensajes claros en cada caso (éxito, error, rechazo)
- Main.java demuestra los 4 escenarios de prueba
- Integridad referencial preservada
- Estructura actualizada:
  ```
  com.forestech/
  └── services/
      ├── MovementService.java
      ├── VehicleService.java
      └── SupplierService.java (NUEVO)
          ├── supplierExists(String) → boolean
          ├── countRelatedMovements(String) → int
          └── deleteSupplier(String) → boolean
  ```

**💡 Concepto clave - Integridad Referencial:**

```
Tabla: combustibles_suppliers
┌────┬────────┐
│ id │  name  │
├────┼────────┤
│S001│Shell   │
│S002│Petrobras│
└────┴────────┘
       ↑
       │ FK supplierId
       │
Tabla: combustibles_movements
┌────┬───────────┬────┐
│ id │supplierId │qty │
├────┼───────────┼────┤
│M001│   S001    │1000│
│M002│   S001    │ 500│
└────┴───────────┴────┘

¿Qué pasa si borras S001?
❌ Sin validación: Movimientos M001 y M002 quedan con supplierId = 'S001' 
   que ya no existe (datos huérfanos, relación rota)

✅ Con validación: DELETE rechazado porque countRelatedMovements() retorna 2
```

**💡 Concepto clave - DELETE vs UPDATE para desactivar:**

```java
// HARD DELETE (destrucción física):
String sql = "DELETE FROM suppliers WHERE id = ?";
// Resultado: Registro desaparece completamente de la BD
// Pros: Libera espacio, limpia datos inútiles
// Contras: Irreversible, pierde historial, rompe auditoría

// SOFT DELETE (marcado lógico):
String sql = "UPDATE suppliers SET isActive = 0, deletedAt = GETDATE(), deletedBy = ? WHERE id = ?";
// Resultado: Registro sigue en BD pero marcado como inactivo
// Pros: Reversible, mantiene historial, auditoría completa
// Contras: Ocupa espacio, queries necesitan WHERE isActive = 1

// Patrón común en producción:
SELECT * FROM suppliers WHERE isActive = 1;  // Solo activos
SELECT * FROM suppliers WHERE isActive = 0;  // Solo eliminados (para auditoría)
```

**💡 Concepto clave - Cascada de eliminación:**

```sql
-- Opción 1: Configurar en el esquema (ON DELETE CASCADE)
CREATE TABLE movements (
    id VARCHAR(50) PRIMARY KEY,
    supplierId VARCHAR(50),
    FOREIGN KEY (supplierId) REFERENCES suppliers(id) ON DELETE CASCADE
);
-- Si borras supplier S001 → SQL Server AUTOMÁTICAMENTE borra sus movimientos
-- ⚠️ PELIGROSO: Puede eliminar más de lo esperado

-- Opción 2: Validación manual en Java (lo que estás haciendo)
if (countRelatedMovements(id) > 0) {
    return false;  // No permitir DELETE
}
-- ✅ MÁS SEGURO: Control total sobre qué se elimina

-- Opción 3: Eliminación controlada en Java
public static boolean deleteSupplierAndMovements(String id) {
    // 1. Eliminar movimientos asociados
    // 2. Luego eliminar el supplier
    // Con transacción para que sea atómico (Fase 5)
}
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| DELETE sin WHERE borra toda la tabla | Olvidaste WHERE | SIEMPRE verifica query antes de ejecutar |
| "FK constraint violation" | Hay registros relacionados en otras tablas | Valida con countRelatedMovements() primero |
| rowsAffected = 0 | ID no existe o ya fue eliminado | Normal, verifica con supplierExists() primero |
| Datos huérfanos después de DELETE | No validaste integridad referencial | SIEMPRE cuenta relaciones antes de eliminar |
| No puedes "deshacer" DELETE | No usaste soft delete | Considera UPDATE isActive = 0 en producción |

**🔍 Comparación: DELETE vs UPDATE (soft delete):**

```java
// DELETE real (hard delete):
String sql = "DELETE FROM suppliers WHERE id = ?";
pstmt.executeUpdate();
// Antes: Registro existe en tabla
// Después: Registro NO existe (desapareció físicamente)
// Recuperación: Solo con backup de BD

// Soft delete (UPDATE):
String sql = "UPDATE suppliers SET isActive = 0 WHERE id = ?";
pstmt.executeUpdate();
// Antes: id=S001, name='Shell', isActive=1
// Después: id=S001, name='Shell', isActive=0 (sigue existiendo)
// Recuperación: UPDATE suppliers SET isActive = 1 WHERE id = 'S001'
```

**📊 Flujo completo de DELETE seguro:**

```
1. Main.java llama
   boolean success = SupplierService.deleteSupplier("S001");

2. Validación 1: ¿Existe el proveedor?
   if (!supplierExists("S001")) → return false;

3. Validación 2: ¿Tiene movimientos asociados?
   int count = countRelatedMovements("S001");
   if (count > 0) → return false;  // Rechazar

4. Ambas validaciones pasadas → Preparar DELETE
   "DELETE FROM suppliers WHERE id = ?"

5. Configurar parámetro y ejecutar
   pstmt.setString(1, "S001");
   int rows = pstmt.executeUpdate();

6. SQL Server ejecuta DELETE
   - Busca registro con id = 'S001'
   - Lo elimina permanentemente
   - Retorna 1 (una fila eliminada)

7. Java verifica y confirma
   if (rows == 1) → "✅ Eliminado exitosamente"

8. El registro ya no existe en la BD
   SELECT * FROM suppliers WHERE id = 'S001';
   -- 0 resultados
```

**💭 Reflexión crítica:**

**Pregunta:** ¿Qué estrategia de eliminación es mejor para Forestech?

**Opciones:**
1. **Hard DELETE:** Eliminar físicamente registros sin dependencias
2. **Soft DELETE:** Marcar como inactivos (isActive = 0) conservando datos
3. **Híbrido:** Soft delete por defecto, hard delete solo con confirmación explícita

**Para Forestech, considera:**
- Es un sistema de gestión de inventario (auditoría importante)
- Necesita historial de movimientos para reportes
- Puede haber errores de captura que requieran "deshacer"
- Regulaciones pueden requerir conservar datos históricos

**Recomendación:** Implementa soft delete en Fase 5 como regla estándar.

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 4.5: CRUD completo para Product

**Concepto clave:** Consolidar todo lo aprendido implementando las cuatro operaciones CRUD completas (CREATE, READ, UPDATE, DELETE) para una entidad, aplicando todos los patrones y validaciones de los checkpoints anteriores.

**📍 DÓNDE:** 
- **Modificar:** `ProductService.java` (ya existe de Fase 3, ahora extenderlo)
- **Main.java:** Para PROBAR el CRUD completo

**🎯 PARA QUÉ:** 
Hasta ahora implementaste operaciones dispersas:
- MovementService: CREATE (4.1) + READ (4.2)
- VehicleService: UPDATE (4.3)
- SupplierService: DELETE (4.4)

Ahora necesitas:
- ✅ **Dominar el patrón completo:** Las 4 operaciones para una misma entidad
- ✅ **Ver el ciclo de vida completo:** Crear → Leer → Actualizar → Eliminar
- ✅ **Practicar sin guía detallada:** Aplicar lo aprendido de forma autónoma
- ✅ **Consolidar conocimiento:** Repetir patrones hasta que sean automáticos

**Diferencia con checkpoints anteriores:**
```
Checkpoints 4.1-4.4:
- Guía paso a paso muy detallada
- Aprender cada operación individualmente
- Diferentes entidades para cada operación

Checkpoint 4.5:
- Especificaciones claras pero menos guía
- Implementar todas las operaciones juntas
- Misma entidad (Product) para consolidar
- TÚ decides cómo estructurar el código
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 5:** Usarás el CRUD de Product para gestionar catálogo de combustibles
- **Fase 6:** El menú llamará a estos métodos para gestión de productos
- **Fase 7:** Agregarás validaciones de negocio (stock mínimo, precio válido)
- **Fase 9:** Reportes de productos más/menos vendidos

**🎓 Analogía:**
Checkpoints 4.1-4.4 fueron como aprender a tocar notas individuales en un piano.
Checkpoint 4.5 es tocar tu primera melodía completa combinando todas las notas.

**Prompts sugeridos:**
```text
"Repasa las diferencias clave entre CREATE, READ, UPDATE y DELETE."
"¿Qué validaciones necesito para cada operación CRUD de Product?"
"¿En qué orden debo implementar los métodos CRUD? ¿Por qué?"
"¿Cómo pruebo un CRUD completo de forma efectiva?"
```

**Tareas paso a paso:**

**IMPORTANTE:** Este checkpoint tiene MENOS guía detallada. Aplica los patrones de 4.1-4.4.

1. **Revisar ProductService actual:**
   
   **Ya tienes de Fase 3:**
   - `getAllProducts()` → List<Product>
   - `getProductById(String id)` → Product
   - `getProductsByType(String type)` → List<Product>
   
   **Pregunta guía:** ¿Cuál de las 4 operaciones CRUD ya tienes implementada?

2. **Implementar CREATE - `createProduct(Product product)`:**
   
   **Especificaciones:**
   - Parámetros: Product product
   - Retorno: boolean
   - Query: INSERT INTO combustibles_products
   - Columnas: id, name, type, unit
   - Validaciones previas:
     - ¿El ID ya existe? (usar getProductById)
     - ¿Los datos son válidos? (name no vacío, type válido)
   
   **Aplica el patrón de:** MovementService.createMovement() (Checkpoint 4.1)
   
   **Pregunta guía:** ¿Qué haces si el producto ya existe? ¿Error o actualización?

3. **Implementar UPDATE - `updateProduct(String id, Product product)`:**
   
   **Especificaciones:**
   - Parámetros: String id, Product product (nuevos datos)
   - Retorno: boolean
   - Query: UPDATE combustibles_products SET ... WHERE id = ?
   - Columnas a actualizar: name, type, unit (NO el id)
   - Validaciones previas:
     - ¿El producto existe? (usar getProductById)
     - ¿Los nuevos datos son válidos?
   
   **Aplica el patrón de:** VehicleService.updateVehicle() (Checkpoint 4.3)
   
   **Pregunta guía:** ¿Por qué necesitas el ID como parámetro separado?

4. **Implementar DELETE - `deleteProduct(String id)`:**
   
   **Especificaciones:**
   - Parámetros: String id
   - Retorno: boolean
   - Query: DELETE FROM combustibles_products WHERE id = ?
   - Validaciones previas:
     - ¿El producto existe?
     - ¿Hay movimientos que usan este producto? (relación FK)
       Query de validación: `SELECT COUNT(*) FROM combustibles_movements WHERE productId = ?`
   
   **Aplica el patrón de:** SupplierService.deleteSupplier() (Checkpoint 4.4)
   
   **Pregunta guía:** ¿Qué es mejor para Product: hard delete o soft delete?

5. **Métodos helper adicionales (si necesitas):**
   
   **Considera crear:**
   - `productExists(String id)` → boolean
   - `countRelatedMovements(String productId)` → int
   - `searchProducts(String namePattern)` → List<Product> (con LIKE)
   
   **Pregunta guía:** ¿Cuáles de estos métodos son reutilizables en múltiples operaciones?

6. **Probar el ciclo CRUD completo en Main.java:**
   
   **Flujo de prueba (TÚ escribes el código completo):**
   
   ```java
   System.out.println("\n=== PRUEBA CRUD COMPLETO DE PRODUCT ===");
   
   // 1. CREATE - Crear producto de prueba
   System.out.println("\n--- 1. CREATE ---");
   // - Crear objeto Product con datos de prueba (ej: "P999", "Diesel Premium", "Combustible", "litros")
   // - Llamar a ProductService.createProduct(product)
   // - Verificar que retorna true
   // - Intentar crear de nuevo (duplicado) - debe retornar false
   
   // 2. READ - Consultar el producto creado
   System.out.println("\n--- 2. READ ---");
   // - Usar getAllProducts() - debe incluir P999
   // - Usar getProductById("P999") - debe retornar el producto
   // - Usar getProductsByType("Combustible") - debe incluir P999
   // - Imprimir los datos para verificar
   
   // 3. UPDATE - Modificar el producto
   System.out.println("\n--- 3. UPDATE ---");
   // - Crear nuevo objeto Product con datos modificados (mismo ID, name diferente)
   // - Llamar a updateProduct("P999", productModificado)
   // - Verificar que retorna true
   // - Consultar de nuevo con getProductById - debe tener los nuevos datos
   // - Imprimir antes y después para comparar
   
   // 4. DELETE - Eliminar el producto
   System.out.println("\n--- 4. DELETE ---");
   // - Verificar que NO tiene movimientos asociados
   // - Llamar a deleteProduct("P999")
   // - Verificar que retorna true
   // - Intentar consultarlo de nuevo - debe retornar null
   // - Intentar eliminarlo de nuevo - debe retornar false (ya no existe)
   
   // 5. Caso especial: Producto con movimientos
   System.out.println("\n--- 5. DELETE CON DEPENDENCIAS ---");
   // - Crear Product P998
   // - Crear Movement M998 asociado a P998
   // - Intentar deleteProduct("P998") - debe retornar false
   // - Mensaje: "tiene X movimiento(s) asociado(s)"
   ```
   
   **Verificación en SQL Server:**
   ```sql
   -- Durante las pruebas, ejecuta para verificar:
   SELECT * FROM combustibles_products WHERE id = 'P999';
   SELECT * FROM combustibles_movements WHERE productId = 'P999';
   ```

7. **Crear tabla de verificación:**
   
   **Completa esta tabla mientras pruebas (en papel o archivo):**
   
   | Operación | Input | Resultado Esperado | Resultado Real | ✅/❌ |
   |-----------|-------|-------------------|----------------|------|
   | CREATE nuevo | P999 válido | true, inserción exitosa | | |
   | CREATE duplicado | P999 existente | false, rechazado | | |
   | READ por ID | "P999" | Objeto Product | | |
   | READ todos | - | Lista con P999 incluido | | |
   | UPDATE existente | "P999" + nuevos datos | true, actualizado | | |
   | UPDATE inexistente | "PXXX" + datos | false, rechazado | | |
   | DELETE sin dependencias | "P999" | true, eliminado | | |
   | DELETE con movimientos | "P998" | false, rechazado | | |
   | DELETE inexistente | "PXXX" | false, no existe | | |

8. **Depuración y validación:**
   
   **Para CADA operación, verifica:**
   - [ ] Compila sin errores
   - [ ] No hay warnings de SQL
   - [ ] Validaciones previas funcionan
   - [ ] Mensajes de error son claros
   - [ ] rowsAffected es correcto (1 para modificaciones exitosas)
   - [ ] SQLException se maneja apropiadamente
   - [ ] La BD refleja los cambios esperados
   - [ ] No hay fugas de recursos (try-with-resources correcto)

9. **Refactorización (OPCIONAL):**
   
   **Después de que todo funcione, considera:**
   - ¿Hay código duplicado entre métodos?
   - ¿Los nombres de métodos son descriptivos?
   - ¿Los mensajes de error son consistentes?
   - ¿Puedes extraer validaciones comunes a métodos helper?
   
   **Ejemplo de refactorización:**
   ```java
   // Antes (código duplicado):
   public static boolean createProduct(Product p) {
       if (p.getName() == null || p.getName().isEmpty()) {
           System.out.println("Error: nombre inválido");
           return false;
       }
       // ...
   }
   
   public static boolean updateProduct(String id, Product p) {
       if (p.getName() == null || p.getName().isEmpty()) {
           System.out.println("Error: nombre inválido");
           return false;
       }
       // ...
   }
   
   // Después (método helper):
   private static boolean validateProductData(Product p) {
       if (p.getName() == null || p.getName().isEmpty()) {
           System.out.println("Error: nombre inválido");
           return false;
       }
       // Más validaciones...
       return true;
   }
   
   public static boolean createProduct(Product p) {
       if (!validateProductData(p)) return false;
       // ...
   }
   
   public static boolean updateProduct(String id, Product p) {
       if (!validateProductData(p)) return false;
       // ...
   }
   ```

**✅ Resultado esperado:** 
- ProductService con CRUD completo:
  - ✅ CREATE: createProduct()
  - ✅ READ: getAllProducts(), getProductById(), getProductsByType()
  - ✅ UPDATE: updateProduct()
  - ✅ DELETE: deleteProduct()
- Todas las operaciones con validaciones apropiadas
- Main.java con pruebas completas de ciclo CRUD
- Tabla de verificación completa con todos los casos
- Confianza en implementar CRUD para cualquier entidad
- Estructura final:
  ```
  com.forestech/
  └── services/
      ├── MovementService.java (CREATE + READ)
      ├── VehicleService.java (READ + UPDATE)
      ├── SupplierService.java (READ + DELETE)
      └── ProductService.java (CRUD COMPLETO - ✅)
          ├── getAllProducts() → List<Product>
          ├── getProductById(String) → Product
          ├── getProductsByType(String) → List<Product>
          ├── createProduct(Product) → boolean (NUEVO)
          ├── updateProduct(String, Product) → boolean (NUEVO)
          └── deleteProduct(String) → boolean (NUEVO)
  ```

**💡 Concepto clave - Ciclo de vida CRUD:**

```
Entidad Product en diferentes estados:

1. No existe aún
   └─> CREATE → Insertado en BD
   
2. Existe en BD
   ├─> READ → Consultar datos actuales
   │
   ├─> UPDATE → Modificar datos
   │   └─> READ → Ver cambios
   │
   └─> DELETE → Eliminar de BD
       └─> No existe (vuelta al estado 1)

Este ciclo aplica a TODA entidad:
- Movement, Vehicle, Supplier, Product
- User, Order, Invoice (futuras entidades)
```

**💡 Concepto clave - Patrones comunes en CRUD:**

```java
Patrón CREATE:
1. Validar datos de entrada
2. Verificar que NO exista (evitar duplicados)
3. INSERT con PreparedStatement
4. Verificar rowsAffected == 1
5. Retornar boolean

Patrón READ:
1. Query SELECT con o sin filtros
2. executeQuery() → ResultSet
3. Mapear ResultSet → Objetos
4. Retornar objeto o List<Objeto>

Patrón UPDATE:
1. Verificar que SÍ existe
2. Validar nuevos datos
3. UPDATE con WHERE (PK)
4. Verificar rowsAffected == 1
5. Retornar boolean

Patrón DELETE:
1. Verificar que existe
2. Validar integridad referencial (FKs)
3. DELETE con WHERE (PK)
4. Verificar rowsAffected == 1
5. Retornar boolean
```

**💡 Concepto clave - Orden de implementación recomendado:**

```
¿En qué orden crear los métodos CRUD?

1️⃣ READ (getById) primero:
   - Necesario para validaciones de otros métodos
   - Más simple (solo SELECT)
   - Te permite probar que la BD tiene datos

2️⃣ CREATE segundo:
   - Necesitas crear datos para probar UPDATE y DELETE
   - Depende de READ para validar duplicados

3️⃣ UPDATE tercero:
   - Necesita datos existentes (de CREATE)
   - Depende de READ para verificar existencia

4️⃣ DELETE último:
   - La operación más peligrosa
   - Necesita verificar relaciones con otras tablas
   - Depende de READ para validar existencia
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| CREATE duplica datos | No validaste existencia previa | Llama a productExists() antes de INSERT |
| UPDATE no hace nada | WHERE con ID inexistente | Valida con getProductById() primero |
| DELETE destruye datos relacionados | No verificaste FK | Cuenta movimientos relacionados antes de DELETE |
| Métodos muy largos | Todo el código en un solo método | Extrae validaciones a métodos helper |
| Código duplicado | Copiar/pegar entre métodos | Identifica patrones y créalos como helper methods |

**🔍 Auto-evaluación:**

Después de completar este checkpoint, debes poder responder SÍ a:

- [ ] ¿Puedo explicar la diferencia entre las 4 operaciones CRUD?
- [ ] ¿Entiendo por qué READ (getById) debe implementarse primero?
- [ ] ¿Sé validar existencia antes de UPDATE/DELETE?
- [ ] ¿Comprendo la importancia de validar integridad referencial?
- [ ] ¿Puedo implementar CRUD para cualquier entidad sin guía?
- [ ] ¿Entiendo cuándo retornar boolean vs objeto vs List?
- [ ] ¿Sé manejar todos los casos (éxito, error, rechazo)?
- [ ] ¿Puedo probar un CRUD completo sistemáticamente?

**📊 Comparación: Entidades completadas en Fase 4:**

| Service | CREATE | READ | UPDATE | DELETE | Completo |
|---------|--------|------|--------|--------|----------|
| ProductService | ✅ 4.5 | ✅ 3.4 | ✅ 4.5 | ✅ 4.5 | ✅ 100% |
| MovementService | ✅ 4.1 | ✅ 4.2 | ⚪ | ⚪ | 🟡 50% |
| VehicleService | ⚪ | ✅ 4.3 | ✅ 4.3 | ⚪ | 🟡 50% |
| SupplierService | ⚪ | ✅ 4.4 | ⚪ | ✅ 4.4 | 🟡 50% |

**🎯 Desafío extra (OPCIONAL):**

Si terminaste rápido y quieres más práctica:

1. **Completa MovementService:**
   - Implementa updateMovement() y deleteMovement()
   - Validaciones especiales: ¿puedes modificar un movimiento histórico?

2. **Completa VehicleService:**
   - Implementa createVehicle() y deleteVehicle()
   - Validación: ¿vehiculo tiene movimientos antes de eliminar?

3. **Completa SupplierService:**
   - Implementa createSupplier() y updateSupplier()
   - Validación: ¿nombre único para proveedores?

4. **Implementa métodos de búsqueda avanzada:**
   - `searchProducts(String namePattern)` con LIKE
   - `getProductsInPriceRange(double min, double max)`
   - `getProductsByMultipleTypes(List<String> types)` con IN

**⏱️ Tiempo estimado:** 4-5 horas

---

## 🧪 Refuerzos de calidad

**Pruebas manuales tras cada operación:**
- Consulta la tabla en SQL Server ANTES y DESPUÉS de cada operación
- Compara lo que ves en la BD con lo que esperabas
- Verifica conteo de filas con `SELECT COUNT(*) FROM tabla`
- Usa transacciones de prueba: BEGIN TRANSACTION ... ROLLBACK para probar sin modificar datos

**Registro de aprendizaje obligatorio:**
- Documenta en `JAVA_LEARNING_LOG.md`:
  - Qué operación implementaste y cuándo
  - Problemas encontrados y cómo los resolviste
  - Queries SQL que funcionaron/fallaron
  - Conceptos nuevos aprendidos
  - Dudas pendientes para investigar

**Script de limpieza de datos de prueba:**
```sql
-- Crear archivo cleanup.sql para restaurar BD a estado inicial
DELETE FROM combustibles_movements WHERE id LIKE '%999' OR id LIKE '%TEST%';
DELETE FROM combustibles_vehicles WHERE id LIKE '%999' OR id LIKE '%TEST%';
DELETE FROM combustibles_suppliers WHERE id LIKE '%999' OR id LIKE '%TEST%';
DELETE FROM combustibles_products WHERE id LIKE '%999' OR id LIKE '%TEST%';

-- Insertar datos de prueba consistentes
INSERT INTO combustibles_products VALUES ('P999', 'Test Diesel', 'Combustible', 'litros');
-- etc.
```

**Testing inicial (preparación para Fase 7):**
- Familiarízate con JUnit (investiga, no implementes aún)
- Piensa: ¿cómo probarías createMovement() automáticamente?
- Identifica qué validaciones son críticas para tests unitarios

**Buenas prácticas aplicadas:**
- ✅ PreparedStatement en el 100% de las queries
- ✅ try-with-resources para todos los recursos (Connection, PreparedStatement, ResultSet)
- ✅ Validaciones previas antes de operaciones destructivas
- ✅ Mensajes de error descriptivos y útiles
- ✅ Retornos consistentes (boolean para operaciones, objetos/listas para consultas)
- ✅ Never return null para listas (siempre lista vacía)
- ✅ Separación de responsabilidades (Service solo acceso a BD, no lógica de negocio)

---

## ✅ Checklist de salida de Fase 4

**Conocimiento de JDBC:**
- [ ] Domino PreparedStatement y puedo explicar cómo evita SQL Injection
- [ ] Entiendo la diferencia entre executeQuery() y executeUpdate()
- [ ] Sé usar try-with-resources para cerrar recursos automáticamente
- [ ] Puedo explicar qué es ResultSet y cómo funciona como cursor
- [ ] Comprendo el significado de rowsAffected (0, 1, >1)

**Implementación de operaciones:**
- [ ] Mis métodos INSERT (CREATE) guardan objetos Java en la BD correctamente
- [ ] Mis métodos SELECT (READ) retornan List<Objeto> correctamente mapeados
- [ ] Mis métodos UPDATE verifican existencia antes de actualizar
- [ ] Mis métodos DELETE validan integridad referencial antes de borrar
- [ ] Todos los métodos manejan SQLException apropiadamente

**Validaciones y buenas prácticas:**
- [ ] Valido existencia antes de UPDATE/DELETE (evito rowsAffected = 0 silencioso)
- [ ] Verifico integridad referencial antes de DELETE (cuento relaciones FK)
- [ ] Mis servicios retornan listas vacías en vez de null
- [ ] Uso PreparedStatement con placeholders (?) para TODOS los parámetros
- [ ] NUNCA concateno strings en queries SQL (seguridad)
- [ ] Siempre incluyo WHERE en UPDATE y DELETE (evito modificar toda la tabla)

**Estructura y organización:**
- [ ] Tengo al menos 4 clases Service creadas (Movement, Vehicle, Supplier, Product)
- [ ] ProductService tiene CRUD completo (4 operaciones)
- [ ] Main.java tiene pruebas para cada operación implementada
- [ ] El código compila sin errores ni warnings
- [ ] Puedo ejecutar todas las pruebas y verificar resultados en SQL Server

**Documentación:**
- [ ] Documenté en JAVA_LEARNING_LOG.md los aprendizajes de cada checkpoint
- [ ] Actualicé JAVA_NEXT_STEPS.md con dudas y siguiente objetivo
- [ ] Creé commits de Git para cada checkpoint completado
- [ ] Tengo script cleanup.sql para restaurar datos de prueba

**Auto-evaluación práctica:**
- [ ] Puedo implementar CREATE para cualquier entidad sin consultar apuntes
- [ ] Puedo implementar READ con filtros sin copiar código anterior
- [ ] Puedo implementar UPDATE con validaciones sin ayuda
- [ ] Puedo implementar DELETE con verificación de FK por mi cuenta
- [ ] Entiendo CUÁNDO usar cada operación CRUD

**Casos de prueba verificados:**

| Operación | Caso Exitoso | Caso Rechazo | Caso Error |
|-----------|--------------|--------------|------------|
| CREATE | Insertar nuevo | Duplicado (ID existe) | Datos inválidos |
| READ | Encontrar existente | Lista vacía (sin datos) | ID null |
| UPDATE | Modificar existente | ID no existe | Datos inválidos |
| DELETE | Eliminar sin FKs | Con movimientos asociados | ID no existe |

**📊 Tabla de Services completados:**

Estado de implementación CRUD por servicio:

| Servicio | CREATE | READ | UPDATE | DELETE | % Completo |
|----------|--------|------|--------|--------|------------|
| **ProductService** | ✅ | ✅ | ✅ | ✅ | **100%** ✨ |
| **MovementService** | ✅ | ✅ | 🎯 | 🎯 | **50%** |
| **VehicleService** | 🎯 | ✅ | ✅ | 🎯 | **50%** |
| **SupplierService** | 🎯 | ✅ | 🎯 | ✅ | **50%** |

**Leyenda:** ✅ Implementado | 🎯 Checkpoint cubierto conceptualmente | ⚪ No cubierto

**Objetivo mínimo de Fase 4:** ProductService al 100% (COMPLETADO ✅)

**🎯 Desafío final (OPCIONAL pero recomendado):** 

Completa al menos uno más de los services al 100%. Esto te dará:
- Práctica adicional sin guía
- Confianza en el patrón CRUD
- Código más completo para Fase 5
- Preparación para implementar cualquier entidad

**Sugerencia:** Completa MovementService (es el más importante para Forestech).

---

## 🚀 Próximo paso: FASE 5 - Lógica de Negocio

**¿Qué dominas ahora?**
- ✅ Conectar Java con SQL Server
- ✅ Ejecutar queries SQL desde Java
- ✅ Insertar, consultar, actualizar y eliminar datos
- ✅ Validar existencia e integridad referencial
- ✅ Mapear ResultSet a objetos Java

**¿Qué te falta?**
- ❌ Validaciones de negocio (reglas de Forestech, no solo validaciones técnicas)
- ❌ Transacciones (múltiples operaciones atómicas)
- ❌ Cálculos complejos (inventario actual, reportes)
- ❌ Manejo robusto de errores (excepciones custom)

**En la siguiente fase aprenderás a:**

**1. Implementar reglas de negocio:**
- No permitir SALIDAS si no hay inventario suficiente
- Calcular inventario actual dinámicamente (ENTRADAS - SALIDAS)
- Validar rangos de fechas, cantidades mínimas/máximas
- Aplicar lógica específica de Forestech

**2. Manejar transacciones:**
```java
// Ejemplo: Transferencia de combustible entre vehículos
try {
    conn.setAutoCommit(false);  // Iniciar transacción
    
    // Operación 1: Restar combustible del vehículo origen
    // Operación 2: Sumar combustible al vehículo destino
    // Operación 3: Registrar el movimiento
    
    conn.commit();  // ✅ Éxito: aplicar todas las operaciones
} catch (Exception e) {
    conn.rollback();  // ❌ Error: deshacer TODAS las operaciones
}
// TODO o NADA (atomicidad)
```

**3. Crear cálculos complejos:**
- `calculateCurrentInventory(String fuelType)` - inventario disponible
- `getTotalValueByPeriod(String start, String end)` - valor total en rango
- `getMostUsedVehicle()` - vehículo con más movimientos
- `getLowStockProducts(double minStock)` - productos con bajo inventario

**4. Implementar soft delete:**
```java
// En vez de DELETE físico:
UPDATE suppliers SET isActive = 0, deletedAt = NOW(), deletedBy = 'user123' WHERE id = ?;

// Queries automáticamente filtran inactivos:
SELECT * FROM suppliers WHERE isActive = 1;
```

**5. Validaciones de negocio robustas:**
```java
public static boolean createMovement(Movement m) {
    // Validación técnica (ya lo haces):
    if (m.getQuantity() <= 0) return false;
    
    // Validación de negocio (NUEVO en Fase 5):
    if (m.getType().equals("SALIDA")) {
        double inventory = calculateInventory(m.getFuelType());
        if (inventory < m.getQuantity()) {
            System.out.println("❌ Inventario insuficiente. Disponible: " + inventory);
            return false;
        }
    }
    
    // Si pasa todas las validaciones, insertar
}
```

**¿Por qué lógica de negocio es crítica?**

```
FASE 4 (CRUD técnico):
Usuario: "Vender 5000 litros de Diesel"
Sistema: ✅ "OK, insertado en BD"
Problema: ¡Solo había 1000 litros disponibles!

FASE 5 (CRUD + Negocio):
Usuario: "Vender 5000 litros de Diesel"
Sistema: 
  1. Calcula inventario actual: 1000 litros
  2. Compara: 5000 > 1000
  3. ❌ "Error: Solo hay 1000 litros disponibles"
  4. No inserta el movimiento inválido
Resultado: ¡Datos consistentes y lógicos!
```

**Diferencia clave:**
```
Validación Técnica (Fase 4):
- ¿El dato es del tipo correcto? (String, double, etc.)
- ¿El ID existe en la BD?
- ¿Se respetan las constraints de SQL? (PK, FK, NOT NULL)

Validación de Negocio (Fase 5):
- ¿El dato tiene sentido en el contexto de Forestech?
- ¿Se respetan las reglas del dominio? (no vender más de lo que tienes)
- ¿La operación es lógicamente válida? (no tener inventario negativo)
```

**Estructura que tendrás en Fase 5:**
```
com.forestech/
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Product.java
├── services/ (acceso a BD - sin lógica)
│   ├── MovementService.java
│   ├── VehicleService.java
│   ├── SupplierService.java
│   └── ProductService.java
├── managers/ (lógica de negocio - NUEVO ROL)
│   ├── InventoryManager.java (cálculos de inventario)
│   ├── MovementManager.java (validaciones de negocio para movimientos)
│   └── ReportManager.java (generación de reportes)
└── validators/ (NUEVO)
    ├── MovementValidator.java
    └── ProductValidator.java
```

**Preparación para Fase 5:**

Antes de empezar la siguiente fase, asegúrate de:
1. Completar ProductService al 100% (CRUD completo)
2. Tener al menos las operaciones CREATE y READ de MovementService
3. Documentar todo en JAVA_LEARNING_LOG.md
4. Revisar y entender los conceptos de Fase 4
5. Investigar (solo leer, no implementar): transacciones en JDBC, setAutoCommit, commit, rollback

**Conceptos que investigarás en Fase 5:**
- Transacciones ACID (Atomicity, Consistency, Isolation, Durability)
- Managers vs Services (separación de lógica de negocio)
- Validators (clases especializadas en validaciones)
- Cálculos agregados con SQL (SUM, AVG, COUNT, GROUP BY)
- Soft delete y auditoría
- Excepciones custom de negocio

**⏱️ Tiempo total Fase 4:** 13-18 horas distribuidas en 2 semanas

**📊 Progreso en el roadmap de Forestech:**

```
✅ Fase 0: Preparación del entorno
✅ Fase 1: Fundamentos de Java
✅ Fase 2: Programación Orientada a Objetos
✅ Fase 3: Conexión a SQL Server (JDBC básico)
✅ Fase 4: Operaciones CRUD ← COMPLETASTE ESTO 🎉
🎯 Fase 5: Lógica de Negocio ← SIGUIENTE
⚪ Fase 6: Menú Interactivo (CLI)
⚪ Fase 7: Manejo de Errores y Validaciones Avanzadas
⚪ Fase 8: Características Avanzadas
⚪ Fase 9: Reportes y Análisis
⚪ Fase 10: Optimización y Deployment
```

**🎓 Logros desbloqueados en Fase 4:**
- 🏆 **CRUD Master:** Dominas las 4 operaciones básicas de BD
- 🔐 **Security Aware:** Usas PreparedStatement para prevenir SQL Injection
- 🎯 **Data Integrity Guardian:** Validas FK antes de eliminar
- 📊 **Mapper Pro:** Conviertes ResultSet a objetos Java fluidamente
- 🐛 **Debugger:** Sabes usar breakpoints para rastrear flujo de datos

**💪 Habilidades nuevas adquiridas:**
1. Insertar datos desde Java a SQL Server
2. Consultar y filtrar datos dinámicamente
3. Actualizar registros con validaciones previas
4. Eliminar con protección de integridad referencial
5. Mapear filas de BD a objetos de dominio
6. Manejar SQLException apropiadamente
7. Usar try-with-resources para gestión de recursos
8. Validar datos antes de operaciones destructivas

**🚀 Estás listo para Fase 5 cuando puedas:**
- Implementar CRUD completo para cualquier entidad sin consultar apuntes
- Explicar con tus palabras qué hace cada operación
- Identificar y corregir errores de SQL Injection
- Depurar problemas de BD con breakpoints
- Diseñar pruebas para verificar cada operación

---

## 📚 Recursos adicionales

**Documentación oficial:**
- [JDBC Tutorial - Oracle](https://docs.oracle.com/javase/tutorial/jdbc/)
- [PreparedStatement - JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)
- [SQL Server JDBC Driver](https://learn.microsoft.com/en-us/sql/connect/jdbc/)

**Videos recomendados:**
- Búsqueda: "JDBC CRUD operations tutorial" (operaciones completas)
- Búsqueda: "PreparedStatement vs Statement" (seguridad)
- Búsqueda: "SQL Injection examples" (entender vulnerabilidades)
- Búsqueda: "Database transactions Java" (preparación para Fase 5)

**Práctica adicional:**
- [HackerRank SQL](https://www.hackerrank.com/domains/sql) - Ejercicios SQL progresivos
- [LeetCode Database](https://leetcode.com/problemset/database/) - Problemas de BD

**Conceptos para investigar (preparación Fase 5):**
- Transacciones en JDBC (commit, rollback)
- DAO Pattern (Data Access Object)
- Repository Pattern
- Unit of Work Pattern
- Soft Delete strategies
- Audit trails (auditoría de cambios)

**🎯 Ejercicios de repaso antes de Fase 5:**

1. **Sin mirar código anterior, implementa:**
   - `CategoryService` con CRUD completo para categorías de combustible
   - Tabla: `combustibles_categories (id, name, description)`

2. **Diseña en papel (sin código):**
   - ¿Qué validaciones necesita cada operación CRUD para Vehicle?
   - ¿Qué relaciones FK tiene Movement? ¿Cómo afectan a DELETE?

3. **Depuración mental:**
   - Si `rowsAffected = 0` en UPDATE, ¿cuáles son las 3 causas posibles?
   - Si DELETE lanza SQLException, ¿qué verificarías primero?

---

**¡Recuerda documentar todo en JAVA_LEARNING_LOG.md!** 📝

**¡Felicitaciones por completar Fase 4!** 🎉

Ya no eres un principiante en bases de datos. Dominas las operaciones fundamentales y entiendes la importancia de validaciones y seguridad. En Fase 5, llevarás estos conocimientos al siguiente nivel agregando inteligencia de negocio a tu aplicación.

**Siguiente archivo a consultar:** `roadmaps/FASE_05_LOGICA_NEGOCIO.md` (cuando estés listo)
