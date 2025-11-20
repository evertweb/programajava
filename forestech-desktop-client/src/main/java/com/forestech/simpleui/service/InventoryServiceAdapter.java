package com.forestech.simpleui.service;

import com.forestech.simpleui.model.Movement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * InventoryServiceAdapter
 * Bridge between UI and the Inventory Microservice.
 */
public class InventoryServiceAdapter {

    private final ServiceClient client;
    private final ObjectMapper mapper;

    // Assuming Gateway at 8080
    private static final String BASE_URL = "http://localhost:8080";

    public InventoryServiceAdapter() {
        this.client = new ServiceClient(BASE_URL, "Inventario");
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Movement> getAllMovements() throws Exception {
        // Endpoint: /api/movements
        String json = client.get("/api/movements");
        return mapper.readValue(json, new TypeReference<List<Movement>>() {
        });
    }

    public void createEntrada(Movement movement) throws Exception {
        String json = mapper.writeValueAsString(movement);
        client.post("/api/movements/entrada", json);
    }

    public void createSalida(Movement movement) throws Exception {
        String json = mapper.writeValueAsString(movement);
        client.post("/api/movements/salida", json);
    }

    public java.math.BigDecimal getStock(String productId) throws Exception {
        String response = client.get("/api/movements/stock/" + productId);
        // Response is likely just a number or a simple JSON wrapper.
        // Assuming it returns a raw number string based on typical Spring Boot
        // RestController returning BigDecimal.
        return new java.math.BigDecimal(response);
    }
}
