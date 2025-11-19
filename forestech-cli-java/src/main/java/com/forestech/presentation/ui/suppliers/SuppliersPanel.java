package com.forestech.presentation.ui.suppliers;

import com.forestech.modules.partners.models.Supplier;
import com.forestech.presentation.clients.SupplierServiceClient;
import com.forestech.presentation.ui.utils.UIUtils;
import com.forestech.presentation.ui.utils.ColorScheme;
import com.forestech.presentation.ui.utils.FontScheme;
import com.forestech.presentation.ui.utils.IconManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel de gestión de proveedores.
 * Implementación MVP (Vista).
 */
public class SuppliersPanel extends JPanel implements SuppliersContract.View {

    private final JFrame owner;
    private final SuppliersContract.Presenter presenter;

    private JTable tablaProveedores;
    private DefaultTableModel modeloProveedores;
    private JTextField txtBuscarProveedor;
    private JComboBox<String> cmbFiltroContacto;
    private JLabel lblResumenProveedores;

    public SuppliersPanel(JFrame owner, Consumer<String> logger, SupplierServiceClient supplierClient) {
        this.owner = owner;
        this.presenter = new SuppliersPresenter(this, supplierClient, logger);

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
        txtBuscarProveedor.addActionListener(e -> presenter.loadSuppliers("Enter búsqueda"));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> presenter.loadSuppliers("Botón Buscar"));

        cmbFiltroContacto = new JComboBox<>(new String[] {
                "Cualquier estado",
                "Con email",
                "Sin email",
                "Con teléfono",
                "Sin teléfono"
        });
        cmbFiltroContacto.setPreferredSize(new Dimension(150, 28));
        cmbFiltroContacto.addActionListener(e -> presenter.loadSuppliers("Filtro contacto"));

        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> {
            txtBuscarProveedor.setText("");
            cmbFiltroContacto.setSelectedIndex(0);
            presenter.loadSuppliers("Limpiar filtros");
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
        btnRegistrar.setIcon(IconManager.getIcon("plus"));
        btnRegistrar.setBackground(ColorScheme.BUTTON_PRIMARY_BG);
        btnRegistrar.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btnRegistrar.addActionListener(e -> presenter.registerSupplier());
        panelBotones.add(btnRegistrar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setIcon(IconManager.getIcon("edit"));
        btnEditar.setBackground(ColorScheme.BUTTON_WARNING_BG);
        btnEditar.setForeground(ColorScheme.TEXT_PRIMARY);
        btnEditar.addActionListener(e -> presenter.editSupplier(getSelectedSupplierId()));
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setIcon(IconManager.getIcon("trash"));
        btnEliminar.setBackground(ColorScheme.BUTTON_DANGER_BG);
        btnEliminar.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btnEliminar.addActionListener(e -> presenter.deleteSupplier(getSelectedSupplierId()));
        panelBotones.add(btnEliminar);

        JButton btnDetalles = new JButton("Ver Detalles");
        btnDetalles.setIcon(IconManager.getIcon("eye"));
        btnDetalles.addActionListener(e -> presenter.showDetails(getSelectedSupplierId()));
        panelBotones.add(btnDetalles);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setIcon(IconManager.getIcon("refresh"));
        btnRefrescar.addActionListener(e -> presenter.loadSuppliers("Botón Refrescar"));
        panelBotones.add(btnRefrescar);

        return panelBotones;
    }

    // --- View Implementation ---

    @Override
    public void showSuppliers(List<Supplier> suppliers) {
        modeloProveedores.setRowCount(0);
        for (Supplier proveedor : suppliers) {
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
    public void showSupplierDetails(Supplier proveedor) {
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

        JTextArea area = new JTextArea(detalle);
        area.setEditable(false);
        area.setFont(FontScheme.MONOSPACED);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(owner, new JScrollPane(area),
                "Detalle del proveedor", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void updateSummary(int total, long withEmail, long withPhone) {
        lblResumenProveedores.setText(String.format(
                "Mostrando %d proveedores | Con email: %d | Con teléfono: %d",
                total, withEmail, withPhone));
        lblResumenProveedores.setForeground(total == 0
                ? ColorScheme.DANGER_500
                : ColorScheme.FOREGROUND_SECONDARY);
    }

    @Override
    public String getSearchTerm() {
        return txtBuscarProveedor.getText().trim();
    }

    @Override
    public String getContactFilter() {
        return (String) cmbFiltroContacto.getSelectedItem();
    }

    @Override
    public Supplier showSupplierForm(String title, Supplier existing) {
        JTextField txtNombre = new JTextField(existing != null ? existing.getName() : "");
        JTextField txtNit = new JTextField(existing != null ? existing.getNit() : "");
        JTextField txtTelefono = new JTextField(
                existing != null ? UIUtils.editableValue(existing.getTelephone()) : "");
        JTextField txtEmail = new JTextField(existing != null ? UIUtils.editableValue(existing.getEmail()) : "");
        JTextField txtDireccion = new JTextField(
                existing != null ? UIUtils.editableValue(existing.getAddress()) : "");

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
                title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        if (nombre.isEmpty() || nit.isEmpty()) {
            showWarning("Nombre y NIT son obligatorios");
            return null;
        }

        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            showWarning("Ingresa un email válido");
            return null;
        }

        String telefono = UIUtils.nullIfBlank(txtTelefono.getText().trim());
        String direccion = UIUtils.nullIfBlank(txtDireccion.getText().trim());
        email = UIUtils.nullIfBlank(email);

        if (existing == null) {
            return new Supplier(nombre, nit, telefono, email, direccion);
        }
        return new Supplier(existing.getId(), nombre, nit, telefono, email, direccion);
    }

    // --- Public Methods for external access ---

    public void requestRefresh(String origin) {
        presenter.loadSuppliers(origin);
    }

    public void cancelCurrentLoad() {
        presenter.cancelCurrentOperation();
    }

    public void requestCreationShortcut() {
        presenter.registerSupplier();
    }

    // --- Helper ---

    private String getSelectedSupplierId() {
        if (tablaProveedores == null || tablaProveedores.getSelectedRow() == -1) {
            return null;
        }
        int filaVista = tablaProveedores.getSelectedRow();
        int filaModelo = tablaProveedores.convertRowIndexToModel(filaVista);
        return (String) modeloProveedores.getValueAt(filaModelo, 0);
    }
}
