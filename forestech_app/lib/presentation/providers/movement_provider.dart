import '../../data/repositories/movement_repository_impl.dart';
import '../../data/repositories/product_repository_impl.dart';
import '../../data/repositories/vehicle_repository_impl.dart';
import '../../domain/entities/movement_entity.dart';
import '../../domain/entities/product_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/repositories/i_movement_repository.dart';
import '../../domain/repositories/i_product_repository.dart';
import '../../domain/repositories/i_vehicle_repository.dart';
import '../../data/models/movement_model.dart';
import 'base_provider.dart';

/// Date range options matching React's DateRange type
enum DateRange {
  days7(7),
  days15(15),
  days30(30),
  days90(90);

  final int value;
  const DateRange(this.value);
}

/// Movement stats matching React's stats calculation
class MovementStats {
  final double todayEntries;
  final double todayExits;
  final double monthEntries;
  final double monthExits;
  final double lastMonthEntries;
  final double lastMonthExits;
  final int todayEntriesCount;
  final int todayExitsCount;
  final int monthEntriesCount;
  final int monthExitsCount;
  final double totalStock;
  final double balance;
  final double entriesChange;
  final double exitsChange;

  const MovementStats({
    required this.todayEntries,
    required this.todayExits,
    required this.monthEntries,
    required this.monthExits,
    required this.lastMonthEntries,
    required this.lastMonthExits,
    required this.todayEntriesCount,
    required this.todayExitsCount,
    required this.monthEntriesCount,
    required this.monthExitsCount,
    required this.totalStock,
    required this.balance,
    required this.entriesChange,
    required this.exitsChange,
  });

  factory MovementStats.empty() => const MovementStats(
        todayEntries: 0,
        todayExits: 0,
        monthEntries: 0,
        monthExits: 0,
        lastMonthEntries: 0,
        lastMonthExits: 0,
        todayEntriesCount: 0,
        todayExitsCount: 0,
        monthEntriesCount: 0,
        monthExitsCount: 0,
        totalStock: 0,
        balance: 0,
        entriesChange: 0,
        exitsChange: 0,
      );
}

/// Timeline data item for charts
class TimelineDataItem {
  final String date;
  final double entries;
  final double exits;

  const TimelineDataItem({
    required this.date,
    required this.entries,
    required this.exits,
  });
}

/// Product movement item for bar chart
class ProductMovementItem {
  final String name;
  final double entries;
  final double exits;
  final double total;

  const ProductMovementItem({
    required this.name,
    required this.entries,
    required this.exits,
    required this.total,
  });
}

/// Alert type matching React's alert types
enum AlertType { lowStock, highConsumption, noMovement }

/// Alert severity matching React's severity
enum AlertSeverity { error, warning, info }

/// Alert item matching React's alerts array
class AlertItem {
  final AlertType type;
  final String message;
  final AlertSeverity severity;

  const AlertItem({
    required this.type,
    required this.message,
    required this.severity,
  });
}

/// Provider for managing Movement state
/// Matches React MovementsPanel logic including all analytics
class MovementProvider extends BaseProvider<MovementEntity> {
  final IMovementRepository _repository;
  final IProductRepository _productRepository;
  final IVehicleRepository _vehicleRepository;

  // Lookup maps matching React's products and vehicles state
  Map<String, ProductEntity> _productsMap = {};
  Map<String, VehicleEntity> _vehiclesMap = {};

  // Filter state matching React's typeFilter
  MovementTypeFilter _typeFilter = MovementTypeFilter.all;

  // Date range for timeline
  DateRange _dateRange = DateRange.days30;

  // Computed analytics matching React's useMemo calculations
  MovementStats _stats = MovementStats.empty();
  List<TimelineDataItem> _timelineData = [];
  List<ProductMovementItem> _productMovements = [];
  List<AlertItem> _alerts = [];

  // Getters
  Map<String, ProductEntity> get productsMap => _productsMap;
  Map<String, VehicleEntity> get vehiclesMap => _vehiclesMap;
  MovementTypeFilter get typeFilter => _typeFilter;
  DateRange get dateRange => _dateRange;
  MovementStats get stats => _stats;
  List<TimelineDataItem> get timelineData => _timelineData;
  List<ProductMovementItem> get productMovements => _productMovements;
  List<AlertItem> get alerts => _alerts;

