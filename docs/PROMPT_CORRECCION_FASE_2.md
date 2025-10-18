# 📋 PROMPT PARA CORRECCIÓN DE FASE 2 - POO

## 🎯 Objetivo de la tarea

Necesito que revises y corrijas el archivo `roadmaps/FASE_02_POO.md` para aplicar a todo  el código el formato   **diagramas de instrucciones tipo árbol** que guíen al usuario a escribir su propio código.

Este es un proyecto de APRENDIZAJE, no de desarrollo rápido. El usuario está aprendiendo Java desde CERO y debe escribir el código él mismo, no copiarlo.

---

## ✅ Formato correcto aplicado en Fase 1 (REFERENCIA)

### ❌ FORMATO INCORRECTO (lo que hay que eliminar si se encuentra ):

```java
// Aquí está toda la clase Movement completa:
public class Movement {
    private String id;
    private String type;
    private double quantity;
    
    public Movement(String type, double quantity) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.quantity = quantity;
    }
    
    public String getId() {
        return id;
    }
    
    // ... 50 líneas más de código listo para copiar
}
```

### ✅ FORMATO CORRECTO (lo que debe quedar por ejemplo):

```
Movement.java
│
├── Atributos privados (encapsulamiento)
│   ├── String id (identificador único del movimiento)
│   ├── String type (puede ser "ENTRADA" o "SALIDA")
│   ├── double quantity (cantidad en litros)
│   ├── double unitPrice (precio por litro)
│   └── LocalDateTime timestamp (fecha y hora del movimiento)
│
├── Constructor principal
│   ├── Parámetros que debe recibir:
│   │   • String type
│   │   • double quantity
│   │   • double unitPrice
│   │
│   ├── Lógica a implementar:
│   │   1. Generar ID único usando UUID.randomUUID().toString()
│   │   2. Asignar los parámetros a los atributos con 'this.'
│   │   3. Generar timestamp actual con LocalDateTime.now()
│   │
│   └── 💡 PISTA: 'this' se usa para diferenciar atributos de parámetros
│       cuando tienen el mismo nombre
│
├── Método: getId
│   ├── Modificador: public
│   ├── Tipo de retorno: String
│   ├── Parámetros: ninguno
│   └── Retorna: this.id
│
├── Método: getType
│   ├── Modificador: public
│   ├── Tipo de retorno: String
│   ├── Parámetros: ninguno
│   └── Retorna: this.type
│
└── Método: calculateTotal
    ├── Modificador: public
    ├── Tipo de retorno: double
    ├── Parámetros: ninguno
    │
    ├── Lógica a implementar:
    │   1. Calcular subtotal: quantity * unitPrice
    │   2. Calcular IVA: subtotal * 0.19
    │   3. Retornar: subtotal + iva
    │
    └── 💡 NOTA: Este método USA los atributos del objeto (this.quantity, this.unitPrice)
```

---

## 📐 Elementos del formato tipo "Diagrama de Árbol"

Cada checkpoint debe seguir esta estructura:

### 1. **Encabezado del Checkpoint**
```markdown
## ✅ Checkpoint X.X: Nombre del Concepto

**Concepto clave:** Explicación en 1-2 frases de qué se aprende

**📍 DÓNDE:** 
- Qué archivos crear
- Qué archivos modificar

**🎯 PARA QUÉ:** 
- Explicación del propósito real del código
- Cómo se usa en Forestech

**🔗 CONEXIÓN FUTURA:** 
- Cómo evolucionará este código en fases posteriores

**Prompts sugeridos:**
```text
"Pregunta 1 que el usuario puede hacer"
"Pregunta 2 que el usuario puede hacer"
```
```

### 2. **Diagrama de Tareas (Formato Árbol)**

Usa este formato visual para cada clase/método:

