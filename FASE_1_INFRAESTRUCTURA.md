# 🏗️ FASE 1: INFRAESTRUCTURA BASE
> **Preparación del entorno Docker y servicios de soporte**

---

## 📋 Información de la Fase

| Atributo | Valor |
|----------|-------|
| **Fase** | 1 de 5 |
| **Nombre** | Infraestructura Base |
| **Duración Estimada** | 8-12 horas |
| **Complejidad** | ⭐⭐ (Intermedio) |
| **Sesiones** | 2 (1.1 y 1.2) |
| **Dependencias** | Ninguna (primera fase) |
| **Desbloquea** | Fase 2 - Primeros Microservicios |

---

## 🎯 Objetivos de la Fase

Al completar esta fase, tendrás:

- [x] Estructura de directorios completa para el proyecto de microservicios
- [x] Docker Compose configurado con red dedicada
- [x] Consul (Service Registry) funcionando
- [x] Config Server funcionando
- [x] 5 bases de datos MySQL independientes creadas y accesibles
- [x] Scripts de utilidad creados (start, stop, health-check)
- [x] Sistema base verificado y documentado

---

## ✅ Pre-requisitos

Verifica que tienes instalado:

```bash
# Docker
docker --version
# Debe mostrar: Docker version 20.10+ o superior

# Docker Compose
docker-compose --version
# Debe mostrar: Docker Compose version 2.x o superior

# Maven (para futuras fases)
mvn --version
# Debe mostrar: Apache Maven 3.8+ y Java 17

# Git
git --version

# jq (para parsear JSON en scripts)
jq --version
```

Si falta alguno, **DETÉN LA EJECUCIÓN** y reporta en el documento final.

---

## 📂 Estructura de Directorios a Crear

```
forestech-microservices/
├── docker-compose.yml
├── .env
├── .gitignore
├── README.md
├── services/
│   ├── catalog-service/
│   ├── fleet-service/
│   ├── inventory-service/
│   ├── partners-service/
│   ├── invoicing-service/
│   ├── reports-service/
│   └── api-gateway/
├── infrastructure/
│   ├── config-server/
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/forestech/config/
│   │   │       │       └── ConfigServerApplication.java
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── pom.xml
│   │   └── Dockerfile
│   ├── service-registry/
│   └── databases/
│       ├── catalog/
│       │   └── init.sql
│       ├── fleet/
│       │   └── init.sql
│       ├── inventory/
│       │   └── init.sql
│       ├── partners/
│       │   └── init.sql
│       └── invoicing/
│           └── init.sql
├── shared-libs/
├── scripts/
│   ├── start-all.sh
│   ├── stop-all.sh
│   ├── health-check.sh
│   └── setup-environment.sh
├── docs/
└── reportes/
```

---

## 🔨 SESIÓN 1.1: Setup Docker y Configuración Base

**Duración Estimada:** 4-6 horas

### Tarea 1.1.1: Crear Estructura de Directorios

```bash
# Crear directorio raíz del proyecto
mkdir -p ~/forestech-microservices
cd ~/forestech-microservices

# Crear estructura completa
mkdir -p services/{catalog-service,fleet-service,inventory-service,partners-service,invoicing-service,reports-service,api-gateway}
mkdir -p infrastructure/{config-server,service-registry,databases}
mkdir -p infrastructure/databases/{catalog,fleet,inventory,partners,invoicing}
mkdir -p shared-libs
mkdir -p scripts
mkdir -p docs
mkdir -p reportes

# Verificar estructura
tree -L 2
```

**Criterio de éxito:**
```bash
# Debe mostrar la estructura completa
ls -la
# Deben existir: services/, infrastructure/, shared-libs/, scripts/, docs/, reportes/
```

---

### Tarea 1.1.2: Crear Archivo `.gitignore`

```bash
cat > .gitignore << 'EOF'
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml

# Docker
.dockerignore

# IDE
.idea/
.vscode/
*.iml
*.ipr
*.iws

# Logs
logs/
*.log

# Environment
.env.local
.env.*.local

# OS
.DS_Store
Thumbs.db

# Backups
*.bak
*.backup
EOF
```

