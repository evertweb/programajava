package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Movement;
import com.forestech.simpleui.model.Product;
import com.forestech.simpleui.model.Vehicle;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.service.FleetServiceAdapter;
import com.forestech.simpleui.service.InventoryServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * MovementFormDialog
 * Modal dialog for registering inventory movements (ENTRADA/SALIDA).
 */
public class MovementFormDialog extends JDialog {

    private final String type; // ENTRADA or SALIDA
    private final InventoryServiceAdapter inventoryService;
    private final CatalogServiceAdapter catalogService;
    private final FleetServiceAdapter fleetService;
    private final Runnable onSuccess;

    private JComboBox<Product> productCombo;
    private JComboBox<Vehicle> vehicleCombo;
    private FTextField quantityField;
    private FTextField priceField;
    private FTextField descField;
    private FButton saveButton;
    private FButton cancelButton;
    private JPanel vehiclePanel;

    public MovementFormDialog(Frame owner, String type,
            InventoryServiceAdapter inventoryService,
            CatalogServiceAdapter catalogService,
            FleetServiceAdapter fleetService,
            Runnable onSuccess) {
        super(owner, "Registrar " + type, true);
        this.type = type;
        this.inventoryService = inventoryService;
        this.catalogService = catalogService;
        this.fleetService = fleetService;
        this.onSuccess = onSuccess;

        setLayout(new BorderLayout());
        setSize(500, 650);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        initUI();
        loadMetadata();
    }

