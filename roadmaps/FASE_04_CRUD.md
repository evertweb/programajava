# 🛠️ FASE 4: OPERACIONES CRUD (Semanas 6-7)

> Objetivo general: crear, leer, actualizar y eliminar datos en SQL Server desde Java con buenas prácticas de seguridad y validación.

---

## 🧠 Preparación

- 📄 Repasa consultas SQL `INSERT`, `UPDATE`, `DELETE`, `SELECT ... WHERE` directamente en la base
- 🔐 Reflexiona sobre riesgos de SQL Injection y cómo los evita `PreparedStatement`
- 🧪 Configura tests manuales: antes de automatizar, define registros de prueba y limpia la tabla tras cada ejercicio
- 🐞 Depura cada operación observando valores en el `PreparedStatement` justo antes de ejecutar

---

## ✅ Checkpoint 4.1: Insertar movimientos (`INSERT`)

**Concepto clave:** `PreparedStatement` con placeholders `?` evita inyección SQL y optimiza ejecución.

**📍 DÓNDE:** 
- Crear carpeta `services` dentro de `com/forestech/`
- Crear archivo `MovementService.java` en `src/main/java/com/forestech/services/`

**🎯 PARA QUÉ:** 
Hasta ahora solo lees datos. Insertar es CRÍTICO porque:
- **Permite** que el usuario registre nuevos movimientos desde la aplicación
- **Persiste** los objetos Movement que creas en Java hacia la BD
- **Evita** perder datos al cerrar la aplicación
- **Comienza** el ciclo completo de gestión de datos

**🔗 CONEXIÓN FUTURA:**
- En Fase 5, agregarás validaciones de negocio antes de insertar (verificar inventario)
- En Fase 6, el usuario usará el menú para crear movimientos que se guardan con este método
- En Fase 7, manejarás errores específicos (duplicados, violaciones de FK)
- En Fase 9, consultarás estos movimientos para reportes

**Prompts sugeridos:**
```text
"Muéstrame paso a paso cómo llenar un PreparedStatement evitando SQL Injection."
"¿Cuál es la diferencia entre executeUpdate y executeQuery?"
"¿Por qué PreparedStatement es más seguro que Statement?"
```

**Tareas paso a paso:**

1. **Crear paquete y clase:**
   - Crea paquete `services`
   - Crea clase `MovementService` (sin instancias, métodos estáticos por ahora)

2. **Crear método `createMovement(Movement movement)`:**
   - Recibe un objeto Movement completo
   - Retorna boolean (true si se guardó, false si falló)

3. **Dentro del método:**
   - Obtén conexión con DatabaseConnection
   - Construye query INSERT con placeholders (?)
   - Crea PreparedStatement
   - Asigna valores a los placeholders usando setString, setDouble, etc.
   - Ejecuta con executeUpdate()
   - Verifica filas afectadas (debe ser 1)

4. **Manejo de errores:**
   - Usa try-catch para SQLException
   - Imprime mensaje claro si falla
   - Retorna false en caso de error

5. **Probar en Main.java:**
   - Crea un objeto Movement
   - Llama a MovementService.createMovement(movement)
   - Verifica en SQL Server que se insertó el registro

**✅ Resultado esperado:** 
- Crear Movement en Java y verlo aparecer en la tabla SQL
- Mensaje de éxito en consola
- Filas afectadas = 1

**💡 Concepto clave:** Los placeholders (?) se llenan en orden. El primer setString corresponde al primer ?, el segundo al segundo ?, etc.

**⚠️ PELIGRO - SQL Injection:** 
NUNCA hagas: `"INSERT INTO ... VALUES ('" + variable + "')"`
SIEMPRE usa PreparedStatement con placeholders.

**🔍 Depuración:** Coloca breakpoint después de crear el PreparedStatement y observa la query completa.

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 4.2: Consultas filtradas (`SELECT ... WHERE`)

**Concepto clave:** Mapear `ResultSet` a objetos de dominio con PreparedStatement parametrizado.

**📍 DÓNDE:** 
- Clase `MovementService` en `MovementService.java`
- Crear métodos adicionales de consulta

**🎯 PARA QUÉ:** 
No siempre quieres TODOS los movimientos, necesitas filtrar:
- **Movimientos de tipo ENTRADA** solamente
- **Movimientos de un combustible específico**
- **Movimientos de una fecha específica**
- **Movimientos ordenados** por fecha o valor

**🔗 CONEXIÓN FUTURA:**
- En Fase 6, el menú mostrará movimientos filtrados por tipo o fecha
- En Fase 9, generarás reportes consultando por rangos de fechas
- En Fase 5, combinarás con JOINs para obtener datos relacionados

**Prompts sugeridos:**
```text
"¿Cómo mapeo un ResultSet a un objeto Movement?"
"¿Por qué retornar List<Movement> en vez de imprimir directamente?"
"¿Qué hago si la consulta no retorna resultados?"
```

