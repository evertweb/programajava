package com.forestech.enums;

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
}
