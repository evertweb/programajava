package com.forestech.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar operaciones CRUD de proveedores.
 */
public class SupplierServices {

    // CREATE
    public static void insertSupplier(Supplier supplier) throws DatabaseException {
        String sql = "INSERT INTO suppliers (id, name, nit, telephone, email, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplier.getId());
            pstmt.setString(2, supplier.getName());
            pstmt.setString(3, supplier.getNit());
            pstmt.setString(4, supplier.getTelephone());
            pstmt.setString(5, supplier.getEmail());
            pstmt.setString(6, supplier.getAddress());

            pstmt.executeUpdate();
            System.out.println("✅ Proveedor insertado: " + supplier.getId());

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DatabaseException("Error: Ya existe un proveedor con ese NIT", e);
            }
            throw new DatabaseException("Error al insertar proveedor", e);
        }
    }

    // READ ALL
    public static List<Supplier> getAllSuppliers() throws DatabaseException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT id, name, nit, telephone, email, address FROM suppliers ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }

            System.out.println("✅ Se cargaron " + suppliers.size() + " proveedores");

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener proveedores", e);
        }

        return suppliers;
    }

    // READ BY ID
    /**
     * Busca un proveedor por su ID.
     * ÚTIL PARA VALIDAR FOREIGN KEYS antes de insertar Factura.
     *
     * @param supplierId ID del proveedor (formato: SUP-XXXXXXXX)
     * @return Supplier encontrado, o null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static Supplier getSupplierById(String supplierId) throws DatabaseException {
        String sql = "SELECT id, name, nit, telephone, email, address FROM suppliers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSupplier(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar proveedor", e);
        }
    }

    /**
     * Verifica si un proveedor existe en la base de datos.
     * Método de conveniencia para validaciones de FK.
     *
     * @param supplierId ID del proveedor a verificar
     * @return true si existe, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean existsSupplier(String supplierId) throws DatabaseException {
        return getSupplierById(supplierId) != null;
    }

    // UPDATE
    public static boolean updateSupplier(Supplier supplier) throws DatabaseException {
        String sql = "UPDATE suppliers SET name = ?, nit = ?, telephone = ?, " +
                     "email = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getNit());
            pstmt.setString(3, supplier.getTelephone());
            pstmt.setString(4, supplier.getEmail());
            pstmt.setString(5, supplier.getAddress());
            pstmt.setString(6, supplier.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Proveedor actualizado: " + supplier.getId());
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar proveedor", e);
        }
    }

    // DELETE
    public static boolean deleteSupplier(String supplierId) throws DatabaseException {
        String sql = "DELETE FROM suppliers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, supplierId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Proveedor eliminado: " + supplierId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new DatabaseException(
                    "No se puede eliminar: el proveedor tiene facturas asociadas", e);
            }
            throw new DatabaseException("Error al eliminar proveedor", e);
        }
    }

    // HELPER
    private static Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("nit"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}
