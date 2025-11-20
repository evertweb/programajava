package com.forestech.presentation.ui;

import com.forestech.presentation.ui.core.NavigationManager;
import com.forestech.presentation.ui.components.AppMenuBuilder;
import com.forestech.presentation.ui.components.SideNavigationPanel;
import com.forestech.presentation.ui.dashboard.DashboardPanel;
import com.forestech.presentation.ui.invoices.InvoicesPanel;
import com.forestech.presentation.ui.logs.LogsPanel;
import com.forestech.presentation.ui.movements.MovementsPanel;
import com.forestech.presentation.ui.products.ProductsPanel;
import com.forestech.presentation.ui.suppliers.SuppliersPanel;
import com.forestech.presentation.ui.utils.CatalogCache;
import com.forestech.presentation.ui.vehicles.VehiclesPanel;
import com.forestech.presentation.clients.ProductServiceClient;
import com.forestech.presentation.clients.VehicleServiceClient;
import com.forestech.presentation.clients.SupplierServiceClient;
import com.forestech.presentation.clients.MovementServiceClient;
import com.forestech.presentation.clients.ReportsServiceClient;
import com.forestech.presentation.clients.InvoiceServiceClient;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import com.forestech.presentation.ui.utils.BackgroundTaskExecutor;
import com.forestech.presentation.ui.utils.DiagnosticLogger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Versión modular de ForestechProfessionalApp. El JFrame actúa únicamente como
 * orquestador mientras que cada módulo mantiene su lógica dentro de un JPanel
 * dedicado.
 * 
 * Refactorizado: Usa NavigationManager, AppMenuBuilder y SideNavigationPanel.
 */
public class ForestechProfessionalApp extends JFrame {

    private static final DateTimeFormatter LOG_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;
    private NavigationManager navigationManager;
    private SideNavigationPanel sideNavigationPanel;

    private DashboardPanel dashboardPanel;
    private ProductsPanel productsPanel;
    private VehiclesPanel vehiclesPanel;
    private SuppliersPanel suppliersPanel;
    private MovementsPanel movementsPanel;
    private InvoicesPanel invoicesPanel;
    private LogsPanel logsPanel;

    private final CatalogCache catalogCache;

    // Clients
    private final ProductServiceClient productClient;
    private final VehicleServiceClient vehicleClient;
    private final SupplierServiceClient supplierClient;
    private final MovementServiceClient movementClient;
    private final ReportsServiceClient reportsClient;
    private final InvoiceServiceClient invoiceClient;

    // Timer para debounce de refreshDashboard
    private Timer dashboardRefreshTimer;
    private static final int DASHBOARD_DEBOUNCE_MS = 500;

