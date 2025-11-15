# GUÍA DE SEGUIMIENTO - ROADMAPS RETROSPECTIVOS FORESTECHOIL
**Control de Progreso para Documentación de Fases**

---

## Estado Actual del Proyecto

**Última actualización:** 2025-01-14

### Código Implementado (Funcional)

El proyecto ForestechOil está en **Fase 7 completada** (funcional completo):

✅ **Fase 01:** Fundamentos (helpers, utils, config)
✅ **Fase 02:** POO - Modelos (Movement, Product, Vehicle, Supplier, Factura, DetalleFactura)
✅ **Fase 03:** Colecciones - MovementManagers (legacy, educativo)
✅ **Fase 04:** JDBC - DatabaseConnection
✅ **Fase 05:** CRUD - Services (ProductServices, MovementServices, VehicleServices, SupplierServices, FacturaServices)
✅ **Fase 06:** Lógica de Negocio - Validaciones, transacciones, stock tracking
✅ **Fase 07:** Excepciones - DatabaseException, InsufficientStockException, TransactionFailedException, InvalidMovementException
✅ **Fase 08:** CLI Interactiva - AppController con menús anidados, wizards, reportes
⚠️ **Fase 09:** Streams/Lambdas - NO implementado (futuro)
⚠️ **Fase 10:** Testing - NO implementado (JUnit disponible pero sin tests)

---

## Estado de Documentación (Roadmaps)

### Roadmaps Completados ✅

| Fase | Archivo | Estado | Fecha | Líneas | Cobertura |
|------|---------|--------|-------|--------|-----------|
| 01 | `FASE_01_FUNDAMENTOS.md` | ✅ COMPLETO | 2025-01-14 | ~450 | 100% |
| 02 | `FASE_02_POO_MODELOS.md` | ✅ COMPLETO | 2025-01-14 | ~650 | 100% |
| 03 | `FASE_03_MANAGERS_COLECCIONES.md` | ✅ COMPLETO | 2025-01-14 | ~550 | 100% |

**Total documentado:** 3/10 fases (30%)

---

### Roadmaps Pendientes 🔄

| Fase | Archivo Objetivo | Archivos a Analizar | Prioridad | Complejidad |
|------|------------------|---------------------|-----------|-------------|
| 04 | `FASE_04_CONEXION_JDBC.md` | `config/DatabaseConnection.java` | 🔴 ALTA | MEDIA |
| 05 | `FASE_05_CRUD_BASICO.md` | `services/ProductServices.java`, `services/VehicleServices.java` | 🔴 ALTA | ALTA |
| 06 | `FASE_06_LOGICA_NEGOCIO.md` | `services/MovementServices.java` (validaciones), `services/FacturaServices.java` (transacciones) | 🔴 ALTA | ALTA |
| 07 | `FASE_07_EXCEPCIONES.md` | `exceptions/DatabaseException.java`, `exceptions/InsufficientStockException.java`, etc. | 🟡 MEDIA | BAJA |
| 08 | `FASE_08_CLI_INTERACTIVA.md` | `AppController.java` (completo, ~800 líneas) | 🔴 ALTA | MUY ALTA |
| 09 | `FASE_09_STREAMS_LAMBDAS.md` | (Fase futura, ejemplos de refactorización) | 🟢 BAJA | MEDIA |
| 10 | `FASE_10_TESTING.md` | (Fase futura, crear tests desde cero) | 🟢 BAJA | MEDIA |

---

## Formato Estándar de Roadmaps

### Estructura Obligatoria

Cada roadmap debe seguir esta estructura:

```markdown
# FASE XX: TÍTULO DE LA FASE
**Roadmap Retrospectivo - Análisis del Código Existente**

## Contexto de esta Fase
- Explicar QUÉ se implementó en esta fase
- Relacionar con fases anteriores y siguientes

## Objetivos de Aprendizaje
- Lista numerada de conceptos que se refuerzan

## Arquitectura de la Fase
- Diagrama ASCII de la estructura
- Ubicación de archivos en el proyecto

## [Secciones por Archivo]
### Archivo: `ruta/Clase.java`
#### X.1 Concepto: Explicación teórica
#### X.2 Análisis del Código (líneas específicas)
#### X.3 Análisis línea por línea (con ejemplos)
#### X.4 Uso en el Proyecto (referencias reales)
#### X.5 Checkpoint de Verificación ✅

## Bugs y Mejoras Identificados
- Tabla con bugs encontrados y correcciones

## Ejercicios de Corrección/Mejora 🔧
- Ejercicios prácticos con soluciones

## Resumen de la Fase
- Tabla de conceptos implementados
- Diagrama de relaciones

## Ejercicio Final de la Fase 🎯
- Tarea completa con verificación

## Autoevaluación ✅
- Preguntas de comprensión

## Próximos Pasos
- Enlace a la siguiente fase
```

