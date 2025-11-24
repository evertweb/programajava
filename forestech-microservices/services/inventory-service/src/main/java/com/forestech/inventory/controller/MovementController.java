package com.forestech.inventory.controller;

import com.forestech.inventory.exception.MovementNotFoundException;
import com.forestech.inventory.model.Movement;
import com.forestech.inventory.service.MovementService;
import com.forestech.shared.exception.EntityNotFoundException;
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
    public ResponseEntity<List<Movement>> getAllMovements(
            @RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) {
            try {
                Movement.MovementType movementType = Movement.MovementType.valueOf(type.toUpperCase());
                return ResponseEntity.ok(movementService.getMovementsByType(movementType));
            } catch (IllegalArgumentException e) {
                log.warn("Tipo de movimiento inválido: {}", type);
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(movementService.findAll());
    }

    /**
     * ENDPOINT INTERNO - Solo para uso de invoicing-service
     * Los movimientos ENTRADA solo se crean automáticamente al registrar facturas
     * La única forma de agregar stock es mediante facturas de compra
     */
    @PostMapping("/internal/entrada")
    public ResponseEntity<Movement> registrarEntradaInternal(@RequestBody MovementRequest request) {
        log.info("POST /api/movements/internal/entrada (llamada interna desde invoicing-service)");
        Movement movement = new Movement();
        movement.setMovementType(Movement.MovementType.ENTRADA);
        movement.setProductId(request.getProductId());
        movement.setVehicleId(request.getVehicleId());
        movement.setInvoiceId(request.getInvoiceId());
        movement.setQuantity(request.getQuantity());
        movement.setUnitPrice(request.getUnitPrice());
        movement.setDescription(request.getDescription());

        Movement created = movementService.create(movement);
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

        Movement created = movementService.create(movement);
        return ResponseEntity.created(URI.create("/api/movements/" + created.getId()))
                .body(created);
    }

    @GetMapping("/stock/{productId}")
    public ResponseEntity<StockResponse> getStock(@PathVariable String productId) {
        BigDecimal stock = movementService.calculateStock(productId);
        return ResponseEntity.ok(new StockResponse(productId, stock));
    }

    /**
     * Obtiene el stock con precio promedio ponderado para un producto.
     * Útil para valoración de inventario.
     */
    @GetMapping("/stock/{productId}/valued")
    public ResponseEntity<StockValuedResponse> getStockValued(@PathVariable String productId) {
        MovementService.StockWithPrice stockWithPrice = movementService.getStockWithPrice(productId);
        return ResponseEntity.ok(new StockValuedResponse(
                stockWithPrice.getProductId(),
                stockWithPrice.getStock(),
                stockWithPrice.getWeightedAveragePrice()));
    }

    /**
     * ENDPOINT INTERNO - Solo para uso de invoicing-service
     * Los movimientos solo se eliminan al anular facturas
     */
    @DeleteMapping("/internal/{id}")
    public ResponseEntity<Void> deleteMovementInternal(@PathVariable String id) {
        log.info("DELETE /api/movements/internal/{} (llamada interna desde invoicing-service)", id);
        movementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ENDPOINT PÚBLICO - Eliminar movimiento SALIDA con recomposición de stock
     * Al eliminar una SALIDA, se restaura automáticamente el stock a las entradas
     * FIFO
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable String id) {
        log.info("DELETE /api/movements/{}", id);
        movementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ MovementNotFoundException.class, EntityNotFoundException.class })
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

    @Data
    static class StockValuedResponse {
        private final String productId;
        private final BigDecimal stock;
        private final BigDecimal weightedAveragePrice;
    }
}
