/**
 * Electron Main Process
 * ForestechOil Desktop Application
 * With Auto-Update Support
 */

const { app, BrowserWindow, Menu, ipcMain, dialog } = require('electron');
const { autoUpdater } = require('electron-updater');
const path = require('path');

// Configure auto-updater
autoUpdater.autoDownload = false;
autoUpdater.autoInstallOnAppQuit = true;

// Disable hardware acceleration for better compatibility
app.disableHardwareAcceleration();

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1024,
    minHeight: 768,
    title: 'ForestechOil - Sistema de Gestión',
    icon: path.join(__dirname, '../public/icon.png'),
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      webSecurity: true,
      preload: path.join(__dirname, 'preload.cjs'),
    },
    backgroundColor: '#f5f5f5',
    show: false, // Don't show until ready
  });

  // Load the app
  const isDev = process.env.NODE_ENV === 'development';

  if (isDev) {
    // Development mode: load from Vite dev server
    const port = process.env.VITE_PORT || 5173;
    mainWindow.loadURL(`http://localhost:${port}`);

    // Open DevTools in development
    mainWindow.webContents.openDevTools();
  } else {
    // Production mode: load from built files
    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
  }

  // Show window when ready
  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
    mainWindow.focus();

    // Check for updates (only in production)
    const isDev = process.env.NODE_ENV === 'development';
    if (!isDev) {
      setTimeout(() => {
        console.log('Checking for updates...');
        autoUpdater.checkForUpdates();
      }, 3000);
    }
  });

  // Handle window close
  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  // Create application menu
  createMenu();
}

