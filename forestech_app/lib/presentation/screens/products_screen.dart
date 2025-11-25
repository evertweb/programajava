import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

import '../../core/theme/app_theme.dart';
import '../../domain/entities/product_entity.dart';
import '../providers/product_provider.dart';

/// ViewMode enum matching React's ViewMode type
enum ViewMode { table, analytics }

/// ProductsScreen - Main products management interface
/// Matches React ProductsPanel component functionality
class ProductsScreen extends StatefulWidget {
  const ProductsScreen({super.key});

  @override
  State<ProductsScreen> createState() => _ProductsScreenState();
}

class _ProductsScreenState extends State<ProductsScreen> {
  ViewMode _viewMode = ViewMode.table;
  late ProductProvider _provider;
  late ProductDataSource _dataSource;

  // Currency formatter matching React's formatCurrency
  final _currencyFormat = NumberFormat.currency(
    locale: 'es_CO',
    symbol: '\$',
    decimalDigits: 0,
  );

  String _formatCurrency(double amount) => _currencyFormat.format(amount);

  @override
  void initState() {
    super.initState();
    _provider = ProductProvider();
    _dataSource = ProductDataSource(
      products: [],
      stockLevels: {},
      getStockLevelInfo: _provider.getStockLevelInfo,
      formatCurrency: _formatCurrency,
    );

    // Load products and movements on mount (matches React useEffect)
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _provider.fetchProducts();
    });
  }

  @override
  void dispose() {
    _provider.dispose();
    super.dispose();
  }

  void _handleRefresh() {
    _provider.refresh();
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _provider,
      child: Consumer<ProductProvider>(
        builder: (context, provider, _) {
          // Update data source when products change
          _dataSource = ProductDataSource(
            products: provider.items,
            stockLevels: provider.stockLevels,
            getStockLevelInfo: provider.getStockLevelInfo,
            formatCurrency: _formatCurrency,
          );

          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Header
              _buildHeader(provider),
              const SizedBox(height: 16),
              // Content based on view mode
              Expanded(
                child: _viewMode == ViewMode.table
                    ? _buildTableView(provider)
                    : _buildAnalyticsView(provider),
              ),
            ],
          );
        },
      ),
    );
  }

  /// Build header with title and controls
  Widget _buildHeader(ProductProvider provider) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text(
          'Catálogo de Productos',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w600,
            color: AppTheme.textPrimary,
          ),
        ),
        Row(
          children: [
            // View mode toggle
            ToggleButtons(
              isSelected: [
                _viewMode == ViewMode.table,
                _viewMode == ViewMode.analytics,
              ],
              onPressed: (index) {
                setState(() {
                  _viewMode = index == 0 ? ViewMode.table : ViewMode.analytics;
                });
              },
              borderRadius: BorderRadius.circular(8),
              constraints: const BoxConstraints(
                minHeight: 36,
                minWidth: 100,
              ),
              children: const [
                Row(
                  children: [
                    Icon(Icons.table_chart, size: 18),
                    SizedBox(width: 4),
                    Text('Tabla'),
                  ],
                ),
                Row(
                  children: [
                    Icon(Icons.assessment, size: 18),
                    SizedBox(width: 4),
                    Text('Analytics'),
                  ],
                ),
              ],
            ),
            const SizedBox(width: 8),
            // Refresh button
            OutlinedButton.icon(
              onPressed: provider.isLoading ? null : _handleRefresh,
              icon: provider.isLoading
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Icon(Icons.refresh, size: 18),
              label: const Text('Actualizar'),
            ),
          ],
        ),
      ],
    );
  }

  /// Build table view with SfDataGrid
  Widget _buildTableView(ProductProvider provider) {
    if (provider.isLoading && provider.items.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    if (provider.isError) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error_outline, size: 48, color: AppTheme.error),
            const SizedBox(height: 16),
            Text(
              provider.errorMessage ?? 'Error al cargar productos',
              style: const TextStyle(color: AppTheme.error),
            ),
            const SizedBox(height: 16),
            ElevatedButton.icon(
              onPressed: _handleRefresh,
              icon: const Icon(Icons.refresh),
              label: const Text('Reintentar'),
            ),
          ],
        ),
      );
    }

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE0E0E0)),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(8),
        child: SfDataGrid(
          source: _dataSource,
          columns: _buildColumns(),
          columnWidthMode: ColumnWidthMode.fill,
          gridLinesVisibility: GridLinesVisibility.horizontal,
          headerGridLinesVisibility: GridLinesVisibility.horizontal,
          rowHeight: 52,
          headerRowHeight: 48,
          allowSorting: true,
          selectionMode: SelectionMode.none,
        ),
      ),
    );
  }

  /// Build data grid columns matching React columns definition
  List<GridColumn> _buildColumns() {
    return [
      GridColumn(
        columnName: 'id',
        width: 120,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'ID',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'name',
        minimumWidth: 200,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Nombre del Producto',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'stock',
        width: 280,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Stock Actual',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'unitPrice',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerRight,
          child: const Text(
            'Precio Unitario',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'value',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerRight,
          child: const Text(
            'Valor en Stock',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'presentation',
        width: 130,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.center,
          child: const Text(
            'Presentación',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
    ];
  }

  /// Build analytics view with stats cards and charts
  Widget _buildAnalyticsView(ProductProvider provider) {
    if (provider.isLoading && provider.items.isEmpty) {
      return const Center(child: CircularProgressIndicator());
    }

    return SingleChildScrollView(
      child: Column(
        children: [
          // Stats Cards Row
          _buildStatsCards(provider),
          const SizedBox(height: 24),
          // Charts Row
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Stock Distribution Chart (Bar)
              Expanded(
                flex: 7,
                child: _buildStockDistributionChart(provider),
              ),
              const SizedBox(width: 24),
              // Value Distribution Chart (Pie)
              Expanded(
                flex: 5,
                child: _buildValueDistributionChart(provider),
              ),
            ],
          ),
          const SizedBox(height: 24),
          // Data Grid at bottom
          SizedBox(
            height: 400,
            child: _buildTableView(provider),
          ),
        ],
      ),
    );
  }

  /// Build stats cards matching React stats cards
  Widget _buildStatsCards(ProductProvider provider) {
    final stats = provider.stats;

    return Row(
      children: [
        Expanded(
          child: _StatsCard(
            title: 'Total Productos',
            value: stats.total.toString(),
            subtitle: '${stats.active} activos',
            icon: Icons.inventory_2_outlined,
            iconColor: AppTheme.primary,
          ),
        ),
        const SizedBox(width: 24),
        Expanded(
          child: _StatsCard(
            title: 'Stock Total',
            value: NumberFormat('#,##0', 'es_CO').format(stats.totalStock),
            subtitle: 'galones',
            icon: Icons.local_gas_station_outlined,
            iconColor: const Color(0xFF2E7D32),
          ),
        ),
        const SizedBox(width: 24),
        Expanded(
          child: _StatsCard(
            title: 'Valor en Inventario',
            value: _formatCurrency(stats.totalValue),
            subtitle: 'total',
            icon: Icons.attach_money,
            iconColor: const Color(0xFF1976D2),
          ),
        ),
        const SizedBox(width: 24),
        Expanded(
          child: _StatsCard(
            title: 'Alertas de Stock',
            value: stats.lowStockCount.toString(),
            subtitle: 'productos bajos',
            icon: Icons.warning_outlined,
            iconColor: const Color(0xFFF57C00),
          ),
        ),
      ],
    );
  }

  /// Build stock distribution bar chart (Top 10)
  Widget _buildStockDistributionChart(ProductProvider provider) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE0E0E0)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Distribución de Stock (Top 10)',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'Productos con mayor cantidad en galones',
            style: TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 350,
            child: provider.stockDistribution.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos de stock disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: CategoryAxis(
                      labelStyle: const TextStyle(fontSize: 11),
                      majorGridLines: const MajorGridLines(width: 0),
                    ),
                    primaryYAxis: NumericAxis(
                      numberFormat: NumberFormat.compact(locale: 'es'),
                      majorGridLines: const MajorGridLines(
                        width: 0.5,
                        color: Color(0xFFE0E0E0),
                      ),
                    ),
                    tooltipBehavior: TooltipBehavior(
                      enable: true,
                      format: 'point.x: point.y gal',
                    ),
                    series: <CartesianSeries<StockDistributionItem, String>>[
                      BarSeries<StockDistributionItem, String>(
                        dataSource: provider.stockDistribution,
                        xValueMapper: (item, _) => item.name,
                        yValueMapper: (item, _) => item.stock,
                        color: const Color(0xFF2E7D32),
                        borderRadius: BorderRadius.circular(4),
                        dataLabelSettings: const DataLabelSettings(
                          isVisible: true,
                          labelAlignment: ChartDataLabelAlignment.outer,
                          textStyle: TextStyle(fontSize: 10),
                        ),
                      ),
                    ],
                  ),
          ),
        ],
      ),
    );
  }

  /// Build value distribution pie chart (Top 5)
  Widget _buildValueDistributionChart(ProductProvider provider) {
    return Container(
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE0E0E0)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Distribución de Valor (Top 5)',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'Productos con mayor valor en inventario',
            style: TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 350,
            child: provider.valueDistribution.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos de valor disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCircularChart(
                    legend: Legend(
                      isVisible: true,
                      position: LegendPosition.bottom,
                      overflowMode: LegendItemOverflowMode.wrap,
                      textStyle: const TextStyle(fontSize: 11),
                    ),
                    tooltipBehavior: TooltipBehavior(
                      enable: true,
                      format: 'point.x: ${_formatCurrency(0)}'.replaceAll('\$0', 'point.y'),
                    ),
                    series: <CircularSeries<ValueDistributionItem, String>>[
                      DoughnutSeries<ValueDistributionItem, String>(
                        dataSource: provider.valueDistribution,
                        xValueMapper: (item, _) => item.label,
                        yValueMapper: (item, _) => item.value,
                        innerRadius: '60%',
                        cornerStyle: CornerStyle.bothCurve,
                        dataLabelSettings: DataLabelSettings(
                          isVisible: true,
                          labelPosition: ChartDataLabelPosition.outside,
                          textStyle: const TextStyle(fontSize: 10),
                          builder: (data, point, series, pointIndex, seriesIndex) {
                            final item = data as ValueDistributionItem;
                            return Text(
                              _formatCurrency(item.value),
                              style: const TextStyle(fontSize: 10),
                            );
                          },
                        ),
                      ),
                    ],
                  ),
          ),
        ],
      ),
    );
  }
}

