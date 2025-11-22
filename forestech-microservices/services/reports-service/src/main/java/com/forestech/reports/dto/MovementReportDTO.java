package com.forestech.reports.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
public class MovementReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private String type;
    private String productId;
    private BigDecimal quantity;
}
