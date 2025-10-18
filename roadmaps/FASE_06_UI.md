# 🖥️ FASE 6: INTERFAZ DE USUARIO EN CONSOLA (Semana 10)

> Objetivo general: construir un menú CLI robusto, usable y seguro para interactuar con la lógica de negocio.

---

## 🧠 Mindset antes de iniciar

- 🎨 **Diseño en papel:** Dibuja la estructura del menú principal y todos los submenús antes de codificar. Anota qué pasa cuando el usuario presiona opciones inválidas o cancela.
- 🔁 **Flujos de navegación:** Define claramente cómo ir de una pantalla a otra y cómo volver atrás sin perder datos.
- 📚 **Manejo de errores:** La UI es la primera línea de defensa. Un error aquí puede arruinar la experiencia del usuario.
- 🧭 **Piensa como usuario:** ¿Qué esperarías ver? ¿Qué sería confuso? ¿Qué te haría sentir seguro al operar el sistema?

---

## ✅ Checkpoint 6.1: Menú principal interactivo

**Concepto clave:** Un bucle `while(true)` controlado con `break` es la forma más común de implementar menús CLI que no terminan hasta que el usuario lo decide.

**📍 DÓNDE:**
- Crear paquete `ui` en `src/main/java/com/forestech/ui/`
- Crear clase `ConsoleMenu.java` dentro del paquete `ui`
- Método principal: `start()` que inicia el loop del menú
- Método auxiliar: `displayMainMenu()` que imprime las opciones

**🎯 PARA QUÉ:**
Hasta ahora has probado todo desde `Main.java` ejecutando métodos manualmente. Pero Forestech necesita que **cualquier operador** (sin conocimientos de programación) pueda:
- **Registrar movimientos** de combustible sin tocar código
- **Ver inventario** en tiempo real
- **Consultar vehículos** y proveedores
- **Generar reportes** cuando lo necesite

Sin un menú claro, tu aplicación solo sirve para desarrolladores. Con un menú bien hecho, cualquier persona puede operar el sistema.

**🔗 CONEXIÓN FUTURA:**
- En Fase 7, mejorarás el manejo de errores para que el menú nunca se rompa por entradas inválidas
- En Fase 8, usarás interfaces para inyectar servicios al menú de forma más flexible
- En Fase 9, agregarás opciones de reportes y búsquedas avanzadas al menú
- En Fase 10, este menú será el punto de entrada del JAR ejecutable

**Prompts sugeridos:**
```text
"¿Cuáles son las diferencias entre un menú con while(true) y uno recursivo en Java?"
"¿Cómo manejo InputMismatchException cuando el usuario ingresa texto en lugar de número?"
"¿Qué es mejor: limpiar el buffer con scanner.nextLine() o crear un nuevo Scanner?"
"Dame ejemplos de bordes ASCII profesionales para encabezados de consola."
```

**Tareas paso a paso:**

1. **Estructura de clases:** 📐

```
Clase: ConsoleMenu
├── Atributos:
│   ├── scanner: Scanner (para leer entrada del usuario)
│   ├── movementService: MovementService
│   ├── inventoryService: InventoryService
│   ├── vehicleService: VehicleService
│   └── supplierService: SupplierService
├── Constructor:
│   └── Recibe los servicios como parámetros y los almacena
└── Métodos principales:
    ├── start(): void
    ├── displayMainMenu(): void
    ├── handleMovementsMenu(): void
    ├── handleVehiclesMenu(): void
    └── (otros submenús)
```

2. **Método `displayMainMenu()`:** 📋

```
Especificación (TÚ IMPLEMENTAS):
├── Paso 1: Imprimir encabezado visual
│   └── Usa bordes ASCII: ========== FORESTECH CLI ==========
│
├── Paso 2: Mostrar información del sistema
│   ├── Versión actual: 1.0
│   └── Fecha/Hora: LocalDateTime.now()
│
├── Paso 3: Listar opciones principales
│   ├── 1. Gestión de Movimientos
│   ├── 2. Ver Inventario
│   ├── 3. Gestión de Vehículos
│   ├── 4. Gestión de Proveedores
│   ├── 5. Reportes
│   └── 0. Salir
│
└── Paso 4: Solicitar entrada
    └── System.out.print("Seleccione opción: ")
```

