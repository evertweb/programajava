package com.forestech.presentation.ui.movements;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.InsufficientStockException;
import com.forestech.data.models.Movement;
import com.forestech.business.services.FacturaServices;
import com.forestech.business.services.MovementServices;
import com.forestech.business.services.ProductServices;
import com.forestech.business.services.VehicleServices;
import com.forestech.presentation.ui.utils.AsyncLoadManager;
import com.forestech.presentation.ui.utils.CatalogCache;
import com.forestech.presentation.ui.utils.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import com.forestech.presentation.ui.utils.FontScheme;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import com.forestech.presentation.ui.utils.ColorScheme;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Panel de gestión de movimientos (Vista MVC).
 * Responsabilidad: Solo UI - Botones, layouts, eventos.
 */
public class MovementsPanel extends JPanel {

    // Dependencias
    private final JFrame owner;
    private final Consumer<String> logger;
    private final Runnable dashboardRefresh;
    private final Consumer<String> productReloadRequest;
    private final AsyncLoadManager loadManager;

    // Services (Dependency Injection)
    private final MovementServices movementServices;
    private final ProductServices productServices;
    private final VehicleServices vehicleServices;
    private final FacturaServices facturaServices;

    // Componentes de UI
    private JTable movementsTable;
    private MovementsTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterType;
    private JTextField txtDateFrom;
    private JTextField txtDateTo;
    private JLabel lblSummary;

    // Datos
    private List<Movement> visibleMovements = List.of();
    private Map<String, String> productNamesCache = new HashMap<>();
    private Map<String, String> vehicleNamesCache = new HashMap<>();

    // Lógica de negocio
    private final MovementsDataLoader dataLoader;

    public MovementsPanel(JFrame owner,
            Consumer<String> logger,
            Runnable dashboardRefresh,
            Consumer<String> productReloadRequest,
            MovementServices movementServices,
            ProductServices productServices,
            VehicleServices vehicleServices,
            FacturaServices facturaServices,
            CatalogCache catalogCache) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.productReloadRequest = productReloadRequest;
        this.movementServices = movementServices;
        this.productServices = productServices;
        this.vehicleServices = vehicleServices;
        this.facturaServices = facturaServices;
        this.loadManager = new AsyncLoadManager("Movimientos", logger, this::loadMovements);
        this.dataLoader = new MovementsDataLoader(movementServices, catalogCache);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        // Encabezado
        JLabel title = new JLabel("GESTIÓN DE MOVIMIENTOS", JLabel.CENTER);
        title.setFont(FontScheme.HEADER_2);

