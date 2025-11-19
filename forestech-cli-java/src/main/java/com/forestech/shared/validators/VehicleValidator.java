package com.forestech.shared.validators;

import com.forestech.shared.exceptions.ValidationException;
import com.forestech.modules.fleet.models.Vehicle;
import com.forestech.shared.enums.VehicleCategory;

/**
 * Validador centralizado para vehículos.
 * 
 * <p><strong>Validaciones que realiza:</strong></p>
 * <ol>
 *   <li>Objeto no nulo</li>
 *   <li>Nombre/placa válido</li>
 *   <li>Categoría válida</li>
 *   <li>Capacidad positiva y dentro de límites</li>
 * </ol>
 * 
 * @version 1.0
 */
public class VehicleValidator {
    
    /**
     * Valida un vehículo completo antes de insertar/actualizar.
     * 
     * @param vehicle Vehículo a validar
     * @throws ValidationException Si hay errores de validación
     */
    public static void validate(Vehicle vehicle) throws ValidationException {
        validateNotNull(vehicle);
        validateName(vehicle.getName());
        validateCategory(vehicle.getCategory());
        
        // Solo validar capacidad si está definida
        if (vehicle.getCapacity() > 0) {
            validateCapacity(vehicle.getCapacity());
        }
    }
    
    /**
     * Valida que el vehículo no sea nulo.
     */
    private static void validateNotNull(Vehicle vehicle) throws ValidationException {
        if (vehicle == null) {
            throw new ValidationException("❌ Vehicle cannot be null");
        }
    }
    
    /**
     * Valida el nombre/placa del vehículo.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>No puede ser null o vacío</li>
     *   <li>Mínimo 3 caracteres</li>
     *   <li>Máximo 50 caracteres</li>
     * </ul>
     */
    private static void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("❌ Vehicle name/plate is required");
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < 3) {
            throw new ValidationException(
                "❌ Vehicle name too short: '" + name + "'\n" +
                "   Minimum length: 3 characters"
            );
        }
        if (trimmedName.length() > 50) {
            throw new ValidationException(
                "❌ Vehicle name too long: " + name.length() + " characters\n" +
                "   Maximum length: 50 characters"
            );
        }
    }
    
    /**
     * Valida la categoría del vehículo.
     * 
     * <p><strong>Valores permitidos:</strong></p>
     * <ul>
     *   <li>CAMION</li>
     *   <li>CAMIONETA</li>
     *   <li>MOTO</li>
     *   <li>AUTOMOVIL</li>
     *   <li>MAQUINARIA</li>
     *   <li>OTRO</li>
     * </ul>
     */
    private static void validateCategory(VehicleCategory category) throws ValidationException {
        if (category == null) {
            throw new ValidationException("❌ Vehicle category is required");
        }
        // VehicleCategory es un enum, por lo que siempre es válido si no es null
    }
    
    /**
     * Valida la capacidad del vehículo.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>Debe ser mayor que 0 (si está definida)</li>
     *   <li>No puede exceder 50,000 litros (límite razonable)</li>
     * </ul>
     */
    private static void validateCapacity(double capacity) throws ValidationException {
        if (capacity <= 0) {
            throw new ValidationException(
                "❌ Vehicle capacity must be greater than 0 (received: " + capacity + ")"
            );
        }
        if (capacity > 50000) {
            throw new ValidationException(
                "❌ Vehicle capacity too large: " + capacity + " liters\n" +
                "   Maximum allowed: 50,000 liters"
            );
        }
    }
    
    /**
     * Valida el ID del vehículo (formato VEH-XXX).
     * 
     * @param vehicleId ID a validar
     * @throws ValidationException Si el formato es inválido
     */
    public static void validateId(String vehicleId) throws ValidationException {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            throw new ValidationException("❌ Vehicle ID is required");
        }
        if (!vehicleId.startsWith("VEH-")) {
            throw new ValidationException(
                "❌ Invalid Vehicle ID format: '" + vehicleId + "'\n" +
                "   Expected format: VEH-XXX (example: VEH-001)"
            );
        }
    }
    
    /**
     * Valida solo campos básicos (sin capacidad).
     * Útil para validaciones parciales.
     * 
     * @param vehicle Vehículo a validar
     * @throws ValidationException Si hay errores básicos
     */
    public static void validateBasic(Vehicle vehicle) throws ValidationException {
        validateNotNull(vehicle);
        validateName(vehicle.getName());
        validateCategory(vehicle.getCategory());
    }
}