3. **Método `start()`:** 🔄

```
Flujo (TÚ LO ESCRIBES):
├── Bucle externo:
│   └── while (true) { ... }
│
├── Dentro del bucle:
│   ├── 1. Llamar displayMainMenu()
│   ├── 2. Leer opción con scanner.nextInt()
│   ├── 3. (IMPORTANTE) Limpiar buffer con scanner.nextLine()
│   └── 4. Usar switch para cada opción:
│       ├── case 1: handleMovementsMenu()
│       ├── case 2: displayInventory()
│       ├── case 3: handleVehiclesMenu()
│       ├── case 4: handleSuppliersMenu()
│       ├── case 5: handleReports()
│       ├── case 0: salir (break)
│       └── default: mostrar "❌ Opción inválida"
│
└── IMPORTANTE: Estructura del try-catch
    ├── try {
    │   └── scanner.nextInt()
    │
    ├── catch (InputMismatchException e) {
    │   ├── System.out.println("❌ Por favor ingrese un número válido")
    │   └── scanner.nextLine()  ⚠️ CRÍTICO: Limpiar buffer
    │
    └── }
```

4. **Patrón de submenús:** 🌳

```
Método: handleMovementsMenu()
├── Estructura (IGUAL que start()):
│   ├── while (true) { ... }
│   ├── Mostrar opciones del submenú
│   ├── Leer opción con try-catch
│   ├── switch para cada opción:
│   │   ├── case 1: registerEntryWizard()
│   │   ├── case 2: registerExitWizard()
│   │   ├── case 3: viewMovementHistory()
│   │   └── case 0: return (volver atrás)
│   │
│   └── No uses break para salir, usa return
│       (porque no quieres romper el loop externo)
│
├── Opciones del menú:
│   ├── 1. Registrar Entrada
│   ├── 2. Registrar Salida
│   ├── 3. Ver Historial
│   └── 0. Volver al menú principal
│
└── 💡 PISTA: Reutiliza el try-catch del método start()
```

5. **Confirmación al salir:** ⚠️

```
En case 0 del menú principal:
├── System.out.print("¿Está seguro que desea salir? (S/N): ")
├── Leer respuesta
├── if (respuesta.equalsIgnoreCase("S")) {
│   └── break (salir del bucle)
│
└── else {
    └── continue (volver a mostrar menú)
```

**✅ Resultado esperado:**
Al ejecutar `ConsoleMenu.start()`, debes ver un menú limpio que:
- Se repite después de cada operación
- No se rompe cuando ingresas letras en lugar de números
- Te permite navegar entre submenús
- Solo sale cuando confirmas explícitamente
- Muestra mensajes claros para opciones inválidas

**💡 Tip de limpieza de buffer:**
Cuando usas `nextInt()` seguido de `nextLine()`, el `nextInt()` deja el carácter de salto de línea en el buffer. Siempre haz `scanner.nextLine()` después de `nextInt()` para consumir ese carácter sobrante.

**⚠️ PELIGRO: Loop infinito**
Si NO limpias el buffer después de `InputMismatchException`, el Scanner intentará leer el mismo token inválido una y otra vez, creando un loop infinito que imprimirá el mensaje de error miles de veces por segundo.

**🔍 Depuración:**
Si el menú no se repite o se comporta raro, agrega `System.out.println("DEBUG: Opción leída: " + opcion);` después de leer la entrada para ver qué valor está recibiendo realmente.

**⏱️ Tiempo estimado:** 3-4 horas

---

## ✅ Checkpoint 6.2: Wizard para movimientos de ENTRADA

**Concepto clave:** Un wizard es un proceso paso a paso que guía al usuario a través de una tarea compleja, validando cada entrada antes de continuar.

