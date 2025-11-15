package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.*;
import com.forestech.services.*;

import javax.swing.*;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Checkpoint 9.13: ForestechProfessionalApp - Aplicación Profesional Completa
 *
 * APLICACIÓN DEFINITIVA que integra TODOS los módulos de Forestech con arquitectura profesional.
 *
 * CONCEPTOS DEMOSTRADOS:
 * ======================
 * 1. JSplitPane - Panel divisible (navegación lateral | contenido)
 * 2. CardLayout - Alternancia entre vistas sin crear ventanas nuevas
 * 3. Dashboard - Panel de estadísticas con datos reales de BD
 * 4. CRUD completo de Facturas (tabla + formulario + transacciones)
 * 5. Arquitectura modular - Reutiliza Services existentes
 * 6. Integración total de 5 módulos en 1 sola aplicación
 *
 * ARQUITECTURA:
 * =============
 * JSplitPane (horizontal)
 *   ├── Panel Navegación (izquierda, 200px fijo)
 *   │   ├── Botón "🏠 Inicio"
 *   │   ├── Botón "📦 Productos"
 *   │   ├── Botón "🚛 Vehículos"
 *   │   ├── Botón "📊 Movimientos"
 *   │   └── Botón "🧾 Facturas"
 *   └── Contenedor CardLayout (derecha, dinámico)
 *       ├── Card "dashboard" → DashboardPanel (estadísticas)
 *       ├── Card "productos" → Panel con tabla de productos + CRUD
 *       ├── Card "vehiculos" → Panel con tabla de vehículos + CRUD
 *       ├── Card "movimientos" → Panel con tabla de movimientos + CRUD
 *       └── Card "facturas" → Panel con tabla de facturas + CRUD completo
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class ForestechProfessionalApp extends JFrame {

    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ========== CARDLAYOUT Y CONTENEDOR ==========
    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;

    // ========== TABLAS ==========
    private JTable tablaProductos;
    private JTable tablaVehiculos;
    private JTable tablaProveedores;
    private JTable tablaMovimientos;
    private JTable tablaFacturas;

    // ========== CONTROLES DE PRODUCTOS ==========
    private JTextField txtBuscarProducto;
    private JComboBox<String> cmbFiltroUnidad;
    private JLabel lblResumenProductos;

    // ========== CONTROLES DE VEHÍCULOS ==========
    private JTextField txtBuscarVehiculo;
    private JComboBox<String> cmbFiltroCategoriaVehiculo;
    private JLabel lblResumenVehiculos;

    // ========== CONTROLES DE PROVEEDORES ==========
    private JTextField txtBuscarProveedor;
    private JComboBox<String> cmbFiltroContactoProveedor;
    private JLabel lblResumenProveedores;

    // ========== CONTROLES DE MOVIMIENTOS ==========
    private JTextField txtBuscarMovimiento;
    private JComboBox<String> cmbFiltroTipoMovimiento;
    private JTextField txtFechaDesdeMovimiento;
    private JTextField txtFechaHastaMovimiento;
    private JLabel lblResumenMovimientos;

    // ========== MODELOS DE TABLA ==========
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloVehiculos;
    private DefaultTableModel modeloProveedores;
    private DefaultTableModel modeloMovimientos;
    private DefaultTableModel modeloFacturas;

    // ========== ETIQUETAS DEL DASHBOARD ==========
    private JLabel lblTotalProductos;
    private JLabel lblTotalVehiculos;
    private JLabel lblMovimientosHoy;
    private JLabel lblTotalFacturas;

    // ========== BOTONES DE NAVEGACIÓN ==========
    private JButton btnInicio;
    private JButton btnProductos;
    private JButton btnVehiculos;
    private JButton btnProveedores;
    private JButton btnMovimientos;
    private JButton btnFacturas;
    private JButton btnLogs;

    // ========== LOG DE APLICACIÓN ==========
    private JTextArea txtLogAplicacion;
    private volatile boolean productosCargando = false;
    private boolean productosCargaPendiente = false;
    private String origenUltimaCargaProductos = "Inicial";
    private String origenCargaPendiente = null;
    private boolean actualizandoFiltroUnidad = false;
    private volatile boolean proveedoresCargando = false;
    private boolean proveedoresCargaPendiente = false;
    private String origenUltimaCargaProveedores = "Inicial";
    private String origenCargaProveedoresPendiente = null;
    private volatile boolean movimientosCargando = false;
    private boolean movimientosCargaPendiente = false;
    private String origenUltimaCargaMovimientos = "Inicial";
    private String origenCargaMovimientosPendiente = null;
    private List<Movement> movimientosVisibles = new ArrayList<>();
    private Map<String, String> cacheNombresProductos = new HashMap<>();
    private Map<String, String> cacheNombresVehiculos = new HashMap<>();

    /**
     * Constructor principal.
     */
    public ForestechProfessionalApp() {
        // Configuración de la ventana
        setTitle("Forestech Oil Management System - Professional Edition");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear menú bar
        crearMenuBar();

        // Crear layout principal con JSplitPane
        crearLayoutPrincipal();

        // Cargar datos iniciales del dashboard
        cargarDashboard();

        setVisible(true);
    }

    /**
     * Crea el JMenuBar con menús de navegación rápida.
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ========== MENÚ ARCHIVO ==========
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');

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
        menuVer.setMnemonic('V');

        JMenuItem itemInicio = new JMenuItem("Ir a Inicio");
        itemInicio.setAccelerator(KeyStroke.getKeyStroke("control 1"));
        itemInicio.addActionListener(e -> navegarA("dashboard"));
        menuVer.add(itemInicio);

        JMenuItem itemProductos = new JMenuItem("Ir a Productos");
        itemProductos.setAccelerator(KeyStroke.getKeyStroke("control 2"));
        itemProductos.addActionListener(e -> navegarA("productos"));
        menuVer.add(itemProductos);

        JMenuItem itemVehiculos = new JMenuItem("Ir a Vehículos");
        itemVehiculos.setAccelerator(KeyStroke.getKeyStroke("control 3"));
        itemVehiculos.addActionListener(e -> navegarA("vehiculos"));
        menuVer.add(itemVehiculos);

        JMenuItem itemProveedores = new JMenuItem("Ir a Proveedores");
        itemProveedores.setAccelerator(KeyStroke.getKeyStroke("control 6"));
        itemProveedores.addActionListener(e -> navegarA("proveedores"));
        menuVer.add(itemProveedores);

        JMenuItem itemMovimientos = new JMenuItem("Ir a Movimientos");
        itemMovimientos.setAccelerator(KeyStroke.getKeyStroke("control 4"));
        itemMovimientos.addActionListener(e -> navegarA("movimientos"));
        menuVer.add(itemMovimientos);

        JMenuItem itemFacturas = new JMenuItem("Ir a Facturas");
        itemFacturas.setAccelerator(KeyStroke.getKeyStroke("control 5"));
        itemFacturas.addActionListener(e -> navegarA("facturas"));
        menuVer.add(itemFacturas);

        menuBar.add(menuVer);

        // ========== MENÚ AYUDA ==========
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('Y');

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de Forestech");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(itemAcercaDe);

        JMenuItem itemAtajos = new JMenuItem("Atajos de Teclado");
        itemAtajos.setAccelerator(KeyStroke.getKeyStroke("F1"));
        itemAtajos.addActionListener(e -> mostrarAtajos());
        menuAyuda.add(itemAtajos);

        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    /**
     * Crea el JSplitPane con panel de navegación y contenedor de vistas.
     */
    private void crearLayoutPrincipal() {
        // Panel de navegación (izquierda)
        JPanel panelNavegacion = crearPanelNavegacion();

        // Contenedor principal con CardLayout (derecha)
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);

        // Agregar todas las "tarjetas" (vistas)
        contenedorPrincipal.add(crearDashboardPanel(), "dashboard");
        contenedorPrincipal.add(crearPanelProductos(), "productos");
        contenedorPrincipal.add(crearPanelVehiculos(), "vehiculos");
        contenedorPrincipal.add(crearPanelProveedores(), "proveedores");
        contenedorPrincipal.add(crearPanelMovimientos(), "movimientos");
        contenedorPrincipal.add(crearPanelFacturas(), "facturas");
        contenedorPrincipal.add(crearPanelLogs(), "logs");

        // Crear JSplitPane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,  // División horizontal (izq | der)
            panelNavegacion,               // Componente izquierdo
            contenedorPrincipal            // Componente derecho
        );

        // Configuración del JSplitPane
        splitPane.setDividerLocation(200);      // Ancho del panel de navegación
        splitPane.setOneTouchExpandable(true);  // Botones de colapso rápido
        splitPane.setDividerSize(8);            // Grosor del divisor
        splitPane.setResizeWeight(0.0);         // Panel izq no crece al redimensionar

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Crea el panel de navegación lateral izquierdo.
     */
    private JPanel crearPanelNavegacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 52, 54));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Título
        JLabel lblTitulo = new JLabel("FORESTECH", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Botones de navegación
        btnInicio = crearBotonNavegacion("🏠 Inicio", "dashboard");
        btnProductos = crearBotonNavegacion("📦 Productos", "productos");
        btnVehiculos = crearBotonNavegacion("🚛 Vehículos", "vehiculos");
        btnProveedores = crearBotonNavegacion("🤝 Proveedores", "proveedores");
        btnMovimientos = crearBotonNavegacion("📊 Movimientos", "movimientos");
        btnFacturas = crearBotonNavegacion("🧾 Facturas", "facturas");
        btnLogs = crearBotonNavegacion("📝 Logs", "logs");

        panel.add(btnInicio);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnProductos);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnVehiculos);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnProveedores);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnMovimientos);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnFacturas);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnLogs);

        // Marcar "Inicio" como activo por defecto
        marcarBotonActivo(btnInicio);

        return panel;
    }

    /**
     * Crea un botón de navegación estilizado.
     */
    private JButton crearBotonNavegacion(String texto, String vista) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(45, 52, 54));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.addActionListener(e -> {
            navegarA(vista);
            marcarBotonActivo(btn);
        });

        return btn;
    }

    /**
     * Navega a una vista específica usando CardLayout.
     */
    private void navegarA(String vista) {
        cardLayout.show(contenedorPrincipal, vista);

        // Refrescar datos según la vista
        switch (vista) {
            case "dashboard" -> cargarDashboard();
            case "productos" -> solicitarCargaProductos("Navegación");
            case "vehiculos" -> cargarVehiculos();
            case "proveedores" -> solicitarCargaProveedores("Navegación");
            case "movimientos" -> solicitarCargaMovimientos("Navegación");
            case "facturas" -> cargarFacturas();
        }
    }

    /**
     * Marca un botón como activo visualmente.
     */
    private void marcarBotonActivo(JButton botonActivo) {
        // Resetear todos los botones
        btnInicio.setBackground(new Color(45, 52, 54));
        btnProductos.setBackground(new Color(45, 52, 54));
        btnVehiculos.setBackground(new Color(45, 52, 54));
        btnProveedores.setBackground(new Color(45, 52, 54));
        btnMovimientos.setBackground(new Color(45, 52, 54));
        btnFacturas.setBackground(new Color(45, 52, 54));
        btnLogs.setBackground(new Color(45, 52, 54));

        // Marcar el activo
        botonActivo.setBackground(new Color(52, 152, 219));
    }

    // ========================================================================
    // PANEL 1: DASHBOARD
    // ========================================================================

    /**
     * Crea el panel del Dashboard con estadísticas.
     */
    private JPanel crearDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Título
        JLabel titulo = new JLabel("📊 DASHBOARD - RESUMEN GENERAL", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titulo, BorderLayout.NORTH);

        // Panel central con tarjetas de estadísticas
        JPanel panelEstadisticas = new JPanel(new GridLayout(2, 3, 20, 20));
        panelEstadisticas.setBackground(Color.WHITE);
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Tarjetas de estadísticas
        panelEstadisticas.add(crearTarjetaEstadistica("📦 Total Productos", "0", new Color(52, 152, 219)));
        panelEstadisticas.add(crearTarjetaEstadistica("🚛 Total Vehículos", "0", new Color(46, 204, 113)));
        panelEstadisticas.add(crearTarjetaEstadistica("📊 Movimientos Hoy", "0", new Color(155, 89, 182)));
        panelEstadisticas.add(crearTarjetaEstadistica("🧾 Total Facturas", "0", new Color(230, 126, 34)));
        panelEstadisticas.add(crearTarjetaEstadistica("⚠️ Stock Bajo", "0", new Color(231, 76, 60)));
        panelEstadisticas.add(crearTarjetaEstadistica("✅ Sistema Activo", "OK", new Color(26, 188, 156)));

        panel.add(panelEstadisticas, BorderLayout.CENTER);

        // Panel de accesos rápidos
        JPanel panelAccesos = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelAccesos.setBackground(Color.WHITE);

        JButton btnNuevoProducto = new JButton("Nuevo Producto");
        btnNuevoProducto.setBackground(new Color(52, 152, 219));
        btnNuevoProducto.setForeground(Color.WHITE);
        btnNuevoProducto.setFocusPainted(false);
        btnNuevoProducto.addActionListener(e -> {
            navegarA("productos");
            agregarProducto();
        });

        JButton btnNuevoVehiculo = new JButton("Nuevo Vehículo");
        btnNuevoVehiculo.setBackground(new Color(46, 204, 113));
        btnNuevoVehiculo.setForeground(Color.WHITE);
        btnNuevoVehiculo.setFocusPainted(false);
        btnNuevoVehiculo.addActionListener(e -> {
            navegarA("vehiculos");
            JOptionPane.showMessageDialog(this, "Navega a Vehículos para agregar");
        });

        JButton btnNuevoProveedor = new JButton("Nuevo Proveedor");
        btnNuevoProveedor.setBackground(new Color(241, 196, 15));
        btnNuevoProveedor.setForeground(Color.WHITE);
        btnNuevoProveedor.setFocusPainted(false);
        btnNuevoProveedor.addActionListener(e -> {
            navegarA("proveedores");
            registrarProveedor();
        });

        JButton btnNuevoMovimiento = new JButton("Registrar Movimiento");
        btnNuevoMovimiento.setBackground(new Color(155, 89, 182));
        btnNuevoMovimiento.setForeground(Color.WHITE);
        btnNuevoMovimiento.setFocusPainted(false);
        btnNuevoMovimiento.addActionListener(e -> {
            navegarA("movimientos");
            JOptionPane.showMessageDialog(this, "Navega a Movimientos para registrar");
        });

        JButton btnNuevaFactura = new JButton("Nueva Factura");
        btnNuevaFactura.setBackground(new Color(230, 126, 34));
        btnNuevaFactura.setForeground(Color.WHITE);
        btnNuevaFactura.setFocusPainted(false);
        btnNuevaFactura.addActionListener(e -> {
            navegarA("facturas");
            mostrarFormularioNuevaFactura();
        });

        panelAccesos.add(new JLabel("ACCESOS RÁPIDOS:"));
        panelAccesos.add(btnNuevoProducto);
        panelAccesos.add(btnNuevoVehiculo);
        panelAccesos.add(btnNuevoProveedor);
        panelAccesos.add(btnNuevoMovimiento);
        panelAccesos.add(btnNuevaFactura);

        panel.add(panelAccesos, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea una tarjeta de estadística para el dashboard.
     */
    private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout(5, 5));
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 36));
        lblValor.setForeground(Color.WHITE);

        // Guardar referencia para actualizar después
        if (titulo.contains("Productos")) lblTotalProductos = lblValor;
        else if (titulo.contains("Vehículos")) lblTotalVehiculos = lblValor;
        else if (titulo.contains("Movimientos")) lblMovimientosHoy = lblValor;
        else if (titulo.contains("Facturas")) lblTotalFacturas = lblValor;

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        return tarjeta;
    }

    /**
     * Carga las estadísticas del dashboard desde la BD.
     */
    private void cargarDashboard() {
        try {
            // Total productos
            List<Product> productos = ProductServices.getAllProducts();
            lblTotalProductos.setText(String.valueOf(productos.size()));

            // Total vehículos
            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();
            lblTotalVehiculos.setText(String.valueOf(vehiculos.size()));

            // Movimientos de hoy
            List<Movement> movimientos = MovementServices.getAllMovements();
            actualizarResumenMovimientosDashboard(movimientos);

            // Total facturas
            List<Factura> facturas = FacturaServices.getAllFacturas();
            lblTotalFacturas.setText(String.valueOf(facturas.size()));

            System.out.println("✅ Dashboard actualizado");

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar estadísticas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========================================================================
    // PANEL 2: PRODUCTOS
    // ========================================================================

    /**
     * Crea el panel de gestión de productos.
     */
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Encabezado (título + filtros)
        JLabel titulo = new JLabel("📦 GESTIÓN DE PRODUCTOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelEncabezado = new JPanel(new BorderLayout(0, 5));
        panelEncabezado.add(titulo, BorderLayout.NORTH);
        panelEncabezado.add(crearPanelFiltrosProductos(), BorderLayout.CENTER);
        panel.add(panelEncabezado, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Precio (COP)", "Unidad", "Stock Actual"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(25);
        configurarEstiloTabla(tablaProductos);
        aplicarRendererProductos();
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaProductos.getSelectedRow() != -1) {
                    mostrarDetallesProducto();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProductos);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarProducto());
        panelBotones.add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarProducto());
        panelBotones.add(btnEditar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> mostrarDetallesProducto());
        panelBotones.add(btnDetalles);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> solicitarCargaProductos("Botón Refrescar Productos"));
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Construye el panel superior con búsqueda y filtros para productos.
     */
    private JPanel crearPanelFiltrosProductos() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Campo de búsqueda
        txtBuscarProducto = new JTextField(18);
        txtBuscarProducto.setToolTipText("Escribe parte del nombre y presiona Enter para buscar");
        txtBuscarProducto.addActionListener(e -> solicitarCargaProductos("Enter en búsqueda"));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setToolTipText("Aplica la búsqueda difusa por nombre");
        btnBuscar.addActionListener(e -> solicitarCargaProductos("Botón Buscar"));

        // Combo de unidades
        cmbFiltroUnidad = new JComboBox<>();
        cmbFiltroUnidad.addItem("Todas");
        cmbFiltroUnidad.setPreferredSize(new Dimension(160, 28));
        cmbFiltroUnidad.setToolTipText("Filtra por unidad de medida registrada");
        cmbFiltroUnidad.addActionListener(e -> {
            if (actualizandoFiltroUnidad) {
                registrarLog("Productos: evento de unidad ignorado (actualización interna)");
                return;
            }
            solicitarCargaProductos("Filtro Unidad");
        });

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarProducto.setText("");
            cmbFiltroUnidad.setSelectedIndex(0);
            solicitarCargaProductos("Limpiar filtros");
        });

        panelControles.add(new JLabel("🔍 Nombre:"));
        panelControles.add(txtBuscarProducto);
        panelControles.add(btnBuscar);
        panelControles.add(new JLabel("⚖️ Unidad:"));
        panelControles.add(cmbFiltroUnidad);
        panelControles.add(btnLimpiar);

        lblResumenProductos = new JLabel("Mostrando todos los productos");
        lblResumenProductos.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenProductos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenProductos, BorderLayout.SOUTH);

        return panelFiltros;
    }

    /**
     * Panel de filtros para vehículos.
     */
    private JPanel crearPanelFiltrosVehiculos() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        txtBuscarVehiculo = new JTextField(18);
        txtBuscarVehiculo.setToolTipText("Busca por ID o nombre y presiona Enter");
        txtBuscarVehiculo.addActionListener(e -> cargarVehiculos());

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargarVehiculos());

        cmbFiltroCategoriaVehiculo = new JComboBox<>();
        cmbFiltroCategoriaVehiculo.addItem("Todas");
        cmbFiltroCategoriaVehiculo.setPreferredSize(new Dimension(160, 28));
        cmbFiltroCategoriaVehiculo.setToolTipText("Filtra por categoría");
        cmbFiltroCategoriaVehiculo.addActionListener(e -> cargarVehiculos());

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarVehiculo.setText("");
            cmbFiltroCategoriaVehiculo.setSelectedIndex(0);
            cargarVehiculos();
        });

        panelControles.add(new JLabel("🔍 ID/Nombre:"));
        panelControles.add(txtBuscarVehiculo);
        panelControles.add(btnBuscar);
        panelControles.add(new JLabel("🏷️ Categoría:"));
        panelControles.add(cmbFiltroCategoriaVehiculo);
        panelControles.add(btnLimpiar);

        lblResumenVehiculos = new JLabel("Mostrando todos los vehículos");
        lblResumenVehiculos.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenVehiculos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenVehiculos, BorderLayout.SOUTH);

        return panelFiltros;
    }

    // ========================================================================
    // PANEL 4: PROVEEDORES (NUEVO)
    // ========================================================================

    private JPanel crearPanelProveedores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("🤝 GESTIÓN DE PROVEEDORES", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelEncabezado = new JPanel(new BorderLayout(0, 5));
        panelEncabezado.add(titulo, BorderLayout.NORTH);
        panelEncabezado.add(crearPanelFiltrosProveedores(), BorderLayout.CENTER);
        panel.add(panelEncabezado, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "NIT", "Teléfono", "Email", "Dirección"};
        modeloProveedores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProveedores = new JTable(modeloProveedores);
        tablaProveedores.setRowHeight(24);
        tablaProveedores.setAutoCreateRowSorter(true);
        configurarEstiloTabla(tablaProveedores);
        tablaProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaProveedores.getSelectedRow() != -1) {
                    mostrarDetallesProveedor();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProveedores);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(52, 152, 219));
        btnRegistrar.addActionListener(e -> registrarProveedor());
        panelBotones.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarProveedor());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarProveedor());
        panelBotones.add(btnEliminar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> mostrarDetallesProveedor());
        panelBotones.add(btnDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> solicitarCargaProveedores("Botón Refrescar Proveedores"));
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> solicitarCargaProveedores("Inicial"));

        return panel;
    }

    private JPanel crearPanelFiltrosProveedores() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        txtBuscarProveedor = new JTextField(18);
        txtBuscarProveedor.setToolTipText("ID, NIT o nombre");
        txtBuscarProveedor.addActionListener(e -> solicitarCargaProveedores("Enter búsqueda proveedores"));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> solicitarCargaProveedores("Botón Buscar Proveedores"));

        cmbFiltroContactoProveedor = new JComboBox<>(new String[]{
            "Cualquier estado",
            "Con email",
            "Sin email",
            "Con teléfono",
            "Sin teléfono"
        });
        cmbFiltroContactoProveedor.setPreferredSize(new Dimension(150, 28));
        cmbFiltroContactoProveedor.addActionListener(e -> solicitarCargaProveedores("Filtro contacto"));

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarProveedor.setText("");
            cmbFiltroContactoProveedor.setSelectedIndex(0);
            solicitarCargaProveedores("Limpiar filtros proveedores");
        });

        panelControles.add(new JLabel("🔍 ID/NIT/Nombre:"));
        panelControles.add(txtBuscarProveedor);
        panelControles.add(btnBuscar);
        panelControles.add(new JLabel("📞 Contacto:"));
        panelControles.add(cmbFiltroContactoProveedor);
        panelControles.add(btnLimpiar);

        lblResumenProveedores = new JLabel("Mostrando todos los proveedores");
        lblResumenProveedores.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenProveedores.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenProveedores, BorderLayout.SOUTH);

        return panelFiltros;
    }

    private void solicitarCargaProveedores(String origen) {
        if (origen == null || origen.isBlank()) {
            origen = "Desconocido";
        }

        if (proveedoresCargando) {
            proveedoresCargaPendiente = true;
            origenCargaProveedoresPendiente = origen;
            registrarLog("Proveedores: solicitud en espera desde " + origen +
                " (en curso: " + origenUltimaCargaProveedores + ")");
            return;
        }

        registrarLog("Proveedores: solicitud de carga desde " + origen);
        origenUltimaCargaProveedores = origen;
        cargarProveedores();
    }

    private void cargarProveedores() {
        proveedoresCargando = true;
        long inicio = System.currentTimeMillis();
        registrarLog("Proveedores: iniciando carga (origen: " + origenUltimaCargaProveedores + ")");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final String criterio = txtBuscarProveedor != null
            ? txtBuscarProveedor.getText().trim().toLowerCase()
            : "";
        final String filtroContacto = (cmbFiltroContactoProveedor != null
            && cmbFiltroContactoProveedor.getSelectedItem() != null)
            ? cmbFiltroContactoProveedor.getSelectedItem().toString()
            : "Cualquier estado";

        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Supplier> doInBackground() throws Exception {
                List<Supplier> proveedores = SupplierServices.getAllSuppliers();

                if (!criterio.isBlank()) {
                    proveedores = proveedores.stream()
                        .filter(p -> coincideProveedorConTexto(p, criterio))
                        .collect(Collectors.toList());
                }

                if (!"Cualquier estado".equalsIgnoreCase(filtroContacto)) {
                    final String filtro = filtroContacto;
                    proveedores = proveedores.stream()
                        .filter(p -> coincideProveedorConContacto(p, filtro))
                        .collect(Collectors.toList());
                }

                return proveedores;
            }

            @Override
            protected void done() {
                try {
                    List<Supplier> proveedores = get();
                    actualizarTablaProveedores(proveedores);
                    actualizarResumenProveedores(proveedores);
                    registrarLog("Proveedores: cargados " + proveedores.size() + " registros");
                } catch (ExecutionException ex) {
                    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(ForestechProfessionalApp.this,
                        "Error al cargar proveedores: " + mensaje,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    registrarLog("Proveedores: error al cargar → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    registrarLog("Proveedores: carga interrumpida");
                } finally {
                    finalizarCargaProveedores(inicio);
                }
            }
        };

        worker.execute();
    }

    private void finalizarCargaProveedores(long inicio) {
        setCursor(Cursor.getDefaultCursor());
        proveedoresCargando = false;
        registrarLog("Proveedores: hilo EDT libre");

        if (proveedoresCargaPendiente) {
            proveedoresCargaPendiente = false;
            String origenPendiente = origenCargaProveedoresPendiente;
            origenCargaProveedoresPendiente = null;
            registrarLog(String.format(
                "Proveedores: ejecutando carga pendiente (%s) tras %.0f ms",
                origenPendiente,
                (double) (System.currentTimeMillis() - inicio)
            ));
            SwingUtilities.invokeLater(() -> solicitarCargaProveedores(origenPendiente));
        }
    }

    private void actualizarTablaProveedores(List<Supplier> proveedores) {
        if (modeloProveedores == null) {
            return;
        }
        modeloProveedores.setRowCount(0);
        for (Supplier proveedor : proveedores) {
            modeloProveedores.addRow(new Object[]{
                proveedor.getId(),
                proveedor.getName(),
                proveedor.getNit(),
                valorOpcional(proveedor.getTelephone()),
                valorOpcional(proveedor.getEmail()),
                valorOpcional(proveedor.getAddress())
            });
        }
    }

    private void actualizarResumenProveedores(List<Supplier> proveedores) {
        if (lblResumenProveedores == null) {
            return;
        }

        long conEmail = proveedores.stream().filter(this::tieneEmail).count();
        long conTelefono = proveedores.stream().filter(this::tieneTelefono).count();

        lblResumenProveedores.setText(String.format(
            "Mostrando %d proveedores | Con email: %d | Con teléfono: %d",
            proveedores.size(),
            conEmail,
            conTelefono
        ));
    }

    private boolean coincideProveedorConContacto(Supplier proveedor, String filtro) {
        return switch (filtro) {
            case "Con email" -> tieneEmail(proveedor);
            case "Sin email" -> !tieneEmail(proveedor);
            case "Con teléfono" -> tieneTelefono(proveedor);
            case "Sin teléfono" -> !tieneTelefono(proveedor);
            default -> true;
        };
    }

    private boolean coincideProveedorConTexto(Supplier proveedor, String termino) {
        if (termino == null || termino.isBlank()) {
            return true;
        }
        return contiene(proveedor.getId(), termino)
            || contiene(proveedor.getName(), termino)
            || contiene(proveedor.getNit(), termino)
            || contiene(proveedor.getTelephone(), termino)
            || contiene(proveedor.getEmail(), termino)
            || contiene(proveedor.getAddress(), termino);
    }

    private boolean tieneEmail(Supplier proveedor) {
        return proveedor.getEmail() != null && !proveedor.getEmail().isBlank();
    }

    private boolean tieneTelefono(Supplier proveedor) {
        return proveedor.getTelephone() != null && !proveedor.getTelephone().isBlank();
    }

    private void registrarProveedor() {
        Supplier nuevo = mostrarDialogoProveedor("Registrar proveedor", null);
        if (nuevo == null) {
            registrarLog("Proveedores: alta cancelada por el usuario");
            return;
        }

        try {
            SupplierServices.insertSupplier(nuevo);
            JOptionPane.showMessageDialog(this,
                "Proveedor creado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            registrarLog("Proveedores: creado " + nuevo.getId());
            solicitarCargaProveedores("Post alta proveedor");
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al crear proveedor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Proveedores: error al crear → " + e.getMessage());
        }
    }

    private void editarProveedor() {
        String proveedorId = obtenerProveedorSeleccionado();
        if (proveedorId == null) {
            return;
        }

        try {
            Supplier existente = SupplierServices.getSupplierById(proveedorId);
            if (existente == null) {
                JOptionPane.showMessageDialog(this,
                    "El proveedor ya no existe",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                solicitarCargaProveedores("Proveedor desaparecido");
                return;
            }

            Supplier actualizado = mostrarDialogoProveedor("Editar proveedor " + proveedorId, existente);
            if (actualizado == null) {
                registrarLog("Proveedores: edición cancelada " + proveedorId);
                return;
            }

            if (SupplierServices.updateSupplier(actualizado)) {
                JOptionPane.showMessageDialog(this,
                    "Proveedor actualizado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                registrarLog("Proveedores: actualizado " + proveedorId);
                solicitarCargaProveedores("Post edición proveedor");
            }

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Proveedores: error al actualizar → " + e.getMessage());
        }
    }

    private void eliminarProveedor() {
        String proveedorId = obtenerProveedorSeleccionado();
        if (proveedorId == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar proveedor " + proveedorId + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (SupplierServices.deleteSupplier(proveedorId)) {
                JOptionPane.showMessageDialog(this,
                    "Proveedor eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                registrarLog("Proveedores: eliminado " + proveedorId);
                solicitarCargaProveedores("Post eliminación proveedor");
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "No se pudo eliminar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Proveedores: error al eliminar → " + e.getMessage());
        }
    }

    private void mostrarDetallesProveedor() {
        String proveedorId = obtenerProveedorSeleccionado();
        if (proveedorId == null) {
            return;
        }

        try {
            Supplier proveedor = SupplierServices.getSupplierById(proveedorId);
            if (proveedor == null) {
                JOptionPane.showMessageDialog(this,
                    "El proveedor ya no existe",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                solicitarCargaProveedores("Proveedor inexistente");
                return;
            }

            String detalle = """
                ID: %s
                Nombre: %s
                NIT: %s
                Teléfono: %s
                Email: %s
                Dirección: %s
                """.formatted(
                proveedor.getId(),
                proveedor.getName(),
                proveedor.getNit(),
                valorOpcional(proveedor.getTelephone()),
                valorOpcional(proveedor.getEmail()),
                valorOpcional(proveedor.getAddress())
            );

            JTextArea area = new JTextArea(detalle);
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 13));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Detalle del proveedor", JOptionPane.INFORMATION_MESSAGE);

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al consultar proveedor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerProveedorSeleccionado() {
        if (tablaProveedores == null || tablaProveedores.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un proveedor primero",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int filaVista = tablaProveedores.getSelectedRow();
        int filaModelo = tablaProveedores.convertRowIndexToModel(filaVista);
        return (String) modeloProveedores.getValueAt(filaModelo, 0);
    }

    private Supplier mostrarDialogoProveedor(String titulo, Supplier existente) {
        JTextField txtNombre = new JTextField(existente != null ? existente.getName() : "");
        JTextField txtNit = new JTextField(existente != null ? existente.getNit() : "");
        JTextField txtTelefono = new JTextField(existente != null ? valorEditable(existente.getTelephone()) : "");
        JTextField txtEmail = new JTextField(existente != null ? valorEditable(existente.getEmail()) : "");
        JTextField txtDireccion = new JTextField(existente != null ? valorEditable(existente.getAddress()) : "");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("NIT:"));
        panel.add(txtNit);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Dirección:"));
        panel.add(txtDireccion);

        int opcion = JOptionPane.showConfirmDialog(this, panel,
            titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        if (nombre.isEmpty() || nit.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nombre y NIT son obligatorios",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                "Ingresa un email válido",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String telefono = nullSiVacio(txtTelefono.getText().trim());
        String direccion = nullSiVacio(txtDireccion.getText().trim());
        email = nullSiVacio(email);

        if (existente == null) {
            return new Supplier(nombre, nit, telefono, email, direccion);
        }
        return new Supplier(existente.getId(), nombre, nit, telefono, email, direccion);
    }

    private JPanel crearPanelFiltrosMovimientos() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));

        JPanel panelControles = new JPanel(new GridBagLayout());
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        txtBuscarMovimiento = new JTextField(18);
        txtBuscarMovimiento.setToolTipText("ID, producto o vehículo");
        txtBuscarMovimiento.addActionListener(e -> solicitarCargaMovimientos("Enter búsqueda movimientos"));

        cmbFiltroTipoMovimiento = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA"});
        cmbFiltroTipoMovimiento.setPreferredSize(new Dimension(140, 28));
        cmbFiltroTipoMovimiento.addActionListener(e -> solicitarCargaMovimientos("Filtro tipo movimientos"));

        txtFechaDesdeMovimiento = new JTextField(10);
        txtFechaDesdeMovimiento.setToolTipText("Desde (YYYY-MM-DD)");
        txtFechaDesdeMovimiento.addActionListener(e -> solicitarCargaMovimientos("Enter fecha desde"));

        txtFechaHastaMovimiento = new JTextField(10);
        txtFechaHastaMovimiento.setToolTipText("Hasta (YYYY-MM-DD)");
        txtFechaHastaMovimiento.addActionListener(e -> solicitarCargaMovimientos("Enter fecha hasta"));

        JButton btnAplicar = new JButton("Aplicar filtros");
        btnAplicar.addActionListener(e -> solicitarCargaMovimientos("Botón Aplicar filtros Movimientos"));

        JButton btnHoy = new JButton("Solo hoy");
        btnHoy.addActionListener(e -> {
            String hoy = LocalDate.now().toString();
            txtFechaDesdeMovimiento.setText(hoy);
            txtFechaHastaMovimiento.setText(hoy);
            solicitarCargaMovimientos("Filtro Solo Hoy");
        });

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            txtBuscarMovimiento.setText("");
            cmbFiltroTipoMovimiento.setSelectedIndex(0);
            txtFechaDesdeMovimiento.setText("");
            txtFechaHastaMovimiento.setText("");
            solicitarCargaMovimientos("Limpiar filtros Movimientos");
        });

        // Fila 0: búsqueda + tipo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelControles.add(new JLabel("🔍 ID/Producto/Vehículo:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(txtBuscarMovimiento, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelControles.add(new JLabel("⚙️ Tipo:"), gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        panelControles.add(cmbFiltroTipoMovimiento, gbc);

        // Fila 1: Fechas + botones
        gbc.gridy = 1;
        gbc.gridx = 0;
        panelControles.add(new JLabel("📅 Desde:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(txtFechaDesdeMovimiento, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelControles.add(new JLabel("Hasta:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(txtFechaHastaMovimiento, gbc);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 4;
        panelControles.add(btnAplicar, gbc);

        gbc.gridx = 5;
        panelControles.add(btnHoy, gbc);

        gbc.gridx = 6;
        panelControles.add(btnLimpiar, gbc);

        lblResumenMovimientos = new JLabel("Mostrando todos los movimientos");
        lblResumenMovimientos.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenMovimientos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenMovimientos, BorderLayout.SOUTH);

        return panelFiltros;
    }

    /**
     * Solicita recargar productos indicando quién disparó la acción.
     */
    private void solicitarCargaProductos(String origen) {
        if (origen == null || origen.isBlank()) {
            origen = "Desconocido";
        }
        if (productosCargando) {
            productosCargaPendiente = true;
            origenCargaPendiente = origen;
            registrarLog("Productos: solicitud en espera desde " + origen +
                " (en curso: " + origenUltimaCargaProductos + ")");
            return;
        }

        registrarLog("Productos: solicitud de carga desde " + origen);
        origenUltimaCargaProductos = origen;
        cargarProductos();
    }

    /**
     * Carga productos desde la BD.
     */
    private void cargarProductos() {
        productosCargando = true;
        long inicio = System.currentTimeMillis();
        registrarLog("Productos: iniciando carga de datos (origen: " + origenUltimaCargaProductos + ")");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final String criterio = txtBuscarProducto != null ? txtBuscarProducto.getText().trim() : "";
        final String unidadSeleccionada = (cmbFiltroUnidad != null && cmbFiltroUnidad.getSelectedItem() != null)
            ? cmbFiltroUnidad.getSelectedItem().toString()
            : "Todas";
        final boolean debeActualizarUnidades = criterio.isEmpty();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            private Map<String, Double> stockPorProducto = new HashMap<>();

            @Override
            protected List<Product> doInBackground() throws Exception {
                List<Product> productos = criterio.isEmpty()
                    ? ProductServices.getAllProducts()
                    : ProductServices.searchProductsByName(criterio);

                if (unidadSeleccionada != null && !"Todas".equalsIgnoreCase(unidadSeleccionada)) {
                    productos = productos.stream()
                        .filter(p -> unidadSeleccionada.equalsIgnoreCase(p.getUnidadDeMedida()))
                        .collect(Collectors.toList());
                }

                if (!productos.isEmpty()) {
                    List<String> ids = productos.stream()
                        .map(Product::getId)
                        .collect(Collectors.toList());
                    stockPorProducto = MovementServices.getStockByProductIds(ids);
                }

                return productos;
            }

            @Override
            protected void done() {
                try {
                    List<Product> productos = get();

                    modeloProductos.setRowCount(0);
                    for (Product p : productos) {
                        double stock = stockPorProducto.getOrDefault(p.getId(), 0.0);
                        modeloProductos.addRow(new Object[]{
                            p.getId(),
                            p.getName(),
                            String.format("$%,.2f", p.getPriceXUnd()),
                            p.getUnidadDeMedida(),
                            stock
                        });
                    }

                    if (debeActualizarUnidades) {
                        actualizarOpcionesUnidadDesde(productos);
                    }

                    actualizarChipResumen(productos.size(), criterio, unidadSeleccionada);
                    registrarLog(String.format(
                        "Productos: carga completada en %d ms (%d registros)",
                        System.currentTimeMillis() - inicio,
                        productos.size()
                    ));

                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    String mensaje = causa != null ? causa.getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(ForestechProfessionalApp.this,
                        "Error al cargar productos: " + mensaje,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    registrarLog("Productos: error durante carga → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    registrarLog("Productos: carga interrumpida");
                } finally {
                    finalizarCargaProductos(inicio);
                }
            }
        };

        worker.execute();
    }

    /**
     * Limpia estado de carga y atiende solicitudes pendientes.
     */
    private void finalizarCargaProductos(long inicio) {
        setCursor(Cursor.getDefaultCursor());
        productosCargando = false;
        registrarLog("Productos: hilo EDT libre");

        if (productosCargaPendiente) {
            productosCargaPendiente = false;
            String origenPendiente = origenCargaPendiente;
            origenCargaPendiente = null;
            registrarLog(String.format(
                "Productos: ejecutando carga pendiente (%s) tras %.0f ms",
                origenPendiente,
                (double) (System.currentTimeMillis() - inicio)
            ));
            SwingUtilities.invokeLater(() -> solicitarCargaProductos(origenPendiente));
        }
    }

    /**
     * Aplica un renderer que resalta productos con stock bajo.
     */
    private void aplicarRendererProductos() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    comp.setBackground(new Color(52, 152, 219));
                    comp.setForeground(Color.WHITE);
                } else {
                    comp.setForeground(Color.BLACK);
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));

                    Object stockValue = table.getValueAt(row, 4);
                    double stock = 0;
                    if (stockValue instanceof Number) {
                        stock = ((Number) stockValue).doubleValue();
                    } else if (stockValue != null) {
                        try {
                            stock = Double.parseDouble(stockValue.toString().replace(",", "."));
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    if (stock < 10) {
                        comp.setBackground(new Color(255, 235, 238));
                        comp.setForeground(new Color(192, 57, 43));
                    }
                }
                return comp;
            }
        };

        tablaProductos.setDefaultRenderer(Object.class, renderer);
    }

    /**
     * Actualiza las opciones del combo de unidad respetando la selección actual.
     */
    private void actualizarOpcionesUnidadDesde(List<Product> productos) {
        if (cmbFiltroUnidad == null) {
            return;
        }

        String seleccionActual = (String) cmbFiltroUnidad.getSelectedItem();
        Set<String> unidades = productos.stream()
            .map(Product::getUnidadDeMedida)
            .filter(unidad -> unidad != null && !unidad.isBlank())
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        actualizandoFiltroUnidad = true;
        try {
            cmbFiltroUnidad.removeAllItems();
            cmbFiltroUnidad.addItem("Todas");
            unidades.forEach(cmbFiltroUnidad::addItem);

            boolean existeSeleccion = seleccionActual != null && unidades.stream()
                .anyMatch(u -> u.equalsIgnoreCase(seleccionActual));

            if (existeSeleccion) {
                cmbFiltroUnidad.setSelectedItem(seleccionActual);
            } else {
                cmbFiltroUnidad.setSelectedIndex(0);
            }
        } finally {
            actualizandoFiltroUnidad = false;
        }
    }

    /**
     * Muestra un resumen textual del filtro aplicado en la parte superior.
     */
    private void actualizarChipResumen(int total, String criterio, String unidadSeleccionada) {
        if (lblResumenProductos == null) {
            return;
        }

        StringBuilder resumen = new StringBuilder("Mostrando ")
            .append(total)
            .append(total == 1 ? " producto" : " productos");

        if (criterio != null && !criterio.isBlank()) {
            resumen.append(" | Nombre contiene \"")
                .append(criterio)
                .append("\"");
        }

        if (unidadSeleccionada != null && !"Todas".equalsIgnoreCase(unidadSeleccionada)) {
            resumen.append(" | Unidad: ")
                .append(unidadSeleccionada);
        }

        lblResumenProductos.setText(resumen.toString());
        lblResumenProductos.setForeground(total == 0
            ? new Color(192, 57, 43)
            : new Color(80, 80, 80));
    }

    /**
     * Agrega un producto (versión simplificada).
     */
    private void agregarProducto() {
        registrarLog("Productos: abriendo diálogo de alta");
        ProductDialogForm dialog = new ProductDialogForm(this, true);
        if (dialog.isGuardadoExitoso()) {
            solicitarCargaProductos("Post Alta Producto");
            cargarDashboard();
            registrarLog("Productos: registro agregado correctamente");
        } else {
            registrarLog("Productos: alta cancelada por el usuario");
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
                solicitarCargaProductos("Post Eliminación Producto");
                cargarDashboard();
                registrarLog("Productos: eliminado " + id);

            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                registrarLog("Productos: error al eliminar " + id + " → " + e.getMessage());
            }
        }
    }

    /**
     * Abre el diálogo de edición con los datos seleccionados.
     */
    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloProductos.getValueAt(fila, 0);
        try {
            Product producto = ProductServices.getProductById(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "El producto ya no existe",
                    "Error", JOptionPane.ERROR_MESSAGE);
                solicitarCargaProductos("Resincronización tras ausencia");
                return;
            }

            ProductDialogForm dialog = new ProductDialogForm(this, true, producto);
            if (dialog.isGuardadoExitoso()) {
                solicitarCargaProductos("Post Edición Producto");
                cargarDashboard();
                registrarLog("Productos: edición exitosa " + id);
            } else {
                registrarLog("Productos: edición cancelada " + id);
            }

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Productos: error al preparar edición → " + e.getMessage());
        }
    }

    /**
     * Muestra un resumen completo del producto en un diálogo.
     */
    private void mostrarDetallesProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloProductos.getValueAt(fila, 0);
        try {
            Product producto = ProductServices.getProductById(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el producto",
                    "Error", JOptionPane.ERROR_MESSAGE);
                solicitarCargaProductos("Post Detalle Inconsistente");
                return;
            }

            double stock = MovementServices.getProductStock(id);
            String mensaje = "ID: " + producto.getId() +
                "\nNombre: " + producto.getName() +
                "\nUnidad: " + producto.getUnidadDeMedida() +
                "\nPrecio: $" + String.format("%,.2f", producto.getPriceXUnd()) +
                "\nStock actual: " + String.format("%,.2f", stock);

            JOptionPane.showMessageDialog(this, mensaje,
                "Detalle de producto", JOptionPane.INFORMATION_MESSAGE);
            registrarLog("Productos: detalles consultados para " + id);

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Productos: error al mostrar detalles → " + e.getMessage());
        }
    }

    // ========================================================================
    // PANEL 3: VEHÍCULOS
    // ========================================================================

    /**
     * Crea el panel de gestión de vehículos.
     */
    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título + filtros
        JLabel titulo = new JLabel("🚛 GESTIÓN DE VEHÍCULOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel encabezado = new JPanel(new BorderLayout(0, 5));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(crearPanelFiltrosVehiculos(), BorderLayout.CENTER);
        panel.add(encabezado, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Categoría", "Capacidad (L)", "Combustible", "Horómetro"};
        modeloVehiculos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloVehiculos);
        tablaVehiculos.setRowHeight(25);
        configurarEstiloTabla(tablaVehiculos);
        tablaVehiculos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaVehiculos.getSelectedRow() != -1) {
                    mostrarDetallesVehiculo();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaVehiculos);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarVehiculo());
        panelBotones.add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarVehiculo());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        panelBotones.add(btnEliminar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> mostrarDetallesVehiculo());
        panelBotones.add(btnDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarVehiculos());
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Carga vehículos desde la BD.
     */
    private void cargarVehiculos() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String criterio = txtBuscarVehiculo != null ? txtBuscarVehiculo.getText().trim().toLowerCase() : "";
            String categoriaSeleccionada = (cmbFiltroCategoriaVehiculo != null && cmbFiltroCategoriaVehiculo.getSelectedItem() != null)
                ? cmbFiltroCategoriaVehiculo.getSelectedItem().toString()
                : "Todas";

            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();
            List<Vehicle> vehiculosFiltrados = vehiculos;

            if (!criterio.isBlank()) {
                vehiculosFiltrados = vehiculosFiltrados.stream()
                    .filter(v -> v.getId().toLowerCase().contains(criterio) ||
                        v.getName().toLowerCase().contains(criterio))
                    .collect(Collectors.toList());
            }

            if (categoriaSeleccionada != null && !"Todas".equalsIgnoreCase(categoriaSeleccionada)) {
                vehiculosFiltrados = vehiculosFiltrados.stream()
                    .filter(v -> categoriaSeleccionada.equalsIgnoreCase(v.getCategory()))
                    .collect(Collectors.toList());
            }

            modeloVehiculos.setRowCount(0);
            for (Vehicle v : vehiculosFiltrados) {
                modeloVehiculos.addRow(new Object[]{
                    v.getId(),
                    v.getName(),
                    v.getCategory(),
                    String.format("%,.2f", v.getCapacity()),
                    v.getFuelProductId() != null ? v.getFuelProductId() : "Sin asignar",
                    v.isHaveHorometer() ? "Sí" : "No"
                });
            }

            actualizarCategoriasVehiculos(vehiculos);
            actualizarResumenVehiculos(vehiculosFiltrados.size(), criterio, categoriaSeleccionada);
            registrarLog("Vehículos: cargados " + vehiculosFiltrados.size() + " registros");

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar vehículos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Vehículos: error al cargar → " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void actualizarCategoriasVehiculos(List<Vehicle> vehiculos) {
        if (cmbFiltroCategoriaVehiculo == null) {
            return;
        }

        String seleccionActual = (String) cmbFiltroCategoriaVehiculo.getSelectedItem();
        Set<String> categorias = vehiculos.stream()
            .map(Vehicle::getCategory)
            .filter(cat -> cat != null && !cat.isBlank())
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        cmbFiltroCategoriaVehiculo.removeAllItems();
        cmbFiltroCategoriaVehiculo.addItem("Todas");
        categorias.forEach(cmbFiltroCategoriaVehiculo::addItem);

        if (seleccionActual != null && categorias.stream().anyMatch(c -> c.equalsIgnoreCase(seleccionActual))) {
            cmbFiltroCategoriaVehiculo.setSelectedItem(seleccionActual);
        } else {
            cmbFiltroCategoriaVehiculo.setSelectedIndex(0);
        }
    }

    private void actualizarResumenVehiculos(int total, String criterio, String categoria) {
        if (lblResumenVehiculos == null) {
            return;
        }

        StringBuilder sb = new StringBuilder("Mostrando ")
            .append(total)
            .append(total == 1 ? " vehículo" : " vehículos");

        if (criterio != null && !criterio.isBlank()) {
            sb.append(" | Coincidencia: \"")
                .append(criterio)
                .append("\"");
        }

        if (categoria != null && !"Todas".equalsIgnoreCase(categoria)) {
            sb.append(" | Categoría: ")
                .append(categoria);
        }

        lblResumenVehiculos.setText(sb.toString());
        lblResumenVehiculos.setForeground(total == 0 ? new Color(192, 57, 43) : new Color(80, 80, 80));
    }

    private void agregarVehiculo() {
        registrarLog("Vehículos: abriendo diálogo de alta");
        VehicleDialogForm dialog = new VehicleDialogForm(this, true);
        if (dialog.isGuardadoExitoso()) {
            cargarVehiculos();
            cargarDashboard();
            registrarLog("Vehículos: alta exitosa");
        }
    }

    private void editarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloVehiculos.getValueAt(fila, 0);
        try {
            Vehicle vehiculo = VehicleServices.getVehicleById(id);
            if (vehiculo == null) {
                JOptionPane.showMessageDialog(this, "El vehículo ya no existe",
                    "Error", JOptionPane.ERROR_MESSAGE);
                cargarVehiculos();
                return;
            }

            VehicleDialogForm dialog = new VehicleDialogForm(this, true, vehiculo);
            if (dialog.isGuardadoExitoso()) {
                cargarVehiculos();
                cargarDashboard();
                registrarLog("Vehículos: edición exitosa " + id);
            }

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Vehículos: error al editar → " + e.getMessage());
        }
    }

    private void eliminarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloVehiculos.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar vehículo " + id + "?", "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                VehicleServices.deleteVehicle(id);
                JOptionPane.showMessageDialog(this, "Vehículo eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarVehiculos();
                cargarDashboard();
                registrarLog("Vehículos: eliminado " + id);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                registrarLog("Vehículos: error al eliminar → " + e.getMessage());
            }
        }
    }

    private void mostrarDetallesVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloVehiculos.getValueAt(fila, 0);
        try {
            Vehicle vehiculo = VehicleServices.getVehicleById(id);
            if (vehiculo == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el vehículo",
                    "Error", JOptionPane.ERROR_MESSAGE);
                cargarVehiculos();
                return;
            }

            StringBuilder detalle = new StringBuilder()
                .append("ID: ").append(vehiculo.getId())
                .append("\nNombre: ").append(vehiculo.getName())
                .append("\nCategoría: ").append(vehiculo.getCategory())
                .append("\nCapacidad: ").append(String.format("%,.2f L", vehiculo.getCapacity()))
                .append("\nCombustible: ").append(
                    vehiculo.getFuelProductId() != null ? vehiculo.getFuelProductId() : "Sin asignar")
                .append("\nHorómetro: ").append(vehiculo.isHaveHorometer() ? "Sí" : "No");

            JOptionPane.showMessageDialog(this, detalle.toString(),
                "Detalle de vehículo", JOptionPane.INFORMATION_MESSAGE);
            registrarLog("Vehículos: detalles mostrados para " + id);

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            registrarLog("Vehículos: error al consultar detalles → " + e.getMessage());
        }
    }

    // ========================================================================
    // PANEL 4: MOVIMIENTOS
    // ========================================================================

    /**
     * Crea el panel de gestión de movimientos.
     */
    private JPanel crearPanelMovimientos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("📊 GESTIÓN DE MOVIMIENTOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelEncabezado = new JPanel(new BorderLayout(0, 5));
        panelEncabezado.add(titulo, BorderLayout.NORTH);
        panelEncabezado.add(crearPanelFiltrosMovimientos(), BorderLayout.CENTER);
        panel.add(panelEncabezado, BorderLayout.NORTH);

        String[] columnas = {
            "ID", "Tipo", "Producto", "Vehículo", "Cantidad (L)",
            "Unidad", "Factura", "Precio Unitario", "Subtotal", "Fecha"
        };
        modeloMovimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setRowHeight(25);
        tablaMovimientos.setAutoCreateRowSorter(true);
        configurarEstiloTabla(tablaMovimientos);
        tablaMovimientos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaMovimientos.getSelectedRow() != -1) {
                    verDetallesMovimiento();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaMovimientos);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(155, 89, 182));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.addActionListener(e -> registrarMovimiento());
        panelBotones.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarMovimiento());
        panelBotones.add(btnEditar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> verDetallesMovimiento());
        panelBotones.add(btnDetalles);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarMovimiento());
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> solicitarCargaMovimientos("Botón Refrescar Movimientos"));
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Orquesta solicitudes de carga de movimientos evitando bloqueos del EDT.
     */
    private void solicitarCargaMovimientos(String origen) {
        if (origen == null || origen.isBlank()) {
            origen = "Desconocido";
        }

        if (movimientosCargando) {
            movimientosCargaPendiente = true;
            origenCargaMovimientosPendiente = origen;
            registrarLog("Movimientos: solicitud en espera desde " + origen +
                " (actual: " + origenUltimaCargaMovimientos + ")");
            return;
        }

        registrarLog("Movimientos: solicitud de carga desde " + origen);
        origenUltimaCargaMovimientos = origen;
        cargarMovimientos();
    }

    /**
     * Carga movimientos desde la BD aplicando filtros y actualiza la tabla.
     */
    private void cargarMovimientos() {
        final String criterio = txtBuscarMovimiento != null
            ? txtBuscarMovimiento.getText().trim()
            : "";
        final String tipoSeleccionado = (cmbFiltroTipoMovimiento != null &&
            cmbFiltroTipoMovimiento.getSelectedItem() != null)
            ? cmbFiltroTipoMovimiento.getSelectedItem().toString()
            : "Todos";

        final LocalDate fechaDesde;
        final LocalDate fechaHasta;
        try {
            fechaDesde = parsearFechaFiltro(txtFechaDesdeMovimiento);
            fechaHasta = parsearFechaFiltro(txtFechaHastaMovimiento);

            if (fechaDesde != null && fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
                throw new IllegalArgumentException("La fecha \"Hasta\" no puede ser anterior a \"Desde\"");
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Filtro de fecha inválido",
                JOptionPane.WARNING_MESSAGE);
            registrarLog("Movimientos: filtros inválidos → " + ex.getMessage());
            return;
        }

        movimientosCargando = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final long inicio = System.currentTimeMillis();
        registrarLog("Movimientos: iniciando carga (origen: " + origenUltimaCargaMovimientos + ")");

        final String terminoBusqueda = criterio.toLowerCase();

        SwingWorker<MovimientosCargaResult, Void> worker = new SwingWorker<>() {
            @Override
            protected MovimientosCargaResult doInBackground() throws Exception {
                List<Movement> movimientos = MovementServices.getAllMovements();

                Map<String, String> productosNombres = ProductServices.getAllProducts().stream()
                    .collect(Collectors.toMap(
                        Product::getId,
                        Product::getName,
                        (existente, nuevo) -> existente,
                        HashMap::new
                    ));

                Map<String, String> vehiculosNombres = VehicleServices.getAllVehicles().stream()
                    .collect(Collectors.toMap(
                        Vehicle::getId,
                        Vehicle::getName,
                        (existente, nuevo) -> existente,
                        HashMap::new
                    ));

                List<Movement> filtrados = movimientos.stream()
                    .filter(m -> coincideConTipo(m, tipoSeleccionado))
                    .filter(m -> coincideConFecha(m, fechaDesde, fechaHasta))
                    .filter(m -> coincideConTexto(m, terminoBusqueda, productosNombres, vehiculosNombres))
                    .collect(Collectors.toList());

                long totalEntradas = 0;
                long totalSalidas = 0;
                double litrosEntradas = 0;
                double litrosSalidas = 0;

                for (Movement movimiento : filtrados) {
                    if ("ENTRADA".equalsIgnoreCase(movimiento.getMovementType())) {
                        totalEntradas++;
                        litrosEntradas += movimiento.getQuantity();
                    } else if ("SALIDA".equalsIgnoreCase(movimiento.getMovementType())) {
                        totalSalidas++;
                        litrosSalidas += movimiento.getQuantity();
                    }
                }

                return new MovimientosCargaResult(
                    filtrados,
                    productosNombres,
                    vehiculosNombres,
                    totalEntradas,
                    litrosEntradas,
                    totalSalidas,
                    litrosSalidas
                );
            }

            @Override
            protected void done() {
                try {
                    MovimientosCargaResult resultado = get();
                    movimientosVisibles = resultado.movimientos;
                    cacheNombresProductos = resultado.productos;
                    cacheNombresVehiculos = resultado.vehiculos;

                    modeloMovimientos.setRowCount(0);
                    for (Movement movimiento : movimientosVisibles) {
                        modeloMovimientos.addRow(new Object[]{
                            movimiento.getId(),
                            movimiento.getMovementType(),
                            construirEtiquetaProducto(movimiento.getProductId()),
                            construirEtiquetaVehiculo(movimiento.getVehicleId()),
                            String.format("%,.2f", movimiento.getQuantity()),
                            movimiento.getUnidadDeMedida(),
                            movimiento.getNumeroFactura() != null ? movimiento.getNumeroFactura() : "—",
                            formatearMoneda(movimiento.getUnitPrice()),
                            formatearMoneda(movimiento.getQuantity() * movimiento.getUnitPrice()),
                            formatearFechaMovimiento(movimiento.getMovementDate())
                        });
                    }

                    actualizarResumenMovimientos(resultado);
                    registrarLog(String.format(
                        "Movimientos: carga completada en %d ms (%d registros)",
                        System.currentTimeMillis() - inicio,
                        movimientosVisibles.size()
                    ));

                } catch (ExecutionException ex) {
                    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(ForestechProfessionalApp.this,
                        "Error al cargar movimientos: " + mensaje,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    registrarLog("Movimientos: error al cargar → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    registrarLog("Movimientos: carga interrumpida");
                } finally {
                    finalizarCargaMovimientos(inicio);
                }
            }
        };

        worker.execute();
    }

    /**
     * Libera el estado de carga y procesa solicitudes pendientes.
     */
    private void finalizarCargaMovimientos(long inicio) {
        setCursor(Cursor.getDefaultCursor());
        movimientosCargando = false;
        registrarLog("Movimientos: hilo EDT libre");

        if (movimientosCargaPendiente) {
            movimientosCargaPendiente = false;
            String origenPendiente = origenCargaMovimientosPendiente;
            origenCargaMovimientosPendiente = null;
            registrarLog(String.format(
                "Movimientos: ejecutando carga pendiente (%s) tras %.0f ms",
                origenPendiente,
                (double) (System.currentTimeMillis() - inicio)
            ));
            SwingUtilities.invokeLater(() -> solicitarCargaMovimientos(origenPendiente));
        }
    }

    /**
     * Abre el formulario modal para registrar un nuevo movimiento.
     */
    private void registrarMovimiento() {
        MovementDialogForm dialog = new MovementDialogForm(this, true);
        if (dialog.isGuardadoExitoso()) {
            registrarLog("Movimientos: registro exitoso desde formulario");
            solicitarCargaMovimientos("Nuevo movimiento");
            solicitarCargaProductos("Recalcular stock tras movimiento");
            cargarDashboard();
        }
    }

    /**
     * Permite editar cantidad/precio de un movimiento existente.
     */
    private void editarMovimiento() {
        String movimientoId = obtenerMovimientoSeleccionado();
        if (movimientoId == null) {
            return;
        }

        try {
            Movement movimiento = MovementServices.getMovementById(movimientoId);
            if (movimiento == null) {
                JOptionPane.showMessageDialog(this,
                    "El movimiento ya no existe. Actualizando lista...",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                solicitarCargaMovimientos("Movimiento desaparecido");
                return;
            }

            String cantidadStr = JOptionPane.showInputDialog(this,
                "Nueva cantidad (L)", movimiento.getQuantity());
            if (cantidadStr == null) {
                return;
            }

            String precioStr = JOptionPane.showInputDialog(this,
                "Nuevo precio unitario", movimiento.getUnitPrice());
            if (precioStr == null) {
                return;
            }

            double nuevaCantidad = Double.parseDouble(cantidadStr);
            double nuevoPrecio = Double.parseDouble(precioStr);

            MovementServices.updateMovement(movimientoId, nuevaCantidad, nuevoPrecio);
            JOptionPane.showMessageDialog(this,
                "Movimiento actualizado correctamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

            registrarLog("Movimientos: edición aplicada sobre " + movimientoId);
            solicitarCargaMovimientos("Edición movimiento");
            solicitarCargaProductos("Recalcular stock tras edición movimiento");
            cargarDashboard();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Valores numéricos inválidos",
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar movimiento: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un movimiento seleccionado tras confirmación.
     */
    private void eliminarMovimiento() {
        String movimientoId = obtenerMovimientoSeleccionado();
        if (movimientoId == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar movimiento " + movimientoId + "?", "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (MovementServices.deleteMovement(movimientoId)) {
                JOptionPane.showMessageDialog(this,
                    "Movimiento eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                registrarLog("Movimientos: eliminado " + movimientoId);
                solicitarCargaMovimientos("Eliminación movimiento");
                solicitarCargaProductos("Recalcular stock tras eliminación");
                cargarDashboard();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontró el movimiento",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar movimiento: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra un cuadro de diálogo con información detallada del movimiento.
     */
    private void verDetallesMovimiento() {
        String movimientoId = obtenerMovimientoSeleccionado();
        if (movimientoId == null) {
            return;
        }

        try {
            Movement movimiento = MovementServices.getMovementById(movimientoId);
            if (movimiento == null) {
                JOptionPane.showMessageDialog(this,
                    "El movimiento ya no existe",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                solicitarCargaMovimientos("Movimiento faltante");
                return;
            }

            String detalle = """
                ID: %s
                Tipo: %s
                Producto: %s
                Vehículo: %s
                Cantidad: %,.2f %s
                Precio unitario: %s
                Subtotal: %s
                Factura: %s
                Fecha: %s
                """.formatted(
                movimiento.getId(),
                movimiento.getMovementType(),
                construirEtiquetaProducto(movimiento.getProductId()),
                construirEtiquetaVehiculo(movimiento.getVehicleId()),
                movimiento.getQuantity(),
                movimiento.getUnidadDeMedida(),
                formatearMoneda(movimiento.getUnitPrice()),
                formatearMoneda(movimiento.getQuantity() * movimiento.getUnitPrice()),
                movimiento.getNumeroFactura() != null ? movimiento.getNumeroFactura() : "—",
                formatearFechaMovimiento(movimiento.getMovementDate())
            );

            JTextArea area = new JTextArea(detalle);
            area.setEditable(false);
            area.setFont(new Font("Consolas", Font.PLAIN, 13));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Detalle de movimiento", JOptionPane.INFORMATION_MESSAGE);

        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al consultar movimiento: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene el ID del movimiento seleccionado en la tabla, considerando el sorter.
     */
    private String obtenerMovimientoSeleccionado() {
        if (tablaMovimientos == null || tablaMovimientos.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un movimiento primero",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int filaVista = tablaMovimientos.getSelectedRow();
        int filaModelo = tablaMovimientos.convertRowIndexToModel(filaVista);
        return (String) modeloMovimientos.getValueAt(filaModelo, 0);
    }

    private void actualizarResumenMovimientos(MovimientosCargaResult resultado) {
        if (lblResumenMovimientos == null) {
            return;
        }

        lblResumenMovimientos.setText(String.format(
            "Mostrando %d movimientos | Entradas: %d (%,.2f L) | Salidas: %d (%,.2f L)",
            movimientosVisibles.size(),
            resultado.totalEntradas,
            resultado.litrosEntradas,
            resultado.totalSalidas,
            resultado.litrosSalidas
        ));
    }

    private LocalDate parsearFechaFiltro(JTextField campo) {
        if (campo == null) {
            return null;
        }
        String valor = campo.getText().trim();
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + valor);
        }
    }

    private LocalDate extraerFechaMovimiento(String fechaCruda) {
        if (fechaCruda == null || fechaCruda.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(fechaCruda).toLocalDate();
        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(fechaCruda);
            } catch (DateTimeParseException ignored) {
                registrarLog("Movimientos: formato de fecha desconocido → " + fechaCruda);
                return null;
            }
        }
    }

    private String formatearFechaMovimiento(String fechaCruda) {
        LocalDate fecha = extraerFechaMovimiento(fechaCruda);
        return fecha != null ? fecha.toString() : "—";
    }

    private String formatearMoneda(double valor) {
        return String.format("$%,.2f", valor);
    }

    private boolean coincideConTipo(Movement movimiento, String filtroTipo) {
        return "Todos".equalsIgnoreCase(filtroTipo)
            || movimiento.getMovementType().equalsIgnoreCase(filtroTipo);
    }

    private boolean coincideConFecha(Movement movimiento, LocalDate desde, LocalDate hasta) {
        LocalDate fechaMovimiento = extraerFechaMovimiento(movimiento.getMovementDate());
        if (fechaMovimiento == null) {
            return false;
        }

        boolean despuesDeDesde = (desde == null) || !fechaMovimiento.isBefore(desde);
        boolean antesDeHasta = (hasta == null) || !fechaMovimiento.isAfter(hasta);
        return despuesDeDesde && antesDeHasta;
    }

    private boolean coincideConTexto(Movement movimiento, String termino,
                                     Map<String, String> nombresProductos,
                                     Map<String, String> nombresVehiculos) {
        if (termino == null || termino.isBlank()) {
            return true;
        }

        String lower = termino.toLowerCase();
        return contiene(movimiento.getId(), lower)
            || contiene(movimiento.getProductId(), lower)
            || contiene(nombresProductos.get(movimiento.getProductId()), lower)
            || contiene(movimiento.getVehicleId(), lower)
            || contiene(nombresVehiculos.get(movimiento.getVehicleId()), lower)
            || contiene(movimiento.getNumeroFactura(), lower);
    }

    private boolean contiene(String valor, String termino) {
        return valor != null && valor.toLowerCase().contains(termino);
    }

    private String valorOpcional(String valor) {
        return valor != null && !valor.isBlank() ? valor : "—";
    }

    private String valorEditable(String valor) {
        return valor != null ? valor : "";
    }

    private String nullSiVacio(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }

    private String construirEtiquetaProducto(String productId) {
        if (productId == null) {
            return "—";
        }
        String nombre = cacheNombresProductos.getOrDefault(productId, productId);
        return productId + " - " + nombre;
    }

    private String construirEtiquetaVehiculo(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return "—";
        }
        String nombre = cacheNombresVehiculos.getOrDefault(vehicleId, vehicleId);
        return vehicleId + " - " + nombre;
    }

    private void actualizarResumenMovimientosDashboard(List<Movement> movimientos) {
        if (lblMovimientosHoy == null) {
            return;
        }
        long movimientosHoy = movimientos.stream()
            .map(m -> extraerFechaMovimiento(m.getMovementDate()))
            .filter(fecha -> fecha != null && fecha.equals(LocalDate.now()))
            .count();
        lblMovimientosHoy.setText(String.valueOf(movimientosHoy));
    }

    private static class MovimientosCargaResult {
        final List<Movement> movimientos;
        final Map<String, String> productos;
        final Map<String, String> vehiculos;
        final long totalEntradas;
        final double litrosEntradas;
        final long totalSalidas;
        final double litrosSalidas;

        MovimientosCargaResult(List<Movement> movimientos,
                               Map<String, String> productos,
                               Map<String, String> vehiculos,
                               long totalEntradas,
                               double litrosEntradas,
                               long totalSalidas,
                               double litrosSalidas) {
            this.movimientos = movimientos;
            this.productos = productos;
            this.vehiculos = vehiculos;
            this.totalEntradas = totalEntradas;
            this.litrosEntradas = litrosEntradas;
            this.totalSalidas = totalSalidas;
            this.litrosSalidas = litrosSalidas;
        }
    }

    // ========================================================================
    // PANEL 5: FACTURAS (NUEVO - CRUD COMPLETO)
    // ========================================================================

    /**
     * Crea el panel de gestión de facturas.
     */
    private JPanel crearPanelFacturas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titulo = new JLabel("🧾 GESTIÓN DE FACTURAS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Nº Factura", "Fecha Emisión", "Proveedor", "Subtotal", "IVA", "Total"};
        modeloFacturas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setRowHeight(25);
        configurarEstiloTabla(tablaFacturas);

        JScrollPane scroll = new JScrollPane(tablaFacturas);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNuevaFactura = new JButton("Nueva Factura");
        btnNuevaFactura.setBackground(new Color(100, 200, 100));
        btnNuevaFactura.addActionListener(e -> mostrarFormularioNuevaFactura());
        panelBotones.add(btnNuevaFactura);

        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.addActionListener(e -> verDetallesFactura());
        panelBotones.add(btnVerDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarFacturas());
        panelBotones.add(btnRefrescar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    // ========================================================================
    // PANEL 6: LOG DE APLICACIÓN
    // ========================================================================

    /**
     * Panel dedicado para visualizar el log interno de la aplicación.
     */
    private JPanel crearPanelLogs() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("📝 LOG DE APLICACIÓN", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titulo, BorderLayout.NORTH);

        txtLogAplicacion = new JTextArea();
        txtLogAplicacion.setEditable(false);
        txtLogAplicacion.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtLogAplicacion.setLineWrap(true);
        txtLogAplicacion.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtLogAplicacion,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnLimpiar = new JButton("Limpiar Log");
        btnLimpiar.addActionListener(e -> txtLogAplicacion.setText(""));

        JButton btnCopiar = new JButton("Copiar Todo");
        btnCopiar.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(txtLogAplicacion.getText()), null);
            JOptionPane.showMessageDialog(this, "Log copiado al portapapeles",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        });

        panelAcciones.add(btnLimpiar);
        panelAcciones.add(btnCopiar);
        panel.add(panelAcciones, BorderLayout.SOUTH);

        registrarLog("Panel de logs inicializado");

        return panel;
    }

    /**
     * Carga facturas desde la BD.
     */
    private void cargarFacturas() {
        try {
            List<Factura> facturas = FacturaServices.getAllFacturas();
            modeloFacturas.setRowCount(0);

            for (Factura f : facturas) {
                modeloFacturas.addRow(new Object[]{
                    f.getNumeroFactura(),
                    f.getFechaEmision(),
                    f.getSupplierId() != null ? f.getSupplierId() : "N/A",
                    String.format("$%,.2f", f.getSubtotal()),
                    String.format("$%,.2f", f.getIva()),
                    String.format("$%,.2f", f.getTotal())
                });
            }

            System.out.println("✅ Facturas cargadas: " + facturas.size());

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar facturas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra el formulario para crear una nueva factura.
     */
    private void mostrarFormularioNuevaFactura() {
        // Crear JDialog modal
        JDialog dialog = new JDialog(this, "Nueva Factura", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Panel de formulario
        JPanel panelForm = new JPanel(new GridLayout(8, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Campos
        JTextField txtNumeroFactura = new JTextField();
        JTextField txtFechaEmision = new JTextField(LocalDate.now().toString());
        JTextField txtFechaVencimiento = new JTextField(LocalDate.now().plusDays(30).toString());

        // ComboBox de proveedores
        JComboBox<String> cmbProveedor = new JComboBox<>();
        try {
            List<Supplier> proveedores = SupplierServices.getAllSuppliers();
            cmbProveedor.addItem("--- Sin proveedor ---");
            for (Supplier s : proveedores) {
                cmbProveedor.addItem(s.getId() + " - " + s.getName());
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        JTextField txtSubtotal = new JTextField();
        JTextField txtIva = new JTextField("19");  // IVA por defecto 19%
        JTextField txtObservaciones = new JTextField();
        JTextField txtFormaPago = new JTextField("EFECTIVO");

        // Agregar componentes
        panelForm.add(new JLabel("Nº Factura:"));
        panelForm.add(txtNumeroFactura);
        panelForm.add(new JLabel("Fecha Emisión:"));
        panelForm.add(txtFechaEmision);
        panelForm.add(new JLabel("Fecha Vencimiento:"));
        panelForm.add(txtFechaVencimiento);
        panelForm.add(new JLabel("Proveedor:"));
        panelForm.add(cmbProveedor);
        panelForm.add(new JLabel("Subtotal (COP):"));
        panelForm.add(txtSubtotal);
        panelForm.add(new JLabel("IVA (%):"));
        panelForm.add(txtIva);
        panelForm.add(new JLabel("Observaciones:"));
        panelForm.add(txtObservaciones);
        panelForm.add(new JLabel("Forma de Pago:"));
        panelForm.add(txtFormaPago);

        dialog.add(panelForm, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(100, 200, 100));
        btnGuardar.addActionListener(e -> {
            try {
                // Validaciones
                String numero = txtNumeroFactura.getText().trim();
                if (numero.isEmpty()) {
                    throw new IllegalArgumentException("Nº de factura es requerido");
                }

                double subtotal = Double.parseDouble(txtSubtotal.getText().trim());
                double ivaPorcentaje = Double.parseDouble(txtIva.getText().trim());
                double ivaValor = subtotal * ivaPorcentaje / 100;
                double total = subtotal + ivaValor;

                String proveedorId = null;
                if (cmbProveedor.getSelectedIndex() > 0) {
                    String seleccion = (String) cmbProveedor.getSelectedItem();
                    proveedorId = seleccion.split(" - ")[0];
                }

                // Crear objeto Factura
                Factura factura = new Factura(
                    numero,
                    LocalDate.parse(txtFechaEmision.getText().trim()),
                    LocalDate.parse(txtFechaVencimiento.getText().trim()),
                    proveedorId,
                    subtotal,
                    ivaValor,
                    total,
                    txtObservaciones.getText().trim(),
                    txtFormaPago.getText().trim(),
                    null  // cuenta_bancaria
                );

                // Guardar en BD (con lista vacía de detalles por simplicidad)
                FacturaServices.createFacturaWithDetails(factura, new ArrayList<>());

                JOptionPane.showMessageDialog(dialog,
                    "Factura creada correctamente\n" +
                    "Total: $" + String.format("%,.2f", total),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                cargarFacturas();
                cargarDashboard();  // Actualizar estadísticas

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error en formato numérico: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al crear factura: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Muestra los detalles de una factura seleccionada.
     */
    private void verDetallesFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona una factura para ver detalles",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numeroFactura = (String) modeloFacturas.getValueAt(fila, 0);

        try {
            List<DetalleFactura> detalles = FacturaServices.getDetallesByFactura(numeroFactura);

            if (detalles.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Esta factura no tiene detalles registrados",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Crear tabla de detalles
            String[] columnas = {"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
            DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0);

            for (DetalleFactura d : detalles) {
                double subtotal = d.getCantidad() * d.getPrecioUnitario();
                modeloDetalles.addRow(new Object[]{
                    d.getIdDetalle(),
                    d.getProducto(),
                    d.getCantidad(),
                    String.format("$%,.2f", d.getPrecioUnitario()),
                    String.format("$%,.2f", subtotal)
                });
            }

            JTable tablaDetalles = new JTable(modeloDetalles);
            tablaDetalles.setRowHeight(25);
            JScrollPane scroll = new JScrollPane(tablaDetalles);
            scroll.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(this, scroll,
                "Detalles de Factura " + numeroFactura,
                JOptionPane.INFORMATION_MESSAGE);

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar detalles: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

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
     * Refresca todas las tablas.
     */
    private void refrescarTodo() {
        cargarDashboard();
        solicitarCargaProductos("Refrescar todo");
        cargarVehiculos();
        solicitarCargaProveedores("Refrescar todo");
        solicitarCargaMovimientos("Refrescar todo");
        cargarFacturas();
        JOptionPane.showMessageDialog(this,
            "Todas las vistas han sido actualizadas",
            "Refrescado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra información sobre Forestech.
     */
    private void mostrarAcercaDe() {
        String mensaje = """
            Forestech Oil Management System
            Professional Edition 1.0
            
            Sistema integral de gestión de combustibles
            Desarrollado con Java Swing + MySQL
            
            Módulos:
            • Gestión de Productos
            • Gestión de Vehículos
            • Gestión de Proveedores
            • Registro de Movimientos
            • Gestión de Facturas
            • Dashboard de Estadísticas
            
            © 2025 Forestech Learning Project
            """;

        JOptionPane.showMessageDialog(this, mensaje,
            "Acerca de Forestech", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra atajos de teclado disponibles.
     */
    private void mostrarAtajos() {
        String mensaje = """
            ATAJOS DE TECLADO DISPONIBLES:
            
            NAVEGACIÓN:
            • Ctrl+1 → Ir a Inicio (Dashboard)
            • Ctrl+2 → Ir a Productos
            • Ctrl+3 → Ir a Vehículos
            • Ctrl+4 → Ir a Movimientos
            • Ctrl+5 → Ir a Facturas
            • Ctrl+6 → Ir a Proveedores
            
            ACCIONES:
            • Ctrl+R → Refrescar todas las vistas
            • Ctrl+Q → Salir de la aplicación
            • F1 → Mostrar esta ayuda
            
            NAVEGACIÓN CON MOUSE:
            • Click en botones del panel izquierdo
            • Divisor JSplitPane ajustable arrastrando
            """;

        JOptionPane.showMessageDialog(this, mensaje,
            "Atajos de Teclado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Registra un mensaje en el log interno y en consola.
     */
    private void registrarLog(String mensaje) {
        String linea = "[" + LocalDateTime.now().format(LOG_TIME_FORMAT) + "] " + mensaje;
        System.out.println(linea);

        if (txtLogAplicacion == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            txtLogAplicacion.append(linea + System.lineSeparator());
            txtLogAplicacion.setCaretPosition(txtLogAplicacion.getDocument().getLength());
        });
    }

    // ========================================================================
    // MÉTODO MAIN
    // ========================================================================

    /**
     * Método principal para ejecutar la aplicación.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Aplicar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar Look and Feel del sistema");
        }

        // Iniciar aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new ForestechProfessionalApp();
        });

        // Log de inicio
        System.out.println("=".repeat(70));
        System.out.println("FORESTECH OIL MANAGEMENT SYSTEM - PROFESSIONAL EDITION");
        System.out.println("=".repeat(70));
        System.out.println("Checkpoint 9.13: Arquitectura Profesional");
        System.out.println("\nCONCEPTOS IMPLEMENTADOS:");
        System.out.println("• JSplitPane - Panel de navegación lateral ajustable");
        System.out.println("• CardLayout - Cambio fluido entre vistas");
        System.out.println("• Dashboard - Estadísticas en tiempo real");
        System.out.println("• CRUD Facturas - Gestión completa con transacciones");
        System.out.println("• 5 módulos integrados en 1 aplicación");
        System.out.println("\nConexión: MySQL localhost:3306/FORESTECHOIL");
        System.out.println("Atajos: Ctrl+1/2/3/4/5 | Ctrl+R | Ctrl+Q | F1");
        System.out.println("=".repeat(70));
    }
}
