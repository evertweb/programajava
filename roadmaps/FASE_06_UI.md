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

1. **Crear la estructura básica:**
   - Crear paquete `ui`
   - Crear clase `ConsoleMenu` con atributo `Scanner` y atributos para los servicios (MovementService, InventoryService, etc.)
   - Constructor que inicialice el Scanner y reciba los servicios como parámetros

2. **Método `displayMainMenu()`:**
   - Imprimir encabezado con bordes ASCII (ejemplo: `========== FORESTECH CLI ==========`)
   - Mostrar información del sistema (versión, fecha actual)
   - Listar opciones numeradas:
     - 1. Gestión de Movimientos (submenú)
     - 2. Ver Inventario
     - 3. Gestión de Vehículos (submenú)
     - 4. Gestión de Proveedores (submenú)
     - 5. Reportes (submenú)
     - 0. Salir
   - Solicitar al usuario que ingrese una opción

3. **Método `start()`:**
   - Crear bucle `while(true)`
   - Dentro del bucle: llamar a `displayMainMenu()`
   - Leer opción del usuario con `scanner.nextInt()`
   - Usar `switch` para manejar cada opción
   - Para cada case, llamar al método correspondiente (ej: `handleMovementsMenu()`)
   - Case 0: mostrar mensaje de despedida y hacer `break` para salir del bucle
   - Default: mostrar mensaje de opción inválida

4. **Manejo de excepciones:**
   - Envolver `scanner.nextInt()` en `try-catch` para capturar `InputMismatchException`
   - Si ocurre la excepción: mostrar mensaje claro tipo "❌ Por favor ingrese un número válido"
   - **CRÍTICO:** Limpiar el buffer con `scanner.nextLine()` después del catch
   - Sin esta limpieza, el scanner queda en estado inválido y crea un loop infinito

5. **Submenús:**
   - Crear método `handleMovementsMenu()` con estructura similar (while, opciones, switch)
   - Opciones: 1. Registrar Entrada, 2. Registrar Salida, 3. Ver Historial, 0. Volver
   - Repetir patrón para otros submenús (vehículos, proveedores, reportes)

6. **Confirmación al salir:**
   - Antes del `break` en case 0, preguntar: "¿Está seguro que desea salir? (S/N)"
   - Leer respuesta y solo salir si es 'S' o 's'
   - Esto evita salidas accidentales

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

1. **Estructura del método:**
   - Crear método `registerEntryWizard()` que no retorne nada (void)
   - Imprimir encabezado del wizard: `========== REGISTRAR ENTRADA DE COMBUSTIBLE ==========`
   - Declarar variables para almacenar cada dato recolectado

2. **Paso 1: Seleccionar proveedor**
   - Invocar `supplierService.getAllSuppliers()` para obtener lista de proveedores
   - Mostrar lista numerada de proveedores con formato:
     ```
     Proveedores disponibles:
     1. PetroSur (RUT: 12345678-9)
     2. Distribuidora Norte (RUT: 98765432-1)
     0. Cancelar
     ```
   - Solicitar al usuario que elija un número
   - Validar que el número esté en el rango válido
   - Si elige 0, mostrar "Operación cancelada" y retornar
   - Guardar el proveedor seleccionado

3. **Paso 2: Seleccionar tipo de combustible**
   - Mostrar lista de tipos de combustible disponibles (puedes hardcodear por ahora o consultar desde la BD)
   - Ejemplo: 1. Diesel, 2. Gasolina 93, 3. Gasolina 95, 4. Mezcla
   - Permitir cancelar con 0
   - Guardar el tipo seleccionado

4. **Paso 3: Ingresar cantidad**
   - Solicitar: "Ingrese cantidad en litros:"
   - Leer como `double`
   - Validar que sea > 0
   - Si no es válido, mostrar error y volver a pedir
   - Permitir escribir "cancelar" para abortar (requiere leer como String primero)

