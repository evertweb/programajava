package com.forestech.shared.exception;

/**
 * Excepción genérica para errores de validación
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