---

### Tarea 1.1.3: Crear Archivo de Variables de Entorno `.env`

```bash
cat > .env << 'EOF'
# MySQL Configuration
MYSQL_ROOT_PASSWORD=forestech_root_2024
MYSQL_VERSION=8.0

# Database Names
CATALOG_DB_NAME=catalog_db
FLEET_DB_NAME=fleet_db
INVENTORY_DB_NAME=inventory_db
PARTNERS_DB_NAME=partners_db
INVOICING_DB_NAME=invoicing_db

# Ports
CONSUL_PORT=8500
CONFIG_SERVER_PORT=8888
CATALOG_SERVICE_PORT=8081
FLEET_SERVICE_PORT=8082
INVENTORY_SERVICE_PORT=8083
PARTNERS_SERVICE_PORT=8084
INVOICING_SERVICE_PORT=8085
REPORTS_SERVICE_PORT=8086
API_GATEWAY_PORT=8080
MYSQL_PORT=3306

# Network
NETWORK_NAME=forestech-network
NETWORK_SUBNET=172.20.0.0/16
EOF
```

---

### Tarea 1.1.4: Crear Docker Compose Base

```bash
cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  # ============================================
  # SERVICE REGISTRY - Consul
  # ============================================
  consul:
    image: consul:1.15
    container_name: consul
    hostname: consul
    ports:
      - "${CONSUL_PORT:-8500}:8500"
    environment:
      - CONSUL_BIND_INTERFACE=eth0
      - CONSUL_CLIENT_INTERFACE=eth0
    command: agent -server -ui -bootstrap-expect=1 -client=0.0.0.0
    healthcheck:
      test: ["CMD", "consul", "members"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - forestech-network
    volumes:
      - consul-data:/consul/data

  # ============================================
  # DATABASES - MySQL
  # ============================================
  mysql-catalog:
    image: mysql:${MYSQL_VERSION:-8.0}
    container_name: mysql-catalog
    hostname: mysql-catalog
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${CATALOG_DB_NAME}
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3307:3306"
    volumes:
      - catalog-data:/var/lib/mysql
      - ./infrastructure/databases/catalog/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - forestech-network

  mysql-fleet:
    image: mysql:${MYSQL_VERSION:-8.0}
    container_name: mysql-fleet
    hostname: mysql-fleet
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${FLEET_DB_NAME}
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3308:3306"
    volumes:
      - fleet-data:/var/lib/mysql
      - ./infrastructure/databases/fleet/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - forestech-network

  mysql-inventory:
    image: mysql:${MYSQL_VERSION:-8.0}
    container_name: mysql-inventory
    hostname: mysql-inventory
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${INVENTORY_DB_NAME}
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3309:3306"
    volumes:
      - inventory-data:/var/lib/mysql
      - ./infrastructure/databases/inventory/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - forestech-network

  mysql-partners:
    image: mysql:${MYSQL_VERSION:-8.0}
    container_name: mysql-partners
    hostname: mysql-partners
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${PARTNERS_DB_NAME}
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3310:3306"
    volumes:
      - partners-data:/var/lib/mysql
      - ./infrastructure/databases/partners/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - forestech-network

  mysql-invoicing:
    image: mysql:${MYSQL_VERSION:-8.0}
    container_name: mysql-invoicing
    hostname: mysql-invoicing
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${INVOICING_DB_NAME}
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3311:3306"
    volumes:
      - invoicing-data:/var/lib/mysql
      - ./infrastructure/databases/invoicing/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - forestech-network

# ============================================
# NETWORKS
# ============================================
networks:
  forestech-network:
    driver: bridge
    ipam:
      config:
        - subnet: ${NETWORK_SUBNET:-172.20.0.0/16}

# ============================================
# VOLUMES
# ============================================
volumes:
  consul-data:
  catalog-data:
  fleet-data:
  inventory-data:
  partners-data:
  invoicing-data:
EOF
```

---

