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

**Tareas paso a paso:**

1. **Crear el paquete `models`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "models"
   - Este paquete contendrá TODAS las clases del dominio (Movement, Vehicle, Supplier, Product)
   - **¿Por qué un paquete separado?** Organización profesional: los modelos de datos van separados de la lógica

2. **Crear la clase Movement:**
   - Clic derecho en `models` → New → Java Class → "Movement"
   - **TÚ debes escribir:** La declaración de clase pública
   - **Pista de sintaxis:**
     ```java
     public class NombreDeLaClase {
         // contenido
     }
     ```

3. **Declarar 6 atributos privados:**
   
   **Piensa:** ¿Qué información ESENCIAL necesitas guardar de un movimiento de combustible en Forestech?
   
   **Debes declarar (TÚ escribes el código):**
   - Identificador único (String) - nombre sugerido: `id`
   - Tipo de movimiento (String) - ¿"ENTRADA" o "SALIDA"?
   - Tipo de combustible (String) - "Diesel", "Gasolina 93", etc.
   - Cantidad (double) - en litros
   - Precio por litro (double)
   - Fecha del movimiento (String por ahora) - luego lo mejoraremos
   
   **Sintaxis de atributo privado:**
   ```java
   private TipoDeDato nombreAtributo;
   ```
   
   **Recuerda:** TODOS deben ser `private` para encapsulamiento.

4. **Agregar comentarios explicativos:**
   - Sobre cada atributo, escribe un comentario breve explicando su propósito
   - Ejemplo: `// Identificador único del movimiento`
   - Esto te ayuda a entender el diseño mientras lo construyes

5. **Verificar que compile:**
   - Ejecuta desde terminal: `mvn clean compile`
   - Debe compilar sin errores (aunque la clase no hace nada aún)

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

2. **Crear tu PRIMER constructor (ejemplo completo - primera vez que lo ves):**
   
   Un constructor es un método especial con estas características:
   - Mismo nombre que la clase
   - NO tiene tipo de retorno (ni void)
   - Se ejecuta automáticamente al hacer `new`
   
   **EJEMPLO DIDÁCTICO (estudia la sintaxis):**
   ```java
   public Movement(String movementType, String fuelType, double quantity, double unitPrice) {
       // this.atributo distingue el atributo de la clase del parámetro
       this.id = UUID.randomUUID().toString();  // Genera ID único
       this.movementType = movementType;
       this.fuelType = fuelType;
       this.quantity = quantity;
       this.unitPrice = unitPrice;
       this.movementDate = LocalDateTime.now().toString();
   }
   ```
   
   **Anatomía del constructor:**
   - `public`: Puede ser llamado desde cualquier lugar
   - `Movement`: Mismo nombre que la clase (NO es un método normal)
   - `(String movementType, ...)`: Parámetros que recibe
   - `this.id = ...`: El `this` se refiere al objeto que se está creando
   - `UUID.randomUUID()`: Genera un ID único automáticamente
   - `LocalDateTime.now()`: Captura la fecha/hora actual

3. **Entiende el flujo (depuración obligatoria):**
   - Coloca un breakpoint en la primera línea del constructor
   - Crea un Movement en Main.java
   - Ejecuta en debug mode
   - Usa "Step Over" (F8) para ver línea por línea cómo se asignan los valores
   - Observa en el panel de variables cómo `this` va tomando forma

4. **AHORA TÚ - Constructor sin parámetros (sobrecarga):**
   
   **Objetivo:** Crear un constructor vacío para casos especiales (frameworks lo necesitan).
   
   **Especificaciones:**
   - No recibe parámetros
   - Genera el id automáticamente (igual que el otro)
   - Genera la fecha automáticamente
   - Los demás atributos quedan en null/0 (se llenarán con setters después)
   
   **Pregunta guía:** ¿Por qué tendrías DOS constructores en la misma clase? ¿Cuándo usarías uno u otro?