  MovementProvider({
    IMovementRepository? repository,
    IProductRepository? productRepository,
    IVehicleRepository? vehicleRepository,
  })  : _repository = repository ?? MovementRepositoryImpl(),
        _productRepository = productRepository ?? ProductRepositoryImpl(),
        _vehicleRepository = vehicleRepository ?? VehicleRepositoryImpl();

  /// Get product by ID from lookup map
  ProductEntity? getProduct(String productId) => _productsMap[productId];

  /// Get vehicle by ID from lookup map
  VehicleEntity? getVehicle(String? vehicleId) =>
      vehicleId != null ? _vehiclesMap[vehicleId] : null;

  /// Set type filter and reload (matches React's handleFilterChange)
  Future<void> setTypeFilter(MovementTypeFilter filter) async {
    _typeFilter = filter;
    notifyListeners();
    await fetchMovements();
  }

  /// Set date range for timeline (matches React's setDateRange)
  void setDateRange(DateRange range) {
    _dateRange = range;
    _calculateTimelineData();
    notifyListeners();
  }

  /// Fetches all data: movements, products, and vehicles
  /// Matches React's loadData function with Promise.all
  Future<void> loadAllData() async {
    setLoading();

    try {
      // Parallel fetch matching React's Promise.all
      final results = await Future.wait([
        _repository.getAll(type: _typeFilter),
        _productRepository.getAll(),
        _vehicleRepository.getAll(),
      ]);

      final (movements, movementFailure) =
          results[0] as (List<MovementEntity>?, dynamic);
      final (products, productFailure) =
          results[1] as (List<ProductEntity>?, dynamic);
      final (vehicles, vehicleFailure) =
          results[2] as (List<VehicleEntity>?, dynamic);

      if (movementFailure != null) {
        setError(movementFailure);
        return;
      }

      // Build lookup maps matching React's reduce operations
      _productsMap = {
        for (final p in products ?? []) p.id: p,
      };
      _vehiclesMap = {
        for (final v in vehicles ?? []) v.id: v,
      };

      setSuccess(movements ?? []);
      _calculateAllAnalytics();
    } catch (e) {
      setErrorMessage(e.toString());
    }
  }

  /// Fetches all movements from the repository
  Future<void> fetchMovements() async {
    setLoading();

    final (movements, failure) = await _repository.getAll(type: _typeFilter);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(movements ?? []);
    _calculateAllAnalytics();
  }

  /// Gets a movement by ID
  Future<void> getMovementById(String id) async {
    setLoading();

    final (movement, failure) = await _repository.getById(id);

    if (failure != null) {
      setError(failure);
      return;
    }

    if (movement != null) {
      setSelectedItem(movement);
    }
  }

  /// Creates a new movement (routes to entrada/salida based on type)
  Future<bool> createMovement(MovementEntity movement) async {
    setLoading();

    final (createdMovement, failure) = await _repository.create(movement);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (createdMovement != null) {
      addItem(createdMovement);
      _calculateAllAnalytics();
    }

    return true;
  }

  /// Creates a SALIDA movement specifically
  Future<bool> createSalida(MovementEntity movement) async {
    setLoading();

    final (createdMovement, failure) = await _repository.createSalida(movement);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (createdMovement != null) {
      addItem(createdMovement);
      _calculateAllAnalytics();
    }

    return true;
  }

  /// Updates an existing movement
  Future<bool> updateMovement(String id, MovementEntity movement) async {
    setLoading();

    final (updatedMovement, failure) = await _repository.update(id, movement);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (updatedMovement != null) {
      updateItem(updatedMovement, (m) => m.id == id);
      _calculateAllAnalytics();
    }

    return true;
  }

  /// Deletes a movement
  Future<bool> deleteMovement(String id) async {
    setLoading();

    final (success, failure) = await _repository.delete(id);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (success) {
      removeItem((m) => m.id == id);
      _calculateAllAnalytics();
    }

    return success;
  }

  /// Fetches movements by product ID
  Future<void> fetchByProductId(String productId) async {
    setLoading();

    final (movements, failure) = await _repository.getByProductId(productId);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(movements ?? []);
  }

  /// Fetches movements by vehicle ID
  Future<void> fetchByVehicleId(String vehicleId) async {
    setLoading();

    final (movements, failure) = await _repository.getByVehicleId(vehicleId);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(movements ?? []);
  }

  /// Gets only ENTRADA movements
  List<MovementEntity> get entradas =>
      items.where((m) => m.movementType == MovementType.entrada).toList();

  /// Gets only SALIDA movements
  List<MovementEntity> get salidas =>
      items.where((m) => m.movementType == MovementType.salida).toList();

