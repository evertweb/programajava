# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ForestechOil** is a fuel management system built with a microservices architecture. It manages fuel movements (ENTRADA/SALIDA), vehicles, suppliers, invoices, and inventory for a forestry operation.

**Architecture:** Microservices backend (Spring Boot) + React/Electron frontend

## Project Structure

```
forestechOil/
├── forestech-microservices/     # Backend - Java Spring Boot microservices
│   ├── services/
│   │   ├── api-gateway/         # Spring Cloud Gateway (port 8080)
│   │   ├── catalog-service/     # Products CRUD (port 8081)
│   │   ├── fleet-service/       # Vehicles CRUD (port 8082)
│   │   ├── inventory-service/   # Movements CRUD (port 8083)
│   │   ├── partners-service/    # Suppliers CRUD (port 8084)
│   │   ├── invoicing-service/   # Invoices CRUD (port 8085)
│   │   └── reports-service/     # Reporting (port 8086)
│   ├── infrastructure/
│   │   ├── config-server/       # Spring Cloud Config
│   │   └── databases/           # SQL init scripts
│   └── docker-compose.yml       # Full stack orchestration
│
├── forestech-ui/                # Frontend - React + Electron
│   ├── src/
│   │   ├── components/          # React components by domain
│   │   │   ├── dashboard/
│   │   │   ├── inventory/       # Movements UI
│   │   │   ├── invoicing/       # Invoices UI
│   │   │   ├── partners/        # Suppliers UI
│   │   │   ├── products/        # Products UI
│   │   │   ├── vehicles/        # Fleet UI
│   │   │   └── layout/          # App shell, sidebar
│   │   ├── services/            # API clients (axios)
│   │   ├── types/               # TypeScript interfaces
│   │   ├── context/             # React context providers
│   │   └── theme/               # MUI theme customization
│   └── electron/                # Electron main process
│
└── forestech-desktop-client/    # (Legacy - Java Swing client)
```

## Tech Stack

### Backend (forestech-microservices)
- **Language:** Java 17
- **Framework:** Spring Boot 3.x with Spring Cloud
- **Service Discovery:** Consul
- **API Gateway:** Spring Cloud Gateway
- **Database:** MySQL 8.0 (shared instance, single schema)
- **Cache:** Redis 7
- **Build:** Maven
- **Containerization:** Docker + Docker Compose

### Frontend (forestech-ui)
- **Framework:** React 19 + TypeScript
- **UI Library:** Material UI (MUI) 7
- **Data Grid:** MUI X Data Grid
- **HTTP Client:** Axios
- **Routing:** React Router 7
- **Build Tool:** Vite 7
- **Desktop:** Electron 39
- **Package Manager:** npm

## Build and Run Commands

### Backend - Docker Compose (Recommended)

```bash
cd forestech-microservices

# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Rebuild a specific service
docker compose up -d --build catalog-service

# Stop all services
docker compose down

# Stop and remove volumes (reset data)
docker compose down -v
```

### Backend - Individual Service (Development)

```bash
cd forestech-microservices/services/catalog-service

# Build
mvn clean package -DskipTests

# Run (requires MySQL and Consul running)
mvn spring-boot:run
```

### Frontend

```bash
cd forestech-ui

# Install dependencies
npm install

# Development (browser)
npm run dev

# Development (Electron)
npm run electron:dev

# Build for production
npm run build

# Build Electron distributables
npm run electron:build:win    # Windows portable
npm run electron:build:linux  # Linux AppImage
```

## Service Ports Reference

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Main entry point for frontend |
| Catalog Service | 8081 | Products (oil_products) |
| Fleet Service | 8082 | Vehicles |
| Inventory Service | 8083 | Movements |
| Partners Service | 8084 | Suppliers |
| Invoicing Service | 8085 | Invoices (facturas) |
| Reports Service | 8086 | Reporting queries |
| Config Server | 8888 | Centralized config |
| Consul | 8500 | Service discovery UI |
| MySQL | 3307 | Database (mapped from 3306) |
| Redis | 6379 | Cache |

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`

```
# Products (Catalog Service)
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}

# Vehicles (Fleet Service)
GET    /api/vehicles
GET    /api/vehicles/{id}
POST   /api/vehicles
PUT    /api/vehicles/{id}
DELETE /api/vehicles/{id}

# Movements (Inventory Service)
GET    /api/movements
GET    /api/movements/{id}
POST   /api/movements
PUT    /api/movements/{id}
DELETE /api/movements/{id}

# Suppliers (Partners Service)
GET    /api/suppliers
GET    /api/suppliers/{id}
POST   /api/suppliers
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}

