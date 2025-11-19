package com.forestech.reports.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StockReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal stock;
    private BigDecimal valorTotal;
}
