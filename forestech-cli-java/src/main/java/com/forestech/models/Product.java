package com.forestech.models;

import com.forestech.enums.MeasurementUnit;
import com.forestech.utils.IdGenerator;

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

    @Deprecated
    public Product(String name, String unidadDeMedida, double priceXUnd) {
        this(name,
             unidadDeMedida != null ? MeasurementUnit.fromCode(unidadDeMedida) : null,
             priceXUnd);
    }

    public Product(String id, String name, MeasurementUnit measurementUnit, double unitPrice) {
        this.id = id;
        this.name = name;
        this.measurementUnit = measurementUnit;
        this.unitPrice = unitPrice;
    }

    @Deprecated
    public Product(String id, String name, String unidadDeMedida, double priceXUnd) {
        this(id,
             name,
             unidadDeMedida != null ? MeasurementUnit.fromCode(unidadDeMedida) : null,
             priceXUnd);
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

    public void setMeasurementUnitFromCode(String unidadDeMedida) {
        if (unidadDeMedida == null) {
            this.measurementUnit = null;
        } else {
            this.measurementUnit = MeasurementUnit.fromCode(unidadDeMedida);
        }
    }

    @Deprecated
    public String getUnidadDeMedida() {
        return getMeasurementUnitCode();
    }

    @Deprecated
    public void setUnidadDeMedida(String unidadDeMedida) {
        setMeasurementUnitFromCode(unidadDeMedida);
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Deprecated
    public double getPriceXUnd() {
        return getUnitPrice();
    }

    @Deprecated
    public void setPriceXUnd(double priceXUnd) {
        setUnitPrice(priceXUnd);
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
