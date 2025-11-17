package com.forestech.ui;

import com.forestech.enums.MeasurementUnit;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.services.ProductServices;

import javax.swing.*;
import java.awt.*;

/**
 * Checkpoint 9.8: JDialog - Ventanas Modales para Formularios
 *
 * Demuestra:
 * - JDialog (ventana emergente/modal)
 * - Diferencia entre modal y non-modal
 * - Comunicación entre diálogo y ventana padre
 * - Patrón de diálogo reutilizable (AGREGAR y EDITAR)
 * - Validación completa antes de guardar
 *
 * CONCEPTOS CLAVE:
 * ================
 *
 * 1. MODAL vs NON-MODAL:
 *    - Modal: Bloquea la ventana padre (usuario DEBE cerrar el diálogo primero)
 *    - Non-Modal: Permite interactuar con ventana padre
 *
 * 2. PATRÓN AGREGAR/EDITAR:
 *    - Si productoExistente == null → AGREGAR (INSERT)
 *    - Si productoExistente != null → EDITAR (UPDATE)
 *
 * 3. COMUNICACIÓN CON PADRE:
 *    - boolean guardadoExitoso: indica si se guardó correctamente
 *    - Ventana padre verifica este flag al cerrar el diálogo
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class ProductDialogForm extends JDialog {

    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> cmbUnidad;

    // Estado del diálogo
    private Product productoExistente; // null = AGREGAR, object = EDITAR
    private boolean guardadoExitoso = false;

    /**
     * Constructor para AGREGAR un nuevo producto.
     *
     * @param parent Ventana padre
     * @param modal  Si true, bloquea la ventana padre
     */
    public ProductDialogForm(JFrame parent, boolean modal) {
        this(parent, modal, null); // Llama al constructor completo con null
    }

    /**
     * Constructor completo para AGREGAR o EDITAR.
     *
     * @param parent            Ventana padre
     * @param modal             Si true, bloquea la ventana padre
     * @param productoExistente Si es null → AGREGAR, si tiene valor → EDITAR
     */
    public ProductDialogForm(JFrame parent, boolean modal, Product productoExistente) {
        super(parent, productoExistente == null ? "Agregar Producto" : "Editar Producto", modal);

        this.productoExistente = productoExistente;

        setSize(450, 300);
        setLocationRelativeTo(parent); // Centrar respecto a la ventana padre
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL DEL FORMULARIO ==========
        crearFormulario();

        // ========== PANEL DE BOTONES ==========
        crearPanelBotones();

        // Si es edición, llenar los campos con los datos existentes
        if (productoExistente != null) {
            cargarDatosExistentes();
        }

        setVisible(true); // IMPORTANTE: Bloquea aquí si es modal
    }

    /**
     * Crea el panel del formulario con los campos.
     */
    private void crearFormulario() {
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayout(3, 2, 10, 15));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Nombre
        panelFormulario.add(new JLabel("Nombre del Producto:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        // Precio
        panelFormulario.add(new JLabel("Precio (COP):"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        // Unidad de medida
        panelFormulario.add(new JLabel("Unidad de Medida:"));
        cmbUnidad = new JComboBox<>(new String[]{"Litros", "Galones", "Kilogramos", "Unidades"});
        panelFormulario.add(cmbUnidad);

        add(panelFormulario, BorderLayout.CENTER);
    }

    /**
     * Crea el panel de botones (Guardar y Cancelar).
     */
    private void crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnGuardar = new JButton(productoExistente == null ? "Agregar" : "Guardar Cambios");
        btnGuardar.setBackground(new Color(100, 200, 100));
        btnGuardar.addActionListener(e -> guardarProducto());
        panelBotones.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(255, 150, 150));
        btnCancelar.addActionListener(e -> {
            guardadoExitoso = false;
            dispose(); // Cerrar el diálogo
        });
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Carga los datos del producto existente en los campos (modo EDITAR).
     */
    private void cargarDatosExistentes() {
        txtNombre.setText(productoExistente.getName());
        txtPrecio.setText(String.valueOf(productoExistente.getUnitPrice()));
        cmbUnidad.setSelectedItem(productoExistente.getMeasurementUnitCode());
    }

    /**
     * Valida y guarda el producto (INSERT o UPDATE según el modo).
     */
    private void guardarProducto() {
        // PASO 1: VALIDAR CAMPOS
        String nombre = txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre del producto es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        if (precioTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El precio es obligatorio",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un número válido\nEjemplo: 12500 o 12500.50",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtPrecio.selectAll();
            txtPrecio.requestFocus();
            return;
        }

        if (precio <= 0) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser mayor a cero",
                "Error de Validación",
                JOptionPane.ERROR_MESSAGE);
            txtPrecio.selectAll();
            txtPrecio.requestFocus();
            return;
        }

        String unidad = (String) cmbUnidad.getSelectedItem();

        // PASO 2: GUARDAR EN LA BASE DE DATOS
        try {
            if (productoExistente == null) {
                // MODO AGREGAR: Crear nuevo producto
                Product nuevoProducto = new Product(nombre, MeasurementUnit.fromCode(unidad), precio);
                new ProductServices().insertProduct(nuevoProducto);

                JOptionPane.showMessageDialog(this,
                    "Producto agregado exitosamente:\n\n" +
                    "ID: " + nuevoProducto.getId() + "\n" +
                    "Nombre: " + nuevoProducto.getName() + "\n" +
                    "Precio: $" + String.format("%,.2f", nuevoProducto.getUnitPrice()),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

                System.out.println("✅ Producto agregado: " + nuevoProducto.getId());

            } else {
                // MODO EDITAR: Actualizar producto existente
                productoExistente.setName(nombre);
                productoExistente.setUnitPrice(precio);
                productoExistente.setMeasurementUnitFromCode(unidad);

                new ProductServices().updateProduct(productoExistente);

                JOptionPane.showMessageDialog(this,
                    "Producto actualizado exitosamente:\n\n" +
                    "ID: " + productoExistente.getId() + "\n" +
                    "Nombre: " + productoExistente.getName() + "\n" +
                    "Precio: $" + String.format("%,.2f", productoExistente.getUnitPrice()),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

                System.out.println("✅ Producto actualizado: " + productoExistente.getId());
            }

            // Marcar como exitoso y cerrar diálogo
            guardadoExitoso = true;
            dispose();

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar en la base de datos:\n\n" + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);

            System.err.println("❌ Error al guardar producto: " + e.getMessage());
        }
    }

    /**
     * Indica si el guardado fue exitoso.
     *
     * @return true si se guardó correctamente, false si se canceló o hubo error
     */
    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    /**
     * Método main para probar el diálogo de forma independiente.
     *
     * @param args Argumentos
     */
    public static void main(String[] args) {
        // Aplicar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar Look and Feel del sistema");
        }

        SwingUtilities.invokeLater(() -> {
            // Crear ventana padre ficticia
            JFrame framePadre = new JFrame("Ventana Padre - Demo");
            framePadre.setSize(600, 400);
            framePadre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            framePadre.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());
            JLabel lblTitulo = new JLabel("Haz clic en un botón para abrir el diálogo", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(lblTitulo, BorderLayout.NORTH);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));

            // Botón para AGREGAR
            JButton btnAgregar = new JButton("Abrir Diálogo AGREGAR (Modal)");
            btnAgregar.setBackground(new Color(100, 200, 100));
            btnAgregar.addActionListener(e -> {
                ProductDialogForm dialogo = new ProductDialogForm(framePadre, true);

                // Al llegar aquí, el diálogo ya se cerró (porque es modal)
                if (dialogo.isGuardadoExitoso()) {
                    lblTitulo.setText("✅ Producto agregado correctamente");
                } else {
                    lblTitulo.setText("❌ Operación cancelada");
                }
            });
            panelBotones.add(btnAgregar);

            // Botón para EDITAR (simulado)
            JButton btnEditar = new JButton("Abrir Diálogo EDITAR (Modal)");
            btnEditar.setBackground(new Color(150, 150, 255));
            btnEditar.addActionListener(e -> {
                // Simular un producto existente
                Product productoSimulado = new Product("Diesel Premium", MeasurementUnit.GALON, 12500.0);

                ProductDialogForm dialogo = new ProductDialogForm(framePadre, true, productoSimulado);

                if (dialogo.isGuardadoExitoso()) {
                    lblTitulo.setText("✅ Producto editado correctamente");
                } else {
                    lblTitulo.setText("❌ Operación cancelada");
                }
            });
            panelBotones.add(btnEditar);

            panel.add(panelBotones, BorderLayout.CENTER);
            framePadre.add(panel);
            framePadre.setVisible(true);
        });

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.8: ProductDialogForm - Diálogos Modales");
        System.out.println("=".repeat(60));
        System.out.println("\nCONCEPTOS DEMOSTRADOS:");
        System.out.println("1. JDialog modal → Bloquea ventana padre");
        System.out.println("2. Patrón AGREGAR/EDITAR → Un solo diálogo para ambos");
        System.out.println("3. Comunicación con padre → isGuardadoExitoso()");
        System.out.println("4. Validación completa → Antes de INSERT/UPDATE");
        System.out.println("5. Integración con ProductServices");
        System.out.println("\nPrueba ambos botones y observa el comportamiento!");
        System.out.println("=".repeat(60));
    }
}