**Tareas paso a paso:**

1. **Crear método `getMovementsByType(String type)`:**
   - Recibe el tipo como parámetro ("ENTRADA" o "SALIDA")
   - Retorna List<Movement>

2. **Construir query con WHERE:**
   - SELECT * FROM combustibles_movements WHERE type = ?
   - Agrega ORDER BY date DESC para los más recientes primero

3. **Crear PreparedStatement y asignar parámetro:**
   - Usa setString para el placeholder del tipo

4. **Recorrer ResultSet y mapear a objetos:**
   - Por cada fila, crea un objeto Movement
   - Extrae cada columna y pásala al constructor de Movement
   - Agrega cada Movement al ArrayList

5. **Retornar la lista:**
   - Si no hay resultados, retorna lista vacía (NO null)

6. **Crear métodos similares:**
   - `getAllMovements()` - sin filtro
   - `getMovementsByFuelType(String fuelType)` - filtrar por combustible

7. **Probar en Main.java:**
   - Llama a cada método
   - Imprime los resultados usando for-each
   - Verifica que los filtros funcionen

**✅ Resultado esperado:** 
- Obtener listas de movimientos filtradas correctamente
- Poder recorrer e imprimir cada Movement
- Lista vacía si no hay coincidencias (no error)

**💡 Patrón importante:** Siempre retorna colecciones vacías, nunca null. Esto evita NullPointerException.

**🔍 Depuración:** Inspecciona el ArrayList y verifica que los objetos Movement tengan los datos correctos.

**⏱️ Tiempo estimado:** 3 horas

---

## ✅ Checkpoint 4.3: Actualizar vehículos (`UPDATE`)

**Concepto clave:** Validar existencia previa y entender impacto de `WHERE` en UPDATE.

**📍 DÓNDE:** 
- Crear archivo `VehicleService.java` en `src/main/java/com/forestech/services/`

**🎯 PARA QUÉ:** 
Los datos cambian con el tiempo:
- **Corregir errores** en datos capturados
- **Actualizar información** (ej: nuevo número de placa)
- **Modificar estado** (ej: vehículo activo/inactivo)

Sin UPDATE, tendrías que eliminar y crear nuevamente (¡perdiendo historial!)

**🔗 CONEXIÓN FUTURA:**
- En Fase 5, actualizarás cantidades de inventario después de cada movimiento
- En Fase 6, el usuario podrá editar datos desde el menú
- En Fase 7, validarás permisos antes de permitir actualizaciones
- En Fase 8, registrarás auditoría de qué cambió

**Prompts sugeridos:**
```text
"¿Qué pasa si hago UPDATE sin WHERE?"
"¿Cómo verifico que el registro existe antes de actualizar?"
"¿Qué significa que executeUpdate() retorne 0?"
```

**Tareas paso a paso:**

1. **Crear VehicleService:**
   - Similar a MovementService
   - Métodos estáticos por ahora

2. **Crear método `updateVehicle(String id, Vehicle vehicle)`:**
   - Recibe el id del vehículo a actualizar
   - Recibe el objeto Vehicle con los nuevos datos
   - Retorna boolean (true si actualizó, false si no)

3. **Verificar existencia (IMPORTANTE):**
   - Antes de actualizar, ejecuta SELECT COUNT(*) WHERE id = ?
   - Si retorna 0, el vehículo no existe
   - Imprime mensaje y retorna false

4. **Construir query UPDATE:**
   - UPDATE combustibles_vehicles SET plate = ?, model = ?, category = ? WHERE id = ?
   - Usa placeholders para todos los valores

5. **Ejecutar UPDATE:**
   - Asigna valores con setString
   - Ejecuta executeUpdate()
   - Verifica filas afectadas (debe ser 1)

6. **Probar en Main.java:**
   - Inserta un vehículo
   - Modifícalo con updateVehicle
   - Verifica en SQL Server que cambió
   - Intenta actualizar un id que no existe

**✅ Resultado esperado:** 
- Actualizar vehículo existente exitosamente
- Rechazar actualización si el id no existe
- Mensaje claro en ambos casos

**⚠️ PELIGRO:** UPDATE sin WHERE actualiza TODAS las filas. Siempre verifica tu query.

**💡 Buena práctica:** Imprime cuántos campos cambiaron (puedes comparar antes/después).

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✅ Checkpoint 4.4: Eliminar con validación (`DELETE`)

**Concepto clave:** Preservar integridad referencial antes de eliminar.

**📍 DÓNDE:** 
- Clase `SupplierService` en nuevo archivo `SupplierService.java`
- O agregar método en MovementService

**🎯 PARA QUÉ:** 
Eliminar datos es PELIGROSO:
- **No se puede deshacer** fácilmente
- **Puede romper relaciones** con otras tablas
- **Puede perder información valiosa** para auditoría

