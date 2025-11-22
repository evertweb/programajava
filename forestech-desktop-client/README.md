# 🚀 Forestech CLI - Sistema de Gestión de Combustibles en Java

> **Proyecto de Aprendizaje:** Sistema de línea de comandos (CLI) para gestionar movimientos de combustible, vehículos, proveedores e inventario, conectado directamente a SQL Server sin necesidad de Firebase.

---

## 📋 **DESCRIPCIÓN DEL PROYECTO**

Este es un **proyecto de aprendizaje de Java desde cero**, donde construiremos una aplicación real y útil para Forestech. La aplicación replicará la funcionalidad de la app web React de combustibles, pero con arquitectura más simple:

```
Java CLI ← JDBC → SQL Server (DigitalOcean)
```

**Sin intermediarios (Firebase, Cloud Run, etc.)**

---

## 🎯 **OBJETIVOS**

### **Objetivo de Aprendizaje:**
- Aprender Java desde fundamentos hasta conceptos avanzados
- Entender Programación Orientada a Objetos (POO)
- Dominar conexión JDBC a SQL Server
- Implementar lógica de negocio compleja
- Crear aplicación CLI profesional

### **Objetivo Funcional:**
- Gestionar movimientos tipo ENTRADA (compras de combustible)
- Gestionar movimientos tipo SALIDA (asignación a vehículos)
- Consultar inventario en tiempo real
- Administrar vehículos y proveedores
- Generar reportes básicos

---

## 🏗️ **ARQUITECTURA**

```
forestech-cli-java/
├── src/
│   ├── main/
│   │   ├── java/                      # Código fuente Java
│   │   │   ├── Main.java              # Punto de entrada
│   │   │   ├── config/
│   │   │   │   └── DatabaseConnection.java
│   │   │   ├── models/                # Clases del dominio
│   │   │   │   ├── Movement.java
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Supplier.java
│   │   │   │   └── Product.java
│   │   │   ├── services/              # Lógica de negocio
│   │   │   │   ├── MovementService.java
│   │   │   │   ├── VehicleService.java
│   │   │   │   ├── SupplierService.java
│   │   │   │   └── InventoryService.java
│   │   │   ├── ui/                    # Interfaz de usuario
│   │   │   │   └── ConsoleMenu.java
│   │   │   └── exceptions/            # Excepciones custom
│   │   │       └── InsufficientInventoryException.java
│   │   └── resources/                 # Configuración
│   │       └── config.properties
│   └── test/
│       └── java/                      # Tests (futuro)
├── pom.xml                            # Maven configuration
└── README.md                          # Este archivo
```

---

## 🔧 **TECNOLOGÍAS**

- **Java:** 17 o 21 (LTS)
- **Maven:** Gestor de dependencias
- **JDBC:** Java Database Connectivity
- **SQL Server:** Base de datos (DigitalOcean)
- **IntelliJ IDEA / VS Code:** IDEs

---

## 🗄️ **BASE DE DATOS**

**Servidor SQL Server:**
- **Host:** 24.199.89.134
- **Puerto:** 1433
- **Base de datos:** DBforestech
- **Usuario:** SA
- **Password:** [Configurado en código]

**Tablas Principales:**
- `combustibles_movements` - Movimientos ENTRADA/SALIDA
- `combustibles_inventory` - Inventario actual
- `combustibles_vehicles` - Vehículos de la flota
- `combustibles_suppliers` - Proveedores de combustible
- `combustibles_products` - Catálogo de productos

---

## 📦 **INSTALACIÓN Y SETUP**

### **Prerrequisitos:**
```bash
# Verificar Java
java -version  # Debe mostrar 17 o 21

# Verificar Maven
mvn -version   # Debe mostrar 3.8+

# Verificar Git
git --version
```

### **Clonar Repositorio:**
```bash
cd /home/hp/Documents/forestech
# El proyecto ya está creado en: forestech-cli-java/
```

### **Compilar Proyecto:**
```bash
cd forestech-cli-java
mvn clean compile
```

### **Ejecutar Proyecto:**
```bash
mvn exec:java -Dexec.mainClass="Main"
```

---

## 🚀 **USO**

### **Menú Principal:**
```
╔════════════════════════════════════════════════════╗
║     FORESTECH - Sistema de Gestión Combustibles   ║
╚════════════════════════════════════════════════════╝

📋 MENÚ PRINCIPAL:
1. Movimientos (ENTRADA/SALIDA)
2. Consultar Inventario
3. Gestionar Vehículos
4. Gestionar Proveedores
0. Salir

Selecciona opción:
```

### **Crear Movimiento ENTRADA:**
1. Seleccionar proveedor
2. Seleccionar tipo de combustible
3. Ingresar cantidad
4. Ingresar precio unitario
5. Seleccionar ubicación destino
6. Confirmar

**Resultado:**
- ✅ Movimiento registrado en BD
- ✅ Inventario actualizado automáticamente

### **Crear Movimiento SALIDA:**
1. Seleccionar vehículo
2. Seleccionar combustible
3. Ingresar cantidad
4. Ingresar lectura horómetro
5. Confirmar

