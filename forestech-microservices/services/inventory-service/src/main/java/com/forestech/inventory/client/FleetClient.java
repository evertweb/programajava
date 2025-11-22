package com.forestech.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fleet-service", fallback = FleetClientFallback.class)
public interface FleetClient {

    @GetMapping("/api/vehicles/{id}")
    VehicleDTO getVehicleById(@PathVariable String id);
}

@Component
class FleetClientFallback implements FleetClient {
    @Override
    public VehicleDTO getVehicleById(String id) {
        return null;
    }
}
