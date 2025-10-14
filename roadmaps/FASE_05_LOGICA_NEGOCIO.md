# ⚙️ FASE 5: LÓGICA DE NEGOCIO Y TRANSACCIONES (Semanas 8-9)

> Objetivo general: implementar reglas de negocio complejas asegurando consistencia de datos mediante transacciones.

---

## 🧠 Mindset antes de iniciar

- 🔄 **Diagrama de flujo:** dibuja el proceso completo de una entrada y una salida (paso a paso) antes de codificar.
- 📦 **Casos de prueba:** define los escenarios mínimos (éxito, datos inválidos, inventario insuficiente) y regístralos en el log.
- 🧮 **Verificación manual:** calcula a mano cómo debe quedar el inventario tras cada operación para contrastar con la ejecución real.

---

## ✅ Checkpoint 5.1: Movimiento de ENTRADA con transacción

**Concepto clave:** Garantizar atomicidad entre múltiples operaciones (INSERT + UPDATE deben ser todo o nada).

**📍 DÓNDE:** 
- Clase `MovementService` en `MovementService.java`
- Crear método nuevo `createEntryMovement(Movement movement)`

**🎯 PARA QUÉ:** 
Cuando registras una entrada de combustible, NO es solo insertar un movimiento. Debes:
- **Guardar el movimiento** en la tabla combustibles_movements
- **Actualizar el inventario** sumando la cantidad recibida
- **Asegurar** que si una operación falla, la otra también se deshaga (no quieres un movimiento registrado sin inventario actualizado)

Sin transacciones, podrías tener inconsistencias: movimiento guardado pero inventario sin actualizar (¡desastre!)

**🔗 CONEXIÓN FUTURA:**
- En Fase 6, el usuario usará el wizard de entrada que invocará este método
- En Fase 8, agregarás validaciones con excepciones personalizadas
- En Fase 9, los reportes se basarán en que estos datos sean consistentes
- En Fase 10, podrás auditar cada transacción

**Prompts sugeridos:**
```text
"Explícame el acrónimo ACID con ejemplos orientados a movimientos de combustible."
"¿Cuándo debo usar rollback dentro de una transacción?"
"¿Qué pasa si no restablezco autoCommit a true?"
```

**Tareas paso a paso:**

1. **Validaciones previas (antes de tocar la BD):**
   - Verificar que movement.getType() sea "ENTRADA"
   - Verificar que quantity > 0 y unitPrice > 0
   - Verificar que el producto (fuelType) exista en combustibles_products

2. **Iniciar transacción:**
   - Obtener Connection
   - Llamar a `conn.setAutoCommit(false)` (desactiva commit automático)

3. **Operación 1 - Insertar movimiento:**
   - Construir query INSERT para combustibles_movements
   - Incluir todos los campos (id con UUID, date con GETDATE(), etc.)
   - Ejecutar con PreparedStatement

4. **Operación 2 - Actualizar inventario:**
   - Primero verificar si existe registro en combustibles_inventory para ese combustible y ubicación
   - Si existe: UPDATE sumando la cantidad
   - Si no existe: INSERT creando nuevo registro
   - Ejecutar con PreparedStatement

5. **Confirmar transacción:**
   - Si ambas operaciones fueron exitosas: `conn.commit()`
   - Si alguna falló: `conn.rollback()`

6. **Bloque finally:**
   - SIEMPRE restablecer `conn.setAutoCommit(true)`
   - Cerrar recursos

7. **Logging:**
   - Registrar cada paso (inicio transacción, operación 1, operación 2, commit/rollback)
   - Si falla, registrar el error específico

**✅ Resultado esperado:** 
- Al crear entrada, ver movimiento en combustibles_movements Y ver inventario actualizado
- Si algo falla, NO debe quedar ningún registro (rollback automático)
- Mensaje claro de éxito o error

**💡 Concepto ACID:**
- **A**tomicity: Todo o nada
- **C**onsistency: Datos siempre válidos
- **I**solation: Otras transacciones no ven cambios a medias
- **D**urability: Una vez confirmado, no se pierde

**⚠️ PELIGRO:** Si olvidas restablecer autoCommit o cerrar la conexión, podrías bloquear la BD.

**🔍 Depuración:** Coloca breakpoints antes de commit y rollback para inspeccionar el estado de la BD.

**⏱️ Tiempo estimado:** 4-5 horas

