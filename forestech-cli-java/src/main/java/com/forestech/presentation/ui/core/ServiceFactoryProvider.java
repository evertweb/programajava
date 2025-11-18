package com.forestech.presentation.ui.core;

import com.forestech.business.services.ServiceFactory;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Centraliza la forma en que las GUIs obtienen {@link ServiceFactory} para
 * facilitar pruebas y personalizar la inyeccion de dependencias.
 */
public final class ServiceFactoryProvider {

    private static Supplier<ServiceFactory> supplier = ServiceFactory::getInstance;

    private ServiceFactoryProvider() {
        // Utility class
    }

    /**
     * Devuelve la instancia actual de {@link ServiceFactory} definida por el proveedor.
     *
     * @return ServiceFactory a utilizar en la capa UI
     */
    public static ServiceFactory getFactory() {
        return supplier.get();
    }

    /**
     * Permite sobreescribir el proveedor de {@link ServiceFactory} (por ejemplo en tests).
     *
     * @param customSupplier proveedor personalizado
     */
    public static void setSupplier(Supplier<ServiceFactory> customSupplier) {
        supplier = Objects.requireNonNull(customSupplier, "customSupplier no puede ser null");
    }

    /**
     * Restaura el proveedor por defecto basado en {@link ServiceFactory#getInstance()}.
     */
    public static void reset() {
        supplier = ServiceFactory::getInstance;
    }
}