```
NombreClase.java
│
├── Elemento 1: descripción
│   ├── Sub-elemento 1.1
│   ├── Sub-elemento 1.2
│   └── Lógica paso a paso:
│       1. Primer paso
│       2. Segundo paso
│       3. Tercer paso
│
├── Elemento 2: descripción
│   ├── Parámetros:
│   │   • tipo1 nombre1 (descripción)
│   │   • tipo2 nombre2 (descripción)
│   │
│   ├── Condiciones a validar:
│   │   • condición 1
│   │   • condición 2
│   │
│   └── Tipo de retorno: tipo
│
└── Elemento 3: descripción
    └── 💡 PISTA: Explicación adicional útil
```

### 3. **Sección de Pruebas**

En lugar de dar el código de pruebas completo:

```markdown
**🧪 PRUEBAS EN Main.java:**

```
Casos de prueba a implementar:

Prueba 1: Crear un movimiento de entrada
├── Crear objeto: Movement entrada = new Movement("ENTRADA", 100, 3.45)
├── Llamar a: entrada.getId()
├── Llamar a: entrada.calculateTotal()
└── Resultado esperado: Ver ID generado y total calculado

Prueba 2: Validar encapsulamiento
├── Intentar acceder: entrada.quantity (debe dar error de compilación)
├── Usar getter: entrada.getQuantity() (debe funcionar)
└── Concepto: Los atributos privados solo se acceden por getters/setters
```
```

### 4. **Pistas de Implementación**

En lugar de código completo, dar pistas:

```markdown
**💡 PISTAS DE IMPLEMENTACIÓN:**

1. **Para generar un ID único:**
   - Importa: java.util.UUID
   - Usa: UUID.randomUUID().toString()
   - Asigna al atributo id en el constructor

2. **Para usar 'this' correctamente:**
   - 'this.quantity' se refiere al atributo de la clase
   - 'quantity' (sin this) se refiere al parámetro del método
   - Usa 'this' cuando hay ambigüedad

3. **Para crear getters:**
   - Patrón: public TipoRetorno getNombreAtributo() { return this.nombreAtributo; }
   - Ejemplo: public String getType() { return this.type; }
```

### 5. **Comparaciones Visuales**

Cuando expliques diferencias entre conceptos (como método static vs instancia):

```markdown
**📊 COMPARACIÓN: Métodos Estáticos vs Métodos de Instancia**

```
┌─────────────────────────────────────────────────────────────┐
│ MÉTODOS ESTÁTICOS (static)                                  │
├─────────────────────────────────────────────────────────────┤
│ • Pertenecen a la CLASE, no al objeto                       │
│ • Se llaman: NombreClase.nombreMetodo()                     │
│ • NO pueden acceder a atributos de instancia                │
│ • Útiles para: utilidades, cálculos sin estado              │
│                                                              │
│ Ejemplo Fase 1:                                             │
│   MovementCalculator.calculateSubtotal(100, 3.45)          │
│   └─→ NO necesitas crear un objeto                         │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ MÉTODOS DE INSTANCIA (sin static)                          │
├─────────────────────────────────────────────────────────────┤
│ • Pertenecen a un OBJETO específico                         │
│ • Se llaman: nombreObjeto.nombreMetodo()                    │
│ • PUEDEN acceder a atributos de instancia (this.atributo)  │
│ • Útiles para: operaciones sobre datos del objeto           │
│                                                              │
│ Ejemplo Fase 2:                                             │
│   Movement mov = new Movement("ENTRADA", 100, 3.45);       │
│   mov.calculateTotal();                                     │
│   └─→ Usa los datos específicos de 'mov'                   │
└─────────────────────────────────────────────────────────────┘
```
```

### 6. **Errores Comunes**

Incluye sección de errores típicos CON explicación, NO con solución completa:

```markdown
**🚨 ERRORES COMUNES:**

```
❌ ERROR 1: Non-static variable cannot be referenced from a static context
Causa: Intentas acceder a un atributo de instancia desde un método static
Solución: Quita 'static' del método o crea un objeto primero

❌ ERROR 2: Constructor Movement in class Movement cannot be applied
Causa: Llamaste al constructor con parámetros incorrectos
Solución: Verifica que los tipos y cantidad de parámetros coincidan

❌ ERROR 3: id has private access in Movement
Causa: Intentas acceder directamente a un atributo privado
Solución: Usa el getter: movement.getId() en lugar de movement.id
```
```

