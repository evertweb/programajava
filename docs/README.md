# Documentación ForestechOil

> **Sistema de Gestión de Combustibles**  
> Frontend: Flutter Desktop | Backend: Spring Boot Microservices

---

## �� Índice de Documentación

| Documento | Descripción |
|-----------|-------------|
| [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md) | Guía de desarrollo local |
| [FLUTTER_MIGRATION_ROADMAP.md](FLUTTER_MIGRATION_ROADMAP.md) | Roadmap de migración Electron → Flutter |
| [ARQUITECTURA_UML.md](ARQUITECTURA_UML.md) | Diagramas de arquitectura |
| [PRODUCCION_DESKTOP.md](../forestech_app/docs/PRODUCCION_DESKTOP.md) | Builds de producción Flutter |

---

## 🚀 Quick Start

### Requisitos
- Docker & Docker Compose
- Flutter SDK 3.x (stable)
- Git

### Iniciar Backend
```bash
cd forestech-microservices
docker compose up -d
```

### Iniciar Frontend (Desarrollo)
```bash
cd forestech_app
flutter pub get
flutter run -d linux
```

---

## 🏗️ Arquitectura

```
forestechOil/
├── forestech_app/           # Frontend Flutter Desktop
├── forestech-microservices/ # Backend Spring Boot
├── forestech-ui/            # [DEPRECADO] Frontend Electron
└── docs/                    # Documentación
```

### Stack Tecnológico

| Capa | Tecnología |
|------|------------|
| Frontend | Flutter 3.x + Dart + Provider |
| API Gateway | Spring Cloud Gateway |
| Microservicios | Spring Boot 3.x + Java 17 |
| Base de Datos | MySQL 8.0 + Redis 7 |
| Service Discovery | Consul |
| CI/CD | GitHub Actions |

---

## 📦 Releases

Los releases se generan automáticamente via GitHub Actions cuando se crea un tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

**Artefactos generados:**
- `ForestechOil-Windows-vX.X.X.zip` - Ejecutable Windows
- `ForestechOil-Linux-vX.X.X.tar.gz` - Ejecutable Linux

Descargar desde: https://github.com/evertweb/programajava/releases

---

## 🔗 Links Útiles

- [Flutter Documentation](https://docs.flutter.dev/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Docker Compose](https://docs.docker.com/compose/)
