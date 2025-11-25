package com.forestech.inventory.client;

import com.forestech.shared.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", fallback = CatalogClientFallback.class)
public interface CatalogClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable String id);
}

@Component
@Slf4j
class CatalogClientFallback implements CatalogClient {
    @Override
    public ProductDTO getProductById(String id) {
        log.error("Fallback activado: catalog-service no disponible para producto {}", id);
        throw new ServiceUnavailableException("catalog-service", 
            "El servicio de catálogo no está disponible. No se puede validar el producto: " + id);
    }
}
