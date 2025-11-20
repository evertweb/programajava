package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Vehicle;
import com.forestech.simpleui.service.FleetServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * FleetPanel
 * Displays a list of vehicles.
 */
public class FleetPanel extends JPanel {

    private final FTable table;
    private final FleetServiceAdapter service;
    private boolean loaded = false;

    public FleetPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        service = new FleetServiceAdapter();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Gestión de Flota");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title, BorderLayout.WEST);

        FButton refreshBtn = new FButton("Refrescar", FButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadData());

        FButton createBtn = new FButton("Nuevo Vehículo", FButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> openCreateDialog());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(refreshBtn);
        actions.add(createBtn);

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
        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Cargando flota...",
                NotificationManager.Type.INFO);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        return service.getAllVehicles();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (vehicles) -> {
                    updateTable(vehicles);
                    loaded = true;
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Flota cargada",
                            NotificationManager.Type.SUCCESS);
                },
                (error) -> {
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                            "Error: " + error.getMessage(), NotificationManager.Type.ERROR);
                    error.printStackTrace();
                });
    }

    private void openCreateDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            VehicleFormDialog dialog = new VehicleFormDialog((Frame) window, service, this::loadData);
            dialog.setVisible(true);
        }
    }

    private void updateTable(List<Vehicle> vehicles) {
        String[] columns = { "Placa", "Marca", "Modelo", "Año", "Categoría", "Activo" };
        Object[][] data = new Object[vehicles.size()][6];

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            data[i][0] = v.getPlaca();
            data[i][1] = v.getMarca();
            data[i][2] = v.getModelo();
            data[i][3] = v.getAnio();
            data[i][4] = v.getCategory();
            data[i][5] = (v.getIsActive() != null && v.getIsActive()) ? "Sí" : "No";
        }

        table.setModel(data, columns);
    }
}
