# GitHub Actions Workflow - ForestechOil Auto-Release

Este workflow automatiza la generación de instaladores para Windows, Linux y macOS.

## 🚀 Cómo Usar

### 1. Crear una nueva release:

```bash
# Desde WSL/Linux/Mac - cualquier sistema
git tag v0.0.3
git push origin v0.0.3
```

### 2. GitHub Actions automáticamente:
- ✅ Construye el instalador Windows (.exe)
- ✅ Construye el instalador Linux (.AppImage)
- ✅ Construye el instalador macOS (.dmg)
- ✅ Publica todos en GitHub Releases
- ✅ Genera `latest.yml` para auto-update

### 3. Los usuarios con la app instalada:
- Reciben notificación de actualización automática
- Descargan e instalan con un click

---

## 📋 Flujo Completo de Release

### Paso 1: Actualizar versión en package.json
```bash
cd forestech-ui
npm version patch  # 0.0.2 → 0.0.3
# o
npm version minor  # 0.0.2 → 0.1.0
# o
npm version major  # 0.0.2 → 1.0.0
```

### Paso 2: Commit y crear tag
```bash
git add package.json package-lock.json
git commit -m "chore: bump version to v0.0.3"
git push origin main
git tag v0.0.3
git push origin v0.0.3
```

### Paso 3: Esperar (5-10 minutos)
GitHub Actions construye automáticamente en paralelo:
- Windows runner → ForestechOil Setup 0.0.3.exe
- Linux runner → ForestechOil-0.0.3.AppImage
- macOS runner → ForestechOil-0.0.3.dmg

### Paso 4: ¡Listo!
Verifica en: https://github.com/evertweb/programajava/releases

---

## 🔍 Monitorear el Proceso

1. Ve a: https://github.com/evertweb/programajava/actions
2. Verás el workflow "Build and Release" ejecutándose
3. Click para ver logs en tiempo real de cada OS

---

## ⚙️ Configuración Técnica

- **Trigger**: Push de tags que empiezan con `v` (v0.0.3, v1.2.0, etc.)
- **Runners**: windows-latest, ubuntu-latest, macos-latest (paralelo)
- **Node.js**: v20
- **Publisher**: GitHub Releases (automático)
- **Token**: `GITHUB_TOKEN` (automático, no requiere configuración)

---

## 🛠️ Troubleshooting

### Error: "Resource not accessible by integration"
- Ir a: Settings → Actions → General
- Scroll a "Workflow permissions"
- Seleccionar: "Read and write permissions"
- Save

### Build falla en un OS específico
- Ver logs en Actions tab
- Generalmente son problemas de dependencias nativas
- Los otros OS seguirán funcionando

---

## 📦 Archivos Generados

Para cada release, se publican automáticamente:

**Windows:**
- `ForestechOil-Setup-X.X.X.exe` (instalador NSIS)
- `latest.yml` (metadata para auto-update)

**Linux:**
- `ForestechOil-X.X.X.AppImage`
- `latest-linux.yml`

**macOS:**
- `ForestechOil-X.X.X.dmg`
- `latest-mac.yml`

---

## 🎯 Ventajas de este Setup

✅ **Desarrolla en cualquier OS** - WSL, Linux, Mac, Windows
✅ **Builds reproducibles** - Mismo resultado siempre
✅ **Multi-plataforma automático** - 3 OS a la vez
✅ **Sin configuración manual** - Solo haz push del tag
✅ **Auto-update funciona** - latest.yml generado automáticamente
✅ **Gratis** - GitHub Actions gratis para repos públicos (2000 mins/mes para privados)