### 7. **Auto-evaluación**

Al final de cada checkpoint:

```markdown
**🎯 AUTO-EVALUACIÓN:**

Antes de continuar, pregúntate:
- [ ] ¿Puedo explicar la diferencia entre clase y objeto?
- [ ] ¿Entiendo qué es encapsulamiento y por qué es importante?
- [ ] ¿Sé cuándo usar 'this' y cuándo no?
- [ ] ¿Mi código compila sin errores?
- [ ] ¿Puedo crear varios objetos Movement diferentes?
```

---

## 🎯 Conceptos específicos de Fase 2 - POO

La Fase 2 cubre estos temas. Asegúrate de aplicar el formato de diagramas a TODOS:

### Checkpoint 2.1: Primera Clase - Movement
- Crear clase con atributos privados
- Constructor
- Concepto de encapsulamiento
- Diferencia entre clase y objeto

**Formato a aplicar:**
- Diagrama de árbol mostrando ESTRUCTURA de la clase (atributos, constructor)
- NO dar código completo de atributos
- Explicar qué es 'private' y por qué se usa
- Pistas sobre cómo inicializar atributos en constructor

### Checkpoint 2.2: Getters y Setters
- Métodos de acceso
- Validaciones en setters
- Concepto de 'this'

**Formato a aplicar:**
- Diagrama mostrando PATRÓN de getters/setters
- NO dar código completo de todos los getters
- Explicar regla de nomenclatura (get + NombreAtributo)
- Pistas sobre validaciones en setters

### Checkpoint 2.3: Métodos de Instancia
- Diferencia entre static y métodos de instancia
- Métodos que usan atributos del objeto
- Retorno de valores calculados

**Formato a aplicar:**
- Tabla comparativa visual: static vs instancia
- Diagrama de métodos (calculateTotal, etc.)
- Explicar cuándo el método necesita acceder a 'this'

### Checkpoint 2.4: ArrayList y Colecciones
- Almacenar múltiples objetos
- Agregar, buscar, eliminar elementos
- Iterar sobre colecciones

**Formato a aplicar:**
- Diagrama de operaciones (add, remove, get, size)
- NO dar código completo del ArrayList
- Comparación visual: Array vs ArrayList
- Casos de uso con Movement

### Checkpoint 2.5: Sobrecarga de Constructores
- Múltiples constructores
- Constructor por defecto vs con parámetros
- Concepto de firma del método

**Formato a aplicar:**
- Diagrama mostrando ESTRUCTURA de cada constructor
- Explicar qué es "firma del método"
- Casos de uso: ¿cuándo usar cada constructor?

### Checkpoint 2.6: Método toString()
- Override de métodos de Object
- Representación en String de objetos
- Uso de @Override

**Formato a aplicar:**
- Explicar qué hace toString() por defecto
- Diagrama de implementación personalizada
- Formato esperado del String resultante

---

## 📋 Checklist de corrección

Para cada checkpoint de Fase 2, verifica:

### ✅ Estructura del checkpoint
- [ ] Tiene título con emoji ✅
- [ ] Incluye "Concepto clave" en 1-2 frases
- [ ] Define DÓNDE (qué archivos)
- [ ] Define PARA QUÉ (propósito real)
- [ ] Incluye "Prompts sugeridos"

### ❌ Eliminar código completo
- [ ] NO hay bloques grandes de código Java (>10 líneas)
- [ ] NO hay clases completas con todos los métodos escritos
- [ ] NO hay código de Main.java listo para copiar/pegar

### ✅ Reemplazar con diagramas
- [ ] Usa formato de árbol ASCII (├── └──)
- [ ] Describe QUÉ debe hacer el código, no CÓMO escribirlo
- [ ] Incluye pistas, no soluciones completas
- [ ] Tiene sección "💡 PISTAS DE IMPLEMENTACIÓN"

