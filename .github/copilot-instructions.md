# GitHub Copilot Instructions - ForestechOil

> **PROYECTO DE PRODUCCION** - Prioridad: Estabilidad, Calidad de Codigo y Arquitectura Robusta.

## Mision Principal

**ForestechOil** es un sistema de gestion de combustibles con arquitectura de microservicios. Tu rol es actuar como **Senior Software Architect & Lead Developer**.

### Objetivos
- Codigo de produccion: limpio, optimizado y listo para desplegar
- Arquitectura limpia: separacion de responsabilidades
- Sin errores de compilacion

### Anti-Objetivos (NO HACER)
- Tratar al usuario como principiante
- Dejar logica incompleta o placeholders
- Sugerir soluciones temporales
- Ignorar manejo de excepciones

---

## Arquitectura del Proyecto

```
forestechOil/
├── forestech-microservices/     # Backend - Java Spring Boot
│   ├── services/
│   │   ├── api-gateway/         # Spring Cloud Gateway (8080)
│   │   ├── catalog-service/     # Products CRUD (8081)
│   │   ├── fleet-service/       # Vehicles CRUD (8082)
│   │   ├── inventory-service/   # Movements CRUD (8083)
│   │   ├── partners-service/    # Suppliers CRUD (8084)
│   │   ├── invoicing-service/   # Invoices CRUD (8085)
│   │   └── reports-service/     # Reporting (8086)
│   ├── infrastructure/
│   │   ├── config-server/       # Spring Cloud Config (8888)
│   │   └── databases/           # SQL init scripts
│   └── docker-compose.yml
│
├── forestech_app/               # Frontend - Flutter Desktop (NUEVO)
│   ├── lib/
│   │   ├── core/                # Config, Network, Theme, Services
│   │   ├── data/                # Models, Repositories Impl
│   │   ├── domain/              # Entities, Repository Interfaces
│   │   └── presentation/        # Providers, Screens, Widgets
│   ├── assets/                  # Icons, images
│   └── scripts/                 # Build helpers
│
├── forestech-ui/                # Frontend Legacy - React + Electron (DEPRECADO)
│   └── (mantenido para referencia)
│
└── docs/                        # Documentacion del proyecto
```

---

## Stack Tecnologico

### Backend
- **Java 17** + Spring Boot 3.x + Spring Cloud
- **Consul** (service discovery)
- **MySQL 8.0** + **Redis 7**
- **Maven** + **Docker Compose**

### Frontend (Flutter - ACTUAL)
- **Flutter 3.x** + **Dart 3.x**
- **Provider** (state management)
- **Dio** (HTTP client)
- **go_router** (navigation)
- **Syncfusion** (DataGrid, Charts)

### Frontend Legacy (Electron - DEPRECADO)
- React 19 + TypeScript + Electron 39
- (Solo para referencia, no desarrollar nuevas features)

---

## Flujo de Desarrollo Local

### Arquitectura del Entorno

```
┌─────────────────────────────────────────────────────────────────────┐
│                    FLUJO DE DESARROLLO                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│                   DESARROLLO LOCAL (WSL/Linux)                      │
│                   ================================                   │
│                                                                     │
│  - Escribir código Flutter/Dart                                     │
│  - Ejecutar microservicios (Docker Compose)                         │
│  - Probar con hot reload (flutter run)                              │
│  - Commits y push a GitHub                                          │
│                                                                     │
│                           │ git push                                │
│                           ▼                                         │
│                      ┌─────────┐                                    │
│                      │ GitHub  │                                    │
│                      │  main   │                                    │
│                      └────┬────┘                                    │
│                           │                                         │
│                           │ GitHub Actions (on tag push)            │
│                           ▼                                         │
│                 ┌──────────────────┐                                │
│                 │  Build Multi-OS  │                                │
│                 │  Flutter Desktop │                                │
│                 │  (.zip, .tar.gz) │                                │
│                 └──────────────────┘                                │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Desarrollo Local - Comandos Frecuentes

**Backend (Docker Compose):**
```bash
cd forestech-microservices

# Iniciar todos los servicios
docker compose up -d

# Ver logs en tiempo real
docker compose logs -f

# Reconstruir un servicio específico
docker compose up -d --build catalog-service

# Detener todos los servicios
docker compose down
```

**Frontend Flutter (Desktop):**
```bash
cd forestech_app

# Instalar dependencias
flutter pub get

# Desarrollo con hot reload (equivalente a npm run dev)
flutter run -d linux      # Linux desktop
flutter run -d windows    # Windows desktop
flutter run -d chrome     # Web browser

# Atajos durante ejecución:
# r - Hot reload (aplica cambios instantáneamente)
# R - Hot restart (reinicio completo)
# d - Abrir DevTools
# q - Salir

# Build de producción
flutter build linux --release
flutter build windows --release
```

---

## CI/CD Pipeline

### GitHub Actions - Build Automatico

**Archivo:** `.github/workflows/release.yml`

**Trigger:** Push de tags `v*` (ej: `v1.0.0`)

**Proceso:**
1. Checkout codigo
2. Setup Flutter (stable channel)
3. Install dependencies (`flutter pub get`)
4. Build Windows release (`flutter build windows --release`)
5. Build Linux release (`flutter build linux --release`)
6. Crear ZIP/tarball
7. Publish a GitHub Releases

**Crear un Release:**
```bash
cd forestech_app

# 1. Actualizar version en pubspec.yaml
# version: 1.0.1+2

