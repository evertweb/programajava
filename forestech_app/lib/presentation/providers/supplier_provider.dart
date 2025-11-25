import '../../data/repositories/supplier_repository_impl.dart';
import '../../domain/entities/supplier_entity.dart';
import '../../domain/repositories/i_supplier_repository.dart';
import 'base_provider.dart';

/// Provider for managing Supplier state
class SupplierProvider extends BaseProvider<SupplierEntity> {
  final ISupplierRepository _repository;

  SupplierProvider({ISupplierRepository? repository})
      : _repository = repository ?? SupplierRepositoryImpl();

  /// Fetches all suppliers from the repository
  Future<void> fetchSuppliers() async {
    setLoading();

    final (suppliers, failure) = await _repository.getAll();

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(suppliers ?? []);
  }

  /// Gets a supplier by ID
  Future<void> getSupplierById(String id) async {
    setLoading();

    final (supplier, failure) = await _repository.getById(id);

    if (failure != null) {
      setError(failure);
      return;
    }

    if (supplier != null) {
      setSelectedItem(supplier);
    }
  }

  /// Creates a new supplier
  Future<bool> createSupplier(SupplierEntity supplier) async {
    setLoading();

    final (createdSupplier, failure) = await _repository.create(supplier);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (createdSupplier != null) {
      addItem(createdSupplier);
    }

    return true;
  }

  /// Updates an existing supplier
  Future<bool> updateSupplier(String id, SupplierEntity supplier) async {
    setLoading();

    final (updatedSupplier, failure) = await _repository.update(id, supplier);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (updatedSupplier != null) {
      updateItem(updatedSupplier, (s) => s.id == id);
    }

    return true;
  }

  /// Deletes a supplier
  Future<bool> deleteSupplier(String id) async {
    setLoading();

    final (success, failure) = await _repository.delete(id);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (success) {
      removeItem((s) => s.id == id);
    }

    return success;
  }

  /// Searches suppliers by query
  Future<void> searchSuppliers(String query) async {
    if (query.isEmpty) {
      await fetchSuppliers();
      return;
    }

    setLoading();

    final (suppliers, failure) = await _repository.search(query);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(suppliers ?? []);
  }

  /// Gets a supplier name by ID (useful for display)
  String getSupplierName(String id) {
    final supplier = items.firstWhere(
      (s) => s.id == id,
      orElse: () => SupplierEntity.empty(),
    );
    return supplier.name;
  }
}