### ✅ Elementos educativos
- [ ] Incluye "🧪 PRUEBAS" con casos de prueba descritos
- [ ] Incluye "🚨 ERRORES COMUNES" con explicación
- [ ] Incluye "🎯 AUTO-EVALUACIÓN" con preguntas
- [ ] Incluye comparaciones visuales cuando hay conceptos similares

### ✅ Conexión con Forestech
- [ ] Todos los ejemplos usan contexto de combustibles
- [ ] Menciona cómo se usará en el proyecto real
- [ ] Conecta con checkpoints previos y futuros

---

## 🎨 Ejemplo completo de un checkpoint corregido

Aquí tienes un ejemplo de cómo debe quedar un checkpoint completo:

```markdown
## ✅ Checkpoint 2.1: Primera Clase POO - Movement

**Concepto clave:** Crear la primera clase con Programación Orientada a Objetos. Una clase es un molde que define características (atributos) y comportamientos (métodos) de algo. Un objeto es una instancia concreta creada a partir de ese molde.

**📍 DÓNDE:** 
- **Crear nueva clase:** `Movement.java` en `forestech-cli-java/src/main/java/com/forestech/`
- **Modificar:** `Main.java` solo para probar la nueva clase

**🎯 PARA QUÉ:** 
Hasta ahora usaste métodos estáticos en MovementCalculator. Ahora aprenderás POO real: cada movimiento será un objeto con sus propios datos (cantidad, precio, tipo) y comportamientos (calcular total, generar ID).

**🔗 CONEXIÓN FUTURA:** 
- En Fase 4: Estos objetos Movement se guardarán en la base de datos
- En Fase 5: Agregarás más métodos como calcularDescuento(), validarStock()
- MovementCalculator desaparecerá, su lógica estará en Movement

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre clase y objeto? Dame ejemplos con Movement."
"¿Qué significa 'encapsulamiento' y por qué los atributos son private?"
"Explícame qué hace 'this' en el constructor."
```

**📋 DIAGRAMA DE TAREAS:**

```
Movement.java
│
├── Atributos privados (variables de instancia)
│   ├── String id
│   │   └── Descripción: Identificador único del movimiento
│   │
│   ├── String type
│   │   └── Descripción: Tipo de movimiento ("ENTRADA" o "SALIDA")
│   │
│   ├── double quantity
│   │   └── Descripción: Cantidad de combustible en litros
│   │
│   ├── double unitPrice
│   │   └── Descripción: Precio por litro
│   │
│   └── LocalDateTime timestamp
│       └── Descripción: Fecha y hora del movimiento
│
│   💡 PISTA: Todos los atributos deben ser 'private' (encapsulamiento)
│              Esto evita que se modifiquen directamente desde fuera
│
├── Constructor
│   ├── Nombre: public Movement (mismo que la clase)
│   │
│   ├── Parámetros:
│   │   • String type
│   │   • double quantity  
│   │   • double unitPrice
│   │
│   ├── Lógica paso a paso:
│   │   1. Generar ID único: UUID.randomUUID().toString()
│   │   2. Asignar parámetros a atributos usando 'this':
│   │      this.type = type;
│   │      this.quantity = quantity;
│   │      this.unitPrice = unitPrice;
│   │   3. Generar timestamp actual: LocalDateTime.now()
│   │
│   └── 💡 EXPLICACIÓN DE 'this':
│       • 'this.quantity' = atributo de la clase
│       • 'quantity' (sin this) = parámetro del constructor
│       • Usas 'this' para diferenciarlos cuando tienen el mismo nombre
│
└── Imports necesarios
    ├── import java.util.UUID;
    └── import java.time.LocalDateTime;
```

**🧪 PRUEBAS EN Main.java:**

