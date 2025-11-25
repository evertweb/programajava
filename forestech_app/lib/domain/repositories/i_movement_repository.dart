import '../entities/movement_entity.dart';
import '../../core/errors/failures.dart';

/// Movement type filter matching React's MovementTypeFilter
enum MovementTypeFilter { entrada, salida, all }

/// Abstract repository interface for Movement operations
/// Defines the contract that the data layer must implement
abstract class IMovementRepository {
  /// Retrieves all movements from the data source
  /// Optionally filtered by movement type (matches React's getAll with type filter)
  Future<(List<MovementEntity>?, Failure?)> getAll({MovementTypeFilter? type});

  /// Retrieves a single movement by its ID
  Future<(MovementEntity?, Failure?)> getById(String id);

  /// Creates a new ENTRADA movement (matches React's /movements/entrada endpoint)
  Future<(MovementEntity?, Failure?)> createEntrada(MovementEntity movement);

  /// Creates a new SALIDA movement (matches React's /movements/salida endpoint)
  Future<(MovementEntity?, Failure?)> createSalida(MovementEntity movement);

  /// Creates a new movement (legacy - routes to entrada/salida based on type)
  Future<(MovementEntity?, Failure?)> create(MovementEntity movement);

  /// Updates an existing movement
  Future<(MovementEntity?, Failure?)> update(String id, MovementEntity movement);

  /// Deletes a movement by its ID
  Future<(bool, Failure?)> delete(String id);

  /// Retrieves movements filtered by product ID
  Future<(List<MovementEntity>?, Failure?)> getByProductId(String productId);

  /// Retrieves movements filtered by vehicle ID
  Future<(List<MovementEntity>?, Failure?)> getByVehicleId(String vehicleId);

  /// Gets current stock for a product (matches React's getStock)
  Future<(double?, Failure?)> getStock(String productId);

  /// Gets valued stock for a product (matches React's getStockValued)
  Future<(({double stock, double weightedAveragePrice})?, Failure?)> getStockValued(
    String productId,
  );
}
