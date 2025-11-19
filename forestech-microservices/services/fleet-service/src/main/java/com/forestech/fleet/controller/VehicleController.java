package com.forestech.fleet.controller;

import com.forestech.fleet.model.Vehicle;
import com.forestech.fleet.service.VehicleService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false, defaultValue = "false") boolean onlyActive) {
        log.info("GET /api/vehicles - onlyActive: {}", onlyActive);
        List<Vehicle> vehicles = onlyActive
                ? vehicleService.findAllActive()
                : vehicleService.findAll();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        log.info("GET /api/vehicles/{}", id);
        Vehicle vehicle = vehicleService.findById(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(@RequestParam String placa) {
        log.info("GET /api/vehicles/search?placa={}", placa);
        List<Vehicle> vehicles = vehicleService.searchByPlaca(placa);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        log.info("POST /api/vehicles - {}", vehicle.getPlaca());
        Vehicle created = vehicleService.create(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody Vehicle vehicle) {
        log.info("PUT /api/vehicles/{}", id);
        Vehicle updated = vehicleService.update(id, vehicle);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        log.info("DELETE /api/vehicles/{}", id);
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(VehicleService.VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFound(VehicleService.VehicleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(VehicleService.DuplicateVehicleException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateVehicle(VehicleService.DuplicateVehicleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String message;
    }
}