Por eso necesitas validaciones estrictas.

**🔗 CONEXIÓN FUTURA:**
- En Fase 5, implementarás "soft delete" (marcar como inactivo en vez de borrar)
- En Fase 7, agregarás confirmación del usuario antes de eliminar
- En Fase 8, registrarás quién eliminó y cuándo (auditoría)
- En Fase 9, los reportes solo mostrarán registros activos

**Prompts sugeridos:**
```text
"Explícame integridad referencial y qué podría romperse si borro sin validar."
"¿Qué es un DELETE en cascada y cuándo conviene usarlo?"
"¿Qué es soft delete y cuáles son sus ventajas?"
```

**Tareas paso a paso:**

1. **Crear método `deleteSupplier(String id)`:**
   - Recibe el id del proveedor a eliminar
   - Retorna boolean

2. **Validar dependencias (CRÍTICO):**
   - Antes de borrar, consulta: SELECT COUNT(*) FROM combustibles_movements WHERE supplierId = ?
   - Si hay movimientos asociados, NO eliminar
   - Imprime mensaje: "No se puede eliminar, tiene X movimientos asociados"
   - Retorna false

3. **Si no hay dependencias:**
   - Construye query DELETE con WHERE id = ?
   - Ejecuta executeUpdate()
   - Verifica filas afectadas

4. **Considerar alternativa soft delete:**
   - En vez de DELETE, usa UPDATE para cambiar un campo isActive = false
   - Ventaja: conservas el historial

5. **Probar en Main.java:**
   - Intenta eliminar un proveedor CON movimientos (debe rechazar)
   - Intenta eliminar un proveedor SIN movimientos (debe permitir)
   - Verifica en SQL Server el resultado

**✅ Resultado esperado:** 
- Proteger integridad de datos rechazando eliminaciones peligrosas
- Eliminar solo cuando es seguro
- Mensajes claros sobre por qué se rechaza

**💡 Reflexión:** ¿Qué es mejor para Forestech: DELETE real o soft delete? Discútelo con tu IA.

**⏱️ Tiempo estimado:** 2-3 horas

---

## 🧪 Refuerzos de calidad

**Pruebas manuales tras cada operación:**
- Consulta la tabla en SQL Server para verificar estado
- Compara lo que ves en la BD con lo que esperabas
- Verifica conteo de filas antes y después

**Registro de aprendizaje:**
- Anota SQL ejecutado, filas afectadas y resultados esperados vs reales
- Documenta errores encontrados y cómo los resolviste
- Crea script `cleanup.sql` para restaurar datos de prueba

**Testing inicial:**
- Prepara tu primer test JUnit simple (aunque se conecte a BD real)
- Valida que insertar un Movement retorna true
- Valida que insertar con datos inválidos retorna false

---

## ✅ Checklist de salida de Fase 4

- [ ] Domino PreparedStatement y sé cómo evita SQL Injection
- [ ] Puedo explicar la diferencia entre executeQuery y executeUpdate
- [ ] Mis métodos INSERT guardan objetos Java en la BD correctamente
- [ ] Mis métodos SELECT retornan List<Objeto> correctamente mapeados
- [ ] Mis métodos UPDATE verifican existencia antes de actualizar
- [ ] Mis métodos DELETE validan integridad referencial antes de borrar
- [ ] Mis servicios retornan colecciones vacías en vez de null
- [ ] Documenté en JAVA_LEARNING_LOG.md decisiones, casos de prueba y aprendizajes

**🎯 Desafío final:** 
Crea un método `getMovementsByDateRange(String startDate, String endDate)` que retorne movimientos entre dos fechas.

**📊 Tabla de métodos CRUD completados:**

| Servicio | CREATE | READ | UPDATE | DELETE |
|----------|--------|------|--------|--------|
| MovementService | ✅ | ✅ | ⚪ | ⚪ |
| VehicleService | ⚪ | ⚪ | ✅ | ⚪ |
| SupplierService | ⚪ | ⚪ | ⚪ | ✅ |

**Objetivo:** Completar todas las operaciones para al menos MovementService.

---

## 🚀 Próximo paso: FASE 5 - Lógica de Negocio

En la siguiente fase aprenderás a:
- Implementar reglas de negocio (no permitir salidas mayores al inventario)
- Manejar transacciones (varias operaciones que deben ser atómicas)
- Calcular inventario actual dinámicamente
- Validar datos antes de guardar
- Crear métodos de servicio más complejos

**¿Por qué lógica de negocio?** Hasta ahora solo guardas/lees datos. La lógica de negocio asegura que los datos sean VÁLIDOS y CONSISTENTES según las reglas de Forestech (ej: no vender más combustible del que tienes).

**⏱️ Tiempo total Fase 4:** 10-15 horas distribuidas en 2 semanas
