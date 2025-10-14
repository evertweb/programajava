# 🧩 FASE 2: PROGRAMACIÓN ORIENTADA A OBJETOS (Semanas 3-4)

> Objetivo general: modelar el dominio de Forestech con clases, objetos y colecciones dinámicas.

---

## 🧠 Enfoque de la fase

- 🧱 **Diseño visual primero:** antes de codificar cada clase, dibuja un diagrama sencillo (puede ser en papel o en herramientas como draw.io) mostrando atributos y relaciones.
- 📓 Mantén en `JAVA_LEARNING_LOG.md` el racional de diseño: por qué cada clase existe, qué responsabilidades tiene y qué dudas quedan abiertas.
- 🐞 Refuerza depuración: inspecciona objetos en runtime y usa "Evaluate Expression" para leer/ajustar atributos.

---

## ✅ Checkpoint 2.1: Primera clase `Movement`

**Concepto clave:** Una clase es un molde que encapsula datos (atributos) y comportamientos (métodos).

**📍 DÓNDE:** 
- Crear carpeta `models` dentro de `com/forestech/`
- Crear archivo `Movement.java` en `src/main/java/com/forestech/models/`

**🎯 PARA QUÉ:** 
En Fase 1 creaste variables sueltas que desaparecen al terminar `main()`. Ahora necesitas una estructura que:
- **Agrupe** todos los datos de un movimiento (id, tipo, cantidad, precio)
- **Persista** mientras la aplicación esté corriendo
- **Permita crear múltiples movimientos** sin repetir código
- **Se pueda guardar** en la base de datos (Fase 3-4)

**🔗 CONEXIÓN FUTURA:**
- En Fase 3 conectarás a SQL Server y leerás movimientos de la BD, convirtiéndolos en objetos Movement
- En Fase 4 guardarás estos objetos en la BD
- En Fase 6 el usuario creará movimientos desde el menú
- En Fase 9 generarás reportes recorriendo listas de Movement

**Prompts sugeridos:**
```text
"Explícame con analogía qué es una clase y cómo se relaciona con un objeto."
"¿Qué atributos debería tener un movimiento de combustible real en Forestech?"
"¿Por qué los atributos deben ser privados?"
```

**Tareas paso a paso:**

1. **Crear el paquete `models`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "models"

2. **Crear la clase Movement:**
   - Dentro de `models`, crear archivo Java llamado "Movement"
   - Declarar como clase pública

3. **Declarar atributos privados** que representen un movimiento:
   - Identificador único (String)
   - Tipo de movimiento: "ENTRADA" o "SALIDA" (String)
   - Tipo de combustible: "Diesel", "Gasolina", etc. (String)
   - Cantidad en galones (double)
   - Precio unitario (double)
   - Fecha del movimiento (String por ahora)

4. **Verificar que compile:**
   - Ejecuta `mvn clean compile` desde terminal
   - No debe haber errores

**✅ Resultado esperado:** Archivo Movement.java con declaración de clase y atributos privados, compilando sin errores

**💡 Concepto a dominar:** Los atributos son privados para que nadie pueda cambiarlos directamente desde fuera. Más adelante crearás métodos (getters/setters) para acceso controlado.

**⏱️ Tiempo estimado:** 1 hora

---

## ✅ Checkpoint 2.2: Constructores y `this`

**Concepto clave:** El constructor es un método especial que inicializa objetos garantizando estados válidos.

**📍 DÓNDE:** Dentro de la clase `Movement` en `Movement.java`

**🎯 PARA QUÉ:** 
Sin constructor, los objetos se crearían vacíos o con valores null. El constructor:
- **Garantiza** que todo Movement tenga id, tipo, combustible, cantidad y precio desde el momento de creación
- **Genera automáticamente** el id único (no quieres que el usuario lo invente)
- **Evita** crear movimientos inválidos (ej: sin precio)

**🔗 CONEXIÓN FUTURA:**
- En Fase 4, cuando guardes en la BD, el constructor generará el id antes de insertar
- En Fase 6, cuando el usuario ingrese datos, crearás el Movement con esos datos via constructor
- En Fase 7, agregarás validaciones dentro del constructor (lanzar excepciones si cantidad <= 0)

**Prompts sugeridos:**
```text
"¿Por qué el constructor no lleva tipo de retorno?"
"Desglosa el uso de 'this' para diferenciar parámetros de atributos."
"¿Qué es sobrecarga de constructores?"
```