```
Casos de prueba para validar tu clase Movement:

Prueba 1: Crear un movimiento de entrada
├── Código a escribir:
│   Movement entrada = new Movement("ENTRADA", 100.0, 3.45);
│
├── Qué verificar:
│   • El código compila sin errores
│   • No hay error de "cannot find symbol"
│   
└── Concepto: Acabas de crear tu primer OBJETO a partir de la clase Movement

Prueba 2: Crear varios movimientos diferentes
├── Código a escribir:
│   Movement entrada1 = new Movement("ENTRADA", 100.0, 3.45);
│   Movement salida1 = new Movement("SALIDA", 50.0, 3.50);
│   Movement entrada2 = new Movement("ENTRADA", 200.0, 3.40);
│
└── Concepto: Cada objeto es INDEPENDIENTE, tiene sus propios datos
               entrada1.quantity ≠ salida1.quantity ≠ entrada2.quantity

Prueba 3: Intentar acceder a atributo privado (debe fallar)
├── Código a escribir:
│   System.out.println(entrada.quantity);
│
├── Resultado esperado: ERROR de compilación
│   "quantity has private access in Movement"
│
└── Concepto: Encapsulamiento - los atributos privados NO se acceden directamente
              (En el siguiente checkpoint crearás getters para accederlos)
```

**💡 PISTAS DE IMPLEMENTACIÓN:**

1. **Estructura básica de una clase:**
   ```java
   public class NombreClase {
       // Atributos (variables de instancia)
       // Constructor
       // Métodos
   }
   ```

2. **Para declarar atributos privados:**
   - Sintaxis: `private TipoD Dato nombreAtributo;`
   - Ejemplo: `private String type;`
   - Van al inicio de la clase, antes del constructor

3. **Para crear un constructor:**
   - Debe tener el MISMO nombre que la clase
   - NO tiene tipo de retorno (ni void, ni nada)
   - Ejemplo: `public Movement(parámetros) { ... }`

4. **Para generar un ID único:**
   - Importa: `import java.util.UUID;`
   - Usa: `UUID.randomUUID().toString()`
   - Asigna: `this.id = UUID.randomUUID().toString();`

5. **Para obtener fecha/hora actual:**
   - Importa: `import java.time.LocalDateTime;`
   - Usa: `LocalDateTime.now()`
   - Asigna: `this.timestamp = LocalDateTime.now();`

**📊 COMPARACIÓN: Fase 1 vs Fase 2**

```
┌──────────────────────────────────────────────────────────────┐
│ FASE 1: Métodos Estáticos (sin POO)                         │
├──────────────────────────────────────────────────────────────┤
│ MovementCalculator.calculateTotal(100, 3.45)                │
│                                                               │
│ Problema:                                                     │
│ • Los datos están separados de la lógica                    │
│ • Necesitas pasar todos los parámetros cada vez             │
│ • No hay "memoria" del movimiento                            │
│ • Difícil de manejar múltiples movimientos                   │
└──────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────┐
│ FASE 2: Objetos (con POO)                                   │
├──────────────────────────────────────────────────────────────┤
│ Movement mov = new Movement("ENTRADA", 100, 3.45);          │
│ mov.calculateTotal();  // Usa SUS propios datos             │
│                                                               │
│ Ventajas:                                                     │
│ • Los datos Y la lógica están juntos                        │
│ • El objeto "sabe" su cantidad, precio, tipo                │
│ • Puedes tener muchos objetos diferentes                     │
│ • Cada uno mantiene su propio estado                         │
└──────────────────────────────────────────────────────────────┘
```

**🚨 ERRORES COMUNES:**

```
❌ ERROR 1: "Cannot find symbol - class Movement"
├── Causa: El archivo Movement.java no está en el lugar correcto
├── Solución: Verifica que esté en src/main/java/com/forestech/
└── O falta declarar la clase como 'public class Movement'

❌ ERROR 2: "Constructor Movement() is undefined"
├── Causa: Intentas crear objeto sin parámetros pero el constructor requiere parámetros
├── Ejemplo del error: new Movement() ← faltan parámetros
└── Solución: new Movement("ENTRADA", 100, 3.45) ← con parámetros

❌ ERROR 3: "Cannot resolve symbol 'UUID'"
├── Causa: Falta el import
└── Solución: Agrega al inicio: import java.util.UUID;

❌ ERROR 4: "quantity has private access in Movement"
├── Causa: Intentas acceder: movement.quantity (atributo privado)
├── Concepto: Esto es CORRECTO que falle - es el encapsulamiento funcionando
└── Solución: En el siguiente checkpoint crearás getters para acceder
```

