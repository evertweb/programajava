# 📋 PROMPT PARA CORRECCIÓN DE FASE 3 - SQL Y CONEXIÓN A BASE DE DATOS

## 🎯 Objetivo de la tarea

Necesito que revises y corrijas el archivo `roadmaps/FASE_03_SQL.md` para aplicar a todo el código el formato **diagramas de instrucciones tipo árbol** que guíen al usuario a escribir su propio código.

Este es un proyecto de APRENDIZAJE, no de desarrollo rápido. El usuario está aprendiendo Java desde CERO y debe escribir el código él mismo, no copiarlo. En esta fase, aprenderá a conectarse a SQL Server y realizar operaciones CRUD básicas.

---

## ✅ Formato correcto aplicado en Fases 1 y 2 (REFERENCIA)

### ❌ FORMATO INCORRECTO (lo que hay que eliminar si se encuentra):

```java
// Aquí está la clase DatabaseConnection completa:
public class DatabaseConnection {
    private static Connection connection;
    private static final String SERVER = "localhost";
    private static final String DATABASE = "forestech_db";
    
    public static Connection getConnection() throws SQLException {
        String connectionString = "jdbc:sqlserver://" + SERVER + ":1433;databaseName=" + DATABASE;
        connection = DriverManager.getConnection(connectionString, "sa", "password");
        return connection;
    }
    
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    // ... 40 líneas más de código listo para copiar
}
```

### ✅ FORMATO CORRECTO (lo que debe quedar):

```
DatabaseConnection.java
│
├── Constantes de configuración
│   ├── String SERVER → URL del servidor SQL Server
│   ├── String DATABASE → Nombre de la base de datos
│   ├── String USER → Usuario de SQL Server
│   └── String PASSWORD → Contraseña (usar AppConfig para datos sensibles)
│
├── Método estático: getConnection()
│   ├── Tipo retorno: Connection
│   ├── Pasos a implementar:
│   │   1. Construir connectionString con jdbc:sqlserver://
│   │   2. Usar DriverManager.getConnection()
│   │   3. Retornar la conexión abierta
│   │
│   ├── Manejo de excepciones:
│   │   • SQLException si la conexión falla
│   │
│   └── 💡 PISTA: Usar try-catch o throws en la firma del método
│
└── Método estático: closeConnection()
    ├── Tipo retorno: void
    ├── Parámetro: Connection conn
    ├── Validación:
    │   • Verificar que conn != null
    │   • Verificar que conn.isClosed() sea false
    │
    └── Lógica: Llamar a conn.close()
```

---

## 📐 Elementos del formato tipo "Diagrama de Árbol"

Cada checkpoint debe seguir esta estructura:

### 1. **Encabezado del Checkpoint**
```markdown
## ✅ Checkpoint 3.X: Nombre del Concepto

**Concepto clave:** Explicación en 1-2 frases de qué se aprende

**📍 DÓNDE:** 
- Qué archivos crear
- Qué archivos modificar

**🎯 PARA QUÉ:** 
- Explicación del propósito real del código
- Cómo se usa en Forestech

**🔗 CONEXIÓN FUTURA:** 
- Cómo evolucionará este código en fases posteriores

**Prompts sugeridos:**
```text
"Pregunta 1 que el usuario puede hacer"
"Pregunta 2 que el usuario puede hacer"
```
```

### 2. **Diagrama de Tareas (Formato Árbol)**

Usa este formato visual para cada clase/método:

```
NombreClase.java
│
├── Elemento 1: descripción
│   ├── Sub-elemento 1.1
│   ├── Sub-elemento 1.2
│   └── Lógica paso a paso:
│       1. Primer paso
│       2. Segundo paso
│       3. Tercer paso
│
├── Elemento 2: descripción
│   ├── Parámetros:
│   │   • tipo1 nombre1 (descripción)
│   │   • tipo2 nombre2 (descripción)
│   │
│   ├── Condiciones a validar:
│   │   • condición 1
│   │   • condición 2
│   │
│   └── Tipo de retorno: tipo
│
└── Elemento 3: descripción
    └── 💡 PISTA: Explicación adicional útil
```

### 3. **Sección de Pruebas**

En lugar de dar el código de pruebas completo:

```markdown
**🧪 PRUEBAS EN Main.java:**

```
Casos de prueba a implementar:

Prueba 1: Conectar a la BD
├── Llamar a: DatabaseConnection.getConnection()
├── Capturar: Connection conn = ...
├── Validar: conn != null
└── Resultado esperado: Conexión exitosa sin excepciones

Prueba 2: Ejecutar consulta simple
├── Crear: String query = "SELECT * FROM movements"
├── Ejecutar: Statement stmt = conn.createStatement()
├── Llamar a: stmt.executeQuery(query)
└── Resultado esperado: ResultSet no vacío
```
```

