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

    @Transactional(readOnly = true)
    public List<Movement> getMovementsByType(Movement.MovementType type) {
        return movementRepository.findByMovementType(type);
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

    /**
     * Calcula el precio promedio ponderado del stock actual de un producto.
     * Fórmula: Σ(remaining_quantity × unit_price) / Σ(remaining_quantity)
     * Solo considera las entradas que aún tienen stock disponible.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateWeightedAveragePrice(String productId) {
        BigDecimal totalRemainingQuantity = movementRepository.sumRemainingQuantityByProductId(productId);

        if (totalRemainingQuantity == null || totalRemainingQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal weightedSum = movementRepository.sumWeightedValueByProductId(productId);

        if (weightedSum == null) {
            return BigDecimal.ZERO;
        }

        // Precio promedio = suma ponderada / cantidad total
        return weightedSum.divide(totalRemainingQuantity, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Obtiene el stock con precio promedio ponderado para un producto.
     */
    @Transactional(readOnly = true)
    public StockWithPrice getStockWithPrice(String productId) {
        BigDecimal stock = movementRepository.sumRemainingQuantityByProductId(productId);
        BigDecimal weightedAveragePrice = calculateWeightedAveragePrice(productId);

        return new StockWithPrice(productId, stock, weightedAveragePrice);
    }

    // DTO para stock con precio
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class StockWithPrice {
        private String productId;
        private BigDecimal stock;
        private BigDecimal weightedAveragePrice;
    }

    @Transactional
    public void deleteMovement(String id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado: " + id));

        // Validación de integridad: No eliminar ENTRADA si ya fue consumido (FIFO)
        if (movement.getMovementType() == Movement.MovementType.ENTRADA) {
            if (movement.getRemainingQuantity().compareTo(movement.getQuantity()) != 0) {
                throw new RuntimeException("No se puede eliminar este movimiento de entrada porque su stock ya ha sido utilizado en salidas posteriores.");
            }
        }

        // Para SALIDA: Recomponer el stock (restaurar remaining_quantity de entradas)
        if (movement.getMovementType() == Movement.MovementType.SALIDA) {
            restoreFifoStock(movement);
        }

        log.warn("Eliminando movimiento {} (Tipo: {})", id, movement.getMovementType());
        movementRepository.delete(movement);
    }

    /**
     * Restaura el stock de las entradas después de eliminar una salida.
     * Usa FIFO inverso: restaura primero las entradas más recientes.
     */
    private void restoreFifoStock(Movement salida) {
        BigDecimal quantityToRestore = salida.getQuantity();
        String productId = salida.getProductId();

        log.info("Restaurando {} unidades al stock del producto {}", quantityToRestore, productId);

        // Buscar entradas del mismo producto ordenadas por fecha DESC (más recientes primero)
        List<Movement> entries = movementRepository
                .findByProductIdAndMovementTypeOrderByCreatedAtDesc(productId, Movement.MovementType.ENTRADA);

        for (Movement entry : entries) {
            if (quantityToRestore.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal currentRemaining = entry.getRemainingQuantity();
            BigDecimal originalQuantity = entry.getQuantity();
            BigDecimal spaceAvailable = originalQuantity.subtract(currentRemaining);

            if (spaceAvailable.compareTo(BigDecimal.ZERO) > 0) {
                // Esta entrada tiene espacio para restaurar (fue parcial o totalmente consumida)
                BigDecimal toRestore;
                if (spaceAvailable.compareTo(quantityToRestore) >= 0) {
                    // Podemos restaurar todo lo que falta en esta entrada
                    toRestore = quantityToRestore;
                    quantityToRestore = BigDecimal.ZERO;
                } else {
                    // Restauramos todo el espacio disponible y seguimos
                    toRestore = spaceAvailable;
                    quantityToRestore = quantityToRestore.subtract(spaceAvailable);
                }

                entry.setRemainingQuantity(currentRemaining.add(toRestore));
                movementRepository.save(entry);

                log.info("FIFO Restore: Restored {} to Entry {} (now: {}/{})",
                        toRestore, entry.getId(), entry.getRemainingQuantity(), entry.getQuantity());
            }
        }

        if (quantityToRestore.compareTo(BigDecimal.ZERO) > 0) {
            log.warn("No se pudo restaurar completamente el stock. Restante: {}", quantityToRestore);
        }
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
