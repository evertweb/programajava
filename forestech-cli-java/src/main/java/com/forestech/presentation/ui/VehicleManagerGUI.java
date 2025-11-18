package com.forestech.presentation.ui;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Vehicle;
import com.forestech.business.services.ProductServices;
import com.forestech.business.services.ServiceFactory;
import com.forestech.business.services.VehicleServices;
import com.forestech.presentation.ui.core.ServiceFactoryProvider;
import com.forestech.presentation.ui.vehicles.VehicleDialogForm;
import com.forestech.presentation.ui.utils.ColorScheme;
import com.forestech.presentation.ui.utils.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.10: VehicleManagerGUI - CRUD de Vehículos con validación de FK
 *
 * Demuestra integración completa con VehicleServices y validación de
 * Foreign Key (fuel_product_id → oil_products.id).
 */
public class VehicleManagerGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private final VehicleServices vehicleServices;
    private final ProductServices productServices;
    
    public VehicleManagerGUI(VehicleServices vehicleServices, ProductServices productServices) {
        this.vehicleServices = vehicleServices;
        this.productServices = productServices;

        setTitle("Gestor de Vehículos - Forestech");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(ColorScheme.BACKGROUND_LIGHT);

        crearTabla();
        crearPanelBotones();
        cargarVehiculos();

        setVisible(true);
    }

    private void crearTabla() {
        String[] columnas = {"ID", "Nombre", "Categoría", "Capacidad (L)", "Combustible", "Horómetro"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        UIUtils.styleTable(tabla);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    comp.setBackground(ColorScheme.TABLE_SELECTION_BG);
                    comp.setForeground(ColorScheme.TABLE_SELECTION_FG);
                } else {
                    comp.setBackground(row % 2 == 0
                        ? ColorScheme.TABLE_ROW_PRIMARY
                        : ColorScheme.TABLE_ROW_STRIPE);
                    comp.setForeground(ColorScheme.FOREGROUND_PRIMARY);
                }
                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND_PANEL);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Vehículos Registrados"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(ColorScheme.BACKGROUND_PANEL);

        JButton btnAgregar = new JButton("Agregar Vehículo");
        styleFilledButton(btnAgregar, ColorScheme.BUTTON_PRIMARY_BG);
        btnAgregar.addActionListener(e -> agregarVehiculo());
        panel.add(btnAgregar);
        JButton btnEditar = new JButton("Editar Seleccionado");
        styleFilledButton(btnEditar, ColorScheme.BUTTON_WARNING_BG);
        btnEditar.addActionListener(e -> editarVehiculo());
        panel.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        styleFilledButton(btnEliminar, ColorScheme.BUTTON_DANGER_BG);
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        panel.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        styleSecondaryButton(btnRefrescar);
        btnRefrescar.addActionListener(e -> cargarVehiculos());
        panel.add(btnRefrescar);

        add(panel, BorderLayout.SOUTH);
    }

    private void cargarVehiculos() {
        try {
            List<Vehicle> vehiculos = vehicleServices.getAllVehicles();
            modeloTabla.setRowCount(0);

            for (Vehicle v : vehiculos) {
                modeloTabla.addRow(new Object[]{
                    v.getId(),
                    v.getName(),
                    v.getCategory(),
                    v.getCapacity(),
                    v.getFuelProductId() != null ? v.getFuelProductId() : "N/A",
                    v.hasHorometer() ? "Sí" : "No"
                });
            }

            System.out.println("✅ Vehículos cargados: " + vehiculos.size());

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar vehículos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarVehiculo() {
        VehicleDialogForm dialogo = new VehicleDialogForm(this, true, vehicleServices, productServices);
        if (dialogo.isGuardadoExitoso()) {
            cargarVehiculos();
        }
    }

    private void editarVehiculo() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo para editar",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabla.getValueAt(fila, 0);

        try {
            Vehicle vehiculo = vehicleServices.getVehicleById(id);
            if (vehiculo != null) {
                VehicleDialogForm dialogo = new VehicleDialogForm(this, true, vehiculo, vehicleServices, productServices);
                if (dialogo.isGuardadoExitoso()) {
                    cargarVehiculos();
                }
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarVehiculo() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehículo para eliminar",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar vehículo " + nombre + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                vehicleServices.deleteVehicle(id);
                JOptionPane.showMessageDialog(this, "Vehículo eliminado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarVehiculos();
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleFilledButton(JButton button, Color background) {
        button.setBackground(background);
        button.setForeground(ColorScheme.TEXT_ON_COLOR);
        button.setFocusPainted(false);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(ColorScheme.BUTTON_SECONDARY_BG);
        button.setForeground(ColorScheme.BUTTON_SECONDARY_FG);
        button.setFocusPainted(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ServiceFactory factory = ServiceFactoryProvider.getFactory();
            new VehicleManagerGUI(factory.getVehicleServices(), factory.getProductServices());
        });

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.10: VehicleManagerGUI");
        System.out.println("CRUD completo con validación de FK (fuel_product_id)");
        System.out.println("=".repeat(60));
    }
}
