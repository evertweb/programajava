# ForestechOil - Modern Web UI

Sistema de gestión de combustibles y flota vehicular construido con React + TypeScript + Material-UI.

## 🚀 Características

- ✅ **React 18 + TypeScript** - Tipado fuerte y componentes modernos
- ✅ **Material-UI (MUI)** - Componentes profesionales y responsivos
- ✅ **DataGrid** - Tablas avanzadas con filtrado, paginación y ordenamiento
- ✅ **Axios** - Cliente HTTP para consumir microservicios
- ✅ **Arquitectura modular** - Separación de servicios, tipos y componentes
- ✅ **Hot Module Replacement (HMR)** - Cambios en vivo durante desarrollo

## 📦 Módulos Implementados

### ✅ Productos (Completo)
- Ver listado de productos con DataGrid
- Crear nuevo producto
- Editar producto existente
- Eliminar producto
- Validaciones de formulario
- Notificaciones (Snackbar)

### 🚧 Próximamente
- Vehículos
- Movimientos de inventario
- Facturas
- Proveedores
- Dashboard con métricas

## 🛠️ Requisitos

- Node.js 18+
- npm 9+
- Microservicios corriendo:
  - catalog-service (puerto 8081)
  - fleet-service (puerto 8082)
  - inventory-service (puerto 8083)
  - partners-service (puerto 8084)
  - invoicing-service (puerto 8085)

## 🏃 Ejecutar en Desarrollo

```bash
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev

# La aplicación estará disponible en http://localhost:5173
```

## 🏗️ Build para Producción

```bash
# Generar build optimizado
npm run build

# Previsualizar build de producción
npm run preview
```

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend updating the configuration to enable type-aware lint rules:

```js
export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...

      // Remove tseslint.configs.recommended and replace with this
      tseslint.configs.recommendedTypeChecked,
      // Alternatively, use this for stricter rules
      tseslint.configs.strictTypeChecked,
      // Optionally, add this for stylistic rules
      tseslint.configs.stylisticTypeChecked,

      // Other configs...
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```

You can also install [eslint-plugin-react-x](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-x) and [eslint-plugin-react-dom](https://github.com/Rel1cx/eslint-react/tree/main/packages/plugins/eslint-plugin-react-dom) for React-specific lint rules:

```js
// eslint.config.js
import reactX from 'eslint-plugin-react-x'
import reactDom from 'eslint-plugin-react-dom'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      // Other configs...
      // Enable lint rules for React
      reactX.configs['recommended-typescript'],
      // Enable lint rules for React DOM
      reactDom.configs.recommended,
    ],
    languageOptions: {
      parserOptions: {
        project: ['./tsconfig.node.json', './tsconfig.app.json'],
        tsconfigRootDir: import.meta.dirname,
      },
      // other options...
    },
  },
])
```
