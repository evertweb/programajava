package com.forestech.presentation.ui.movements;

import com.forestech.shared.enums.MovementType;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Movement;
import com.forestech.business.services.MovementServices;
import com.forestech.presentation.ui.utils.CatalogCache;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase encargada de cargar y filtrar datos de movimientos.
 * Maneja la lógica de filtrado y cálculos estadísticos.
 */
public class MovementsDataLoader {

    private final MovementServices movementServices;
    private final CatalogCache catalogCache;

    public MovementsDataLoader(MovementServices movementServices, CatalogCache catalogCache) {
        this.movementServices = movementServices;
        this.catalogCache = catalogCache;
    }

    /**
     * Carga y filtra movimientos según criterios especificados.
     *
     * @param searchTerm     Término de búsqueda (ID, producto o vehículo)
     * @param selectedType   Tipo de movimiento ("Todos", "ENTRADA", "SALIDA")
     * @param startDate      Fecha desde (puede ser null)
     * @param endDate        Fecha hasta (puede ser null)
     * @return Resultado de carga con movimientos filtrados y estadísticas
     */
    public MovementsLoadResult loadMovements(String searchTerm,
                                             String selectedType,
                                             LocalDate startDate,
                                             LocalDate endDate) throws DatabaseException {
        // Cargar todos los movimientos
        List<Movement> allMovements = movementServices.getAllMovements();

        // Obtener nombres desde el cache
        Map<String, String> productNames = catalogCache.getProductNames();
        Map<String, String> vehicleNames = catalogCache.getVehicleNames();

        // Convertir término de búsqueda a lowercase
        String lowerSearchTerm = searchTerm != null ? searchTerm.toLowerCase() : "";

        // Aplicar filtros
        List<Movement> filteredMovements = allMovements.stream()
            .filter(m -> matchesType(m, selectedType))
            .filter(m -> matchesDateRange(m, startDate, endDate))
            .filter(m -> matchesSearchTerm(m, lowerSearchTerm, productNames, vehicleNames))
            .collect(Collectors.toList());

        // Calcular estadísticas
        MovementStatistics statistics = calculateStatistics(filteredMovements);

        return new MovementsLoadResult(
            filteredMovements,
            productNames,
            vehicleNames,
            statistics
        );
    }

    /**
     * Verifica si un movimiento coincide con el tipo seleccionado.
     *
     * @param movement     Movimiento a verificar
     * @param selectedType Tipo seleccionado ("Todos", "ENTRADA", "SALIDA")
     * @return true si coincide
     */
    private boolean matchesType(Movement movement, String selectedType) {
        if ("Todos".equalsIgnoreCase(selectedType)) {
            return true;
        }
        MovementType type = movement.getMovementType();
        return type != null && type.getCode().equalsIgnoreCase(selectedType);
    }

    /**
     * Verifica si un movimiento está dentro del rango de fechas.
     *
     * @param movement  Movimiento a verificar
     * @param startDate Fecha desde (puede ser null)
     * @param endDate   Fecha hasta (puede ser null)
     * @return true si está dentro del rango
     */
    private boolean matchesDateRange(Movement movement, LocalDate startDate, LocalDate endDate) {
        if (movement.getCreatedAt() == null) {
            return false;
        }

        LocalDate movementDate = movement.getCreatedAt().toLocalDate();
        if (movementDate == null) {
            return false;
        }

        boolean afterStart = (startDate == null) || !movementDate.isBefore(startDate);
        boolean beforeEnd = (endDate == null) || !movementDate.isAfter(endDate);
        return afterStart && beforeEnd;
    }

    /**
     * Verifica si un movimiento coincide con el término de búsqueda.
     *
     * @param movement      Movimiento a verificar
     * @param searchTerm    Término de búsqueda (ya en lowercase)
     * @param productNames  Mapa de nombres de productos
     * @param vehicleNames  Mapa de nombres de vehículos
     * @return true si coincide
     */
    private boolean matchesSearchTerm(Movement movement, String searchTerm,
                                      Map<String, String> productNames,
                                      Map<String, String> vehicleNames) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return true;
        }

        return containsIgnoreCase(movement.getId(), searchTerm)
            || containsIgnoreCase(movement.getProductId(), searchTerm)
            || containsIgnoreCase(productNames.get(movement.getProductId()), searchTerm)
            || containsIgnoreCase(movement.getVehicleId(), searchTerm)
            || containsIgnoreCase(vehicleNames.get(movement.getVehicleId()), searchTerm)
            || containsIgnoreCase(movement.getInvoiceNumber(), searchTerm);
    }

    /**
     * Verifica si un valor contiene el término de búsqueda (case-insensitive).
     *
     * @param value      Valor a verificar
     * @param searchTerm Término de búsqueda (ya en lowercase)
     * @return true si contiene el término
     */
    private boolean containsIgnoreCase(String value, String searchTerm) {
        return value != null && value.toLowerCase().contains(searchTerm);
    }

    /**
     * Calcula estadísticas de los movimientos filtrados.
     *
     * @param movements Lista de movimientos
     * @return Estadísticas calculadas
     */
    private MovementStatistics calculateStatistics(List<Movement> movements) {
        long totalEntries = 0;
        long totalExits = 0;
        double litersEntries = 0;
        double litersExits = 0;

        for (Movement movement : movements) {
            MovementType type = movement.getMovementType();
            if (MovementType.ENTRADA.equals(type)) {
                totalEntries++;
                litersEntries += movement.getQuantity();
            } else if (MovementType.SALIDA.equals(type)) {
                totalExits++;
                litersExits += movement.getQuantity();
            }
        }

        return new MovementStatistics(totalEntries, litersEntries, totalExits, litersExits);
    }

    /**
     * Clase interna para almacenar el resultado de carga.
     */
    public static class MovementsLoadResult {
        public final List<Movement> movements;
        public final Map<String, String> productNames;
        public final Map<String, String> vehicleNames;
        public final MovementStatistics statistics;

        public MovementsLoadResult(List<Movement> movements,
                                   Map<String, String> productNames,
                                   Map<String, String> vehicleNames,
                                   MovementStatistics statistics) {
            this.movements = movements;
            this.productNames = productNames;
            this.vehicleNames = vehicleNames;
            this.statistics = statistics;
        }
    }

    /**
     * Clase interna para almacenar estadísticas de movimientos.
     */
    public static class MovementStatistics {
        public final long totalEntries;
        public final double litersEntries;
        public final long totalExits;
        public final double litersExits;

        public MovementStatistics(long totalEntries, double litersEntries,
                                  long totalExits, double litersExits) {
            this.totalEntries = totalEntries;
            this.litersEntries = litersEntries;
            this.totalExits = totalExits;
            this.litersExits = litersExits;
        }
    }
}