5. **Probar en Main.java:**
   
   **Escribe este código de prueba:**
   
   a) Crea un movimiento usando el constructor principal:
      ```java
      Movement entrada1 = new Movement("ENTRADA", "Diesel", 100.0, 3.45);
      ```
   
   b) Imprime el objeto (verás algo como `Movement@2a84aee7` por ahora - lo arreglaremos en Checkpoint 2.4):
      ```java
      System.out.println(entrada1);
      ```
   
   c) Crea otro movimiento diferente para verificar que cada uno tiene su propio ID:
      ```java
      Movement salida1 = new Movement("SALIDA", "Gasolina 93", 50.0, 4.20);
      System.out.println(salida1);
      ```
   
   d) Prueba el constructor vacío:
      ```java
      Movement vacio = new Movement();
      System.out.println(vacio);
      ```

6. **Compilar y ejecutar:**
   - Terminal: `mvn clean compile`
   - IntelliJ: Run Main.java
   - Deberías ver 3 líneas con hashcodes diferentes (cada objeto es único)

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

1. **Crear tu PRIMER getter (ejemplo didáctico):**
   
   Un getter es un método público que retorna el valor de un atributo privado.
   
   **EJEMPLO COMPLETO (estudia el patrón):**
   ```java
   public String getId() {
       return this.id;
   }
   ```
   
   **Anatomía del getter:**
   - `public`: Otros pueden llamarlo
   - `String`: Tipo de dato que retorna (mismo que el atributo)
   - `getId`: Convención: get + NombreAtributo (camelCase)
   - `return this.id`: Devuelve el valor del atributo
   - **NO modifica** nada, solo lectura

2. **AHORA TÚ - Crear getters para los demás atributos:**
   
   **RECUERDA:** Ya viste el patrón en el paso 1. Ahora aplícalo.
   
   **Debes crear getters para:**
   - `movementType` (String) → `getMovementType()`
   - `fuelType` (String) → `getFuelType()`
   - `quantity` (double) → `getQuantity()`
   - `unitPrice` (double) → `getUnitPrice()`
   - `movementDate` (String) → `getMovementDate()`
   
   **Pista:** Usa el mismo patrón que `getId()`, solo cambia:
   - El tipo de retorno (según el atributo)
   - El nombre del método
   - El atributo que retornas

3. **Crear método de cálculo `getTotalValue()` (migrado de MovementCalculator):**
   
   **Diferencia importante:** Este NO es un getter normal porque no hay un atributo `totalValue`. Es un **cálculo derivado**.
   
   **TÚ debes implementarlo con estas especificaciones:**
   - Tipo de retorno: double
   - Sin parámetros (¿por qué? porque los datos están en `this`)
   - Fórmula: cantidad multiplicada por precio unitario
   - Usa `this.quantity` y `this.unitPrice`
   
   **Pregunta guía:** ¿Por qué este método no necesita parámetros si está multiplicando dos valores?

4. **AHORA TÚ - Crear métodos adicionales de cálculo:**
   
   Siguiendo el patrón de `getTotalValue()`, crea:
   
   a) **`getIVA()`** (migrado de MovementCalculator):
      - Tipo de retorno: double
      - Calcula el IVA del movimiento
      - Usa `getTotalValue()` para obtener el subtotal
      - Multiplica por `AppConfig.IVA_RATE` (debes agregarlo a AppConfig si no existe)
   
   b) **`getTotalWithIVA()`** (migrado de MovementCalculator):
      - Tipo de retorno: double
      - Suma el subtotal + IVA
      - **Pista:** Llama a `getTotalValue()` y `getIVA()` - ¡reutiliza métodos!

5. **Agregar constante IVA a AppConfig (si no existe):**
   ```java
   // En AppConfig.java
   public static final double IVA_RATE = 0.19;  // 19% IVA en Colombia
   ```

6. **Crear tu PRIMER setter con validación (ejemplo didáctico):**
   
   Un setter es un método público que modifica un atributo, pero con VALIDACIÓN.
   
   **EJEMPLO COMPLETO (estudia el patrón):**
   ```java
   public void setQuantity(double quantity) {
       // Validar ANTES de asignar
       if (quantity <= 0) {
           System.out.println("⚠️  Error: La cantidad debe ser mayor a 0. Valor rechazado.");
           return;  // Sale del método sin asignar
       }
       // Si llegó aquí, el valor es válido
       this.quantity = quantity;
   }
   ```
   
   **Anatomía del setter:**
   - `public void`: No retorna nada
   - `setQuantity`: Convención: set + NombreAtributo
   - Parámetro: mismo tipo que el atributo
   - **Validación primero**: evita valores inválidos
   - `return` temprano si es inválido
   - Asigna con `this` solo si es válido

