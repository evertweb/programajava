# 🗺️ Plan de Reescritura de Roadmaps - Metodología Invertida

> **"Primero construimos, luego documentamos el camino que recorrimos"**

---

## 📘 Filosofía: Código Primero, Documentación Después

### ¿Por qué esta metodología?

**Enfoque tradicional (Waterfall educativo):**
```
Teoría → Roadmap → Código → Testing
```
❌ Problema: El roadmap se vuelve obsoleto cuando el código evoluciona

**Enfoque invertido (Forestech):**
```
Código funcional → Testing → Roadmap basado en código real
```
✅ Ventaja: El roadmap refleja la REALIDAD del proyecto, no una idealización

### Beneficios para el Estudiante

1. **Veracidad**: Cada ejemplo en el roadmap apunta a código que REALMENTE funciona
2. **Descubrimiento Guiado**: El estudiante explora código existente en lugar de escribir desde cero
3. **Debugging Real**: Los errores documentados son los que realmente ocurrieron
4. **Contexto Completo**: Los roadmaps muestran cómo encajan las piezas entre sí

---

## 🎯 Estado Actual del Proyecto

### Código Implementado (Base para Roadmaps)

| Fase | Completitud Código | Completitud Roadmap | Archivos Clave |
|------|-------------------|---------------------|----------------|
| **Fase 0** | 100% | 0% | Setup, instalación de herramientas |
| **Fase 1** | 100% | 0% | Variables, loops, métodos básicos |
| **Fase 2** | 100% | 0% | `models/*.java` (Movement, Product, Vehicle, Supplier) |
| **Fase 2.5** | 100% | 0% | `managers/MovementManagers.java` (patrón manager) |
| **Fase 2.9** | 100% | 0% | Defensive copying en constructores |
| **Fase 3** | 100% | 90% | `config/DatabaseConnection.java`, JDBC básico |
| **Fase 4.1** | 100% | 100% | `services/ProductServices.java` (INSERT) |
| **Fase 4.2** | 100% | 100% | `services/ProductServices.java` (SELECT) |
| **Fase 4.3** | 100% | 100% | `services/ProductServices.java` (UPDATE) |
| **Fase 4.4** | 100% | 100% | `services/ProductServices.java` (DELETE) |
| **Fase 4.5** | 100% | 100% | CRUD consolidado |
| **Fase 4.6** | 100% | 100% | `services/FacturaServices.java` (transacciones) |
| **Fase 4.7** | 80% | 70% | Soft delete (parcialmente implementado) |
| **Fase 4.8** | 70% | 60% | JOINs en MovementServices |
| **Fase 5** | 60% | 80% | `exceptions/*.java`, validaciones |
| **Fase 6** | 10% | 0% | `AppController.java` (skeleton) |
| **Fase 7** | 0% | 0% | Manejo avanzado de excepciones |
| **Fase 8** | 0% | 0% | Streams, Lambdas |
| **Fase 9** | 0% | 0% | Testing con JUnit |
| **Fase 10** | 0% | 0% | Refactoring, patrones de diseño |

---

## 📋 Roadmaps a Crear (en orden de prioridad)

### 🔴 PRIORIDAD ALTA (Código 100% funcional, falta documentación)

#### 1. FASE_00_PREPARACION.md
**📍 Código base:** Setup inicial del proyecto
**🎯 Objetivo:** Configurar entorno de desarrollo (Java, Maven, MySQL, IDE)
**📝 Contenido:**
- Instalación de JDK 17
- Configuración de Maven
- Instalación de MySQL
- Creación de proyecto Maven
- Estructura de carpetas inicial

**Conceptos:**
- ¿Qué es Java?
- ¿Qué es Maven?
- ¿Qué es una base de datos?

**Checkpoints:**
- [x] JDK instalado y configurado
- [x] Maven funcionando (`mvn --version`)
- [x] MySQL corriendo en localhost
- [x] Proyecto Maven creado con `pom.xml`

---

#### 2. FASE_01_FUNDAMENTOS.md
**📍 Código base:** Versión inicial de `Main.java` (commits antiguos)
**🎯 Objetivo:** Aprender sintaxis básica de Java
**📝 Contenido:**
- Variables y tipos de datos
- Operadores aritméticos
- Condicionales (if/else)
- Bucles (for, while)
- Métodos básicos (sin POO)

