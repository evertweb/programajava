# 🔍 FASE 4.8: CONSULTAS SQL AVANZADAS

> **Duración estimada:** 3-4 horas de estudio activo
> **Dificultad:** ⭐⭐⭐ (Intermedia-Avanzada)
> **Prerequisito obligatorio:** FASE 4.2 (SELECT básico) completada

---

## 🎯 Objetivos de Aprendizaje

Al finalizar esta fase, serás capaz de:

- [ ] Usar `LIKE` para búsquedas parciales con wildcards (`%`, `_`)
- [ ] Filtrar múltiples valores con `IN` de forma eficiente
- [ ] Consultar rangos de datos con `BETWEEN` (números y fechas)
- [ ] Combinar múltiples filtros con operadores lógicos `AND` / `OR`
- [ ] Implementar paginación básica con `OFFSET` y `FETCH NEXT`
- [ ] Construir métodos de búsqueda con filtros opcionales (parámetros null)
- [ ] Optimizar consultas dinámicas con `StringBuilder`
- [ ] Entender el impacto de índices en consultas complejas
- [ ] Crear sistemas de búsqueda reutilizables y escalables

---

## 📚 Requisitos Previos

Antes de comenzar, asegúrate de haber completado:

- ✅ **FASE_04.2_SELECT_READ.md:** `executeQuery()`, `ResultSet`, mapeo de filas
- ✅ **FASE_04.1_INSERT_CREATE.md:** `PreparedStatement` y placeholders `?`
- ✅ **Conceptos SQL:** `WHERE`, `ORDER BY`, operadores de comparación (`=`, `>`, `<`, `!=`)
- ✅ **Java básico:** `StringBuilder`, manipulación de Strings, lógica condicional

---

## 🧠 Pre-Test: Active Recall Inicial

Antes de leer la teoría, responde estas preguntas para activar tu conocimiento previo:

### Pregunta 1: Búsqueda Parcial
```
¿Cómo buscarías todos los vehículos cuya placa CONTENGA la letra "A"
en cualquier posición (inicio, medio o final)?

Placa: "ABC-123" ✅ (contiene A)
Placa: "XYZ-789" ❌ (no contiene A)
Placa: "DEF-A45" ✅ (contiene A)
```

💭 **[ESPACIO PARA PENSAR - NO LEER LA RESPUESTA AÚN]**

<details>
<summary>✅ Ver respuesta correcta</summary>

```sql
SELECT * FROM combustibles_vehicles
WHERE plate LIKE '%A%';
```

El símbolo `%` representa "cualquier cantidad de caracteres". Por eso `'%A%'` significa:
- `%` → 0 o más caracteres antes de la A
- `A` → la letra A
- `%` → 0 o más caracteres después de la A

</details>

---

### Pregunta 2: Múltiples Valores
```
Tienes que buscar movimientos que sean de tipo "ENTRADA" o "SALIDA",
pero NO de tipo "TRANSFERENCIA".

¿Cómo lo harías con una sola consulta SQL sin usar múltiples WHERE?
```

💭 **[ESPACIO PARA PENSAR]**

<details>
<summary>✅ Ver respuesta correcta</summary>

```sql
SELECT * FROM combustibles_movements
WHERE type IN ('ENTRADA', 'SALIDA');
```

`IN` permite filtrar múltiples valores en una sola expresión, equivalente a:
```sql
WHERE type = 'ENTRADA' OR type = 'SALIDA'
```

Pero mucho más legible y escalable cuando tienes 5, 10 o 20 valores posibles.

</details>

---

### Pregunta 3: Rangos de Datos
```
Necesitas buscar movimientos con cantidad entre 100 y 500 litros (inclusive).
¿Cómo escribirías la condición WHERE de forma concisa?
```

💭 **[ESPACIO PARA PENSAR]**

<details>
<summary>✅ Ver respuesta correcta</summary>

```sql
SELECT * FROM combustibles_movements
WHERE quantity BETWEEN 100 AND 500;
```

`BETWEEN` es equivalente a:
```sql
WHERE quantity >= 100 AND quantity <= 500
```

**IMPORTANTE:** Los límites son inclusivos (100 y 500 están incluidos).

</details>

---

## 📖 Teoría: Las 5 Cláusulas Avanzadas de SQL

### 🔹 1. LIKE - Búsquedas con Patrones

#### ¿Qué es LIKE?

`LIKE` es un operador SQL para **buscar patrones de texto** usando caracteres especiales llamados **wildcards**. Piensa en LIKE como el "buscador de Google" de tu base de datos.

#### Wildcards en SQL Server

| Wildcard | Significado | Ejemplo | Coincide con |
|----------|------------|---------|--------------|
| `%` | 0 o más caracteres | `'%ABC%'` | "ABC", "xABCy", "ABCz", "qABC" |
| `_` | Exactamente 1 carácter | `'A_C'` | "ABC", "A1C", "AxC" (NO "AC" ni "ABBC") |
| `[abc]` | Un carácter de la lista | `'[ABC]12'` | "A12", "B12", "C12" |
| `[a-z]` | Un carácter en rango | `'[0-9]ABC'` | "0ABC", "5ABC", "9ABC" |
| `[^abc]` | Un carácter NO en lista | `'[^A]BC'` | "XBC", "1BC" (NO "ABC") |

#### Ejemplos Prácticos con Forestech

```sql
-- 1. Placas que EMPIEZAN con "ABC"
SELECT * FROM combustibles_vehicles
WHERE plate LIKE 'ABC%';  -- ABC-123 ✅, ABC-999 ✅, XYZ-123 ❌

-- 2. Placas que TERMINAN con "123"
SELECT * FROM combustibles_vehicles
WHERE plate LIKE '%123';  -- ABC-123 ✅, XYZ-123 ✅, ABC-999 ❌

-- 3. Placas que CONTIENEN "BC" en cualquier parte
SELECT * FROM combustibles_vehicles
WHERE plate LIKE '%BC%';  -- ABC-123 ✅, XBCY-1 ✅, AXY-123 ❌

-- 4. Placas de exactamente 7 caracteres que empiezan con A
SELECT * FROM combustibles_vehicles
WHERE plate LIKE 'A______';  -- A123456 ✅, AB12345 ❌ (8 chars)

-- 5. Proveedores cuyo nombre empieza con vocal
SELECT * FROM combustibles_suppliers
WHERE name LIKE '[AEIOU]%';  -- "Ecopetrol" ✅, "Shell" ❌
```

#### ⚠️ Errores Comunes con LIKE

```sql
-- ❌ ERROR 1: Olvidar los wildcards
WHERE plate LIKE 'ABC';  -- Solo encuentra "ABC" exacto, no "ABC-123"

-- ✅ CORRECTO: Usar %
WHERE plate LIKE '%ABC%';

-- ❌ ERROR 2: Confundir * con %
WHERE plate LIKE '*ABC*';  -- ¡* NO es válido en SQL!

-- ✅ CORRECTO: Usar %
WHERE plate LIKE '%ABC%';

-- ❌ ERROR 3: Olvidar que LIKE es case-sensitive (depende del collation)
WHERE name LIKE 'abc';  -- Podría NO encontrar "ABC" o "Abc"

-- ✅ CORRECTO: Usar LOWER() o UPPER()
WHERE LOWER(name) LIKE '%abc%';
```

---

### 🔹 2. IN - Múltiples Valores en una Condición

#### ¿Qué es IN?

`IN` permite verificar si un valor **coincide con cualquiera de los valores de una lista**. Es como preguntar: "¿Este valor está en mi lista de opciones válidas?"

#### Sintaxis Básica

```sql
SELECT * FROM tabla
WHERE columna IN (valor1, valor2, valor3, ...);
```

#### Comparación: OR vs IN

```sql
-- ❌ DIFÍCIL DE LEER: Múltiples OR
SELECT * FROM combustibles_movements
WHERE type = 'ENTRADA'
   OR type = 'SALIDA'
   OR type = 'TRANSFERENCIA'
   OR type = 'AJUSTE';

-- ✅ LEGIBLE: Usar IN
SELECT * FROM combustibles_movements
WHERE type IN ('ENTRADA', 'SALIDA', 'TRANSFERENCIA', 'AJUSTE');
```

