package com.forestech.invoicing.client;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MovementDTO {
    private String id;
    private String productId;
    private BigDecimal quantity;
}