**Ejemplos con Forestech:**
```java
// Variables
double litrosAcpm = 500.0;
double precioPorLitro = 8500.0;
double total = litrosAcpm * precioPorLitro;

// Condicionales
if (litrosAcpm > 0) {
    System.out.println("Stock disponible");
} else {
    System.out.println("Sin stock");
}

// Bucles
for (int i = 0; i < 10; i++) {
    System.out.println("Movimiento " + i);
}

// Métodos
public static double calcularTotal(double litros, double precio) {
    return litros * precio;
}
```

**Checkpoints:**
- [x] Declarar variables de diferentes tipos
- [x] Usar operadores aritméticos
- [x] Crear condicionales if/else
- [x] Implementar bucles for y while
- [x] Escribir métodos con parámetros y retorno

---

#### 3. FASE_02_POO.md
**📍 Código base:**
- `models/Movement.java:1-263`
- `models/Product.java:1-66`
- `models/Vehicle.java:1-122`
- `models/Supplier.java`

**🎯 Objetivo:** Entender clases, objetos, encapsulación
**📝 Contenido:**
- Concepto de clase vs objeto
- Atributos privados
- Constructores (vacío, con parámetros, dual para crear/cargar)
- Getters y setters
- toString()
- `final` en atributos inmutables

**Análisis línea por línea de Movement.java:**

```java
// Línea 15: Atributo final (inmutable)
private final String id;
// ¿Por qué final? → El ID no debe cambiar después de crearse

// Línea 42: Constructor para CREAR (genera ID)
public Movement(String movementType, String productId, ...) {
    this.id = IdGenerator.generateMovementId();  // Auto-generado
    // ...
}

// Línea 68: Constructor para CARGAR (ID existente)
public Movement(String id, String movementType, ...) {
    this.id = id;  // ID de la BD
    // ...
}
```

**Analogía:** Clase = Molde de galletas, Objeto = Galleta

**Checkpoints:**
- [x] Crear clase `Movement` con atributos privados
- [x] Implementar constructores dual (crear vs cargar)
- [x] Escribir getters y setters con validación
- [x] Sobrescribir toString() para debugging
- [x] Entender diferencia entre `null` y `""`

---

#### 4. FASE_02.5_MOVEMENTMANAGER.md
**📍 Código base:** `managers/MovementManagers.java:1-end`
**🎯 Objetivo:** Patrón Manager, Collections (ArrayList)
**📝 Contenido:**
- ArrayList vs arrays tradicionales
- Patrón Manager (CRUD en memoria)
- Métodos de búsqueda (findById, getMovementsByType)
- Métodos de cálculo (calculateTotalEntered, calculateTotalExited)
- Javadoc completo

**¿Por qué este patrón?**
Antes de JDBC, necesitábamos manejar colecciones de movimientos en memoria. El Manager centraliza la lógica.

**Métodos clave:**
```java
// CRUD básico
public Movement addMovement(Movement movement);
public Movement findById(String id);
public List<Movement> getAllMovements();

// Filtros
public List<Movement> getMovementsByType(String type);

// Cálculos
public double calculateTotalEntered();
public double calculateCurrentStock();
```

**Checkpoints:**
- [x] Crear ArrayList de Movements
- [x] Implementar addMovement()
- [x] Implementar findById() con búsqueda lineal
- [x] Implementar getMovementsByType() con filtro
- [x] Calcular stock sumando ENTRADAS - SALIDAS

---

#### 5. FASE_02.9_COPIAS_DEFENSIVAS.md
**📍 Código base:** `Main.java` (demostraciones de defensive copy)
**🎯 Objetivo:** Proteger datos internos de modificaciones externas
**📝 Contenido:**
- Problema: referencias compartidas
- Solución: copias defensivas en constructor
- Solución: copias defensivas en getters
- Trade-offs (memoria vs seguridad)

**Ejemplo del problema:**
```java
// ❌ SIN DEFENSIVE COPY
List<Movement> baseDatos = new ArrayList<>();
MovementManager manager = new MovementManager(baseDatos);

baseDatos.clear();  // ⚠️ Esto BORRA la lista interna del manager!
```

