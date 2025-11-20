package com.forestech.presentation.ui.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.Icon;
import java.awt.Color;

/**
 * Utility class to manage and load SVG icons using FlatLaf Extras.
 * Centralizes icon loading to ensure consistency and ease of maintenance.
 */
public class IconManager {

    // Base path for icons in resources
    private static final String ICON_BASE_PATH = "icons/";

    /**
     * Loads an SVG icon from the resources.
     * 
     * @param name   The name of the icon file (without extension).
     * @param width  The desired width.
     * @param height The desired height.
     * @return The loaded FlatSVGIcon, or null if not found.
     */
    public static FlatSVGIcon getIcon(String name, int width, int height) {
        String path = ICON_BASE_PATH + name + ".svg";
        try {
            return new FlatSVGIcon(path, width, height);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads a standard size icon (16x16) for buttons/menus.
     */
    public static FlatSVGIcon getIcon(String name) {
        return getIcon(name, 16, 16);
    }

    /**
     * Loads a larger icon (24x24) for toolbars or main actions.
     */
    public static FlatSVGIcon getLargeIcon(String name) {
        return getIcon(name, 24, 24);
    }

    /**
     * Loads an icon with a specific color filter.
     */
    public static FlatSVGIcon getIcon(String name, int size, Color color) {
        FlatSVGIcon icon = getIcon(name, size, size);
        if (icon != null) {
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color1 -> color));
        }
        return icon;
    }
}
