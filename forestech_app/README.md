# ForestechOil - Flutter Desktop App

Aplicación de gestión de combustibles migrada de Electron a Flutter.

## Desarrollo Local

```bash
# Instalar dependencias
flutter pub get

# Ejecutar en modo desarrollo (equivalente a npm run dev)
flutter run -d linux      # Desktop Linux con hot reload
flutter run -d windows    # Desktop Windows
flutter run -d chrome     # Web (navegador)

# Atajos durante ejecución:
# r - Hot reload (aplica cambios sin reiniciar)
# R - Hot restart (reinicio completo)
# d - Abrir DevTools
# q - Salir
```

## Producción Desktop (Fase 5)

### Requisitos
- Flutter SDK (versión estable)
- Linux: clang, cmake, ninja-build, libgtk-3-dev
- Windows: Visual Studio con C++ desktop workload

### Build Local

```bash
# Generar iconos de plataforma
flutter pub run flutter_launcher_icons:main

# Build Linux
flutter build linux --release
# Output: build/linux/x64/release/bundle/

# Build Windows (solo desde Windows)
flutter build windows --release
# Output: build/windows/runner/Release/
```

### Script Helper
```bash
./scripts/build_desktop.sh
```

### Notas vs Electron
| Aspecto | Electron (anterior) | Flutter (actual) |
|---------|---------------------|------------------|
| Dev server | `npm run electron:dev` | `flutter run -d linux` |
| Hot reload | Limitado | Instantáneo (tecla r) |
| Build Linux | `npm run electron:build:linux` | `flutter build linux --release` |
| Build Windows | GitHub Actions | GitHub Actions (modificado) |
| Auto-update | electron-updater | Por implementar (ver docs/) |

Ver documentación completa en `docs/PRODUCCION_DESKTOP.md`.

## Recursos

- [Flutter Desktop](https://docs.flutter.dev/desktop)
- [Cookbook](https://docs.flutter.dev/cookbook)
