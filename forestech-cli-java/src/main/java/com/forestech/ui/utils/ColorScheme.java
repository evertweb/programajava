package com.forestech.ui.utils;

import java.awt.Color;

/**
 * Esquema de colores centralizado para la aplicación Forestech.
 * Define una paleta consistente de máximo 3 colores de fuente + colores de fondo.
 * 
 * METODOLOGÍA: "Máximo 3 colores de fuente" asegura coherencia visual y accesibilidad.
 * Todos los componentes de UI deben usar estos colores predefinidos.
 */
public final class ColorScheme {

    private ColorScheme() {
        // Clase de utilidades - no instanciar
    }

    // ===== COLORES DE FUENTE (3 colores máximo) =====
    
    /**
     * Color de fuente PRIMARIO: Texto normal y contenido general.
     * Gris oscuro para máxima legibilidad en fondos claros.
     */
    public static final Color FOREGROUND_PRIMARY = new Color(51, 51, 51); // #333333

    /**
     * Color de fuente SECUNDARIO: Texto sobre fondos oscuros (headers, botones activos).
     * Blanco para máximo contraste en backgrounds oscuros.
     */
    public static final Color FOREGROUND_SECONDARY = Color.WHITE;

    /**
     * Color de fuente TERCIARIO: Acentos, destacados, enlaces, texto de error.
     * Azul oscuro para diferenciación visual.
     */
    public static final Color FOREGROUND_ACCENT = new Color(26, 90, 150); // #1a5a96

    // ===== COLORES DE FONDO COMUNES =====
    
    /** Background claro para paneles principales */
    public static final Color BACKGROUND_LIGHT = Color.WHITE;

    /** Background oscuro para navegación y headers */
    public static final Color BACKGROUND_DARK = new Color(45, 52, 54); // #2D3436

    /** Background para tablas (header) */
    public static final Color TABLE_HEADER_BG = new Color(70, 130, 180); // #4682B4

    /** Background para inputs/campos */
    public static final Color INPUT_BG = Color.WHITE;

    // ===== COLORES PARA BOTONES (mantienen coherencia) =====
    
    /** Botón Success: verde coherente con diseño */
    public static final Color BUTTON_SUCCESS_BG = new Color(46, 204, 113); // #2ECC71

    /** Botón Danger/Delete: rojo coherente */
    public static final Color BUTTON_DANGER_BG = new Color(231, 76, 60); // #E74C3C

    /** Botón Info: azul coherente */
    public static final Color BUTTON_INFO_BG = new Color(52, 152, 219); // #3498DB

    /** Botón Neutral: gris neutro */
    public static final Color BUTTON_NEUTRAL_BG = new Color(149, 165, 166); // #95A5A6

    // ===== COLORES DE ESTADO (Dashboard) =====
    
    public static final Color STATUS_INFO = new Color(52, 152, 219); // Azul
    public static final Color STATUS_SUCCESS = new Color(46, 204, 113); // Verde
    public static final Color STATUS_WARNING = new Color(241, 196, 15); // Amarillo
    public static final Color STATUS_DANGER = new Color(231, 76, 60); // Rojo
    public static final Color STATUS_SECONDARY = new Color(155, 89, 182); // Púrpura

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
     * Verifica que los colores de fuente cumplan con el estándar de máximo 3 colores.
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
