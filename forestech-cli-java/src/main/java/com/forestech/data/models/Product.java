package com.forestech.data.models;

import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.shared.utils.IdGenerator;

public class Product {
    private final String id;
    private String name;
    private MeasurementUnit measurementUnit;
    private double unitPrice;

    public Product(String name, MeasurementUnit measurementUnit, double unitPrice) {
        this.id = IdGenerator.generateFuelId();
        this.name = name;
        this.measurementUnit = measurementUnit;
        this.unitPrice = unitPrice;
    }

    public Product(String id, String name, MeasurementUnit measurementUnit, double unitPrice) {
        this.id = id;
        this.name = name;
        this.measurementUnit = measurementUnit;
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", measurementUnit='" + getMeasurementUnitCode() + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }

}
