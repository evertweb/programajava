package com.forestech.invoicing.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Collections;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalog-service", fallback = CatalogClientFallback.class)
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

@Component
@Slf4j
class CatalogClientFallback implements CatalogClient {
    
    @Override
    public ProductDTO getProductById(String id) {
        log.error("Fallback activado: catalog-service no disponible para producto {}", id);
        throw new ServiceUnavailableException("catalog-service",
            "El servicio de catálogo no está disponible. No se puede obtener el producto: " + id);
    }

    @Override
    public java.util.List<ProductDTO> searchProducts(String name) {
        log.error("Fallback activado: catalog-service no disponible para búsqueda: {}", name);
        throw new ServiceUnavailableException("catalog-service",
            "El servicio de catálogo no está disponible. No se puede buscar productos.");
    }

    @Override
    public ProductDTO createProduct(ProductDTO product) {
        log.error("Fallback activado: catalog-service no disponible para crear producto");
        throw new ServiceUnavailableException("catalog-service",
            "El servicio de catálogo no está disponible. No se puede crear el producto.");
    }
}