/// Stats Card Widget matching React's stats cards
class _StatsCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final IconData icon;
  final Color iconColor;

  const _StatsCard({
    required this.title,
    required this.value,
    required this.subtitle,
    required this.icon,
    required this.iconColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.05),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 13,
                    color: AppTheme.textSecondary,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  value,
                  style: TextStyle(
                    fontSize: 28,
                    fontWeight: FontWeight.w600,
                    color: iconColor,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  subtitle,
                  style: const TextStyle(
                    fontSize: 12,
                    color: AppTheme.textSecondary,
                  ),
                ),
              ],
            ),
          ),
          Icon(
            icon,
            size: 48,
            color: iconColor.withValues(alpha: 0.3),
          ),
        ],
      ),
    );
  }
}

/// DataSource for SfDataGrid
/// Handles data mapping and cell rendering for products
class ProductDataSource extends DataGridSource {
  final List<ProductEntity> products;
  final Map<String, double> stockLevels;
  final StockLevel Function(double) getStockLevelInfo;
  final String Function(double) formatCurrency;

  List<DataGridRow> _dataGridRows = [];

  ProductDataSource({
    required this.products,
    required this.stockLevels,
    required this.getStockLevelInfo,
    required this.formatCurrency,
  }) {
    _buildDataGridRows();
  }

