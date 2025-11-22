# Arquitectura Actualizada – Forestech CLI

## 1. Capas Principales

1. **core/**
   - Aloja `Main`, `AppOrchestrator`, `AppController` y `AppConfig`.
   - `Main` es el único punto de entrada y delega toda la decisión a `AppOrchestrator`.
   - `AppOrchestrator` decide si se levanta la CLI (`AppController.iniciar()`) o la GUI (`ForestechProfessionalApp`) según los argumentos.
   - `AppController` mantiene el flujo de la CLI (menús, interacciones con servicios).

2. **business/**
   - Aquí vive la lógica de dominio.
   - `controllers/` encapsulan casos de uso de alto nivel y coordinan servicios.
   - `services/` implementan reglas de negocio y orquestan los DAO.
   - `services/interfaces/` define contratos que facilitan pruebas y sustitución de implementaciones.
   - `helpers/` agrupa utilidades específicas para la capa de negocio (por ejemplo, helpers de menús o cálculos de movimientos).
   - `managers/` queda disponible para futuros coordinadores de estado en memoria (actualmente vacío tras movidas).

3. **data/**
   - `dao/` contiene los objetos de acceso a datos (CRUD y consultas especializadas).
   - `models/` contiene entidades y los `builders/` asociados que simplifican la creación de objetos complejos.
   - La capa está totalmente aislada del resto mediante interfaces de servicio.

4. **presentation/**
   - Reúne toda la UI bajo `presentation/ui/`.
   - Subcarpetas por feature (`movements`, `products`, `vehicles`, `suppliers`, `dashboard`, `logs`, `invoices`).
   - Los formularios (DialogForm) comparten subcarpeta con sus paneles para mantener cohesión.
   - `core/ServiceFactoryProvider` inyecta dependencias hacia la UI, evitando acoplamiento directo con `ServiceFactory`.
   - Se preservaron demos y prototipos (p. ej. `ButtonExampleApp`) como materiales de laboratorio dentro del mismo paquete para evitar perderlos.

5. **config/**
   - Centraliza clases de configuración (`ConfigLoader`, `DatabaseConnection`, `HikariCPDataSource`).
   - Aunque `DatabaseConnection` está deprecado, se mantiene para compatibilidad hasta que toda la capa de datos use HikariCP al 100%.

6. **shared/**
   - Repositorio de elementos transversales.
   - `enums/` (MeasurementUnit, MovementType, VehicleCategory) usados por modelos, servicios y UI.
   - `exceptions/` define excepciones de negocio reutilizables.
   - `utils/IdGenerator` y `validators/*` se consumen desde varias capas.

## 2. Flujo de Ejecución

1. **Arranque**
   - `Main` lee argumentos y usa `AppOrchestrator`.
   - CLI: `AppOrchestrator.startCLI()` crea `AppController`, este construye los controladores a través de `ServiceFactory`.
   - GUI: `AppOrchestrator.startGUI()` ejecuta `ForestechProfessionalApp` via `SwingUtilities` y obtiene servicios por `ServiceFactoryProvider`.

2. **Caso de Uso (ej. registrar movimiento)**
   - CLI/UI invoca `MovementController` → `MovementServices`.
   - `MovementServices` valida con `MovementValidator`, usa `MovementDAO` para persistir y `IdGenerator` para generar claves.
   - Excepciones del DAO se convierten en `DatabaseException` o equivalentes dentro de la capa de negocio.

3. **Datos y Configuración**
   - `HikariCPDataSource` proporciona conexiones a DAO.
   - Archivos de propiedades residen en `src/main/resources/` y se cargan mediante `ConfigLoader`/`AppConfig` según corresponda.

## 3. Beneficios Clave

- **Lectura Top-Down** clara: `core` → `business` → `data` → `presentation` → `shared`.
- **Consistencia de paquetes**: todas las clases comparten prefijo `com.forestech.<capa>`.
- **Cero lógica alterada**: solo se movieron archivos y actualizaron imports; pruebas válidas confirman el comportamiento.
- **Preparado para documentación**: `TREE.md` y este archivo permiten a nuevos desarrolladores entender estructura en segundos.
- **Facilita refactors futuros**: `AppOrchestrator` actúa como punto único para habilitar CLI/GUI, y `shared/` separa dependencias comunes.

## 4. Próximos pasos recomendados

1. Migrar gradualmente `DatabaseConnection` hacia el pool de `HikariCP` y deprecate la API antigua.
2. Extraer los demos legacy de UI en módulos de ejemplo o eliminar los que ya no se necesiten.
3. Implementar tests de integración para `FacturaServices`, ahora que la división de capas está más clara.
