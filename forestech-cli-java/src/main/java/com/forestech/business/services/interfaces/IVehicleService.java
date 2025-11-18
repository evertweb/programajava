package com.forestech.business.services.interfaces;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.data.models.Vehicle;

import java.util.List;

/**
 * Interfaz para servicios de vehículos.
 * 
 * <p>Define el contrato para operaciones CRUD de vehículos.</p>
 * 
 * @version 1.0
 */
public interface IVehicleService {
    
    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================
    
    /**
     * Inserta un nuevo vehículo con validaciones.
     * 
     * @param vehicle Vehículo a insertar
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    void insertVehicle(Vehicle vehicle) throws DatabaseException, ValidationException;
    
    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================
    
    /**
     * Obtiene todos los vehículos.
     * 
     * @return Lista de todos los vehículos
     * @throws DatabaseException Si hay error de BD
     */
    List<Vehicle> getAllVehicles() throws DatabaseException;
    
    /**
     * Busca un vehículo por su ID.
     * 
     * @param vehicleId ID del vehículo
     * @return Vehicle encontrado, o null si no existe
     * @throws DatabaseException Si hay error de BD
     */
    Vehicle getVehicleById(String vehicleId) throws DatabaseException;
    
    /**
     * Verifica si un vehículo existe.
     * 
     * @param vehicleId ID del vehículo
     * @return true si existe, false si no
     * @throws DatabaseException Si hay error de BD
     */
    boolean existsVehicle(String vehicleId) throws DatabaseException;
    
    /**
     * Filtra vehículos por categoría.
     * 
     * @param category Categoría (CAMION, CAMIONETA, MOTO, etc.)
     * @return Lista de vehículos de esa categoría
     * @throws DatabaseException Si hay error de BD
     */
    List<Vehicle> getVehiclesByCategory(String category) throws DatabaseException;
    
    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================
    
    /**
     * Actualiza un vehículo existente.
     * 
     * @param vehicle Vehículo con datos actualizados
     * @return true si se actualizó, false si no existía
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    boolean updateVehicle(Vehicle vehicle) throws DatabaseException, ValidationException;
    
    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================
    
    /**
     * Elimina un vehículo de la BD.
     * 
     * @param vehicleId ID del vehículo
     * @return true si se eliminó, false si no existía
     * @throws DatabaseException Si hay error de BD
     */
    boolean deleteVehicle(String vehicleId) throws DatabaseException;
}
