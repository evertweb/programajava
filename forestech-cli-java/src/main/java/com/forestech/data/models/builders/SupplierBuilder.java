package com.forestech.data.models.builders;

import com.forestech.data.models.Supplier;
import com.forestech.shared.utils.IdGenerator;

/**
 * Builder sencillo para {@link Supplier}.
 */
public class SupplierBuilder {

    private String id;
    private String name;
    private String nit;
    private String telephone;
    private String email;
    private String address;

    public SupplierBuilder() {
        this.id = IdGenerator.generateSupplierId();
    }

    public SupplierBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SupplierBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SupplierBuilder nit(String nit) {
        this.nit = nit;
        return this;
    }

    public SupplierBuilder telephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public SupplierBuilder email(String email) {
        this.email = email;
        return this;
    }

    public SupplierBuilder address(String address) {
        this.address = address;
        return this;
    }

    public Supplier build() {
        validate();
        String finalId = id != null ? id : IdGenerator.generateSupplierId();
        return new Supplier(finalId, name, nit, telephone, email, address);
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Supplier name is required");
        }
        if (nit == null || nit.trim().isEmpty()) {
            throw new IllegalStateException("Supplier NIT is required");
        }
    }
}
