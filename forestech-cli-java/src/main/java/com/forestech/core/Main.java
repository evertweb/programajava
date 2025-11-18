package com.forestech.core;

/**
 * Clase Main - Punto de entrada central de Forestech.
 *
 * <p><strong>Flujo:</strong></p>
 * <ol>
 *   <li>El usuario ejecuta: mvn exec:java -Dexec.mainClass="com.forestech.core.Main"</li>
 *   <li>Main delega en AppOrchestrator para decidir GUI o CLI</li>
 *   <li>AppOrchestrator lanza ForestechProfessionalApp (GUI) o AppController (CLI)</li>
 *   <li>El usuario interactúa con la interfaz seleccionada</li>
 * </ol>
 */
public class Main {

    /**
     * Método principal de la aplicación.
     *
     * @param args Argumentos de línea de comandos ("--gui" lanza la interfaz gráfica)
     */
    public static void main(String[] args) {
        boolean guiRequested = args.length > 0 && "--gui".equalsIgnoreCase(args[0]);

        if (guiRequested) {
            AppOrchestrator.startGUI();
        } else {
            AppOrchestrator.startCLI();
        }
    }
}