5. **Paso 4: Ingresar precio unitario**
   - Solicitar: "Ingrese precio unitario: $"
   - Leer como `double`
   - Validar que sea > 0
   - Calcular y mostrar el total: `Total: $XXX.XX`

6. **Paso 5: Seleccionar ubicación**
   - Mostrar lista de ubicaciones/bodegas disponibles
   - Ejemplo: 1. Bodega Central, 2. Bodega Norte, 3. Bodega Sur
   - Permitir cancelar
   - Guardar ubicación seleccionada

7. **Paso 6: Resumen y confirmación**
   - Mostrar resumen completo:
     ```
     ========== RESUMEN DE ENTRADA ==========
     Proveedor: PetroSur
     Combustible: Diesel
     Cantidad: 500.00 litros
     Precio unitario: $3.45
     Total: $1,725.00
     Ubicación: Bodega Central
     ¿Confirma el registro? (S/N):
     ```
   - Leer respuesta
   - Si es 'N', mostrar "Operación cancelada" y retornar

8. **Paso 7: Ejecutar operación**
   - Crear objeto `Movement` con todos los datos recolectados
   - Invocar `movementService.createEntryMovement(movement)`
   - Si tiene éxito, mostrar: `✅ Entrada registrada exitosamente. ID: [uuid]`
   - Si falla, mostrar: `❌ Error al registrar entrada: [mensaje]`

9. **Manejo de errores:**
   - Todo el wizard debe estar en un `try-catch` amplio
   - Capturar `InputMismatchException` y mostrar mensaje claro
   - Capturar excepciones de la capa de servicios y mostrarlas amigablemente

**✅ Resultado esperado:**
Al ejecutar el wizard, el usuario:
- Ve claramente qué se espera en cada paso
- Puede cancelar en cualquier momento sin dejar datos inconsistentes
- Ve un resumen antes de confirmar
- Recibe confirmación clara del ID generado
- No ve errores técnicos (SQLExceptions, etc.), solo mensajes amigables

**💡 Patrón común:**
Este mismo patrón de wizard lo vas a repetir para salidas, registro de vehículos, proveedores, etc. Considera crear métodos auxiliares como `selectFromList(List items, String prompt)` que puedan reutilizarse.

**⚠️ CUIDADO con el estado a medias:**
Si el usuario cancela o hay un error, NO debes dejar datos guardados a medias. Por eso construyes el objeto `Movement` completo ANTES de llamar al servicio, y solo lo guardas si todos los datos están listos.

**🔍 Depuración:**
Si algo falla, agrega prints después de cada paso: `System.out.println("DEBUG: Proveedor seleccionado: " + proveedor.getName());` para rastrear en qué punto ocurre el problema.

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

1. **Estructura del método:**
   - Crear método `registerExitWizard()` void
   - Imprimir encabezado: `========== REGISTRAR SALIDA DE COMBUSTIBLE ==========`
   - Declarar variables para almacenar datos

2. **Paso 1: Seleccionar vehículo**
   - Invocar `vehicleService.getAllVehicles()` para obtener lista
   - Mostrar lista numerada con información clave:
     ```
     Vehículos disponibles:
     1. Excavadora CAT-320 (Horómetro: 1,250 hrs)
     2. Camión Volvo FH (Horómetro: 45,890 hrs)
     3. Grúa Liebherr (Horómetro: 8,432 hrs)
     0. Cancelar
     ```
   - Permitir cancelar
   - Guardar vehículo seleccionado
   - Mostrar el horómetro actual del vehículo seleccionado

3. **Paso 2: Ingresar nueva lectura de horómetro**
   - Solicitar: "Ingrese nueva lectura de horómetro:"
   - Leer como `double`
   - **VALIDACIÓN CRÍTICA:** Verificar que nuevo horómetro > horómetro actual
   - Si es menor o igual, mostrar: `❌ Error: El horómetro nuevo (XXX) debe ser mayor que el actual (YYY)`
   - Volver a solicitar hasta que sea válido o cancelar
   - Calcular horas trabajadas: `horasT trabajadas = nuevoHorometro - horometroActual`
   - Mostrar: `✅ Horas trabajadas desde última carga: XX.X hrs`

