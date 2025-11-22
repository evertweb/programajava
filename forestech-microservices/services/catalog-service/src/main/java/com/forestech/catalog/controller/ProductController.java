package com.forestech.catalog.controller;

import com.forestech.catalog.model.Product;
import com.forestech.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para Products
 * API REST para gestión de productos de combustible
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false, defaultValue = "false") boolean onlyActive) {
        log.info("GET /api/products - onlyActive: {}", onlyActive);
        List<Product> products = onlyActive
                ? productService.findAllActive()
                : productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        log.info("GET /api/products/{}", id);
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        log.info("GET /api/products/search?name={}", name);
        List<Product> products = productService.searchByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * ENDPOINT INTERNO - Solo para uso de invoicing-service
     * Los productos solo pueden ser creados al crear facturas
     */
    @PostMapping("/internal")
    public ResponseEntity<Product> createProductInternal(@Valid @RequestBody Product product) {
        log.info("POST /api/products/internal - {} (llamada interna desde invoicing-service)", product.getName());
        Product created = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * ENDPOINT INTERNO - Solo para uso de invoicing-service
     */
    @PutMapping("/internal/{id}")
    public ResponseEntity<Product> updateProductInternal(
            @PathVariable String id,
            @Valid @RequestBody Product product) {
        log.info("PUT /api/products/internal/{} (llamada interna)", id);
        Product updated = productService.update(id, product);
        return ResponseEntity.ok(updated);
    }

    /**
     * ENDPOINT INTERNO - Solo para uso de invoicing-service
     */
    @DeleteMapping("/internal/{id}")
    public ResponseEntity<Void> deleteProductInternal(@PathVariable String id) {
        log.info("DELETE /api/products/internal/{} (llamada interna)", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Exception Handlers
    @ExceptionHandler(ProductService.ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductService.ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProductService.DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProduct(ProductService.DuplicateProductException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String message;
    }
}
