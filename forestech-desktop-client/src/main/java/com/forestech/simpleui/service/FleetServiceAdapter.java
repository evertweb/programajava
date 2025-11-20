package com.forestech.simpleui.service;

import com.forestech.simpleui.model.Vehicle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * FleetServiceAdapter
 * Bridge between UI and the Fleet Microservice.
 */
public class FleetServiceAdapter {

    private final ServiceClient client;
    private final ObjectMapper mapper;

    // Assuming Gateway at 8080
    private static final String BASE_URL = "http://localhost:8080";

    public FleetServiceAdapter() {
        this.client = new ServiceClient(BASE_URL, "Flota");
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Vehicle> getAllVehicles() throws Exception {
        // Endpoint: /api/vehicles
        String json = client.get("/api/vehicles");
        return mapper.readValue(json, new TypeReference<List<Vehicle>>() {
        });
    }

    public void createVehicle(Vehicle vehicle) throws Exception {
        String json = mapper.writeValueAsString(vehicle);
        client.post("/api/vehicles", json);
    }

    public void updateVehicle(String id, Vehicle vehicle) throws Exception {
        String json = mapper.writeValueAsString(vehicle);
        client.put("/api/vehicles/" + id, json);
    }

    public void deleteVehicle(String id) throws Exception {
        client.delete("/api/vehicles/" + id);
    }
}
