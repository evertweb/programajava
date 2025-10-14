# 🤖 PROMPT PARA ACTUALIZAR FASES 6-10 DEL ROADMAP DE JAVA

## 📋 CONTEXTO

Estoy trabajando en un proyecto de aprendizaje de Java llamado **Forestech CLI** (sistema de gestión de combustibles). Las Fases 1-5 del roadmap ya fueron actualizadas con un nuevo formato más didáctico y contextualizado.

**Tu tarea:** Actualizar las **Fases 6, 7, 8, 9 y 10** siguiendo EXACTAMENTE el mismo formato que se usó en las Fases 1-5.

---

## 🎯 FORMATO REQUERIDO

Cada checkpoint debe tener esta estructura:

```markdown
## ✅ Checkpoint X.Y: [Nombre del Checkpoint]

**Concepto clave:** [Explicación breve del concepto principal]

**📍 DÓNDE:** 
- [Ubicación EXACTA del archivo]
- [Ruta completa desde src/main/java/]
- [Método o clase específica donde trabajar]

**🎯 PARA QUÉ:** 
[Explicación del CONTEXTO REAL de Forestech: qué problema resuelve, por qué es necesario, qué pasaría sin esto]

**🔗 CONEXIÓN FUTURA:**
- En Fase X, [cómo se usará más adelante]
- En Fase Y, [cómo evolucionará]
- En Fase Z, [dónde se integrará]

**Prompts sugeridos:**
```text
"[Pregunta 1 para pedirle a una IA]"
"[Pregunta 2 para pedirle a una IA]"
"[Pregunta 3 para pedirle a una IA]"
```

**Tareas paso a paso:**

1. **[Tarea 1]:**
   - [Indicación específica de QUÉ hacer]
   - [NO escribas código completo, solo DESCRIBE qué hacer]
   - [Ejemplo: "Crear método que reciba parámetros X e Y"]

2. **[Tarea 2]:**
   - [Siguiente paso lógico]
   - [Qué validaciones o consideraciones tener]

[... más tareas ...]

**✅ Resultado esperado:** 
[Qué debe ver el usuario cuando termine este checkpoint]

**💡 [Concepto clave / Tip importante]:**
[Explicación adicional de algo crítico]

**⚠️ [CUIDADO / PELIGRO / RECUERDA]:**
[Advertencia sobre errores comunes]

**🔍 Depuración:** 
[Sugerencia de cómo depurar o verificar]

**⏱️ Tiempo estimado:** [X-Y horas]
```

---

## ❌ LO QUE NO DEBES HACER

1. **NO incluyas código completo** para copiar/pegar
   - ❌ MAL: Mostrar un método completo con toda su implementación
   - ✅ BIEN: "Crea un método que reciba Connection, use try-with-resources, y retorne List<Movement>"

2. **NO uses frases vagas**
   - ❌ MAL: "Implementa la funcionalidad de menú"
   - ✅ BIEN: "En ConsoleMenu.java, crea método start() con while(true) que muestre 6 opciones numeradas y use switch para invocar cada acción"

3. **NO omitas el contexto de Forestech**
   - ❌ MAL: "Una excepción personalizada es útil para manejar errores"
   - ✅ BIEN: "Necesitas InsufficientInventoryException porque cuando el usuario intente una salida sin stock, debes rechazarla con un mensaje claro explicando cuánto falta"

---

## 📁 ARCHIVOS A ACTUALIZAR

Ubicados en `/home/hp/Documents/programajava/roadmaps/`:

1. **FASE_06_UI.md** - Interfaz de Usuario en Consola
2. **FASE_07_ERRORES.md** - Manejo de Errores y Excepciones
3. **FASE_08_AVANZADOS.md** - Conceptos Avanzados de Código
4. **FASE_09_FEATURES.md** - Features Adicionales (Reportes, Config)
5. **FASE_10_DEPLOY.md** - Deploy y Distribución

---

## 📖 EJEMPLO DE REFERENCIA

Revisa la **FASE_05_LOGICA_NEGOCIO.md** (adjunta) como ejemplo PERFECTO del formato a seguir. Observa especialmente:

✅ **Checkpoint 5.1** - Ejemplo perfecto de:
- Sección "📍 DÓNDE" muy específica
- Sección "🎯 PARA QUÉ" con contexto de Forestech
- "🔗 CONEXIÓN FUTURA" que menciona fases específicas
- "Tareas paso a paso" sin código completo
- Múltiples callouts: 💡, ⚠️, 🔍

---

## 🎨 ESTILO Y TONO

- **Usa emojis** para mejorar legibilidad: 📍 🎯 🔗 ✅ ❌ ⚠️ 💡 🔍
- **Sé específico** sobre ubicaciones de archivos
- **Explica el "por qué"** antes del "cómo"
- **Conecta con el proyecto real** (Forestech CLI)
- **Mantén tono didáctico** (esto es para APRENDER, no para desarrollo rápido)

---

## 📝 CONTENIDO ACTUAL DE LAS FASES A ACTUALIZAR

### FASE 6: Interfaz de Usuario en Consola

