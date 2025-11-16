package com.forestech.models;

import com.forestech.enums.VehicleCategory;
import com.forestech.utils.IdGenerator;

public class Vehicle {
    private final String id;
    private String name;
    private VehicleCategory category;
    private double capacity;
    private String fuelProductId;
    private boolean hasHorometer;

    // ============================================================================
    // CONSTRUCTORES
    // ============================================================================

    /**
     * Constructor para CREAR nuevos vehículos (genera ID automático).
     *
     * @param name           Nombre o placa del vehículo
     * @param category       Categoría: Camión, Excavadora, Motosierra, etc.
     * @param capacity       Capacidad del tanque en litros
     * @param fuelProductId  FK → oil_products.id (ID del combustible que usa)
     * @param haveHorometer  true si tiene horómetro, false si no
     */
    public Vehicle(String name, VehicleCategory category, double capacity, String fuelProductId, boolean hasHorometer) {
        this.id = IdGenerator.generateVehicleId();
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.fuelProductId = fuelProductId;
        this.hasHorometer = hasHorometer;
    }

    @Deprecated
    public Vehicle(String name, String category, double capacity, String fuelProductId, boolean haveHorometer) {
        this(name,
             category != null ? VehicleCategory.fromCode(category) : null,
             capacity,
             fuelProductId,
             haveHorometer);
    }

    /**
     * Constructor para CARGAR desde la base de datos (usa ID existente).
     *
     * @param id             ID existente del vehículo
     * @param name           Nombre o placa
     * @param category       Categoría
     * @param capacity       Capacidad en litros
     * @param fuelProductId  FK → oil_products.id
     * @param haveHorometer  Tiene horómetro
     */
    public Vehicle(String id, String name, VehicleCategory category, double capacity,
                   String fuelProductId, boolean hasHorometer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.fuelProductId = fuelProductId;
        this.hasHorometer = hasHorometer;
    }

    @Deprecated
    public Vehicle(String id, String name, String category, double capacity,
                   String fuelProductId, boolean haveHorometer) {
        this(id,
             name,
             category != null ? VehicleCategory.fromCode(category) : null,
             capacity,
             fuelProductId,
             haveHorometer);
    }

    // ============================================================================
    // GETTERS Y SETTERS
    // ============================================================================

    public String getFuelProductId() {
        return fuelProductId;
    }

    public void setFuelProductId(String fuelProductId) {
        this.fuelProductId = fuelProductId;
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

    public VehicleCategory getCategory() {
        return category;
    }

    public String getCategoryCode() {
        return category != null ? category.getCode() : null;
    }

    public void setCategory(VehicleCategory category) {
        this.category = category;
    }

    public void setCategoryFromCode(String categoryCode) {
        if (categoryCode == null) {
            this.category = null;
        } else {
            this.category = VehicleCategory.fromCode(categoryCode);
        }
    }

    @Deprecated
    public void setCategory(String category) {
        setCategoryFromCode(category);
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public boolean hasHorometer() {
        return hasHorometer;
    }

    public void setHasHorometer(boolean hasHorometer) {
        this.hasHorometer = hasHorometer;
    }

    @Deprecated
    public boolean isHaveHorometer() {
        return hasHorometer();
    }

    @Deprecated
    public void setHaveHorometer(boolean haveHorometer) {
        setHasHorometer(haveHorometer);
    }

    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL VEHICULO               │\n" +
                "├────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:                " + id + "\n" +
                "│ 📌 Nombre:            " + name + "\n" +
                "│ ⛽ Combustible ID:     " + fuelProductId + "\n" +
                "│ 📦 Categoría:         " + getCategoryCode() + "\n" +
                "│ 💾 Capacidad (lts):   " + capacity + "\n" +
                "│ ⏱️  Horómetro:         " + (hasHorometer ? "Sí" : "No") + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }

}
