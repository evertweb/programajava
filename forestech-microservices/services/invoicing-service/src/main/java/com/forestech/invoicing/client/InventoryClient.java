package com.forestech.invoicing.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "inventory-service")
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
