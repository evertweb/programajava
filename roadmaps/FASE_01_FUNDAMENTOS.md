# 🚀 FASE 1: FUNDAMENTOS DE JAVA (Semanas 1-2)

> Objetivo general: dominar la sintaxis base de Java y los patrones de control que usarás durante todo el proyecto Forestech CLI.

---

## 🧠 Antes de empezar

- 🐞 **Rutina de depuración:** en cada checkpoint detente al menos una vez para ejecutar el código en modo debug, inspeccionar variables y usar `step over`/`step into`.
- 🗒️ **Notas rápidas:** al cierre de cada checkpoint anota en `JAVA_LEARNING_LOG.md` qué entendiste, qué dudas persisten y qué probarás mañana.
- 🔁 **Git loop:** al completar cada checkpoint crea un commit con mensaje claro (`git commit -m "fase1 checkpoint 1.2"`).

---

## ✅ Checkpoint 1.1: Primer Programa

**Concepto clave:** En Java el método `main(String[] args)` es el punto de entrada obligatorio.

**📍 DÓNDE:** Archivo `forestech-cli-java/src/main/java/com/forestech/Main.java`

**🎯 PARA QUÉ:** Este será el punto de arranque de toda tu aplicación Forestech. Desde aquí invocarás menús, servicios y toda la lógica del sistema. Sin `main()`, Java no sabe por dónde empezar a ejecutar tu programa.

**🔗 CONEXIÓN FUTURA:** Más adelante, desde este `main()` invocarás:
- El menú principal (Fase 6)
- Los servicios de base de datos (Fase 4)
- La gestión de movimientos, vehículos y proveedores

**Prompts sugeridos para tu IA:**
```text
"Estoy en Checkpoint 1.1. Explícame con analogía qué es el método main()."
"¿Qué significa cada palabra en public static void main(String[] args)?"
```

**Tareas paso a paso:**

1. **Crear/Abrir** el archivo `Main.java` en la ruta indicada arriba
   - Si usas IntelliJ, clic derecho en `com.forestech` → New → Java Class → "Main"

2. **Escribir** la estructura mínima de una clase Java con método main
   - Necesitas: declaración de package, clase pública y método main
   - Dentro de main, imprime un mensaje de bienvenida a Forestech

3. **Compilar** desde terminal:
   - Navega a la carpeta `forestech-cli-java/`
   - Ejecuta el comando de Maven para compilar

4. **Ejecutar** de dos formas:
   - Desde terminal con Maven (investiga el plugin exec)
   - Desde IntelliJ con clic derecho

**✅ Resultado esperado:** Ver en consola un mensaje de bienvenida a Forestech CLI

**🔍 Depuración:** Coloca un breakpoint en la línea de impresión y observa cómo se ejecuta

**⏱️ Tiempo estimado:** 1-2 horas

---

## ✅ Checkpoint 1.2: Variables y Tipos de Datos

**Concepto clave:** Diferencia entre tipos primitivos (int, double, boolean) y de referencia (String); uso de constantes con `final`.

**📍 DÓNDE:** Dentro del método `main()` en `Main.java`, después de tu mensaje de bienvenida

**🎯 PARA QUÉ:** Necesitas representar información real de Forestech:
- **Nombre del combustible** → String (ej: "Diesel", "Gasolina Premium")
- **Cantidad en galones** → int o double (ej: 100, 150.5)
- **Precio por galón** → double (ej: 3.45, 4.20)
- **¿Es entrada o salida?** → boolean (true = entrada, false = salida)

**🔗 CONEXIÓN FUTURA:** 
- Estas variables se convertirán en **atributos de la clase Movement** (Fase 2)
- Los cálculos que hagas aquí se volverán **métodos** como `getTotalValue()` (Fase 2)
- Las validaciones se moverán a **servicios** (Fase 4-5)

**Prompts sugeridos:**
```text
"¿Por qué existe int e Integer? Dame ejemplos con movimientos de combustible."
"¿Cuándo usar double vs int para cantidades en Forestech?"
```

**Tareas paso a paso:**

1. **Declarar variables** que representen un movimiento de combustible:
   - Tipo de combustible (texto)
   - Cantidad en galones (número)
   - Precio por galón (número con decimales)
   - Si es entrada o salida (verdadero/falso)

2. **Declarar una constante** con el nombre del sistema (nunca cambiará)
   - Usa `final` antes del tipo
   - Convención: MAYÚSCULAS_CON_GUION_BAJO

