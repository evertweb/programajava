package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Product;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;

/**
 * ProductFormDialog
 * Modal dialog for creating/editing products.
 * Implements Policy 3.3 (Error Prevention) & 3.2 (Visual Feedback).
 */
public class ProductFormDialog extends JDialog {

    private final FTextField nameField;
    private final FTextField priceField;
    private final FTextField unitField;
    private final FTextField descField;
    private final FButton saveButton;
    private final FButton cancelButton;

    private final CatalogServiceAdapter service;
    private final Runnable onSuccess;

    public ProductFormDialog(Frame owner, CatalogServiceAdapter service, Runnable onSuccess) {
        super(owner, "Nuevo Producto", true);
        this.service = service;
        this.onSuccess = onSuccess;

        setLayout(new BorderLayout());
        setSize(450, 500);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // Content Panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Crear Producto");
        title.setFont(ThemeConstants.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Fields
        nameField = new FTextField("Nombre del Producto");
        content.add(nameField);
        content.add(Box.createVerticalStrut(15));

        priceField = new FTextField("Precio Unitario");
        content.add(priceField);
        content.add(Box.createVerticalStrut(15));

        unitField = new FTextField("Unidad de Medida (ej. GALON)");
        content.add(unitField);
        content.add(Box.createVerticalStrut(15));

        descField = new FTextField("Descripción");
        content.add(descField);
        content.add(Box.createVerticalStrut(25));

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.BORDER_COLOR));

        cancelButton = new FButton("Cancelar", FButton.Variant.SECONDARY);
        cancelButton.addActionListener(e -> dispose());

        saveButton = new FButton("Guardar Producto", FButton.Variant.PRIMARY);
        saveButton.setEnabled(false); // Disabled by default
        saveButton.addActionListener(e -> saveProduct());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Validation Listeners
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

        nameField.addDocumentListener(validationListener);
        priceField.addDocumentListener(validationListener);
        unitField.addDocumentListener(validationListener);
    }

    private void validateForm() {
        boolean isValid = true;

        // Name Validation
        if (nameField.getText().trim().isEmpty()) {
            isValid = false;
        }

        // Price Validation
        try {
            String priceText = priceField.getText().trim();
            if (priceText.isEmpty()) {
                isValid = false;
            } else {
                double price = Double.parseDouble(priceText);
                if (price < 0) {
                    priceField.setError("El precio no puede ser negativo");
                    isValid = false;
                } else {
                    priceField.clearError();
                }
            }
        } catch (NumberFormatException e) {
            if (!priceField.getText().trim().isEmpty()) {
                priceField.setError("Precio inválido");
            }
            isValid = false;
        }

        // Unit Validation
        if (unitField.getText().trim().isEmpty()) {
            isValid = false;
        }

        saveButton.setEnabled(isValid);
    }

    private void saveProduct() {
        // Disable inputs during save
        setInputsEnabled(false);
        saveButton.setText("Guardando...");

        Product product = new Product();
        product.setName(nameField.getText().trim());
        product.setUnitPrice(new BigDecimal(priceField.getText().trim()));
        product.setMeasurementUnit(unitField.getText().trim());
        product.setDescription(descField.getText().trim());
        product.setIsActive(true);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        service.createProduct(product);
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (result) -> {
                    NotificationManager.show((JFrame) getOwner(), "Producto creado exitosamente",
                            NotificationManager.Type.SUCCESS);
                    dispose();
                    if (onSuccess != null)
                        onSuccess.run();
                },
                (error) -> {
                    setInputsEnabled(true);
                    saveButton.setText("Guardar Producto");
                    NotificationManager.show((JFrame) getOwner(), "Error al guardar: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                });
    }

    private void setInputsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        priceField.setEnabled(enabled);
        unitField.setEnabled(enabled);
        descField.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }
}
