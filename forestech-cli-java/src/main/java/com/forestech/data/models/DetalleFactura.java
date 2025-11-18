package com.forestech.data.models;

/**
 * Modelo que representa el detalle de una factura (línea de productos).
 */
public class DetalleFactura {
    private int idDetalle;  // Autoincremental
    private String numeroFactura;  // FK → facturas.numero_factura
    private String producto;  // Nombre del producto (copia, no FK)
    private double cantidad;
    private double precioUnitario;

    // Constructor para CREAR (sin idDetalle, se genera automáticamente en BD)
    public DetalleFactura(String numeroFactura, String producto, double cantidad, double precioUnitario) {
        this.numeroFactura = numeroFactura;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Constructor para CARGAR desde BD (con idDetalle)
    public DetalleFactura(int idDetalle, String numeroFactura, String producto, double cantidad, double precioUnitario) {
        this.idDetalle = idDetalle;
        this.numeroFactura = numeroFactura;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    /**
     * Calcula el subtotal de esta línea (cantidad * precio).
     */
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    @Override
    public String toString() {
        return "DetalleFactura{" +
                "producto='" + producto + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precioUnitario +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
