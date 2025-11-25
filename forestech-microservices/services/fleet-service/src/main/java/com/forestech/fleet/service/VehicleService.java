package com.forestech.fleet.service;

import com.forestech.fleet.model.Vehicle;
import com.forestech.fleet.repository.VehicleRepository;
import com.forestech.shared.exception.DuplicateEntityException;
import com.forestech.shared.service.BaseService;
import com.forestech.shared.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para Vehicle
 * REFACTORIZADO: Ahora extiende BaseService para reducir código duplicado
 * 
 * CACHE STRATEGY:
 * - vehicles: Cache de vehículos individuales por ID (TTL: 30min)
 * - vehiclesActive: Lista de vehículos activos (TTL: 10min)
 * - vehicleSearch: Resultados de búsqueda (TTL: 5min)
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
    // Métodos con Cache
    // ============================================

    /**
     * Obtener vehículo por ID con cache
     */
    @Override
    @Cacheable(value = "vehicles", key = "#id")
    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        log.info("Buscando vehículo por ID: {} (cache miss)", id);
        return super.findById(id);
    }

    /**
     * Listar solo vehículos activos (con cache)
     */
    @Cacheable(value = "vehiclesActive")
    @Transactional(readOnly = true)
    public List<Vehicle> findAllActive() {
        log.info("Listando vehículos activos (cache miss)");
        return vehicleRepository.findByIsActiveTrue();
    }

    /**
     * Buscar vehículos por placa (con cache)
     */
    @Cacheable(value = "vehicleSearch", key = "#placa")
    @Transactional(readOnly = true)
    public List<Vehicle> searchByPlaca(String placa) {
        log.info("Buscando vehículos por placa: {} (cache miss)", placa);
        return vehicleRepository.findByPlacaContainingIgnoreCase(placa);
    }

    /**
     * Crear vehículo - invalida caches de listas
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "vehiclesActive", allEntries = true),
            @CacheEvict(value = "vehicleSearch", allEntries = true)
    })
    @Transactional
    public Vehicle create(Vehicle vehicle) {
        log.info("Creando vehículo - invalidando caches de listas");
        return super.create(vehicle);
    }

    /**
     * Actualizar vehículo - invalida caches
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "vehicles", key = "#id"),
            @CacheEvict(value = "vehiclesActive", allEntries = true),
            @CacheEvict(value = "vehicleSearch", allEntries = true)
    })
    @Transactional
    public Vehicle update(String id, Vehicle vehicle) {
        log.info("Actualizando vehículo ID: {} - invalidando caches", id);
        return super.update(id, vehicle);
    }

    /**
     * Eliminar vehículo (soft delete) - invalida caches
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "vehicles", key = "#id"),
            @CacheEvict(value = "vehiclesActive", allEntries = true),
            @CacheEvict(value = "vehicleSearch", allEntries = true)
    })
    @Transactional
    public void delete(String id) {
        log.info("Eliminando vehículo con ID: {} (soft delete) - invalidando caches", id);
        Vehicle vehicle = super.findById(id);
        vehicle.setIsActive(false);
        getRepository().save(vehicle);
        log.info("Vehículo marcado como inactivo");
    }
}
