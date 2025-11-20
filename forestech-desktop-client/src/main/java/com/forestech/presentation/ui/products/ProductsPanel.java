package com.forestech.presentation.ui.products;

import com.forestech.modules.catalog.models.Product;
import com.forestech.presentation.clients.ProductServiceClient;
import com.forestech.presentation.clients.MovementServiceClient;
import com.forestech.presentation.clients.ReportsServiceClient;
import com.forestech.presentation.ui.utils.UIUtils;
import com.forestech.presentation.ui.utils.ColorScheme;
import com.forestech.presentation.ui.utils.FontScheme;
import com.forestech.presentation.ui.utils.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Panel de gestión de productos.
 * Implementación MVP (Vista).
 */
public class ProductsPanel extends JPanel implements ProductsContract.View {

    private final JFrame owner;
    private final Runnable dashboardRefresh;
    private final ProductsContract.Presenter presenter;

    // Clients
    private final ProductServiceClient productClient;
    private final MovementServiceClient movementClient;
    private final ReportsServiceClient reportsClient;
    
    private final Consumer<String> logger;

    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JTextField txtBuscarProducto;
    private JComboBox<String> cmbFiltroUnidad;
    private JLabel lblResumenProductos;

    public ProductsPanel(JFrame owner,
            Consumer<String> logger,
            Runnable dashboardRefresh,
            ProductServiceClient productClient,
            MovementServiceClient movementClient,
            ReportsServiceClient reportsClient) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        
        this.productClient = productClient != null ? productClient : new ProductServiceClient();
        this.movementClient = movementClient != null ? movementClient : new MovementServiceClient();
        this.reportsClient = reportsClient != null ? reportsClient : new ReportsServiceClient();
        
