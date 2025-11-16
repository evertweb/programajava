package com.forestech.models.builders;

import com.forestech.enums.MeasurementUnit;
import com.forestech.enums.MovementType;
import com.forestech.models.Movement;
import com.forestech.utils.IdGenerator;

import java.time.LocalDateTime;

/**
 * Builder para crear instancias de {@link Movement} de forma fluida y legible.
 */
public class MovementBuilder {

    private String id;
    private MovementType movementType;
    private String productId;
    private String vehicleId;
    private String invoiceNumber;
    private MeasurementUnit measurementUnit = MeasurementUnit.GALON;
    private double quantity;
    private double unitPrice;
    private LocalDateTime createdAt;

    public MovementBuilder() {
        this.id = IdGenerator.generateMovementId();
        this.createdAt = LocalDateTime.now();
    }

    public MovementBuilder id(String id) {
        this.id = id;
        return this;
    }

    public MovementBuilder type(MovementType movementType) {
        this.movementType = movementType;
        return this;
    }

    /**
     * Compatibilidad para flujos que todavía manejan código en texto.
     */
    public MovementBuilder type(String movementTypeCode) {
        if (movementTypeCode != null) {
            this.movementType = MovementType.fromCode(movementTypeCode);
        }
        return this;
    }

    public MovementBuilder product(String productId) {
        this.productId = productId;
        return this;
    }

    public MovementBuilder vehicle(String vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }

    public MovementBuilder invoice(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public MovementBuilder unit(MeasurementUnit measurementUnit) {
        if (measurementUnit != null) {
            this.measurementUnit = measurementUnit;
        }
        return this;
    }

    public MovementBuilder unit(String measurementUnitCode) {
        if (measurementUnitCode != null) {
            this.measurementUnit = MeasurementUnit.fromCode(measurementUnitCode);
        }
        return this;
    }

    public MovementBuilder quantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public MovementBuilder unitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public MovementBuilder createdAt(LocalDateTime createdAt) {
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
        return this;
    }

    public Movement build() {
        validateRequiredFields();
        LocalDateTime finalCreatedAt = createdAt != null ? createdAt : LocalDateTime.now();
        String finalId = id != null ? id : IdGenerator.generateMovementId();

        return new Movement(
            finalId,
            movementType,
            productId,
            vehicleId,
            invoiceNumber,
            measurementUnit,
            quantity,
            unitPrice,
            finalCreatedAt
        );
    }

    private void validateRequiredFields() {
        if (movementType == null) {
            throw new IllegalStateException("Movement type is required");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalStateException("Product ID is required");
        }
        if (measurementUnit == null) {
            throw new IllegalStateException("Measurement unit is required");
        }
        if (quantity <= 0) {
            throw new IllegalStateException("Quantity must be greater than 0");
        }
        if (unitPrice <= 0) {
            throw new IllegalStateException("Unit price must be greater than 0");
        }
    }
}
