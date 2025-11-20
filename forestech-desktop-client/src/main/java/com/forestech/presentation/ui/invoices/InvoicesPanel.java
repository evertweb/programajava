package com.forestech.presentation.ui.invoices;

import com.forestech.modules.invoicing.models.Invoice;
import com.forestech.modules.invoicing.models.InvoiceDetail;
import com.forestech.modules.partners.models.Supplier;
import com.forestech.presentation.clients.InvoiceServiceClient;
import com.forestech.presentation.clients.SupplierServiceClient;
import com.forestech.presentation.ui.utils.AsyncLoadManager;
import com.forestech.presentation.ui.utils.UIUtils;

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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import com.forestech.presentation.ui.utils.BackgroundTaskExecutor;
import com.forestech.presentation.ui.utils.FontScheme;
import com.forestech.presentation.ui.utils.IconManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.forestech.presentation.ui.utils.ColorScheme;
import java.math.BigDecimal;
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

    // Clients (Microservices)
    private final InvoiceServiceClient invoiceClient;
    private final SupplierServiceClient supplierClient;

    private JTable tablaFacturas;
    private DefaultTableModel modeloFacturas;

    public InvoicesPanel(JFrame owner,
            Consumer<String> logger,
            Runnable dashboardRefresh,
            InvoiceServiceClient invoiceClient,
            SupplierServiceClient supplierClient) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.invoiceClient = invoiceClient;
        this.supplierClient = supplierClient;
        this.loadManager = new AsyncLoadManager("Facturas", logger, this::refreshAsync);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("GESTIÓN DE FACTURAS", JLabel.CENTER);
        titulo.setFont(FontScheme.HEADER_2);
        add(titulo, BorderLayout.NORTH);

        String[] columnas = { "Nº Factura", "Fecha Emisión", "Proveedor", "Subtotal", "IVA", "Total" };
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
        btnNuevaFactura.setIcon(IconManager.getIcon("plus"));
        btnNuevaFactura.setBackground(ColorScheme.BUTTON_PRIMARY_BG);
        btnNuevaFactura.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btnNuevaFactura.addActionListener(e -> mostrarFormularioNuevaFactura());
        panelBotones.add(btnNuevaFactura);

        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.setIcon(IconManager.getIcon("eye"));
        btnVerDetalles.addActionListener(e -> verDetallesFactura());
        panelBotones.add(btnVerDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setIcon(IconManager.getIcon("refresh"));
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

        SwingWorker<List<Invoice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Invoice> doInBackground() throws Exception {
                return invoiceClient.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Invoice> invoices = get();
                    modeloFacturas.setRowCount(0);

                    for (Invoice f : invoices) {
                        modeloFacturas.addRow(new Object[] {
                                f.getId(),
                                f.getIssueDate() != null
                                        ? f.getIssueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                        : "N/A",
                                f.getSupplierId() != null ? f.getSupplierId() : "N/A",
                                "N/A", // Subtotal no disponible en modelo simple
                                "N/A", // IVA no disponible en modelo simple
                                f.getTotalAmount() != null ? UIUtils.formatCurrency(f.getTotalAmount().doubleValue())
                                        : "0.00"
                        });
                    }

                    logger.accept(String.format(
                            "Facturas: cargadas %d entradas en %d ms",
                            invoices.size(),
                            System.currentTimeMillis() - inicio));
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
        BackgroundTaskExecutor.submit(worker);
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
            List<Supplier> proveedores = supplierClient.findAll();
            cmbProveedor.addItem("--- Sin proveedor ---");
            for (Supplier s : proveedores) {
                cmbProveedor.addItem(s.getId() + " - " + s.getName());
            }
        } catch (Exception e) {
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

        btnGuardar.setBackground(ColorScheme.BUTTON_SUCCESS_BG);
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

                Invoice invoice = new Invoice();
                invoice.setId(numero);
                invoice.setIssueDate(LocalDate.parse(txtFechaEmision.getText().trim()).atStartOfDay());
                invoice.setSupplierId(proveedorId);
                invoice.setTotalAmount(BigDecimal.valueOf(total));
                invoice.setStatus("PENDING");
                invoice.setDetalles(new ArrayList<>());

                invoiceClient.create(invoice);

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
            Invoice invoice = invoiceClient.findById(numeroFactura);
            List<InvoiceDetail> detalles = invoice.getDetalles();

            if (detalles == null || detalles.isEmpty()) {
                JOptionPane.showMessageDialog(owner,
                        "Esta factura no tiene detalles registrados",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] columnas = { "ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal" };
            DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0);

            for (InvoiceDetail d : detalles) {
                BigDecimal subtotal = d.getSubtotal();
                if (subtotal == null && d.getCantidad() != null && d.getPrecioUnitario() != null) {
                    subtotal = d.getCantidad().multiply(d.getPrecioUnitario());
                }

                modeloDetalles.addRow(new Object[] {
                        "N/A",
                        d.getProductId(),
                        d.getCantidad(),
                        d.getPrecioUnitario() != null ? UIUtils.formatCurrency(d.getPrecioUnitario().doubleValue())
                                : "0.00",
                        subtotal != null ? UIUtils.formatCurrency(subtotal.doubleValue()) : "0.00"
                });
            }

            JTable tablaDetalles = new JTable(modeloDetalles);
            tablaDetalles.setRowHeight(25);
            JScrollPane scroll = new JScrollPane(tablaDetalles);
            scroll.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(owner, scroll,
                    "Detalles de Factura " + numeroFactura,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
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
