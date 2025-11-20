package com.forestech.presentation.ui.movements;

import com.forestech.modules.inventory.models.Movement;
import com.forestech.modules.catalog.models.Product;
import com.forestech.modules.fleet.models.Vehicle;
import com.forestech.modules.invoicing.models.Invoice;
import com.forestech.presentation.clients.MovementServiceClient;
import com.forestech.presentation.clients.ProductServiceClient;
import com.forestech.presentation.clients.VehicleServiceClient;
import com.forestech.presentation.clients.InvoiceServiceClient;
import com.forestech.presentation.ui.utils.CatalogCache;
import com.forestech.presentation.ui.utils.UIUtils;
import com.forestech.presentation.ui.utils.FontScheme;
import com.forestech.presentation.ui.utils.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Panel de gestión de movimientos (Vista MVP).
 * Responsabilidad: Solo UI - Botones, layouts, eventos.
 */
public class MovementsPanel extends JPanel implements MovementsContract.View {

    // Dependencias
    private final JFrame owner;
    private final Consumer<String> logger;
    private final Runnable dashboardRefresh;
    private final Consumer<String> productReloadRequest;

    // Presenter
    private final MovementsContract.Presenter presenter;

    // Componentes de UI
    private JTable movementsTable;
    private MovementsTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterType;
    private JTextField txtDateFrom;
    private JTextField txtDateTo;
    private JLabel lblSummary;

    public MovementsPanel(JFrame owner,
            Consumer<String> logger,
            Runnable dashboardRefresh,
            Consumer<String> productReloadRequest,
            MovementServiceClient movementClient,
            ProductServiceClient productClient,
            VehicleServiceClient vehicleClient,
            InvoiceServiceClient invoiceClient,
            CatalogCache catalogCache) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.productReloadRequest = productReloadRequest;

        // Inyectar todos los servicios en el Presenter
        this.presenter = new MovementsPresenter(this, movementClient, productClient, vehicleClient,
                invoiceClient, catalogCache, logger);

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
        tableModel = new MovementsTableModel(Map.of(), Map.of()); // Empty initially
        movementsTable = new JTable(tableModel);
        movementsTable.setRowHeight(25);
        movementsTable.setAutoCreateRowSorter(true);
        UIUtils.styleTable(movementsTable);
        movementsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && movementsTable.getSelectedRow() != -1) {
                    presenter.showDetails(getSelectedMovementId());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(movementsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Resumen y Botones
        JPanel footer = new JPanel(new BorderLayout());
        lblSummary = new JLabel("Cargando...");
        footer.add(lblSummary, BorderLayout.NORTH);
        footer.add(createButtonsPanel(), BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtSearch = new JTextField(15);
        txtSearch.setToolTipText("Buscar...");
        txtSearch.addActionListener(e -> presenter.loadMovements("Enter en búsqueda"));

        cmbFilterType = new JComboBox<>(new String[] { "TODOS", "ENTRADA", "SALIDA" });
        cmbFilterType.addActionListener(e -> presenter.loadMovements("Cambio filtro tipo"));

        txtDateFrom = new JTextField(8);
        txtDateFrom.setToolTipText("YYYY-MM-DD");
        txtDateTo = new JTextField(8);
        txtDateTo.setToolTipText("YYYY-MM-DD");

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> presenter.loadMovements("Botón Filtrar"));

        panel.add(new JLabel("Buscar:"));
        panel.add(txtSearch);
        panel.add(new JLabel("Tipo:"));
        panel.add(cmbFilterType);
        panel.add(new JLabel("Desde:"));
        panel.add(txtDateFrom);
        panel.add(new JLabel("Hasta:"));
        panel.add(txtDateTo);
        panel.add(btnFiltrar);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnNuevo = new JButton("Nuevo Movimiento");
        btnNuevo.setBackground(ColorScheme.BUTTON_SUCCESS_BG);
        btnNuevo.setForeground(ColorScheme.BUTTON_PRIMARY_FG);
        btnNuevo.addActionListener(e -> presenter.registerMovement());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(ColorScheme.BUTTON_DANGER_BG);
        btnEliminar.setForeground(ColorScheme.BUTTON_PRIMARY_FG);
        btnEliminar.addActionListener(e -> presenter.deleteMovement(getSelectedMovementId()));

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> presenter.showDetails(getSelectedMovementId()));

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> presenter.loadMovements("Botón Actualizar"));

        panel.add(btnNuevo);
        panel.add(btnDetalles);
        panel.add(btnEliminar);
        panel.add(btnRefresh);

        return panel;
    }

    private String getSelectedMovementId() {
        int row = movementsTable.getSelectedRow();
        if (row == -1)
            return null;
        return (String) tableModel.getValueAt(movementsTable.convertRowIndexToModel(row), 0); // ID column
    }

    // =============================================================================================
    // IMPLEMENTACIÓN DE VIEW
    // =============================================================================================

    @Override
    public void showMovements(List<Movement> movements, Map<String, String> productNames,
            Map<String, String> vehicleNames) {
        tableModel.setData(movements, productNames, vehicleNames);
    }

    @Override
    public void showLoading(boolean isLoading) {
        setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        movementsTable.setEnabled(!isLoading);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean showConfirmation(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public void showMovementDetails(String details) {
        JTextArea area = new JTextArea(details);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scroll, "Detalles del Movimiento", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void updateSummary(String summaryText) {
        lblSummary.setText(summaryText);
    }

    @Override
    public String getSearchTerm() {
        return txtSearch.getText().trim();
    }

    @Override
    public String getFilterType() {
        String selected = (String) cmbFilterType.getSelectedItem();
        return "TODOS".equals(selected) ? null : selected;
    }

    @Override
    public LocalDate getDateFrom() {
        return UIUtils.parseDate(txtDateFrom.getText().trim());
    }

    @Override
    public LocalDate getDateTo() {
        return UIUtils.parseDate(txtDateTo.getText().trim());
    }

    @Override
    public void showMovementForm(Movement movement, List<Product> products, List<Vehicle> vehicles,
            List<Invoice> invoices) {
        // Crear el diálogo con los datos precargados (sin servicios)
        MovementDialogForm dialog = new MovementDialogForm(owner, true, products, vehicles, invoices);

        // Si es edición, setear datos (TODO: Implementar setMovement en Dialog si es
        // necesario)
        // Por ahora, MovementDialogForm parece solo soportar creación (no tiene
        // setMovement)
        // Si 'movement' no es null, deberíamos llenar el form.

        dialog.setVisible(true); // Bloquea hasta que se cierra

        Movement result = dialog.getResult();
        if (result != null) {
            // Si el usuario confirmó, guardar a través del Presenter
            presenter.saveMovement(result);
        }
    }

    @Override
    public void refreshDashboard() {
        if (dashboardRefresh != null)
            dashboardRefresh.run();
    }

    @Override
    public void requestProductsRecalc(String origin) {
        if (productReloadRequest != null)
            productReloadRequest.accept(origin);
    }

    /**
     * Método para compatibilidad con ForestechProfessionalApp.
     * Delega en el presenter.
     */
    public void requestRefresh(String origin) {
        presenter.loadMovements(origin);
    }

    /**
     * Método para compatibilidad con ForestechProfessionalApp.
     * Delega en el presenter.
     */
    public void registrarMovimiento() {
        presenter.registerMovement();
    }

    /**
     * Método para compatibilidad con ForestechProfessionalApp.
     * Delega en el presenter.
     */
    public void cancelCurrentLoad() {
        presenter.cancelCurrentOperation();
    }
}
