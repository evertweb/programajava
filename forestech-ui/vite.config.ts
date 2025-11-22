import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base: './', // Important for Electron
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-react': ['react', 'react-dom'],
          'vendor-mui': ['@mui/material', '@mui/icons-material'],
          'vendor-grid': ['@mui/x-data-grid'],
          'vendor-date': ['@mui/x-date-pickers', 'date-fns'],
          'vendor-motion': ['framer-motion'],
        },
      },
    },
    chunkSizeWarningLimit: 1000,
    minify: 'esbuild', // Use esbuild for faster minification
    sourcemap: false,
  },
  esbuild: {
    drop: ['console', 'debugger'], // Drop console logs in production
  },
  optimizeDeps: {
    include: ['axios', 'date-fns', 'framer-motion'],
  },
  server: {
    port: 5173,
    strictPort: false,
  },
})