    private void initUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Registrar " + type);
        title.setFont(ThemeConstants.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Product Combo
        JLabel prodLabel = new JLabel("Producto");
        prodLabel.setFont(ThemeConstants.FONT_SMALL);
        prodLabel.setForeground(ThemeConstants.TEXT_MUTED);
        prodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(prodLabel);
        content.add(Box.createVerticalStrut(5));

        productCombo = new JComboBox<>();
        productCombo.setFont(ThemeConstants.FONT_REGULAR);
        productCombo.setBackground(Color.WHITE);
        productCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        productCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        // Custom Renderer for Product
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getName());
                }
                return this;
            }
        });
        content.add(productCombo);
        content.add(Box.createVerticalStrut(15));

        // Vehicle Combo (Only for SALIDA)
        vehiclePanel = new JPanel();
        vehiclePanel.setLayout(new BoxLayout(vehiclePanel, BoxLayout.Y_AXIS));
        vehiclePanel.setBackground(Color.WHITE);
        vehiclePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if ("SALIDA".equals(type)) {
            JLabel vehLabel = new JLabel("Vehículo (Opcional)");
            vehLabel.setFont(ThemeConstants.FONT_SMALL);
            vehLabel.setForeground(ThemeConstants.TEXT_MUTED);
            vehLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            vehiclePanel.add(vehLabel);
            vehiclePanel.add(Box.createVerticalStrut(5));

            vehicleCombo = new JComboBox<>();
            vehicleCombo.setFont(ThemeConstants.FONT_REGULAR);
            vehicleCombo.setBackground(Color.WHITE);
            vehicleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            vehicleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            // Custom Renderer for Vehicle
            vehicleCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Vehicle) {
                        Vehicle v = (Vehicle) value;
                        setText(v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo());
                    } else {
                        setText("Ninguno");
                    }
                    return this;
                }
            });
            vehiclePanel.add(vehicleCombo);
            vehiclePanel.add(Box.createVerticalStrut(15));
        }
        content.add(vehiclePanel);

        // Quantity
        quantityField = new FTextField("Cantidad");
        content.add(quantityField);
        content.add(Box.createVerticalStrut(15));

        // Unit Price
        priceField = new FTextField("Precio Unitario");
        content.add(priceField);
        content.add(Box.createVerticalStrut(15));

        // Description
        descField = new FTextField("Descripción / Motivo");
        content.add(descField);
        content.add(Box.createVerticalStrut(25));

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.BORDER_COLOR));

        cancelButton = new FButton("Cancelar", FButton.Variant.SECONDARY);
        cancelButton.addActionListener(e -> dispose());

        saveButton = new FButton("Guardar Movimiento", FButton.Variant.PRIMARY);
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveMovement());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Validation
        DocumentListener validationListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validateForm();
            }

            public void removeUpdate(DocumentEvent e) {
                validateForm();
            }

            public void changedUpdate(DocumentEvent e) {
                validateForm();
            }
        };
        quantityField.addDocumentListener(validationListener);
        priceField.addDocumentListener(validationListener);
        descField.addDocumentListener(validationListener);
    }

    private void loadMetadata() {
        saveButton.setEnabled(false);
        saveButton.setText("Cargando datos...");

        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Product> products = catalogService.getAllProducts();
                        List<Vehicle> vehicles = null;
                        if ("SALIDA".equals(type)) {
                            vehicles = fleetService.getAllVehicles();
                        }
                        return new Metadata(products, vehicles);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (data) -> {
                    // Populate Products
                    productCombo.removeAllItems();
                    for (Product p : data.products) {
                        productCombo.addItem(p);
                    }

                    // Populate Vehicles if SALIDA
                    if ("SALIDA".equals(type) && data.vehicles != null) {
                        vehicleCombo.removeAllItems();
                        vehicleCombo.addItem(null); // Option for no vehicle
                        for (Vehicle v : data.vehicles) {
                            vehicleCombo.addItem(v);
                        }
                    }

                    saveButton.setText("Guardar Movimiento");
                    validateForm();
                },
                (error) -> {
                    NotificationManager.show((JFrame) getOwner(), "Error cargando datos: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                    dispose();
                });
    }

    private void validateForm() {
        boolean isValid = true;

        if (productCombo.getSelectedItem() == null)
            isValid = false;

        try {
            BigDecimal qty = new BigDecimal(quantityField.getText().trim());
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                quantityField.setError("Debe ser mayor a 0");
                isValid = false;
            } else {
                quantityField.clearError();
            }
        } catch (NumberFormatException e) {
            if (!quantityField.getText().trim().isEmpty())
                quantityField.setError("Número inválido");
            isValid = false;
        }

        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                priceField.setError("No puede ser negativo");
                isValid = false;
            } else {
                priceField.clearError();
            }
        } catch (NumberFormatException e) {
            if (!priceField.getText().trim().isEmpty())
                priceField.setError("Número inválido");
            isValid = false;
        }

        if (descField.getText().trim().isEmpty())
            isValid = false;

        saveButton.setEnabled(isValid);
    }

    private void saveMovement() {
        setInputsEnabled(false);
        saveButton.setText("Guardando...");

        Movement m = new Movement();
        m.setMovementType(type);

        Product p = (Product) productCombo.getSelectedItem();
        if (p != null)
            m.setProductId(p.getId()); // Ideally we send ID

        if ("SALIDA".equals(type)) {
            Vehicle v = (Vehicle) vehicleCombo.getSelectedItem();
            if (v != null)
                m.setVehicleId(v.getId()); // Ideally we send ID
        }

        m.setQuantity(new BigDecimal(quantityField.getText().trim()));
        m.setUnitPrice(new BigDecimal(priceField.getText().trim()));
        m.setDescription(descField.getText().trim());

        AsyncServiceTask.execute(
                () -> {
                    try {
                        if ("ENTRADA".equals(type)) {
                            inventoryService.createEntrada(m);
                        } else {
                            inventoryService.createSalida(m);
                        }
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (result) -> {
                    NotificationManager.show((JFrame) getOwner(), "Movimiento registrado",
                            NotificationManager.Type.SUCCESS);
                    dispose();
                    if (onSuccess != null)
                        onSuccess.run();
                },
                (error) -> {
                    setInputsEnabled(true);
                    saveButton.setText("Guardar Movimiento");
                    NotificationManager.show((JFrame) getOwner(), "Error: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                });
    }

    private void setInputsEnabled(boolean enabled) {
        productCombo.setEnabled(enabled);
        if (vehicleCombo != null)
            vehicleCombo.setEnabled(enabled);
        quantityField.setEnabled(enabled);
        priceField.setEnabled(enabled);
        descField.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }

    // Helper class for loading data
    private static class Metadata {
        List<Product> products;
        List<Vehicle> vehicles;

        Metadata(List<Product> p, List<Vehicle> v) {
            this.products = p;
            this.vehicles = v;
        }
    }
}
