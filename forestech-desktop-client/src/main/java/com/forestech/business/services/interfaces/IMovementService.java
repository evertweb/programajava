package com.forestech.business.services.interfaces;

import com.forestech.shared.enums.MovementType;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.InsufficientStockException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.modules.inventory.models.Movement;

import java.util.List;
import java.util.Map;

/**
 * Interfaz para servicios de movimientos de combustible.
 * 
 * <p><strong>¿Por qué usar interfaces?</strong></p>
 * <ul>
 *   <li>Desacoplamiento: permite cambiar la implementación sin afectar clientes</li>
 *   <li>Testing: facilita crear mocks para pruebas unitarias</li>
 *   <li>Documentación: define el contrato claro del servicio</li>
 *   <li>Extensibilidad: múltiples implementaciones (ej: SQL, NoSQL, REST)</li>
 * </ul>
 * 
 * @version 1.0
 */
public interface IMovementService {
    
    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================
    
    /**
     * Inserta un nuevo movimiento con validaciones completas.
     * 
     * @param movement Movimiento a insertar
     * @throws DatabaseException Si hay error de BD
     * @throws InsufficientStockException Si no hay stock suficiente (SALIDA)
     * @throws ValidationException Si los datos no son válidos
     */
    void insertMovement(Movement movement) 
        throws DatabaseException, InsufficientStockException, ValidationException;
    
    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================
    
    /**
     * Obtiene todos los movimientos.
     * 
     * @return Lista de todos los movimientos
     * @throws DatabaseException Si hay error de BD
     */
    List<Movement> getAllMovements() throws DatabaseException;
    
    /**
     * Busca un movimiento por su ID.
     * 
     * @param movementId ID del movimiento
     * @return Movement encontrado, o null si no existe
     * @throws DatabaseException Si hay error de BD
     */
    Movement getMovementById(String movementId) throws DatabaseException;
    
    /**
     * Filtra movimientos por tipo (ENTRADA/SALIDA).
     * 
     * @param type Tipo de movimiento
     * @return Lista de movimientos del tipo especificado
     * @throws DatabaseException Si hay error de BD
     */
    List<Movement> getMovementsByType(MovementType type) throws DatabaseException;

    @Deprecated
    default List<Movement> getMovementsByType(String type) throws DatabaseException {
        MovementType movementType = type != null ? MovementType.fromCode(type) : null;
        return getMovementsByType(movementType);
    }
    
    /**
     * Filtra movimientos por producto.
     * 
     * @param productId ID del producto
     * @return Lista de movimientos del producto
     * @throws DatabaseException Si hay error de BD
     */
    List<Movement> getMovementsByProduct(String productId) throws DatabaseException;
    
    /**
     * Filtra movimientos por vehículo.
     * 
     * @param vehicleId ID del vehículo
     * @return Lista de movimientos del vehículo
     * @throws DatabaseException Si hay error de BD
     */
    List<Movement> getMovementsByVehicle(String vehicleId) throws DatabaseException;
    
    /**
     * Calcula el stock actual de un producto (ENTRADAS - SALIDAS).
     * 
     * @param productId ID del producto
     * @return Stock actual
     * @throws DatabaseException Si hay error de BD
     */
    double getProductStock(String productId) throws DatabaseException;
    
    /**
     * Calcula el stock de todos los productos.
     * 
     * @return Mapa con productId → stock
     * @throws DatabaseException Si hay error de BD
     */
    Map<String, Double> getAllProductsStock() throws DatabaseException;
    
    /**
     * Calcula el stock de productos específicos.
     * 
     * @param productIds Lista de IDs de productos
     * @return Mapa con productId → stock
     * @throws DatabaseException Si hay error de BD
     */
    Map<String, Double> getStockByProductIds(List<String> productIds) throws DatabaseException;
    
    /**
     * Obtiene movimientos por rango de fechas.
     * 
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Lista de movimientos en el rango
     * @throws DatabaseException Si hay error de BD
     */
    List<Movement> getMovementsByDateRange(String startDate, String endDate) throws DatabaseException;
    
    /**
     * Obtiene el total de movimientos registrados.
     * 
     * @return Cantidad total de movimientos
     * @throws DatabaseException Si hay error de BD
     */
    int getTotalMovements() throws DatabaseException;
    
    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================
    
    /**
     * Actualiza la cantidad y precio de un movimiento.
     * 
     * @param id ID del movimiento
     * @param newQuantity Nueva cantidad
     * @param newUnitPrice Nuevo precio unitario
     * @throws DatabaseException Si hay error de BD
     * @throws InsufficientStockException Si la actualización causa stock negativo
     */
    void updateMovement(String id, double newQuantity, double newUnitPrice) 
        throws DatabaseException, InsufficientStockException;
    
    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================
    
    /**
     * Elimina un movimiento de la BD.
     * 
     * @param movementId ID del movimiento
     * @return true si se eliminó, false si no existía
     * @throws DatabaseException Si hay error de BD
     */
    boolean deleteMovement(String movementId) throws DatabaseException;
}
