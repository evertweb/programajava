package com.forestech.data.models.builders;

import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.modules.catalog.models.Product;
import com.forestech.shared.utils.IdGenerator;

/**
 * Builder para {@link Product} con nombres autoexplicativos.
 */
public class ProductBuilder {

    private String id;
    private String name;
    private MeasurementUnit measurementUnit = MeasurementUnit.GALON;
    private double unitPrice;

    public ProductBuilder() {
        this.id = IdGenerator.generateFuelId();
    }

    public ProductBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ProductBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder measurementUnit(MeasurementUnit measurementUnit) {
        if (measurementUnit != null) {
            this.measurementUnit = measurementUnit;
        }
        return this;
    }

    public ProductBuilder measurementUnit(String measurementUnitCode) {
        if (measurementUnitCode != null) {
            this.measurementUnit = MeasurementUnit.fromCode(measurementUnitCode);
        }
        return this;
    }

    public ProductBuilder unitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public Product build() {
        validate();
        String finalId = id != null ? id : IdGenerator.generateFuelId();
        return new Product(finalId, name, measurementUnit, unitPrice);
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Product name is required");
        }
        if (measurementUnit == null) {
            throw new IllegalStateException("Measurement unit is required");
        }
        if (unitPrice <= 0) {
            throw new IllegalStateException("Unit price must be positive");
        }
    }
}