#### Ejemplos Prácticos con Forestech

```sql
-- 1. Buscar movimientos de tipos específicos
SELECT * FROM combustibles_movements
WHERE type IN ('ENTRADA', 'SALIDA');

-- 2. Buscar vehículos de ciertas placas
SELECT * FROM combustibles_vehicles
WHERE plate IN ('ABC-123', 'XYZ-789', 'DEF-456');

-- 3. Buscar productos de combustibles premium
SELECT * FROM combustibles_products
WHERE name IN ('Diesel Extra', 'Gasolina Premium', 'Gasolina Corriente');

-- 4. IN con números
SELECT * FROM combustibles_movements
WHERE supplierId IN (1, 3, 5, 7);  -- Proveedores específicos

-- 5. IN negado con NOT IN
SELECT * FROM combustibles_vehicles
WHERE plate NOT IN ('ABC-123', 'XYZ-789');  -- Todos EXCEPTO estos
```

#### ⚠️ Errores Comunes con IN

```sql
-- ❌ ERROR 1: Olvidar las comillas con strings
WHERE type IN (ENTRADA, SALIDA);  -- SQL busca columnas con esos nombres

-- ✅ CORRECTO: Usar comillas
WHERE type IN ('ENTRADA', 'SALIDA');

-- ❌ ERROR 2: Mezclar tipos de datos
WHERE id IN (1, '2', 3);  -- Mezclar int y string puede causar problemas

-- ✅ CORRECTO: Usar el mismo tipo
WHERE id IN (1, 2, 3);

-- ❌ ERROR 3: Usar IN con NULL
WHERE supplierId IN (1, 2, NULL);  -- NULL no funciona con IN

-- ✅ CORRECTO: Combinar con OR IS NULL
WHERE supplierId IN (1, 2) OR supplierId IS NULL;
```

---

### 🔹 3. BETWEEN - Rangos Inclusivos

#### ¿Qué es BETWEEN?

`BETWEEN` verifica si un valor está **dentro de un rango inclusivo** (incluyendo los límites). Funciona con números, fechas y strings (orden alfabético).

#### Sintaxis Básica

```sql
SELECT * FROM tabla
WHERE columna BETWEEN valor_min AND valor_max;
```

**IMPORTANTE:** Los límites son inclusivos (se incluyen `valor_min` y `valor_max`).

#### Ejemplos Prácticos con Forestech

```sql
-- 1. Movimientos con cantidad entre 100 y 500 litros
SELECT * FROM combustibles_movements
WHERE quantity BETWEEN 100 AND 500;  -- 100 y 500 incluidos

-- 2. Movimientos de un rango de fechas
SELECT * FROM combustibles_movements
WHERE movementDate BETWEEN '2025-01-01' AND '2025-01-31';  -- Enero completo

-- 3. Vehículos con capacidad mediana (5,000 a 10,000 litros)
SELECT * FROM combustibles_vehicles
WHERE tankCapacity BETWEEN 5000 AND 10000;

-- 4. Productos con precio en rango específico
SELECT * FROM combustibles_products
WHERE pricePerLiter BETWEEN 2.5 AND 3.5;

-- 5. BETWEEN negado con NOT
SELECT * FROM combustibles_movements
WHERE quantity NOT BETWEEN 100 AND 500;  -- Menos de 100 o más de 500
```

#### Comparación: >= AND <= vs BETWEEN

```sql
-- Ambas consultas son equivalentes:

-- Versión tradicional
SELECT * FROM combustibles_movements
WHERE quantity >= 100 AND quantity <= 500;

-- Versión con BETWEEN (más legible)
SELECT * FROM combustibles_movements
WHERE quantity BETWEEN 100 AND 500;
```

#### ⚠️ Errores Comunes con BETWEEN

```sql
-- ❌ ERROR 1: Invertir el orden (mínimo > máximo)
WHERE quantity BETWEEN 500 AND 100;  -- ¡NO devuelve resultados!

-- ✅ CORRECTO: Mínimo primero
WHERE quantity BETWEEN 100 AND 500;

-- ❌ ERROR 2: Confundir inclusivo con exclusivo
WHERE quantity BETWEEN 100 AND 500;  -- 100 y 500 SÍ están incluidos

-- Si quieres excluir los límites, usa:
WHERE quantity > 100 AND quantity < 500;  -- 100 y 500 NO incluidos

-- ❌ ERROR 3: Fechas con formato incorrecto
WHERE movementDate BETWEEN '01/01/2025' AND '31/01/2025';  -- Formato incorrecto

-- ✅ CORRECTO: Formato ISO 8601
WHERE movementDate BETWEEN '2025-01-01' AND '2025-01-31';
```

#### 📊 Diagrama ASCII: BETWEEN Inclusivo

```
    ┌──────────────────────────────────────┐
    │     BETWEEN 100 AND 500              │
    └──────────────────────────────────────┘
           ↓                         ↓
        100  ✅                    500  ✅
         │                          │
    ┌────┴──────────────────────────┴────┐
    │   Todos los valores dentro         │
    │   100, 101, 102, ..., 499, 500     │
    └────────────────────────────────────┘

    ❌ 99   ✅ 100   ✅ 250   ✅ 500   ❌ 501
```

---

### 🔹 4. AND / OR - Combinación de Filtros

#### Operadores Lógicos

| Operador | Significado | Ejemplo | Resultado |
|----------|-------------|---------|-----------|
| `AND` | Ambas condiciones deben cumplirse | `A AND B` | True solo si A=True y B=True |
| `OR` | Al menos una condición debe cumplirse | `A OR B` | True si A=True o B=True o ambos |
| `NOT` | Niega la condición | `NOT A` | True si A=False |

#### Precedencia de Operadores (Orden de Evaluación)

```
1. Paréntesis ()
2. NOT
3. AND
4. OR
```

#### Ejemplos Prácticos con Forestech

```sql
-- 1. AND simple: Movimientos de ENTRADA con cantidad > 1000
SELECT * FROM combustibles_movements
WHERE type = 'ENTRADA' AND quantity > 1000;

-- 2. OR simple: Movimientos de ENTRADA o SALIDA
SELECT * FROM combustibles_movements
WHERE type = 'ENTRADA' OR type = 'SALIDA';

-- 3. Combinación AND + OR (SIN paréntesis - ¡cuidado!)
SELECT * FROM combustibles_movements
WHERE type = 'ENTRADA' AND quantity > 1000 OR productId = 3;
-- Esto se evalúa como: (type='ENTRADA' AND quantity>1000) OR (productId=3)

-- 4. Combinación AND + OR (CON paréntesis - recomendado)
SELECT * FROM combustibles_movements
WHERE type = 'ENTRADA' AND (quantity > 1000 OR productId = 3);
-- Busca ENTRADAS que tengan cantidad>1000 O que sean del producto 3

-- 5. Filtro complejo: Movimientos grandes de proveedores específicos
SELECT * FROM combustibles_movements
WHERE (type = 'ENTRADA' OR type = 'SALIDA')
  AND quantity BETWEEN 500 AND 2000
  AND supplierId IN (1, 3, 5);
```

#### ⚠️ Errores Comunes con AND/OR

```sql
-- ❌ ERROR 1: Olvidar paréntesis con combinación AND + OR
WHERE type = 'ENTRADA' AND quantity > 1000 OR productId = 3;
-- Esto NO es lo que crees. AND tiene prioridad sobre OR.

-- ✅ CORRECTO: Usar paréntesis
WHERE type = 'ENTRADA' AND (quantity > 1000 OR productId = 3);

-- ❌ ERROR 2: Repetir columna innecesariamente con OR
WHERE type = 'ENTRADA' OR 'SALIDA' OR 'TRANSFERENCIA';  -- ¡Sintaxis inválida!

-- ✅ CORRECTO: Usar IN
WHERE type IN ('ENTRADA', 'SALIDA', 'TRANSFERENCIA');

-- ❌ ERROR 3: Usar AND cuando necesitas OR
WHERE supplierId = 1 AND supplierId = 3;  -- ¡Imposible! Un valor no puede ser 1 Y 3

-- ✅ CORRECTO: Usar OR o IN
WHERE supplierId = 1 OR supplierId = 3;
WHERE supplierId IN (1, 3);
```

