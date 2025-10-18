# 🔌 FASE 3: CONEXIÓN A SQL SERVER (Semana 5)

> Objetivo general: comprender JDBC, conectar Java con SQL Server y ejecutar las primeras consultas.

---

## 🧠 Antes de empezar

- 📚 **Fundamentos SQL:** antes de tocar JDBC, repasa manualmente en SQL Server Management Studio o Azure Data Studio:
  - Consultas básicas `SELECT`, `INSERT`, `UPDATE`, `DELETE`
  - Conceptos de tablas, columnas, tipos de datos, PK/FK, normalización ligera
  - Cláusulas `WHERE`, `ORDER BY`, `GROUP BY`
- 📝 Documenta en `JAVA_LEARNING_LOG.md` las consultas manuales que ejecutaste y resultados
- 🧪 Practica consultas con `FORESTECH` para tener contexto cuando programes
- 🔁 **Git loop:** al completar cada checkpoint crea un commit con mensaje claro (`git commit -m "fase3 checkpoint 3.1"`).
- 🎯 **ORGANIZACIÓN CLARA:** Introduciremos nuevos paquetes (`config`, `services`) para mantener la arquitectura profesional
- ✍️ **APRENDIZAJE ACTIVO:** Recibirás DIRECTIVAS, no código completo. TÚ escribirás y entenderás cada línea.

---

## 📦 ESTRUCTURA AL TERMINAR FASE 2

Antes de empezar Fase 3, debes tener esta estructura de tu proyecto:

```
com.forestech/
├── Main.java
├── AppConfig.java
├── MenuHelper.java
├── DataDisplay.java
├── InputHelper.java
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   └── Supplier.java
└── managers/
    └── MovementManager.java
```

**Verifica que esto esté completo antes de continuar.**

---

## 🎯 ESTRUCTURA AL TERMINAR FASE 3

Al finalizar esta fase tendrás:

```
com.forestech/
├── Main.java
├── AppConfig.java
├── MenuHelper.java
├── DataDisplay.java
├── InputHelper.java
├── config/
│   └── DatabaseConnection.java (NUEVO - conexión centralizada)
├── models/
│   ├── Movement.java
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Product.java (NUEVO)
├── managers/
│   └── MovementManager.java
└── services/
    └── ProductService.java (NUEVO - acceso a BD)
```

