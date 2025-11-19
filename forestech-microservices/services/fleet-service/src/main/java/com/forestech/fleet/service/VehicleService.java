package com.forestech.fleet.service;

import com.forestech.fleet.model.Vehicle;
import com.forestech.fleet.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        log.info("Listando todos los vehículos");
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAllActive() {
        log.info("Listando vehículos activos");
        return vehicleRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        log.info("Buscando vehículo con ID: {}", id);
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehículo no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> searchByPlaca(String placa) {
        log.info("Buscando vehículos por placa: {}", placa);
        return vehicleRepository.findByPlacaContainingIgnoreCase(placa);
    }

    @Transactional
    public Vehicle create(Vehicle vehicle) {
        log.info("Creando nuevo vehículo: {}", vehicle.getPlaca());

        if (vehicleRepository.existsByPlacaIgnoreCase(vehicle.getPlaca())) {
            throw new DuplicateVehicleException("Ya existe un vehículo con la placa: " + vehicle.getPlaca());
        }

        if (vehicle.getId() == null || vehicle.getId().isEmpty()) {
            vehicle.setId("VEH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (vehicle.getIsActive() == null) {
            vehicle.setIsActive(true);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehículo creado exitosamente con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Vehicle update(String id, Vehicle vehicleData) {
        log.info("Actualizando vehículo con ID: {}", id);

        Vehicle existing = findById(id);
        existing.setPlaca(vehicleData.getPlaca());
        existing.setMarca(vehicleData.getMarca());
        existing.setModelo(vehicleData.getModelo());
        existing.setAnio(vehicleData.getAnio());
        existing.setCategory(vehicleData.getCategory());
        existing.setDescripcion(vehicleData.getDescripcion());

        Vehicle updated = vehicleRepository.save(existing);
        log.info("Vehículo actualizado exitosamente");
        return updated;
    }

    @Transactional
    public void delete(String id) {
        log.info("Eliminando vehículo con ID: {}", id);
        Vehicle vehicle = findById(id);
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
        log.info("Vehículo marcado como inactivo");
    }

    public static class VehicleNotFoundException extends RuntimeException {
        public VehicleNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateVehicleException extends RuntimeException {
        public DuplicateVehicleException(String message) {
            super(message);
        }
    }
}