#### 📊 Diagrama ASCII: Precedencia AND/OR

```
┌────────────────────────────────────────────────────┐
│  Consulta: WHERE A AND B OR C                      │
└────────────────────────────────────────────────────┘
                    ↓
          SIN PARÉNTESIS (AND primero)
          ┌─────────────┐
          │   A AND B   │  OR  C
          └─────────────┘
          Si (A y B) es True, o si C es True → TRUE

          ┌────────────────────────────────────────┐
          │  Consulta: WHERE A AND (B OR C)        │
          └────────────────────────────────────────┘
                    ↓
          CON PARÉNTESIS (OR primero)
                    ┌───────────┐
          A  AND    │  B OR C   │
                    └───────────┘
          Si A es True Y (B o C) es True → TRUE
```

---

### 🔹 5. Paginación con OFFSET y FETCH NEXT

#### ¿Qué es la Paginación?

La paginación divide resultados grandes en **"páginas"** más pequeñas, como Google muestra 10 resultados por página.

**Ventajas:**
- ✅ Reduce memoria usada (no cargas 10,000 filas en el `ResultSet`)
- ✅ Mejora velocidad de carga (el usuario ve resultados inmediatamente)
- ✅ Mejor experiencia de usuario (navegación página 1, 2, 3...)

#### Sintaxis SQL Server

```sql
SELECT * FROM tabla
ORDER BY columna  -- OBLIGATORIO para paginación
OFFSET n ROWS     -- Saltar las primeras n filas
FETCH NEXT m ROWS ONLY;  -- Traer solo las siguientes m filas
```

#### Fórmula para Calcular OFFSET

```
OFFSET = (página - 1) × tamañoPágina
```

Ejemplos:
- Página 1, tamaño 10 → OFFSET = (1-1) × 10 = **0** (no saltar nada)
- Página 2, tamaño 10 → OFFSET = (2-1) × 10 = **10** (saltar primeras 10)
- Página 3, tamaño 10 → OFFSET = (3-1) × 10 = **20** (saltar primeras 20)

#### Ejemplos Prácticos con Forestech

```sql
-- 1. Primera página (10 movimientos más recientes)
SELECT * FROM combustibles_movements
ORDER BY movementDate DESC
OFFSET 0 ROWS
FETCH NEXT 10 ROWS ONLY;

-- 2. Segunda página (movimientos 11-20)
SELECT * FROM combustibles_movements
ORDER BY movementDate DESC
OFFSET 10 ROWS
FETCH NEXT 10 ROWS ONLY;

-- 3. Tercera página (movimientos 21-30)
SELECT * FROM combustibles_movements
ORDER BY movementDate DESC
OFFSET 20 ROWS
FETCH NEXT 10 ROWS ONLY;

-- 4. Paginación con filtro (solo ENTRADAS)
SELECT * FROM combustibles_movements
WHERE type = 'ENTRADA'
ORDER BY quantity DESC
OFFSET 0 ROWS
FETCH NEXT 5 ROWS ONLY;
```

#### ⚠️ Errores Comunes con Paginación

```sql
-- ❌ ERROR 1: Olvidar ORDER BY
SELECT * FROM combustibles_movements
OFFSET 10 ROWS
FETCH NEXT 10 ROWS ONLY;
-- ¡Error SQL! OFFSET requiere ORDER BY obligatorio

-- ✅ CORRECTO: Incluir ORDER BY
SELECT * FROM combustibles_movements
ORDER BY id
OFFSET 10 ROWS
FETCH NEXT 10 ROWS ONLY;

-- ❌ ERROR 2: Sintaxis incorrecta (MySQL vs SQL Server)
-- MySQL usa LIMIT:
SELECT * FROM tabla LIMIT 10 OFFSET 5;  -- NO funciona en SQL Server

-- SQL Server usa FETCH:
SELECT * FROM tabla
ORDER BY id
OFFSET 5 ROWS
FETCH NEXT 10 ROWS ONLY;

-- ❌ ERROR 3: OFFSET negativo
OFFSET -10 ROWS  -- ¡Error SQL!

-- ✅ CORRECTO: Validar en Java antes
if (page < 1) page = 1;
int offset = (page - 1) * pageSize;
```

#### 📊 Diagrama ASCII: Paginación Visual

```
    Base de datos: 50 movimientos totales

    ┌──────────────────────────────────────────────┐
    │ Mov1, Mov2, ..., Mov10 [Página 1]           │ OFFSET 0, FETCH 10
    ├──────────────────────────────────────────────┤
    │ Mov11, Mov12, ..., Mov20 [Página 2]         │ OFFSET 10, FETCH 10
    ├──────────────────────────────────────────────┤
    │ Mov21, Mov22, ..., Mov30 [Página 3]         │ OFFSET 20, FETCH 10
    ├──────────────────────────────────────────────┤
    │ Mov31, Mov32, ..., Mov40 [Página 4]         │ OFFSET 30, FETCH 10
    ├──────────────────────────────────────────────┤
    │ Mov41, Mov42, ..., Mov50 [Página 5]         │ OFFSET 40, FETCH 10
    └──────────────────────────────────────────────┘

    Usuario solicita Página 3:
    → OFFSET = (3-1) × 10 = 20
    → FETCH NEXT 10 ROWS
    → Resultado: Mov21 hasta Mov30
```

---

## 💬 Explícalo con Tus Palabras (Técnica Feynman)

Antes de continuar, **explica con tus propias palabras** (sin ver apuntes):

1. ¿Cuál es la diferencia entre `'%ABC%'`, `'ABC%'` y `'%ABC'` en LIKE?

   📝 **[Escribe aquí tu explicación]**

2. ¿Por qué usarías `IN` en vez de múltiples `OR`?

   📝 **[Escribe aquí tu explicación]**

3. ¿Qué significa "BETWEEN es inclusivo"? Da un ejemplo con números.

   📝 **[Escribe aquí tu explicación]**

4. Explica la fórmula `OFFSET = (página - 1) × tamañoPágina` como si se lo explicaras a alguien que no sabe programar.

   📝 **[Escribe aquí tu explicación]**

**💡 CONSEJO:** Si no puedes explicar algo claramente, vuelve a leer esa sección antes de continuar.

---

## 👨‍💻 Implementación Paso a Paso

Ahora vamos a implementar **métodos de búsqueda avanzada** en `MovementService.java`. Cada método usará una de las cláusulas aprendidas.

---

### 🔹 Método 1: searchMovementsByType() con LIKE

#### ❓ Active Recall

Antes de ver el código, responde:

¿Qué consulta SQL usarías para buscar movimientos cuyo tipo CONTENGA la palabra "ENT" (para encontrar "ENTRADA", "REENTRADA", etc.)?

💭 **[Piensa 30 segundos]**

<details>
<summary>✅ Ver respuesta</summary>

```sql
SELECT * FROM combustibles_movements
WHERE type LIKE '%ENT%';
```

</details>

---

#### Implementación Completa

```java
// MovementService.java

/**
 * Busca movimientos cuyo tipo contenga el texto especificado (case-insensitive).
 * Útil para búsquedas parciales o autocompletado.
 *
 * @param partialType Texto a buscar (ej: "ENT" encuentra "ENTRADA")
 * @return Lista de movimientos que coinciden (puede estar vacía)
 */
public List<Movement> searchMovementsByType(String partialType) {
    // Validación de entrada
    if (partialType == null || partialType.trim().isEmpty()) {
        System.out.println("⚠️ El texto de búsqueda no puede estar vacío");
        return new ArrayList<>();  // Retornar lista vacía, no null
    }

    List<Movement> results = new ArrayList<>();

    // SQL con LIKE y LOWER() para búsqueda case-insensitive
    String sql = "SELECT * FROM combustibles_movements " +
                 "WHERE LOWER(type) LIKE LOWER(?) " +
                 "ORDER BY movementDate DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // IMPORTANTE: Añadir wildcards % en Java, no en SQL
        String searchPattern = "%" + partialType.trim() + "%";
        pstmt.setString(1, searchPattern);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            results.add(mapResultSetToMovement(rs));
        }

        System.out.println("✅ Búsqueda completada: " + results.size() + " movimientos encontrados");

    } catch (SQLException e) {
        System.err.println("❌ Error al buscar movimientos por tipo: " + e.getMessage());
        e.printStackTrace();
    }

    return results;
}
```