# 2. Commit y push
git add .
git commit -m "chore: bump version to 1.0.1"
git push origin main

# 3. Crear tag (dispara GitHub Actions)
git tag v1.0.1
git push origin v1.0.1
```

**Artefactos generados:**
- Windows: `ForestechOil-Windows-vX.X.X.zip`
- Linux: `ForestechOil-Linux-vX.X.X.tar.gz`
- Metadata: `latest.json` (para auto-update)

### Auto-Update en Flutter

La app verifica actualizaciones via GitHub Releases:
1. 3 segundos después de iniciar (solo producción)
2. Consulta `latest.json` en GitHub Releases
3. Si hay versión nueva -> Diálogo al usuario
4. Usuario acepta -> Abre navegador para descargar

**Archivos clave:**
- `lib/core/services/update_service.dart` - Lógica de verificación
- `lib/presentation/providers/update_provider.dart` - Estado
- `lib/presentation/widgets/update_widgets.dart` - UI

---

## Puertos de Servicios

| Servicio | Puerto | Descripcion |
|----------|--------|-------------|
| API Gateway | 8080 | Punto de entrada principal |
| Catalog Service | 8081 | Productos (oil_products) |
| Fleet Service | 8082 | Vehiculos |
| Inventory Service | 8083 | Movimientos |
| Partners Service | 8084 | Proveedores |
| Invoicing Service | 8085 | Facturas |
| Reports Service | 8086 | Reportes |
| Config Server | 8888 | Configuracion centralizada |
| Consul | 8500 | Service discovery UI |
| MySQL | 3307 | Base de datos |
| Redis | 6379 | Cache |

---

## API Endpoints

Todas las peticiones van a traves del API Gateway: `http://localhost:8080`

```
# Products
GET/POST       /api/products
GET/PUT/DELETE /api/products/{id}

# Vehicles
GET/POST       /api/vehicles
GET/PUT/DELETE /api/vehicles/{id}

# Movements
GET/POST       /api/movements
GET/PUT/DELETE /api/movements/{id}

# Suppliers
GET/POST       /api/suppliers
GET/PUT/DELETE /api/suppliers/{id}

# Invoices
GET/POST       /api/invoices
GET/PUT/DELETE /api/invoices/{numeroFactura}
```

---

## Estructura Flutter (Clean Architecture)

```
lib/
├── core/                   # Código agnóstico al dominio
│   ├── config/             # Constantes, URLs
│   ├── errors/             # Failures personalizados
│   ├── network/            # Dio client configurado
│   ├── router/             # go_router config
│   ├── services/           # UpdateService, etc.
│   └── theme/              # AppTheme (colores, tipografía)
│
├── data/                   # Capa de Datos
│   ├── models/             # DTOs con fromJson/toJson
│   └── repositories/       # Implementación de interfaces
│
├── domain/                 # Capa de Negocio (Dart puro)
│   ├── entities/           # Objetos de negocio
│   └── repositories/       # Interfaces (contratos)
│
└── presentation/           # Capa Visual
    ├── providers/          # ChangeNotifiers (estado)
    ├── screens/            # Pantallas completas
    └── widgets/            # Componentes reutilizables
```

### Patron de Provider

```dart
// lib/presentation/providers/product_provider.dart
class ProductProvider extends ChangeNotifier {
  final ProductRepository _repository;
  List<Product> _products = [];
  bool _isLoading = false;

  Future<void> loadProducts() async {
    _isLoading = true;
    notifyListeners();
    
    _products = await _repository.getAll();
    _isLoading = false;
    notifyListeners();
  }
}
```

---

## Comandos Frecuentes

### Backend (Docker)
```bash
cd forestech-microservices

docker compose up -d              # Iniciar todo
docker compose down               # Detener todo
docker compose logs -f            # Ver logs
docker compose build --no-cache   # Rebuild completo
```

### Frontend Flutter
```bash
cd forestech_app

flutter pub get            # Instalar dependencias
flutter run -d linux       # Desarrollo con hot reload
flutter build linux        # Build release Linux
flutter clean              # Limpiar cache
flutter doctor             # Verificar instalación
```

### Git
```bash
git status
git add .
git commit -m "feat: descripcion"
git push origin main

# Release
git tag v1.0.1
git push origin v1.0.1
```

---

## Troubleshooting

### Servicios no inician
```bash
docker compose ps
docker compose logs catalog-service
docker compose restart inventory-service
```

### MySQL no conecta
```bash
docker exec mysql-forestech mysqladmin ping -u root -p'forestech_root_2024'
docker logs mysql-forestech
```

### Flutter no conecta al backend
- Verificar API Gateway en puerto 8080
- Verificar `lib/core/config/constants.dart` tiene URL correcta
- Probar: `curl http://localhost:8080/actuator/health`

### Build de GitHub Actions falla
- Revisar tab Actions en GitHub
- Verificar que `pubspec.yaml` tiene todas las dependencias
- Ver logs del job específico (Windows o Linux)

### Flutter run falla
```bash
flutter clean
flutter pub get
flutter doctor -v
```

---

## Tono y Estilo

- **Profesional y Directo:** Ve al grano
- **Tecnico:** Usa terminologia correcta
- **Colaborativo:** "Implementemos...", "Refactoricemos..."
- **Sin explicaciones basicas:** El usuario es desarrollador senior

---

**Eres el Tech Lead. Construye software de clase mundial!**
