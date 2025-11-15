package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.models.Movement;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;
import com.forestech.services.MovementServices;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.9: ForestechMainGUI - Aplicación Principal con JTabbedPane
 *
 * Esta es la APLICACIÓN PRINCIPAL de Forestech con interfaz gráfica completa.
 *
 * Demuestra:
 * - JTabbedPane (pestañas para múltiples vistas)
 * - Integración completa con 3 Services (Productos, Vehículos, Movimientos)
 * - CRUD completo en 3 pestañas diferentes
 * - JMenuBar con atajos de teclado (Checkpoint 9.7 integrado)
 * - Arquitectura modular: cada pestaña es un JPanel independiente
 *
 * ARQUITECTURA:
 * =============
 * ForestechMainGUI (ventana principal)
 *   ├── JMenuBar (menús: Archivo, Ver, Ayuda)
 *   └── JTabbedPane (pestañas)
 *       ├── Tab 1: Panel de Productos (CRUD Product)
 *       ├── Tab 2: Panel de Vehículos (CRUD Vehicle)
 *       └── Tab 3: Panel de Movimientos (CRUD Movement)
 *
 * CHECKPOINT 9.7 INTEGRADO:
 * - JMenuBar con menús Archivo, Ver, Ayuda
 * - Mnemonics: Alt+A (Archivo), Alt+V (Ver), Alt+Y (Ayuda)
 * - Accelerators: Ctrl+P (Productos), Ctrl+V (Vehículos), Ctrl+M (Movimientos)
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class ForestechMainGUI extends JFrame {

    private JTabbedPane tabbedPane;

    // Tablas para cada pestaña
    private JTable tablaProductos;
    private JTable tablaVehiculos;
    private JTable tablaMovimientos;

    // Modelos de tabla
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloVehiculos;
    private DefaultTableModel modeloMovimientos;

    /**
     * Constructor que crea la aplicación principal.
     */
    public ForestechMainGUI() {
        setTitle("Forestech Oil Management System - GUI Edition");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ========== CREAR MENUBAR (Checkpoint 9.7) ==========
        crearMenuBar();

        // ========== CREAR TABBEDPANE ==========
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Agregar pestañas
        tabbedPane.addTab("📦 Productos", crearPanelProductos());
        tabbedPane.addTab("🚛 Vehículos", crearPanelVehiculos());
        tabbedPane.addTab("📊 Movimientos", crearPanelMovimientos());

        // Detectar cambio de pestaña
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String titulo = tabbedPane.getTitleAt(index);
            System.out.println("Pestaña activa: " + titulo);

            // Refrescar datos al cambiar de pestaña
            switch (index) {
                case 0 -> cargarProductos();
                case 1 -> cargarVehiculos();
                case 2 -> cargarMovimientos();
            }
        });

        add(tabbedPane);

        // Cargar datos iniciales
        cargarProductos();

        setVisible(true);
    }

    /**
     * Crea el JMenuBar con menús y atajos (Checkpoint 9.7).
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ========== MENÚ ARCHIVO ==========
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A'); // Alt+A para abrir

        JMenuItem itemRefrescar = new JMenuItem("Refrescar Todo");
        itemRefrescar.setAccelerator(KeyStroke.getKeyStroke("control R"));
        itemRefrescar.addActionListener(e -> refrescarTodo());
        menuArchivo.add(itemRefrescar);

        menuArchivo.addSeparator();

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);

        menuBar.add(menuArchivo);

        // ========== MENÚ VER ==========
        JMenu menuVer = new JMenu("Ver");
        menuVer.setMnemonic('V'); // Alt+V

        JMenuItem itemProductos = new JMenuItem("Ir a Productos");
        itemProductos.setAccelerator(KeyStroke.getKeyStroke("control P"));
        itemProductos.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        menuVer.add(itemProductos);

        JMenuItem itemVehiculos = new JMenuItem("Ir a Vehículos");
        itemVehiculos.setAccelerator(KeyStroke.getKeyStroke("control shift V"));
        itemVehiculos.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        menuVer.add(itemVehiculos);

        JMenuItem itemMovimientos = new JMenuItem("Ir a Movimientos");
        itemMovimientos.setAccelerator(KeyStroke.getKeyStroke("control M"));
        itemMovimientos.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        menuVer.add(itemMovimientos);

        menuBar.add(menuVer);

        // ========== MENÚ AYUDA ==========
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('Y'); // Alt+Y

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de Forestech");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(itemAcercaDe);

        JMenuItem itemAtajos = new JMenuItem("Atajos de Teclado");
        itemAtajos.addActionListener(e -> mostrarAtajos());
        menuAyuda.add(itemAtajos);

        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    /**
     * Crea el panel de productos con tabla y botones.
     */
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla
        String[] columnas = {"ID", "Nombre", "Precio (COP)", "Unidad"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(25);
        configurarEstiloTabla(tablaProductos);

        JScrollPane scroll = new JScrollPane(tablaProductos);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarProducto());
        panelBotones.add(btnAgregar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarProductos());
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea el panel de vehículos.
     */
    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla
        String[] columnas = {"ID", "Nombre", "Categoría", "Capacidad (L)", "Combustible"};
        modeloVehiculos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloVehiculos);
        tablaVehiculos.setRowHeight(25);
        configurarEstiloTabla(tablaVehiculos);

        JScrollPane scroll = new JScrollPane(tablaVehiculos);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar Vehículo");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Funcionalidad completa en Checkpoint 9.10", "Info", JOptionPane.INFORMATION_MESSAGE));
        panelBotones.add(btnAgregar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarVehiculos());
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea el panel de movimientos.
     */
    private JPanel crearPanelMovimientos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla
        String[] columnas = {"ID", "Tipo", "Producto", "Vehículo", "Cantidad (L)", "Fecha"};
        modeloMovimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setRowHeight(25);
        configurarEstiloTabla(tablaMovimientos);

        JScrollPane scroll = new JScrollPane(tablaMovimientos);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Registrar Movimiento");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Funcionalidad completa en Checkpoint 9.11", "Info", JOptionPane.INFORMATION_MESSAGE));
        panelBotones.add(btnAgregar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarMovimientos());
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Configura el estilo visual de una tabla.
     */
    private void configurarEstiloTabla(JTable tabla) {
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Carga productos desde la BD.
     */
    private void cargarProductos() {
        try {
            List<Product> productos = ProductServices.getAllProducts();
            modeloProductos.setRowCount(0);

            for (Product p : productos) {
                modeloProductos.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    String.format("$%,.2f", p.getPriceXUnd()),
                    p.getUnidadDeMedida()
                });
            }

            System.out.println("✅ Productos cargados: " + productos.size());

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga vehículos desde la BD.
     */
    private void cargarVehiculos() {
        try {
            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();
            modeloVehiculos.setRowCount(0);

            for (Vehicle v : vehiculos) {
                modeloVehiculos.addRow(new Object[]{
                    v.getId(),
                    v.getName(),
                    v.getCategory(),
                    v.getCapacity(),
                    v.getFuelProductId() != null ? v.getFuelProductId() : "N/A"
                });
            }

            System.out.println("✅ Vehículos cargados: " + vehiculos.size());

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar vehículos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga movimientos desde la BD.
     */
    private void cargarMovimientos() {
        try {
            List<Movement> movimientos = MovementServices.getAllMovements();
            modeloMovimientos.setRowCount(0);

            for (Movement m : movimientos) {
                modeloMovimientos.addRow(new Object[]{
                    m.getId(),
                    m.getMovementType(),
                    m.getProductId(),
                    m.getVehicleId() != null ? m.getVehicleId() : "N/A",
                    m.getQuantity(),
                    m.getMovementDate()
                });
            }

            System.out.println("✅ Movimientos cargados: " + movimientos.size());

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar movimientos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Agrega un producto (versión simplificada con inputs).
     */
    private void agregarProducto() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del producto:");
        if (nombre == null || nombre.trim().isEmpty()) return;

        String precioStr = JOptionPane.showInputDialog(this, "Precio:");
        if (precioStr == null || precioStr.trim().isEmpty()) return;

        try {
            double precio = Double.parseDouble(precioStr);
            Product producto = new Product(nombre, "Litros", precio);
            ProductServices.insertProduct(producto);

            JOptionPane.showMessageDialog(this, "Producto agregado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

            cargarProductos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un producto.
     */
    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloProductos.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar producto " + id + "?", "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                ProductServices.deleteProduct(id);
                JOptionPane.showMessageDialog(this, "Producto eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();

            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar (puede tener movimientos asociados):\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refresca todas las tablas.
     */
    private void refrescarTodo() {
        cargarProductos();
        cargarVehiculos();
        cargarMovimientos();
        JOptionPane.showMessageDialog(this, "Todas las tablas han sido actualizadas",
            "Refrescado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra información sobre Forestech.
     */
    private void mostrarAcercaDe() {
        String mensaje = "Forestech Oil Management System\n" +
                        "Versión GUI 1.0\n\n" +
                        "Sistema de gestión de combustibles\n" +
                        "Desarrollado con Java Swing + MySQL\n\n" +
                        "© 2025 Forestech Learning Project";

        JOptionPane.showMessageDialog(this, mensaje, "Acerca de Forestech",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra atajos de teclado disponibles.
     */
    private void mostrarAtajos() {
        String mensaje = "ATAJOS DE TECLADO DISPONIBLES:\n\n" +
                        "Ctrl+P → Ir a Productos\n" +
                        "Ctrl+Shift+V → Ir a Vehículos\n" +
                        "Ctrl+M → Ir a Movimientos\n" +
                        "Ctrl+R → Refrescar todo\n" +
                        "Ctrl+Q → Salir\n\n" +
                        "Alt+A → Menú Archivo\n" +
                        "Alt+V → Menú Ver\n" +
                        "Alt+Y → Menú Ayuda";

        JOptionPane.showMessageDialog(this, mensaje, "Atajos de Teclado",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Método main.
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
            new ForestechMainGUI();
        });

        System.out.println("=".repeat(60));
        System.out.println("FORESTECH OIL MANAGEMENT SYSTEM - GUI EDITION");
        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.9: Aplicación Principal con JTabbedPane");
        System.out.println("Checkpoint 9.7: JMenuBar con atajos integrado");
        System.out.println("\nConexión: MySQL localhost:3306/FORESTECHOIL");
        System.out.println("Pestañas activas: Productos, Vehículos, Movimientos");
        System.out.println("Atajos: Ctrl+P/V/M/R/Q | Alt+A/V/Y");
        System.out.println("=".repeat(60));
    }
}
