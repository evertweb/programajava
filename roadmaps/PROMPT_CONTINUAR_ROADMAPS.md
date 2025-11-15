# PROMPT REUTILIZABLE: CONTINUAR ROADMAPS RETROSPECTIVOS

**Copia este prompt completo en futuras sesiones de Claude Code para continuar con las fases pendientes**

---

## Prompt para Claude Code

```
Estoy continuando con la creación de roadmaps retrospectivos para mi proyecto ForestechOil.

# CONTEXTO DEL PROYECTO

ForestechOil es un sistema CLI en Java 17 para gestión de inventario de combustibles. El proyecto está COMPLETAMENTE FUNCIONAL en Fase 7 (excepciones implementadas), pero los roadmaps de aprendizaje están incompletos.

**Estado actual de documentación:**
- ✅ FASE_01_FUNDAMENTOS.md (completado)
- ✅ FASE_02_POO_MODELOS.md (completado)
- ✅ FASE_03_MANAGERS_COLECCIONES.md (completado)
- ⚠️ FASE_04 a FASE_10 (pendientes)

# TU TAREA

Consulta el archivo `roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md` para:

1. Ver el estado actual de progreso
2. Identificar la siguiente fase a documentar
3. Revisar el formato estándar obligatorio
4. Consultar los archivos a analizar
5. Conocer los bugs conocidos y ejercicios sugeridos

# FORMATO OBLIGATORIO

Cada roadmap debe seguir esta estructura (NO omitir secciones):

```markdown
# FASE XX: TÍTULO DE LA FASE
**Roadmap Retrospectivo - Análisis del Código Existente**

## Contexto de esta Fase
[Explicar QUÉ se implementó, relacionar con fases anteriores/siguientes]

## Objetivos de Aprendizaje
[Lista numerada de conceptos]

## Arquitectura de la Fase
[Diagrama ASCII + ubicación de archivos]

## [Secciones por Archivo Analizado]
### Archivo: `ruta/Clase.java`

#### X.1 Concepto: [Explicación teórica]
#### X.2 Análisis del Código
[Mostrar código con líneas específicas]

#### X.3 Análisis Línea por Línea
[Explicar QUÉ hace cada línea importante]

#### X.4 Uso en el Proyecto
[Referencias reales a otros archivos]

#### X.5 Checkpoint de Verificación ✅
[Preguntas de comprensión]

## Bugs y Mejoras Identificados
[Tabla con bugs, líneas, severidad, corrección]

## Ejercicios de Corrección/Mejora 🔧
[Ejercicios prácticos con código]

## Resumen de la Fase
[Tabla de conceptos + diagramas]

## Ejercicio Final de la Fase 🎯
[Tarea completa paso a paso]

## Autoevaluación ✅
[Preguntas finales]

## Próximos Pasos
[Enlace a siguiente fase]
```

# REQUISITOS CRÍTICOS

1. **SIEMPRE leer primero** `roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md`
2. **Referencias exactas:** Usar formato `Archivo.java:línea`
3. **Análisis línea por línea:** Explicar QUÉ hace cada línea clave
4. **Conceptos antes de código:** Teoría → Código → Ejemplos
5. **Contexto ForestechOil:** Usar el proyecto real en ejemplos
6. **Identificar bugs:** Marcar con 🔴 y proponer correcciones
7. **Ejercicios ejecutables:** Código que se pueda compilar/probar
8. **Checkpoints frecuentes:** Cada 2-3 secciones

# FLUJO DE TRABAJO

1. **Leer guía de seguimiento** (`roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md`)
2. **Identificar fase actual** (siguiente pendiente)
3. **Leer archivos indicados** en la guía para esa fase
4. **Crear roadmap** siguiendo formato estándar
5. **Actualizar guía** (marcar fase como completada)
6. **Commit cambios:**
   ```bash
   git add roadmaps/FASE_XX_TITULO.md roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md
   git commit -m "Docs: Completar FASE_XX - Descripción"
   ```

# EJEMPLO DE INICIO

Si la próxima fase es FASE_04, comenzarías así:

1. Leo `roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md`
2. Identifico: FASE_04 = Conexión JDBC
3. Archivos a analizar: `config/DatabaseConnection.java`
4. Conceptos clave: JDBC, DriverManager, Connection, try-with-resources
5. Bugs conocidos: Credenciales hardcoded
6. Creo: `roadmaps/FASE_04_CONEXION_JDBC.md`
7. Actualizo guía marcando Fase 04 como ✅
8. Commit ambos archivos

# PREGUNTAS PARA MÍ

Antes de empezar, pregúntame:

1. ¿Quieres que continúe con la siguiente fase pendiente automáticamente?
2. ¿O prefieres especificar qué fase quieres documentar ahora?
3. ¿Cuántas fases quieres completar en esta sesión? (recomendado: 1-2)

# NOTAS IMPORTANTES

- El código YA ESTÁ IMPLEMENTADO (no crear código nuevo)
- El objetivo es DOCUMENTAR y EXPLICAR lo que existe
- Enfoque DIDÁCTICO (enseñar, no solo describir)
- Identificar bugs es OBLIGATORIO (ver guía para bugs conocidos)
- Los roadmaps deben ser LARGOS y DETALLADOS (400-650 líneas)

¿Comenzamos con la siguiente fase?
```

---

## Instrucciones de Uso

### Para Continuar en una Nueva Sesión:

1. **Abre Claude Code** en el proyecto `/home/hp/forestechOil`

2. **Copia el prompt completo** desde la sección "Prompt para Claude Code" arriba

