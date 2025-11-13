# 🔧 FASE X: [TÍTULO DEL CONCEPTO PRINCIPAL]

> **"Frase inspiradora o analogía contextualizada con el mundo real"**
> _— Fuente o principio de programación_

---

## 🎯 Objetivos de Aprendizaje

Al finalizar este archivo, serás capaz de:

- [ ] **[Verbo de acción]** [Concepto 1] (ej: Explicar la diferencia entre clase y objeto)
- [ ] **[Verbo de acción]** [Concepto 2] (ej: Implementar constructores con parámetros)
- [ ] **[Verbo de acción]** [Concepto 3] (ej: Utilizar getters y setters con validación)
- [ ] **[Verbo de acción]** [Concepto 4] (ej: Sobrescribir toString() para debugging)
- [ ] **[Verbo de acción]** [Concepto 5] (ej: Diferenciar atributos final vs mutables)
- [ ] **[Verbo de acción]** [Habilidad práctica] (ej: Crear 3 clases completas del modelo Forestech)

**Verbos recomendados:** Explicar, Diferenciar, Implementar, Utilizar, Aplicar, Identificar, Depurar, Decidir, Diseñar

---

## 📚 Requisitos Previos

Antes de empezar, asegúrate de haber completado:

- ✅ **FASE_X-1_[NOMBRE].md** → [Concepto prerequisito clave]
- ✅ **FASE_X-2_[NOMBRE].md** → [Otro concepto prerequisito]

**Conceptos que debes saber:**
- Concepto 1 (ej: Variables y tipos de datos)
- Concepto 2 (ej: Métodos con parámetros)
- Concepto 3 (ej: Condicionales if/else)

**Herramientas requeridas:**
- Java 17 instalado
- Maven configurado
- IDE (IntelliJ IDEA / VSCode / Eclipse)
- [Otra herramienta específica de la fase]

---

## 🧠 Concepto Clave (Active Recall)

Antes de leer la teoría, responde mentalmente estas preguntas:

### ❓ Pregunta 1: [Pregunta provocadora sobre el problema que resuelve el concepto]

[Descripción de un escenario problemático sin el concepto]

**Ejemplo:**
```
Imagina que tienes 100 vehículos en Forestech y necesitas almacenar:
- Nombre, placa, capacidad, tipo de combustible

¿Cómo lo harías SIN clases?
```

_(Piensa 30 segundos antes de continuar)_

<details>
<summary>💭 Espacio para tu respuesta (haz clic para ver la respuesta correcta)</summary>

**Respuesta SIN clases (arrays separados):**
```java
String[] nombres = new String[100];
String[] placas = new String[100];
double[] capacidades = new double[100];
String[] combustibles = new String[100];

// ❌ Problema: Si añades un vehículo, debes actualizar 4 arrays
nombres[0] = "Excavadora 1";
placas[0] = "ABC-123";
capacidades[0] = 250.0;
combustibles[0] = "Diesel";
```

**Respuesta CON clases:**
```java
class Vehicle {
    String nombre;
    String placa;
    double capacidad;
    String combustible;
}

Vehicle[] vehiculos = new Vehicle[100];
vehiculos[0] = new Vehicle("Excavadora 1", "ABC-123", 250.0, "Diesel");
// ✅ Ventaja: Toda la información está agrupada
```

</details>

---

### ❓ Pregunta 2: [Comparación con la vida real]

**¿Cuál de estas situaciones es análoga a [concepto de esta fase]?**

A) [Opción A]
B) [Opción B - la correcta]
C) [Opción C]
D) [Opción D]

<details>
<summary>💭 Espacio para tu respuesta (haz clic para ver la respuesta correcta)</summary>

**Respuesta correcta: B) [Opción B]**

**Razón:**
- [Explicación de por qué la analogía es correcta]
- [Conexión con el concepto de programación]

**Por qué las otras NO son válidas:**
- A) [Por qué no aplica]
- C) [Por qué no aplica]
- D) [Por qué no aplica]

