package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Product;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ProductPanel
 * Displays a list of products.
 * Implements Policy 5.1 (Lazy Load) via onShow() - to be called by MainFrame.
 */
public class ProductPanel extends JPanel {

    private final FTable table;
    private final CatalogServiceAdapter service;
    private boolean loaded = false;
    private List<Product> currentProducts;

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        service = new CatalogServiceAdapter();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Catálogo de Productos");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title, BorderLayout.WEST);

        FButton refreshBtn = new FButton("Refrescar", FButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadData());

        FButton createBtn = new FButton("Nuevo", FButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> openCreateDialog());

        FButton editBtn = new FButton("Editar", FButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> openEditDialog());

        FButton deleteBtn = new FButton("Eliminar", FButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteProduct());

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
        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Cargando productos...",
                NotificationManager.Type.INFO);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        return service.getAllProducts();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (products) -> {
                    updateTable(products);
                    loaded = true;
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Productos cargados",
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
            ProductFormDialog dialog = new ProductFormDialog((Frame) window, service, this::loadData);
            dialog.setVisible(true);
        }
    }

    private void openEditDialog() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Seleccione un producto para editar", NotificationManager.Type.WARNING);
            return;
        }
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            ProductFormDialog dialog = new ProductFormDialog((Frame) window, service, selected, this::loadData);
            dialog.setVisible(true);
        }
    }

    private void deleteProduct() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Seleccione un producto para eliminar", NotificationManager.Type.WARNING);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el producto " + selected.getName() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AsyncServiceTask.execute(
                    () -> {
                        try {
                            service.deleteProduct(selected.getId());
                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (result) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Producto eliminado",
                                NotificationManager.Type.SUCCESS);
                        loadData();
                    },
                    (error) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                                "Error al eliminar: " + error.getMessage(), NotificationManager.Type.ERROR);
                    });
        }
    }

    private Product getSelectedProduct() {
        int row = table.getSelectedRow();
        if (row == -1 || currentProducts == null || row >= currentProducts.size())
            return null;
        return currentProducts.get(row);
    }

    private void updateTable(List<Product> products) {
        this.currentProducts = products;
        String[] columns = { "ID", "Nombre", "Precio", "Unidad", "Activo" };
        Object[][] data = new Object[products.size()][5];

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i][0] = p.getId();
            data[i][1] = p.getName();
            data[i][2] = p.getUnitPrice();
            data[i][3] = p.getMeasurementUnit();
            data[i][4] = p.getIsActive() ? "Sí" : "No";
        }

        table.setModel(data, columns);
    }
}
