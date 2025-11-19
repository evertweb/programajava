package com.forestech.modules.partners.dao;

import com.forestech.config.HikariCPDataSource;
import com.forestech.modules.partners.models.Supplier;
import com.forestech.data.dao.GenericDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operaciones CRUD de proveedores.
 */
public class SupplierDAO implements GenericDAO<Supplier> {

    @Override
    public void insert(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (id, name, nit, telephone, email, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getId());
            stmt.setString(2, supplier.getName());
            stmt.setString(3, supplier.getNit());
            stmt.setString(4, supplier.getTelephone());
            stmt.setString(5, supplier.getEmail());
            stmt.setString(6, supplier.getAddress());

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Supplier> findById(String id) throws SQLException {
        String sql = "SELECT id, name, nit, telephone, email, address FROM suppliers WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToSupplier(rs));
            }
            return Optional.empty();
        }
    }

    @Override
    public List<Supplier> findAll() throws SQLException {
        String sql = "SELECT id, name, nit, telephone, email, address FROM suppliers ORDER BY name";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }

        return suppliers;
    }

    @Override
    public void update(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET name = ?, nit = ?, telephone = ?, " +
                "email = ?, address = ? WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getNit());
            stmt.setString(3, supplier.getTelephone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean exists(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM suppliers WHERE id = ?";

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
     * Busca un proveedor por NIT (único).
     */
    public Optional<Supplier> findByNit(String nit) throws SQLException {
        String sql = "SELECT id, name, nit, telephone, email, address FROM suppliers WHERE nit = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nit);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToSupplier(rs));
            }
            return Optional.empty();
        }
    }

    /**
     * Busca proveedores por nombre (LIKE).
     */
    public List<Supplier> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, nit, telephone, email, address FROM suppliers " +
                "WHERE name LIKE ? ORDER BY name";
        List<Supplier> suppliers = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }

        return suppliers;
    }

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("nit"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("address"));
    }
}
