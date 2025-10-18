# 📝 Resumen de Cambios en FASE_01_FUNDAMENTOS.md

## ✅ Lo que YA corregí

### Checkpoint 1.3: Validaciones y Operadores Lógicos
**ANTES:** Código completo listo para copiar/pegar  
**AHORA:** Diagrama de árbol con:
- Qué parámetros debe recibir cada método
- Qué condiciones debe validar
- Qué tipo de retorno debe tener
- Casos de prueba para validar

### Checkpoint 1.4: Control de Flujo y Menú Básico
**ANTES:** Código completo listo para copiar/pegar  
**AHORA:** Diagrama de árbol con:
- Estructura de cada método (modificador, retorno, parámetros)
- Qué debe imprimir cada caso del switch
- Pistas sobre cuándo usar switch vs if-else
- Explicación del problema del `break`

---

## ⚠️ Lo que AÚN falta corregir

### Checkpoints 1.5 y 1.6
Estos checkpoints TODAVÍA tienen el código completo. Deben ser convertidos al nuevo formato.

---

## 🎯 Formato correcto: Instrucciones tipo "Diagrama"

### ❌ MAL (código completo):
```java
public static void displayMainMenu() {
    System.out.println("\n========== MENÚ PRINCIPAL ==========");
    System.out.println("1. Registrar Entrada de Combustible");
    System.out.println("2. Registrar Salida de Combustible");
}
```

### ✅ BIEN (diagrama de instrucciones):
```
MenuHelper.java
│
├── Método 1: displayMainMenu
│   ├── Modificador: public static
│   ├── Tipo de retorno: void
│   ├── Parámetros: ninguno
│   │
│   └── Debe imprimir:
│       ┌─────────────────────────────────────┐
│       │   ========== MENÚ PRINCIPAL ==========   │
│       │   1. Registrar Entrada de Combustible    │
│       │   2. Registrar Salida de Combustible     │
│       └─────────────────────────────────────┘
```

---

## 📚 Diferencia entre Clase, Método y Función

### 🏗️ **CLASE**
**Qué es:** Un plano o molde para crear objetos.

**Analogía:** El plano arquitectónico de una casa 🏠

**Ejemplo:**
```java
public class Casa {
    // Características (atributos)
    String color;
    int habitaciones;
    
    // Comportamientos (métodos)
    void abrirPuerta() { ... }
    void encenderLuz() { ... }
}
```

**En Forestech:**
- `Movement` es una clase (representa un movimiento de combustible)
- `MenuHelper` es una clase (representa funcionalidad del menú)

---

### 🔧 **MÉTODO**
**Qué es:** Una acción o comportamiento que pertenece a una clase.

**Analogía:** Las acciones que puede hacer un objeto (una casa puede "abrirPuerta")

**Ejemplo:**
```java
public void calcularTotal(double cantidad, double precio) {
    return cantidad * precio;
}
```

**Características:**
- SIEMPRE está dentro de una clase (en Java TODO es clase)
- Puede ser `static` (no necesita objeto) o de instancia (necesita objeto)
- Tiene modificador de acceso: `public`, `private`, `protected`

---

### 📦 **FUNCIÓN**
**Qué es:** En otros lenguajes (JavaScript, Python, C), código reutilizable que NO necesita estar en una clase.

**⚠️ EN JAVA NO EXISTEN FUNCIONES**, solo métodos.

**Comparación:**

| Lenguaje   | Concepto                     | Ejemplo                          |
|------------|------------------------------|----------------------------------|
| JavaScript | Función independiente        | `function sumar(a, b) { ... }`   |
| Python     | Función independiente        | `def sumar(a, b): ...`           |
| Java       | Método (siempre en clase)    | `public int sumar(int a, int b)` |

**Por costumbre:** Mucha gente dice "función" en Java, pero técnicamente son **métodos**.

---

## 🎓 Resumen para recordar

```
En Java:
├── CLASE = molde/plano (ej: MenuHelper, Movement)
├── MÉTODO = acción dentro de una clase (ej: displayMainMenu(), calcularTotal())
└── FUNCIÓN = NO EXISTE en Java (es concepto de otros lenguajes)
```

**Ejemplo completo:**
```java
// CLASE
public class MovementCalculator {
    
    // MÉTODO estático (no necesita objeto)
    public static double calculateSubtotal(double qty, double price) {
        return qty * price;
    }
    
    // MÉTODO estático
    public static void printSummary(String type) {
        System.out.println("Tipo: " + type);
    }
}

// Uso:
MovementCalculator.calculateSubtotal(100, 3.45);  // Llamar método de la clase
```

---

## 📋 Próximos pasos

1. **Checkpoint 1.5 (Bucles y Arrays)** debe ser reescrito con:
   - Diagrama de cada método (showFuelTypes, showMenuWithForEach, simulateProcessing)
   - Tabla comparativa: for vs for-each vs while vs do-while
   - Casos de prueba

2. **Checkpoint 1.6 (Scanner)** debe ser reescrito con:
   - Diagrama de cada método (readInt, readDouble, readString, readFuelType)
   - Explicación del problema del buffer (sin mostrar código completo)
   - Flujo de prueba en Main.java (describir, no dar código)

---

## 🎯 Principio fundamental

> **"No des el pescado, enseña a pescar"**

El roadmap debe:
- ✅ Decir QUÉ debe hacer el código
- ✅ Explicar POR QUÉ debe hacerlo así
- ✅ Dar pistas de CÓMO hacerlo
- ❌ NO dar el código completo listo para copiar

**Razón:** El usuario aprende más cuando:
1. Lee lo que debe hacer
2. Intenta escribirlo
3. Comete errores
4. Los corrige con ayuda de Copilot
5. Entiende por qué funciona

