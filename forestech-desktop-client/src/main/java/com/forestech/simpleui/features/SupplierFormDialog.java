package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Supplier;
import com.forestech.simpleui.service.SupplierServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SupplierFormDialog extends JDialog {

    private final FTextField nameField;
    private final FTextField nitField;
    private final FTextField phoneField;
    private final FTextField emailField;
    private final FTextField addressField;
    private final FButton saveButton;
    private final FButton cancelButton;

    private final SupplierServiceAdapter service;
    private final Runnable onSuccess;
    private final Supplier supplierToEdit;

    public SupplierFormDialog(Frame owner, SupplierServiceAdapter service, Runnable onSuccess) {
        this(owner, service, null, onSuccess);
    }

    public SupplierFormDialog(Frame owner, SupplierServiceAdapter service, Supplier supplierToEdit, Runnable onSuccess) {
        super(owner, supplierToEdit == null ? "Nuevo Proveedor" : "Editar Proveedor", true);
        this.service = service;
        this.onSuccess = onSuccess;
        this.supplierToEdit = supplierToEdit;

        setLayout(new BorderLayout());
        setSize(450, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // Content Panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel(supplierToEdit == null ? "Registrar Proveedor" : "Editar Proveedor");
        title.setFont(ThemeConstants.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Fields
        nameField = new FTextField("Nombre / Razón Social");
        content.add(nameField);
        content.add(Box.createVerticalStrut(15));

        nitField = new FTextField("NIT / RUC");
        content.add(nitField);
        content.add(Box.createVerticalStrut(15));

        phoneField = new FTextField("Teléfono");
        content.add(phoneField);
        content.add(Box.createVerticalStrut(15));

        emailField = new FTextField("Email");
        content.add(emailField);
        content.add(Box.createVerticalStrut(15));

        addressField = new FTextField("Dirección");
        content.add(addressField);
        content.add(Box.createVerticalStrut(25));

        add(content, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.BORDER_COLOR));

        cancelButton = new FButton("Cancelar", FButton.Variant.SECONDARY);
        cancelButton.addActionListener(e -> dispose());

        saveButton = new FButton("Guardar", FButton.Variant.PRIMARY);
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveSupplier());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Validation Listeners
        DocumentListener validationListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };

        nameField.addDocumentListener(validationListener);
        nitField.addDocumentListener(validationListener);

        if (supplierToEdit != null) {
            fillForm();
        }
    }

    private void validateForm() {
        boolean isValid = true;
        if (nameField.getText().trim().isEmpty()) isValid = false;
        if (nitField.getText().trim().isEmpty()) isValid = false;
        saveButton.setEnabled(isValid);
    }

    private void fillForm() {
        nameField.setText(supplierToEdit.getName());
        nitField.setText(supplierToEdit.getNit());
        phoneField.setText(supplierToEdit.getTelephone());
        emailField.setText(supplierToEdit.getEmail());
        addressField.setText(supplierToEdit.getAddress());
        validateForm();
    }

    private void saveSupplier() {
        setInputsEnabled(false);
        saveButton.setText("Guardando...");

        Supplier supplier = supplierToEdit != null ? supplierToEdit : new Supplier();
        supplier.setName(nameField.getText().trim());
        supplier.setNit(nitField.getText().trim());
        supplier.setTelephone(phoneField.getText().trim());
        supplier.setEmail(emailField.getText().trim());
        supplier.setAddress(addressField.getText().trim());

        AsyncServiceTask.execute(
                () -> {
                    try {
                        if (supplierToEdit != null) {
                            service.updateSupplier(supplier.getId(), supplier);
                        } else {
                            service.createSupplier(supplier);
                        }
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (result) -> {
                    NotificationManager.show((JFrame) getOwner(), 
                        supplierToEdit != null ? "Proveedor actualizado" : "Proveedor registrado",
                        NotificationManager.Type.SUCCESS);
                    dispose();
                    if (onSuccess != null) onSuccess.run();
                },
                (error) -> {
                    setInputsEnabled(true);
                    saveButton.setText("Guardar");
                    NotificationManager.show((JFrame) getOwner(), "Error: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                });
    }

    private void setInputsEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        nitField.setEnabled(enabled);
        phoneField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        addressField.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }
}
