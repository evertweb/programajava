package com.forestech.shared.exceptions;

/**
 * Excepción lanzada cuando ocurre un error de validación de datos.
 * 
 * <p>Se utiliza en los Validators para indicar que los datos no cumplen
 * con las reglas de negocio o formato esperado.</p>
 * 
 * <p><strong>Ejemplos de uso:</strong></p>
 * <ul>
 *   <li>Cantidad negativa o cero</li>
 *   <li>Formato de ID inválido</li>
 *   <li>Campos obligatorios faltantes</li>
 *   <li>Valores fuera de rango permitido</li>
 * </ul>
 * 
 * @version 1.0
 */
public class ValidationException extends Exception {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param message Descripción del error de validación
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa raíz.
     * 
     * @param message Descripción del error de validación
     * @param cause Excepción que causó el error
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
