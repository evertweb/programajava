package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.*;
import com.forestech.simpleui.service.*;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * InvoiceFormDialog
 * Complex dialog for creating invoices with Master-Detail structure.
 */
public class InvoiceFormDialog extends JDialog {

    private final InvoicingServiceAdapter invoiceService;
    private final PartnersServiceAdapter partnersService;
    private final CatalogServiceAdapter catalogService;
    private final Runnable onSuccess;

    // Master Fields
    private JComboBox<Supplier> supplierCombo;

    // Detail Fields
    private JComboBox<Product> productCombo;
    private FTextField quantityField;
    private FTextField priceField;
    private FButton addButton;

    // Table
    private FTable detailsTable;
    private DefaultTableModel tableModel;
    private List<DetalleRequest> currentDetails = new ArrayList<>();

    // Totals
    private JLabel subtotalLabel;
    private JLabel ivaLabel;
    private JLabel totalLabel;

    private FButton saveButton;
    private FButton cancelButton;

    public InvoiceFormDialog(Frame owner,
            InvoicingServiceAdapter invoiceService,
            PartnersServiceAdapter partnersService,
            CatalogServiceAdapter catalogService,
            Runnable onSuccess) {
        super(owner, "Nueva Factura", true);
        this.invoiceService = invoiceService;
        this.partnersService = partnersService;
        this.catalogService = catalogService;
        this.onSuccess = onSuccess;

        setLayout(new BorderLayout());
        setSize(900, 700);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.WHITE);

