package com.forestech.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fifo_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FifoAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "output_movement_id", nullable = false)
    private String outputMovementId;

    @Column(name = "input_movement_id", nullable = false)
    private String inputMovementId;

    @Column(nullable = false)
    private BigDecimal quantity;

    @CreationTimestamp
    @Column(name = "allocated_at")
    private LocalDateTime allocatedAt;
}