---

### Características del Análisis

**OBLIGATORIAS:**
1. ✅ **Referencias exactas:** `Archivo.java:línea`
2. ✅ **Análisis línea por línea:** Explicar QUÉ hace cada línea
3. ✅ **Conceptos teóricos:** Antes de cada análisis de código
4. ✅ **Ejemplos contextuales:** Usar ForestechOil como contexto
5. ✅ **Identificar bugs:** Marcar con 🔴 y proponer correcciones
6. ✅ **Ejercicios prácticos:** Con código ejecutable
7. ✅ **Checkpoints frecuentes:** Preguntas de verificación cada 2-3 secciones
8. ✅ **Navegación:** Enlaces a fase anterior y siguiente

**PROHIBIDAS:**
- ❌ Código completo listo para copiar sin explicación
- ❌ Asumir conocimientos previos sin explicar
- ❌ Saltar análisis de métodos complejos
- ❌ Teoría sin ejemplos del proyecto real

---

## Contenido por Fase

### FASE 04: CONEXIÓN JDBC

**Archivos a analizar:**
- `config/DatabaseConnection.java` (completo)

**Conceptos clave:**
- JDBC API y DriverManager
- Connection, Statement, PreparedStatement
- URL de conexión MySQL
- Singleton pattern (estático)
- testConnection() con metadata
- Try-with-resources básico
- SQLException handling

**Bugs conocidos:**
- Credenciales hardcoded (línea config)
- No cierra conexiones explícitamente (mejora: connection pooling)

**Ejercicios:**
1. Externalizar credenciales a `config.properties`
2. Crear método `closeConnection()`
3. Implementar connection pooling con HikariCP (avanzado)

**Complejidad:** MEDIA (70-100 líneas de código a analizar)

---

### FASE 05: CRUD BÁSICO

**Archivos a analizar:**
- `services/ProductServices.java` (completo, ~150 líneas)
- `services/VehicleServices.java` (completo, ~120 líneas)
- `services/SupplierServices.java` (completo, ~100 líneas)

**Conceptos clave:**
- PreparedStatement vs Statement (SQL injection)
- INSERT con parámetros
- SELECT con ResultSet
- UPDATE y DELETE
- mapResultSetToModel() pattern
- Try-with-resources con múltiples recursos
- Manejo de FK constraints

**Bugs conocidos:**
- Ninguno crítico (código maduro)

**Ejercicios:**
1. Agregar método `searchProductsByPriceRange(min, max)`
2. Implementar soft delete en Products
3. Crear índices en la base de datos para mejorar SELECT

**Complejidad:** ALTA (400+ líneas de código total)

---

### FASE 06: LÓGICA DE NEGOCIO

**Archivos a analizar:**
- `services/MovementServices.java` (validación de stock, FK)
- `services/FacturaServices.java` (transacciones)

**Conceptos clave:**
- Validación de stock antes de SALIDA
- Cálculos con SUM(CASE WHEN...)
- Transacciones JDBC (setAutoCommit, commit, rollback)
- Batch operations (addBatch, executeBatch)
- Atomicidad de operaciones
- Foreign key validation

**Bugs conocidos:**
- Ninguno crítico

**Ejercicios:**
1. Agregar rollback con savepoint
2. Implementar factura con movimientos automáticos
3. Crear trigger para actualizar stock (SQL)

**Complejidad:** ALTA (transacciones son complejas conceptualmente)

---

### FASE 07: EXCEPCIONES

**Archivos a analizar:**
- `exceptions/DatabaseException.java`
- `exceptions/InsufficientStockException.java`
- `exceptions/TransactionFailedException.java`
- `exceptions/InvalidMovementException.java`

