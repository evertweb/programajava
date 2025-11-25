package com.forestech.invoicing.saga;

import lombok.Data;
import lombok.Builder;
import java.util.ArrayList;
import java.util.List;

/**
 * Context object that tracks the state of an invoice creation SAGA.
 * Contains all the data needed for compensating transactions.
 */
@Data
@Builder
public class InvoiceSagaContext {
    
    private String invoiceId;
    private String invoiceNumber;
    
    @Builder.Default
    private List<String> createdProductIds = new ArrayList<>();
    
    @Builder.Default
    private List<String> createdMovementIds = new ArrayList<>();
    
    @Builder.Default
    private SagaState state = SagaState.STARTED;
    
    @Builder.Default
    private List<SagaStep> completedSteps = new ArrayList<>();
    
    private String failureReason;
    
    public enum SagaState {
        STARTED,
        VALIDATING_SUPPLIER,
        VALIDATING_PRODUCTS,
        CREATING_INVOICE,
        CREATING_MOVEMENTS,
        COMPLETED,
        COMPENSATING,
        FAILED
    }
    
    public enum SagaStep {
        VALIDATE_SUPPLIER,
        VALIDATE_PRODUCTS,
        CREATE_PRODUCTS,
        CREATE_INVOICE,
        CREATE_MOVEMENTS,
        LINK_MOVEMENTS
    }
    
    public void addCreatedProduct(String productId) {
        if (createdProductIds == null) {
            createdProductIds = new ArrayList<>();
        }
        createdProductIds.add(productId);
    }
    
    public void addCreatedMovement(String movementId) {
        if (createdMovementIds == null) {
            createdMovementIds = new ArrayList<>();
        }
        createdMovementIds.add(movementId);
    }
    
    public void markStepCompleted(SagaStep step) {
        if (completedSteps == null) {
            completedSteps = new ArrayList<>();
        }
        completedSteps.add(step);
    }
    
    public boolean isStepCompleted(SagaStep step) {
        return completedSteps != null && completedSteps.contains(step);
    }
}
