# Memoria de contexto del proyecto Forestech CLI

## 🔗 Acceso a Base de Datos vía MCP

**IMPORTANTE:** Claude Code tiene acceso completo a la base de datos MySQL a través de MCP Server.

### Configuración activa del MCP:
- **Servidor MCP:** @benborla29/mcp-server-mysql
- **Base de datos:** FORESTECHOIL
- **Usuario:** root
- **Host:** localhost:3306
- **Permisos:** LECTURA Y ESCRITURA (MYSQL_READONLY=false)
- **Archivo de configuración:** `/home/hp/.claude.json`

### Tablas disponibles en FORESTECHOIL:
1. **Movement** - Movimientos de combustible (ENTRADA/SALIDA)
   - Campos: id, movementType, productType, unidadDeMedida, quantity, unitPrice, movementDate
2. **detalle_factura** - Detalles de facturas
3. **facturas** - Facturas principales
4. **oil_products** - Catálogo de productos de combustible
5. **vehicles** - Vehículos de la flota

### Capacidades disponibles:
- ✅ Inspeccionar esquemas de tablas (DESCRIBE, SHOW TABLES)
- ✅ Ejecutar consultas SELECT para análisis de datos
- ✅ Ejecutar INSERT/UPDATE/DELETE (permisos completos activados)
- ✅ Generar código JDBC preciso basado en el esquema real
- ✅ Validar queries SQL antes de implementarlas en Java
- ✅ Analizar datos existentes para debugging

### Uso recomendado:
- Consultar la estructura de tablas antes de generar código JDBC
- Validar que las inserciones/actualizaciones funcionen correctamente
- Analizar datos de prueba durante el desarrollo de la Fase 3+
- Sugerir optimizaciones basadas en el esquema real

### Seguridad:
- Las credenciales están protegidas en `.gitignore`
- El archivo `MCP_INSTALLATION_VSCODE.md` contiene la documentación completa
- VS Code Insiders puede configurarse siguiendo la guía creada

---

## 📚 Estado del proyecto

**Fase actual:** Fase 3 - Conexión MySQL/JDBC
**Última actualización MCP:** 2025-11-12

