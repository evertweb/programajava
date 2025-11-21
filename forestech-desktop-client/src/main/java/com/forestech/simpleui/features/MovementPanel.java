package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Movement;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.service.FleetServiceAdapter;
import com.forestech.simpleui.service.InventoryServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * MovementPanel
 * Displays a list of inventory movements.
 */
public class MovementPanel extends JPanel {

    private final FTable table;
    private final InventoryServiceAdapter service;
    private final CatalogServiceAdapter catalogService;
    private final FleetServiceAdapter fleetService;
    private boolean loaded = false;

    public MovementPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        service = new InventoryServiceAdapter();
        catalogService = new CatalogServiceAdapter();
        fleetService = new FleetServiceAdapter();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Historial de Movimientos");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title, BorderLayout.WEST);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        FButton refreshBtn = new FButton("Refrescar", FButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadData());
        actions.add(refreshBtn);

        // Entrada button removed as per new workflow (Invoices -> Inventory)

        FButton salidaBtn = new FButton("Registrar Salida", FButton.Variant.DANGER);
        salidaBtn.addActionListener(e -> openCreateDialog("SALIDA"));
        actions.add(salidaBtn);

        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table
        table = new FTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        FCard card = new FCard();
        card.setLayout(new BorderLayout());
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void onShow() {
        if (!loaded) {
            loadData();
        }
    }

    private void loadData() {
        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Cargando movimientos...",
                NotificationManager.Type.INFO);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        return service.getAllMovements();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (movements) -> {
                    updateTable(movements);
                    loaded = true;
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Movimientos cargados",
                            NotificationManager.Type.SUCCESS);
                },
                (error) -> {
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                            "Error: " + error.getMessage(), NotificationManager.Type.ERROR);
                    error.printStackTrace();
                });
    }

    private void updateTable(List<Movement> movements) {
        String[] columns = { "Fecha", "Tipo", "Producto", "Vehículo", "Cantidad", "Precio U.", "Total", "Descripción" };
        Object[][] data = new Object[movements.size()][8];

        for (int i = 0; i < movements.size(); i++) {
            Movement m = movements.get(i);
            data[i][0] = m.getCreatedAt();
            data[i][1] = m.getMovementType();
            data[i][2] = m.getProductId(); // Ideally should be Product Name, but ID for now
            data[i][3] = m.getVehicleId() != null ? m.getVehicleId() : "-";
            data[i][4] = m.getQuantity();
            data[i][5] = m.getUnitPrice();
            data[i][6] = m.getSubtotal();
            data[i][7] = m.getDescription();
        }

        table.setModel(data, columns);
    }

    private void openCreateDialog(String type) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            MovementFormDialog dialog = new MovementFormDialog(
                    (Frame) window,
                    type,
                    service,
                    catalogService,
                    fleetService,
                    this::loadData);
            dialog.setVisible(true);
        }
    }
}