### 4. **Pistas de Implementación**

En lugar de código completo, dar pistas:

```markdown
**💡 PISTAS DE IMPLEMENTACIÓN:**

1. **Para crear connectionString en SQL Server:**
   - Patrón: "jdbc:sqlserver://SERVER:PUERTO;databaseName=DATABASE"
   - Ejemplo: "jdbc:sqlserver://localhost:1433;databaseName=forestech"
   - Dónde obtenerlo: AppConfig.java

2. **Para usar try-with-resources (cierre automático):**
   - Patrón: try (Connection conn = DriverManager.getConnection(...)) { }
   - Ventaja: Cierra automáticamente al salir del try

3. **Para iterar un ResultSet:**
   - while (rs.next()) { rs.getString("columnName"); }
   - Método next() avanza a la siguiente fila
```

### 5. **Comparaciones Visuales**

Cuando expliques diferencias entre conceptos:

```markdown
**📊 COMPARACIÓN: Statement vs PreparedStatement**

```
┌─────────────────────────────────────────────────────┐
│ Statement (Básico)          │ PreparedStatement (Seguro) │
├─────────────────────────────────────────────────────┤
│ stmt.executeQuery(query)    │ pstmt = conn.prepareStatement(query) │
│ ✅ Simple                   │ ✅ Previene SQL Injection  │
│ ❌ Lento (se compila c/vez) │ ❌ Más líneas de código   │
│ ❌ SQL Injection posible    │ ✅ Query compilada 1 vez  │
└─────────────────────────────────────────────────────┘
```
```

### 6. **NO Incluir Bloque Completo de Código**

En su lugar, usar pseudocódigo o pistas:

```markdown
**❌ INCORRECTO:**
```java
public List<Movement> getAllMovements() throws SQLException {
    List<Movement> movements = new ArrayList<>();
    String query = "SELECT * FROM movements";
    
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        while (rs.next()) {
            String id = rs.getString("id");
            String type = rs.getString("type");
            // ... 20 líneas más
        }
    }
    return movements;
}
```

**✅ CORRECTO:**
```
Método: getAllMovements()
├── Tipo retorno: List<Movement>
├── Excepciones: throws SQLException
├── Estructura:
│   1. Crear ArrayList<Movement> vacío
│   2. Abrir conexión con try-with-resources
│   3. Crear Statement
│   4. Ejecutar SELECT *
│   5. Iterar ResultSet (while(rs.next()))
│   6. Por cada fila: crear objeto Movement y agregar a lista
│   7. Retornar lista
│
└── 💡 PISTA: Usa getters del ResultSet como rs.getString("id")
```
```

---

## 📋 CHECKPOINTS A REVISAR EN FASE 3

**Checkpoint 3.1:** Introducción a SQL y Base de Datos
- Conceptos: Tablas, columnas, tipos de datos SQL, relaciones
- Evitar: Código SQL completo, scripts listos para ejecutar
- Usar: Diagramas de entidad-relación, pistas de sintaxis

**Checkpoint 3.2:** Driver JDBC y Configuración
- Conceptos: DriverManager, Connection, SQLException
- Evitar: Clase DatabaseConnection completa
- Usar: Diagrama de pasos, pistas de configuración

**Checkpoint 3.3:** Consultas SELECT (Lectura)
- Conceptos: Statement, ResultSet, iteración
- Evitar: Métodos completos con toda la lógica
- Usar: Pseudocódigo paso a paso, diagramas de flujo

**Checkpoint 3.4:** Inserción de Datos (INSERT)
- Conceptos: PreparedStatement, parámetros, executeUpdate()
- Evitar: Método INSERT completo
- Usar: Estructura de pasos, pistas sobre parámetros

**Checkpoint 3.5:** Actualización (UPDATE) y Eliminación (DELETE)
- Conceptos: Cláusula WHERE, sentencias condicionales
- Evitar: Métodos completos de UPDATE/DELETE
- Usar: Especificaciones de qué modificar, pistas de sintaxis

**Checkpoint 3.6:** Manejo de Errores y Transacciones
- Conceptos: try-catch, SQLException, setAutoCommit()
- Evitar: Bloques completos de manejo de excepciones
- Usar: Estructura de errores esperados, pistas de recuperación

---

## ✏️ Proceso de Corrección

1. **Abre el archivo:** `roadmaps/FASE_03_SQL.md`

2. **Por cada checkpoint:**
   - Identifica bloques de código > 10 líneas
   - Reemplaza con diagrama de árbol
   - Convierte ejemplos en pistas
   - Mantén explicaciones conceptuales
   - Agrega analogías Forestech

3. **Valida criterios:**
   - [ ] NO hay código Java > 10 líneas consecutivas
   - [ ] Todos los checkpoints tienen diagrama de árbol
   - [ ] Las instrucciones dicen QUÉ, no CÓMO
   - [ ] Hay pistas pero no soluciones completas
   - [ ] El usuario DEBE escribir el código
   - [ ] Contexto Forestech en todos los ejemplos

