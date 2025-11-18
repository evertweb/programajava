package com.forestech.presentation.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Checkpoint 9.3: Campos de Texto y Formularios con Validación
 *
 * Demuestra:
 * - JTextField (campos de entrada de texto)
 * - JTextArea (área de texto multilínea)
 * - GridLayout (cuadrícula de filas x columnas iguales)
 * - Validación de datos ingresados por el usuario
 * - JOptionPane para mensajes de error/éxito
 *
 * CONCEPTO CLAVE: Validación de Entrada
 * ======================================
 * NUNCA confíes en que el usuario ingrese datos correctos.
 * Siempre valida:
 * 1. Campos vacíos
 * 2. Tipos de datos (texto vs número)
 * 3. Rangos válidos (precio > 0, cantidad > 0, etc.)
 *
 * Analogía: La validación es como un control de seguridad en un aeropuerto.
 * Verifica que todo esté en orden antes de permitir el acceso.
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class FormularioProductoSimple extends JFrame {

    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtCategoria;
    private JTextArea txtAreaDescripcion;

    /**
     * Constructor que crea el formulario de producto simple.
     */
    public FormularioProductoSimple() {
        // Configuración de la ventana
        setTitle("Formulario de Producto - Con Validación");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL DEL FORMULARIO (CENTER) ==========
        JPanel panelFormulario = new JPanel();
        // GridLayout: cuadrícula de 5 filas x 2 columnas
        // Parámetros: filas, columnas, espacioHorizontal, espacioVertical
        panelFormulario.setLayout(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        // EmptyBorder: crea márgenes internos (top, left, bottom, right)

        // Fila 1: Nombre del producto
        panelFormulario.add(new JLabel("Nombre del Producto:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        // Fila 2: Precio
        panelFormulario.add(new JLabel("Precio (COP):"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        // Fila 3: Categoría
        panelFormulario.add(new JLabel("Categoría:"));
        txtCategoria = new JTextField();
        panelFormulario.add(txtCategoria);

        // Fila 4-5: Descripción (ocupa 2 filas)
        panelFormulario.add(new JLabel("Descripción:"));
        txtAreaDescripcion = new JTextArea(3, 20);
        txtAreaDescripcion.setLineWrap(true); // Ajustar líneas automáticamente
        txtAreaDescripcion.setWrapStyleWord(true); // Cortar por palabras, no por caracteres
        JScrollPane scrollDescripcion = new JScrollPane(txtAreaDescripcion);
        // JScrollPane: agrega barras de desplazamiento al JTextArea
        panelFormulario.add(scrollDescripcion);

        add(panelFormulario, BorderLayout.CENTER);

        // ========== PANEL DE BOTONES (SOUTH) ==========
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnGuardar = new JButton("Guardar Producto");
        btnGuardar.setBackground(new Color(100, 200, 100)); // Verde claro
        btnGuardar.addActionListener(e -> guardarProducto());
        panelBotones.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panelBotones.add(btnLimpiar);

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(255, 150, 150)); // Rojo claro
        btnSalir.addActionListener(e -> System.exit(0));
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);

        // Instrucciones en la parte superior
        JLabel lblInstrucciones = new JLabel(
            "Complete los campos y presione Guardar (se validarán los datos)",
            SwingConstants.CENTER
        );
        lblInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblInstrucciones, BorderLayout.NORTH);

        setVisible(true);
    }

    /**
     * Método que maneja el guardado del producto con validación completa.
     *
     * VALIDACIONES IMPLEMENTADAS:
     * 1. Campos vacíos (nombre, precio, categoría)
     * 2. Precio debe ser numérico
     * 3. Precio debe ser positivo (> 0)
     */
    private void guardarProducto() {
        // PASO 1: Obtener valores de los campos
        String nombre = txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtAreaDescripcion.getText().trim();

        // PASO 2: Validar campos vacíos
        if (nombre.isEmpty()) {
            mostrarError("El nombre del producto es obligatorio");
            txtNombre.requestFocus(); // Poner cursor en el campo con error
            return;
        }

        if (precioTexto.isEmpty()) {
            mostrarError("El precio es obligatorio");
            txtPrecio.requestFocus();
            return;
        }

        if (categoria.isEmpty()) {
            mostrarError("La categoría es obligatoria");
            txtCategoria.requestFocus();
            return;
        }

        // PASO 3: Validar que el precio sea numérico
        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido\nEjemplo: 25000 o 25000.50");
            txtPrecio.requestFocus();
            txtPrecio.selectAll(); // Seleccionar todo el texto para facilitar corrección
            return;
        }

        // PASO 4: Validar que el precio sea positivo
        if (precio <= 0) {
            mostrarError("El precio debe ser mayor a cero");
            txtPrecio.requestFocus();
            txtPrecio.selectAll();
            return;
        }

        // PASO 5: Si todas las validaciones pasaron, mostrar resumen
        String mensaje = String.format(
            "Producto guardado exitosamente:\n\n" +
            "Nombre: %s\n" +
            "Precio: $%,.2f COP\n" +
            "Categoría: %s\n" +
            "Descripción: %s",
            nombre,
            precio,
            categoria,
            descripcion.isEmpty() ? "(Sin descripción)" : descripcion
        );

        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE
        );

        // En una aplicación real, aquí llamarías a new ProductServices().insertProduct()
        System.out.println("=".repeat(50));
        System.out.println("PRODUCTO GUARDADO:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: $" + String.format("%,.2f", precio));
        System.out.println("Categoría: " + categoria);
        System.out.println("Descripción: " + descripcion);
        System.out.println("=".repeat(50));

        // Limpiar formulario después de guardar
        limpiarFormulario();
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCategoria.setText("");
        txtAreaDescripcion.setText("");
        txtNombre.requestFocus(); // Poner cursor en el primer campo
    }

    /**
     * Muestra un mensaje de error con formato estándar.
     *
     * @param mensaje Mensaje de error a mostrar
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Error de Validación",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Método main para ejecutar la aplicación.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FormularioProductoSimple();
        });

        System.out.println("=".repeat(50));
        System.out.println("Checkpoint 9.3: Formularios con Validación");
        System.out.println("=".repeat(50));
        System.out.println("Prueba estas validaciones:");
        System.out.println("1. Deja campos vacíos y presiona Guardar");
        System.out.println("2. Escribe texto en el campo Precio");
        System.out.println("3. Escribe un precio negativo");
        System.out.println("4. Completa correctamente y verifica el mensaje");
        System.out.println("=".repeat(50));
    }
}
