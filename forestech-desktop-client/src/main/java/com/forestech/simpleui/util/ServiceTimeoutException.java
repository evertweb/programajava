package com.forestech.simpleui.util;

/**
 * ServiceTimeoutException
 * Thrown when a service request times out.
 * This exception triggers retry logic.
 */
public class ServiceTimeoutException extends ServiceException {

    public ServiceTimeoutException(String serviceName, String operation, String message) {
        super(serviceName, operation, 408, message);
    }

    public ServiceTimeoutException(String serviceName, String operation, Throwable cause) {
        super(serviceName, operation, cause);
    }
}
