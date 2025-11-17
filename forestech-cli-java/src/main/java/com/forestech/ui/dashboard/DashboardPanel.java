package com.forestech.ui.dashboard;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Factura;
import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.services.FacturaServices;
import com.forestech.services.MovementServices;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;
import com.forestech.ui.utils.AsyncLoadManager;

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
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.forestech.ui.utils.UIUtils;

/**
 * Panel principal de estadísticas.
 */
public class DashboardPanel extends JPanel {

    private final DashboardActions actions;
    private final Consumer<String> logger;
    private final AsyncLoadManager loadManager;

    // Services (Dependency Injection)
    private final ProductServices productServices;
    private final VehicleServices vehicleServices;
    private final MovementServices movementServices;
    private final FacturaServices facturaServices;

    private JLabel lblTotalProductos;
    private JLabel lblTotalVehiculos;
    private JLabel lblMovimientosHoy;
    private JLabel lblTotalFacturas;

    public DashboardPanel(DashboardActions actions,
                          Consumer<String> logger,
                          ProductServices productServices,
                          VehicleServices vehicleServices,
                          MovementServices movementServices,
                          FacturaServices facturaServices) {
        this.actions = actions;
        this.logger = logger;
        this.productServices = productServices;
        this.vehicleServices = vehicleServices;
        this.movementServices = movementServices;
        this.facturaServices = facturaServices;
        this.loadManager = new AsyncLoadManager("Dashboard", logger, this::refreshStatsAsync);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        add(createTitle(), BorderLayout.NORTH);
        add(createCards(), BorderLayout.CENTER);
        add(createQuickActions(), BorderLayout.SOUTH);
    }

    private JLabel createTitle() {
        JLabel titulo = new JLabel("📊 DASHBOARD - RESUMEN GENERAL", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        return titulo;
    }

    private JPanel createCards() {
        JPanel cards = new JPanel(new GridLayout(2, 3, 20, 20));
        cards.setBackground(Color.WHITE);
        cards.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        cards.add(createCard("📦 Total Productos", label -> lblTotalProductos = label, new Color(52, 152, 219)));
        cards.add(createCard("🚛 Total Vehículos", label -> lblTotalVehiculos = label, new Color(46, 204, 113)));
        cards.add(createCard("📊 Movimientos Hoy", label -> lblMovimientosHoy = label, new Color(155, 89, 182)));
        cards.add(createCard("🧾 Total Facturas", label -> lblTotalFacturas = label, new Color(230, 126, 34)));
        cards.add(createCard("⚠️ Stock Bajo", null, new Color(231, 76, 60)));
        cards.add(createCard("✅ Sistema Activo", null, new Color(26, 188, 156)));

        return cards;
    }

    private JPanel createCard(String title, Consumer<JLabel> holder, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel("0", JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);

        if (holder != null) {
            holder.accept(lblValue);
        }

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel createQuickActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);

        panel.add(createActionButton("Nuevo Producto", "productos", new Color(52, 152, 219), actions::requestProductCreation));
        panel.add(createActionButton("Nuevo Vehículo", "vehiculos", new Color(46, 204, 113), actions::requestVehicleCreation));
        panel.add(createActionButton("Nuevo Proveedor", "proveedores", new Color(241, 196, 15), actions::requestSupplierCreation));
        panel.add(createActionButton("Registrar Movimiento", "movimientos", new Color(155, 89, 182), actions::requestMovementCreation));
        panel.add(createActionButton("Nueva Factura", "facturas", new Color(230, 126, 34), actions::requestInvoiceCreation));

        return panel;
    }

    private JButton createActionButton(String text, String targetCard, Color color, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
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
        final long inicio = System.currentTimeMillis();
        SwingWorker<DashboardMetrics, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardMetrics doInBackground() throws Exception {
                List<Product> productos = productServices.getAllProducts();
                List<Vehicle> vehiculos = vehicleServices.getAllVehicles();
                List<Movement> movimientos = movementServices.getAllMovements();
                List<Factura> facturas = facturaServices.getAllFacturas();
                return new DashboardMetrics(
                    productos.size(),
                    vehiculos.size(),
                    countTodayMovements(movimientos),
                    facturas.size()
                );
            }

            @Override
            protected void done() {
                try {
                    DashboardMetrics metrics = get();
                    lblTotalProductos.setText(String.valueOf(metrics.totalProductos));
                    lblTotalVehiculos.setText(String.valueOf(metrics.totalVehiculos));
                    lblMovimientosHoy.setText(String.valueOf(metrics.movimientosHoy));
                    lblTotalFacturas.setText(String.valueOf(metrics.totalFacturas));
                    logger.accept(String.format("Dashboard actualizado (%s)", origin));
                } catch (ExecutionException ex) {
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

        // Registrar worker para cancelación antes de ejecutar
        loadManager.registerWorker(worker);
        worker.execute();
    }

    private void handleError(Throwable error) {
        String mensaje = error instanceof DatabaseException
            ? error.getMessage()
            : "Error inesperado en dashboard: " + error.getMessage();
        JOptionPane.showMessageDialog(this,
            "Error al cargar estadísticas: " + mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE);
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
