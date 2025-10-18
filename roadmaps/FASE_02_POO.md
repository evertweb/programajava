# 🧩 FASE 2: PROGRAMACIÓN ORIENTADA A OBJETOS (Semanas 3-4)

> Objetivo general: modelar el dominio de Forestech con clases, objetos y colecciones dinámicas.

---

## 🧠 Antes de empezar

- 🧱 **Diseño visual primero:** antes de codificar cada clase, dibuja un diagrama sencillo (puede ser en papel o en herramientas como draw.io) mostrando atributos y relaciones.
- 📓 Mantén en `JAVA_LEARNING_LOG.md` el racional de diseño: por qué cada clase existe, qué responsabilidades tiene y qué dudas quedan abiertas.
- 🐞 Refuerza depuración: inspecciona objetos en runtime y usa "Evaluate Expression" para leer/ajustar atributos.
- 🔁 **Git loop:** al completar cada checkpoint crea un commit con mensaje claro (`git commit -m "fase2 checkpoint 2.1"`).
- 🎯 **ORGANIZACIÓN CLARA:** Cada checkpoint indica **EXACTAMENTE** dónde escribir el código para construir una arquitectura profesional desde el inicio.
- ✍️ **APRENDIZAJE ACTIVO:** NO copiarás código completo. Recibirás DIRECTIVAS para que TÚ escribas el código y aprendas de verdad.

---

## ✅ Checkpoint 2.1: Primera clase `Movement` - Atributos Privados

**Concepto clave:** Una clase es un molde que encapsula datos (atributos) y comportamientos (métodos). Es la base de la POO.

**📍 DÓNDE:** 
- **Crear paquete nuevo:** `models` dentro de `com.forestech/` 
- **Crear archivo:** `Movement.java` en `forestech-cli-java/src/main/java/com/forestech/models/`
- **Main.java:** Por ahora NO lo toques (solo verificarás que compile)

**🎯 PARA QUÉ:** 
En Fase 1 trabajaste con variables sueltas y métodos estáticos en MovementCalculator. Eso funcionó para aprender fundamentos, pero tiene limitaciones:
- ❌ Las variables desaparecen al terminar `main()`
- ❌ No puedes tener múltiples movimientos al mismo tiempo
- ❌ No hay forma de persistir datos
- ❌ Código repetitivo para cada nuevo movimiento

La clase Movement soluciona todo esto:
- ✅ **Agrupa** todos los datos de un movimiento en una estructura coherente
- ✅ **Persiste** mientras la aplicación esté corriendo
- ✅ **Permite crear múltiples objetos** (puedes tener 100 movimientos diferentes)
- ✅ **Se puede guardar** en la base de datos (Fase 3-4)
- ✅ **Encapsula comportamiento** (los cálculos irán dentro de Movement, no en una clase separada)

**🔗 CONEXIÓN FUTURA:**
- **Fase 3:** Conectarás a SQL Server y leerás movimientos de la BD, convirtiéndolos en objetos Movement
- **Fase 4:** Guardarás estos objetos en la BD con INSERT/UPDATE
- **Fase 5:** MovementCalculator DESAPARECERÁ - sus métodos se convertirán en métodos de instancia de Movement
- **Fase 6:** El usuario creará movimientos desde el menú interactivo
- **Fase 9:** Generarás reportes recorriendo listas de Movement

**📚 EVOLUCIÓN DEL CÓDIGO:**
```
FASE 1 (Procedural):
MovementCalculator.calculateTotal(quantity, price) → método estático

FASE 2 (POO):
movement.getTotalValue() → método de instancia
```

**Prompts sugeridos:**
```text
"Explícame con analogía del mundo real qué es una clase vs un objeto."
"¿Qué atributos debería tener un movimiento de combustible en Forestech?"
"¿Por qué los atributos deben ser privados? Dame ejemplos de qué problemas evita."
"¿Qué significa 'encapsulamiento' en términos simples?"
```

**Diagrama de tareas - Estructura de Movement.java:**

```
Movement.java
│
├── Declaración de clase
│   └── public class Movement { }
│
└── Atributos privados (6 totales)
    ├── String id
    │   └── Descripción: Identificador único del movimiento
    │       Cómo generarlo: UUID.randomUUID().toString()
    │
    ├── String movementType
    │   └── Descripción: Tipo de movimiento ("ENTRADA" o "SALIDA")
    │       Validación: Solo esos dos valores permitidos
    │
    ├── String fuelType
    │   └── Descripción: Tipo de combustible (Diesel, Gasolina 93, ACPM, etc.)
    │       Ejemplos: "Diesel", "Gasolina 93", "ACPM"
    │
    ├── double quantity
    │   └── Descripción: Cantidad en litros
    │       Validación: Debe ser > 0
    │
    ├── double unitPrice
    │   └── Descripción: Precio por litro en pesos
    │       Validación: Debe ser > 0
    │
    └── String movementDate
        └── Descripción: Fecha y hora del movimiento
            Cómo capturarlo: LocalDateTime.now().toString()
```

**Tareas paso a paso:**

1. **Crear el paquete `models`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "models"
   - Este paquete contendrá TODAS las clases del dominio (Movement, Vehicle, Supplier, Product)
   - **¿Por qué un paquete separado?** Organización profesional: los modelos de datos van separados de la lógica

2. **Crear la clase Movement:**
   - Clic derecho en `models` → New → Java Class → "Movement"
   - **TÚ debes escribir:** La declaración de clase pública
   - **Pista de sintaxis:**
     ```
     public class NOMBRE {
         // El contenido va aquí
     }
     ```
   - **Pregunta:** ¿Cuál debe ser el NOMBRE de la clase?

3. **Declarar los 6 atributos privados:**
   
   Usando el diagrama anterior como referencia, TÚ escribes la declaración de cada atributo.
   
   **Estructura para cada atributo:**
   ```
   private TIPO NOMBRE;
   ```
   
   **Ejemplos:**
   - `private String id;`
   - `private double cantidad;` (pero usa el nombre del diagrama)
   
   **Tareas:**
   - [ ] Declara todos los 6 atributos
   - [ ] Cada uno en una línea separada
   - [ ] Todos marcados como `private`
   - [ ] Tipos correctos (String, double, etc.)
   
   **Validación:** Cuando termines, cada atributo debe aparecer una sola vez en la clase.

4. **Agregar comentarios en cada atributo:**
   - Sobre cada línea, escribe un comentario breve explicando su propósito
   - Uso: `// comentario aquí`
   - Ejemplo: `// Identificador único del movimiento`
   - Beneficio: Te ayuda a entender el diseño mientras lo construyes

5. **Verificar que compile:**
   - Ejecuta desde terminal: `mvn clean compile`
   - Debe compilar sin errores (aunque la clase no hace nada aún)
   - **Si hay errores:** Revisa que los tipos estén bien (String vs double)

**✅ Resultado esperado:** 
- Archivo `Movement.java` creado en el paquete `models`
- 6 atributos privados declarados con tipos correctos y comentarios
- Compilación exitosa sin errores
- Estructura de proyecto:
  ```
  com.forestech/
  ├── Main.java
  ├── AppConfig.java
  ├── MovementCalculator.java (de Fase 1)
  ├── MenuHelper.java (de Fase 1)
  ├── DataDisplay.java (de Fase 1)
  ├── InputHelper.java (de Fase 1)
  └── models/
      └── Movement.java (NUEVO)
  ```

