package com.forestech.services;

import com.forestech.dao.ProductDAO;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Servicio para gestionar operaciones CRUD de productos.
 * 
 * <p><strong>REFACTORIZADO - Usa DAO Pattern:</strong></p>
 * <ul>
 *   <li>Antes: 373 líneas con JDBC directo</li>
 *   <li>Después: 110 líneas delegando a ProductDAO</li>
 *   <li>Reducción: 70% menos código</li>
 * </ul>
 * 
 * @version 2.0 (Refactorizado con DAO Pattern)
 */
public class ProductServices {

    private static final Logger logger = LoggerFactory.getLogger(ProductServices.class);
    private static final ProductDAO productDAO = new ProductDAO();

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Inserta un nuevo producto en la base de datos.
     *
     * <p><strong>⚠️ IMPORTANTE:</strong></p>
     * <ul>
     *   <li>El ID del producto se genera automáticamente usando IdGenerator</li>
     *   <li>El nombre del producto debe ser único (verificación opcional)</li>
     *   <li>El precio debe ser mayor a 0</li>
     * </ul>
     */
    public static void insertProduct(Product product) throws DatabaseException {
        try {
            productDAO.insert(product);
            logger.info("Producto insertado - ID: {}, Nombre: {}", product.getId(), product.getName());
            System.out.println("✅ Producto insertado exitosamente: " + product.getId());
        } catch (Exception e) {
            logger.error("Error al insertar producto - ID: {}", product.getId(), e);
            throw new DatabaseException("Error al insertar producto", e);
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Recupera todos los productos de la base de datos.
     */
    public static List<Product> getAllProducts() throws DatabaseException {
        try {
            List<Product> products = productDAO.findAll();
            logger.debug("Se cargaron {} productos", products.size());
            System.out.println("✅ Se cargaron " + products.size() + " productos");
            return products;
        } catch (Exception e) {
            logger.error("Error al obtener productos", e);
            throw new DatabaseException("Error al obtener productos", e);
        }
    }

    /**
     * Busca un producto por su ID.
     */
    public static Product getProductById(String productId) throws DatabaseException {
        try {
            return productDAO.findById(productId).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar producto por ID: {}", productId, e);
            throw new DatabaseException("Error al buscar producto: " + productId, e);
        }
    }

    /**
     * Verifica si un producto existe en la BD.
     * Útil para validar FKs antes de insertar Movimientos o Vehículos.
     */
    public static boolean existsProduct(String productId) throws DatabaseException {
        try {
            return productDAO.exists(productId);
        } catch (Exception e) {
            logger.error("Error al verificar existencia de producto: {}", productId, e);
            throw new DatabaseException("Error al verificar producto", e);
        }
    }

    /**
     * Busca productos por nombre (coincidencia parcial).
     */
    public static List<Product> searchProductsByName(String searchTerm) throws DatabaseException {
        try {
            List<Product> products = productDAO.findByName(searchTerm);
            logger.debug("Búsqueda por nombre '{}' - {} productos encontrados", searchTerm, products.size());
            System.out.println("✅ Encontrados " + products.size() + " productos");
            return products;
        } catch (Exception e) {
            logger.error("Error al buscar productos por nombre: {}", searchTerm, e);
            throw new DatabaseException("Error al buscar productos", e);
        }
    }

    /**
     * Filtra productos por categoría.
     */
    public static List<Product> getProductsByCategory(String category) throws DatabaseException {
        try {
            List<Product> products = productDAO.findByCategory(category);
            System.out.println("✅ Encontrados " + products.size() + " productos en categoría: " + category);
            return products;
        } catch (Exception e) {
            throw new DatabaseException("Error al filtrar productos por categoría", e);
        }
    }

    /**
     * Filtra productos por unidad de medida.
     */
    public static List<Product> getProductsByUnidadDeMedida(String unidadDeMedida) throws DatabaseException {
        try {
            List<Product> products = productDAO.findByCategory(unidadDeMedida);
            System.out.println("✅ Encontrados " + products.size() + " productos con unidad: " + unidadDeMedida);
            return products;
        } catch (Exception e) {
            throw new DatabaseException("Error al filtrar productos por unidad", e);
        }
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    public static boolean updateProduct(Product product) throws DatabaseException {
        try {
            productDAO.update(product);
            logger.info("Producto actualizado - ID: {}", product.getId());
            System.out.println("✅ Producto actualizado: " + product.getId());
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar producto - ID: {}", product.getId(), e);
            System.out.println("⚠️ Error al actualizar producto: " + product.getId());
            throw new DatabaseException("Error al actualizar producto", e);
        }
    }

    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================

    public static boolean deleteProduct(String productId) throws DatabaseException {
        try {
            boolean deleted = productDAO.delete(productId);
            if (deleted) {
                logger.info("Producto eliminado - ID: {}", productId);
                System.out.println("✅ Producto eliminado: " + productId);
            } else {
                logger.warn("No se encontró producto para eliminar - ID: {}", productId);
                System.out.println("⚠️ No se encontró el producto: " + productId);
            }
            return deleted;
        } catch (Exception e) {
            logger.error("Error al eliminar producto - ID: {}", productId, e);
            throw new DatabaseException("Error al eliminar producto", e);
        }
    }
}



