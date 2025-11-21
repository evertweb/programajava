package com.forestech.invoicing.client;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private BigDecimal unitPrice;
    private String measurementUnit;
    private String presentation;
}
