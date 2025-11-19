package com.forestech.modules.partners.models;

import com.forestech.shared.utils.IdGenerator;

/**
 * Modelo que representa un proveedor de combustible.
 *
 * @author Forestech Development Team
 * @version 2.0 (con CRUD completo)
 * @since Fase 4
 */
public class Supplier {
    // ============================================================================
    // ATRIBUTOS
    // ============================================================================
    private final String id; // ID es final porque nunca cambia
    private String name;
    private String nit;
    private String telephone;
    private String email;
    private String address;

    // ============================================================================
    // CONSTRUCTORES
    // ============================================================================

    /**
     * Constructor para CREAR nuevos proveedores (genera ID automático).
     *
     * @param name      Nombre del proveedor
     * @param nit       NIT del proveedor (único)
     * @param telephone Teléfono de contacto
     * @param email     Correo electrónico
     * @param address   Dirección física
     */
    public Supplier(String name, String nit, String telephone, String email, String address) {
        this.id = IdGenerator.generateSupplierId();
        this.name = name;
        this.nit = nit;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
    }

    /**
     * Constructor para CARGAR desde la base de datos (usa ID existente).
     *
     * @param id        ID existente del proveedor
     * @param name      Nombre del proveedor
     * @param nit       NIT del proveedor
     * @param telephone Teléfono
     * @param email     Correo
     * @param address   Dirección
     */
    public Supplier(String id, String name, String nit, String telephone, String email, String address) {
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
    }

    // ============================================================================
    // GETTERS Y SETTERS
    // ============================================================================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ============================================================================
    // MÉTODOS
    // ============================================================================

    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL PROVEEDOR              │\n" +
                "├────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:        " + id + "\n" +
                "│ 📌 Nombre:    " + name + "\n" +
                "│ 🏢 NIT:       " + nit + "\n" +
                "│ 📞 Teléfono:  " + (telephone != null ? telephone : "N/A") + "\n" +
                "│ 📧 Email:     " + (email != null ? email : "N/A") + "\n" +
                "│ 📍 Dirección: " + (address != null ? address : "N/A") + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }
}
