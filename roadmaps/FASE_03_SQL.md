# 🔌 FASE 3: CONEXIÓN A SQL SERVER (Semana 5)

> Objetivo general: comprender JDBC, conectar Java con SQL Server y ejecutar las primeras consultas.

---

## 🧠 Enfoque previo

- 📚 **Fundamentos SQL:** antes de tocar JDBC, repasa manualmente en SQL Server Management Studio o Azure Data Studio:
  - Consultas básicas `SELECT`, `INSERT`, `UPDATE`, `DELETE`
  - Conceptos de tablas, columnas, tipos de datos, PK/FK, normalización ligera
  - Cláusulas `WHERE`, `ORDER BY`, `GROUP BY`
- 📝 Documenta en `JAVA_LEARNING_LOG.md` las consultas manuales que ejecutaste y resultados
- 🧪 Practica consultas con `FORESTECH` para tener contexto cuando programes

---

## ✅ Checkpoint 3.1: Configurar JDBC Driver

**Concepto clave:** JDBC es la API estándar de Java para comunicarse con bases de datos.

**📍 DÓNDE:** 
- Archivo `pom.xml` en la raíz del proyecto `forestech-cli-java/`
- Terminal para ejecutar Maven

**🎯 PARA QUÉ:** 
Sin el driver JDBC de SQL Server, Java no puede "hablar" con tu base de datos. El driver:
- **Traduce** las llamadas de Java a comandos que SQL Server entiende
- **Gestiona** la conexión de red entre tu aplicación y el servidor
- **Maneja** el protocolo de comunicación específico de SQL Server

**🔗 CONEXIÓN FUTURA:**
- En Fase 4 usarás este driver para INSERT, UPDATE, DELETE
- En Fase 5 ejecutarás queries complejas con JOINs
- En Fase 9 generarás reportes consultando datos históricos

**Prompts sugeridos:**
```text
"Explícame con analogía cómo funciona JDBC como puente entre Java y SQL Server."
"¿Dónde guarda Maven las dependencias y cómo puedo verificarlo?"
"¿Qué diferencia hay entre JDBC y el driver específico de SQL Server?"
```

**Tareas paso a paso:**

1. **Abrir `pom.xml`** en el editor

2. **Agregar la dependencia** del driver de SQL Server:
   - Busca la sección `<dependencies>`
   - Agrega una nueva dependencia para `mssql-jdbc`
   - Usa la versión 12.8.1.jre11 o superior

3. **Guardar** el archivo pom.xml

4. **Compilar** desde terminal:
   - Navega a `forestech-cli-java/`
   - Ejecuta Maven para descargar dependencias

5. **Verificar descarga:**
   - El driver debe estar en tu repositorio local de Maven (~/.m2/repository/)
   - Busca la carpeta com/microsoft/sqlserver/mssql-jdbc/

**✅ Resultado esperado:** 
- Maven descarga el driver sin errores
- El proyecto compila exitosamente
- Puedes ver el JAR del driver en tu repositorio local

**⏱️ Tiempo estimado:** 30 minutos

---

## ✅ Checkpoint 3.2: Clase `DatabaseConnection`

**Concepto clave:** Encapsular la lógica de conexión y evitar repetir credenciales en todo el código.

**📍 DÓNDE:** 
- Crear carpeta `config` dentro de `com/forestech/`
- Crear archivo `DatabaseConnection.java` en `src/main/java/com/forestech/config/`

**🎯 PARA QUÉ:** 
Sin esta clase, cada vez que necesites la BD tendrías que escribir las credenciales y lógica de conexión. Con DatabaseConnection:
- **Centralizas** la configuración (URL, usuario, contraseña)
- **Reutilizas** el método de conexión desde cualquier servicio
- **Facilitas cambios** (si cambias de BD, solo modificas un archivo)
- **Pruebas más simples** (puedes cambiar a BD de prueba fácilmente)