**Resultado:**
- ✅ Movimiento registrado
- ✅ Inventario descontado
- ✅ Horómetro actualizado

---

## 📚 **ROADMAP DE DESARROLLO**

**Fase 0:** Preparación (Setup de herramientas)  
**Fase 1:** Fundamentos de Java (Variables, loops, clases básicas)  
**Fase 2:** POO (Clases del dominio)  
**Fase 3:** Conexión SQL Server (JDBC)  
**Fase 4:** CRUD (Create, Read, Update, Delete)  
**Fase 5:** Lógica de Negocio (Transacciones, validaciones)  
**Fase 6:** Interfaz CLI (Menús interactivos)  
**Fase 7:** Manejo de Errores (Excepciones)  
**Fase 8:** Conceptos Avanzados (Streams, Lambdas)  
**Fase 9:** Features Adicionales (Reportes, búsquedas)  
**Fase 10:** Optimización (Connection pooling, JAR ejecutable)

**Ver roadmap completo en:** `JAVA_LEARNING_ROADMAP.md`

---

## 📖 **DOCUMENTACIÓN**

- **`JAVA_LEARNING_ROADMAP.md`** - Ruta de aprendizaje completa
- **`JAVA_PROJECT_SETUP.md`** - Guía de instalación detallada
- **`JAVA_AGENTS_INSTRUCTIONS.md`** - Guía para agentes IA
- **`JAVA_LEARNING_LOG.md`** - Registro de progreso
- **`JAVA_NEXT_STEPS.md`** - Siguiente tarea específica

---

## 🎓 **CONCEPTOS JAVA A APRENDER**

### **Fundamentos:**
- Variables y tipos de datos
- Operadores y expresiones
- Control de flujo (if, switch)
- Bucles (for, while)
- Arrays y colecciones

### **POO:**
- Clases y objetos
- Encapsulamiento
- Herencia
- Polimorfismo
- Interfaces

### **JDBC:**
- Connection
- Statement vs PreparedStatement
- ResultSet
- Transacciones

### **Avanzado:**
- Excepciones personalizadas
- Streams API
- Lambda expressions
- Date/Time API
- Enums

---

## 🔐 **SEGURIDAD**

**Credenciales de Base de Datos:**
- ⚠️ NO commitear credenciales en Git
- ✅ Usar archivo `config.properties` (en `.gitignore`)
- ✅ Variables de entorno para producción

**SQL Injection:**
- ✅ SIEMPRE usar `PreparedStatement`
- ❌ NUNCA concatenar SQL directamente

---

## 🧪 **TESTING (Futuro)**

```bash
# Ejecutar tests
mvn test

# Coverage report
mvn jacoco:report
```

---

## 🚢 **DEPLOYMENT**

### **Generar JAR Ejecutable:**
```bash
mvn clean package
```

### **Ejecutar JAR:**
```bash
java -jar target/forestech-cli-1.0-jar-with-dependencies.jar
```

---

## 🤝 **CONTRIBUCIÓN**

Este es un proyecto de aprendizaje personal. No se aceptan contribuciones externas por ahora.

---

## 📄 **LICENCIA**

Proyecto privado de Forestech de Colombia.

---

## 🆘 **SOPORTE**

Si tienes dudas durante el aprendizaje:
1. Consulta `JAVA_NEXT_STEPS.md` para tu siguiente tarea
2. Revisa `JAVA_LEARNING_ROADMAP.md` para entender dónde estás
3. Lee `JAVA_AGENTS_INSTRUCTIONS.md` para guía de IAs
4. Pregunta a tu agente IA (GitHub Copilot, etc.)

---

## 🎯 **ESTADO ACTUAL**

**Fase:** 0 - Preparación  
**Progreso:** 0%  
**Próximo Paso:** Verificar instalación de JDK y Maven

**Ver detalles en:** `JAVA_NEXT_STEPS.md`

---

## 🏆 **HITOS A ALCANZAR**

- [ ] **Hito 1:** "Hello World" ejecutado exitosamente
- [ ] **Hito 2:** Primera clase Java creada (Movement)
- [ ] **Hito 3:** Conexión exitosa a SQL Server
- [ ] **Hito 4:** Primer INSERT en base de datos
- [ ] **Hito 5:** CRUD completo funcionando
- [ ] **Hito 6:** Menú CLI interactivo
- [ ] **Hito 7:** Lógica de negocio completa (ENTRADA/SALIDA)
- [ ] **Hito 8:** Aplicación CLI profesional terminada

---

## 📞 **CONTACTO**

**Proyecto:** Forestech CLI Java  
**Repositorio:** forestech (monorepo)  
**Fecha de Inicio:** 7 de octubre de 2025  
**Desarrollador:** [Tu Nombre]

---

**¡Empecemos esta aventura de aprendizaje Java! 🚀**

---

## 🔗 **ENLACES ÚTILES**

- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [SQL Server JDBC Driver](https://learn.microsoft.com/en-us/sql/connect/jdbc/)

---

**Última actualización:** 7 de octubre de 2025
