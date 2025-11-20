# Cambios de Imports tras la Reorganización

La refactorización movió clases a nuevos paquetes. Estos fueron los reemplazos sistemáticos aplicados en el código fuente y en las pruebas:

| Antes                            | Después                                      | Comentario |
|----------------------------------|----------------------------------------------|------------|
| `com.forestech.Main`             | `com.forestech.core.Main`                    | Nuevo punto de entrada en `core/`.
| `com.forestech.AppController`    | `com.forestech.core.AppController`           | Controlador CLI ahora en `core/`.
| `com.forestech.AppConfig`        | `com.forestech.core.AppConfig`               | Configuración global acompaña al núcleo.
| `com.forestech.controllers.*`    | `com.forestech.business.controllers.*`       | Controladores bajo la capa de negocio.
| `com.forestech.services.*`       | `com.forestech.business.services.*`          | Servicios + interfaces en `business/`.
| `com.forestech.dao.*`            | `com.forestech.data.dao.*`                   | DAO agrupados en `data/`.
| `com.forestech.models.*`         | `com.forestech.data.models.*`                | Entidades y builders bajo `data/models`.
| `com.forestech.enums.*`          | `com.forestech.shared.enums.*`               | Enumeraciones compartidas.
| `com.forestech.exceptions.*`     | `com.forestech.shared.exceptions.*`          | Excepciones reutilizables.
| `com.forestech.utils.*`          | `com.forestech.shared.utils.*`               | Utilidades comunes.
| `com.forestech.validators.*`     | `com.forestech.shared.validators.*`          | Validadores centralizados.
| `com.forestech.helpers.*`        | `com.forestech.business.helpers.*`           | Helpers consumidos por la capa CLI/servicios.
| `com.forestech.ui.*`             | `com.forestech.presentation.ui.*`            | Componentes Swing y formularios reubicados.
| `com.forestech.MovementCalculator` | `com.forestech.business.helpers.MovementCalculator` | Clase usada por servicios y CLI.
| `com.forestech.ServiceFactory`   | `com.forestech.business.services.ServiceFactory` | Factory único para instanciar servicios.
| `com.forestech.ForestechProfessionalApp` | `com.forestech.presentation.ui.ForestechProfessionalApp` | GUI principal referenciada desde `AppOrchestrator`.
| `com.forestech.validators.VehicleValidator` | `com.forestech.shared.validators.VehicleValidator` | Import corregido para evitar errores de compilación.

Todos los archivos `.java` y los tests en `src/test/java/com/forestech/services` se actualizaron siguiendo esta tabla para mantener coherencia con la nueva jerarquía.
