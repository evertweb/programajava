package com.forestech.simpleui.model;

import java.math.BigDecimal;

public class DetalleRequest {
    private String productId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;

    public DetalleRequest() {
    }

    public DetalleRequest(String productId, BigDecimal cantidad, BigDecimal precioUnitario) {
        this.productId = productId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
