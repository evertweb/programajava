# 🎓 REGLAS PARA ACTUALIZAR TODAS LAS FASES - APRENDIZAJE REAL

> **PRINCIPIO FUNDAMENTAL:** Este es un proyecto de APRENDIZAJE, NO de desarrollo rápido. El usuario debe PENSAR, ESCRIBIR y ENTENDER cada línea de código.

---

## 🚨 REGLA CRÍTICA - NO DAR CÓDIGO COMPLETO

### ❌ LO QUE NO SE DEBE HACER:

```markdown
**Tareas paso a paso:**

1. **Crear constructor principal:**
   ```java
   public Movement(String movementType, String fuelType, double quantity, double unitPrice) {
       this.id = UUID.randomUUID().toString();
       this.movementType = movementType;
       this.fuelType = fuelType;
       this.quantity = quantity;
       this.unitPrice = unitPrice;
       this.movementDate = LocalDateTime.now().toString();
   }
   ```

2. **Crear método getTotalValue():**
   ```java
   public double getTotalValue() {
       return this.quantity * this.unitPrice;
   }
   ```
```

**PROBLEMA:** El usuario solo va a copiar y pegar. No aprende NADA.

---

### ✅ LO QUE SÍ SE DEBE HACER:

```markdown
**Tareas paso a paso:**

1. **Crear constructor principal:**
   
   **Firma del método:** Debe recibir 4 parámetros (movementType, fuelType, quantity, unitPrice)
   
   **Dentro del constructor debes:**
   - Generar automáticamente el `id` usando UUID
     - Investiga: `UUID.randomUUID().toString()`
   - Asignar cada parámetro al atributo correspondiente usando `this`
   - Generar automáticamente la fecha actual como String
     - Investiga: `LocalDateTime.now().toString()`
   
   **Pregunta guía:** ¿Por qué necesitas usar `this.quantity = quantity`? ¿Qué pasa si solo escribes `quantity = quantity`?

2. **Crear método getTotalValue():**
   
   **Propósito:** Calcular el valor total del movimiento
   
   **Especificaciones:**
   - Tipo de retorno: double
   - Sin parámetros
   - Fórmula: cantidad × precio unitario
   
   **Pregunta guía:** ¿Por qué no necesitas parámetros si estás multiplicando dos valores?
```

**VENTAJA:** El usuario debe PENSAR, buscar información y ESCRIBIR el código. APRENDE de verdad.

---

## 📋 CASOS DONDE SÍ MOSTRAR CÓDIGO

### ✅ Permitido mostrar código completo:

1. **Primera vez que ve una sintaxis completamente nueva:**
   - Primer constructor (explica la sintaxis)
   - Primer getter (muestra el patrón)
   - Primer método de validación (muestra estructura if)
   - Luego: solo directivas

2. **Configuraciones técnicas obligatorias:**
   - Dependencias en pom.xml
   - Connection strings
   - Configuración de frameworks

3. **Boilerplate mínimo:**
   - Imports necesarios (primera vez)
   - Estructura de clase vacía
   - Annotations (@Override por primera vez)

4. **Fragmentos ilustrativos pequeños (1-3 líneas):**
   ```java
   // Mostrar sintaxis específica
   this.id = UUID.randomUUID().toString();
   ```
   Pero NO el método completo.

---

## 🎯 ESTRATEGIA DE ENSEÑANZA PROGRESIVA

### Checkpoint 1 de un concepto:
```markdown
**Crear tu primer getter (getId):**

Un getter es un método público que retorna el valor de un atributo privado.

```java
public String getId() {
    return this.id;
}
```

**Anatomía del getter:**
- `public`: Otros pueden llamarlo
- `String`: Tipo de dato que retorna
- `getId`: Nombre del método (convención: get + NombreAtributo)
- `return this.id`: Devuelve el valor del atributo
```

**AHORA TÚ:**
Crea los getters para los demás atributos siguiendo el mismo patrón.
```

### Checkpoint 2+ del mismo concepto:
```markdown
**Crear getters para todos los atributos:**

**RECUERDA:** Un getter retorna el valor de un atributo privado. Ya viste el patrón en el checkpoint anterior.

**Debes crear getters para:**
- movementType (String)
- fuelType (String)
- quantity (double)
- unitPrice (double)
- movementDate (String)

