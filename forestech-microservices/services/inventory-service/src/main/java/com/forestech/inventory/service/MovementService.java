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

        // 4. Lógica FIFO y Remaining Quantity
        if (movement.getMovementType() == Movement.MovementType.ENTRADA) {
            // Nueva entrada: el remanente es igual a la cantidad inicial
            movement.setRemainingQuantity(movement.getQuantity());
        } else if (movement.getMovementType() == Movement.MovementType.SALIDA) {
            // Salida: consumir de las entradas más antiguas (FIFO)
            processFifoOutput(movement);
        }

        // 5. Generar ID
        if (movement.getId() == null) {
            movement.setId("MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        return movementRepository.save(movement);
    }

    private void processFifoOutput(Movement salida) {
        BigDecimal quantityToConsume = salida.getQuantity();
        
        // Buscar entradas con stock disponible, ordenadas por fecha (FIFO)
        List<Movement> availableEntries = movementRepository
                .findByProductIdAndMovementTypeAndRemainingQuantityGreaterThanOrderByCreatedAtAsc(
                        salida.getProductId(), 
                        Movement.MovementType.ENTRADA, 
                        BigDecimal.ZERO);

        BigDecimal totalAvailable = availableEntries.stream()
                .map(Movement::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(quantityToConsume) < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + salida.getProductId() + 
                    ". Disponible: " + totalAvailable + ", Solicitado: " + quantityToConsume);
        }

        for (Movement entry : availableEntries) {
            if (quantityToConsume.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal availableInEntry = entry.getRemainingQuantity();
            BigDecimal consumedFromEntry;

            if (availableInEntry.compareTo(quantityToConsume) >= 0) {
                // Esta entrada cubre todo lo que falta
                consumedFromEntry = quantityToConsume;
                entry.setRemainingQuantity(availableInEntry.subtract(quantityToConsume));
                quantityToConsume = BigDecimal.ZERO;
            } else {
                // Consumimos todo lo de esta entrada y seguimos
                consumedFromEntry = availableInEntry;
                entry.setRemainingQuantity(BigDecimal.ZERO);
                quantityToConsume = quantityToConsume.subtract(availableInEntry);
            }
            
            // Actualizamos la entrada en BD
            movementRepository.save(entry);
            
            log.info("FIFO: Consumed {} from Entry {} (Invoice: {})", 
                    consumedFromEntry, entry.getId(), entry.getInvoiceId());
        }

        // Calcular precio unitario promedio ponderado para el registro de salida
        if (salida.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalCost = salida.getSubtotal(); // Ya calculado arriba como Qty * UnitPrice (del form)
            // Pero queremos el costo REAL basado en lo que consumimos
            // TODO: Para ser estrictos, deberíamos sumar (consumedFromEntry * entry.UnitPrice)
            // Por ahora, mantenemos el precio de referencia del catálogo para la salida,
            // pero el costo real de inventario se refleja en la reducción de las entradas.
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateStock(String productId) {
        BigDecimal entradas = movementRepository
                .sumQuantityByProductAndType(productId, Movement.MovementType.ENTRADA);

        BigDecimal salidas = movementRepository
                .sumQuantityByProductAndType(productId, Movement.MovementType.SALIDA);

        if (entradas == null) entradas = BigDecimal.ZERO;
        if (salidas == null) salidas = BigDecimal.ZERO;

        return entradas.subtract(salidas);
    }

    @Transactional
    public void deleteMovement(String id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado: " + id));

        // Validación de integridad: No eliminar si ya fue consumido (FIFO)
        if (movement.getMovementType() == Movement.MovementType.ENTRADA) {
            if (movement.getRemainingQuantity().compareTo(movement.getQuantity()) != 0) {
                throw new RuntimeException("No se puede eliminar este movimiento de entrada porque su stock ya ha sido utilizado en salidas posteriores.");
            }
        }

        log.warn("Eliminando movimiento {} (Tipo: {})", id, movement.getMovementType());
        movementRepository.delete(movement);
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