**💡 Concepto clave - Encapsulamiento:** 
Los atributos son **privados** para que nadie pueda:
- Asignar valores inválidos directamente (ej: `movement.quantity = -50` → ERROR DE COMPILACIÓN)
- Cambiar el id después de creado
- Romper el estado interno del objeto

Más adelante crearás métodos públicos (getters/setters) que controlen el acceso.

**🎓 Analogía:**
- **Clase Movement:** Molde de galletas (define la forma)
- **Objeto movement:** Galleta concreta hecha con ese molde
- **Atributos privados:** Ingredientes dentro de la galleta (no puedes tocarlos directamente)
- **Métodos públicos (próximo checkpoint):** Empaque de la galleta (forma controlada de interactuar)

**⏱️ Tiempo estimado:** 1 hora

---

## ✅ Checkpoint 2.2: Constructores y `this` - Inicialización de Objetos

**Concepto clave:** El constructor es un método especial que se ejecuta automáticamente al crear un objeto, garantizando que nazca en un estado válido.

**📍 DÓNDE:** 
- **Clase existente:** `Movement.java` (agregar constructores después de los atributos)
- **Main.java:** Para PROBAR que puedes crear objetos Movement

**🎯 PARA QUÉ:** 
Sin constructor, los objetos nacerían con valores `null` o `0` en todos los atributos (estado inválido). El constructor:
- ✅ **Garantiza** que todo Movement tenga id, tipo, combustible, cantidad y precio desde el momento de creación
- ✅ **Genera automáticamente** el id único (no quieres que el usuario lo invente)
- ✅ **Evita** crear movimientos inválidos (ej: sin tipo, sin precio)
- ✅ **Simplifica el código** - una línea crea un objeto completo

**Sin constructor vs Con constructor:**
```
SIN: Movement m = new Movement();
     m.setId(...);  // 10 líneas configurando todo
     m.setType(...);
     
CON: Movement m = new Movement("ENTRADA", "Diesel", 100.0, 3.45);  // ✅ Listo
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 3-4:** Cuando leas de la BD, usarás el constructor para convertir ResultSet en objetos
- **Fase 6:** Cuando el usuario ingrese datos desde el menú, crearás Movement con constructor
- **Fase 7:** Agregarás validaciones dentro del constructor (lanzar excepciones si cantidad <= 0)

**Prompts sugeridos:**
```text
"¿Por qué el constructor no lleva tipo de retorno (ni void, ni nada)?"
"Desglosa paso a paso qué hace 'this.quantity = quantity'."
"¿Qué es sobrecarga de constructores? Dame ejemplos."
"¿Qué pasa si olvido inicializar un atributo en el constructor?"
```

**Tareas paso a paso:**

1. **Agregar imports necesarios (boilerplate permitido):**
   ```java
   import java.util.UUID;
   import java.time.LocalDateTime;
   ```

**Diagrama de tareas - Constructores para Movement:**

```
Movement.java - Constructores
│
├── Constructor Principal (con parámetros)
│   ├── Firma: public Movement(String, String, double, double)
│   │
│   ├── Parámetros que recibe:
│   │   • String movementType → Tipo de movimiento
│   │   • String fuelType → Tipo de combustible
│   │   • double quantity → Cantidad en litros
│   │   • double unitPrice → Precio por litro
│   │
│   ├── Inicialización de atributos:
│   │   1. this.id = UUID.randomUUID().toString()
│   │   2. this.movementType = movementType
│   │   3. this.fuelType = fuelType
│   │   4. this.quantity = quantity
│   │   5. this.unitPrice = unitPrice
│   │   6. this.movementDate = LocalDateTime.now().toString()
│   │
│   └── 💡 PISTA: 'this.' se refiere al atributo de la clase
│       'this.quantity' = el atributo
│       'quantity' = el parámetro
│
└── Constructor Vacío (sin parámetros)
    ├── Firma: public Movement()
    │
    ├── Inicialización:
    │   1. Generar ID único (igual que el constructor principal)
    │   2. Generar fecha actual (igual que el constructor principal)
    │   3. Los demás atributos: dejarlos como null o 0 (se llenarán con setters después)
    │
    └── 💡 CUÁNDO USARLO: Frameworks y herramientas lo necesitan a veces
```

**Tareas paso a paso:**

1. **Agregar imports necesarios (boilerplate permitido):**
   
   Antes de la declaración de clase `public class Movement`, agrega:
   ```
   import java.util.UUID;
   import java.time.LocalDateTime;
   ```
   
   **¿Por qué?** UUID es para generar IDs, LocalDateTime para la fecha actual.

2. **Analiza el patrón del constructor (ejemplo didáctico):**
   
   Lee y **ENTIENDE** este pseudocódigo:
   
   ```
   Un constructor es como las instrucciones de un IKEA:
   
   public Movement(parámetro1, parámetro2, ...) {
       // Paso 1: Usar los parámetros para llenar los atributos privados
       this.atributo1 = parámetro1;
       this.atributo2 = parámetro2;
       
       // Paso 2: Generar valores automáticos si es necesario
       this.id = UUID.randomUUID().toString();
   }
   ```
   
   **Preguntas de reflexión:**
   - ¿Por qué todas las líneas usan `this.`?
   - ¿Qué pasa si olvidas asignar un atributo?

3. **AHORA TÚ - Implementar el constructor principal:**
   
   Usando el diagrama como referencia, TÚ escribes:
   
   - Abre un bloque: `public Movement(String movementType, String fuelType, double quantity, double unitPrice) {`
   - Dentro, asigna los 4 parámetros a sus atributos con `this.`
   - Genera el ID: `this.id = UUID.randomUUID().toString();`
   - Genera la fecha: `this.movementDate = LocalDateTime.now().toString();`
   - Cierra el bloque: `}`
   
   **Verificación:**
   - [ ] El nombre de la clase (`Movement`) es igual al nombre del constructor
   - [ ] No escribiste `void` ni otro tipo de retorno (los constructores NO tienen retorno)
   - [ ] Todos los `this.` están presentes
   - [ ] Compiló sin errores (`mvn clean compile`)

4. **AHORA TÚ - Implementar el constructor vacío:**
   
   Similar al anterior, pero:
   - NO recibe parámetros: `public Movement() {`
   - Genera ID: `this.id = UUID.randomUUID().toString();`
   - Genera fecha: `this.movementDate = LocalDateTime.now().toString();`
   - Los demás atributos (`movementType`, `fuelType`, etc.) NO los inicializas aquí
   
   **¿Por qué?** Se llenarán con setters después cuando sea necesario.

5. **Depuración obligatoria - Entender el flujo:**
   
   Sigamos esto paso a paso:
   
   - En Main.java, crea un movimiento: `Movement m = new Movement("ENTRADA", "Diesel", 100.0, 3.45);`
   - Coloca un **breakpoint** en la primera línea del constructor
   - Ejecuta en **debug mode** (Run → Debug)
   - Usa **Step Over** (F8) para ver línea por línea
   - En el panel de **Variables**, observa cómo se llena cada atributo
   - **Reflexiona:** ¿Ves el ID siendo generado? ¿La fecha? ¿Los parámetros siendo asignados?

6. **Probar en Main.java:**
   
   Escribe código para **probar ambos constructores**:
   
   ```
   a) Crear un movimiento con el constructor principal:
      Movement entrada1 = new Movement("ENTRADA", "Diesel", 100.0, 3.45);
      System.out.println(entrada1);
   
   b) Crear otro movimiento diferente:
      Movement salida1 = new Movement("SALIDA", "Gasolina 93", 50.0, 4.20);
      System.out.println(salida1);
   
   c) Probar el constructor vacío:
      Movement vacio = new Movement();
      System.out.println(vacio);
   ```
   
   **¿Qué esperas ver?**
   - 3 salidas
   - Cada una con un ID diferente (generado automáticamente)
   - Los datos que pasaste al constructor aparecen en la salida

7. **Compilar y ejecutar:**
   - Terminal: `mvn clean compile`
   - Ejecuta Main.java
   - Deberías ver algo como: `com.forestech.models.Movement@2a84aee7` (por ahora inútil - lo arreglaremos en Checkpoint 2.4)

**✅ Resultado esperado:** 
- Puedes crear objetos Movement con `new Movement("ENTRADA", "Diesel", 100.0, 3.45)`
- Cada objeto tiene un id único generado automáticamente
- La fecha se registra automáticamente
- Output tipo: `com.forestech.models.Movement@2a84aee7`

**💡 Concepto clave - `this`:**
```java
// ❌ INCORRECTO - ambos son el parámetro, no asigna nada
quantity = quantity;