</details>

---

## 🗂️ Archivos que Crearás/Modificarás

| Archivo | Acción | Descripción |
|---------|--------|-------------|
| `ruta/archivo1.java` | **CREAR** | [Qué hace este archivo] |
| `ruta/archivo2.java` | **MODIFICAR** | [Qué cambiarás en este archivo] |
| `ruta/archivo3.java` | **LEER** | [Qué aprenderás de este archivo] |

**Estructura del proyecto al terminar esta fase:**
```
com.forestech/
├─ models/
│  ├─ [Archivo1.java]  ← NUEVO
│  └─ [Archivo2.java]  ← MODIFICADO
└─ Main.java           ← Testing
```

---

## 1️⃣ INTRODUCCIÓN (10%)

### 🎯 ¿Qué vas a aprender?

En esta fase, dominarás:

- 📘 **[Concepto 1]**: [Explicación de 1 línea]
- 📗 **[Concepto 2]**: [Explicación de 1 línea]
- 📙 **[Concepto 3]**: [Explicación de 1 línea]
- 📕 **[Concepto 4]**: [Explicación de 1 línea]

**Aplicación en Forestech:**
[1-2 párrafos explicando cómo estos conceptos se usan en el proyecto real]

### 🏢 Analogía: [Título de la analogía]

**Imagina tu aplicación como [analogía del mundo real]:**

| Componente del código | Rol en [analogía] | Responsabilidad |
|----------------------|-------------------|-----------------|
| **[Concepto A]** | [Rol A] | [Qué hace] |
| **[Concepto B]** | [Rol B] | [Qué hace] |
| **[Concepto C]** | [Rol C] | [Qué hace] |

**Ejemplo concreto:**
[Escenario narrativo aplicando la analogía paso a paso]

```
ESCENARIO: [Descripción del escenario]
│
├─ Paso 1: [Concepto A] hace [acción]
├─ Paso 2: [Concepto B] hace [acción]
└─ Paso 3: [Concepto C] hace [acción]
```

### 👀 Vista Previa del Resultado

**Al final de esta fase, podrás hacer esto:**

```java
// Código funcional final de la fase
// Este es el código REAL que estará en el proyecto

public class [Clase] {
    // Ejemplo del código que escribirás
}
```

**Salida esperada:**
```
[Output del programa]
```

---

## 2️⃣ CONCEPTOS FUNDAMENTALES (20%)

### 📦 Concepto 1: [Nombre del Concepto]

#### ¿Qué es?

[Explicación simple en 2-3 párrafos, sin jerga técnica]

**Definición formal:**
> [Definición académica entre comillas]

**Definición simple:**
[La misma definición explicada como si fuera para un niño de 10 años]

#### ¿Para qué sirve en Forestech?

[Ejemplo concreto aplicado al dominio de combustibles/vehículos]

**Escenario real:**
```
Sin [concepto]: [Consecuencia negativa]
Con [concepto]: [Beneficio positivo]
```

#### Sintaxis Básica

```java
// Estructura mínima del concepto
[sintaxis esquelética con placeholders]
```

**Explicación de cada parte:**
```java
[parte 1]  // ¿Qué hace? → [explicación]
[parte 2]  // ¿Por qué es necesario? → [explicación]
[parte 3]  // ¿Qué pasa si lo omites? → [explicación]
```

#### Comparación Visual (Diagrama ASCII)

```
┌─────────────────────────────────────────┐
│  [Título del diagrama]                  │
├─────────────────────────────────────────┤
│  [Representación visual del concepto]   │
│                                         │
│  [Flechas y relaciones]                 │
└─────────────────────────────────────────┘
```

#### Errores Comunes (Anti-patrones)

| ❌ MAL (Anti-patrón) | ✅ BIEN (Buena práctica) |
|---------------------|-------------------------|
| ```java<br>// Código incorrecto<br>``` | ```java<br>// Código correcto<br>``` |
| **Problema:** [Por qué está mal] | **Ventaja:** [Por qué está bien] |

