package com.forestech.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Measurement units used to handle fuel volumes across the system.
 */
public enum MeasurementUnit {
    GALON("GALON", "Gallon"),
    GARRAFA("GARRAFA", "Garrafon"),
    CUARTO("CUARTO", "Quarter"),
    CANECA("CANECA", "Can"),
    UNIDAD("UNIDAD", "Unit")
    ;

    private final String code;
    private final String displayName;

    MeasurementUnit(String code, String displayName) {
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

    public static MeasurementUnit fromCode(String code) {
        for (MeasurementUnit unit : values()) {
            if (unit.code.equalsIgnoreCase(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Invalid measurement unit: " + code);
    }

    /**
     * Método robusto para deserializar Enums desde JSON.
     * Maneja variaciones como plurales o mayúsculas/minúsculas.
     */
    @JsonCreator
    public static MeasurementUnit fromString(String value) {
        if (value == null) return null;
        
        String normalized = value.trim().toUpperCase();
        
        // Mapeos de compatibilidad para datos legacy o inconsistentes
        switch (normalized) {
            case "GALONES": return GALON;
            case "GARRAFAS": return GARRAFA;
            case "CUARTOS": return CUARTO;
            case "CANECAS": return CANECA;
            case "UNIDADES": return UNIDAD;
        }

        // Intento de búsqueda estándar
        for (MeasurementUnit unit : values()) {
            if (unit.name().equals(normalized) || unit.code.equals(normalized)) {
                return unit;
            }
        }
        
        // Si no se encuentra, devolver UNIDAD por defecto en lugar de romper la app
        System.err.println("⚠️ Unidad desconocida recibida: " + value + ". Usando UNIDAD por defecto.");
        return UNIDAD;
    }
}
