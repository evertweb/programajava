package com.forestech.catalog.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Product - Productos de combustible
 * Migrado desde: com.forestech.modules.catalog.models.Product
 */
@Entity
@Table(name = "oil_products", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(length = 50)
    private String id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "La unidad de medida es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_unit", nullable = false)
    private MeasurementUnit measurementUnit;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MeasurementUnit {
        LITROS, GALONES, BARRILES, GALON, CANECA, CUARTO, UNIDAD
    }
}