#### 🔍 Análisis Línea por Línea

```java
// LÍNEA 1: Validación de entrada
if (partialType == null || partialType.trim().isEmpty()) {
    // Si el usuario pasa null o "", evitamos consultar la BD innecesariamente
    return new ArrayList<>();  // Retornar lista vacía, NO null (patrón estándar)
}

// LÍNEA 2: SQL con LOWER() para case-insensitive
"WHERE LOWER(type) LIKE LOWER(?)"
// LOWER("ENTRADA") = "entrada"
// LOWER("ENT") = "ent"
// "entrada" LIKE "%ent%" → TRUE ✅

// LÍNEA 3: Añadir wildcards en Java
String searchPattern = "%" + partialType.trim() + "%";
pstmt.setString(1, searchPattern);
// Si partialType = "ENT", searchPattern = "%ENT%"
// El PreparedStatement se encarga del escape automático
```

#### ⚠️ Errores Comunes

```java
// ❌ ERROR 1: Wildcards en SQL en vez de Java
String sql = "WHERE type LIKE '%?%'";  // ¡El placeholder ? queda literal!
pstmt.setString(1, "ENT");  // Busca "%ENT%" pero SQL interpreta "%?%"

// ✅ CORRECTO: Wildcards en Java
String sql = "WHERE type LIKE ?";
pstmt.setString(1, "%" + partialType + "%");

// ❌ ERROR 2: No usar LOWER() (búsqueda case-sensitive)
"WHERE type LIKE ?"  // "ENTRADA" ≠ "entrada" (dependiendo del collation)

// ✅ CORRECTO: LOWER() en ambos lados
"WHERE LOWER(type) LIKE LOWER(?)"

// ❌ ERROR 3: No validar entrada null
public List<Movement> search(String text) {
    String sql = "WHERE type LIKE ?";
    pstmt.setString(1, "%" + text + "%");  // ¡NullPointerException si text=null!
}

// ✅ CORRECTO: Validar primero
if (partialType == null || partialType.trim().isEmpty()) {
    return new ArrayList<>();
}
```

---

### 🔹 Método 2: getMovementsByTypes() con IN

#### ❓ Active Recall

¿Cómo construirías dinámicamente una consulta SQL con `IN` si tienes una `List<String> types` de tamaño variable (podría tener 2, 5 o 10 elementos)?

💭 **[Piensa 30 segundos]**

<details>
<summary>✅ Ver respuesta</summary>

Necesitas construir placeholders `?` dinámicamente:
```java
// Si types tiene 3 elementos: ["ENTRADA", "SALIDA", "TRANSFERENCIA"]
String sql = "WHERE type IN (?, ?, ?)";  // 3 placeholders
```

Esto se hace con un bucle o `String.join()`.

</details>

---

#### Implementación Completa

```java
// MovementService.java

/**
 * Obtiene movimientos que sean de cualquiera de los tipos especificados.
 * Usa IN para eficiencia en vez de múltiples OR.
 *
 * @param types Lista de tipos a buscar (ej: ["ENTRADA", "SALIDA"])
 * @return Lista de movimientos que coinciden (puede estar vacía)
 */
public List<Movement> getMovementsByTypes(List<String> types) {
    // Validación: lista no puede estar vacía
    if (types == null || types.isEmpty()) {
        System.out.println("⚠️ Debe especificar al menos un tipo");
        return new ArrayList<>();
    }

    List<Movement> results = new ArrayList<>();

    // Construir placeholders dinámicamente: ?, ?, ?
    String placeholders = String.join(", ",
        types.stream()
             .map(t -> "?")  // Reemplazar cada elemento con "?"
             .toArray(String[]::new)
    );

    // SQL con IN dinámico
    String sql = "SELECT * FROM combustibles_movements " +
                 "WHERE type IN (" + placeholders + ") " +
                 "ORDER BY movementDate DESC";

    System.out.println("📝 SQL generado: " + sql);

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // Asignar valores a cada placeholder
        for (int i = 0; i < types.size(); i++) {
            pstmt.setString(i + 1, types.get(i));  // i+1 porque JDBC empieza en 1
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            results.add(mapResultSetToMovement(rs));
        }

        System.out.println("✅ Encontrados " + results.size() + " movimientos de tipos: " + types);

    } catch (SQLException e) {
        System.err.println("❌ Error al buscar movimientos por tipos: " + e.getMessage());
        e.printStackTrace();
    }

    return results;
}
```

#### 🔍 Análisis Línea por Línea

```java
// LÍNEA 1: Construir placeholders dinámicos
String placeholders = String.join(", ",
    types.stream()
         .map(t -> "?")
         .toArray(String[]::new)
);
// Si types = ["ENTRADA", "SALIDA", "TRANSFERENCIA"]
// Resultado: "?, ?, ?"

// LÍNEA 2: SQL dinámico
String sql = "WHERE type IN (" + placeholders + ")";
// Resultado final: "WHERE type IN (?, ?, ?)"

// LÍNEA 3: Asignar valores con bucle
for (int i = 0; i < types.size(); i++) {
    pstmt.setString(i + 1, types.get(i));
}
// Iteración 1: pstmt.setString(1, "ENTRADA")
// Iteración 2: pstmt.setString(2, "SALIDA")
// Iteración 3: pstmt.setString(3, "TRANSFERENCIA")
```

#### 📊 Diagrama: Construcción Dinámica de IN

```
    Entrada: types = ["ENTRADA", "SALIDA", "TRANSFERENCIA"]

    ┌──────────────────────────────────────────────┐
    │  PASO 1: Convertir cada elemento a "?"      │
    └──────────────────────────────────────────────┘
                    ↓
            ["?", "?", "?"]
                    ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 2: Unir con ", "                      │
    └──────────────────────────────────────────────┘
                    ↓
              "?, ?, ?"
                    ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 3: Insertar en SQL                     │
    └──────────────────────────────────────────────┘
                    ↓
    "WHERE type IN (?, ?, ?)"
                    ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 4: Asignar valores con bucle          │
    └──────────────────────────────────────────────┘
                    ↓
    pstmt.setString(1, "ENTRADA")
    pstmt.setString(2, "SALIDA")
    pstmt.setString(3, "TRANSFERENCIA")
                    ↓
    ✅ Consulta completa lista para ejecutar
```

---

### 🔹 Método 3: getMovementsByQuantityRange() con BETWEEN

#### ❓ Active Recall

Si quieres buscar movimientos con cantidad entre 100 y 500 litros, ¿qué cláusula SQL es más legible: `BETWEEN` o `>= AND <=`?

💭 **[Piensa 10 segundos]**

<details>
<summary>✅ Ver respuesta</summary>

Ambas son equivalentes, pero `BETWEEN` es más concisa y legible:

```sql
-- Opción 1: BETWEEN (recomendado)
WHERE quantity BETWEEN 100 AND 500;

-- Opción 2: Comparadores (más verboso)
WHERE quantity >= 100 AND quantity <= 500;
```

</details>

---

#### Implementación Completa

```java
// MovementService.java

/**
 * Obtiene movimientos con cantidad dentro de un rango específico.
 * Usa BETWEEN para rangos inclusivos.
 *
 * @param minQuantity Cantidad mínima (inclusive)
 * @param maxQuantity Cantidad máxima (inclusive)
 * @return Lista de movimientos en el rango (puede estar vacía)
 */
public List<Movement> getMovementsByQuantityRange(double minQuantity, double maxQuantity) {
    // Validación: min no puede ser mayor que max
    if (minQuantity > maxQuantity) {
        System.out.println("⚠️ Cantidad mínima no puede ser mayor que la máxima");
        return new ArrayList<>();
    }

    // Validación: cantidades no pueden ser negativas
    if (minQuantity < 0 || maxQuantity < 0) {
        System.out.println("⚠️ Las cantidades no pueden ser negativas");
        return new ArrayList<>();
    }

    List<Movement> results = new ArrayList<>();

    String sql = "SELECT * FROM combustibles_movements " +
                 "WHERE quantity BETWEEN ? AND ? " +
                 "ORDER BY quantity DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setDouble(1, minQuantity);
        pstmt.setDouble(2, maxQuantity);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            results.add(mapResultSetToMovement(rs));
        }

        System.out.println("✅ Encontrados " + results.size() +
                           " movimientos con cantidad entre " + minQuantity +
                           " y " + maxQuantity + " litros");

    } catch (SQLException e) {
        System.err.println("❌ Error al buscar movimientos por rango de cantidad: " + e.getMessage());
        e.printStackTrace();
    }

    return results;
}
```

