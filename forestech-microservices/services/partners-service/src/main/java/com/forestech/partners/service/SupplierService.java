package com.forestech.partners.service;

import com.forestech.partners.model.Supplier;
import com.forestech.partners.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> findById(String id) {
        return supplierRepository.findById(id);
    }

    public Optional<Supplier> findByNit(String nit) {
        return supplierRepository.findByNit(nit);
    }

    @Transactional
    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteById(String id) {
        supplierRepository.deleteById(id);
    }
}
