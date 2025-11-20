package com.forestech.simpleui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SimpleMain {

    public static void main(String[] args) {
        // Swing no es "thread-safe", así que siempre debemos iniciar la UI
        // dentro del hilo de eventos de Swing (Event Dispatch Thread).
        // Esto evita muchos errores raros de congelamiento.
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        System.out.println("Iniciando Dashboard...");

        // Launch the Main Shell
        com.forestech.simpleui.shell.MainFrame mainFrame = new com.forestech.simpleui.shell.MainFrame();
        mainFrame.setVisible(true);

        System.out.println("Dashboard visible.");
    }
}
