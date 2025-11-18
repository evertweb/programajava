package com.forestech.business.services;

import com.forestech.data.dao.MovementDAO;
import com.forestech.shared.enums.MovementType;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.InsufficientStockException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.data.models.Movement;
import com.forestech.business.services.interfaces.IMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar operaciones CRUD de movimientos de combustible.
 *
 * <p>Esta clase maneja todas las operaciones de base de datos relacionadas con
 * la tabla Movement, incluyendo:</p>
 * <ul>
 *   <li>CREATE: Insertar nuevos movimientos (ENTRADA/SALIDA)</li>
 *   <li>READ: Consultar movimientos con JOINs a tablas relacionadas</li>
 *   <li>UPDATE: Actualizar movimientos existentes</li>
 *   <li>DELETE: Eliminar movimientos (soft delete recomendado)</li>
 * </ul>
 *
 * @author Forestech Development Team
 * @version 3.0 (implementa IMovementService, instance methods)
 * @since Fase 4
 */
public class MovementServices implements IMovementService {

    // Singleton instance (lazy initialization)
    private static MovementServices instance;

    private static final Logger logger = LoggerFactory.getLogger(MovementServices.class);
    private final MovementDAO dao;
    private final ProductServices productServices;
    private final VehicleServices vehicleServices;
    private final FacturaServices facturaServices;

    /**
     * Constructor PRIVADO (Singleton pattern).
     * Usa getInstance() de cada Service para las dependencias.
     */
    private MovementServices() {
        this.dao = new MovementDAO();
        this.productServices = ProductServices.getInstance();
        this.vehicleServices = VehicleServices.getInstance();
        this.facturaServices = FacturaServices.getInstance();
    }

