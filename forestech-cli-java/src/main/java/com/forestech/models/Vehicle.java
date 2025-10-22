package com.forestech.models;

import com.forestech.utils.IdGenerator;

public class Vehicle {
    private final String id;
    private String name;
    private String category;
    private double capacity;
    private String fuelType;

    public Vehicle(String name, String category, double capacity, String fuelType) {
        this.id = IdGenerator.generateVehicleId();
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.fuelType = fuelType;

    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
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

    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL VEHICULO               │\n" +
                "├────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:                " + id + "\n" +
                "│ 📌 Nombre:            " + name + "\n" +
                "│ ⛽ Combustible:        " + fuelType + "\n" +
                "│ 📦 Categoría:         " + category + "\n" +
                "│ 💾 Capacidad (lts):   " + capacity + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }

}
