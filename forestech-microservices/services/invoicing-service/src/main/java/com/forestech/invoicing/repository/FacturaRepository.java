package com.forestech.invoicing.repository;

import com.forestech.invoicing.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
}
