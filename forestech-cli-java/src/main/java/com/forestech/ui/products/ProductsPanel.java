package com.forestech.ui.products;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import com.forestech.services.MovementServices;
import com.forestech.services.ProductServices;
import com.forestech.ui.ProductDialogForm;
import com.forestech.ui.utils.AsyncLoadManager;
import com.forestech.ui.utils.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Panel de gestión de productos.
 */
public class ProductsPanel extends JPanel {

    private final JFrame owner;
    private final Consumer<String> logger;
    private final Runnable dashboardRefresh;
    private final AsyncLoadManager loadManager;

    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JTextField txtBuscarProducto;
    private JComboBox<String> cmbFiltroUnidad;
    private JLabel lblResumenProductos;
    private boolean actualizandoFiltroUnidad;

    public ProductsPanel(JFrame owner,
                         Consumer<String> logger,
                         Runnable dashboardRefresh) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.loadManager = new AsyncLoadManager("Productos", logger, this::cargarProductos);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("📦 GESTIÓN DE PRODUCTOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelEncabezado = new JPanel(new BorderLayout(0, 5));
        panelEncabezado.add(titulo, BorderLayout.NORTH);
        panelEncabezado.add(crearPanelFiltrosProductos(), BorderLayout.CENTER);
        add(panelEncabezado, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Precio (COP)", "Unidad", "Stock Actual"};
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
                    mostrarDetallesProducto();
                }
            }
        });

        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFiltrosProductos() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        txtBuscarProducto = new JTextField(18);
        txtBuscarProducto.setToolTipText("Escribe parte del nombre y presiona Enter para buscar");
        txtBuscarProducto.addActionListener(e -> requestRefresh("Enter en búsqueda"));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> requestRefresh("Botón Buscar"));

        cmbFiltroUnidad = new JComboBox<>();
        cmbFiltroUnidad.addItem("Todas");
        cmbFiltroUnidad.setPreferredSize(new Dimension(160, 28));
        cmbFiltroUnidad.addActionListener(e -> {
            if (actualizandoFiltroUnidad) {
                logger.accept("Productos: evento de unidad ignorado (actualización interna)");
                return;
            }
            requestRefresh("Filtro Unidad");
        });

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarProducto.setText("");
            cmbFiltroUnidad.setSelectedIndex(0);
            requestRefresh("Limpiar filtros");
        });

        panelControles.add(new JLabel("🔍 Nombre:"));
        panelControles.add(txtBuscarProducto);
        panelControles.add(btnBuscar);
        panelControles.add(new JLabel("⚖️ Unidad:"));
        panelControles.add(cmbFiltroUnidad);
        panelControles.add(btnLimpiar);

        lblResumenProductos = new JLabel("Mostrando todos los productos");
        lblResumenProductos.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenProductos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenProductos, BorderLayout.SOUTH);
        return panelFiltros;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarProducto());
        panelBotones.add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarProducto());
        panelBotones.add(btnEditar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> mostrarDetallesProducto());
        panelBotones.add(btnDetalles);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> requestRefresh("Botón Refrescar Productos"));
        panelBotones.add(btnRefrescar);

        return panelBotones;
    }

    public void requestRefresh(String origin) {
        loadManager.requestLoad(origin);
    }

    public void cancelCurrentLoad() {
        loadManager.cancelCurrentLoad();
    }

    private void cargarProductos(String origin) {
        long inicio = System.currentTimeMillis();
        logger.accept("Productos: iniciando carga de datos (origen: " + origin + ")");
        setBusyCursor(true);

        final String criterio = txtBuscarProducto != null ? txtBuscarProducto.getText().trim() : "";
        final String unidadSeleccionada = (cmbFiltroUnidad != null && cmbFiltroUnidad.getSelectedItem() != null)
            ? cmbFiltroUnidad.getSelectedItem().toString()
            : "Todas";
        final boolean debeActualizarUnidades = criterio.isEmpty();

        // Crear el SwingWorker
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            private Map<String, Double> stockPorProducto = new HashMap<>();

            @Override
            protected List<Product> doInBackground() throws Exception {
                List<Product> productos = criterio.isEmpty()
                    ? ProductServices.getAllProducts()
                    : ProductServices.searchProductsByName(criterio);

                if (unidadSeleccionada != null && !"Todas".equalsIgnoreCase(unidadSeleccionada)) {
                    productos = productos.stream()
                        .filter(p -> unidadSeleccionada.equalsIgnoreCase(p.getUnidadDeMedida()))
                        .collect(Collectors.toList());
                }

                if (!productos.isEmpty()) {
                    List<String> ids = productos.stream().map(Product::getId).toList();
                    stockPorProducto = MovementServices.getStockByProductIds(ids);
                }

                return productos;
            }

            @Override
            protected void done() {
                try {
                    List<Product> productos = get();
                    modeloProductos.setRowCount(0);
                    for (Product p : productos) {
                        double stock = stockPorProducto.getOrDefault(p.getId(), 0.0);
                        modeloProductos.addRow(new Object[]{
                            p.getId(),
                            p.getName(),
                            UIUtils.formatCurrency(p.getPriceXUnd()),
                            p.getUnidadDeMedida(),
                            stock
                        });
                    }

                    if (debeActualizarUnidades) {
                        actualizarOpcionesUnidadDesde(productos);
                    }

                    actualizarChipResumen(productos.size(), criterio, unidadSeleccionada);
                    logger.accept(String.format(
                        "Productos: carga completada en %d ms (%d registros)",
                        System.currentTimeMillis() - inicio,
                        productos.size()
                    ));
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    String mensaje = causa != null ? causa.getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(owner,
                        "Error al cargar productos: " + mensaje,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    logger.accept("Productos: error durante carga → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Productos: carga interrumpida");
                } finally {
                    finalizarCarga(inicio);
                }
            }
        };

        // Registrar worker para cancelación antes de ejecutar
        loadManager.registerWorker(worker);
        worker.execute();
    }

    private void finalizarCarga(long inicio) {
        setBusyCursor(false);
        loadManager.finish(inicio);
    }

    private void aplicarRendererProductos() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus,
                                                                    int row, int column) {
                java.awt.Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    comp.setBackground(new Color(52, 152, 219));
                    comp.setForeground(Color.WHITE);
                } else {
                    comp.setForeground(Color.BLACK);
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));

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
                        comp.setBackground(new Color(255, 235, 238));
                        comp.setForeground(new Color(192, 57, 43));
                    }
                }
                return comp;
            }
        };
        tablaProductos.setDefaultRenderer(Object.class, renderer);
    }

    private void actualizarOpcionesUnidadDesde(List<Product> productos) {
        if (cmbFiltroUnidad == null) {
            return;
        }

        String seleccionActual = (String) cmbFiltroUnidad.getSelectedItem();
        Set<String> unidades = productos.stream()
            .map(Product::getUnidadDeMedida)
            .filter(unidad -> unidad != null && !unidad.isBlank())
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        actualizandoFiltroUnidad = true;
        try {
            cmbFiltroUnidad.removeAllItems();
            cmbFiltroUnidad.addItem("Todas");
            unidades.forEach(cmbFiltroUnidad::addItem);

            boolean existeSeleccion = seleccionActual != null && unidades.stream()
                .anyMatch(u -> u.equalsIgnoreCase(seleccionActual));

            if (existeSeleccion) {
                cmbFiltroUnidad.setSelectedItem(seleccionActual);
            } else {
                cmbFiltroUnidad.setSelectedIndex(0);
            }
        } finally {
            actualizandoFiltroUnidad = false;
        }
    }

    private void actualizarChipResumen(int total, String criterio, String unidadSeleccionada) {
        if (lblResumenProductos == null) {
            return;
        }

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
            ? new Color(192, 57, 43)
            : new Color(80, 80, 80));
    }

    private void agregarProducto() {
        logger.accept("Productos: abriendo diálogo de alta");
        ProductDialogForm dialog = new ProductDialogForm(owner, true);
        if (dialog.isGuardadoExitoso()) {
            requestRefresh("Post Alta Producto");
            refreshDashboard();
            logger.accept("Productos: registro agregado correctamente");
        } else {
            logger.accept("Productos: alta cancelada por el usuario");
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un producto",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloProductos.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(owner,
            "¿Eliminar producto " + id + "?", "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                ProductServices.deleteProduct(id);
                JOptionPane.showMessageDialog(owner, "Producto eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                requestRefresh("Post Eliminación Producto");
                refreshDashboard();
                logger.accept("Productos: eliminado " + id);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(owner,
                    "Error al eliminar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                logger.accept("Productos: error al eliminar " + id + " → " + e.getMessage());
            }
        }
    }

    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un producto",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloProductos.getValueAt(fila, 0);
        try {
            Product producto = ProductServices.getProductById(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(owner, "El producto ya no existe",
                    "Error", JOptionPane.ERROR_MESSAGE);
                requestRefresh("Resincronización tras ausencia");
                return;
            }

            ProductDialogForm dialog = new ProductDialogForm(owner, true, producto);
            if (dialog.isGuardadoExitoso()) {
                requestRefresh("Post Edición Producto");
                refreshDashboard();
                logger.accept("Productos: edición exitosa " + id);
            } else {
                logger.accept("Productos: edición cancelada " + id);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Productos: error al preparar edición → " + e.getMessage());
        }
    }

    private void mostrarDetallesProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un producto",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloProductos.getValueAt(fila, 0);
        try {
            Product producto = ProductServices.getProductById(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(owner, "No se encontró el producto",
                    "Error", JOptionPane.ERROR_MESSAGE);
                requestRefresh("Post Detalle Inconsistente");
                return;
            }

            double stock = MovementServices.getProductStock(id);
            String mensaje = "ID: " + producto.getId() +
                "\nNombre: " + producto.getName() +
                "\nUnidad: " + producto.getUnidadDeMedida() +
                "\nPrecio: " + UIUtils.formatCurrency(producto.getPriceXUnd()) +
                "\nStock actual: " + String.format("%,.2f", stock);

            JOptionPane.showMessageDialog(owner, mensaje,
                "Detalle de producto", JOptionPane.INFORMATION_MESSAGE);
            logger.accept("Productos: detalles consultados para " + id);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Productos: error al mostrar detalles → " + e.getMessage());
        }
    }

    public void requestCreationShortcut() {
        agregarProducto();
    }

    private void setBusyCursor(boolean busy) {
        Cursor cursor = busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        if (owner != null) {
            owner.setCursor(cursor);
        }
        setCursor(cursor);
    }

    private void refreshDashboard() {
        if (dashboardRefresh != null) {
            dashboardRefresh.run();
        }
    }
}
