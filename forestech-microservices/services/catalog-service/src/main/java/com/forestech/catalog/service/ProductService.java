package com.forestech.catalog.service;

import com.forestech.catalog.model.Product;
import com.forestech.catalog.repository.ProductRepository;
import com.forestech.shared.exception.DuplicateEntityException;
import com.forestech.shared.service.BaseService;
import com.forestech.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para Product
 * Migrado desde: com.forestech.modules.catalog.services.ProductServices
 * REFACTORIZADO: Ahora extiende BaseService para reducir código duplicado
 * 
 * CACHE STRATEGY:
 * - products: Cache de productos individuales por ID (TTL: 30min)
 * - productsActive: Lista de productos activos (TTL: 10min)
 * - productSearch: Resultados de búsqueda (TTL: 5min)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService extends BaseService<Product, String> {

    private final ProductRepository productRepository;

    @Override
    protected JpaRepository<Product, String> getRepository() {
        return productRepository;
    }

    @Override
    protected String getEntityName() {
        return "Product";
    }

    @Override
    protected void updateFields(Product existing, Product newData) {
        // Actualizar campos del producto
        existing.setName(newData.getName());
        existing.setMeasurementUnit(newData.getMeasurementUnit());
        existing.setUnitPrice(newData.getUnitPrice());
        existing.setDescription(newData.getDescription());
        existing.setPresentation(newData.getPresentation());
    }

    @Override
    protected void beforeCreate(Product product) {
        // Validar que no exista producto con el mismo nombre
        if (productRepository.existsByNameIgnoreCase(product.getName())) {
            throw new DuplicateEntityException("Ya existe un producto con el nombre: " + product.getName());
        }

        // Generar ID si no tiene
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(IdGenerator.generate("PROD"));
        }

        // Asegurar que está activo por defecto
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
    }

    // ============================================
    // Métodos con Cache
    // ============================================

    /**
     * Obtener producto por ID con cache
     */
    @Override
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public Product findById(String id) {
        log.info("Buscando producto por ID: {} (cache miss)", id);
        return super.findById(id);
    }

    /**
     * Listar solo productos activos (con cache)
     */
    @Cacheable(value = "productsActive")
    @Transactional(readOnly = true)
    public List<Product> findAllActive() {
        log.info("Listando productos activos (cache miss)");
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Buscar productos por nombre (con cache)
     */
    @Cacheable(value = "productSearch", key = "#name")
    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        log.info("Buscando productos por nombre: {} (cache miss)", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Crear producto - invalida caches de listas
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "productsActive", allEntries = true),
            @CacheEvict(value = "productSearch", allEntries = true)
    })
    @Transactional
    public Product create(Product product) {
        log.info("Creando producto - invalidando caches de listas");
        return super.create(product);
    }

    /**
     * Actualizar producto - invalida caches
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "productsActive", allEntries = true),
            @CacheEvict(value = "productSearch", allEntries = true)
    })
    @Transactional
    public Product update(String id, Product product) {
        log.info("Actualizando producto ID: {} - invalidando caches", id);
        return super.update(id, product);
    }

    /**
     * Eliminar producto (soft delete) - invalida caches
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "productsActive", allEntries = true),
            @CacheEvict(value = "productSearch", allEntries = true)
    })
    @Transactional
    public void delete(String id) {
        log.info("Eliminando producto con ID: {} (soft delete) - invalidando caches", id);
        Product product = super.findById(id);
        product.setIsActive(false);
        getRepository().save(product);
        log.info("Producto marcado como inactivo");
    }

    /**
     * Eliminar producto (hard delete) - invalida caches
     */
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "productsActive", allEntries = true),
            @CacheEvict(value = "productSearch", allEntries = true)
    })
    @Transactional
    public void hardDelete(String id) {
        log.info("Eliminación permanente de producto con ID: {} - invalidando caches", id);
        Product product = super.findById(id);
        beforeDelete(product);
        getRepository().delete(product);
    }
}
