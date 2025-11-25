package com.forestech.invoicing.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", fallback = InventoryClientFallback.class)
public interface InventoryClient {
    /**
     * Endpoint interno para registrar entrada de stock
     * Solo invoicing-service puede crear movimientos ENTRADA (al crear facturas)
     */
    @PostMapping("/api/movements/internal/entrada")
    MovementDTO registrarEntrada(@RequestBody MovementRequest request);

    /**
     * Endpoint interno para eliminar movimientos
     * Solo invoicing-service puede eliminar movimientos (al anular facturas)
     */
    @DeleteMapping("/api/movements/internal/{id}")
    void deleteMovement(@PathVariable("id") String id);
}

@Component
@Slf4j
class InventoryClientFallback implements InventoryClient {
    
    @Override
    public MovementDTO registrarEntrada(MovementRequest request) {
        log.error("Fallback activado: inventory-service no disponible para registrar entrada");
        throw new ServiceUnavailableException("inventory-service",
            "El servicio de inventario no está disponible. No se puede registrar la entrada de stock.");
    }

    @Override
    public void deleteMovement(String id) {
        log.error("Fallback activado: inventory-service no disponible para eliminar movimiento {}", id);
        throw new ServiceUnavailableException("inventory-service",
            "El servicio de inventario no está disponible. No se puede eliminar el movimiento: " + id);
    }
}
