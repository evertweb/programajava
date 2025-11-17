package com.forestech.ui;

import com.forestech.services.ServiceFactory;
import com.forestech.ui.dashboard.DashboardPanel;
import com.forestech.ui.invoices.InvoicesPanel;
import com.forestech.ui.logs.LogsPanel;
import com.forestech.ui.movements.MovementsPanel;
import com.forestech.ui.products.ProductsPanel;
import com.forestech.ui.suppliers.SuppliersPanel;
import com.forestech.ui.vehicles.VehiclesPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Versión modular de ForestechProfessionalApp. El JFrame actúa únicamente como
 * orquestador mientras que cada módulo mantiene su lógica dentro de un JPanel
 * dedicado.
 */
public class ForestechProfessionalApp extends JFrame {

    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final long NAVIGATION_COOLDOWN_MS = 1200L;

    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;

    private JButton btnInicio;
    private JButton btnProductos;
    private JButton btnVehiculos;
    private JButton btnProveedores;
    private JButton btnMovimientos;
    private JButton btnFacturas;
    private JButton btnLogs;

    private DashboardPanel dashboardPanel;
    private ProductsPanel productsPanel;
    private VehiclesPanel vehiclesPanel;
    private SuppliersPanel suppliersPanel;
    private MovementsPanel movementsPanel;
    private InvoicesPanel invoicesPanel;
    private LogsPanel logsPanel;

    private String vistaActual = "dashboard";
    private final Map<String, Long> ultimaCargaPorVista = new HashMap<>();

    // Timer para debounce de refreshDashboard (evita múltiples refreshes en cascada)
    private Timer dashboardRefreshTimer;
    private static final int DASHBOARD_DEBOUNCE_MS = 500;

    /**
     * Constructor principal.
     */
    public ForestechProfessionalApp() {
        // Configuración de la ventana
        setTitle("Forestech Oil Management System - Professional Edition");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearMenuBar();
        crearLayoutPrincipal();
        cargarDatosIniciales();
        setVisible(true);
    }

