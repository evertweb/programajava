package com.forestech.business.services;

import com.forestech.data.dao.VehicleDAO;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Vehicle;
import com.forestech.business.services.interfaces.IVehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Servicio para gestionar operaciones CRUD de vehículos.
 *
 * <p><strong>REFACTORIZADO - Usa DAO Pattern:</strong></p>
 * <ul>
 *   <li>Antes: 347 líneas con JDBC directo</li>
 *   <li>Después: 115 líneas delegando a VehicleDAO</li>
 *   <li>Reducción: 67% menos código</li>
 * </ul>
 *
 * @version 3.0 (implementa IVehicleService, instance methods)
 */
public class VehicleServices implements IVehicleService {

    // Singleton instance (lazy initialization)
    private static VehicleServices instance;

    private static final Logger logger = LoggerFactory.getLogger(VehicleServices.class);
    private final VehicleDAO vehicleDAO;
    private final ProductServices productServices;

    /**
     * Constructor PRIVADO (Singleton pattern).
     * Usa getInstance() de ProductServices para la dependencia.
     */
    private VehicleServices() {
        this.vehicleDAO = new VehicleDAO();
        this.productServices = ProductServices.getInstance();
    }

    /**
     * Obtiene la instancia única de VehicleServices (thread-safe).
     *
     * @return Instancia única de VehicleServices
     */
    public static synchronized VehicleServices getInstance() {
        if (instance == null) {
            instance = new VehicleServices();
        }
        return instance;
    }

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Inserta un nuevo vehículo en la base de datos CON VALIDACIONES DE INTEGRIDAD.
     *
     * <p><strong>⚠️ VALIDACIONES AUTOMÁTICAS (antes del INSERT):</strong></p>
     * <ol>
     *   <li><strong>fuel_product_id:</strong> Si NO es NULL, DEBE existir en oil_products</li>
     * </ol>
     */
    @Override
    public void insertVehicle(Vehicle vehicle) throws DatabaseException {
        // VALIDACIÓN: fuel_product_id (solo si NO es NULL, debe existir)
        if (vehicle.getFuelProductId() != null && !vehicle.getFuelProductId().trim().isEmpty()) {
            if (!productServices.existsProduct(vehicle.getFuelProductId())) {
                throw new DatabaseException(
                    "ERROR: El producto de combustible '" + vehicle.getFuelProductId() +
                    "' NO existe en oil_products. " +
                    "Debes crear el producto primero antes de asignarlo a un vehículo.",
                    new IllegalArgumentException("fuel_product_id inválido")
                );
            }
        }

        try {
            vehicleDAO.insert(vehicle);
            logger.info("Vehículo insertado - ID: {}, Nombre: {}", vehicle.getId(), vehicle.getName());
            System.out.println("✅ Vehículo insertado exitosamente: " + vehicle.getId());
        } catch (Exception e) {
            logger.error("Error al insertar vehículo - ID: {}", vehicle.getId(), e);
            throw new DatabaseException("Error al insertar vehículo", e);
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Recupera todos los vehículos de la base de datos.
     */
    @Override
    public List<Vehicle> getAllVehicles() throws DatabaseException {
        try {
            List<Vehicle> vehicles = vehicleDAO.findAll();
            logger.debug("Se cargaron {} vehículos", vehicles.size());
            System.out.println("✅ Se cargaron " + vehicles.size() + " vehículos");
            return vehicles;
        } catch (Exception e) {
            logger.error("Error al obtener vehículos", e);
            throw new DatabaseException("Error al obtener vehículos", e);
        }
    }

    /**
     * Busca un vehículo por su ID.
     */
    @Override
    public Vehicle getVehicleById(String vehicleId) throws DatabaseException {
        try {
            return vehicleDAO.findById(vehicleId).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar vehículo por ID: {}", vehicleId, e);
            throw new DatabaseException("Error al buscar vehículo: " + vehicleId, e);
        }
    }

    /**
     * Verifica si un vehículo existe en la BD.
     * Útil para validar FKs antes de insertar Movimientos.
     */
    @Override
    public boolean existsVehicle(String vehicleId) throws DatabaseException {
        try {
            return vehicleDAO.exists(vehicleId);
        } catch (Exception e) {
            logger.error("Error al verificar existencia de vehículo: {}", vehicleId, e);
            throw new DatabaseException("Error al verificar vehículo", e);
        }
    }

    /**
     * Filtra vehículos por categoría.
     */
    @Override
    public List<Vehicle> getVehiclesByCategory(String category) throws DatabaseException {
        try {
            List<Vehicle> vehicles = vehicleDAO.findByCategory(category);
            System.out.println("✅ Encontrados " + vehicles.size() + " vehículos en categoría: " + category);
            return vehicles;
        } catch (Exception e) {
            throw new DatabaseException("Error al filtrar vehículos", e);
        }
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    @Override
    public boolean updateVehicle(Vehicle vehicle) throws DatabaseException {
        try {
            vehicleDAO.update(vehicle);
            logger.info("Vehículo actualizado - ID: {}", vehicle.getId());
            System.out.println("✅ Vehículo actualizado: " + vehicle.getId());
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar vehículo - ID: {}", vehicle.getId(), e);
            System.out.println("⚠️ Error al actualizar vehículo: " + vehicle.getId());
            throw new DatabaseException("Error al actualizar vehículo", e);
        }
    }

    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================

    @Override
    public boolean deleteVehicle(String vehicleId) throws DatabaseException {
        try {
            boolean deleted = vehicleDAO.delete(vehicleId);
            if (deleted) {
                logger.info("Vehículo eliminado - ID: {}", vehicleId);
                System.out.println("✅ Vehículo eliminado: " + vehicleId);
            } else {
                logger.warn("No se encontró vehículo para eliminar - ID: {}", vehicleId);
                System.out.println("⚠️ No se encontró el vehículo: " + vehicleId);
            }
            return deleted;
        } catch (Exception e) {
            logger.error("Error al eliminar vehículo - ID: {}", vehicleId, e);
            throw new DatabaseException("Error al eliminar vehículo", e);
        }
    }
}