3. **Pégalo en Claude Code** tal cual (puedes agregar al final: "Sí, comienza con FASE_04")

4. **Claude Code:**
   - Leerá la guía de seguimiento
   - Identificará la siguiente fase pendiente
   - Leerá los archivos necesarios
   - Creará el roadmap siguiendo el formato
   - Actualizará la guía de seguimiento
   - Te pedirá hacer commit

5. **Después del commit:**
   - Si quieres continuar con otra fase en la misma sesión, di: "Continúa con la siguiente fase"
   - Si quieres parar, di: "Gracias, continúo en otra sesión"

---

## Variaciones del Prompt

### Si Quieres Especificar la Fase

Agrega al final del prompt:
```
En esta sesión, quiero documentar específicamente la FASE_06 (Lógica de Negocio).
```

### Si Quieres Múltiples Fases

Agrega al final:
```
En esta sesión, quiero completar FASE_04 y FASE_07 (las más cortas).
```

### Si Encuentras un Bug Nuevo

Durante la sesión, di:
```
He encontrado un bug adicional en [Archivo.java:línea].
Agrégalo a la sección de bugs del roadmap y a la guía de seguimiento.
```

---

## Checklist Pre-Sesión

Antes de iniciar una nueva sesión con el prompt:

- [ ] Verificar que estás en el directorio correcto: `/home/hp/forestechOil`
- [ ] Revisar `roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md` para ver qué fases faltan
- [ ] Decidir cuántas fases quieres completar (recomendado: 1-2 por sesión)
- [ ] Tener el proyecto compilado: `mvn clean compile`
- [ ] (Opcional) Leer los archivos que se van a documentar para familiarizarte

---

## Checklist Post-Sesión

Después de completar una o más fases:

- [ ] Verificar que el roadmap tiene todas las secciones obligatorias
- [ ] Verificar que la guía de seguimiento fue actualizada
- [ ] Hacer commit de ambos archivos:
  ```bash
  git add roadmaps/FASE_XX_TITULO.md roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md
  git commit -m "Docs: Completar FASE_XX - [Descripción]"
  ```
- [ ] (Opcional) Revisar el roadmap creado para validar calidad
- [ ] (Opcional) Compilar y probar ejercicios del roadmap

---

## Estimación de Tiempo por Sesión

| Fases en Sesión | Tiempo Estimado | Notas |
|-----------------|-----------------|-------|
| 1 fase corta (07) | 45-60 min | Excepciones |
| 1 fase media (04, 03) | 60-90 min | Conexión, Managers |
| 1 fase larga (05, 06, 08) | 90-120 min | Services, CLI |
| 2 fases cortas (04 + 07) | 90-120 min | Combinar fáciles |
| 2 fases medias | 120-180 min | No recomendado |

**Recomendación:** Completar 1-2 fases por sesión para mantener calidad alta.

---

## Progreso Esperado

Con el prompt reutilizable, el progreso esperado es:

**Sesión 1 (Completada):**
- ✅ Fase 01 (60 min)
- ✅ Fase 02 (90 min)
- ✅ Fase 03 (70 min)
- Total: 220 min (~3.5 horas)

**Sesión 2 (Próxima):**
- 🔄 Fase 04 (50 min)
- 🔄 Fase 07 (40 min)
- Total: 90 min (~1.5 horas)

**Sesión 3:**
- 🔄 Fase 05 (90 min)
- Total: 90 min

**Sesión 4:**
- 🔄 Fase 06 (80 min)
- Total: 80 min

**Sesión 5:**
- 🔄 Fase 08 (120 min)
- Total: 120 min

**Sesión 6 (Opcional - Fases Futuras):**
- 🔄 Fase 09 (60 min)
- 🔄 Fase 10 (70 min)
- Total: 130 min

**Total proyecto:** ~9.5 horas distribuidas en 5-6 sesiones

---

## Troubleshooting

### Si Claude Code no encuentra la guía

```
Lee el archivo roadmaps/GUIA_DE_SEGUIMIENTO_ROADMAPS.md completo antes de continuar.
```

### Si el formato no se respeta

```
Detente. El roadmap debe seguir EXACTAMENTE el formato de FASE_01_FUNDAMENTOS.md.
Revisa ese archivo como referencia y comienza de nuevo.
```

### Si falta análisis línea por línea

```
La sección X.3 debe explicar línea por línea QUÉ hace cada línea importante.
Usa el formato: "Línea XX: [código] - [explicación]".
Revisa FASE_02_POO_MODELOS.md como ejemplo.
```

### Si no se identifican bugs

```
Revisa la guía de seguimiento. Hay bugs conocidos para esta fase.
Agrégalos a la sección "Bugs y Mejoras Identificados" con:
- Línea exacta
- Descripción del bug
- Severidad (🔴/🟡/🟢)
- Corrección propuesta
```

---

## Mantenimiento del Prompt

Este prompt debe actualizarse si:

- Cambia el formato estándar de roadmaps
- Se agregan nuevas fases al proyecto
- Se descubren nuevos bugs no documentados
- Cambia la estructura del proyecto

**Última actualización:** 2025-01-14 (v1.0)

---

**🎓 Prompt Reutilizable Creado**

Este prompt está diseñado para ser copiado íntegramente en futuras sesiones. Claude Code leerá la guía de seguimiento, identificará el progreso actual, y continuará con la siguiente fase automáticamente.

**Próximo uso:** Sesión 2 → Copiar prompt → Continuar con Fase 04 (Conexión JDBC)
