package com.forestech.shared.validators;

import com.forestech.shared.enums.MovementType;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.modules.inventory.models.Movement;

/**
 * Validador centralizado para movimientos de combustible.
 * 
 * <p><strong>Separación de responsabilidades:</strong></p>
 * <ul>
 *   <li>Movement (modelo) = solo datos</li>
 *   <li>MovementValidator = reglas de validación</li>
 *   <li>MovementServices = lógica de negocio</li>
 * </ul>
 * 
 * <p><strong>Validaciones que realiza:</strong></p>
 * <ol>
 *   <li>Objeto no nulo</li>
 *   <li>Product ID válido (formato FUE-xxx)</li>
 *   <li>Cantidad positiva y dentro de límites</li>
 *   <li>Precio unitario positivo</li>
 *   <li>Tipo de movimiento válido (ENTRADA/SALIDA)</li>
 *   <li>Reglas de negocio (factura para ENTRADA, vehículo para SALIDA)</li>
 * </ol>
 * 
 * @version 1.0
 */
public class MovementValidator {
    
    /**
     * Valida un movimiento completo antes de insertar/actualizar.
     * 
     * @param movement Movimiento a validar
     * @throws ValidationException Si hay errores de validación
     */
    public static void validate(Movement movement) throws ValidationException {
        validateNotNull(movement);
        validateProductId(movement.getProductId());
        validateQuantity(movement.getQuantity());
        validateUnitPrice(movement.getUnitPrice());
        validateMovementType(movement.getMovementType());
        validateBusinessRules(movement);
    }
    
    /**
     * Valida que el movimiento no sea nulo.
     */
    private static void validateNotNull(Movement movement) throws ValidationException {
        if (movement == null) {
            throw new ValidationException("❌ Movement cannot be null");
        }
    }
    
    /**
     * Valida el formato y existencia del Product ID.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>No puede ser null o vacío</li>
     *   <li>Debe empezar con "FUE-" (formato estándar)</li>
     * </ul>
     */
    private static void validateProductId(String productId) throws ValidationException {
        if (productId == null || productId.trim().isEmpty()) {
            throw new ValidationException("❌ Product ID is required");
        }
        if (!productId.startsWith("FUE-")) {
            throw new ValidationException(
                "❌ Invalid Product ID format: '" + productId + "'\n" +
                "   Expected format: FUE-XXX (example: FUE-001)"
            );
        }
    }
    
    /**
     * Valida que la cantidad sea válida.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>Debe ser mayor que 0</li>
     *   <li>No puede exceder 10,000 unidades (límite del sistema)</li>
     * </ul>
     */
    private static void validateQuantity(double quantity) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException(
                "❌ Quantity must be greater than 0 (received: " + quantity + ")"
            );
        }
        if (quantity > 10000) {
            throw new ValidationException(
                "❌ Quantity too large: " + quantity + "\n" +
                "   Maximum allowed: 10,000 units"
            );
        }
    }
    
    /**
     * Valida que el precio unitario sea válido.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>Debe ser mayor que 0</li>
     * </ul>
     */
    private static void validateUnitPrice(double unitPrice) throws ValidationException {
        if (unitPrice <= 0) {
            throw new ValidationException(
                "❌ Unit price must be greater than 0 (received: " + unitPrice + ")"
            );
        }
    }
    
    /**
     * Valida que el tipo de movimiento sea válido.
     * 
     * <p><strong>Valores permitidos:</strong></p>
     * <ul>
     *   <li>ENTRADA</li>
     *   <li>SALIDA</li>
     * </ul>
     */
    private static void validateMovementType(MovementType type) throws ValidationException {
        if (type == null) {
            throw new ValidationException("❌ Movement type is required");
        }
    }
    
    /**
     * Valida reglas de negocio específicas según el tipo de movimiento.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>ENTRADA: Debe tener numeroFactura (no nulo/vacío)</li>
     *   <li>SALIDA: Debe tener vehicleId (no nulo/vacío)</li>
     * </ul>
     */
    private static void validateBusinessRules(Movement movement) throws ValidationException {
        MovementType type = movement.getMovementType();
        
        // ENTRADA debe tener numeroFactura
        if (MovementType.ENTRADA.equals(type)) {
            String numeroFactura = movement.getInvoiceNumber();
            if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
                throw new ValidationException(
                    "❌ ENTRADA movements require an invoice number (numeroFactura)\n" +
                    "   Use: movement.setNumeroFactura(\"FAC-XXX\")"
                );
            }
        }
        
        // SALIDA debe tener vehicleId
        if (MovementType.SALIDA.equals(type)) {
            String vehicleId = movement.getVehicleId();
            if (vehicleId == null || vehicleId.trim().isEmpty()) {
                throw new ValidationException(
                    "❌ SALIDA movements require a vehicle ID\n" +
                    "   Use: movement.setVehicleId(\"VEH-XXX\")"
                );
            }
        }
    }
    
    /**
     * Valida solo campos básicos (sin reglas de negocio).
     * Útil para validaciones parciales.
     * 
     * @param movement Movimiento a validar
     * @throws ValidationException Si hay errores básicos
     */
    public static void validateBasic(Movement movement) throws ValidationException {
        validateNotNull(movement);
        validateProductId(movement.getProductId());
        validateQuantity(movement.getQuantity());
        validateUnitPrice(movement.getUnitPrice());
        validateMovementType(movement.getMovementType());
    }
}