**Pista:** Usa el mismo patrón que usaste para `getId()`, solo cambia el nombre del método y el atributo que retornas.
```

---

## 💡 FORMATO DE TAREAS EFECTIVAS

### ❌ MAL (da todo resuelto):
```markdown
1. **Crear setter para quantity:**
   ```java
   public void setQuantity(double quantity) {
       if (quantity <= 0) {
           System.out.println("Error: cantidad inválida");
           return;
       }
       this.quantity = quantity;
   }
   ```
```

### ✅ BIEN (guía sin resolver):
```markdown
1. **Crear setter para quantity:**
   
   **Especificaciones:**
   - Método público
   - No retorna nada (void)
   - Recibe un parámetro double llamado quantity
   
   **Lógica de validación (TÚ debes implementarla):**
   - Si el parámetro es menor o igual a 0:
     - Mostrar mensaje de error
     - NO actualizar el atributo (usar return para salir)
   - Si el parámetro es válido:
     - Asignar el valor al atributo usando this
   
   **Desafío:** ¿Cómo diferencias el parámetro `quantity` del atributo `quantity`?
   
   **Prueba:** Intenta asignar -50 y verifica que el atributo NO cambie.
```

---

## 🧩 EJEMPLO COMPLETO: CHECKPOINT BIEN ESTRUCTURADO

### ✅ Checkpoint 2.3: Encapsulamiento con Getters/Setters

**Concepto clave:** Mantener atributos privados y exponer acceso controlado mediante métodos públicos.

**📍 DÓNDE:**
- **Clase existente:** `Movement.java` (agregar métodos después de los constructores)
- **Main.java:** Para PROBAR los getters/setters

**Prompts sugeridos:**
```text
"¿Qué es un getter? Muéstrame la sintaxis básica."
"¿Cuándo NO debo crear un setter para un atributo?"
"¿Cómo valido datos dentro de un setter?"
```

**Tareas paso a paso:**

1. **Crear tu primer getter (getId):**
   
   Un getter es un método público que retorna el valor de un atributo privado.
   
   ```java
   public String getId() {
       return this.id;
   }
   ```
   
   **¿Entendiste el patrón?** Ahora crea los getters restantes para:
   - movementType
   - fuelType  
   - quantity
   - unitPrice
   - movementDate

2. **Crear método de cálculo getTotalValue():**
   
   **Diferencia importante:** Este NO es un getter normal porque no hay un atributo `totalValue`. Es un cálculo derivado.
   
   **Especificaciones:**
   - Tipo de retorno: double
   - Sin parámetros
   - Retorna: cantidad multiplicada por precio unitario
   - Usa los atributos de la clase con `this`

3. **Crear setter para quantity con validación:**
   
   **Especificaciones:**
   - Tipo de retorno: void
   - Recibe: double quantity
   
   **Implementa la siguiente lógica:**
   - Valida que quantity sea > 0
   - Si es inválido: muestra mensaje y NO actualiza el atributo
   - Si es válido: actualiza usando `this.quantity = quantity`
   
   **Pista:** Usa `if` para validar y `return` para salir temprano si es inválido.

4. **Crear setters adicionales:**
   
   Siguiendo el patrón del paso 3, crea setters con validación para:
   - unitPrice (debe ser > 0)
   - movementType (debe ser "ENTRADA" o "SALIDA")
   
   **Recuerda:** Para comparar Strings usa `.equals()`, NO `==`

5. **NO crear setters para:**
   - id (nunca debe cambiar)
   - movementDate (la fecha de creación es inmutable)

6. **Probar en Main.java:**
   
   **Pruebas que debes hacer:**
   - Crea un Movement
   - Lee todos sus datos con getters e imprime
   - Calcula el total con getTotalValue()
   - Intenta modificar quantity con valor válido → debe funcionar
   - Intenta modificar quantity con valor inválido (-50) → debe rechazarse
   - Verifica que después del rechazo, el valor original no cambió

**✅ Resultado esperado:**
- Puedes leer todos los atributos
- Puedes modificar los permitidos con validación
- Los valores inválidos son rechazados
- El objeto siempre mantiene un estado válido

**⏱️ Tiempo estimado:** 3 horas

---

## 📊 COMPARACIÓN DIRECTA

### ❌ ENFOQUE INCORRECTO (Solo Copiar/Pegar):

```markdown
**Crear clase Movement completa:**

