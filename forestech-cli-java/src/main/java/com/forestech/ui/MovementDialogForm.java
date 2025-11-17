package com.forestech.ui;

import com.forestech.enums.MeasurementUnit;
import com.forestech.enums.MovementType;
import com.forestech.exceptions.DatabaseException;
import com.forestech.exceptions.InsufficientStockException;
import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.models.Factura;
import com.forestech.services.MovementServices;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;
import com.forestech.services.FacturaServices;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.11: MovementDialogForm - Formulario con 3 Foreign Keys + Validación de Stock
 *
 * Este es el formulario MÁS COMPLEJO del proyecto. Demuestra:
 * - Validación de 3 Foreign Keys simultáneas
 * - Lógica de negocio según tipo de movimiento (ENTRADA vs SALIDA)
 * - Manejo de InsufficientStockException
 * - Campos condicionales según el tipo de movimiento
 */
public class MovementDialogForm extends JDialog {

    private JComboBox<String> cmbTipo;
    private JComboBox<ComboItem> cmbProducto;
    private JComboBox<ComboItem> cmbVehiculo;
    private JComboBox<ComboItem> cmbFactura;
    private JTextField txtCantidad;
    private JLabel lblVehiculoLabel;
    private JLabel lblFacturaLabel;

    private boolean guardadoExitoso = false;

    public MovementDialogForm(JFrame parent, boolean modal) {
        super(parent, "Registrar Movimiento", modal);

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        crearFormulario();
        crearPanelBotones();

        setVisible(true);
    }

