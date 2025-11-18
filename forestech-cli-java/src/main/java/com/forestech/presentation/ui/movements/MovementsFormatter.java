package com.forestech.presentation.ui.movements;

import com.forestech.data.models.Movement;
import com.forestech.presentation.ui.utils.UIUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Clase utilitaria para formatear datos de movimientos para la UI.
 */
public class MovementsFormatter {

    /**
     * Formatea el resumen de movimientos para mostrar en la UI.
     *
     * @param totalMovements Cantidad total de movimientos
     * @param statistics     Estadísticas de movimientos
     * @return Texto formateado del resumen
     */
    public static String formatSummary(int totalMovements,
                                       MovementsDataLoader.MovementStatistics statistics) {
        return String.format(
            "Mostrando %d movimientos | Entradas: %d (%,.2f L) | Salidas: %d (%,.2f L)",
            totalMovements,
            statistics.totalEntries,
            statistics.litersEntries,
            statistics.totalExits,
            statistics.litersExits
        );
    }

    /**
     * Formatea los detalles completos de un movimiento para mostrar en un diálogo.
     *
     * @param movement         Movimiento a formatear
     * @param productNamesCache Cache de nombres de productos
     * @param vehicleNamesCache Cache de nombres de vehículos
     * @return Texto formateado con todos los detalles
     */
    public static String formatMovementDetails(Movement movement,
                                               Map<String, String> productNamesCache,
                                               Map<String, String> vehicleNamesCache) {
        return """
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
            movement.getId(),
            movement.getMovementType() != null ? movement.getMovementType().getCode() : "—",
            buildProductLabel(movement.getProductId(), productNamesCache),
            buildVehicleLabel(movement.getVehicleId(), vehicleNamesCache),
            movement.getQuantity(),
            movement.getMeasurementUnitCode() != null ? movement.getMeasurementUnitCode() : "—",
            UIUtils.formatCurrency(movement.getUnitPrice()),
            UIUtils.formatCurrency(movement.getQuantity() * movement.getUnitPrice()),
            movement.getInvoiceNumber() != null ? movement.getInvoiceNumber() : "—",
            UIUtils.formatMovementDate(movement.getCreatedAt() != null ? movement.getCreatedAt().toString() : null)
        );
    }

    /**
     * Construye la etiqueta del producto (ID - Nombre).
     *
     * @param productId         ID del producto
     * @param productNamesCache Cache de nombres de productos
     * @return Etiqueta formateada
     */
    private static String buildProductLabel(String productId, Map<String, String> productNamesCache) {
        if (productId == null) {
            return "—";
        }
        String name = productNamesCache.getOrDefault(productId, productId);
        return productId + " - " + name;
    }

    /**
     * Construye la etiqueta del vehículo (ID - Nombre).
     *
     * @param vehicleId         ID del vehículo
     * @param vehicleNamesCache Cache de nombres de vehículos
     * @return Etiqueta formateada
     */
    private static String buildVehicleLabel(String vehicleId, Map<String, String> vehicleNamesCache) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return "—";
        }
        String name = vehicleNamesCache.getOrDefault(vehicleId, vehicleId);
        return vehicleId + " - " + name;
    }

    /**
     * Parsea una fecha desde un campo de texto.
     *
     * @param dateText Texto de fecha en formato YYYY-MM-DD
     * @return LocalDate parseado, o null si está vacío
     * @throws IllegalArgumentException Si el formato es inválido
     */
    public static LocalDate parseDateFilter(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + dateText);
        }
    }
}
