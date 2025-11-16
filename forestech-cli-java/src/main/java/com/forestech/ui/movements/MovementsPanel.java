package com.forestech.ui.movements;

import com.forestech.enums.MovementType;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Movement;
import com.forestech.services.MovementServices;
import com.forestech.ui.MovementDialogForm;
import com.forestech.ui.utils.AsyncLoadManager;
import com.forestech.ui.utils.CatalogCache;
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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Panel de gestión de movimientos.
 */
public class MovementsPanel extends JPanel {

    private final JFrame owner;
    private final Consumer<String> logger;
    private final Runnable dashboardRefresh;
    private final Consumer<String> productReloadRequest;
    private final AsyncLoadManager loadManager;

    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;
    private JTextField txtBuscarMovimiento;
    private JComboBox<String> cmbFiltroTipoMovimiento;
    private JTextField txtFechaDesdeMovimiento;
    private JTextField txtFechaHastaMovimiento;
    private JLabel lblResumenMovimientos;

    private List<Movement> movimientosVisibles = List.of();
    private Map<String, String> cacheNombresProductos = new HashMap<>();
    private Map<String, String> cacheNombresVehiculos = new HashMap<>();

    public MovementsPanel(JFrame owner,
                          Consumer<String> logger,
                          Runnable dashboardRefresh,
                          Consumer<String> productReloadRequest) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.productReloadRequest = productReloadRequest;
        this.loadManager = new AsyncLoadManager("Movimientos", logger, this::cargarMovimientos);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("📊 GESTIÓN DE MOVIMIENTOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel encabezado = new JPanel(new BorderLayout(0, 5));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(crearPanelFiltros(), BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        String[] columnas = {
            "ID", "Tipo", "Producto", "Vehículo", "Cantidad (L)",
            "Unidad", "Factura", "Precio Unitario", "Subtotal", "Fecha"
        };
        modeloMovimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setRowHeight(25);
        tablaMovimientos.setAutoCreateRowSorter(true);
        UIUtils.styleTable(tablaMovimientos);
        tablaMovimientos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaMovimientos.getSelectedRow() != -1) {
                    verDetallesMovimiento();
                }
            }
        });

        add(new JScrollPane(tablaMovimientos), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFiltros() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));

        JPanel panelControles = new JPanel(new GridBagLayout());
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        txtBuscarMovimiento = new JTextField(18);
        txtBuscarMovimiento.setToolTipText("ID, producto o vehículo");
        txtBuscarMovimiento.addActionListener(e -> requestRefresh("Enter búsqueda movimientos"));

        cmbFiltroTipoMovimiento = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA"});
        cmbFiltroTipoMovimiento.setPreferredSize(new java.awt.Dimension(140, 28));
        cmbFiltroTipoMovimiento.addActionListener(e -> requestRefresh("Filtro tipo movimientos"));

        txtFechaDesdeMovimiento = new JTextField(10);
        txtFechaDesdeMovimiento.setToolTipText("Desde (YYYY-MM-DD)");
        txtFechaDesdeMovimiento.addActionListener(e -> requestRefresh("Enter fecha desde"));

        txtFechaHastaMovimiento = new JTextField(10);
        txtFechaHastaMovimiento.setToolTipText("Hasta (YYYY-MM-DD)");
        txtFechaHastaMovimiento.addActionListener(e -> requestRefresh("Enter fecha hasta"));

        JButton btnAplicar = new JButton("Aplicar filtros");
        btnAplicar.addActionListener(e -> requestRefresh("Botón Aplicar filtros Movimientos"));

        JButton btnHoy = new JButton("Solo hoy");
        btnHoy.addActionListener(e -> {
            String hoy = LocalDate.now().toString();
            txtFechaDesdeMovimiento.setText(hoy);
            txtFechaHastaMovimiento.setText(hoy);
            requestRefresh("Filtro Solo Hoy");
        });

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            txtBuscarMovimiento.setText("");
            cmbFiltroTipoMovimiento.setSelectedIndex(0);
            txtFechaDesdeMovimiento.setText("");
            txtFechaHastaMovimiento.setText("");
            requestRefresh("Limpiar filtros Movimientos");
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelControles.add(new JLabel("🔍 ID/Producto/Vehículo:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(txtBuscarMovimiento, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelControles.add(new JLabel("⚙️ Tipo:"), gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        panelControles.add(cmbFiltroTipoMovimiento, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panelControles.add(new JLabel("📅 Desde:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(txtFechaDesdeMovimiento, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelControles.add(new JLabel("Hasta:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelControles.add(txtFechaHastaMovimiento, gbc);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 4;
        panelControles.add(btnAplicar, gbc);

        gbc.gridx = 5;
        panelControles.add(btnHoy, gbc);

        gbc.gridx = 6;
        panelControles.add(btnLimpiar, gbc);

        lblResumenMovimientos = new JLabel("Mostrando todos los movimientos");
        lblResumenMovimientos.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenMovimientos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenMovimientos, BorderLayout.SOUTH);
        return panelFiltros;
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(155, 89, 182));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.addActionListener(e -> registrarMovimiento());
        panelBotones.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarMovimiento());
        panelBotones.add(btnEditar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> verDetallesMovimiento());
        panelBotones.add(btnDetalles);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarMovimiento());
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> requestRefresh("Botón Refrescar Movimientos"));
        panelBotones.add(btnRefrescar);

        return panelBotones;
    }

    public void requestRefresh(String origin) {
        loadManager.requestLoad(origin);
    }

    public void cancelCurrentLoad() {
        loadManager.cancelCurrentLoad();
    }

    private void cargarMovimientos(String origin) {
        final String criterio = txtBuscarMovimiento != null
            ? txtBuscarMovimiento.getText().trim()
            : "";
        final String tipoSeleccionado = (cmbFiltroTipoMovimiento != null &&
            cmbFiltroTipoMovimiento.getSelectedItem() != null)
            ? cmbFiltroTipoMovimiento.getSelectedItem().toString()
            : "Todos";

        final LocalDate fechaDesde;
        final LocalDate fechaHasta;
        try {
            fechaDesde = parsearFechaFiltro(txtFechaDesdeMovimiento);
            fechaHasta = parsearFechaFiltro(txtFechaHastaMovimiento);
            if (fechaDesde != null && fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
                throw new IllegalArgumentException("La fecha \"Hasta\" no puede ser anterior a \"Desde\"");
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(owner,
                ex.getMessage(),
                "Filtro de fecha inválido",
                JOptionPane.WARNING_MESSAGE);
            logger.accept("Movimientos: filtros inválidos → " + ex.getMessage());
            loadManager.finish(0);
            return;
        }

        setBusyCursor(true);
        final long inicio = System.currentTimeMillis();
        logger.accept("Movimientos: iniciando carga (origen: " + origin + ")");

        final String terminoBusqueda = criterio.toLowerCase();

        SwingWorker<MovimientosCargaResult, Void> worker = new SwingWorker<>() {
            @Override
            protected MovimientosCargaResult doInBackground() throws Exception {
                List<Movement> movimientos = MovementServices.getAllMovements();

                // Usar CatalogCache en lugar de consultas duplicadas
                Map<String, String> productosNombres = CatalogCache.getInstance().getProductNames();
                Map<String, String> vehiculosNombres = CatalogCache.getInstance().getVehicleNames();

                List<Movement> filtrados = movimientos.stream()
                    .filter(m -> coincideConTipo(m, tipoSeleccionado))
                    .filter(m -> coincideConFecha(m, fechaDesde, fechaHasta))
                    .filter(m -> coincideConTexto(m, terminoBusqueda, productosNombres, vehiculosNombres))
                    .collect(Collectors.toList());

                long totalEntradas = 0;
                long totalSalidas = 0;
                double litrosEntradas = 0;
                double litrosSalidas = 0;

                for (Movement movimiento : filtrados) {
                    MovementType type = movimiento.getMovementType();
                    if (MovementType.ENTRADA.equals(type)) {
                        totalEntradas++;
                        litrosEntradas += movimiento.getQuantity();
                    } else if (MovementType.SALIDA.equals(type)) {
                        totalSalidas++;
                        litrosSalidas += movimiento.getQuantity();
                    }
                }

                return new MovimientosCargaResult(
                    filtrados,
                    productosNombres,
                    vehiculosNombres,
                    totalEntradas,
                    litrosEntradas,
                    totalSalidas,
                    litrosSalidas
                );
            }

            @Override
            protected void done() {
                try {
                    MovimientosCargaResult resultado = get();
                    movimientosVisibles = resultado.movimientos;
                    cacheNombresProductos = resultado.productos;
                    cacheNombresVehiculos = resultado.vehiculos;

                    modeloMovimientos.setRowCount(0);
                    for (Movement movimiento : movimientosVisibles) {
                        String typeLabel = movimiento.getMovementType() != null
                            ? movimiento.getMovementType().getCode()
                            : "—";
                        String measurementUnit = movimiento.getMeasurementUnitCode() != null
                            ? movimiento.getMeasurementUnitCode()
                            : "—";
                        String invoiceLabel = movimiento.getInvoiceNumber() != null
                            ? movimiento.getInvoiceNumber()
                            : "—";
                        String createdAtRaw = movimiento.getCreatedAt() != null
                            ? movimiento.getCreatedAt().toString()
                            : null;

                        modeloMovimientos.addRow(new Object[]{
                            movimiento.getId(),
                            typeLabel,
                            construirEtiquetaProducto(movimiento.getProductId()),
                            construirEtiquetaVehiculo(movimiento.getVehicleId()),
                            String.format("%,.2f", movimiento.getQuantity()),
                            measurementUnit,
                            invoiceLabel,
                            UIUtils.formatCurrency(movimiento.getUnitPrice()),
                            UIUtils.formatCurrency(movimiento.getQuantity() * movimiento.getUnitPrice()),
                            UIUtils.formatMovementDate(createdAtRaw)
                        });
                    }

                    actualizarResumenMovimientos(resultado);
                    logger.accept(String.format(
                        "Movimientos: carga completada en %d ms (%d registros)",
                        System.currentTimeMillis() - inicio,
                        movimientosVisibles.size()
                    ));
                } catch (ExecutionException ex) {
                    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(owner,
                        "Error al cargar movimientos: " + mensaje,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    logger.accept("Movimientos: error al cargar → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Movimientos: carga interrumpida");
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

    public void registrarMovimiento() {
        MovementDialogForm dialog = new MovementDialogForm(owner, true);
        if (dialog.isGuardadoExitoso()) {
            logger.accept("Movimientos: registro exitoso desde formulario");
            requestRefresh("Nuevo movimiento");
            requestProductosRecalc("Recalcular stock tras movimiento");
            refreshDashboard();
        }
    }

    private void editarMovimiento() {
        String movimientoId = obtenerMovimientoSeleccionado();
        if (movimientoId == null) {
            return;
        }

        try {
            Movement movimiento;
            try {
                movimiento = MovementServices.getMovementById(movimientoId);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(owner,
                    "Error al buscar movimiento: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (movimiento == null) {
                JOptionPane.showMessageDialog(owner,
                    "El movimiento ya no existe. Actualizando lista...",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
                requestRefresh("Movimiento desaparecido");
                return;
            }

            String cantidadStr = JOptionPane.showInputDialog(owner,
                "Nueva cantidad (L)", movimiento.getQuantity());
            if (cantidadStr == null) {
                return;
            }

            String precioStr = JOptionPane.showInputDialog(owner,
                "Nuevo precio unitario", movimiento.getUnitPrice());
            if (precioStr == null) {
                return;
            }

            double nuevaCantidad = Double.parseDouble(cantidadStr);
            double nuevoPrecio = Double.parseDouble(precioStr);

            try {
                MovementServices.updateMovement(movimientoId, nuevaCantidad, nuevoPrecio);
                JOptionPane.showMessageDialog(owner,
                    "Movimiento actualizado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                logger.accept("Movimientos: edición aplicada sobre " + movimientoId);
                requestRefresh("Edición movimiento");
                requestProductosRecalc("Recalcular stock tras edición movimiento");
                refreshDashboard();
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(owner,
                    "Error al actualizar movimiento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner,
                "Valores numéricos inválidos",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMovimiento() {
        String movimientoId = obtenerMovimientoSeleccionado();
        if (movimientoId == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(owner,
            "¿Eliminar movimiento " + movimientoId + "?", "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (MovementServices.deleteMovement(movimientoId)) {
                JOptionPane.showMessageDialog(owner,
                    "Movimiento eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                logger.accept("Movimientos: eliminado " + movimientoId);
                requestRefresh("Eliminación movimiento");
                requestProductosRecalc("Recalcular stock tras eliminación");
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(owner,
                    "No se encontró el movimiento",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(owner,
                "Error al eliminar movimiento: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetallesMovimiento() {
        String movimientoId = obtenerMovimientoSeleccionado();
        if (movimientoId == null) {
            return;
        }

        try {
            Movement movimiento = MovementServices.getMovementById(movimientoId);
            if (movimiento == null) {
                JOptionPane.showMessageDialog(owner,
                    "El movimiento ya no existe",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                requestRefresh("Movimiento faltante");
                return;
            }

            String detalle = """
                ID: %s
                Tipo: %s
                Producto: %s
                Vehículo: %s
                Cantidad: %,.2f %s
                Precio unitario: %s
                Subtotal: %s
                Factura: %s
                Fecha: %s
                """.formatted(
                movimiento.getId(),
                movimiento.getMovementType() != null ? movimiento.getMovementType().getCode() : "—",
                construirEtiquetaProducto(movimiento.getProductId()),
                construirEtiquetaVehiculo(movimiento.getVehicleId()),
                movimiento.getQuantity(),
                movimiento.getMeasurementUnitCode() != null ? movimiento.getMeasurementUnitCode() : "—",
                UIUtils.formatCurrency(movimiento.getUnitPrice()),
                UIUtils.formatCurrency(movimiento.getQuantity() * movimiento.getUnitPrice()),
                movimiento.getInvoiceNumber() != null ? movimiento.getInvoiceNumber() : "—",
                UIUtils.formatMovementDate(movimiento.getCreatedAt() != null ? movimiento.getCreatedAt().toString() : null)
            );

            javax.swing.JTextArea area = new javax.swing.JTextArea(detalle);
            area.setEditable(false);
            area.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(owner, new javax.swing.JScrollPane(area),
                "Detalle de movimiento", JOptionPane.INFORMATION_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(owner,
                "Error al consultar movimiento: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerMovimientoSeleccionado() {
        if (tablaMovimientos == null || tablaMovimientos.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(owner,
                "Selecciona un movimiento primero",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int filaVista = tablaMovimientos.getSelectedRow();
        int filaModelo = tablaMovimientos.convertRowIndexToModel(filaVista);
        return (String) modeloMovimientos.getValueAt(filaModelo, 0);
    }

    private void actualizarResumenMovimientos(MovimientosCargaResult resultado) {
        if (lblResumenMovimientos == null) {
            return;
        }

        lblResumenMovimientos.setText(String.format(
            "Mostrando %d movimientos | Entradas: %d (%,.2f L) | Salidas: %d (%,.2f L)",
            movimientosVisibles.size(),
            resultado.totalEntradas,
            resultado.litrosEntradas,
            resultado.totalSalidas,
            resultado.litrosSalidas
        ));
    }

    private LocalDate parsearFechaFiltro(JTextField campo) {
        if (campo == null) {
            return null;
        }
        String valor = campo.getText().trim();
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + valor);
        }
    }

    private boolean coincideConTipo(Movement movimiento, String filtroTipo) {
        if ("Todos".equalsIgnoreCase(filtroTipo)) {
            return true;
        }
        MovementType type = movimiento.getMovementType();
        return type != null && type.getCode().equalsIgnoreCase(filtroTipo);
    }

    private boolean coincideConFecha(Movement movimiento, LocalDate desde, LocalDate hasta) {
        if (movimiento.getCreatedAt() == null) {
            return false;
        }
        LocalDate fechaMovimiento = movimiento.getCreatedAt().toLocalDate();
        if (fechaMovimiento == null) {
            return false;
        }

        boolean despuesDeDesde = (desde == null) || !fechaMovimiento.isBefore(desde);
        boolean antesDeHasta = (hasta == null) || !fechaMovimiento.isAfter(hasta);
        return despuesDeDesde && antesDeHasta;
    }

    private boolean coincideConTexto(Movement movimiento, String termino,
                                     Map<String, String> nombresProductos,
                                     Map<String, String> nombresVehiculos) {
        if (termino == null || termino.isBlank()) {
            return true;
        }

        String lower = termino.toLowerCase();
        return contains(movimiento.getId(), lower)
            || contains(movimiento.getProductId(), lower)
            || contains(nombresProductos.get(movimiento.getProductId()), lower)
            || contains(movimiento.getVehicleId(), lower)
            || contains(nombresVehiculos.get(movimiento.getVehicleId()), lower)
            || contains(movimiento.getInvoiceNumber(), lower);
    }

    private boolean contains(String valor, String termino) {
        return valor != null && valor.toLowerCase().contains(termino);
    }

    private String construirEtiquetaProducto(String productId) {
        if (productId == null) {
            return "—";
        }
        String nombre = cacheNombresProductos.getOrDefault(productId, productId);
        return productId + " - " + nombre;
    }

    private String construirEtiquetaVehiculo(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return "—";
        }
        String nombre = cacheNombresVehiculos.getOrDefault(vehicleId, vehicleId);
        return vehicleId + " - " + nombre;
    }

    private void requestProductosRecalc(String origin) {
        if (productReloadRequest != null) {
            productReloadRequest.accept(origin);
        }
    }

    private void refreshDashboard() {
        if (dashboardRefresh != null) {
            dashboardRefresh.run();
        }
    }

    private void setBusyCursor(boolean busy) {
        Cursor cursor = busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        if (owner != null) {
            owner.setCursor(cursor);
        }
        setCursor(cursor);
    }

    private static class MovimientosCargaResult {
        final List<Movement> movimientos;
        final Map<String, String> productos;
        final Map<String, String> vehiculos;
        final long totalEntradas;
        final double litrosEntradas;
        final long totalSalidas;
        final double litrosSalidas;

        MovimientosCargaResult(List<Movement> movimientos,
                               Map<String, String> productos,
                               Map<String, String> vehiculos,
                               long totalEntradas,
                               double litrosEntradas,
                               long totalSalidas,
                               double litrosSalidas) {
            this.movimientos = movimientos;
            this.productos = productos;
            this.vehiculos = vehiculos;
            this.totalEntradas = totalEntradas;
            this.litrosEntradas = litrosEntradas;
            this.totalSalidas = totalSalidas;
            this.litrosSalidas = litrosSalidas;
        }
    }
}
