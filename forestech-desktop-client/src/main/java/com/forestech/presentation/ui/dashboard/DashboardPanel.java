package com.forestech.presentation.ui.dashboard;

import com.forestech.modules.inventory.models.Movement;
import com.forestech.modules.catalog.models.Product;
import com.forestech.modules.fleet.models.Vehicle;
import com.forestech.modules.invoicing.models.Invoice;
import com.forestech.presentation.clients.MovementServiceClient;
import com.forestech.presentation.clients.ProductServiceClient;
import com.forestech.presentation.clients.VehicleServiceClient;
import com.forestech.presentation.clients.InvoiceServiceClient;
import com.forestech.presentation.ui.utils.AsyncLoadManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import com.forestech.presentation.ui.utils.FontScheme;
import com.forestech.presentation.ui.utils.IconManager;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import com.forestech.presentation.ui.utils.ColorScheme;
import com.forestech.presentation.ui.utils.BackgroundTaskExecutor;

/**
 * Panel principal de estadísticas.
 */
public class DashboardPanel extends JPanel {

    private final DashboardActions actions;
    private final Consumer<String> logger;
    private final AsyncLoadManager loadManager;

    // Clients (Microservices)
    private final ProductServiceClient productClient;
    private final VehicleServiceClient vehicleClient;
    private final MovementServiceClient movementClient;
    private final InvoiceServiceClient invoiceClient;

    private JLabel lblTotalProductos;
    private JLabel lblTotalVehiculos;
    private JLabel lblMovimientosHoy;
    private JLabel lblTotalFacturas;

    public DashboardPanel(DashboardActions actions,
            Consumer<String> logger,
            ProductServiceClient productClient,
            VehicleServiceClient vehicleClient,
            MovementServiceClient movementClient,
            InvoiceServiceClient invoiceClient) {
        this.actions = actions;
        this.logger = logger;
        this.productClient = productClient;
        this.vehicleClient = vehicleClient;
        this.movementClient = movementClient;
        this.invoiceClient = invoiceClient;
        this.loadManager = new AsyncLoadManager("Dashboard", logger, this::refreshStatsAsync);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(ColorScheme.BACKGROUND_LIGHT);

        add(createTitle(), BorderLayout.NORTH);
        add(createCards(), BorderLayout.CENTER);
        add(createQuickActions(), BorderLayout.SOUTH);
    }

    private JLabel createTitle() {
        JLabel titulo = new JLabel("DASHBOARD - RESUMEN GENERAL", JLabel.CENTER);
        titulo.setFont(FontScheme.HEADER_1);
        return titulo;
    }

    private JPanel createCards() {
        JPanel cards = new JPanel(new GridLayout(2, 3, 20, 20));
        cards.setBackground(ColorScheme.BACKGROUND_LIGHT);
        cards.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        cards.add(createCard("Total Productos", label -> lblTotalProductos = label, ColorScheme.STATUS_INFO));
        cards.add(createCard("Total Vehículos", label -> lblTotalVehiculos = label, ColorScheme.STATUS_SUCCESS));
        cards.add(createCard("Movimientos Hoy", label -> lblMovimientosHoy = label, ColorScheme.STATUS_SECONDARY));
        cards.add(createCard("Total Facturas", label -> lblTotalFacturas = label, ColorScheme.STATUS_WARNING));
        cards.add(createCard("Stock Bajo", null, ColorScheme.STATUS_DANGER));
        cards.add(createCard("Sistema Activo", null, ColorScheme.STATUS_SUCCESS));

        return cards;
    }

    private JPanel createCard(String title, Consumer<JLabel> holder, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(FontScheme.BODY_BOLD);
        lblTitle.setForeground(ColorScheme.TEXT_ON_COLOR);

        JLabel lblValue = new JLabel("0", JLabel.CENTER);
        lblValue.setFont(FontScheme.DASHBOARD_VALUE);
        lblValue.setForeground(ColorScheme.TEXT_ON_COLOR);

        if (holder != null) {
            holder.accept(lblValue);
        }

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(ColorScheme.BACKGROUND_LIGHT);

        panel.add(createActionButton("Nuevo Producto", "productos", "plus", ColorScheme.BUTTON_PRIMARY_BG,
                actions::requestProductCreation));
        panel.add(createActionButton("Nuevo Vehículo", "vehiculos", "truck", ColorScheme.SUCCESS,
                actions::requestVehicleCreation));
        panel.add(createActionButton("Nuevo Proveedor", "proveedores", "users", ColorScheme.BUTTON_INFO_BG,
                actions::requestSupplierCreation));
        panel.add(createActionButton("Registrar Movimiento", "movimientos", "exchange", ColorScheme.SECONDARY,
                actions::requestMovementCreation));
        panel.add(
                createActionButton("Nueva Factura", "facturas", "file-text", ColorScheme.WARNING,
                        actions::requestInvoiceCreation));

        return panel;
    }

