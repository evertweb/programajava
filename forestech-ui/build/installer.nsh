; ===============================================
; ForestechOil - Script NSIS Personalizado
; ===============================================
; Este script añade funcionalidad avanzada al instalador

; ============= VARIABLES GLOBALES =============
Var StartMenuFolder
Var PreviousVersion
Var IsUpgrade

; ============= VERIFICACIÓN DE REQUISITOS DEL SISTEMA =============
!macro customInit
  ; Verificar versión de Windows (Windows 7 o superior)
  ${If} ${AtMostWinVista}
    MessageBox MB_OK|MB_ICONEXCLAMATION "ForestechOil requiere Windows 7 o superior.$\r$\n$\r$\nSu sistema operativo no es compatible."
    Quit
  ${EndIf}
  
  ; Verificar espacio en disco (mínimo 500 MB)
  ${GetRoot} "$INSTDIR" $R0
  ${DriveSpace} "$R0\" "/D=F /S=M" $R1
  ${If} $R1 < 500
    MessageBox MB_OK|MB_ICONEXCLAMATION "No hay suficiente espacio en disco.$\r$\n$\r$\nSe requieren al menos 500 MB libres.$\r$\nEspacio disponible: $R1 MB"
    Quit
  ${EndIf}
  
  ; Verificar si ya está instalado y obtener versión
  ReadRegStr $PreviousVersion HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "DisplayVersion"
  ${If} $PreviousVersion != ""
    StrCpy $IsUpgrade "1"
    MessageBox MB_YESNO|MB_ICONQUESTION \
      "ForestechOil $PreviousVersion ya está instalado.$\r$\n$\r$\n¿Desea actualizar a la versión ${VERSION}?$\r$\n$\r$\nNota: Sus datos y configuraciones se conservarán." \
      IDYES upgrade
      Abort
    
    upgrade:
      DetailPrint "Detectada versión anterior: $PreviousVersion"
      DetailPrint "Preparando actualización a ${VERSION}..."
  ${Else}
    StrCpy $IsUpgrade "0"
  ${EndIf}
!macroend

; ============= INSTALACIÓN PERSONALIZADA =============
!macro customInstall
  DetailPrint "========================================="
  DetailPrint "Instalando ForestechOil ${VERSION}"
  DetailPrint "========================================="
  
  ; Crear entradas del registro
  DetailPrint "Registrando aplicación en el sistema..."
  WriteRegStr HKLM "Software\Forestech\ForestechOil" "InstallPath" "$INSTDIR"
  WriteRegStr HKLM "Software\Forestech\ForestechOil" "Version" "${VERSION}"
  WriteRegStr HKLM "Software\Forestech\ForestechOil" "InstallDate" "$YEAR-$MONTH-$DAY"
  
  ; Crear archivo de configuración inicial
  ${If} $IsUpgrade == "0"
    DetailPrint "Creando configuración inicial..."
    FileOpen $0 "$INSTDIR\config.json" w
    FileWrite $0 '{'
    FileWrite $0 '  "firstRun": true,$\r$\n'
    FileWrite $0 '  "version": "${VERSION}",$\r$\n'
    FileWrite $0 '  "installedDate": "$YEAR-$MONTH-$DAY",$\r$\n'
    FileWrite $0 '  "language": "es"$\r$\n'
    FileWrite $0 '}'
    FileClose $0
  ${Else}
    DetailPrint "Actualizando configuración existente..."
  ${EndIf}
  
  ; Crear accesos directos con descripciones
  DetailPrint "Creando accesos directos..."
  
  ; Acceso directo del escritorio
  ${If} ${FileExists} "$DESKTOP\ForestechOil.lnk"
    DetailPrint "Acceso directo del escritorio ya existe, actualizando..."
  ${EndIf}
  CreateShortCut "$DESKTOP\ForestechOil.lnk" \
    "$INSTDIR\ForestechOil.exe" \
    "" \
    "$INSTDIR\ForestechOil.exe" 0 \
    SW_SHOWNORMAL \
    "" \
    "Sistema de Gestión de Combustibles y Flota Vehicular"
  
  ; Acceso directo del menú inicio
  CreateDirectory "$SMPROGRAMS\Forestech"
  CreateShortCut "$SMPROGRAMS\Forestech\ForestechOil.lnk" \
    "$INSTDIR\ForestechOil.exe" \
    "" \
    "$INSTDIR\ForestechOil.exe" 0 \
    SW_SHOWNORMAL \
    "" \
    "Sistema de Gestión de Combustibles y Flota Vehicular"
  
  ; Acceso directo al desinstalador
  CreateShortCut "$SMPROGRAMS\Forestech\Desinstalar ForestechOil.lnk" \
    "$INSTDIR\Uninstall ForestechOil.exe"
  
  ; Escribir información en el registro de Windows para "Programas y características"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "DisplayName" "ForestechOil - Sistema de Gestión de Combustibles"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "Publisher" "Forestech"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "DisplayIcon" "$INSTDIR\ForestechOil.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "HelpLink" "https://forestech.com/soporte"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "URLInfoAbout" "https://forestech.com"
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_GUID}" "NoRepair" 1
  
  DetailPrint "========================================="
  DetailPrint "Instalación completada exitosamente"
  DetailPrint "========================================="
  
  ; Mensaje de bienvenida final
  ${If} $IsUpgrade == "0"
    DetailPrint ""
    DetailPrint "¡Bienvenido a ForestechOil!"
    DetailPrint "Puede ejecutar la aplicación desde el escritorio o menú inicio."
  ${Else}
    DetailPrint ""
    DetailPrint "¡Actualización completada!"
    DetailPrint "ForestechOil ha sido actualizado de $PreviousVersion a ${VERSION}."
  ${EndIf}
