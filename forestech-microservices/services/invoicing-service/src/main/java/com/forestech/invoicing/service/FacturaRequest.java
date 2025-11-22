package com.forestech.invoicing.service;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class FacturaRequest {
    private String supplierId;
    private List<DetalleRequest> detalles;
    private String observaciones;
    private String formaPago;
}
