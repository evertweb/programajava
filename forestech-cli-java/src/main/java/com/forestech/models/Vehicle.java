package com.forestech.models;

import com.forestech.utils.IdGenerator;

public class Vehicle {
    // ============================================================================
    // ATRIBUTOS
    // ============================================================================
    private final String id;
    private String name;
    private String category;
    private double capacity;
    private String fuelProductId;  // FK → oil_products.id
    private boolean haveHorometer;

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
    public Vehicle(String name, String category, double capacity, String fuelProductId, boolean haveHorometer) {
        this.id = IdGenerator.generateVehicleId();
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.fuelProductId = fuelProductId;
        this.haveHorometer = haveHorometer;
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
    public Vehicle(String id, String name, String category, double capacity,
                   String fuelProductId, boolean haveHorometer) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.fuelProductId = fuelProductId;
        this.haveHorometer = haveHorometer;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public boolean isHaveHorometer() {
        return haveHorometer;
    }

    public void setHaveHorometer(boolean haveHorometer) {
        this.haveHorometer = haveHorometer;
    }

    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL VEHICULO               │\n" +
                "├────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:                " + id + "\n" +
                "│ 📌 Nombre:            " + name + "\n" +
                "│ ⛽ Combustible ID:     " + fuelProductId + "\n" +
                "│ 📦 Categoría:         " + category + "\n" +
                "│ 💾 Capacidad (lts):   " + capacity + "\n" +
                "│ ⏱️  Horómetro:         " + (haveHorometer ? "Sí" : "No") + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }

}
