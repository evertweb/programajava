package com.forestech.business.services.interfaces;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.TransactionFailedException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.data.models.DetalleFactura;
import com.forestech.data.models.Factura;

import java.util.List;

/**
 * Interfaz para servicios de facturas.
 *
 * <p>Define el contrato para operaciones CRUD de facturas con transacciones.</p>
 *
 * @version 1.0
 */
public interface IFacturaService {

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Crea una factura con sus detalles en UNA SOLA TRANSACCIÓN.
     * Si algo falla, hace ROLLBACK de todo.
     *
     * @param factura Factura a insertar
     * @param detalles Lista de detalles de la factura
     * @throws TransactionFailedException Si la transacción falla
     * @throws DatabaseException Si hay error de BD
     * @throws ValidationException Si los datos no son válidos
     */
    void createFacturaWithDetails(Factura factura, List<DetalleFactura> detalles)
        throws TransactionFailedException, DatabaseException, ValidationException;

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Obtiene todas las facturas.
     *
     * @return Lista de todas las facturas
     * @throws DatabaseException Si hay error de BD
     */
    List<Factura> getAllFacturas() throws DatabaseException;

    /**
     * Busca una factura por su número.
     *
     * @param numeroFactura Número de factura
     * @return Factura encontrada, o null si no existe
     * @throws DatabaseException Si hay error de BD
     */
    Factura getFacturaByNumero(String numeroFactura) throws DatabaseException;

    /**
     * Verifica si una factura existe.
     *
     * @param numeroFactura Número de factura
     * @return true si existe, false si no
     * @throws DatabaseException Si hay error de BD
     */
    boolean existsFactura(String numeroFactura) throws DatabaseException;

    /**
     * Obtiene los detalles de una factura específica.
     *
     * @param numeroFactura Número de factura
     * @return Lista de detalles de la factura
     * @throws DatabaseException Si hay error de BD
     */
    List<DetalleFactura> getDetallesByFactura(String numeroFactura) throws DatabaseException;
}
