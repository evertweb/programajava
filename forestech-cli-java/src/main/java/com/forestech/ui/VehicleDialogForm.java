package com.forestech.ui;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.10: VehicleDialogForm - Formulario Modal para Vehículos
 *
 * Demuestra validación de Foreign Key (fuel_product_id → oil_products.id)
 * mediante JComboBox poblado desde la base de datos.
 */
public class VehicleDialogForm extends JDialog {

    private JTextField txtNombre;
    private JTextField txtCategoria;
    private JTextField txtCapacidad;
    private JComboBox<ComboItem> cmbCombustible;
    private JCheckBox chkTieneHorometro;

    private Vehicle vehiculoExistente;
    private boolean guardadoExitoso = false;

    public VehicleDialogForm(JFrame parent, boolean modal) {
        this(parent, modal, null);
    }

    public VehicleDialogForm(JFrame parent, boolean modal, Vehicle vehiculoExistente) {
        super(parent, vehiculoExistente == null ? "Agregar Vehículo" : "Editar Vehículo", modal);
        this.vehiculoExistente = vehiculoExistente;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        crearFormulario();
        crearPanelBotones();

        if (vehiculoExistente != null) {
            cargarDatosExistentes();
        }

        setVisible(true);
    }

    private void crearFormulario() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        panel.add(new JLabel("Nombre del Vehículo:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        panel.add(new JLabel("Categoría:"));
        txtCategoria = new JTextField();
        panel.add(txtCategoria);

        panel.add(new JLabel("Capacidad (Litros):"));
        txtCapacidad = new JTextField();
        panel.add(txtCapacidad);

        panel.add(new JLabel("Tipo de Combustible:"));
        cmbCombustible = new JComboBox<>();
        cargarProductosCombustible();
        panel.add(cmbCombustible);

        panel.add(new JLabel("¿Tiene Horómetro?"));
        chkTieneHorometro = new JCheckBox();
        panel.add(chkTieneHorometro);

        add(panel, BorderLayout.CENTER);
    }

    private void cargarProductosCombustible() {
        try {
            List<Product> productos = new ProductServices().getAllProducts();
            cmbCombustible.addItem(new ComboItem(null, "(Sin asignar)"));
            for (Product p : productos) {
                cmbCombustible.addItem(new ComboItem(p.getId(), p.getName()));
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnGuardar = new JButton(vehiculoExistente == null ? "Agregar" : "Guardar");
        btnGuardar.setBackground(new Color(100, 200, 100));
        btnGuardar.addActionListener(e -> guardarVehiculo());
        panel.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(255, 150, 150));
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar);

        add(panel, BorderLayout.SOUTH);
    }

    private void cargarDatosExistentes() {
        txtNombre.setText(vehiculoExistente.getName());
        txtCategoria.setText(vehiculoExistente.getCategory() != null ? vehiculoExistente.getCategory().getCode() : "");
        txtCapacidad.setText(String.valueOf(vehiculoExistente.getCapacity()));
        chkTieneHorometro.setSelected(vehiculoExistente.isHaveHorometer());

        // Seleccionar el combustible correcto en el combo
        String fuelId = vehiculoExistente.getFuelProductId();
        for (int i = 0; i < cmbCombustible.getItemCount(); i++) {
            ComboItem item = cmbCombustible.getItemAt(i);
            if ((fuelId == null && item.id == null) || (fuelId != null && fuelId.equals(item.id))) {
                cmbCombustible.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardarVehiculo() {
        String nombre = txtNombre.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String capacidadTexto = txtCapacidad.getText().trim();

        if (nombre.isEmpty() || categoria.isEmpty() || capacidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double capacidad;
        try {
            capacidad = Double.parseDouble(capacidadTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La capacidad debe ser un número válido",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (capacidad <= 0) {
            JOptionPane.showMessageDialog(this, "La capacidad debe ser mayor a cero",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ComboItem itemSeleccionado = (ComboItem) cmbCombustible.getSelectedItem();
        String fuelProductId = itemSeleccionado != null ? itemSeleccionado.id : null;
        boolean tieneHorometro = chkTieneHorometro.isSelected();

        try {
            if (vehiculoExistente == null) {
                Vehicle nuevoVehiculo = new Vehicle(nombre, categoria, capacidad, fuelProductId, tieneHorometro);
                new VehicleServices().insertVehicle(nuevoVehiculo);
                JOptionPane.showMessageDialog(this, "Vehículo agregado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                vehiculoExistente.setName(nombre);
                vehiculoExistente.setCategory(categoria);
                vehiculoExistente.setCapacity(capacidad);
                vehiculoExistente.setFuelProductId(fuelProductId);
                vehiculoExistente.setHaveHorometer(tieneHorometro);
                new VehicleServices().updateVehicle(vehiculoExistente);
                JOptionPane.showMessageDialog(this, "Vehículo actualizado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            guardadoExitoso = true;
            dispose();

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    /**
     * Clase auxiliar para items del JComboBox.
     */
    private static class ComboItem {
        String id;
        String nombre;

        ComboItem(String id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