// ✅ CORRECTO - this.quantity es el atributo, quantity es el parámetro
this.quantity = quantity;
```

El `this` dice: "el atributo quantity DE ESTE objeto específico".

**🎓 Analogía del constructor:**
- **Sin parámetros:** Fábrica de autos que produce modelo básico
- **Con parámetros:** Fábrica que personaliza el auto según pedido
- **this:** Es como decir "el color DE ESTE auto" vs "el color que me pediste"

**⚠️ ERROR COMÚN:**
Si olvidas `this`, el código compila pero NO asigna valores:
```java
public Movement(String type) {
    type = type;  // ❌ Esto no hace nada útil
}
```

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 2.3: Encapsulamiento con Getters/Setters - Acceso Controlado

**Concepto clave:** Mantener atributos privados y exponer acceso controlado mediante métodos públicos (getters/setters).

**📍 DÓNDE:** 
- **Clase existente:** `Movement.java` (agregar métodos después de los constructores)
- **Main.java:** Para PROBAR los getters/setters
- **MIGRAR LÓGICA:** Los cálculos de `MovementCalculator.java` se convertirán en métodos de Movement

**🎯 PARA QUÉ:** 
Los atributos privados no se pueden leer ni modificar desde fuera. Necesitas métodos para:
- ✅ **Leer valores** (getters) para mostrar en pantalla, enviar a la BD, o usar en cálculos
- ✅ **Modificar valores** (setters) con **validación** (ej: rechazar cantidad negativa)
- ✅ **Calcular valores derivados** (ej: `getTotalValue()` = cantidad × precio)
- ✅ **Proteger atributos** que NUNCA deben cambiar (ej: id, fecha) - NO crear setters para ellos

**Sin vs Con encapsulamiento:**
```
Si fueran públicos: movement.quantity = -100;  // ❌ Nadie evita esto
Con getters/setters: movement.setQuantity(-100);  // ✅ Setter valida y rechaza
```

**🔗 CONEXIÓN FUTURA:**
- **Fase 3:** Usarás getters para construir queries SQL: `INSERT INTO ... VALUES (movement.getId(), ...)`
- **Fase 4:** Los setters validarán antes de guardar en BD
- **Fase 5:** Validarás reglas de negocio (ej: no permitir salidas > inventario)
- **Fase 6:** Mostrarás datos en la UI usando getters
- **Fase 9:** Calcularás estadísticas recorriendo objetos con getters

**📚 MIGRACIÓN DE MOVEMENTCALCULATOR:**
```
FASE 1 (Procedural):
MovementCalculator.calculateTotal(quantity, price)  // Método estático separado

FASE 2 (POO - AHORA):
movement.getTotalValue()  // El objeto se calcula a sí mismo
```

**Prompts sugeridos:**
```text
"¿Qué es un getter? Muéstrame la sintaxis básica."
"¿Cuándo NO debo crear un setter para un atributo?"
"¿Cómo valido datos dentro de un setter? Dame ejemplos."
"¿Cuál es la diferencia entre un getter normal y getTotalValue()?"
```

**Tareas paso a paso:**

**Diagrama de tareas - Métodos de acceso (Getters y Setters):**

```
Movement.java - Acceso Controlado a Atributos
│
├── GETTERS (Lectura)
│   ├── getId()
│   │   └── Retorna: this.id
│   │
│   ├── getMovementType()
│   │   └── Retorna: this.movementType
│   │
│   ├── getFuelType()
│   │   └── Retorna: this.fuelType
│   │
│   ├── getQuantity()
│   │   └── Retorna: this.quantity
│   │
│   ├── getUnitPrice()
│   │   └── Retorna: this.unitPrice
│   │
│   └── getMovementDate()
│       └── Retorna: this.movementDate
│
├── MÉTODOS DE CÁLCULO (Derivados)
│   ├── getTotalValue()
│   │   ├── Tipo retorno: double
│   │   ├── Fórmula: quantity × unitPrice
│   │   ├── Usa: this.quantity y this.unitPrice
│   │   └── Ejemplo: 100 litros × $3.45 = $345.00
│   │
│   ├── getIVA()
│   │   ├── Tipo retorno: double
│   │   ├── Fórmula: getTotalValue() × 0.19
│   │   ├── Usa: Llama a getTotalValue() (reutilización)
│   │   └── Depende de: AppConfig.IVA_RATE
│   │
│   └── getTotalWithIVA()
│       ├── Tipo retorno: double
│       ├── Fórmula: getTotalValue() + getIVA()
│       ├── Usa: Llama a otros métodos de cálculo
│       └── Concepto: Reutiliza métodos en lugar de duplicar lógica
│
├── SETTERS (Modificación con validación)
│   ├── setQuantity(double quantity)
│   │   ├── Validación: quantity > 0
│   │   ├── Si NO valida: Mostrar mensaje, NO asignar
│   │   └── Si SÍ valida: this.quantity = quantity
│   │
│   ├── setUnitPrice(double unitPrice)
│   │   ├── Validación: unitPrice > 0
│   │   ├── Si NO valida: Mostrar mensaje, NO asignar
│   │   └── Si SÍ valida: this.unitPrice = unitPrice
│   │
│   └── setMovementType(String movementType)
│       ├── Validación: "ENTRADA" o "SALIDA"
│       ├── Usa: .equals() para comparar Strings (NO ==)
│       ├── Si NO valida: Mostrar mensaje, NO asignar
│       └── Si SÍ valida: this.movementType = movementType
│
└── SIN SETTERS (Atributos inmutables)
    ├── NO crear setter para id (nunca cambia)
    └── NO crear setter para movementDate (la fecha es histórica)