---

### 📦 Concepto 2: [Nombre del Concepto]

[Repetir la misma estructura para cada concepto fundamental de la fase]

---

## 3️⃣ IMPLEMENTACIÓN PASO A PASO (60%)

### ✅ Checkpoint X.1: [Nombre del Checkpoint]

**⏱️ Tiempo estimado:** [X minutos / X horas]
**📍 Archivo:** `ruta/archivo.java`
**🎯 Objetivo:** [Qué lograrás al completar este checkpoint]

#### 🎨 Diagrama de Flujo (Opcional)

```
PROCESO: [Nombre del proceso]
│
├─ Paso 1: [Acción]
│  └─ Resultado: [Qué se obtiene]
│
├─ Paso 2: [Acción]
│  └─ Resultado: [Qué se obtiene]
│
└─ Paso 3: [Acción]
   └─ Resultado: [Qué se obtiene]
```

---

#### 🔧 Paso 1: [Nombre del Paso]

**📋 Qué harás:**
[Descripción clara de la acción a realizar]

**🤔 ¿Por qué este paso?**
[Explicación de la razón pedagógica o técnica]

**Código a escribir:**

```java
// Archivo: ruta/archivo.java
// Líneas: X-Y (aproximado)

package com.forestech.models;

public class [Clase] {
    // TODO: Completa esta parte
    // PISTA: Debes [explicación detallada de qué hacer]

    // Ejemplo de estructura esperada:
    [código skeleton]
}
```

**💡 Explicación línea por línea:**

```java
// Línea X:
[código de la línea]
// ¿Qué hace? → [explicación]
// ¿Por qué es necesario? → [justificación]
// ¿Qué pasa si lo cambias? → [consecuencia]

// Línea X+1:
[código de la línea]
// ...
```

**🚨 Errores que podrías encontrar:**

| Error | Causa | Solución |
|-------|-------|----------|
| `[Mensaje de error]` | [Por qué ocurre] | [Cómo solucionarlo paso a paso] |

---

#### 🔧 Paso 2: [Nombre del Paso]

[Repetir la misma estructura para cada paso del checkpoint]

---

#### 🧪 Testing del Checkpoint

**Crea este archivo de prueba:**

```java
// Archivo: Main.java (temporal para testing)

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST CHECKPOINT X.1 ===\n");

        // Test 1: [Descripción]
        [código de test]

        // Test 2: [Descripción]
        [código de test]
    }
}
```

**Salida esperada:**

```
=== TEST CHECKPOINT X.1 ===

Test 1: [Descripción]
✅ [Resultado esperado]

Test 2: [Descripción]
✅ [Resultado esperado]
```

**🔍 Verificación manual:**

Además de ejecutar el código, verifica:
- [ ] El código compila sin errores (`mvn clean compile`)
- [ ] [Criterio de verificación 1]
- [ ] [Criterio de verificación 2]
- [ ] [Criterio de verificación 3]

---

#### ✅ Checkpoint X.1 completado cuando:

- [ ] El código compila sin errores
- [ ] Los tests muestran la salida esperada
- [ ] Entiendes [concepto clave relacionado]
- [ ] Puedes explicar [aspecto importante] con tus propias palabras
- [ ] Completaste los TODOs del código

**🔗 Conexión con siguiente checkpoint:**
En X.2 aprenderás [teaser del próximo checkpoint]

---

### ✅ Checkpoint X.2: [Nombre del Siguiente Checkpoint]

[Repetir la misma estructura para cada checkpoint de la fase]

---

## 4️⃣ EJERCICIOS PRÁCTICOS (10%)

> **Instrucciones generales:**
> - Resuelve cada ejercicio sin mirar la solución
> - Los ejercicios están ordenados por dificultad creciente
> - Puedes usar la documentación oficial de Java
> - Compila y prueba tu código antes de ver la solución

