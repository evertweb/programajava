package com.forestech.presentation.ui;

import com.forestech.shared.enums.MovementType;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Movement;
import com.forestech.business.services.FacturaServices;
import com.forestech.business.services.MovementServices;
import com.forestech.business.services.ProductServices;
import com.forestech.business.services.ServiceFactory;
import com.forestech.business.services.VehicleServices;
import com.forestech.presentation.ui.core.ServiceFactoryProvider;
import com.forestech.presentation.ui.movements.MovementDialogForm;
import com.forestech.presentation.ui.utils.ColorScheme;
import com.forestech.presentation.ui.utils.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Checkpoint 9.11: MovementManagerGUI - CRUD de Movimientos con Validación Completa
 *
 * Este es el gestor MÁS COMPLETO del proyecto. Demuestra:
 * - Listar movimientos con información completa
 * - Registrar ENTRADAS y SALIDAS
 * - Validación de 3 Foreign Keys
 * - Manejo de stock insuficiente
 * - Filtrado por tipo de movimiento
 */
public class MovementManagerGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltro;
    private JLabel lblTotalEntradas;
    private JLabel lblTotalSalidas;
    private JLabel lblStockActual;
    private final MovementServices movementServices;
    private final ProductServices productServices;
    private final VehicleServices vehicleServices;
    private final FacturaServices facturaServices;

    public MovementManagerGUI(MovementServices movementServices,
                              ProductServices productServices,
                              VehicleServices vehicleServices,
                              FacturaServices facturaServices) {
        this.movementServices = movementServices;
        this.productServices = productServices;
        this.vehicleServices = vehicleServices;
        this.facturaServices = facturaServices;

        setTitle("Gestor de Movimientos - Forestech");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(ColorScheme.BACKGROUND_LIGHT);

        crearPanelSuperior();
        crearTabla();
        crearPanelInferior();
        cargarMovimientos();
        calcularTotales();

        setVisible(true);
    }

    private void crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Filtros y Resumen"));
        panel.setBackground(ColorScheme.BACKGROUND_PANEL);

        panel.add(createFilterLabel("Filtrar por tipo:"));
        cmbFiltro = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA"});
        cmbFiltro.addActionListener(e -> aplicarFiltro());
        panel.add(cmbFiltro);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        lblTotalEntradas = new JLabel("Entradas: 0 L");
        lblTotalEntradas.setForeground(ColorScheme.SUCCESS_500);
        lblTotalEntradas.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTotalEntradas);

        lblTotalSalidas = new JLabel("Salidas: 0 L");
        lblTotalSalidas.setForeground(ColorScheme.DANGER_500);
        lblTotalSalidas.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTotalSalidas);

        lblStockActual = new JLabel("Stock: 0 L");
        lblStockActual.setForeground(ColorScheme.FOREGROUND_ACCENT);
        lblStockActual.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblStockActual);

        add(panel, BorderLayout.NORTH);
    }

    private void crearTabla() {
        String[] columnas = {"ID", "Tipo", "Producto", "Vehículo", "Factura", "Cantidad (L)", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        UIUtils.styleTable(tabla);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120); // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(80);  // Tipo
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Producto
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120); // Vehículo
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120); // Factura
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100); // Cantidad
        tabla.getColumnModel().getColumn(6).setPreferredWidth(150); // Fecha

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.getViewport().setBackground(ColorScheme.BACKGROUND_PANEL);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Movimientos Registrados"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(ColorScheme.BACKGROUND_PANEL);

        JButton btnRegistrar = new JButton("Registrar Movimiento");
        styleFilledButton(btnRegistrar, ColorScheme.BUTTON_PRIMARY_BG);
        btnRegistrar.addActionListener(e -> registrarMovimiento());
        panel.add(btnRegistrar);

        JButton btnRefrescar = new JButton("Refrescar");
        styleSecondaryButton(btnRefrescar);
        btnRefrescar.addActionListener(e -> {
            cargarMovimientos();
            calcularTotales();
        });
        panel.add(btnRefrescar);

        JButton btnReporte = new JButton("Ver Reporte");
        styleFilledButton(btnReporte, ColorScheme.BUTTON_INFO_BG);
        btnReporte.addActionListener(e -> mostrarReporte());
        panel.add(btnReporte);

        add(panel, BorderLayout.SOUTH);
    }

    private void cargarMovimientos() {
        try {
            List<Movement> movimientos = movementServices.getAllMovements();
            modeloTabla.setRowCount(0);

            for (Movement m : movimientos) {
                String movementTypeLabel = m.getMovementType() != null
                        ? m.getMovementType().getCode()
                        : "N/A";
                String invoiceLabel = m.getInvoiceNumber() != null ? m.getInvoiceNumber() : "N/A";
                String createdAt = m.getCreatedAt() != null ? m.getCreatedAt().toString() : "N/A";

                modeloTabla.addRow(new Object[]{
                    m.getId(),
                    movementTypeLabel,
                    m.getProductId(),
                    m.getVehicleId() != null ? m.getVehicleId() : "N/A",
                    invoiceLabel,
                    String.format("%.2f", m.getQuantity()),
                    createdAt
                });
            }

            System.out.println("✅ Movimientos cargados: " + movimientos.size());

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar movimientos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltro() {
        String filtro = (String) cmbFiltro.getSelectedItem();

        try {
            List<Movement> movimientos;

            if ("Todos".equals(filtro)) {
                movimientos = movementServices.getAllMovements();
            } else {
                MovementType movementType = MovementType.fromCode(filtro);
                movimientos = movementServices.getMovementsByType(movementType);
            }

            modeloTabla.setRowCount(0);

            for (Movement m : movimientos) {
                String movementTypeLabel = m.getMovementType() != null
                        ? m.getMovementType().getCode()
                        : "N/A";
                String invoiceLabel = m.getInvoiceNumber() != null ? m.getInvoiceNumber() : "N/A";
                String createdAt = m.getCreatedAt() != null ? m.getCreatedAt().toString() : "N/A";

                modeloTabla.addRow(new Object[]{
                    m.getId(),
                    movementTypeLabel,
                    m.getProductId(),
                    m.getVehicleId() != null ? m.getVehicleId() : "N/A",
                    invoiceLabel,
                    String.format("%.2f", m.getQuantity()),
                    createdAt
                });
            }

            System.out.println("✅ Filtro aplicado: " + filtro + " (" + movimientos.size() + " resultados)");

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al aplicar filtro: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularTotales() {
        try {
            // Calcular manualmente desde la lista de movimientos
            List<Movement> movimientos = movementServices.getAllMovements();
            double totalEntradas = 0;
            double totalSalidas = 0;

            for (Movement m : movimientos) {
                MovementType type = m.getMovementType();
                if (MovementType.ENTRADA.equals(type)) {
                    totalEntradas += m.getQuantity();
                } else if (MovementType.SALIDA.equals(type)) {
                    totalSalidas += m.getQuantity();
                }
            }

            double stockActual = totalEntradas - totalSalidas;

            lblTotalEntradas.setText(String.format("Entradas: %,.2f L", totalEntradas));
            lblTotalSalidas.setText(String.format("Salidas: %,.2f L", totalSalidas));
            lblStockActual.setText(String.format("Stock: %,.2f L", stockActual));

            // Cambiar color del stock según nivel
            if (stockActual < 1000) {
                lblStockActual.setForeground(ColorScheme.DANGER_500);
            } else if (stockActual < 5000) {
                lblStockActual.setForeground(ColorScheme.WARNING_500);
            } else {
                lblStockActual.setForeground(ColorScheme.SUCCESS_500);
            }

        } catch (DatabaseException e) {
            System.err.println("Error al calcular totales: " + e.getMessage());
        }
    }

    private void registrarMovimiento() {
        MovementDialogForm dialogo = new MovementDialogForm(
            this,
            true,
            movementServices,
            productServices,
            vehicleServices,
            facturaServices
        );
        if (dialogo.isGuardadoExitoso()) {
            cargarMovimientos();
            calcularTotales();
        }
    }

    private void mostrarReporte() {
        try {
            // Calcular manualmente
            List<Movement> movimientos = movementServices.getAllMovements();
            double totalEntradas = 0;
            double totalSalidas = 0;
            int contEntradas = 0;
            int contSalidas = 0;

            for (Movement m : movimientos) {
                MovementType type = m.getMovementType();
                if (MovementType.ENTRADA.equals(type)) {
                    totalEntradas += m.getQuantity();
                    contEntradas++;
                } else if (MovementType.SALIDA.equals(type)) {
                    totalSalidas += m.getQuantity();
                    contSalidas++;
                }
            }

            double stockActual = totalEntradas - totalSalidas;
            int totalMovimientos = movimientos.size();

            String reporte = String.format("""
                REPORTE DE MOVIMIENTOS - FORESTECH
                ═══════════════════════════════════════════════

                Total de movimientos registrados: %d

                ENTRADAS:
                  • Total ingresado: %,.2f Litros

                SALIDAS:
                  • Total despachado: %,.2f Litros

                STOCK ACTUAL:
                  • Inventario disponible: %,.2f Litros

                ANÁLISIS:
                  • Promedio por entrada: %,.2f L
                  • Promedio por salida: %,.2f L
                  • Rotación: %.1f%%

                ═══════════════════════════════════════════════
                Fecha del reporte: %s
                """,
                totalMovimientos,
                totalEntradas,
                totalSalidas,
                stockActual,
                contEntradas > 0 ? totalEntradas / contEntradas : 0,
                contSalidas > 0 ? totalSalidas / contSalidas : 0,
                totalEntradas > 0 ? (totalSalidas / totalEntradas * 100) : 0,
                java.time.LocalDateTime.now()
            );

            JTextArea textArea = new JTextArea(reporte);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane,
                "Reporte de Movimientos", JOptionPane.INFORMATION_MESSAGE);

        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                "Error al generar reporte: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createFilterLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ColorScheme.FOREGROUND_PRIMARY);
        return label;
    }

    private void styleFilledButton(JButton button, Color background) {
        button.setBackground(background);
        button.setForeground(ColorScheme.TEXT_ON_COLOR);
        button.setFocusPainted(false);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(ColorScheme.BUTTON_SECONDARY_BG);
        button.setForeground(ColorScheme.BUTTON_SECONDARY_FG);
        button.setFocusPainted(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ServiceFactory factory = ServiceFactoryProvider.getFactory();
            new MovementManagerGUI(
                factory.getMovementServices(),
                factory.getProductServices(),
                factory.getVehicleServices(),
                factory.getFacturaServices()
            );
        });

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.11: MovementManagerGUI");
        System.out.println("CRUD completo con 3 FKs + validación de stock");
        System.out.println("=".repeat(60));
    }
}
