package com.forestech.simpleui.util;

/**
 * ServiceException
 * Base class for all service-related exceptions.
 * Provides context about which service failed and what operation was being
 * performed.
 */
public class ServiceException extends Exception {

    private final String serviceName;
    private final String operation;
    private final int statusCode;

    public ServiceException(String serviceName, String operation, int statusCode, String message) {
        super(message);
        this.serviceName = serviceName;
        this.operation = operation;
        this.statusCode = statusCode;
    }

    public ServiceException(String serviceName, String operation, int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.operation = operation;
        this.statusCode = statusCode;
    }

    public ServiceException(String serviceName, String operation, Throwable cause) {
        super(String.format("Error en %s durante %s: %s", serviceName, operation, cause.getMessage()), cause);
        this.serviceName = serviceName;
        this.operation = operation;
        this.statusCode = -1;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getOperation() {
        return operation;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDetailedMessage() {
        if (statusCode > 0) {
            return String.format("[%s] %s - HTTP %d: %s",
                    serviceName, operation, statusCode, getMessage());
        }
        return String.format("[%s] %s: %s", serviceName, operation, getMessage());
    }
}