---

## ✅ Checkpoint 5.2: Movimiento de SALIDA con validación de stock

**Concepto clave:** Validar inventario disponible ANTES de permitir la salida y actualizar datos relacionados.

**📍 DÓNDE:** 
- Clase `MovementService` en `MovementService.java`
- Crear método `createExitMovement(Movement movement, String vehicleId)`

**🎯 PARA QUÉ:** 
Una salida es más compleja que una entrada porque:
- **NO puedes vender** lo que no tienes (validar stock primero)
- **Debes restar** del inventario
- **Debes actualizar** el vehículo (horómetro, horas trabajadas)
- **Todo debe ser atómico** (si no hay stock, no actualizar nada)

**🔗 CONEXIÓN FUTURA:**
- En Fase 6, el wizard de salida usará este método
- En Fase 7, lanzarás InsufficientInventoryException si no hay stock
- En Fase 9, generarás reportes de consumo por vehículo
- En Fase 10, auditarás quién autorizó salidas grandes

**Prompts sugeridos:**
```text
"¿Cómo valido que hay stock suficiente antes de hacer la transacción?"
"¿Qué hago si el vehículo no existe?"
"¿Debo validar que el nuevo horómetro sea mayor al anterior?"
```

**Tareas paso a paso:**

1. **Validaciones previas:**
   - Verificar que movement.getType() sea "SALIDA"
   - Verificar que vehicleId exista en combustibles_vehicles
   - Verificar que quantity > 0

2. **Validar inventario disponible:**
   - Consultar combustibles_inventory para ese fuelType y ubicación
   - Si no existe registro: rechazar (no hay stock)
   - Si existe: verificar que available_quantity >= movement.getQuantity()
   - Si no hay suficiente: rechazar con mensaje claro

3. **Iniciar transacción:**
   - `conn.setAutoCommit(false)`

4. **Operación 1 - Insertar movimiento:**
   - INSERT en combustibles_movements (similar a entrada)
   - Incluir vehicleId en el registro

5. **Operación 2 - Actualizar inventario:**
   - UPDATE restando la cantidad: `SET available_quantity = available_quantity - ?`
   - WHERE fuelType = ? AND location = ?

6. **Operación 3 - Actualizar vehículo (si aplica):**
   - UPDATE combustibles_vehicles
   - Actualizar horómetro, calcular horas trabajadas
   - Actualizar fecha de última carga

7. **Confirmar o revertir:**
   - `commit()` si todo bien
   - `rollback()` si alguna operación falló

8. **Finally y logging:**
   - Restablecer autoCommit
   - Registrar resultado

**✅ Resultado esperado:** 
- Solo permitir salidas cuando hay stock suficiente
- Inventario y vehículo actualizados correctamente
- Si falla algo, ningún dato cambia

**💡 Clave:** La validación de stock debe ser DENTRO de la transacción para evitar race conditions (dos salidas simultáneas).

**⏱️ Tiempo estimado:** 4-5 horas

---

## ✅ Checkpoint 5.3: Consultar y mostrar inventario

**Concepto clave:** Presentar información agregada de forma legible y útil para el usuario.

**📍 DÓNDE:** 
- Crear nueva clase `InventoryService` en `src/main/java/com/forestech/services/`
- Opcionalmente crear clase `InventoryRecord` en `models` para mapear resultados

**🎯 PARA QUÉ:** 
El inventario es el corazón de Forestech. Necesitas:
- **Ver en tiempo real** cuánto combustible hay de cada tipo
- **Alertas** cuando el stock está bajo
- **Organizar** por ubicación para saber dónde está cada producto
- **Calcular valor monetario** del inventario

**🔗 CONEXIÓN FUTURA:**
- En Fase 6, el menú mostrará el inventario con este servicio
- En Fase 9, generarás reportes de rotación de inventario
- En Fase 10, exportarás inventario a Excel/PDF

**Prompts sugeridos:**
```text
"¿Cómo formateo tablas ASCII prolijas en consola?"
"Explícame String.format y los especificadores de formato."
"¿Qué es mejor: retornar String formateado o retornar objetos y formatear en UI?"
```

**Tareas paso a paso:**

1. **Crear clase InventoryRecord (opcional pero recomendado):**
   - Atributos: fuelType, location, availableQuantity, unitPrice, lastUpdated
   - Constructor, getters
   - Método getTotalValue() que calcule quantity × price

