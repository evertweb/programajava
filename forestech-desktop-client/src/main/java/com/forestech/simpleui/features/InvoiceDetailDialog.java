package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.DetalleFactura;
import com.forestech.simpleui.model.Factura;
import com.forestech.simpleui.service.InvoicingServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * InvoiceDetailDialog
 * Shows read-only details of an invoice.
 */
public class InvoiceDetailDialog extends JDialog {

    private final InvoicingServiceAdapter service;
    private final String invoiceId;
    private FTable detailsTable;
    private DefaultTableModel tableModel;

    public InvoiceDetailDialog(Frame owner, InvoicingServiceAdapter service, String invoiceId) {
        super(owner, "Detalle de Factura", true);
        this.service = service;
        this.invoiceId = invoiceId;

        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.WHITE);

        initUI();
        loadData();
    }

    private void initUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Productos de la Factura");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title);

        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = { "Producto", "Cantidad", "Precio U.", "Subtotal" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailsTable = new FTable();
        detailsTable.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.BORDER_COLOR));

        FButton closeBtn = new FButton("Cerrar", FButton.Variant.SECONDARY);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private void loadData() {
        AsyncServiceTask.execute(
                () -> {
                    try {
                        return service.getInvoiceById(invoiceId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (factura) -> {
                    updateTable(factura.getDetalles());
                },
                (error) -> {
                    NotificationManager.show((JFrame) getOwner(), "Error cargando detalles: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                    dispose();
                });
    }

    private void updateTable(List<DetalleFactura> detalles) {
        tableModel.setRowCount(0);
        if (detalles != null) {
            for (DetalleFactura d : detalles) {
                Object[] row = {
                        d.getProducto(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getCantidad().multiply(d.getPrecioUnitario())
                };
                tableModel.addRow(row);
            }
        }
    }
}