    private JButton createActionButton(String text, String targetCard, String iconName, Color color, Runnable action) {
        JButton button = new JButton(text);
        button.setIcon(IconManager.getIcon(iconName));
        button.setBackground(color);
        button.setForeground(ColorScheme.TEXT_ON_COLOR);
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            actions.navigateTo(targetCard);
            if (action != null) {
                action.run();
            }
        });
        return button;
    }

    public void refreshStats() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refreshStats);
            return;
        }
        loadManager.requestLoad("Solicitud externa");
    }

    public void cancelCurrentLoad() {
        loadManager.cancelCurrentLoad();
    }

    private void refreshStatsAsync(String origin) {
        setBusyCursor(true);

        // Mostrar indicadores de carga
        lblTotalProductos.setText("...");
        lblTotalVehiculos.setText("...");
        lblMovimientosHoy.setText("...");
        lblTotalFacturas.setText("...");
        logger.accept("Cargando estadísticas del Dashboard...");

        final long inicio = System.currentTimeMillis();
        SwingWorker<DashboardMetrics, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardMetrics doInBackground() throws Exception {
                List<Product> productos = productClient.findAll();
                List<Vehicle> vehiculos = vehicleClient.findAll();
                List<Movement> movimientos = movementClient.findAll();
                List<Invoice> facturas = invoiceClient.findAll();
                return new DashboardMetrics(
                        productos.size(),
                        vehiculos.size(),
                        countTodayMovements(movimientos),
                        facturas.size());
            }

            @Override
            protected void done() {
                try {
                    DashboardMetrics metrics = get();
                    lblTotalProductos.setText(String.valueOf(metrics.totalProductos));
                    lblTotalVehiculos.setText(String.valueOf(metrics.totalVehiculos));
                    lblMovimientosHoy.setText(String.valueOf(metrics.movimientosHoy));
                    lblTotalFacturas.setText(String.valueOf(metrics.totalFacturas));
                    long duracion = System.currentTimeMillis() - inicio;
                    logger.accept(String.format("✅ Dashboard actualizado en %d ms (%s)", duracion, origin));
                } catch (ExecutionException ex) {
                    // Restaurar labels en caso de error
                    lblTotalProductos.setText("ERROR");
                    lblTotalVehiculos.setText("ERROR");
                    lblMovimientosHoy.setText("ERROR");
                    lblTotalFacturas.setText("ERROR");
                    handleError(ex.getCause() != null ? ex.getCause() : ex);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Dashboard: carga interrumpida");
                } finally {
                    setBusyCursor(false);
                    loadManager.finish(inicio);
                }
            }
        };

        // Registrar worker para cancelación y ejecutar en pool centralizado
        loadManager.registerWorker(worker);
        BackgroundTaskExecutor.submit(worker);
    }

    private void handleError(Throwable error) {
        String mensaje = "Error inesperado en dashboard: " + error.getMessage();

        // Log detallado del error
        logger.accept("❌ Error al cargar dashboard: " + mensaje);
        error.printStackTrace();

        // Mostrar diálogo con opción de reintentar
        int opcion = JOptionPane.showOptionDialog(
                this,
                "No se pudieron cargar las estadísticas del Dashboard.\n\n" +
                        "Detalles: " + mensaje + "\n\n" +
                        "Posibles causas:\n" +
                        "• Microservicios no están corriendo\n" +
                        "• API Gateway no accesible\n\n" +
                        "¿Deseas reintentar?",
                "Error de Conexión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                new Object[] { "Reintentar", "Cancelar" },
                "Reintentar");

        if (opcion == JOptionPane.YES_OPTION) {
            refreshStats();
        }
    }

    private long countTodayMovements(List<Movement> movimientos) {
        LocalDate today = LocalDate.now();
        return movimientos.stream()
                .map(m -> m.getCreatedAt() != null ? m.getCreatedAt().toLocalDate() : null)
                .filter(today::equals)
                .count();
    }

    private void setBusyCursor(boolean busy) {
        setCursor(busy ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR)
                : java.awt.Cursor.getDefaultCursor());
    }

    private record DashboardMetrics(int totalProductos,
            int totalVehiculos,
            long movimientosHoy,
            int totalFacturas) {
    }

    public interface DashboardActions {
        void navigateTo(String cardName);

        void requestProductCreation();

        void requestVehicleCreation();

        void requestSupplierCreation();

        void requestMovementCreation();

        void requestInvoiceCreation();
    }
}
