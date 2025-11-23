package com.forestech.inventory.repository;

import com.forestech.inventory.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, String> {

    @Query("SELECT COALESCE(SUM(m.quantity), 0) FROM Movement m WHERE m.productId = :productId AND m.movementType = :type")
    BigDecimal sumQuantityByProductAndType(@Param("productId") String productId,
            @Param("type") Movement.MovementType type);

    List<Movement> findByProductIdAndMovementTypeAndRemainingQuantityGreaterThanOrderByCreatedAtAsc(
            String productId, Movement.MovementType movementType, BigDecimal remainingQuantity);

    // Obtener suma de remaining_quantity para un producto (stock actual)
    @Query("SELECT COALESCE(SUM(m.remainingQuantity), 0) FROM Movement m WHERE m.productId = :productId AND m.movementType = 'ENTRADA'")
    BigDecimal sumRemainingQuantityByProductId(@Param("productId") String productId);

    // Obtener suma ponderada (remaining_quantity * unit_price) para calcular promedio
    @Query("SELECT COALESCE(SUM(m.remainingQuantity * m.unitPrice), 0) FROM Movement m WHERE m.productId = :productId AND m.movementType = 'ENTRADA' AND m.remainingQuantity > 0")
    BigDecimal sumWeightedValueByProductId(@Param("productId") String productId);

    // Obtener todos los productos distintos con entradas
    @Query("SELECT DISTINCT m.productId FROM Movement m WHERE m.movementType = 'ENTRADA'")
    List<String> findDistinctProductIds();

    // Filtrar movimientos por tipo
    List<Movement> findByMovementType(Movement.MovementType movementType);

    // Buscar entradas de un producto ordenadas por fecha DESC (para recomposición FIFO inversa)
    List<Movement> findByProductIdAndMovementTypeOrderByCreatedAtDesc(
            String productId, Movement.MovementType movementType);
}
