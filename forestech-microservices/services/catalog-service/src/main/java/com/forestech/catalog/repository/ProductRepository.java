package com.forestech.catalog.repository;

import com.forestech.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Product
 * Reemplaza: com.forestech.modules.catalog.dao.ProductDAO
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Buscar productos por nombre (case-insensitive, parcial)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar productos activos
     */
    List<Product> findByIsActiveTrue();

    /**
     * Buscar por unidad de medida
     */
    List<Product> findByMeasurementUnit(Product.MeasurementUnit unit);

    /**
     * Verificar si existe un producto por nombre (exacto)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Buscar producto activo por ID
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.isActive = true")
    Optional<Product> findActiveById(@Param("id") String id);
}
