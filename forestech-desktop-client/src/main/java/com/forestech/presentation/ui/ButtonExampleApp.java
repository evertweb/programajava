package com.forestech.presentation.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Checkpoint 9.2: Botones y Etiquetas con ActionListener
 *
 * Demuestra:
 * - Uso de JLabel (etiquetas de texto)
 * - Uso de JButton (botones clicables)
 * - ActionListener (escuchador de eventos de clic)
 * - BorderLayout (gestor de diseño de 5 zonas)
 * - Cambio de texto dinámico en respuesta a eventos
 *
 * CONCEPTO CLAVE: ActionListener
 * ================================
 * Un ActionListener "escucha" eventos de acción (como clics en botones).
 * Es como tener un vigilante que detecta cuando alguien presiona un botón
 * y ejecuta código en respuesta.
 *
 * Hay 3 formas de implementar ActionListener:
 * 1. Clase anónima (verbosa pero clara)
 * 2. Lambda expression (compacta, Java 8+)
 * 3. Clase separada que implementa ActionListener (para lógica compleja)
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class ButtonExampleApp extends JFrame {

    // Componentes como atributos para poder acceder desde múltiples métodos
    private JLabel etiqueta;
    private int contadorClics = 0;

    /**
     * Constructor que configura la ventana con botones interactivos.
     */
    public ButtonExampleApp() {
        // Configurar la ventana principal
        setTitle("Ejemplo de Botones y Eventos");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // LAYOUT MANAGER: BorderLayout
        // Divide la ventana en 5 zonas: NORTH, SOUTH, EAST, WEST, CENTER
        // Por defecto, JFrame ya usa BorderLayout
        setLayout(new BorderLayout());

        // ========== ZONA NORTH (Arriba) ==========
        etiqueta = new JLabel("Haz clic en algún botón", SwingConstants.CENTER);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 18));
        etiqueta.setOpaque(true); // Hacer opaco para ver el color de fondo
        etiqueta.setBackground(Color.LIGHT_GRAY);
        add(etiqueta, BorderLayout.NORTH);

        // ========== ZONA CENTER (Centro) ==========
        // Panel para organizar múltiples botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        // FlowLayout: organiza componentes en fila, salta a siguiente línea si no caben
        // Parámetros: alineación, espacio horizontal, espacio vertical

        // BOTÓN 1: Incrementar contador (usando clase anónima)
        JButton btnIncrementar = new JButton("Incrementar Contador");
        btnIncrementar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorClics++;
                etiqueta.setText("Clics totales: " + contadorClics);
                etiqueta.setForeground(Color.BLUE);
            }
        });
        panelBotones.add(btnIncrementar);

        // BOTÓN 2: Cambiar color de fondo (usando lambda)
        JButton btnCambiarColor = new JButton("Cambiar Color");
        btnCambiarColor.addActionListener(e -> {
            // Lambda: sintaxis compacta para ActionListener
            // (ActionEvent e) -> { código }
            Color colorAleatorio = new Color(
                (int)(Math.random() * 256),
                (int)(Math.random() * 256),
                (int)(Math.random() * 256)
            );
            etiqueta.setBackground(colorAleatorio);
            etiqueta.setText("Color cambiado - Clics: " + contadorClics);
        });
        panelBotones.add(btnCambiarColor);

        // BOTÓN 3: Resetear contador (usando lambda)
        JButton btnReset = new JButton("Resetear");
        btnReset.addActionListener(e -> {
            contadorClics = 0;
            etiqueta.setText("Contador reseteado");
            etiqueta.setForeground(Color.RED);
            etiqueta.setBackground(Color.LIGHT_GRAY);
        });
        panelBotones.add(btnReset);

        add(panelBotones, BorderLayout.CENTER);

        // ========== ZONA SOUTH (Abajo) ==========
        JButton btnSalir = new JButton("Salir de la Aplicación");
        btnSalir.setBackground(new Color(255, 100, 100)); // Rojo claro
        btnSalir.addActionListener(e -> {
            // Mostrar diálogo de confirmación antes de cerrar
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres salir?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                System.out.println("Aplicación cerrada por el usuario");
                System.exit(0); // Terminar la aplicación
            }
        });
        add(btnSalir, BorderLayout.SOUTH);

        // Hacer visible la ventana
        setVisible(true);
    }

    /**
     * Método main para ejecutar la aplicación.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // SIEMPRE crear componentes Swing en el EDT
        SwingUtilities.invokeLater(() -> {
            new ButtonExampleApp();
        });

        System.out.println("=".repeat(50));
        System.out.println("Checkpoint 9.2: Botones y ActionListener");
        System.out.println("=".repeat(50));
        System.out.println("Conceptos demostrados:");
        System.out.println("- JLabel con texto dinámico");
        System.out.println("- JButton con ActionListener");
        System.out.println("- BorderLayout (5 zonas)");
        System.out.println("- FlowLayout para paneles");
        System.out.println("- JOptionPane para confirmación");
        System.out.println("- Cambio de colores y texto en tiempo real");
        System.out.println("=".repeat(50));
    }
}