3. **Imprimir** todos estos datos con formato profesional:
   - Un título como "=== Registro de Movimiento ==="
   - Cada dato en una línea separada con su etiqueta
   - Para el tipo de movimiento, usa operador ternario (? :)

4. **Experimento:** Intenta cambiar la constante después de declararla
   - Observa el error de compilación
   - Comenta la línea para que compile

**✅ Resultado esperado:** Ver en consola una "ficha" del movimiento con todos sus datos

**💡 Concepto a dominar:** Entiende que estas variables solo existen mientras se ejecuta `main()`. Después desaparecen. Por eso necesitarás objetos (Fase 2) y base de datos (Fase 3).

**⏱️ Tiempo estimado:** 1-2 horas

---

## ✅ Checkpoint 1.3: Operadores y Expresiones

**Concepto clave:** Usar operadores aritméticos (+, -, *, /), relacionales (>, <, ==) y lógicos (&&, ||) para cálculos y decisiones.

**📍 DÓNDE:** Dentro de `main()` en `Main.java`, después de las variables del checkpoint anterior

**🎯 PARA QUÉ:** En Forestech necesitas hacer cálculos reales:
- **Valor total** del movimiento = cantidad × precio
- **IVA** = total × 0.19
- **Total con IVA** = subtotal + IVA
- **Validar** si es una compra grande (>= 100 galones)
- **Combinar condiciones** para alertas (ej: entrada de Diesel > 50 galones)

