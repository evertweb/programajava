package com.forestech.fleet.repository;

import com.forestech.fleet.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findByPlacaContainingIgnoreCase(String placa);

    List<Vehicle> findByIsActiveTrue();

    List<Vehicle> findByCategory(Vehicle.VehicleCategory category);

    boolean existsByPlacaIgnoreCase(String placa);

    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.isActive = true")
    Optional<Vehicle> findActiveById(@Param("id") String id);

    Optional<Vehicle> findByPlacaIgnoreCase(String placa);
}