  /// Calculates total quantity for a specific product
  double getTotalQuantityByProduct(String productId) {
    return items.where((m) => m.productId == productId).fold(0.0, (sum, m) {
      if (m.movementType == MovementType.entrada) {
        return sum + m.quantity;
      } else {
        return sum - m.quantity;
      }
    });
  }

  /// Calculate all analytics
  void _calculateAllAnalytics() {
    _calculateStats();
    _calculateTimelineData();
    _calculateProductMovements();
    _calculateAlerts();
    notifyListeners();
  }

  /// Calculate stats matching React's stats useMemo
  void _calculateStats() {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final thisMonth = DateTime(now.year, now.month, 1);
    final lastMonth = DateTime(now.year, now.month - 1, 1);

    double todayEntries = 0;
    double todayExits = 0;
    double monthEntries = 0;
    double monthExits = 0;
    double lastMonthEntries = 0;
    double lastMonthExits = 0;
    int todayEntriesCount = 0;
    int todayExitsCount = 0;
    int monthEntriesCount = 0;
    int monthExitsCount = 0;

    for (final m in items) {
      final movementDate = m.createdAt;
      final isToday = movementDate.isAfter(today) ||
          movementDate.isAtSameMomentAs(today);
      final isThisMonth = movementDate.isAfter(thisMonth) ||
          movementDate.isAtSameMomentAs(thisMonth);
      final isLastMonth =
          (movementDate.isAfter(lastMonth) ||
                  movementDate.isAtSameMomentAs(lastMonth)) &&
              movementDate.isBefore(thisMonth);

      if (m.movementType == MovementType.entrada) {
        if (isToday) {
          todayEntries += m.quantity;
          todayEntriesCount++;
        }
        if (isThisMonth) {
          monthEntries += m.quantity;
          monthEntriesCount++;
        }
        if (isLastMonth) {
          lastMonthEntries += m.quantity;
        }
      } else {
        if (isToday) {
          todayExits += m.quantity;
          todayExitsCount++;
        }
        if (isThisMonth) {
          monthExits += m.quantity;
          monthExitsCount++;
        }
        if (isLastMonth) {
          lastMonthExits += m.quantity;
        }
      }
    }

    // Calculate current stock per product
    final stockMap = <String, double>{};
    for (final m in items) {
      final current = stockMap[m.productId] ?? 0;
      final change =
          m.movementType == MovementType.entrada ? m.quantity : -m.quantity;
      stockMap[m.productId] = current + change;
    }

    final totalStock = stockMap.values.fold(0.0, (sum, val) => sum + val);

    // Calculate changes
    final entriesChange = lastMonthEntries > 0
        ? ((monthEntries - lastMonthEntries) / lastMonthEntries) * 100
        : 0.0;
    final exitsChange = lastMonthExits > 0
        ? ((monthExits - lastMonthExits) / lastMonthExits) * 100
        : 0.0;

    _stats = MovementStats(
      todayEntries: todayEntries,
      todayExits: todayExits,
      monthEntries: monthEntries,
      monthExits: monthExits,
      lastMonthEntries: lastMonthEntries,
      lastMonthExits: lastMonthExits,
      todayEntriesCount: todayEntriesCount,
      todayExitsCount: todayExitsCount,
      monthEntriesCount: monthEntriesCount,
      monthExitsCount: monthExitsCount,
      totalStock: totalStock,
      balance: monthEntries - monthExits,
      entriesChange: entriesChange,
      exitsChange: exitsChange,
    );
  }

  /// Calculate timeline data matching React's timelineData useMemo
  void _calculateTimelineData() {
    final now = DateTime.now();
    final startDate = now.subtract(Duration(days: _dateRange.value));

    // Create map for each day
    final dailyData = <String, ({double entries, double exits})>{};

    // Initialize all days with 0
    for (int i = 0; i <= _dateRange.value; i++) {
      final date = startDate.add(Duration(days: i));
      final dateKey = _formatDateKey(date);
      dailyData[dateKey] = (entries: 0.0, exits: 0.0);
    }

    // Fill with actual data
    for (final m in items) {
      final movementDate = m.createdAt;
      if (movementDate.isAfter(startDate) ||
          movementDate.isAtSameMomentAs(startDate)) {
        final dateKey = _formatDateKey(movementDate);
        final current = dailyData[dateKey] ?? (entries: 0.0, exits: 0.0);
        if (m.movementType == MovementType.entrada) {
          dailyData[dateKey] =
              (entries: current.entries + m.quantity, exits: current.exits);
        } else {
          dailyData[dateKey] =
              (entries: current.entries, exits: current.exits + m.quantity);
        }
      }
    }

    // Convert to array sorted by date
    final sortedKeys = dailyData.keys.toList()..sort();
    _timelineData = sortedKeys.map((date) {
      final data = dailyData[date]!;
      return TimelineDataItem(
        date: _formatDisplayDate(date),
        entries: data.entries,
        exits: data.exits,
      );
    }).toList();
  }