  void _buildDataGridRows() {
    _dataGridRows = products.map<DataGridRow>((product) {
      final stock = stockLevels[product.id] ?? 0;
      final value = stock * product.unitPrice;

      return DataGridRow(cells: [
        DataGridCell<String>(columnName: 'id', value: product.id),
        DataGridCell<String>(columnName: 'name', value: product.name),
        DataGridCell<double>(columnName: 'stock', value: stock),
        DataGridCell<double>(columnName: 'unitPrice', value: product.unitPrice),
        DataGridCell<double>(columnName: 'value', value: value),
        DataGridCell<String>(
          columnName: 'presentation',
          value: product.presentation ?? product.measurementUnit.value,
        ),
      ]);
    }).toList();
  }

  @override
  List<DataGridRow> get rows => _dataGridRows;

  @override
  DataGridRowAdapter buildRow(DataGridRow row) {
    final stock = row.getCells()[2].value as double;
    final stockLevel = getStockLevelInfo(stock);

    return DataGridRowAdapter(
      cells: row.getCells().map<Widget>((cell) {
        if (cell.columnName == 'stock') {
          return _buildStockCell(stock, stockLevel);
        }
        if (cell.columnName == 'unitPrice' || cell.columnName == 'value') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerRight,
            child: Text(
              formatCurrency(cell.value as double),
              style: cell.columnName == 'value'
                  ? const TextStyle(fontWeight: FontWeight.w600)
                  : null,
            ),
          );
        }
        if (cell.columnName == 'presentation') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.center,
            child: Text(cell.value?.toString() ?? 'N/A'),
          );
        }
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: Text(cell.value?.toString() ?? ''),
        );
      }).toList(),
    );
  }

  /// Build stock cell with progress indicator (matches React's renderCell for stock)
  Widget _buildStockCell(double stock, StockLevel level) {
    final stockColor = Color(level.colorValue);

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      alignment: Alignment.centerLeft,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                '${NumberFormat('#,##0', 'es_CO').format(stock)} gal',
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
              Text(
                level.label,
                style: TextStyle(
                  fontSize: 11,
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          ClipRRect(
            borderRadius: BorderRadius.circular(3),
            child: LinearProgressIndicator(
              value: level.percentage / 100,
              backgroundColor: stockColor.withValues(alpha: 0.12),
              valueColor: AlwaysStoppedAnimation<Color>(stockColor),
              minHeight: 6,
            ),
          ),
        ],
      ),
    );
  }
}
