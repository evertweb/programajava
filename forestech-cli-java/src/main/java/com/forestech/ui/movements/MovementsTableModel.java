package com.forestech.ui.movements;

import com.forestech.enums.MovementType;
import com.forestech.models.Movement;
import com.forestech.ui.utils.UIUtils;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

/**
 * TableModel personalizado para la tabla de movimientos.
 * Maneja la estructura de datos y formato de la tabla.
 */
public class MovementsTableModel extends DefaultTableModel {

    private static final String[] COLUMN_NAMES = {
        "ID", "Tipo", "Producto", "Vehículo", "Cantidad (L)",
        "Unidad", "Factura", "Precio Unitario", "Subtotal", "Fecha"
    };

    private final Map<String, String> productNamesCache;
    private final Map<String, String> vehicleNamesCache;

    /**
     * Constructor del modelo de tabla.
     *
     * @param productNamesCache Cache de nombres de productos (ID -> Nombre)
     * @param vehicleNamesCache Cache de nombres de vehículos (ID -> Nombre)
     */
    public MovementsTableModel(Map<String, String> productNamesCache,
                               Map<String, String> vehicleNamesCache) {
        super(COLUMN_NAMES, 0);
        this.productNamesCache = productNamesCache;
        this.vehicleNamesCache = vehicleNamesCache;
    }

    /**
     * Deshabilita la edición de celdas.
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Carga una lista de movimientos en la tabla.
     *
     * @param movements Lista de movimientos a cargar
     */
    public void loadMovements(List<Movement> movements) {
        setRowCount(0); // Limpiar tabla

        for (Movement movement : movements) {
            addMovementRow(movement);
        }
    }

    /**
     * Agrega una fila para un movimiento específico.
     *
     * @param movement Movimiento a agregar
     */
    private void addMovementRow(Movement movement) {
        String typeLabel = movement.getMovementType() != null
            ? movement.getMovementType().getCode()
            : "—";

        String measurementUnit = movement.getMeasurementUnitCode() != null
            ? movement.getMeasurementUnitCode()
            : "—";

        String invoiceLabel = movement.getInvoiceNumber() != null
            ? movement.getInvoiceNumber()
            : "—";

        String createdAtRaw = movement.getCreatedAt() != null
            ? movement.getCreatedAt().toString()
            : null;

        double subtotal = movement.getQuantity() * movement.getUnitPrice();

        addRow(new Object[]{
            movement.getId(),
            typeLabel,
            buildProductLabel(movement.getProductId()),
            buildVehicleLabel(movement.getVehicleId()),
            String.format("%,.2f", movement.getQuantity()),
            measurementUnit,
            invoiceLabel,
            UIUtils.formatCurrency(movement.getUnitPrice()),
            UIUtils.formatCurrency(subtotal),
            UIUtils.formatMovementDate(createdAtRaw)
        });
    }

    /**
     * Construye la etiqueta del producto (ID - Nombre).
     *
     * @param productId ID del producto
     * @return Etiqueta formateada
     */
    private String buildProductLabel(String productId) {
        if (productId == null) {
            return "—";
        }
        String name = productNamesCache.getOrDefault(productId, productId);
        return productId + " - " + name;
    }

    /**
     * Construye la etiqueta del vehículo (ID - Nombre).
     *
     * @param vehicleId ID del vehículo
     * @return Etiqueta formateada
     */
    private String buildVehicleLabel(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return "—";
        }
        String name = vehicleNamesCache.getOrDefault(vehicleId, vehicleId);
        return vehicleId + " - " + name;
    }

    /**
     * Obtiene el ID del movimiento en una fila específica.
     *
     * @param modelRowIndex Índice de fila en el modelo
     * @return ID del movimiento
     */
    public String getMovementIdAt(int modelRowIndex) {
        return (String) getValueAt(modelRowIndex, 0);
    }
}