7. **AHORA TÚ - Crear setters adicionales con validación:**
   
   Siguiendo el patrón del paso 6, crea setters para:
   
   a) **`setUnitPrice(double unitPrice)`:**
      - Valida que sea > 0
      - Si no, muestra mensaje y no asigna
   
   b) **`setMovementType(String movementType)`:**
      - Valida que sea "ENTRADA" o "SALIDA"
      - **Pista:** Usa `.equals()` para comparar Strings
      - **Recuerda:** NUNCA uses `==` para comparar Strings

8. **NO crear setters para:**
   - `id` - NUNCA debe cambiar después de creado
   - `movementDate` - La fecha de creación es inmutable
   
   **Si alguien intenta `movement.setId("hack")`, el código ni compilará** (el método no existe).

9. **Probar COMPLETO en Main.java:**
   
   **Escribe este código de prueba (TÚ lo escribes):**
   
   a) Crear un movimiento
   
   b) **Probar GETTERS:**
      - Imprime cada dato del movimiento usando los getters
      - Formato: `"ID: " + entrada1.getId()`
   
   c) **Probar MÉTODOS DE CÁLCULO:**
      - Imprime subtotal con `getTotalValue()`
      - Imprime IVA con `getIVA()`
      - Imprime total con IVA con `getTotalWithIVA()`
      - **Verifica:** ¿Los cálculos son correctos?
   
   d) **Probar SETTERS VÁLIDOS:**
      - Cambia la cantidad a un valor válido (ej: 150.0)
      - Imprime la nueva cantidad
      - Imprime el nuevo total (debería cambiar)
   
   e) **Probar SETTERS INVÁLIDOS:**
      - Intenta asignar cantidad negativa (-50)
      - Debería mostrar mensaje de error
      - Verifica que la cantidad NO cambió (debe seguir en 150.0)
      - Intenta asignar precio 0
      - Intenta asignar tipo "VENTA"
      - **Verifica:** Todos deben ser rechazados

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

1. **Entender @Override (boilerplate permitido):**
   
   `@Override` le dice a Java: "Este método sobrescribe uno de la clase padre (Object)".
   
   **Ventaja:** Si escribes mal el nombre, Java te avisa con error de compilación.

2. **Crear método toString() (primera vez - ejemplo didáctico):**
   
   **EJEMPLO COMPLETO:**
   ```java
   @Override
   public String toString() {
       return String.format("Movement[id=%s, type=%s, fuel=%s, qty=%.2f, price=$%.2f, total=$%.2f]",
           this.id.substring(0, 8),  // Solo primeros 8 caracteres del ID
           this.movementType,
           this.fuelType,
           this.quantity,
           this.unitPrice,
           this.getTotalValue()  // Reutiliza el método de cálculo
       );
   }
   ```
   
   **Anatomía del toString():**
   - `@Override`: Indica sobrescritura
   - `public String`: Retorna un String
   - No recibe parámetros
   - `String.format()`: Formatea con placeholders
     - `%s`: String
     - `%.2f`: double con 2 decimales
   - Incluye valores calculados (getTotalValue())

3. **Estudiar String.format():**
   
   **Especificadores comunes:**
   - `%s`: String
   - `%d`: int
   - `%f`: double (default 6 decimales)
   - `%.2f`: double con 2 decimales
   - `%10s`: String con ancho mínimo 10
   
   **Investiga:** ¿Qué hace `%.2f` vs `%f`?

