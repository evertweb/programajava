package com.forestech.simpleui.util;

/**
 * ServiceUnavailableException
 * Thrown when a service is temporarily unavailable (503, connection refused,
 * timeout).
 * This exception triggers retry logic.
 */
public class ServiceUnavailableException extends ServiceException {

    public ServiceUnavailableException(String serviceName, String operation, int statusCode, String message) {
        super(serviceName, operation, statusCode, message);
    }

    public ServiceUnavailableException(String serviceName, String operation, Throwable cause) {
        super(serviceName, operation, cause);
    }

    public ServiceUnavailableException(String serviceName, String operation, String message) {
        super(serviceName, operation, 503, message);
    }
}