        this.presenter = new ProductsPresenter(this, this.productClient, this.movementClient, this.reportsClient, logger);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.BACKGROUND_LIGHT);
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("GESTIÓN DE PRODUCTOS", JLabel.CENTER);
        titulo.setFont(FontScheme.HEADER_2);
        titulo.setForeground(ColorScheme.FOREGROUND_PRIMARY);

        JPanel panelEncabezado = new JPanel(new BorderLayout(0, 5));
        panelEncabezado.setBackground(ColorScheme.BACKGROUND_PANEL);
        panelEncabezado.add(titulo, BorderLayout.NORTH);
        panelEncabezado.add(crearPanelFiltrosProductos(), BorderLayout.CENTER);
        add(panelEncabezado, BorderLayout.NORTH);

        String[] columnas = { "ID", "Nombre", "Precio (COP)", "Unidad", "Stock Actual" };
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(25);
        UIUtils.styleTable(tablaProductos);
        aplicarRendererProductos();
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaProductos.getSelectedRow() != -1) {
                    presenter.showDetails(getSelectedProductId());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.getViewport().setBackground(ColorScheme.BACKGROUND_PANEL);
        add(scroll, BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFiltrosProductos() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));
        panelFiltros.setBackground(ColorScheme.BACKGROUND_PANEL);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelControles.setBackground(ColorScheme.BACKGROUND_PANEL);

        txtBuscarProducto = new JTextField(18);
        txtBuscarProducto.setToolTipText("Escribe parte del nombre y presiona Enter para buscar");
        txtBuscarProducto.addActionListener(e -> presenter.loadProducts("Enter en búsqueda"));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> presenter.loadProducts("Botón Buscar"));

        cmbFiltroUnidad = new JComboBox<>();
        cmbFiltroUnidad.addItem("Todas");
        cmbFiltroUnidad.setPreferredSize(new Dimension(160, 28));
        cmbFiltroUnidad.addActionListener(e -> presenter.loadProducts("Filtro Unidad"));

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarProducto.setText("");
            cmbFiltroUnidad.setSelectedIndex(0);
            presenter.loadProducts("Limpiar filtros");
        });

        panelControles.add(new JLabel("Nombre:"));
        panelControles.add(txtBuscarProducto);
        panelControles.add(btnBuscar);
        panelControles.add(new JLabel("Unidad:"));
        panelControles.add(cmbFiltroUnidad);
        panelControles.add(btnLimpiar);

        lblResumenProductos = new JLabel("Mostrando todos los productos");
        lblResumenProductos.setFont(FontScheme.SMALL_ITALIC);
        lblResumenProductos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        lblResumenProductos.setForeground(ColorScheme.FOREGROUND_SECONDARY);

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenProductos, BorderLayout.SOUTH);
        return panelFiltros;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(ColorScheme.BACKGROUND_PANEL);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setIcon(IconManager.getIcon("plus"));
        btnAgregar.setBackground(ColorScheme.BUTTON_PRIMARY_BG);
        btnAgregar.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btnAgregar.addActionListener(e -> presenter.addProduct());
        panelBotones.add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setIcon(IconManager.getIcon("edit"));
        btnEditar.setBackground(ColorScheme.BUTTON_WARNING_BG);
        btnEditar.setForeground(ColorScheme.TEXT_PRIMARY);
        btnEditar.addActionListener(e -> presenter.editProduct(getSelectedProductId()));
        panelBotones.add(btnEditar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> presenter.showDetails(getSelectedProductId()));
        panelBotones.add(btnDetalles);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setIcon(IconManager.getIcon("trash"));
        btnEliminar.setBackground(ColorScheme.BUTTON_DANGER_BG);
        btnEliminar.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btnEliminar.addActionListener(e -> presenter.deleteProduct(getSelectedProductId()));
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> presenter.loadProducts("Botón Refrescar Productos"));
        panelBotones.add(btnRefrescar);

        return panelBotones;
    }

    // --- View Implementation ---

    @Override
    public void showProducts(List<Product> productos, Map<String, Double> stockPorProducto) {
        modeloProductos.setRowCount(0);
        for (Product p : productos) {
            double stock = stockPorProducto.getOrDefault(p.getId(), 0.0);
            modeloProductos.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    UIUtils.formatCurrency(p.getUnitPrice()),
                    p.getMeasurementUnitCode(),
                    stock
            });
        }
    }

    @Override
    public void showLoading(boolean isLoading) {
        Cursor cursor = isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        if (owner != null) {
            owner.setCursor(cursor);
        }
        setCursor(cursor);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(owner, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(owner, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWarning(String message) {
        JOptionPane.showMessageDialog(owner, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean showConfirmation(String message) {
        return JOptionPane.showConfirmDialog(owner, message, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public void showProductDetails(Product producto, double stock) {
        String mensaje = "ID: " + producto.getId() +
                "\nNombre: " + producto.getName() +
                "\nUnidad: " + producto.getMeasurementUnitCode() +
                "\nPrecio: " + UIUtils.formatCurrency(producto.getUnitPrice()) +
                "\nStock actual: " + String.format("%,.2f", stock);

        JOptionPane.showMessageDialog(owner, mensaje,
                "Detalle de producto", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void updateSummary(int total, String criterio, String unidadSeleccionada) {
        StringBuilder resumen = new StringBuilder("Mostrando ")
                .append(total)
                .append(total == 1 ? " producto" : " productos");

        if (criterio != null && !criterio.isBlank()) {
            resumen.append(" | Nombre contiene \"")
                    .append(criterio)
                    .append("\"");
        }

        if (unidadSeleccionada != null && !"Todas".equalsIgnoreCase(unidadSeleccionada)) {
            resumen.append(" | Unidad: ")
                    .append(unidadSeleccionada);
        }

        lblResumenProductos.setText(resumen.toString());
        lblResumenProductos.setForeground(total == 0
                ? ColorScheme.DANGER_500
                : ColorScheme.FOREGROUND_SECONDARY);
    }

    @Override
    public void updateUnitOptions(List<String> units, String currentSelection) {
        cmbFiltroUnidad.removeAllItems();
        cmbFiltroUnidad.addItem("Todas");
        units.forEach(cmbFiltroUnidad::addItem);

        boolean existeSeleccion = currentSelection != null && units.stream()
                .anyMatch(u -> u.equalsIgnoreCase(currentSelection));

        if (existeSeleccion) {
            cmbFiltroUnidad.setSelectedItem(currentSelection);
        } else {
            cmbFiltroUnidad.setSelectedIndex(0);
        }
    }

    @Override
    public String getSearchTerm() {
        return txtBuscarProducto.getText().trim();
    }

    @Override
    public String getUnitFilter() {
        return (String) cmbFiltroUnidad.getSelectedItem();
    }

    @Override
    public void showProductForm(Product product) {
        // Reuse existing dialog logic
        ProductDialogForm dialog;
        if (product == null) {
            dialog = new ProductDialogForm(owner, true, productClient);
        } else {
            dialog = new ProductDialogForm(owner, true, product, productClient);
        }

        if (dialog.isGuardadoExitoso()) {
            presenter.loadProducts("Post Guardado Producto");
            refreshDashboard();
            logger.accept("Productos: operación exitosa");
        } else {
            logger.accept("Productos: operación cancelada");
        }
    }

    @Override
    public void refreshDashboard() {
        if (dashboardRefresh != null) {
            dashboardRefresh.run();
        }
    }

    // --- Public Methods ---

    public void requestRefresh(String origin) {
        presenter.loadProducts(origin);
    }

    public void cancelCurrentLoad() {
        presenter.cancelCurrentOperation();
    }

    public void requestCreationShortcut() {
        presenter.addProduct();
    }

    // --- Helper ---

    private String getSelectedProductId() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        return (String) modeloProductos.getValueAt(fila, 0);
    }

    private void aplicarRendererProductos() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                java.awt.Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                if (isSelected) {
                    comp.setBackground(ColorScheme.TABLE_SELECTION_BG);
                    comp.setForeground(ColorScheme.TABLE_SELECTION_FG);
                } else {
                    comp.setForeground(ColorScheme.FOREGROUND_PRIMARY);
                    comp.setBackground(row % 2 == 0
                            ? ColorScheme.TABLE_ROW_PRIMARY
                            : ColorScheme.TABLE_ROW_STRIPE);

                    Object stockValue = table.getValueAt(row, 4);
                    double stock = 0;
                    if (stockValue instanceof Number n) {
                        stock = n.doubleValue();
                    } else if (stockValue != null) {
                        try {
                            stock = Double.parseDouble(stockValue.toString().replace(",", "."));
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    if (stock < 10) {
                        comp.setBackground(ColorScheme.BACKGROUND_ERROR_SOFT);
                        comp.setForeground(ColorScheme.DANGER_500);
                    }
                }
                return comp;
            }
        };
        tablaProductos.setDefaultRenderer(Object.class, renderer);
    }
}
