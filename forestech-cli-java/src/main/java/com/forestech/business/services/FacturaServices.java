package com.forestech.business.services;

import com.forestech.config.DatabaseConnection;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.TransactionFailedException;
import com.forestech.data.models.Factura;
import com.forestech.data.models.DetalleFactura;
import com.forestech.business.services.interfaces.IFacturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar facturas con transacciones.
 *
 * @version 3.0 (implementa IFacturaService, instance methods)
 */
public class FacturaServices implements IFacturaService {

    // Singleton instance (lazy initialization)
    private static FacturaServices instance;

    private static final Logger logger = LoggerFactory.getLogger(FacturaServices.class);
    private final SupplierServices supplierServices;

    /**
     * Constructor PRIVADO (Singleton pattern).
     * Usa getInstance() de SupplierServices para la dependencia.
     */
    private FacturaServices() {
        this.supplierServices = SupplierServices.getInstance();
    }

    /**
     * Obtiene la instancia única de FacturaServices (thread-safe).
     *
     * @return Instancia única de FacturaServices
     */
    public static synchronized FacturaServices getInstance() {
        if (instance == null) {
            instance = new FacturaServices();
        }
        return instance;
    }

    /**
     * Crea una factura con sus detalles en UNA SOLA TRANSACCIÓN CON VALIDACIONES.
     * Si algo falla, hace ROLLBACK de todo.
     *
     * <p><strong>⚠️ VALIDACIONES AUTOMÁTICAS (antes del INSERT):</strong></p>
     * <ol>
     *   <li><strong>supplier_id:</strong> Si NO es NULL, DEBE existir en suppliers</li>
     * </ol>
     */
    @Override
    public void createFacturaWithDetails(Factura factura, List<DetalleFactura> detalles)
            throws TransactionFailedException, DatabaseException {

        // ========================================================================
        // VALIDACIÓN: supplier_id (solo si NO es NULL, debe existir)
        // ========================================================================
        if (factura.getSupplierId() != null && !factura.getSupplierId().trim().isEmpty()) {
            if (!supplierServices.existsSupplier(factura.getSupplierId())) {
                throw new DatabaseException(
                    "ERROR: El proveedor '" + factura.getSupplierId() + "' NO existe en suppliers. " +
                    "Debes crear el proveedor primero antes de usarlo en una factura.",
                    new IllegalArgumentException("supplier_id inválido")
                );
            }
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Iniciar transacción

            // PASO 1: Insertar factura
            String sqlFactura = "INSERT INTO facturas (numero_factura, fecha_emision, " +
                    "fecha_vencimiento, supplier_id, subtotal, iva, total, observaciones, " +
                    "forma_pago, cuenta_bancaria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlFactura)) {
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

            // PASO 2: Insertar detalles
            String sqlDetalle = "INSERT INTO detalle_factura (numero_factura, producto, " +
                    "cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {
                for (DetalleFactura detalle : detalles) {
                    pstmt.setString(1, factura.getNumeroFactura());
                    pstmt.setString(2, detalle.getProducto());
                    pstmt.setDouble(3, detalle.getCantidad());
                    pstmt.setDouble(4, detalle.getPrecioUnitario());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();  // ✅ TODO OK, confirmar transacción
            logger.info("Factura creada con transacción exitosa - Número: {}, Detalles: {}",
                factura.getNumeroFactura(), detalles.size());
            System.out.println("✅ Factura creada con " + detalles.size() + " detalles");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // ❌ ERROR, revertir TODO
                    logger.warn("Transacción revertida para factura: {}", factura.getNumeroFactura());
                    System.out.println("⚠️  Transacción revertida");
                } catch (SQLException ex) {
                    logger.error("Error al hacer rollback de factura: {}", factura.getNumeroFactura(), ex);
                    throw new TransactionFailedException("Error al hacer rollback", ex);
                }
            }
            logger.error("Error al crear factura con transacción - Número: {}", factura.getNumeroFactura(), e);
            throw new TransactionFailedException("Error al crear factura", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // READ: Obtener todas las facturas
    @Override
    public List<Factura> getAllFacturas() throws DatabaseException {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT numero_factura, fecha_emision, fecha_vencimiento, supplier_id, " +
                "subtotal, iva, total, observaciones, forma_pago, cuenta_bancaria FROM facturas " +
                "ORDER BY fecha_emision DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                facturas.add(new Factura(
                        rs.getString("numero_factura"),
                        rs.getDate("fecha_emision").toLocalDate(),
                        rs.getDate("fecha_vencimiento").toLocalDate(),
                        rs.getString("supplier_id"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("iva"),
                        rs.getDouble("total"),
                        rs.getString("observaciones"),
                        rs.getString("forma_pago"),
                        rs.getString("cuenta_bancaria")
                ));
            }

        } catch (SQLException e) {
            logger.error("Error al obtener todas las facturas", e);
            throw new DatabaseException("Error al obtener facturas", e);
        }

        return facturas;
    }

    // READ BY ID
    /**
     * Busca una factura por su número.
     * ÚTIL PARA VALIDAR FOREIGN KEYS antes de insertar Movement.
     *
     * @param numeroFactura Número de factura
     * @return Factura encontrada, o null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public Factura getFacturaByNumero(String numeroFactura) throws DatabaseException {
        String sql = "SELECT numero_factura, fecha_emision, fecha_vencimiento, supplier_id, " +
                "subtotal, iva, total, observaciones, forma_pago, cuenta_bancaria " +
                "FROM facturas WHERE numero_factura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeroFactura);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
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
                        rs.getString("cuenta_bancaria")
                );
            }
            return null;

        } catch (SQLException e) {
            logger.error("Error al buscar factura por número: {}", numeroFactura, e);
            throw new DatabaseException("Error al buscar factura", e);
        }
    }

    /**
     * Verifica si una factura existe en la base de datos.
     * Método de conveniencia para validaciones de FK.
     *
     * @param numeroFactura Número de factura a verificar
     * @return true si existe, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    @Override
    public boolean existsFactura(String numeroFactura) throws DatabaseException {
        return getFacturaByNumero(numeroFactura) != null;
    }

    // READ: Obtener detalles de una factura
    @Override
    public List<DetalleFactura> getDetallesByFactura(String numeroFactura)
            throws DatabaseException {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = "SELECT id_detalle, numero_factura, producto, cantidad, precio_unitario " +
                "FROM detalle_factura WHERE numero_factura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numeroFactura);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                detalles.add(new DetalleFactura(
                        rs.getInt("id_detalle"),
                        rs.getString("numero_factura"),
                        rs.getString("producto"),
                        rs.getDouble("cantidad"),
                        rs.getDouble("precio_unitario")
                ));
            }

        } catch (SQLException e) {
            logger.error("Error al obtener detalles de factura: {}", numeroFactura, e);
            throw new DatabaseException("Error al obtener detalles", e);
        }

        return detalles;
    }
}
