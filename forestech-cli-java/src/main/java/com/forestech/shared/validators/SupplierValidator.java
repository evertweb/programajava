package com.forestech.shared.validators;

import com.forestech.shared.exceptions.ValidationException;
import com.forestech.data.models.Supplier;

/**
 * Validador centralizado para proveedores.
 * 
 * <p><strong>Validaciones que realiza:</strong></p>
 * <ol>
 *   <li>Objeto no nulo</li>
 *   <li>NIT válido (formato y longitud)</li>
 *   <li>Nombre válido</li>
 *   <li>Email válido (formato básico)</li>
 *   <li>Teléfono válido (si está presente)</li>
 * </ol>
 * 
 * @version 1.0
 */
public class SupplierValidator {
    
    /**
     * Valida un proveedor completo antes de insertar/actualizar.
     * 
     * @param supplier Proveedor a validar
     * @throws ValidationException Si hay errores de validación
     */
    public static void validate(Supplier supplier) throws ValidationException {
        validateNotNull(supplier);
        validateNIT(supplier.getNit());
        validateName(supplier.getName());
        
        // Validar email solo si está presente
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            validateEmail(supplier.getEmail());
        }
        
        // Validar teléfono solo si está presente
        if (supplier.getTelephone() != null && !supplier.getTelephone().trim().isEmpty()) {
            validatePhone(supplier.getTelephone());
        }
    }
    
    /**
     * Valida que el proveedor no sea nulo.
     */
    private static void validateNotNull(Supplier supplier) throws ValidationException {
        if (supplier == null) {
            throw new ValidationException("❌ Supplier cannot be null");
        }
    }
    
    /**
     * Valida el NIT del proveedor.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>No puede ser null o vacío</li>
     *   <li>Mínimo 8 caracteres</li>
     *   <li>Máximo 15 caracteres</li>
     *   <li>Solo puede contener números y guiones</li>
     * </ul>
     */
    private static void validateNIT(String nit) throws ValidationException {
        if (nit == null || nit.trim().isEmpty()) {
            throw new ValidationException("❌ NIT is required");
        }
        
        String trimmedNIT = nit.trim();
        if (trimmedNIT.length() < 8) {
            throw new ValidationException(
                "❌ NIT too short: '" + nit + "'\n" +
                "   Minimum length: 8 characters"
            );
        }
        if (trimmedNIT.length() > 15) {
            throw new ValidationException(
                "❌ NIT too long: " + nit.length() + " characters\n" +
                "   Maximum length: 15 characters"
            );
        }
        
        // Validar que solo contenga números y guiones
        if (!trimmedNIT.matches("[0-9\\-]+")) {
            throw new ValidationException(
                "❌ Invalid NIT format: '" + nit + "'\n" +
                "   NIT can only contain numbers and hyphens (example: 900123456-1)"
            );
        }
    }
    
    /**
     * Valida el nombre del proveedor.
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
            throw new ValidationException("❌ Supplier name is required");
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < 3) {
            throw new ValidationException(
                "❌ Supplier name too short: '" + name + "'\n" +
                "   Minimum length: 3 characters"
            );
        }
        if (trimmedName.length() > 100) {
            throw new ValidationException(
                "❌ Supplier name too long: " + name.length() + " characters\n" +
                "   Maximum length: 100 characters"
            );
        }
    }
    
    /**
     * Valida el formato del email.
     * 
     * <p><strong>Validación básica:</strong></p>
     * <ul>
     *   <li>Debe contener @</li>
     *   <li>Debe contener un dominio después del @</li>
     *   <li>Máximo 100 caracteres</li>
     * </ul>
     */
    private static void validateEmail(String email) throws ValidationException {
        String trimmedEmail = email.trim();
        
        if (trimmedEmail.length() > 100) {
            throw new ValidationException(
                "❌ Email too long: " + email.length() + " characters\n" +
                "   Maximum length: 100 characters"
            );
        }
        
        // Validación básica de email
        if (!trimmedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException(
                "❌ Invalid email format: '" + email + "'\n" +
                "   Example: supplier@example.com"
            );
        }
    }
    
    /**
     * Valida el formato del teléfono.
     * 
     * <p><strong>Reglas:</strong></p>
     * <ul>
     *   <li>Mínimo 7 caracteres</li>
     *   <li>Máximo 15 caracteres</li>
     *   <li>Solo números, espacios, guiones y paréntesis</li>
     * </ul>
     */
    private static void validatePhone(String phone) throws ValidationException {
        String trimmedPhone = phone.trim();
        
        if (trimmedPhone.length() < 7) {
            throw new ValidationException(
                "❌ Phone number too short: '" + phone + "'\n" +
                "   Minimum length: 7 characters"
            );
        }
        if (trimmedPhone.length() > 15) {
            throw new ValidationException(
                "❌ Phone number too long: " + phone.length() + " characters\n" +
                "   Maximum length: 15 characters"
            );
        }
        
        // Validar que solo contenga números, espacios, guiones y paréntesis
        if (!trimmedPhone.matches("[0-9\\s\\-()]+")) {
            throw new ValidationException(
                "❌ Invalid phone format: '" + phone + "'\n" +
                "   Phone can only contain numbers, spaces, hyphens and parentheses\n" +
                "   Example: (601) 123-4567 or 3001234567"
            );
        }
    }
    
    /**
     * Valida el ID del proveedor (formato PRO-XXX).
     * 
     * @param supplierId ID a validar
     * @throws ValidationException Si el formato es inválido
     */
    public static void validateId(String supplierId) throws ValidationException {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            throw new ValidationException("❌ Supplier ID is required");
        }
        if (!supplierId.startsWith("PRO-")) {
            throw new ValidationException(
                "❌ Invalid Supplier ID format: '" + supplierId + "'\n" +
                "   Expected format: PRO-XXX (example: PRO-001)"
            );
        }
    }
    
    /**
     * Valida solo campos obligatorios (NIT y nombre).
     * Útil para validaciones parciales.
     * 
     * @param supplier Proveedor a validar
     * @throws ValidationException Si hay errores en campos obligatorios
     */
    public static void validateBasic(Supplier supplier) throws ValidationException {
        validateNotNull(supplier);
        validateNIT(supplier.getNit());
        validateName(supplier.getName());
    }
}
