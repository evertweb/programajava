package com.forestech.shared.utils;

import java.util.UUID;

/**
 * 🎓 Generador de IDs únicos para Forestech
 * <p>
 * Esta clase centraliza la generación de IDs para diferentes entidades.
 * Cada entidad tiene un prefijo que la identifica.
 * <p>
 * Ejemplos:
 * - Movimiento: MOV-A1B2C3D4
 * - Proveedor: PROV-E5F6G7H8
 * - Combustible: FUEL-I9J0K1L2
 */
public class IdGenerator {

    /**
     * Genera un ID para un movimiento
     * Formato: MOV-XXXXXXXX (12 caracteres)
     *
     * @return ID único del movimiento
     */
    public static String generateMovementId() {
        return generateId("MOV");
    }

    /**
     * Genera un ID para un proveedor
     * Formato: PROV-XXXXXXXX (13 caracteres)
     *
     * @return ID único del proveedor
     */
    public static String generateSupplierId() {
        return generateId("PROV");
    }

    /**
     * Genera un ID para un combustible
     * Formato: FUEL-XXXXXXXX (13 caracteres)
     *
     * @return ID único del combustible
     */
    public static String generateFuelId() {
        return generateId("FUEL");
    }

    public static String generateVehicleId() {
        return generateId("VEH");
    }

    /**
     * Método privado que genera el ID con el prefijo dado
     * <p>
     * ¿Cómo funciona?
     * 1. UUID.randomUUID() → Genera un UUID aleatorio
     * 2. .toString() → Lo convierte a String
     * 3. .substring(0, 8) → Toma los primeros 8 caracteres
     * 4. .toUpperCase() → Los convierte a mayúsculas
     * 5. prefix + "-" + ... → Agrega el prefijo (MOV-, PROV-, etc.)
     *
     * @param prefix Prefijo del ID (MOV, PROV, FUEL)
     * @return ID formateado
     */
    private static String generateId(String prefix) {
        String uniquePart = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        return prefix + "-" + uniquePart;
    }

    /**
     * Versión alternativa: ID con timestamp (ordenable por fecha)
     * Formato: MOV-1234567890-AB12 (22 caracteres)
     * <p>
     * Ventaja: Los IDs están ordenados cronológicamente
     *
     * @return ID con timestamp
     */
    public static String generateMovementIdWithTimestamp() {
        long timestamp = System.currentTimeMillis();
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "MOV-" + timestamp + "-" + random;
    }


}