    private void crearFormulario() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Fila 1: Tipo de movimiento
        panel.add(new JLabel("Tipo de Movimiento:"));
        cmbTipo = new JComboBox<>(new String[]{"ENTRADA", "SALIDA"});
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
        try {
            List<Product> productos = ProductServices.getInstance().getAllProducts();
            for (Product p : productos) {
                cmbProducto.addItem(new ComboItem(p.getId(), p.getName()));
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarVehiculos() {
        try {
            cmbVehiculo.addItem(new ComboItem(null, "(Opcional)"));
            List<Vehicle> vehiculos = VehicleServices.getInstance().getAllVehicles();
            for (Vehicle v : vehiculos) {
                cmbVehiculo.addItem(new ComboItem(v.getId(), v.getName()));
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar vehículos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFacturas() {
        try {
            cmbFactura.addItem(new ComboItem(null, "(Opcional)"));
            List<Factura> facturas = FacturaServices.getInstance().getAllFacturas();
            for (Factura f : facturas) {
                cmbFactura.addItem(new ComboItem(f.getNumeroFactura(),
                    "Factura #" + f.getNumeroFactura() + " - " + f.getFechaEmision()));
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar facturas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Habilita/deshabilita campos según el tipo de movimiento.
     * - ENTRADA: requiere factura (opcional vehículo)
     * - SALIDA: requiere vehículo (opcional factura)
     */
    private void actualizarCamposSegunTipo() {
        String tipo = (String) cmbTipo.getSelectedItem();

        if ("ENTRADA".equals(tipo)) {
            // ENTRADA: vehículo opcional, factura recomendada
            lblVehiculoLabel.setText("Vehículo (opcional):");
            lblFacturaLabel.setText("Factura (recomendada):");
            cmbVehiculo.setEnabled(true);
            cmbFactura.setEnabled(true);
            cmbVehiculo.setBackground(Color.WHITE);
            cmbFactura.setBackground(new Color(255, 255, 200)); // Amarillo claro

        } else { // SALIDA
            // SALIDA: vehículo recomendado, factura opcional
            lblVehiculoLabel.setText("Vehículo (recomendado):");
            lblFacturaLabel.setText("Factura (opcional):");
            cmbVehiculo.setEnabled(true);
            cmbFactura.setEnabled(true);
            cmbVehiculo.setBackground(new Color(255, 255, 200)); // Amarillo claro
            cmbFactura.setBackground(Color.WHITE);
        }
    }

    private void crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnGuardar = new JButton("Registrar Movimiento");
        btnGuardar.setBackground(new Color(100, 200, 100));
        btnGuardar.addActionListener(e -> guardarMovimiento());
        panel.add(btnGuardar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(255, 150, 150));
        btnCancelar.addActionListener(e -> dispose());
        panel.add(btnCancelar);

        add(panel, BorderLayout.SOUTH);
    }

    private void guardarMovimiento() {
        // VALIDAR CAMPOS
        String tipo = (String) cmbTipo.getSelectedItem();
        String cantidadTexto = txtCantidad.getText().trim();

        if (cmbProducto.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Debes seleccionar un producto",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La cantidad es obligatoria",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe ser un número válido",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this,
                "La cantidad debe ser mayor a cero",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener IDs de los combos
        ComboItem productoItem = (ComboItem) cmbProducto.getSelectedItem();
        String productId = productoItem.id;

        ComboItem vehiculoItem = (ComboItem) cmbVehiculo.getSelectedItem();
        String vehicleId = (vehiculoItem != null && vehiculoItem.id != null) ? vehiculoItem.id : null;

        ComboItem facturaItem = (ComboItem) cmbFactura.getSelectedItem();
        String numeroFactura = (facturaItem != null && facturaItem.id != null) ? facturaItem.id : null;

        // Crear y guardar movimiento
        try {
            // Constructor requiere: MovementType, productId, vehicleId, numeroFactura, MeasurementUnit, quantity, unitPrice
            Movement nuevoMovimiento = new Movement(
                MovementType.fromCode(tipo),
                productId,
                vehicleId,
                numeroFactura,
                MeasurementUnit.GALON,
                cantidad,
                0.0
            );
            MovementServices.getInstance().insertMovement(nuevoMovimiento);

            JOptionPane.showMessageDialog(this,
                String.format("Movimiento registrado exitosamente:\n\n" +
                    "Tipo: %s\n" +
                    "Producto: %s\n" +
                    "Cantidad: %.2f L\n" +
                    "ID: %s",
                    tipo, productoItem.nombre, cantidad, nuevoMovimiento.getId()),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            System.out.println("✅ Movimiento registrado: " + nuevoMovimiento.getId());

            guardadoExitoso = true;
            dispose();

        } catch (com.forestech.exceptions.ValidationException e) {
            // MANEJO ESPECÍFICO PARA ERRORES DE VALIDACIÓN
            JOptionPane.showMessageDialog(this,
                "Error de validación: " + e.getMessage(),
                "Datos inválidos",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("❌ Error de validación: " + e.getMessage());

        } catch (InsufficientStockException e) {
            // MANEJO ESPECÍFICO PARA STOCK INSUFICIENTE
            JOptionPane.showMessageDialog(this,
                String.format("❌ STOCK INSUFICIENTE\n\n" +
                    "No se puede registrar esta SALIDA:\n\n" +
                    "Cantidad solicitada: %.2f L\n" +
                    "Stock disponible: %.2f L\n" +
                    "Faltante: %.2f L\n\n" +
                    "Solución: Registra primero una ENTRADA de este producto.",
                    e.getCantidadSolicitada(),
                    e.getStockActual(),
                    e.getCantidadSolicitada() - e.getStockActual()),
                "Stock Insuficiente",
                JOptionPane.ERROR_MESSAGE);

            System.err.println("❌ Stock insuficiente: " + e.getMessage());

        } catch (DatabaseException e) {
            // OTROS ERRORES DE BD
            String mensaje = e.getMessage();

            if (mensaje.contains("foreign key") || mensaje.contains("Cannot add or update")) {
                JOptionPane.showMessageDialog(this,
                    "Error de Foreign Key:\n\n" +
                    "Verifica que el producto, vehículo y factura existan.\n\n" +
                    "Error técnico: " + mensaje,
                    "Error de Integridad",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar movimiento:\n" + mensaje,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            System.err.println("❌ Error al guardar movimiento: " + mensaje);
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