4. **Paso 3: Mostrar combustibles con stock disponible**
   - Invocar `inventoryService.getAllInventory()` para ver qué hay disponible
   - Mostrar solo combustibles con stock > 0:
     ```
     Combustibles disponibles:
     1. Diesel - 500.00 litros disponibles (Bodega Central)
     2. Gasolina 93 - 300.00 litros disponibles (Bodega Norte)
     0. Cancelar
     ```
   - Si NO hay stock de nada, mostrar: `⚠️ No hay combustibles disponibles. Registre una entrada primero.`
   - Retornar sin permitir continuar

5. **Paso 4: Seleccionar combustible y validar cantidad**
   - Usuario elige el combustible
   - Guardar el stock disponible de ese combustible
   - Solicitar: "Ingrese cantidad a retirar (disponible: XXX litros):"
   - Leer cantidad
   - **VALIDACIÓN CRÍTICA:** Verificar que cantidad <= stock disponible
   - Si excede, mostrar: `❌ Error: Solo hay XXX litros disponibles`
   - Volver a solicitar o cancelar

6. **Paso 5: Seleccionar ubicación de origen**
   - Mostrar ubicaciones donde hay stock de ese combustible
   - Permitir elegir una
   - Guardar ubicación

7. **Paso 6: Resumen y confirmación**
   - Mostrar resumen completo:
     ```
     ========== RESUMEN DE SALIDA ==========
     Vehículo: Excavadora CAT-320
     Horómetro anterior: 1,250 hrs
     Horómetro nuevo: 1,268 hrs
     Horas trabajadas: 18.0 hrs
     Combustible: Diesel
     Cantidad: 45.00 litros
     Ubicación: Bodega Central
     Stock después de salida: 455.00 litros
     ¿Confirma el registro? (S/N):
     ```
   - Leer confirmación

8. **Paso 7: Ejecutar operación**
   - Crear objeto `Movement` con todos los datos
   - Invocar `movementService.createExitMovement(movement, vehicleId)`
   - Si tiene éxito, mostrar: `✅ Salida registrada exitosamente. ID: [uuid]`
   - Mostrar también: `📊 Nuevo stock de [combustible]: XXX litros`
   - Si falla por falta de stock (aunque ya validaste), mostrar mensaje claro

9. **Manejo de errores:**
   - Try-catch amplio
   - Capturar `InsufficientInventoryException` específicamente (cuando la implementes en Fase 7)
   - Mostrar mensaje: `❌ Stock insuficiente: Se requieren XXX litros pero solo hay YYY disponibles`

**✅ Resultado esperado:**
Al ejecutar el wizard:
- El usuario solo ve combustibles que realmente tienen stock
- No puede ingresar cantidades mayores al stock disponible
- No puede ingresar un horómetro menor al actual
- Ve claramente cuántas horas trabajó el vehículo
- Ve el stock resultante antes de confirmar
- La operación es atómica: si algo falla, nada se guarda

**💡 Double validation:**
Aunque valides el stock en la UI, SIEMPRE debes validar también en el servicio. ¿Por qué? Porque en el futuro podrías tener múltiples UIs (web, móvil) o APIs, y todas deben pasar por la misma validación del servicio.

**⚠️ RECUERDA: Race conditions:**
Si dos usuarios intentan sacar combustible al mismo tiempo, ambos podrían pasar la validación de stock en la UI, pero uno de ellos debería fallar en el servicio. Por eso la validación de stock en el servicio debe hacerse DENTRO de la transacción.

**🔍 Depuración:**
Si el horómetro no se actualiza, verifica que estés pasando el `vehicleId` correctamente al servicio y que el UPDATE de vehículos esté dentro de la misma transacción que el movimiento.

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
