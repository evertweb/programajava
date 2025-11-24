package com.forestech.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Data
public class Movement {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type")
    private MovementType movementType;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "invoice_id")
    private String invoiceId;

    private BigDecimal quantity;

    @Column(name = "remaining_quantity")
    private BigDecimal remainingQuantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "real_unit_price")
    private BigDecimal realUnitPrice;

    private BigDecimal subtotal;

    @Column(name = "real_cost")
    private BigDecimal realCost;

    private String description;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MovementType {
        ENTRADA, SALIDA
    }
}
