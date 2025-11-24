package com.forestech.inventory.repository;

import com.forestech.inventory.model.FifoAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FifoAllocationRepository extends JpaRepository<FifoAllocation, String> {
    List<FifoAllocation> findByOutputMovementId(String outputMovementId);

    List<FifoAllocation> findByInputMovementId(String inputMovementId);
}