```

**Tareas paso a paso:**

1. **Crear tu PRIMER getter (ejemplo didáctico):**
   
   Lee y ENTIENDE este patrón:
   
   ```
   public TipoDeRetorno getNombreAtributo() {
       return this.nombreAtributo;
   }
   
   Ejemplo real:
   public String getId() {
       return this.id;
   }
   ```
   
   **Anatomía:**
   - `public`: Otros pueden llamarlo
   - `String`: Tipo de dato que retorna (igual al atributo)
   - `getId`: Convención: get + NombreAtributo (camelCase)
   - `return this.id`: Devuelve el valor del atributo
   - NO modifica nada, solo lectura

2. **AHORA TÚ - Crear getters para todos los atributos:**
   
   Usando el patrón que viste en el paso 1, TÚ escribes:
   
   - [ ] `getMovementType()` retorna this.movementType
   - [ ] `getFuelType()` retorna this.fuelType
   - [ ] `getQuantity()` retorna this.quantity
   - [ ] `getUnitPrice()` retorna this.unitPrice
   - [ ] `getMovementDate()` retorna this.movementDate
   
   **Pista:** Usa el mismo patrón que `getId()`, solo cambia:
   - El tipo de retorno (según el atributo)
   - El nombre del método
   - El atributo que retornas

3. **Crear método de cálculo `getTotalValue()` (migrado de MovementCalculator):**
   
   Este NO es un getter normal - no hay un atributo `totalValue`.
   Es un **método de cálculo** que deriva un valor de otros atributos.
   
   **Especificaciones:**
   - Tipo de retorno: double
   - Sin parámetros (¿por qué? los datos están en `this`)
   - Fórmula: `this.quantity * this.unitPrice`
   - Retorna el resultado
   
   **Pregunta guía:** ¿Por qué este método no necesita parámetros si multiplica dos valores?

4. **AHORA TÚ - Crear métodos adicionales de cálculo:**
   
   Siguiendo el patrón de `getTotalValue()`, TÚ implementas:
   
   a) **`getIVA()`:**
      - Tipo de retorno: double
      - Fórmula: `getTotalValue() * 0.19`
      - 💡 **PISTA:** Reutiliza getTotalValue() en lugar de duplicar la lógica
   
   b) **`getTotalWithIVA()`:**
      - Tipo de retorno: double
      - Fórmula: `getTotalValue() + getIVA()`
      - 💡 **PISTA:** Llama ambos métodos, sumalos

5. **Crear tu PRIMER setter con validación (ejemplo didáctico):**
   
   Lee y ENTIENDE este patrón:
   
   ```
   public void setNombreAtributo(Tipo valor) {
       // Paso 1: VALIDAR
       if (condición invalida) {
           System.out.println("Error: mensaje claro");
           return;  // Sale sin asignar
       }
       
       // Paso 2: Si llegó aquí, es válido
       this.nombreAtributo = valor;
   }
   
   Ejemplo real:
   public void setQuantity(double quantity) {
       if (quantity <= 0) {
           System.out.println("⚠️ Error: Cantidad debe ser > 0");
           return;
       }
       this.quantity = quantity;
   }
   ```
   
   **Anatomía:**
   - `public void`: No retorna nada
   - `setQuantity`: Convención: set + NombreAtributo
   - Parámetro: mismo tipo que el atributo
   - **Validación PRIMERO**: evita estados inválidos
   - `return` temprano si es inválido
   - `this.` solo si es válido

6. **AHORA TÚ - Crear setters adicionales con validación:**
   
   Siguiendo el patrón del paso 5, TÚ implementas:
   
   a) **`setUnitPrice(double unitPrice)`:**
      - Valida: unitPrice > 0
      - Si NO: muestra mensaje y retorna
      - Si SÍ: asigna con this.
   
   b) **`setMovementType(String movementType)`:**
      - Valida: debe ser "ENTRADA" o "SALIDA"
      - **Recuerda:** Usa `.equals()` para comparar Strings (NUNCA `==`)
      - Si NO: muestra mensaje y retorna
      - Si SÍ: asigna con this.

7. **IMPORTANTE - NO crear setters para:**
   
   - `id`: NUNCA debe cambiar después de creado
   - `movementDate`: La fecha de creación es histórica e inmutable
   
   **Ventaja:** Si alguien intenta `movement.setId("hack")`, el código ni compilará (el método no existe).

8. **Probar en Main.java:**
   
   **TÚ escribes código que pruebe TODO:**
   
   ```
   a) Crear un movimiento:
      Movement m = new Movement("ENTRADA", "Diesel", 100.0, 3.45);
   
   b) PROBAR GETTERS (lectura):
      - Imprime: m.getId()
      - Imprime: m.getQuantity()
      - Imprime: m.getUnitPrice()
   
   c) PROBAR CÁLCULOS:
      - Imprime: m.getTotalValue()  → ¿es 345.00?
      - Imprime: m.getIVA()  → ¿es ~65.55?
      - Imprime: m.getTotalWithIVA()  → ¿es ~410.55?
   
   d) PROBAR SETTERS VÁLIDOS:
      - m.setQuantity(150.0)
      - Imprime cantidad (debe ser 150)
      - Imprime nuevo total (debe ser diferente)
   
   e) PROBAR SETTERS INVÁLIDOS:
      - m.setQuantity(-50)  → Debe rechazarse
      - m.setUnitPrice(0)  → Debe rechazarse
      - m.setMovementType("VENTA")  → Debe rechazarse
      - Verifica que los valores NO cambiaron
   ```

**✅ Resultado esperado:** 
- Puedes leer todos los datos con getters
- Puedes calcular total, IVA y total con IVA sin llamar a MovementCalculator
- Puedes modificar cantidad y precio con validación
- Los valores inválidos son rechazados con mensajes claros
- El objeto siempre mantiene un estado válido
- Main.java muestra todo correctamente

**💡 Concepto clave - Getter simple vs Método de cálculo:**
- **Getter simple:** Retorna un atributo existente (`getId()`, `getQuantity()`)
- **Método de cálculo:** Calcula un valor derivado (`getTotalValue()`, `getIVA()`)
- Ambos empiezan con `get`, pero su propósito es diferente

**🎓 Ventajas del encapsulamiento en Forestech:**
1. **Validación centralizada:** Toda validación de cantidad está en UN lugar (el setter)
2. **Cambios fáciles:** Si el IVA cambia, solo modificas `getIVA()`
3. **Depuración simple:** Breakpoint en el setter ve TODAS las modificaciones
4. **Código autodocumentado:** `movement.getTotalWithIVA()` es más claro que `calculate(m.q, m.p, IVA)`

**🔍 Depuración obligatoria:**
1. Coloca breakpoint en `setQuantity()`
2. Ejecuta el código de prueba
3. Observa cómo el setter valida ANTES de asignar
4. Ve cómo rechaza el valor negativo

**⚠️ ERROR COMÚN:**
```java
public void setQuantity(double quantity) {
    quantity = quantity;  // ❌ Falta this - no hace nada
    this.quantity = quantity;  // ✅ CORRECTO
}
```

**📊 ANTES vs DESPUÉS:**
```
FASE 1: MovementCalculator.calculateSubtotal(100.0, 3.45)
        MovementCalculator.calculateIVA(subtotal)
        MovementCalculator.calculateTotal(subtotal, iva)

FASE 2: movement.getTotalValue()
        movement.getIVA()
        movement.getTotalWithIVA()
        
