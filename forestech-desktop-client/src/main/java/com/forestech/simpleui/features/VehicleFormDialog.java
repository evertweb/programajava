package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Vehicle;
import com.forestech.simpleui.service.FleetServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.Year;

/**
 * VehicleFormDialog
 * Modal dialog for creating/editing vehicles.
 */
public class VehicleFormDialog extends JDialog {

    private final FTextField placaField;
    private final FTextField marcaField;
    private final FTextField modeloField;
    private final FTextField anioField;
    private final JComboBox<String> categoryCombo;
    private final FButton saveButton;
    private final FButton cancelButton;

    private final FleetServiceAdapter service;
    private final Runnable onSuccess;
    private final Vehicle vehicleToEdit;

    public VehicleFormDialog(Frame owner, FleetServiceAdapter service, Runnable onSuccess) {
        this(owner, service, null, onSuccess);
    }

    public VehicleFormDialog(Frame owner, FleetServiceAdapter service, Vehicle vehicleToEdit, Runnable onSuccess) {
        super(owner, vehicleToEdit == null ? "Nuevo Vehículo" : "Editar Vehículo", true);
        this.service = service;
        this.onSuccess = onSuccess;
        this.vehicleToEdit = vehicleToEdit;

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
        JLabel title = new JLabel(vehicleToEdit == null ? "Registrar Vehículo" : "Editar Vehículo");
        title.setFont(ThemeConstants.FONT_H2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Fields
        placaField = new FTextField("Placa (ej. ABC-1234)");
        content.add(placaField);
        content.add(Box.createVerticalStrut(15));

        marcaField = new FTextField("Marca");
        content.add(marcaField);
        content.add(Box.createVerticalStrut(15));

        modeloField = new FTextField("Modelo");
        content.add(modeloField);
        content.add(Box.createVerticalStrut(15));

        anioField = new FTextField("Año");
        content.add(anioField);
        content.add(Box.createVerticalStrut(15));

        // Category Combo
        JLabel catLabel = new JLabel("Categoría");
        catLabel.setFont(ThemeConstants.FONT_SMALL);
        catLabel.setForeground(ThemeConstants.TEXT_MUTED);
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(catLabel);
        content.add(Box.createVerticalStrut(5));

        String[] categories = { "CAMION", "TANQUERO", "CAMIONETA", "AUTOMOVIL", "MOTO", "MAQUINARIA" };
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setEditable(true); // Allow custom categories
        categoryCombo.setFont(ThemeConstants.FONT_REGULAR);
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        content.add(categoryCombo);
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
        saveButton.addActionListener(e -> saveVehicle());

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

        placaField.addDocumentListener(validationListener);
        marcaField.addDocumentListener(validationListener);
        modeloField.addDocumentListener(validationListener);
        anioField.addDocumentListener(validationListener);

        if (vehicleToEdit != null) {
            fillForm();
        }
    }

    private void fillForm() {
        placaField.setText(vehicleToEdit.getPlaca());
        marcaField.setText(vehicleToEdit.getMarca());
        modeloField.setText(vehicleToEdit.getModelo());
        anioField.setText(String.valueOf(vehicleToEdit.getAnio()));
        categoryCombo.setSelectedItem(vehicleToEdit.getCategory());
        validateForm();
    }

    private void validateForm() {
        boolean isValid = true;

        if (placaField.getText().trim().isEmpty())
            isValid = false;
        if (marcaField.getText().trim().isEmpty())
            isValid = false;
        if (modeloField.getText().trim().isEmpty())
            isValid = false;

        // Year Validation
        try {
            String yearText = anioField.getText().trim();
            if (yearText.isEmpty()) {
                isValid = false;
            } else {
                int year = Integer.parseInt(yearText);
                int currentYear = Year.now().getValue();
                if (year < 1900 || year > currentYear + 1) {
                    anioField.setError("Año inválido");
                    isValid = false;
                } else {
                    anioField.clearError();
                }
            }
        } catch (NumberFormatException e) {
            if (!anioField.getText().trim().isEmpty()) {
                anioField.setError("Debe ser un número");
            }
            isValid = false;
        }

        saveButton.setEnabled(isValid);
    }

    private void saveVehicle() {
        setInputsEnabled(false);
        saveButton.setText("Guardando...");

        Vehicle vehicle = vehicleToEdit != null ? vehicleToEdit : new Vehicle();
        vehicle.setPlaca(placaField.getText().trim().toUpperCase());
        vehicle.setMarca(marcaField.getText().trim());
        vehicle.setModelo(modeloField.getText().trim());
        vehicle.setAnio(Integer.parseInt(anioField.getText().trim()));
        vehicle.setCategory((String) categoryCombo.getSelectedItem());
        vehicle.setIsActive(true);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        if (vehicleToEdit != null) {
                            service.updateVehicle(vehicle.getId(), vehicle);
                        } else {
                            service.createVehicle(vehicle);
                        }
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (result) -> {
                    NotificationManager.show((JFrame) getOwner(), 
                        vehicleToEdit != null ? "Vehículo actualizado" : "Vehículo registrado",
                        NotificationManager.Type.SUCCESS);
                    dispose();
                    if (onSuccess != null)
                        onSuccess.run();
                },
                (error) -> {
                    setInputsEnabled(true);
                    saveButton.setText("Guardar");
                    NotificationManager.show((JFrame) getOwner(), "Error al guardar: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                });
    }

    private void setInputsEnabled(boolean enabled) {
        placaField.setEnabled(enabled);
        marcaField.setEnabled(enabled);
        modeloField.setEnabled(enabled);
        anioField.setEnabled(enabled);
        categoryCombo.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }
}
