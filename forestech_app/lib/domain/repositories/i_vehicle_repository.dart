import '../entities/vehicle_entity.dart';
import '../../core/errors/failures.dart';

/// Abstract repository interface for Vehicle operations
/// Defines the contract that the data layer must implement
abstract class IVehicleRepository {
  /// Retrieves all vehicles from the data source
  Future<(List<VehicleEntity>?, Failure?)> getAll();

  /// Retrieves a single vehicle by its ID
  Future<(VehicleEntity?, Failure?)> getById(String id);

  /// Creates a new vehicle
  Future<(VehicleEntity?, Failure?)> create(VehicleEntity vehicle);

  /// Updates an existing vehicle
  Future<(VehicleEntity?, Failure?)> update(String id, VehicleEntity vehicle);

  /// Deletes a vehicle by its ID
  Future<(bool, Failure?)> delete(String id);

  /// Searches vehicles by query string
  Future<(List<VehicleEntity>?, Failure?)> search(String query);
}