4. **Probar en Main.java:**
   
   **TÚ debes escribir:**
   
   a) Crear un Movement:
      ```java
      Movement m = new Movement("ENTRADA", "Diesel", 100.0, 3.45);
      ```
   
   b) Imprimir directamente (ahora verás datos legibles):
      ```java
      System.out.println(m);
      ```
   
   c) Crear varios y comparar:
      ```java
      Movement m2 = new Movement("SALIDA", "Gasolina 93", 50.0, 4.20);
      System.out.println(m2);
      ```
   
   **Resultado esperado:**
   ```
   Movement[id=a3f2b4c5, type=ENTRADA, fuel=Diesel, qty=100.00, price=$3.45, total=$345.00]
   Movement[id=d6e8f1a2, type=SALIDA, fuel=Gasolina 93, qty=50.00, price=$4.20, total=$210.00]
   ```

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

1. **Analizar y diseñar ANTES de codificar:**
   
   En papel o herramienta digital, dibuja 4 cajas (Movement, Vehicle, Supplier, Product).
   
   **Piensa y anota:**
   - **Vehicle:** ¿Qué datos necesitas? (placa, modelo, categoría, capacidad...)
   - **Supplier:** ¿Qué datos? (nombre, contacto, dirección...)
   - **Product:** ¿Qué datos? (nombre del combustible, tipo, unidad, precio estándar...)
   
   **Pregunta guía:** ¿Hay relaciones entre ellas? (ej: Movement podría tener un supplierId)

2. **Crear clase Vehicle (TÚ debes hacerlo):**
   
   **Especificaciones:**
   - Archivo: `Vehicle.java` en el paquete `models`
   - **Atributos privados sugeridos:**
     - id (String) - generado automáticamente
     - placa (String) - ej: "ABC123"
     - modelo (String) - ej: "Volvo FH16"
     - categoria (String) - ej: "Camión", "Cisterna"
     - capacidadLitros (double) - cuántos litros puede cargar
   
   **Debes implementar:**
   - Constructor principal (recibe todo menos id)
   - Constructor vacío
   - Getters para todos los atributos
   - Setters solo para atributos modificables (NO para id)
   - toString() personalizado
   
   **Patrón:** Usa Movement como referencia - misma estructura, diferentes atributos.

3. **Crear clase Supplier (TÚ debes hacerlo):**
   
   **Especificaciones:**
   - Archivo: `Supplier.java` en el paquete `models`
   - **Atributos privados sugeridos:**
     - id (String)
     - nombre (String) - ej: "Petro Colombia S.A."
     - telefono (String)
     - email (String)
     - direccion (String)
   
   **Implementar:** Constructor, getters, setters selectivos, toString()
   
   **Pregunta guía:** ¿Qué atributos NO deberían tener setter? ¿Por qué?

4. **Crear clase Product (TÚ debes hacerlo):**
   
   **Especificaciones:**
   - Archivo: `Product.java` en el paquete `models`
   - **Atributos privados sugeridos:**
     - id (String)
     - nombre (String) - ej: "Diesel Extra"
     - tipoCombustible (String) - ej: "Diesel", "Gasolina"
     - unidadMedida (String) - ej: "Litros"
     - precioEstandar (double) - precio base actual
   
   **Implementar:** Constructor, getters, setters selectivos, toString()
   
   **Desafío:** Agrega un método `getPrecioConDescuento(double porcentaje)` que calcule un precio con descuento.

5. **Probar CADA clase en Main.java:**
   
   **TÚ debes escribir pruebas para:**
   
   a) **Vehicle:**
      ```java
      Vehicle camion1 = new Vehicle("ABC123", "Volvo FH16", "Cisterna", 5000.0);
      System.out.println(camion1);
      ```
      Verifica que el toString() muestre datos legibles.
   
   b) **Supplier:**
      Crea un proveedor con datos realistas e imprímelo.
   
   c) **Product:**
      Crea un producto (tipo de combustible) e imprímelo.
      Prueba el método de descuento si lo implementaste.

6. **Actualizar diagrama:**
   
   Ahora que las clases existen, actualiza tu diagrama con los atributos reales.
   
   **Reflexiona:** ¿Ves relaciones potenciales? (Movement podría tener vehicleId y supplierId)

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

1. **Importar ArrayList (boilerplate permitido):**
   ```java
   import java.util.ArrayList;
   ```

