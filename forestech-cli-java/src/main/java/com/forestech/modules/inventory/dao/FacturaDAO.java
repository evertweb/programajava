package com.forestech.modules.inventory.dao;

import com.forestech.config.HikariCPDataSource;
import com.forestech.modules.inventory.models.DetalleFactura;
import com.forestech.modules.inventory.models.Factura;
import com.forestech.shared.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Factura and DetalleFactura.
 * Handles raw JDBC operations.
 */
public class FacturaDAO {

    // --- Transactional Methods (Connection passed from Service) ---

    public void insertFactura(Connection conn, Factura factura) throws SQLException {
        String sql = "INSERT INTO facturas (numero_factura, fecha_emision, " +
                "fecha_vencimiento, supplier_id, subtotal, iva, total, observaciones, " +
                "forma_pago, cuenta_bancaria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, factura.getNumeroFactura());
            pstmt.setDate(2, Date.valueOf(factura.getFechaEmision()));
            pstmt.setDate(3, Date.valueOf(factura.getFechaVencimiento()));
            pstmt.setString(4, factura.getSupplierId());
            pstmt.setDouble(5, factura.getSubtotal());
            pstmt.setDouble(6, factura.getIva());
            pstmt.setDouble(7, factura.getTotal());
            pstmt.setString(8, factura.getObservaciones());
            pstmt.setString(9, factura.getFormaPago());
            pstmt.setString(10, factura.getCuentaBancaria());
            pstmt.executeUpdate();
        }
    }

    public void insertDetalles(Connection conn, Factura factura, List<DetalleFactura> detalles) throws SQLException {
        String sql = "INSERT INTO detalle_factura (numero_factura, producto, " +
                "cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (DetalleFactura detalle : detalles) {
                pstmt.setString(1, factura.getNumeroFactura());
                pstmt.setString(2, detalle.getProducto());
                pstmt.setDouble(3, detalle.getCantidad());
                pstmt.setDouble(4, detalle.getPrecioUnitario());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // --- Non-Transactional Methods (Connection managed internally) ---

    public List<Factura> findAll() throws DatabaseException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT numero_factura, fecha_emision, fecha_vencimiento, supplier_id, " +
                "subtotal, iva, total, observaciones, forma_pago, cuenta_bancaria FROM facturas " +
                "ORDER BY fecha_emision DESC";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                facturas.add(mapResultSetToFactura(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener facturas", e);
        }

        return facturas;
    }

    public Factura findByNumero(String numeroFactura) throws DatabaseException {
        String sql = "SELECT numero_factura, fecha_emision, fecha_vencimiento, supplier_id, " +
                "subtotal, iva, total, observaciones, forma_pago, cuenta_bancaria " +
                "FROM facturas WHERE numero_factura = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeroFactura);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFactura(rs);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar factura", e);
        }
        return null;
    }

    public List<DetalleFactura> findDetallesByFactura(String numeroFactura) throws DatabaseException {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = "SELECT id_detalle, numero_factura, producto, cantidad, precio_unitario " +
                "FROM detalle_factura WHERE numero_factura = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeroFactura);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetalleFactura(
                            rs.getInt("id_detalle"),
                            rs.getString("numero_factura"),
                            rs.getString("producto"),
                            rs.getDouble("cantidad"),
                            rs.getDouble("precio_unitario")));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener detalles", e);
        }

        return detalles;
    }

    // --- Helper ---

    private Factura mapResultSetToFactura(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getString("numero_factura"),
                rs.getDate("fecha_emision").toLocalDate(),
                rs.getDate("fecha_vencimiento").toLocalDate(),
                rs.getString("supplier_id"),
                rs.getDouble("subtotal"),
                rs.getDouble("iva"),
                rs.getDouble("total"),
                rs.getString("observaciones"),
                rs.getString("forma_pago"),
                rs.getString("cuenta_bancaria"));
    }
}
