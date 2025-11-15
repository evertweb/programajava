package com.forestech.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un movimiento inválido.
 *
 * Por ejemplo:
 * - ENTRADA con vehicleId (las entradas no deben tener vehículo)
 * - SALIDA con numeroFactura (las salidas no deben tener factura)
 * - Cantidad negativa o cero
 */
public class InvalidMovementException extends Exception {

    public InvalidMovementException(String message) {
        super(message);
    }

    public InvalidMovementException(String message, Throwable cause) {
        super(message, cause);
    }
}