### Tarea 1.1.5: Crear Scripts SQL de Inicialización

#### Catalog Database
```bash
cat > infrastructure/databases/catalog/init.sql << 'EOF'
-- Catalog Database Initialization
USE catalog_db;

CREATE TABLE IF NOT EXISTS oil_products (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    measurement_unit ENUM('LITROS', 'GALONES', 'BARRILES') NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO oil_products (id, name, measurement_unit, unit_price, description) VALUES
('PROD-001', 'ACPM', 'GALONES', 8500.00, 'Diesel para vehículos'),
('PROD-002', 'Gasolina Corriente', 'GALONES', 9200.00, 'Gasolina 87 octanos'),
('PROD-003', 'Gasolina Extra', 'GALONES', 10500.00, 'Gasolina 92 octanos');
EOF
```

#### Fleet Database
```bash
cat > infrastructure/databases/fleet/init.sql << 'EOF'
-- Fleet Database Initialization
USE fleet_db;

CREATE TABLE IF NOT EXISTS vehicles (
    id VARCHAR(50) PRIMARY KEY,
    placa VARCHAR(20) UNIQUE NOT NULL,
    category ENUM('MEZCLADORAS', 'MONTACARGA', 'VOLQUETAS', 'CARGADORES', 'GENERAL') NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    year INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_placa (placa),
    INDEX idx_category (category),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO vehicles (id, placa, category, brand, model, year) VALUES
('VEH-001', 'ABC123', 'MONTACARGA', 'Toyota', 'Forklift', 2020),
('VEH-002', 'DEF456', 'VOLQUETAS', 'Kenworth', 'T800', 2019);
EOF
```

#### Inventory Database
```bash
cat > infrastructure/databases/inventory/init.sql << 'EOF'
-- Inventory Database Initialization
USE inventory_db;

CREATE TABLE IF NOT EXISTS movements (
    id VARCHAR(50) PRIMARY KEY,
    movement_type ENUM('ENTRADA', 'SALIDA') NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    vehicle_id VARCHAR(50),
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (movement_type),
    INDEX idx_product (product_id),
    INDEX idx_vehicle (vehicle_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
EOF
```

#### Partners Database
```bash
cat > infrastructure/databases/partners/init.sql << 'EOF'
-- Partners Database Initialization
USE partners_db;

CREATE TABLE IF NOT EXISTS suppliers (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    nit VARCHAR(20) UNIQUE NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_nit (nit),
    INDEX idx_name (name),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO suppliers (id, name, nit, contact_person, phone, email) VALUES
('SUP-001', 'Petróleo S.A.', '900123456-7', 'Juan Pérez', '3001234567', 'juan@petroleo.com');
EOF
```

#### Invoicing Database
```bash
cat > infrastructure/databases/invoicing/init.sql << 'EOF'
-- Invoicing Database Initialization
USE invoicing_db;

CREATE TABLE IF NOT EXISTS facturas (
    id VARCHAR(50) PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE NOT NULL,
    supplier_id VARCHAR(50) NOT NULL,
    fecha TIMESTAMP NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    iva DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADA', 'ANULADA') DEFAULT 'PENDIENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_supplier (supplier_id),
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS detalles_factura (
    id VARCHAR(50) PRIMARY KEY,
    factura_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
    INDEX idx_factura (factura_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
EOF
```

---

### Tarea 1.1.6: Crear Script de Health Check

```bash
cat > scripts/health-check.sh << 'EOF'
#!/bin/bash

echo "🏥 Verificando salud de servicios..."
echo "=========================================="

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para verificar servicio
check_service() {
    local name=$1
    local url=$2
    
    if curl -sf "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $name: UP${NC}"
        return 0
    else
        echo -e "${RED}❌ $name: DOWN${NC}"
        return 1
    fi
}

# Verificar Consul
check_service "Consul" "http://localhost:8500/v1/status/leader"

# Verificar Config Server (cuando exista)
# check_service "Config Server" "http://localhost:8888/actuator/health"

# Verificar Bases de Datos
echo ""
echo "Verificando Bases de Datos..."
for port in 3307 3308 3309 3310 3311; do
    if nc -z localhost $port 2>/dev/null; then
        echo -e "${GREEN}✅ MySQL en puerto $port: UP${NC}"
    else
        echo -e "${RED}❌ MySQL en puerto $port: DOWN${NC}"
    fi
done

echo ""
echo "=========================================="
echo "Verificación completada"
EOF

chmod +x scripts/health-check.sh
```