#### ⚠️ Errores Comunes

```java
// ❌ ERROR 1: Invertir min y max
pstmt.setDouble(1, maxQuantity);  // ¡Orden incorrecto!
pstmt.setDouble(2, minQuantity);

// ✅ CORRECTO: Min primero, max segundo
pstmt.setDouble(1, minQuantity);
pstmt.setDouble(2, maxQuantity);

// ❌ ERROR 2: No validar orden de parámetros
public List<Movement> getRange(double min, double max) {
    // Si el usuario pasa (500, 100), BETWEEN 500 AND 100 NO devuelve resultados
}

// ✅ CORRECTO: Validar y/o intercambiar
if (minQuantity > maxQuantity) {
    // Opción 1: Retornar error
    return new ArrayList<>();

    // Opción 2: Intercambiar automáticamente (depende del requisito)
    double temp = minQuantity;
    minQuantity = maxQuantity;
    maxQuantity = temp;
}
```

---

### 🔹 Método 4: advancedSearch() con Filtros Opcionales

Este método combina **múltiples cláusulas** (LIKE, IN, BETWEEN) y permite filtros opcionales (null = no aplicar ese filtro).

#### ❓ Active Recall

Si tienes un método con 5 parámetros opcionales (pueden ser null), ¿cómo construirías dinámicamente la consulta SQL?

💭 **[Piensa 1 minuto]**

<details>
<summary>✅ Ver pista</summary>

Usa `StringBuilder` y añade cada cláusula `WHERE` solo si el parámetro no es null:

```java
StringBuilder sql = new StringBuilder("SELECT * FROM tabla WHERE 1=1");
if (param1 != null) sql.append(" AND columna1 = ?");
if (param2 != null) sql.append(" AND columna2 LIKE ?");
```

</details>

---

#### Implementación Completa

```java
// MovementService.java

/**
 * Búsqueda avanzada con filtros opcionales.
 * Todos los parámetros pueden ser null (se ignoran si lo son).
 *
 * @param type Tipo exacto (ej: "ENTRADA") o null
 * @param minQuantity Cantidad mínima o null
 * @param maxQuantity Cantidad máxima o null
 * @param supplierId ID de proveedor o null
 * @param partialDescription Texto a buscar en descripción o null
 * @return Lista de movimientos que cumplen TODOS los filtros especificados
 */
public List<Movement> advancedSearch(String type,
                                      Double minQuantity,
                                      Double maxQuantity,
                                      Integer supplierId,
                                      String partialDescription) {
    List<Movement> results = new ArrayList<>();

    // Construir SQL dinámicamente
    StringBuilder sql = new StringBuilder(
        "SELECT * FROM combustibles_movements WHERE 1=1"  // Truco: 1=1 siempre es true
    );

    // Lista para guardar valores de parámetros en orden
    List<Object> parameters = new ArrayList<>();

    // Añadir filtros solo si no son null
    if (type != null && !type.trim().isEmpty()) {
        sql.append(" AND type = ?");
        parameters.add(type.trim());
    }

    if (minQuantity != null && maxQuantity != null) {
        sql.append(" AND quantity BETWEEN ? AND ?");
        parameters.add(minQuantity);
        parameters.add(maxQuantity);
    } else if (minQuantity != null) {
        sql.append(" AND quantity >= ?");
        parameters.add(minQuantity);
    } else if (maxQuantity != null) {
        sql.append(" AND quantity <= ?");
        parameters.add(maxQuantity);
    }

    if (supplierId != null) {
        sql.append(" AND supplierId = ?");
        parameters.add(supplierId);
    }

    if (partialDescription != null && !partialDescription.trim().isEmpty()) {
        sql.append(" AND LOWER(description) LIKE LOWER(?)");
        parameters.add("%" + partialDescription.trim() + "%");
    }

    sql.append(" ORDER BY movementDate DESC");

    System.out.println("📝 SQL generado: " + sql.toString());
    System.out.println("📝 Parámetros: " + parameters);

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

        // Asignar parámetros dinámicamente
        for (int i = 0; i < parameters.size(); i++) {
            Object param = parameters.get(i);

            // Usar setObject() para manejar múltiples tipos
            pstmt.setObject(i + 1, param);
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            results.add(mapResultSetToMovement(rs));
        }

        System.out.println("✅ Búsqueda avanzada completada: " + results.size() + " resultados");

    } catch (SQLException e) {
        System.err.println("❌ Error en búsqueda avanzada: " + e.getMessage());
        e.printStackTrace();
    }

    return results;
}
```

#### 🔍 Análisis Línea por Línea

```java
// LÍNEA 1: Truco del WHERE 1=1
StringBuilder sql = new StringBuilder("WHERE 1=1");
// 1=1 es siempre TRUE, permite añadir AND sin preocuparse del primer filtro
// Sin 1=1:
//   "WHERE" + " AND type=?" → "WHERE AND type=?" ❌ (sintaxis inválida)
// Con 1=1:
//   "WHERE 1=1" + " AND type=?" → "WHERE 1=1 AND type=?" ✅ (válido)

// LÍNEA 2: Validar null antes de añadir filtro
if (type != null && !type.trim().isEmpty()) {
    sql.append(" AND type = ?");
    parameters.add(type.trim());
}
// Si type=null, este bloque NO se ejecuta (el filtro se ignora)

// LÍNEA 3: Manejar rangos parciales
if (minQuantity != null && maxQuantity != null) {
    sql.append(" AND quantity BETWEEN ? AND ?");  // Ambos límites
} else if (minQuantity != null) {
    sql.append(" AND quantity >= ?");  // Solo mínimo
} else if (maxQuantity != null) {
    sql.append(" AND quantity <= ?");  // Solo máximo
}
// Permite búsquedas como "cantidad >= 100" sin especificar máximo

// LÍNEA 4: Usar setObject() para múltiples tipos
pstmt.setObject(i + 1, param);
// setObject() detecta automáticamente el tipo (String, Double, Integer)
```

#### 📊 Diagrama: Construcción Dinámica de SQL

```
    Entrada: type="ENTRADA", minQty=100, maxQty=500, supplierId=null, desc=null

    ┌──────────────────────────────────────────────┐
    │  PASO 1: SQL base                            │
    └──────────────────────────────────────────────┘
            ↓
    "SELECT * FROM combustibles_movements WHERE 1=1"
            ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 2: Añadir filtro type (no es null)    │
    └──────────────────────────────────────────────┘
            ↓
    "... WHERE 1=1 AND type = ?"
    parameters = ["ENTRADA"]
            ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 3: Añadir filtro cantidad (ambos set) │
    └──────────────────────────────────────────────┘
            ↓
    "... WHERE 1=1 AND type = ? AND quantity BETWEEN ? AND ?"
    parameters = ["ENTRADA", 100, 500]
            ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 4: Saltar supplierId (es null)        │
    └──────────────────────────────────────────────┘
            ↓
    (No se añade nada)
            ↓
    ┌──────────────────────────────────────────────┐
    │  PASO 5: Saltar description (es null)       │
    └──────────────────────────────────────────────┘
            ↓
    SQL final: "... WHERE 1=1 AND type = ? AND quantity BETWEEN ? AND ?"
    Parámetros: ["ENTRADA", 100, 500]
            ↓
    ✅ Ejecutar pstmt.executeQuery()
```

---

### 🔹 Método 5: getMovementsPaginated() con OFFSET y FETCH

#### Implementación Completa