**Ejemplo de la solución:**
```java
// ✅ CON DEFENSIVE COPY
public MovementManager(List<Movement> movements) {
    this.movements = new ArrayList<>(movements);  // Copia
}
```

**Checkpoints:**
- [x] Demostrar problema de referencias compartidas
- [x] Implementar constructor con defensive copy
- [x] Implementar getter con defensive copy
- [x] Entender cuándo NO usar defensive copies

---

### 🟡 PRIORIDAD MEDIA (Código completo, roadmap parcial)

#### 6. FASE_03_SQL_FUNDAMENTOS.md (Nuevo - Falta crear)
**📍 Código base:** Scripts `01_recreate_tables_with_fk.sql`, `02_restore_data.sql`
**🎯 Objetivo:** SQL básico antes de JDBC
**📝 Contenido:**
- DDL: CREATE TABLE, ALTER TABLE, DROP TABLE
- DML: INSERT, UPDATE, DELETE
- DQL: SELECT con WHERE, ORDER BY
- Foreign Keys (FK)
- Tipos de datos (VARCHAR, INT, DECIMAL, DATE, ENUM)

**Ejemplos Forestech:**
```sql
-- Crear tabla
CREATE TABLE Movement (
    id VARCHAR(50) PRIMARY KEY,
    movementType ENUM('ENTRADA', 'SALIDA'),
    quantity DECIMAL(10,2),
    ...
);

-- Insertar datos
INSERT INTO Movement VALUES ('MOV-001', 'ENTRADA', 500.0, ...);

-- Consultar
SELECT * FROM Movement WHERE movementType = 'SALIDA';
```

**Checkpoints:**
- [x] Crear tabla oil_products
- [x] Insertar 5 productos
- [x] Consultar productos con SELECT
- [x] Actualizar precio de un producto
- [x] Eliminar producto no usado

---

#### 7. FASE_03.3_JDBC_CONEXION.md (Ya existe - Completar)
**📍 Código base:** `config/DatabaseConnection.java:1-53`
**🎯 Objetivo:** Conectar Java con MySQL
**Estado actual:** 90% completo
**📝 Falta agregar:**
- Troubleshooting de errores comunes (puerto 3306 ocupado, credenciales incorrectas)
- Ejercicio: crear segunda conexión a BD de pruebas

---

### 🟢 PRIORIDAD BAJA (Código 0-50%, roadmap 0%)

#### 8. FASE_06_UI.md
**📍 Código base:** `AppController.java:1-101` (skeleton), `helpers/*.java`
**🎯 Objetivo:** CLI interactiva con menús
**Estado actual:** 10% código, 0% roadmap
**📝 Contenido planificado:**
- Bucle principal de menú
- Switch-case para opciones
- InputHelper para validar entrada
- MenuHelper para mostrar opciones
- Manejo de errores de entrada del usuario

**Estructura del menú:**
```
╔════════════════════════════════════╗
║  FORESTECH CLI - MENÚ PRINCIPAL   ║
╠════════════════════════════════════╣
║ 1. Gestionar Productos            ║
║ 2. Gestionar Movimientos          ║
║ 3. Gestionar Vehículos            ║
║ 4. Gestionar Proveedores          ║
║ 5. Ver Inventario                 ║
║ 6. Generar Reportes               ║
║ 0. Salir                          ║
╚════════════════════════════════════╝
Opción: _
```

**Checkpoints pendientes:**
- [ ] Implementar bucle principal while(true)
- [ ] Crear switch-case para procesar opciones
- [ ] Integrar InputHelper.readInt() con validación
- [ ] Crear submenús para cada módulo
- [ ] Manejar opción "Salir" con confirmación

---

#### 9. FASE_07_ERRORES.md
**📍 Código base:** `exceptions/*.java` (ya implementado)
**🎯 Objetivo:** Manejo avanzado de excepciones
**Estado actual:** Código 100%, roadmap 0%
**📝 Contenido:**
- Jerarquía de excepciones
- Checked vs Unchecked
- Try-catch-finally
- Try-with-resources
- Crear excepciones personalizadas
- Multi-catch
- Excepciones encadenadas