2. **Crear tu primer ArrayList (ejemplo didáctico):**
   
   **En Main.java:**
   ```java
   ArrayList<Movement> movements = new ArrayList<>();
   ```
   
   **Anatomía:**
   - `ArrayList<Movement>`: Tipo de la variable - lista de Movement
   - `movements`: Nombre de la variable
   - `new ArrayList<>()`: Crea la lista vacía
   - `<>`: El compilador infiere que es de Movement

3. **Agregar objetos al ArrayList:**
   
   **TÚ debes escribir:**
   - Crea al menos 3 movimientos diferentes:
     - 1 ENTRADA de Diesel
     - 1 SALIDA de Gasolina 93
     - 1 ENTRADA de ACPM
   
   - Agrégalos al ArrayList usando `.add()`:
     ```java
     movements.add(entrada1);
     movements.add(salida1);
     movements.add(entrada2);
     ```

4. **Operaciones básicas (TÚ implementas):**
   
   a) **Obtener el tamaño:**
      - Usa `.size()` para saber cuántos movimientos hay
      - Imprime: "Total de movimientos: X"
   
   b) **Obtener un elemento específico:**
      - Usa `.get(indice)` para obtener el movimiento en posición 0
      - Imprime ese movimiento
      - **Pregunta:** ¿Por qué el primer elemento está en posición 0 y no 1?
   
   c) **Eliminar un elemento:**
      - Usa `.remove(indice)` para eliminar el del medio
      - Imprime el tamaño después de eliminar
      - **Verifica:** ¿El tamaño disminuyó?

5. **Recorrer e imprimir TODOS (3 formas):**
   
   **Forma 1 - for clásico (TÚ escribes):**
   ```java
   for (int i = 0; i < movements.size(); i++) {
       Movement m = movements.get(i);
       System.out.println(m);
   }
   ```
   
   **Forma 2 - for-each (TÚ escribes):**
   ```java
   for (Movement m : movements) {
       // Imprimir m
   }
   ```
   
   **Pregunta guía:** ¿Cuál es más legible? ¿Cuándo usarías una u otra?
   
   **Forma 3 - Desafío avanzado (opcional):**
   Investiga `movements.forEach()` con lambdas (lo verás en Fase 8).

6. **Cálculo sobre la colección (TÚ implementas):**
   
   **Objetivo:** Sumar el total de TODOS los movimientos.
   
   **Especificaciones:**
   - Variable acumuladora: `double granTotal = 0;`
   - Recorre el ArrayList
   - En cada iteración: suma el `getTotalValue()` del movimiento
   - Al final, imprime el gran total
   
   **Pregunta guía:** ¿Por qué necesitas una variable acumuladora?

7. **Búsqueda simple (TÚ implementas):**
   
   **Objetivo:** Encontrar el PRIMER movimiento de tipo "ENTRADA".
   
   **Especificaciones:**
   - Recorre el ArrayList
   - Para cada movimiento, verifica si `getMovementType().equals("ENTRADA")`
   - Cuando lo encuentres, guárdalo y **sal del bucle** con `break`
   - Imprime cuál encontraste
   
   **Pregunta:** ¿Qué pasa si ninguno es ENTRADA? ¿Cómo lo manejarías?

8. **Búsqueda avanzada (TÚ implementas):**
   
   **Objetivo:** Encontrar TODOS los movimientos de "SALIDA" y ponerlos en otra lista.
   
   **Especificaciones:**
   - Crea un nuevo ArrayList: `ArrayList<Movement> salidas = new ArrayList<>();`
   - Recorre el ArrayList original
   - Si el movimiento es SALIDA, agrégalo a `salidas`
   - Imprime cuántas salidas encontraste
   - Imprime la lista de salidas

9. **Estadística compleja (Desafío):**
   
   **Objetivo:** Encontrar el movimiento con mayor cantidad de litros.
   
   **Pistas:**
   - Variable para guardar el máximo: `Movement mayorMovimiento = null;`
   - Recorre el ArrayList
   - Compara cantidades con `getQuantity()`
   - Actualiza `mayorMovimiento` si encuentras uno mayor

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
