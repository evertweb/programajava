# 🎯 FASE 0: PREPARACIÓN (Semana 0)

> Objetivo general: tener el entorno listo, adquirir hábitos de trabajo y arrancar con buena higiene técnica.

---

## 🧭 Checkpoint 0.0: Rutina de Arranque y Troubleshooting

**Por qué importa:** evitar bloqueos técnicos y generar disciplina diaria desde el primer día.

- 🧪 Ejercicio diario (10 min): mini-kata de lógica en Java (FizzBuzz, números primos, etc.) ejecutada en el entorno real.
- 🔁 Ritual Git rápido cada sesión:
  1. `git status`
  2. `git pull`
  3. Trabaja en una rama de práctica (`git checkout -b learning/yyyymmdd`)
  4. `git add` + `git commit -m "kata fizzbuzz"`
- 🩺 Checklist de troubleshooting antes de pedir ayuda:
  1. ¿El JDK está bien configurado? `java -version`
  2. ¿Se ejecuta un "Hello World" desde terminal e IDE?
  3. ¿Dependencias descargadas (`mvn -v`)?
  4. ¿Hay mensajes de error exactos guardados para compartir?

👉 Documenta en `JAVA_LEARNING_LOG.md` cualquier incidente y su solución.

---

## ✅ Checkpoint 0.1: Verificar Entorno

- [x] JDK instalado (Java 17 o 21)
- [x] IntelliJ IDEA configurado
- [x] VS Code con extensiones Java
- [x] Maven instalado
- [x] Git configurado

**Conceptos a repasar:**

- ¿Qué es JDK vs JRE vs JVM?
- ¿Para qué sirven las variables de entorno JAVA_HOME y MAVEN_HOME?
- Flujo básico: editar → compilar (`javac`) → ejecutar (`java`).

**Tiempo estimado:** 1-2 horas

---

## ✅ Checkpoint 0.2: Crear Proyecto Base

- [x] Estructura de carpetas creada
- [x] `pom.xml` configurado (Maven)
- [x] SQL Server JDBC driver agregado
- [x] Primera ejecución exitosa de "Hello World"

**Preguntas para tu IA:**

```text
"Repasemos: ¿qué hace Maven por mí y dónde guarda las dependencias?"
"Explícame con una metáfora qué es el bytecode (.class)."
```

**Tiempo estimado:** 1-2 días

---

## 🧠 Hábitos de Calidad desde Fase 0

- 🕒 Agenda bloques cortos (Pomodoro 25/5) y deja registro de lo logrado.
- 🧭 Cierra cada sesión con mini retrospectiva: ¿qué aprendí?, ¿qué quiero preguntar mañana?, ¿qué quedó bloqueado?
- 🔐 Empieza a separar credenciales en variables de entorno (`export DB_USER=...`).
- 🐞 Primeros pasos de depuración: coloca un breakpoint en IntelliJ, ejecuta el "Hello World" en modo debug y observa variables.

> **Checklist antes de pasar a Fase 1:**
>
> - Puedo compilar y ejecutar desde terminal e IDE sin errores.
> - Tengo un repositorio limpio y sé hacer commit/push.
> - Documenté en el log cualquier incidente de instalación.
> - Probé al menos un breakpoint en el IDE.