**✅ Resultado esperado:**
- Archivo Movement.java creado con 5 atributos privados
- Constructor que recibe 3 parámetros y genera ID y timestamp
- Código compila sin errores
- Puedes crear objetos Movement en Main.java
- Entiendes la diferencia entre clase (molde) y objeto (instancia)

**🎯 AUTO-EVALUACIÓN:**

Antes de continuar, pregúntate:
- [ ] ¿Puedo explicar con mis palabras qué es una clase?
- [ ] ¿Puedo explicar con mis palabras qué es un objeto?
- [ ] ¿Entiendo por qué los atributos son private?
- [ ] ¿Sé qué hace 'this' y cuándo usarlo?
- [ ] ¿Puedo crear varios objetos Movement diferentes?
- [ ] ¿Entiendo que cada objeto tiene sus propios datos independientes?

**⏱️ Tiempo estimado:** 2-3 horas
```

---

## 🚀 Instrucciones finales

1. **Lee completamente** el archivo `roadmaps/FASE_02_POO.md`

2. **Identifica todos los checkpoints** y aplica el formato de diagramas a CADA UNO

3. **Elimina TODO código completo** (bloques de >10 líneas de Java)

4. **Reemplaza con diagramas tipo árbol** siguiendo los ejemplos de este prompt

5. **Mantén la estructura general** del archivo (títulos, orden de checkpoints)

6. **Asegúrate de que cada checkpoint tenga**:
   - Diagrama de tareas (formato árbol)
   - Sección de pruebas (casos descritos, no código completo)
   - Pistas de implementación
   - Errores comunes
   - Auto-evaluación




---

## ✅ Criterios de éxito

La corrección será exitosa cuando:

- ✅ NO haya bloques de código Java de más de 10 líneas
- ✅ Todos los checkpoints usen formato de diagrama de árbol
- ✅ Las instrucciones digan QUÉ hacer, no CÓMO escribirlo
- ✅ Haya pistas pero no soluciones completas
- ✅ El usuario tenga que PENSAR y ESCRIBIR el código, no copiarlo
- ✅ Cada concepto de POO esté explicado visualmente
- ✅ Los ejemplos usen el contexto de Forestech (combustibles)

---

## 🔄 DIRECTIVA DE CASCADA - PRÓXIMAS FASES

**⚠️ IMPORTANTE:** Al terminar la corrección de esta FASE 2, debes:

1. **Editar este archivo** para crear un nuevo prompt similar
2. **Cambiar referencias:**
   - `FASE_02_POO.md` → `FASE_03_SQL.md`
   - Checkpoint 2.X → Checkpoint 3.X
   - Conceptos POO → Conceptos SQL y BD
   - Ejemplos de clases → Ejemplos de conexión BD

3. **Guardar como:** `PROMPT_CORRECCION_FASE_3.md` (YA EXISTE - Actualizar si es necesario)

4. **Repetir el ciclo para cada fase:**
   - FASE 3 (SQL y BD) → `PROMPT_CORRECCION_FASE_3.md` ✅ Creado
   - FASE 4 (CRUD Completo) → `PROMPT_CORRECCION_FASE_4.md`
   - FASE 5 (Lógica de Negocio) → `PROMPT_CORRECCION_FASE_5.md`
   - FASE 6 (Interfaz de Usuario) → `PROMPT_CORRECCION_FASE_6.md`
   - FASE 7 (Manejo de Errores) → `PROMPT_CORRECCION_FASE_7.md`
   - FASE 8 (Características Avanzadas) → `PROMPT_CORRECCION_FASE_8.md`

5. **Al terminar TODAS las fases:**
   - Tendrás un archivo de prompt para cada fase
   - Todos los roadmaps estarán en formato didáctico de árbol
   - El usuario podrá aprender escribiendo código, no copiándolo

---

**Recuerda:** Este es un proyecto de APRENDIZAJE. El objetivo NO es que el código funcione rápido, sino que el usuario ENTIENDA lo que escribe.

🎓 **"No des el pescado, enseña a pescar"**