**Tareas paso a paso:**

1. **Crear constructor principal** que reciba:
   - Tipo de movimiento
   - Tipo de combustible
   - Cantidad
   - Precio unitario

2. **Dentro del constructor:**
   - Genera el id automáticamente usando UUID (investiga `UUID.randomUUID().toString()`)
   - Asigna los parámetros a los atributos usando `this`
   - Inicializa la fecha con la fecha actual como String (por ahora usa `LocalDateTime.now().toString()`)

3. **Crear constructor sin parámetros (sobrecarga):**
   - No recibe nada
   - Inicializa con valores por defecto razonables

4. **Probar en Main.java:**
   - Crea un objeto Movement usando el constructor principal
   - Imprime el objeto (verás algo como `Movement@hashcode` por ahora)

**✅ Resultado esperado:** Poder crear objetos Movement desde Main.java con `new Movement("ENTRADA", "Diesel", 100.0, 3.45)`

**⚠️ RECUERDA:** Usa `this.atributo` para diferenciar el atributo del parámetro cuando tengan el mismo nombre.

**🔍 Depuración:** Coloca breakpoint dentro del constructor y observa cómo se asignan los valores uno por uno.

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 2.3: Encapsulamiento con getters/setters

**Concepto clave:** Mantener atributos privados y exponer acceso controlado mediante métodos públicos.

**📍 DÓNDE:** Dentro de la clase `Movement` en `Movement.java`, después de los constructores

**🎯 PARA QUÉ:** 
Los atributos privados no se pueden leer ni modificar desde fuera. Necesitas métodos para:
- **Leer valores** (getters) para mostrar en pantalla o enviar a la BD
- **Modificar valores** (setters) con validación (ej: rechazar cantidad negativa)
- **Calcular valores derivados** (ej: getTotalValue = cantidad × precio)
- **Proteger** atributos que nunca deben cambiar (ej: id)

**🔗 CONEXIÓN FUTURA:**
- En Fase 3, usarás getters para extraer datos del objeto y construir queries SQL
- En Fase 5, validarás reglas de negocio (ej: no permitir salidas mayores al inventario)
- En Fase 6, mostrarás datos en la UI usando getters
- En Fase 9, calcularás estadísticas recorriendo objetos con getters

**Prompts sugeridos:**
```text
"¿Qué beneficios reales obtengo al encapsular atributos?"
"¿Cuándo NO debo crear un setter para un atributo?"
"¿Cómo valido datos dentro de un setter?"
```

**Tareas paso a paso:**

1. **Crear getters para TODOS los atributos:**
   - Método público que retorna el valor del atributo
   - Convención: `getAtributo()` (ej: `getId()`, `getType()`)

2. **Crear método especial `getTotalValue()`:**
   - NO es un getter normal, es un cálculo
   - Retorna cantidad × precio unitario
   - Útil para mostrar el total sin calcularlo cada vez

3. **Crear setters SOLO para atributos modificables:**
   - Cantidad (puede corregirse después de crear el movimiento)
   - Precio unitario (puede actualizarse)
   - **NO crear setter para:** id, date (no deben cambiar)

4. **Agregar validación en los setters:**
   - Si la cantidad es <= 0, no actualizar (o lanzar mensaje)
   - Si el precio es <= 0, no actualizar

5. **Probar en Main.java:**
   - Crea un Movement
   - Lee valores con getters e imprime
   - Intenta modificar con setter válido
   - Intenta modificar con setter inválido (valor negativo)
   - Llama a getTotalValue() e imprime

**✅ Resultado esperado:** Poder leer y modificar atributos de forma controlada, rechazando valores inválidos

**💡 Tip:** Si un setter rechaza un valor, imprime un mensaje de advertencia para que sepas qué pasó.

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 2.4: Sobrescritura de `toString()`

**Concepto clave:** Definir cómo se representa un objeto cuando lo imprimes.

**📍 DÓNDE:** Dentro de la clase `Movement`, después de getters/setters

**🎯 PARA QUÉ:** 
Sin `toString()`, al imprimir un objeto ves `Movement@2a84aee7` (inútil). Con `toString()`:
- **Depurar** fácilmente (imprimir el objeto y ver sus datos)
- **Mostrar** información legible en la consola
- **Verificar** que los datos son correctos sin debugger

**🔗 CONEXIÓN FUTURA:**
- En Fase 6, cuando el usuario vea la lista de movimientos, usarás toString() o un método similar
- En Fase 9, para reportes en consola usarás toString()
- En Fase 8 (logging), toString() será útil para registrar objetos completos

