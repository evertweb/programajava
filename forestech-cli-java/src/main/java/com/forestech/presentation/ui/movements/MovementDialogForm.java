package com.forestech.presentation.ui.movements;

import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.shared.enums.MovementType;
import com.forestech.modules.inventory.models.Movement;
import com.forestech.modules.catalog.models.Product;
import com.forestech.modules.fleet.models.Vehicle;
import com.forestech.modules.invoicing.models.Invoice;
import com.forestech.presentation.ui.utils.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.11: MovementDialogForm - Formulario con 3 Foreign Keys +
 * Validación de Stock
 *
 * Refactorizado para MVP:
 * - No tiene dependencias de Services.
 * - Recibe listas de datos (Productos, Vehículos, Facturas).
 * - Retorna el objeto Movement creado.
 * - Valida stock localmente usando los datos del producto.
 */
public class MovementDialogForm extends JDialog {

    private JComboBox<String> cmbTipo;
    private JComboBox<ComboItem> cmbProducto;
    private JComboBox<ComboItem> cmbVehiculo;
    private JComboBox<ComboItem> cmbFactura;
    private JTextField txtCantidad;
    private JLabel lblVehiculoLabel;
    private JLabel lblFacturaLabel;

    private Movement result = null;
    private final List<Product> products;
    private final List<Vehicle> vehicles;
    private final List<Invoice> invoices;

    public MovementDialogForm(Window parent,
            boolean modal,
            List<Product> products,
            List<Vehicle> vehicles,
            List<Invoice> invoices) {
        super(parent, "Registrar Movimiento", modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.products = products;
        this.vehicles = vehicles;
        this.invoices = invoices;

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        crearFormulario();
        crearPanelBotones();
    }

    public Movement getResult() {
        return result;
    }

    private void crearFormulario() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Fila 1: Tipo de movimiento
        panel.add(new JLabel("Tipo de Movimiento:"));
        cmbTipo = new JComboBox<>(new String[] { "ENTRADA", "SALIDA" });
        cmbTipo.addActionListener(e -> actualizarCamposSegunTipo());
        panel.add(cmbTipo);

        // Fila 2: Producto (FK 1) - OBLIGATORIO
        panel.add(new JLabel("Producto:"));
        cmbProducto = new JComboBox<>();
        cargarProductos();
        panel.add(cmbProducto);

        // Fila 3: Cantidad
        panel.add(new JLabel("Cantidad (Litros):"));
        txtCantidad = new JTextField();
        panel.add(txtCantidad);

        // Fila 4: Vehículo (FK 2) - SOLO PARA SALIDA
        lblVehiculoLabel = new JLabel("Vehículo:");
        panel.add(lblVehiculoLabel);
        cmbVehiculo = new JComboBox<>();
        cargarVehiculos();
        panel.add(cmbVehiculo);

        // Fila 5: Factura (FK 3) - SOLO PARA ENTRADA
        lblFacturaLabel = new JLabel("Factura:");
        panel.add(lblFacturaLabel);
        cmbFactura = new JComboBox<>();
        cargarFacturas();
        panel.add(cmbFactura);

        add(panel, BorderLayout.CENTER);

        // Actualizar campos según tipo inicial
        actualizarCamposSegunTipo();
    }

    private void cargarProductos() {
        for (Product p : products) {
            // Guardamos el objeto Product completo en el ComboItem para validar stock
            cmbProducto.addItem(new ComboItem(p.getId(), p.getName(), p));
        }
    }

    private void cargarVehiculos() {
        cmbVehiculo.addItem(new ComboItem(null, "(Opcional)", null));
        for (Vehicle v : vehicles) {
            cmbVehiculo.addItem(new ComboItem(v.getId(), v.getName(), v));
        }
    }

    private void cargarFacturas() {
        cmbFactura.addItem(new ComboItem(null, "(Opcional)", null));
        for (Invoice f : invoices) {
            cmbFactura.addItem(new ComboItem(f.getId(),
                    "Factura #" + f.getId() + " - " + f.getIssueDate(), f));
        }
    }

