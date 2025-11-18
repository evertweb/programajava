package com.forestech.presentation.ui.utils;

import java.awt.Color;

/**
 * Esquema de colores centralizado para la aplicación Forestech.
 * Define una paleta consistente de máximo 3 colores de fuente + colores de
 * fondo.
 * 
 * METODOLOGÍA: "Máximo 3 colores de fuente" asegura coherencia visual y
 * accesibilidad.
 * Todos los componentes de UI deben usar estos colores predefinidos.
 */
public final class ColorScheme {

    private ColorScheme() {
        // Clase de utilidades - no instanciar
    }

    // ===== PALETA BASE (3 Principales + 2 Secundarios) =====

    // 1. PRIMARIO (Brand / Sidebar / Headers) - Midnight Blue
    public static final Color PRIMARY = Color.decode("#1A237E");

    // 2. SECUNDARIO (Action / Selection) - Bright Blue
    public static final Color SECONDARY = Color.decode("#2196F3");

    // 3. ACENTO (Highlights / Success) - Teal
    public static final Color ACCENT = Color.decode("#009688");

    // NEUTROS
    public static final Color BACKGROUND_LIGHT = Color.decode("#F5F7FA"); // Fondo general
    public static final Color BACKGROUND_PANEL = Color.WHITE; // Cards/Paneles
    public static final Color TEXT_PRIMARY = Color.decode("#263238"); // Texto principal (Dark Slate)
    public static final Color TEXT_SECONDARY = Color.decode("#546E7A"); // Texto secundario (Blue Grey)
    public static final Color TEXT_ON_PRIMARY = Color.WHITE; // Texto sobre fondo oscuro

    // ===== ALIAS SEMÁNTICOS (Para mantener compatibilidad y claridad) =====

    public static final Color BACKGROUND_DARK = PRIMARY;
    public static final Color FOREGROUND_PRIMARY = TEXT_PRIMARY;
    public static final Color FOREGROUND_SECONDARY = TEXT_SECONDARY;
    public static final Color FOREGROUND_ACCENT = ACCENT;
    public static final Color TEXT_ON_COLOR = TEXT_ON_PRIMARY;

    // Estados
    public static final Color SUCCESS = ACCENT;
    public static final Color DANGER = Color.decode("#D32F2F"); // Excepción necesaria para errores
    public static final Color WARNING = Color.decode("#FFA000"); // Excepción necesaria para alertas

    // Alias Legacy (Mapeados a la nueva paleta)
    public static final Color PRIMARY_600 = PRIMARY;
    public static final Color PRIMARY_500 = SECONDARY;
    public static final Color SECONDARY_500 = SECONDARY;
    public static final Color SUCCESS_500 = SUCCESS;
    public static final Color DANGER_500 = DANGER;
    public static final Color WARNING_500 = WARNING;

    // ===== COMPONENTES =====

    // Tablas
    public static final Color TABLE_HEADER_BG = PRIMARY;
    public static final Color TABLE_HEADER_FG = TEXT_ON_PRIMARY;
    public static final Color TABLE_ROW_PRIMARY = BACKGROUND_PANEL;
    public static final Color TABLE_ROW_STRIPE = BACKGROUND_LIGHT;
    public static final Color TABLE_SELECTION_BG = SECONDARY;
    public static final Color TABLE_SELECTION_FG = TEXT_ON_PRIMARY;

    // Botones
    public static final Color BUTTON_PRIMARY_BG = PRIMARY;
    public static final Color BUTTON_PRIMARY_FG = TEXT_ON_PRIMARY;
    public static final Color BUTTON_SECONDARY_BG = BACKGROUND_LIGHT;
    public static final Color BUTTON_SECONDARY_FG = TEXT_PRIMARY;
    public static final Color BUTTON_INFO_BG = SECONDARY;
    public static final Color BUTTON_SUCCESS_BG = SUCCESS;
    public static final Color BUTTON_DANGER_BG = DANGER;
    public static final Color BUTTON_WARNING_BG = WARNING; // Restored

    // Dashboard Cards & Status
    public static final Color CARD_BLUE = SECONDARY;
    public static final Color CARD_GREEN = ACCENT;
    public static final Color CARD_RED = DANGER;
    public static final Color CARD_YELLOW = WARNING;

    public static final Color STATUS_INFO = SECONDARY;
    public static final Color STATUS_SUCCESS = SUCCESS;
    public static final Color STATUS_WARNING = WARNING;
    public static final Color STATUS_DANGER = DANGER;
    public static final Color STATUS_SECONDARY = PRIMARY;

    // Misc
    public static final Color BACKGROUND_ERROR_SOFT = Color.decode("#FDECEE"); // Kept for utility
    public static final Color BORDER_SUBTLE = Color.decode("#D0D7DE"); // Kept for utility

    // ===== MÉTODOS HELPER =====

    /**
     * Valida que el contraste entre foreground y background sea suficiente.
     * Retorna true si el contraste es aceptable (WCAG AA standard: 4.5:1).
     * 
     * @param foreground Color de fuente
     * @param background Color de fondo
     * @return true si el contraste es aceptable
     */
    public static boolean hasGoodContrast(Color foreground, Color background) {
        double fgLuminance = calculateLuminance(foreground);
        double bgLuminance = calculateLuminance(background);
        double contrast = (Math.max(fgLuminance, bgLuminance) + 0.05)
                / (Math.min(fgLuminance, bgLuminance) + 0.05);
        return contrast >= 4.5; // WCAG AA
    }

    /**
     * Calcula la luminancia relativa de un color según WCAG.
     */
    private static double calculateLuminance(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        r = r <= 0.03928 ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
        g = g <= 0.03928 ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
        b = b <= 0.03928 ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    /**
     * Verifica que los colores de fuente cumplan con el estándar de máximo 3
     * colores.
     * (Método de utilidad para validación - aunque es conceptual)
     */
    public static void validateColorScheme() {
        System.out.println("✓ Esquema de colores validado:");
        System.out.println("  - Primario: " + FOREGROUND_PRIMARY.toString());
        System.out.println("  - Secundario: " + FOREGROUND_SECONDARY.toString());
        System.out.println("  - Terciario: " + FOREGROUND_ACCENT.toString());
        System.out.println("  - Contraste Primario sobre blanco: "
                + (hasGoodContrast(FOREGROUND_PRIMARY, BACKGROUND_LIGHT) ? "✓ OK" : "✗ FALLO"));
        System.out.println("  - Contraste Secundario sobre oscuro: "
                + (hasGoodContrast(FOREGROUND_SECONDARY, BACKGROUND_DARK) ? "✓ OK" : "✗ FALLO"));
    }
}
