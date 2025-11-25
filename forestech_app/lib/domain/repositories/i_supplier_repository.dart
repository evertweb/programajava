import '../entities/supplier_entity.dart';
import '../../core/errors/failures.dart';

/// Abstract repository interface for Supplier operations
/// Defines the contract that the data layer must implement
abstract class ISupplierRepository {
  /// Retrieves all suppliers from the data source
  Future<(List<SupplierEntity>?, Failure?)> getAll();

  /// Retrieves a single supplier by its ID
  Future<(SupplierEntity?, Failure?)> getById(String id);

  /// Creates a new supplier
  Future<(SupplierEntity?, Failure?)> create(SupplierEntity supplier);

  /// Updates an existing supplier
  Future<(SupplierEntity?, Failure?)> update(String id, SupplierEntity supplier);

  /// Deletes a supplier by its ID
  Future<(bool, Failure?)> delete(String id);

  /// Searches suppliers by query string
  Future<(List<SupplierEntity>?, Failure?)> search(String query);
}