**📍 DÓNDE:**
- Clase `ConsoleMenu` en `src/main/java/com/forestech/ui/ConsoleMenu.java`
- Crear método `registerEntryWizard()`
- Este método será llamado desde `handleMovementsMenu()` cuando el usuario elija la opción de registrar entrada

**🎯 PARA QUÉ:**
Registrar una entrada de combustible requiere varios datos:
- ¿De qué proveedor viene?
- ¿Qué tipo de combustible es?
- ¿Cuántos galones/litros?
- ¿Cuál es el precio unitario?
- ¿A qué ubicación va?

Si le pides al usuario todos estos datos de una vez, es abrumador y propenso a errores. Un wizard:
- **Divide** el proceso en pasos pequeños
- **Valida** cada entrada antes de pedir la siguiente
- **Muestra** opciones claras (lista de proveedores, no un campo de texto libre)
- **Permite cancelar** en cualquier momento sin dejar datos a medias
- **Confirma** antes de ejecutar para evitar errores costosos

**🔗 CONEXIÓN FUTURA:**
- En Fase 7, manejarás excepciones personalizadas si el proveedor no existe o los datos son inválidos
- En Fase 8, usarás enums para tipos de combustible en lugar de Strings
- En Fase 9, el wizard registrará cada paso en el log profesional (SLF4J)
- En Fase 10, podrás exportar el registro completo de entradas a Excel

**Prompts sugeridos:**
```text
"¿Cómo implemento un menú de selección que muestre una lista numerada de proveedores?"
"¿Cuál es la mejor forma de validar que un número sea mayor que cero en Java?"
"¿Cómo permito que el usuario cancele un wizard sin ejecutar cambios en la base de datos?"
"Dame ejemplos de mensajes de confirmación claros antes de operaciones críticas."
```

**Tareas paso a paso:**

1. **Estructura básica del wizard:** 📐

```
Método: registerEntryWizard()
├── Tipo: void
├── Inicialización:
│   ├── Mostrar encabezado
│   └── Declarar variables para almacenar datos
└── Estructura: 7 pasos secuenciales
    ├── Paso 1: Seleccionar proveedor
    ├── Paso 2: Seleccionar combustible
    ├── Paso 3: Ingresar cantidad
    ├── Paso 4: Ingresar precio unitario
    ├── Paso 5: Seleccionar ubicación
    ├── Paso 6: Mostrar resumen y confirmar
    └── Paso 7: Ejecutar la operación
```

2. **Paso 1: Seleccionar proveedor** 📋

```
Especificación (TÚ IMPLEMENTAS):
├── Invocar: supplierService.getAllSuppliers()
├── Mostrar lista numerada:
│   └── Cada proveedor con su identificador
├── Permitir opción 0 para cancelar
├── Validación:
│   ├── ¿Número en rango válido?
│   ├── ¿Es 0? → Mostrar "Operación cancelada" + return
│   └── ¿Es válido? → Guardar proveedor, continuar
└── 💡 PISTA: Usa un loop pequeño aquí para re-pedir si entra mal
```

3. **Paso 2: Seleccionar combustible** ⛽

```
Especificación (TÚ IMPLEMENTAS):
├── Mostrar lista de tipos disponibles
│   ├── Opción 1: Diesel
│   ├── Opción 2: Gasolina 93
│   ├── Opción 3: Gasolina 95
│   └── Opción 4: Mezcla (o consultar BD)
├── Permitir opción 0 para cancelar
├── Validación:
│   ├── ¿Número válido?
│   ├── ¿Es 0? → Cancelar operación
│   └── ¿Válido? → Guardar tipo, continuar
└── 💡 PISTA: Si usas enums, mejor; por ahora puedes hardcodear
```

4. **Paso 3 y 4: Cantidad y Precio** 💰

```
Especificación (TÚ IMPLEMENTAS):
├── Paso 3: Ingresar cantidad en litros
│   ├── Solicitar entrada
│   ├── Leer como double
│   ├── Validación:
│   │   ├── ¿Es > 0?
│   │   ├── Si NO → mostrar error, volver a pedir
│   │   └── Si SÍ → guardar, continuar
│   └── 💡 Usa try-catch para InputMismatchException
│
├── Paso 4: Ingresar precio unitario
│   ├── Solicitar entrada
│   ├── Leer como double
│   ├── Validación similar a Paso 3
│   ├── Calcular total: cantidad × precio
│   └── Mostrar total calculado
└── 💡 PISTA: Estos pasos comparten la misma lógica de validación
```

