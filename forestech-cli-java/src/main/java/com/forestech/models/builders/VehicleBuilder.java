package com.forestech.models.builders;

import com.forestech.enums.VehicleCategory;
import com.forestech.models.Vehicle;
import com.forestech.utils.IdGenerator;

/**
 * Builder para {@link Vehicle} que facilita la lectura en los controladores.
 */
public class VehicleBuilder {

    private String id;
    private String name;
    private VehicleCategory category;
    private double capacity;
    private String fuelProductId;
    private boolean hasHorometer;

    public VehicleBuilder() {
        this.id = IdGenerator.generateVehicleId();
    }

    public VehicleBuilder id(String id) {
        this.id = id;
        return this;
    }

    public VehicleBuilder name(String name) {
        this.name = name;
        return this;
    }

    public VehicleBuilder category(VehicleCategory category) {
        this.category = category;
        return this;
    }

    public VehicleBuilder category(String categoryCode) {
        if (categoryCode != null) {
            this.category = VehicleCategory.fromCode(categoryCode);
        }
        return this;
    }

    public VehicleBuilder capacity(double capacity) {
        this.capacity = capacity;
        return this;
    }

    public VehicleBuilder fuelProduct(String fuelProductId) {
        this.fuelProductId = fuelProductId;
        return this;
    }

    public VehicleBuilder hasHorometer(boolean hasHorometer) {
        this.hasHorometer = hasHorometer;
        return this;
    }

    public Vehicle build() {
        validate();
        String finalId = id != null ? id : IdGenerator.generateVehicleId();
        return new Vehicle(finalId, name, category, capacity, fuelProductId, hasHorometer);
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Vehicle name is required");
        }
        if (category == null) {
            throw new IllegalStateException("Vehicle category is required");
        }
        if (capacity <= 0) {
            throw new IllegalStateException("Vehicle capacity must be positive");
        }
        if (fuelProductId == null || fuelProductId.trim().isEmpty()) {
            throw new IllegalStateException("Fuel product ID is required");
        }
    }
}
