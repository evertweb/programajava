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

    private BigDecimal subtotal;

    private String description;

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
