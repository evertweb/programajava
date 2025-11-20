package com.forestech.business.services.interfaces;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.modules.catalog.models.Product;

import java.util.List;

/**
 * Interfaz para servicios de productos.
 * 
 * <p>Define el contrato para operaciones CRUD de productos de combustible.</p>
 * 
 * @version 1.0
 */
public interface IProductService {
    
    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================
    
    /**
     * Inserta un nuevo producto con validaciones.
     * 
     * @param product Producto a insertar
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    void insertProduct(Product product) throws DatabaseException, ValidationException;
    
    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================
    
    /**
     * Obtiene todos los productos.
     * 
     * @return Lista de todos los productos
     * @throws DatabaseException Si hay error de BD
     */
    List<Product> getAllProducts() throws DatabaseException;
    
    /**
     * Busca un producto por su ID.
     * 
     * @param productId ID del producto
     * @return Product encontrado, o null si no existe
     * @throws DatabaseException Si hay error de BD
     */
    Product getProductById(String productId) throws DatabaseException;
    
    /**
     * Verifica si un producto existe.
     * 
     * @param productId ID del producto
     * @return true si existe, false si no
     * @throws DatabaseException Si hay error de BD
     */
    boolean existsProduct(String productId) throws DatabaseException;
    
    /**
     * Busca productos por nombre (coincidencia parcial).
     * 
     * @param searchTerm Término de búsqueda
     * @return Lista de productos que coinciden
     * @throws DatabaseException Si hay error de BD
     */
    List<Product> searchProductsByName(String searchTerm) throws DatabaseException;
    
    /**
     * Filtra productos por unidad de medida.
     *
     * @param measurementUnitCode Unidad de medida (GALON, GARRAFA, etc.)
     * @return Lista de productos con esa unidad
     * @throws DatabaseException Si hay error de BD
     */
    List<Product> getProductsByMeasurementUnit(String measurementUnitCode) throws DatabaseException;
    
    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================
    
    /**
     * Actualiza un producto existente.
     * 
     * @param product Producto con datos actualizados
     * @return true si se actualizó, false si no existía
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    boolean updateProduct(Product product) throws DatabaseException, ValidationException;
    
    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================
    
    /**
     * Elimina un producto de la BD.
     * 
     * @param productId ID del producto
     * @return true si se eliminó, false si no existía
     * @throws DatabaseException Si hay error de BD
     */
    boolean deleteProduct(String productId) throws DatabaseException;
}
