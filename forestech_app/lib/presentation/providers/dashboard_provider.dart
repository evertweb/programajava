import 'dart:math';
import '../../data/repositories/repositories.dart';
import '../../domain/entities/entities.dart';
import '../../domain/repositories/i_product_repository.dart';
import '../../domain/repositories/i_vehicle_repository.dart';
import '../../domain/repositories/i_movement_repository.dart';
import '../../domain/repositories/i_invoice_repository.dart';
import '../../domain/repositories/i_supplier_repository.dart';
import '../../data/models/movement_model.dart';
import 'base_provider.dart';

/// Dashboard metrics matching React's DashboardMetrics interface
class DashboardMetrics {
  final int productsCount;
  final int vehiclesCount;
  final int movementsCount;
  final int invoicesCount;
  final int suppliersCount;
  final List<ProductEntity> products;
  final List<MovementEntity> movements;

  const DashboardMetrics({
    required this.productsCount,
    required this.vehiclesCount,
    required this.movementsCount,
    required this.invoicesCount,
    required this.suppliersCount,
    required this.products,
    required this.movements,
  });

  factory DashboardMetrics.empty() => const DashboardMetrics(
        productsCount: 0,
        vehiclesCount: 0,
        movementsCount: 0,
        invoicesCount: 0,
        suppliersCount: 0,
        products: [],
        movements: [],
      );
}

/// Scale type options matching React's ScaleType
enum ScaleType {
  linear,
  log,
  percent,
}

/// Stock data item for bar chart
class StockDataItem {
  final String id;
  final String name;
  final double stock;
  final double originalStock;

  const StockDataItem({
    required this.id,
    required this.name,
    required this.stock,
    required this.originalStock,
  });
}

/// Movement distribution item for pie chart
class MovementDistributionItem {
  final int id;
  final int value;
  final String label;
  final int color;

  const MovementDistributionItem({
    required this.id,
    required this.value,
    required this.label,
    required this.color,
  });
}

/// Provider for managing Dashboard state
/// Matches React Dashboard logic exactly
class DashboardProvider extends BaseProvider<DashboardMetrics> {
  final IProductRepository _productRepository;
  final IVehicleRepository _vehicleRepository;
  final IMovementRepository _movementRepository;
  final IInvoiceRepository _invoiceRepository;
  final ISupplierRepository _supplierRepository;

  DashboardMetrics _metrics = DashboardMetrics.empty();
  ScaleType _scaleType = ScaleType.log;
  bool _isDisconnected = false;

  // Getters
  DashboardMetrics get metrics => _metrics;
  ScaleType get scaleType => _scaleType;
  bool get isDisconnected => _isDisconnected;

  DashboardProvider({
    IProductRepository? productRepository,
    IVehicleRepository? vehicleRepository,
    IMovementRepository? movementRepository,
    IInvoiceRepository? invoiceRepository,
    ISupplierRepository? supplierRepository,
  })  : _productRepository = productRepository ?? ProductRepositoryImpl(),
        _vehicleRepository = vehicleRepository ?? VehicleRepositoryImpl(),
        _movementRepository = movementRepository ?? MovementRepositoryImpl(),
        _invoiceRepository = invoiceRepository ?? InvoiceRepositoryImpl(),
        _supplierRepository = supplierRepository ?? SupplierRepositoryImpl();

  /// Set scale type for the stock chart
  void setScaleType(ScaleType type) {
    _scaleType = type;
    notifyListeners();
  }

  /// Fetch all dashboard metrics in parallel (matching React's Promise.all)
  Future<void> fetchMetrics() async {
    setLoading();
    _isDisconnected = false;

    try {
      // Fetch all data in parallel (replicating React's Promise.all)
      final results = await Future.wait([
        _productRepository.getAll(),
        _vehicleRepository.getAll(),
        _movementRepository.getAll(),
        _invoiceRepository.getAll(),
        _supplierRepository.getAll(),
      ]);

      // Check for any failures
      final productResult = results[0] as (List<ProductEntity>?, dynamic);
      final vehicleResult = results[1] as (List<VehicleEntity>?, dynamic);
      final movementResult = results[2] as (List<MovementEntity>?, dynamic);
      final invoiceResult = results[3] as (List<InvoiceEntity>?, dynamic);
      final supplierResult = results[4] as (List<SupplierEntity>?, dynamic);

      // Check if any had a network error
      if (productResult.$2 != null ||
          vehicleResult.$2 != null ||
          movementResult.$2 != null ||
          invoiceResult.$2 != null ||
          supplierResult.$2 != null) {
        // Check if it's a connection error
        final failure = productResult.$2 ??
            vehicleResult.$2 ??
            movementResult.$2 ??
            invoiceResult.$2 ??
            supplierResult.$2;

        if (failure.message?.contains('Network') == true ||
            failure.message?.contains('ECONNREFUSED') == true ||
            failure.message?.contains('Connection') == true) {
          _isDisconnected = true;
        }

        setError(failure);
        return;
      }

      final products = productResult.$1 ?? [];
      final vehicles = vehicleResult.$1 ?? [];
      final movements = movementResult.$1 ?? [];
      final invoices = invoiceResult.$1 ?? [];
      final suppliers = supplierResult.$1 ?? [];

      _metrics = DashboardMetrics(
        productsCount: products.length,
        vehiclesCount: vehicles.length,
        movementsCount: movements.length,
        invoicesCount: invoices.length,
        suppliersCount: suppliers.length,
        products: products,
        movements: movements,
      );

      setSuccess([_metrics]);
    } catch (e) {
      // Check for network errors
      final errorMsg = e.toString();
      if (errorMsg.contains('Network') ||
          errorMsg.contains('ECONNREFUSED') ||
          errorMsg.contains('Connection') ||
          errorMsg.contains('SocketException')) {
        _isDisconnected = true;
      }
      setErrorMessage(e.toString());
    }
  }

