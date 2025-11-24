package com.forestech.inventory.service;

import com.forestech.inventory.client.CatalogClient;
import com.forestech.inventory.client.FleetClient;
import com.forestech.inventory.client.ProductDTO;
import com.forestech.inventory.model.FifoAllocation;
import com.forestech.inventory.model.Movement;
import com.forestech.inventory.repository.FifoAllocationRepository;
import com.forestech.inventory.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private FifoAllocationRepository fifoAllocationRepository;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private FleetClient fleetClient;

    @InjectMocks
    private MovementService movementService;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO();
        productDTO.setId("PROD-1");
        productDTO.setUnitPrice(new BigDecimal("10.00"));
    }

    @Test
    void createMovement_Entrada_ShouldSetRemainingQuantity() {
        Movement input = new Movement();
        input.setProductId("PROD-1");
        input.setQuantity(new BigDecimal("100"));
        input.setMovementType(Movement.MovementType.ENTRADA);

        when(catalogClient.getProductById("PROD-1")).thenReturn(productDTO);
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);

        Movement created = movementService.create(input);

        assertEquals(new BigDecimal("100"), created.getRemainingQuantity());
        assertEquals(new BigDecimal("10.00"), created.getRealUnitPrice());
    }

    @Test
    void createMovement_Salida_ShouldConsumeFifo() {
        // Setup existing entries
        Movement entry1 = new Movement();
        entry1.setId("ENTRY-1");
        entry1.setProductId("PROD-1");
        entry1.setQuantity(new BigDecimal("50"));
        entry1.setRemainingQuantity(new BigDecimal("50"));
        entry1.setUnitPrice(new BigDecimal("10.00"));
        entry1.setRealUnitPrice(new BigDecimal("10.00"));
        entry1.setMovementType(Movement.MovementType.ENTRADA);

        Movement entry2 = new Movement();
        entry2.setId("ENTRY-2");
        entry2.setProductId("PROD-1");
        entry2.setQuantity(new BigDecimal("50"));
        entry2.setRemainingQuantity(new BigDecimal("50"));
        entry2.setUnitPrice(new BigDecimal("12.00"));
        entry2.setRealUnitPrice(new BigDecimal("12.00"));
        entry2.setMovementType(Movement.MovementType.ENTRADA);

        List<Movement> entries = new ArrayList<>();
        entries.add(entry1);
        entries.add(entry2);

        // Setup output
        Movement output = new Movement();
        output.setProductId("PROD-1");
        output.setQuantity(new BigDecimal("60")); // Should consume all of entry1 and 10 of entry2
        output.setMovementType(Movement.MovementType.SALIDA);

        when(catalogClient.getProductById("PROD-1")).thenReturn(productDTO);
        when(movementRepository.findByProductIdAndMovementTypeAndRemainingQuantityGreaterThanOrderByCreatedAtAsc(
                eq("PROD-1"), eq(Movement.MovementType.ENTRADA), eq(BigDecimal.ZERO)))
                .thenReturn(entries);
        when(movementRepository.save(any(Movement.class))).thenAnswer(i -> i.getArguments()[0]);

        Movement created = movementService.create(output);

        // Verify Entry 1 consumed
        assertEquals(BigDecimal.ZERO, entry1.getRemainingQuantity());

        // Verify Entry 2 partially consumed (50 - 10 = 40 remaining)
        assertEquals(new BigDecimal("40"), entry2.getRemainingQuantity());

        // Verify Allocations created
        verify(fifoAllocationRepository, times(2)).save(any(FifoAllocation.class));

        // Verify Real Cost Calculation
        // 50 * 10.00 + 10 * 12.00 = 500 + 120 = 620
        assertEquals(new BigDecimal("620.00"), created.getRealCost());
        // Real Unit Price = 620 / 60 = 10.3333
        assertEquals(new BigDecimal("10.3333"), created.getRealUnitPrice());
    }
}