**Prompts sugeridos:**
```text
"¿Qué ocurre si imprimo un objeto sin sobrescribir toString()?"
"Muéstrame ejemplos de String.format con diferentes especificadores."
"¿Qué es la anotación @Override y por qué usarla?"
```

**Tareas paso a paso:**

1. **Crear método `toString()`:**
   - Usa la anotación `@Override` antes del método
   - Retorna String
   - No recibe parámetros

2. **Construir la cadena de retorno con:**
   - Nombre de la clase y corchetes: "Movement["
   - Cada atributo con su nombre: "id=..., type=..., fuelType=..., quantity=..., unitPrice=..., total=..."
   - Formatea números con 2 decimales usando `String.format("%.2f", numero)`
   - Cierra con "]"

3. **Probar en Main.java:**
   - Crea un Movement
   - Imprime el objeto directamente: `System.out.println(movement)`
   - Deberías ver los datos legibles

**✅ Resultado esperado:** Al imprimir un Movement, ver algo como:
```
Movement[id=abc-123, type=ENTRADA, fuelType=Diesel, quantity=100.00, unitPrice=3.45, total=345.00]
```

**🔍 Actividad:** Inspecciona el objeto en el debugger y compara con la salida de toString().

**⏱️ Tiempo estimado:** 1 hora

---

## ✅ Checkpoint 2.5: Más clases del dominio

**Concepto clave:** Modelar todas las entidades principales de Forestech (vehículos, proveedores, productos).

**📍 DÓNDE:** 
- Carpeta `src/main/java/com/forestech/models/`
- Crear archivos: `Vehicle.java`, `Supplier.java`, `Product.java`

**🎯 PARA QUÉ:** 
Forestech no solo maneja movimientos, también necesita:
- **Vehicle** (vehículos): para registrar qué camión recibió combustible
- **Supplier** (proveedores): de dónde viene el combustible
- **Product** (productos): los tipos de combustible disponibles

Cada entidad merece su propia clase con sus propios atributos.

**🔗 CONEXIÓN FUTURA:**
- En Fase 3-4 crearás tablas en SQL Server para estas entidades
- En Fase 5 relacionarás Movement con Vehicle (¿a qué vehículo se le cargó?)
- En Fase 6 el usuario podrá gestionar vehículos y proveedores desde el menú
- En Fase 9 generarás reportes por proveedor o por vehículo

**Prompts sugeridos:**
```text
"¿Cómo decido si algo debe ser una clase separada o un simple atributo?"
"Sugiere atributos clave para Vehicle, Supplier y Product basados en Forestech."
"¿Qué es una relación entre clases?"
```

**Tareas paso a paso:**

1. **Analizar qué datos necesitas:**
   - **Vehicle:** placa, modelo, categoría (camión/carro), capacidad
   - **Supplier:** nombre, teléfono, email, dirección
   - **Product:** nombre, tipo (combustible), unidad (galones), precio estándar

2. **Crear cada clase con:**
   - Atributos privados relevantes
   - Constructor que genere id automáticamente
   - Getters para todos los atributos
   - Setters solo para atributos modificables
   - Método toString() personalizado

3. **Dibujar diagrama:**
   - En papel o herramienta digital, dibuja las 4 clases (Movement, Vehicle, Supplier, Product)
   - Anota los atributos de cada una
   - Piensa: ¿hay relaciones entre ellas? (ej: Movement podría tener un Supplier)

4. **Probar en Main.java:**
   - Crea un objeto de cada clase
   - Imprime cada uno
   - Verifica que toString() muestra datos legibles

**✅ Resultado esperado:** Tener 4 clases funcionales (Movement, Vehicle, Supplier, Product) que puedes instanciar e imprimir

**💡 Reflexión:** No todas las clases tendrán los mismos atributos. Piensa qué es relevante para cada entidad en Forestech.

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 2.6: Colecciones con `ArrayList`

**Concepto clave:** Almacenar listas dinámicas de objetos y operarlas.

**📍 DÓNDE:** En el método `main()` de `Main.java` (temporalmente, luego esto se moverá a servicios)

**🎯 PARA QUÉ:** 
Un array normal tiene tamaño fijo. ArrayList es dinámico:
- **Agregar** movimientos sin saber cuántos habrá
- **Eliminar** movimientos
- **Buscar** un movimiento específico
- **Recorrer** todos los movimientos para calcular totales o mostrarlos

