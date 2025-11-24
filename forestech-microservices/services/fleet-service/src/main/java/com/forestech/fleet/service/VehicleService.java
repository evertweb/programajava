package com.forestech.fleet.service;

import com.forestech.fleet.model.Vehicle;
import com.forestech.fleet.repository.VehicleRepository;
import com.forestech.shared.exception.DuplicateEntityException;
import com.forestech.shared.service.BaseService;
import com.forestech.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para Vehicle
 * REFACTORIZADO: Ahora extiende BaseService para reducir código duplicado
 * 
 * ANTES: 104 líneas
 * DESPUÉS: ~70 líneas (-33%)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService extends BaseService<Vehicle, String> {

    private final VehicleRepository vehicleRepository;

    @Override
    protected JpaRepository<Vehicle, String> getRepository() {
        return vehicleRepository;
    }

    @Override
    protected String getEntityName() {
        return "Vehículo";
    }

    @Override
    protected void updateFields(Vehicle existing, Vehicle newData) {
        // Actualizar campos del vehículo
        existing.setPlaca(newData.getPlaca());
        existing.setMarca(newData.getMarca());
        existing.setModelo(newData.getModelo());
        existing.setAnio(newData.getAnio());
        existing.setCategory(newData.getCategory());
        existing.setDescripcion(newData.getDescripcion());
    }

    @Override
    protected void beforeCreate(Vehicle vehicle) {
        // Validar que no exista vehículo con la misma placa
        if (vehicleRepository.existsByPlacaIgnoreCase(vehicle.getPlaca())) {
            throw new DuplicateEntityException("Ya existe un vehículo con la placa: " + vehicle.getPlaca());
        }

        // Generar ID si no tiene
        if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
            vehicle.setId(IdGenerator.generate("VEH"));
        }

        // Asegurar que está activo por defecto
        if (vehicle.getIsActive() == null) {
            vehicle.setIsActive(true);
        }
    }

    // ============================================
    // Métodos custom específicos de Vehicle
    // ============================================

    /**
     * Listar solo vehículos activos
     */
    @Transactional(readOnly = true)
    public List<Vehicle> findAllActive() {
        log.info("Listando vehículos activos");
        return vehicleRepository.findByIsActiveTrue();
    }

    /**
     * Buscar vehículos por placa
     */
    @Transactional(readOnly = true)
    public List<Vehicle> searchByPlaca(String placa) {
        log.info("Buscando vehículos por placa: {}", placa);
        return vehicleRepository.findByPlacaContainingIgnoreCase(placa);
    }

    /**
     * Eliminar vehículo (soft delete)
     */
    @Override
    @Transactional
    public void delete(String id) {
        log.info("Eliminando vehículo con ID: {} (soft delete)", id);
        Vehicle vehicle = findById(id);
        vehicle.setIsActive(false);
        getRepository().save(vehicle);
        log.info("Vehículo marcado como inactivo");
    }
}
