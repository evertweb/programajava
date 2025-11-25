# Producción Desktop — Fase 5

Este documento resume los pasos necesarios para preparar y generar builds de escritorio (Linux y Windows) para `forestech_app`.

Requisitos previos
- Flutter SDK instalado (compatible con la versión del proyecto).
- En Linux: herramientas para build de Flutter (glibc, clang, cmake, etc.).
- Para Windows `.exe`: usar un runner Windows (GitHub Actions) o compilar desde Windows con Visual Studio.

Pasos locales (Linux)
1. Añade tu icono en `assets/icon.png` (reemplaza por el PNG de tu marca).
2. Desde `forestech_app/`:

```bash
flutter pub get
flutter pub run flutter_launcher_icons:main
flutter build linux --release
```

Salida esperada (Linux): `build/linux/x64/release/bundle` (contiene el ejecutable y recursos).

Generar .exe (Windows)
- No se recomienda intentar compilar `.exe` desde WSL o Linux. Para Windows, use CI o una máquina Windows.
- Ejemplo básico de job de GitHub Actions (workflow):

```yaml
name: Build Windows
on:
  push:
    tags:
      - 'v*'
jobs:
  build-windows:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Flutter
        uses: subosito/flutter-action@v2
        with:
          flutter-version: 'stable'
      - name: Install dependencies
        run: flutter pub get
      - name: Generate icons
        run: flutter pub run flutter_launcher_icons:main
      - name: Build windows
        run: flutter build windows --release
      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: forestech-win
          path: build\windows\runner\Release\
```

Notas adicionales
- El plugin `flutter_launcher_icons` genera los iconos de plataforma usando `assets/icon.png`. Sustituye el archivo por tu icono oficial antes de ejecutar la generación.
- Para integraciones firmadas y distribución profesional se recomienda configurar firma de código para Windows y notarización para macOS.

Preguntas frecuentes
- ¿Puedo generar un `.AppImage`? Sí: después de `flutter build linux --release` puedes empaquetar el bundle en un AppImage usando herramientas externas (electron-builder no aplica aquí). Hay utilidades como `linuxdeploy` y `appimagetool`.
- ¿Dónde poner archivos extra de empaquetado? Crea `scripts/` con helpers (hay un ejemplo `scripts/build_desktop.sh`).