5. **Paso 5: Seleccionar ubicación** 📍

```
Especificación (TÚ IMPLEMENTAS):
├── Mostrar lista de ubicaciones (bodegas)
├── Permitir cancelar con 0
├── Validación de opción elegida
└── Guardar ubicación seleccionada
```

6. **Paso 6: Resumen y confirmación** 📊

```
Especificación (TÚ IMPLEMENTAS):
├── Mostrar tabla con todos los datos:
│   ├── Proveedor seleccionado
│   ├── Tipo de combustible
│   ├── Cantidad en litros
│   ├── Precio unitario
│   ├── Total (cantidad × precio)
│   └── Ubicación de destino
├── Preguntar: "¿Confirma el registro? (S/N):"
├── Leer respuesta
└── Si es 'N' o 'n' → Mostrar "Operación cancelada" + return
```

7. **Paso 7: Ejecutar operación** ✅

```
Especificación (TÚ IMPLEMENTAS):
├── Crear nuevo objeto Movement con todos los datos
├── Try-catch para capturar excepciones:
│   ├── Invocar: movementService.createEntryMovement(movement)
│   ├── Si ÉXITO:
│   │   └── Mostrar: "✅ Entrada registrada. ID: [uuid]"
│   └── Si FALLO:
│       └── Mostrar: "❌ Error al registrar: [mensaje]"
└── 💡 NO muestres el stacktrace completo, solo mensaje amigable
```

8. **Manejo global de errores:** 🛡️

```
Estructura (TÚ IMPLEMENTAS):
├── TODO el método envuelto en try-catch externo
├── Capturar InputMismatchException
│   └── Mostrar: "❌ Ingrese un número válido"
├── Capturar SQLException (excepciones de base de datos)
│   └── Mostrar: "❌ Error de base de datos. Intente más tarde."
└── Capturar Exception genérica (catch-all)
    └── Mostrar: "❌ Error inesperado. Intente nuevamente."
```

**✅ Resultado esperado:**

- Usuario ve cada paso claramente
- Puede cancelar en cualquier momento
- Ve un resumen antes de confirmar
- Recibe mensajes claros de éxito o error
- No ve excepciones técnicas

**💡 Métodos auxiliares sugeridos:**

```
selectFromList(List items, String prompt): Object
├── Mostrar lista numerada
├── Validar selección
└── Retornar elemento seleccionado
   
validatePositiveDouble(String prompt): double
├── Solicitar entrada
├── Validar > 0
├── Retornar double válido
```

**⏱️ Tiempo estimado:** 4-5 horas

---

## ✅ Checkpoint 6.3: Wizard para movimientos de SALIDA

**Concepto clave:** Las salidas son más complejas que las entradas porque requieren validaciones adicionales: stock disponible, vehículo válido y actualización de horómetro.

**📍 DÓNDE:**
- Clase `ConsoleMenu` en `src/main/java/com/forestech/ui/ConsoleMenu.java`
- Crear método `registerExitWizard()`
- Este método será llamado desde `handleMovementsMenu()` cuando el usuario elija la opción de registrar salida

**🎯 PARA QUÉ:**
Una salida de combustible en Forestech no es solo restar del inventario. También necesitas:
- **Verificar** que hay stock suficiente antes de permitir la operación
- **Identificar** qué vehículo recibe el combustible
- **Actualizar** el horómetro del vehículo
- **Calcular** cuántas horas trabajó el vehículo desde la última carga
- **Validar** que el nuevo horómetro sea mayor que el anterior (no puede retroceder)

Si permites una salida sin stock, tu inventario quedará negativo (¡desastre!). Si no actualizas el horómetro, pierdes la trazabilidad de cuánto consume cada vehículo.