→ El objeto se calcula a sí mismo. Más natural y menos errores.
```

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 2.4: Sobrescritura de `toString()` - Representación Legible

**Concepto clave:** Definir cómo se representa un objeto cuando lo imprimes, haciendo debugging más fácil.

**📍 DÓNDE:** 
- **Clase existente:** `Movement.java` (después de getters/setters)
- **Main.java:** Para probar toString()

**🎯 PARA QUÉ:** 
Sin `toString()`, al imprimir ves `Movement@2a84aee7` (inútil). Con `toString()`:
- ✅ **Depurar** fácilmente (ver todos los datos del objeto)
- ✅ **Mostrar** información legible en consola
- ✅ **Verificar** datos correctos sin debugger

**🔗 CONEXIÓN FUTURA:**
- **Fase 6:** Mostrarás listas de movimientos en el menú
- **Fase 8:** Logging - registrar objetos completos en archivos
- **Fase 9:** Reportes en consola

**Prompts sugeridos:**
```text
"¿Qué ocurre si imprimo un objeto sin sobrescribir toString()?"
"Muéstrame ejemplos de String.format con diferentes especificadores."
"¿Qué es la anotación @Override y por qué usarla?"
```

**Tareas paso a paso:**

**Diagrama de tareas - Método toString():**

```
Movement.java - toString() para representación legible
│
└── toString()
    ├── Anotación: @Override
    │   └── Indica que sobrescribe un método de la clase padre (Object)
    │
    ├── Firma: public String toString()
    │   ├── Modificador: public
    │   ├── Retorno: String
    │   └── Sin parámetros
    │
    ├── Contenido:
    │   └── return String.format("patrón", valor1, valor2, ...)
    │
    ├── Formato sugerido:
    │   Movement[id=..., type=..., fuel=..., qty=..., price=$..., total=$...]
    │
    ├── Especificadores de formato:
    │   • %s = String
    │   • %.2f = double con 2 decimales
    │   • ID sugerido: mostrar solo primeros 8 caracteres (.substring(0, 8))
    │
    ├── Valores a mostrar:
    │   1. ID (parcial)
    │   2. Tipo de movimiento
    │   3. Tipo de combustible
    │   4. Cantidad (2 decimales)
    │   5. Precio unitario (2 decimales, formato moneda $)
    │   6. Total calculado (getTotalValue())
    │
    └── Resultado esperado:
        Movement[id=a3f2b4c5, type=ENTRADA, fuel=Diesel, qty=100.00, price=$3.45, total=$345.00]
```

**Tareas paso a paso:**

1. **Entender @Override (boilerplate permitido):**
   
   `@Override` es una anotación que le dice a Java:
   - "Este método sobrescribe uno de la clase padre (Object)"
   - Ventaja: Si escribes mal el nombre, Java te avisa con error
   
   Simplemente: agrega `@Override` encima del método, eso es todo.

2. **Estudiar String.format() con especificadores:**
   
   Lee y ENTIENDE estos ejemplos:
   
   ```
   String.format("Hola %s", "Juan")
   → Resultado: "Hola Juan"
   
   String.format("Precio: $%.2f", 345.1)
   → Resultado: "Precio: $345.10"
   
   String.format("ID: %s, Qty: %.2f", "abc123", 100.5)
   → Resultado: "ID: abc123, Qty: 100.50"
   ```
   
   **Especificadores comunes:**
   - `%s`: String
   - `%d`: entero
   - `%f`: double (por defecto 6 decimales)
   - `%.2f`: double con exactamente 2 decimales
   
   **Tu tarea:** Entiende por qué `%.2f` es mejor que `%f` para dinero.

3. **Ver el patrón de toString() (referencia didáctica):**
   
   Estructura general:
   
   ```
   @Override
   public String toString() {
       return String.format(
           "Movement[campos y valores con formato]",
           this.atributo1,
           this.atributo2,
           método de cálculo,
           ...
       );
   }
   ```

4. **AHORA TÚ - Implementar toString():**
   
   Usando el diagrama como guía, TÚ escribes:
   
   - [ ] Agrega `@Override` encima
   - [ ] Declara: `public String toString() {`
   - [ ] Usa `String.format()` con patrón:
     - Muestra: id (primeros 8 chars), movementType, fuelType, quantity, unitPrice, getTotalValue()
     - Formatos: Strings con %s, números con %.2f, moneda con $ delante
   - [ ] Retorna el resultado
   - [ ] Cierra el método
   
   **Pista:** Tu formato podría verse así (pero ajusta el texto según quieras):
   ```
   "Movement[id=%s, type=%s, fuel=%s, qty=%.2f, price=$%.2f, total=$%.2f]"
   ```

5. **Probar en Main.java:**
   
   **TÚ escribes:**
   
   ```
   a) Crear un movimiento:
      Movement m = new Movement("ENTRADA", "Diesel", 100.0, 3.45);
   
   b) Imprimir directamente:
      System.out.println(m);
      → Ahora verás datos legibles en lugar de "Movement@2a84aee7"
   
   c) Crear varios movimientos:
      Movement m2 = new Movement("SALIDA", "Gasolina 93", 50.0, 4.20);
      System.out.println(m2);
      
   d) Compara la salida:
      ¿Cambian los valores?
      ¿Tienen IDs diferentes?
   ```
   
   **Resultado esperado:**
   ```
   Movement[id=a3f2b4c5, type=ENTRADA, fuel=Diesel, qty=100.00, price=$3.45, total=$345.00]
   Movement[id=d6e8f1a2, type=SALIDA, fuel=Gasolina 93, qty=50.00, price=$4.20, total=$210.00]
   ```

6. **Validar en el debugger:**
   
   - Ejecuta Main.java
   - Coloca breakpoint en `System.out.println(m);`
   - Inspecciona el objeto en el panel de variables
   - Compara el toString() impreso con los valores reales
   - ¿Coinciden?

**✅ Resultado esperado:** 
Al imprimir un Movement, ver todos sus datos formateados y legibles.

**🔍 Actividad de reflexión:**
Inspecciona el objeto en el debugger y compara con la salida de toString(). ¿Coinciden los valores?

**💡 Tip:** Puedes personalizar el formato. Por ejemplo, agregar emojis: `"🚛 Movement[...]"` o cambiar el orden de los campos.

**⏱️ Tiempo estimado:** 1 hora

---

## ✅ Checkpoint 2.5: Más Clases del Dominio - Vehicle, Supplier, Product

**Concepto clave:** Modelar todas las entidades principales de Forestech. Cada concepto del mundo real merece su propia clase.

**📍 DÓNDE:** 
- **Paquete existente:** `models/`
- **Crear archivos:** `Vehicle.java`, `Supplier.java`, `Product.java`
- **Main.java:** Para probar cada clase

**🎯 PARA QUÉ:** 
Forestech no solo maneja movimientos. Necesitas representar:
- **Vehicle:** Vehículos/camiones que reciben combustible
- **Supplier:** Proveedores de donde viene el combustible
- **Product:** Tipos de combustible disponibles

Cada entidad tiene sus propios atributos y comportamientos → merece su clase.

**🔗 CONEXIÓN FUTURA:**
- **Fase 3-4:** Crearás tablas SQL para estas entidades
- **Fase 5:** Relacionarás Movement con Vehicle y Supplier (¿qué camión?, ¿de qué proveedor?)
- **Fase 6:** El usuario gestionará vehículos y proveedores desde el menú
- **Fase 9:** Reportes por proveedor o por vehículo

**Prompts sugeridos:**
```text
"¿Cómo decido si algo debe ser una clase separada o un atributo de otra clase?"
"Sugiere atributos realistas para Vehicle, Supplier y Product en Forestech."
"¿Qué es una relación entre clases? Dame ejemplos."
```

**Tareas paso a paso:**

**Diagrama de tareas - Nuevas clases del dominio:**

```
Vehicle.java
├── Atributos privados:
│   ├── String id → Generado con UUID
│   ├── String placa → Ej: "ABC123"
│   ├── String modelo → Ej: "Volvo FH16"
│   ├── String categoria → Ej: "Cisterna" o "Camión"
│   └── double capacidadLitros → Cuántos litros puede cargar
│
├── Constructores:
│   ├── Principal: recibe placa, modelo, categoria, capacidadLitros
│   └── Vacío: solo inicializa id y fecha
│
├── Getters: para todos los atributos
│
└── toString(): muestra placa, modelo, capacidad

