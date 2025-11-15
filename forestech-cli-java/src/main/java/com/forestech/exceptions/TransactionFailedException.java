package com.forestech.exceptions;

/**
 * Excepción lanzada cuando falla una transacción de base de datos.
 *
 * Se usa en operaciones que requieren múltiples INSERT/UPDATE
 * y deben ser atómicas (todo o nada).
 */
public class TransactionFailedException extends Exception {

    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
