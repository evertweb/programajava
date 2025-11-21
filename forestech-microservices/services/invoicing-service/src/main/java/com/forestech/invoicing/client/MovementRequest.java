package com.forestech.invoicing.client;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovementRequest {
    private String productId;
    private String invoiceId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private String description;
}