---

### Tarea 1.1.7: Crear Scripts Start/Stop

#### Start Script
```bash
cat > scripts/start-all.sh << 'EOF'
#!/bin/bash

echo "🚀 Iniciando Forestech Microservices..."
echo "=========================================="

# Cargar variables de entorno
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# 1. Levantar infraestructura base
echo "📦 Levantando Consul..."
docker-compose up -d consul
sleep 10

# 2. Levantar bases de datos
echo "📊 Levantando bases de datos MySQL..."
docker-compose up -d mysql-catalog mysql-fleet mysql-inventory mysql-partners mysql-invoicing
sleep 20

echo ""
echo "✅ Infraestructura base iniciada!"
echo "=========================================="
echo "Consul UI: http://localhost:8500"
echo ""
echo "Bases de datos MySQL:"
echo "  - Catalog:   localhost:3307"
echo "  - Fleet:     localhost:3308"
echo "  - Inventory: localhost:3309"
echo "  - Partners:  localhost:3310"
echo "  - Invoicing: localhost:3311"
echo ""
echo "Ejecuta './scripts/health-check.sh' para verificar"
EOF

chmod +x scripts/start-all.sh
```

#### Stop Script
```bash
cat > scripts/stop-all.sh << 'EOF'
#!/bin/bash

echo "🛑 Deteniendo Forestech Microservices..."
docker-compose down

echo "✅ Todos los servicios detenidos"
echo ""
echo "Para eliminar también los volúmenes (⚠️  BORRA DATOS):"
echo "  docker-compose down -v"
EOF

chmod +x scripts/stop-all.sh
```

---

### Tarea 1.1.8: Crear README Principal

```bash
cat > README.md << 'EOF'
# Forestech Oil - Microservices Architecture

Sistema de gestión de combustibles en arquitectura de microservicios.

## 🏗️ Arquitectura

- **7 Microservicios:** catalog, fleet, inventory, partners, invoicing, reports, api-gateway
- **Infraestructura:** Consul (service registry), Config Server, MySQL (5 databases)
- **Tecnologías:** Java 17, Spring Boot, Docker, Docker Compose

## 🚀 Quick Start

```bash
# 1. Iniciar infraestructura y bases de datos
./scripts/start-all.sh

# 2. Verificar salud de servicios
./scripts/health-check.sh

# 3. Acceder a Consul UI
# http://localhost:8500
```

## 🛑 Detener Servicios

```bash
./scripts/stop-all.sh
```

## 📊 Bases de Datos

| Base de Datos | Puerto | Usuario | Password |
|---------------|--------|---------|----------|
| catalog_db | 3307 | root | (ver .env) |
| fleet_db | 3308 | root | (ver .env) |
| inventory_db | 3309 | root | (ver .env) |
| partners_db | 3310 | root | (ver .env) |
| invoicing_db | 3311 | root | (ver .env) |

## 🔍 Verificar Estado

```bash
# Ver contenedores en ejecución
docker-compose ps

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f consul
```

## 📚 Documentación

- [Roadmap Maestro](./ROADMAP_MAESTRO_MICROSERVICIOS.md)
- [Plan de Implementación](./docs/implementation_plan.md)

## 📝 Estado Actual

- ✅ Fase 1: Infraestructura Base
- ⏳ Fase 2: Primeros Microservicios
- ⏳ Fase 3: Servicios Core
- ⏳ Fase 4: Gateway y Reportes
- ⏳ Fase 5: Frontend y Finalización
EOF
```

---

### ✅ Verificación de Sesión 1.1

Ejecuta estos comandos para verificar que todo está correcto:

