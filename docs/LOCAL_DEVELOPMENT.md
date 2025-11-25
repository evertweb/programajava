# Guía de Desarrollo Local - ForestechOil

> Actualizado: Noviembre 2025 (Migración a Flutter)

## Arquitectura General

```
┌─────────────────────────────────────────────────────────────┐
│                    ForestechOil Stack                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐     ┌──────────────────────────────┐  │
│  │   Flutter App    │────▶│     API Gateway (8080)       │  │
│  │  forestech_app/  │     │                              │  │
│  └──────────────────┘     └──────────────┬───────────────┘  │
│                                          │                   │
│                    ┌─────────────────────┼─────────────────┐│
│                    │                     │                 ││
│              ┌─────▼─────┐  ┌────────────▼──┐  ┌──────────┐││
│              │ Catalog   │  │  Inventory    │  │ Partners │││
│              │  (8081)   │  │   (8083)      │  │  (8084)  │││
│              └───────────┘  └───────────────┘  └──────────┘││
│                                                             ││
│              ┌───────────┐  ┌───────────────┐  ┌──────────┐││
│              │  Fleet    │  │  Invoicing    │  │ Reports  │││
│              │  (8082)   │  │   (8085)      │  │  (8086)  │││
│              └───────────┘  └───────────────┘  └──────────┘││
│                                                             ││
│              ┌───────────────────────────────────────────┐ ││
│              │        MySQL (3307) + Redis (6379)        │ ││
│              └───────────────────────────────────────────┘ ││
└─────────────────────────────────────────────────────────────┘
```

---

## Requisitos

### Backend (Docker)
- Docker Desktop o Docker Engine
- Docker Compose v2

### Frontend (Flutter)
- Flutter SDK 3.x (stable)
- Dart SDK 3.x
- Para Linux: `clang`, `cmake`, `ninja-build`, `libgtk-3-dev`
- Para Windows: Visual Studio con C++ desktop workload

Verificar instalación:
```bash
flutter doctor -v
```

---

## Iniciar Backend

```bash
cd forestech-microservices

# Primera vez (construye imágenes)
docker compose up -d --build

# Siguientes veces
docker compose up -d

# Ver logs
docker compose logs -f

# Ver estado
docker compose ps

# Detener todo
docker compose down
```

### Verificar que funciona
```bash
# Health check del gateway
curl http://localhost:8080/actuator/health

# Listar productos
curl http://localhost:8080/api/products
```

---

## Desarrollo Frontend (Flutter)

### Primer Setup
```bash
cd forestech_app

# Instalar dependencias
flutter pub get

# Verificar que todo está OK
flutter doctor
```

### Desarrollo Diario
```bash
cd forestech_app

# Ejecutar con hot reload
flutter run -d linux

# Atajos en ejecución:
# r - Hot reload (instantáneo)
# R - Hot restart
# d - DevTools
# q - Salir
```

### Alternativas de ejecución
```bash
# En navegador (útil para debug rápido)
flutter run -d chrome

# Con verbose logging
flutter run -d linux --verbose
```

---

## Estructura del Proyecto Flutter

```
forestech_app/
├── lib/
│   ├── main.dart                 # Entry point
│   ├── core/
│   │   ├── config/constants.dart # API URL, etc.
│   │   ├── network/dio_client.dart
│   │   ├── router/app_router.dart
│   │   ├── services/update_service.dart
│   │   └── theme/app_theme.dart
│   ├── data/
│   │   ├── models/               # DTOs (fromJson/toJson)
│   │   └── repositories/         # Implementaciones
│   ├── domain/
│   │   ├── entities/             # Objetos de negocio
│   │   └── repositories/         # Interfaces
│   └── presentation/
│       ├── providers/            # State management
│       ├── screens/              # Pantallas
│       └── widgets/              # Componentes
├── assets/
│   └── icon.png                  # Icono de la app
├── pubspec.yaml                  # Dependencias
└── scripts/
    └── build_desktop.sh          # Helper de build
```

---

## Comandos Útiles

### Flutter
```bash
# Limpiar y reinstalar
flutter clean && flutter pub get

# Analizar código
flutter analyze

# Formatear código
dart format lib/

# Generar código (json_serializable, etc.)
flutter pub run build_runner build

# Build release
flutter build linux --release
flutter build windows --release
```

### Docker
```bash
# Rebuild un servicio
docker compose up -d --build catalog-service

# Ver logs de un servicio
docker compose logs -f inventory-service

# Ejecutar comando en contenedor
docker exec -it mysql-forestech mysql -u root -p

# Limpiar todo
docker compose down -v
docker system prune -f
```

---

## Variables de Entorno

### Flutter (`lib/core/config/constants.dart`)
```dart
class AppConstants {
  static const String apiBaseUrl = 'http://localhost:8080/api';
  static const Duration timeout = Duration(seconds: 30);
}
```

### Backend (docker-compose.yml)
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - MYSQL_HOST=mysql-forestech
  - MYSQL_PORT=3306
```

---

## Troubleshooting

### Flutter no encuentra dispositivo Linux
```bash
# Habilitar desktop
flutter config --enable-linux-desktop

# Verificar
flutter devices
```

### Error de conexión al backend
1. Verificar que Docker está corriendo: `docker compose ps`
2. Verificar gateway: `curl http://localhost:8080/actuator/health`
3. Verificar URL en `constants.dart`

### Hot reload no funciona
```bash
# Reiniciar completamente
# En terminal de flutter: presionar R (mayúscula)
# O salir con q y volver a ejecutar
flutter run -d linux
```

### Errores de compilación
```bash
flutter clean
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
```

---

## Workflows de Desarrollo

### Nuevo Feature
1. Crear branch: `git checkout -b feat/nueva-feature`
2. Desarrollar con `flutter run -d linux`
3. Probar cambios (hot reload)
4. Commit: `git commit -m "feat: descripción"`
5. Push: `git push origin feat/nueva-feature`
6. Crear PR en GitHub

### Release
1. Actualizar versión en `pubspec.yaml`
2. Commit: `git commit -m "chore: bump version"`
3. Push: `git push origin main`
4. Tag: `git tag v1.0.1 && git push origin v1.0.1`
5. GitHub Actions genera builds automáticamente

---

## Recursos

- [Flutter Desktop](https://docs.flutter.dev/desktop)
- [Provider Package](https://pub.dev/packages/provider)
- [Dio HTTP Client](https://pub.dev/packages/dio)
- [Syncfusion Flutter](https://www.syncfusion.com/flutter-widgets)
