package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Factura;
import com.forestech.simpleui.model.Movement;
import com.forestech.simpleui.model.Product;
import com.forestech.simpleui.model.Vehicle;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.service.FleetServiceAdapter;
import com.forestech.simpleui.service.InventoryServiceAdapter;
import com.forestech.simpleui.service.InvoicingServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DashboardPanel
 * Displays high-level metrics and recent activity.
 */
public class DashboardPanel extends JPanel {

    private final CatalogServiceAdapter catalogService;
    private final FleetServiceAdapter fleetService;
    private final InventoryServiceAdapter inventoryService;
    private final InvoicingServiceAdapter invoicingService;

    private JLabel totalProductsLabel;
    private JLabel totalVehiclesLabel;
    private JLabel totalMovementsLabel;
    private JLabel totalInvoicesLabel;

    private boolean loaded = false;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        catalogService = new CatalogServiceAdapter();
        fleetService = new FleetServiceAdapter();
        inventoryService = new InventoryServiceAdapter();
        invoicingService = new InvoicingServiceAdapter();

        initUI();
    }

    private void initUI() {
        // Title
        JLabel title = new JLabel("Dashboard General");
        title.setFont(ThemeConstants.FONT_H1);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Cards Container
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 150));

        // 1. Products Card
        totalProductsLabel = createMetricCard(cardsPanel, "Productos", "items", ThemeConstants.PRIMARY_COLOR);

        // 2. Vehicles Card
        totalVehiclesLabel = createMetricCard(cardsPanel, "Vehículos", "unidades", new Color(46, 204, 113)); // Green

        // 3. Movements Card
        totalMovementsLabel = createMetricCard(cardsPanel, "Movimientos", "registros", new Color(52, 152, 219)); // Blue

        // 4. Invoices Card
        totalInvoicesLabel = createMetricCard(cardsPanel, "Facturas", "emitidas", new Color(155, 89, 182)); // Purple

        // Main Content Area (Placeholder for charts or recent lists)
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        mainContent.add(cardsPanel, BorderLayout.NORTH);

        // Welcome Message / Instructions
        FCard infoCard = new FCard();
        infoCard.setLayout(new BorderLayout());
        infoCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeTitle = new JLabel("Bienvenido a Forestech Oil Management");
        welcomeTitle.setFont(ThemeConstants.FONT_H2);

        JTextArea welcomeText = new JTextArea(
                "Utilice el menú lateral para navegar entre los diferentes módulos.\n\n" +
                        "• Catálogo: Gestione sus productos y precios.\n" +
                        "• Flota: Administre los vehículos de la empresa.\n" +
                        "• Movimientos: Registre entradas y salidas de inventario.\n" +
                        "• Facturación: Emita facturas y gestione clientes.");
        welcomeText.setFont(ThemeConstants.FONT_REGULAR);
        welcomeText.setForeground(ThemeConstants.TEXT_MUTED);
        welcomeText.setEditable(false);
        welcomeText.setOpaque(false);
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);

        infoCard.add(welcomeTitle, BorderLayout.NORTH);
        infoCard.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        infoCard.add(welcomeText, BorderLayout.SOUTH);

        mainContent.add(infoCard, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JLabel createMetricCard(JPanel container, String title, String subtitle, Color accentColor) {
        FCard card = new FCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeConstants.FONT_REGULAR);
        titleLabel.setForeground(ThemeConstants.TEXT_MUTED);

        JLabel valueLabel = new JLabel("-");
        valueLabel.setFont(ThemeConstants.FONT_H1);
        valueLabel.setForeground(ThemeConstants.TEXT_COLOR);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(ThemeConstants.FONT_SMALL);
        subLabel.setForeground(ThemeConstants.TEXT_MUTED);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(subLabel);

        card.add(content, BorderLayout.CENTER);
        container.add(card);

        return valueLabel;
    }

    public void onShow() {
        if (!loaded) {
            loadData();
        }
    }

    private void loadData() {
        // Load each service independently for graceful degradation
        loadProducts();
        loadVehicles();
        loadMovements();
        loadInvoices();
    }

    private void loadProducts() {
        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Product> products = catalogService.getAllProducts();
                        return products.size();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (count) -> {
                    totalProductsLabel.setText(String.valueOf(count));
                },
                (error) -> {
                    totalProductsLabel.setText("N/A");
                    System.err.println("[Dashboard] Error al cargar productos: " + error.getMessage());
                });
    }

    private void loadVehicles() {
        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Vehicle> vehicles = fleetService.getAllVehicles();
                        return vehicles.size();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (count) -> {
                    totalVehiclesLabel.setText(String.valueOf(count));
                },
                (error) -> {
                    totalVehiclesLabel.setText("N/A");
                    System.err.println("[Dashboard] Error al cargar vehículos: " + error.getMessage());
                });
    }

    private void loadMovements() {
        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Movement> movements = inventoryService.getAllMovements();
                        return movements.size();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (count) -> {
                    totalMovementsLabel.setText(String.valueOf(count));
                },
                (error) -> {
                    totalMovementsLabel.setText("N/A");
                    System.err.println("[Dashboard] Error al cargar movimientos: " + error.getMessage());
                });
    }

    private void loadInvoices() {
        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Factura> invoices = invoicingService.getAllInvoices();
                        return invoices.size();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (count) -> {
                    totalInvoicesLabel.setText(String.valueOf(count));
                    loaded = true; // Mark as loaded after the last service
                },
                (error) -> {
                    totalInvoicesLabel.setText("N/A");
                    System.err.println("[Dashboard] Error al cargar facturas: " + error.getMessage());
                    loaded = true; // Mark as loaded even on error
                });
    }
}
