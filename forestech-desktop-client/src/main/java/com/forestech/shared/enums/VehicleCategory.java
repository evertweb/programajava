package com.forestech.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Available vehicle categories for inventory and reporting.
 */
public enum VehicleCategory {
    CAMION("CAMION", "Truck"),
    CAMIONETA("CAMIONETA", "Pickup"),
    TRACTOR("TRACTOR", "Tractor"),
    EXCAVADORA("EXCAVADORA", "Excavator"),
    VOLQUETA("VOLQUETA", "Dumper"),
    MOTONIVELADORA("MOTONIVELADORA", "Motor grader");

    private final String code;
    private final String displayName;

    VehicleCategory(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static VehicleCategory fromCode(String code) {
        for (VehicleCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle category: " + code);
    }

    /**
     * Método robusto para deserializar Enums desde JSON.
     */
    @JsonCreator
    public static VehicleCategory fromString(String value) {
        if (value == null) return null;
        
        String normalized = value.trim().toUpperCase();
        
        // Mapeos de compatibilidad (plurales comunes)
        if (normalized.endsWith("S") && !normalized.endsWith("SS")) {
             // Intento simple de quitar la 'S' final si no encontramos la palabra exacta
             // Esto ayuda con "CAMIONES" -> "CAMION"
             String singular = normalized.substring(0, normalized.length() - 1);
             for (VehicleCategory cat : values()) {
                 if (cat.name().equals(singular)) return cat;
             }
        }

        for (VehicleCategory category : values()) {
            if (category.name().equals(normalized) || category.code.equals(normalized)) {
                return category;
            }
        }
        
        // Fallback seguro
        System.err.println("⚠️ Categoría desconocida recibida: " + value + ". Usando CAMION por defecto.");
        return CAMION;
    }
}