```java
package com.forestech.models;

import java.util.UUID;
import java.time.LocalDateTime;

public class Movement {
    private String id;
    private String movementType;
    private String fuelType;
    private double quantity;
    private double unitPrice;
    private String movementDate;
    
    public Movement(String movementType, String fuelType, double quantity, double unitPrice) {
        this.id = UUID.randomUUID().toString();
        this.movementType = movementType;
        this.fuelType = fuelType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.movementDate = LocalDateTime.now().toString();
    }
    
    public String getId() {
        return this.id;
    }
    
    // ... 20 métodos más
}
```

**Siguiente paso:** Compila y prueba.
```

**RESULTADO:** Usuario copia todo → NO aprende nada → Al día siguiente no recuerda qué hizo.

---

### ✅ ENFOQUE CORRECTO (Aprendizaje Real):

```markdown
**Checkpoint 2.1: Estructura básica de la clase Movement**

**📍 DÓNDE:**
- Crear paquete `models` en `com.forestech`
- Crear archivo `Movement.java`

**Tareas:**

1. **Declarar la clase pública Movement**
   
   **Pista:** La sintaxis básica es:
   ```java
   public class NombreDeLaClase {
       // contenido
   }
   ```

2. **Declarar 6 atributos privados:**
   
   **Piensa:** ¿Qué información necesitas guardar de un movimiento de combustible?
   
   Debes declarar:
   - Identificador único (String) - nombre sugerido: `id`
   - Tipo de movimiento (String) - ¿"ENTRADA" o "SALIDA"?
   - Tipo de combustible (String)
   - Cantidad (double)
   - Precio por unidad (double)
   - Fecha (String, por ahora)
   
   **Recuerda:** Todos deben ser `private` para encapsulamiento.

3. **Compilar y verificar:**
   ```bash
   mvn clean compile
   ```
   
   Debe compilar sin errores (aunque la clase no hace nada aún).

**Siguiente checkpoint:** Agregarás los constructores para poder crear objetos Movement.
```

**RESULTADO:** Usuario piensa → escribe código → entiende decisiones → aprende de verdad.

---

## ✅ CHECKLIST ANTES DE PUBLICAR UN CHECKPOINT

Antes de finalizar un checkpoint, verifica:

- [ ] ¿Di código Java completo que el usuario podría copiar/pegar sin pensar?
- [ ] ¿Expliqué QUÉ debe hacer el código en lugar de CÓMO escribirlo?
- [ ] ¿Di ejemplos de sintaxis solo para conceptos completamente nuevos?
- [ ] ¿Incluí preguntas guía que hagan pensar al usuario?
- [ ] ¿Las tareas requieren que el usuario investigue o aplique lo aprendido?
- [ ] ¿Incluí validaciones/pruebas que el usuario debe hacer?
- [ ] ¿El checkpoint prepara para el siguiente sin resolverlo todo?

---

## 📝 FRASES CLAVE PARA USAR

### ✅ Buenas frases:
- "Siguiendo el patrón anterior, ahora tú..."
- "Implementa la siguiente lógica: [especificación]"
- "Investiga cómo usar [concepto] en Java"
- "Piensa: ¿qué datos necesita este método?"
- "Desafío: ¿Cómo validarías que...?"
- "TÚ debes decidir..."
- "Aplica el mismo patrón para..."

### ❌ Frases a evitar:
- "Aquí está el código completo:"
- "Solo copia esto:"
- "El código es el siguiente:"
- "Usa exactamente este código:"

---

## 🎯 OBJETIVO FINAL

Al terminar cada fase, el usuario debe poder:
- ✅ Explicar con sus palabras cada concepto
- ✅ Escribir código similar sin consultar los apuntes
- ✅ Identificar y corregir errores por sí mismo
- ✅ Aplicar los conceptos a problemas nuevos

**Si el usuario solo copió código → FALLAMOS como maestros.**

---

**Estas reglas aplican a TODAS las fases del roadmap de Forestech CLI.**

**Última actualización:** 2025-01-17
