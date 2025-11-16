package com.forestech.dao;

import com.forestech.config.HikariCPDataSource;
import com.forestech.enums.VehicleCategory;
import com.forestech.models.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operaciones CRUD de vehículos/maquinaria.
 */
public class VehicleDAO implements GenericDAO<Vehicle> {
    
    @Override
    public void insert(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (id, placa, tipo, capacidad_tanque, " +
                    "combustible_id, tiene_horometro) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getId());
            stmt.setString(2, vehicle.getName());
            stmt.setString(3, vehicle.getCategoryCode());
            stmt.setDouble(4, vehicle.getCapacity());
            stmt.setString(5, vehicle.getFuelProductId());
            stmt.setBoolean(6, vehicle.hasHorometer());
            
            stmt.executeUpdate();
        }
    }
    
    @Override
    public Optional<Vehicle> findById(String id) throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_tanque, combustible_id, tiene_horometro " +
                    "FROM vehicles WHERE id = ?";
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToVehicle(rs));
            }
            return Optional.empty();
        }
    }
    
    @Override
    public List<Vehicle> findAll() throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_tanque, combustible_id, tiene_horometro " +
                    "FROM vehicles ORDER BY placa";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        
        return vehicles;
    }
    
    @Override
    public void update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET placa = ?, tipo = ?, capacidad_tanque = ?, " +
                    "combustible_id = ?, tiene_horometro = ? WHERE id = ?";
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getName());
            stmt.setString(2, vehicle.getCategoryCode());
            stmt.setDouble(3, vehicle.getCapacity());
            stmt.setString(4, vehicle.getFuelProductId());
            stmt.setBoolean(5, vehicle.hasHorometer());
            stmt.setString(6, vehicle.getId());
            
            stmt.executeUpdate();
        }
    }
    
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean exists(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE id = ?";
        
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
    
    // ===== MÉTODOS ESPECÍFICOS =====
    
    /**
     * Busca vehículos por tipo/categoría.
     */
    public List<Vehicle> findByCategory(VehicleCategory category) throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_tanque, combustible_id, tiene_horometro " +
                    "FROM vehicles WHERE tipo = ? ORDER BY placa";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category.getCode());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        
        return vehicles;
    }
    
    @Deprecated
    public List<Vehicle> findByCategory(String category) throws SQLException {
        return findByCategory(VehicleCategory.fromCode(category));
    }
    
    /**
     * Busca vehículos por placa (LIKE).
     */
    public List<Vehicle> findByPlaca(String placa) throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_tanque, combustible_id, tiene_horometro " +
                    "FROM vehicles WHERE placa LIKE ? ORDER BY placa";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + placa + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        
        return vehicles;
    }
    
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(
            rs.getString("id"),
            rs.getString("placa"),
            VehicleCategory.fromCode(rs.getString("tipo")),
            rs.getDouble("capacidad_tanque"),
            rs.getString("combustible_id"),
            rs.getBoolean("tiene_horometro")
        );
    }
}
