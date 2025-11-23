; ===============================================
; ForestechOil - Textos de Instalador en Español
; ===============================================
; Este archivo personaliza todos los mensajes del instalador NSIS
; para que se vean profesionales y en español

; ============= PÁGINA DE BIENVENIDA =============
!define MUI_WELCOMEPAGE_TITLE "Bienvenido a ForestechOil Setup ${VERSION}"
!define MUI_WELCOMEPAGE_TEXT "Este asistente le guiará a través de la instalación de ForestechOil.$\r$\n$\r$\nForestechOil es un sistema completo de gestión de combustibles y flota vehicular diseñado para optimizar sus operaciones.$\r$\n$\r$\nSe recomienda cerrar todas las demás aplicaciones antes de continuar. Esto permitirá actualizar archivos del sistema sin necesidad de reiniciar el equipo.$\r$\n$\r$\nHaga clic en Siguiente para continuar."

; ============= PÁGINA DE LICENCIA =============
!define MUI_LICENSEPAGE_TEXT_TOP "Por favor revise los términos de licencia antes de instalar ForestechOil."
!define MUI_LICENSEPAGE_TEXT_BOTTOM "Si acepta todos los términos del acuerdo, haga clic en 'Acepto' para continuar. Debe aceptar el acuerdo para instalar ForestechOil."
!define MUI_LICENSEPAGE_BUTTON "&Acepto"

; ============= PÁGINA DE COMPONENTES =============
!define MUI_COMPONENTSPAGE_TITLE "Selección de Componentes"
!define MUI_COMPONENTSPAGE_SUBTITLE "Elija qué características de ForestechOil desea instalar."
!define MUI_COMPONENTSPAGE_TEXT_TOP "Seleccione los componentes que desea instalar y deseleccione los componentes que no desea instalar. Haga clic en Siguiente para continuar."
!define MUI_COMPONENTSPAGE_TEXT_COMPLIST "Componentes a instalar:"

; ============= PÁGINA DE DIRECTORIO =============
!define MUI_DIRECTORYPAGE_TEXT_TOP "El instalador instalará ForestechOil en la siguiente carpeta.$\r$\n$\r$\nPara instalar en una carpeta diferente, haga clic en Examinar y seleccione otra carpeta. Haga clic en Instalar para comenzar la instalación."
!define MUI_DIRECTORYPAGE_TEXT_DESTINATION "Carpeta de destino"
!define MUI_DIRECTORYPAGE_TITLE "Elegir ubicación de instalación"
!define MUI_DIRECTORYPAGE_SUBTITLE "Elija la carpeta donde se instalará ForestechOil."

; ============= PÁGINA DE INSTALACIÓN =============
!define MUI_INSTFILESPAGE_TITLE "Instalando ForestechOil"
!define MUI_INSTFILESPAGE_SUBTITLE "Por favor espere mientras ForestechOil se instala en su equipo."
!define MUI_INSTFILESPAGE_FINISHHEADER_TEXT "Instalación Completada"
!define MUI_INSTFILESPAGE_FINISHHEADER_SUBTEXT "La instalación se completó exitosamente."
!define MUI_INSTFILESPAGE_ABORTHEADER_TEXT "Instalación Cancelada"
!define MUI_INSTFILESPAGE_ABORTHEADER_SUBTEXT "La instalación no se completó exitosamente."

; ============= PÁGINA DE FINALIZACIÓN =============
!define MUI_FINISHPAGE_TITLE "Instalación de ForestechOil Completada"
!define MUI_FINISHPAGE_TEXT "ForestechOil se ha instalado correctamente en su equipo.$\r$\n$\r$\n¡Gracias por elegir ForestechOil para gestionar su flota vehicular!$\r$\n$\r$\nHaga clic en Finalizar para cerrar este asistente."
!define MUI_FINISHPAGE_RUN_TEXT "Ejecutar ForestechOil ahora"
!define MUI_FINISHPAGE_SHOWREADME_TEXT "Mostrar información de la versión"
!define MUI_FINISHPAGE_LINK "Visitar sitio web de Forestech"
!define MUI_FINISHPAGE_LINK_LOCATION "https://forestech.com"

; ============= TEXTOS DE BOTONES =============
!define MUI_BUTTONTEXT_FINISH "&Finalizar"
!define MUI_BUTTONTEXT_NEXT "&Siguiente >"
!define MUI_BUTTONTEXT_BACK "< &Atrás"
!define MUI_BUTTONTEXT_CANCEL "&Cancelar"

; ============= DESINSTALADOR =============
!define MUI_UNCONFIRMPAGE_TEXT_TOP "ForestechOil será desinstalado de su equipo. Haga clic en Desinstalar para comenzar la desinstalación."
!define MUI_UNFINISHPAGE_TITLE "Desinstalación Completada"
!define MUI_UNFINISHPAGE_TEXT "ForestechOil se ha desinstalado correctamente de su equipo."

; ============= MENSAJES DE ERROR =============
!define MUI_TEXT_INSTALLING_TITLE "Instalando"
!define MUI_TEXT_INSTALLING_SUBTITLE "Por favor espere mientras se instala ForestechOil..."
!define MUI_TEXT_FINISH_TITLE "Instalación completa"
!define MUI_TEXT_FINISH_SUBTITLE "La instalación se completó exitosamente."
!define MUI_TEXT_ABORT_TITLE "Instalación cancelada"
!define MUI_TEXT_ABORT_SUBTITLE "La instalación no se completó exitosamente."

; ============= DESCRIPCIONES DE COMPONENTES =============
LangString DESC_SecMain ${LANG_SPANISH} "Archivos principales de ForestechOil (requerido)"
LangString DESC_SecDesktop ${LANG_SPANISH} "Crear acceso directo en el escritorio"
LangString DESC_SecStartMenu ${LANG_SPANISH} "Crear acceso directo en el menú inicio"
LangString DESC_SecQuickLaunch ${LANG_SPANISH} "Crear acceso directo en la barra de tareas"

; ============= MENSAJES PERSONALIZADOS =============
LangString WelcomeTitle ${LANG_SPANISH} "Sistema de Gestión de Combustibles"
LangString InstallSuccess ${LANG_SPANISH} "¡ForestechOil se instaló exitosamente!"
LangString UninstallSuccess ${LANG_SPANISH} "ForestechOil se desinstaló correctamente."
LangString AlreadyInstalled ${LANG_SPANISH} "ForestechOil ya está instalado. ¿Desea actualizar a la versión ${VERSION}?"
LangString RequiresAdmin ${LANG_SPANISH} "Este instalador requiere privilegios de administrador."
LangString InsufficientSpace ${LANG_SPANISH} "No hay suficiente espacio en disco. Se requieren al menos 500 MB."
