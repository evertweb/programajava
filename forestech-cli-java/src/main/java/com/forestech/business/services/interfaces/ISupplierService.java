package com.forestech.business.services.interfaces;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.data.models.Supplier;

import java.util.List;

/**
 * Interfaz para servicios de proveedores.
 * 
 * <p>Define el contrato para operaciones CRUD de proveedores.</p>
 * 
 * @version 1.0
 */
public interface ISupplierService {
    
    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================
    
    /**
     * Inserta un nuevo proveedor con validaciones.
     * 
     * @param supplier Proveedor a insertar
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    void insertSupplier(Supplier supplier) throws DatabaseException, ValidationException;
    
    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================
    
    /**
     * Obtiene todos los proveedores.
     * 
     * @return Lista de todos los proveedores
     * @throws DatabaseException Si hay error de BD
     */
    List<Supplier> getAllSuppliers() throws DatabaseException;
    
    /**
     * Busca un proveedor por su ID.
     * 
     * @param supplierId ID del proveedor
     * @return Supplier encontrado, o null si no existe
     * @throws DatabaseException Si hay error de BD
     */
    Supplier getSupplierById(String supplierId) throws DatabaseException;
    
    /**
     * Verifica si un proveedor existe.
     * 
     * @param supplierId ID del proveedor
     * @return true si existe, false si no
     * @throws DatabaseException Si hay error de BD
     */
    boolean existsSupplier(String supplierId) throws DatabaseException;
    
    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================
    
    /**
     * Actualiza un proveedor existente.
     * 
     * @param supplier Proveedor con datos actualizados
     * @return true si se actualizó, false si no existía
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    boolean updateSupplier(Supplier supplier) throws DatabaseException, ValidationException;
    
    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================
    
    /**
     * Elimina un proveedor de la BD.
     * 
     * @param supplierId ID del proveedor
     * @return true si se eliminó, false si no existía
     * @throws DatabaseException Si hay error de BD
     */
    boolean deleteSupplier(String supplierId) throws DatabaseException;
}
