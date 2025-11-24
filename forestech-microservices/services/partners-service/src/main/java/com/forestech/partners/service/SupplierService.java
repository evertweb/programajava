package com.forestech.partners.service;

import com.forestech.partners.model.Supplier;
import com.forestech.partners.repository.SupplierRepository;
import com.forestech.shared.service.BaseService;
import com.forestech.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de lógica de negocio para Supplier
 * REFACTORIZADO: Ahora extiende BaseService para reducir código duplicado
 * 
 * ANTES: 40 líneas
 * DESPUÉS: ~30 líneas (-25%)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService extends BaseService<Supplier, String> {

    private final SupplierRepository supplierRepository;

    @Override
    protected JpaRepository<Supplier, String> getRepository() {
        return supplierRepository;
    }

    @Override
    protected String getEntityName() {
        return "Proveedor";
    }

    @Override
    protected void updateFields(Supplier existing, Supplier newData) {
        // Actualizar campos del proveedor
        existing.setNit(newData.getNit());
        existing.setName(newData.getName());
        existing.setTelephone(newData.getTelephone());
        existing.setEmail(newData.getEmail());
        existing.setAddress(newData.getAddress());
    }

    @Override
    protected void beforeCreate(Supplier supplier) {
        // Generar ID si no tiene
        if (supplier.getId() == null || supplier.getId().isEmpty()) {
            supplier.setId(IdGenerator.generate("SUP"));
        }
    }

    // Método custom específico de proveedor
    public Optional<Supplier> findByNit(String nit) {
        log.info("Buscando proveedor por NIT: {}", nit);
        return supplierRepository.findByNit(nit);
    }
}
