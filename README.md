# ForestechOil - Sistema de Gestión de Combustibles

![Flutter](https://img.shields.io/badge/Flutter-3.x-blue)
![Dart](https://img.shields.io/badge/Dart-3.x-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Build](https://img.shields.io/badge/build-passing-brightgreen)

## 📋 Descripción

**ForestechOil** es una aplicación de escritorio para gestionar inventarios de combustible, flota de vehículos, proveedores y facturación en empresas forestales.

### Características Principales

✅ **Gestión de Productos** - Catálogo de combustibles (Diesel, Gasolina, Aceite, etc.)  
✅ **Gestión de Vehículos** - Flota vehicular (camiones, excavadoras, motosierras)  
✅ **Movimientos de Combustible** - Registro de ENTRADAS/SALIDAS con validación de stock  
✅ **Facturas de Compra** - Gestión de facturas con detalles  
✅ **Proveedores** - Catálogo de proveedores de combustible  
✅ **Dashboard** - Resumen ejecutivo con métricas clave  
✅ **Auto-Update** - Verificación automática de nuevas versiones  

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                      ForestechOil                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│   ┌────────────────────────────────────────────────────┐    │
│   │              Flutter Desktop App                    │    │
│   │              (Windows / Linux)                      │    │
│   └────────────────────────┬───────────────────────────┘    │
│                            │ HTTP/REST                       │
│                            ▼                                 │
│   ┌────────────────────────────────────────────────────┐    │
│   │              API Gateway (8080)                     │    │
│   └────────────────────────┬───────────────────────────┘    │
│                            │                                 │
│   ┌────────────────────────┼────────────────────────────┐   │
│   │                        │                            │   │
│   │  ┌─────────┐  ┌───────┴──────┐  ┌─────────────┐   │   │
│   │  │Catalog  │  │  Inventory   │  │   Fleet     │   │   │
│   │  │ (8081)  │  │   (8083)     │  │   (8082)    │   │   │
│   │  └─────────┘  └──────────────┘  └─────────────┘   │   │
│   │                                                     │   │
│   │  ┌─────────┐  ┌──────────────┐  ┌─────────────┐   │   │
│   │  │Partners │  │  Invoicing   │  │   Reports   │   │   │
│   │  │ (8084)  │  │   (8085)     │  │   (8086)    │   │   │
│   │  └─────────┘  └──────────────┘  └─────────────┘   │   │
│   │                                                     │   │
│   │  ┌─────────────────────────────────────────────┐   │   │
│   │  │       MySQL (3307) + Redis (6379)           │   │   │
│   │  └─────────────────────────────────────────────┘   │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Requisitos
- Docker & Docker Compose
- Flutter SDK 3.x (stable)

### 1. Iniciar Backend
```bash
cd forestech-microservices
docker compose up -d
```

### 2. Iniciar Frontend (Desarrollo)
```bash
cd forestech_app
flutter pub get
flutter run -d linux
```

### Atajos durante desarrollo
| Tecla | Acción |
|-------|--------|
| `r` | Hot reload (instantáneo) |
| `R` | Hot restart (completo) |
| `d` | Abrir DevTools |
| `q` | Salir |

---

## 📦 Descargar Release

Los ejecutables están disponibles en [GitHub Releases](https://github.com/evertweb/programajava/releases):

| Plataforma | Archivo |
|------------|---------|
| Windows | `ForestechOil-Windows-vX.X.X.zip` |
| Linux | `ForestechOil-Linux-vX.X.X.tar.gz` |

### Instalación
1. Descargar el archivo para tu plataforma
2. Extraer el contenido
3. Ejecutar `forestech_app.exe` (Windows) o `./forestech_app` (Linux)

---

## 🛠️ Stack Tecnológico

### Frontend
| Tecnología | Uso |
|------------|-----|
| Flutter 3.x | Framework UI |
| Dart 3.x | Lenguaje |
| Provider | State Management |
| Dio | HTTP Client |
| go_router | Navegación |
| Syncfusion | DataGrid, Charts |

### Backend
| Tecnología | Uso |
|------------|-----|
| Java 17 | Lenguaje |
| Spring Boot 3.x | Framework |
| Spring Cloud | Microservicios |
| MySQL 8.0 | Base de datos |
| Redis 7 | Cache |
| Consul | Service Discovery |

---

## 📁 Estructura del Proyecto

```
forestechOil/
├── forestech_app/               # Frontend Flutter
│   ├── lib/
│   │   ├── core/                # Config, Network, Theme
│   │   ├── data/                # Models, Repositories
│   │   ├── domain/              # Entities, Interfaces
│   │   └── presentation/        # Providers, Screens, Widgets
│   └── pubspec.yaml
│
├── forestech-microservices/     # Backend Spring Boot
│   ├── services/
│   │   ├── api-gateway/
│   │   ├── catalog-service/
│   │   ├── fleet-service/
│   │   ├── inventory-service/
│   │   ├── partners-service/
│   │   ├── invoicing-service/
│   │   └── reports-service/
│   └── docker-compose.yml
│
├── forestech-ui/                # [DEPRECADO] Electron
└── docs/                        # Documentación
```

---

## 🔄 CI/CD

Los releases se generan automáticamente con GitHub Actions:

```bash
# Crear un release
git tag v1.0.1
git push origin v1.0.1
```

Esto dispara el workflow que:
1. Compila para Windows y Linux
2. Crea un GitHub Release
3. Sube los artefactos

---

## 📚 Documentación

- [Desarrollo Local](docs/LOCAL_DEVELOPMENT.md)
- [Roadmap de Migración](docs/FLUTTER_MIGRATION_ROADMAP.md)
- [Producción Desktop](forestech_app/docs/PRODUCCION_DESKTOP.md)

---

## 📜 Licencia

Proyecto privado - Todos los derechos reservados.
