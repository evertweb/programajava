package com.forestech.ui.vehicles;

import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Vehicle;
import com.forestech.services.VehicleServices;
import com.forestech.ui.VehicleDialogForm;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Panel de gestión de vehículos.
 */
public class VehiclesPanel extends JPanel {

    private final JFrame owner;
    private final Consumer<String> logger;
    private final Runnable dashboardRefresh;
    private final AsyncLoadManager loadManager;
    private boolean suppressFilterEvents;

    // Services (Dependency Injection)
    private final VehicleServices vehicleServices;

    private JTable tablaVehiculos;
    private DefaultTableModel modeloVehiculos;
    private JTextField txtBuscarVehiculo;
    private JComboBox<String> cmbFiltroCategoria;
    private JLabel lblResumenVehiculos;

    public VehiclesPanel(JFrame owner,
                         Consumer<String> logger,
                         Runnable dashboardRefresh,
                         VehicleServices vehicleServices) {
        this.owner = owner;
        this.logger = logger;
        this.dashboardRefresh = dashboardRefresh;
        this.vehicleServices = vehicleServices;
        this.loadManager = new AsyncLoadManager("Vehículos", logger, this::refreshAsync);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("🚛 GESTIÓN DE VEHÍCULOS", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel encabezado = new JPanel(new BorderLayout(0, 5));
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(createFilterPanel(), BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Categoría", "Capacidad (L)", "Combustible", "Horómetro"};
        modeloVehiculos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloVehiculos);
        tablaVehiculos.setRowHeight(25);
        UIUtils.styleTable(tablaVehiculos);
        tablaVehiculos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaVehiculos.getSelectedRow() != -1) {
                    mostrarDetallesVehiculo();
                }
            }
        });

        add(new JScrollPane(tablaVehiculos), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        txtBuscarVehiculo = new JTextField(18);
        txtBuscarVehiculo.setToolTipText("Busca por ID o nombre y presiona Enter");
        txtBuscarVehiculo.addActionListener(e -> refresh());

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> refresh());

        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.addItem("Todas");
        cmbFiltroCategoria.setPreferredSize(new Dimension(160, 28));
        cmbFiltroCategoria.addActionListener(e -> {
            if (!suppressFilterEvents) {
                refresh();
            }
        });

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarVehiculo.setText("");
            cmbFiltroCategoria.setSelectedIndex(0);
            refresh();
        });

        controles.add(new JLabel("🔍 ID/Nombre:"));
        controles.add(txtBuscarVehiculo);
        controles.add(btnBuscar);
        controles.add(new JLabel("🏷️ Categoría:"));
        controles.add(cmbFiltroCategoria);
        controles.add(btnLimpiar);

        lblResumenVehiculos = new JLabel("Mostrando todos los vehículos");
        lblResumenVehiculos.setFont(new Font("Arial", Font.ITALIC, 12));
        lblResumenVehiculos.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        panelFiltros.add(controles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenVehiculos, BorderLayout.SOUTH);
        return panelFiltros;
    }

    private JPanel createButtonsPanel() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBackground(new Color(100, 200, 100));
        btnAgregar.addActionListener(e -> agregarVehiculo());
        panelBotones.add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 211, 105));
        btnEditar.addActionListener(e -> editarVehiculo());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 150, 150));
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        panelBotones.add(btnEliminar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.addActionListener(e -> mostrarDetallesVehiculo());
        panelBotones.add(btnDetalles);

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
        final String criterio = txtBuscarVehiculo != null ? txtBuscarVehiculo.getText().trim().toLowerCase() : "";
        final String categoriaSeleccionada = (cmbFiltroCategoria != null && cmbFiltroCategoria.getSelectedItem() != null)
            ? cmbFiltroCategoria.getSelectedItem().toString()
            : "Todas";

        setBusyState(true);
        final long inicio = System.currentTimeMillis();

        SwingWorker<VehiclesPayload, Void> worker = new SwingWorker<>() {
            @Override
            protected VehiclesPayload doInBackground() throws Exception {
                List<Vehicle> vehiculos = vehicleServices.getAllVehicles();
                List<Vehicle> filtrados = aplicarFiltros(vehiculos, criterio, categoriaSeleccionada);
                return new VehiclesPayload(vehiculos, filtrados, criterio, categoriaSeleccionada);
            }

            @Override
            protected void done() {
                try {
                    VehiclesPayload data = get();
                    cargarVehiculosEnTabla(data.vehiculosFiltrados());
                    actualizarCategoriasVehiculos(data.vehiculos());
                    actualizarResumenVehiculos(data.vehiculosFiltrados().size(), data.criterio(), data.categoria());
                    logger.accept(String.format("Vehículos: %d registros (%s)",
                        data.vehiculosFiltrados().size(), origin));
                } catch (ExecutionException ex) {
                    handleRefreshError(ex.getCause());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Vehículos: carga interrumpida");
                } finally {
                    setBusyState(false);
                    loadManager.finish(inicio);
                }
            }
        };

        // Registrar worker para cancelación antes de ejecutar
        loadManager.registerWorker(worker);
        worker.execute();
    }

    private List<Vehicle> aplicarFiltros(List<Vehicle> vehiculos,
                                         String criterio,
                                         String categoriaSeleccionada) {
        List<Vehicle> resultado = vehiculos;

        if (criterio != null && !criterio.isBlank()) {
            resultado = resultado.stream()
                .filter(v -> v.getId().toLowerCase().contains(criterio) ||
                    v.getName().toLowerCase().contains(criterio))
                .collect(Collectors.toList());
        }

        if (categoriaSeleccionada != null && !"Todas".equalsIgnoreCase(categoriaSeleccionada)) {
            String categoria = categoriaSeleccionada.toLowerCase();
            resultado = resultado.stream()
                .filter(v -> v.getCategory() != null && v.getCategory().getCode().toLowerCase().equals(categoria))
                .collect(Collectors.toList());
        }

        return resultado;
    }

    private void cargarVehiculosEnTabla(List<Vehicle> vehiculosFiltrados) {
        modeloVehiculos.setRowCount(0);
        for (Vehicle v : vehiculosFiltrados) {
            modeloVehiculos.addRow(new Object[]{
                v.getId(),
                v.getName(),
                v.getCategory(),
                String.format("%,.2f", v.getCapacity()),
                v.getFuelProductId() != null ? v.getFuelProductId() : "Sin asignar",
                v.hasHorometer() ? "Sí" : "No"
            });
        }
    }

    private void handleRefreshError(Throwable throwable) {
        Throwable cause = throwable instanceof DatabaseException ? throwable : new DatabaseException(
            throwable != null ? throwable.getMessage() : "Error desconocido");
        JOptionPane.showMessageDialog(owner,
            "Error al cargar vehículos: " + cause.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        logger.accept("Vehículos: error en carga → " + cause.getMessage());
    }

    private void actualizarCategoriasVehiculos(List<Vehicle> vehiculos) {
        if (cmbFiltroCategoria == null) {
            return;
        }

        String seleccionActual = (String) cmbFiltroCategoria.getSelectedItem();
        Set<String> categorias = vehiculos.stream()
            .map(Vehicle::getCategory)
            .filter(cat -> cat != null)
            .map(cat -> cat.getCode())
            .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));

        suppressFilterEvents = true;
        try {
            cmbFiltroCategoria.removeAllItems();
            cmbFiltroCategoria.addItem("Todas");
            categorias.forEach(cmbFiltroCategoria::addItem);

            if (seleccionActual != null && categorias.stream().anyMatch(c -> c.equalsIgnoreCase(seleccionActual))) {
                cmbFiltroCategoria.setSelectedItem(seleccionActual);
            } else {
                cmbFiltroCategoria.setSelectedIndex(0);
            }
        } finally {
            suppressFilterEvents = false;
        }
    }

    private void actualizarResumenVehiculos(int total, String criterio, String categoria) {
        if (lblResumenVehiculos == null) {
            return;
        }

        StringBuilder sb = new StringBuilder("Mostrando ")
            .append(total)
            .append(total == 1 ? " vehículo" : " vehículos");

        if (criterio != null && !criterio.isBlank()) {
            sb.append(" | Coincidencia: \"")
                .append(criterio)
                .append("\"");
        }

        if (categoria != null && !"Todas".equalsIgnoreCase(categoria)) {
            sb.append(" | Categoría: ")
                .append(categoria);
        }

        lblResumenVehiculos.setText(sb.toString());
        lblResumenVehiculos.setForeground(total == 0 ? new Color(192, 57, 43) : new Color(80, 80, 80));
    }

    private void setBusyState(boolean busy) {
        setCursor(busy ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR)
            : java.awt.Cursor.getDefaultCursor());
    }

    private void agregarVehiculo() {
        logger.accept("Vehículos: abriendo diálogo de alta");
        VehicleDialogForm dialog = new VehicleDialogForm(owner, true);
        if (dialog.isGuardadoExitoso()) {
            refresh();
            refreshDashboard();
            logger.accept("Vehículos: alta exitosa");
        }
    }

    private void editarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un vehículo",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloVehiculos.getValueAt(fila, 0);
        try {
            Vehicle vehiculo = vehicleServices.getVehicleById(id);
            if (vehiculo == null) {
                JOptionPane.showMessageDialog(owner, "El vehículo ya no existe",
                    "Error", JOptionPane.ERROR_MESSAGE);
                refresh();
                return;
            }

            VehicleDialogForm dialog = new VehicleDialogForm(owner, true, vehiculo);
            if (dialog.isGuardadoExitoso()) {
                refresh();
                refreshDashboard();
                logger.accept("Vehículos: edición exitosa " + id);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Vehículos: error al editar → " + e.getMessage());
        }
    }

    private void eliminarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un vehículo",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloVehiculos.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(owner,
            "¿Eliminar vehículo " + id + "?", "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                vehicleServices.deleteVehicle(id);
                JOptionPane.showMessageDialog(owner, "Vehículo eliminado",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refresh();
                refreshDashboard();
                logger.accept("Vehículos: eliminado " + id);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(owner,
                    "Error al eliminar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                logger.accept("Vehículos: error al eliminar → " + e.getMessage());
            }
        }
    }

    private void mostrarDetallesVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(owner, "Selecciona un vehículo",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloVehiculos.getValueAt(fila, 0);
        try {
            Vehicle vehiculo = vehicleServices.getVehicleById(id);
            if (vehiculo == null) {
                JOptionPane.showMessageDialog(owner, "No se encontró el vehículo",
                    "Error", JOptionPane.ERROR_MESSAGE);
                refresh();
                return;
            }

            StringBuilder detalle = new StringBuilder()
                .append("ID: ").append(vehiculo.getId())
                .append("\nNombre: ").append(vehiculo.getName())
                .append("\nCategoría: ").append(vehiculo.getCategory())
                .append("\nCapacidad: ").append(String.format("%,.2f L", vehiculo.getCapacity()))
                .append("\nCombustible: ")
                .append(vehiculo.getFuelProductId() != null ? vehiculo.getFuelProductId() : "Sin asignar")
                .append("\nHorómetro: ").append(vehiculo.hasHorometer() ? "Sí" : "No");

            JOptionPane.showMessageDialog(owner, detalle.toString(),
                "Detalle de vehículo", JOptionPane.INFORMATION_MESSAGE);
            logger.accept("Vehículos: detalles mostrados para " + id);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Vehículos: error al consultar detalles → " + e.getMessage());
        }
    }

    public void requestCreationShortcut() {
        agregarVehiculo();
    }

    private void refreshDashboard() {
        if (dashboardRefresh != null) {
            dashboardRefresh.run();
        }
    }

    private record VehiclesPayload(List<Vehicle> vehiculos,
                                   List<Vehicle> vehiculosFiltrados,
                                   String criterio,
                                   String categoria) {
    }
}
