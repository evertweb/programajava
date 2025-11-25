import '../../data/models/movement_model.dart';
import '../../data/repositories/movement_repository_impl.dart';
import '../../data/repositories/product_repository_impl.dart';
import '../../domain/entities/movement_entity.dart';
import '../../domain/entities/product_entity.dart';
import '../../domain/repositories/i_movement_repository.dart';
import '../../domain/repositories/i_product_repository.dart';
import 'base_provider.dart';

/// Stock level info matching React's getStockLevel function
class StockLevel {
  final String label;
  final int colorValue;
  final double percentage;

  const StockLevel({
    required this.label,
    required this.colorValue,
    required this.percentage,
  });
}

/// Product statistics matching React's stats calculation
class ProductStats {
  final int total;
  final int active;
  final double totalStock;
  final double totalValue;
  final int lowStockCount;

  const ProductStats({
    required this.total,
    required this.active,
    required this.totalStock,
    required this.totalValue,
    required this.lowStockCount,
  });

  factory ProductStats.empty() => const ProductStats(
        total: 0,
        active: 0,
        totalStock: 0,
        totalValue: 0,
        lowStockCount: 0,
      );
}

/// Stock distribution item for charts
class StockDistributionItem {
  final String name;
  final double stock;
  final double value;

  const StockDistributionItem({
    required this.name,
    required this.stock,
    required this.value,
  });
}

/// Value distribution item for pie chart
class ValueDistributionItem {
  final String id;
  final String label;
  final double value;

  const ValueDistributionItem({
    required this.id,
    required this.label,
    required this.value,
  });
}

/// Provider for managing Product state
/// Matches React ProductsPanel logic including stock calculations
class ProductProvider extends BaseProvider<ProductEntity> {
  final IProductRepository _repository;
  final IMovementRepository _movementRepository;

  List<MovementEntity> _movements = [];
  Map<String, double> _stockLevels = {};
  ProductStats _stats = ProductStats.empty();
  List<StockDistributionItem> _stockDistribution = [];
  List<ValueDistributionItem> _valueDistribution = [];

  // Getters
  List<MovementEntity> get movements => _movements;
  Map<String, double> get stockLevels => _stockLevels;
  ProductStats get stats => _stats;
  List<StockDistributionItem> get stockDistribution => _stockDistribution;
  List<ValueDistributionItem> get valueDistribution => _valueDistribution;

  ProductProvider({
    IProductRepository? repository,
    IMovementRepository? movementRepository,
  })  : _repository = repository ?? ProductRepositoryImpl(),
        _movementRepository = movementRepository ?? MovementRepositoryImpl();

  /// Get stock for a specific product
  double getStock(String productId) => _stockLevels[productId] ?? 0;

  /// Get stock level info matching React's getStockLevel function
  StockLevel getStockLevelInfo(double stock) {
    const double maxStock = 10000; // Maximum expected stock
    final double percentage = (stock / maxStock * 100).clamp(0, 100);

    if (stock <= 0) {
      return StockLevel(
        label: 'Sin stock',
        colorValue: 0xFFD32F2F, // Red
        percentage: 0,
      );
    } else if (stock < 500) {
      return StockLevel(
        label: 'Stock bajo',
        colorValue: 0xFFF57C00, // Orange
        percentage: percentage,
      );
    } else if (stock < 2000) {
      return StockLevel(
        label: 'Stock medio',
        colorValue: 0xFFFBC02D, // Yellow
        percentage: percentage,
      );
    } else {
      return StockLevel(
        label: 'Stock óptimo',
        colorValue: 0xFF2E7D32, // Green
        percentage: percentage,
      );
    }
  }

  /// Calculate stock levels from movements (matches React stockLevels useMemo)
  void _calculateStockLevels() {
    final Map<String, double> stockMap = {};

    for (final movement in _movements) {
      final current = stockMap[movement.productId] ?? 0;
      final change = movement.movementType == MovementType.entrada
          ? movement.quantity
          : -movement.quantity;
      stockMap[movement.productId] = current + change;
    }

    _stockLevels = stockMap;
  }

