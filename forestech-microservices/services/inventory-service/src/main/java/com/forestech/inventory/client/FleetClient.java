package com.forestech.inventory.client;

import com.forestech.shared.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class FleetClientFallback implements FleetClient {
    @Override
    public VehicleDTO getVehicleById(String id) {
        log.error("Fallback activado: fleet-service no disponible para vehículo {}", id);
        throw new ServiceUnavailableException("fleet-service",
            "El servicio de flota no está disponible. No se puede validar el vehículo: " + id);
    }
}