**🔗 CONEXIÓN FUTURA:**
- En Fase 7, lanzarás `InsufficientInventoryException` si no hay stock suficiente
- En Fase 8, validarás que el horómetro nuevo sea mayor usando lógica más robusta
- En Fase 9, generarás reportes de consumo por vehículo basados en estos datos
- En Fase 10, podrás auditar quién autorizó salidas grandes y detectar anomalías

**Prompts sugeridos:**
```text
"¿Cómo valido que hay stock suficiente antes de permitir una salida?"
"¿Qué hacer si el vehículo no existe en la base de datos?"
"¿Cómo calculo las horas trabajadas si tengo horómetro anterior y nuevo?"
"¿Es mejor validar stock en la UI o en el servicio? ¿Por qué?"
```

**Tareas paso a paso:**

1. **Estructura básica del wizard:** 📐

```
Método: registerExitWizard()
├── Tipo: void
├── Inicialización:
│   ├── Mostrar encabezado
│   └── Declarar variables para almacenar datos
└── Estructura: 7 pasos secuenciales
    ├── Paso 1: Seleccionar vehículo
    ├── Paso 2: Validar e ingresar nuevo horómetro
    ├── Paso 3: Mostrar combustibles disponibles
    ├── Paso 4: Seleccionar combustible y cantidad
    ├── Paso 5: Seleccionar ubicación de origen
    ├── Paso 6: Mostrar resumen y confirmar
    └── Paso 7: Ejecutar la operación
```

2. **Paso 1: Seleccionar vehículo** 🚗

```
Especificación (TÚ IMPLEMENTAS):
├── Invocar: vehicleService.getAllVehicles()
├── Mostrar lista con horómetro actual:
│   ├── 1. Excavadora CAT-320 (Horómetro: 1,250 hrs)
│   ├── 2. Camión Volvo FH (Horómetro: 45,890 hrs)
│   └── 0. Cancelar
├── Leer opción del usuario
├── Validación:
│   ├── ¿Es 0? → Cancelar operación + return
│   ├── ¿Es válido? → Guardar vehículo + horómetro actual
│   └── 💡 Extrae y guarda el horómetro actual para paso 2
└── Mostrar horómetro actual al usuario
```

3. **Paso 2: Validar horómetro nuevo** ⚠️

```
Especificación (TÚ IMPLEMENTAS):
├── Solicitar: "Ingrese nueva lectura de horómetro:"
├── Leer como double
├── Validación CRÍTICA (loop hasta válido):
│   ├── ¿Es > 0?
│   ├── ¿Es > horómetro_actual?
│   │   ├── SÍ → Calcular horas: nuevo - actual
│   │   ├── Mostrar: "✅ Horas trabajadas: XX.X hrs"
│   │   └── Guardar nuevo horómetro + horas trabajadas + continuar
│   └── NO → Mostrar: "❌ Debe ser mayor que XXX" + volver a pedir
└── 💡 Este paso es CRÍTICO: validar antes de continuar
```

4. **Paso 3: Mostrar combustibles con stock** ⛽

```
Especificación (TÚ IMPLEMENTAS):
├── Invocar: inventoryService.getAllInventory()
├── Filtrar: solo combustibles con stock > 0
├── Si NO hay combustibles disponibles:
│   ├── Mostrar: "⚠️ No hay combustibles. Registre una entrada primero."
│   └── return (cancelar wizard)
├── Si HAY combustibles, mostrar lista:
│   ├── 1. Diesel - 500.00 litros (Bodega Central)
│   ├── 2. Gasolina 93 - 300.00 litros (Bodega Norte)
│   └── 0. Cancelar
└── Leer selección del usuario
```

5. **Paso 4: Seleccionar cantidad a retirar** 💨

```
Especificación (TÚ IMPLEMENTAS):
├── Guardar stock disponible del combustible seleccionado
├── Solicitar: "Ingrese cantidad a retirar (disponible: XXX L):"
├── Leer como double
├── Validación (loop si falla):
│   ├── ¿Es > 0?
│   ├── ¿Es <= stock_disponible?
│   │   ├── SÍ → Guardar cantidad + continuar
│   │   └── NO → Mostrar error + volver a pedir
│   └── Mostrar: "Stock después de salida: YYY litros"
└── 💡 Este es otro punto crítico de validación
```

