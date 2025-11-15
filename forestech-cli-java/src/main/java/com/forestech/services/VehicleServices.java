package com.forestech.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar operaciones CRUD de veh\u00edculos.
 *
 * <p>Esta clase maneja todas las operaciones de base de datos relacionadas con
 * la tabla vehicles.</p>
 *
 * @author Forestech Development Team
 * @version 2.0 (con llaves foráneas)
 * @since Fase 4
 */
public class VehicleServices {

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Inserta un nuevo vehículo en la base de datos CON VALIDACIONES DE INTEGRIDAD.
     *
     * <p><strong>⚠️ VALIDACIONES AUTOMÁTICAS (antes del INSERT):</strong></p>
     * <ol>
     *   <li><strong>fuel_product_id:</strong> Si NO es NULL, DEBE existir en oil_products</li>
     * </ol>
     *
     * @param vehicle Objeto Vehicle a insertar
     * @throws DatabaseException Si hay error de conexión o FK inválida
     */
    public static void insertVehicle(Vehicle vehicle) throws DatabaseException {
        // ========================================================================
        // VALIDACIÓN: fuel_product_id (solo si NO es NULL, debe existir)
        // ========================================================================
        if (vehicle.getFuelProductId() != null && !vehicle.getFuelProductId().trim().isEmpty()) {
            if (!ProductServices.existsProduct(vehicle.getFuelProductId())) {
                throw new DatabaseException(
                    "ERROR: El producto de combustible '" + vehicle.getFuelProductId() +
                    "' NO existe en oil_products. " +
                    "Debes crear el producto primero antes de asignarlo a un vehículo.",
                    new IllegalArgumentException("fuel_product_id inválido")
                );
            }
        }

        String sql = "INSERT INTO vehicles (id, name, category, capacity, fuel_product_id, haveHorometer) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getId());
            pstmt.setString(2, vehicle.getName());
            pstmt.setString(3, vehicle.getCategory());
            pstmt.setDouble(4, vehicle.getCapacity());
            pstmt.setString(5, vehicle.getFuelProductId());  // FK → oil_products
            pstmt.setBoolean(6, vehicle.isHaveHorometer());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Vehículo insertado exitosamente: " + vehicle.getId());
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new DatabaseException(
                    "Error: El fuelProductId no existe en oil_products. " +
                    "Verifica que el combustible esté registrado.", e);
            } else {
                throw new DatabaseException("Error al insertar vehículo", e);
            }
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Obtiene TODOS los vehículos de la base de datos.
     *
     * @return Lista de vehículos
     * @throws DatabaseException Si hay error de conexión
     */
    public static List<Vehicle> getAllVehicles() throws DatabaseException {
        List<Vehicle> vehicles = new ArrayList<>();

        String sql = "SELECT id, name, category, capacity, fuel_product_id, haveHorometer " +
                     "FROM vehicles ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }

            System.out.println("✅ Se cargaron " + vehicles.size() + " vehículos");

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener vehículos", e);
        }