**🔗 CONEXIÓN FUTURA:**
- En Fase 4, todos los servicios (MovementService, VehicleService) usarán esta clase
- En Fase 5, agregarás manejo de transacciones aquí
- En Fase 8, migrarás credenciales a un archivo externo (properties)
- En Fase 10, configurarás diferentes conexiones para dev/producción

**Prompts sugeridos:**
```text
"¿Qué es una connection string y cuáles son sus partes?"
"¿Por qué es mala práctica hardcodear credenciales en el código?"
"Explícame qué hace try-with-resources y por qué es importante para conexiones."
```

**Tareas paso a paso:**

1. **Crear el paquete `config`:**
   - En IntelliJ: clic derecho en `com.forestech` → New → Package → "config"

2. **Crear la clase DatabaseConnection:**
   - Clase pública con constructor privado (no queremos instancias)

3. **Declarar constantes privadas:**
   - URL de conexión (jdbc:sqlserver://localhost:1433;databaseName=FORESTECH)
   - Usuario de SQL Server
   - Contraseña de SQL Server

4. **Crear método estático `getConnection()`:**
   - Retorna un objeto Connection
   - Puede lanzar SQLException
   - Usa DriverManager para obtener la conexión

5. **Crear método estático `testConnection()`:**
   - Usa try-with-resources para obtener conexión
   - Si conecta, imprime mensaje de éxito y nombre de la BD
   - Si falla, captura la excepción e imprime mensaje claro

6. **Probar en Main.java:**
   - Llama a `DatabaseConnection.testConnection()`
   - Ejecuta y verifica conexión exitosa

**✅ Resultado esperado:** 
- Ver mensaje "✅ Conexión exitosa a FORESTECH" en consola
- Si falla, ver mensaje de error claro indicando el problema

**⚠️ RECUERDA:** 
- SQL Server debe estar corriendo
- El usuario/contraseña deben ser correctos
- El puerto (1433) debe estar abierto

**🔍 Depuración:** Si falla, el error te dirá si es problema de red, credenciales o BD inexistente.

**⏱️ Tiempo estimado:** 2 horas

---

## ✅ Checkpoint 3.3: Primera query `SELECT`

**Concepto clave:** Usar `Statement` / `ResultSet` para leer filas de la base de datos.

**📍 DÓNDE:** 
- Opción 1: Dentro de `Main.java` temporalmente
- Opción 2: Crear clase `ProductService` en paquete `services`

**🎯 PARA QUÉ:** 
Hasta ahora solo probaste la conexión. Ahora necesitas:
- **Leer datos** de las tablas existentes en SQL Server
- **Convertir** las filas SQL en objetos Java (por ahora solo imprimir)
- **Entender** el flujo: Connection → Statement → ResultSet → Datos

**🔗 CONEXIÓN FUTURA:**
- En Fase 4, este mismo patrón lo usarás para SELECT con WHERE
- En Fase 5, mapearás ResultSet a objetos Product completos
- En Fase 6, mostrarás estos productos en el menú del usuario
- En Fase 9, generarás reportes consultando múltiples tablas

**Prompts sugeridos:**
```text
"¿Cuál es la diferencia entre Statement y PreparedStatement?"
"Explícame como cursor qué hace ResultSet al moverse con rs.next()."
"¿Por qué necesito try-with-resources para Connection, Statement y ResultSet?"
```

**Tareas paso a paso:**

1. **Crear método `listProducts()`:**
   - Puede ser en Main.java o en nueva clase ProductService
   - Método estático (por ahora)
   - No recibe parámetros, no retorna nada (solo imprime)

2. **Dentro del método:**
   - Obtén conexión usando DatabaseConnection.getConnection()
   - Crea Statement desde la conexión
   - Define tu query SQL: SELECT id, name, type, unit FROM combustibles_products
   - Ejecuta la query y obtén ResultSet

3. **Recorrer el ResultSet:**
   - Usa while(rs.next()) para iterar sobre las filas
   - Extrae cada columna con rs.getString("nombre_columna")
   - Imprime los datos con formato legible

4. **Manejo de recursos:**
   - Usa try-with-resources para cerrar automáticamente
   - Captura SQLException y muestra mensaje de error

5. **Probar desde Main.java:**
   - Llama al método listProducts()
   - Verifica que se muestran todos los productos de la tabla

**✅ Resultado esperado:** 
- Ver lista de productos de la BD en consola
- Al final, mostrar total de productos listados
- No errores de conexión

**💡 Concepto clave:** ResultSet es como un cursor que apunta a una fila. Cada vez que llamas next(), avanza a la siguiente. Cuando no hay más filas, next() retorna false.

**⚠️ CUIDADO:** 
- Los nombres de columnas en rs.getString() deben coincidir con tu tabla
- Si la tabla está vacía, el while no se ejecutará (no es error)

**🔍 Depuración:** Coloca breakpoint dentro del while y observa los valores de cada columna.

**⏱️ Tiempo estimado:** 2-3 horas

---

## ✏️ Refuerzos adicionales de la fase

**Conceptos importantes a dominar:**

1. **try-with-resources:**
   - ¿Por qué es mejor que try-finally?
   - ¿Qué interfaces deben implementar los recursos?
   - ¿Qué pasa si hay excepción durante el cierre?

2. **SQLException:**
   - ¿Qué información contiene?
   - ¿Cómo extraer el código de error específico?
   - ¿Cuáles son los errores más comunes? (timeout, credenciales, tabla no existe)

3. **Arquitectura JDBC:**
   - DriverManager → Connection → Statement → ResultSet
   - ¿Para qué sirve cada componente?
   - ¿Cuándo se cierra cada uno?

**Ejercicios de refuerzo:**

- Modifica listProducts() para que retorne el conteo de productos en vez de solo imprimir
- Crea listVehicles() siguiendo el mismo patrón
- Intenta una query con WHERE (ej: solo productos de tipo "Diesel")

---

## 🧾 Checklist de salida de Fase 3

- [ ] Agregué correctamente la dependencia JDBC en pom.xml
- [ ] Puedo explicar qué hace el driver JDBC y por qué lo necesito
- [ ] Mi clase DatabaseConnection centraliza la configuración de BD
- [ ] Probé la conexión exitosamente con testConnection()
- [ ] Ejecuté mi primera query SELECT y vi resultados en consola
- [ ] Entiendo el flujo: Connection → Statement → executeQuery → ResultSet → rs.next()
- [ ] Sé usar try-with-resources para cerrar conexiones automáticamente
- [ ] Puedo explicar qué es SQLException y cómo manejarla
- [ ] Documenté en JAVA_LEARNING_LOG.md los aprendizajes y problemas encontrados

**⚠️ Problemas comunes y soluciones:**

| Problema | Posible causa | Solución |
|----------|--------------|----------|
| "No suitable driver found" | Driver no descargado | Ejecuta mvn clean install |
| "Cannot connect to server" | SQL Server no corriendo | Inicia el servicio SQL Server |
| "Login failed" | Credenciales incorrectas | Verifica usuario/contraseña |
| "Database does not exist" | BD no creada | Crea BD FORESTECH en SQL Server |

---

## 🚀 Próximo paso: FASE 4 - Operaciones CRUD

En la siguiente fase aprenderás a:
- Usar PreparedStatement para evitar SQL Injection
- Insertar datos (INSERT) desde objetos Java
- Actualizar registros (UPDATE) con validaciones
- Eliminar datos (DELETE) con cuidado
- Mapear ResultSet a objetos de tu modelo

**¿Por qué CRUD es importante?** Ahora solo lees datos. Con CRUD podrás crear, actualizar y eliminar movimientos, vehículos y proveedores desde tu aplicación Java, haciendo que Forestech sea completamente funcional.

**⏱️ Tiempo total Fase 3:** 5-8 horas distribuidas en 1 semana