```bash
# 1. Verificar estructura de directorios
ls -la
# Debe existir: services/, infrastructure/, scripts/, docs/, reportes/

# 2. Verificar archivos de configuración
ls -la *.yml *.env
# Debe existir: docker-compose.yml, .env

# 3. Verificar scripts tienen permisos de ejecución
ls -la scripts/
# start-all.sh, stop-all.sh, health-check.sh deben tener 'x' en permisos

# 4. Iniciar servicios
./scripts/start-all.sh

# 5. Esperar 30 segundos
sleep 30

# 6. Verificar salud
./scripts/health-check.sh

# 7. Verificar contenedores
docker-compose ps
# Todos deben estar en estado "Up"

# 8. Acceder a Consul UI
# Abrir navegador en http://localhost:8500
# Debe mostrar interfaz de Consul sin errores
```

**Criterios de Éxito de Sesión 1.1:**
- [x] Estructura de directorios creada
- [x] docker-compose.yml configurado
- [x] Scripts de utilidad creados y ejecutables
- [x] Consul accesible en puerto 8500
- [x] 5 bases de datos MySQL arrancadas y accesibles
- [x] Health check pasa sin errores

---

## 🔨 SESIÓN 1.2: Config Server y Finalización

**Duración Estimada:** 4-6 horas

### Tarea 1.2.1: Crear Config Server - pom.xml

```bash
cat > infrastructure/config-server/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.forestech</groupId>
    <artifactId>config-server</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Forestech Config Server</name>
    <description>Centralized configuration server</description>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>

    <dependencies>
        <!-- Spring Cloud Config Server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>

        <!-- Spring Boot Actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF
```

---

### Tarea 1.2.2: Crear Config Server - Aplicación Java

```bash
mkdir -p infrastructure/config-server/src/main/java/com/forestech/config

cat > infrastructure/config-server/src/main/java/com/forestech/config/ConfigServerApplication.java << 'EOF'
package com.forestech.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server - Servidor de configuración centralizada
 * Puerto: 8888
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
EOF
```

---

### Tarea 1.2.3: Crear Config Server - Configuración

```bash
mkdir -p infrastructure/config-server/src/main/resources

cat > infrastructure/config-server/src/main/resources/application.yml << 'EOF'
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config
  profiles:
    active: native

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

logging:
  level:
    root: INFO
    com.forestech: DEBUG
EOF
```

---

### Tarea 1.2.4: Crear Archivos de Configuración para Microservicios

```bash
mkdir -p infrastructure/config-server/src/main/resources/config

# Configuración para catalog-service
cat > infrastructure/config-server/src/main/resources/config/catalog-service.yml << 'EOF'
server:
  port: 8081

spring:
  application:
    name: catalog-service
  datasource:
    url: jdbc:mysql://mysql-catalog:3306/catalog_db?useSSL=false&serverTimezone=UTC
    username: root
    password: forestech_root_2024
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
EOF

# Configuración para fleet-service
cat > infrastructure/config-server/src/main/resources/config/fleet-service.yml << 'EOF'
server:
  port: 8082

spring:
  application:
    name: fleet-service
  datasource:
    url: jdbc:mysql://mysql-fleet:3306/fleet_db?useSSL=false&serverTimezone=UTC
    username: root
    password: forestech_root_2024
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
EOF
```

---

### Tarea 1.2.5: Crear Dockerfile para Config Server

```bash
cat > infrastructure/config-server/Dockerfile << 'EOF'
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
```

---

### Tarea 1.2.6: Actualizar Docker Compose con Config Server

```bash
# Agregar servicio de config-server al docker-compose.yml
cat >> docker-compose.yml << 'EOF'

  # ============================================
  # CONFIG SERVER
  # ============================================
  config-server:
    build:
      context: ./infrastructure/config-server
      dockerfile: Dockerfile
    container_name: config-server
    hostname: config-server
    ports:
      - "${CONFIG_SERVER_PORT:-8888}:8888"
    depends_on:
      - consul
    environment:
      SPRING_PROFILES_ACTIVE: native
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8888/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    networks:
      - forestech-network
EOF
```