**Conceptos clave:**
- Custom exceptions
- Exception wrapping (causa original)
- Checked vs unchecked exceptions
- Exception propagation
- Try-catch en servicios
- Mensajes de error user-friendly

**Bugs conocidos:**
- InvalidMovementException definida pero no usada

**Ejercicios:**
1. Usar InvalidMovementException en MovementServices
2. Crear VehicleNotFoundException
3. Implementar logging con excepciones

**Complejidad:** BAJA (clases cortas, concepto claro)

---

### FASE 08: CLI INTERACTIVA

**Archivos a analizar:**
- `AppController.java` (completo, ~800 líneas)

**Conceptos clave:**
- Arquitectura MVC en CLI
- Menús anidados (switch-case)
- Wizards de entrada (paso a paso)
- Formateo de tablas ASCII
- Selección por índice
- Confirmación de operaciones destructivas
- Manejo de excepciones en UI
- Reportes con indicadores visuales

**Bugs conocidos:**
- Ninguno crítico (código funcional)

**Ejercicios:**
1. Refactorizar menús a clases separadas
2. Implementar paginación en listados
3. Agregar búsqueda incremental

**Complejidad:** MUY ALTA (archivo más grande del proyecto)

---

### FASE 09: STREAMS Y LAMBDAS (Futura)

**Archivos a crear/refactorizar:**
- Ejemplos de refactorización con streams
- Uso de lambdas en filtros
- Method references

**Conceptos clave:**
- Stream API
- map(), filter(), reduce()
- Collectors
- Lambdas vs clases anónimas
- Parallel streams

**Ejercicios:**
1. Refactorizar `getMovementsByType()` con streams
2. Calcular stock con reduce()
3. Agrupar movimientos por fecha con groupingBy()

**Complejidad:** MEDIA (conceptos nuevos pero código corto)

---

### FASE 10: TESTING CON JUNIT (Futura)

**Archivos a crear:**
- Tests para ProductServices
- Tests para MovementServices
- Mocks con Mockito

**Conceptos clave:**
- JUnit 5 (Jupiter)
- @Test, @BeforeEach, @AfterEach
- Assertions
- Mockito para aislar dependencias
- Test de base de datos (H2 in-memory)

**Ejercicios:**
1. Crear test para insertProduct()
2. Crear test para validación de stock
3. Crear test para transacciones (rollback)

**Complejidad:** MEDIA (tests son explícitos)

---

## Estrategia de Completación

### Orden Recomendado

**Sesión 1 (Completada):**
- ✅ Fase 01: Fundamentos
- ✅ Fase 02: POO Modelos
- ✅ Fase 03: Managers

**Sesión 2 (Próxima):**
- 🔄 Fase 04: Conexión JDBC
- 🔄 Fase 07: Excepciones (más fácil, momentum)

**Sesión 3:**
- 🔄 Fase 05: CRUD Básico (la más larga)

**Sesión 4:**
- 🔄 Fase 06: Lógica de Negocio
- 🔄 Fase 08: CLI Interactiva (la más compleja)

**Sesión 5 (Opcional):**
- 🔄 Fase 09: Streams (refactorización)
- 🔄 Fase 10: Testing (creación nueva)

---

## Métricas de Progreso

### Por Líneas de Código

| Fase | Líneas de Código | Líneas de Documentación | Ratio |
|------|------------------|-------------------------|-------|
| 01 | ~150 | ~450 | 3.0x |
| 02 | ~400 | ~650 | 1.6x |
| 03 | ~170 | ~550 | 3.2x |
| 04 | ~80 | ~400 (estimado) | 5.0x |
| 05 | ~400 | ~600 (estimado) | 1.5x |
| 06 | ~300 | ~550 (estimado) | 1.8x |
| 07 | ~100 | ~350 (estimado) | 3.5x |
| 08 | ~800 | ~900 (estimado) | 1.1x |
| 09 | ~200 | ~400 (estimado) | 2.0x |
| 10 | ~300 | ~500 (estimado) | 1.7x |

**Total:** ~2,900 líneas código → ~5,750 líneas documentación

---

### Por Tiempo Estimado