**Checkpoints existentes:**
- 6.1: Menú principal interactivo
- 6.2: Wizard para movimientos de ENTRADA
- 6.3: Wizard para movimientos de SALIDA

**Conceptos a enfatizar:**
- Bucles while(true) con break controlado
- Manejo de InputMismatchException
- Limpieza de buffer de Scanner
- Wizards paso a paso con validación inmediata
- UX con símbolos ASCII (✅ ⚠️ ❌)

---

### FASE 7: Manejo de Errores y Excepciones

**Checkpoints existentes:**
- 7.1: Excepciones personalizadas
- 7.2: try-catch-finally y try-with-resources
- 7.3: Manejo centralizado en la UI

**Conceptos a enfatizar:**
- Jerarquía de excepciones (checked vs unchecked)
- Cuándo crear excepciones personalizadas
- Importancia de cerrar recursos
- No capturar Exception genérico sin razón
- Logging de errores

**Ejemplos de excepciones de Forestech:**
- InsufficientInventoryException (cuando no hay stock)
- InvalidMovementException (datos inválidos)
- DatabaseConnectionException (problemas de BD)

---

### FASE 8: Conceptos Avanzados

**Checkpoints existentes:**
- 8.1: Interfaces y polimorfismo
- 8.2: Clase base y herencia
- 8.3: Enums para constantes de dominio
- 8.4: API java.time
- 8.5: Streams y expresiones lambda

**Conceptos a enfatizar:**
- Programar contra interfaces (IMovementService)
- Evitar duplicación con BaseService abstracta
- Enums: MovementType {ENTRADA, SALIDA}, FuelType
- LocalDateTime en vez de String para fechas
- Streams: filter, map, reduce aplicados a movimientos

---

### FASE 9: Features Adicionales

**Checkpoints probables:**
- 9.1: Sistema de reportes
- 9.2: Configuración externa (properties)
- 9.3: Export a CSV/Excel
- 9.4: Logging con SLF4J
- 9.5: Búsqueda y filtros avanzados

**Conceptos a enfatizar:**
- Reportes: por rango de fechas, por vehículo, por combustible
- Properties: credenciales de BD fuera del código
- POI para Excel o OpenCSV
- Logger en vez de System.out.println

---

### FASE 10: Deploy y Distribución

**Checkpoints probables:**
- 10.1: Empaquetar con Maven (JAR ejecutable)
- 10.2: Scripts de inicio (.sh / .bat)
- 10.3: Documentación de usuario
- 10.4: Instalación y configuración inicial
- 10.5: Backup y restauración de BD

**Conceptos a enfatizar:**
- Maven Assembly Plugin para JAR con dependencias
- Script que verifica Java y SQL Server antes de iniciar
- README con instrucciones de instalación
- Script SQL para crear BD inicial

---

## ✅ CHECKLIST DE VALIDACIÓN

Antes de entregar cada fase actualizada, verifica:

- [ ] Cada checkpoint tiene las 3 secciones obligatorias: 📍 DÓNDE, 🎯 PARA QUÉ, 🔗 CONEXIÓN FUTURA
- [ ] NO hay bloques de código completo (solo indicaciones)
- [ ] Los prompts sugeridos son preguntas útiles para pedirle a una IA
- [ ] Las "Tareas paso a paso" son ESPECÍFICAS pero sin código
- [ ] Hay al menos un callout: 💡, ⚠️, o 🔍
- [ ] Se menciona dónde se trabajará (archivo y método exacto)
- [ ] El contexto explica POR QUÉ es necesario en Forestech
- [ ] Hay conexión con fases futuras (o pasadas)
- [ ] El tono es didáctico (para aprender, no para desarrollo rápido)

---

## 🚀 CÓMO PROCEDER

1. **Lee completamente** FASE_05_LOGICA_NEGOCIO.md adjunta como referencia
2. **Toma el contenido actual** de cada fase (6, 7, 8, 9, 10)
3. **Expande cada checkpoint** aplicando el formato mostrado
4. **Revisa el checklist** antes de finalizar cada fase
5. **Mantén consistencia** entre todas las fases

---

## 💬 NOTA IMPORTANTE

El usuario está APRENDIENDO Java desde cero. Este NO es un proyecto de desarrollo rápido. El objetivo es que:
- ✅ Entienda cada concepto antes de avanzar
- ✅ Escriba el código él mismo (no copie/pegue)
- ✅ Vea cómo todo se conecta en el proyecto real (Forestech)
- ✅ Pueda explicar lo que hizo después de hacerlo

Por eso NO incluyas código completo. Solo guías claras de QUÉ hacer y POR QUÉ.

---

## 📤 FORMATO DE ENTREGA

Actualiza cada fase como un archivo separado manteniendo su nombre original:
- FASE_06_UI.md
- FASE_07_ERRORES.md
- FASE_08_AVANZADOS.md
- FASE_09_FEATURES.md
- FASE_10_DEPLOY.md

Usa el tool `insert_edit_into_file` para actualizar cada archivo, reemplazando el contenido actual con el nuevo formato expandido.

---

¡Buena suerte! El resultado debe ser tan detallado y contextualizado como FASE_05_LOGICA_NEGOCIO.md 🚀

