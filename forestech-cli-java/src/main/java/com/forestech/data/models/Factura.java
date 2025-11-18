package com.forestech.data.models;

import java.time.LocalDate;

/**
 * Modelo que representa una factura de compra de combustible.
 */
public class Factura {
    private final String numeroFactura;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String supplierId;  // FK → suppliers.id
    private double subtotal;
    private double iva;
    private double total;
    private String observaciones;
    private String formaPago;
    private String cuentaBancaria;

    // Constructor para CREAR (sin supplier, se calcula en el servicio)
    public Factura(String numeroFactura, LocalDate fechaEmision, LocalDate fechaVencimiento,
                   String supplierId, String observaciones, String formaPago, String cuentaBancaria) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.supplierId = supplierId;
        this.subtotal = 0.0;
        this.iva = 0.0;
        this.total = 0.0;
        this.observaciones = observaciones;
        this.formaPago = formaPago;
        this.cuentaBancaria = cuentaBancaria;
    }

    // Constructor para CARGAR desde BD (con todos los valores)
    public Factura(String numeroFactura, LocalDate fechaEmision, LocalDate fechaVencimiento,
                   String supplierId, double subtotal, double iva, double total,
                   String observaciones, String formaPago, String cuentaBancaria) {
        this.numeroFactura = numeroFactura;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.supplierId = supplierId;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.observaciones = observaciones;
        this.formaPago = formaPago;
        this.cuentaBancaria = cuentaBancaria;
    }

    // Getters y Setters
    public String getNumeroFactura() { return numeroFactura; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public String getCuentaBancaria() { return cuentaBancaria; }
    public void setCuentaBancaria(String cuentaBancaria) { this.cuentaBancaria = cuentaBancaria; }

    @Override
    public String toString() {
        return "Factura{" +
                "numeroFactura='" + numeroFactura + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", supplierId='" + supplierId + '\'' +
                ", total=" + total +
                '}';
    }
}