---

### Ejercicio 1: [Nombre del Ejercicio] - ⭐ Fácil

**🎯 Objetivo:** [Qué concepto practicar]

**📋 Tarea:**
[Instrucción específica y clara]

**Ejemplo de entrada/salida:**
```
Entrada: [datos de entrada]
Salida esperada: [resultado esperado]
```

**Pistas:**
1. [Pista 1 - muy sutil]
2. [Pista 2 - un poco más directa]
3. [Pista 3 - casi te da la respuesta]

**Solución:**

<details>
<summary>Ver solución (intenta resolverlo primero)</summary>

```java
// Solución completa y probada

public class Solucion {
    [código de la solución]
}
```

**Explicación:**
[Por qué esta es la solución correcta]

**Conceptos aplicados:**
- [Concepto 1]
- [Concepto 2]

</details>

---

### Ejercicio 2: [Nombre del Ejercicio] - ⭐⭐ Medio

[Misma estructura que Ejercicio 1, pero más complejo]

---

### Ejercicio 3: [Nombre del Ejercicio] - ⭐⭐⭐ Difícil

[Misma estructura que Ejercicio 1, pero aún más complejo]

---

### 🎖️ Ejercicio Bonus: [Nombre] - ⭐⭐⭐⭐ Experto

**🎯 Objetivo:** [Integrar múltiples conceptos de la fase]

**📋 Tarea:**
[Proyecto mini que integre todo lo aprendido]

**Restricciones:**
- No usar [concepto de fase futura]
- Debe compilar con Java 17
- [Otra restricción]

**Este ejercicio es opcional, pero altamente recomendado.**

---

## 5️⃣ CONSOLIDACIÓN Y CIERRE (5%)

### ✅ Checklist de Salida

Antes de pasar a la siguiente fase, asegúrate de:

**Verificación técnica:**
- [ ] Todo el código compila sin errores (`mvn clean compile`)
- [ ] Todos los tests del checkpoint pasan
- [ ] No hay warnings en el IDE
- [ ] Los archivos están organizados en los paquetes correctos

**Verificación conceptual:**
- [ ] Puedo explicar [concepto clave 1] con mis propias palabras
- [ ] Puedo explicar [concepto clave 2] con mis propias palabras
- [ ] Entiendo la diferencia entre [concepto A] y [concepto B]
- [ ] Sé cuándo usar [técnica X] vs [técnica Y]

**Verificación práctica:**
- [ ] Completé al menos 2 de los 3 ejercicios prácticos
- [ ] Documenté el código con comentarios en español
- [ ] Probé casos edge (valores nulos, negativos, etc.)

**Auto-evaluación (1-5):**
- [ ] Comprensión teórica: ⭐⭐⭐⭐⭐
- [ ] Habilidad práctica: ⭐⭐⭐⭐⭐
- [ ] Confianza para avanzar: ⭐⭐⭐⭐⭐

---

### 🐛 Errores Comunes y Soluciones

| Error | Causa Raíz | Solución Paso a Paso |
|-------|-----------|----------------------|
| `NullPointerException` | Variable no inicializada | 1. Identificar qué variable es null<br>2. Inicializar antes de usar<br>3. Agregar validación `if (var != null)` |
| `[Mensaje de error específico de la fase]` | [Causa técnica] | [Solución detallada] |
| `[Otro error común]` | [Causa] | [Solución] |

**Debugging Tips:**
- ✅ Usa `System.out.println()` para ver valores de variables
- ✅ Lee el stack trace de abajo hacia arriba
- ✅ Compila después de cada cambio pequeño
- ✅ Busca el error en Google con comillas: `"mensaje exacto del error"`

---

### 📚 Recursos Adicionales