6. **Paso 5: Seleccionar ubicación de origen** 📍

```
Especificación (TÚ IMPLEMENTAS):
├── Mostrar ubicaciones donde existe ese combustible
├── Usuario elige una
├── Guardar ubicación seleccionada
└── 💡 Podría ser que mismo combustible esté en varias bodegas
```

7. **Paso 6: Resumen completo** 📊

```
Especificación (TÚ IMPLEMENTAS):
├── Mostrar tabla con TODOS los datos:
│   ├── Vehículo: [nombre]
│   ├── Horómetro anterior: XXX hrs
│   ├── Horómetro nuevo: YYY hrs
│   ├── Horas trabajadas: ZZZ hrs  ← CALCULADO
│   ├── Combustible: [tipo]
│   ├── Cantidad a retirar: NNN litros
│   ├── Ubicación de origen: [bodega]
│   └── Stock después de salida: MMM litros  ← CALCULADO
├── Preguntar: "¿Confirma el registro? (S/N):"
└── Si responde 'N' → Cancelar y return
```

8. **Paso 7: Ejecutar transacción** ✅

```
Especificación (TÚ IMPLEMENTAS):
├── Crear objeto Movement con todos los datos
├── Crear objeto actualización de vehículo (horómetro nuevo)
├── Try-catch para capturar excepciones:
│   ├── Invocar: movementService.createExitMovement(movement)
│   ├── Invocar: vehicleService.updateOdometer(vehicleId, newHorometer)
│   ├── Si ÉXITO:
│   │   ├── Mostrar: "✅ Salida registrada. ID: [uuid]"
│   │   └── Mostrar: "📊 Nuevo stock: XXX litros"
│   └── Si FALLO (stock insuficiente):
│       └── Mostrar: "❌ Stock insuficiente: necesarios XXX, disponibles YYY"
└── ⚠️ IMPORTANTE: ambas operaciones deben estar en la MISMA transacción
```

9. **Validaciones adicionales** 🛡️

```
Especificación (TÚ IMPLEMENTAS):
├── Try-catch general envuelve todo el método
├── Capturar InputMismatchException
│   └── Mostrar: "❌ Ingrese un número válido"
├── Capturar InsufficientInventoryException (Fase 7)
│   └── Mostrar: "❌ Stock insuficiente para esta operación"
├── Capturar SQLException
│   └── Mostrar: "❌ Error de base de datos. Intente más tarde."
└── Capturar Exception (catch-all)
    └── Mostrar: "❌ Error inesperado."
```

**✅ Resultado esperado:**

- Usuario ve solo combustibles que realmente tienen stock
- No puede ingresar cantidad mayor al stock disponible
- No puede usar horómetro menor al actual
- Ve claramente cuántas horas trabajó el vehículo
- Ve el stock resultante antes de confirmar
- Si falla, nada se guarda (transacción atómica)

**💡 Diferencia clave entrada vs salida:**

Entrada: agregar combustible (menor riesgo)
Salida: restar combustible + actualizar vehículo + validar stock (MAYOR riesgo)

**⚠️ Race condition importante:**

Aunque valides en UI, DOS usuarios podrían intentar sacar combustible simultáneamente.
Ambos pasan la validación en UI, pero UNO falla en el servicio.
Por eso SIEMPRE valida también en el servicio dentro de la transacción.

**⏱️ Tiempo estimado:** 5-6 horas

---

## 📈 Mejoras de UX y usabilidad

**Símbolos y separadores:**
- Usa `✅` para operaciones exitosas
- Usa `❌` para errores
- Usa `⚠️` para advertencias
- Usa `📊` para datos estadísticos
- Usa `========` o `----------` para separar secciones

**Limpieza de consola:**
En sistemas Unix/Linux, puedes limpiar la consola con:
```java
System.out.print("\033[H\033[2J");
System.out.flush();
```
Pero esto no funciona en todos los sistemas. Documenta si decides usarlo.

