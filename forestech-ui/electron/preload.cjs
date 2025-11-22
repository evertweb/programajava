/**
 * Electron Preload Script
 * Exposes safe APIs to the renderer process
 */

const { contextBridge } = require('electron');

// Expose protected methods that allow the renderer process to use
// the Electron APIs in a safe and controlled way
contextBridge.exposeInMainWorld('electronAPI', {
  platform: process.platform,
  version: process.versions.electron,
});
