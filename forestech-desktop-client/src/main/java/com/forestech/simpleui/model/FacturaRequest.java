package com.forestech.simpleui.model;

import java.util.List;

public class FacturaRequest {
    private String supplierId;
    private List<DetalleRequest> detalles;

    public FacturaRequest() {
    }

    public FacturaRequest(String supplierId, List<DetalleRequest> detalles) {
        this.supplierId = supplierId;
        this.detalles = detalles;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public List<DetalleRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleRequest> detalles) {
        this.detalles = detalles;
    }

    private String observaciones;
    private String formaPago;

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
}
