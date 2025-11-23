# 🎯 Instalador Profesional ForestechOil - Guía Rápida

## ✅ Archivos Configurados

Tu instalador ahora incluye:

### 📁 Assets Visuales (en `build/`)
- ✅ `icon.png` - Icono de la aplicación
- ✅ `installerSidebar.png` - Banner lateral del instalador (164x314px)
- ✅ `installerHeader.png` - Header superior del instalador (150x57px)
- ✅ `welcomeImage.png` - Imagen de bienvenida (500x300px)

### 📜 Scripts NSIS Personalizados (en `build/`)
- ✅ `installer.nsh` - Script principal con:
  - Verificación de Windows 7+
  - Verificación de espacio en disco (500MB mínimo)
  - Detección de versiones anteriores
  - Manejo inteligente de actualizaciones
  - Accesos directos mejorados
  - Registro completo en Windows
  - Desinstalación avanzada

- ✅ `installer_es.nsh` - Textos en español profesional
  - Mensajes corporativos de bienvenida
  - Textos personalizados para todas las páginas
  - Descripciones de componentes
  - Mensajes de error claros

- ✅ `license.txt` - Términos y condiciones

### ⚙️ Configuración (package.json)
Actualizado con opciones profesionales:
- Imágenes personalizadas del instalador
- Scripts NSIS incluidos automáticamente
- Naming mejorado: `ForestechOil-Setup-X.X.X.exe`
- Instalación por máquina (no por usuario)
- Categoría en menú inicio: "Forestech"
- GUID único para la aplicación

---

## 🚀 Cómo Compilar el Instalador

### Opción 1: En Windows (Recomendado)

```bash
cd forestech-ui
npm run electron:build:win
```

El instalador se generará en: `release/ForestechOil-Setup-0.0.5.exe`

### Opción 2: Desde Linux/WSL (Requiere Wine)

```bash
# Instalar Wine si no lo tienes
sudo apt-get update
sudo apt-get install wine wine64

# Compilar
cd forestech-ui
npm run electron:build:win
```

### Opción 3: GitHub Actions (Automático)

El CI/CD puede compilar automáticamente en Windows runners.

---

## 🎨 Lo que Verá el Usuario

### 1. Ejecutar el Instalador
Al hacer doble clic en `ForestechOil-Setup-0.0.5.exe`:

**Pantalla de Bienvenida:**
- Banner lateral con logo y versión
- Mensaje: "Bienvenido a ForestechOil Setup"
- Descripción de la aplicación en español

**Términos de Licencia:**
- Muestra el contenido de `license.txt`
- Botón "Acepto" en español

**Selección de Directorio:**
- Permite elegir carpeta de instalación
- Por defecto: `C:\Program Files\ForestechOil`
- Muestra espacio requerido

**Instalación:**
- Barra de progreso con mensajes detallados:
  - "Registrando aplicación en el sistema..."
  - "Creando configuración inicial..."
  - "Creando accesos directos..."

**Finalización:**
- Mensaje de éxito en español
- Checkbox: "Ejecutar ForestechOil ahora"
- La app se abre automáticamente si se selecciona

### 2. Post-Instalación

El usuario tendrá:
- ✅ Acceso directo en el **Escritorio**
- ✅ Acceso directo en **Menú Inicio > Forestech > ForestechOil**
- ✅ Entrada en **Programas y Características** (Panel de Control)
- ✅ Archivo `config.json` con configuración inicial

### 3. Actualización (Si ya estaba instalado)

Si el usuario ejecuta el instalador con una versión anterior:
- Detecta la versión previa
- Pregunta: "¿Desea actualizar de 0.0.4 a 0.0.5?"
- Conserva los datos de usuario
- Actualiza solo los archivos necesarios

### 4. Desinstalación

Desde **Programas y Características** o **Menú Inicio > Forestech > Desinstalar**:
- Cierra la aplicación si está corriendo
- Pregunta: "¿Eliminar también los datos de usuario?"
- Si NO: conserva configuraciones para reinstalación futura
- Si SÍ: limpia todo incluyendo `%APPDATA%\ForestechOil`
- Elimina accesos directos y entradas del registro

---

## 🔍 Verificación de Calidad

### Antes de Distribuir, Verifica:

1. **Ejecutar en VM Windows Limpia:**
   - Instala en una máquina sin desarrollo
   - Verifica que no pida dependencias

2. **Probar Actualización:**
   - Instala versión anterior
   - Ejecuta la nueva versión
   - Verifica que detecta y actualiza

3. **Probar Desinstalación:**
   - Desinstala desde Panel de Control
   - Verifica que limpia todo correctamente

4. **Verificar Visualmente:**
   - Las imágenes se ven bien (no pixeladas)
   - Los textos están en español
   - El icono aparece correctamente

---

## 📊 Comparación: Antes vs Ahora

| Característica | Antes | Ahora |
|----------------|-------|-------|
| **Idioma** | Inglés genérico | Español profesional |
| **Imágenes** | Sin personalizar | Logo + banners corporativos |
| **Verificaciones** | Ninguna | Windows 7+, espacio en disco |
| **Actualizaciones** | No detecta | Detecta y pregunta |
| **Mensajes** | Genéricos NSIS | Corporativos ForestechOil |
| **Desinstalador** | Básico | Pregunta por datos, limpia registro |
| **Accesos directos** | Simples | Con descripciones detalladas |
| **Registro Windows** | Mínimo | Completo con metadata |
| **Nombre archivo** | Genérico | ForestechOil-Setup-X.X.X.exe |

---

## 🎓 Próximos Pasos Opcionales

### Para Nivel Premium:

1. **Firma Digital ($150-500/año):**
   - Elimina advertencia "Editor desconocido"
   - Aumenta confianza del usuario
   - Proveedores: Sectigo, DigiCert

2. **Splash Screen Animado:**
   - Logo animado mientras carga el instalador
   - Añadir `build/splash.gif`

3. **Auto-actualización:**
   - Ya tienes `electron-updater` instalado
   - Configurar verificación automática de actualizaciones

4. **Telemetría:**
   - Saber cuántas instalaciones exitosas
   - Detectar errores comunes

---

## ❓ Preguntas Frecuentes

**P: ¿El instalador funciona sin internet?**
R: Sí, es completamente offline.

**P: ¿Necesito privilegios de administrador?**
R: Sí, para instalación en `Program Files` y registro del sistema.

**P: ¿Qué pasa con los datos al actualizar?**
R: Se conservan automáticamente (archivos de configuración, base de datos local).

**P: ¿Puedo cambiar los colores/imágenes?**
R: Sí, edita las imágenes en `build/` y recompila.

**P: ¿Funciona en Windows 11?**
R: Sí, compatible con Windows 7, 8, 10 y 11.

---

## 📝 Notas Importantes

- Los scripts NSH usan macros `!macro customInit`, `!macro customInstall`, etc.
- Electron-builder inyecta automáticamente estos scripts
- Los textos en `installer_es.nsh` sobrescriben los defaults de NSIS
- El GUID debe ser único y consistente entre versiones (no cambiar)

---

**¡Tu instalador ahora está al nivel de las grandes empresas!** 🎉

Para más detalles técnicos, consulta: `GUIA_INSTALADOR_PROFESIONAL.md`
