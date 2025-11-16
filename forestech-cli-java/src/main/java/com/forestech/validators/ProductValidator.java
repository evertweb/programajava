package com.forestech.validators;

import com.forestech.exceptions.ValidationException;
import com.forestech.models.Product;

/**
 * Validador centralizado para productos.
 * 
 * <p><strong>Validaciones que realiza:</strong></p>
 * <ol>
 *   <li>Objeto no nulo</li>
 *   <li>Nombre válido (no vacío, longitud correcta)</li>
 *   <li>Precio positivo</li>
 *   <li>Unidad de medida válida</li>
 *   <li>Categoría válida</li>
 * </ol>
 * 
 * @version 1.0
 */
public class ProductValidator {
    
    // Unidades de medida permitidas
    private static final String[] VALID_UNITS = {"GALON", "GARRAFA", "CUARTO", "CANECA"};
    
    // Categorías permitidas
    private static final String[] VALID_CATEGORIES = {"DIESEL", "GASOLINA", "ACEITE", "OTRO"};
    
    /**
     * Valida un producto completo antes de insertar/actualizar.
     * 
     * @param product Producto a validar
     * @throws ValidationException Si hay errores de validación
     */
    public static void validate(Product product) throws ValidationException {
        validateNotNull(product);
        validateName(product.getName());
        validatePrice(product.getPriceXUnd());
        validateUnit(product.getUnidadDeMedida());
        // Note: Product model doesn't have category field, removed validation
    }
    
    /**
     * Valida que el producto no sea nulo.
     */
    private static void validateNotNull(Product product) throws ValidationException {
        if (product == null) {
            throw new ValidationException("❌ Product cannot be null");
        }
    }
    
    /**
     * Valida el nombre del producto.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>No puede ser null o vacío</li>
     *   <li>Mínimo 3 caracteres</li>
     *   <li>Máximo 100 caracteres</li>
     * </ul>
     */
    private static void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("❌ Product name is required");
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < 3) {
            throw new ValidationException(
                "❌ Product name too short: '" + name + "'\n" +
                "   Minimum length: 3 characters"
            );
        }
        if (trimmedName.length() > 100) {
            throw new ValidationException(
                "❌ Product name too long: " + name.length() + " characters\n" +
                "   Maximum length: 100 characters"
            );
        }
    }
    
    /**
     * Valida el precio del producto.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>Debe ser mayor que 0</li>
     *   <li>Debe ser un número válido (no NaN, no Infinity)</li>
     * </ul>
     */
    private static void validatePrice(double price) throws ValidationException {
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new ValidationException("❌ Price is not a valid number");
        }
        if (price <= 0) {
            throw new ValidationException(
                "❌ Price must be greater than 0 (received: " + price + ")"
            );
        }
    }
    
    /**
     * Valida la unidad de medida.
     * 
     * <p><strong>Valores permitidos:</strong></p>
     * <ul>
     *   <li>GALON</li>
     *   <li>GARRAFA</li>
     *   <li>CUARTO</li>
     *   <li>CANECA</li>
     * </ul>
     */
    private static void validateUnit(String unit) throws ValidationException {
        if (unit == null || unit.trim().isEmpty()) {
            throw new ValidationException("❌ Unit of measure is required");
        }
        
        boolean isValid = false;
        for (String validUnit : VALID_UNITS) {
            if (validUnit.equals(unit)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new ValidationException(
                "❌ Invalid unit of measure: '" + unit + "'\n" +
                "   Valid units: GALON, GARRAFA, CUARTO, CANECA"
            );
        }
    }
    
    /**
     * Valida la categoría del producto.
     * 
     * <p><strong>NOTA:</strong> Product model actualmente no tiene campo category.
     * Este método está disponible para futuras versiones.</p>
     * 
     * @param category Categoría a validar
     * @throws ValidationException Si la categoría no es válida
     */
    @Deprecated
    private static void validateCategory(String category) throws ValidationException {
        if (category == null || category.trim().isEmpty()) {
            throw new ValidationException("❌ Product category is required");
        }
        
        boolean isValid = false;
        for (String validCategory : VALID_CATEGORIES) {
            if (validCategory.equals(category)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new ValidationException(
                "❌ Invalid category: '" + category + "'\n" +
                "   Valid categories: DIESEL, GASOLINA, ACEITE, OTRO"
            );
        }
    }
    
    /**
     * Valida el ID del producto (formato FUE-XXX).
     * 
     * @param productId ID a validar
     * @throws ValidationException Si el formato es inválido
     */
    public static void validateId(String productId) throws ValidationException {
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
}
