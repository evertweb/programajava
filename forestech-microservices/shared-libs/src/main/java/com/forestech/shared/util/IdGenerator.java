package com.forestech.shared.util;

import java.util.UUID;

/**
 * Utilidad para generar IDs únicos con prefijo
 * Reemplaza la generación manual de IDs en cada servicio
 * 
 * Ejemplos:
 * - IdGenerator.generate("PROD") → "PROD-A1B2C3D4"
 * - IdGenerator.generate("VEH") → "VEH-E5F6G7H8"
 */
public class IdGenerator {

    /**
     * Genera un ID único con el prefijo especificado
     * 
     * @param prefix Prefijo para el ID (ej: "PROD", "VEH", "MOV")
     * @return ID generado en formato "PREFIX-XXXXXXXX"
     */
    public static String generate(String prefix) {
        return prefix + "-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
