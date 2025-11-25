import '../entities/invoice_entity.dart';
import '../../core/errors/failures.dart';

/// Abstract repository interface for Invoice operations
/// Defines the contract that the data layer must implement
abstract class IInvoiceRepository {
  /// Retrieves all invoices from the data source
  Future<(List<InvoiceEntity>?, Failure?)> getAll();

  /// Retrieves a single invoice by its numero factura
  Future<(InvoiceEntity?, Failure?)> getByNumeroFactura(String numeroFactura);

  /// Creates a new invoice
  Future<(InvoiceEntity?, Failure?)> create(InvoiceEntity invoice);

  /// Updates an existing invoice
  Future<(InvoiceEntity?, Failure?)> update(
      String numeroFactura, InvoiceEntity invoice);

  /// Deletes an invoice by its numero factura
  Future<(bool, Failure?)> delete(String numeroFactura);

  /// Retrieves invoices filtered by supplier ID
  Future<(List<InvoiceEntity>?, Failure?)> getBySupplierId(String supplierId);

  /// Retrieves invoices filtered by status
  Future<(List<InvoiceEntity>?, Failure?)> getByEstado(String estado);

  /// Cancels an invoice (sets estado to ANULADA)
  /// This action cannot be undone
  Future<(bool, Failure?)> cancel(String id);
}
