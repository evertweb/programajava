package com.forestech.shared.enums;

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
}