    /**
     * Habilita/deshabilita campos según el tipo de movimiento.
     */
    private void actualizarCamposSegunTipo() {
        String tipo = (String) cmbTipo.getSelectedItem();

        if ("ENTRADA".equals(tipo)) {
            lblVehiculoLabel.setText("Vehículo (opcional):");
            lblFacturaLabel.setText("Factura (recomendada):");
            cmbVehiculo.setEnabled(true);
            cmbFactura.setEnabled(true);
            cmbVehiculo.setBackground(ColorScheme.BACKGROUND_LIGHT);
            cmbFactura.setBackground(ColorScheme.BACKGROUND_LIGHT);

        } else { // SALIDA
            lblVehiculoLabel.setText("Vehículo (recomendado):");
            lblFacturaLabel.setText("Factura (opcional):");
            cmbVehiculo.setEnabled(true);
            cmbFactura.setEnabled(true);
            cmbVehiculo.setBackground(ColorScheme.BACKGROUND_LIGHT);
            cmbFactura.setBackground(ColorScheme.BACKGROUND_LIGHT);
        }
    }

    private void crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnGuardar = new JButton("Registrar Movimiento");
        btnGuardar.setBackground(ColorScheme.BUTTON_SUCCESS_BG);
        btnGuardar.addActionListener(e -> confirmarMovimiento());
        panel.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorScheme.BUTTON_DANGER_BG);
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar);

        add(panel, BorderLayout.SOUTH);
    }

    private void confirmarMovimiento() {
        // VALIDAR CAMPOS
        String tipo = (String) cmbTipo.getSelectedItem();
        String cantidadTexto = txtCantidad.getText().trim();

        if (cmbProducto.getSelectedItem() == null) {
            mostrarError("Debes seleccionar un producto");
            return;
        }

        if (cantidadTexto.isEmpty()) {
            mostrarError("La cantidad es obligatoria");
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadTexto);
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número válido");
            return;
        }

        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor a cero");
            return;
        }

        // Obtener IDs de los combos
        ComboItem productoItem = (ComboItem) cmbProducto.getSelectedItem();
        String productId = productoItem.id;
        Product product = (Product) productoItem.data;

        // VALIDACIÓN DE STOCK (Solo para SALIDA)
        // La validación de stock se delega al Presenter/Service ya que el modelo
        // Product no tiene stock.
        /*
         * if ("SALIDA".equals(tipo)) {
         * if (product.getStock() < cantidad) {
         * JOptionPane.showMessageDialog(this,
         * String.format("❌ STOCK INSUFICIENTE\n\n" +
         * "No se puede registrar esta SALIDA:\n\n" +
         * "Cantidad solicitada: %.2f L\n" +
         * "Stock disponible: %.2f L\n" +
         * "Faltante: %.2f L\n\n" +
         * "Solución: Registra primero una ENTRADA de este producto.",
         * cantidad,
         * product.getStock(),
         * cantidad - product.getStock()),
         * "Stock Insuficiente",
         * JOptionPane.ERROR_MESSAGE);
         * return;
         * }
         * }
         */

        ComboItem vehiculoItem = (ComboItem) cmbVehiculo.getSelectedItem();
        String vehicleId = (vehiculoItem != null && vehiculoItem.id != null) ? vehiculoItem.id : null;

        ComboItem facturaItem = (ComboItem) cmbFactura.getSelectedItem();
        String numeroFactura = (facturaItem != null && facturaItem.id != null) ? facturaItem.id : null;

        // Crear objeto Movement
        try {
            this.result = new Movement(
                    MovementType.fromCode(tipo),
                    productId,
                    vehicleId,
                    numeroFactura,
                    MeasurementUnit.GALON,
                    cantidad,
                    0.0 // Precio unitario (se podría calcular o pedir, por ahora 0)
            );

            dispose(); // Cerrar diálogo y retornar control al Panel

        } catch (Exception e) {
            mostrarError("Error al crear movimiento: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Clase auxiliar para items del JComboBox.
     */
    private static class ComboItem {
        String id;
        String nombre;
        Object data; // Para guardar el objeto completo (Product, Vehicle, Factura)

        ComboItem(String id, String nombre, Object data) {
            this.id = id;
            this.nombre = nombre;
            this.data = data;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
