package com.forestech.ui.invoices;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.DetalleFactura;
import com.forestech.models.Factura;
import com.forestech.models.Supplier;
import com.forestech.services.FacturaServices;
import com.forestech.services.SupplierServices;
import com.forestech.ui.utils.AsyncLoadManager;
import com.forestech.ui.utils.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Panel de gestión de facturas.
 */
public class InvoicesPanel extends JPanel {

    private final JFrame owner;
    private final Consumer<String> logger;
    private final Runnable dashboardRefresh;
    private final AsyncLoadManager loadManager;

    // Services (Dependency Injection)
    private final FacturaServices facturaServices;
    private final SupplierServices supplierServices;

    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;

    public InvoicesPanel(JFrame owner,
                         Consumer<String> logger,
                         Runnable dashboardRefresh,
                         FacturaServices facturaServices,
                         SupplierServices supplierServices) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.facturaServices = facturaServices;
        this.supplierServices = supplierServices;
        this.loadManager = new AsyncLoadManager("Facturas", logger, this::refreshAsync);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("🧾 GESTIÓN DE FACTURAS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"Nº Factura", "Fecha Emisión", "Proveedor", "Subtotal", "IVA", "Total"};
        modeloFacturas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setRowHeight(25);
        UIUtils.styleTable(tablaFacturas);
        add(new JScrollPane(tablaFacturas), BorderLayout.CENTER);

        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonsPanel() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNuevaFactura = new JButton("Nueva Factura");
        btnNuevaFactura.setBackground(new Color(100, 200, 100));
        btnNuevaFactura.addActionListener(e -> mostrarFormularioNuevaFactura());
        panelBotones.add(btnNuevaFactura);

        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.addActionListener(e -> verDetallesFactura());
        panelBotones.add(btnVerDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refresh());
        panelBotones.add(btnRefrescar);

        return panelBotones;
    }