!macroend

; ============= DESINSTALACIÓN PERSONALIZADA =============
!macro customUnInstall
  DetailPrint "========================================="
  DetailPrint "Desinstalando ForestechOil"
  DetailPrint "========================================="
  
  ; Cerrar la aplicación si está en ejecución
  DetailPrint "Verificando si ForestechOil está en ejecución..."
  nsExec::Exec 'taskkill /F /IM ForestechOil.exe'
  Sleep 1000
  
  ; Limpiar registro
  DetailPrint "Limpiando entradas del registro..."
  DeleteRegKey HKLM "Software\Forestech\ForestechOil"
  
  ; Eliminar accesos directos
  DetailPrint "Eliminando accesos directos..."
  Delete "$DESKTOP\ForestechOil.lnk"
  Delete "$SMPROGRAMS\Forestech\ForestechOil.lnk"
  Delete "$SMPROGRAMS\Forestech\Desinstalar ForestechOil.lnk"
  RMDir "$SMPROGRAMS\Forestech"
  
  ; Limpiar barra de tareas (Windows 7+)
  Delete "$APPDATA\Microsoft\Internet Explorer\Quick Launch\User Pinned\TaskBar\ForestechOil.lnk"
  
  ; Preguntar si borrar datos de usuario
  MessageBox MB_YESNO|MB_ICONQUESTION \
    "¿Desea eliminar también los datos y configuraciones de la aplicación?$\r$\n$\r$\nEsto incluye bases de datos locales y preferencias.$\r$\n$\r$\nSeleccione 'No' si planea reinstalar ForestechOil más tarde." \
    IDNO skip_appdata
    
    DetailPrint "Eliminando datos de usuario..."
    RMDir /r "$APPDATA\ForestechOil"
    RMDir /r "$LOCALAPPDATA\ForestechOil"
    Delete "$INSTDIR\config.json"
    
  skip_appdata:
  
  DetailPrint "========================================="
  DetailPrint "Desinstalación completada"
  DetailPrint "========================================="
!macroend

; ============= FUNCIÓN DE PÁGINA PERSONALIZADA =============
!macro customWelcomePage
  ; Esta macro se puede usar para añadir páginas personalizadas
  ; Por ahora, dejamos el comportamiento por defecto
!macroend

; ============= COMPROBACIÓN DE ARQUITECTURA =============
!macro customInstallMode
  ; Verificar si es sistema de 64 bits
  ${If} ${RunningX64}
    StrCpy $INSTDIR "$PROGRAMFILES64\ForestechOil"
  ${Else}
    StrCpy $INSTDIR "$PROGRAMFILES\ForestechOil"
  ${EndIf}
!macroend

; ============= LIMPIEZA DE INSTALACIÓN FALLIDA =============
!macro customInstallError
  DetailPrint "Error durante la instalación."
  DetailPrint "Limpiando archivos parciales..."
  
  ; Intentar limpiar si falla la instalación
  RMDir /r "$INSTDIR"
  DeleteRegKey HKLM "Software\Forestech\ForestechOil"
  
  MessageBox MB_OK|MB_ICONEXCLAMATION \
    "La instalación no se completó correctamente.$\r$\n$\r$\nPor favor contacte al soporte técnico si el problema persiste.$\r$\n$\r$\nsoporte@forestech.com"
!macroend
