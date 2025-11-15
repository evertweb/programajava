# 📚 GUÍA DE USO - FORESTECH CLI

## 🌲 Sistema de Gestión de Combustibles y Vehículos

**Versión:** 3.0
**Última actualización:** Noviembre 2025
**Autor:** Forestech Development Team

---

## 📋 TABLA DE CONTENIDOS

1. [Introducción](#introducción)
2. [Instalación y Configuración](#instalación-y-configuración)
3. [Iniciar la Aplicación](#iniciar-la-aplicación)
4. [Navegación General](#navegación-general)
5. [Módulo de Movimientos](#módulo-de-movimientos)
6. [Módulo de Productos](#módulo-de-productos)
7. [Módulo de Vehículos](#módulo-de-vehículos)
8. [Módulo de Proveedores](#módulo-de-proveedores)
9. [Módulo de Reportes](#módulo-de-reportes)
10. [Casos de Uso Comunes](#casos-de-uso-comunes)
11. [Solución de Problemas](#solución-de-problemas)
12. [Preguntas Frecuentes](#preguntas-frecuentes)

---

## 🎯 INTRODUCCIÓN

### ¿Qué es Forestech CLI?

Forestech CLI es un sistema de gestión de inventario diseñado específicamente para empresas forestales y madereras que necesitan controlar:

- 📦 **Movimientos de combustibles** (entradas y salidas)
- 🛢️ **Catálogo de productos** (diesel, gasolina, aceites, etc.)
- 🚜 **Flota de vehículos** (camiones, excavadoras, motosierras)
- 🏭 **Proveedores** de combustibles
- 📊 **Reportes** de inventario y consumo

### Características Principales

✅ **Gestión completa de inventario** - Control de entradas y salidas
✅ **Validación de stock** - Previene salidas mayores al disponible
✅ **Selección visual** - Listas interactivas para productos y vehículos
✅ **Búsqueda avanzada** - Buscar productos por nombre parcial
✅ **Reportes en tiempo real** - Stock actualizado al instante
✅ **Interfaz intuitiva** - Menús claros y fáciles de usar

---

## ⚙️ INSTALACIÓN Y CONFIGURACIÓN

### Requisitos Previos

- **Java 17 o superior** instalado
- **Maven 3.x** instalado
- **MySQL 8.0+** corriendo
- **Base de datos FORESTECHOIL** creada

### Verificar Instalación

```bash
# Verificar Java
java -version
# Debe mostrar: java version "17.x.x"

# Verificar Maven
mvn -version
# Debe mostrar: Apache Maven 3.x.x

# Verificar MySQL
sudo service mysql status
# Debe mostrar: mysql is running
```

### Configuración de Base de Datos

1. **Crear la base de datos:**
```sql
CREATE DATABASE FORESTECHOIL;
USE FORESTECHOIL;
```

2. **Ejecutar scripts de creación de tablas:**
```bash
mysql -u root -p FORESTECHOIL < 01_recreate_tables_with_fk.sql
mysql -u root -p FORESTECHOIL < 02_restore_data.sql
mysql -u root -p FORESTECHOIL < 03_add_suppliers_table.sql
```

3. **Verificar tablas creadas:**
```sql
SHOW TABLES;
```

Deberías ver:
- `Movement`
- `oil_products`
- `vehicles`
- `suppliers`
- `facturas`
- `detalle_factura`

---

## 🚀 INICIAR LA APLICACIÓN

### Método 1: Con Maven (Recomendado para desarrollo)

```bash
# 1. Navegar al directorio del proyecto
cd /home/hp/forestechOil/forestech-cli-java

# 2. Compilar el proyecto
mvn clean compile

# 3. Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

### Método 2: Ejecutable JAR (Producción)

```bash
# 1. Generar el JAR
mvn clean package

# 2. Ejecutar el JAR
java -jar target/forestech-cli-java-1.0.0.jar
```

### Pantalla de Bienvenida

Al iniciar, verás:

```
╔═══════════════════════════════════════════════════════════╗
║          🌲 BIENVENIDO A FORESTECH CLI 🌲                   ║
╚═════════════════════════════════════════════════════════════╝
┌───────────────────────────────────────────────────────────┐
│  📋 Proyecto: Forestech CLI
│  🔖 Versión: 1.0.0
│  📅 Año: 2025
│  💾 Base de datos: FORESTECHOIL
│  ✅ Estado: Activo
└───────────────────────────────────────────────────────────┘

✅ BD conectada!

╔══════════════════════════════════════════════════════════╗
║        ¡BIENVENIDO AL SISTEMA DE GESTIÓN FORESTECH!     ║
╚══════════════════════════════════════════════════════════╝
📅 Fecha y hora: 13/11/2025 17:30:45
👤 Sistema de gestión de combustibles, vehículos y más
══════════════════════════════════════════════════════════
```

---

## 🧭 NAVEGACIÓN GENERAL

### Menú Principal

```
╔════════════════════════════════════════════╗
║          🏠 MENÚ PRINCIPAL                 ║
╚════════════════════════════════════════════╝
  1. 📦 Gestionar Movimientos
  2. 🛢️  Gestionar Productos
  3. 🚜 Gestionar Vehículos
  4. 🏭 Gestionar Proveedores
  5. 📊 Reportes
  0. 🚪 Salir
════════════════════════════════════════════
```

### Principios de Navegación

- **Seleccionar opción:** Ingresa el número y presiona ENTER
- **Volver atrás:** Selecciona `0` en cualquier sub-menú
- **Salir completamente:** Selecciona `0` en el menú principal
- **Continuar:** Presiona ENTER cuando veas el mensaje "Presione ENTER para continuar..."

### Convenciones

- ✅ Verde: Operación exitosa
- ❌ Rojo: Error o fallo
- ⚠️ Amarillo: Advertencia
- 📋 Azul: Información
- 👉 Indicador de input del usuario

---

## 📦 MÓDULO DE MOVIMIENTOS

### Sub-menú de Movimientos

```
╔════════════════════════════════════════════╗
║       📦 GESTIÓN DE MOVIMIENTOS            ║
╚════════════════════════════════════════════╝
  1. ➕ Registrar Entrada
  2. ➖ Registrar Salida
  3. 📋 Listar todos los movimientos
  4. 🔍 Buscar movimiento por ID
  5. 📊 Calcular stock de un producto
  6. ✏️  Actualizar movimiento
  7. 🗑️  Eliminar movimiento
  0. 🔙 Volver al menú principal
════════════════════════════════════════════
```

### 1. Registrar Entrada de Combustible

**Propósito:** Registrar la llegada de combustible desde un proveedor.

**Pasos:**

1. Seleccionar opción `1` en el sub-menú de Movimientos

2. **Seleccionar producto:**
   - Verás una tabla con todos los productos disponibles
   - Ingresa el número del producto deseado

```
📋 PRODUCTOS DISPONIBLES:
┌────┬─────────────┬─────────────────────────┬─────────────┬──────────────┐
│ N° │  PRODUCTO   │         NOMBRE          │   UNIDAD    │    PRECIO    │
│ 1  │ FUEL-ABC    │ Diesel Premium          │ Galon       │ $3500.00     │
│ 2  │ FUEL-DEF    │ Gasolina 95             │ Litro       │ $4200.00     │
└────┴─────────────┴─────────────────────────┴─────────────┴──────────────┘

👉 Seleccione el número del producto: 1
✅ Seleccionado: Diesel Premium (ID: FUEL-ABC)
```

3. **Ingresar cantidad:**
```
💧 Ingrese la cantidad: 500
```

4. **Ingresar precio unitario:**
```
💰 Ingrese el precio unitario: 3500
```

5. **Ingresar ID del proveedor (opcional):**
```
🏭 Ingrese ID del proveedor (opcional, presione ENTER para omitir): PROV-12345678
```

6. **Seleccionar unidad de medida:**
```
📏 Unidades de medida:
  1. GALON
  2. GARRAFA
  3. CUARTO
  4. CANECA
Seleccione unidad: 1
```

7. **Confirmación:**
```
✅ ENTRADA registrada exitosamente!
   ID: MOV-A1B2C3D4
   Producto ID: FUEL-ABC
   Cantidad: 500 GALON
   Subtotal: $1750000.00
   IVA: $332500.00
   Total: $2082500.00
```

### 2. Registrar Salida de Combustible

**Propósito:** Registrar el consumo de combustible por un vehículo.

**Pasos:**

1. Seleccionar opción `2` en el sub-menú de Movimientos

2. **Seleccionar producto:**
   - Igual que en Entrada, selecciona de la tabla

3. **Ver stock disponible:**
```
📦 Stock disponible: 500.00 unidades
```

4. **Ingresar cantidad a retirar:**
```
💧 Ingrese la cantidad a retirar: 50
```

5. **Ingresar precio unitario:**
```
💰 Ingrese el precio unitario: 3500
```

6. **Seleccionar vehículo:**
```
🚜 VEHÍCULOS DISPONIBLES:
┌────┬─────────────┬─────────────────────────┬──────────────┬──────────────┐
│ N° │  VEHÍCULO   │         NOMBRE          │  CATEGORÍA   │  CAPACIDAD   │
│ 1  │ VEH-ABC     │ Camión Volvo FH16       │ Camión       │ 300.00       │
│ 2  │ VEH-DEF     │ Excavadora CAT 320      │ Excavadora   │ 150.00       │
└────┴─────────────┴─────────────────────────┴──────────────┴──────────────┘

👉 Seleccione el número del vehículo: 1
✅ Seleccionado: Camión Volvo FH16 (ID: VEH-ABC)
```

7. **Seleccionar unidad de medida:**
```
📏 Unidades de medida:
  1. GALON
  2. GARRAFA
  3. CUARTO
  4. CANECA
Seleccione unidad: 1
```

8. **Confirmación:**
```
✅ SALIDA registrada exitosamente!
   ID: MOV-E5F6G7H8
   Producto ID: FUEL-ABC
   Vehículo ID: VEH-ABC
   Cantidad: 50 GALON
   Stock restante: 450.00
   Total: $208250.00
```

### 3. Listar Todos los Movimientos

**Propósito:** Ver todos los movimientos registrados en el sistema.

**Pasos:**

1. Seleccionar opción `3`

2. **Ver lista:**
```
═══════════════════════════════════════
    📋 LISTA DE MOVIMIENTOS
═══════════════════════════════════════

Total de movimientos: 15
───────────────────────────────────────

🆔 ID: MOV-A1B2C3D4
   📌 Tipo: ENTRADA
   🛢️  Producto ID: FUEL-ABC
   💧 Cantidad: 500 GALON
   💰 Total: $2082500.00
   📅 Fecha: 2025-11-13 17:30:45
───────────────────────────────────────
🆔 ID: MOV-E5F6G7H8
   📌 Tipo: SALIDA
   🛢️  Producto ID: FUEL-ABC
   💧 Cantidad: 50 GALON
   💰 Total: $208250.00
   📅 Fecha: 2025-11-13 17:35:12
───────────────────────────────────────
```

### 4. Buscar Movimiento por ID

**Propósito:** Encontrar un movimiento específico.

**Pasos:**

1. Seleccionar opción `4`
2. Ingresar el ID:
```
🆔 Ingrese el ID del movimiento (MOV-XXXXXXXX): MOV-A1B2C3D4
```

3. **Ver resultado:**
```
✅ Movimiento encontrado:

Movement{id='MOV-A1B2C3D4', movementType='ENTRADA',
productId='FUEL-ABC', vehicleId='null',
numeroFactura='null', unidadDeMedida='GALON',
quantity=500.0, unitPrice=3500.0,
movementDate='2025-11-13 17:30:45'}
```

### 5. Calcular Stock de un Producto

**Propósito:** Ver el stock actual de un producto específico.

**Pasos:**

1. Seleccionar opción `5`
2. Ingresar ID del producto:
```
🛢️  Ingrese el ID del producto: FUEL-ABC
```

3. **Ver stock:**
```
📦 STOCK ACTUAL:
   Producto ID: FUEL-ABC
   Stock: 450.00 unidades
   ✅ Stock normal
```

**Indicadores de stock:**
- ✅ Stock normal: >= 10 unidades
- ⚡ Stock bajo: 1-9 unidades
- ⚠️ Stock vacío: 0 unidades
- ❌ Stock negativo: Error de datos

### 6. Actualizar Movimiento

**Propósito:** Modificar la cantidad o precio de un movimiento existente.

**Pasos:**

1. Seleccionar opción `6`
2. Ingresar ID del movimiento
3. Ver datos actuales
4. Ingresar nueva cantidad
5. Ingresar nuevo precio
6. Confirmar actualización

### 7. Eliminar Movimiento

**Propósito:** Eliminar un movimiento del sistema.

**⚠️ ADVERTENCIA:** Esta operación es irreversible.

**Pasos:**

1. Seleccionar opción `7`
2. Ingresar ID del movimiento
3. Confirmar con `S` o cancelar con `N`

```
🆔 Ingrese el ID del movimiento a eliminar: MOV-A1B2C3D4
⚠️  ¿Está seguro? (S/N): S

✅ Movimiento eliminado exitosamente!
```

---

## 🛢️ MÓDULO DE PRODUCTOS

### Sub-menú de Productos

```
╔════════════════════════════════════════════╗
║       🛢️  GESTIÓN DE PRODUCTOS             ║
╚════════════════════════════════════════════╝
  1. ➕ Crear nuevo producto
  2. 📋 Listar todos los productos
  3. 🔍 Buscar producto por nombre
  4. 📏 Buscar producto por unidad de medida
  5. ✏️  Actualizar producto
  6. 🗑️  Eliminar producto
  0. 🔙 Volver al menú principal
════════════════════════════════════════════
```

### 1. Crear Nuevo Producto

**Propósito:** Agregar un nuevo tipo de combustible o producto al catálogo.

**Pasos:**

1. Seleccionar opción `1`

2. **Ingresar datos:**
```
📌 Nombre del producto: Aceite Motor 15W40
📏 Unidad de medida (Litro, Galon, etc.): Litro
💰 Precio por unidad: 15500
```

3. **Confirmación:**
```
✅ Producto creado exitosamente!
   ID: FUEL-I9J0K1L2
   Nombre: Aceite Motor 15W40
   Unidad: Litro
   Precio: $15500.0
```

### 2. Listar Todos los Productos

**Propósito:** Ver el catálogo completo de productos.

**Salida:**
```
═══════════════════════════════════════
    📋 LISTA DE PRODUCTOS
═══════════════════════════════════════

Product{id='FUEL-ABC', name='Diesel Premium',
unidadDeMedida='Galon', priceXUnd=3500.0}

Product{id='FUEL-DEF', name='Gasolina 95',
unidadDeMedida='Litro', priceXUnd=4200.0}

Product{id='FUEL-I9J0K1L2', name='Aceite Motor 15W40',
unidadDeMedida='Litro', priceXUnd=15500.0}
```

### 3. Buscar Producto por Nombre (Fuzzy Search)

**Propósito:** Buscar productos escribiendo solo parte del nombre.

**Ejemplo:**

```
📝 Ingrese el nombre o parte del nombre: diesel

✅ Resultados de búsqueda:

Product{id='FUEL-ABC', name='Diesel Premium', ...}
Product{id='FUEL-GHI', name='Diesel Extra', ...}
Product{id='FUEL-JKL', name='Aceite Diesel Motor', ...}
```

**Características:**
- No distingue mayúsculas/minúsculas
- Busca coincidencias parciales
- Muestra todos los productos que contengan el texto

### 4. Buscar Producto por Unidad de Medida

**Propósito:** Filtrar productos por su unidad de medida.

**Ejemplo:**

```
📏 Ingrese la unidad de medida: Litro

✅ Encontrados: 5 producto(s)

Product{id='...', name='Gasolina 95', unidadDeMedida='Litro', ...}
Product{id='...', name='Aceite Hidráulico', unidadDeMedida='Litro', ...}
```

### 5. Actualizar Producto

**Propósito:** Modificar los datos de un producto existente.

**Pasos:**

1. Ingresar ID del producto
2. Ingresar nuevo nombre
3. Ingresar nueva unidad de medida
4. Ingresar nuevo precio
5. Confirmar actualización

### 6. Eliminar Producto

**Propósito:** Eliminar un producto del catálogo.

**⚠️ IMPORTANTE:** No se puede eliminar un producto si tiene movimientos asociados.

---

## 🚜 MÓDULO DE VEHÍCULOS

### Sub-menú de Vehículos

```
╔════════════════════════════════════════════╗
║       🚜 GESTIÓN DE VEHÍCULOS              ║
╚════════════════════════════════════════════╝
  1. ➕ Crear nuevo vehículo
  2. 📋 Listar todos los vehículos
  3. 🔍 Buscar vehículo por ID
  4. 📦 Filtrar vehículos por categoría
  5. ✏️  Actualizar vehículo
  6. 🗑️  Eliminar vehículo
  0. 🔙 Volver al menú principal
════════════════════════════════════════════
```

### 1. Crear Nuevo Vehículo

**Propósito:** Agregar un vehículo a la flota.

**Pasos:**

```
📌 Nombre/Placa del vehículo: Camión Volvo FH16
📦 Categoría (Camión, Excavadora, etc.): Camión
⛽ Capacidad del tanque (litros): 300
🛢️  ID del producto combustible: FUEL-ABC
⏱️  ¿Tiene horómetro? (S/N): S
```

**Confirmación:**
```
✅ Vehículo creado exitosamente!
   ID: VEH-M1N2O3P4
```

### 2. Listar Todos los Vehículos

**Salida:**
```
┌─────────────────────────────────────────────────────┐
│              📋 DETALLE DEL VEHICULO               │
├────────────────────────────────────────────────────┤
│ 🆔 ID:                VEH-ABC
│ 📌 Nombre:            Camión Volvo FH16
│ ⛽ Combustible ID:     FUEL-ABC
│ 📦 Categoría:         Camión
│ 💾 Capacidad (lts):   300.0
│ ⏱️  Horómetro:         Sí
└─────────────────────────────────────────────────────┘
```

### 3. Buscar Vehículo por ID

**Propósito:** Encontrar un vehículo específico.

```
🆔 Ingrese el ID del vehículo: VEH-ABC
```

### 4. Filtrar Vehículos por Categoría

**Propósito:** Ver solo vehículos de una categoría específica.

**Ejemplo:**

```
📋 Ingrese la categoría (Camión, Excavadora, Motosierra, etc.): Camión

✅ Se encontraron 3 vehículo(s) en categoría: Camión

┌─────────────────────────────────────────────────────┐
│              📋 DETALLE DEL VEHICULO               │
├────────────────────────────────────────────────────┤
│ 🆔 ID:                VEH-ABC
│ 📌 Nombre:            Camión Volvo FH16
│ 📦 Categoría:         Camión
│ 💾 Capacidad (lts):   300.0
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│              📋 DETALLE DEL VEHICULO               │
├────────────────────────────────────────────────────┤
│ 🆔 ID:                VEH-DEF
│ 📌 Nombre:            Camión Mercedes Actros
│ 📦 Categoría:         Camión
│ 💾 Capacidad (lts):   350.0
└─────────────────────────────────────────────────────┘
```

**Categorías comunes:**
- Camión
- Excavadora
- Motosierra
- Tractor
- Grúa
- Cargador

---

## 🏭 MÓDULO DE PROVEEDORES

### Sub-menú de Proveedores

```
╔════════════════════════════════════════════╗
║       🏭 GESTIÓN DE PROVEEDORES            ║
╚════════════════════════════════════════════╝
  1. ➕ Crear nuevo proveedor
  2. 📋 Listar todos los proveedores
  3. 🔍 Buscar proveedor por ID
  4. ✏️  Actualizar proveedor
  5. 🗑️  Eliminar proveedor
  0. 🔙 Volver al menú principal
════════════════════════════════════════════
```

### 1. Crear Nuevo Proveedor

**Propósito:** Agregar un proveedor de combustibles.

**Pasos:**

```
📌 Nombre del proveedor: Distribuidora Petro S.A.
🏢 NIT: 900123456-7
📞 Teléfono: 3001234567
📧 Email: ventas@petro.com
📍 Dirección: Calle 50 #45-30, Medellín
```

**Confirmación:**
```
✅ Proveedor creado exitosamente!
   ID: PROV-Q4R5S6T7
```

### 2. Listar Todos los Proveedores

**Salida:**
```
┌─────────────────────────────────────────────────────┐
│              📋 DETALLE DEL PROVEEDOR              │
├────────────────────────────────────────────────────┤
│ 🆔 ID:        PROV-Q4R5S6T7
│ 📌 Nombre:    Distribuidora Petro S.A.
│ 🏢 NIT:       900123456-7
│ 📞 Teléfono:  3001234567
│ 📧 Email:     ventas@petro.com
│ 📍 Dirección: Calle 50 #45-30, Medellín
└─────────────────────────────────────────────────────┘
```

---

## 📊 MÓDULO DE REPORTES

### Sub-menú de Reportes

```
╔════════════════════════════════════════════╗
║       📊 REPORTES                          ║
╚════════════════════════════════════════════╝
  1. 📦 Stock actual de todos los productos
  2. 📅 Movimientos por rango de fechas
  3. 🚜 Movimientos por vehículo
  4. 📈 Total de movimientos
  5. 📋 Movimientos por tipo (ENTRADA/SALIDA)
  0. 🔙 Volver al menú principal
════════════════════════════════════════════
```

### 1. Stock Actual de Todos los Productos

**Propósito:** Ver el inventario completo con indicadores de estado.

**Salida:**

```
═══════════════════════════════════════════════════════════════════════
                       📦 REPORTE DE STOCK ACTUAL
═══════════════════════════════════════════════════════════════════════

┌─────────────┬─────────────────────────┬─────────────┬──────────────┬───────────┐
│  PRODUCTO   │         NOMBRE          │   UNIDAD    │    PRECIO    │   STOCK   │
│     ID      │                         │   MEDIDA    │   X UNIDAD   │   ACTUAL  │
├─────────────┼─────────────────────────┼─────────────┼──────────────┼───────────┤
│ FUEL-ABC    │ Diesel Premium          │ Galon       │ $3500.00     │ ✅ 450.00 │
│ FUEL-DEF    │ Gasolina 95             │ Litro       │ $4200.00     │ ⚡ 8.50   │
│ FUEL-GHI    │ Aceite Hidráulico       │ Litro       │ $12500.00    │ ⚠️ 0.00   │
└─────────────┴─────────────────────────┴─────────────┴──────────────┴───────────┘

📊 RESUMEN:
   Total de productos: 3
   ✅ Con stock normal: 1
   ⚡ Con stock bajo: 1
   ⚠️  Sin stock: 1
   📦 Stock total acumulado: 458.50 unidades

📌 LEYENDA:
   ✅ Stock normal (>= 10 unidades)
   ⚡ Stock bajo (1-9 unidades)
   ⚠️  Sin stock (0 unidades)
   ❌ Stock negativo (error de datos)
```

**Usos:**
- Control diario de inventario
- Identificar productos que necesitan reabastecimiento
- Detectar productos sin movimiento

### 2. Movimientos por Rango de Fechas

**Propósito:** Ver movimientos en un período específico.

**Pasos:**

```
📅 Fecha inicio (YYYY-MM-DD HH:MM:SS): 2025-11-01 00:00:00
📅 Fecha fin (YYYY-MM-DD HH:MM:SS): 2025-11-30 23:59:59
```

**Salida:**
```
✅ Se encontraron 15 movimientos:

Movement{id='MOV-...', movementType='ENTRADA', ...}
Movement{id='MOV-...', movementType='SALIDA', ...}
...
```

**Usos:**
- Reportes mensuales
- Auditorías de períodos específicos
- Análisis de consumo

### 3. Movimientos por Vehículo

**Propósito:** Ver el historial de consumo de un vehículo.

**Pasos:**

```
🚜 Ingrese ID del vehículo: VEH-ABC
```

**Salida:**
```
✅ Se encontraron 5 movimientos:

Movement{id='MOV-...', vehicleId='VEH-ABC', quantity=50.0, ...}
Movement{id='MOV-...', vehicleId='VEH-ABC', quantity=45.0, ...}
...
```

**Usos:**
- Calcular consumo total de un vehículo
- Identificar vehículos con alto consumo
- Planificación de mantenimiento

### 4. Total de Movimientos

**Propósito:** Conteo rápido de todos los movimientos.

**Salida:**
```
📊 Total de movimientos en el sistema: 127
```

### 5. Movimientos por Tipo (ENTRADA/SALIDA)

**Propósito:** Ver solo entradas o solo salidas.

**Pasos:**

```
Seleccione el tipo:
  1. ENTRADA
  2. SALIDA
👉 Opción: 1
```

**Salida:**
```
✅ Se encontraron 63 movimientos de tipo ENTRADA:

Movement{id='...', movementType='ENTRADA', ...}
...
```

**Usos:**
- Ver todas las compras de combustible
- Ver todos los consumos
- Análisis de flujo de inventario

---

## 💼 CASOS DE USO COMUNES

### Caso 1: Recepción de Combustible del Proveedor

**Escenario:** Llegó un camión con 1000 galones de Diesel Premium.

**Pasos:**

1. Menú Principal → `1` (Gestionar Movimientos)
2. Sub-menú → `1` (Registrar Entrada)
3. Seleccionar producto → `1` (Diesel Premium)
4. Cantidad → `1000`
5. Precio unitario → `3500`
6. ID Proveedor → `PROV-ABC123` (o ENTER para omitir)
7. Unidad de medida → `1` (GALON)
8. Confirmar

**Resultado:** El stock de Diesel Premium aumenta en 1000 galones.

---

### Caso 2: Despacho de Combustible a Vehículo

**Escenario:** El Camión Volvo necesita 80 galones de diesel.

**Pasos:**

1. Menú Principal → `1` (Gestionar Movimientos)
2. Sub-menú → `2` (Registrar Salida)
3. Seleccionar producto → `1` (Diesel Premium)
4. Ver stock disponible → `1000.00 unidades`
5. Cantidad a retirar → `80`
6. Precio unitario → `3500`
7. Seleccionar vehículo → `1` (Camión Volvo FH16)
8. Unidad de medida → `1` (GALON)
9. Confirmar

**Resultado:**
- Stock de Diesel Premium: 1000 - 80 = 920 galones
- Se registra el consumo del vehículo

---

### Caso 3: Verificar Stock Antes de Planificar Compra

**Escenario:** Necesitas saber qué productos necesitan reabastecimiento.

**Pasos:**

1. Menú Principal → `5` (Reportes)
2. Sub-menú → `1` (Stock actual de todos los productos)
3. Revisar la tabla
4. Identificar productos con indicador ⚡ (stock bajo) o ⚠️ (sin stock)
5. Planificar compra de esos productos

---

### Caso 4: Auditoría Mensual de Consumo por Vehículo

**Escenario:** Necesitas saber cuánto combustible consumió cada vehículo en noviembre.

**Pasos:**

1. Menú Principal → `5` (Reportes)
2. Sub-menú → `3` (Movimientos por vehículo)
3. Ingresar ID del vehículo → `VEH-ABC`
4. Revisar todos los movimientos
5. Sumar las cantidades manualmente o exportar datos
6. Repetir para cada vehículo

---

### Caso 5: Buscar un Producto sin Recordar el Nombre Exacto

**Escenario:** Sabes que el producto tiene "aceite" en el nombre pero no recuerdas el nombre completo.

**Pasos:**

1. Menú Principal → `2` (Gestionar Productos)
2. Sub-menú → `3` (Buscar producto por nombre)
3. Ingresar → `aceite`
4. Ver todos los productos que contengan "aceite"
5. Identificar el correcto

---

### Caso 6: Ver Todos los Camiones de la Flota

**Escenario:** Necesitas una lista solo de camiones, excluyendo excavadoras y otros.

**Pasos:**

1. Menú Principal → `3` (Gestionar Vehículos)
2. Sub-menú → `4` (Filtrar vehículos por categoría)
3. Ingresar categoría → `Camión`
4. Ver solo los camiones

---

## 🔧 SOLUCIÓN DE PROBLEMAS

### Problema 1: "No se pudo conectar a la base de datos"

**Síntomas:**
```
❌ ERROR CRÍTICO: No se pudo conectar a la base de datos
```

**Soluciones:**

1. **Verificar que MySQL está corriendo:**
```bash
sudo service mysql status
```

2. **Iniciar MySQL si está detenido:**
```bash
sudo service mysql start
```

3. **Verificar credenciales:**
- Revisar `DatabaseConnection.java`
- Usuario por defecto: `root`
- Contraseña: la configurada en tu instalación

4. **Verificar que la base de datos existe:**
```sql
mysql -u root -p
SHOW DATABASES;
```

---

### Problema 2: "Stock insuficiente"

**Síntomas:**
```
❌ STOCK INSUFICIENTE
   Producto: FUEL-ABC
   Stock disponible: 10.00
   Cantidad solicitada: 50.00
   Faltante: 40.00
```

**Soluciones:**

1. **Registrar una entrada** del producto antes de intentar la salida
2. **Verificar el stock actual:**
   - Ir a Reportes → Stock actual
   - O usar Movimientos → Calcular stock del producto

---

### Problema 3: "No se encontraron productos/vehículos"

**Síntomas:**
```
⚠️  No hay productos registrados. Cree uno primero.
```

**Soluciones:**

1. **Crear al menos un producto o vehículo** antes de registrar movimientos
2. **Verificar que los datos se cargaron en la BD:**
```sql
SELECT * FROM oil_products;
SELECT * FROM vehicles;
```

---

### Problema 4: Error al eliminar producto/vehículo

**Síntomas:**
```
❌ Error: No se puede eliminar: el producto tiene movimientos asociados
```

**Causa:** Hay foreign keys que protegen la integridad de los datos.

**Soluciones:**

1. **Primero eliminar los movimientos** asociados al producto/vehículo
2. **O simplemente no eliminar** - mantener el registro para historial

---

### Problema 5: "java: command not found" al ejecutar

**Causa:** Java no está instalado o no está en el PATH.

**Solución:**

```bash
# Instalar Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# Verificar instalación
java -version
```

---

### Problema 6: Compilación falla con Maven

**Síntomas:**
```
[ERROR] Failed to execute goal
```

**Soluciones:**

1. **Limpiar el proyecto:**
```bash
mvn clean
```

2. **Verificar que Maven está instalado:**
```bash
mvn -version
```

3. **Reinstalar dependencias:**
```bash
mvn clean install
```

---

## ❓ PREGUNTAS FRECUENTES

### ¿Cómo salir de la aplicación?

Selecciona `0` en el menú principal y presiona ENTER.

---

### ¿Se pueden deshacer las operaciones?

No, las operaciones son irreversibles. Por eso se pide confirmación antes de eliminar.

**Recomendación:** Hacer backups periódicos de la base de datos.

```bash
mysqldump -u root -p FORESTECHOIL > backup_$(date +%Y%m%d).sql
```

---

### ¿Cómo hacer un backup de los datos?

```bash
# Backup completo
mysqldump -u root -p FORESTECHOIL > backup_forestech.sql

# Restaurar backup
mysql -u root -p FORESTECHOIL < backup_forestech.sql
```

---

### ¿Puedo usar la aplicación en Windows?

Sí, pero necesitas:
1. Java 17 instalado
2. Maven instalado
3. MySQL corriendo en Windows

Los comandos son similares, solo cambia `sudo service` por `net start mysql` en Windows.

---

### ¿Cuántos usuarios pueden usar la aplicación simultáneamente?

Actualmente es una aplicación **single-user** (un usuario a la vez). Para multi-usuario necesitarías:
- Implementar autenticación
- Gestión de sesiones
- Control de concurrencia

---

### ¿Se pueden exportar los reportes a Excel o PDF?

En la versión actual no. Puedes:
1. Copiar y pegar la salida en un archivo de texto
2. Implementar exportación como mejora futura

---

### ¿Cómo agregar nuevos tipos de unidades de medida?

Actualmente las unidades están hardcodeadas:
- GALON
- GARRAFA
- CUARTO
- CANECA

Para agregar más, editar `AppController.java` en los métodos `registrarEntrada()` y `registrarSalida()`.

---

### ¿Qué significa el formato de IDs como "MOV-XXXXXXXX"?

- **MOV:** Prefijo que indica que es un Movement (movimiento)
- **XXXXXXXX:** 8 caracteres aleatorios generados por UUID

Otros prefijos:
- **FUEL:** Productos (fuel)
- **VEH:** Vehículos
- **PROV:** Proveedores

---

### ¿Cómo ver la versión de la aplicación?

En la pantalla de bienvenida al iniciar, o en:
- `AppConfig.java` → `VERSION`

---

### ¿Dónde reportar bugs o sugerir mejoras?

Contactar al equipo de desarrollo de Forestech o crear un issue en el repositorio del proyecto.

---

## 📚 APÉNDICES

### Apéndice A: Códigos de Estado

- ✅ Operación exitosa
- ❌ Error
- ⚠️ Advertencia
- 📦 Stock normal (>= 10)
- ⚡ Stock bajo (1-9)
- ⚠️ Stock vacío (0)
- ❌ Stock negativo (error)

---

### Apéndice B: Comandos Útiles

**Compilar:**
```bash
mvn clean compile
```

**Ejecutar:**
```bash
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

**Generar JAR:**
```bash
mvn clean package
```

**Ver logs de MySQL:**
```bash
sudo tail -f /var/log/mysql/error.log
```

**Conectar a MySQL:**
```bash
mysql -u root -p FORESTECHOIL
```

---

### Apéndice C: Estructura de la Base de Datos

**Tabla Movement:**
- id (PK)
- movementType (ENTRADA/SALIDA)
- product_id (FK → oil_products)
- vehicle_id (FK → vehicles)
- numero_factura (FK → facturas)
- unidadDeMedida
- quantity
- unitPrice
- movementDate

**Tabla oil_products:**
- id (PK)
- name
- unidadDeMedida
- priceXUnd

**Tabla vehicles:**
- id (PK)
- name
- category
- capacity
- fuel_product_id (FK → oil_products)
- haveHorometer

**Tabla suppliers:**
- id (PK)
- name
- nit
- telephone
- email
- address

---

## 📞 CONTACTO Y SOPORTE

**Desarrollado por:** Forestech Development Team
**Versión de esta guía:** 3.0
**Última actualización:** Noviembre 2025

**Soporte técnico:**
Para asistencia técnica, consultar con el administrador del sistema o el equipo de desarrollo.

---

## 📝 NOTAS FINALES

Esta guía cubre todas las funcionalidades actuales de Forestech CLI v3.0. Para futuras actualizaciones, consultar el changelog del proyecto.

**¡Gracias por usar Forestech CLI!** 🌲
