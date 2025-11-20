package com.forestech.presentation.clients;

import com.forestech.modules.invoicing.models.Invoice;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class InvoiceServiceClient {
    
    private final ApiClient apiClient;
    
    public InvoiceServiceClient() {
        this.apiClient = new ApiClient();
    }
    
    public List<Invoice> findAll() throws Exception {
        return apiClient.get("/api/invoices", new TypeReference<List<Invoice>>() {});
    }
    
    public Invoice findById(String id) throws Exception {
        return apiClient.get("/api/invoices/" + id, Invoice.class);
    }
    
    public Invoice create(Invoice invoice) throws Exception {
        return apiClient.post("/api/invoices", invoice, Invoice.class);
    }
}