**Jerarquía Forestech:**
```
Throwable
  └─ Exception
      └─ ForesotechException (base abstracta)
          ├─ DatabaseException
          ├─ InsufficientStockException
          ├─ InvalidMovementException
          └─ TransactionFailedException
```

---

#### 10. FASE_08_AVANZADOS.md
**📍 Código base:** (No implementado aún)
**🎯 Objetivo:** Streams, Lambdas, Optional
**Estado actual:** 0% código, 0% roadmap
**📝 Contenido futuro:**
- Streams API para filtrar movimientos
- Lambdas en lugar de clases anónimas
- Optional para evitar null
- Method references
- Collectors

**Ejemplo futuro:**
```java
// Con Streams
List<Movement> salidas = movements.stream()
    .filter(m -> m.getMovementType().equals("SALIDA"))
    .filter(m -> m.getQuantity() > 100)
    .collect(Collectors.toList());

// Con Optional
Optional<Movement> movimiento = MovementServices.findById("MOV-001");
movimiento.ifPresent(m -> System.out.println(m));
```

---

## 📐 Estructura Estándar de Cada Roadmap

Todos los roadmaps seguirán esta plantilla:

```markdown
# FASE X: [TÍTULO]

> Frase inspiradora o analogía contextualizada

---

## 🎯 Objetivos de Aprendizaje

Al finalizar este archivo, serás capaz de:
- [ ] Objetivo 1 medible
- [ ] Objetivo 2 medible
- [ ] ...

---

## 📚 Pre-requisitos

- ✅ Fase X-1 completada
- ✅ Conceptos que debes saber: [lista]

---

## 🗂️ Archivos que Trabajarás

- `ruta/archivo.java` - Descripción de qué hace
- `ruta/otro_archivo.java` - Descripción

---

## 1️⃣ INTRODUCCIÓN (10%)

### ¿Qué vas a aprender?
[Bullets con aprendizajes concretos]

### Analogía con Forestech
[Analogía contextualizada con combustibles]

### Vista previa del resultado
```java
// Código final o output esperado
```

---

## 2️⃣ CONCEPTOS FUNDAMENTALES (20%)

### Concepto 1: [Nombre]

**¿Qué es?**
[Explicación simple en 2-3 párrafos]

**¿Para qué sirve en Forestech?**
[Ejemplo concreto del proyecto]

**Sintaxis básica:**
```java
// Pseudocódigo o ejemplo mínimo
```

**Comparación con alternativas:**
| Enfoque A | Enfoque B |
|-----------|-----------|
| Ventaja 1 | Ventaja 1 |
| Desventaja 1 | Desventaja 1 |

[Repetir para cada concepto clave]

---

## 3️⃣ IMPLEMENTACIÓN PASO A PASO (60%)

### ✅ Checkpoint X.1: [Nombre del checkpoint]

**⏱️ Tiempo estimado:** [minutos/horas]
**📍 Archivo:** `ruta/archivo.java`
**🎯 Objetivo:** [Qué lograrás en este checkpoint]

#### Paso 1: [Acción concreta]

[Explicación detallada]

```java
// Código REAL del proyecto (puede estar incompleto si tiene TODO)
public void metodo() {
    // TODO: Completa esta parte
    // PISTA: Debes [explicación de qué hacer]
}
```

**💡 Explicación línea por línea:**
```java
int x = 10;  // Declarar variable entera
// ¿Por qué int? → Porque solo necesitamos números enteros
// ¿Por qué 10? → Valor inicial de ejemplo
```

#### Testing del Checkpoint

```java
// Cómo probar que funciona
public static void main(String[] args) {
    // Test aquí
}
```

**Salida esperada:**
```
✅ Mensaje de éxito
```

#### ✅ Checkpoint completado cuando:
- [ ] Criterio 1 verificable
- [ ] Criterio 2 verificable
- [ ] ...

[Repetir estructura para cada checkpoint]

---

## 4️⃣ EJERCICIOS PRÁCTICOS (10%)

### Ejercicio 1: [Nombre] - ⭐ Fácil

**🎯 Objetivo:** [Qué practicar]
**📋 Tarea:** [Instrucción específica]

**Pistas:**
1. [Pista 1]
2. [Pista 2]

**Solución:**
<details>
<summary>Ver solución</summary>

```java
// Código solución
```

</details>

[Repetir para 3-5 ejercicios con dificultad creciente: ⭐ Fácil, ⭐⭐ Medio, ⭐⭐⭐ Difícil]

---

## 5️⃣ CONSOLIDACIÓN (5%)

### ✅ Checklist de Salida

- [ ] Compilé sin errores (`mvn clean compile`)
- [ ] Todos los tests pasan
- [ ] Entiendo [concepto clave 1]
- [ ] Entiendo [concepto clave 2]
- [ ] Documenté con comentarios en español

### 🐛 Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `NullPointerException` | Variable no inicializada | Inicializar antes de usar |
| [Mensaje de error] | [Por qué ocurre] | [Cómo solucionarlo] |

### 📚 Recursos Adicionales

- [Documentación oficial de Java](https://docs.oracle.com/javase/17/)
- Sección específica del concepto

### 🔗 Próxima Fase

En la **Fase X+1** aprenderás: [teaser de lo que viene]

### 💾 Commit Sugerido

```bash
git add .
git commit -m "Fase X: [descripción breve del checkpoint completado]"
```

---

## 📝 Notas del Instructor (Metadata)

**Duración estimada:** X semanas
**Dificultad:** ⭐⭐⭐ (1-5 estrellas)
**Conceptos prerequisito:** [lista]
**Conceptos enseñados:** [lista]
**Archivos modificados:** [lista]
**Tests requeridos:** [lista]
```