    /**
     * Crea el JMenuBar con menús de navegación rápida.
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

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

        JMenu menuVer = new JMenu("Ver");
        menuVer.setMnemonic('V');

        JMenuItem itemInicio = new JMenuItem("Ir a Inicio");
        itemInicio.setAccelerator(KeyStroke.getKeyStroke("control 1"));
        itemInicio.addActionListener(e -> navegarDesdeMenu("dashboard"));
        menuVer.add(itemInicio);

        JMenuItem itemProductos = new JMenuItem("Ir a Productos");
        itemProductos.setAccelerator(KeyStroke.getKeyStroke("control 2"));
        itemProductos.addActionListener(e -> navegarDesdeMenu("productos"));
        menuVer.add(itemProductos);

        JMenuItem itemVehiculos = new JMenuItem("Ir a Vehículos");
        itemVehiculos.setAccelerator(KeyStroke.getKeyStroke("control 3"));
        itemVehiculos.addActionListener(e -> navegarDesdeMenu("vehiculos"));
        menuVer.add(itemVehiculos);

        JMenuItem itemProveedores = new JMenuItem("Ir a Proveedores");
        itemProveedores.setAccelerator(KeyStroke.getKeyStroke("control 6"));
        itemProveedores.addActionListener(e -> navegarDesdeMenu("proveedores"));
        menuVer.add(itemProveedores);

        JMenuItem itemMovimientos = new JMenuItem("Ir a Movimientos");
        itemMovimientos.setAccelerator(KeyStroke.getKeyStroke("control 4"));
        itemMovimientos.addActionListener(e -> navegarDesdeMenu("movimientos"));
        menuVer.add(itemMovimientos);

        JMenuItem itemFacturas = new JMenuItem("Ir a Facturas");
        itemFacturas.setAccelerator(KeyStroke.getKeyStroke("control 5"));
        itemFacturas.addActionListener(e -> navegarDesdeMenu("facturas"));
        menuVer.add(itemFacturas);

        menuBar.add(menuVer);

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
        JPanel panelNavegacion = crearPanelNavegacion();
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);

        inicializarPaneles();
        contenedorPrincipal.add(dashboardPanel, "dashboard");
        contenedorPrincipal.add(productsPanel, "productos");
        contenedorPrincipal.add(vehiclesPanel, "vehiculos");
        contenedorPrincipal.add(suppliersPanel, "proveedores");
        contenedorPrincipal.add(movementsPanel, "movimientos");
        contenedorPrincipal.add(invoicesPanel, "facturas");
        contenedorPrincipal.add(logsPanel, "logs");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelNavegacion, contenedorPrincipal);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.0);

        add(splitPane, BorderLayout.CENTER);
    }

    private void inicializarPaneles() {
        // Obtener ServiceFactory para inyectar dependencias
        ServiceFactory factory = ServiceFactory.getInstance();

        Runnable dashboardReload = this::refrescarDashboard;

        // Inicializar Panels con Dependency Injection
        productsPanel = new ProductsPanel(
            this,
            this::registrarLog,
            dashboardReload,
            factory.getProductServices(),
            factory.getMovementServices()
        );

        vehiclesPanel = new VehiclesPanel(
            this,
            this::registrarLog,
            dashboardReload,
            factory.getVehicleServices()
        );

        suppliersPanel = new SuppliersPanel(
            this,
            this::registrarLog,
            factory.getSupplierServices()
        );

        movementsPanel = new MovementsPanel(
            this,
            this::registrarLog,
            dashboardReload,
            origen -> productsPanel.requestRefresh(origen),
            factory.getMovementServices(),
            factory.getProductServices(),
            factory.getVehicleServices()
        );

        invoicesPanel = new InvoicesPanel(
            this,
            this::registrarLog,
            dashboardReload,
            factory.getFacturaServices(),
            factory.getSupplierServices()
        );

        logsPanel = new LogsPanel();

        dashboardPanel = new DashboardPanel(
            new DashboardCallbacks(),
            this::registrarLog,
            factory.getProductServices(),
            factory.getVehicleServices(),
            factory.getMovementServices(),
            factory.getFacturaServices()
        );
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

        marcarBotonActivo(btnInicio);

        return panel;
    }

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

    private void navegarA(String vista) {
        navegarA(vista, false);
    }

    private void navegarA(String vista, boolean forzar) {
        if (vista == null) {
            return;
        }

        if (debeIgnorarNavegacion(vista, forzar)) {
            return;
        }

        // CANCELAR carga de vista anterior antes de cambiar
        cancelarCargaVistaActual();

        cardLayout.show(contenedorPrincipal, vista);
        vistaActual = vista;

        switch (vista) {
            case "dashboard" -> dashboardPanel.refreshStats();
            case "productos" -> productsPanel.requestRefresh("Navegación");
            case "vehiculos" -> vehiclesPanel.refresh();
            case "proveedores" -> suppliersPanel.requestRefresh("Navegación");
            case "movimientos" -> movementsPanel.requestRefresh("Navegación");
            case "facturas" -> invoicesPanel.refresh();
        }

        ultimaCargaPorVista.put(vista, System.currentTimeMillis());
    }

    /**
     * Cancela la carga en curso de la vista actual antes de navegar a otra.
     * Esto evita acumulación de workers obsoletos.
     */
    private void cancelarCargaVistaActual() {
        if (vistaActual == null) {
            return;
        }

        switch (vistaActual) {
            case "dashboard" -> dashboardPanel.cancelCurrentLoad();
            case "productos" -> productsPanel.cancelCurrentLoad();
            case "vehiculos" -> vehiclesPanel.cancelCurrentLoad();
            case "proveedores" -> suppliersPanel.cancelCurrentLoad();
            case "movimientos" -> movementsPanel.cancelCurrentLoad();
            case "facturas" -> invoicesPanel.cancelCurrentLoad();
        }
    }

    private boolean debeIgnorarNavegacion(String vista, boolean forzar) {
        if (forzar) {
            return false;
        }

        if (!vista.equals(vistaActual)) {
            return false;
        }

        long ultimaCarga = ultimaCargaPorVista.getOrDefault(vista, 0L);
        long ahora = System.currentTimeMillis();
        if (ahora - ultimaCarga < NAVIGATION_COOLDOWN_MS) {
            registrarLog(String.format(
                "Navegación ignorada para %s (cooldown %.0f ms)",
                vista,
                (double) (NAVIGATION_COOLDOWN_MS - (ahora - ultimaCarga))
            ));
            return true;
        }

        return false;
    }

