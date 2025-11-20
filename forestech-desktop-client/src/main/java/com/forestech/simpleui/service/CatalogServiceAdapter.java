package com.forestech.simpleui.service;

import com.forestech.simpleui.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * CatalogServiceAdapter
 * Bridge between UI and the Catalog Microservice.
 */
public class CatalogServiceAdapter {

    private final ServiceClient client;
    private final ObjectMapper mapper;

    // Assuming Gateway at 8080
    private static final String BASE_URL = "http://localhost:8080";

    public CatalogServiceAdapter() {
        this.client = new ServiceClient(BASE_URL);
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Product> getAllProducts() throws Exception {
        // Endpoint: /api/products
        String json = client.get("/api/products");
        return mapper.readValue(json, new TypeReference<List<Product>>() {
        });
    }

    public void createProduct(Product product) throws Exception {
        String json = mapper.writeValueAsString(product);
        client.post("/api/products", json);
    }
}
