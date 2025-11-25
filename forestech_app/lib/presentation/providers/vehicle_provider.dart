import '../../data/models/movement_model.dart';
import '../../data/repositories/movement_repository_impl.dart';
import '../../data/repositories/vehicle_repository_impl.dart';
import '../../domain/entities/movement_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/repositories/i_movement_repository.dart';
import '../../domain/repositories/i_vehicle_repository.dart';
import 'base_provider.dart';

/// Statistics for the fleet dashboard
class FleetStats {
  final int total;
  final int active;
  final int inactive;
  final double activeChange;
  final double monthConsumption;

  const FleetStats({
    required this.total,
    required this.active,
    required this.inactive,
    required this.activeChange,
    required this.monthConsumption,
  });

  factory FleetStats.empty() => const FleetStats(
        total: 0,
        active: 0,
        inactive: 0,
        activeChange: 0,
        monthConsumption: 0,
      );
}

/// Category distribution data for pie chart
class CategoryData {
  final String category;
  final int count;

  const CategoryData({required this.category, required this.count});
}

/// Age distribution data for bar chart
class AgeData {
  final String range;
  final int count;

  const AgeData({required this.range, required this.count});
}

/// Consumption data for top consumers chart
class ConsumptionData {
  final String placa;
  final double total;

  const ConsumptionData({required this.placa, required this.total});
}

/// Provider for managing Vehicle state
class VehicleProvider extends BaseProvider<VehicleEntity> {
  final IVehicleRepository _repository;
  final IMovementRepository _movementRepository;
  
  List<MovementEntity> _movements = [];
  List<MovementEntity> get movements => _movements;

  VehicleProvider({
    IVehicleRepository? repository,
    IMovementRepository? movementRepository,
  })  : _repository = repository ?? VehicleRepositoryImpl(),
        _movementRepository = movementRepository ?? MovementRepositoryImpl();

  /// Fetches all vehicles from the repository
  Future<void> fetchVehicles() async {
    setLoading();

    final (vehicles, failure) = await _repository.getAll();

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(vehicles ?? []);
  }

  /// Fetches movements for analytics (only SALIDA with vehicleId)
  Future<void> fetchMovementsForAnalytics() async {
    final (movementsResult, failure) = await _movementRepository.getAll();

    if (failure == null && movementsResult != null) {
      _movements = movementsResult;
      notifyListeners();
    }
  }

  /// Refreshes both vehicles and movements
  Future<void> refresh() async {
    await fetchVehicles();
    await fetchMovementsForAnalytics();
  }

  /// Gets a vehicle by ID
  Future<void> getVehicleById(String id) async {
    setLoading();

    final (vehicle, failure) = await _repository.getById(id);

    if (failure != null) {
      setError(failure);
      return;
    }

    if (vehicle != null) {
      setSelectedItem(vehicle);
    }
  }

  /// Creates a new vehicle
  Future<bool> createVehicle(VehicleEntity vehicle) async {
    setLoading();

    final (createdVehicle, failure) = await _repository.create(vehicle);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (createdVehicle != null) {
      addItem(createdVehicle);
    }

    return true;
  }

  /// Updates an existing vehicle
  Future<bool> updateVehicle(String id, VehicleEntity vehicle) async {
    setLoading();

    final (updatedVehicle, failure) = await _repository.update(id, vehicle);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (updatedVehicle != null) {
      updateItem(updatedVehicle, (v) => v.id == id);
    }

    return true;
  }

  /// Deletes a vehicle
  Future<bool> deleteVehicle(String id) async {
    setLoading();

    final (success, failure) = await _repository.delete(id);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (success) {
      removeItem((v) => v.id == id);
    }

    return success;
  }

  /// Searches vehicles by query
  Future<void> searchVehicles(String query) async {
    if (query.isEmpty) {
      await fetchVehicles();
      return;
    }

    setLoading();

    final (vehicles, failure) = await _repository.search(query);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(vehicles ?? []);
  }

  /// Gets only active vehicles
  List<VehicleEntity> get activeVehicles =>
      items.where((v) => v.isActive).toList();

  /// Calculate fleet statistics (matches React stats useMemo)
  FleetStats get stats {
    final total = items.length;
    final active = items.where((v) => v.isActive).length;
    final inactive = total - active;

    // Calculate month consumption from SALIDA movements
    final now = DateTime.now();
    final thisMonth = DateTime(now.year, now.month, 1);
    
    double monthConsumption = 0;
    for (final m in _movements) {
      if (m.movementType == MovementType.salida && m.vehicleId != null) {
        if (m.createdAt.isAfter(thisMonth) || 
            m.createdAt.isAtSameMomentAs(thisMonth)) {
          monthConsumption += m.quantity;
        }
      }
    }

    return FleetStats(
      total: total,
      active: active,
      inactive: inactive,
      activeChange: 0, // Placeholder - would need historical data
      monthConsumption: monthConsumption,
    );
  }

  /// Fleet by category data for pie chart (matches React categoryData useMemo)
  List<CategoryData> get categoryData {
    final categoryMap = <String, int>{};
    for (final v in items) {
      categoryMap[v.category] = (categoryMap[v.category] ?? 0) + 1;
    }

    return categoryMap.entries
        .map((e) => CategoryData(category: e.key, count: e.value))
        .toList();
  }

  /// Fleet by age data for bar chart (matches React ageData useMemo)
  List<AgeData> get ageData {
    final currentYear = DateTime.now().year;
    final ageRanges = {
      '<5 años': 0,
      '5-10 años': 0,
      '10-15 años': 0,
      '>15 años': 0,
    };

    for (final v in items) {
      final age = currentYear - v.anio;
      if (age < 5) {
        ageRanges['<5 años'] = ageRanges['<5 años']! + 1;
      } else if (age < 10) {
        ageRanges['5-10 años'] = ageRanges['5-10 años']! + 1;
      } else if (age < 15) {
        ageRanges['10-15 años'] = ageRanges['10-15 años']! + 1;
      } else {
        ageRanges['>15 años'] = ageRanges['>15 años']! + 1;
      }
    }

    return ageRanges.entries
        .map((e) => AgeData(range: e.key, count: e.value))
        .toList();
  }

  /// Top 10 consumers data (matches React consumptionData useMemo)
  List<ConsumptionData> get consumptionData {
    final consumptionMap = <String, ({String placa, double total})>{};

    for (final m in _movements) {
      if (m.movementType == MovementType.salida && m.vehicleId != null) {
        final vehicle = items.firstWhere(
          (v) => v.id == m.vehicleId,
          orElse: () => VehicleEntity.empty(),
        );
        if (vehicle.id.isNotEmpty) {
          final current = consumptionMap[m.vehicleId!] ?? 
              (placa: vehicle.placa, total: 0.0);
          consumptionMap[m.vehicleId!] = (
            placa: current.placa,
            total: current.total + m.quantity,
          );
        }
      }
    }

    final sorted = consumptionMap.entries.toList()
      ..sort((a, b) => b.value.total.compareTo(a.value.total));

    return sorted
        .take(10)
        .map((e) => ConsumptionData(placa: e.value.placa, total: e.value.total))
        .toList();
  }
}