  /// Refresh dashboard data
  Future<void> refresh() async {
    await fetchMetrics();
  }

  /// Calculate stock per product (matching React's stockMap logic)
  Map<String, double> _calculateStockMap() {
    final stockMap = <String, double>{};

    for (final movement in _metrics.movements) {
      final current = stockMap[movement.productId] ?? 0;
      final change = movement.movementType == MovementType.entrada
          ? movement.quantity
          : -movement.quantity;
      stockMap[movement.productId] = current + change;
    }

    return stockMap;
  }

  /// Get max stock value for percentage calculations
  double get maxStock {
    final stockMap = _calculateStockMap();
    if (stockMap.isEmpty) return 1;
    return stockMap.values.reduce((a, b) => a > b ? a : b);
  }

  /// Get top 7 products by stock with scale transformation
  /// (matching React's chartData.stock logic exactly)
  List<StockDataItem> get stockData {
    if (_metrics.products.isEmpty) return [];

    final stockMap = _calculateStockMap();
    final maxStockValue = maxStock;

    // Map products with their stock values
    final stockRaw = _metrics.products.map((p) {
      return {
        'id': p.id,
        'name': p.name,
        'stock': stockMap[p.id] ?? 0.0,
      };
    }).toList();

    // Sort by stock descending and take top 7
    stockRaw.sort((a, b) => (b['stock'] as double).compareTo(a['stock'] as double));
    final top7 = stockRaw.take(7).toList();

    // Transform data based on scale type (matching React logic exactly)
    return top7.map((item) {
      final originalStock = item['stock'] as double;
      double displayValue = originalStock;

      if (_scaleType == ScaleType.log) {
        // Escala logarítmica (base 10): permite ver todos los productos
        displayValue = originalStock > 0 ? log(originalStock + 1) / ln10 : 0;
      } else if (_scaleType == ScaleType.percent) {
        // Porcentaje del máximo: normaliza entre 0-100
        displayValue = maxStockValue > 0 ? (originalStock / maxStockValue) * 100 : 0;
      }
      // 'linear' mantiene el valor original

      return StockDataItem(
        id: item['id'] as String,
        name: item['name'] as String,
        stock: displayValue,
        originalStock: originalStock,
      );
    }).toList();
  }

  /// Get movement distribution for pie chart
  /// (matching React's chartData.movements logic)
  List<MovementDistributionItem> get movementDistribution {
    final entries = _metrics.movements
        .where((m) => m.movementType == MovementType.entrada)
        .length;
    final exits = _metrics.movements
        .where((m) => m.movementType == MovementType.salida)
        .length;

    return [
      MovementDistributionItem(
        id: 0,
        value: entries,
        label: 'Entradas',
        color: 0xFF009688, // Teal
      ),
      MovementDistributionItem(
        id: 1,
        value: exits,
        label: 'Salidas',
        color: 0xFFF57C00, // Orange
      ),
    ];
  }

  /// Get scale label for chart axis
  String get scaleLabel {
    switch (_scaleType) {
      case ScaleType.linear:
        return 'Stock (galones)';
      case ScaleType.log:
        return 'Stock (log10)';
      case ScaleType.percent:
        return 'Stock (%)';
    }
  }

  /// Get scale info chip text
  String get scaleInfoText {
    switch (_scaleType) {
      case ScaleType.linear:
        return 'Escala real: muestra valores absolutos (galones)';
      case ScaleType.log:
        return 'Escala logarítmica: productos pequeños se ven mejor';
      case ScaleType.percent:
        return 'Escala porcentual: normalizado al 100% del máximo';
    }
  }

  /// Format stock value based on scale type
  String formatStockValue(double value) {
    switch (_scaleType) {
      case ScaleType.linear:
        return '${value.toStringAsFixed(0)} gal';
      case ScaleType.log:
        return 'log₁₀(${value.toStringAsFixed(2)})';
      case ScaleType.percent:
        return '${value.toStringAsFixed(1)}%';
    }
  }
}
