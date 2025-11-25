import '../entities/product_entity.dart';
import '../../core/errors/failures.dart';

/// Abstract repository interface for Product operations
/// Defines the contract that the data layer must implement
abstract class IProductRepository {
  /// Retrieves all products from the data source
  Future<(List<ProductEntity>?, Failure?)> getAll();

  /// Retrieves a single product by its ID
  Future<(ProductEntity?, Failure?)> getById(String id);

  /// Creates a new product
  Future<(ProductEntity?, Failure?)> create(ProductEntity product);

  /// Updates an existing product
  Future<(ProductEntity?, Failure?)> update(String id, ProductEntity product);

  /// Deletes a product by its ID
  Future<(bool, Failure?)> delete(String id);

  /// Searches products by query string
  Future<(List<ProductEntity>?, Failure?)> search(String query);
}
