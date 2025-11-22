package com.forestech.invoicing.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") String id);

    @GetMapping("/api/products/search")
    java.util.List<ProductDTO> searchProducts(@org.springframework.web.bind.annotation.RequestParam("name") String name);

    /**
     * Endpoint interno para crear productos
     * Solo invoicing-service puede crear productos (al crear facturas)
     */
    @PostMapping("/api/products/internal")
    ProductDTO createProduct(@RequestBody ProductDTO product);
}
