package com.forestech.presentation.clients;

import com.forestech.modules.inventory.models.Movement;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class MovementServiceClient {
    
    private final ApiClient apiClient;
    
    public MovementServiceClient() {
        this.apiClient = new ApiClient();
    }
    
    public List<Movement> findAll() throws Exception {
        return apiClient.get("/api/movements", new TypeReference<List<Movement>>() {});
    }
    
    public Movement findById(String id) throws Exception {
        return apiClient.get("/api/movements/" + id, Movement.class);
    }

    public void delete(String id) throws Exception {
        apiClient.delete("/api/movements/" + id);
    }

    public Movement create(Movement movement) throws Exception {
        if ("ENTRADA".equalsIgnoreCase(movement.getMovementTypeCode())) {
            return createEntrada(movement);
        } else if ("SALIDA".equalsIgnoreCase(movement.getMovementTypeCode())) {
            return createSalida(movement);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento desconocido: " + movement.getMovementTypeCode());
        }
    }

    public Movement createEntrada(Movement movement) throws Exception {
        return apiClient.post("/api/movements/entrada", movement, Movement.class);
    }

    public Movement createSalida(Movement movement) throws Exception {
        return apiClient.post("/api/movements/salida", movement, Movement.class);
    }

    public Double getStock(String productId) throws Exception {
        // Assuming the API returns a JSON object like {"stock": 100.0} or just the number.
        // The plan says: STOCK=$(curl -s "$API_URL/api/stock/$PRODUCT_ID" | jq -r '.stock')
        // So it returns an object.
        // I'll create a small inner class or use a Map.
        java.util.Map<String, Object> response = apiClient.get("/api/stock/" + productId, new TypeReference<java.util.Map<String, Object>>() {});
        Object stock = response.get("stock");
        if (stock instanceof Number) {
            return ((Number) stock).doubleValue();
        }
        return 0.0;
    }
}
