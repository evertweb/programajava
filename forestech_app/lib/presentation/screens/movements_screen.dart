import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

import '../../core/theme/app_theme.dart';
import '../../data/models/movement_model.dart';
import '../../domain/entities/movement_entity.dart';
import '../../domain/entities/product_entity.dart';
import '../../domain/repositories/i_movement_repository.dart';
import '../providers/movement_provider.dart';
import '../widgets/movement_form_dialog.dart';

/// ViewMode enum matching React's ViewMode type
enum ViewMode { table, analytics }

/// MovementsScreen - Main inventory movements management interface
/// Matches React MovementsPanel component functionality 1:1
class MovementsScreen extends StatefulWidget {
  const MovementsScreen({super.key});

  @override
  State<MovementsScreen> createState() => _MovementsScreenState();
}

class _MovementsScreenState extends State<MovementsScreen> {
  ViewMode _viewMode = ViewMode.table;
  late MovementProvider _provider;
  late MovementDataSource _dataSource;

  // Currency formatter matching React's formatCurrency
  final _currencyFormat = NumberFormat.currency(
    locale: 'es_CO',
    symbol: '\$',
    decimalDigits: 0,
  );

  String _formatCurrency(double amount) => _currencyFormat.format(amount);

  String _formatDate(DateTime date) {
    return DateFormat('dd/MM/yyyy HH:mm', 'es_CO').format(date);
  }

