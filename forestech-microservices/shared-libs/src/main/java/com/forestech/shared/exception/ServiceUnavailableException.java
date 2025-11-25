package com.forestech.shared.exception;

/**
 * Excepción lanzada cuando un servicio externo no está disponible.
 * Usada por los Feign Client fallbacks para indicar fallos de comunicación.
 */
public class ServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public ServiceUnavailableException(String serviceName) {
        super(serviceName + " no está disponible. Por favor, intente más tarde.");
        this.serviceName = serviceName;
    }

    public ServiceUnavailableException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ServiceUnavailableException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
