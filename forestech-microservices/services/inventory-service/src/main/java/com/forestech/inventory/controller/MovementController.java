package com.forestech.inventory.controller;

import com.forestech.inventory.model.Movement;
import com.forestech.inventory.service.MovementService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@Slf4j
public class MovementController {

    private final MovementService movementService;

    @GetMapping
    public ResponseEntity<List<Movement>> getAllMovements() {
        return ResponseEntity.ok(movementService.getAllMovements());
    }

    @PostMapping("/entrada")
    public ResponseEntity<Movement> registrarEntrada(@RequestBody MovementRequest request) {
        log.info("POST /api/movements/entrada");
        Movement movement = new Movement();
        movement.setMovementType(Movement.MovementType.ENTRADA);
        movement.setProductId(request.getProductId());
        movement.setVehicleId(request.getVehicleId());
        movement.setInvoiceId(request.getInvoiceId());
        movement.setQuantity(request.getQuantity());
        movement.setUnitPrice(request.getUnitPrice());
        movement.setDescription(request.getDescription());

        Movement created = movementService.createMovement(movement);
        return ResponseEntity.created(URI.create("/api/movements/" + created.getId()))
                .body(created);
    }

    @PostMapping("/salida")
    public ResponseEntity<Movement> registrarSalida(@RequestBody MovementRequest request) {
        log.info("POST /api/movements/salida");
        Movement movement = new Movement();
        movement.setMovementType(Movement.MovementType.SALIDA);
        movement.setProductId(request.getProductId());
        movement.setVehicleId(request.getVehicleId());
        movement.setQuantity(request.getQuantity());
        movement.setUnitPrice(request.getUnitPrice());
        movement.setDescription(request.getDescription());

        Movement created = movementService.createMovement(movement);
        return ResponseEntity.created(URI.create("/api/movements/" + created.getId()))
                .body(created);
    }

    @GetMapping("/stock/{productId}")
    public ResponseEntity<StockResponse> getStock(@PathVariable String productId) {
        BigDecimal stock = movementService.calculateStock(productId);
        return ResponseEntity.ok(new StockResponse(productId, stock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable String id) {
        movementService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ MovementService.ProductNotFoundException.class,
            MovementService.VehicleNotFoundException.class })
    public ResponseEntity<String> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @Data
    static class MovementRequest {
        private String productId;
        private String vehicleId;
        private String invoiceId;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private String description;
    }

    @Data
    static class StockResponse {
        private final String productId;
        private final BigDecimal stock;
    }
}
