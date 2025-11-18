package com.forestech.presentation.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Checkpoint 9.5: JTable - Tablas para Mostrar Datos
 *
 * Demuestra:
 * - JTable (tabla con filas y columnas)
 * - DefaultTableModel (modelo de datos para la tabla)
 * - JScrollPane (barras de desplazamiento)
 * - Agregar, eliminar y limpiar filas
 * - Obtener fila seleccionada
 * - Hacer celdas no editables
 *
 * CONCEPTO CLAVE: MVC en Swing
 * =============================
 * JTable sigue el patrón Model-View-Controller:
 * - MODEL: DefaultTableModel (datos)
 * - VIEW: JTable (visualización)
 * - CONTROLLER: ActionListeners (lógica)
 *
 * Analogía: JTable es como una hoja de Excel en tu aplicación.
 * Filas = registros, Columnas = campos.
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class TablaProductosApp extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> cmbUnidad;

    /**
     * Constructor que crea la aplicación con tabla.
     */
    public TablaProductosApp() {
        setTitle("Gestor de Productos - JTable");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR: FORMULARIO ==========
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(15);
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Precio:"));
        txtPrecio = new JTextField(10);
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Unidad:"));
        cmbUnidad = new JComboBox<>(new String[]{"Litros", "Galones", "Kg", "Unidades"});
        panelFormulario.add(cmbUnidad);

        JButton btnAgregar = new JButton("Agregar a Tabla");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarProducto());
        panelFormulario.add(btnAgregar);

        add(panelFormulario, BorderLayout.NORTH);

        // ========== PANEL CENTRAL: TABLA ==========
        // Definir columnas de la tabla
        String[] columnas = {"ID", "Nombre", "Precio (COP)", "Unidad"};

        // Crear modelo de tabla
        // Parámetro 1: array de columnas
        // Parámetro 2: número inicial de filas (0 = vacía)
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer todas las celdas NO editables
                // El usuario no puede hacer doble clic para editar
                return false;
            }
        };

        // Crear JTable con el modelo
        tabla = new JTable(modeloTabla);

        // Configuración estética de la tabla
        tabla.setRowHeight(25); // Altura de filas
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180)); // Azul acero
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo 1 fila seleccionable
        tabla.setGridColor(Color.LIGHT_GRAY);

        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120); // Precio
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100); // Unidad

        // JScrollPane: agregar barras de desplazamiento
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Productos Registrados"));
        add(scrollPane, BorderLayout.CENTER);

        // ========== PANEL INFERIOR: BOTONES DE ACCIÓN ==========
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        JButton btnLimpiarTabla = new JButton("Limpiar Toda la Tabla");
        btnLimpiarTabla.addActionListener(e -> limpiarTabla());
        panelBotones.add(btnLimpiarTabla);

        JButton btnMostrarInfo = new JButton("Mostrar Info de Tabla");
        btnMostrarInfo.addActionListener(e -> mostrarInfoTabla());
        panelBotones.add(btnMostrarInfo);

        add(panelBotones, BorderLayout.SOUTH);

        // Agregar algunos productos de ejemplo
        agregarEjemplos();

        setVisible(true);
    }

    /**
     * Agrega un producto a la tabla con validación.
     */
    private void agregarProducto() {
        String nombre = txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre del producto",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (precioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el precio",
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

        // Generar ID automático
        int id = modeloTabla.getRowCount() + 1;

        String unidad = (String) cmbUnidad.getSelectedItem();

        // AGREGAR FILA A LA TABLA
        // Object[] representa una fila completa
        Object[] fila = {
            "PROD-" + String.format("%03d", id),
            nombre,
            String.format("$%,.2f", precio),
            unidad
        };

        modeloTabla.addRow(fila);

        System.out.println("Producto agregado a tabla: " + nombre);

        // Limpiar campos
        txtNombre.setText("");
        txtPrecio.setText("");
        txtNombre.requestFocus();
    }

    /**
     * Elimina el producto seleccionado de la tabla.
     */
    private void eliminarProducto() {
        // Obtener fila seleccionada (-1 si no hay selección)
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un producto de la tabla para eliminar",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener datos de la fila para confirmar
        String id = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el producto:\n" + id + " - " + nombre + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(filaSeleccionada);
            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Producto eliminado: " + nombre);
        }
    }

    /**
     * Limpia todas las filas de la tabla.
     */
    private void limpiarTabla() {
        int totalFilas = modeloTabla.getRowCount();

        if (totalFilas == 0) {
            JOptionPane.showMessageDialog(this, "La tabla ya está vacía",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar todos los " + totalFilas + " productos de la tabla?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // setRowCount(0) elimina todas las filas
            modeloTabla.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Tabla limpiada correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Tabla limpiada completamente");
        }
    }

    /**
     * Muestra información sobre el estado actual de la tabla.
     */
    private void mostrarInfoTabla() {
        int totalFilas = modeloTabla.getRowCount();
        int totalColumnas = modeloTabla.getColumnCount();
        int filaSeleccionada = tabla.getSelectedRow();

        String info = String.format(
            "INFORMACIÓN DE LA TABLA\n\n" +
            "Total de filas: %d\n" +
            "Total de columnas: %d\n" +
            "Fila seleccionada: %s",
            totalFilas,
            totalColumnas,
            filaSeleccionada == -1 ? "Ninguna" : String.valueOf(filaSeleccionada)
        );

        System.out.println("\n" + "=".repeat(50));
        System.out.println(info);

        // Mostrar datos de la fila seleccionada
        if (filaSeleccionada != -1) {
            System.out.println("\nDATOS DE LA FILA SELECCIONADA:");
            for (int col = 0; col < totalColumnas; col++) {
                String nombreColumna = modeloTabla.getColumnName(col);
                Object valor = modeloTabla.getValueAt(filaSeleccionada, col);
                System.out.println("  " + nombreColumna + ": " + valor);
            }
        }
        System.out.println("=".repeat(50) + "\n");

        JOptionPane.showMessageDialog(this, info, "Info de Tabla",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Agrega productos de ejemplo a la tabla.
     */
    private void agregarEjemplos() {
        Object[][] ejemplos = {
            {"PROD-001", "Diesel Premium", "$12,500.00", "Litros"},
            {"PROD-002", "Gasolina Extra", "$11,800.00", "Litros"},
            {"PROD-003", "Aceite 15W40", "$45,000.00", "Galones"}
        };

        for (Object[] fila : ejemplos) {
            modeloTabla.addRow(fila);
        }

        System.out.println("Productos de ejemplo agregados a la tabla");
    }

    /**
     * Método main.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TablaProductosApp();
        });

        System.out.println("=".repeat(50));
        System.out.println("Checkpoint 9.5: JTable (Tablas)");
        System.out.println("=".repeat(50));
        System.out.println("Métodos principales:");
        System.out.println("- modeloTabla.addRow(Object[]): Agregar fila");
        System.out.println("- modeloTabla.removeRow(int): Eliminar fila");
        System.out.println("- modeloTabla.setRowCount(0): Limpiar tabla");
        System.out.println("- tabla.getSelectedRow(): Obtener fila seleccionada");
        System.out.println("- modeloTabla.getValueAt(row, col): Obtener valor");
        System.out.println("- modeloTabla.getRowCount(): Total de filas");
        System.out.println("=".repeat(50));
    }
}
