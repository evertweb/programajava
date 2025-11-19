package com.forestech.modules.invoicing.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
    private String id;
    private String supplierId;
    private LocalDateTime issueDate;
    private BigDecimal totalAmount;
    private String status;
    private List<InvoiceDetail> detalles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<InvoiceDetail> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<InvoiceDetail> detalles) {
        this.detalles = detalles;
    }
}