    public void refresh() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::refresh);
            return;
        }
        loadManager.requestLoad("Solicitud externa");
    }

    public void cancelCurrentLoad() {
        loadManager.cancelCurrentLoad();
    }

    private void refreshAsync(String origin) {
        final long inicio = System.currentTimeMillis();
        setBusyCursor(true);

        SwingWorker<List<Factura>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Factura> doInBackground() throws Exception {
                return facturaServices.getAllFacturas();
            }

            @Override
            protected void done() {
                try {
                    List<Factura> facturas = get();
                    modeloFacturas.setRowCount(0);

                    for (Factura f : facturas) {
                        modeloFacturas.addRow(new Object[]{
                            f.getNumeroFactura(),
                            f.getFechaEmision(),
                            f.getSupplierId() != null ? f.getSupplierId() : "N/A",
                            UIUtils.formatCurrency(f.getSubtotal()),
                            UIUtils.formatCurrency(f.getIva()),
                            UIUtils.formatCurrency(f.getTotal())
                        });
                    }

                    logger.accept(String.format(
                        "Facturas: cargadas %d entradas en %d ms",
                        facturas.size(),
                        System.currentTimeMillis() - inicio
                    ));
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    String mensaje = causa != null ? causa.getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(owner,
                        "Error al cargar facturas: " + mensaje,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    logger.accept("Facturas: error durante carga → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Facturas: carga interrumpida");
                } finally {
                    setBusyCursor(false);
                    loadManager.finish(inicio);
                }
            }
        };

        // Registrar worker para cancelación antes de ejecutar
        loadManager.registerWorker(worker);
        worker.execute();
    }

    private void setBusyCursor(boolean busy) {
        Cursor cursor = busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        if (owner != null) {
            owner.setCursor(cursor);
        }
    }

    private void mostrarFormularioNuevaFactura() {
        JDialog dialog = new JDialog(owner, "Nueva Factura", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new java.awt.GridLayout(8, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtNumeroFactura = new JTextField();
        JTextField txtFechaEmision = new JTextField(LocalDate.now().toString());
        JTextField txtFechaVencimiento = new JTextField(LocalDate.now().plusDays(30).toString());

        JComboBox<String> cmbProveedor = new JComboBox<>();
        try {
            List<Supplier> proveedores = supplierServices.getAllSuppliers();
            cmbProveedor.addItem("--- Sin proveedor ---");
            for (Supplier s : proveedores) {
                cmbProveedor.addItem(s.getId() + " - " + s.getName());
            }
        } catch (DatabaseException e) {
            logger.accept("Facturas: error cargando proveedores para el formulario");
        }

        JTextField txtSubtotal = new JTextField();
        JTextField txtIva = new JTextField("19");
        JTextField txtObservaciones = new JTextField();
        JTextField txtFormaPago = new JTextField("EFECTIVO");

        panelForm.add(new JLabel("Nº Factura:"));
        panelForm.add(txtNumeroFactura);
        panelForm.add(new JLabel("Fecha Emisión:"));
        panelForm.add(txtFechaEmision);
        panelForm.add(new JLabel("Fecha Vencimiento:"));
        panelForm.add(txtFechaVencimiento);
        panelForm.add(new JLabel("Proveedor:"));
        panelForm.add(cmbProveedor);
        panelForm.add(new JLabel("Subtotal (COP):"));
        panelForm.add(txtSubtotal);
        panelForm.add(new JLabel("IVA (%):"));
        panelForm.add(txtIva);
        panelForm.add(new JLabel("Observaciones:"));
        panelForm.add(txtObservaciones);
        panelForm.add(new JLabel("Forma de Pago:"));
        panelForm.add(txtFormaPago);

        dialog.add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setBackground(new Color(100, 200, 100));
        btnGuardar.addActionListener(e -> {
            try {
                String numero = txtNumeroFactura.getText().trim();
                if (numero.isEmpty()) {
                    throw new IllegalArgumentException("Nº de factura es requerido");
                }

                double subtotal = Double.parseDouble(txtSubtotal.getText().trim());
                double ivaPorcentaje = Double.parseDouble(txtIva.getText().trim());
                double ivaValor = subtotal * ivaPorcentaje / 100;
                double total = subtotal + ivaValor;

                String proveedorId = null;
                if (cmbProveedor.getSelectedIndex() > 0) {
                    String seleccion = (String) cmbProveedor.getSelectedItem();
                    proveedorId = seleccion.split(" - ")[0];
                }

                Factura factura = new Factura(
                    numero,
                    LocalDate.parse(txtFechaEmision.getText().trim()),
                    LocalDate.parse(txtFechaVencimiento.getText().trim()),
                    proveedorId,
                    subtotal,
                    ivaValor,
                    total,
                    txtObservaciones.getText().trim(),
                    txtFormaPago.getText().trim(),
                    null
                );

                facturaServices.createFacturaWithDetails(factura, new ArrayList<>());

                JOptionPane.showMessageDialog(dialog,
                    "Factura creada correctamente\nTotal: " + UIUtils.formatCurrency(total),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                refresh();
                refreshDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error en formato numérico: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al crear factura: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void verDetallesFactura() {
        int fila = tablaFacturas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner,
                "Selecciona una factura para ver detalles",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numeroFactura = (String) modeloFacturas.getValueAt(fila, 0);

        try {
            List<DetalleFactura> detalles = facturaServices.getDetallesByFactura(numeroFactura);

            if (detalles.isEmpty()) {
                JOptionPane.showMessageDialog(owner,
                    "Esta factura no tiene detalles registrados",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] columnas = {"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
            DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0);

            for (DetalleFactura d : detalles) {
                double subtotal = d.getCantidad() * d.getPrecioUnitario();
                modeloDetalles.addRow(new Object[]{
                    d.getIdDetalle(),
                    d.getProducto(),
                    d.getCantidad(),
                    UIUtils.formatCurrency(d.getPrecioUnitario()),
                    UIUtils.formatCurrency(subtotal)
                });
            }

            JTable tablaDetalles = new JTable(modeloDetalles);
            tablaDetalles.setRowHeight(25);
            JScrollPane scroll = new JScrollPane(tablaDetalles);
            scroll.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(owner, scroll,
                "Detalles de Factura " + numeroFactura,
                JOptionPane.INFORMATION_MESSAGE);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                "Error al cargar detalles: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void requestCreationShortcut() {
        mostrarFormularioNuevaFactura();
    }

    private void refreshDashboard() {
        if (dashboardRefresh != null) {
            dashboardRefresh.run();
        }
    }
}
