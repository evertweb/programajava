package com.forestech.modules.catalog.dao;

import com.forestech.config.HikariCPDataSource;
import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.modules.catalog.models.Product;
import com.forestech.data.dao.GenericDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para operaciones CRUD de productos/combustibles.
 */
public class ProductDAO implements GenericDAO<Product> {

    @Override
    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd) VALUES (?, ?, ?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getMeasurementUnitCode());
            stmt.setDouble(4, product.getUnitPrice());

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Product> findById(String id) throws SQLException {
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products ORDER BY name";
        List<Product> products = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }

        return products;
    }

    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE oil_products SET name = ?, unidadDeMedida = ?, priceXUnd = ? WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getMeasurementUnitCode());
            stmt.setDouble(3, product.getUnitPrice());
            stmt.setString(4, product.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM oil_products WHERE id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean exists(String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM oil_products WHERE id = ?";

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
     * Busca productos por nombre (LIKE).
     */
    public List<Product> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products " +
                "WHERE name LIKE ? ORDER BY name";
        List<Product> products = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }

        return products;
    }

    /**
     * Obtiene productos por categoría.
     */
    public List<Product> findByMeasurementUnit(MeasurementUnit unit) throws SQLException {
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products " +
                "WHERE unidadDeMedida = ? ORDER BY name";
        List<Product> products = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, unit.getCode());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }

        return products;
    }

    @Deprecated
    public List<Product> findByCategory(String category) throws SQLException {
        return findByMeasurementUnit(MeasurementUnit.fromCode(category));
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getString("id"),
                rs.getString("name"),
                MeasurementUnit.fromCode(rs.getString("unidadDeMedida")),
                rs.getDouble("priceXUnd"));
    }
}
