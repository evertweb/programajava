package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Supplier;
import com.forestech.simpleui.service.SupplierServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SupplierPanel extends JPanel {

    private final FTable table;
    private final SupplierServiceAdapter service;
    private boolean loaded = false;
    private List<Supplier> currentSuppliers;

    public SupplierPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        service = new SupplierServiceAdapter();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Gestión de Proveedores");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title, BorderLayout.WEST);

        FButton refreshBtn = new FButton("Refrescar", FButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadData());

        FButton createBtn = new FButton("Nuevo", FButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> openCreateDialog());

        FButton editBtn = new FButton("Editar", FButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> openEditDialog());

        FButton deleteBtn = new FButton("Eliminar", FButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSupplier());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(refreshBtn);
        actions.add(createBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);

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
        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Cargando proveedores...",
                NotificationManager.Type.INFO);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        return service.getAllSuppliers();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (suppliers) -> {
                    updateTable(suppliers);
                    loaded = true;
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Proveedores cargados",
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
            SupplierFormDialog dialog = new SupplierFormDialog((Frame) window, service, this::loadData);
            dialog.setVisible(true);
        }
    }

    private void openEditDialog() {
        Supplier selected = getSelectedSupplier();
        if (selected == null) {
            NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Seleccione un proveedor para editar", NotificationManager.Type.WARNING);
            return;
        }
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            SupplierFormDialog dialog = new SupplierFormDialog((Frame) window, service, selected, this::loadData);
            dialog.setVisible(true);
        }
    }

    private void deleteSupplier() {
        Supplier selected = getSelectedSupplier();
        if (selected == null) {
            NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Seleccione un proveedor para eliminar", NotificationManager.Type.WARNING);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al proveedor " + selected.getName() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AsyncServiceTask.execute(
                    () -> {
                        try {
                            service.deleteSupplier(selected.getId());
                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (result) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Proveedor eliminado",
                                NotificationManager.Type.SUCCESS);
                        loadData();
                    },
                    (error) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                                "Error al eliminar: " + error.getMessage(), NotificationManager.Type.ERROR);
                    });
        }
    }

    private Supplier getSelectedSupplier() {
        int row = table.getSelectedRow();
        if (row == -1 || currentSuppliers == null || row >= currentSuppliers.size())
            return null;
        return currentSuppliers.get(row);
    }

    private void updateTable(List<Supplier> suppliers) {
        this.currentSuppliers = suppliers;
        String[] columns = { "Nombre", "NIT", "Teléfono", "Email", "Dirección" };
        Object[][] data = new Object[suppliers.size()][5];

        for (int i = 0; i < suppliers.size(); i++) {
            Supplier s = suppliers.get(i);
            data[i][0] = s.getName();
            data[i][1] = s.getNit();
            data[i][2] = s.getTelephone();
            data[i][3] = s.getEmail();
            data[i][4] = s.getAddress();
        }

        table.setModel(data, columns);
    }
}
