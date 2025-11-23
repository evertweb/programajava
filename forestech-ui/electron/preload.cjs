/**
 * Electron Preload Script
 * Exposes safe APIs to the renderer process
 */

const { contextBridge, ipcRenderer } = require('electron');

// Expose protected methods that allow the renderer process to use
// the Electron APIs in a safe and controlled way
contextBridge.exposeInMainWorld('electronAPI', {
  platform: process.platform,
  version: process.versions.electron,

  // App version from package.json
  getAppVersion: () => ipcRenderer.invoke('get-app-version'),

  // Auto-update methods
  checkForUpdates: () => ipcRenderer.invoke('check-for-updates'),

  // Listen for update events from main process
  onUpdateAvailable: (callback) => {
    ipcRenderer.on('update-available', (_event, info) => callback(info));
  },
  onUpdateNotAvailable: (callback) => {
    ipcRenderer.on('update-not-available', () => callback());
  },
  onUpdateDownloaded: (callback) => {
    ipcRenderer.on('update-downloaded', (_event, info) => callback(info));
  },
  onUpdateError: (callback) => {
    ipcRenderer.on('update-error', (_event, error) => callback(error));
  },
  onDownloadProgress: (callback) => {
    ipcRenderer.on('download-progress', (_event, progress) => callback(progress));
  },

  // Remove listeners
  removeUpdateListeners: () => {
    ipcRenderer.removeAllListeners('update-available');
    ipcRenderer.removeAllListeners('update-not-available');
    ipcRenderer.removeAllListeners('update-downloaded');
    ipcRenderer.removeAllListeners('update-error');
    ipcRenderer.removeAllListeners('download-progress');
  }
});
