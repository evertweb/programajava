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
docker compose ps

# Ver logs
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f consul
```

## 📚 Documentación

- [Roadmap Maestro](../ROADMAP_MAESTRO_MICROSERVICIOS.md)
- [Fase 1: Infraestructura](../FASE_1_INFRAESTRUCTURA.md)

## 📝 Estado Actual

- ✅ Fase 1: Infraestructura Base
- ⏳ Fase 2: Primeros Microservicios
- ⏳ Fase 3: Servicios Core
- ⏳ Fase 4: Gateway y Reportes
- ⏳ Fase 5: Frontend y Finalización
