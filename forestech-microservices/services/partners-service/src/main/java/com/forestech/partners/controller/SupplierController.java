package com.forestech.partners.controller;

import com.forestech.partners.model.Supplier;
import com.forestech.partners.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable String id) {
        // BaseService.findById() throws exception if not found, no need for Optional
        Supplier supplier = supplierService.findById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/nit/{nit}")
    public ResponseEntity<Supplier> getSupplierByNit(@PathVariable String nit) {
        return supplierService.findByNit(nit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable String id, @Valid @RequestBody Supplier supplier) {
        Supplier updated = supplierService.update(id, supplier);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