**🔗 CONEXIÓN FUTURA:**
- El cálculo de total será el método `getTotalValue()` en la clase Movement (Fase 2)
- Las validaciones se convertirán en lógica de negocio en MovementService (Fase 5)
- Los reportes (Fase 9) usarán estos cálculos para estadísticas

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre / y % ? Dame ejemplos con Forestech."
"¿Por qué debo usar .equals() en vez de == para comparar Strings?"
```

**Tareas paso a paso:**

1. **Calcular el subtotal** de un movimiento:
   - Multiplica la cantidad por el precio unitario
   - Guarda en una nueva variable

2. **Calcular el IVA** (19% en Colombia):
   - Aplica el porcentaje sobre el subtotal
   - Recuerda que 19% = 0.19

3. **Calcular el total final** con IVA incluido:
   - Suma subtotal + IVA

4. **Imprimir los cálculos** con formato de moneda:
   - Usa `String.format("%.2f", numero)` para 2 decimales
   - Muestra subtotal, IVA y total

5. **Evaluar si es compra grande**:
   - Crea un boolean que sea verdadero si quantity >= 100
   - Imprime el resultado

6. **Combinar condiciones** para una validación compleja:
   - Crea un boolean que valide: es entrada && es Diesel && cantidad > 50
   - Imprime si cumple o no

**✅ Resultado esperado:** Ver en consola los cálculos financieros y las validaciones booleanas

**💡 Practica:** Cambia los valores iniciales y observa cómo cambian los resultados

**⏱️ Tiempo estimado:** 1-2 horas

---

## ✅ Checkpoint 1.4: Control de Flujo (if/else, switch)

**Concepto clave:** Hacer que el programa tome diferentes caminos según condiciones.

**📍 DÓNDE:** Dentro de `main()` en `Main.java`

**🎯 PARA QUÉ:** Tu aplicación debe reaccionar diferente según:
- **El tipo de movimiento**: entrada aumenta inventario, salida lo disminuye
- **La opción del menú** que elija el usuario: 1=registrar entrada, 2=salida, 3=ver inventario
- **El tipo de combustible**: diferentes precios y validaciones

**🔗 CONEXIÓN FUTURA:**
- El `switch` será el **menú principal** de tu aplicación (Fase 6)
- Los `if/else` se volverán **validaciones en servicios** (Fase 4-5)
- Las condiciones complejas se convertirán en **reglas de negocio** (Fase 5)

**Prompts sugeridos:**
```text
"¿Cuándo debo preferir switch sobre múltiples if-else?"
"Muéstrame con ejemplo por qué comparar Strings con == puede fallar."
```

**Tareas paso a paso:**

1. **Crear una variable** que represente el tipo de movimiento ("ENTRADA", "SALIDA", o "INVALIDO")

2. **Usar if/else** para validar el tipo:
   - Si es "ENTRADA": mostrar mensaje que el inventario aumentará
   - Si es "SALIDA": mostrar mensaje que el inventario disminuirá
   - Si es otra cosa: mostrar error
   - **Importante:** usa `.equals()` para comparar Strings

3. **Crear una variable** que represente una opción de menú (número del 1 al 4)

4. **Usar switch** para el menú:
   - Opción 1: Mostrar "Registrar Entrada de Combustible"
   - Opción 2: Mostrar "Registrar Salida de Combustible"
   - Opción 3: Mostrar "Ver Inventario Actual"
   - Opción 4: Mostrar "Salir del sistema"
   - Default: Mostrar "Opción inválida"
   - **No olvides** los `break` en cada case

**✅ Resultado esperado:** Ver diferentes mensajes según los valores de las variables

**🔍 Actividad de depuración:** 
- Cambia el valor de las variables y ejecuta varias veces
- Usa breakpoints para ver qué rama se ejecuta

**⚠️ Recuerda:** El switch tradicional solo funciona con valores exactos (números, Strings). Para rangos o condiciones complejas, usa if/else.

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 1.5: Bucles (for, while, do-while)

**Concepto clave:** Repetir acciones múltiples veces sin duplicar código.

**📍 DÓNDE:** Dentro de `main()` en `Main.java`

**🎯 PARA QUÉ:** En Forestech necesitas repetir operaciones:
- **Mostrar el menú** hasta que el usuario elija "Salir" → `while`
- **Recorrer listas** de movimientos para mostrarlos → `for` / `for-each`
- **Validar entrada** del usuario hasta que sea correcta → `do-while`
- **Procesar múltiples** movimientos en un reporte → `for-each`

**🔗 CONEXIÓN FUTURA:**
- El `while(true)` será el **bucle principal** del menú (Fase 6)
- El `for-each` recorrerá `ArrayList<Movement>` para mostrar inventario (Fase 2)
- El `do-while` validará inputs del usuario (Fase 7)

**Prompts sugeridos:**
```text
"Compárame for, while y do-while con ejemplos de la vida real."
"¿Qué es un índice de array y por qué empieza en 0?"
```

**Tareas paso a paso:**

1. **Crear un array** con las opciones del menú (array de Strings)
   - Incluye: "Registrar Entrada", "Registrar Salida", "Ver Inventario", etc.

2. **Usar bucle for clásico** para mostrar el menú numerado:
   - Recorre desde índice 0 hasta la longitud del array
   - Imprime: número (i+1) + el texto de la opción

3. **Usar bucle while** para simular procesamiento de movimientos:
   - Crea un contador que empiece en 0
   - Mientras sea menor que 5:
     - Incrementa el contador
     - Imprime "Procesando movimiento #X"

4. **Usar bucle do-while** para simular reintentos:
   - Crea un contador de intentos
   - Repite 3 veces:
     - Incrementa contador
     - Imprime "Intento #X de guardar en base de datos..."

5. **BONUS - Usar for-each** para mostrar tipos de combustible:
   - Crea un array con: "Diesel", "Gasolina Regular", "Gasolina Premium", "ACPM"
   - Recorre con for-each y muestra cada uno

**✅ Resultado esperado:** 
- Ver el menú numerado automáticamente
- Ver el contador de movimientos procesados
- Ver los intentos de guardado
- Ver la lista de combustibles

**⚠️ CUIDADO:** 
- No olvides incrementar contadores (evita bucles infinitos)
- Verifica las condiciones de salida del while
- Recuerda que do-while se ejecuta al menos una vez

**🔍 Depuración:** Inspecciona el valor del índice y contador en cada iteración

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✅ Checkpoint 1.6: Entrada de Usuario (Scanner)

**Concepto clave:** Convertir tu aplicación de estática (datos hardcodeados) a interactiva (usuario ingresa datos).

**📍 DÓNDE:** 
- **Import:** Al inicio del archivo Main.java (después de `package`)
- **Scanner:** Al inicio del método `main()`
- **Uso:** REEMPLAZA los valores hardcodeados por lecturas con Scanner

**🎯 PARA QUÉ:** ¡Esto es CRÍTICO! Sin Scanner, tu aplicación es solo una demo. Con Scanner:
- El usuario elige opciones del menú escribiendo números
- El usuario ingresa cantidades de combustible
- El usuario escribe nombres de proveedores
- Tu aplicación se vuelve **realmente útil**

**🔗 CONEXIÓN FUTURA:**
- Este Scanner se moverá a un bucle `while(true)` para el menú repetitivo (Fase 6)
- Los datos capturados se usarán para crear objetos Movement (Fase 2)
- Agregarás try-catch para manejar errores de entrada (Fase 7)
- Crearás métodos auxiliares para validar entradas (Fase 5)

**Prompts sugeridos:**
```text
"Explícame paso a paso qué hace new Scanner(System.in)."
"¿Por qué necesito scanner.nextLine() después de nextInt()? Explícame el buffer."
```

**Tareas paso a paso:**

1. **Importar Scanner:**
   - Agrega el import al inicio del archivo

2. **Crear instancia de Scanner:**
   - Al inicio de main(), crea el Scanner con System.in

3. **Mostrar menú de opciones:**
   - Imprime título "FORESTECH CLI - Sistema de Combustibles"
   - Imprime las 4 opciones del menú numeradas
   - Imprime "Seleccione una opción: " SIN salto de línea

4. **Leer opción del usuario:**
   - Usa el método apropiado de Scanner para leer un número entero
   - **IMPORTANTE:** Después, limpia el buffer con nextLine()

5. **Si eligió opción 1 o 2** (registrar movimiento):
   - Pide tipo de combustible (texto)
   - Pide cantidad en galones (número decimal)
   - Pide precio por galón (número decimal)
   - **No olvides** limpiar buffer después de cada número

6. **Mostrar resumen:**
   - Imprime todos los datos capturados
   - Muestra el cálculo del total
   - Indica si fue entrada o salida según la opción

7. **Cerrar Scanner:**
   - Al final de main(), cierra el Scanner

**✅ Resultado esperado:** 
- El programa muestra el menú
- Espera a que TÚ escribas un número
- Te pide los datos uno por uno
- Muestra un resumen con lo que ingresaste
- Se cierra limpiamente

**⚠️ PROBLEMA COMÚN - EL BUFFER:**
Cuando usas `nextInt()` o `nextDouble()`, el Enter que presionas queda en el buffer. Si después usas `nextLine()`, este captura ese Enter vacío. **Solución:** Siempre usa un `nextLine()` extra después de leer números.

**🔍 Experimento:** 
- Quita el `nextLine()` de limpieza y observa qué pasa
- Intenta ingresar texto donde espera número (se romperá, lo arreglaremos en Fase 7)

**⏱️ Tiempo estimado:** 2-3 horas

---

## 🧾 Checklist de salida de Fase 1

Antes de pasar a Fase 2, verifica que puedes responder SÍ a todo:

**Comprensión de conceptos:**
- [ ] Puedo explicar con mis palabras qué es el método `main()` y por qué es necesario
- [ ] Entiendo la diferencia entre `int` e `Integer`, `double` y `Double`
- [ ] Sé cuándo usar `final` y qué significa "inmutable"
- [ ] Puedo explicar por qué `==` no funciona bien con Strings
- [ ] Entiendo cuándo usar `if/else` vs `switch`
- [ ] Sé la diferencia entre `while` y `do-while`
- [ ] Entiendo qué es el buffer de Scanner y cómo limpiarlo

**Habilidades prácticas:**
- [ ] Compilé y ejecuté código desde terminal con Maven
- [ ] Usé el debugger al menos 3 veces con breakpoints
- [ ] Creé commits de Git por cada checkpoint completado
- [ ] Documenté aprendizajes en `JAVA_LEARNING_LOG.md`

**Entregable funcional:**
- [ ] Mi `Main.java` tiene un menú interactivo que:
  - Muestra 4 opciones numeradas
  - Captura la elección del usuario con Scanner
  - Si elige opción 1 o 2, pide datos del movimiento
  - Calcula y muestra el total con IVA
  - Se cierra sin errores

**🎯 Desafío final (opcional):**
Agrega un bucle `while` para que el menú se repita hasta que el usuario elija opción 4. Por ahora está bien si solo se ejecuta una vez.

**📸 Evidencia:**
Toma un screenshot de tu programa funcionando y guárdalo en tu carpeta del proyecto con nombre `fase1-completada.png`

---

## 🚀 Próximo paso: FASE 2 - Programación Orientada a Objetos

En la siguiente fase aprenderás a:
- Crear la clase `Movement` con atributos y métodos
- Organizar el código en diferentes archivos
- Usar constructores para inicializar objetos
- Trabajar con `ArrayList` para manejar múltiples movimientos
- Entender encapsulamiento, getters y setters

**¿Por qué necesitas objetos?** Ahora mismo tus variables desaparecen cuando termina `main()`. Con objetos podrás crear, almacenar y manipular múltiples movimientos, vehículos y proveedores de forma organizada.

**⏱️ Tiempo total Fase 1:** 10-15 horas distribuidas en 1-2 semanas
