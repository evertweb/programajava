package com.forestech.shared.exception;

/**
 * Excepción genérica cuando se intenta crear una entidad duplicada
 * Reemplaza: DuplicateProductException, DuplicateVehicleException, etc.
 */
public class DuplicateEntityException extends RuntimeException {
    
    public DuplicateEntityException(String message) {
        super(message);
    }
    
    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
