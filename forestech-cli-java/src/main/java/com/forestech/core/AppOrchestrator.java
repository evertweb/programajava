package com.forestech.core;

import javax.swing.SwingUtilities;

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
        SwingUtilities.invokeLater(() -> new ForestechProfessionalApp().setVisible(true));
    }

    /** Lanza la experiencia de consola clásica. */
    public static void startCLI() {
        new AppController().iniciar();
    }
}
