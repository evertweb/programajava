package com.forestech.invoicing.client;

/**
 * Excepción local para indicar que un servicio externo no está disponible.
 * Usada por los Feign Client fallbacks.
 */
public class ServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public ServiceUnavailableException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
