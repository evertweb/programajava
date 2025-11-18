package com.forestech.presentation.ui.vehicles;

import com.forestech.shared.enums.VehicleCategory;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Product;
import com.forestech.data.models.Vehicle;
import com.forestech.business.services.ProductServices;
import com.forestech.business.services.VehicleServices;
import com.forestech.presentation.ui.utils.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

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
    private final VehicleServices vehicleServices;
    private final ProductServices productServices;

    public VehicleDialogForm(JFrame parent,
                             boolean modal,
                             VehicleServices vehicleServices,
                             ProductServices productServices) {
        this(parent, modal, null, vehicleServices, productServices);
    }

    public VehicleDialogForm(JFrame parent,
                             boolean modal,
                             Vehicle vehiculoExistente,
                             VehicleServices vehicleServices,
                             ProductServices productServices) {
        super(parent, vehiculoExistente == null ? "Agregar Vehículo" : "Editar Vehículo", modal);
        this.vehiculoExistente = vehiculoExistente;
        this.vehicleServices = Objects.requireNonNull(vehicleServices, "vehicleServices");
        this.productServices = Objects.requireNonNull(productServices, "productServices");

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
            List<Product> productos = productServices.getAllProducts();
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
        btnGuardar.setBackground(ColorScheme.BUTTON_SUCCESS_BG);
        btnGuardar.addActionListener(e -> guardarVehiculo());
        panel.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorScheme.BUTTON_DANGER_BG);
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar);

        add(panel, BorderLayout.SOUTH);
    }

    private void cargarDatosExistentes() {
        txtNombre.setText(vehiculoExistente.getName());
        txtCategoria.setText(vehiculoExistente.getCategory() != null ? vehiculoExistente.getCategory().getCode() : "");
        txtCapacidad.setText(String.valueOf(vehiculoExistente.getCapacity()));
        chkTieneHorometro.setSelected(vehiculoExistente.hasHorometer());

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
            VehicleCategory vehicleCategory = VehicleCategory.fromCode(categoria);
            if (vehiculoExistente == null) {
                Vehicle nuevoVehiculo = new Vehicle(nombre, vehicleCategory, capacidad, fuelProductId, tieneHorometro);
                vehicleServices.insertVehicle(nuevoVehiculo);
                JOptionPane.showMessageDialog(this, "Vehículo agregado exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                vehiculoExistente.setName(nombre);
                vehiculoExistente.setCategory(vehicleCategory);
                vehiculoExistente.setCapacity(capacidad);
                vehiculoExistente.setFuelProductId(fuelProductId);
                vehiculoExistente.setHasHorometer(tieneHorometro);
                vehicleServices.updateVehicle(vehiculoExistente);
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
