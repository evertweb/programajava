package com.forestech.inventory.service;

import com.forestech.inventory.client.CatalogClient;
import com.forestech.inventory.client.FleetClient;
import com.forestech.inventory.client.ProductDTO;
import com.forestech.inventory.client.VehicleDTO;
import com.forestech.inventory.exception.InsufficientStockException;
import com.forestech.inventory.exception.MovementNotFoundException;
import com.forestech.inventory.model.FifoAllocation;
import com.forestech.inventory.model.Movement;
import com.forestech.inventory.repository.FifoAllocationRepository;
import com.forestech.inventory.repository.MovementRepository;
import com.forestech.shared.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService extends BaseService<Movement, String> {

    private final MovementRepository movementRepository;
    private final FifoAllocationRepository fifoAllocationRepository;
    private final CatalogClient catalogClient;
    private final FleetClient fleetClient;

    @Override
    protected JpaRepository<Movement, String> getRepository() {
        return movementRepository;
    }

    @Override
    protected String getEntityName() {
        return "Movimiento";
    }

    @Override
    protected void updateFields(Movement existing, Movement newData) {
        // Los movimientos son inmutables en cuanto a cantidades y productos
        // Solo permitimos actualizar descripción o metadatos no críticos
        existing.setDescription(newData.getDescription());
        existing.setInvoiceId(newData.getInvoiceId());
    }

    @Transactional(readOnly = true)
    public List<Movement> getMovementsByType(Movement.MovementType type) {
        return movementRepository.findByMovementType(type);
    }

    @Override
    protected void beforeCreate(Movement movement) {
        log.info("Validating movement for product: {}", movement.getProductId());

        // 1. Validar que el producto existe
        ProductDTO product = catalogClient.getProductById(movement.getProductId());
        if (product == null) {
            throw new MovementNotFoundException("Producto no encontrado: " + movement.getProductId());
        }

        // 2. Si tiene vehículo, validar que existe
        if (movement.getVehicleId() != null && !movement.getVehicleId().isEmpty()) {
            VehicleDTO vehicle = fleetClient.getVehicleById(movement.getVehicleId());
            if (vehicle == null) {
                throw new MovementNotFoundException("Vehículo no encontrado: " + movement.getVehicleId());
            }
        }

        // 3. Calcular subtotal y precios
        if (movement.getUnitPrice() == null) {
            movement.setUnitPrice(product.getUnitPrice());
        }

        movement.setSubtotal(movement.getQuantity().multiply(movement.getUnitPrice()));

        // 4. Generar ID si no existe
        if (movement.getId() == null) {
            movement.setId("MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // 5. Lógica FIFO
        if (movement.getMovementType() == Movement.MovementType.ENTRADA) {
            // Nueva entrada
            movement.setRemainingQuantity(movement.getQuantity());
            // El costo real unitario inicial es el precio de compra
            movement.setRealUnitPrice(movement.getUnitPrice());
            movement.setRealCost(movement.getSubtotal());
        } else if (movement.getMovementType() == Movement.MovementType.SALIDA) {
            // Salida: consumir de las entradas más antiguas
            processFifoOutput(movement);
        }
    }

    @Override
    protected void beforeDelete(Movement movement) {
        // Validación de integridad: No eliminar ENTRADA si ya fue consumido
        if (movement.getMovementType() == Movement.MovementType.ENTRADA) {
            if (movement.getRemainingQuantity().compareTo(movement.getQuantity()) != 0) {
                throw new RuntimeException(
                        "No se puede eliminar este movimiento de entrada porque su stock ya ha sido utilizado en salidas posteriores.");
            }
        }

        // Para SALIDA: Restaurar el stock usando las asignaciones (Allocations)
        if (movement.getMovementType() == Movement.MovementType.SALIDA) {
            restoreFifoStockFromAllocations(movement);
        }
    }

    private void processFifoOutput(Movement salida) {
        BigDecimal quantityToConsume = salida.getQuantity();
        BigDecimal totalRealCost = BigDecimal.ZERO;

        // Buscar entradas con stock disponible, ordenadas por fecha (FIFO) con LOCK
        // PESIMISTA
        List<Movement> availableEntries = movementRepository
                .findByProductIdAndMovementTypeAndRemainingQuantityGreaterThanOrderByCreatedAtAsc(
                        salida.getProductId(),
                        Movement.MovementType.ENTRADA,
                        BigDecimal.ZERO);

        BigDecimal totalAvailable = availableEntries.stream()
                .map(Movement::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(quantityToConsume) < 0) {
            throw new InsufficientStockException("Stock insuficiente para el producto: " + salida.getProductId() +
                    ". Disponible: " + totalAvailable + ", Solicitado: " + quantityToConsume);
        }

        for (Movement entry : availableEntries) {
            if (quantityToConsume.compareTo(BigDecimal.ZERO) <= 0)
                break;

            BigDecimal availableInEntry = entry.getRemainingQuantity();
            BigDecimal consumedFromEntry;

            if (availableInEntry.compareTo(quantityToConsume) >= 0) {
                consumedFromEntry = quantityToConsume;
                entry.setRemainingQuantity(availableInEntry.subtract(quantityToConsume));
                quantityToConsume = BigDecimal.ZERO;
            } else {
                consumedFromEntry = availableInEntry;
                entry.setRemainingQuantity(BigDecimal.ZERO);
                quantityToConsume = quantityToConsume.subtract(availableInEntry);
            }

            // Crear Allocation
            FifoAllocation allocation = FifoAllocation.builder()
                    .outputMovementId(salida.getId())
                    .inputMovementId(entry.getId())
                    .quantity(consumedFromEntry)
                    .build();
            fifoAllocationRepository.save(allocation);

            // Actualizar entrada
            movementRepository.save(entry);

            // Calcular costo real ponderado
            // Costo real de esta porción = cantidad consumida * precio real unitario de la
            // entrada
            BigDecimal costOfPortion = consumedFromEntry
                    .multiply(entry.getRealUnitPrice() != null ? entry.getRealUnitPrice() : entry.getUnitPrice());
            totalRealCost = totalRealCost.add(costOfPortion);

            log.info("FIFO: Consumed {} from Entry {} (Invoice: {})",
                    consumedFromEntry, entry.getId(), entry.getInvoiceId());
        }

        // Establecer el costo real total y unitario de la salida
        salida.setRealCost(totalRealCost);
        if (salida.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            salida.setRealUnitPrice(totalRealCost.divide(salida.getQuantity(), 4, RoundingMode.HALF_UP));
        }
    }

    private void restoreFifoStockFromAllocations(Movement salida) {
        log.info("Restaurando stock para salida: {}", salida.getId());

        List<FifoAllocation> allocations = fifoAllocationRepository.findByOutputMovementId(salida.getId());

        for (FifoAllocation allocation : allocations) {
            Movement entry = movementRepository.findById(allocation.getInputMovementId())
                    .orElseThrow(() -> new MovementNotFoundException(
                            "Entrada original no encontrada: " + allocation.getInputMovementId()));

            // Restaurar stock
            entry.setRemainingQuantity(entry.getRemainingQuantity().add(allocation.getQuantity()));
            movementRepository.save(entry);

            // Eliminar allocation
            fifoAllocationRepository.delete(allocation);

            log.info("Restored {} units to entry {}", allocation.getQuantity(), entry.getId());
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateStock(String productId) {
        BigDecimal entradas = movementRepository
                .sumQuantityByProductAndType(productId, Movement.MovementType.ENTRADA);

        BigDecimal salidas = movementRepository
                .sumQuantityByProductAndType(productId, Movement.MovementType.SALIDA);

        if (entradas == null)
            entradas = BigDecimal.ZERO;
        if (salidas == null)
            salidas = BigDecimal.ZERO;

        return entradas.subtract(salidas);
    }

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

        return weightedSum.divide(totalRemainingQuantity, 2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public StockWithPrice getStockWithPrice(String productId) {
        BigDecimal stock = movementRepository.sumRemainingQuantityByProductId(productId);
        BigDecimal weightedAveragePrice = calculateWeightedAveragePrice(productId);

        return new StockWithPrice(productId, stock, weightedAveragePrice);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class StockWithPrice {
        private String productId;
        private BigDecimal stock;
        private BigDecimal weightedAveragePrice;
    }
}