        JPanel header = new JPanel(new BorderLayout(0, 5));
        header.add(title, BorderLayout.NORTH);
        header.add(createFiltersPanel(), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Tabla
        tableModel = new MovementsTableModel(productNamesCache, vehicleNamesCache);
        movementsTable = new JTable(tableModel);
        movementsTable.setRowHeight(25);
        movementsTable.setAutoCreateRowSorter(true);
        UIUtils.styleTable(movementsTable);
        movementsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && movementsTable.getSelectedRow() != -1) {
                    viewMovementDetails();
                }
            }
        });

        add(new JScrollPane(movementsTable), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new BorderLayout(0, 5));
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Componentes de filtro
        txtSearch = new JTextField(18);
        txtSearch.setToolTipText("ID, producto o vehículo");
        txtSearch.addActionListener(e -> requestRefresh("Enter búsqueda movimientos"));

        cmbFilterType = new JComboBox<>(new String[] { "Todos", "ENTRADA", "SALIDA" });
        cmbFilterType.setPreferredSize(new java.awt.Dimension(140, 28));
        cmbFilterType.addActionListener(e -> requestRefresh("Filtro tipo movimientos"));

        txtDateFrom = new JTextField(10);
        txtDateFrom.setToolTipText("Desde (YYYY-MM-DD)");
        txtDateFrom.addActionListener(e -> requestRefresh("Enter fecha desde"));

        txtDateTo = new JTextField(10);
        txtDateTo.setToolTipText("Hasta (YYYY-MM-DD)");
        txtDateTo.addActionListener(e -> requestRefresh("Enter fecha hasta"));

        JButton btnApply = new JButton("Aplicar filtros");
        btnApply.addActionListener(e -> requestRefresh("Botón Aplicar filtros Movimientos"));

        JButton btnToday = new JButton("Solo hoy");
        btnToday.addActionListener(e -> {
            String today = LocalDate.now().toString();
            txtDateFrom.setText(today);
            txtDateTo.setText(today);
            requestRefresh("Filtro Solo Hoy");
        });

        JButton btnClear = new JButton("Limpiar");
        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            cmbFilterType.setSelectedIndex(0);
            txtDateFrom.setText("");
            txtDateTo.setText("");
            requestRefresh("Limpiar filtros Movimientos");
        });

        // Layout de componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlsPanel.add(new JLabel("ID/Producto/Vehículo:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(txtSearch, gbc);
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        controlsPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 5;
        controlsPanel.add(cmbFilterType, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        controlsPanel.add(new JLabel("Desde:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(txtDateFrom, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        controlsPanel.add(new JLabel("Hasta:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(txtDateTo, gbc);
        gbc.gridx = 4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        controlsPanel.add(btnApply, gbc);
        gbc.gridx = 5;
        controlsPanel.add(btnToday, gbc);
        gbc.gridx = 6;
        controlsPanel.add(btnClear, gbc);

        lblSummary = new JLabel("Mostrando todos los movimientos");
        lblSummary.setFont(FontScheme.SMALL_ITALIC);
        lblSummary.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        filtersPanel.add(controlsPanel, BorderLayout.CENTER);
        filtersPanel.add(lblSummary, BorderLayout.SOUTH);
        return filtersPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnRegister = new JButton("Registrar");
        btnRegister.setBackground(ColorScheme.BUTTON_PRIMARY_BG);
        btnRegister.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btnRegister.addActionListener(e -> registerMovement());
        buttonsPanel.add(btnRegister);

        JButton btnEdit = new JButton("Editar");
        btnEdit.setBackground(ColorScheme.BUTTON_WARNING_BG);
        btnEdit.setForeground(ColorScheme.TEXT_PRIMARY);
        btnEdit.addActionListener(e -> editMovement());
        buttonsPanel.add(btnEdit);

        JButton btnDetails = new JButton("Ver Detalles");
        btnDetails.addActionListener(e -> viewMovementDetails());
        buttonsPanel.add(btnDetails);

        JButton btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(ColorScheme.BUTTON_DANGER_BG);
        btnDelete.addActionListener(e -> deleteMovement());
        buttonsPanel.add(btnDelete);

        JButton btnRefresh = new JButton("Refrescar");
        btnRefresh.addActionListener(e -> requestRefresh("Botón Refrescar Movimientos"));
        buttonsPanel.add(btnRefresh);

        return buttonsPanel;
    }

    public void requestRefresh(String origin) {
        loadManager.requestLoad(origin);
    }

    public void cancelCurrentLoad() {
        loadManager.cancelCurrentLoad();
    }

    /**
     * Método público para registrar un movimiento (llamado desde Dashboard).
     */
    public void registrarMovimiento() {
        registerMovement();
    }

    private void loadMovements(String origin) {
        // Obtener valores de filtros
        final String searchTerm = txtSearch != null ? txtSearch.getText().trim() : "";
        final String selectedType = (cmbFilterType != null && cmbFilterType.getSelectedItem() != null)
                ? cmbFilterType.getSelectedItem().toString()
                : "Todos";

        // Parsear fechas
        final LocalDate startDate;
        final LocalDate endDate;
        try {
            startDate = MovementsFormatter.parseDateFilter(txtDateFrom != null ? txtDateFrom.getText() : "");
            endDate = MovementsFormatter.parseDateFilter(txtDateTo != null ? txtDateTo.getText() : "");
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("La fecha \"Hasta\" no puede ser anterior a \"Desde\"");
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(owner, ex.getMessage(), "Filtro de fecha inválido",
                    JOptionPane.WARNING_MESSAGE);
            logger.accept("Movimientos: filtros inválidos → " + ex.getMessage());
            loadManager.finish(0);
            return;
        }

        setBusyCursor(true);
        final long startTime = System.currentTimeMillis();
        logger.accept("Movimientos: iniciando carga (origen: " + origin + ")");

        SwingWorker<MovementsDataLoader.MovementsLoadResult, Void> worker = new SwingWorker<>() {
            @Override
            protected MovementsDataLoader.MovementsLoadResult doInBackground() throws Exception {
                return dataLoader.loadMovements(searchTerm, selectedType, startDate, endDate);
            }

            @Override
            protected void done() {
                try {
                    MovementsDataLoader.MovementsLoadResult result = get();
                    visibleMovements = result.movements;
                    productNamesCache = result.productNames;
                    vehicleNamesCache = result.vehicleNames;

                    // Actualizar caches en el tableModel
                    tableModel = new MovementsTableModel(productNamesCache, vehicleNamesCache);
                    movementsTable.setModel(tableModel);

                    // Cargar datos en la tabla
                    tableModel.loadMovements(visibleMovements);

                    // Actualizar resumen
                    updateSummary(result);

                    logger.accept(String.format(
                            "Movimientos: carga completada en %d ms (%d registros)",
                            System.currentTimeMillis() - startTime,
                            visibleMovements.size()));
                } catch (ExecutionException ex) {
                    String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(owner, "Error al cargar movimientos: " + message, "Error",
                            JOptionPane.ERROR_MESSAGE);
                    logger.accept("Movimientos: error al cargar → " + message);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Movimientos: carga interrumpida");
                } finally {
                    finishLoad(startTime);
                }
            }
        };

        loadManager.registerWorker(worker);
        worker.execute();
    }

    private void finishLoad(long startTime) {
        setBusyCursor(false);
        loadManager.finish(startTime);
    }

    private void updateSummary(MovementsDataLoader.MovementsLoadResult result) {
        if (lblSummary == null)
            return;
        lblSummary.setText(MovementsFormatter.formatSummary(visibleMovements.size(), result.statistics));
    }

    private void registerMovement() {
        MovementDialogForm dialog = new MovementDialogForm(
                owner,
                true,
                movementServices,
                productServices,
                vehicleServices,
                facturaServices);
        if (dialog.isGuardadoExitoso()) {
            logger.accept("Movimientos: registro exitoso desde formulario");
            requestRefresh("Nuevo movimiento");
            requestProductsRecalc("Recalcular stock tras movimiento");
            refreshDashboard();
        }
    }

    private void editMovement() {
        String movementId = getSelectedMovementId();
        if (movementId == null)
            return;

        try {
            Movement movement = movementServices.getMovementById(movementId);
            if (movement == null) {
                JOptionPane.showMessageDialog(owner, "El movimiento ya no existe. Actualizando lista...", "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                requestRefresh("Movimiento desaparecido");
                return;
            }

            String quantityStr = JOptionPane.showInputDialog(owner, "Nueva cantidad (L)", movement.getQuantity());
            if (quantityStr == null)
                return;

            String priceStr = JOptionPane.showInputDialog(owner, "Nuevo precio unitario", movement.getUnitPrice());
            if (priceStr == null)
                return;

            double newQuantity = Double.parseDouble(quantityStr);
            double newPrice = Double.parseDouble(priceStr);

            movementServices.updateMovement(movementId, newQuantity, newPrice);
            JOptionPane.showMessageDialog(owner, "Movimiento actualizado correctamente", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            logger.accept("Movimientos: edición aplicada sobre " + movementId);
            requestRefresh("Edición movimiento");
            requestProductsRecalc("Recalcular stock tras edición movimiento");
            refreshDashboard();
        } catch (InsufficientStockException ex) {
            JOptionPane.showMessageDialog(owner, "Error: Stock insuficiente - " + ex.getMessage(), "Error de Stock",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(owner, "Error al actualizar movimiento: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(owner, "Valores numéricos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMovement() {
        String movementId = getSelectedMovementId();
        if (movementId == null)
            return;

        int confirmation = JOptionPane.showConfirmDialog(owner, "¿Eliminar movimiento " + movementId + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION)
            return;

        try {
            if (movementServices.deleteMovement(movementId)) {
                JOptionPane.showMessageDialog(owner, "Movimiento eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                logger.accept("Movimientos: eliminado " + movementId);
                requestRefresh("Eliminación movimiento");
                requestProductsRecalc("Recalcular stock tras eliminación");
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(owner, "No se encontró el movimiento", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(owner, "Error al eliminar movimiento: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMovementDetails() {
        String movementId = getSelectedMovementId();
        if (movementId == null)
            return;

        try {
            Movement movement = movementServices.getMovementById(movementId);
            if (movement == null) {
                JOptionPane.showMessageDialog(owner, "El movimiento ya no existe", "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                requestRefresh("Movimiento faltante");
                return;
            }

            String details = MovementsFormatter.formatMovementDetails(movement, productNamesCache, vehicleNamesCache);

            JTextArea area = new JTextArea(details);
            area.setEditable(false);
            area.setFont(FontScheme.MONOSPACED);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(owner, new JScrollPane(area), "Detalle de movimiento",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(owner, "Error al consultar movimiento: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedMovementId() {
        if (movementsTable == null || movementsTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un movimiento primero", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int viewRow = movementsTable.getSelectedRow();
        int modelRow = movementsTable.convertRowIndexToModel(viewRow);
        return tableModel.getMovementIdAt(modelRow);
    }

    private void requestProductsRecalc(String origin) {
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
}
