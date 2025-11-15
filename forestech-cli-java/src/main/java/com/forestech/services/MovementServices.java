package com.forestech.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.exceptions.DatabaseException;
import com.forestech.exceptions.InsufficientStockException;
import com.forestech.models.Movement;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar operaciones CRUD de movimientos de combustible.
 *
 * <p>Esta clase maneja todas las operaciones de base de datos relacionadas con
 * la tabla Movement, incluyendo:</p>
 * <ul>
 *   <li>CREATE: Insertar nuevos movimientos (ENTRADA/SALIDA)</li>
 *   <li>READ: Consultar movimientos con JOINs a tablas relacionadas</li>
 *   <li>UPDATE: Actualizar movimientos existentes</li>
 *   <li>DELETE: Eliminar movimientos (soft delete recomendado)</li>
 * </ul>
 *
 * @author Forestech Development Team
 * @version 2.0 (con llaves foráneas)
 * @since Fase 4
 */
public class MovementServices {

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Inserta un nuevo movimiento en la base de datos CON VALIDACIONES DE INTEGRIDAD.
     *
     * <p><strong>⚠️ VALIDACIONES AUTOMÁTICAS (antes del INSERT):</strong></p>
     * <ol>
     *   <li><strong>product_id:</strong> DEBE existir en oil_products (OBLIGATORIO)</li>
     *   <li><strong>vehicle_id:</strong> Si NO es NULL, DEBE existir en vehicles</li>
     *   <li><strong>numero_factura:</strong> Si NO es NULL, DEBE existir en facturas</li>
     *   <li><strong>SALIDA:</strong> Valida stock suficiente antes de permitir</li>
     * </ol>
     *
     * <p><strong>🎯 REGLAS DE NEGOCIO POR TIPO:</strong></p>
     * <ul>
     *   <li><strong>ENTRADA:</strong> product_id + numero_factura (vehicle_id = NULL)</li>
     *   <li><strong>SALIDA:</strong> product_id + vehicle_id (numero_factura = NULL)</li>
     * </ul>
     *
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * // ENTRADA de combustible con factura
     * Movement entrada = new Movement(
     *     "ENTRADA",
     *     "FUE-12345678", // productId (DEBE existir en oil_products)
     *     null,           // vehicleId (NULL para entradas)
     *     "10734",        // numeroFactura (DEBE existir en facturas)
     *     "GALON",
     *     50.0,
     *     8500.0
     * );
     * MovementServices.insertMovement(entrada);
     *
     * // SALIDA de combustible a vehículo
     * Movement salida = new Movement(
     *     "SALIDA",
     *     "FUE-12345678",  // productId (DEBE existir en oil_products)
     *     "VEH-87654321",  // vehicleId (DEBE existir en vehicles)
     *     null,            // numeroFactura (NULL para salidas)
     *     "GALON",
     *     20.0,
     *     8500.0
     * );
     * MovementServices.insertMovement(salida);
     * </pre>
     *
     * @param movement Objeto Movement a insertar (con ID ya generado)
     * @throws DatabaseException Si hay error de conexión o alguna FK no existe
     * @throws InsufficientStockException Si es SALIDA y no hay stock suficiente
     * @see Movement
     */
    public static void insertMovement(Movement movement) throws DatabaseException, InsufficientStockException {
        // ========================================================================
        // VALIDACIÓN 1: product_id OBLIGATORIO (DEBE existir en oil_products)
        // ========================================================================
        if (movement.getProductId() == null || movement.getProductId().trim().isEmpty()) {
            throw new DatabaseException(
                "ERROR: product_id es OBLIGATORIO. No puede ser NULL.",
                new IllegalArgumentException("product_id NULL")
            );
        }

        if (!ProductServices.existsProduct(movement.getProductId())) {
            throw new DatabaseException(
                "ERROR: El producto '" + movement.getProductId() + "' NO existe en oil_products. " +
                "Debes crear el producto primero antes de usarlo en un movimiento.",
                new IllegalArgumentException("product_id inválido")
            );
        }

        // ========================================================================
        // VALIDACIÓN 2: vehicle_id (solo si NO es NULL, debe existir en vehicles)
        // ========================================================================
        if (movement.getVehicleId() != null && !movement.getVehicleId().trim().isEmpty()) {
            if (!VehicleServices.existsVehicle(movement.getVehicleId())) {
                throw new DatabaseException(
                    "ERROR: El vehículo '" + movement.getVehicleId() + "' NO existe en vehicles. " +
                    "Debes crear el vehículo primero antes de usarlo en un movimiento.",
                    new IllegalArgumentException("vehicle_id inválido")
                );
            }
        }

        // ========================================================================
        // VALIDACIÓN 3: numero_factura (solo si NO es NULL, debe existir en facturas)
        // ========================================================================
        if (movement.getNumeroFactura() != null && !movement.getNumeroFactura().trim().isEmpty()) {
            if (!FacturaServices.existsFactura(movement.getNumeroFactura())) {
                throw new DatabaseException(
                    "ERROR: La factura '" + movement.getNumeroFactura() + "' NO existe en facturas. " +
                    "Debes crear la factura primero antes de usarla en un movimiento.",
                    new IllegalArgumentException("numero_factura inválido")
                );
            }
        }

        // ========================================================================
        // VALIDACIÓN 4: STOCK para SALIDAS (debe haber suficiente inventario)
        // ========================================================================
        if (movement.getMovementType().equals("SALIDA")) {
            double stockActual = getProductStock(movement.getProductId());
            if (stockActual < movement.getQuantity()) {
                throw new InsufficientStockException(
                    "Stock insuficiente. Disponible: " + stockActual +
                    ", Solicitado: " + movement.getQuantity(),
                    movement.getProductId(),
                    stockActual,
                    movement.getQuantity()
                );
            }
        }

        // ========================================================================
        // SI TODAS LAS VALIDACIONES PASARON → Proceder con INSERT
        // ========================================================================

        String sql = "INSERT INTO Movement " +
                "(id, movementType, product_id, vehicle_id, numero_factura, " +
                "unidadDeMedida, quantity, unitPrice, movementDate) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Configurar parámetros del PreparedStatement
            pstmt.setString(1, movement.getId());
            pstmt.setString(2, movement.getMovementType());
            pstmt.setString(3, movement.getProductId());        // FK → oil_products
            pstmt.setString(4, movement.getVehicleId());        // FK → vehicles (puede ser NULL)
            pstmt.setString(5, movement.getNumeroFactura());    // FK → facturas (puede ser NULL)
            pstmt.setString(6, movement.getUnidadDeMedida());
            pstmt.setDouble(7, movement.getQuantity());
            pstmt.setDouble(8, movement.getUnitPrice());
            pstmt.setString(9, movement.getMovementDate());

            // Ejecutar inserción
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Movimiento insertado exitosamente: " + movement.getId());
            }

        } catch (SQLException e) {
            // Verificar si el error es por violación de Foreign Key
            if (e.getMessage().contains("foreign key constraint")) {
                throw new DatabaseException(
                    "Error: El productId, vehicleId o numeroFactura no existe en la BD. " +
                    "Verifica que las llaves foráneas sean válidas.", e);
            } else {
                throw new DatabaseException("Error al insertar movimiento", e);
            }
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Obtiene TODOS los movimientos con información relacionada de productos.
     *
     * <p>Utiliza LEFT JOIN para traer el nombre del producto desde oil_products.</p>
     *
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * try {
     *     List&lt;Movement&gt; movimientos = MovementServices.getAllMovements();
     *     for (Movement m : movimientos) {
     *         System.out.println(m);  // Muestra todos los campos
     *     }
     * } catch (DatabaseException e) {
     *     System.err.println(e.getMessage());
     * }
     * </pre>
     *
     * @return Lista de movimientos (vacía si no hay registros)
     * @throws DatabaseException Si hay error de conexión
     */
    public static List<Movement> getAllMovements() throws DatabaseException {
        List<Movement> movements = new ArrayList<>();

        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                "m.movementDate " +
                "FROM Movement m " +
                "ORDER BY m.movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }

            System.out.println("✅ Se cargaron " + movements.size() + " movimientos");

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener movimientos", e);
        }

        return movements;
    }

    /**
     * Busca un movimiento por su ID.
     *
     * @param movementId ID del movimiento (formato: MOV-XXXXXXXX)
     * @return Movement encontrado, o null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static Movement getMovementById(String movementId) throws DatabaseException {
        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                "m.movementDate " +
                "FROM Movement m " +
                "WHERE m.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movementId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMovement(rs);
            } else {
                return null;  // No se encontró el movimiento
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar movimiento por ID", e);
        }
    }

    /**
     * Obtiene movimientos filtrados por tipo (ENTRADA o SALIDA).
     *
     * @param movementType Tipo: "ENTRADA" o "SALIDA"
     * @return Lista de movimientos del tipo especificado
     * @throws DatabaseException Si hay error de conexión
     */
    public static List<Movement> getMovementsByType(String movementType) throws DatabaseException {
        List<Movement> movements = new ArrayList<>();

        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                "m.movementDate " +
                "FROM Movement m " +
                "WHERE m.movementType = ? " +
                "ORDER BY m.movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movementType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }

            System.out.println("✅ Se encontraron " + movements.size() + " movimientos de tipo: " + movementType);

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar movimientos por tipo", e);
        }

        return movements;
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    /**
     * Actualiza la cantidad y precio unitario de un movimiento existente.
     *
     * <p><strong>⚠️ NOTA:</strong> No permite actualizar el productId, vehicleId o numeroFactura.
     * Solo actualiza quantity y unitPrice.</p>
     *
     * @param movementId ID del movimiento a actualizar
     * @param newQuantity Nueva cantidad
     * @param newUnitPrice Nuevo precio unitario
     * @return true si se actualizó correctamente, false si no existe el movimiento
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean updateMovement(String movementId, double newQuantity, double newUnitPrice)
            throws DatabaseException {

        String sql = "UPDATE Movement SET quantity = ?, unitPrice = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newQuantity);
            pstmt.setDouble(2, newUnitPrice);
            pstmt.setString(3, movementId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Movimiento actualizado: " + movementId);
                return true;
            } else {
                System.out.println("⚠️  No se encontró el movimiento: " + movementId);
                return false;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar movimiento", e);
        }
    }

    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================

    /**
     * Elimina un movimiento por su ID (DELETE físico).
     *
     * <p><strong>⚠️ ADVERTENCIA:</strong> Esta operación es IRREVERSIBLE.</p>
     * <p>En producción, se recomienda usar "soft delete" (marcar como eliminado)
     * en lugar de borrar físicamente.</p>
     *
     * @param movementId ID del movimiento a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean deleteMovement(String movementId) throws DatabaseException {
        String sql = "DELETE FROM Movement WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movementId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Movimiento eliminado: " + movementId);
                return true;
            } else {
                System.out.println("⚠️  No se encontró el movimiento: " + movementId);
                return false;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar movimiento", e);
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================================

    /**
     * Convierte una fila de ResultSet en un objeto Movement.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @return Objeto Movement con los datos de la fila
     * @throws SQLException Si hay error al leer columnas
     */
    private static Movement mapResultSetToMovement(ResultSet rs) throws SQLException {
        return new Movement(
                rs.getString("id"),
                rs.getString("movementType"),
                rs.getString("product_id"),
                rs.getString("vehicle_id"),       // puede ser NULL
                rs.getString("numero_factura"),   // puede ser NULL
                rs.getString("unidadDeMedida"),
                rs.getDouble("quantity"),
                rs.getDouble("unitPrice"),
                rs.getString("movementDate")
        );
    }

    // ============================================================================
    // 🎯 EJERCICIOS PARA TI (TODOs)
    // ============================================================================

    /**
     * TODO: EJERCICIO 1 - Obtener el total de movimientos
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear un método llamado `getTotalMovements()` que devuelva int</li>
     *   <li>Usar la query: `SELECT COUNT(*) as total FROM Movement`</li>
     *   <li>Ejecutar con `executeQuery()` y leer el resultado con `rs.getInt("total")`</li>
     *   <li>Retornar el número total de movimientos</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static int getTotalMovements() throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     *
     * <p><strong>Pistas:</strong></p>
     * <ul>
     *   <li>Usa `SELECT COUNT(*) as total FROM Movement`</li>
     *   <li>No necesitas PreparedStatement (no hay parámetros)</li>
     *   <li>El resultado es un solo número, no una lista</li>
     * </ul>
     */

    /**
     * TODO: EJERCICIO 2 - Calcular stock actual de un producto
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear método `getProductStock(String productId)` que devuelva double</li>
     *   <li>Calcular: ENTRADAS - SALIDAS de ese producto específico</li>
     *   <li>Usar query con SUM y CASE:</li>
     *   <pre>
     *   SELECT
     *     SUM(CASE WHEN movementType = 'ENTRADA' THEN quantity ELSE 0 END) as entradas,
     *     SUM(CASE WHEN movementType = 'SALIDA' THEN quantity ELSE 0 END) as salidas
     *   FROM Movement
     *   WHERE product_id = ?
     *   </pre>
     *   <li>Retornar: entradas - salidas</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static double getProductStock(String productId) throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     */

    /**
     * TODO: EJERCICIO 3 - Obtener movimientos de un vehículo específico
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear método `getMovementsByVehicle(String vehicleId)` que devuelva `List&lt;Movement&gt;`</li>
     *   <li>Usar query: `SELECT * FROM Movement WHERE vehicle_id = ? ORDER BY movementDate DESC`</li>
     *   <li>Usar PreparedStatement con parámetro vehicleId</li>
     *   <li>Mapear cada fila con `mapResultSetToMovement(rs)`</li>
     *   <li>Retornar la lista</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static List&lt;Movement&gt; getMovementsByVehicle(String vehicleId) throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     */

    /**
     * TODO: EJERCICIO 4 - Obtener movimientos por rango de fechas
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear método `getMovementsByDateRange(String fechaInicio, String fechaFin)`</li>
     *   <li>Usar query: `SELECT * FROM Movement WHERE movementDate BETWEEN ? AND ?`</li>
     *   <li>Los parámetros deben ser fechas en formato: "2025-01-01 00:00:00"</li>
     *   <li>Retornar lista de movimientos</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static List&lt;Movement&gt; getMovementsByDateRange(String fechaInicio, String fechaFin)
     *         throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     */

    // ============================================================================
    // MÉTODOS DE CÁLCULO DE STOCK (solución a ejercicios)
    // ============================================================================

    /**
     * Obtiene el stock actual de un producto (ENTRADAS - SALIDAS).
     *
     * @param productId ID del producto
     * @return Stock actual (puede ser negativo si hubo más salidas que entradas)
     * @throws DatabaseException Si hay error de conexión
     */
    public static double getProductStock(String productId) throws DatabaseException {
        String sql = "SELECT " +
                "SUM(CASE WHEN movementType = 'ENTRADA' THEN quantity ELSE 0 END) as entradas, " +
                "SUM(CASE WHEN movementType = 'SALIDA' THEN quantity ELSE 0 END) as salidas " +
                "FROM Movement WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double entradas = rs.getDouble("entradas");
                double salidas = rs.getDouble("salidas");
                return entradas - salidas;
            }
            return 0.0;

        } catch (SQLException e) {
            throw new DatabaseException("Error al calcular stock", e);
        }
    }

    /**
     * Obtiene el stock actual para una lista de productos en una sola consulta.
     *
     * @param productIds IDs de producto a consultar
     * @return Mapa producto → stock (0.0 si no tiene movimientos)
     * @throws DatabaseException Error al consultar la base de datos
     */
    public static Map<String, Double> getStockByProductIds(List<String> productIds) throws DatabaseException {
        Map<String, Double> stockMap = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return stockMap;
        }

        // Construir placeholders ?,?,?
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < productIds.size(); i++) {
            placeholders.append("?");
            if (i < productIds.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT product_id, " +
                "SUM(CASE WHEN movementType = 'ENTRADA' THEN quantity ELSE 0 END) as entradas, " +
                "SUM(CASE WHEN movementType = 'SALIDA' THEN quantity ELSE 0 END) as salidas " +
                "FROM Movement WHERE product_id IN (" + placeholders + ") GROUP BY product_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < productIds.size(); i++) {
                pstmt.setString(i + 1, productIds.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double entradas = rs.getDouble("entradas");
                double salidas = rs.getDouble("salidas");
                stockMap.put(rs.getString("product_id"), entradas - salidas);
            }

            return stockMap;

        } catch (SQLException e) {
            throw new DatabaseException("Error al calcular stock por lote", e);
        }
    }

    /**
     * Obtiene el total de movimientos en la base de datos.
     */
    public static int getTotalMovements() throws DatabaseException {
        String sql = "SELECT COUNT(*) as total FROM Movement";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error al contar movimientos", e);
        }
    }

    /**
     * Obtiene movimientos de un vehículo específico.
     */
    public static List<Movement> getMovementsByVehicle(String vehicleId) throws DatabaseException {
        List<Movement> movements = new ArrayList<>();
        String sql = "SELECT id, movementType, product_id, vehicle_id, numero_factura, " +
                "unidadDeMedida, quantity, unitPrice, movementDate " +
                "FROM Movement WHERE vehicle_id = ? ORDER BY movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }

            return movements;

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener movimientos por vehículo", e);
        }
    }

    /**
     * Obtiene movimientos por rango de fechas.
     */
    public static List<Movement> getMovementsByDateRange(String fechaInicio, String fechaFin)
            throws DatabaseException {
        List<Movement> movements = new ArrayList<>();
        String sql = "SELECT id, movementType, product_id, vehicle_id, numero_factura, " +
                "unidadDeMedida, quantity, unitPrice, movementDate " +
                "FROM Movement WHERE movementDate BETWEEN ? AND ? ORDER BY movementDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }

            return movements;

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener movimientos por fecha", e);
        }
    }
}