  /// Calculate product movements matching React's productMovements useMemo
  void _calculateProductMovements() {
    final productStats = <String, ({double entries, double exits, String name})>{};

    for (final m in items) {
      final productName = _productsMap[m.productId]?.name ?? 'Desconocido';
      final current = productStats[m.productId] ??
          (entries: 0.0, exits: 0.0, name: productName);

      if (m.movementType == MovementType.entrada) {
        productStats[m.productId] = (
          entries: current.entries + m.quantity,
          exits: current.exits,
          name: current.name,
        );
      } else {
        productStats[m.productId] = (
          entries: current.entries,
          exits: current.exits + m.quantity,
          name: current.name,
        );
      }
    }

    // Convert to list and sort by total movement
    final productList = productStats.entries
        .map((e) => ProductMovementItem(
              name: e.value.name,
              entries: e.value.entries,
              exits: e.value.exits,
              total: e.value.entries + e.value.exits,
            ))
        .toList()
      ..sort((a, b) => b.total.compareTo(a.total));

    // Top 5 products
    _productMovements = productList.take(5).toList();
  }

  /// Calculate alerts matching React's alerts useMemo
  void _calculateAlerts() {
    final detectedAlerts = <AlertItem>[];

    // Calculate stock per product
    final stockMap = <String, double>{};
    for (final m in items) {
      final current = stockMap[m.productId] ?? 0;
      final change =
          m.movementType == MovementType.entrada ? m.quantity : -m.quantity;
      stockMap[m.productId] = current + change;
    }

    // Low stock detection (less than 500 gallons)
    stockMap.forEach((productId, stock) {
      final productName = _productsMap[productId]?.name ?? 'Desconocido';
      if (stock < 500 && stock > 0) {
        detectedAlerts.add(AlertItem(
          type: AlertType.lowStock,
          message: 'Stock bajo: $productName tiene solo ${stock.toStringAsFixed(0)} gal',
          severity: AlertSeverity.warning,
        ));
      }
      if (stock <= 0) {
        detectedAlerts.add(AlertItem(
          type: AlertType.lowStock,
          message: 'Sin stock: $productName está agotado',
          severity: AlertSeverity.error,
        ));
      }
    });

    // High consumption detection (exits > 2x entries in last 7 days)
    final sevenDaysAgo = DateTime.now().subtract(const Duration(days: 7));

    final recentByProduct = <String, ({double entries, double exits})>{};
    for (final m in items) {
      if (m.createdAt.isAfter(sevenDaysAgo)) {
        final current =
            recentByProduct[m.productId] ?? (entries: 0.0, exits: 0.0);
        if (m.movementType == MovementType.entrada) {
          recentByProduct[m.productId] =
              (entries: current.entries + m.quantity, exits: current.exits);
        } else {
          recentByProduct[m.productId] =
              (entries: current.entries, exits: current.exits + m.quantity);
        }
      }
    }

    recentByProduct.forEach((productId, data) {
      final productName = _productsMap[productId]?.name ?? 'Desconocido';
      if (data.exits > data.entries * 2 && data.exits > 1000) {
        detectedAlerts.add(AlertItem(
          type: AlertType.highConsumption,
          message:
              'Consumo alto: $productName - Salidas (${data.exits.toStringAsFixed(0)}) > 2x Entradas (${data.entries.toStringAsFixed(0)}) últimos 7 días',
          severity: AlertSeverity.info,
        ));
      }
    });

    _alerts = detectedAlerts;
  }

  /// Format date key for map
  String _formatDateKey(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
  }

  /// Format date for display
  String _formatDisplayDate(String dateKey) {
    final parts = dateKey.split('-');
    final months = [
      'ene',
      'feb',
      'mar',
      'abr',
      'may',
      'jun',
      'jul',
      'ago',
      'sep',
      'oct',
      'nov',
      'dic'
    ];
    final month = int.parse(parts[1]) - 1;
    final day = int.parse(parts[2]);
    return '${months[month]} $day';
  }
}
