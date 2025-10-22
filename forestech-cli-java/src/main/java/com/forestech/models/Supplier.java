package com.forestech.models;

import com.forestech.utils.IdGenerator;

public class Supplier {
    private final String id;
    private final String name;
    private final String telephone;
    private final String email;
    private final String address;


    public Supplier(String name, String telephone, String email, String address) {
        this.id = IdGenerator.generateSupplierId();
        this.name = name;

        this.telephone = telephone;
        this.email = email;
        this.address = address;
    }


    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL PROVEEDOR              │\n" +
                "├────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:                " + id + "\n" +
                "│ 📌 Nombre:            " + name + "\n" +
                "│ ⛽ Telefono:        " + telephone + "\n" +
                "│ 📦 email:         " + email + "\n" +
                "│ 💾 DIreccion :   " + address + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }


}