---

## 🚀 Estrategia de Implementación

### Orden de Creación de Roadmaps

1. **Semana 1-2:** FASE_00, FASE_01, FASE_02 (fundamentos)
2. **Semana 3:** FASE_02.5, FASE_02.9 (managers y defensive copy)
3. **Semana 4:** FASE_03 (SQL + JDBC)
4. **Semana 5:** Revisar y mejorar FASE_04.x (ya existen, pero pueden ampliarse)
5. **Semana 6:** Completar FASE_05 (lógica de negocio)
6. **Semana 7-8:** FASE_06 (CLI) - requiere implementar código primero
7. **Futuro:** FASE_07-10 cuando el código esté listo

### Tabla de Decisión: ¿Crear Roadmap o Codificar?

| Situación | Acción |
|-----------|--------|
| Código funcional al 100% | ✅ Crear roadmap inmediatamente |
| Código al 80-99% | ✅ Completar código, luego roadmap |
| Código al 50-79% | ⏸️ Terminar código, luego documentar |
| Código al 0-49% | ❌ Programar primero, documentar después |

### Matriz de Prioridades

```
         │ Código 100% │ Código 50-99% │ Código 0-49%
─────────┼─────────────┼───────────────┼──────────────
Fase 0-3 │ PRIORIDAD 1 │ PRIORIDAD 2   │ PRIORIDAD 4
Fase 4-5 │ PRIORIDAD 2 │ PRIORIDAD 3   │ PRIORIDAD 5
Fase 6-10│ PRIORIDAD 5 │ PRIORIDAD 6   │ PRIORIDAD 7
```

---

## 📊 Métricas de Éxito de un Roadmap

Un roadmap se considera **completo y efectivo** cuando cumple:

### Checklist de Calidad

- [ ] Todos los ejemplos de código apuntan a archivos reales del proyecto
- [ ] Cada checkpoint tiene salida esperada verificable
- [ ] Los ejercicios tienen soluciones probadas
- [ ] Hay al menos 1 analogía contextualizada con combustibles
- [ ] Los TODOs en el código coinciden con los ejercicios del roadmap
- [ ] Se incluyen errores comunes que REALMENTE ocurrieron durante desarrollo
- [ ] El tiempo estimado es realista (probado con estudiante real)
- [ ] Hay continuidad con la fase anterior y teaser de la siguiente

### Criterios de Validación

Cada roadmap debe pasar estas pruebas:

1. **Test del Estudiante Nuevo:**
   - ¿Un estudiante sin conocimientos previos puede seguirlo?
   - ¿Cada paso está suficientemente explicado?

2. **Test de Veracidad:**
   - ¿Los números de línea son correctos?
   - ¿El código copia-pegado compila?

3. **Test de Completitud:**
   - ¿Se cubren TODOS los conceptos necesarios para la siguiente fase?
   - ¿Quedan cabos sueltos?