        initUI();
        loadMetadata();
    }

    private void initUI() {
        // --- TOP: Master Info ---
        JPanel masterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        masterPanel.setBackground(Color.WHITE);
        masterPanel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        supplierCombo = new JComboBox<>();
        supplierCombo.setPreferredSize(new Dimension(300, 40));
        supplierCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Supplier) {
                    setText(((Supplier) value).getName());
                }
                return this;
            }
        });

        masterPanel.add(new JLabel("Proveedor/Cliente:"));
        masterPanel.add(supplierCombo);

        add(masterPanel, BorderLayout.NORTH);

        // --- CENTER: Details ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Row
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Agregar Item"));

        productCombo = new JComboBox<>();
        productCombo.setPreferredSize(new Dimension(250, 40));
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getName());
                }
                return this;
            }
        });
        productCombo.addActionListener(e -> updatePriceFromProduct());

        quantityField = new FTextField("Cant.");
        quantityField.setPreferredSize(new Dimension(80, 50));

        priceField = new FTextField("Precio U.");
        priceField.setPreferredSize(new Dimension(100, 50));

        addButton = new FButton("Agregar", FButton.Variant.SECONDARY);
        addButton.addActionListener(e -> addDetail());

        inputPanel.add(new JLabel("Producto:"));
        inputPanel.add(productCombo);
        inputPanel.add(quantityField);
        inputPanel.add(priceField);
        inputPanel.add(addButton);

        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Producto", "Cantidad", "Precio U.", "Subtotal", "Acción" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only action button (handled by click)
            }
        };
        detailsTable = new FTable();
        detailsTable.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(detailsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Totals Panel
        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        totalsPanel.add(new JLabel("Subtotal:"));
        subtotalLabel = new JLabel("0.00");
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(subtotalLabel);

        totalsPanel.add(new JLabel("IVA (13%):"));
        ivaLabel = new JLabel("0.00");
        ivaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(ivaLabel);

        totalsPanel.add(new JLabel("TOTAL:"));
        totalLabel = new JLabel("0.00");
        totalLabel.setFont(ThemeConstants.FONT_H2);
        totalLabel.setForeground(ThemeConstants.PRIMARY_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalsPanel.add(totalLabel);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBackground(Color.WHITE);
        bottomWrapper.add(totalsPanel, BorderLayout.EAST);

        centerPanel.add(bottomWrapper, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Actions ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeConstants.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeConstants.BORDER_COLOR));

        cancelButton = new FButton("Cancelar", FButton.Variant.SECONDARY);
        cancelButton.addActionListener(e -> dispose());

        saveButton = new FButton("Guardar Factura", FButton.Variant.PRIMARY);
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveInvoice());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMetadata() {
        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Supplier> suppliers = partnersService.getAllSuppliers();
                        List<Product> products = catalogService.getAllProducts();
                        return new Metadata(suppliers, products);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (data) -> {
                    supplierCombo.removeAllItems();
                    for (Supplier s : data.suppliers)
                        supplierCombo.addItem(s);

                    productCombo.removeAllItems();
                    for (Product p : data.products)
                        productCombo.addItem(p);
                },
                (error) -> {
                    NotificationManager.show((JFrame) getOwner(), "Error cargando datos: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                    dispose();
                });
    }

    private void updatePriceFromProduct() {
        Product p = (Product) productCombo.getSelectedItem();
        if (p != null && p.getUnitPrice() != null) {
            priceField.setText(p.getUnitPrice().toString());
        }
    }

    private void addDetail() {
        Product p = (Product) productCombo.getSelectedItem();
        if (p == null)
            return;

        BigDecimal qty;
        BigDecimal price;

        try {
            qty = new BigDecimal(quantityField.getText().trim());
            if (qty.compareTo(BigDecimal.ZERO) <= 0)
                throw new NumberFormatException();
        } catch (Exception e) {
            quantityField.setError("Inválido");
            return;
        }
        quantityField.clearError();

        try {
            price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) < 0)
                throw new NumberFormatException();
        } catch (Exception e) {
            priceField.setError("Inválido");
            return;
        }
        priceField.clearError();

        // Add to list
        DetalleRequest detail = new DetalleRequest(p.getId(), qty, price);
        currentDetails.add(detail);

        // Add to table
        Vector<Object> row = new Vector<>();
        row.add(p.getName());
        row.add(qty);
        row.add(price);
        row.add(qty.multiply(price));
        row.add("Eliminar"); // Placeholder for action
        tableModel.addRow(row);

        calculateTotals();

        // Reset inputs
        quantityField.setText("");
        quantityField.requestFocus();
    }

    private void calculateTotals() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleRequest d : currentDetails) {
            subtotal = subtotal.add(d.getCantidad().multiply(d.getPrecioUnitario()));
        }

        BigDecimal iva = subtotal.multiply(new BigDecimal("0.13"));
        BigDecimal total = subtotal.add(iva);

        subtotalLabel.setText(String.format("%.2f", subtotal));
        ivaLabel.setText(String.format("%.2f", iva));
        totalLabel.setText(String.format("%.2f", total));

        saveButton.setEnabled(!currentDetails.isEmpty() && supplierCombo.getSelectedItem() != null);
    }

    private void saveInvoice() {
        Supplier s = (Supplier) supplierCombo.getSelectedItem();
        if (s == null)
            return;

        saveButton.setText("Guardando...");
        saveButton.setEnabled(false);

        FacturaRequest request = new FacturaRequest(s.getId(), currentDetails);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        invoiceService.createInvoice(request);
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (result) -> {
                    NotificationManager.show((JFrame) getOwner(), "Factura creada exitosamente",
                            NotificationManager.Type.SUCCESS);
                    dispose();
                    if (onSuccess != null)
                        onSuccess.run();
                },
                (error) -> {
                    saveButton.setText("Guardar Factura");
                    saveButton.setEnabled(true);
                    NotificationManager.show((JFrame) getOwner(), "Error: " + error.getMessage(),
                            NotificationManager.Type.ERROR);
                });
    }

    private static class Metadata {
        List<Supplier> suppliers;
        List<Product> products;

        Metadata(List<Supplier> s, List<Product> p) {
            this.suppliers = s;
            this.products = p;
        }
    }
}