    private void marcarBotonActivo(JButton botonActivo) {
        btnInicio.setBackground(new Color(45, 52, 54));
        btnProductos.setBackground(new Color(45, 52, 54));
        btnVehiculos.setBackground(new Color(45, 52, 54));
        btnProveedores.setBackground(new Color(45, 52, 54));
        btnMovimientos.setBackground(new Color(45, 52, 54));
        btnFacturas.setBackground(new Color(45, 52, 54));
        btnLogs.setBackground(new Color(45, 52, 54));

        botonActivo.setBackground(new Color(52, 152, 219));
    }

    private void cargarDatosIniciales() {
        SwingUtilities.invokeLater(() -> {
            // LAZY LOADING: Solo cargar Dashboard al inicio
            // Las demás vistas se cargarán cuando el usuario navegue a ellas
            dashboardPanel.refreshStats();
            registrarLog("App: carga inicial completada (lazy loading habilitado)");
        });
    }

    private void navegarDesdeMenu(String vista) {
        navegarA(vista);
        marcarBotonPorVista(vista);
    }

    private void marcarBotonPorVista(String vista) {
        switch (vista) {
            case "dashboard" -> marcarBotonActivo(btnInicio);
            case "productos" -> marcarBotonActivo(btnProductos);
            case "vehiculos" -> marcarBotonActivo(btnVehiculos);
            case "proveedores" -> marcarBotonActivo(btnProveedores);
            case "movimientos" -> marcarBotonActivo(btnMovimientos);
            case "facturas" -> marcarBotonActivo(btnFacturas);
            case "logs" -> marcarBotonActivo(btnLogs);
        }
    }

    private void refrescarTodo() {
        refrescarDashboard();
        productsPanel.requestRefresh("Refrescar todo");
        vehiclesPanel.refresh();
        suppliersPanel.requestRefresh("Refrescar todo");
        movementsPanel.requestRefresh("Refrescar todo");
        invoicesPanel.refresh();
        JOptionPane.showMessageDialog(this,
            "Todas las vistas han sido actualizadas",
            "Refrescado", JOptionPane.INFORMATION_MESSAGE);
    }

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

    private void registrarLog(String mensaje) {
        String linea = "[" + LocalDateTime.now().format(LOG_TIME_FORMAT) + "] " + mensaje;
        System.out.println(linea);
        if (logsPanel != null) {
            SwingUtilities.invokeLater(() -> logsPanel.append(linea));
        }
    }

    /**
     * Refresca el dashboard con debounce inteligente.
     * Si hay múltiples llamadas en 500ms, solo ejecuta el último refresh.
     */
    private void refrescarDashboard() {
        if (dashboardPanel == null) {
            return;
        }

        // Si ya hay un timer corriendo, reiniciarlo (debounce)
        if (dashboardRefreshTimer != null && dashboardRefreshTimer.isRunning()) {
            dashboardRefreshTimer.restart();
            return;
        }

        // Crear nuevo timer que ejecutará el refresh después del delay
        dashboardRefreshTimer = new Timer(DASHBOARD_DEBOUNCE_MS, e -> {
            dashboardPanel.refreshStats();
            dashboardRefreshTimer = null;
        });
        dashboardRefreshTimer.setRepeats(false);
        dashboardRefreshTimer.start();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar Look and Feel del sistema");
        }

        SwingUtilities.invokeLater(ForestechProfessionalApp::new);

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

    private class DashboardCallbacks implements DashboardPanel.DashboardActions {
        @Override
        public void navigateTo(String cardName) {
            navegarA(cardName, true);
            marcarBotonPorVista(cardName);
        }

        @Override
        public void requestProductCreation() {
            productsPanel.requestCreationShortcut();
        }

        @Override
        public void requestVehicleCreation() {
            vehiclesPanel.requestCreationShortcut();
        }

        @Override
        public void requestSupplierCreation() {
            suppliersPanel.requestCreationShortcut();
        }

        @Override
        public void requestMovementCreation() {
            movementsPanel.requestRefresh("Desde Dashboard");
            movementsPanel.registrarMovimiento();
        }

        @Override
        public void requestInvoiceCreation() {
            invoicesPanel.requestCreationShortcut();
        }
    }
}
