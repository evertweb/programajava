package com.forestech.presentation.ui.utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

/**
 * Esquema de fuentes centralizado.
 * Define la tipografía consistente para toda la aplicación.
 */
public final class FontScheme {

    private FontScheme() {
        // No instanciar
    }

    // Detectar si tenemos fuentes modernas disponibles
    private static final String MAIN_FONT_FAMILY = getBestAvailableFont("Segoe UI", "Roboto", "Helvetica Neue", "Arial");
    private static final String MONO_FONT_FAMILY = getBestAvailableFont("Consolas", "Menlo", "Monospaced");

    // ===== DEFINICIONES DE FUENTES =====

    public static final Font HEADER_1 = new Font(MAIN_FONT_FAMILY, Font.BOLD, 24);
    public static final Font HEADER_2 = new Font(MAIN_FONT_FAMILY, Font.BOLD, 20);
    public static final Font SUBHEADER = new Font(MAIN_FONT_FAMILY, Font.BOLD, 16);
    
    public static final Font BODY_BOLD = new Font(MAIN_FONT_FAMILY, Font.BOLD, 14);
    public static final Font BODY_REGULAR = new Font(MAIN_FONT_FAMILY, Font.PLAIN, 14);
    
    public static final Font SMALL_BOLD = new Font(MAIN_FONT_FAMILY, Font.BOLD, 12);
    public static final Font SMALL_REGULAR = new Font(MAIN_FONT_FAMILY, Font.PLAIN, 12);
    public static final Font SMALL_ITALIC = new Font(MAIN_FONT_FAMILY, Font.ITALIC, 12);

    public static final Font MONOSPACED = new Font(MONO_FONT_FAMILY, Font.PLAIN, 13);
    
    // ===== DASHBOARD SPECIFIC =====
    public static final Font DASHBOARD_VALUE = new Font(MAIN_FONT_FAMILY, Font.BOLD, 36);

    /**
     * Retorna la primera fuente disponible de la lista de preferencias.
     */
    private static String getBestAvailableFont(String... fontNames) {
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Arrays.sort(availableFonts); // Para búsqueda binaria o simplemente iterar
        
        for (String fontName : fontNames) {
            // Búsqueda simple
            for (String available : availableFonts) {
                if (available.equalsIgnoreCase(fontName)) {
                    return fontName;
                }
            }
        }
        return "Dialog"; // Fallback seguro de Java
    }
}