**Filosofía de organización:**
- **config/**: Configuraciones técnicas (conexión a BD, credenciales)
- **models/**: POJOs (Plain Old Java Objects) - clases con datos, sin lógica de BD
- **services/**: Clases que interactúan con la BD (SELECT, INSERT, UPDATE, DELETE)
- **managers/**: Lógica de negocio que usa services y models
- **Main.java**: Solo pruebas y punto de entrada

---

## ✅ Checkpoint 3.1: Configurar JDBC Driver

**Concepto clave:** JDBC es la API estándar de Java para comunicarse con bases de datos.

**📍 DÓNDE:** 
- **Archivo:** `pom.xml` en la raíz del proyecto `forestech-cli-java/`
- **Terminal:** Para ejecutar Maven
- **Main.java:** NO tocar (todavía no usaremos la BD)

**🎯 PARA QUÉ:** 
Sin el driver JDBC de SQL Server, Java no puede "hablar" con tu base de datos. Es como tener un teléfono sin SIM card.

El driver JDBC:
- ✅ **Traduce** las llamadas de Java a comandos que SQL Server entiende
- ✅ **Gestiona** la conexión de red entre tu aplicación y el servidor
- ✅ **Maneja** el protocolo de comunicación específico de SQL Server (TDS - Tabular Data Stream)

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Usarás este driver para INSERT, UPDATE, DELETE de movimientos
- **Fase 5:** Ejecutarás queries complejas con JOINs entre tablas
- **Fase 9:** Generarás reportes consultando datos históricos

**🎓 Analogía:**
- **Tu aplicación Java**: Turista que solo habla español
- **SQL Server**: Local que solo habla alemán
- **Driver JDBC**: Traductor que permite la comunicación

**Prompts sugeridos:**
```text
"Explícame con analogía cómo funciona JDBC como puente entre Java y SQL Server."
"¿Dónde guarda Maven las dependencias y cómo puedo verificarlo?"
"¿Qué diferencia hay entre JDBC (API) y el driver de SQL Server (implementación)?"
"¿Por qué cada base de datos (MySQL, PostgreSQL, SQL Server) necesita su propio driver?"
```

**Tareas paso a paso:**

1. **Abrir `pom.xml`** en el editor

2. **Localizar la sección `<dependencies>`:**
   - Si no existe, debes crearla dentro de `<project>`
   - Debe estar antes de `</project>` y después de `<build>`

3. **Agregar la dependencia del driver de SQL Server:**
   
   **Especificaciones (TÚ debes escribirlo):**
   - groupId: `com.microsoft.sqlserver`
   - artifactId: `mssql-jdbc`
   - version: `12.8.1.jre11` o superior
   
   **Sintaxis de dependencia:**
   ```xml
   <dependency>
       <groupId>...</groupId>
       <artifactId>...</artifactId>
       <version>...</version>
   </dependency>
   ```
   
   **Pregunta guía:** ¿Por qué usamos `jre11` en la versión? ¿Qué pasa si tu proyecto usa Java 17?

4. **Guardar** el archivo pom.xml

5. **Descargar dependencia desde terminal:**
   
   **Comandos a ejecutar (en orden):**
   ```bash
   cd forestech-cli-java/
   mvn clean install
   ```
   
   **Observa la salida:** Deberías ver líneas como:
   ```
   Downloading from central: https://repo.maven.apache.org/.../mssql-jdbc-12.8.1.jre11.jar
   ```

6. **Verificar descarga exitosa:**
   
   **Opción A - Por terminal:**
   ```bash
   ls ~/.m2/repository/com/microsoft/sqlserver/mssql-jdbc/
   ```
   Debes ver la carpeta con tu versión (ej: `12.8.1.jre11/`)
   
   **Opción B - Por IntelliJ:**
   - Ve a: View → Tool Windows → Maven
   - Expande: Dependencies → compile
   - Busca: `com.microsoft.sqlserver:mssql-jdbc`

7. **Compilar el proyecto:**
   ```bash
   mvn clean compile
   ```
   Debe compilar sin errores.

**✅ Resultado esperado:** 
- Maven descarga el driver sin errores
- El proyecto compila exitosamente con `mvn clean compile`
- Puedes ver el JAR del driver en tu repositorio local Maven
- En IntelliJ, la dependencia aparece en el árbol de Maven

**💡 Concepto clave:** Las dependencias de Maven se descargan UNA VEZ y se reutilizan en todos tus proyectos. Por eso se guardan en `~/.m2/repository/` (repositorio local compartido).

**⚠️ PROBLEMAS COMUNES:**

| Problema | Causa | Solución |
|----------|-------|----------|
| "Could not resolve dependencies" | Sin internet o proxy | Verifica conexión y configuración de Maven |
| "Unsupported class version" | Driver para Java superior | Usa versión jre11 o jre8 |
| Dependencia no aparece en IntelliJ | Cache desactualizado | Reimport: clic derecho en proyecto → Maven → Reload Project |

**⏱️ Tiempo estimado:** 30 minutos

---

## ✅ Checkpoint 3.2: Clase `DatabaseConnection`

**Concepto clave:** Centralizar la lógica de conexión a BD en una clase específica evita duplicar código y facilita mantenimiento.

**📍 DÓNDE:** 
- **Crear paquete nuevo:** `config` dentro de `com.forestech/`
- **Crear archivo:** `DatabaseConnection.java` en `forestech-cli-java/src/main/java/com/forestech/config/`
- **Main.java:** Solo para PROBAR la conexión (1 línea de prueba)

**🎯 PARA QUÉ:** 
Sin esta clase, cada vez que necesites conectarte a la BD tendrías que:
- ❌ Escribir la URL, usuario y contraseña en CADA servicio
- ❌ Repetir la lógica de `DriverManager.getConnection()` en 10+ lugares
- ❌ Si cambias la contraseña, modificar 10+ archivos
- ❌ Dificultar pruebas (no puedes cambiar fácilmente a BD de prueba)

Con DatabaseConnection:
- ✅ **Centralizas** la configuración (URL, usuario, contraseña) en UN SOLO lugar
- ✅ **Reutilizas** el método de conexión desde cualquier servicio
- ✅ **Facilitas cambios** (si cambias de BD, solo modificas un archivo)
- ✅ **Pruebas más simples** (puedes cambiar a BD de prueba fácilmente)
- ✅ **Separación de responsabilidades** (config vs lógica de negocio)

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Todos los servicios (MovementService, VehicleService) usarán `DatabaseConnection.getConnection()`
- **Fase 5:** Agregarás manejo de transacciones aquí (commit/rollback)
- **Fase 8:** Migrarás credenciales a archivo externo (`application.properties`)
- **Fase 10:** Configurarás diferentes conexiones para dev/producción

**🎓 Analogía:**
- **DatabaseConnection**: Recepcionista del hotel que gestiona las llaves de todas las habitaciones
- **Servicios (ProductService, etc.)**: Huéspedes que piden su llave a la recepcionista
- **Sin DatabaseConnection**: Cada huésped tendría que fabricar su propia llave (caos total)

**Prompts sugeridos:**
```text
"¿Qué es una connection string y cuáles son sus partes?"
"¿Por qué es mala práctica hardcodear credenciales en el código?"
"Explícame qué hace try-with-resources y por qué es importante para conexiones."
"¿Qué es un constructor privado y para qué sirve?"
"¿Por qué DatabaseConnection tiene métodos static?"
```

**Tareas paso a paso:**

1. **Crear el paquete `config`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "config"
   - **¿Por qué `config`?** Convención: configuraciones técnicas van separadas de lógica

2. **Crear la clase DatabaseConnection:**
   - Clic derecho en `config` → New → Java Class → "DatabaseConnection"
   - Declarar como clase pública

3. **Constructor privado (patrón Utility Class):**
   
   **Especificaciones:**
   - Constructor privado sin parámetros
   - Cuerpo vacío (o lanza excepción)
   
   **Pregunta guía:** ¿Por qué NO queremos que nadie haga `new DatabaseConnection()`?
   
   **Pista:** Esta es una clase de utilidad con solo métodos estáticos. No tiene sentido crear objetos de ella.

4. **Declarar constantes privadas de configuración:**
   
   **Especificaciones (TÚ debes escribir):**
   - URL de conexión (String, private static final)
     - Formato: `jdbc:sqlserver://localhost:1433;databaseName=FORESTECH`
     - **Investiga:** ¿Qué significa cada parte de la URL?
   - Usuario (String, private static final)
     - Tu usuario de SQL Server (ej: "sa" o tu usuario Windows)
   - Contraseña (String, private static final)
     - Tu contraseña de SQL Server
   
   **Pregunta guía:** ¿Por qué usar `final` para credenciales?

5. **Crear método estático `getConnection()`:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `Connection` (del paquete java.sql)
   - Parámetros: ninguno
   - Declaración de excepción: `throws SQLException`
   
   **Implementación (TÚ debes hacerla):**
   - Usar `DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)`
   - Retornar el objeto Connection obtenido
   
   **Import necesario:**
   ```java
   import java.sql.Connection;
   import java.sql.DriverManager;
   import java.sql.SQLException;
   ```

6. **Crear método estático `testConnection()`:**
   
   **Propósito:** Verificar que la configuración de conexión es correcta.
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `void`
   - Parámetros: ninguno
   - NO declarar throws (captura SQLException internamente)
   
   **Lógica a implementar (TÚ escribes el código):**
   
   a) Usar try-with-resources para obtener conexión:
      ```java
      try (Connection conn = getConnection()) {
          // código aquí
      } catch (SQLException e) {
          // manejo de error aquí
      }
      ```
   
   b) Si la conexión es exitosa:
      - Obtener metadata de la conexión: `conn.getMetaData()`
      - Imprimir nombre de la BD: `metadata.getDatabaseProductName()`
      - Imprimir mensaje de éxito con el nombre de la BD
   
   c) Si falla (bloque catch):
      - Imprimir mensaje de error claro
      - Imprimir el mensaje de la excepción: `e.getMessage()`
   
   **Pregunta guía:** ¿Por qué usamos try-with-resources en vez de try-finally?

7. **Probar en Main.java:**
   
   **Agregar al método main (después de las pruebas de Fase 2):**
   ```java
   System.out.println("\n=== PRUEBA DE CONEXIÓN A BD ===");
   DatabaseConnection.testConnection();
   ```
   
   **Compilar y ejecutar:**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.forestech.Main"
   ```

**✅ Resultado esperado:** 
- Ver mensaje "✅ Conexión exitosa a FORESTECH (o el nombre de tu BD)" en consola
- Si falla, ver mensaje de error claro indicando el problema específico
- Archivo DatabaseConnection.java en el paquete `config`
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java (NUEVO)
  └── models/
      └── ...
  ```

**💡 Concepto clave - try-with-resources:**
```
SIN try-with-resources (antiguo):
Connection conn = null;
try {
    conn = getConnection();
    // usar conexión
} finally {
    if (conn != null) conn.close();  // Código repetitivo
}

CON try-with-resources (moderno):
try (Connection conn = getConnection()) {
    // usar conexión
}  // ✅ Se cierra automáticamente
```

**⚠️ PREREQUISITOS:**
- SQL Server debe estar corriendo (verifica con SQL Server Management Studio)
- El puerto 1433 debe estar abierto
- La base de datos FORESTECH debe existir
- Usuario/contraseña deben ser correctos

**🔍 Depuración - Errores comunes:**

| Error | Causa probable | Solución |
|-------|---------------|----------|
| "Cannot connect to server" | SQL Server no está corriendo | Inicia el servicio SQL Server |
| "Login failed for user" | Credenciales incorrectas | Verifica usuario/contraseña |
| "Database 'FORESTECH' does not exist" | BD no creada | Crea la BD en SQL Server Management Studio |
| "Connection timed out" | Firewall o puerto cerrado | Verifica firewall y que puerto 1433 esté abierto |
| "No suitable driver found" | Driver no descargado | Ejecuta `mvn clean install` |

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 3.3: Primera query `SELECT` con Service

**Concepto clave:** Los Services son clases especializadas en interactuar con la base de datos. Separan la lógica de acceso a datos de la lógica de negocio.

**📍 DÓNDE:** 
- **Crear paquete nuevo:** `services` dentro de `com.forestech/`
- **Crear archivo:** `ProductService.java` en `forestech-cli-java/src/main/java/com/forestech/services/`
- **Main.java:** Para PROBAR el servicio (llamar al método)

**🎯 PARA QUÉ:** 
Hasta ahora solo probaste la conexión. Ahora necesitas:
- ✅ **Leer datos** de las tablas existentes en SQL Server
- ✅ **Ejecutar queries SQL** desde Java
- ✅ **Procesar resultados** con ResultSet
- ✅ **Separar responsabilidades** (Service se encarga de BD, no el Manager ni Main)

**Arquitectura en capas:**
```
Main.java (prueba)
    ↓ llama a
ProductService.java (acceso a BD)
    ↓ usa
DatabaseConnection.java (obtiene conexión)
    ↓ conecta con
SQL Server (base de datos)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Crearás `MovementService` con INSERT, UPDATE, DELETE siguiendo este patrón
- **Fase 5:** Los Services retornarán listas de objetos (no solo imprimirán)
- **Fase 6:** El menú interactivo llamará a Services para mostrar datos al usuario
- **Fase 9:** Services generarán reportes con queries complejas

**🎓 Analogía:**
- **ProductService**: Bibliotecario que busca libros en el sistema
- **DatabaseConnection**: Llave de acceso a la biblioteca
- **SQL Server**: Los estantes con todos los libros
- **Main.java**: Persona que le pide al bibliotecario "muéstrame todos los productos"

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre Statement y PreparedStatement?"
"Explícame como cursor qué hace ResultSet al moverse con rs.next()."
"¿Por qué necesito try-with-resources para Connection, Statement y ResultSet?"
"¿Qué hace rs.getString() vs rs.getInt() vs rs.getDouble()?"
"¿Dónde va la lógica SQL: en Service o en Manager? ¿Por qué?"
```

**Tareas paso a paso:**

1. **Crear el paquete `services`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "services"
   - **¿Por qué `services`?** Convención: clases que acceden a BD van en services

2. **Crear la clase ProductService:**
   - Clic derecho en `services` → New → Java Class → "ProductService"
   - Declarar como clase pública
   - Constructor privado (es una utility class con métodos estáticos por ahora)

3. **Imports necesarios (boilerplate permitido):**
   ```java
   import com.forestech.config.DatabaseConnection;
   import java.sql.Connection;
   import java.sql.Statement;
   import java.sql.ResultSet;
   import java.sql.SQLException;
   ```

4. **Crear método `getAllProducts()` - versión simple:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `void` (por ahora solo imprime, no retorna nada)
   - Parámetros: ninguno
   - NO declarar throws (captura SQLException internamente)
   
   **Estructura del método (TÚ completas la implementación):**
   
   a) Definir la query SQL como String:
      - Selecciona columnas: id, name, type, unit
      - De la tabla: `combustibles_products`
      - Sin WHERE (queremos todos los productos)
   
   b) Usar try-with-resources anidado para:
      - Connection (obtén con DatabaseConnection.getConnection())
      - Statement (crea con conn.createStatement())
      - ResultSet (ejecuta query con stmt.executeQuery(sql))
   
   **Sintaxis try-with-resources múltiple:**
   ```java
   try (Connection conn = ...;
        Statement stmt = ...;
        ResultSet rs = ...) {
       // código aquí
   } catch (SQLException e) {
       // manejo de error
   }
   ```
   
   c) Imprimir encabezado (antes del while):
      ```
      === PRODUCTOS EN BD ===
      ```
   
   d) Recorrer ResultSet con while:
      - `while (rs.next())` - avanza fila por fila
      - Extraer cada columna con `rs.getString("nombre_columna")`
      - Imprimir datos con formato legible
      
   **Pregunta guía:** ¿Por qué `rs.next()` retorna boolean? ¿Qué pasa cuando no hay más filas?
   
   e) Después del while, imprimir total de productos listados
      - **Desafío:** ¿Cómo llevas la cuenta de cuántas filas procesaste?
   
   f) En el bloque catch:
      - Imprimir mensaje de error descriptivo
      - Imprimir el mensaje de la excepción

5. **Verificar que la tabla existe en tu BD:**
   
   **Antes de probar el código, verifica en SQL Server Management Studio:**
   ```sql
   SELECT * FROM combustibles_products;
   ```
   
   - Si la tabla no existe, créala o ajusta el nombre en la query
   - Asegúrate de tener al menos 1-2 productos para probar

6. **Probar en Main.java:**
   
   **Agregar al método main:**
   ```java
   System.out.println("\n=== PRUEBA DE LECTURA DE PRODUCTOS ===");
   ProductService.getAllProducts();
   ```
   
   **Compilar y ejecutar:**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.forestech.Main"
   ```

7. **Depuración obligatoria:**
   - Coloca breakpoint en la línea `while (rs.next())`
   - Ejecuta en modo debug
   - Usa "Step Over" para ver cómo avanza el cursor
   - Inspecciona el contenido de `rs` en el panel de variables
   - Observa cómo cambian los valores en cada iteración

**✅ Resultado esperado:** 
- Ver lista de productos de la BD en consola con formato claro
- Al final, mensaje indicando cuántos productos se listaron (ej: "Total: 3 productos")
- No errores de conexión ni SQL
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java
  ├── services/
  │   └── ProductService.java (NUEVO)
  └── models/
      └── ...
  ```

**💡 Concepto clave - Ciclo de vida JDBC:**

```
1. Connection conn = DatabaseConnection.getConnection()  
   → Abre canal de comunicación con SQL Server

2. Statement stmt = conn.createStatement()  
   → Crea "mensajero" que llevará tu query

3. ResultSet rs = stmt.executeQuery("SELECT ...")  
   → Ejecuta query y obtiene cursor sobre los resultados

4. while (rs.next()) { ... }  
   → Recorre filas una por una

5. try-with-resources cierra automáticamente:
   rs.close() → stmt.close() → conn.close()
   → Libera recursos (CRÍTICO para no agotar conexiones)
```

**💡 Concepto clave - ResultSet como cursor:**

```
ResultSet es como un puntero que apunta a UNA fila a la vez:

Inicio: [antes de la primera fila]
rs.next() → [fila 1] → retorna true
rs.next() → [fila 2] → retorna true
rs.next() → [fila 3] → retorna true
rs.next() → [después de última fila] → retorna FALSE

Extraer datos de la fila actual:
String id = rs.getString("id");
String name = rs.getString("name");
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| NullPointerException en rs.getString() | Columna no existe en la query | Verifica nombre exacto en SELECT |
| No imprime nada (while no se ejecuta) | Tabla vacía | Inserta datos de prueba en SQL Server |
| "Invalid object name 'combustibles_products'" | Tabla no existe | Crea la tabla o usa el nombre correcto |
| Muchas conexiones abiertas | No cerrar recursos | Usa try-with-resources (cierra automático) |

**🔍 Depuración - Entender el flujo:**

Coloca breakpoints en:
1. Antes de `try` - ver que la query está bien formada
2. Dentro del `while` - ver cada fila que se procesa
3. Después del `while` - ver el total
4. En el `catch` - ver el error si falla

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✅ Checkpoint 3.4: Mapear ResultSet a Objetos Product

**Concepto clave:** Convertir filas de la BD (ResultSet) en objetos Java te permite trabajar con POO en vez de datos planos.

**📍 DÓNDE:** 
- **Crear archivo:** `Product.java` en `forestech-cli-java/src/main/java/com/forestech/models/`
- **Modificar:** `ProductService.java` (cambiar método para que retorne objetos)
- **Main.java:** Para PROBAR la lectura de objetos

**🎯 PARA QUÉ:** 
En Checkpoint 3.3 solo imprimiste datos. Eso está bien para aprender JDBC, pero tiene limitaciones:
- ❌ No puedes manipular los datos después de leerlos (no hay objetos)
- ❌ No puedes pasar los datos a otras funciones
- ❌ No aplicas POO (solo trabajas con Strings sueltos)
- ❌ No puedes agregar comportamiento (cálculos, validaciones)

Con objetos Product:
- ✅ **POO completa:** Cada producto es un objeto con atributos y métodos
- ✅ **Reutilización:** Puedes pasar la lista de productos a otras funciones
- ✅ **Mantenibilidad:** Si agregas atributos, solo cambias la clase Product
- ✅ **Comportamiento:** Puedes agregar métodos como `calculateStock()`, `isLowStock()`, etc.

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** Crearás `Movement.java` mejorado y lo mapearás desde `MovementService`
- **Fase 5:** Los managers trabajarán con listas de objetos (no con ResultSet directo)
- **Fase 6:** Mostrarás productos en el menú usando objetos
- **Fase 9:** Generarás reportes procesando listas de objetos

**🎓 Analogía:**
```
ResultSet (Checkpoint 3.3):  
"Tengo estos datos: Diesel, 1000, litros..."
→ Solo strings sueltos, como papeles desordenados

Product (Checkpoint 3.4):
Product p = new Product("Diesel", 1000, "litros");
→ Objeto estructurado, como una carpeta organizada
```

**Prompts sugeridos:**
```text
"¿Qué diferencia hay entre imprimir desde ResultSet vs crear objetos?"
"¿Por qué es mejor retornar List<Product> que imprimir directamente?"
"¿Cómo mapeo cada columna del ResultSet a un atributo del objeto?"
"¿Qué es un ArrayList y cuándo usarlo?"
```

**Tareas paso a paso:**

1. **Crear la clase Product en models/:**
   
   **Atributos que debe tener (TÚ defines como privados):**
   - id (String)
   - name (String)
   - type (String) - ej: "Diesel", "Gasolina 93"
   - unit (String) - ej: "litros", "galones"
   
   **Pregunta guía:** ¿Por qué Product va en `models/` y no en `services/`?

2. **Crear constructor para Product:**
   
   **Especificaciones:**
   - Constructor con 4 parámetros (uno por cada atributo)
   - Asignar cada parámetro al atributo correspondiente usando `this`
   
   **Pregunta guía:** ¿Es necesario un constructor sin parámetros? ¿Por qué sí o por qué no?

3. **Crear getters para todos los atributos:**
   
   **RECUERDA:** Ya sabes crear getters de Fase 2. Usa el mismo patrón.
   
   Debes crear:
   - `getId()`
   - `getName()`
   - `getType()`
   - `getUnit()`

4. **Crear método `toString()` para Product:**
   
   **Especificaciones:**
   - Sobrescribe el método heredado de Object
   - Usa la anotación `@Override`
   - Retorna String con formato legible
   
   **Formato sugerido:**
   ```
   Product{id='P001', name='Diesel', type='Combustible', unit='litros'}
   ```
   
   **Pregunta guía:** ¿Para qué sirve toString()? ¿Cuándo se llama automáticamente?

5. **Modificar ProductService.getAllProducts():**
   
   **Cambios a realizar:**
   
   a) Cambiar firma del método:
      - De: `public static void getAllProducts()`
      - A: `public static List<Product> getAllProducts()`
      
   b) Agregar import:
      ```java
      import java.util.List;
      import java.util.ArrayList;
      import com.forestech.models.Product;
      ```
   
   c) Crear lista al inicio del método:
      ```java
      List<Product> products = new ArrayList<>();
      ```
   
   d) Dentro del while (en vez de imprimir):
      - Extraer datos del ResultSet
      - Crear objeto Product con esos datos
      - Agregar el objeto a la lista
      
   **TÚ implementas esta lógica:**
   ```java
   while (rs.next()) {
       // Extraer columnas
       String id = rs.getString("id");
       // ... extraer las demás columnas
       
       // Crear objeto Product
       // Agregar a la lista
   }
   ```
   
   e) Después del while, retornar la lista
   
   f) Si hay error (catch), retornar lista vacía
   
   **Pregunta guía:** ¿Por qué retornar lista vacía en vez de null cuando hay error?

6. **Probar en Main.java:**
   
   **Modificar la prueba anterior:**
   ```java
   System.out.println("\n=== PRUEBA DE LECTURA DE PRODUCTOS (CON OBJETOS) ===");
   List<Product> products = ProductService.getAllProducts();
   
   // TÚ debes escribir:
   // - Verificar si la lista está vacía
   // - Si no está vacía, recorrer e imprimir cada producto
   // - Mostrar el total de productos al final
   ```
   
   **Pista para recorrer lista:**
   ```java
   for (Product p : products) {
       // usar p
   }
   ```
   
   **Pregunta guía:** ¿Qué diferencia hay entre este for y el for tradicional con índice?

7. **Depuración obligatoria:**
   - Coloca breakpoint en la línea donde creas el objeto Product
   - Ejecuta en debug
   - Inspecciona el objeto recién creado
   - Ve cómo se va llenando el ArrayList
   - Verifica que al final de getAllProducts() la lista tiene todos los productos

**✅ Resultado esperado:** 
- Clase Product creada en `models/` con atributos, constructor, getters y toString()
- ProductService.getAllProducts() retorna `List<Product>` en vez de void
- Main.java imprime los productos usando los objetos (no ResultSet directo)
- Ver en consola la lista de productos con formato del toString()
- Estructura actualizada:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java
  ├── models/
  │   ├── Movement.java
  │   ├── Vehicle.java
  │   ├── Supplier.java
  │   └── Product.java (NUEVO)
  └── services/
      └── ProductService.java (MODIFICADO)
  ```

**💡 Concepto clave - Separación de responsabilidades:**

```
ANTES (Checkpoint 3.3):
ProductService → Lee BD → Imprime directamente
❌ Service tiene 2 responsabilidades (leer Y mostrar)

AHORA (Checkpoint 3.4):
ProductService → Lee BD → Retorna List<Product>
Main.java → Recibe lista → Imprime
✅ Cada clase tiene UNA responsabilidad
```

**💡 Concepto clave - Mapeo ResultSet → Objeto:**

```
ResultSet (fila de BD):     Objeto Java:
┌─────────────────┐        ┌────────────────────┐
│ id     | "P001" │   →    │ Product            │
│ name   | "Diesel"│   →    │ - id: "P001"       │
│ type   | "Comb." │   →    │ - name: "Diesel"   │
│ unit   | "litros"│   →    │ - type: "Comb."    │
└─────────────────┘        │ - unit: "litros"   │
                           └────────────────────┘

Este proceso se llama "mapeo" o "binding"
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| Constructor con orden incorrecto | Parámetros en orden diferente al ResultSet | Verifica el orden: id, name, type, unit |
| NullPointerException al usar lista | Olvidaste inicializar ArrayList | `new ArrayList<>()` al inicio |
| Lista vacía pero hay datos en BD | No agregas productos a la lista en el while | Verifica `.add(product)` |
| "Cannot find symbol Product" | Product no está en models/ | Verifica el paquete y el import |

**🔍 Comparación lado a lado:**

**Checkpoint 3.3 (solo imprimir):**
```java
while (rs.next()) {
    String id = rs.getString("id");
    System.out.println("ID: " + id);
}
// Los datos desaparecen después del while
```

**Checkpoint 3.4 (crear objetos):**
```java
while (rs.next()) {
    String id = rs.getString("id");
    Product p = new Product(id, ...);
    products.add(p);
}
return products;
// Los datos persisten como objetos reutilizables
```

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 3.5: Queries Parametrizadas con PreparedStatement

**Concepto clave:** PreparedStatement es más seguro y eficiente que Statement porque previene SQL Injection y compila la query una sola vez.

**📍 DÓNDE:** 
- **Modificar:** `ProductService.java` (agregar nuevos métodos)
- **Main.java:** Para PROBAR los filtros

**🎯 PARA QUÉ:** 
En Checkpoint 3.3 y 3.4 usaste queries fijas (sin parámetros). Pero en el mundo real necesitas:
- ✅ **Filtrar por ID:** "Dame el producto con id='P001'"
- ✅ **Filtrar por tipo:** "Dame todos los productos de tipo 'Diesel'"
- ✅ **Búsquedas dinámicas:** El usuario decide qué buscar

**Problema con concatenación de strings (❌ NUNCA HACER ESTO):**
```java
String id = userInput;
String sql = "SELECT * FROM products WHERE id = '" + id + "'";
// ☠️ SI userInput = "' OR '1'='1" → SQL INJECTION
// ☠️ Ejecutaría: SELECT * FROM products WHERE id = '' OR '1'='1'
// ☠️ Retornaría TODOS los productos (vulnerabilidad de seguridad)
```

**Solución con PreparedStatement (✅ CORRECTO):**
```java
String sql = "SELECT * FROM products WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, userInput);
// ✅ El valor se escapa automáticamente (seguro contra SQL Injection)
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 4:** TODOS los INSERT/UPDATE/DELETE usarán PreparedStatement
- **Fase 5:** Queries con múltiples parámetros y JOINs
- **Fase 6:** Filtros dinámicos desde el menú del usuario
- **Fase 9:** Reportes con rangos de fechas (parámetros múltiples)

**🎓 Analogía:**
- **Statement:** Carta escrita a mano cada vez (lenta, insegura)
- **PreparedStatement:** Formulario con espacios en blanco (rápido, seguro)

**Prompts sugeridos:**
```text
"¿Qué es SQL Injection y cómo PreparedStatement lo previene?"
"¿Por qué los parámetros de PreparedStatement se marcan con '?'?"
"¿Qué diferencia hay entre setString(), setInt() y setDouble()?"
"¿Por qué PreparedStatement es más rápido que Statement?"
"Muéstrame un ejemplo de SQL Injection sin PreparedStatement."
```

**Tareas paso a paso:**

1. **Agregar método `getProductById()` en ProductService:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `Product` (un solo producto, no lista)
   - Parámetros: `String id`
   - Retorna null si no encuentra el producto
   
   **Implementación (TÚ la haces):**
   
   a) Definir query SQL con parámetro:
      ```java
      String sql = "SELECT id, name, type, unit FROM combustibles_products WHERE id = ?";
      ```
      
   b) Agregar import necesario:
      ```java
      import java.sql.PreparedStatement;
      ```
      
   c) Usar try-with-resources con PreparedStatement:
      ```java
      try (Connection conn = DatabaseConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {
          
          // Configurar parámetros aquí
          
      } catch (SQLException e) {
          // manejo de error
      }
      ```
   
   d) Configurar el parámetro (el "?"):
      - Usa `pstmt.setString(1, id)`
      - El "1" es la posición del primer "?" en la query
      
   **Pregunta guía:** ¿Por qué la numeración empieza en 1 y no en 0?
   
   e) Ejecutar query:
      - Usa `pstmt.executeQuery()` (igual que Statement)
      - Guarda el ResultSet
   
   f) Verificar si hay resultado:
      ```java
      if (rs.next()) {
          // Extraer columnas y crear objeto Product
          // Retornar el objeto
      } else {
          // No se encontró, retornar null
      }
      ```
   
   g) En el catch, retornar null

2. **Agregar método `getProductsByType()` en ProductService:**
   
   **Especificaciones:**
   - Modificador: `public static`
   - Tipo de retorno: `List<Product>`
   - Parámetros: `String type`
   - Retorna lista vacía si no encuentra productos
   
   **Implementación (TÚ la haces):**
   
   a) Query SQL:
      ```java
      String sql = "SELECT id, name, type, unit FROM combustibles_products WHERE type = ?";
      ```
   
   b) Igual que getAllProducts() pero con PreparedStatement:
      - Crear lista vacía al inicio
      - Configurar parámetro con `pstmt.setString(1, type)`
      - Recorrer ResultSet con while
      - Crear objetos Product y agregarlos a la lista
      - Retornar lista (vacía si no hay resultados)

3. **OPCIONAL - Método con múltiples parámetros `searchProducts()`:**
   
   **Desafío:** Crea un método que busque por nombre Y tipo:
   
   ```java
   public static List<Product> searchProducts(String namePattern, String type)
   ```
   
   **Query SQL:**
   ```java
   String sql = "SELECT id, name, type, unit FROM combustibles_products 
                 WHERE name LIKE ? AND type = ?";
   ```
   
   **Configurar parámetros:**
   ```java
   pstmt.setString(1, "%" + namePattern + "%");  // LIKE con wildcards
   pstmt.setString(2, type);
   ```
   
   **Pregunta guía:** ¿Qué hace el "%" en SQL LIKE?

4. **Probar en Main.java:**
   
   **Prueba 1 - Buscar por ID:**
   ```java
   System.out.println("\n=== BUSCAR PRODUCTO POR ID ===");
   Product product = ProductService.getProductById("P001");
   
   // TÚ debes escribir:
   // - Verificar si product es null
   // - Si no es null, imprimirlo
   // - Si es null, mostrar mensaje "Producto no encontrado"
   ```
   
   **Prueba 2 - Buscar por tipo:**
   ```java
   System.out.println("\n=== BUSCAR PRODUCTOS POR TIPO ===");
   List<Product> dieselProducts = ProductService.getProductsByType("Diesel");
   
   // TÚ debes escribir:
   // - Recorrer e imprimir la lista
   // - Mostrar cantidad de productos encontrados
   ```
   
   **Prueba 3 (opcional) - Búsqueda múltiple:**
   ```java
   List<Product> results = ProductService.searchProducts("Gas", "Combustible");
   ```

5. **Depuración obligatoria:**
   
   **Experimento de seguridad (entiende SQL Injection):**
   
   a) Coloca breakpoint justo después de configurar el parámetro
   b) En el debugger, evalúa `pstmt.toString()` para ver la query compilada
   c) Intenta pasar valores "maliciosos":
      ```java
      ProductService.getProductById("' OR '1'='1");
      ```
   d) Verifica que PreparedStatement lo maneja de forma segura
   e) **Pregunta:** ¿Qué pasaría con Statement + concatenación?

6. **Comparación Statement vs PreparedStatement:**
   
   **Ejercicio de comprensión (NO implementes esto, solo entiéndelo):**
   
   **❌ INSEGURO (Statement con concatenación):**
   ```java
   String id = userInput;  // ej: "' OR '1'='1"
   String sql = "SELECT * FROM products WHERE id = '" + id + "'";
   Statement stmt = conn.createStatement();
   ResultSet rs = stmt.executeQuery(sql);
   // Query ejecutada: SELECT * FROM products WHERE id = '' OR '1'='1'
   // ☠️ Retorna TODOS los productos (SQL Injection exitoso)
   ```
   
   **✅ SEGURO (PreparedStatement):**
   ```java
   String id = userInput;  // ej: "' OR '1'='1"
   String sql = "SELECT * FROM products WHERE id = ?";
   PreparedStatement pstmt = conn.prepareStatement(sql);
   pstmt.setString(1, id);
   ResultSet rs = pstmt.executeQuery();
   // Query ejecutada: SELECT * FROM products WHERE id = ''' OR ''1''=''1'
   // ✅ Busca literalmente un producto con ese ID (escapa las comillas)
   ```

**✅ Resultado esperado:** 
- Método `getProductById()` que retorna un solo producto o null
- Método `getProductsByType()` que retorna lista filtrada
- Main.java prueba ambos métodos exitosamente
- Entiendes la diferencia entre Statement y PreparedStatement
- Sabes por qué PreparedStatement es obligatorio para queries con parámetros
- Estructura completa de Fase 3:
  ```
  com.forestech/
  ├── Main.java
  ├── config/
  │   └── DatabaseConnection.java
  ├── models/
  │   ├── Movement.java
  │   ├── Vehicle.java
  │   ├── Supplier.java
  │   └── Product.java
  ├── managers/
  │   └── MovementManager.java
  └── services/
      └── ProductService.java (con getAllProducts, getProductById, getProductsByType)
  ```

**💡 Concepto clave - Índices de parámetros:**

```
SQL: "SELECT * FROM products WHERE type = ? AND price > ?"
                                          ↑              ↑
                                       índice 1      índice 2

Java:
pstmt.setString(1, "Diesel");    // Configura primer ?
pstmt.setDouble(2, 3.0);         // Configura segundo ?
```

**💡 Concepto clave - Tipos de setters:**

```java
pstmt.setString(1, "texto");     // Para VARCHAR, TEXT
pstmt.setInt(2, 100);            // Para INT
pstmt.setDouble(3, 3.45);        // Para DECIMAL, FLOAT, DOUBLE
pstmt.setBoolean(4, true);       // Para BIT, BOOLEAN
pstmt.setDate(5, sqlDate);       // Para DATE
pstmt.setTimestamp(6, timestamp);// Para DATETIME, TIMESTAMP

// IMPORTANTE: El tipo en Java debe coincidir con el tipo en SQL Server
```

**⚠️ CUIDADOS:**

| Problema común | Causa | Solución |
|----------------|-------|----------|
| "Invalid parameter index" | Índice incorrecto en setString() | Verifica que usas 1, 2, 3... según el orden de los "?" |
| "Parameter not set" | Olvidaste configurar un "?" | Cada "?" debe tener su pstmt.setXxx() correspondiente |
| NullPointerException | Retornaste null y no validaste | Siempre verifica `if (product != null)` antes de usar |
| Query no encuentra datos | Valor exacto no coincide | Verifica mayúsculas/minúsculas, espacios, etc. |

**🔍 Depuración avanzada - Ver query compilada:**

En el debugger, evalúa:
```java
pstmt.toString()
```
Verás algo como:
```
PreparedStatement: SELECT * FROM products WHERE id = 'P001'
```
Esto te muestra cómo se ve la query con los parámetros ya sustituidos.

**📊 Comparación completa:**

| Característica | Statement | PreparedStatement |
|----------------|-----------|-------------------|
| Seguridad | ❌ Vulnerable a SQL Injection | ✅ Seguro contra SQL Injection |
| Velocidad | ❌ Más lento (compila cada vez) | ✅ Más rápido (compila una vez) |
| Legibilidad | ❌ Concatenación confusa | ✅ Query limpia con "?" |
| Uso típico | Solo queries sin parámetros | Queries con parámetros |
| En producción | ❌ NO usar con input del usuario | ✅ SIEMPRE usar con input del usuario |

**⏱️ Tiempo estimado:** 3-4 horas

---

---

## ✏️ Refuerzos adicionales de la fase

**Conceptos importantes a dominar:**

1. **try-with-resources:**
   - ¿Por qué es mejor que try-finally?
   - ¿Qué interfaces deben implementar los recursos? (AutoCloseable)
   - ¿Qué pasa si hay excepción durante el cierre?
   - ¿Puedes anidar múltiples recursos en un solo try?

2. **SQLException:**
   - ¿Qué información contiene? (mensaje, SQLState, código de error)
   - ¿Cómo extraer el código de error específico? (`e.getErrorCode()`)
   - ¿Cuáles son los errores más comunes? (timeout, credenciales, tabla no existe)
   - ¿Cómo diferenciar error de conexión vs error de SQL?

3. **Arquitectura JDBC:**
   - DriverManager → Connection → Statement/PreparedStatement → ResultSet
   - ¿Para qué sirve cada componente?
   - ¿Cuándo se cierra cada uno?
   - ¿Por qué es importante cerrar en orden inverso al que se abrieron?

4. **ResultSet como cursor:**
   - ¿Por qué empieza ANTES de la primera fila?
   - ¿Qué retorna rs.next() cuando no hay más filas?
   - ¿Puedes moverte hacia atrás en un ResultSet normal?
   - ¿Qué pasa si intentas rs.getString() antes del primer rs.next()?

5. **Mapeo ResultSet → Objetos:**
   - ¿Por qué es mejor retornar objetos que imprimir directamente?
   - ¿Dónde va la lógica de mapeo: en Service o en un Mapper separado?
   - ¿Qué haces si una columna puede ser NULL?

6. **PreparedStatement vs Statement:**
   - ¿Cómo previene SQL Injection?
   - ¿Por qué es más rápido?
   - ¿Cuándo está bien usar Statement?
   - ¿Puedes reutilizar un PreparedStatement para múltiples ejecuciones?

**Ejercicios de refuerzo:**

1. **Refactorizar VehicleService:**
   - Crea `Vehicle.java` en models (siguiendo patrón de Product)
   - Crea `VehicleService.java` en services con:
     - `getAllVehicles()` - retorna List<Vehicle>
     - `getVehicleById(String id)` - retorna Vehicle
     - `getVehiclesByType(String type)` - retorna List<Vehicle>

2. **Refactorizar SupplierService:**
   - Crea `Supplier.java` en models
   - Crea `SupplierService.java` en services con métodos similares

3. **Queries más complejas:**
   - En ProductService, agrega `getProductsWithStock(double minStock)`
   - Practica con ORDER BY, LIMIT

4. **Manejo de errores:**
   - Crea método helper `handleSQLException(SQLException e)` que imprima:
     - Mensaje descriptivo
     - SQLState
     - Error code
   - Úsalo en todos los catch de tus services

5. **Mejora de DatabaseConnection:**
   - Agrega método `testConnectionWithTimeout(int seconds)`
   - Experimenta con connection pooling (avanzado)

**Desafíos adicionales:**

1. **Logging básico:**
   - En vez de `System.out.println()`, investiga java.util.logging
   - Agrega logs en DatabaseConnection (conexión abierta/cerrada)

2. **Configuración externa:**
   - Investiga `Properties` en Java
   - Mueve credenciales a un archivo `.properties`

3. **Transacciones básicas:**
   - Investiga `conn.setAutoCommit(false)`
   - Prueba commit/rollback manual

---

## 🧾 Checklist de salida de Fase 3

**Configuración y conexión:**
- [ ] Agregué correctamente la dependencia JDBC en pom.xml
- [ ] Puedo explicar qué hace el driver JDBC y por qué lo necesito
- [ ] Mi clase DatabaseConnection centraliza la configuración de BD
- [ ] Probé la conexión exitosamente con testConnection()
- [ ] Entiendo la diferencia entre URL, usuario y contraseña en la connection string

**Lectura de datos (SELECT):**
- [ ] Ejecuté mi primera query SELECT y vi resultados en consola
- [ ] Entiendo el flujo: Connection → Statement → executeQuery → ResultSet → rs.next()
- [ ] Sé usar try-with-resources para cerrar conexiones automáticamente
- [ ] Puedo explicar qué es ResultSet y cómo funciona como cursor

**Mapeo a objetos:**
- [ ] Creé la clase Product en models/ con atributos privados
- [ ] Implementé constructor, getters y toString() para Product
- [ ] Modifiqué ProductService para retornar List<Product> en vez de void
- [ ] Entiendo la diferencia entre imprimir desde ResultSet vs retornar objetos

**Queries parametrizadas:**
- [ ] Implementé getProductById() usando PreparedStatement
- [ ] Implementé getProductsByType() con filtro dinámico
- [ ] Entiendo qué es SQL Injection y cómo PreparedStatement lo previene
- [ ] Sé usar pstmt.setString(), pstmt.setInt(), pstmt.setDouble()
- [ ] Puedo explicar por qué PreparedStatement es obligatorio con input del usuario

**Manejo de errores:**
- [ ] Puedo explicar qué es SQLException y cómo manejarla
- [ ] Sé interpretar los mensajes de error comunes (conexión, credenciales, tabla no existe)
- [ ] Implemento try-catch correctamente en todos los métodos de servicio

**Arquitectura y organización:**
- [ ] Entiendo la separación: config/ vs models/ vs services/
- [ ] Mi código sigue el patrón: Service retorna datos, Main los muestra
- [ ] DatabaseConnection se usa en todos los Services (no repito credenciales)

**Documentación:**
- [ ] Documenté en JAVA_LEARNING_LOG.md los aprendizajes y problemas encontrados
- [ ] Actualicé JAVA_NEXT_STEPS.md con dudas y siguiente objetivo

**Estructura final del proyecto:**
```
forestech-cli-java/
├── pom.xml (con dependencia JDBC)
└── src/main/java/com/forestech/
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
    │   └── Product.java (NUEVO)
    ├── managers/
    │   └── MovementManager.java
    └── services/
        └── ProductService.java (NUEVO)
```

**⚠️ Problemas comunes y soluciones:**

| Problema | Posible causa | Solución |
|----------|--------------|----------|
| "No suitable driver found" | Driver no descargado | Ejecuta `mvn clean install` |
| "Cannot connect to server" | SQL Server no corriendo | Inicia el servicio SQL Server |
| "Login failed for user" | Credenciales incorrectas | Verifica usuario/contraseña en DatabaseConnection |
| "Database does not exist" | BD no creada | Crea BD FORESTECH en SQL Server Management Studio |
| NullPointerException en Service | Olvidaste inicializar lista/objeto | Revisa que creas `new ArrayList<>()` o retornas objeto |
| "Column not found" | Nombre incorrecto en ResultSet | Verifica que coincida con SELECT (mayúsculas/minúsculas) |
| SQL Injection (vulnerable) | Usaste concatenación + Statement | SIEMPRE usa PreparedStatement con parámetros |

---

## 🚀 Próximo paso: FASE 4 - Operaciones CRUD

En la siguiente fase aprenderás a:
- ✅ **Insertar datos (INSERT)** desde objetos Java a la BD usando PreparedStatement
- ✅ **Actualizar registros (UPDATE)** con validaciones y manejo de errores
- ✅ **Eliminar datos (DELETE)** con precauciones (soft delete vs hard delete)
- ✅ **Transacciones** para operaciones que deben ser atómicas (todo o nada)
- ✅ **MovementService completo** con todas las operaciones CRUD

**¿Por qué CRUD es importante?** 

Ahora solo LEES datos (operación R de CRUD). Con las operaciones CREATE, UPDATE y DELETE completas:
- Podrás **crear movimientos** desde tu aplicación (no solo desde SQL Server)
- Podrás **editar movimientos** si hay errores en los datos
- Podrás **eliminar movimientos** incorrectos o duplicados
- Forestech será una aplicación **completamente funcional** (no solo de consulta)

**Diferencia clave vs Fase 3:**
```
FASE 3 (solo lectura):
Usuario → Main → ProductService.getAllProducts() → BD (SELECT)
                                                 ↓
                                          Retorna datos

FASE 4 (escritura completa):
Usuario → Main → MovementService.insertMovement(movement) → BD (INSERT)
                                                          ↓
                                                   Guarda datos

Usuario → Main → MovementService.updateMovement(movement) → BD (UPDATE)
                                                          ↓
                                                   Modifica datos

Usuario → Main → MovementService.deleteMovement(id) → BD (DELETE)
                                                     ↓
                                                  Elimina datos
```

**Estructura que tendrás al terminar Fase 4:**
```
com.forestech/
├── config/
│   └── DatabaseConnection.java
├── models/
│   ├── Movement.java (mejorado con validaciones)
│   ├── Vehicle.java
│   ├── Supplier.java
│   └── Product.java
└── services/
    ├── ProductService.java (SELECT)
    ├── MovementService.java (CRUD completo - NUEVO)
    ├── VehicleService.java (CRUD completo - NUEVO)
    └── SupplierService.java (CRUD completo - NUEVO)
```

**⏱️ Tiempo estimado Fase 4:** 8-12 horas distribuidas en 1-2 semanas

**⏱️ Tiempo total Fase 3:** 11-14 horas distribuidas en 1 semana

---

## 📚 Recursos adicionales

**Documentación oficial:**
- [JDBC Tutorial de Oracle](https://docs.oracle.com/javase/tutorial/jdbc/)
- [SQL Server JDBC Driver Documentation](https://learn.microsoft.com/en-us/sql/connect/jdbc/)

**Videos recomendados:**
- Búsqueda: "JDBC Java Tutorial" (conceptos básicos)
- Búsqueda: "PreparedStatement vs Statement" (seguridad)
- Búsqueda: "SQL Injection examples" (entender vulnerabilidades)

**Práctica adicional:**
- [SQLZoo](https://sqlzoo.net/) - Practica queries SQL interactivas
- [HackerRank SQL](https://www.hackerrank.com/domains/sql) - Ejercicios SQL progresivos

**Conceptos relacionados para investigar:**
- Connection pooling (HikariCP, C3P0)
- ORM (Object-Relational Mapping) - Hibernate, JPA
- DAO Pattern (Data Access Object)
- Repository Pattern

**¡Recuerda documentar todo en JAVA_LEARNING_LOG.md!** 📝
