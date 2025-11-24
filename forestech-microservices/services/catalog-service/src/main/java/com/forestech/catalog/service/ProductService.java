package com.forestech.catalog.service;

import com.forestech.catalog.model.Product;
import com.forestech.catalog.repository.ProductRepository;
import com.forestech.shared.exception.DuplicateEntityException;
import com.forestech.shared.service.BaseService;
import com.forestech.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para Product
 * Migrado desde: com.forestech.modules.catalog.services.ProductServices
 * REFACTORIZADO: Ahora extiende BaseService para reducir código duplicado
 * 
 * ANTES: 144 líneas
 * DESPUÉS: ~80 líneas (-44%)
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
    // Métodos custom específicos de Product
    // ============================================

    /**
     * Listar solo productos activos
     */
    @Transactional(readOnly = true)
    public List<Product> findAllActive() {
        log.info("Listando productos activos");
        return productRepository.findByIsActiveTrue();
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
     * Eliminar producto (soft delete)
     */
    @Override
    @Transactional
    public void delete(String id) {
        log.info("Eliminando producto con ID: {} (soft delete)", id);
        Product product = findById(id);
        product.setIsActive(false);
        getRepository().save(product);
        log.info("Producto marcado como inactivo");
    }

    /**
     * Eliminar producto (hard delete)
     */
    @Transactional
    public void hardDelete(String id) {
        log.info("Eliminación permanente de producto con ID: {}", id);
        Product product = findById(id);
        beforeDelete(product);
        getRepository().delete(product);
    }
}