**Documentación oficial:**
- [Sección específica de Java Docs](https://docs.oracle.com/javase/17/docs/...)
- [Tutorial de Oracle sobre el tema](https://docs.oracle.com/javase/tutorial/...)

**Videos recomendados (opcional):**
- [Título del video] - [Canal] - [Duración]

**Artículos complementarios:**
- [Título del artículo] - [Sitio web]

**Conceptos relacionados (para explorar después):**
- [Concepto avanzado 1] (se verá en Fase X+2)
- [Concepto avanzado 2] (se verá en Fase X+3)

---

### 🔗 Próxima Fase

**En la Fase X+1 aprenderás:**

📘 [Concepto 1 de la siguiente fase]
📗 [Concepto 2 de la siguiente fase]
📙 [Concepto 3 de la siguiente fase]

**Vista previa (teaser):**
```java
// Adelanto del código de la siguiente fase
[snippet de código interesante que genere curiosidad]
```

**Conexión directa:**
Los [conceptos de esta fase] son prerequisitos para entender [concepto de la siguiente fase], específicamente porque [explicación breve].

---

### 💾 Commit Sugerido

Si estás usando Git para seguir tu progreso:

```bash
# Verificar archivos modificados
git status

# Agregar archivos de esta fase
git add src/main/java/com/forestech/[archivos relevantes]

# Commit con mensaje descriptivo
git commit -m "Fase X: [descripción breve de los checkpoints completados]

- Checkpoint X.1: [descripción]
- Checkpoint X.2: [descripción]
- Ejercicios 1-3 completados"

# (Opcional) Crear tag para marcar el progreso
git tag -a fase-X -m "Completada Fase X: [Título]"
```

**Estructura de mensaje de commit recomendada:**
```
Fase X: [Título breve]

Checkpoints completados:
- X.1: [Nombre]
- X.2: [Nombre]

Conceptos aprendidos:
- [Concepto 1]
- [Concepto 2]

Archivos creados/modificados:
- ruta/archivo1.java
- ruta/archivo2.java
```

---

## 📝 Notas del Instructor (Metadata)

> **Esta sección es para instructores/mantenedores del roadmap, no para estudiantes**

**Duración estimada:** X semanas (basado en 10 horas/semana de estudio)
**Dificultad:** ⭐⭐⭐ (1-5 estrellas)
**Conceptos prerequisito:**
- [Concepto 1]
- [Concepto 2]

**Conceptos enseñados (taxonomía de Bloom):**
- **Recordar:** [Conceptos que deben memorizar]
- **Comprender:** [Conceptos que deben explicar]
- **Aplicar:** [Habilidades que deben ejecutar]
- **Analizar:** [Capacidades de debugging/comparación]
- **Evaluar:** [Decisiones de diseño que deben tomar]
- **Crear:** [Código que deben escribir desde cero]

**Archivos del proyecto involucrados:**
- `ruta/archivo1.java:X-Y` (lectura)
- `ruta/archivo2.java:X-Y` (modificación)
- `ruta/archivo3.java` (creación completa)

**Tests requeridos:**
- Test en `Main.java` (checkpoint X.1)
- Test en `Main.java` (checkpoint X.2)

**Dependencias de fases:**
- **Requiere:** Fase X-1 (conceptos A, B, C)
- **Prerequisito de:** Fase X+1 (usará conceptos D, E, F)

**Notas pedagógicas:**
- [Concepto X] suele ser difícil, enfatizar analogía en sección 2
- Los estudiantes suelen confundir [A] con [B], agregar tabla comparativa
- Ejercicio 3 es intencionalmente desafiante para consolidar

**Historial de cambios:**
- 2025-01-13: Versión inicial (basada en código real del proyecto)
- [Fecha]: [Cambios realizados]

---

## 🎓 Créditos

**Roadmap creado por:** [Autor]
**Basado en código de:** Forestech CLI
**Metodología:** Código primero, documentación después
**Última actualización:** [Fecha]

---

**¡Felicidades por completar la Fase X!**

Estás listo para avanzar a la **[Nombre de la siguiente fase]**.
