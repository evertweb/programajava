# Flutter Development Guide - ForestechOil

## 🚀 Quick Start

### Opción 1: Script de desarrollo (Recomendado)

```bash
cd forestech_app
./dev.sh
```

### Opción 2: Comando directo

```bash
cd forestech_app
flutter run -d web-server --web-port=50000 --web-hostname=0.0.0.0
```

## 🌐 Acceso a la aplicación

Desde tu navegador Windows (Chrome/Edge):

- **URL principal**: http://localhost:50000
- **URL alternativa (WSL IP)**: http://172.27.36.171:50000

## 🔥 Hot Reload - Desarrollo sin recompilar

### ¿Qué es Hot Reload?

Hot Reload permite ver cambios en tu código **instantáneamente** sin recompilar toda la aplicación ni perder el estado actual.

### Comandos durante el desarrollo:

Mientras el servidor está corriendo, puedes presionar:

- **`r`** → Hot reload (recarga los cambios) 🔥🔥🔥
- **`R`** → Hot restart (reinicia la app completamente)
- **`h`** → Muestra todos los comandos
- **`c`** → Limpia la consola
- **`q`** → Cierra el servidor

### Workflow típico:

1. **Inicia el servidor**: `./dev.sh`
2. **Abre la app en el navegador**: http://localhost:50000
3. **Modifica tu código** en VSCode/editor
4. **Guarda el archivo** (Ctrl+S)
5. **Presiona `r`** en la terminal → ¡Los cambios aparecen instantáneamente! ⚡

### Ejemplo:

```dart
// Cambias esto en lib/screens/home_screen.dart:
Text('Bienvenido a ForestechOil')

// Por esto:
Text('¡Sistema de Gestión de Combustible!')

// Guardas el archivo y presionas 'r' → El cambio aparece en ~1 segundo
```

## ⚠️ ¿Cuándo NO funciona Hot Reload?

Hot reload NO detecta cambios en:

- Archivos nativos (Android/iOS/Web HTML)
- Archivos de configuración (pubspec.yaml)
- Cambios en la estructura de widgets muy complejos

En esos casos, usa **`R`** (Hot Restart) o reinicia el servidor.

## 🔄 Comparación: Hot Reload vs Compilación completa

| Método | Tiempo | Cuándo usar |
|--------|--------|-------------|
| **Hot Reload (`r`)** | ~1 segundo | Cambios en UI, lógica, texto |
| **Hot Restart (`R`)** | ~5 segundos | Cambios profundos, reset de estado |
| **`flutter build web`** | ~50 segundos | Solo para producción |

## 🛠️ CORS Configuration

El API Gateway ya está configurado para aceptar requests desde:

- http://localhost:50000 (Servidor de desarrollo)
- http://localhost:8090 (Build de producción local)
- http://localhost:5173 (Vite - React antiguo)
- http://localhost:3000 (React antiguo)

Configuración en: `forestech-microservices/services/api-gateway/src/main/resources/application.yml`

## 📝 Flujo de desarrollo recomendado

### Para cambios rápidos (UI, textos, colores):

```bash
# 1. Inicia el servidor (una sola vez)
./dev.sh

# 2. Haz cambios en tu código
# 3. Guarda el archivo
# 4. Presiona 'r' en la terminal
# 5. ¡Listo! Los cambios aparecen instantáneamente
```

### Para cambios estructurales (nueva dependencia, assets):

```bash
# 1. Detén el servidor (presiona 'q')
# 2. Instala la nueva dependencia
flutter pub get

# 3. Reinicia el servidor
./dev.sh
```

### Para producción:

```bash
# Build optimizado (solo cuando vayas a desplegar)
flutter build web

# Los archivos estarán en: build/web/
```

## 🐛 Troubleshooting

### Error de CORS:

Si ves errores CORS en la consola del navegador:

1. Verifica que el API Gateway esté corriendo:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. Reinicia el API Gateway:
   ```bash
   cd forestech-microservices
   docker compose restart api-gateway
   ```

### El servidor no inicia:

```bash
# Mata procesos anteriores
pkill -f "flutter run"

# Reinicia
./dev.sh
```

### Los cambios no aparecen:

1. Presiona `R` (Hot Restart) en lugar de `r`
2. Si sigue sin funcionar, reinicia el servidor (q + ./dev.sh)

## 📚 Recursos adicionales

- [Flutter Hot Reload Docs](https://docs.flutter.dev/tools/hot-reload)
- [Flutter Web Debugging](https://docs.flutter.dev/platform-integration/web/debugging)
- [Flutter DevTools](https://docs.flutter.dev/tools/devtools/overview)

## 🎯 Tips para máxima productividad

1. **Mantén el servidor corriendo** todo el tiempo durante el desarrollo
2. **Usa `r` frecuentemente** para ver cambios instantáneos
3. **Solo usa `flutter build web`** cuando vayas a desplegar
4. **Usa Hot Restart (`R`)** cuando agregues nuevas dependencias o cambies assets
5. **Guarda con Ctrl+S** y presiona `r` inmediatamente

## 🔥 Hot Reload en acción

```
[Antes]
┌────────────────────────┐
│ Text('Hola')          │  ← Cambias esto en el código
└────────────────────────┘
         │
         │ Guardas (Ctrl+S)
         │ Presionas 'r'
         ↓
[Después - 1 segundo]
┌────────────────────────┐
│ Text('Hola Mundo')    │  ← Aparece automáticamente en el navegador
└────────────────────────┘
```

No más:
- ❌ Esperar 50 segundos para ver un cambio
- ❌ Recargar el navegador manualmente
- ❌ Perder el estado de la aplicación

Solo:
- ✅ Guarda → Presiona `r` → ¡Listo! ⚡
