# Arquitectura UML - Forestech CLI

Este documento resume la arquitectura actualizada del proyecto tras la refactorización de inyección de dependencias aplicada a los formularios y paneles Swing. Incluye un diagrama Mermaid y notas sobre cómo se distribuyen las responsabilidades.

## 1. Diagrama de Capas (Mermaid)

```mermaid
graph TD
    subgraph UI [Capa de Presentación (Swing)]
        MP[MovementsPanel]
        PP[ProductsPanel]
        VP[VehiclesPanel]
        MDF[MovementDialogForm]
        PDF[ProductDialogForm]
        VDF[VehicleDialogForm]
    end

    subgraph Services [Capa de Servicios]
        MS[MovementServices]
        PS[ProductServices]
        VS[VehicleServices]
        SS[SupplierServices]
        FS[FacturaServices]
        SF[ServiceFactory]
    end

    subgraph DAO [Capa DAO]
        MDAO[MovementDAO]
        PDAO[ProductDAO]
        VDAO[VehicleDAO]
        SDAO[SupplierDAO]
        FDAO[FacturaDAO]
    end

    subgraph DB [Base de Datos MySQL]
        TB1[oil_products]
        TB2[vehicles]
        TB3[suppliers]
        TB4[facturas]
        TB5[detalle_factura]
        TB6[Movement]
    end

    MP -->|ctor injection| MS
    MP --> PS
    MP --> VS
    MP --> FS

    PP --> PS
    VP --> VS
    MDF --> MS
    MDF --> PS
    MDF --> VS
    MDF --> FS
    PDF --> PS
    VDF --> VS
    VDF --> PS

    SF --> MS
    SF --> PS
    SF --> VS
    SF --> SS
    SF --> FS

    MS --> MDAO
    PS --> PDAO
    VS --> VDAO
    SS --> SDAO
    FS --> FDAO

    MDAO --> TB6
    PDAO --> TB1
    VDAO --> TB2
    SDAO --> TB3
    FDAO --> TB4
    FDAO --> TB5
```

## 2. Explicación de la Arquitectura

1. **Presentación (Swing):** Panels y Dialogs reciben sus dependencias mediante constructores para evitar llamadas directas a `getInstance()`. Esto permite pruebas unitarias más sencillas y desacoplamiento.
2. **Service Layer:** Los `Services` siguen siendo Singletons gestionados por `ServiceFactory`, pero ya no son resueltos dentro de los componentes UI. Cada Service encapsula la lógica de negocio (validaciones de stock, FK, cálculos de totales, etc.).
3. **DAO Layer:** Cada Service delega el acceso a datos en un DAO dedicado que usa JDBC y PreparedStatements. Se centraliza la configuración de conexión en `DatabaseConnectionFactory`.
4. **Base de Datos:** El esquema `FORESTECHOIL` contiene tablas para productos, vehículos, proveedores, facturas, detalle de factura y movimientos. Se respetan integridades referenciales mediante claves foráneas (RESTRICT, SET NULL, CASCADE).
5. **Flujo de Dependencias:** Se cumple el principio DIP porque las capas superiores dependen de abstracciones (`IProductService`, `IMovementDAO`, etc.). La DI en la GUI evita que la presentación conozca los detalles de construcción de los Services.

## 3. Checklist de Refactorización

- [x] Dialogs Swing usan constructor injection (`ProductDialogForm`, `VehicleDialogForm`, `MovementDialogForm`).
- [x] Panels y Managers reciben Services desde `ForestechProfessionalApp` y `ServiceFactory`.
- [x] `MovementsPanel` ahora depende también de `FacturaServices` para validar facturas asociadas.
- [x] Se añadió documentación extensa a `ServiceFactory` describiendo la intención del patrón.
- [x] Queda prohibido llamar a `ServiceFactory.getInstance()` desde los formularios/Dialogs.

## 4. Próximos Pasos

1. Mantener este documento sincronizado cada vez que se agreguen nuevas dependencias o paneles.
2. Evaluar migrar a un contenedor DI ligero (p.ej. Dagger) cuando el estudiante domine las bases.
3. Considerar diagramas adicionales (secuencia o casos de uso) para describir operaciones complejas como registro de facturas o movimientos multi-tablas.