# Invoices (Invoicing Service)
GET    /api/invoices
GET    /api/invoices/{numeroFactura}
POST   /api/invoices
PUT    /api/invoices/{numeroFactura}
DELETE /api/invoices/{numeroFactura}
```

## Database Information

### MySQL Configuration
- **Host:** localhost (or mysql-forestech in Docker)
- **Port:** 3307 (Docker) / 3306 (WSL local)
- **Database:** FORESTECHOIL
- **User:** root
- **Password:** hp (local dev) / forestech_root_2024 (Docker)

### Tables (6 tables in shared schema)

1. **oil_products** - Product catalog (no dependencies)
2. **suppliers** - Supplier master data (no dependencies)
3. **vehicles** - Fleet vehicles (depends on oil_products)
4. **facturas** - Purchase invoices (depends on suppliers)
5. **Movement** - Fuel movements (depends on oil_products, vehicles, facturas)
6. **detalle_factura** - Invoice line items (depends on facturas)

### Foreign Key Relationships

```
oil_products <── vehicles.fuel_product_id (SET NULL)
oil_products <── Movement.product_id (RESTRICT)
suppliers    <── facturas.supplier_id (RESTRICT)
vehicles     <── Movement.vehicle_id (SET NULL)
facturas     <── Movement.numero_factura (SET NULL)
facturas     <── detalle_factura.numero_factura (CASCADE)
```

**See:** `.claude/DB_SCHEMA_REFERENCE.md` for complete schema details

## Code Patterns

### Backend - Service Layer Pattern

Each microservice follows the same structure:
```
com.forestech.{service}/
├── {Service}Application.java    # Spring Boot main class
├── controller/                  # REST endpoints
├── service/                     # Business logic
├── repository/                  # JPA repositories
├── model/                       # JPA entities
└── client/                      # Feign clients (inter-service calls)
```

### Frontend - Component Pattern

```typescript
// src/components/{domain}/{Domain}List.tsx - Main list view with DataGrid
// src/components/{domain}/{Domain}Form.tsx - Create/Edit dialog
// src/services/{domain}Service.ts - API calls

// Example service pattern
import api from './api';

export const productService = {
  getAll: () => api.get('/products'),
  getById: (id: string) => api.get(`/products/${id}`),
  create: (data: Product) => api.post('/products', data),
  update: (id: string, data: Product) => api.put(`/products/${id}`, data),
  delete: (id: string) => api.delete(`/products/${id}`),
};
```

## Development Workflow

### Adding a New Feature

1. **Backend changes:**
   - Add/modify entity in `model/`
   - Update repository if needed
   - Add business logic in `service/`
   - Expose via `controller/`
   - Test with curl or Postman through gateway

2. **Frontend changes:**
   - Add TypeScript types in `src/types/`
   - Create/update service in `src/services/`
   - Build component in `src/components/{domain}/`
   - Add route in `App.tsx` if needed

### Common Tasks

```bash
# Check service health
curl http://localhost:8080/actuator/health

# View Consul services
open http://localhost:8500

# Access MySQL in Docker
docker exec -it mysql-forestech mysql -u root -p'hp' FORESTECHOIL

# View container logs
docker logs -f inventory-service
```

## Important Notes

### Business Rules

- **ENTRADA** (input) movements must reference a `numero_factura` (invoice)
- **SALIDA** (output) movements must reference a `vehicle_id`
- Products cannot be deleted if referenced by movements or vehicles
- Suppliers cannot be deleted if referenced by invoices
- Invoice deletion cascades to `detalle_factura` and sets `Movement.numero_factura` to NULL

### ID Formats (Generated by Java)

- Products: `FUE-XXXXXXXX`
- Vehicles: `VEH-XXXXXXXX`
- Suppliers: `SUP-XXXXXXXX`
- Movements: `MOV-XXXXXXXX`
- Invoices: Free format (e.g., `FACT-001`)

## Environment Variables

### Backend (forestech-microservices/.env)
```env
MYSQL_ROOT_PASSWORD=forestech_root_2024
CONSUL_PORT=8500
API_GATEWAY_PORT=8080
# ... (see .env file for full list)
```

### Frontend
The frontend connects to the API Gateway at `http://localhost:8080` by default.
Configure in `src/services/api.ts`.

## Git Workflow

- **Main branch:** `main`
- **Current branch:** `updatemovements`
- Commit messages should be descriptive of changes made
- Run `docker compose down && docker compose up -d --build` after backend changes

## Troubleshooting

### Services not starting
```bash
# Check if all dependencies are healthy
docker compose ps

# View specific service logs
docker compose logs catalog-service

# Restart a stuck service
docker compose restart inventory-service
```

### Database connection issues
```bash
# Verify MySQL is running
docker exec mysql-forestech mysqladmin ping -u root -p'hp'

# Check database exists
docker exec mysql-forestech mysql -u root -p'hp' -e "SHOW DATABASES;"
```

### Frontend can't connect to backend
- Ensure API Gateway is running on port 8080
- Check CORS configuration in `api-gateway/src/main/java/.../CorsConfig.java`
- Verify network connectivity: `curl http://localhost:8080/actuator/health`