4. **Crea un respaldo** antes de modificar:
   ```bash
   cp roadmaps/FASE_03_SQL.md roadmaps/FASE_03_SQL.md.backup
   ```

5. **Al terminar**, genera un resumen de cambios indicando:
   - Cuántos checkpoints corregiste
   - Qué secciones tenían más código completo
   - Ejemplos de antes/después

---

## ✅ Criterios de éxito

La corrección será exitosa cuando:

- ✅ NO haya bloques de código Java de más de 10 líneas
- ✅ Todos los checkpoints usen formato de diagrama de árbol
- ✅ Las instrucciones digan QUÉ hacer, no CÓMO escribirlo
- ✅ Haya pistas pero no soluciones completas
- ✅ El usuario tenga que PENSAR y ESCRIBIR el código, no copiarlo
- ✅ Cada concepto SQL esté explicado visualmente
- ✅ Los ejemplos usen el contexto de Forestech (BD de combustibles)

---

## 🔄 DIRECTIVA DE CASCADA - PRÓXIMAS FASES

**⚠️ IMPORTANTE:** Al terminar la corrección de esta FASE 3, debes:

1. **Editar este archivo** para crear un nuevo prompt similar
2. **Cambiar referencias:**
   - `FASE_03_SQL.md` → `FASE_04_CRUD.md`
   - Checkpoint 3.X → Checkpoint 4.X
   - Conceptos SQL → Conceptos CRUD
   - Ejemplos SELECT/INSERT → Ejemplos de operaciones completas

3. **Guardar como:** `PROMPT_CORRECCION_FASE_4.md`

4. **Repetir el ciclo para cada fase:**
   - FASE 4 (CRUD Completo) → `PROMPT_CORRECCION_FASE_4.md`
   - FASE 5 (Lógica de Negocio) → `PROMPT_CORRECCION_FASE_5.md`
   - FASE 6 (Interfaz de Usuario) → `PROMPT_CORRECCION_FASE_6.md`
   - FASE 7 (Manejo de Errores) → `PROMPT_CORRECCION_FASE_7.md`
   - FASE 8 (Características Avanzadas) → `PROMPT_CORRECCION_FASE_8.md`

5. **Al terminar TODAS las fases:**
   - Tendrás un archivo de prompt para cada fase
   - Todos los roadmaps estarán en formato didáctico de árbol
   - El usuario podrá aprender escribiendo código, no copiándolo

---

## 📝 PLANTILLA PARA SIGUIENTE PROMPT

Cuando termines FASE 3 y necesites crear el prompt para FASE 4, usa esta estructura:

```markdown
# 📋 PROMPT PARA CORRECCIÓN DE FASE X - [NOMBRE DE LA FASE]

## 🎯 Objetivo de la tarea

Necesito que revises y corrijas el archivo `roadmaps/FASE_0X_[NOMBRE].md` para aplicar a todo el código el formato **diagramas de instrucciones tipo árbol** que guíen al usuario a escribir su propio código.

[... incluir el resto de secciones igual que este documento ...]

## 🔄 DIRECTIVA DE CASCADA - PRÓXIMAS FASES

**⚠️ IMPORTANTE:** Al terminar la corrección de esta FASE X, debes:

[... mantener las instrucciones de cascada actualizando números de fase ...]
```

---

**Recuerda:** Este es un proyecto de APRENDIZAJE. El objetivo NO es que el código funcione rápido, sino que el usuario ENTIENDA lo que escribe.

🎓 **"No des el pescado, enseña a pescar"**

---

## 📊 Estado de Corrección de Fases

| Fase | Archivo | Estado | Correcciones |
|------|---------|--------|--------------|
| **1** | FASE_01_FUNDAMENTOS.md | ✅ Completada | Formato árbol aplicado |
| **2** | FASE_02_POO.md | ✅ Completada | Formato árbol aplicado |
| **3** | FASE_03_SQL.md | ⏳ En progreso | Pendiente - Usar este prompt |
| **4** | FASE_04_CRUD.md | ⬜ No iniciada | Se usará PROMPT_CORRECCION_FASE_4.md |
| **5** | FASE_05_LOGICA_NEGOCIO.md | ⬜ No iniciada | Se usará PROMPT_CORRECCION_FASE_5.md |
| **6** | FASE_06_UI.md | ⬜ No iniciada | Se usará PROMPT_CORRECCION_FASE_6.md |
| **7** | FASE_07_ERRORES.md | ⬜ No iniciada | Se usará PROMPT_CORRECCION_FASE_7.md |
| **8** | FASE_08_AVANZADOS.md | ⬜ No iniciada | Se usará PROMPT_CORRECCION_FASE_8.md |