    /**
     * Obtiene la instancia única de MovementServices (thread-safe).
     *
     * @return Instancia única de MovementServices
     */
    public static synchronized MovementServices getInstance() {
        if (instance == null) {
            instance = new MovementServices();
        }
        return instance;
    }

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Inserta un nuevo movimiento en la base de datos CON VALIDACIONES DE INTEGRIDAD.
     *
     * <p><strong>⚠️ VALIDACIONES AUTOMÁTICAS (antes del INSERT):</strong></p>
     * <ol>
     *   <li><strong>product_id:</strong> DEBE existir en oil_products (OBLIGATORIO)</li>
     *   <li><strong>vehicle_id:</strong> Si NO es NULL, DEBE existir en vehicles</li>
     *   <li><strong>numero_factura:</strong> Si NO es NULL, DEBE existir en facturas</li>
     *   <li><strong>SALIDA:</strong> Valida stock suficiente antes de permitir</li>
     * </ol>
     *
     * <p><strong>🎯 REGLAS DE NEGOCIO POR TIPO:</strong></p>
     * <ul>
     *   <li><strong>ENTRADA:</strong> product_id + numero_factura (vehicle_id = NULL)</li>
     *   <li><strong>SALIDA:</strong> product_id + vehicle_id (numero_factura = NULL)</li>
     * </ul>
     *
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * // ENTRADA de combustible con factura
     * Movement entrada = new Movement(
     *     "ENTRADA",
     *     "FUE-12345678", // productId (DEBE existir en oil_products)
     *     null,           // vehicleId (NULL para entradas)
     *     "10734",        // numeroFactura (DEBE existir en facturas)
     *     "GALON",
     *     50.0,
     *     8500.0
     * );
     * MovementServices.insertMovement(entrada);
     *
     * // SALIDA de combustible a vehículo
     * Movement salida = new Movement(
     *     "SALIDA",
     *     "FUE-12345678",  // productId (DEBE existir en oil_products)
     *     "VEH-87654321",  // vehicleId (DEBE existir en vehicles)
     *     null,            // numeroFactura (NULL para salidas)
     *     "GALON",
     *     20.0,
     *     8500.0
     * );
     * MovementServices.insertMovement(salida);
     * </pre>
     *
     * @param movement Objeto Movement a insertar (con ID ya generado)
     * @throws DatabaseException Si hay error de conexión o alguna FK no existe
     * @throws InsufficientStockException Si es SALIDA y no hay stock suficiente
     * @throws ValidationException Si los datos no son válidos
     * @see Movement
     */
    @Override
    public void insertMovement(Movement movement) throws DatabaseException, InsufficientStockException, ValidationException {
        try {
            // ========================================================================
            // VALIDACIÓN 1: product_id OBLIGATORIO (DEBE existir en oil_products)
            // ========================================================================
            if (movement.getProductId() == null || movement.getProductId().trim().isEmpty()) {
                throw new DatabaseException(
                    "ERROR: product_id es OBLIGATORIO. No puede ser NULL.",
                    new IllegalArgumentException("product_id NULL")
                );
            }

            if (!productServices.existsProduct(movement.getProductId())) {
                throw new DatabaseException(
                    "ERROR: El producto '" + movement.getProductId() + "' NO existe en oil_products. " +
                    "Debes crear el producto primero antes de usarlo en un movimiento.",
                    new IllegalArgumentException("product_id inválido")
                );
            }

            // ========================================================================
            // VALIDACIÓN 2: vehicle_id (solo si NO es NULL, debe existir en vehicles)
            // ========================================================================
            if (movement.getVehicleId() != null && !movement.getVehicleId().trim().isEmpty()) {
                if (!vehicleServices.existsVehicle(movement.getVehicleId())) {
                    throw new DatabaseException(
                        "ERROR: El vehículo '" + movement.getVehicleId() + "' NO existe en vehicles. " +
                        "Debes crear el vehículo primero antes de usarlo en un movimiento.",
                        new IllegalArgumentException("vehicle_id inválido")
                    );
                }
            }

            // ========================================================================
            // VALIDACIÓN 3: numero_factura (solo si NO es NULL, debe existir en facturas)
            // ========================================================================
            if (movement.getInvoiceNumber() != null && !movement.getInvoiceNumber().trim().isEmpty()) {
                if (!facturaServices.existsFactura(movement.getInvoiceNumber())) {
                    throw new DatabaseException(
                        "ERROR: La factura '" + movement.getInvoiceNumber() + "' NO existe en facturas. " +
                        "Debes crear la factura primero antes de usarla en un movimiento.",
                        new IllegalArgumentException("numero_factura inválido")
                    );
                }
            }

            // ========================================================================
            // VALIDACIÓN 4: STOCK para SALIDAS (debe haber suficiente inventario)
            // ========================================================================
            if (MovementType.SALIDA.equals(movement.getMovementType())) {
                dao.validateStockForSalida(movement.getProductId(), movement.getQuantity());
            }

            // ========================================================================
            // SI TODAS LAS VALIDACIONES PASARON → Delegar inserción al DAO
            // ========================================================================
            dao.insert(movement);

            logger.info("Movimiento insertado exitosamente - ID: {}, Tipo: {}",
                movement.getId(), movement.getMovementTypeCode());
            System.out.println("✅ Movimiento insertado exitosamente: " + movement.getId());

        } catch (SQLException e) {
            // Verificar si el error es por violación de Foreign Key
            if (e.getMessage().contains("foreign key constraint")) {
                logger.error("Violación de FK al insertar movimiento - ID: {}", movement.getId(), e);
                throw new DatabaseException(
                    "Error: El productId, vehicleId o numeroFactura no existe en la BD. " +
                    "Verifica que las llaves foráneas sean válidas.", e);
            } else {
                logger.error("Error SQL al insertar movimiento - ID: {}", movement.getId(), e);
                throw new DatabaseException("Error al insertar movimiento", e);
            }
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Obtiene TODOS los movimientos con información relacionada de productos.
     *
     * <p>Utiliza LEFT JOIN para traer el nombre del producto desde oil_products.</p>
     *
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * try {
     *     List&lt;Movement&gt; movimientos = MovementServices.getAllMovements();
     *     for (Movement m : movimientos) {
     *         System.out.println(m);  // Muestra todos los campos
     *     }
     * } catch (DatabaseException e) {
     *     System.err.println(e.getMessage());
     * }
     * </pre>
     *
     * @return Lista de movimientos (vacía si no hay registros)
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public List<Movement> getAllMovements() throws DatabaseException {
        try {
            List<Movement> movements = dao.findAll();
            logger.debug("Se cargaron {} movimientos", movements.size());
            System.out.println("✅ Se cargaron " + movements.size() + " movimientos");
            return movements;
        } catch (SQLException e) {
            logger.error("Error al obtener todos los movimientos", e);
            throw new DatabaseException("Error al obtener movimientos", e);
        }
    }

    /**
     * Busca un movimiento por su ID.
     *
     * @param movementId ID del movimiento (formato: MOV-XXXXXXXX)
     * @return Movement encontrado, o null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public Movement getMovementById(String movementId) throws DatabaseException {
        try {
            return dao.findById(movementId).orElse(null);
        } catch (SQLException e) {
            logger.error("Error al buscar movimiento por ID: {}", movementId, e);
            throw new DatabaseException("Error al buscar movimiento por ID", e);
        }
    }

    /**
     * Obtiene movimientos filtrados por tipo (ENTRADA o SALIDA).
     *
     * @param movementType Tipo: "ENTRADA" o "SALIDA"
     * @return Lista de movimientos del tipo especificado
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public List<Movement> getMovementsByType(MovementType movementType) throws DatabaseException {
        if (movementType == null) {
            throw new DatabaseException(
                "movementType no puede ser nulo",
                new IllegalArgumentException("movementType null"));
        }

        try {
            List<Movement> movements = dao.findByType(movementType);
            logger.debug("Se encontraron {} movimientos de tipo: {}", movements.size(), movementType.getCode());
            System.out.println("✅ Se encontraron " + movements.size() + " movimientos de tipo: " + movementType.getCode());
            return movements;
        } catch (SQLException e) {
            logger.error("Error al buscar movimientos por tipo: {}", movementType.getCode(), e);
            throw new DatabaseException("Error al buscar movimientos por tipo", e);
        }
    }

    /**
     * @deprecated Usa {@link #getMovementsByType(MovementType)}
     */
    @Deprecated
    public List<Movement> getMovementsByType(String movementType) throws DatabaseException {
        MovementType type = movementType != null ? MovementType.fromCode(movementType) : null;
        return getMovementsByType(type);
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    /**
     * Actualiza la cantidad y precio unitario de un movimiento existente.
     *
     * <p><strong>⚠️ NOTA:</strong> No permite actualizar el productId, vehicleId o numeroFactura.
     * Solo actualiza quantity y unitPrice.</p>
     *
     * @param movementId ID del movimiento a actualizar
     * @param newQuantity Nueva cantidad
     * @param newUnitPrice Nuevo precio unitario
     * @throws DatabaseException Si hay error de conexión
     * @throws InsufficientStockException Si la actualización causa stock negativo
     */
    @Override
    public void updateMovement(String movementId, double newQuantity, double newUnitPrice)
            throws DatabaseException, InsufficientStockException {

        try {
            Optional<Movement> optMovement = dao.findById(movementId);

            if (optMovement.isEmpty()) {
                logger.warn("No se encontró movimiento para actualizar - ID: {}", movementId);
                System.out.println("⚠️  No se encontró el movimiento: " + movementId);
                throw new DatabaseException("Movimiento no encontrado: " + movementId);
            }

            Movement movement = optMovement.get();
            movement.setQuantity(newQuantity);
            movement.setUnitPrice(newUnitPrice);

            dao.update(movement);

            logger.info("Movimiento actualizado - ID: {}, Cantidad: {}, Precio: {}",
                movementId, newQuantity, newUnitPrice);
            System.out.println("✅ Movimiento actualizado: " + movementId);

        } catch (SQLException e) {
            logger.error("Error al actualizar movimiento - ID: {}", movementId, e);
            throw new DatabaseException("Error al actualizar movimiento", e);
        }
    }

    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================

    /**
     * Elimina un movimiento por su ID (DELETE físico).
     *
     * <p><strong>⚠️ ADVERTENCIA:</strong> Esta operación es IRREVERSIBLE.</p>
     * <p>En producción, se recomienda usar "soft delete" (marcar como eliminado)
     * en lugar de borrar físicamente.</p>
     *
     * @param movementId ID del movimiento a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public boolean deleteMovement(String movementId) throws DatabaseException {
        try {
            boolean deleted = dao.delete(movementId);

            if (deleted) {
                logger.info("Movimiento eliminado - ID: {}", movementId);
                System.out.println("✅ Movimiento eliminado: " + movementId);
                return true;
            } else {
                logger.warn("No se encontró movimiento para eliminar - ID: {}", movementId);
                System.out.println("⚠️  No se encontró el movimiento: " + movementId);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar movimiento - ID: {}", movementId, e);
            throw new DatabaseException("Error al eliminar movimiento", e);
        }
    }

    // ============================================================================
    // MÉTODOS DE CÁLCULO DE STOCK
    // ============================================================================

    /**
     * Obtiene el stock actual de un producto (ENTRADAS - SALIDAS).
     *
     * @param productId ID del producto
     * @return Stock actual (puede ser negativo si hubo más salidas que entradas)
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public double getProductStock(String productId) throws DatabaseException {
        try {
            return dao.getStockByProductId(productId);
        } catch (SQLException e) {
            logger.error("Error al calcular stock del producto: {}", productId, e);
            throw new DatabaseException("Error al calcular stock", e);
        }
    }

    /**
     * Obtiene el stock actual para una lista de productos.
     *
     * <p><strong>NOTA:</strong> Esta implementación llama al DAO múltiples veces.
     * Para consultas masivas, considera implementar un método bulk en el DAO.</p>
     *
     * @param productIds IDs de producto a consultar
     * @return Mapa producto → stock (0.0 si no tiene movimientos)
     * @throws DatabaseException Error al consultar la base de datos
     */
    @Override
    public Map<String, Double> getStockByProductIds(List<String> productIds) throws DatabaseException {
        Map<String, Double> stockMap = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return stockMap;
        }

        try {
            for (String productId : productIds) {
                double stock = dao.getStockByProductId(productId);
                stockMap.put(productId, stock);
            }
            return stockMap;
        } catch (SQLException e) {
            logger.error("Error al calcular stock por lote - {} productos", productIds.size(), e);
            throw new DatabaseException("Error al calcular stock por lote", e);
        }
    }

    /**
     * Obtiene el total de movimientos en la base de datos.
     *
     * @return Cantidad total de movimientos
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public int getTotalMovements() throws DatabaseException {
        try {
            List<Movement> allMovements = dao.findAll();
            return allMovements.size();
        } catch (SQLException e) {
            logger.error("Error al contar movimientos", e);
            throw new DatabaseException("Error al contar movimientos", e);
        }
    }

    /**
     * Obtiene movimientos de un vehículo específico.
     *
     * @param vehicleId ID del vehículo
     * @return Lista de movimientos del vehículo
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public List<Movement> getMovementsByVehicle(String vehicleId) throws DatabaseException {
        try {
            return dao.findByVehicleId(vehicleId);
        } catch (SQLException e) {
            logger.error("Error al obtener movimientos por vehículo: {}", vehicleId, e);
            throw new DatabaseException("Error al obtener movimientos por vehículo", e);
        }
    }

    /**
     * Obtiene movimientos por rango de fechas.
     *
     * <p><strong>TODO:</strong> Implementar método findByDateRange() en MovementDAO.</p>
     * <p>Por ahora, usa getAllMovements() y filtra en memoria si es necesario.</p>
     *
     * @param fechaInicio Fecha de inicio (formato: "2025-01-01 00:00:00")
     * @param fechaFin Fecha de fin (formato: "2025-12-31 23:59:59")
     * @return Lista de movimientos en el rango de fechas
     * @throws DatabaseException Si hay error de conexión
     * @deprecated Método pendiente de implementación en el DAO
     */
    @Deprecated
    @Override
    public List<Movement> getMovementsByDateRange(String fechaInicio, String fechaFin)
            throws DatabaseException {
        throw new UnsupportedOperationException(
            "Este método requiere implementar findByDateRange() en MovementDAO. " +
            "Usa getAllMovements() y filtra en memoria por ahora."
        );
    }

    /**
     * Obtiene movimientos de un producto específico.
     *
     * @param productId ID del producto
     * @return Lista de movimientos del producto
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public List<Movement> getMovementsByProduct(String productId) throws DatabaseException {
        try {
            return dao.findByProductId(productId);
        } catch (SQLException e) {
            logger.error("Error al obtener movimientos por producto: {}", productId, e);
            throw new DatabaseException("Error al obtener movimientos por producto", e);
        }
    }

    /**
     * Obtiene el stock de todos los productos.
     *
     * @return Mapa con productId → stock
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public Map<String, Double> getAllProductsStock() throws DatabaseException {
        try {
            // Obtener todos los productos y calcular su stock
            List<String> allProductIds = productServices.getAllProducts()
                .stream()
                .map(p -> p.getId())
                .toList();
            return getStockByProductIds(allProductIds);
        } catch (Exception e) {
            logger.error("Error al calcular stock de todos los productos", e);
            throw new DatabaseException("Error al calcular stock de todos los productos", e);
        }
    }

}