**Pausas estratégicas:**
Después de mostrar un mensaje importante, considera agregar:
```java
System.out.println("\nPresione ENTER para continuar...");
scanner.nextLine();
```
Esto da tiempo al usuario de leer antes de que el menú se redibuje.

**Logging simple:**
Mientras no tengas SLF4J (Fase 9), puedes crear un archivo `forestech.log` y escribir ahí cada operación:
- "[2025-01-15 10:30] Usuario registró entrada: 500L Diesel"
- "[2025-01-15 10:35] Usuario registró salida: 45L Diesel para CAT-320"

Esto ayuda a rastrear qué pasó si algo sale mal.

---

## 🧪 Refuerzos y pruebas sugeridas

**Pruebas de usuario real:**

1. **Happy path completo:**
   - Iniciar aplicación
   - Registrar una entrada
   - Ver inventario
   - Registrar una salida
   - Ver inventario actualizado
   - Salir
   - TODO debe fluir sin errores

2. **Manejo de errores:**
   - Ingresar letras cuando se espera número
   - Elegir opciones que no existen
   - Cancelar en mitad de un wizard
   - Intentar salida con stock insuficiente
   - Ingresar horómetro menor al actual

3. **Edge cases:**
   - Intentar salida cuando no hay vehículos registrados
   - Intentar salida cuando inventario está vacío
   - Ingresar cantidad exacta del stock disponible (no debe quedar negativo)
   - Ingresar valores con decimales donde se esperan enteros

**Registro de pruebas:**
Crea una tabla en `JAVA_LEARNING_LOG.md`:

| Escenario | Entrada | Resultado Esperado | Resultado Real | ¿Pasó? |
|-----------|---------|-------------------|----------------|--------|
| Usuario ingresa letra en menú | "abc" | Mensaje de error + vuelve a menú | Mensaje + vuelve | ✅ |
| Cancelar en paso 2 de entrada | 0 | Cancelación + vuelve a menú | Cancelación + vuelve | ✅ |
| Salida con stock insuficiente | 1000L cuando hay 500L | Rechazo con mensaje claro | (Probar) | ❓ |

---

## ✅ Checklist de salida de Fase 6

- [ ] El menú principal se ejecuta sin fallos y permite navegar entre opciones
- [ ] Puedo registrar entradas completas usando el wizard
- [ ] Puedo registrar salidas con validación de stock y horómetro
- [ ] Las opciones inválidas no rompen la aplicación
- [ ] Puedo cancelar wizards en cualquier paso sin dejar datos inconsistentes
- [ ] Los mensajes son claros y profesionales (no técnicos)
- [ ] Documenté todos los casos de prueba en JAVA_LEARNING_LOG.md
- [ ] El código está organizado con métodos auxiliares reutilizables
- [ ] Comprendo cómo funciona Scanner y cómo limpiar el buffer
- [ ] Puedo explicar por qué es importante validar tanto en UI como en servicios

**🎯 Desafío final:**
Implementa un wizard para consultar el historial de movimientos con filtros opcionales (por fecha, por vehículo, por combustible). Muestra los resultados en una tabla ASCII formateada.

---

## 🚀 Próximo paso: FASE 7 - Manejo de Errores y Excepciones

En la siguiente fase aprenderás a:
- Crear excepciones personalizadas que describan problemas específicos de Forestech
- Usar try-with-resources para evitar fugas de recursos
- Implementar manejo centralizado de errores en la UI
- Diferenciar entre excepciones chequeadas y no chequeadas
- Loggear errores de forma técnica mientras muestras mensajes amigables al usuario

**¿Por qué es crítico?** Ahora mismo, si algo falla, probablemente ves un SQLException técnico. En Fase 7, transformarás esos errores en mensajes claros como "❌ No se pudo registrar la salida: stock insuficiente (disponible: 45L, solicitado: 100L)".

**⏱️ Tiempo total Fase 6:** 12-15 horas distribuidas en 1 semana
