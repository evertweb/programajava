package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Checkpoint 9.1: Tu Primera Ventana Swing
 *
 * Este es el "Hola Mundo" de Swing. Demuestra:
 * - Creación de JFrame (ventana principal)
 * - Configuración básica (tamaño, posición, cierre)
 * - Event Dispatch Thread (EDT) con SwingUtilities.invokeLater()
 *
 * CONCEPTO CLAVE: EDT (Event Dispatch Thread)
 * ==========================================
 * Swing NO es thread-safe. Todos los componentes GUI deben crearse
 * y modificarse en el EDT (hilo especializado de Swing).
 *
 * Analogía: El EDT es como un chef en una cocina - es el ÚNICO autorizado
 * para modificar la GUI (cocinar). Otros hilos son meseros que solo pueden
 * pasar pedidos al chef, pero no pueden cocinar directamente.
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class HelloSwingApp {

    /**
     * Constructor que crea y configura la ventana principal.
     *
     * PASOS FUNDAMENTALES PARA CREAR UNA VENTANA:
     * 1. Crear JFrame con título
     * 2. Establecer tamaño (setSize)
     * 3. Configurar operación de cierre (setDefaultCloseOperation)
     * 4. Centrar en pantalla (setLocationRelativeTo)
     * 5. Hacer visible (setVisible)
     */
    public HelloSwingApp() {
        // 1. Crear la ventana principal
        JFrame ventana = new JFrame("Mi Primera Ventana Swing");

        // 2. Establecer tamaño (ancho x alto en píxeles)
        ventana.setSize(500, 400);

        // 3. Configurar qué pasa al cerrar la ventana
        // EXIT_ON_CLOSE: Termina la aplicación completamente
        // DISPOSE_ON_CLOSE: Solo cierra esta ventana (útil para ventanas secundarias)
        // DO_NOTHING_ON_CLOSE: Permite manejar el cierre manualmente
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 4. Centrar la ventana en la pantalla
        // null = centrar respecto a la pantalla completa
        ventana.setLocationRelativeTo(null);

        // OPCIONAL: Agregar un componente simple (etiqueta de bienvenida)
        JLabel etiqueta = new JLabel("¡Bienvenido a Forestech GUI!", SwingConstants.CENTER);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 20));
        ventana.add(etiqueta);

        // 5. Hacer visible la ventana (SIEMPRE AL FINAL)
        // Si pones esto antes de configurar todo, verás la ventana
        // construyéndose paso a paso (efecto visual indeseable)
        ventana.setVisible(true);

        System.out.println("Ventana creada correctamente en el EDT");
    }

    /**
     * Método main - punto de entrada de la aplicación.
     *
     * IMPORTANTE: Usa SwingUtilities.invokeLater() para crear la GUI
     * en el Event Dispatch Thread (EDT).
     *
     * ¿Por qué?
     * - Swing no es thread-safe
     * - Crear componentes fuera del EDT puede causar comportamientos impredecibles
     * - SwingUtilities.invokeLater() garantiza ejecución en el EDT
     *
     * @param args Argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {
        // PATRÓN OBLIGATORIO PARA SWING:
        // SwingUtilities.invokeLater() programa la ejecución del código
        // en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Lambda expression equivale a:
            // new Runnable() {
            //     public void run() {
            //         new HelloSwingApp();
            //     }
            // }
            new HelloSwingApp();
        });

        System.out.println("main() ha terminado, pero la ventana sigue abierta");
        System.out.println("El EDT mantiene la aplicación viva hasta cerrar la ventana");
    }
}
