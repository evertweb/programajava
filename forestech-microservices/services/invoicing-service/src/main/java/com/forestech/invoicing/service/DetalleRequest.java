package com.forestech.invoicing.service;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleRequest {
    private String productId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}
