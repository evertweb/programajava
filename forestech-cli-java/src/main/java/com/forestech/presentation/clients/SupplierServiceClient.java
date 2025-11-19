package com.forestech.presentation.clients;

import com.forestech.modules.partners.models.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class SupplierServiceClient {
    
    private final ApiClient apiClient;
    
    public SupplierServiceClient() {
        this.apiClient = new ApiClient();
    }
    
    public List<Supplier> findAll() throws Exception {
        return apiClient.get("/api/suppliers", new TypeReference<List<Supplier>>() {});
    }
    
    public Supplier findById(String id) throws Exception {
        return apiClient.get("/api/suppliers/" + id, Supplier.class);
    }
    
    public Supplier create(Supplier supplier) throws Exception {
        return apiClient.post("/api/suppliers", supplier, Supplier.class);
    }

    public void update(Supplier supplier) throws Exception {
        apiClient.put("/api/suppliers/" + supplier.getId(), supplier, Supplier.class);
    }
    
    public void delete(String id) throws Exception {
        apiClient.delete("/api/suppliers/" + id);
    }
}