```java
// MovementService.java

/**
 * Obtiene movimientos paginados.
 *
 * @param page Número de página (empieza en 1)
 * @param pageSize Cantidad de resultados por página
 * @return Lista de movimientos de la página solicitada
 */
public List<Movement> getMovementsPaginated(int page, int pageSize) {
    // Validación: página mínima es 1
    if (page < 1) {
        System.out.println("⚠️ El número de página debe ser >= 1. Usando página 1.");
        page = 1;
    }

    // Validación: tamaño de página razonable
    if (pageSize < 1 || pageSize > 100) {
        System.out.println("⚠️ Tamaño de página inválido. Usando 10.");
        pageSize = 10;
    }

    List<Movement> results = new ArrayList<>();

    // Calcular offset
    int offset = (page - 1) * pageSize;

    String sql = "SELECT * FROM combustibles_movements " +
                 "ORDER BY movementDate DESC " +
                 "OFFSET ? ROWS " +
                 "FETCH NEXT ? ROWS ONLY";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, offset);
        pstmt.setInt(2, pageSize);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            results.add(mapResultSetToMovement(rs));
        }

        System.out.println("✅ Página " + page + " cargada (" + results.size() + " movimientos)");

    } catch (SQLException e) {
        System.err.println("❌ Error al obtener movimientos paginados: " + e.getMessage());
        e.printStackTrace();
    }

    return results;
}

/**
 * Cuenta el total de movimientos (para calcular total de páginas).
 *
 * @return Cantidad total de movimientos en la tabla
 */
public int getTotalMovements() {
    String sql = "SELECT COUNT(*) AS total FROM combustibles_movements";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        if (rs.next()) {
            return rs.getInt("total");
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al contar movimientos: " + e.getMessage());
        e.printStackTrace();
    }

    return 0;
}
```

---

## 🧪 Práctica Deliberada

### 🟢 Nivel 1: Guiado - searchVehiclesByPlate()

**Objetivo:** Implementar búsqueda parcial de vehículos por placa usando `LIKE`.

**Contexto:** El usuario escribe "ABC" y quiere encontrar todos los vehículos con placas como "ABC-123", "XYZ-ABC", "ABC123".

**Ayuda:**
1. Método: `public List<Vehicle> searchVehiclesByPlate(String partialPlate)`
2. SQL: `"SELECT * FROM combustibles_vehicles WHERE plate LIKE ?"`
3. Añadir wildcards en Java: `"%" + partialPlate + "%"`
4. Usar `LOWER()` para case-insensitive

**Esqueleto del Código:**

```java
// VehicleService.java

public List<Vehicle> searchVehiclesByPlate(String partialPlate) {
    // TODO 1: Validar que partialPlate no sea null ni vacío
    // Si lo es, retornar new ArrayList<>()

    List<Vehicle> results = new ArrayList<>();

    // TODO 2: Definir SQL con LIKE y LOWER()
    String sql = "___________________________________";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // TODO 3: Añadir wildcards % antes de asignar parámetro
        String searchPattern = "___________________________________";
        pstmt.setString(1, searchPattern);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            // TODO 4: Mapear ResultSet a Vehicle
            Vehicle v = new Vehicle();
            v.setId(rs.getInt("id"));
            // ... completar los demás campos

            results.add(v);
        }

        System.out.println("✅ Encontrados " + results.size() + " vehículos");

    } catch (SQLException e) {
        System.err.println("❌ Error al buscar vehículos: " + e.getMessage());
        e.printStackTrace();
    }

    return results;
}
```

**Prueba en Main.java:**

```java
// Main.java

VehicleService vehicleService = new VehicleService();

// Buscar placas que contengan "ABC"
List<Vehicle> results = vehicleService.searchVehiclesByPlate("ABC");

for (Vehicle v : results) {
    System.out.println("Placa: " + v.getPlate() + ", Marca: " + v.getBrand());
}
```

---

### 🟡 Nivel 2: Semi-guiado - getSuppliersByPriceRange()

**Objetivo:** Buscar proveedores cuyo precio por litro esté en un rango específico usando `BETWEEN`.

**Contexto:** El administrador quiere ver proveedores con precios entre $2.50 y $3.00 por litro.

**Pistas:**
- Método: `public List<Supplier> getSuppliersByPriceRange(double min, double max)`
- SQL: Usa `BETWEEN ? AND ?` en la columna `pricePerLiter`
- Valida que `min <= max` antes de ejecutar
- Ordena por precio ascendente

**Preguntas Guía:**

1. ¿Qué pasa si el usuario pasa (max=2.5, min=3.0)? ¿Cómo lo validarías?

   💭 **[Piensa antes de codificar]**

2. ¿Deberías permitir precios negativos? ¿Cómo validarlo?

   💭 **[Piensa antes de codificar]**

**Implementa el método completo en SupplierService.java**

---

### 🟠 Nivel 3: Autónomo - advancedVehicleSearch()

**Objetivo:** Crear un método de búsqueda avanzada para vehículos con 4 filtros opcionales.

**Requisitos:**

```java
/**
 * Búsqueda avanzada de vehículos con filtros opcionales.
 *
 * @param partialPlate Texto a buscar en placa (LIKE) o null
 * @param brand Marca exacta o null
 * @param minCapacity Capacidad mínima de tanque o null
 * @param maxCapacity Capacidad máxima de tanque o null
 * @return Lista de vehículos que cumplen TODOS los filtros especificados
 */
public List<Vehicle> advancedVehicleSearch(String partialPlate,
                                            String brand,
                                            Double minCapacity,
                                            Double maxCapacity)
```

**Desafío:** Debes construir el SQL dinámicamente con `StringBuilder` según qué parámetros sean null.

**Casos de Prueba:**

```java
// Caso 1: Solo filtrar por placa
vehicleService.advancedVehicleSearch("ABC", null, null, null);

// Caso 2: Solo filtrar por marca
vehicleService.advancedVehicleSearch(null, "Volvo", null, null);

// Caso 3: Filtrar por placa + rango de capacidad
vehicleService.advancedVehicleSearch("ABC", null, 5000.0, 10000.0);

// Caso 4: Todos los filtros
vehicleService.advancedVehicleSearch("ABC", "Volvo", 5000.0, 15000.0);

// Caso 5: Ningún filtro (debe retornar TODOS los vehículos)
vehicleService.advancedVehicleSearch(null, null, null, null);
```

**Implementa el método completo y prueba los 5 casos en Main.java**

---

### 🔴 Nivel 4: Desafío - Sistema de Paginación Completo

**Objetivo:** Crear una interfaz CLI para navegar movimientos con paginación (Página 1, 2, 3... Siguiente, Anterior, Última).

**Requisitos:**

1. Implementar `getMovementsPaginated(int page, int pageSize)` (ya lo hiciste arriba)
2. Implementar `getTotalPages(int pageSize)` calculando `ceil(totalMovements / pageSize)`
3. Crear un menú en `Main.java` que permita:
   - Ver página actual
   - Ir a página siguiente (validar que no exceda total)
   - Ir a página anterior (validar que no sea < 1)
   - Ir a página específica
   - Cambiar tamaño de página (5, 10, 20 resultados)
   - Salir

**Esqueleto del Menú:**

```java
// Main.java

Scanner scanner = new Scanner(System.in);
MovementService service = new MovementService();

int currentPage = 1;
int pageSize = 10;

while (true) {
    // Obtener movimientos de la página actual
    List<Movement> movements = service.getMovementsPaginated(currentPage, pageSize);

    // Calcular total de páginas
    int totalMovements = service.getTotalMovements();
    int totalPages = (int) Math.ceil((double) totalMovements / pageSize);

    // Mostrar resultados
    System.out.println("\n═════════════════════════════════════");
    System.out.println("📄 PÁGINA " + currentPage + " DE " + totalPages);
    System.out.println("   Mostrando " + movements.size() + " de " + totalMovements + " movimientos");
    System.out.println("═════════════════════════════════════");

    for (Movement m : movements) {
        System.out.println(m.getId() + " | " + m.getType() + " | " +
                           m.getQuantity() + "L | " + m.getMovementDate());
    }

    // Menú de opciones
    System.out.println("\n[N] Siguiente  [A] Anterior  [I] Ir a página  [T] Cambiar tamaño  [S] Salir");
    System.out.print("Opción: ");
    String option = scanner.nextLine().trim().toUpperCase();

    switch (option) {
        case "N":
            // TODO: Ir a página siguiente (validar que no exceda totalPages)
            break;
        case "A":
            // TODO: Ir a página anterior (validar que no sea < 1)
            break;
        case "I":
            // TODO: Solicitar número de página y validar
            break;
        case "T":
            // TODO: Solicitar nuevo tamaño de página (5, 10, 20)
            break;
        case "S":
            System.out.println("¡Hasta luego!");
            return;
        default:
            System.out.println("⚠️ Opción inválida");
    }
}
```

