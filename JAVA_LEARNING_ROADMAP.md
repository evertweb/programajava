# 🎓 JAVA LEARNING ROADMAP - Forestech CLI Project

> **Objetivo:** aprender Java construyendo un CLI real conectado a SQL Server, con hábitos profesionales desde el primer día.

---

## 📚 Filosofía de aprendizaje

- **Aprender haciendo:** cada concepto se practica con código y pruebas reales.
- **Progresión incremental:** avances pequeños y comprobables en cada sesión.
- **Entender, no copiar:** la IA es tu tutor; explica antes de escribir.
- **Retroalimentación constante:** tests manuales, quizzes y retrospectivas breves.
- **Calidad primero:** depuración, control de versiones y documentación acompañan cada entrega.

---

## 🧭 Cómo navegar el roadmap

1. El roadmap está dividido por fases en la carpeta `roadmaps/`.
2. Antes de empezar una fase, lee su archivo y revisa recursos sugeridos.
3. Trabaja checkpoint por checkpoint; cada uno incluye prompts, tareas, refuerzos y checklist.
4. Registra avances, dudas y hallazgos de debugging en `JAVA_LEARNING_LOG.md`.
5. Al cerrar una fase, vuelve a este índice y elige la siguiente.

### 📂 Archivos por fase

- Fase 0 – Preparación: `roadmaps/FASE_00_PREPARACION.md`
- Fase 1 – Fundamentos: `roadmaps/FASE_01_FUNDAMENTOS.md`
- Fase 2 – POO: `roadmaps/FASE_02_POO.md`
- Fase 3 – Conexión SQL: `roadmaps/FASE_03_SQL.md`
- Fase 4 – CRUD: `roadmaps/FASE_04_CRUD.md`
- Fase 5 – Lógica de negocio y transacciones: `roadmaps/FASE_05_LOGICA_NEGOCIO.md`
- Fase 6 – Interfaz de usuario CLI: `roadmaps/FASE_06_UI.md`
- Fase 7 – Manejo de errores: `roadmaps/FASE_07_ERRORES.md`
- Fase 8 – Conceptos avanzados: `roadmaps/FASE_08_AVANZADOS.md`
- Fase 9 – Features adicionales y operabilidad: `roadmaps/FASE_09_FEATURES.md`
- Fase 10 – Optimización y deployment: `roadmaps/FASE_10_DEPLOY.md`

Cada archivo mantiene la estructura interactiva (tareas, prompts, ejercicios, quizzes) e incorpora las nuevas sugerencias: hábitos de debugging, diagramas previos, configuración externa, logging, testing y empaquetado.

---

## 🤖 Cómo trabajar con tu agente IA

1. **Entiende primero:** usa los prompts de la fase antes de escribir código.
2. **Diseña antes de codificar:** crea diagramas, esquemas o listas de pasos.
3. **Itera con feedback:** comparte tu código, pide revisión y solicita quizzes.
4. **Escala las preguntas:** si algo se complica, divídelo en subtemas.

**Prompts útiles en cualquier fase:**

- "Explícame [concepto] con una analogía práctica."
- "Guíame paso a paso para [tarea], sin darme el código completo."
- "Hazme un quiz corto para validar que entendí [tema]."
- "Analiza este error y dime qué está pasando: [stacktrace]."

---

## 🛠️ Hábitos transversales

- **Depuración temprana:** breakpoints, `Evaluate Expression` y seguimiento de variables en cada checkpoint.
- **Control de versiones disciplinado:** cierra cada checkpoint con `git status → add → commit → push`.
- **Registro de aprendizaje:** documenta decisiones, errores y soluciones en el log.
- **Retrospectiva rápida:** al finalizar la sesión responde “¿qué aprendí?”, “¿qué falta?” y “¿qué haré mañana?”.
- **Seguridad desde el inicio:** mantén credenciales fuera del código (`config.properties`, variables de entorno).

---

## 🗺️ Vista rápida de fases

| Semana | Fase | Objetivo clave |
|---|---|---|
| 0 | Preparación | Entorno listo, rituales de Git y troubleshooting |
| 1-2 | Fundamentos | Sintaxis base, control de flujo, Scanner |
| 3-4 | POO | Modelado del dominio Forestech con clases y colecciones |
| 5 | SQL | Conexión JDBC y primeras consultas |
| 6-7 | CRUD | Insertar, consultar, actualizar y eliminar con validaciones |
| 8-9 | Lógica | Transacciones, inventario, reglas de negocio |
| 10 | CLI | Menú interactivo profesional y wizards |
| 11 | Errores | Excepciones personalizadas y manejo robusto |
| 12+ | Avanzados | Interfaces, herencia, enums, Streams, java.time |
| 13+ | Features | Reportes, búsquedas, configuración externa, pruebas |
| 14+ | Deployment | Pooling, empaquetado, scripts, pipeline |

---

## 🧾 Recursos de apoyo

- Documentación: Oracle Java Tutorials, Java API 21, JDBC Tutorial.
- Herramientas: IntelliJ IDEA, Maven, Git, Azure Data Studio / SSMS.
- Comunidad: Stack Overflow (`java`), Reddit r/learnjava, Discord Java Programming.

---

## ✅ Próximos pasos sugeridos

1. Lee `roadmaps/FASE_00_PREPARACION.md` y completa el checklist de entorno.
2. Revisa `JAVA_PROJECT_SETUP.md` y `JAVA_AGENTS_INSTRUCTIONS.md` para alinear instrucciones.
3. Abre `JAVA_NEXT_STEPS.md` para conocer la tarea inmediata.

Recuerda: el progreso constante y documentado vale más que terminar rápido. ¡Vamos paso a paso! 🚀
