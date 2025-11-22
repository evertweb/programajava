# Forestech CLI – Árbol de Paquetes (post-refactor)

```text
src/
├── main/
│   ├── java/com/forestech/
│   │   ├── core/
│   │   │   ├── AppConfig.java
│   │   │   ├── AppController.java
│   │   │   ├── AppOrchestrator.java
│   │   │   └── Main.java
│   │   ├── business/
│   │   │   ├── controllers/
│   │   │   │   ├── MovementController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── SupplierController.java
│   │   │   │   └── VehicleController.java
│   │   │   ├── helpers/
│   │   │   │   ├── BannerMenu.java
│   │   │   │   ├── DataDisplay.java
│   │   │   │   ├── InputHelper.java
│   │   │   │   ├── MenuHelper.java
│   │   │   │   └── MovementCalculator.java
│   │   │   ├── managers/
│   │   │   └── services/
│   │   │       ├── FacturaServices.java
│   │   │       ├── MovementServices.java
│   │   │       ├── ProductServices.java
│   │   │       ├── ServiceFactory.java
│   │   │       ├── SupplierServices.java
│   │   │       ├── VehicleServices.java
│   │   │       └── interfaces/
│   │   │           ├── IFacturaService.java
│   │   │           ├── IMovementService.java
│   │   │           ├── IProductService.java
│   │   │           ├── ISupplierService.java
│   │   │           └── IVehicleService.java
│   │   ├── data/
│   │   │   ├── dao/
│   │   │   │   ├── GenericDAO.java
│   │   │   │   ├── MovementDAO.java
│   │   │   │   ├── ProductDAO.java
│   │   │   │   ├── SupplierDAO.java
│   │   │   │   └── VehicleDAO.java
│   │   │   └── models/
│   │   │       ├── DetalleFactura.java
│   │   │       ├── Factura.java
│   │   │       ├── Movement.java
│   │   │       ├── Product.java
│   │   │       ├── Supplier.java
│   │   │       ├── Vehicle.java
│   │   │       └── builders/
│   │   │           ├── MovementBuilder.java
│   │   │           ├── ProductBuilder.java
│   │   │           ├── SupplierBuilder.java
│   │   │           └── VehicleBuilder.java
│   │   ├── presentation/
│   │   │   └── ui/
│   │   │       ├── ForestechProfessionalApp.java
│   │   │       ├── ForestechMainGUI.java
│   │   │       ├── MovementManagerGUI.java
│   │   │       ├── ProductManagerGUI.java
│   │   │       ├── VehicleManagerGUI.java
│   │   │       ├── ButtonExampleApp.java
│   │   │       ├── HelloSwingApp.java
│   │   │       ├── LookAndFeelDemo.java
│   │   │       ├── MainMenuGUI.java
│   │   │       ├── TablaProductosApp.java
│   │   │       ├── FormularioConComboBox.java
│   │   │       ├── FormularioProductoSimple.java
│   │   │       ├── core/
│   │   │       │   └── ServiceFactoryProvider.java
│   │   │       ├── dashboard/
│   │   │       │   └── DashboardPanel.java
│   │   │       ├── invoices/
│   │   │       │   └── InvoicesPanel.java
│   │   │       ├── logs/
│   │   │       │   └── LogsPanel.java
│   │   │       ├── movements/
│   │   │       │   ├── MovementDialogForm.java
│   │   │       │   ├── MovementsDataLoader.java
│   │   │       │   ├── MovementsFormatter.java
│   │   │       │   ├── MovementsPanel.java
│   │   │       │   └── MovementsTableModel.java
│   │   │       ├── products/
│   │   │       │   ├── ProductDialogForm.java
│   │   │       │   └── ProductsPanel.java
│   │   │       ├── suppliers/
│   │   │       │   └── SuppliersPanel.java
│   │   │       ├── vehicles/
│   │   │       │   ├── VehicleDialogForm.java
│   │   │       │   └── VehiclesPanel.java
│   │   │       └── utils/
│   │   │           ├── AsyncLoadManager.java
│   │   │           ├── CatalogCache.java
│   │   │           ├── ColorScheme.java
│   │   │           └── UIUtils.java
│   │   ├── config/
│   │   │   ├── ConfigLoader.java
│   │   │   ├── DatabaseConnection.java
│   │   │   └── HikariCPDataSource.java
│   │   └── shared/
│   │       ├── enums/
│   │       │   ├── MeasurementUnit.java
│   │       │   ├── MovementType.java
│   │       │   └── VehicleCategory.java
│   │       ├── exceptions/
│   │       │   ├── DatabaseException.java
│   │       │   ├── InsufficientStockException.java
│   │       │   ├── InvalidMovementException.java
│   │       │   ├── TransactionFailedException.java
│   │       │   └── ValidationException.java
│   │       ├── utils/
│   │       │   └── IdGenerator.java
│   │       └── validators/
│   │           ├── MovementValidator.java
│   │           ├── ProductValidator.java
│   │           ├── SupplierValidator.java
│   │           └── VehicleValidator.java
│   └── resources/
│       ├── application.properties
│       └── logback.xml
└── test/
    └── java/com/forestech/services/
        ├── FacturaServicesTest.java
        ├── MovementServicesTest.java
        ├── ProductServicesTest.java
        ├── SupplierServicesTest.java
        └── VehicleServicesTest.java
```