4. **Test de Tiempo:**
   - ¿El tiempo estimado es realista?
   - ¿Hay checkpoints intermedios cada 1-2 horas?

---

## 🎓 Metodología de Enseñanza (Principios)

### 1. Espiral Conceptual (No Lineal)

Los conceptos se introducen en múltiples fases con profundidad creciente:

**Ejemplo: Excepciones**
- Fase 1: "try-catch básico para evitar crashes"
- Fase 4: "SQLException en JDBC"
- Fase 5: "Excepciones personalizadas con datos"
- Fase 7: "Jerarquía completa, multi-catch, try-with-resources"

### 2. Contexto Antes de Sintaxis

Siempre presentar:
1. ¿Por qué existe este concepto?
2. ¿Qué problema resuelve en Forestech?
3. Sintaxis y uso

**NO al revés** (sintaxis sin contexto es memorización ciega)

### 3. Código Verbose (Educativo)

```java
// ✅ CÓDIGO EDUCATIVO (verbose, con comentarios)
// Calcular subtotal del movimiento
double cantidadLitros = movement.getQuantity();
double precioPorLitro = movement.getUnitPrice();
double subtotal = cantidadLitros * precioPorLitro;
System.out.println("Subtotal: " + subtotal);

// ❌ CÓDIGO PRODUCCIÓN (conciso, sin comentarios)
return movement.getQuantity() * movement.getUnitPrice();
```

Los roadmaps SIEMPRE usan la versión verbose, incluso si el código final es conciso.

### 4. Errores como Herramienta de Aprendizaje

Los roadmaps DEBEN incluir:
- Errores comunes que el estudiante encontrará
- Ejercicios que FALLAN a propósito para que el estudiante depure
- Explicaciones de mensajes de error crípticos

**Ejemplo:**
> "Si ves `java.sql.SQLException: No suitable driver found`, significa que..."

---

## 🔧 Herramientas para Crear Roadmaps

### Formato Markdown

Todos los roadmaps usan Markdown con extensiones GitHub:
- `[ ]` Checkboxes
- `<details>` para spoilers
- Tablas con `|---|---|`
- Bloques de código con ` ```java `

### Plantillas Reutilizables

Ver: `roadmaps/TEMPLATE_FASE.md` (a crear en siguiente paso)

### Scripts de Validación (Futuro)

```bash
# Script para validar que los números de línea sean correctos
./validate_roadmap.sh FASE_02_POO.md
```

---

## 📞 Coordinación con CLAUDE.md

El archivo `CLAUDE.md` contiene instrucciones para Claude Code (asistente IA). Al crear roadmaps, actualizar:

1. **Current Phase** en CLAUDE.md cuando se complete un roadmap
2. **Learning Roadmap** con el estado actualizado
3. **Ejemplos** en Teaching Strategies con snippets de los roadmaps

---

## ✅ Criterios de Éxito del Plan

Este plan de roadmaps se considerará exitoso cuando:

- [ ] Todas las Fases 0-5 tengan roadmaps completos
- [ ] Un estudiante nuevo pueda seguir Fase 0 → Fase 5 sin ayuda externa
- [ ] Los roadmaps estén sincronizados con el código (números de línea correctos)
- [ ] Cada roadmap tenga ejercicios con soluciones verificadas
- [ ] Los tiempos estimados sean realistas (validados con al menos 1 estudiante)
- [ ] Haya continuidad narrativa entre fases

---

## 🎯 Resumen Ejecutivo

**Estado actual:** Código fases 0-5 completo, roadmaps parciales
**Próximos pasos:**
1. Crear TEMPLATE_FASE.md (plantilla reutilizable)
2. Escribir FASE_00_PREPARACION.md
3. Escribir FASE_01_FUNDAMENTOS.md
4. Escribir FASE_02_POO.md (el más importante)
5. Escribir FASE_02.5_MOVEMENTMANAGER.md
6. Completar FASE_03.3_JDBC_CONEXION.md
7. Revisar y mejorar FASE_04.x (ya existen)

**Metodología:** Código primero, roadmaps después. Veracidad > Idealización.

**Herramientas:** Markdown, Git, plantillas reutilizables.

**Validación:** Checklist de calidad + Test del estudiante nuevo.
