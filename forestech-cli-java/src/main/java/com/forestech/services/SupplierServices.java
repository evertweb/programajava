package com.forestech.services;

import com.forestech.dao.SupplierDAO;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Supplier;
import com.forestech.services.interfaces.ISupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Servicio para gestionar operaciones CRUD de proveedores.
 *
 * <p><strong>REFACTORIZADO - Usa DAO Pattern:</strong></p>
 * <ul>
 *   <li>Antes: 168 líneas con JDBC directo</li>
 *   <li>Después: 80 líneas delegando a SupplierDAO</li>
 *   <li>Reducción: 52% menos código</li>
 * </ul>
 *
 * <p>Ahora esta clase SOLO maneja:</p>
 * <ul>
 *   <li>Validaciones de negocio (si las hay)</li>
 *   <li>Mensajes de éxito/error</li>
 *   <li>Delegación al DAO</li>
 * </ul>
 *
 * @version 3.0 (implementa ISupplierService, instance methods)
 */
public class SupplierServices implements ISupplierService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierServices.class);
    private final SupplierDAO supplierDAO;

    /**
     * Constructor sin parámetros.
     */
    public SupplierServices() {
        this.supplierDAO = new SupplierDAO();
    }

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    @Override
    public void insertSupplier(Supplier supplier) throws DatabaseException {
        try {
            supplierDAO.insert(supplier);
            logger.info("Proveedor insertado - ID: {}, Nombre: {}", supplier.getId(), supplier.getName());
            System.out.println("✅ Proveedor insertado: " + supplier.getId());
        } catch (Exception e) {
            logger.error("Error al insertar proveedor - ID: {}", supplier.getId(), e);
            throw new DatabaseException("Error al insertar proveedor", e);
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Recupera todos los proveedores de la base de datos.
     */
    @Override
    public List<Supplier> getAllSuppliers() throws DatabaseException {
        try {
            List<Supplier> suppliers = supplierDAO.findAll();
            logger.debug("Se cargaron {} proveedores", suppliers.size());
            System.out.println("✅ Se cargaron " + suppliers.size() + " proveedores");
            return suppliers;
        } catch (Exception e) {
            logger.error("Error al obtener proveedores", e);
            throw new DatabaseException("Error al obtener proveedores", e);
        }
    }

    /**
     * Busca un proveedor por su ID.
     * ÚTIL PARA VALIDAR FOREIGN KEYS antes de insertar Factura.
     *
     * @param supplierId ID del proveedor (formato: SUP-XXXXXXXX)
     * @return Supplier encontrado, o null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public Supplier getSupplierById(String supplierId) throws DatabaseException {
        try {
            return supplierDAO.findById(supplierId).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar proveedor por ID: {}", supplierId, e);
            throw new DatabaseException("Error al buscar proveedor: " + supplierId, e);
        }
    }

    /**
     * Verifica si un proveedor existe en la BD.
     * Útil para validar FKs antes de insertar Facturas.
     */
    @Override
    public boolean existsSupplier(String supplierId) throws DatabaseException {
        try {
            return supplierDAO.exists(supplierId);
        } catch (Exception e) {
            logger.error("Error al verificar existencia de proveedor: {}", supplierId, e);
            throw new DatabaseException("Error al verificar proveedor", e);
        }
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    @Override
    public boolean updateSupplier(Supplier supplier) throws DatabaseException {
        try {
            supplierDAO.update(supplier);
            logger.info("Proveedor actualizado - ID: {}", supplier.getId());
            System.out.println("✅ Proveedor actualizado: " + supplier.getId());
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor - ID: {}", supplier.getId(), e);
            System.out.println("⚠️ Error al actualizar proveedor: " + supplier.getId());
            throw new DatabaseException("Error al actualizar proveedor", e);
        }
    }

    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================

    @Override
    public boolean deleteSupplier(String supplierId) throws DatabaseException {
        try {
            boolean deleted = supplierDAO.delete(supplierId);
            if (deleted) {
                logger.info("Proveedor eliminado - ID: {}", supplierId);
                System.out.println("✅ Proveedor eliminado: " + supplierId);
            } else {
                logger.warn("No se encontró proveedor para eliminar - ID: {}", supplierId);
                System.out.println("⚠️ No se encontró el proveedor: " + supplierId);
            }
            return deleted;
        } catch (Exception e) {
            logger.error("Error al eliminar proveedor - ID: {}", supplierId, e);
            throw new DatabaseException("Error al eliminar proveedor", e);
        }
    }
}
