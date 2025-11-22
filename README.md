# Forestech CLI - Sistema de Gestión de Combustibles

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.x-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![JUnit](https://img.shields.io/badge/JUnit-5.10.0-green)
![Mockito](https://img.shields.io/badge/Mockito-5.5.0-green)
![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-83%2F83-brightgreen)

## 📋 Descripción

**Forestech CLI** es una aplicación Java de escritorio para gestionar inventarios de combustible, flota de vehículos, proveedores y movimientos (ENTRADA/SALIDA) en empresas forestales.

### Características Principales

✅ **Gestión de Productos** - Catálogo de combustibles (Diesel, Gasolina, Aceite, etc.)
✅ **Gestión de Vehículos** - Flota vehicular (camiones, excavadoras, motosierras)
✅ **Movimientos de Combustible** - Registro de ENTRADAS/SALIDAS con validación de stock
✅ **Facturas de Compra** - Gestión de facturas con detalles (transacciones ACID)
✅ **Proveedores** - Catálogo de proveedores de combustible
✅ **Dashboard** - Resumen ejecutivo con métricas clave
✅ **Reportes** - Exportación a TXT/CSV de movimientos por rango de fechas

---

## 🏗️ Arquitectura del Proyecto

### Capas de la Aplicación

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Swing Panels │  │   Dialogs    │  │ Controllers  │     │
│  │  (6 panels)  │  │  (3 forms)   │  │ (5 classes)  │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │             │
│         └──────────────────┴──────────────────┘             │
│                            ↓                                │
│              Dependency Injection via Constructor           │
│                            ↓                                │
├─────────────────────────────────────────────────────────────┤
│                      SERVICE LAYER                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │             ServiceFactory (Singleton)               │  │
│  └──────────────────────────────────────────────────────┘  │
│         ↓            ↓           ↓           ↓              │
│  ┌───────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │
│  │ Movement  │ │ Product  │ │ Vehicle  │ │ Supplier │    │
│  │ Services  │ │ Services │ │ Services │ │ Services │    │
│  └─────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘    │
│        │            │            │            │            │
│        └────────────┴────────────┴────────────┘            │
│                            ↓                                │
│              Business Logic & Validations                   │
│              (FK validation, stock checks)                  │
│                            ↓                                │
├─────────────────────────────────────────────────────────────┤
│                       DAO LAYER                             │
│  ┌───────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │
│  │ Movement  │ │ Product  │ │ Vehicle  │ │ Supplier │    │
│  │   DAO     │ │   DAO    │ │   DAO    │ │   DAO    │    │
│  └─────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘    │
│        │            │            │            │            │
│        └────────────┴────────────┴────────────┘            │
│                            ↓                                │
│                  JDBC + PreparedStatements                  │
│                            ↓                                │
├─────────────────────────────────────────────────────────────┤
│                      DATA LAYER                             │
│              MySQL Database (FORESTECHOIL)                  │
│   Tables: oil_products, vehicles, Movement, facturas,       │
│           suppliers, detalle_factura                        │
└─────────────────────────────────────────────────────────────┘
```

### Patrones de Diseño Implementados

#### 1. **Singleton Pattern**
Todos los Services son Singleton (lazy initialization, thread-safe):

```java
public class ProductServices implements IProductService {
    private static ProductServices instance;

    private ProductServices() {
        this.productDAO = new ProductDAO();
    }

    public static synchronized ProductServices getInstance() {
        if (instance == null) {
            instance = new ProductServices();
        }
        return instance;
    }
}
```

#### 2. **Dependency Injection (Constructor Injection)**
Panels, Controllers y Dialogs reciben Services como parámetros del constructor:

```java
public class MovementsPanel extends JPanel {
    private final MovementServices movementServices;
    private final ProductServices productServices;

    public MovementsPanel(JFrame owner,
                          MovementServices movementServices,
                          ProductServices productServices,
                          VehicleServices vehicleServices,
                          FacturaServices facturaServices) {
        this.movementServices = movementServices;
        this.productServices = productServices;
    }
}
```

#### 3. **Factory Pattern**
`ServiceFactory` centraliza la obtención de Services:

```java
ServiceFactory factory = ServiceFactory.getInstance();
MovementsPanel panel = new MovementsPanel(
    owner,
    factory.getMovementServices(),
    factory.getProductServices(),
    factory.getVehicleServices(),
    factory.getFacturaServices()
);
```

#### 4. **DAO Pattern**
Separación de lógica de negocio (Services) y acceso a datos (DAOs):

```java
ProductServices → ProductDAO → MySQL
```

#### 5. **MVC Pattern**
- **Model:** Entities (`Product`, `Vehicle`, `Movement`, etc.)
- **View:** Swing Panels y Dialogs
- **Controller:** Controllers + Services (lógica de negocio)

---

## 📦 Estructura del Proyecto

```
forestech-cli-java/
├── src/
│   ├── main/
│   │   ├── java/com/forestech/
│   │   │   ├── config/
│   │   │   │   └── DatabaseConnectionFactory.java
│   │   │   ├── controllers/           # Capa de control (CLI)
│   │   │   │   ├── MovementController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── VehicleController.java
│   │   │   │   ├── SupplierController.java
│   │   │   │   └── ReportController.java
│   │   │   ├── dao/                   # Data Access Objects
│   │   │   │   ├── MovementDAO.java
│   │   │   │   ├── ProductDAO.java
│   │   │   │   ├── VehicleDAO.java
│   │   │   │   └── SupplierDAO.java
│   │   │   ├── enums/                 # Enumeraciones
│   │   │   │   ├── MeasurementUnit.java
│   │   │   │   ├── MovementType.java
│   │   │   │   └── VehicleCategory.java
│   │   │   ├── exceptions/            # Excepciones personalizadas
│   │   │   │   ├── DatabaseException.java
│   │   │   │   └── InsufficientStockException.java
│   │   │   ├── models/                # Entidades de dominio
│   │   │   │   ├── Movement.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Vehicle.java
│   │   │   │   ├── Supplier.java
│   │   │   │   └── Factura.java
│   │   │   ├── services/              # Lógica de negocio
│   │   │   │   ├── interfaces/        # Contratos (DIP)
│   │   │   │   │   ├── IMovementService.java
│   │   │   │   │   ├── IProductService.java
│   │   │   │   │   ├── IVehicleService.java
│   │   │   │   │   ├── ISupplierService.java
│   │   │   │   │   └── IFacturaService.java
│   │   │   │   ├── MovementServices.java (Singleton)
│   │   │   │   ├── ProductServices.java (Singleton)
│   │   │   │   ├── VehicleServices.java (Singleton)
│   │   │   │   ├── SupplierServices.java (Singleton)
│   │   │   │   ├── FacturaServices.java (Singleton)
│   │   │   │   └── ServiceFactory.java (Singleton + Factory)
│   │   │   ├── ui/                    # Interfaz gráfica Swing
│   │   │   │   ├── movements/
│   │   │   │   │   ├── MovementsPanel.java (DI)
│   │   │   │   │   └── MovementsDataLoader.java
│   │   │   │   ├── products/
│   │   │   │   │   └── ProductsPanel.java (DI)
│   │   │   │   ├── vehicles/
│   │   │   │   │   └── VehiclesPanel.java (DI)
│   │   │   │   ├── suppliers/
│   │   │   │   │   └── SuppliersPanel.java (DI)
│   │   │   │   ├── invoices/
│   │   │   │   │   └── InvoicesPanel.java (DI)
│   │   │   │   ├── dashboard/
│   │   │   │   │   └── DashboardPanel.java (DI)
│   │   │   │   ├── ProductDialogForm.java (DI)
│   │   │   │   ├── VehicleDialogForm.java (DI)
│   │   │   │   ├── MovementDialogForm.java (DI)
│   │   │   │   └── ForestechProfessionalApp.java
│   │   │   ├── utils/
│   │   │   │   └── IdGenerator.java
│   │   │   ├── validators/
│   │   │   │   ├── ProductValidator.java
│   │   │   │   └── VehicleValidator.java
│   │   │   ├── AppController.java     # Entry point CLI
│   │   │   └── Main.java              # Entry point GUI
│   │   └── resources/
│   │       ├── config.properties
│   │       └── logback.xml
│   └── test/
│       └── java/com/forestech/
│           └── services/              # Tests unitarios (JUnit 5 + Mockito)
│               ├── ProductServicesTest.java (17 tests)
│               ├── VehicleServicesTest.java (19 tests)
│               ├── MovementServicesTest.java (21 tests)
│               ├── SupplierServicesTest.java (15 tests)
│               └── FacturaServicesTest.java (11 tests)
├── pom.xml
└── README.md
```

---

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** (LTS)
- **Maven 3.x** - Build automation
- **MySQL 8.0** - Base de datos relacional
- **JDBC** - Conectividad con BD
- **SLF4J + Logback** - Logging

### Frontend
- **Swing** - GUI de escritorio
- **GridBagLayout** - Layout manager

### Testing
- **JUnit 5** (Jupiter) - Framework de testing
- **Mockito 5.5.0** - Mocking framework
- **Reflection API** - Para inyectar mocks en Singletons

### Dependencias Maven
```xml
<dependencies>
    <!-- MySQL Connector -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 💾 Base de Datos

### Esquema: FORESTECHOIL

**Tablas:**
1. **oil_products** - Catálogo de combustibles
2. **vehicles** - Flota de vehículos
3. **suppliers** - Proveedores
4. **facturas** - Facturas de compra
5. **Movement** - Movimientos de combustible (ENTR ADA/SALIDA)
6. **detalle_factura** - Detalles de facturas

### Relaciones Clave

```sql
-- Foreign Keys
Movement.product_id → oil_products.id (RESTRICT)
Movement.vehicle_id → vehicles.id (SET NULL)
Movement.numero_factura → facturas.numero_factura (SET NULL)
vehicles.fuel_product_id → oil_products.id (SET NULL)
facturas.supplier_id → suppliers.id (RESTRICT)
detalle_factura.numero_factura → facturas.numero_factura (CASCADE)
```

**Ver esquema completo:** `docs/ARQUITECTURA_UML.md`

---

## 🚀 Instalación y Uso

### Prerrequisitos

- Java 17+ (LTS)
- Maven 3.6+
- MySQL 8.0+
- IDE recomendado: IntelliJ IDEA / Eclipse

### 1. Clonar el repositorio

```bash
git clone https://github.com/evertweb/programajava.git
cd programajava/forestech-cli-java
```

### 2. Configurar Base de Datos

```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE FORESTECHOIL;
exit;

# Importar esquema (si tienes script SQL)
mysql -u root -p FORESTECHOIL < schema.sql
```

### 3. Configurar credenciales

Editar `src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/FORESTECHOIL
db.user=root
db.password=tu_password
```

### 4. Compilar el proyecto

```bash
mvn clean compile
```

### 5. Ejecutar tests

```bash
mvn test
```

**Resultado esperado:**
```
Tests run: 83, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 6. Ejecutar la aplicación

**GUI (Swing):**
```bash
mvn exec:java -Dexec.mainClass="com.forestech.Main"
```

**CLI (Consola):**
```bash
mvn exec:java -Dexec.mainClass="com.forestech.AppController"
```

---

## 🧪 Testing

### Suite de Tests

El proyecto incluye **83 tests unitarios** con **~78% de cobertura** de métodos públicos.

**Ejecutar todos los tests:**
```bash
mvn test
```

**Ejecutar un Service específico:**
```bash
mvn test -Dtest=MovementServicesTest
```

**Ejecutar un test específico:**
```bash
mvn test -Dtest=MovementServicesTest#shouldValidateStock_beforeInsertingSalida
```

### Estructura de Tests

- **Mockito** para mockear DAOs
- **Reflection** para inyectar mocks en Singletons
- **Patrón AAA** (Arrange-Act-Assert)
- **Validaciones de negocio:** FK, stock, excepciones

---

## 📊 Principios SOLID Aplicados

✅ **Single Responsibility Principle (SRP)**
✅ **Open/Closed Principle (OCP)**
✅ **Liskov Substitution Principle (LSP)**
✅ **Interface Segregation Principle (ISP)**
✅ **Dependency Inversion Principle (DIP)**

---

## 📈 Métricas del Proyecto

| Métrica | Valor |
|---------|-------|
| Líneas de código (src) | ~12,000 |
| Líneas de tests | ~3,500 |
| Clases | 84 |
| Tests unitarios | 83 |
| Cobertura de código | ~78% |
| Warnings de compilación | 13 (deprecated legacy) |
| Build status | ✅ SUCCESS |

---

## 🎯 Roadmap Completado

- ✅ **Fase 0-1:** Setup y fundamentos Java
- ✅ **Fase 2-2.5:** POO y Manager pattern
- ✅ **Fase 3:** Conexión MySQL/JDBC
- ✅ **Fase 4:** CRUD operations (DAO pattern)
- ✅ **Fase 5:** Lógica de negocio (Services)
- ✅ **Fase 6:** CLI interactiva
- ✅ **Fase 7:** Exception handling
- ✅ **Fase 8:** Streams y Lambdas
- ✅ **Fase 9:** Swing GUI (12 checkpoints)
- ✅ **Refactorización Mayor:**
  - Singleton Pattern en Services
  - Dependency Injection en UI/Controllers
  - 83 tests unitarios (JUnit 5 + Mockito)
  - Naming conventions estandarizadas
  - Eliminación de métodos deprecated

---

## 📝 Convenciones del Código

### Naming
- **Clases:** PascalCase (`MovementServices`)
- **Métodos:** camelCase (`getAllProducts()`)
- **Constantes:** UPPER_SNAKE_CASE (`MAX_CAPACITY`)
- **Packages:** lowercase (`com.forestech.services`)

### Comentarios
- **JavaDoc:** En todos los métodos públicos
- **Comentarios inline:** Solo para lógica compleja
- **Idioma:** Español para aprendizaje, inglés para código profesional

### Logs
- **SLF4J:** Niveles DEBUG, INFO, WARN, ERROR
- **Configuración:** `src/main/resources/logback.xml`

---

## 🤝 Contribuciones

Este es un proyecto educativo. Para contribuir:

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Add: nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

---

## 📄 Licencia

Este proyecto es de código abierto bajo licencia MIT.

---

## 👨‍💻 Autor

**Forestech Team**
Proyecto educativo para aprendizaje de Java desde cero hasta conceptos avanzados.

---

## 📞 Soporte

Para dudas o sugerencias:
- Abrir un [Issue](https://github.com/evertweb/programajava/issues)
- Email: soporte@forestech-cli.com

---

**⭐ Si este proyecto te ayudó, considera darle una estrella en GitHub!**
