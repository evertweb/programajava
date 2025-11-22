# Guía de Desarrollo y Generación de Ejecutable (.exe)

Esta guía describe el flujo de trabajo recomendado para desarrollar nuevas funcionalidades en `forestech-ui` y generar el instalador/ejecutable para Windows.

## 1. Flujo de Desarrollo (Día a día)

Cuando estés implementando nuevas funcionalidades o arreglando bugs:

1.  **Iniciar el entorno de desarrollo:**
    Esto abrirá la aplicación en modo desarrollo con recarga en caliente (hot-reload).
    ```bash
    cd forestech-ui
    npm run electron:dev
    ```

2.  **Implementar cambios:**
    *   Modifica los archivos en `src/` (componentes React, lógica, estilos).
    *   Modifica los archivos en `electron/` si necesitas cambiar el comportamiento de la ventana o comunicación con el sistema operativo.
    *   La aplicación se recargará automáticamente para reflejar tus cambios.

## 2. Generación del Ejecutable (.exe)

Una vez que hayas terminado tus cambios y probado que todo funciona correctamente, sigue estos pasos para generar la versión final para distribución.

### Requisitos Previos
*   Si estás en **Windows**: No necesitas nada extra.
*   Si estás en **Linux/Mac**: Para generar un `.exe` de Windows, necesitas tener instalado **Wine** o usar Docker. Si el comando falla, lo más recomendable es ejecutar el build desde una máquina Windows.

### Pasos para generar el Build

1.  **Ejecutar el script de lanzamiento:**
    Este comando automáticamente:
    *   Actualiza la versión en `package.json` con la fecha actual (ej. `0.0.1-25-11-21`).
    *   Compila el código de React (Vite build).
    *   Empaqueta la aplicación con Electron Builder.
    
    ```bash
    cd forestech-ui
    npm run build:release
    ```

2.  **Ubicar el archivo .exe:**
    Al finalizar exitosamente, encontrarás el ejecutable en:
    `forestech-ui/release/`

    Busca un archivo con extensión `.exe` (por ejemplo `ForestechOil Setup 0.0.1-25-11-21.exe` o similar, dependiendo de la configuración portable/instalador).

## 3. Resumen del Ciclo

1.  `npm run electron:dev` -> **Programar y Probar**
2.  ¿Todo listo? -> **Sí**
3.  `npm run build:release` -> **Generar .exe**
4.  Distribuir el archivo generado en la carpeta `release`.