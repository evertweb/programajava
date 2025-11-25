package com.forestech.invoicing.saga;

/**
 * Exception thrown when an Invoice Creation SAGA fails.
 * Contains the saga context with details about the failure and compensation actions.
 */
public class InvoiceSagaException extends RuntimeException {

    private final InvoiceSagaContext context;

    public InvoiceSagaException(String message, Throwable cause, InvoiceSagaContext context) {
        super(message, cause);
        this.context = context;
    }

    public InvoiceSagaException(String message, InvoiceSagaContext context) {
        super(message);
        this.context = context;
    }

    public InvoiceSagaContext getContext() {
        return context;
    }

    public String getFailureDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("SAGA failed at state: ").append(context.getState());
        sb.append("\nReason: ").append(context.getFailureReason());
        sb.append("\nCompleted steps: ").append(context.getCompletedSteps());
        sb.append("\nCompensated movements: ").append(context.getCreatedMovementIds().size());
        return sb.toString();
    }
}
