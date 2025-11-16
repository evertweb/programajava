package com.forestech.ui;

import com.forestech.enums.MovementType;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Movement;
import com.forestech.services.MovementServices;

import javax.swing.*;
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

    public MovementManagerGUI() {
        setTitle("Gestor de Movimientos - Forestech");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

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

        panel.add(new JLabel("Filtrar por tipo:"));
        cmbFiltro = new JComboBox<>(new String[]{"Todos", "ENTRADA", "SALIDA"});
        cmbFiltro.addActionListener(e -> aplicarFiltro());
        panel.add(cmbFiltro);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        lblTotalEntradas = new JLabel("Entradas: 0 L");
        lblTotalEntradas.setForeground(new Color(0, 150, 0));
        lblTotalEntradas.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTotalEntradas);

        lblTotalSalidas = new JLabel("Salidas: 0 L");
        lblTotalSalidas.setForeground(new Color(200, 0, 0));
        lblTotalSalidas.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTotalSalidas);

        lblStockActual = new JLabel("Stock: 0 L");
        lblStockActual.setForeground(new Color(0, 0, 200));
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
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Ajustar anchos de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120); // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(80);  // Tipo
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Producto
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120); // Vehículo
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120); // Factura
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100); // Cantidad
        tabla.getColumnModel().getColumn(6).setPreferredWidth(150); // Fecha

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Movimientos Registrados"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnRegistrar = new JButton("Registrar Movimiento");
        btnRegistrar.setBackground(new Color(100, 200, 100));
        btnRegistrar.addActionListener(e -> registrarMovimiento());
        panel.add(btnRegistrar);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> {
            cargarMovimientos();
            calcularTotales();
        });
        panel.add(btnRefrescar);

        JButton btnReporte = new JButton("Ver Reporte");
        btnReporte.setBackground(new Color(150, 150, 255));
        btnReporte.addActionListener(e -> mostrarReporte());
        panel.add(btnReporte);

        add(panel, BorderLayout.SOUTH);
    }

    private void cargarMovimientos() {
        try {
            List<Movement> movimientos = new MovementServices().getAllMovements();
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
                movimientos = new MovementServices().getAllMovements();
            } else {
                MovementType movementType = MovementType.fromCode(filtro);
                movimientos = new MovementServices().getMovementsByType(movementType);
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
            List<Movement> movimientos = new MovementServices().getAllMovements();
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
                lblStockActual.setForeground(Color.RED);
            } else if (stockActual < 5000) {
                lblStockActual.setForeground(new Color(255, 140, 0)); // Naranja
            } else {
                lblStockActual.setForeground(new Color(0, 150, 0)); // Verde
            }

        } catch (DatabaseException e) {
            System.err.println("Error al calcular totales: " + e.getMessage());
        }
    }

    private void registrarMovimiento() {
        MovementDialogForm dialogo = new MovementDialogForm(this, true);
        if (dialogo.isGuardadoExitoso()) {
            cargarMovimientos();
            calcularTotales();
        }
    }

    private void mostrarReporte() {
        try {
            // Calcular manualmente
            List<Movement> movimientos = new MovementServices().getAllMovements();
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MovementManagerGUI());

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.11: MovementManagerGUI");
        System.out.println("CRUD completo con 3 FKs + validación de stock");
        System.out.println("=".repeat(60));
    }
}
