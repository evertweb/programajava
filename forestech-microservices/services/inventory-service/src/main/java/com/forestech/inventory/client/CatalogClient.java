package com.forestech.inventory.client;

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
class CatalogClientFallback implements CatalogClient {
    @Override
    public ProductDTO getProductById(String id) {
        return null;
    }
}
