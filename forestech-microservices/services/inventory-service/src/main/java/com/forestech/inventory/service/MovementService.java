package com.forestech.inventory.service;

import com.forestech.inventory.client.CatalogClient;
import com.forestech.inventory.client.FleetClient;
import com.forestech.inventory.client.ProductDTO;
import com.forestech.inventory.client.VehicleDTO;
import com.forestech.inventory.model.Movement;
import com.forestech.inventory.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService {

    private final MovementRepository movementRepository;
    private final CatalogClient catalogClient;
    private final FleetClient fleetClient;

    @Transactional(readOnly = true)
    public List<Movement> getAllMovements() {
        return movementRepository.findAll();
    }

    @Transactional
    public Movement createMovement(Movement movement) {
        log.info("Creating movement for product: {}", movement.getProductId());

        // 1. Validar que el producto existe (llamada remota)
        ProductDTO product = catalogClient.getProductById(movement.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Producto no encontrado: " + movement.getProductId());
        }

        // 2. Si tiene vehículo, validar que existe
        if (movement.getVehicleId() != null && !movement.getVehicleId().isEmpty()) {
            VehicleDTO vehicle = fleetClient.getVehicleById(movement.getVehicleId());
            if (vehicle == null) {
                throw new VehicleNotFoundException("Vehículo no encontrado: " + movement.getVehicleId());
            }
        }

        // 3. Calcular subtotal
        if (movement.getUnitPrice() == null) {
            movement.setUnitPrice(product.getUnitPrice());
        }

        movement.setSubtotal(
                movement.getQuantity().multiply(movement.getUnitPrice()));

        // 4. Generar ID
        if (movement.getId() == null) {
            movement.setId("MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        return movementRepository.save(movement);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateStock(String productId) {
        BigDecimal entradas = movementRepository
                .sumQuantityByProductAndType(productId, Movement.MovementType.ENTRADA);

        BigDecimal salidas = movementRepository
                .sumQuantityByProductAndType(productId, Movement.MovementType.SALIDA);

        return entradas.subtract(salidas);
    }

    // Excepciones
    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public static class VehicleNotFoundException extends RuntimeException {
        public VehicleNotFoundException(String message) {
            super(message);
        }
    }
}
