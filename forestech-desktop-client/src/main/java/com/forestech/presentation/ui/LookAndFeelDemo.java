package com.forestech.presentation.ui;

import com.forestech.presentation.ui.utils.ColorScheme;

import javax.swing.*;
import java.awt.*;

/**
 * Checkpoint 9.12: Look and Feel - Personalización Visual
 *
 * <p>Demuestra:</p>
 * <ul>
 *   <li>UIManager para cambiar la apariencia de Swing</li>
 *   <li>Diferentes Look and Feels disponibles</li>
 *   <li>Cambio de L&amp;F en tiempo real</li>
 *   <li>Aplicar L&amp;F del sistema operativo</li>
 * </ul>
 *
 * CONCEPTO CLAVE: Look and Feel (LaF)
 * ====================================
 * Look and Feel controla la apariencia visual de TODOS los componentes Swing.
 * Es como cambiar el "tema" de la aplicación.
 *
 * <p>LaFs DISPONIBLES:</p>
 * <ol>
 *   <li>Metal (por defecto de Java) - multiplataforma pero anticuado</li>
 *   <li>Nimbus - moderno, multiplataforma</li>
 *   <li>Sistema - usa la apariencia del sistema operativo (RECOMENDADO)</li>
 *   <li>Windows (solo en Windows)</li>
 *   <li>GTK+ (solo en Linux)</li>
 *   <li>Aqua (solo en macOS)</li>
 * </ol>
 *
 * MEJOR PRÁCTICA:
 * Usar siempre el LaF del sistema para que la app se vea nativa.
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class LookAndFeelDemo extends JFrame {

    private JComboBox<String> cmbLookAndFeel;
    private JButton btnAplicar;
    private JTextArea txtInfo;

    /**
     * Constructor que crea la ventana de demostración.
     */
    public LookAndFeelDemo() {
        setTitle("Look and Feel Demo - Personalización Visual");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(ColorScheme.BACKGROUND_LIGHT);

        // ========== PANEL SUPERIOR: SELECCIÓN DE L&F ==========
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelControl.setBorder(BorderFactory.createTitledBorder("Seleccionar Look and Feel"));
        panelControl.setBackground(ColorScheme.BACKGROUND_PANEL);

        JLabel lblApariencia = new JLabel("Apariencia:");
        lblApariencia.setForeground(ColorScheme.FOREGROUND_PRIMARY);
        panelControl.add(lblApariencia);

        // ComboBox con los L&Fs disponibles
        cmbLookAndFeel = new JComboBox<>(new String[]{
            "Sistema Operativo (Recomendado)",
            "Nimbus (Moderno)",
            "Metal (Java por defecto)",
            "Windows Classic"
        });
        panelControl.add(cmbLookAndFeel);

        btnAplicar = new JButton("Aplicar");
        styleFilledButton(btnAplicar, ColorScheme.BUTTON_PRIMARY_BG);
        btnAplicar.addActionListener(e -> cambiarLookAndFeel());
        panelControl.add(btnAplicar);

        JButton btnInfo = new JButton("Info del Sistema");
        styleFilledButton(btnInfo, ColorScheme.BUTTON_INFO_BG);
        btnInfo.addActionListener(e -> mostrarInfoSistema());
        panelControl.add(btnInfo);

        add(panelControl, BorderLayout.NORTH);

        // ========== PANEL CENTRAL: COMPONENTES DE MUESTRA ==========
        JPanel panelMuestra = new JPanel();
        panelMuestra.setLayout(new BoxLayout(panelMuestra, BoxLayout.Y_AXIS));
        panelMuestra.setBackground(ColorScheme.BACKGROUND_PANEL);
        panelMuestra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Componentes de Muestra"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Etiqueta
        JLabel lblDemo = new JLabel("Esta es una etiqueta de demostración");
        lblDemo.setFont(new Font("Arial", Font.BOLD, 14));
        panelMuestra.add(lblDemo);
        panelMuestra.add(Box.createVerticalStrut(10));

        // Campo de texto
        JTextField txtDemo = new JTextField("Campo de texto editable");
        panelMuestra.add(txtDemo);
        panelMuestra.add(Box.createVerticalStrut(10));

        // ComboBox
        JComboBox<String> cmbDemo = new JComboBox<>(new String[]{"Opción 1", "Opción 2", "Opción 3"});
        panelMuestra.add(cmbDemo);
        panelMuestra.add(Box.createVerticalStrut(10));

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(ColorScheme.BACKGROUND_PANEL);
        JButton btn1 = new JButton("Botón Normal");
        styleSecondaryButton(btn1);
        JButton btn2 = new JButton("Botón Deshabilitado");
        btn2.setEnabled(false);
        JButton btn3 = new JButton("Botón con Color");
        styleFilledButton(btn3, ColorScheme.BUTTON_INFO_BG);
        panelBotones.add(btn1);
        panelBotones.add(btn2);
        panelBotones.add(btn3);
        panelMuestra.add(panelBotones);
        panelMuestra.add(Box.createVerticalStrut(10));

        // CheckBox y RadioButton
        JPanel panelCheck = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCheck.setBackground(ColorScheme.BACKGROUND_PANEL);
        JCheckBox check1 = new JCheckBox("CheckBox 1");
        JCheckBox check2 = new JCheckBox("CheckBox 2", true);
        JRadioButton radio1 = new JRadioButton("Radio 1", true);
        JRadioButton radio2 = new JRadioButton("Radio 2");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radio1);
        grupo.add(radio2);
        panelCheck.add(check1);
        panelCheck.add(check2);
        panelCheck.add(radio1);
        panelCheck.add(radio2);
        panelMuestra.add(panelCheck);
        panelMuestra.add(Box.createVerticalStrut(10));

        // Slider
        JSlider slider = new JSlider(0, 100, 50);
        slider.setMajorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(ColorScheme.BACKGROUND_PANEL);
        panelMuestra.add(slider);

        add(panelMuestra, BorderLayout.CENTER);

        // ========== PANEL INFERIOR: INFORMACIÓN ==========
        txtInfo = new JTextArea(5, 50);
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInfo.setBackground(ColorScheme.BACKGROUND_PANEL);
        txtInfo.setForeground(ColorScheme.FOREGROUND_PRIMARY);
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setBorder(BorderFactory.createTitledBorder("Información del Look and Feel"));
        scrollInfo.getViewport().setBackground(ColorScheme.BACKGROUND_PANEL);
        add(scrollInfo, BorderLayout.SOUTH);

        // Mostrar info del LaF actual
        mostrarInfoLaFActual();

        setVisible(true);
    }

    /**
     * Cambia el Look and Feel según la selección del usuario.
     */
    private void cambiarLookAndFeel() {
        String seleccion = (String) cmbLookAndFeel.getSelectedItem();
        String className = "";

        try {
            switch (seleccion) {
                case "Sistema Operativo (Recomendado)" ->
                    className = UIManager.getSystemLookAndFeelClassName();

                case "Nimbus (Moderno)" ->
                    className = "javax.swing.plaf.nimbus.NimbusLookAndFeel";

                case "Metal (Java por defecto)" ->
                    className = "javax.swing.plaf.metal.MetalLookAndFeel";

                case "Windows Classic" ->
                    className = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            }

            // APLICAR EL NUEVO LOOK AND FEEL
            UIManager.setLookAndFeel(className);

            // ACTUALIZAR TODOS LOS COMPONENTES DE LA VENTANA
            SwingUtilities.updateComponentTreeUI(this);

            // REAJUSTAR TAMAÑO
            this.pack();
            this.setSize(700, 500); // Restaurar tamaño original

            mostrarInfoLaFActual();

            JOptionPane.showMessageDialog(this,
                "Look and Feel aplicado correctamente:\n" + seleccion,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al aplicar Look and Feel:\n" + e.getMessage() + "\n\n" +
                "Este LaF puede no estar disponible en tu sistema operativo.",
                "Error",
                JOptionPane.ERROR_MESSAGE);

            System.err.println("Error al aplicar LaF: " + e.getMessage());
        }
    }

    /**
     * Muestra información del Look and Feel actual.
     */
    private void mostrarInfoLaFActual() {
        String nombre = UIManager.getLookAndFeel().getName();
        String className = UIManager.getLookAndFeel().getClass().getName();

        String info = String.format(
            "LOOK AND FEEL ACTUAL:\n" +
            "Nombre: %s\n" +
            "Clase: %s\n\n" +
            "LaFs INSTALADOS EN ESTE SISTEMA:\n",
            nombre, className
        );

        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            info += String.format("  - %s (%s)\n", laf.getName(), laf.getClassName());
        }

        txtInfo.setText(info);
    }

    /**
     * Muestra información del sistema operativo.
     */
    private void mostrarInfoSistema() {
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String arch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");

        String mensaje = String.format(
            "INFORMACIÓN DEL SISTEMA:\n\n" +
            "Sistema Operativo: %s\n" +
            "Versión: %s\n" +
            "Arquitectura: %s\n" +
            "Java Version: %s\n\n" +
            "LaF Recomendado:\n%s",
            os, version, arch, javaVersion,
            UIManager.getSystemLookAndFeelClassName()
        );

        JOptionPane.showMessageDialog(this, mensaje,
            "Info del Sistema", JOptionPane.INFORMATION_MESSAGE);

        System.out.println("\n" + "=".repeat(50));
        System.out.println(mensaje);
        System.out.println("=".repeat(50) + "\n");
    }

    /**
     * Método main.
     *
     * @param args Argumentos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LookAndFeelDemo();
        });

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.12: Look and Feel Demo");
        System.out.println("=".repeat(60));
        System.out.println("\nLooks and Feels disponibles:");
        System.out.println("1. Sistema - Apariencia nativa del SO");
        System.out.println("2. Nimbus - Moderno, multiplataforma");
        System.out.println("3. Metal - Java por defecto (anticuado)");
        System.out.println("4. Windows - Solo disponible en Windows");
        System.out.println("\nRECOMENDACIÓN:");
        System.out.println("Siempre usa el LaF del sistema para aplicaciones profesionales.");
        System.out.println("\nCÓMO APLICAR EN TU APP:");
        System.out.println("try {");
        System.out.println("    UIManager.setLookAndFeel(");
        System.out.println("        UIManager.getSystemLookAndFeelClassName()");
        System.out.println("    );");
        System.out.println("} catch (Exception e) { e.printStackTrace(); }");
        System.out.println("=".repeat(60));
    }

    private void styleFilledButton(JButton button, Color background) {
        button.setBackground(background);
        button.setForeground(ColorScheme.TEXT_ON_COLOR);
        button.setFocusPainted(false);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(ColorScheme.BUTTON_SECONDARY_BG);
        button.setForeground(ColorScheme.BUTTON_SECONDARY_FG);
        button.setFocusPainted(false);
    }
}