---

### Tarea 1.2.7: Compilar y Desplegar Config Server

```bash
# Nota: Esto requiere Maven instalado y conexión a internet
# para descargar dependencias. En ambiente offline, asegúrate
# de tener .m2/repository completo previamente.

cd infrastructure/config-server

# Compilar
mvn clean package -DskipTests

# Volver al directorio raíz
cd ../..

# Levantar config-server
docker-compose up -d config-server

# Esperar que inicie
sleep 30

# Verificar
curl http://localhost:8888/actuator/health | jq
# Debe retornar: {"status":"UP"}
```

---

### Tarea 1.2.8: Actualizar Health Check Script

```bash
cat > scripts/health-check.sh << 'EOF'
#!/bin/bash

echo "🏥 Verificando salud de servicios..."
echo "=========================================="

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar servicio HTTP
check_http_service() {
    local name=$1
    local url=$2
    
    if curl -sf "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $name: UP${NC}"
        return 0
    else
        echo -e "${RED}❌ $name: DOWN${NC}"
        return 1
    fi
}

# Función para verificar puerto TCP
check_tcp_port() {
    local name=$1
    local port=$2
    
    if nc -z localhost $port 2>/dev/null; then
        echo -e "${GREEN}✅ $name (port $port): UP${NC}"
        return 0
    else
        echo -e "${RED}❌ $name (port $port): DOWN${NC}"
        return 1
    fi
}

# Verificar Consul
echo -e "${YELLOW}Infraestructura:${NC}"
check_http_service "Consul" "http://localhost:8500/v1/status/leader"

# Verificar Config Server
check_http_service "Config Server" "http://localhost:8888/actuator/health"

# Verificar Bases de Datos
echo ""
echo -e "${YELLOW}Bases de Datos MySQL:${NC}"
check_tcp_port "Catalog DB" 3307
check_tcp_port "Fleet DB" 3308
check_tcp_port "Inventory DB" 3309
check_tcp_port "Partners DB" 3310
check_tcp_port "Invoicing DB" 3311

echo ""
echo "=========================================="
echo "Verificación completada"
echo ""
echo "Para ver servicios registrados en Consul:"
echo "  curl http://localhost:8500/v1/catalog/services | jq"
EOF
```

---

### Tarea 1.2.9: Actualizar Start Script

```bash
cat > scripts/start-all.sh << 'EOF'
#!/bin/bash

echo "🚀 Iniciando Forestech Microservices Infrastructure..."
echo "=========================================="

# Cargar variables de entorno
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# 1. Levantar Consul (Service Registry)
echo "📦 Levantando Consul (Service Registry)..."
docker-compose up -d consul
sleep 10

# 2. Levantar Config Server
echo "⚙️  Levantando Config Server..."
docker-compose up -d config-server
sleep 15

# 3. Levantar bases de datos
echo "📊 Levantando bases de datos MySQL..."
docker-compose up -d mysql-catalog mysql-fleet mysql-inventory mysql-partners mysql-invoicing
sleep 20

echo ""
echo "✅ Infraestructura base iniciada!"
echo "=========================================="
echo "🔍 Consul UI:        http://localhost:8500"
echo "⚙️  Config Server:    http://localhost:8888/actuator/health"
echo ""
echo "📊 Bases de datos MySQL:"
echo "  - Catalog DB:   localhost:3307 (catalog_db)"
echo "  - Fleet DB:     localhost:3308 (fleet_db)"
echo "  - Inventory DB: localhost:3309 (inventory_db)"
echo "  - Partners DB:  localhost:3310 (partners_db)"
echo "  - Invoicing DB: localhost:3311 (invoicing_db)"
echo ""
echo "📋 Ejecuta './scripts/health-check.sh' para verificar estado"
EOF
```

---

### Tarea 1.2.10: Crear Script de Verificación de Bases de Datos

