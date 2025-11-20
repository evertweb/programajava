package com.forestech.presentation.clients;

import com.forestech.modules.fleet.models.Vehicle;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class VehicleServiceClient {
    
    private final ApiClient apiClient;
    
    public VehicleServiceClient() {
        this.apiClient = new ApiClient();
    }
    
    public List<Vehicle> findAll() throws Exception {
        return apiClient.get("/api/vehicles", new TypeReference<List<Vehicle>>() {});
    }
    
    public Vehicle findById(String id) throws Exception {
        return apiClient.get("/api/vehicles/" + id, Vehicle.class);
    }
    
    public Vehicle create(Vehicle vehicle) throws Exception {
        return apiClient.post("/api/vehicles", vehicle, Vehicle.class);
    }
    
    public void delete(String id) throws Exception {
        apiClient.delete("/api/vehicles/" + id);
    }
    
    public void update(Vehicle vehicle) throws Exception {
        apiClient.put("/api/vehicles/" + vehicle.getId(), vehicle, Vehicle.class);
    }
}
