package com.forestech.exceptions;

/**
 * Excepción lanzada cuando se intenta realizar una SALIDA de combustible
 * pero no hay stock suficiente disponible.
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * if (stockActual < cantidadSolicitada) {
 *     throw new InsufficientStockException(
 *         "Stock insuficiente para ACPM. Disponible: " + stockActual + ", Solicitado: " + cantidadSolicitada
 *     );
 * }
 * </pre>
 */
public class InsufficientStockException extends Exception {

    private String productId;
    private double stockActual;
    private double cantidadSolicitada;

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, String productId, double stockActual, double cantidadSolicitada) {
        super(message);
        this.productId = productId;
        this.stockActual = stockActual;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getProductId() {
        return productId;
    }

    public double getStockActual() {
        return stockActual;
    }

    public double getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    @Override
    public String toString() {
        return "InsufficientStockException{" +
                "productId='" + productId + '\'' +
                ", stockActual=" + stockActual +
                ", cantidadSolicitada=" + cantidadSolicitada +
                ", mensaje='" + getMessage() + '\'' +
                '}';
    }
}