```bash
cat > scripts/verify-databases.sh << 'EOF'
#!/bin/bash

echo "🗄️  Verificando esquemas de bases de datos..."
echo "=========================================="

PASSWORD="forestech_root_2024"

# Función para verificar base de datos
verify_db() {
    local port=$1
    local dbname=$2
    local expected_tables=$3
    
    echo ""
    echo "Verificando $dbname en puerto $port..."
    
    # Verificar que existe la base de datos
    result=$(mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -e "SHOW DATABASES LIKE '$dbname';" 2>/dev/null)
    
    if [ -z "$result" ]; then
        echo "❌ Base de datos $dbname no existe"
        return 1
    fi
    
    # Mostrar tablas
    echo "Tablas en $dbname:"
    mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -e "USE $dbname; SHOW TABLES;" 2>/dev/null
    
    # Contar registros (si existen datos de ejemplo)
    readarray -t tables < <(mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -sN -e "USE $dbname; SHOW TABLES;" 2>/dev/null)
    
    for table in "${tables[@]}"; do
        count=$(mysql -h 127.0.0.1 -P $port -uroot -p$PASSWORD -sN -e "USE $dbname; SELECT COUNT(*) FROM $table;" 2>/dev/null)
        echo "  - $table: $count registros"
    done
    
    echo "✅ $dbname verificado"
}

# Verificar cada base de datos
verify_db 3307 "catalog_db" "oil_products"
verify_db 3308 "fleet_db" "vehicles"
verify_db 3309 "inventory_db" "movements"
verify_db 3310 "partners_db" "suppliers"
verify_db 3311 "invoicing_db" "facturas,detalles_factura"

echo ""
echo "=========================================="
echo "Verificación de bases de datos completada"
EOF

chmod +x scripts/verify-databases.sh
```

---

## ✅ VERIFICACIÓN FINAL DE FASE 1

Ejecuta esta secuencia completa para validar:

```bash
# 1. Detener todo (si algo está corriendo)
./scripts/stop-all.sh
docker-compose down -v  # Limpia volúmenes

# 2. Iniciar todo desde cero
./scripts/start-all.sh

# 3. Esperar a que todo inicie
sleep 45

# 4. Verificar salud de servicios
./scripts/health-check.sh

# 5. Verificar bases de datos
./scripts/verify-databases.sh

# 6. Verificar contenedores
docker-compose ps
# TODOS deben mostrar status "Up" y healthy

# 7. Acceder a Consul UI
# Abrir navegador: http://localhost:8500
# Debe mostrar interfaz sin errores

# 8. Verificar Config Server
curl http://localhost:8888/actuator/health | jq
# Debe retornar: {"status":"UP"}

# 9. Probar configuración de catalog-service
curl http://localhost:8888/catalog-service/default | jq
# Debe retornar la configuración YAML en JSON
```

---

## 📋 CRITERIOS DE ÉXITO DE FASE 1

Esta fase se considera **COMPLETADA** cuando:

- [x] **Estructura de directorios:** Todos los directorios creados según especificación
- [x] **Docker Compose:** Archivo configurado con Consul, Config Server y 5 MySQL
- [x] **Consul:** Corriendo en puerto 8500, UI accesible
- [x] **Config Server:** Corriendo en puerto 8888, health check OK
- [x] **MySQL Databases:** 5 bases de datos corriendo y accesibles:
  - catalog_db en puerto 3307 ✓
  - fleet_db en puerto 3308 ✓
  - inventory_db en puerto 3309 ✓
  - partners_db en puerto 3310 ✓
  - invoicing_db en puerto 3311 ✓
- [x] **Esquemas SQL:** Tablas creadas en cada base de datos con init.sql
- [x] **Datos de ejemplo:** Al menos 1 registro en tablas principales
- [x] **Scripts de utilidad:** start-all.sh, stop-all.sh, health-check.sh ejecutables y funcionando
- [x] **Health Check:** Pasa sin errores (todos los servicios UP)
- [x] **README:** Documentación actualizada con instrucciones claras
- [x] **Network:** Red Docker forestech-network configurada
- [x] **Volumes:** Volúmenes persistentes creados para todas las bases de datos

---

