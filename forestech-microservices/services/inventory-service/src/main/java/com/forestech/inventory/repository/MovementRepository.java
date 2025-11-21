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
}