**Implementa el sistema completo y prueba navegación entre páginas.**

---

## 🔍 Depuración: Código Roto con 6 Errores

El siguiente código tiene **6 errores comunes** en consultas avanzadas. Encuentra y corrígelos:

```java
// MovementService.java

public List<Movement> brokenAdvancedSearch(String type, Double minQty, Double maxQty) {
    List<Movement> results = new ArrayList<>();

    // ERROR 1: ¿Qué pasa si type es null?
    String sql = "SELECT * FROM combustibles_movements " +
                 "WHERE type = ? " +  // ← Error aquí
                 "AND quantity BETWEEN ? AND ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        // ERROR 2: Orden incorrecto de parámetros
        pstmt.setDouble(1, minQty);    // ← Error aquí
        pstmt.setDouble(2, maxQty);
        pstmt.setString(3, type);

        ResultSet rs = pstmt.executeQuery();

        // ERROR 3: No validar si el ResultSet está vacío
        Movement m = new Movement();
        m.setId(rs.getString("id"));       // ← Error aquí
        m.setType(rs.getString("type"));
        results.add(m);

        // ERROR 4: No usar while() para múltiples filas

    } catch (SQLException e) {
        // ERROR 5: No imprimir el error (dificulta debugging)
    }

    // ERROR 6: Retornar null en vez de lista vacía si hay error
    return null;  // ← Error aquí
}
```

**Instrucciones:**
1. Identifica cada error (hay 6 marcados con comentarios)
2. Escribe la corrección para cada uno
3. Explica POR QUÉ es un error

<details>
<summary>✅ Ver solución completa</summary>

```java
public List<Movement> fixedAdvancedSearch(String type, Double minQty, Double maxQty) {
    List<Movement> results = new ArrayList<>();

    // CORRECCIÓN 1: SQL dinámico con StringBuilder para filtros opcionales
    StringBuilder sql = new StringBuilder("SELECT * FROM combustibles_movements WHERE 1=1");
    List<Object> params = new ArrayList<>();

    if (type != null && !type.trim().isEmpty()) {
        sql.append(" AND type = ?");
        params.add(type);
    }

    if (minQty != null && maxQty != null) {
        sql.append(" AND quantity BETWEEN ? AND ?");
        params.add(minQty);
        params.add(maxQty);
    }

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

        // CORRECCIÓN 2: Asignar parámetros en el orden correcto
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = pstmt.executeQuery();

        // CORRECCIÓN 3 y 4: Usar while() y validar que rs.next() sea true
        while (rs.next()) {
            Movement m = new Movement();
            m.setId(rs.getString("id"));
            m.setType(rs.getString("type"));
            m.setQuantity(rs.getDouble("quantity"));
            // ... mapear todos los campos
            results.add(m);
        }

    } catch (SQLException e) {
        // CORRECCIÓN 5: Imprimir error para debugging
        System.err.println("❌ Error al buscar movimientos: " + e.getMessage());
        e.printStackTrace();
    }

    // CORRECCIÓN 6: Siempre retornar lista (vacía si no hay resultados)
    return results;
}
```

**Explicación de Errores:**

1. **SQL estático con parámetros opcionales:** Si `type` es null, el `pstmt.setString()` fallará o buscará registros con `type=NULL` (que probablemente no existen).
2. **Orden incorrecto de parámetros:** El SQL espera `type` primero, pero asignamos `minQty` primero.
3. **No validar rs.next():** `rs.next()` debe llamarse ANTES de acceder a columnas, y retorna `false` si no hay filas.
4. **No usar while():** Si hay múltiples filas, solo se procesa la primera.
5. **No imprimir error:** Dificulta identificar problemas durante desarrollo.
6. **Retornar null:** El estándar es retornar lista vacía (`new ArrayList<>()`) para evitar `NullPointerException` en el código que llama al método.

</details>

---

## 🔄 Interleaving: Tabla Comparativa de Operaciones

| Operación | Sintaxis | Cuándo Usar | Ejemplo Forestech | Ventaja Principal |
|-----------|----------|-------------|-------------------|-------------------|
| **=** | `WHERE col = val` | Búsqueda exacta | `type = 'ENTRADA'` | Máxima velocidad con índices |
| **LIKE** | `WHERE col LIKE '%val%'` | Búsqueda parcial | `plate LIKE '%ABC%'` | Flexible, permite wildcards |
| **IN** | `WHERE col IN (v1, v2)` | Múltiples valores | `type IN ('ENTRADA', 'SALIDA')` | Más legible que múltiples OR |
| **BETWEEN** | `WHERE col BETWEEN min AND max` | Rangos inclusivos | `quantity BETWEEN 100 AND 500` | Conciso para rangos |
| **>= AND <=** | `WHERE col >= min AND col <= max` | Rangos personalizados | `quantity > 100 AND quantity < 500` | Mayor control (excluir límites) |
| **AND** | `WHERE cond1 AND cond2` | Todas las condiciones deben cumplirse | `type='ENTRADA' AND quantity>1000` | Filtros restrictivos |
| **OR** | `WHERE cond1 OR cond2` | Al menos una condición debe cumplirse | `supplierId=1 OR supplierId=3` | Filtros amplios |

### 📊 Performance Relativo (Estimado)

```
    Más rápido                                   Más lento
    ═══════════════════════════════════════════════════════
    =  >  IN  >  BETWEEN  >  LIKE 'ABC%'  >  LIKE '%ABC%'

    Explicación:
    - "=" con índice: Búsqueda directa en índice (O(log n))
    - "IN": Múltiples búsquedas en índice
    - "LIKE 'ABC%'": Puede usar índice (empieza con patrón conocido)
    - "LIKE '%ABC%'": Full table scan (no puede usar índice)
```

---

## 📊 Autoevaluación (Metacognition)

Evalúa tu nivel de confianza en cada concepto (1 = "No entiendo" → 5 = "Puedo enseñarlo"):

| Concepto | Nivel de Confianza (1-5) | Notas Personales |
|----------|--------------------------|------------------|
| Wildcards de LIKE (%, _, []) | [ ] | |
| Diferencia '%ABC%' vs 'ABC%' vs '%ABC' | [ ] | |
| Operador IN con múltiples valores | [ ] | |
| BETWEEN inclusivo vs >= AND <= | [ ] | |
| Combinar AND + OR con paréntesis | [ ] | |
| Precedencia de operadores lógicos | [ ] | |
| Construir SQL dinámico con StringBuilder | [ ] | |
| Fórmula de paginación (OFFSET) | [ ] | |
| Usar LOWER() para case-insensitive | [ ] | |
| Validar parámetros null en filtros opcionales | [ ] | |

**Reflexión:**

1. ¿Qué concepto te resultó más difícil de esta fase?

   📝 **[Escribe aquí]**

2. ¿Por qué crees que fue difícil?

   📝 **[Escribe aquí]**

3. ¿Qué estrategia usarás para reforzar ese concepto?

   📝 **[Escribe aquí]**

---

## ✅ Checklist de Salida

Marca cada ítem solo si puedes hacerlo **sin mirar apuntes**:

- [ ] Puedo explicar la diferencia entre `'%ABC%'`, `'ABC%'` y `'%ABC'` con ejemplos
- [ ] Puedo escribir una consulta con `IN` para buscar múltiples tipos de movimientos
- [ ] Puedo usar `BETWEEN` correctamente con números y fechas
- [ ] Entiendo por qué `LIKE '%ABC%'` es más lento que `LIKE 'ABC%'`
- [ ] Puedo combinar `AND` y `OR` con paréntesis correctamente
- [ ] Puedo construir SQL dinámico con `StringBuilder` para filtros opcionales
- [ ] Puedo calcular `OFFSET` para cualquier número de página
- [ ] Puedo implementar un método de búsqueda avanzada completo sin ayuda
- [ ] Completé al menos 3 de los 4 ejercicios prácticos
- [ ] Identifiqué y corregí los 6 errores del código roto

