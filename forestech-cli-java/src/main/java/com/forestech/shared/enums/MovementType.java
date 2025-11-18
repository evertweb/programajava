package com.forestech.shared.enums;

/**
 * Fuel movement types. Keeps business values centralized and type-safe.
 */
public enum MovementType {
    ENTRADA("ENTRADA", "Fuel entering the warehouse from a supplier"),
    SALIDA("SALIDA", "Fuel leaving the warehouse to refuel a vehicle");

    private final String code;
    private final String description;

    MovementType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MovementType fromCode(String code) {
        for (MovementType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid movement type: " + code);
    }
}
