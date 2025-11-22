package com.forestech.invoicing.service;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetalleRequest {
    private String productId;
    private String productName; // Optional: For new products
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal ivaPercent; // Optional: defaults to 13.0 if null
}
