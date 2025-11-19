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
    @PostMapping("/api/movements/entrada")
    MovementDTO registrarEntrada(@RequestBody MovementRequest request);

    @DeleteMapping("/api/movements/{id}")
    void deleteMovement(@PathVariable("id") String id);
}
