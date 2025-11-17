package com.forestech.services;

/**
 * Factory para centralizar la obtención de Services Singleton.
 *
 * <p>Propósito: Facilitar Dependency Injection en Panels y Controllers
 * sin tener que llamar a XxxServices.getInstance() dispersado por el código.
 *
 * <p>Uso típico:</p>
 * <pre>
 * ServiceFactory factory = ServiceFactory.getInstance();
 * MovementsPanel panel = new MovementsPanel(
 *     owner,
 *     logger,
 *     dashboardRefresh,
 *     factory.getMovementServices(),
 *     factory.getProductServices(),
 *     factory.getVehicleServices()
 * );
 * </pre>
 *
 * <p>Ventajas:</p>
 * <ul>
 *   <li>Centraliza el acceso a Singletons</li>
 *   <li>Facilita testing (se puede mockear ServiceFactory)</li>
 *   <li>Reduce acoplamiento en Panels/Controllers</li>
 * </ul>
 */
public class ServiceFactory {

    private static ServiceFactory instance;

    // Services cacheados
    private final MovementServices movementServices;
    private final ProductServices productServices;
    private final VehicleServices vehicleServices;
    private final SupplierServices supplierServices;
    private final FacturaServices facturaServices;

    /**
     * Constructor privado - Singleton pattern.
     * Inicializa todos los Services una sola vez.
     */
    private ServiceFactory() {
        this.movementServices = MovementServices.getInstance();
        this.productServices = ProductServices.getInstance();
        this.vehicleServices = VehicleServices.getInstance();
        this.supplierServices = SupplierServices.getInstance();
        this.facturaServices = FacturaServices.getInstance();
    }

    /**
     * Obtiene la instancia única del ServiceFactory (Singleton thread-safe).
     *
     * @return Instancia del ServiceFactory
     */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    /**
     * Obtiene el servicio de movimientos.
     *
     * @return MovementServices singleton
     */
    public MovementServices getMovementServices() {
        return movementServices;
    }

    /**
     * Obtiene el servicio de productos.
     *
     * @return ProductServices singleton
     */
    public ProductServices getProductServices() {
        return productServices;
    }

    /**
     * Obtiene el servicio de vehículos.
     *
     * @return VehicleServices singleton
     */
    public VehicleServices getVehicleServices() {
        return vehicleServices;
    }

    /**
     * Obtiene el servicio de proveedores.
     *
     * @return SupplierServices singleton
     */
    public SupplierServices getSupplierServices() {
        return supplierServices;
    }

    /**
     * Obtiene el servicio de facturas.
     *
     * @return FacturaServices singleton
     */
    public FacturaServices getFacturaServices() {
        return facturaServices;
    }
}