Supplier.java
├── Atributos privados:
│   ├── String id → Generado con UUID
│   ├── String nombre → Ej: "Petro Colombia S.A."
│   ├── String telefono → Ej: "+57 310 1234567"
│   ├── String email → Ej: "contacto@petrocol.com"
│   └── String direccion → Ej: "Calle 50 #10-20, Bogotá"
│
├── Constructores:
│   ├── Principal: recibe nombre, telefono, email, direccion
│   └── Vacío: solo inicializa id
│
├── Getters: para todos los atributos
│
└── toString(): muestra nombre, teléfono, ciudad

Product.java
├── Atributos privados:
│   ├── String id → Generado con UUID
│   ├── String nombre → Ej: "Diesel Extra"
│   ├── String tipoCombustible → Ej: "Diesel" o "Gasolina"
│   ├── String unidadMedida → Ej: "Litros"
│   └── double precioEstandar → Precio base actual
│
├── Constructores:
│   ├── Principal: recibe nombre, tipoCombustible, unidadMedida, precioEstandar
│   └── Vacío: solo inicializa id
│
├── Getters: para todos los atributos
│
├── Métodos de cálculo:
│   └── getPrecioConDescuento(double porcentaje)
│       └── Fórmula: precioEstandar * (1 - porcentaje/100)
│
└── toString(): muestra nombre y precio
```

**Tareas paso a paso:**

1. **Analizar y diseñar ANTES de codificar:**
   
   En papel o en tu IDE, dibuja 3 cajas (Vehicle, Supplier, Product).
   
   Para cada una, escribe:
   - [ ] ¿Qué datos reales necesita?
   - [ ] ¿Cuáles deben ser inmutables? (sin setter)
   - [ ] ¿Qué cálculos derivados tiene?

2. **Crear clase Vehicle (TÚ debes hacerlo):**
   
   **Paso a paso:**
   
   a) En el paquete `models`, crea nuevo archivo: `Vehicle.java`
   
   b) Declara la clase pública
   
   c) Agrega 5 atributos privados (ver diagrama):
      - id (String)
      - placa (String)
      - modelo (String)
      - categoria (String)
      - capacidadLitros (double)
   
   d) Constructor principal que recibe: placa, modelo, categoria, capacidadLitros
      - Genera UUID para id
      - Asigna los 4 parámetros a sus atributos
   
   e) Constructor vacío que solo genera el id
   
   f) Getters para todos los atributos
      - NO crear setters (el vehículo no cambia sus características)
   
   g) toString() legible mostrando placa, modelo, categoría
   
   **Verificación:**
   - [ ] Compila sin errores
   - [ ] Puedes crear Vehicle en Main.java
   - [ ] toString() muestra información útil

3. **Crear clase Supplier (TÚ debes hacerlo):**
   
   **Paso a paso:**
   
   a) Crea `Supplier.java` en el paquete `models`
   
   b) Agrega 5 atributos privados:
      - id (String)
      - nombre (String)
      - telefono (String)
      - email (String)
      - direccion (String)
   
   c) Constructor principal que recibe: nombre, telefono, email, direccion
      - Genera UUID para id
      - Asigna los 4 parámetros
   
   d) Constructor vacío
   
   e) Getters para todos
      - NO crear setter para id
      - Considera: ¿hay otros que NO deberían cambiar?
   
   f) toString() mostrando nombre y teléfono
   
   **Pregunta reflexiva:** ¿Deberías poder cambiar el email de un proveedor? ¿Y su dirección?

4. **Crear clase Product (TÚ debes hacerlo):**
   
   **Paso a paso:**
   
   a) Crea `Product.java` en el paquete `models`
   
   b) Agrega 5 atributos privados:
      - id (String)
      - nombre (String)
      - tipoCombustible (String)
      - unidadMedida (String)
      - precioEstandar (double)
   
   c) Constructor principal que recibe: nombre, tipoCombustible, unidadMedida, precioEstandar
      - Genera UUID para id
      - Asigna parámetros
   
   d) Constructor vacío
   
   e) Getters para todos
      - NO crear setter para id ni tipoCombustible
   
   f) Método de cálculo `getPrecioConDescuento(double porcentaje)`:
      - Recibe: un porcentaje (ej: 10 para 10%)
      - Retorna: `precioEstandar * (1 - porcentaje/100)`
      - Ejemplo: precio 100, descuento 10% → retorna 90
   
   g) toString() mostrando nombre y precio
   
   **Desafío:** ¿Por qué es mejor tener `getPrecioConDescuento()` como método en lugar de cambiar `precioEstandar`?

5. **Probar CADA clase en Main.java:**
   
   **TÚ escribes pruebas para Vehicle:**
   ```
   a) Crear: Vehicle v = new Vehicle("ABC123", "Volvo FH16", "Cisterna", 5000.0);
   
   b) Imprimir: System.out.println(v);
   
   c) Verificar getters:
      - System.out.println("Placa: " + v.getPlaca());
      - System.out.println("Modelo: " + v.getModelo());
   ```
   
   **TÚ escribes pruebas para Supplier:**
   ```
   a) Crear: Supplier s = new Supplier("Petro Colombia", "+57310...", "info@...", "Calle 50");
   
   b) Imprimir: System.out.println(s);
   
   c) Verificar getters
   ```
   
   **TÚ escribes pruebas para Product:**
   ```
   a) Crear: Product p = new Product("Diesel Extra", "Diesel", "Litros", 3.45);
   
   b) Imprimir: System.out.println(p);
   
   c) Probar descuento:
      - double precioConDesc = p.getPrecioConDescuento(10);  // 10% descuento
      - System.out.println("Precio con 10% desc: $" + precioConDesc);
   ```

6. **Reflejar sobre el diseño:**
   
   Después de crear las 3 clases, responde:
   - [ ] ¿Hay atributos duplicados entre clases? (malo)
   - [ ] ¿Cada clase representa UNA entidad clara? (bueno)
   - [ ] ¿Hay métodos que podrían mejorar? (ej: validar email en Supplier)
   - [ ] ¿Ves relaciones entre las clases? (Movement podría tener vehicleId y supplierId)

**✅ Resultado esperado:** 
- 4 clases funcionales (Movement, Vehicle, Supplier, Product)
- Cada una compila sin errores
- toString() muestra datos legibles
- Puedes crear objetos de cada tipo en Main
- Entiendes qué atributos van en cada clase

**💡 Concepto clave - Separación de Responsabilidades:**
Cada clase representa UNA cosa del mundo real. Movement NO debería tener atributos de Vehicle. Son entidades diferentes que se pueden relacionar mediante IDs.

**⚠️ NO hagas esto:**
```java
// ❌ MAL - mezclar responsabilidades
class Movement {
    private String vehiclePlate;  // Esto va en Vehicle
    private String supplierName;  // Esto va en Supplier
}
```

**✅ HAZ esto:**
```java
// ✅ BIEN - separación clara
class Movement {
    private String vehicleId;   // Referencia a Vehicle
    private String supplierId;  // Referencia a Supplier
}
```

**🎓 Reflexión:** ¿Por qué es mejor tener 4 clases pequeñas que 1 clase gigante con todo mezclado?

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 2.6: Colecciones con `ArrayList<Movement>`

**Concepto clave:** Almacenar y manipular listas dinámicas de objetos.

**📍 DÓNDE:** 
- **Main.java:** Por ahora trabajarás aquí (en Fase 5 moverás esto a Services)

**🎯 PARA QUÉ:** 
Un array normal tiene tamaño fijo (`Movement[] movements = new Movement[10]` → máximo 10).

ArrayList es dinámico:
- ✅ **Agregar** movimientos sin saber cuántos habrá
- ✅ **Eliminar** movimientos
- ✅ **Buscar** un movimiento específico por ID o tipo
- ✅ **Recorrer** todos para calcular totales
- ✅ **Tamaño flexible** - crece automáticamente

**🔗 CONEXIÓN FUTURA:**
- **Fase 3:** Cargarás movimientos desde la BD a un ArrayList
- **Fase 4:** Operarás sobre ArrayList antes de guardar en BD
- **Fase 5:** Crearás Services que retornen ArrayList<Movement>
- **Fase 6:** Mostrarás el ArrayList en el menú
- **Fase 9:** Recorrerás ArrayList para generar reportes y estadísticas

**Prompts sugeridos:**
```text
"Diferénciame array vs ArrayList con analogía concreta."
"¿Qué significa el <Movement> en ArrayList<Movement>? (generics)"
"¿Por qué no puedo usar ArrayList<double>? ¿Qué hago en su lugar?"
"¿Cuál es la diferencia entre .size() y .length?"
```

**Tareas paso a paso:**

**Diagrama de tareas - Trabajar con ArrayList<Movement>:**

```
Main.java - Operaciones con ArrayList
│
├── 1. Crear el ArrayList
│   └── ArrayList<Movement> movements = new ArrayList<>();
│
├── 2. Operaciones básicas
│   ├── Agregar: movements.add(objeto);
│   ├── Tamaño: movements.size()
│   ├── Obtener: movements.get(indice)
│   └── Eliminar: movements.remove(indice)
│
├── 3. Recorrer la lista
│   ├── Forma 1 - for clásico:
│   │   for (int i = 0; i < movements.size(); i++) {
│   │       Movement m = movements.get(i);
│   │   }
│   │
│   └── Forma 2 - for-each (preferida):
│       for (Movement m : movements) {
│           // Procesar m
│       }
│
├── 4. Búsquedas y filtros
│   ├── Encontrar el PRIMER elemento que cumpla condición:
│   │   for (...) {
│   │       if (condición) {
│   │           guardar resultado;
│   │           break;  // Sal del bucle
│   │       }
│   │   }
│   │
│   └── Encontrar TODOS los elementos (crear lista nueva):
│       ArrayList<Movement> resultado = new ArrayList<>();
│       for (Movement m : movements) {
│           if (condición) {
│               resultado.add(m);
│           }
│       }
│
└── 5. Cálculos sobre la colección
    └── Acumular valores:
        double total = 0;
        for (Movement m : movements) {
            total += m.getTotalValue();
        }
