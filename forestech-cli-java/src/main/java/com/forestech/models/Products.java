package com.forestech.models;

import com.forestech.utils.IdGenerator;

public class Products {
    private final String id;
    private String name;
    private String unidadDeMedida;
    private double priceXUnd;

    public Products(String name, String unidadDeMedida, double priceXUnd) {
        this.id = IdGenerator.generateFuelId();
        this.name = name;
        this.unidadDeMedida = unidadDeMedida;
        this.priceXUnd = priceXUnd;
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

    public String getUnidadDeMedida() {
        return unidadDeMedida;
    }

    public void setUnidadDeMedida(String unidadDeMedida) {
        this.unidadDeMedida = unidadDeMedida;
    }

    public double getPriceXUnd() {
        return priceXUnd;
    }

    public void setPriceXUnd(double priceXUnd) {
        this.priceXUnd = priceXUnd;
    }

    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL PRODUCTO               │\n" +
                "├────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:                " + id + "\n" +
                "│ 📌 Nombre:            " + name + "\n" +
                "│ ⛽ Unidad de medida:        " + unidadDeMedida + "\n" +
                "│ 📦 Precio x Galon:         " + priceXUnd + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }

}
