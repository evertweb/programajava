# Roadmap Maestro de Migración: ForestechOil (Electron -> Flutter)

> **Documento Vivo de Ejecución**
> Este archivo contiene no solo el plan, sino las **instrucciones explícitas para Agentes de IA**.
> Cuando se le pida al Agente "Ejecuta la Fase X", debe leer la sección correspondiente y seguir estrictamente los lineamientos de arquitectura y contexto definidos aquí.

---

## 🤖 Instrucciones Maestras para el Agente

### 1. Contexto del Espacio de Trabajo
- **Origen (Legacy):** `forestech-ui/` (React + TypeScript + Electron).
- **Destino (Nuevo):** `forestech_app/` (Flutter + Dart).
- **Backend:** `forestech-microservices/` (Java Spring Boot). **NO TOCAR**, pero usar como referencia para endpoints.

### 2. Filosofía de Migración: "Logic-First"
No estamos "rediseñando", estamos **portando**.
- Si en React existe `productService.ts`, en Flutter debe existir `ProductRepository`.
- Si en React existe `Product.ts` con 5 campos, en Flutter debe existir `ProductModel` con los mismos 5 campos.
- **Prohibido inventar lógica nueva** a menos que sea estrictamente necesario por el cambio de plataforma.

### 3. Lineamientos de Arquitectura (Clean Architecture)
El Agente debe respetar estrictamente esta estructura de carpetas en `forestech_app/lib/`:

```text
lib/
├── core/                   # Código agnóstico al dominio
│   ├── config/             # Env vars, constantes
│   ├── errors/             # Failures, Exceptions
│   ├── network/            # Dio client, Interceptors
│   └── utils/              # Validadores, Formatters
├── data/                   # Capa de Datos (Implementación)
│   ├── datasources/        # Llamadas HTTP crudas
│   ├── models/             # DTOs (fromJson/toJson)
│   └── repositories/       # Implementación de interfaces de dominio
├── domain/                 # Capa de Negocio (Pura Dart)
│   ├── entities/           # Objetos de negocio puros
│   └── repositories/       # Interfaces (contratos)
└── presentation/           # Capa Visual (Flutter)
    ├── providers/          # State Management (ChangeNotifier/Riverpod)
    ├── screens/            # Pantallas completas
    └── widgets/            # Componentes reutilizables
```

### 4. Reglas de Oro (Policies)
1.  **Null Safety:** Todo el código Dart debe ser Null Safe. Usar `required` y `?` correctamente.
2.  **Librerías Aprobadas:**
    *   HTTP: `dio`
    *   Rutas: `go_router`
    *   Estado: `provider` (Simple y efectivo para este tamaño)
    *   UI: `syncfusion_flutter_datagrid`, `syncfusion_flutter_charts` (Community License)
3.  **Manejo de Errores:** Nunca dejar `catch (e) { print(e); }`. Usar `Either<Failure, Type>` o excepciones personalizadas mapeadas.

---

## 📅 Roadmap de Ejecución

### ✅ Fase 1: Cimientos y Arquitectura (Sesiones 1-2)

> **Prompt para el Agente:** "Agente, ejecuta la Fase 1 del Roadmap de Migración. Crea el proyecto y establece la estructura base."

#### Tareas Detalladas:
1.  **Inicialización:**
    *   Ejecutar `flutter create --org com.forestech --platforms=windows,linux,android forestech_app`.
    *   Limpiar el `main.dart` por defecto.
2.  **Estructura de Directorios:**
    *   Crear físicamente las carpetas definidas en "Lineamientos de Arquitectura".
3.  **Dependencias:**
    *   Agregar a `pubspec.yaml`: `dio`, `provider`, `go_router`, `shared_preferences`, `intl`, `equatable`.
    *   Agregar dev_dependencies: `build_runner`, `json_serializable`, `json_annotation`.
4.  **Configuración Core:**
    *   Crear `core/config/constants.dart` con la URL base (`http://localhost:8080/api`).
    *   Crear `core/network/dio_client.dart` configurando Dio con Timeouts y BaseUrl.

---

### 🧠 Fase 2: Migración de Lógica de Negocio (Sesiones 3-5)

> **Prompt para el Agente:** "Agente, ejecuta la Fase 2. Migra los modelos y servicios de React a Repositorios de Flutter."

#### Tareas Detalladas:
1.  **Modelos (Data Layer):**
    *   Leer `forestech-ui/src/types/Product.ts`. -> Crear `data/models/product_model.dart`.
    *   Leer `forestech-ui/src/types/Vehicle.ts`. -> Crear `data/models/vehicle_model.dart`.
    *   Asegurar métodos `fromJson` y `toJson`.
2.  **Repositorios (Domain & Data):**
    *   Definir interfaz `domain/repositories/i_auth_repository.dart`.
    *   Implementar `data/repositories/auth_repository_impl.dart` usando `Dio`.
    *   Crear `GenericRepository<T>` para replicar la lógica de `createCrudService.ts`.
3.  **Providers (Presentation Layer - Logic):**
    *   Crear `AuthProvider` que use `AuthRepository`.
    *   Crear `BaseProvider` con estados `isLoading`, `error`, `data`.

---

### 🎨 Fase 3: UI Framework y Navegación (Sesiones 6-7)

> **Prompt para el Agente:** "Agente, ejecuta la Fase 3. Configura Syncfusion, el tema y la navegación."

#### Tareas Detalladas:
1.  **Syncfusion Setup:**
    *   Agregar paquetes `syncfusion_flutter_core`, `syncfusion_flutter_datagrid`.
    *   Registrar licencia en `main.dart`.
2.  **Theming:**
    *   Leer `forestech-ui/src/theme/theme.ts`.
    *   Crear `core/theme/app_theme.dart` replicando la paleta de colores (Primary, Secondary, Error).
3.  **Router:**
    *   Configurar `go_router` en `core/router/app_router.dart`.
    *   Definir rutas: `/login`, `/dashboard`, `/products`, etc.
4.  **Layout Shell:**
    *   Crear `presentation/widgets/main_layout.dart` con un `NavigationRail` (Sidebar) a la izquierda y un `AppBar` arriba.

---

### 📦 Fase 4: Migración Modular (Sesiones 8-12)

> **Prompt para el Agente:** "Agente, ejecuta la Fase 4, específicamente el módulo [NOMBRE_MODULO]."

#### Módulos:
*   **4.1 Catálogo:** Migrar `ProductsPanel.tsx` -> `ProductsScreen.dart` usando `SfDataGrid`.
*   **4.2 Flota:** Migrar `VehiclesPanel.tsx` -> `VehiclesScreen.dart`.
*   **4.3 Inventario:** Migrar `MovementsPanel.tsx` -> `MovementsScreen.dart`.
*   **4.4 Dashboard:** Migrar `Dashboard.tsx` -> `DashboardScreen.dart` con `SfCartesianChart`.
*   **4.5 Facturación:** Migrar `InvoicesPanel.tsx` -> `InvoicingScreen.dart`.

---

### 🚀 Fase 5: Producción Desktop (Sesión 13)

> **Prompt para el Agente:** "Agente, finaliza la migración preparando los ejecutables de escritorio para windows."

#### Tareas:
1.  Configurar iconos de aplicación (`flutter_launcher_icons`).
2.  Ajustar permisos de AndroidManifest y Linux (network).
3.  Generar builds de release.
al final explicame al usuario como es el nuevo proceso de build ya que antes se genraba exe pero con electron.
