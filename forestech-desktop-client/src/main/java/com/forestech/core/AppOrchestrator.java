package com.forestech.core;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;

import com.forestech.presentation.ui.ForestechProfessionalApp;

/**
 * Coordina los puntos de entrada disponibles (CLI y GUI).
 */
public final class AppOrchestrator {

    private AppOrchestrator() {
        // Evitar instanciación
    }

    /** Lanza la interfaz gráfica en el hilo de eventos de Swing. */
    public static void startGUI() {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new ForestechProfessionalApp().setVisible(true));
    }
}
