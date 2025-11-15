package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Checkpoint 9.4: JComboBox - Listas Desplegables
 *
 * Demuestra:
 * - JComboBox (lista desplegable/dropdown)
 * - Agregar elementos a un combo
 * - Obtener el elemento seleccionado
 * - ItemListener (detectar cambio de selección)
 * - Formulario mejorado con combos para selección
 *
 * CONCEPTO CLAVE: JComboBox
 * ==========================
 * JComboBox es perfecto para cuando el usuario debe seleccionar
 * UNA opción de una lista predefinida (no puede escribir libremente).
 *
 * Analogía: Es como un menú de restaurante - eliges de las opciones
 * disponibles, no puedes pedir algo que no está en el menú.
 *
 * DIFERENCIAS:
 * - JComboBox: selección de UNA opción (ejemplo: género, categoría)
 * - JList: selección de MÚLTIPLES opciones (ejemplo: etiquetas)
 * - JTextField: entrada libre de texto
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class FormularioConComboBox extends JFrame {

    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> cmbUnidadMedida;
    private JComboBox<String> cmbCategoria;
    private JLabel lblPreview; // Para mostrar selección actual

    /**
     * Constructor que crea el formulario con combos.
     */
    public FormularioConComboBox() {
        setTitle("Formulario con JComboBox");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL DEL FORMULARIO ==========
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayout(5, 2, 10, 15));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Fila 1: Nombre del producto
        panelFormulario.add(new JLabel("Nombre del Producto:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        // Fila 2: Precio
        panelFormulario.add(new JLabel("Precio (COP):"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        // Fila 3: Unidad de medida (COMBOBOX)
        panelFormulario.add(new JLabel("Unidad de Medida:"));
        cmbUnidadMedida = new JComboBox<>();
        // Agregar opciones al combo
        cmbUnidadMedida.addItem("Litros");
        cmbUnidadMedida.addItem("Galones");
        cmbUnidadMedida.addItem("Kilogramos");
        cmbUnidadMedida.addItem("Unidades");
        cmbUnidadMedida.addItem("Metros cúbicos");
        // Seleccionar "Litros" por defecto
        cmbUnidadMedida.setSelectedIndex(0);
        panelFormulario.add(cmbUnidadMedida);

        // Fila 4: Categoría (COMBOBOX)
        panelFormulario.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Combustibles");
        cmbCategoria.addItem("Lubricantes");
        cmbCategoria.addItem("Aditivos");
        cmbCategoria.addItem("Filtros");
        cmbCategoria.addItem("Accesorios");
        cmbCategoria.setSelectedIndex(0);
        panelFormulario.add(cmbCategoria);

        // Fila 5: Etiqueta de preview (solo para mostrar selección)
        panelFormulario.add(new JLabel("Selección actual:"));
        lblPreview = new JLabel("Litros - Combustibles");
        lblPreview.setForeground(Color.BLUE);
        panelFormulario.add(lblPreview);

        add(panelFormulario, BorderLayout.CENTER);

        // ========== LISTENERS PARA COMBOS ==========
        // Detectar cambios en la selección y actualizar preview
        cmbUnidadMedida.addItemListener(e -> actualizarPreview());
        cmbCategoria.addItemListener(e -> actualizarPreview());

        // ========== PANEL DE BOTONES ==========
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnGuardar = new JButton("Guardar Producto");
        btnGuardar.setBackground(new Color(100, 200, 100));
        btnGuardar.addActionListener(e -> guardarProducto());
        panelBotones.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panelBotones.add(btnLimpiar);

        JButton btnMostrarSeleccion = new JButton("Mostrar Selección");
        btnMostrarSeleccion.addActionListener(e -> mostrarSeleccion());
        panelBotones.add(btnMostrarSeleccion);

        add(panelBotones, BorderLayout.SOUTH);

        // Instrucciones
        JLabel lblTitulo = new JLabel(
            "Formulario con JComboBox (Listas Desplegables)",
            SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(lblTitulo, BorderLayout.NORTH);

        setVisible(true);
    }

    /**
     * Actualiza la etiqueta de preview cuando cambian los combos.
     */
    private void actualizarPreview() {
        String unidad = (String) cmbUnidadMedida.getSelectedItem();
        String categoria = (String) cmbCategoria.getSelectedItem();
        lblPreview.setText(unidad + " - " + categoria);
    }

    /**
     * Guarda el producto con validación.
     */
    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (precioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio es obligatorio",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (precio <= 0) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor a cero",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener valores de los combos
        // MÉTODO 1: getSelectedItem() - Retorna Object, necesita cast
        String unidad = (String) cmbUnidadMedida.getSelectedItem();
        String categoria = (String) cmbCategoria.getSelectedItem();

        // MÉTODO 2: getSelectedIndex() - Retorna índice numérico
        int indiceUnidad = cmbUnidadMedida.getSelectedIndex();
        int indiceCategoria = cmbCategoria.getSelectedIndex();

        String mensaje = String.format(
            "Producto guardado:\n\n" +
            "Nombre: %s\n" +
            "Precio: $%,.2f COP\n" +
            "Unidad: %s (índice: %d)\n" +
            "Categoría: %s (índice: %d)",
            nombre, precio, unidad, indiceUnidad, categoria, indiceCategoria
        );

        JOptionPane.showMessageDialog(this, mensaje,
            "Éxito", JOptionPane.INFORMATION_MESSAGE);

        System.out.println("=".repeat(50));
        System.out.println("PRODUCTO GUARDADO CON COMBOBOX:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: $" + String.format("%,.2f", precio));
        System.out.println("Unidad: " + unidad);
        System.out.println("Categoría: " + categoria);
        System.out.println("=".repeat(50));

        limpiarFormulario();
    }

    /**
     * Muestra en consola la selección actual de los combos.
     */
    private void mostrarSeleccion() {
        System.out.println("\n--- SELECCIÓN ACTUAL ---");
        System.out.println("Unidad de medida:");
        System.out.println("  - Item: " + cmbUnidadMedida.getSelectedItem());
        System.out.println("  - Índice: " + cmbUnidadMedida.getSelectedIndex());
        System.out.println("  - Total opciones: " + cmbUnidadMedida.getItemCount());

        System.out.println("Categoría:");
        System.out.println("  - Item: " + cmbCategoria.getSelectedItem());
        System.out.println("  - Índice: " + cmbCategoria.getSelectedIndex());
        System.out.println("  - Total opciones: " + cmbCategoria.getItemCount());
        System.out.println("------------------------\n");
    }

    /**
     * Limpia el formulario.
     */
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        cmbUnidadMedida.setSelectedIndex(0);
        cmbCategoria.setSelectedIndex(0);
        txtNombre.requestFocus();
    }

    /**
     * Método main.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FormularioConComboBox();
        });

        System.out.println("=".repeat(50));
        System.out.println("Checkpoint 9.4: JComboBox (Listas Desplegables)");
        System.out.println("=".repeat(50));
        System.out.println("Métodos útiles de JComboBox:");
        System.out.println("- addItem(item): Agregar opción");
        System.out.println("- getSelectedItem(): Obtener item seleccionado (Object)");
        System.out.println("- getSelectedIndex(): Obtener índice (int, empieza en 0)");
        System.out.println("- setSelectedIndex(i): Seleccionar por índice");
        System.out.println("- getItemCount(): Total de opciones");
        System.out.println("- removeAllItems(): Limpiar combo");
        System.out.println("=".repeat(50));
    }
}