**Si marcaste menos de 8 ítems, repasa las secciones donde tienes dudas antes de avanzar.**

---

## 📅 Plan de Repaso (Spaced Repetition)

### 📆 Día 1 (Hoy - Después de estudiar)

- ✅ Completar los 4 ejercicios de práctica deliberada
- ✅ Probar cada método en `Main.java` con datos reales
- ✅ Crear 5 consultas SQL con LIKE, IN, BETWEEN en un archivo `consultas_practica.sql`

---

### 📆 Día 3 (Repaso Corto - 30 minutos)

**Sin mirar apuntes, responde:**

1. Escribe una consulta SQL que busque vehículos con placa que EMPIECE con "ABC"
2. Escribe una consulta SQL que busque movimientos de tipo "ENTRADA", "SALIDA" o "AJUSTE"
3. Escribe una consulta SQL que busque movimientos con cantidad entre 200 y 800 litros
4. Calcula el `OFFSET` para la página 5 con tamaño de página 15

**Luego, implementa en Java:**
- Un método `searchProductsByName(String partialName)` usando LIKE
- Prueba el método con 3 búsquedas diferentes

---

### 📆 Día 7 (Repaso Profundo - 1 hora)

**Proyecto Mini: Sistema de Búsqueda de Proveedores**

Crea en `SupplierService.java`:

```java
/**
 * Búsqueda avanzada de proveedores con filtros opcionales.
 *
 * @param partialName Texto a buscar en nombre (LIKE) o null
 * @param minPrice Precio mínimo por litro o null
 * @param maxPrice Precio máximo por litro o null
 * @param isActive Estado activo (1/0) o null
 * @return Lista de proveedores que cumplen todos los filtros
 */
public List<Supplier> advancedSupplierSearch(String partialName,
                                              Double minPrice,
                                              Double maxPrice,
                                              Boolean isActive)
```

**Requisitos:**
- Usar LIKE para el nombre
- Usar BETWEEN para precios (si ambos están definidos)
- Construir SQL dinámicamente
- Probar con 5 combinaciones diferentes de filtros en `Main.java`

---

### 📆 Día 14 (Consolidación - 45 minutos)

**Explica en voz alta o por escrito (sin mirar apuntes):**

1. ¿Qué es un wildcard y cuáles existen en SQL Server?
2. ¿Por qué `LIKE '%ABC%'` es más lento que `LIKE 'ABC%'`?
3. ¿Cuándo usarías `IN` en vez de múltiples `OR`?
4. ¿Qué significa "BETWEEN es inclusivo"? Da un ejemplo.
5. ¿Por qué necesitas `ORDER BY` con `OFFSET` y `FETCH NEXT`?

**Luego, enseña estos conceptos a alguien más (o a un patito de goma 🦆).**

---

## 🐛 Errores Comunes y Soluciones

### Error 1: "Invalid object name" con LIKE

```sql
-- ❌ Causa: Tabla o columna incorrecta
SELECT * FROM movements WHERE tipo LIKE '%ABC%';
--                            ↑ Columna "tipo" no existe (es "type")

-- ✅ Solución: Verificar nombres exactos
SELECT * FROM combustibles_movements WHERE type LIKE '%ABC%';
```

---

### Error 2: "The parameterized query expects parameter '@P1', which was not supplied"

```java
// ❌ Causa: Olvidar asignar parámetro
String sql = "WHERE type = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.executeQuery();  // ¡Falta setString()!

// ✅ Solución: Asignar TODOS los parámetros
pstmt.setString(1, "ENTRADA");
pstmt.executeQuery();
```

---

### Error 3: "ORDER BY items must appear in the select list" con OFFSET

```sql
-- ❌ Causa: SQL Server requiere ORDER BY explícito
SELECT * FROM combustibles_movements
OFFSET 10 ROWS
FETCH NEXT 10 ROWS ONLY;
-- Error: The ORDER BY clause is required when OFFSET is used

-- ✅ Solución: Añadir ORDER BY (aunque no importe el orden)
SELECT * FROM combustibles_movements
ORDER BY id  -- Orden arbitrario pero válido
OFFSET 10 ROWS
FETCH NEXT 10 ROWS ONLY;
```

---

### Error 4: LIKE no encuentra resultados (case-sensitivity)

```java
// ❌ Problema: Collation case-sensitive
pstmt.setString(1, "%abc%");  // Busca "abc" minúsculas
// Tabla tiene "ABC" mayúsculas → No encuentra nada

// ✅ Solución: Usar LOWER() o UPPER() en ambos lados
String sql = "WHERE LOWER(plate) LIKE LOWER(?)";
pstmt.setString(1, "%abc%");  // Ahora sí encuentra "ABC", "abc", "Abc"
```

---

### Error 5: IN con lista vacía

```java
// ❌ Problema: Lista de tipos vacía genera SQL inválido
List<String> types = new ArrayList<>();  // Vacía
String sql = "WHERE type IN ()";  // SQL inválido

// ✅ Solución: Validar antes
if (types == null || types.isEmpty()) {
    return new ArrayList<>();  // Retornar vacío sin consultar
}
```

---

## 📚 Recursos Adicionales

### Documentación Oficial

- [SQL Server LIKE Operator](https://learn.microsoft.com/en-us/sql/t-sql/language-elements/like-transact-sql)
- [SQL Server IN Operator](https://learn.microsoft.com/en-us/sql/t-sql/language-elements/in-transact-sql)
- [SQL Server BETWEEN](https://learn.microsoft.com/en-us/sql/t-sql/language-elements/between-transact-sql)
- [SQL Server OFFSET-FETCH](https://learn.microsoft.com/en-us/sql/t-sql/queries/select-order-by-clause-transact-sql#offset-fetch)

### Herramientas Útiles

- **SQL Server Management Studio (SSMS):** Para probar consultas antes de implementarlas en Java
- **DBeaver:** Cliente SQL multiplataforma (alternativa gratuita a SSMS)

### Ejercicios Extra

- [SQLZoo - SELECT with LIKE](https://sqlzoo.net/wiki/SELECT_names)
- [HackerRank - SQL Advanced Select](https://www.hackerrank.com/domains/sql?filters%5Bsubdomains%5D%5B%5D=advanced-select)

---

## ➡️ Próximo Paso

### FASE_04.9_PROYECTO_INTEGRACION.md (Próximo archivo)

En la siguiente fase integraremos TODOS los conceptos aprendidos (CRUD completo + consultas avanzadas + transacciones + soft delete) en un **proyecto final: Sistema de Reportes Forestech**.

**Adelanto:**
- Reporte de inventario con filtros avanzados
- Reporte de movimientos por fecha con paginación
- Dashboard con estadísticas agregadas
- Exportación de resultados a CSV
- Menú interactivo completo

**Requisito:** Haber completado Fase 4.1 a 4.8 con confianza nivel 4/5 en cada concepto.

---

## 🎓 Conclusión

¡Felicitaciones! Ahora dominas consultas SQL avanzadas:

✅ **LIKE** para búsquedas flexibles con wildcards
✅ **IN** para múltiples valores sin múltiples OR
✅ **BETWEEN** para rangos inclusivos elegantes
✅ **AND/OR** combinados con precedencia correcta
✅ **Paginación** con OFFSET y FETCH NEXT
✅ **SQL dinámico** con StringBuilder para filtros opcionales

Estos conceptos son **universales** y se aplican a cualquier base de datos SQL (MySQL, PostgreSQL, Oracle, etc.) con ligeras variaciones en sintaxis.

---

**📌 ÚLTIMA ACTUALIZACIÓN:** 2025-01-09
**📌 AUTOR:** Claude Code (Forestech CLI - Fase 4.8)
**📌 SIGUIENTE ARCHIVO:** FASE_04.9_PROYECTO_INTEGRACION.md (Pendiente)

---