2. **Crear InventoryService con métodos:**

   **a) getAllInventory():**
   - SELECT de combustibles_inventory con JOIN a combustibles_products
   - Retorna List<InventoryRecord>
   - Ordena por ubicación y tipo de combustible

   **b) getInventoryByLocation(String location):**
   - Filtra por ubicación específica
   - Útil para reportes por sede

   **c) getInventoryByFuelType(String fuelType):**
   - Muestra dónde hay stock de un combustible específico
   - Útil para saber en qué ubicación buscar

   **d) getLowStockAlerts(double threshold):**
   - Retorna productos con quantity < threshold
   - Marca con ⚠️ en la salida
   - Útil para reordenar a proveedores

3. **Método para mostrar en consola:**
   - `displayInventory()` que formatea en tabla ASCII
   - Columnas: Combustible | Ubicación | Cantidad | Precio Unit. | Valor Total
   - Usa String.format para alinear columnas
   - Muestra totales al final

**✅ Resultado esperado:** 
Ver tabla clara del inventario como:
```
========================================
INVENTARIO ACTUAL - FORESTECH
========================================
Combustible         | Ubicación | Cant.  | P.Unit | Total
--------------------|-----------|--------|--------|----------
Diesel              | Bodega A  | 500.00 | $3.45  | $1,725.00
Gasolina Regular    | Bodega A  | 300.00 | $4.20  | $1,260.00
⚠️ Diesel           | Bodega B  |  45.00 | $3.50  | $  157.50
========================================
VALOR TOTAL INVENTARIO: $3,142.50
```

**💡 Tips de formato:**
- Usa `%-20s` para alinear a la izquierda
- Usa `%8.2f` para números con 2 decimales alineados a la derecha
- Usa `String.format("%,. 2f", valor)` para separadores de miles

**⏱️ Tiempo estimado:** 3-4 horas

---

## 🧪 Refuerzos y pruebas sugeridas

**Casos de prueba a cubrir:**

1. **Entrada válida:**
   - Insertar 100 galones de Diesel
   - Verificar en BD que movimiento existe
   - Verificar que inventario aumentó en 100

2. **Salida con stock suficiente:**
   - Sacar 50 galones de Diesel
   - Verificar que inventario disminuyó en 50
   - Verificar que vehículo se actualizó

3. **Salida sin stock (debe fallar):**
   - Intentar sacar 1000 galones cuando solo hay 50
   - Verificar que rechaza con mensaje claro
   - Verificar que inventario NO cambió

4. **Transacción interrumpida:**
   - Simular error en mitad de transacción (ej: cerrar BD)
   - Verificar que rollback funciona

**Registro de pruebas:**
- Documenta inputs, outputs esperados y resultados reales
- Antes/después: haz SELECT del inventario para confirmar cambios
- Anota tiempos de ejecución de transacciones

**Ejercicio extra:**
Implementa entrada/salida para ubicaciones múltiples y traslados entre bodegas.

---

## ✅ Checklist de salida de Fase 5

- [ ] Comprendo y puedo explicar ACID con ejemplos de Forestech
- [ ] Sé usar setAutoCommit(false), commit() y rollback()
- [ ] Los movimientos de ENTRADA actualizan inventario correctamente
- [ ] Los movimientos de SALIDA validan stock antes de permitir
- [ ] Las transacciones se revierten completamente si algo falla
- [ ] El inventario se muestra organizado y legible
- [ ] Puedo calcular valor total del inventario
- [ ] Documenté decisiones, casos de prueba y aprendizajes en JAVA_LEARNING_LOG.md

**🎯 Desafío final:** 
Implementa un método `transferInventory(String fuelType, String fromLocation, String toLocation, double quantity)` que mueva combustible entre bodegas usando transacciones.

---

## 🚀 Próximo paso: FASE 6 - Interfaz de Usuario en Consola

En la siguiente fase aprenderás a:
- Crear un menú principal con bucle infinito y submenús
- Implementar wizards paso a paso para entrada/salida
- Validar entrada del usuario sin romper la aplicación
- Limpiar consola y formatear mensajes profesionales

**¿Por qué UI es importante?** Hasta ahora probaste todo desde Main.java. Con una UI completa, cualquier usuario podrá operar Forestech sin saber programar.

**⏱️ Tiempo total Fase 5:** 12-15 horas distribuidas en 2 semanas
