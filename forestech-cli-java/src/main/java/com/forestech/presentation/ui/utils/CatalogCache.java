package com.forestech.presentation.ui.utils;

import com.forestech.data.models.Product;
import com.forestech.data.models.Vehicle;
import com.forestech.business.services.ProductServices;
import com.forestech.business.services.VehicleServices;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Caché compartido de catálogos para evitar consultas SQL duplicadas.
 * Mantiene versiones cacheadas de productos y vehículos con validez temporal.
 */
public class CatalogCache {

    private final ProductServices productServices;
    private final VehicleServices vehicleServices;

    private Map<String, String> productNames;
    private Map<String, String> vehicleNames;
    private long lastProductsUpdate = 0;
    private long lastVehiclesUpdate = 0;

    // Tiempo de validez del caché: 5 segundos
    private static final long CACHE_VALIDITY_MS = 5000;

    public CatalogCache(ProductServices productServices, VehicleServices vehicleServices) {
        this.productServices = productServices;
        this.vehicleServices = vehicleServices;
        this.productNames = new HashMap<>();
        this.vehicleNames = new HashMap<>();
    }

    /**
     * Obtiene mapa de ID → Nombre de productos.
     * Usa caché si es válido, sino consulta la BD.
     */
    public Map<String, String> getProductNames() {
        long now = System.currentTimeMillis();
        if (productNames == null || (now - lastProductsUpdate) > CACHE_VALIDITY_MS) {
            refreshProducts();
        }
        // Retornar copia defensiva
        return new HashMap<>(productNames);
    }

    /**
     * Obtiene mapa de ID → Nombre de vehículos.
     * Usa caché si es válido, sino consulta la BD.
     */
    public Map<String, String> getVehicleNames() {
        long now = System.currentTimeMillis();
        if (vehicleNames == null || (now - lastVehiclesUpdate) > CACHE_VALIDITY_MS) {
            refreshVehicles();
        }
        // Retornar copia defensiva
        return new HashMap<>(vehicleNames);
    }

    /**
     * Invalida el caché de productos.
     * Debe llamarse cuando se crea/edita/elimina un producto.
     */
    public void invalidateProducts() {
        productNames = null;
    }

    /**
     * Invalida el caché de vehículos.
     * Debe llamarse cuando se crea/edita/elimina un vehículo.
     */
    public void invalidateVehicles() {
        vehicleNames = null;
    }

    /**
     * Invalida todos los cachés.
     */
    public void invalidateAll() {
        productNames = null;
        vehicleNames = null;
    }

    /**
     * Refresca el caché de productos desde la BD.
     */
    private void refreshProducts() {
        try {
            productNames = productServices.getAllProducts().stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));
            lastProductsUpdate = System.currentTimeMillis();
        } catch (Exception e) {
            // En caso de error, retornar mapa vacío para evitar NPE
            productNames = new HashMap<>();
            System.err.println("CatalogCache: Error al refrescar productos → " + e.getMessage());
        }
    }

    /**
     * Refresca el caché de vehículos desde la BD.
     */
    private void refreshVehicles() {
        try {
            vehicleNames = vehicleServices.getAllVehicles().stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getName));
            lastVehiclesUpdate = System.currentTimeMillis();
        } catch (Exception e) {
            // En caso de error, retornar mapa vacío para evitar NPE
            vehicleNames = new HashMap<>();
            System.err.println("CatalogCache: Error al refrescar vehículos → " + e.getMessage());
        }
    }
}
