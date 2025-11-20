package com.forestech.modules.inventory.services;

import com.forestech.modules.inventory.dao.MovementDAO;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.InsufficientStockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor especializado en el cálculo y validación de stock.
 * 
 * <p>
 * <strong>Responsabilidades:</strong>
 * </p>
 * <ul>
 * <li>Calcular stock actual de productos</li>
 * <li>Validar disponibilidad de stock para salidas</li>
 * <li>Consultas masivas de stock</li>
 * </ul>
 * 
 * @author Forestech Development Team
 * @version 1.0
 */
public class StockManager {

    private static final Logger logger = LoggerFactory.getLogger(StockManager.class);
    private static StockManager instance;
    private final MovementDAO dao;

    private StockManager() {
        this.dao = new MovementDAO();
    }

    public static synchronized StockManager getInstance() {
        if (instance == null) {
            instance = new StockManager();
        }
        return instance;
    }

    /**
     * Obtiene el stock actual de un producto.
     * 
     * @param productId ID del producto
     * @return Stock actual
     * @throws DatabaseException Si hay error de base de datos
     */
    public double getProductStock(String productId) throws DatabaseException {
        try {
            return dao.getStockByProductId(productId);
        } catch (SQLException e) {
            logger.error("Error al calcular stock del producto: {}", productId, e);
            throw new DatabaseException("Error al calcular stock", e);
        }
    }

    /**
     * Valida si hay suficiente stock para una salida.
     * 
     * @param productId ID del producto
     * @param quantity  Cantidad requerida
     * @throws InsufficientStockException Si no hay suficiente stock
     * @throws DatabaseException          Si hay error de base de datos
     */
    public void validateStockForSalida(String productId, double quantity)
            throws InsufficientStockException, DatabaseException {
        try {
            dao.validateStockForSalida(productId, quantity);
        } catch (SQLException e) {
            // El DAO ya lanza InsufficientStockException, pero si es SQL puro, lo
            // envolvemos
            if (e instanceof java.sql.SQLException) { // Simplificación, idealmente el DAO maneja esto
                // Re-throw si es nuestra excepción custom (aunque DAO suele lanzar Runtime o
                // checked especificas)
                // Asumimos que el DAO lanza InsufficientStockException directamente o la
                // SQLException
                // En la implementación actual de MovementDAO, validateStockForSalida lanza
                // InsufficientStockException
            }
            logger.error("Error al validar stock para producto: {}", productId, e);
            throw new DatabaseException("Error al validar stock", e);
        }
    }

    /**
     * Obtiene el stock actual para una lista de productos.
     * 
     * @param productIds IDs de producto a consultar
     * @return Mapa producto → stock
     * @throws DatabaseException Error al consultar la base de datos
     */
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
            logger.error("Error al calcular stock por lote", e);
            throw new DatabaseException("Error al calcular stock por lote", e);
        }
    }
}