  /// Calculate stats (matches React stats useMemo)
  void _calculateStats() {
    final total = items.length;
    final active = items.where((p) => p.isActive).length;

    double totalStock = 0;
    double totalValue = 0;
    int lowStockCount = 0;

    for (final product in items) {
      final stock = _stockLevels[product.id] ?? 0;
      totalStock += stock;
      totalValue += stock * product.unitPrice;

      if (stock < 500 && stock > 0) {
        lowStockCount++;
      }
    }

    _stats = ProductStats(
      total: total,
      active: active,
      totalStock: totalStock,
      totalValue: totalValue,
      lowStockCount: lowStockCount,
    );
  }

  /// Calculate stock distribution for bar chart (matches React stockDistribution useMemo)
  void _calculateStockDistribution() {
    final distribution = items
        .map((p) => StockDistributionItem(
              name: p.name,
              stock: _stockLevels[p.id] ?? 0,
              value: (_stockLevels[p.id] ?? 0) * p.unitPrice,
            ))
        .where((p) => p.stock > 0)
        .toList()
      ..sort((a, b) => b.stock.compareTo(a.stock));

    _stockDistribution = distribution.take(10).toList(); // Top 10 by stock
  }

  /// Calculate value distribution for pie chart (matches React valueDistribution useMemo)
  void _calculateValueDistribution() {
    final distribution = items
        .map((p) => ValueDistributionItem(
              id: p.id,
              label: p.name,
              value: (_stockLevels[p.id] ?? 0) * p.unitPrice,
            ))
        .where((p) => p.value > 0)
        .toList()
      ..sort((a, b) => b.value.compareTo(a.value));

    _valueDistribution = distribution.take(5).toList(); // Top 5 by value
  }

  /// Recalculate all derived data
  void _recalculateAll() {
    _calculateStockLevels();
    _calculateStats();
    _calculateStockDistribution();
    _calculateValueDistribution();
  }

  /// Fetches all products and movements from the repository
  Future<void> fetchProducts() async {
    setLoading();

    // Fetch products and movements in parallel (like React's useEffect)
    final results = await Future.wait([
      _repository.getAll(),
      _movementRepository.getAll(),
    ]);

    final (products, productFailure) =
        results[0] as (List<ProductEntity>?, dynamic);
    final (movements, _) = results[1] as (List<MovementEntity>?, dynamic);

    if (productFailure != null) {
      setError(productFailure);
      return;
    }

    _movements = movements ?? [];
    setSuccess(products ?? []);
    _recalculateAll();
  }

  /// Refresh products and movements (matches React handleRefresh)
  Future<void> refresh() async {
    await fetchProducts();
  }

  /// Gets a product by ID
  Future<void> getProductById(String id) async {
    setLoading();

    final (product, failure) = await _repository.getById(id);

    if (failure != null) {
      setError(failure);
      return;
    }

    if (product != null) {
      setSelectedItem(product);
    }
  }

  /// Creates a new product
  Future<bool> createProduct(ProductEntity product) async {
    setLoading();

    final (createdProduct, failure) = await _repository.create(product);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (createdProduct != null) {
      addItem(createdProduct);
    }

    return true;
  }

  /// Updates an existing product
  Future<bool> updateProduct(String id, ProductEntity product) async {
    setLoading();

    final (updatedProduct, failure) = await _repository.update(id, product);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (updatedProduct != null) {
      updateItem(updatedProduct, (p) => p.id == id);
    }

    return true;
  }

  /// Deletes a product
  Future<bool> deleteProduct(String id) async {
    setLoading();

    final (success, failure) = await _repository.delete(id);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (success) {
      removeItem((p) => p.id == id);
    }

    return success;
  }

  /// Searches products by query
  Future<void> searchProducts(String query) async {
    if (query.isEmpty) {
      await fetchProducts();
      return;
    }

    setLoading();

    final (products, failure) = await _repository.search(query);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(products ?? []);
  }
}