  @override
  void initState() {
    super.initState();
    _provider = MovementProvider();
    _dataSource = MovementDataSource(
      movements: [],
      productsMap: {},
      vehiclesMap: {},
      formatCurrency: _formatCurrency,
      formatDate: _formatDate,
      onDelete: _handleDeleteClick,
    );

    // Load all data on mount (matches React useEffect)
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _provider.loadAllData();
    });
  }

  @override
  void dispose() {
    _provider.dispose();
    super.dispose();
  }

  void _handleRefresh() {
    _provider.loadAllData();
  }

  /// Handle registrar salida button (matches React's handleOpenDialog('SALIDA'))
  /// NOTE: Entradas are only created via invoice registration as per business logic
  void _handleOpenSalidaDialog() {
    showDialog(
      context: context,
      builder: (context) => MovementFormDialog(
        provider: _provider,
        mode: MovementType.salida,
      ),
    );
  }

  /// Handle delete click (matches React's handleDeleteClick)
  void _handleDeleteClick(MovementEntity movement) {
    showDialog(
      context: context,
      builder: (context) => _DeleteConfirmDialog(
        movement: movement,
        provider: _provider,
        productsMap: _provider.productsMap,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _provider,
      child: Consumer<MovementProvider>(
        builder: (context, provider, _) {
          // Update data source when movements change
          _dataSource = MovementDataSource(
            movements: provider.items,
            productsMap: provider.productsMap,
            vehiclesMap: provider.vehiclesMap,
            formatCurrency: _formatCurrency,
            formatDate: _formatDate,
            onDelete: _handleDeleteClick,
          );

          // En modo Analytics, todo el contenido es scrollable
          // En modo Table, solo se muestra header + tabla expandida
          if (_viewMode == ViewMode.analytics) {
            return CustomScrollView(
              slivers: [
                SliverToBoxAdapter(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      // Header
                      _buildHeader(provider),
                      const SizedBox(height: 16),
                      // Alerts Section
                      if (provider.alerts.isNotEmpty)
                        _buildAlertsSection(provider),
                      // Stats Cards
                      _buildStatsCards(provider),
                      // Charts Row
                      _buildChartsRow(provider),
                      // Table with fixed height in analytics mode
                      SizedBox(
                        height: 400,
                        child: _buildTableView(provider),
                      ),
                      const SizedBox(height: 24),
                    ],
                  ),
                ),
              ],
            );
          }
          
          // Modo Table: layout simple con tabla expandida
          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Header
              _buildHeader(provider),
              const SizedBox(height: 16),
              // Content - Table takes all remaining space
              Expanded(
                child: _buildTableView(provider),
              ),
            ],
          );
        },
      ),
    );
  }

  /// Build header with title and controls (matches React header)
  Widget _buildHeader(MovementProvider provider) {
    return Wrap(
      alignment: WrapAlignment.spaceBetween,
      crossAxisAlignment: WrapCrossAlignment.center,
      spacing: 16,
      runSpacing: 12,
      children: [
        const Text(
          'Movimientos de Inventario',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w600,
            color: AppTheme.textPrimary,
          ),
        ),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          crossAxisAlignment: WrapCrossAlignment.center,
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
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(Icons.table_chart, size: 18),
                    SizedBox(width: 4),
                    Text('Tabla'),
                  ],
                ),
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(Icons.assessment, size: 18),
                    SizedBox(width: 4),
                    Text('Análisis'),
                  ],
                ),
              ],
            ),
            // Type filter toggle
            ToggleButtons(
              isSelected: [
                provider.typeFilter == MovementTypeFilter.all,
                provider.typeFilter == MovementTypeFilter.entrada,
                provider.typeFilter == MovementTypeFilter.salida,
              ],
              onPressed: (index) {
                final filter = [
                  MovementTypeFilter.all,
                  MovementTypeFilter.entrada,
                  MovementTypeFilter.salida,
                ][index];
                provider.setTypeFilter(filter);
              },
              borderRadius: BorderRadius.circular(8),
              constraints: const BoxConstraints(
                minHeight: 36,
                minWidth: 80,
              ),
              children: const [
                Text('Todos'),
                Text('Entradas'),
                Text('Salidas'),
              ],
            ),
            // NOTE: Registrar Entrada button removed as per business logic
            // Entries are only created via invoice registration
            ElevatedButton.icon(
              onPressed: _handleOpenSalidaDialog,
              icon: const Icon(Icons.remove, size: 18),
              label: const Text('Registrar Salida'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.warning,
                foregroundColor: Colors.white,
              ),
            ),
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

  /// Build alerts section (matches React's alerts Accordion)
  Widget _buildAlertsSection(MovementProvider provider) {
    final alerts = provider.alerts;
    final hasErrors = alerts.any((a) => a.severity == AlertSeverity.error);
    final hasWarnings = alerts.any((a) => a.severity == AlertSeverity.warning);

    Color bgColor = const Color(0xFFE3F2FD); // info
    if (hasErrors) {
      bgColor = const Color(0xFFFFEBEE); // error
    } else if (hasWarnings) {
      bgColor = const Color(0xFFFFF3E0); // warning
    }

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.1),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: ExpansionTile(
        leading: Badge(
          label: Text(alerts.length.toString()),
          child: const Icon(Icons.notifications),
        ),
        title: const Text(
          'Alertas del Sistema',
          style: TextStyle(fontWeight: FontWeight.w600),
        ),
        subtitle: Text(
          '${alerts.where((a) => a.severity == AlertSeverity.error).length} críticas, '
          '${alerts.where((a) => a.severity == AlertSeverity.warning).length} advertencias, '
          '${alerts.where((a) => a.severity == AlertSeverity.info).length} informativas',
          style: TextStyle(fontSize: 12, color: AppTheme.textSecondary),
        ),
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: alerts.map((alert) {
                Color alertColor;
                IconData alertIcon;
                switch (alert.severity) {
                  case AlertSeverity.error:
                    alertColor = AppTheme.error;
                    alertIcon = Icons.error;
                    break;
                  case AlertSeverity.warning:
                    alertColor = AppTheme.warning;
                    alertIcon = Icons.warning;
                    break;
                  case AlertSeverity.info:
                    alertColor = AppTheme.info;
                    alertIcon = Icons.info;
                    break;
                }

                return Container(
                  margin: const EdgeInsets.only(bottom: 8),
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: alertColor.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(4),
                    border: Border.all(color: alertColor.withValues(alpha: 0.3)),
                  ),
                  child: Row(
                    children: [
                      Icon(alertIcon, color: alertColor, size: 20),
                      const SizedBox(width: 12),
                      Expanded(child: Text(alert.message)),
                    ],
                  ),
                );
              }).toList(),
            ),
          ),
        ],
      ),
    );
  }

  /// Build stats cards matching React's stats cards
  Widget _buildStatsCards(MovementProvider provider) {
    final stats = provider.stats;

    return Padding(
      padding: const EdgeInsets.only(bottom: 24),
      child: Row(
        children: [
          // Entradas Este Mes
          Expanded(
            child: _StatsCard(
              title: 'Entradas Este Mes',
              value: NumberFormat('#,##0', 'es_CO').format(stats.monthEntries),
              subtitle: '${stats.monthEntriesCount} movimientos',
              icon: Icons.move_to_inbox,
              iconColor: const Color(0xFF2E7D32),
              bgColor: const Color(0xFFE8F5E9),
              change: stats.entriesChange,
              changePositiveIsGood: true,
            ),
          ),
          const SizedBox(width: 24),
          // Salidas Este Mes
          Expanded(
            child: _StatsCard(
              title: 'Salidas Este Mes',
              value: NumberFormat('#,##0', 'es_CO').format(stats.monthExits),
              subtitle: '${stats.monthExitsCount} movimientos',
              icon: Icons.outbox,
              iconColor: const Color(0xFFF57C00),
              bgColor: const Color(0xFFFFF3E0),
              change: stats.exitsChange,
              changePositiveIsGood: false,
            ),
          ),
          const SizedBox(width: 24),
          // Balance Mensual
          Expanded(
            child: _StatsCard(
              title: 'Balance Mensual',
              value:
                  '${stats.balance > 0 ? '+' : ''}${NumberFormat('#,##0', 'es_CO').format(stats.balance)}',
              subtitle: '${stats.monthEntriesCount} E / ${stats.monthExitsCount} S',
              icon: stats.balance >= 0 ? Icons.trending_up : Icons.trending_down,
              iconColor:
                  stats.balance >= 0 ? const Color(0xFF1976D2) : AppTheme.error,
              bgColor: const Color(0xFFE3F2FD),
            ),
          ),
          const SizedBox(width: 24),
          // Stock Total Actual
          Expanded(
            child: _StatsCard(
              title: 'Stock Total Actual',
              value: NumberFormat('#,##0', 'es_CO').format(stats.totalStock),
              subtitle: 'galones',
              icon: Icons.inventory,
              iconColor: const Color(0xFF7B1FA2),
              bgColor: const Color(0xFFF3E5F5),
            ),
          ),
        ],
      ),
    );
  }

  /// Build charts row (Timeline + Product Movements)
  Widget _buildChartsRow(MovementProvider provider) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 24),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Timeline Chart (8/12 width)
          Expanded(
            flex: 8,
            child: _buildTimelineChart(provider),
          ),
          const SizedBox(width: 24),
          // Product Movements Chart (4/12 width)
          Expanded(
            flex: 4,
            child: _buildProductMovementsChart(provider),
          ),
        ],
      ),
    );
  }

  /// Build timeline chart (matches React's LineChart)
  Widget _buildTimelineChart(MovementProvider provider) {
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
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'Tendencia de Movimientos',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                  color: AppTheme.textPrimary,
                ),
              ),
              ToggleButtons(
                isSelected: [
                  provider.dateRange == DateRange.days7,
                  provider.dateRange == DateRange.days15,
                  provider.dateRange == DateRange.days30,
                  provider.dateRange == DateRange.days90,
                ],
                onPressed: (index) {
                  final range = [
                    DateRange.days7,
                    DateRange.days15,
                    DateRange.days30,
                    DateRange.days90,
                  ][index];
                  provider.setDateRange(range);
                },
                borderRadius: BorderRadius.circular(8),
                constraints: const BoxConstraints(
                  minHeight: 32,
                  minWidth: 60,
                ),
                textStyle: const TextStyle(fontSize: 12),
                children: const [
                  Text('7 días'),
                  Text('15 días'),
                  Text('30 días'),
                  Text('90 días'),
                ],
              ),
            ],
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 350,
            child: provider.timelineData.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos de movimientos disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: CategoryAxis(
                      labelStyle: const TextStyle(fontSize: 10),
                      majorGridLines: const MajorGridLines(width: 0),
                      labelRotation: -45,
                    ),
                    primaryYAxis: NumericAxis(
                      numberFormat: NumberFormat.compact(locale: 'es'),
                      majorGridLines: const MajorGridLines(
                        width: 0.5,
                        color: Color(0xFFE0E0E0),
                      ),
                    ),
                    tooltipBehavior: TooltipBehavior(enable: true),
                    legend: Legend(
                      isVisible: true,
                      position: LegendPosition.bottom,
                    ),
                    series: <CartesianSeries<TimelineDataItem, String>>[
                      LineSeries<TimelineDataItem, String>(
                        name: 'Entradas',
                        dataSource: provider.timelineData,
                        xValueMapper: (item, _) => item.date,
                        yValueMapper: (item, _) => item.entries,
                        color: const Color(0xFF2E7D32),
                        markerSettings: MarkerSettings(
                          isVisible: provider.dateRange.value <= 30,
                          height: 6,
                          width: 6,
                        ),
                      ),
                      LineSeries<TimelineDataItem, String>(
                        name: 'Salidas',
                        dataSource: provider.timelineData,
                        xValueMapper: (item, _) => item.date,
                        yValueMapper: (item, _) => item.exits,
                        color: const Color(0xFFF57C00),
                        markerSettings: MarkerSettings(
                          isVisible: provider.dateRange.value <= 30,
                          height: 6,
                          width: 6,
                        ),
                      ),
                    ],
                  ),
          ),
        ],
      ),
    );
  }

  /// Build product movements chart (matches React's BarChart)
  Widget _buildProductMovementsChart(MovementProvider provider) {
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
            'Top 5 Productos',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'Por volumen total movido',
            style: TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 350,
            child: provider.productMovements.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos de productos disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: NumericAxis(
                      numberFormat: NumberFormat.compact(locale: 'es'),
                      majorGridLines: const MajorGridLines(
                        width: 0.5,
                        color: Color(0xFFE0E0E0),
                      ),
                    ),
                    primaryYAxis: CategoryAxis(
                      labelStyle: const TextStyle(fontSize: 10),
                      majorGridLines: const MajorGridLines(width: 0),
                    ),
                    tooltipBehavior: TooltipBehavior(enable: true),
                    legend: Legend(
                      isVisible: true,
                      position: LegendPosition.bottom,
                    ),
                    series: <CartesianSeries<ProductMovementItem, String>>[
                      StackedBarSeries<ProductMovementItem, String>(
                        name: 'Entradas',
                        dataSource: provider.productMovements,
                        xValueMapper: (item, _) => item.name,
                        yValueMapper: (item, _) => item.entries,
                        color: const Color(0xFF2E7D32),
                        borderRadius: BorderRadius.circular(2),
                      ),
                      StackedBarSeries<ProductMovementItem, String>(
                        name: 'Salidas',
                        dataSource: provider.productMovements,
                        xValueMapper: (item, _) => item.name,
                        yValueMapper: (item, _) => item.exits,
                        color: const Color(0xFFF57C00),
                        borderRadius: BorderRadius.circular(2),
                      ),
                    ],
                  ),
          ),
        ],
      ),
    );
  }

  /// Build table view with SfDataGrid
  Widget _buildTableView(MovementProvider provider) {
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
              provider.errorMessage ?? 'Error al cargar movimientos',
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
          sortingGestureType: SortingGestureType.tap,
          selectionMode: SelectionMode.none,
        ),
      ),
    );
  }

  /// Build data grid columns matching React columns definition
  List<GridColumn> _buildColumns() {
    return [
      GridColumn(
        columnName: 'createdAt',
        width: 180,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Fecha',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'movementType',
        width: 120,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Tipo',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'product',
        minimumWidth: 200,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Producto',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'vehicle',
        width: 180,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Vehículo',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'quantity',
        width: 140,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerRight,
          child: const Text(
            'Cantidad',
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
            'Precio Unit.',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'total',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerRight,
          child: const Text(
            'Total',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'realUnitPrice',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerRight,
          child: const Text(
            'Precio Real',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'realCost',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerRight,
          child: const Text(
            'Costo Real',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'description',
        minimumWidth: 180,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Descripción',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'actions',
        width: 100,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.center,
          child: const Text(
            'Acciones',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
    ];
  }
}

/// Stats Card Widget matching React's stats cards
class _StatsCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final IconData icon;
  final Color iconColor;
  final Color bgColor;
  final double? change;
  final bool changePositiveIsGood;

  const _StatsCard({
    required this.title,
    required this.value,
    required this.subtitle,
    required this.icon,
    required this.iconColor,
    required this.bgColor,
    this.change,
    this.changePositiveIsGood = true,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(8),
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
                Row(
                  children: [
                    Text(
                      subtitle,
                      style: const TextStyle(
                        fontSize: 12,
                        color: AppTheme.textSecondary,
                      ),
                    ),
                    if (change != null && change != 0) ...[
                      const SizedBox(width: 8),
                      _buildChangeChip(),
                    ],
                  ],
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

  Widget _buildChangeChip() {
    final isPositive = change! > 0;
    final isGood = changePositiveIsGood ? isPositive : !isPositive;
    final color = isGood ? AppTheme.success : AppTheme.error;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(10),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            isPositive ? Icons.trending_up : Icons.trending_down,
            size: 12,
            color: color,
          ),
          const SizedBox(width: 2),
          Text(
            '${isPositive ? '+' : ''}${change!.toStringAsFixed(1)}%',
            style: TextStyle(
              fontSize: 10,
              fontWeight: FontWeight.w600,
              color: color,
            ),
          ),
        ],
      ),
    );
  }
}

/// Delete Confirmation Dialog (matches React's delete dialog)
class _DeleteConfirmDialog extends StatefulWidget {
  final MovementEntity movement;
  final MovementProvider provider;
  final Map<String, ProductEntity> productsMap;

  const _DeleteConfirmDialog({
    required this.movement,
    required this.provider,
    required this.productsMap,
  });

  @override
  State<_DeleteConfirmDialog> createState() => _DeleteConfirmDialogState();
}

class _DeleteConfirmDialogState extends State<_DeleteConfirmDialog> {
  bool _isDeleting = false;

  Future<void> _handleDelete() async {
    setState(() => _isDeleting = true);

    final success = await widget.provider.deleteMovement(widget.movement.id);

    if (mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            success
                ? 'Movimiento eliminado correctamente. El stock ha sido restaurado.'
                : widget.provider.errorMessage ?? 'Error al eliminar el movimiento',
          ),
          backgroundColor: success ? AppTheme.success : AppTheme.error,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final product = widget.productsMap[widget.movement.productId];

    return AlertDialog(
      title: const Text('Confirmar Eliminación'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '¿Está seguro de que desea eliminar este movimiento de salida?',
          ),
          const SizedBox(height: 16),
          const Text(
            'El stock será restaurado automáticamente.',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 16),
          Text('Producto: ${product?.name ?? 'Desconocido'}'),
          Text(
            'Cantidad a restaurar: ${widget.movement.quantity.toStringAsFixed(0)} ${product?.measurementUnit.value ?? ''}',
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: _isDeleting ? null : () => Navigator.pop(context),
          child: const Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: _isDeleting ? null : _handleDelete,
          style: ElevatedButton.styleFrom(
            backgroundColor: AppTheme.error,
            foregroundColor: Colors.white,
          ),
          child: _isDeleting
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(
                    strokeWidth: 2,
                    color: Colors.white,
                  ),
                )
              : const Text('Eliminar'),
        ),
      ],
    );
  }
}

/// DataSource for SfDataGrid
/// Handles data mapping and cell rendering for movements
class MovementDataSource extends DataGridSource {
  final List<MovementEntity> movements;
  final Map<String, ProductEntity> productsMap;
  final Map<String, dynamic> vehiclesMap;
  final String Function(double) formatCurrency;
  final String Function(DateTime) formatDate;
  final void Function(MovementEntity) onDelete;

  List<DataGridRow> _dataGridRows = [];

  MovementDataSource({
    required this.movements,
    required this.productsMap,
    required this.vehiclesMap,
    required this.formatCurrency,
    required this.formatDate,
    required this.onDelete,
  }) {
    _buildDataGridRows();
  }

  void _buildDataGridRows() {
    _dataGridRows = movements.map<DataGridRow>((movement) {
      final product = productsMap[movement.productId];
      final vehicle = vehiclesMap[movement.vehicleId];
      final total = movement.quantity * movement.unitPrice;

      return DataGridRow(cells: [
        DataGridCell<DateTime>(columnName: 'createdAt', value: movement.createdAt),
        DataGridCell<MovementType>(
          columnName: 'movementType',
          value: movement.movementType,
        ),
        DataGridCell<String>(
          columnName: 'product',
          value: product?.name ?? 'Producto desconocido',
        ),
        DataGridCell<String>(
          columnName: 'vehicle',
          value: vehicle != null ? '${vehicle.placa} - ${vehicle.marca}' : '-',
        ),
        DataGridCell<String>(
          columnName: 'quantity',
          value: '${movement.quantity.toStringAsFixed(0)} ${product?.measurementUnit.value ?? ''}',
        ),
        DataGridCell<double>(columnName: 'unitPrice', value: movement.unitPrice),
        DataGridCell<double>(columnName: 'total', value: total),
        DataGridCell<double?>(
          columnName: 'realUnitPrice',
          value: movement.realUnitPrice,
        ),
        DataGridCell<double?>(columnName: 'realCost', value: movement.realCost),
        DataGridCell<String>(
          columnName: 'description',
          value: movement.description,
        ),
        DataGridCell<MovementEntity>(columnName: 'actions', value: movement),
      ]);
    }).toList();
  }

  @override
  List<DataGridRow> get rows => _dataGridRows;

  @override
  DataGridRowAdapter buildRow(DataGridRow row) {
    return DataGridRowAdapter(
      cells: row.getCells().map<Widget>((cell) {
        if (cell.columnName == 'createdAt') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerLeft,
            child: Text(formatDate(cell.value as DateTime)),
          );
        }
        if (cell.columnName == 'movementType') {
          final type = cell.value as MovementType;
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerLeft,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              decoration: BoxDecoration(
                color: type == MovementType.entrada
                    ? AppTheme.success
                    : AppTheme.warning,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                type.value,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 12,
                ),
              ),
            ),
          );
        }
        if (cell.columnName == 'unitPrice' || cell.columnName == 'total') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerRight,
            child: Text(
              formatCurrency(cell.value as double),
              style: cell.columnName == 'total'
                  ? const TextStyle(fontWeight: FontWeight.w600)
                  : null,
            ),
          );
        }
        if (cell.columnName == 'realUnitPrice' ||
            cell.columnName == 'realCost') {
          final value = cell.value as double?;
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerRight,
            child: Text(value != null ? formatCurrency(value) : '-'),
          );
        }
        if (cell.columnName == 'quantity') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerRight,
            child: Text(cell.value?.toString() ?? ''),
          );
        }
        if (cell.columnName == 'actions') {
          final movement = cell.value as MovementEntity;
          // Only show delete for SALIDA movements (matches React logic)
          if (movement.movementType != MovementType.salida) {
            return const SizedBox.shrink();
          }
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.center,
            child: Tooltip(
              message: 'Eliminar salida y restaurar stock',
              child: IconButton(
                icon: const Icon(Icons.delete, color: AppTheme.error),
                onPressed: () => onDelete(movement),
                iconSize: 20,
              ),
            ),
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
}
