# 🎨 Guía Completa: Instalador Profesional para ForestechOil

## 📋 Índice
1. [Qué hacen las grandes empresas](#qué-hacen-las-grandes-empresas)
2. [Elementos de un instalador profesional](#elementos-de-un-instalador-profesional)
3. [Implementación paso a paso](#implementación-paso-a-paso)
4. [Personalización avanzada](#personalización-avanzada)
5. [Ejemplos de código](#ejemplos-de-código)

---

## 🏢 Qué hacen las grandes empresas

Las empresas como **Microsoft, Adobe, Slack, Discord** y otras invierten en:

### 1. **Branding Visual Consistente**
- Logo y colores corporativos en cada pantalla
- Imágenes de alta resolución (banner, header, sidebar)
- Animaciones sutiles durante la instalación
- Iconos personalizados (.ico de 256x256px)

### 2. **Experiencia de Usuario (UX)**
- Mensajes claros y en el idioma del usuario
- Progreso visual detallado (no solo una barra)
- Opciones de instalación claras (típica/personalizada)
- Términos y condiciones profesionales
- Página de bienvenida atractiva

### 3. **Calidad Técnica**
- Firma digital de código (Code Signing)
- Verificación de requisitos del sistema
- Detección de versiones anteriores
- Desinstalador completo
- Actualizaciones automáticas

### 4. **Post-instalación**
- Ejecutar aplicación automáticamente
- Abrir página de bienvenida o tutorial
- Crear accesos directos inteligentes
- Registro en el sistema operativo

---

## 🎯 Elementos de un Instalador Profesional

### **Estructura de Pantallas (Wizard Pages)**

```
1. Splash Screen (opcional)
   └─ Logo + "Cargando instalador..."

2. Welcome Page
   └─ Imagen grande + mensaje de bienvenida

3. License Agreement
   └─ Términos y condiciones + checkbox "Acepto"

4. Installation Type (opcional)
   ├─ Típica (recomendada)
   ├─ Personalizada
   └─ Mínima

5. Choose Directory
   └─ Ruta de instalación + tamaño requerido

6. Components (opcional)
   ├─ Programa principal [obligatorio]
   ├─ Acceso directo en escritorio
   └─ Acceso directo en menú inicio

7. Installation Progress
   └─ Barra de progreso + mensajes detallados

8. Finish Page
   ├─ Mensaje de éxito
   ├─ Checkbox "Ejecutar ForestechOil"
   └─ Checkbox "Ver archivo README"
```

---

## 🛠️ Implementación Paso a Paso

### **Paso 1: Preparar Assets Visuales**

Necesitas crear las siguientes imágenes:

#### **1.1 Icono de Aplicación (.ico)**
- **Tamaño:** 256x256px (incluir también 48x48, 32x32, 16x16)
- **Formato:** .ico (multi-resolución)
- **Ubicación:** `build/icon.ico`
- **Herramientas:** 
  - Online: https://icoconvert.com/
  - Desktop: GIMP, Photoshop, Inkscape

#### **1.2 Banner del Instalador**
- **Tamaño:** 164x314px
- **Formato:** BMP (si soporte limitado) o PNG
- **Ubicación:** `build/installerSidebar.bmp`
- **Contenido:** Logo + nombre de la app + versión
- **Colores:** Corporativos de Forestech

#### **1.3 Header del Instalador**
- **Tamaño:** 150x57px
- **Formato:** BMP o PNG
- **Ubicación:** `build/installerHeader.bmp`
- **Contenido:** Logo minimizado + texto "Setup"

#### **1.4 Imagen de Bienvenida (opcional)**
- **Tamaño:** 500x300px
- **Formato:** PNG
- **Ubicación:** `build/welcomeImage.png`
- **Contenido:** Captura de pantalla de la app o diseño atractivo

---

### **Paso 2: Configurar Textos Personalizados**

#### **2.1 Crear archivo de licencia**

Ya tienes `build/license.txt`, pero asegúrate que incluya:
- Términos de uso claros
- Política de privacidad básica
- Derechos de autor
- Contacto de soporte

#### **2.2 Crear archivo de idioma personalizado**

Crea `build/installer_es.nsh` para mensajes en español:

```nsis
!define MUI_WELCOMEPAGE_TITLE "Bienvenido a ForestechOil"
!define MUI_WELCOMEPAGE_TEXT "Este asistente le guiará en la instalación de ForestechOil.$\r$\n$\r$\nSe recomienda cerrar todas las demás aplicaciones antes de continuar.$\r$\n$\r$\nHaga clic en Siguiente para continuar."

!define MUI_LICENSEPAGE_TEXT_TOP "Por favor revise los términos de licencia antes de instalar ForestechOil."
!define MUI_LICENSEPAGE_TEXT_BOTTOM "Si acepta todos los términos del acuerdo, seleccione Acepto para continuar. Debe aceptar el acuerdo para instalar ForestechOil."

!define MUI_COMPONENTSPAGE_TITLE "Selección de Componentes"
!define MUI_COMPONENTSPAGE_TEXT_TOP "Seleccione los componentes que desea instalar y deseleccione los que no desea instalar."

!define MUI_DIRECTORYPAGE_TEXT_TOP "El instalador instalará ForestechOil en la siguiente carpeta.$\r$\n$\r$\nPara instalar en una carpeta diferente, haga clic en Examinar y seleccione otra carpeta."

!define MUI_INSTFILESPAGE_TITLE "Instalando ForestechOil"
!define MUI_INSTFILESPAGE_TEXT_TOP "Por favor espere mientras ForestechOil se instala en su equipo."

!define MUI_FINISHPAGE_TITLE "Instalación Completada"
!define MUI_FINISHPAGE_TEXT "ForestechOil se ha instalado correctamente en su equipo.$\r$\n$\r$\nHaga clic en Finalizar para cerrar este asistente."
!define MUI_FINISHPAGE_RUN_TEXT "Ejecutar ForestechOil"
```

---

### **Paso 3: Configuración Avanzada de NSIS**

#### **3.1 Actualizar package.json con opciones avanzadas**

```json
"nsis": {
  "oneClick": false,
  "allowToChangeInstallationDirectory": true,
  "allowElevation": true,
  "createDesktopShortcut": true,
  "createStartMenuShortcut": true,
  "shortcutName": "ForestechOil",
  "deleteAppDataOnUninstall": false,
  "runAfterFinish": true,
  
  // NUEVAS OPCIONES PROFESIONALES
  "installerIcon": "build/icon.ico",
  "uninstallerIcon": "build/icon.ico",
  "installerSidebar": "build/installerSidebar.bmp",
  "installerHeader": "build/installerHeader.bmp",
  "installerHeaderIcon": "build/icon.ico",
  
  "license": "build/license.txt",
  "installerLanguages": ["es-419", "en_US"],
  "language": "es-419",
  
  "artifactName": "${productName}-Setup-${version}.${ext}",
  "warningsAsErrors": false,
  "differentialPackage": true,
  
  // Mensajes personalizados
  "include": "build/installer_es.nsh",
  
  // Componentes opcionales
  "perMachine": true,
  "menuCategory": true,
  
  // Páginas del wizard
  "displayLanguageSelector": true,
  "multiLanguageInstaller": true,
  
  // Post-instalación
  "installerHeaderIcon": "build/icon.ico",
  "guid": "com.forestech.oil.app"
}
```

---

### **Paso 4: Script NSIS Personalizado (Avanzado)**

Para control total, crea `build/installer.nsh`:

```nsis
# Macros personalizadas para el instalador

# Verificar requisitos del sistema
!macro customInit
  # Verificar Windows 7 o superior
  ${If} ${AtMostWin7}
    MessageBox MB_OK|MB_ICONEXCLAMATION "ForestechOil requiere Windows 7 o superior."
    Quit
  ${EndIf}
  
  # Verificar si ya está instalado
  ReadRegStr $R0 HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "UninstallString"
  StrCmp $R0 "" done
  
  MessageBox MB_YESNO|MB_ICONQUESTION \
    "ForestechOil ya está instalado. ¿Desea actualizar a la versión ${VERSION}?" \
    IDYES upgrade
    Abort
  
  upgrade:
    ExecWait '$R0 /S _?=$INSTDIR'
    Delete "$INSTDIR\Uninstall.exe"
  
  done:
!macroend

# Acciones después de la instalación
!macro customInstall
  # Crear entradas del registro
  WriteRegStr HKLM "Software\Forestech\ForestechOil" "InstallPath" "$INSTDIR"
  WriteRegStr HKLM "Software\Forestech\ForestechOil" "Version" "${VERSION}"
  
  # Crear archivo de configuración inicial
  FileOpen $0 "$INSTDIR\config.json" w
  FileWrite $0 '{"firstRun": true, "version": "${VERSION}"}'
  FileClose $0
  
  # Mensaje de bienvenida personalizado
  DetailPrint "Configurando ForestechOil..."
  DetailPrint "Creando accesos directos..."
  DetailPrint "Registrando aplicación..."
!macroend

# Acciones después de desinstalar
!macro customUnInstall
  # Limpiar registro
  DeleteRegKey HKLM "Software\Forestech\ForestechOil"
  
  # Preguntar si borrar datos de usuario
  MessageBox MB_YESNO|MB_ICONQUESTION \
    "¿Desea eliminar también los datos de la aplicación?" \
    IDNO skip_appdata
    RMDir /r "$APPDATA\ForestechOil"
  skip_appdata:
!macroend

# Página personalizada de bienvenida
!macro customWelcomePage
  !define MUI_PAGE_CUSTOMFUNCTION_PRE WelcomePagePre
  !define MUI_PAGE_CUSTOMFUNCTION_SHOW WelcomePageShow
  
  Function WelcomePagePre
    # Mostrar imagen de bienvenida si existe
    IfFileExists "$TEMP\welcomeImage.png" 0 +2
      Delete "$TEMP\welcomeImage.png"
    File /oname=$TEMP\welcomeImage.png "build\welcomeImage.png"
  FunctionEnd
  
  Function WelcomePageShow
    # Personalización adicional de la página
  FunctionEnd
!macroend
```

---

### **Paso 5: Añadir Firma Digital (Code Signing)**

> ⚠️ **Requiere certificado de firma de código**

#### **5.1 Obtener certificado**
- **Proveedores:** Sectigo, DigiCert, GlobalSign
- **Costo:** $150-$500/año
- **Tipo:** Code Signing Certificate (SHA-256)

#### **5.2 Configurar en package.json**

```json
"win": {
  "certificateFile": "certs/forestech-cert.pfx",
  "certificatePassword": "TU_PASSWORD_AQUI",
  "signingHashAlgorithms": ["sha256"],
  "signDlls": true,
  "sign": "build/sign.js"
}
```

#### **5.3 Script de firma (build/sign.js)**

```javascript
exports.default = async function(configuration) {
  require('child_process').execSync(
    `signtool.exe sign /f "certs/forestech-cert.pfx" /p "${process.env.CSC_PASSWORD}" ` +
    `/tr http://timestamp.digicert.com /td sha256 /fd sha256 "${configuration.path}"`,
    { stdio: 'inherit' }
  );
};
```

---

## 🎨 Personalización Avanzada

### **Opción 1: Tema Visual Personalizado**

Crea `build/modern_ui_theme.nsh`:

```nsis
# Tema moderno con colores Forestech
!define MUI_BGCOLOR FFFFFF
!define MUI_TEXTCOLOR 1A1A1A

# Fuentes personalizadas
!define MUI_FONT "Segoe UI, 9"
!define MUI_FONT_TITLE "Segoe UI Semibold, 12"

# Colores de íconos
!define MUI_ICON_COLOR 0088CC
```

### **Opción 2: Splash Screen Animado**

```json
"nsis": {
  "displayLanguageSelector": false,
  "splash": "build/splash.gif"
}
```

### **Opción 3: Diálogos Personalizados**

Para casos avanzados (seleccionar base de datos, configurar servidor, etc), usa **nsDialogs**:

```nsis
Function CustomConfigPage
  nsDialogs::Create 1018
  Pop $Dialog
  
  ${NSD_CreateLabel} 0 0 100% 12u "Configuración de Servidor:"
  
  ${NSD_CreateText} 0 20u 200u 12u ""
  Pop $ServerInput
  
  nsDialogs::Show
FunctionEnd
```

---

## 📊 Comparativa de Calidad

| Característica | Básico | Profesional |
|----------------|--------|-------------|
| Icono | ❌ Genérico | ✅ Corporativo multi-res |
| Imágenes | ❌ Sin personalizar | ✅ Banner + Header |
| Idioma | ❌ Inglés | ✅ Español nativo |
| Licencia | ❌ Sin mostrar | ✅ EULA personalizado |
| Firma digital | ❌ No firmado | ✅ Certificado válido |
| Mensajes | ❌ Genéricos NSIS | ✅ Textos corporativos |
| Desinstalador | ✅ Básico | ✅ Con limpieza avanzada |
| Actualizaciones | ❌ Manual | ✅ Auto-update |
| Splash screen | ❌ No | ✅ Logo animado |
| Verificación sistema | ❌ No | ✅ Check requisitos |

---

## 🚀 Checklist de Implementación

### Fase 1: Básico (2-3 horas)
- [ ] Crear icono .ico profesional (256x256)
- [ ] Diseñar banner lateral (164x314)
- [ ] Diseñar header (150x57)
- [ ] Actualizar license.txt con términos reales
- [ ] Configurar idioma español en package.json

### Fase 2: Intermedio (4-6 horas)
- [ ] Crear archivo installer_es.nsh con textos
- [ ] Añadir imágenes al instalador
- [ ] Configurar opciones avanzadas NSIS
- [ ] Probar instalador en máquina limpia
- [ ] Crear desinstalador mejorado

### Fase 3: Avanzado (1-2 días)
- [ ] Obtener certificado de firma de código
- [ ] Implementar code signing
- [ ] Crear script NSIS personalizado
- [ ] Añadir verificación de requisitos
- [ ] Implementar auto-actualización
- [ ] Crear splash screen animado

### Fase 4: Premium (opcionales)
- [ ] Diálogos personalizados de configuración
- [ ] Detección de versiones previas
- [ ] Migración de datos automática
- [ ] Telemetría de instalación
- [ ] A/B testing de instalador

---

## 🎓 Recursos Adicionales

### **Herramientas Recomendadas**
- **Diseño de íconos:** Figma, Adobe Illustrator, Inkscape
- **Conversión ICO:** IcoFX, GIMP
- **Generación imágenes:** Canva, Photoshop
- **Firma de código:** SignTool (Windows SDK)
- **Testing:** VirtualBox con Windows limpio

### **Referencias**
- [electron-builder NSIS docs](https://www.electron.build/configuration/nsis)
- [NSIS Documentation](https://nsis.sourceforge.io/Docs/)
- [Modern UI Reference](https://nsis.sourceforge.io/Docs/Modern%20UI%202/Readme.html)
- [Code Signing Guide](https://www.electron.build/code-signing)

### **Ejemplos de instaladores profesionales**
- Slack (excelente UX)
- Discord (splash screen animado)
- VS Code (minimalista pero efectivo)
- Adobe Creative Cloud (altamente personalizado)

---

## 💡 Consejos Profesionales

1. **Prueba en máquinas limpias:** Usa VMs para verificar que el instalador funciona sin dependencias
2. **Mide el tamaño:** Instaladores >200MB pueden desanimar usuarios
3. **Velocidad importa:** Optimiza assets, usa compresión LZMA
4. **Feedback visual:** Los usuarios quieren ver progreso detallado
5. **Rollback automático:** Si falla la instalación, limpia todo
6. **Logs de instalación:** Guarda logs para debugging (`$INSTDIR\install.log`)
7. **Soporte offline:** El instalador debe funcionar sin internet
8. **Actualizaciones delta:** Para updates, solo descarga lo que cambió

---

## 🎯 Próximos Pasos

1. **Crear assets visuales** (prioridad alta)
2. **Implementar configuración básica mejorada**
3. **Probar en Windows limpio**
4. **Iterar basado en feedback**
5. **Considerar firma de código para producción**

---

**¿Necesitas ayuda con algún paso específico?**
- Puedo generar los assets visuales (íconos, banners)
- Puedo crear los scripts NSIS personalizados
- Puedo configurar el package.json completo

¡Vamos a hacer que tu instalador se vea tan profesional como el de las grandes empresas! 🚀
