package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.services.ProductServices;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.6: Integración con ProductServices - CRUD Completo con BD
 *
 * Este es el PRIMER checkpoint que conecta la GUI con la BASE DE DATOS REAL.
 *
 * Demuestra:
 * - Integración de Swing con Services (capa de lógica de negocio)
 * - CRUD completo: Listar, Agregar, Eliminar productos REALES
 * - Manejo de DatabaseException con JOptionPane
 * - Refrescar tabla automáticamente después de operaciones
 * - Validación de campos antes de insertar
 *
 * ARQUITECTURA:
 * =============
 * GUI (esta clase) → ProductServices → DatabaseConnection → MySQL
 *
 * SEPARACIÓN DE RESPONSABILIDADES:
 * - ProductManagerGUI: Solo interfaz visual (botones, tablas, formularios)
 * - ProductServices: Lógica de negocio y acceso a BD
 * - Product: Modelo de datos
 *
 * Analogía: Esta clase es el MESERO (toma pedidos y muestra resultados),
 * ProductServices es el CHEF (hace el trabajo real de cocinar/BD).
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class ProductManagerGUI extends JFrame {

    // Componentes de la tabla
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JComboBox<String> cmbUnidad;

    /**
     * Constructor que crea la ventana de gestión de productos.
     */
    public ProductManagerGUI() {
        setTitle("Gestor de Productos - Forestech (Conectado a BD)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR: FORMULARIO ==========
        crearPanelFormulario();

        // ========== PANEL CENTRAL: TABLA ==========
        crearTabla();

        // ========== PANEL INFERIOR: BOTONES DE ACCIÓN ==========
        crearPanelBotones();

        // Cargar productos desde la BD al iniciar
        cargarProductosDesdeDB();

        setVisible(true);
    }

    /**
     * Crea el panel del formulario para agregar productos.
     */
    private void crearPanelFormulario() {
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Agregar Nuevo Producto"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(20);
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Precio (COP):"));
        txtPrecio = new JTextField(12);
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Unidad:"));
        cmbUnidad = new JComboBox<>(new String[]{"Litros", "Galones", "Kilogramos", "Unidades"});
        panelFormulario.add(cmbUnidad);

        JButton btnAgregar = new JButton("Agregar a BD");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarProducto());
        panelFormulario.add(btnAgregar);

        add(panelFormulario, BorderLayout.NORTH);
    }

    /**
     * Crea la tabla para mostrar productos.
     */
    private void crearTabla() {
        String[] columnas = {"ID", "Nombre", "Precio (COP)", "Unidad de Medida"};

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120); // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300); // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Precio
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150); // Unidad

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Productos desde MySQL"));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea el panel de botones de acción.
     */
    private void crearPanelBotones() {
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnRefrescar = new JButton("Refrescar Tabla");
        btnRefrescar.addActionListener(e -> cargarProductosDesdeDB());
        panelBotones.add(btnRefrescar);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Carga todos los productos desde la base de datos y los muestra en la tabla.
     *
     * INTEGRACIÓN CON SERVICES:
     * - Llama a new ProductServices().getAllProducts()
     * - Maneja DatabaseException con JOptionPane
     * - Actualiza la tabla con los datos recuperados
     */
    private void cargarProductosDesdeDB() {
        try {
            // LLAMADA A LA CAPA DE SERVICES
            List<Product> productos = new ProductServices().getAllProducts();

            // Limpiar tabla antes de cargar
            modeloTabla.setRowCount(0);

            // Agregar cada producto a la tabla
            for (Product p : productos) {
                Object[] fila = {
                    p.getId(),
                    p.getName(),
                    String.format("$%,.2f", p.getPriceXUnd()),
                    p.getUnidadDeMedida()
                };
                modeloTabla.addRow(fila);
            }

            System.out.println("✅ Productos cargados desde BD: " + productos.size());

        } catch (DatabaseException e) {
            // MANEJO DE ERRORES CON GUI
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos desde la base de datos:\n" + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);

            System.err.println("❌ Error al cargar productos: " + e.getMessage());
        }
    }

    /**
     * Agrega un nuevo producto a la base de datos.
     *
     * PASOS:
     * 1. Validar campos del formulario
     * 2. Crear objeto Product
     * 3. Llamar a new ProductServices().insertProduct()
     * 4. Manejar errores (DatabaseException)
     * 5. Refrescar tabla si fue exitoso
     */
    private void agregarProducto() {
        // PASO 1: VALIDACIONES
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

        // PASO 2: CREAR OBJETO PRODUCT
        // El constructor de Product genera automáticamente el ID
        Product nuevoProducto = new Product(nombre, unidad, precio);

        // PASO 3: INSERTAR EN BD MEDIANTE SERVICES
        try {
            new ProductServices().insertProduct(nuevoProducto);

            // ÉXITO: Mostrar mensaje y refrescar
            JOptionPane.showMessageDialog(this,
                "Producto agregado exitosamente:\n\n" +
                "ID: " + nuevoProducto.getId() + "\n" +
                "Nombre: " + nuevoProducto.getName() + "\n" +
                "Precio: $" + String.format("%,.2f", nuevoProducto.getPriceXUnd()),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            // Limpiar formulario
            txtNombre.setText("");
            txtPrecio.setText("");
            cmbUnidad.setSelectedIndex(0);
            txtNombre.requestFocus();

            // PASO 5: REFRESCAR TABLA
            cargarProductosDesdeDB();

        } catch (DatabaseException e) {
            // PASO 4: MANEJO DE ERRORES
            JOptionPane.showMessageDialog(this,
                "Error al agregar producto a la base de datos:\n\n" + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);

            System.err.println("❌ Error al insertar producto: " + e.getMessage());
        }
    }

    /**
     * Elimina el producto seleccionado de la base de datos.
     *
     * IMPORTANTE:
     * - Maneja la restricción de Foreign Key (si el producto tiene movimientos asociados)
     * - Detecta el error y muestra un mensaje amigable
     */
    private void eliminarProducto() {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un producto de la tabla para eliminar",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener ID del producto seleccionado
        String productId = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String productName = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        // Confirmar eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de eliminar el producto?\n\n" +
            "ID: " + productId + "\n" +
            "Nombre: " + productName + "\n\n" +
            "ADVERTENCIA: Si este producto tiene movimientos asociados, no se podrá eliminar.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Llamar a Services para eliminar
                new ProductServices().deleteProduct(productId);

                JOptionPane.showMessageDialog(this,
                    "Producto eliminado correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

                // Refrescar tabla
                cargarProductosDesdeDB();

            } catch (DatabaseException e) {
                // Detectar error de Foreign Key
                if (e.getMessage().contains("foreign key") ||
                    e.getMessage().contains("movimientos asociados")) {

                    JOptionPane.showMessageDialog(this,
                        "❌ NO SE PUEDE ELIMINAR EL PRODUCTO\n\n" +
                        "Este producto tiene movimientos asociados en la tabla Movement.\n\n" +
                        "Solución:\n" +
                        "1. Elimina primero todos los movimientos de este producto\n" +
                        "2. Luego intenta eliminar el producto nuevamente\n\n" +
                        "Error técnico: " + e.getMessage(),
                        "Restricción de Integridad Referencial",
                        JOptionPane.ERROR_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al eliminar producto:\n" + e.getMessage(),
                        "Error de Base de Datos",
                        JOptionPane.ERROR_MESSAGE);
                }

                System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            }
        }
    }

    /**
     * Método main para ejecutar la aplicación.
     *
     * IMPORTANTE: Aplica Look and Feel del sistema antes de crear la GUI.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Aplicar Look and Feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar Look and Feel del sistema");
        }

        SwingUtilities.invokeLater(() -> {
            new ProductManagerGUI();
        });

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.6: ProductManagerGUI - CRUD con Base de Datos");
        System.out.println("=".repeat(60));
        System.out.println("Conexión a MySQL: localhost:3306/FORESTECHOIL");
        System.out.println("Tabla: oil_products");
        System.out.println("Operaciones disponibles:");
        System.out.println("  - Listar productos desde BD (carga automática)");
        System.out.println("  - Agregar producto a BD");
        System.out.println("  - Eliminar producto (con validación de FK)");
        System.out.println("  - Refrescar tabla");
        System.out.println("=".repeat(60));
    }
}
