package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Factura;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.service.InvoicingServiceAdapter;
import com.forestech.simpleui.service.PartnersServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * InvoicePanel
 * Displays a list of invoices.
 */
public class InvoicePanel extends JPanel {

    private final FTable table;
    private final InvoicingServiceAdapter service;
    private final PartnersServiceAdapter partnersService;
    private final CatalogServiceAdapter catalogService;
    private List<Factura> currentInvoices;
    private boolean loaded = false;

    public InvoicePanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        service = new InvoicingServiceAdapter();
        partnersService = new PartnersServiceAdapter();
        catalogService = new CatalogServiceAdapter();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Facturación");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title, BorderLayout.WEST);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        FButton refreshBtn = new FButton("Refrescar", FButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadData());
        actions.add(refreshBtn);

        FButton createBtn = new FButton("Nueva Factura", FButton.Variant.PRIMARY);
        createBtn.addActionListener(e -> openCreateDialog());
        actions.add(createBtn);

        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Toolbar for selected invoice actions
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(ThemeConstants.BACKGROUND_COLOR);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        FButton viewDetailsBtn = new FButton("Ver Detalle", FButton.Variant.SECONDARY);
        viewDetailsBtn.addActionListener(e -> openDetailDialog());
        toolbar.add(viewDetailsBtn);

        FButton cancelBtn = new FButton("Anular", FButton.Variant.DANGER);
        cancelBtn.addActionListener(e -> cancelInvoice());
        toolbar.add(cancelBtn);

        FButton editBtn = new FButton("Editar", FButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> openEditDialog());
        toolbar.add(editBtn);

        add(toolbar, BorderLayout.SOUTH);

        // Table
        table = new FTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        FCard card = new FCard();
        card.setLayout(new BorderLayout());
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void onShow() {
        if (!loaded) {
            loadData();
        }
    }

    private void loadData() {
        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Cargando facturas...",
                NotificationManager.Type.INFO);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        return service.getAllInvoices();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (invoices) -> {
                    updateTable(invoices);
                    loaded = true;
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Facturas cargadas",
                            NotificationManager.Type.SUCCESS);
                },
                (error) -> {
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                            "Error: " + error.getMessage(), NotificationManager.Type.ERROR);
                    error.printStackTrace();
                });
    }

    private void updateTable(List<Factura> invoices) {
        this.currentInvoices = invoices;
        String[] columns = { "Nº Factura", "Fecha", "Cliente", "NIT", "Total", "Estado" };
        Object[][] data = new Object[invoices.size()][6];

        for (int i = 0; i < invoices.size(); i++) {
            Factura f = invoices.get(i);
            data[i][0] = f.getNumeroFactura();
            data[i][1] = f.getFechaEmision();
            data[i][2] = f.getClienteNombre();
            data[i][3] = f.getClienteNit();
            data[i][4] = f.getTotal();
            data[i][5] = f.getEstado();
        }

        table.setModel(data, columns);
    }

    private void openCreateDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            InvoiceFormDialog dialog = new InvoiceFormDialog(
                    (Frame) window,
                    service,
                    partnersService,
                    catalogService,
                    this::loadData);
            dialog.setVisible(true);
        }
    }

    private void openDetailDialog() {
        String id = getSelectedInvoiceId();
        if (id == null)
            return;

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof Frame) {
            InvoiceDetailDialog dialog = new InvoiceDetailDialog((Frame) window, service, id);
            dialog.setVisible(true);
        }
    }

    private void cancelInvoice() {
        String id = getSelectedInvoiceId();
        if (id == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea anular esta factura?\nEsta acción revertirá los movimientos de inventario.",
                "Confirmar Anulación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AsyncServiceTask.execute(
                    () -> {
                        try {
                            service.cancelInvoice(id);
                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (result) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                                "Factura anulada exitosamente", NotificationManager.Type.SUCCESS);
                        loadData();
                    },
                    (error) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                                "Error al anular: " + error.getMessage(), NotificationManager.Type.ERROR);
                    });
        }
    }

    private void openEditDialog() {
        String id = getSelectedInvoiceId();
        if (id == null)
            return;

        // For now, simple input dialog for observations as a proof of concept for
        // metadata update
        // In a full implementation, we would use a dialog similar to InvoiceFormDialog
        String newObs = JOptionPane.showInputDialog(this, "Editar Observaciones:", "Editar Factura",
                JOptionPane.PLAIN_MESSAGE);
        if (newObs != null) {
            com.forestech.simpleui.model.FacturaRequest request = new com.forestech.simpleui.model.FacturaRequest();
            request.setObservaciones(newObs);

            AsyncServiceTask.execute(
                    () -> {
                        try {
                            service.updateInvoice(id, request);
                            return null;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (result) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                                "Factura actualizada", NotificationManager.Type.SUCCESS);
                        loadData();
                    },
                    (error) -> {
                        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                                "Error al actualizar: " + error.getMessage(), NotificationManager.Type.ERROR);
                    });
        }
    }

    private String getSelectedInvoiceId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                    "Seleccione una factura", NotificationManager.Type.WARNING);
            return null;
        }
        if (currentInvoices != null && row < currentInvoices.size()) {
            return currentInvoices.get(row).getId();
        }
        return null;
    }
}