    /**
     * Constructor principal.
     */
    public ForestechProfessionalApp() {
        // Initialize Clients
        this.productClient = new ProductServiceClient();
        this.vehicleClient = new VehicleServiceClient();
        this.supplierClient = new SupplierServiceClient();
        this.movementClient = new MovementServiceClient();
        this.reportsClient = new ReportsServiceClient();
        this.invoiceClient = new InvoiceServiceClient();

        this.catalogCache = new CatalogCache(
                productClient,
                vehicleClient);

        // Initialize DiagnosticLogger FIRST
        DiagnosticLogger.initialize();
        DiagnosticLogger.log("ForestechProfessionalApp iniciando...");

        // Initialize BackgroundTaskExecutor
        BackgroundTaskExecutor.setGlobalLogger(this::registrarLog);

        // Configuración de la ventana
        setTitle("Forestech Oil Management System - Professional Edition");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            registrarLog("Cerrando executor de tareas...");
            BackgroundTaskExecutor.shutdown();
        }));

        // Inicializar Layout y NavigationManager
        crearLayoutPrincipal();

        // Inicializar Menú (depende de navigationManager)
        crearMenuBar();

        cargarDatosIniciales();
        setVisible(true);
    }

    /**
     * Crea el JMenuBar usando AppMenuBuilder.
     */
    private void crearMenuBar() {
        AppMenuBuilder menuBuilder = new AppMenuBuilder(
                navigationManager,
                this::refrescarTodo,
                () -> System.exit(0),
                this::mostrarAcercaDe,
                this::mostrarAtajos);
        setJMenuBar(menuBuilder.build());
    }

    /**
     * Crea el JSplitPane con panel de navegación y contenedor de vistas.
     */
    private void crearLayoutPrincipal() {
        cardLayout = new CardLayout();
        contenedorPrincipal = new JPanel(cardLayout);

        // Inicializar NavigationManager
        navigationManager = new NavigationManager(cardLayout, contenedorPrincipal, this::registrarLog);

        // Inicializar y registrar paneles
        inicializarPaneles();

        // Inicializar SideNavigationPanel
        sideNavigationPanel = new SideNavigationPanel(navigationManager);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideNavigationPanel, contenedorPrincipal);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.0);

        add(splitPane, BorderLayout.CENTER);
    }

    private void inicializarPaneles() {
        Runnable dashboardReload = this::refrescarDashboard;

        // 1. Dashboard
        dashboardPanel = new DashboardPanel(
                new DashboardCallbacks(),
                this::registrarLog,
                productClient,
                vehicleClient,
                movementClient,
                invoiceClient);
        navigationManager.registerView("dashboard", dashboardPanel, dashboardPanel::refreshStats,
                dashboardPanel::cancelCurrentLoad);

        // 2. Productos
        productsPanel = new ProductsPanel(
                this,
                this::registrarLog,
                dashboardReload,
                productClient,
                movementClient,
                reportsClient);
        navigationManager.registerView("productos", productsPanel, () -> productsPanel.requestRefresh("Navegación"),
                productsPanel::cancelCurrentLoad);

        // 3. Vehículos
        vehiclesPanel = new VehiclesPanel(
                this,
                this::registrarLog,
                dashboardReload,
                vehicleClient,
                productClient);
        navigationManager.registerView("vehiculos", vehiclesPanel, vehiclesPanel::refresh,
                vehiclesPanel::cancelCurrentLoad);

        // 4. Proveedores
        suppliersPanel = new SuppliersPanel(
                this,
                this::registrarLog,
                supplierClient);
        navigationManager.registerView("proveedores", suppliersPanel, () -> suppliersPanel.requestRefresh("Navegación"),
                suppliersPanel::cancelCurrentLoad);

        // 5. Movimientos
        movementsPanel = new MovementsPanel(
                this,
                this::registrarLog,
                dashboardReload,
                origen -> productsPanel.requestRefresh(origen),
                movementClient,
                productClient,
                vehicleClient,
                invoiceClient,
                catalogCache);
        navigationManager.registerView("movimientos", movementsPanel, () -> movementsPanel.requestRefresh("Navegación"),
                movementsPanel::cancelCurrentLoad);

        // 6. Facturas
        invoicesPanel = new InvoicesPanel(
                this,
                this::registrarLog,
                dashboardReload,
                invoiceClient,
                supplierClient);
        navigationManager.registerView("facturas", invoicesPanel, invoicesPanel::refresh,
                invoicesPanel::cancelCurrentLoad);

        // 7. Logs
        logsPanel = new LogsPanel();
        navigationManager.registerView("logs", logsPanel, null, null);
    }

    private void cargarDatosIniciales() {
        SwingUtilities.invokeLater(() -> {
            registrarLog("App: inicialización completada (lazy loading habilitado)");
        });
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

    private void refrescarDashboard() {
        if (dashboardPanel == null) {
            return;
        }

        if (dashboardRefreshTimer != null && dashboardRefreshTimer.isRunning()) {
            dashboardRefreshTimer.restart();
            return;
        }

        dashboardRefreshTimer = new Timer(DASHBOARD_DEBOUNCE_MS, e -> {
            dashboardPanel.refreshStats();
            dashboardRefreshTimer = null;
        });
        dashboardRefreshTimer.setRepeats(false);
        dashboardRefreshTimer.start();
    }

    private class DashboardCallbacks implements DashboardPanel.DashboardActions {
        @Override
        public void navigateTo(String cardName) {
            navigationManager.navigateTo(cardName, true);
            if (sideNavigationPanel != null) {
                sideNavigationPanel.setActiveButton(cardName);
            }
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
