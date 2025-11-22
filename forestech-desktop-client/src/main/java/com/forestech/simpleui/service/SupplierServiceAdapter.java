package com.forestech.simpleui.service;

import com.forestech.simpleui.model.Supplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class SupplierServiceAdapter {

    private final ServiceClient client;
    private final ObjectMapper mapper;
    private static final String BASE_URL = "http://localhost:8080";

    public SupplierServiceAdapter() {
        this.client = new ServiceClient(BASE_URL, "Proveedores");
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Supplier> getAllSuppliers() throws Exception {
        String json = client.get("/api/suppliers");
        return mapper.readValue(json, new TypeReference<List<Supplier>>() {});
    }

    public void createSupplier(Supplier supplier) throws Exception {
        String json = mapper.writeValueAsString(supplier);
        client.post("/api/suppliers", json);
    }

    public void updateSupplier(String id, Supplier supplier) throws Exception {
        String json = mapper.writeValueAsString(supplier);
        client.put("/api/suppliers/" + id, json);
    }

    public void deleteSupplier(String id) throws Exception {
        client.delete("/api/suppliers/" + id);
    }
}
