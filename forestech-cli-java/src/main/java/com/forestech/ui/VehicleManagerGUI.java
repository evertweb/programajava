package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Vehicle;
import com.forestech.services.VehicleServices;

import javax.swing.*;
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

    public VehicleManagerGUI() {
        setTitle("Gestor de Vehículos - Forestech");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

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
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Vehículos Registrados"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnAgregar = new JButton("Agregar Vehículo");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarVehiculo());
        panel.add(btnAgregar);

        JButton btnEditar = new JButton("Editar Seleccionado");
        btnEditar.setBackground(new Color(150, 150, 255));
        btnEditar.addActionListener(e -> editarVehiculo());
        panel.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        panel.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarVehiculos());
        panel.add(btnRefrescar);

        add(panel, BorderLayout.SOUTH);
    }

    private void cargarVehiculos() {
        try {
            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();
            modeloTabla.setRowCount(0);

            for (Vehicle v : vehiculos) {
                modeloTabla.addRow(new Object[]{
                    v.getId(),
                    v.getName(),
                    v.getCategory(),
                    v.getCapacity(),
                    v.getFuelProductId() != null ? v.getFuelProductId() : "N/A",
                    v.isHaveHorometer() ? "Sí" : "No"
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
        VehicleDialogForm dialogo = new VehicleDialogForm(this, true);
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
            Vehicle vehiculo = VehicleServices.getVehicleById(id);
            if (vehiculo != null) {
                VehicleDialogForm dialogo = new VehicleDialogForm(this, true, vehiculo);
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
                VehicleServices.deleteVehicle(id);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new VehicleManagerGUI());

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.10: VehicleManagerGUI");
        System.out.println("CRUD completo con validación de FK (fuel_product_id)");
        System.out.println("=".repeat(60));
    }
}
