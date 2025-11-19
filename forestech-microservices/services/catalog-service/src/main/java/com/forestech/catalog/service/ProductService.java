package com.forestech.catalog.service;

import com.forestech.catalog.model.Product;
import com.forestech.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de lógica de negocio para Product
 * Migrado desde: com.forestech.modules.catalog.services.ProductServices
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Listar todos los productos
     */
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.info("Listando todos los productos");
        return productRepository.findAll();
    }

    /**
     * Listar solo productos activos
     */
    @Transactional(readOnly = true)
    public List<Product> findAllActive() {
        log.info("Listando productos activos");
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Buscar producto por ID
     */
    @Transactional(readOnly = true)
    public Product findById(String id) {
        log.info("Buscando producto con ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado: " + id));
    }

    /**
     * Buscar productos por nombre
     */
    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        log.info("Buscando productos por nombre: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Crear nuevo producto
     */
    @Transactional
    public Product create(Product product) {
        log.info("Creando nuevo producto: {}", product.getName());

        // Validar que no exista producto con el mismo nombre
        if (productRepository.existsByNameIgnoreCase(product.getName())) {
            throw new DuplicateProductException("Ya existe un producto con el nombre: " + product.getName());
        }

        // Generar ID si no tiene
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId("PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Asegurar que está activo
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }

        Product saved = productRepository.save(product);
        log.info("Producto creado exitosamente con ID: {}", saved.getId());
        return saved;
    }

    /**
     * Actualizar producto existente
     */
    @Transactional
    public Product update(String id, Product productData) {
        log.info("Actualizando producto con ID: {}", id);

        Product existing = findById(id);

        // Actualizar campos
        existing.setName(productData.getName());
        existing.setMeasurementUnit(productData.getMeasurementUnit());
        existing.setUnitPrice(productData.getUnitPrice());
        existing.setDescription(productData.getDescription());

        Product updated = productRepository.save(existing);
        log.info("Producto actualizado exitosamente");
        return updated;
    }

    /**
     * Eliminar producto (soft delete)
     */
    @Transactional
    public void delete(String id) {
        log.info("Eliminando producto con ID: {}", id);

        Product product = findById(id);
        product.setIsActive(false);
        productRepository.save(product);

        log.info("Producto marcado como inactivo");
    }

    /**
     * Eliminar producto (hard delete)
     */
    @Transactional
    public void hardDelete(String id) {
        log.info("Eliminación permanente de producto con ID: {}", id);
        productRepository.deleteById(id);
    }

    // Excepciones personalizadas
    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateProductException extends RuntimeException {
        public DuplicateProductException(String message) {
            super(message);
        }
    }
}
