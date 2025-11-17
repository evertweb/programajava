package com.forestech.dao;

import com.forestech.config.HikariCPDataSource;
import com.forestech.enums.MeasurementUnit;
import com.forestech.enums.MovementType;
import com.forestech.exceptions.InsufficientStockException;
import com.forestech.models.Movement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operaciones CRUD de movimientos de combustible.
 * 
 * <p>Implementa GenericDAO y agrega métodos específicos para movimientos:</p>
 * <ul>
 *   <li>Filtrar por tipo (ENTRADA/SALIDA)</li>
 *   <li>Obtener movimientos por producto</li>
 *   <li>Obtener movimientos por vehículo</li>
 *   <li>Calcular stock por producto</li>
 * </ul>
 */
public class MovementDAO implements GenericDAO<Movement> {
    
    @Override
    public void insert(Movement movement) throws SQLException {
        String sql = "INSERT INTO Movement (id, movementType, product_id, vehicle_id, " +
                    "numero_factura, unidadDeMedida, quantity, unitPrice, " +
                    "movementDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movement.getId());
            stmt.setString(2, movement.getMovementTypeCode());
            stmt.setString(3, movement.getProductId());
            stmt.setString(4, movement.getVehicleId());
            stmt.setString(5, movement.getInvoiceNumber());
            stmt.setString(6, movement.getMeasurementUnitCode());
            stmt.setDouble(7, movement.getQuantity());
            stmt.setDouble(8, movement.getUnitPrice());
            stmt.setTimestamp(9, Timestamp.valueOf(movement.getCreatedAt()));

            stmt.executeUpdate();
        }
    }
    
    @Override
    public Optional<Movement> findById(String id) throws SQLException {
        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                    "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                    "m.movementDate, " +
                    "p.nombre AS producto_nombre, p.categoria AS producto_categoria, " +
                    "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
                    "FROM Movement m " +
                    "LEFT JOIN oil_products p ON m.product_id = p.id " +
                    "LEFT JOIN vehicles v ON m.vehicle_id = v.id " +
                    "WHERE m.id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMovement(rs));
            }
            return Optional.empty();
        }
    }
    
    @Override
    public List<Movement> findAll() throws SQLException {
        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                    "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                    "m.movementDate, " +
                    "p.nombre AS producto_nombre, p.categoria AS producto_categoria, " +
                    "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
                    "FROM Movement m " +
                    "LEFT JOIN oil_products p ON m.product_id = p.id " +
                    "LEFT JOIN vehicles v ON m.vehicle_id = v.id " +
                    "ORDER BY m.movementDate DESC";

        List<Movement> movements = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }

        return movements;
    }
    
    @Override
    public void update(Movement movement) throws SQLException {
        String sql = "UPDATE Movement SET movementType = ?, product_id = ?, " +
                    "vehicle_id = ?, numero_factura = ?, unidadDeMedida = ?, " +
                    "quantity = ?, unitPrice = ? " +
                    "WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movement.getMovementTypeCode());
            stmt.setString(2, movement.getProductId());
            stmt.setString(3, movement.getVehicleId());
            stmt.setString(4, movement.getInvoiceNumber());
            stmt.setString(5, movement.getMeasurementUnitCode());
            stmt.setDouble(6, movement.getQuantity());
            stmt.setDouble(7, movement.getUnitPrice());
            stmt.setString(8, movement.getId());

            stmt.executeUpdate();
        }
    }
    
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Movement WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean exists(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Movement WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
    
    // ===== MÉTODOS ESPECÍFICOS DE MOVIMIENTOS =====
    
    /**
     * Obtiene movimientos filtrados por tipo (ENTRADA o SALIDA).
     */
    public List<Movement> findByType(MovementType movementType) throws SQLException {
        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                    "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                    "m.movementDate, " +
                    "p.nombre AS producto_nombre, p.categoria AS producto_categoria, " +
                    "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
                    "FROM Movement m " +
                    "LEFT JOIN oil_products p ON m.product_id = p.id " +
                    "LEFT JOIN vehicles v ON m.vehicle_id = v.id " +
                    "WHERE m.movementType = ? " +
                    "ORDER BY m.movementDate DESC";

        List<Movement> movements = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movementType.getCode());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }

        return movements;
    }

    @Deprecated
    public List<Movement> findByType(String movementType) throws SQLException {
        return findByType(MovementType.fromCode(movementType));
    }
    
    /**
     * Obtiene movimientos de un producto específico.
     */
    public List<Movement> findByProductId(String productId) throws SQLException {
        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                    "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                    "m.movementDate, " +
                    "p.nombre AS producto_nombre, p.categoria AS producto_categoria, " +
                    "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
                    "FROM Movement m " +
                    "LEFT JOIN oil_products p ON m.product_id = p.id " +
                    "LEFT JOIN vehicles v ON m.vehicle_id = v.id " +
                    "WHERE m.product_id = ? " +
                    "ORDER BY m.movementDate DESC";

        List<Movement> movements = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }

        return movements;
    }
    
    /**
     * Obtiene movimientos de un vehículo específico.
     */
    public List<Movement> findByVehicleId(String vehicleId) throws SQLException {
        String sql = "SELECT m.id, m.movementType, m.product_id, m.vehicle_id, " +
                    "m.numero_factura, m.unidadDeMedida, m.quantity, m.unitPrice, " +
                    "m.movementDate, " +
                    "p.nombre AS producto_nombre, p.categoria AS producto_categoria, " +
                    "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
                    "FROM Movement m " +
                    "LEFT JOIN oil_products p ON m.product_id = p.id " +
                    "LEFT JOIN vehicles v ON m.vehicle_id = v.id " +
                    "WHERE m.vehicle_id = ? " +
                    "ORDER BY m.movementDate DESC";

        List<Movement> movements = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movements.add(mapResultSetToMovement(rs));
            }
        }

        return movements;
    }
    
    /**
     * Calcula el stock actual de un producto específico.
     * Stock = Total ENTRADAS - Total SALIDAS
     */
    public double getStockByProductId(String productId) throws SQLException {
        String sql = "SELECT " +
                    "COALESCE(SUM(CASE WHEN movementType = 'ENTRADA' THEN quantity ELSE 0 END), 0) - " +
                    "COALESCE(SUM(CASE WHEN movementType = 'SALIDA' THEN quantity ELSE 0 END), 0) AS stock " +
                    "FROM Movement WHERE product_id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("stock");
            }
            return 0.0;
        }
    }
    
    /**
     * Valida que hay stock suficiente antes de una SALIDA.
     * 
     * @throws InsufficientStockException Si no hay stock suficiente
     */
    public void validateStockForSalida(String productId, double quantityToExit) 
            throws SQLException, InsufficientStockException {
        
        double currentStock = getStockByProductId(productId);
        
        if (currentStock < quantityToExit) {
            throw new InsufficientStockException(
                "Stock insuficiente. Disponible: " + currentStock + 
                ", Solicitado: " + quantityToExit
            );
        }
    }
    
    // ===== MÉTODO AUXILIAR =====
    
    /**
     * Mapea un ResultSet a un objeto Movement.
     */
    private Movement mapResultSetToMovement(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("movementDate");
        Movement movement = new Movement(
            rs.getString("id"),
            MovementType.fromCode(rs.getString("movementType")),
            rs.getString("product_id"),
            rs.getString("vehicle_id"),
            rs.getString("numero_factura"),
            MeasurementUnit.fromCode(rs.getString("unidadDeMedida")),
            rs.getDouble("quantity"),
            rs.getDouble("unitPrice"),
            timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now()
        );

        movement.setProductName(rs.getString("producto_nombre"));
        movement.setProductCategory(rs.getString("producto_categoria"));
        movement.setVehiclePlate(rs.getString("vehiculo_placa"));
        movement.setVehicleType(rs.getString("vehiculo_tipo"));

        return movement;
    }
}
