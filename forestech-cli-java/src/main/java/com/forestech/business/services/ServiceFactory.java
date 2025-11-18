package com.forestech.business.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory para centralizar la obtención de Services Singleton en Forestech CLI.
 *
 * <p>Esta clase implementa el patrón Singleton + Factory para proveer una única
 * instancia de cada Service del proyecto, facilitando la Inyección de Dependencias
 * en Panels, Controllers y Dialogs.</p>
 *
 * <h2>Propósito</h2>
 * <p>Evitar múltiples llamadas dispersas a {@code Service.getInstance()} en todo
 * el código, centralizando la obtención de Services en un único lugar.</p>
 *
 * <h2>Uso típico en Dependency Injection</h2>
 * <pre>{@code
 * // En AppController o ForestechProfessionalApp:
 * ServiceFactory factory = ServiceFactory.getInstance();
 *
 * // Inyectar Services en Panels:
 * MovementsPanel panel = new MovementsPanel(
 *     owner,
 *     logger,
 *     dashboardRefresh,
 *     origen -> productsPanel.requestRefresh(origen),
 *     factory.getMovementServices(),
 *     factory.getProductServices(),
 *     factory.getVehicleServices(),
 *     factory.getFacturaServices()
 * );
 *
 * // Inyectar Services en Controllers:
 * MovementController controller = new MovementController(
 *     scanner,
 *     factory.getMovementServices(),
 *     factory.getProductServices(),
 *     factory.getVehicleServices()
 * );
 * }</pre>
 *
 * <h2>Services Gestionados</h2>
 * <ul>
 *   <li>{@link MovementServices} - Gestión de movimientos de combustible (ENTRADA/SALIDA)</li>
 *   <li>{@link ProductServices} - Gestión del catálogo de productos (combustibles)</li>
 *   <li>{@link VehicleServices} - Gestión de la flota de vehículos</li>
 *   <li>{@link SupplierServices} - Gestión de proveedores</li>
 *   <li>{@link FacturaServices} - Gestión de facturas de compra</li>
 * </ul>
 *
 * <h2>Patrón de Diseño</h2>
 * <p>Combina dos patrones:</p>
 * <ul>
 *   <li><b>Singleton:</b> Una única instancia de ServiceFactory</li>
 *   <li><b>Factory:</b> Provee métodos para obtener instancias de Services</li>
 * </ul>
 *
 * <h2>Thread-Safety</h2>
 * <p>El método {@link #getInstance()} está sincronizado para garantizar que solo
 * se cree una instancia en entornos multihilo.</p>
 *
 * <h2>Beneficios</h2>
 * <ol>
 *   <li><b>Desacoplamiento:</b> Clases UI/Controllers no dependen directamente de llamadas estáticas</li>
 *   <li><b>Testabilidad:</b> Facilita inyectar mocks de Services en tests unitarios</li>
 *   <li><b>Mantenibilidad:</b> Centraliza la creación de Services en un único lugar</li>
 *   <li><b>SOLID:</b> Cumple con Dependency Inversion Principle</li>
 * </ol>
 *
 * @author Forestech Team
 * @version 1.0.0
 * @since 2025-11-17
 *
 * @see MovementServices
 * @see ProductServices
 * @see VehicleServices
 * @see SupplierServices
 * @see FacturaServices
 */
public class ServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
    private static ServiceFactory instance;

    private final MovementServices movementServices;
    private final ProductServices productServices;
    private final VehicleServices vehicleServices;
    private final SupplierServices supplierServices;
    private final FacturaServices facturaServices;

    /**
     * Constructor privado para implementar Singleton pattern.
     *
     * <p>Inicializa todos los Services obteniendo sus instancias Singleton
     * mediante {@code Service.getInstance()}.</p>
     *
     * @throws RuntimeException si algún Service no puede ser inicializado
     */
    private ServiceFactory() {
        logger.info("Inicializando ServiceFactory...");
        this.movementServices = MovementServices.getInstance();
        this.productServices = ProductServices.getInstance();
        this.vehicleServices = VehicleServices.getInstance();
        this.supplierServices = SupplierServices.getInstance();
        this.facturaServices = FacturaServices.getInstance();
        logger.info("ServiceFactory inicializado exitosamente");
    }

    /**
     * Obtiene la instancia única de ServiceFactory (thread-safe).
     *
     * <p>Implementa lazy initialization: la instancia se crea en la primera
     * llamada. El método está sincronizado para evitar problemas de concurrencia.</p>
     *
     * @return la instancia única de ServiceFactory
     */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    /**
     * Obtiene la instancia de MovementServices.
     *
     * <p>Servicio para gestionar movimientos de combustible (ENTRADA/SALIDA),
     * validar Foreign Keys (product_id, vehicle_id, numero_factura) y calcular stock.</p>
     *
     * @return instancia Singleton de MovementServices
     */
    public MovementServices getMovementServices() {
        return movementServices;
    }

    /**
     * Obtiene la instancia de ProductServices.
     *
     * <p>Servicio para gestionar el catálogo de productos (combustibles): Diesel,
     * Gasolina, Aceite, etc.</p>
     *
     * @return instancia Singleton de ProductServices
     */
    public ProductServices getProductServices() {
        return productServices;
    }

    /**
     * Obtiene la instancia de VehicleServices.
     *
     * <p>Servicio para gestionar la flota de vehículos (camiones, excavadoras,
     * motosierras, etc.) y validar que fuel_product_id exista.</p>
     *
     * @return instancia Singleton de VehicleServices
     */
    public VehicleServices getVehicleServices() {
        return vehicleServices;
    }

    /**
     * Obtiene la instancia de SupplierServices.
     *
     * <p>Servicio para gestionar proveedores de combustible.</p>
     *
     * @return instancia Singleton de SupplierServices
     */
    public SupplierServices getSupplierServices() {
        return supplierServices;
    }

    /**
     * Obtiene la instancia de FacturaServices.
     *
     * <p>Servicio para gestionar facturas de compra y sus detalles (transacciones ACID).</p>
     *
     * @return instancia Singleton de FacturaServices
     */
    public FacturaServices getFacturaServices() {
        return facturaServices;
    }
}
