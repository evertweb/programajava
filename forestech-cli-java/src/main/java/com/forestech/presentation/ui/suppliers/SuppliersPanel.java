package com.forestech.presentation.ui.suppliers;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Supplier;
import com.forestech.business.services.SupplierServices;
import com.forestech.presentation.ui.utils.AsyncLoadManager;
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
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import com.forestech.presentation.ui.utils.FontScheme;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.forestech.presentation.ui.utils.ColorScheme;

/**
 * Panel de gestión de proveedores.
 */
public class SuppliersPanel extends JPanel {

    private final JFrame owner;
    private final Consumer<String> logger;
    private final AsyncLoadManager loadManager;

    // Services (Dependency Injection)
    private final SupplierServices supplierServices;

    private JTable tablaProveedores;
    private DefaultTableModel modeloProveedores;
    private JTextField txtBuscarProveedor;
    private JComboBox<String> cmbFiltroContacto;
    private JLabel lblResumenProveedores;

    public SuppliersPanel(JFrame owner, Consumer<String> logger, SupplierServices supplierServices) {
        this.owner = owner;
        this.logger = logger;
        this.supplierServices = supplierServices;
        this.loadManager = new AsyncLoadManager("Proveedores", logger, this::cargarProveedores);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.BACKGROUND_LIGHT);
        buildUI();
    }

    private void buildUI() {
        JLabel titulo = new JLabel("GESTIÓN DE PROVEEDORES", JLabel.CENTER);
        titulo.setFont(FontScheme.HEADER_2);
        titulo.setForeground(ColorScheme.FOREGROUND_PRIMARY);

        JPanel encabezado = new JPanel(new BorderLayout(0, 5));
        encabezado.setBackground(ColorScheme.BACKGROUND_PANEL);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(createFilterPanel(), BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        String[] columnas = { "ID", "Nombre", "NIT", "Teléfono", "Email", "Dirección" };
        modeloProveedores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProveedores = new JTable(modeloProveedores);
        tablaProveedores.setRowHeight(24);
        tablaProveedores.setAutoCreateRowSorter(true);
        UIUtils.styleTable(tablaProveedores);
        tablaProveedores.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
                    comp.setBackground(row % 2 == 0
                            ? ColorScheme.TABLE_ROW_PRIMARY
                            : ColorScheme.TABLE_ROW_STRIPE);
                    comp.setForeground(ColorScheme.FOREGROUND_PRIMARY);
                }
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProveedores);
        scroll.getViewport().setBackground(ColorScheme.BACKGROUND_PANEL);
        add(scroll, BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel panelFiltros = new JPanel(new BorderLayout(0, 5));
        panelFiltros.setBackground(ColorScheme.BACKGROUND_PANEL);
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelControles.setBackground(ColorScheme.BACKGROUND_PANEL);

        txtBuscarProveedor = new JTextField(18);
        txtBuscarProveedor.setToolTipText("ID, NIT o nombre");
        txtBuscarProveedor.addActionListener(e -> requestRefresh("Enter búsqueda proveedores"));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> requestRefresh("Botón Buscar Proveedores"));

        cmbFiltroContacto = new JComboBox<>(new String[] {
                "Cualquier estado",
                "Con email",
                "Sin email",
                "Con teléfono",
                "Sin teléfono"
        });
        cmbFiltroContacto.setPreferredSize(new Dimension(150, 28));
        cmbFiltroContacto.addActionListener(e -> requestRefresh("Filtro contacto"));

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarProveedor.setText("");
            cmbFiltroContacto.setSelectedIndex(0);
            requestRefresh("Limpiar filtros proveedores");
        });

        JLabel lblFiltroTitulo = new JLabel("ID/NIT/Nombre:");
        lblFiltroTitulo.setForeground(ColorScheme.FOREGROUND_PRIMARY);
        panelControles.add(lblFiltroTitulo);
        panelControles.add(txtBuscarProveedor);
        panelControles.add(btnBuscar);
        JLabel lblContacto = new JLabel("Contacto:");
        lblContacto.setForeground(ColorScheme.FOREGROUND_PRIMARY);
        panelControles.add(lblContacto);
        panelControles.add(cmbFiltroContacto);
        panelControles.add(btnLimpiar);

        lblResumenProveedores = new JLabel("Mostrando todos los proveedores");
        lblResumenProveedores.setFont(FontScheme.SMALL_ITALIC);
        lblResumenProveedores.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        lblResumenProveedores.setForeground(ColorScheme.FOREGROUND_SECONDARY);

        panelFiltros.add(panelControles, BorderLayout.CENTER);
        panelFiltros.add(lblResumenProveedores, BorderLayout.SOUTH);
        return panelFiltros;
    }

    private JPanel createButtonsPanel() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(ColorScheme.BACKGROUND_PANEL);

        JButton btnRegistrar = new JButton("Registrar");
        styleFilledButton(btnRegistrar, ColorScheme.BUTTON_PRIMARY_BG);
        btnRegistrar.addActionListener(e -> registrarProveedor());
        panelBotones.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        styleFilledButton(btnEditar, ColorScheme.BUTTON_WARNING_BG);
        btnEditar.addActionListener(e -> editarProveedor());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        styleFilledButton(btnEliminar, ColorScheme.BUTTON_DANGER_BG);
        btnEliminar.addActionListener(e -> eliminarProveedor());
        panelBotones.add(btnEliminar);

        JButton btnDetalles = new JButton("Ver Detalles");
        styleSecondaryButton(btnDetalles);
        btnDetalles.addActionListener(e -> mostrarDetallesProveedor());
        panelBotones.add(btnDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        styleSecondaryButton(btnRefrescar);
        btnRefrescar.addActionListener(e -> requestRefresh("Botón Refrescar Proveedores"));
        panelBotones.add(btnRefrescar);

        return panelBotones;
    }

    public void requestRefresh(String origin) {
        loadManager.requestLoad(origin);
    }

    public void cancelCurrentLoad() {
        loadManager.cancelCurrentLoad();
    }

    private void cargarProveedores(String origin) {
        long inicio = System.currentTimeMillis();
        logger.accept("Proveedores: iniciando carga (origen: " + origin + ")");
        setBusyCursor(true);

        final String criterio = txtBuscarProveedor != null
                ? txtBuscarProveedor.getText().trim().toLowerCase()
                : "";
        final String filtroContacto = (cmbFiltroContacto != null
                && cmbFiltroContacto.getSelectedItem() != null)
                        ? cmbFiltroContacto.getSelectedItem().toString()
                        : "Cualquier estado";

        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Supplier> doInBackground() throws Exception {
                List<Supplier> proveedores = supplierServices.getAllSuppliers();

                if (!criterio.isBlank()) {
                    proveedores = proveedores.stream()
                            .filter(p -> coincideProveedorConTexto(p, criterio))
                            .collect(Collectors.toList());
                }

                if (!"Cualquier estado".equalsIgnoreCase(filtroContacto)) {
                    final String filtro = filtroContacto;
                    proveedores = proveedores.stream()
                            .filter(p -> coincideProveedorConContacto(p, filtro))
                            .collect(Collectors.toList());
                }

                return proveedores;
            }

            @Override
            protected void done() {
                try {
                    List<Supplier> proveedores = get();
                    actualizarTablaProveedores(proveedores);
                    actualizarResumenProveedores(proveedores);
                    logger.accept("Proveedores: cargados " + proveedores.size() + " registros");
                } catch (ExecutionException ex) {
                    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(owner,
                            "Error al cargar proveedores: " + mensaje,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    logger.accept("Proveedores: error al cargar → " + mensaje);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Proveedores: carga interrumpida");
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

    private void actualizarTablaProveedores(List<Supplier> proveedores) {
        if (modeloProveedores == null) {
            return;
        }
        modeloProveedores.setRowCount(0);
        for (Supplier proveedor : proveedores) {
            modeloProveedores.addRow(new Object[] {
                    proveedor.getId(),
                    proveedor.getName(),
                    proveedor.getNit(),
                    UIUtils.optionalValue(proveedor.getTelephone()),
                    UIUtils.optionalValue(proveedor.getEmail()),
                    UIUtils.optionalValue(proveedor.getAddress())
            });
        }
    }

    private void actualizarResumenProveedores(List<Supplier> proveedores) {
        if (lblResumenProveedores == null) {
            return;
        }

        long conEmail = proveedores.stream().filter(this::tieneEmail).count();
        long conTelefono = proveedores.stream().filter(this::tieneTelefono).count();

        lblResumenProveedores.setText(String.format(
                "Mostrando %d proveedores | Con email: %d | Con teléfono: %d",
                proveedores.size(),
                conEmail,
                conTelefono));
        lblResumenProveedores.setForeground(proveedores.isEmpty()
                ? ColorScheme.DANGER_500
                : ColorScheme.FOREGROUND_SECONDARY);
    }

    private boolean coincideProveedorConContacto(Supplier proveedor, String filtro) {
        return switch (filtro) {
            case "Con email" -> tieneEmail(proveedor);
            case "Sin email" -> !tieneEmail(proveedor);
            case "Con teléfono" -> tieneTelefono(proveedor);
            case "Sin teléfono" -> !tieneTelefono(proveedor);
            default -> true;
        };
    }

    private boolean coincideProveedorConTexto(Supplier proveedor, String termino) {
        if (termino == null || termino.isBlank()) {
            return true;
        }
        return UIUtils.containsIgnoreCase(proveedor.getId(), termino)
                || UIUtils.containsIgnoreCase(proveedor.getName(), termino)
                || UIUtils.containsIgnoreCase(proveedor.getNit(), termino)
                || UIUtils.containsIgnoreCase(proveedor.getTelephone(), termino)
                || UIUtils.containsIgnoreCase(proveedor.getEmail(), termino)
                || UIUtils.containsIgnoreCase(proveedor.getAddress(), termino);
    }

    private boolean tieneEmail(Supplier proveedor) {
        return proveedor.getEmail() != null && !proveedor.getEmail().isBlank();
    }

    private boolean tieneTelefono(Supplier proveedor) {
        return proveedor.getTelephone() != null && !proveedor.getTelephone().isBlank();
    }

    private void registrarProveedor() {
        Supplier nuevo = mostrarDialogoProveedor("Registrar proveedor", null);
        if (nuevo == null) {
            logger.accept("Proveedores: alta cancelada por el usuario");
            return;
        }

        try {
            supplierServices.insertSupplier(nuevo);
            JOptionPane.showMessageDialog(owner,
                    "Proveedor creado correctamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            logger.accept("Proveedores: creado " + nuevo.getId());
            requestRefresh("Post alta proveedor");
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                    "Error al crear proveedor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Proveedores: error al crear → " + e.getMessage());
        }
    }

    private void editarProveedor() {
        String proveedorId = obtenerProveedorSeleccionado();
        if (proveedorId == null) {
            return;
        }

        try {
            Supplier existente = supplierServices.getSupplierById(proveedorId);
            if (existente == null) {
                JOptionPane.showMessageDialog(owner,
                        "El proveedor ya no existe",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                requestRefresh("Proveedor desaparecido");
                return;
            }

            Supplier actualizado = mostrarDialogoProveedor("Editar proveedor " + proveedorId, existente);
            if (actualizado == null) {
                logger.accept("Proveedores: edición cancelada " + proveedorId);
                return;
            }

            if (supplierServices.updateSupplier(actualizado)) {
                JOptionPane.showMessageDialog(owner,
                        "Proveedor actualizado",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                logger.accept("Proveedores: actualizado " + proveedorId);
                requestRefresh("Post edición proveedor");
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                    "Error al actualizar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Proveedores: error al actualizar → " + e.getMessage());
        }
    }

    private void eliminarProveedor() {
        String proveedorId = obtenerProveedorSeleccionado();
        if (proveedorId == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(owner,
                "¿Eliminar proveedor " + proveedorId + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (supplierServices.deleteSupplier(proveedorId)) {
                JOptionPane.showMessageDialog(owner,
                        "Proveedor eliminado",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                logger.accept("Proveedores: eliminado " + proveedorId);
                requestRefresh("Post eliminación proveedor");
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                    "No se pudo eliminar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.accept("Proveedores: error al eliminar → " + e.getMessage());
        }
    }

    private void mostrarDetallesProveedor() {
        String proveedorId = obtenerProveedorSeleccionado();
        if (proveedorId == null) {
            return;
        }

        try {
            Supplier proveedor = supplierServices.getSupplierById(proveedorId);
            if (proveedor == null) {
                JOptionPane.showMessageDialog(owner,
                        "El proveedor ya no existe",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                requestRefresh("Proveedor inexistente");
                return;
            }

            String detalle = """
                    ID: %s
                    Nombre: %s
                    NIT: %s
                    Teléfono: %s
                    Email: %s
                    Dirección: %s
                    """.formatted(
                    proveedor.getId(),
                    proveedor.getName(),
                    proveedor.getNit(),
                    UIUtils.optionalValue(proveedor.getTelephone()),
                    UIUtils.optionalValue(proveedor.getEmail()),
                    UIUtils.optionalValue(proveedor.getAddress()));

            javax.swing.JTextArea area = new javax.swing.JTextArea(detalle);
            area.setEditable(false);
            area.setFont(FontScheme.MONOSPACED);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(owner, new javax.swing.JScrollPane(area),
                    "Detalle del proveedor", JOptionPane.INFORMATION_MESSAGE);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(owner,
                    "Error al consultar proveedor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerProveedorSeleccionado() {
        if (tablaProveedores == null || tablaProveedores.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(owner,
                    "Selecciona un proveedor primero",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int filaVista = tablaProveedores.getSelectedRow();
        int filaModelo = tablaProveedores.convertRowIndexToModel(filaVista);
        return (String) modeloProveedores.getValueAt(filaModelo, 0);
    }

    private Supplier mostrarDialogoProveedor(String titulo, Supplier existente) {
        JTextField txtNombre = new JTextField(existente != null ? existente.getName() : "");
        JTextField txtNit = new JTextField(existente != null ? existente.getNit() : "");
        JTextField txtTelefono = new JTextField(
                existente != null ? UIUtils.editableValue(existente.getTelephone()) : "");
        JTextField txtEmail = new JTextField(existente != null ? UIUtils.editableValue(existente.getEmail()) : "");
        JTextField txtDireccion = new JTextField(
                existente != null ? UIUtils.editableValue(existente.getAddress()) : "");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("NIT:"));
        panel.add(txtNit);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Dirección:"));
        panel.add(txtDireccion);

        int opcion = JOptionPane.showConfirmDialog(owner, panel,
                titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        if (nombre.isEmpty() || nit.isEmpty()) {
            JOptionPane.showMessageDialog(owner,
                    "Nombre y NIT son obligatorios",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(owner,
                    "Ingresa un email válido",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String telefono = UIUtils.nullIfBlank(txtTelefono.getText().trim());
        String direccion = UIUtils.nullIfBlank(txtDireccion.getText().trim());
        email = UIUtils.nullIfBlank(email);

        if (existente == null) {
            return new Supplier(nombre, nit, telefono, email, direccion);
        }
        return new Supplier(existente.getId(), nombre, nit, telefono, email, direccion);
    }

    private void setBusyCursor(boolean busy) {
        Cursor cursor = busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        if (owner != null) {
            owner.setCursor(cursor);
        }
        setCursor(cursor);
    }

    public void requestCreationShortcut() {
        registrarProveedor();
    }

    private void styleFilledButton(JButton button, Color background) {
        button.setBackground(background);
        button.setForeground(ColorScheme.TEXT_ON_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(ColorScheme.BUTTON_SECONDARY_BG);
        button.setForeground(ColorScheme.BUTTON_SECONDARY_FG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorScheme.BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    }
}