```

**Tareas paso a paso:**

1. **Importar ArrayList (boilerplate permitido):**
   
   En Main.java, agrega al inicio:
   ```
   import java.util.ArrayList;
   ```

2. **Crear tu primer ArrayList (TÚ lo escribes):**
   
   En el método main(), escribe:
   ```
   ArrayList<Movement> movements = new ArrayList<>();
   ```
   
   **Entiende:**
   - `ArrayList<Movement>`: Tipo - una lista de objetos Movement
   - `movements`: Nombre de tu variable
   - `new ArrayList<>()`: Crea la lista vacía y lista para crecer
   - `<>`: El compilador infiere automáticamente que es de Movement

3. **Crear 3 movimientos y agregarlos:**
   
   **TÚ escribes:**
   
   a) Crea movimiento 1: ENTRADA de Diesel, 100 litros, $3.45
   
   b) Agrégalo: `movements.add(entrada1);`
   
   c) Crea movimiento 2: SALIDA de Gasolina 93, 50 litros, $4.20
   
   d) Agrégalo: `movements.add(salida1);`
   
   e) Crea movimiento 3: ENTRADA de ACPM, 75 litros, $2.90
   
   f) Agrégalo: `movements.add(entrada2);`
   
   **Resultado:** ArrayList con 3 objetos Movement

4. **Probar operaciones básicas:**
   
   **TÚ escribes y verifica:**
   
   a) Obtener el tamaño:
      ```
      System.out.println("Total: " + movements.size());
      → Debe mostrar: "Total: 3"
      ```
   
   b) Obtener el primer movimiento:
      ```
      Movement primero = movements.get(0);
      System.out.println(primero);
      ```
   
   c) Obtener el tercero:
      ```
      Movement tercero = movements.get(2);
      System.out.println(tercero);
      ```
   
   d) Intentar obtener el 4º (no existe):
      ```
      Movement inexistente = movements.get(3);
      → ¿Qué error ves? IndexOutOfBoundsException
      ```
   
   e) Eliminar el del medio:
      ```
      movements.remove(1);
      System.out.println("Después de eliminar: " + movements.size());
      → Debe mostrar: "Después de eliminar: 2"
      ```

5. **Recorrer e imprimir TODOS (Forma 1 - for clásico):**
   
   **TÚ escribes:**
   
   ```
   for (int i = 0; i < movements.size(); i++) {
       Movement m = movements.get(i);
       System.out.println("Movimiento " + (i+1) + ": " + m);
   }
   ```
   
   **Entiende:**
   - `i = 0`: Empieza en el índice 0 (primer elemento)
   - `i < movements.size()`: Continúa mientras i sea menor al tamaño
   - `movements.get(i)`: Obtiene el elemento en posición i
   - `i+1` en la impresión: Porque usuarios cuentan desde 1, no desde 0

6. **Recorrer e imprimir TODOS (Forma 2 - for-each, PREFERIDA):**
   
   **TÚ escribes:**
   
   ```
   for (Movement m : movements) {
       System.out.println(m);
   }
   ```
   
   **Ventajas sobre form 1:**
   - Más legible
   - No necesitas índices
   - Menos errores por "off-by-one"
   
   **Pregunta:** ¿Cuándo usarías forma 1 vs forma 2?
   - Forma 1: Cuando necesites el índice
   - Forma 2: Cuando solo necesites iterar

7. **Calcular el total de TODOS los movimientos:**
   
   **TÚ escribes:**
   
   ```
   a) Declare acumulador:
      double granTotal = 0;
   
   b) Recorre la lista:
      for (Movement m : movements) {
          granTotal += m.getTotalValue();
      }
   
   c) Imprime:
      System.out.println("Gran total: $" + granTotal);
   ```
   
   **Entiende:**
   - `granTotal += m.getTotalValue()` es lo mismo que `granTotal = granTotal + m.getTotalValue()`
   - Vas acumulando el total de cada movimiento
   - Al final tienes el total de todos

8. **Búsqueda simple - Encontrar el PRIMER movimiento de tipo "ENTRADA":**
   
   **TÚ escribes:**
   
   ```
   a) Variable para guardar el resultado:
      Movement primeraEntrada = null;
   
   b) Recorre buscando:
      for (Movement m : movements) {
          if (m.getMovementType().equals("ENTRADA")) {
              primeraEntrada = m;
              break;  // Sal del bucle
          }
      }
   
   c) Verifica:
      if (primeraEntrada != null) {
          System.out.println("Primera entrada: " + primeraEntrada);
      } else {
          System.out.println("No hay entradas");
      }
   ```
   
   **Importante:**
   - `.equals()` para comparar Strings (NO ==)
   - `break` para salir apenas encuentres el primero
   - `!= null` para verificar que encontraste algo

9. **Búsqueda avanzada - Encontrar TODOS los movimientos de "SALIDA":**
   
   **TÚ escribes:**
   
   ```
   a) Create lista nueva para resultados:
      ArrayList<Movement> salidas = new ArrayList<>();
   
   b) Recorre y filtra:
      for (Movement m : movements) {
          if (m.getMovementType().equals("SALIDA")) {
              salidas.add(m);
          }
      }
   
   c) Imprime resultados:
      System.out.println("Total salidas: " + salidas.size());
      for (Movement s : salidas) {
          System.out.println(s);
      }
   ```

10. **Desafío - Encontrar el movimiento con MAYOR cantidad:**
    
    **TÚ escribes:**
    
    ```
    a) Variable para guardar el máximo:
       Movement movimientoMayor = null;
       double cantidadMaxima = 0;
    
    b) Recorre comparando:
       for (Movement m : movements) {
           if (m.getQuantity() > cantidadMaxima) {
               cantidadMaxima = m.getQuantity();
               movimientoMayor = m;
           }
       }
    
    c) Imprime:
       if (movimientoMayor != null) {
           System.out.println("Mayor cantidad: " + movimientoMayor);
       }
    ```

**✅ Resultado esperado:** 
- Lista de movimientos que crece/decrece dinámicamente
- Puedes agregar, eliminar, buscar, filtrar
- Puedes calcular estadísticas recorriendo la lista
- Entiendes la diferencia entre array y ArrayList
- Sabes usar `.add()`, `.get()`, `.remove()`, `.size()`

**💡 Concepto clave - Generics `<Movement>`:**
```java
ArrayList<Movement> movements;  // ✅ Solo puede contener Movement
ArrayList movements;  // ⚠️ Puede contener cualquier cosa - peligroso
```

El `<Movement>` garantiza que solo guardes objetos Movement, evitando errores.

**⚠️ CUIDADO con los índices:**
```java
ArrayList<Movement> lista = new ArrayList<>();
lista.add(m1);  // Índice 0
lista.add(m2);  // Índice 1
lista.add(m3);  // Índice 2