        return vehicles;
    }

    /**
     * Busca un vehículo por su ID.
     * ÚTIL PARA VALIDAR FOREIGN KEYS antes de insertar Movement.
     *
     * @param vehicleId ID del vehículo (formato: VEH-XXXXXXXX)
     * @return Vehicle encontrado, o null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static Vehicle getVehicleById(String vehicleId) throws DatabaseException {
        String sql = "SELECT id, name, category, capacity, fuel_product_id, haveHorometer " +
                     "FROM vehicles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToVehicle(rs);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar vehículo por ID", e);
        }
    }

    /**
     * Verifica si un vehículo existe en la base de datos.
     * Método de conveniencia para validaciones de FK.
     *
     * @param vehicleId ID del vehículo a verificar
     * @return true si existe, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean existsVehicle(String vehicleId) throws DatabaseException {
        return getVehicleById(vehicleId) != null;
    }

    /**
     * Obtiene vehículos filtrados por categoría.
     *
     * @param category Categoría a filtrar (Camión, Excavadora, etc.)
     * @return Lista de vehículos de esa categoría
     * @throws DatabaseException Si hay error de conexión
     */
    public static List<Vehicle> getVehiclesByCategory(String category) throws DatabaseException {
        List<Vehicle> vehicles = new ArrayList<>();

        String sql = "SELECT id, name, category, capacity, fuel_product_id, haveHorometer " +
                     "FROM vehicles WHERE category = ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }

            System.out.println("✅ Se encontraron " + vehicles.size() + " vehículo(s) en categoría: " + category);

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar vehículos por categoría", e);
        }

        return vehicles;
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    /**
     * Actualiza los datos de un vehículo existente.
     *
     * @param vehicle Objeto Vehicle con los datos actualizados
     * @return true si se actualizó correctamente, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean updateVehicle(Vehicle vehicle) throws DatabaseException {
        String sql = "UPDATE vehicles SET name = ?, category = ?, capacity = ?, " +
                     "fuel_product_id = ?, haveHorometer = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getName());
            pstmt.setString(2, vehicle.getCategory());
            pstmt.setDouble(3, vehicle.getCapacity());
            pstmt.setString(4, vehicle.getFuelProductId());
            pstmt.setBoolean(5, vehicle.isHaveHorometer());
            pstmt.setString(6, vehicle.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Vehículo actualizado: " + vehicle.getId());
                return true;
            } else {
                System.out.println("⚠️  No se encontró el vehículo: " + vehicle.getId());
                return false;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar vehículo", e);
        }
    }

    // ============================================================================
    // DELETE - OPERACIONES DE ELIMINACIÓN
    // ============================================================================

    /**
     * Elimina un vehículo por su ID.
     *
     * <p><strong>⚠️ NOTA:</strong> Si el vehículo tiene movimientos asociados,
     * esos movimientos quedarán con vehicle_id = NULL (ON DELETE SET NULL).</p>
     *
     * @param vehicleId ID del vehículo a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean deleteVehicle(String vehicleId) throws DatabaseException {
        String sql = "DELETE FROM vehicles WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicleId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Vehículo eliminado: " + vehicleId);
                return true;
            } else {
                System.out.println("⚠️  No se encontró el vehículo: " + vehicleId);
                return false;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar vehículo", e);
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================================

    /**
     * Convierte una fila de ResultSet en un objeto Vehicle.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @return Objeto Vehicle con los datos de la fila
     * @throws SQLException Si hay error al leer columnas
     */
    private static Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("capacity"),
                rs.getString("fuel_product_id"),  // puede ser NULL
                rs.getBoolean("haveHorometer")
        );
    }

    // ============================================================================
    // 🎯 EJERCICIOS PARA TI (TODOs)
    // ============================================================================

    /**
     * TODO: EJERCICIO 1 - Obtener vehículos por categoría
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear método `getVehiclesByCategory(String category)` que devuelva `List&lt;Vehicle&gt;`</li>
     *   <li>Usar query: `SELECT * FROM vehicles WHERE category = ?`</li>
     *   <li>Usar PreparedStatement con parámetro category</li>
     *   <li>Mapear cada fila con `mapResultSetToVehicle(rs)`</li>
     *   <li>Retornar la lista</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static List&lt;Vehicle&gt; getVehiclesByCategory(String category) throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     */

    /**
     * TODO: EJERCICIO 2 - Contar vehículos por tipo de combustible
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear método `countVehiclesByFuel(String fuelProductId)` que devuelva int</li>
     *   <li>Usar query: `SELECT COUNT(*) as total FROM vehicles WHERE fuel_product_id = ?`</li>
     *   <li>Retornar el número de vehículos que usan ese combustible</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static int countVehiclesByFuel(String fuelProductId) throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     */

    /**
     * TODO: EJERCICIO 3 - Obtener vehículos con horómetro
     *
     * <p><strong>Instrucciones:</strong></p>
     * <ol>
     *   <li>Crear método `getVehiclesWithHorometer()` que devuelva `List&lt;Vehicle&gt;`</li>
     *   <li>Usar query: `SELECT * FROM vehicles WHERE haveHorometer = TRUE`</li>
     *   <li>Retornar lista de vehículos que tienen horómetro</li>
     * </ol>
     *
     * <p><strong>Firma del método:</strong></p>
     * <pre>
     * public static List&lt;Vehicle&gt; getVehiclesWithHorometer() throws DatabaseException {
     *     // Tu código aquí
     * }
     * </pre>
     */
}