## 📝 REPORTE DE FASE 1

Al completar esta fase, genera el siguiente reporte:

```bash
mkdir -p reportes

cat > reportes/REPORTE_FASE_1.md << 'EOFR'
# REPORTE - Fase 1 - Infraestructura Base

## 📅 Información General
- **Fecha de inicio:** [COMPLETAR]
- **Fecha de finalización:** [COMPLETAR]
- **Duración real:** [COMPLETAR] horas
- **Agente ejecutor:** [COMPLETAR]

## ✅ Tareas Completadas

### Sesión 1.1: Setup Docker
- [x] Estructura de directorios creada
- [x] Archivo .gitignore creado
- [x] Archivo .env con variables configuradas
- [x] docker-compose.yml base creado
- [x] Scripts SQL de inicialización creados (5 databases)
- [x] Scripts de utilidad creados (start, stop, health-check)
- [x] README principal creado
- [x] Servicios iniciados correctamente

### Sesión 1.2: Config Server
- [x] pom.xml de Config Server creado
- [x] ConfigServerApplication.java creado
- [x] application.yml configurado
- [x] Archivos de configuración para servicios creados
- [x] Dockerfile de Config Server creado
- [x] Config Server agregado a docker-compose.yml
- [x] Config Server compilado y desplegado
- [x] Scripts actualizados con Config Server
- [x] Script de verificación de databases creado

## 🐛 Problemas Encontrados
[Si hubo problemas, listarlos aquí con sus soluciones]

## 📦 Artefactos Generados
- `docker-compose.yml`
- `.env`
- `.gitignore`
- `README.md`
- `infrastructure/config-server/` (completo)
- `infrastructure/databases/*/init.sql` (5 archivos)
- `scripts/start-all.sh`
- `scripts/stop-all.sh`
- `scripts/health-check.sh`
- `scripts/verify-databases.sh`

## ✅ Verificación de Criterios de Éxito

```bash
# Comando de verificación ejecutado:
./scripts/health-check.sh

# Resultado:
[PEGAR OUTPUT AQUÍ]
```

```bash
# Verificación de bases de datos:
./scripts/verify-databases.sh

# Resultado:
[PEGAR OUTPUT AQUÍ]
```

```bash
# Docker Compose Status:
docker-compose ps

# Resultado:
[PEGAR OUTPUT AQUÍ]
```

## 🔗 Estado para Siguiente Fase
- **Fase desbloqueada:** Fase 2 - Primeros Microservicios
- **Pre-requisitos cumplidos:**
  - [x] Consul funcionando
  - [x] Config Server funcionando
  - [x] 5 bases de datos MySQL con esquemas inicializados
  - [x] Scripts de utilidad operativos
  - [x] Red Docker configurada

- **Notas para siguiente agente:**
  - Consul está registrando servicios en http://localhost:8500
  - Config Server tiene configuraciones para catalog-service y fleet-service
  - Las bases de datos ya tienen datos de ejemplo para testing
  - Usar `./scripts/health-check.sh` antes de empezar siguiente fase

## 📊 Métricas
- **Archivos creados:** [CONTAR]
- **Líneas de código/config:** [APROXIMAR]
- **Contenedores funcionando:** 7/7 (consul + config-server + 5 mysql)
- **Servicios con health check OK:** 2/2 (consul + config-server)
- **Bases de datos inicializadas:** 5/5

## 🎯 Conclusión
Fase 1 completada exitosamente. La infraestructura base está lista para recibir los microservicios en Fase 2.
EOFR
```

---

## 🚀 Próximos Pasos

Una vez completada y verificada esta Fase 1:

1. ✅ Generar reporte `reportes/REPORTE_FASE_1.md`
2. ✅ Hacer commit de todos los cambios
3. ✅ Marcar Fase 1 como completa en `ROADMAP_MAESTRO.md`
4. ✅ Desbloquear Fase 2: PRIMEROS MICROSERVICIOS

---

**Fase Creada:** 2025-11-19  
**Versión:** 1.0  
**Status:** Lista para Ejecución
