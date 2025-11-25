package com.forestech.invoicing.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "partners-service", fallback = PartnersClientFallback.class)
public interface PartnersClient {
    @GetMapping("/api/suppliers/{id}")
    SupplierDTO getSupplierById(@PathVariable("id") String id);
}

@Component
@Slf4j
class PartnersClientFallback implements PartnersClient {
    @Override
    public SupplierDTO getSupplierById(String id) {
        log.error("Fallback activado: partners-service no disponible para proveedor {}", id);
        throw new ServiceUnavailableException("partners-service",
            "El servicio de proveedores no está disponible. No se puede validar el proveedor: " + id);
    }
}