lista.get(0);   // ✅ m1
lista.get(3);   // ❌ IndexOutOfBoundsException - no existe
```

Los índices van de 0 a (size - 1).

**🎓 Array vs ArrayList:**
| Característica | Array | ArrayList |
|---------------|-------|-----------|
| Tamaño | Fijo | Dinámico |
| Sintaxis | `Movement[]` | `ArrayList<Movement>` |
| Agregar | NO puedes | `.add()` |
| Tamaño | `.length` | `.size()` |
| Cuándo usar | Tamaño conocido | Tamaño variable |

**🔍 Depuración obligatoria:**
1. Coloca breakpoint en el for que recorre el ArrayList
2. Observa en el panel de variables cómo se ve el ArrayList
3. Usa "Step Over" para ver cómo cambia el índice
4. Inspecciona cada Movement mientras recorres

**📊 Práctica adicional:**
Implementa un método en Main.java:
```java
public static double calcularTotalPorTipo(ArrayList<Movement> lista, String tipo) {
    // Suma el total de todos los movimientos del tipo especificado
    // Retorna el gran total
}
```

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✏️ Refuerzos adicionales de la fase

- 📊 **Quiz autoevaluación:** Pide a tu IA 6 preguntas (teóricas y de código) cubriendo clases, constructores, encapsulamiento, `toString` y ArrayList.
- 🧪 **Mini-ejercicio extra:** Modela una clase `Driver` (conductor) con atributos relevantes. Agrégala a tu diagrama. ¿Se relaciona con Vehicle o Movement?
- 📌 **Registro de aprendizaje:** Documenta en JAVA_LEARNING_LOG.md las decisiones de diseño y patrones que observaste.
- 🐞 **Buen hábito:** Cuando un setter rechace un valor, captura el stacktrace en depuración para entender el flujo completo.
- 🎨 **Diagrama final:** Actualiza tu diagrama de clases con TODAS las clases y sus relaciones.

---

## ✅ Checklist de salida de Fase 2

Antes de pasar a Fase 3, verifica que puedes responder SÍ a todo:

**Comprensión de conceptos:**
- [ ] Puedo explicar con mis palabras qué es una clase vs un objeto
- [ ] Entiendo para qué sirve un constructor y cuándo crear múltiples (sobrecarga)
- [ ] Sé qué es encapsulamiento y por qué los atributos son privados
- [ ] Entiendo cuándo crear/NO crear setters para atributos
- [ ] Puedo explicar la diferencia entre un getter normal y un método de cálculo
- [ ] Mi toString() muestra información útil para depurar
- [ ] Entiendo la diferencia entre array y ArrayList
- [ ] Sé qué son los generics (`<Movement>`) y para qué sirven

**Habilidades prácticas:**
- [ ] Creé al menos 4 clases del dominio (Movement, Vehicle, Supplier, Product)
- [ ] Cada clase tiene constructores, getters, setters selectivos y toString()
- [ ] Probé cada clase en Main.java y funcionan correctamente
- [ ] Trabajé cómodamente con ArrayList<Movement>
- [ ] Implementé búsquedas, filtros y cálculos sobre ArrayList
- [ ] Usé el debugger para inspeccionar objetos y colecciones
- [ ] Documenté aprendizajes en JAVA_LEARNING_LOG.md
- [ ] Hice commits de Git por cada checkpoint

**Estructura del proyecto:**
- [ ] Tengo el paquete `models/` con 4 clases
- [ ] Cada clase compila sin errores
- [ ] Main.java tiene código de prueba funcional
- [ ] **NINGUNA** clase tiene más de 150 líneas
- [ ] Mi código NO tiene duplicación excesiva

**Entregable funcional:**
- [ ] Puedo crear objetos de cualquier clase
- [ ] Puedo agregar objetos a ArrayList
- [ ] Puedo recorrer ArrayList e imprimir todos los objetos
- [ ] Puedo calcular totales sumando valores de los objetos
- [ ] Puedo buscar y filtrar objetos en ArrayList

**🎯 Desafío final:**
Implementa un método en Main.java:
```java
public static ArrayList<Movement> filtrarPorTipoYMinimo(
    ArrayList<Movement> lista, 
    String tipo, 
    double cantidadMinima
) {
    // Retorna solo los movimientos que:
    // - Sean del tipo especificado
    // - Y tengan cantidad >= cantidadMinima
}
```

**📸 Evidencia:**
Toma screenshots de:
1. Tu diagrama de clases con los 4 modelos
2. Main.java con código de prueba funcionando
3. Output mostrando varios movimientos con toString()
4. Guárdalos como `fase2-diagrama.png`, `fase2-codigo.png`, `fase2-output.png`

---

## 🚀 Próximo paso: FASE 3 - Conexión a SQL Server

En la siguiente fase aprenderás a:
- Configurar el driver JDBC para SQL Server
- Crear la clase DatabaseConnection
- Ejecutar tu primera consulta SELECT
- Convertir ResultSet en objetos Movement
- Entender try-with-resources para cerrar conexiones automáticamente

**¿Por qué necesitas BD?** 
Ahora mismo tus objetos solo existen mientras la aplicación corre. Cuando cierras el programa, TODO se pierde. Con base de datos, la información persiste para siempre.

**Lo que cambiar á:**
- Los ArrayList se llenarán con datos de la BD (no hardcodeados)
- Los objetos que crees se guardarán en la BD
- Tu aplicación será REAL - podrás usarla con datos reales

**⏱️ Tiempo total Fase 2:** 10-15 horas distribuidas en 1-2 semanas