**🔗 CONEXIÓN FUTURA:**
- En Fase 3, cargarás movimientos desde la BD a un ArrayList
- En Fase 4, operarás sobre ArrayList antes de guardar en BD
- En Fase 5, crearás servicios que retornen ArrayList<Movement>
- En Fase 6, mostrarás el contenido del ArrayList en el menú
- En Fase 9, recorrerás ArrayList para generar reportes

**Prompts sugeridos:**
```text
"Diferénciame entre array y ArrayList con analogía concreta."
"¿Qué significa el <Movement> en ArrayList<Movement>? (generics)"
"¿Por qué no puedo usar ArrayList<double>? ¿Qué hago en su lugar?"
```

**Tareas paso a paso:**

1. **Importar ArrayList:**
   - Al inicio del archivo: `import java.util.ArrayList;`

2. **Crear un ArrayList de Movement:**
   - Declarar e inicializar en main()
   - Especifica el tipo usando <>

3. **Agregar al menos 3 movimientos:**
   - Usa el método `.add()`
   - Crea diferentes tipos: entrada/salida, diferentes combustibles

4. **Operaciones básicas:**
   - Obtener el tamaño de la lista
   - Obtener un movimiento específico por índice
   - Eliminar un movimiento

5. **Recorrer e imprimir:**
   - Usa for-each para recorrer todos los movimientos
   - Imprime cada uno (gracias a toString())

6. **Cálculo sobre la colección:**
   - Recorre el ArrayList
   - Suma el total de todos los movimientos (usando getTotalValue())
   - Imprime el gran total

7. **Búsqueda simple:**
   - Recorre el ArrayList
   - Encuentra el primer movimiento de tipo "ENTRADA"
   - Imprime cuál es

**✅ Resultado esperado:** 
- Lista de movimientos que crece dinámicamente
- Puedes agregar, eliminar, buscar
- Puedes calcular estadísticas recorriendo la lista

**⚠️ CUIDADO:** Los índices empiezan en 0. Si tienes 3 elementos, los índices son 0, 1, 2.

**🔍 Depuración:** Inspecciona el ArrayList en el debugger y observa cómo cambia tras cada operación.

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✏️ Refuerzos adicionales de la fase

- 📊 **Quiz autoevaluación:** pide a tu IA 6 preguntas (teóricas y de código) cubriendo clases, constructores, encapsulamiento, `toString` y ArrayList.
- 🧪 **Mini-ejercicio extra:** modela un `Driver` (conductor) con atributos relevantes y agrégalo a tu diagrama aunque no codifiques la clase aún. Identifica si se relaciona a `Vehicle` o `Movement`.
- 📌 **Registro de aprendizaje:** documenta decisiones de diseño y cualquier patrón repetido que observes.
- 🐞 **Buen hábito:** cuando un setter rechaza un valor, captura el stacktrace en depuración para entender el flujo completo.

---

## ✅ Checklist de salida de Fase 2

- [ ] Diagramé las clases del dominio y entendí sus relaciones básicas
- [ ] Puedo explicar con mis palabras qué es una clase, objeto, atributo y método
- [ ] Entiendo para qué sirve un constructor y cuándo crear múltiples (sobrecarga)
- [ ] Sé cuándo crear/no crear setters para atributos
- [ ] Mi toString() muestra información útil para depurar
- [ ] Trabajo cómodamente con ArrayList<Movement> y comprendo métodos principales
- [ ] Puedo explicar qué es encapsulamiento y por qué importa
- [ ] Dejé registro en JAVA_LEARNING_LOG.md de aprendizajes clave y dudas pendientes

**🎯 Desafío final:** Crea un método en Main.java que reciba un ArrayList<Movement> y retorne solo los movimientos de tipo "ENTRADA".

---

## 🚀 Próximo paso: FASE 3 - Conexión a SQL Server

En la siguiente fase aprenderás a:
- Configurar el driver JDBC para SQL Server
- Crear la clase DatabaseConnection
- Ejecutar tu primera consulta SELECT
- Entender try-with-resources para cerrar conexiones automáticamente

**¿Por qué necesitas BD?** Ahora mismo tus objetos solo existen mientras la aplicación corre. Cuando cierras el programa, TODO se pierde. Con base de datos, la información persiste para siempre.

**⏱️ Tiempo total Fase 2:** 10-15 horas distribuidas en 1-2 semanas
