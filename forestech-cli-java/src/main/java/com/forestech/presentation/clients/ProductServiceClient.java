package com.forestech.presentation.clients;

import com.forestech.modules.catalog.models.Product;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class ProductServiceClient {
    
    private final ApiClient apiClient;
    
    public ProductServiceClient() {
        this.apiClient = new ApiClient();
    }
    
    public List<Product> findAll() throws Exception {
        return apiClient.get("/api/products", new TypeReference<List<Product>>() {});
    }
    
    public Product findById(String id) throws Exception {
        return apiClient.get("/api/products/" + id, Product.class);
    }
    
    public Product create(Product product) throws Exception {
        return apiClient.post("/api/products", product, Product.class);
    }
    
    public void update(Product product) throws Exception {
        apiClient.put("/api/products/" + product.getId(), product, Product.class);
    }
    
    public void delete(String id) throws Exception {
        apiClient.delete("/api/products/" + id);
    }
}