| Fase | Complejidad | Tiempo Estimado | Notas |
|------|-------------|-----------------|-------|
| 01 | MEDIA | ✅ 60 min | Completada |
| 02 | ALTA | ✅ 90 min | Completada |
| 03 | MEDIA | ✅ 70 min | Completada |
| 04 | MEDIA | 50 min | Archivo corto |
| 05 | ALTA | 90 min | 3 archivos Services |
| 06 | ALTA | 80 min | Transacciones complejas |
| 07 | BAJA | 40 min | Clases cortas |
| 08 | MUY ALTA | 120 min | AppController gigante |
| 09 | MEDIA | 60 min | Ejemplos refactorización |
| 10 | MEDIA | 70 min | Crear tests desde cero |

**Total estimado:** ~10 horas (distribuir en 4-5 sesiones)

---

## Checklist por Sesión

### Antes de Empezar una Fase

- [ ] Leer la sección correspondiente en esta guía
- [ ] Identificar los archivos a analizar
- [ ] Abrir los archivos en el IDE para tener contexto
- [ ] Revisar los bugs conocidos de esa fase

### Durante la Escritura

- [ ] Seguir la estructura estándar
- [ ] Incluir referencias `Archivo.java:línea`
- [ ] Explicar conceptos teóricos ANTES del código
- [ ] Analizar línea por línea secciones complejas
- [ ] Identificar y marcar bugs con 🔴
- [ ] Crear ejercicios prácticos ejecutables
- [ ] Incluir checkpoints cada 2-3 secciones

### Al Completar una Fase

- [ ] Verificar que tiene todas las secciones obligatorias
- [ ] Compilar el proyecto para verificar referencias
- [ ] Actualizar esta guía (marcar fase como completada)
- [ ] Hacer commit del roadmap:
  ```bash
  git add roadmaps/FASE_XX_TITULO.md roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md
  git commit -m "Docs: Completar FASE_XX - Título descriptivo"
  ```

---

## Convenciones de Escritura

### Código en Markdown

**Fragmentos cortos (inline):**
```markdown
El método `insertProduct()` usa `PreparedStatement`.
```

**Bloques de código:**
````markdown
```java
public void insertProduct(Product product) throws DatabaseException {
    String sql = "INSERT INTO oil_products VALUES (?, ?, ?, ?)";
    // ...
}
```
````

### Referencia a Líneas

**Formato estándar:**
```markdown
**Línea 42:** `this.id = IdGenerator.generateFuelId();`
```

**Rango de líneas:**
```markdown
**Líneas 56-62: Constructor LOAD**
```

### Marcadores Visuales

- ✅ **Completado/Correcto**
- ❌ **Incorrecto/Error**
- ⚠️ **Advertencia/Atención**
- 🔴 **Bug crítico**
- 🟡 **Bug menor**
- 🟢 **Mejora opcional**
- 🔧 **Ejercicio de corrección**
- 🎯 **Ejercicio de práctica**
- 📌 **Nota importante**

### Diagramas ASCII

**Relaciones:**
```
Movement ──► Product
    ↓
 Vehicle
```

**Estructuras:**
```
com.forestech/
├── models/
│   └── Movement.java
└── services/
    └── MovementServices.java
```

---

## Archivos Relacionados

- **Guía principal:** `roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md` (este archivo)
- **Prompt reutilizable:** `roadmaps/PROMPT_CONTINUAR_ROADMAPS.md` (siguiente)
- **Análisis inicial:** Resultados del agente Explore en sesión 1
- **Roadmaps completados:** `roadmaps/FASE_01_FUNDAMENTOS.md`, etc.

---

## Control de Versiones

| Versión | Fecha | Cambios | Autor |
|---------|-------|---------|-------|
| 1.0 | 2025-01-14 | Creación inicial, Fases 01-03 completadas | Claude Code |
| 1.1 | TBD | Fase 04 completada | (Próxima sesión) |
| 1.2 | TBD | Fase 05 completada | (Próxima sesión) |

---

**🎓 Guía de Seguimiento Creada**

Esta guía es el mapa de ruta para completar las 10 fases de roadmaps retrospectivos. Actualízala después de cada sesión para mantener el progreso visible.

**Siguiente paso:** Usar el prompt reutilizable en `PROMPT_CONTINUAR_ROADMAPS.md` para continuar con Fase 04.
