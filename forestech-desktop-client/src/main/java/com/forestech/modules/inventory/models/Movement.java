package com.forestech.modules.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.forestech.core.AppConfig;
import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.shared.enums.MovementType;
import com.forestech.shared.utils.IdGenerator;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movement {

    private String id;
    private MovementType movementType;
    private String productId;
    private String vehicleId;
    private String invoiceNumber;
    private MeasurementUnit measurementUnit;
    private double quantity;
    private double unitPrice;
    private LocalDateTime createdAt;

    private String productName;
    private String productCategory;
    private String vehiclePlate;
    private String vehicleType;

    public Movement(MovementType movementType, String productId, String vehicleId, String invoiceNumber,
            MeasurementUnit measurementUnit, double quantity, double unitPrice) {
        this.id = IdGenerator.generateMovementId();
        this.movementType = movementType;
        this.productId = productId;
        this.vehicleId = vehicleId;
        this.invoiceNumber = invoiceNumber;
        this.measurementUnit = measurementUnit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = LocalDateTime.now();
    }

    public Movement(String id, MovementType movementType, String productId,
            String vehicleId, String invoiceNumber, MeasurementUnit measurementUnit,
            double quantity, double unitPrice, LocalDateTime createdAt) {
        this.id = id;
        this.movementType = movementType;
        this.productId = productId;
        this.vehicleId = vehicleId;
        this.invoiceNumber = invoiceNumber;
        this.measurementUnit = measurementUnit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

    public Movement() {
        this.id = IdGenerator.generateMovementId();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public String getMovementTypeCode() {
        return movementType != null ? movementType.getCode() : null;
    }

    public void setMovementType(@NotNull MovementType movementType) {
        this.movementType = movementType;
    }

    public void setMovementTypeFromCode(String movementTypeCode) {
        if (movementTypeCode == null) {
            this.movementType = null;
        } else {
            this.movementType = MovementType.fromCode(movementTypeCode);
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public String getMeasurementUnitCode() {
        return measurementUnit != null ? measurementUnit.getCode() : null;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public void setMeasurementUnitFromCode(String measurementUnitCode) {
        if (measurementUnitCode == null) {
            this.measurementUnit = null;
        } else {
            this.measurementUnit = MeasurementUnit.fromCode(measurementUnitCode);
        }
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        } else {
            System.out.println("CANTIDAD NO VALIDA");
        }
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice > 0) {
            this.unitPrice = unitPrice;
        } else {
            System.out.println("PRECIO NO VALIDO");
        }
    }

    public double getPricePerUnit() {
        return unitPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id='" + id + '\'' +
                ", movementType='" + getMovementTypeCode() + '\'' +
                ", productId='" + productId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", measurementUnit='" + getMeasurementUnitCode() + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
