package com.forestech.business.helpers;

import com.forestech.modules.inventory.models.Movement;
import com.forestech.shared.enums.MovementType;

/**
 * Servicio encargado de cálculos de negocio para movimientos.
 * 
 * <p><strong>Separación de responsabilidades:</strong></p>
 * <ul>
 *   <li>Movement (modelo) = solo datos (getters/setters)</li>
 *   <li>MovementCalculator (service) = lógica de cálculo</li>
 * </ul>
 * 
 * <p><strong>Refactorización:</strong></p>
 * <ul>
 *   <li>Antes: Movement tenía métodos de cálculo (getTotalValue, getIva, etc.)</li>
 *   <li>Después: Movement es POJO puro, cálculos aquí</li>
 * </ul>
 * 
 * @version 2.0 (Refactorizado - Service Layer)
 */
public class MovementCalculator {
    
    public static final double IVA = 0.19;
    public static final String TXTCALCULO = "╔═══════════════════════════════════════════════════════════╗\n" +
                                             "║            💰 DESGLOSE DE LA OPERACIÓN                   ║\n" +
                                             "╚═══════════════════════════════════════════════════════════╝";

    // ============================================================================
    // MÉTODOS MODERNOS (USAN OBJETOS MOVEMENT)
    // ============================================================================

    /**
     * Calcula el subtotal de un movimiento (cantidad × precio unitario).
     * 
     * @param movement Movimiento a calcular
     * @return Subtotal sin IVA
     */
    public static double calculateSubtotal(Movement movement) {
        return movement.getQuantity() * movement.getUnitPrice();
    }
    
    /**
     * Calcula el IVA sobre un movimiento.
     * 
     * @param movement Movimiento a calcular
     * @return Monto del IVA
     */
    public static double calculateIVA(Movement movement) {
        return calculateSubtotal(movement) * IVA;
    }
    
    /**
     * Calcula el total con IVA incluido.
     * 
     * @param movement Movimiento a calcular
     * @return Total con IVA
     */
    public static double calculateTotalWithIVA(Movement movement) {
        double subtotal = calculateSubtotal(movement);
        double iva = calculateIVA(movement);
        return subtotal + iva;
    }
    
    /**
     * Valida si un movimiento tiene datos válidos.
     * 
     * @param movement Movimiento a validar
     * @return true si es válido, false si no
     */
    public static boolean isValidMovement(Movement movement) {
        return movement != null &&
               movement.getQuantity() > 0 && 
               movement.getUnitPrice() > 0 &&
               movement.getProductId() != null &&
               !movement.getProductId().trim().isEmpty();
    }
    
    /**
     * Determina si es una compra grande (>= 100 unidades).
     * 
     * @param movement Movimiento a evaluar
     * @return true si la cantidad es >= 100
     */
    public static boolean isBigPurchase(Movement movement) {
        return movement.getQuantity() >= 100;
    }
    
    /**
     * Determina si un movimiento requiere aprobación gerencial.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>Solo aplica a SALIDAS</li>
     *   <li>Cantidad > 100 unidades, O</li>
     *   <li>Total con IVA > $500</li>
     * </ul>
     * 
     * @param movement Movimiento a evaluar
     * @return true si requiere aprobación
     */
    public static boolean requiresApproval(Movement movement) {
        MovementType type = movement.getMovementType();
        if (!MovementType.SALIDA.equals(type)) {
            return false;
        }
        
        double total = calculateTotalWithIVA(movement);
        return movement.getQuantity() > 100 || total > 500;
    }

    // ============================================================================
    // MÉTODOS LEGACY (COMPATIBILIDAD CON CÓDIGO ANTIGUO)
    // ============================================================================

    /**
     * @deprecated Usar calculateSubtotal(Movement) en su lugar
     */
    @Deprecated
    public static double calculateSubtotal(double quantity, double unitPrice) {
        return quantity * unitPrice;
    }
    
    /**
     * @deprecated Usar calculateIVA(Movement) en su lugar
     */
    @Deprecated
    public static double calculateIVA(double subtotal) {
        return subtotal * IVA;
    }
    
    /**
     * @deprecated Calcular directamente desde Movement
     */
    @Deprecated
    public static double calculateTotal(double subtotal, double iva) {
        return subtotal + iva;
    }
    
    /**
     * @deprecated Usar isValidMovement(Movement) en su lugar
     */
    @Deprecated
    public static boolean isValidMovement(String fuelType, double quantity, double unitPrice) {
        return fuelType != null && !fuelType.isEmpty() && quantity > 0 && unitPrice > 0;
    }
    
    /**
     * @deprecated Usar isBigPurchase(Movement) en su lugar
     */
    @Deprecated
    public static boolean isBigPurchase(double quantity) {
        return quantity >= 100;
    }
    
    /**
     * @deprecated Usar requiresApproval(Movement) en su lugar
     */
    @Deprecated
    public static boolean requiresApproval(String typeMovement, double quantity, double total) {
        MovementType type = typeMovement != null ? MovementType.fromCode(typeMovement) : null;
        return MovementType.SALIDA.equals(type) && (quantity > 100 || total > 500);
    }

    /**
     * @deprecated Usar métodos individuales de cálculo
     */
    @Deprecated
    public static void printMovementSummary(String fuelType, double quantity, double unitPrice) {
        double subTotal = calculateSubtotal(quantity, unitPrice);
        double iva = calculateIVA(subTotal);
        double total = calculateTotal(subTotal, iva);

        System.out.println("\n" + TXTCALCULO);
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│  ⛽ Tipo de combustible: " + fuelType);
        System.out.println("│  📦 Cantidad: " + quantity + " galones");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  💵 Subtotal: $" + String.format("%.2f", subTotal));
        System.out.println("│  📊 IVA (19%): $" + String.format("%.2f", iva));
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  💰 TOTAL A PAGAR: $" + String.format("%.2f", total));
        System.out.println("└───────────────────────────────────────────────────────────┘");
    }
}