function createMenu() {
  const template = [
    {
      label: 'Archivo',
      submenu: [
        {
          label: 'Actualizar',
          accelerator: 'CmdOrCtrl+R',
          click: () => {
            if (mainWindow) mainWindow.reload();
          },
        },
        { type: 'separator' },
        {
          label: 'Salir',
          accelerator: 'CmdOrCtrl+Q',
          click: () => {
            app.quit();
          },
        },
      ],
    },
    {
      label: 'Edición',
      submenu: [
        { role: 'undo', label: 'Deshacer' },
        { role: 'redo', label: 'Rehacer' },
        { type: 'separator' },
        { role: 'cut', label: 'Cortar' },
        { role: 'copy', label: 'Copiar' },
        { role: 'paste', label: 'Pegar' },
        { role: 'selectAll', label: 'Seleccionar Todo' },
      ],
    },
    {
      label: 'Ver',
      submenu: [
        { role: 'reload', label: 'Recargar' },
        { role: 'toggleDevTools', label: 'Herramientas de Desarrollo' },
        { type: 'separator' },
        { role: 'resetZoom', label: 'Zoom Normal' },
        { role: 'zoomIn', label: 'Acercar' },
        { role: 'zoomOut', label: 'Alejar' },
        { type: 'separator' },
        { role: 'togglefullscreen', label: 'Pantalla Completa' },
      ],
    },
    {
      label: 'Ayuda',
      submenu: [
        {
          label: 'Acerca de ForestechOil',
          click: () => {
            // Show about dialog
            const { dialog } = require('electron');
            dialog.showMessageBox(mainWindow, {
              type: 'info',
              title: 'Acerca de ForestechOil',
              message: 'ForestechOil v1.0.0',
              detail: 'Sistema de Gestión de Combustibles y Flota Vehicular\n\nDesarrollado con React + Electron',
              buttons: ['OK'],
            });
          },
        },
      ],
    },
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

// ============================================
// Auto-Updater Event Handlers
// ============================================

autoUpdater.on('checking-for-update', () => {
  console.log('[AutoUpdater] Checking for updates...');
  console.log('[AutoUpdater] Current version:', app.getVersion());
  console.log('[AutoUpdater] Feed URL:', autoUpdater.getFeedURL());
});

autoUpdater.on('update-available', (info) => {
  console.log('[AutoUpdater] Update available:', info.version);
  console.log('[AutoUpdater] Release info:', JSON.stringify(info, null, 2));

  // Send event to renderer
  if (mainWindow) {
    mainWindow.webContents.send('update-available', info);
  }

  dialog.showMessageBox(mainWindow, {
    type: 'info',
    title: 'Actualización Disponible',
    message: `Hay una nueva versión disponible: ${info.version}`,
    detail: '¿Desea descargar e instalar la actualización ahora?',
    buttons: ['Descargar Ahora', 'Más Tarde'],
    defaultId: 0,
    cancelId: 1,
  }).then((result) => {
    if (result.response === 0) {
      autoUpdater.downloadUpdate();

      // Show download progress
      dialog.showMessageBox(mainWindow, {
        type: 'info',
        title: 'Descargando Actualización',
        message: 'La actualización se está descargando...',
        buttons: ['OK'],
      });
    }
  });
});

autoUpdater.on('update-not-available', (info) => {
  console.log('[AutoUpdater] No updates available');
  console.log('[AutoUpdater] Current is latest:', info?.version || 'unknown');
  // Send event to renderer
  if (mainWindow) {
    mainWindow.webContents.send('update-not-available');
  }
});

autoUpdater.on('download-progress', (progressObj) => {
  console.log(`Download speed: ${progressObj.bytesPerSecond}`);
  console.log(`Downloaded ${progressObj.percent}%`);
  console.log(`(${progressObj.transferred}/${progressObj.total})`);
  // Send progress to renderer
  if (mainWindow) {
    mainWindow.webContents.send('download-progress', progressObj);
  }
});

autoUpdater.on('update-downloaded', (info) => {
  console.log('Update downloaded:', info.version);
  // Send event to renderer
  if (mainWindow) {
    mainWindow.webContents.send('update-downloaded', info);
  }

  dialog.showMessageBox(mainWindow, {
    type: 'info',
    title: 'Actualización Lista',
    message: 'La actualización se ha descargado exitosamente.',
    detail: 'La aplicación se reiniciará para instalar la actualización.',
    buttons: ['Reiniciar Ahora', 'Más Tarde'],
    defaultId: 0,
    cancelId: 1,
  }).then((result) => {
    if (result.response === 0) {
      autoUpdater.quitAndInstall(false, true);
    }
  });
});

autoUpdater.on('error', (error) => {
  console.error('[AutoUpdater] Error:', error.message);
  console.error('[AutoUpdater] Error stack:', error.stack);

  // Send error to renderer for logging
  if (mainWindow) {
    mainWindow.webContents.send('update-error', error.message);
  }
});

// ============================================
// IPC Handlers
// ============================================

// Get app version
ipcMain.handle('get-app-version', () => {
  return app.getVersion();
});

// Manual update check from UI
ipcMain.handle('check-for-updates', async () => {
  console.log('[IPC] check-for-updates called');
  const isDev = process.env.NODE_ENV === 'development';
  console.log('[IPC] NODE_ENV:', process.env.NODE_ENV);
  console.log('[IPC] isDev:', isDev);

  if (!isDev) {
    try {
      console.log('[IPC] Calling autoUpdater.checkForUpdates()...');
      const result = await autoUpdater.checkForUpdates();
      console.log('[IPC] checkForUpdates result:', result?.updateInfo?.version || 'no info');
      return { checking: true, result: result?.updateInfo };
    } catch (error) {
      console.error('[IPC] Error checking for updates:', error.message);
      if (mainWindow) {
        mainWindow.webContents.send('update-error', error.message);
      }
      return { checking: false, error: error.message };
    }
  }
  console.log('[IPC] Skipping update check (dev mode)');
  return { checking: false, isDev: true };
});

// App lifecycle events
app.whenReady().then(createWindow);

app.on('window-all-closed', () => {
  // On macOS, keep app running until user explicitly quits
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  // On macOS, re-create window when dock icon is clicked
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});

// Handle uncaught exceptions
process.on('uncaughtException', (error) => {
  console.error('Uncaught Exception:', error);
});

process.on('unhandledRejection', (error) => {
  console.error('Unhandled Rejection:', error);
});
