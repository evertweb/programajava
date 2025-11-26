import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

import '../../core/theme/app_theme.dart';
import '../../core/extensions/number_extensions.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../mixins/confirmation_dialog_mixin.dart';
import '../mixins/snackbar_mixin.dart';
import '../providers/vehicle_provider.dart';
import '../widgets/common/data_grid_wrapper.dart';
import '../widgets/common/error_state_widget.dart';
import '../widgets/common/loading_state_widget.dart';
import '../widgets/common/stats_card.dart';
import '../widgets/vehicle_form_dialog.dart';

/// ViewMode enum matching React's ViewMode type
enum ViewMode { table, analytics }

/// VehiclesScreen - Main vehicles management interface
/// Matches React VehiclesPanel component functionality
class VehiclesScreen extends StatefulWidget {
  const VehiclesScreen({super.key});

  @override
  State<VehiclesScreen> createState() => _VehiclesScreenState();
}

class _VehiclesScreenState extends State<VehiclesScreen>
    with ConfirmationDialogMixin, SnackBarMixin {
  ViewMode _viewMode = ViewMode.table;
  late VehicleProvider _provider;
  late VehicleDataSource _dataSource;

  @override
  void initState() {
    super.initState();
    _provider = VehicleProvider();
    _dataSource = VehicleDataSource(
      vehicles: [],
      onEdit: _handleEdit,
      onDelete: _handleDelete,
    );

    // Load vehicles on mount (matches React useEffect)
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _provider.fetchVehicles();
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

  void _handleCreate() {
    showDialog(
      context: context,
      builder: (context) => VehicleFormDialog(provider: _provider),
    );
  }

  void _handleEdit(VehicleEntity vehicle) {
    showDialog(
      context: context,
      builder: (context) => VehicleFormDialog(
        provider: _provider,
        vehicle: vehicle,
      ),
    );
  }

  Future<void> _handleDelete(String id) async {
    // Using ConfirmationDialogMixin
    final confirmed = await showDeleteConfirmation(
      itemName: 'este vehículo',
      customMessage: '¿Está seguro que desea eliminar este vehículo? Esta acción no se puede deshacer.',
    );

    if (confirmed) {
      final success = await _provider.deleteVehicle(id);
      
      // Using SnackBarMixin
      if (success) {
        showSuccessMessage('Vehículo eliminado exitosamente');
      } else {
        showErrorMessage(_provider.errorMessage ?? 'Error al eliminar vehículo');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _provider,
      child: Consumer<VehicleProvider>(
        builder: (context, provider, _) {
          // Update data source when vehicles change
          _dataSource = VehicleDataSource(
            vehicles: provider.items,
            onEdit: _handleEdit,
            onDelete: _handleDelete,
          );

          // Load movements when switching to analytics view
          if (_viewMode == ViewMode.analytics && provider.movements.isEmpty) {
            WidgetsBinding.instance.addPostFrameCallback((_) {
              provider.fetchMovementsForAnalytics();
            });
          }

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

  /// Build header with title and controls (matches React header)
  Widget _buildHeader(VehicleProvider provider) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text(
          'Gestión de Vehículos',
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
                    Text('Dashboard'),
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
            const SizedBox(width: 8),
            // Add button
            ElevatedButton.icon(
              onPressed: _handleCreate,
              icon: const Icon(Icons.add, size: 18),
              label: const Text('Nuevo Vehículo'),
            ),
          ],
        ),
      ],
    );
  }

  /// Build table view with SfDataGrid (matches React DataGrid)
  Widget _buildTableView(VehicleProvider provider) {
    // Using LoadingStateWidget
    if (provider.isLoading && provider.items.isEmpty) {
      return const LoadingStateWidget(message: 'Cargando vehículos...');
    }

    // Using ErrorStateWidget
    if (provider.isError) {
      return ErrorStateWidget(
        errorMessage: provider.errorMessage ?? 'Error al cargar vehículos',
        onRetry: _handleRefresh,
      );
    }

    // Using DataGridWrapper
    return DataGridWrapper(
      source: _dataSource,
      columns: _buildColumns(),
    );
  }

  /// Build data grid columns (matches React columns definition)
  List<GridColumn> _buildColumns() {
    return [
      GridColumn(
        columnName: 'placa',
        width: 130,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Placa',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'marca',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Marca',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'modelo',
        width: 150,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Modelo',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'anio',
        width: 100,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Año',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'category',
        width: 180,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Categoría',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'isActive',
        width: 120,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.center,
          child: const Text(
            'Estado',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'descripcion',
        minimumWidth: 250,
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
        width: 130,
        allowSorting: false,
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

  /// Build analytics view with stats cards and charts (matches React analytics view)
  Widget _buildAnalyticsView(VehicleProvider provider) {
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
              // Fleet by Category Chart (Pie)
              Expanded(
                flex: 4,
                child: _buildCategoryChart(provider),
              ),
              const SizedBox(width: 24),
              // Fleet by Age Chart (Bar)
              Expanded(
                flex: 4,
                child: _buildAgeChart(provider),
              ),
              const SizedBox(width: 24),
              // Top 10 Consumers Chart (Bar)
              Expanded(
                flex: 4,
                child: _buildConsumptionChart(provider),
              ),
            ],
          ),
        ],
      ),
    );
  }

  /// Build stats cards (matches React stats cards)
  Widget _buildStatsCards(VehicleProvider provider) {
    final stats = provider.stats;

    return Row(
      children: [
        // Using StatsCard widget
        Expanded(
          child: StatsCard(
            title: 'Total Vehículos',
            value: stats.total.toString(),
            icon: Icons.local_shipping,
            iconColor: const Color(0xFF009688),
          ),
        ),
        const SizedBox(width: 24),
        Expanded(
          child: StatsCard(
            title: 'Vehículos Activos',
            value: stats.active.toString(),
            icon: Icons.check_circle,
            iconColor: const Color(0xFF4CAF50),
            change: stats.activeChange,
          ),
        ),
        const SizedBox(width: 24),
        Expanded(
          child: StatsCard(
            title: 'Vehículos Inactivos',
            value: stats.inactive.toString(),
            icon: Icons.cancel,
            iconColor: const Color(0xFF757575),
          ),
        ),
        const SizedBox(width: 24),
        Expanded(
          child: StatsCard(
            title: 'Consumo Este Mes (gal)',
            value: stats.monthConsumption.toFormatted(), // Using extension
            icon: Icons.local_gas_station,
            iconColor: const Color(0xFFF57C00),
          ),
        ),
      ],
    );
  }

  /// Build category pie chart (matches React Fleet by Category)
  Widget _buildCategoryChart(VehicleProvider provider) {
    final categoryData = provider.categoryData;

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
            'Flota por Categoría',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 300,
            child: categoryData.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCircularChart(
                    legend: const Legend(
                      isVisible: true,
                      position: LegendPosition.bottom,
                      overflowMode: LegendItemOverflowMode.wrap,
                      textStyle: TextStyle(fontSize: 11),
                    ),
                    tooltipBehavior: TooltipBehavior(enable: true),
                    series: <CircularSeries<CategoryData, String>>[
                      DoughnutSeries<CategoryData, String>(
                        dataSource: categoryData,
                        xValueMapper: (item, _) => item.category,
                        yValueMapper: (item, _) => item.count,
                        innerRadius: '50%',
                        cornerStyle: CornerStyle.bothCurve,
                        dataLabelSettings: const DataLabelSettings(
                          isVisible: true,
                          labelPosition: ChartDataLabelPosition.outside,
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

  /// Build age distribution bar chart (matches React Fleet by Age)
  Widget _buildAgeChart(VehicleProvider provider) {
    final ageData = provider.ageData;

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
            'Distribución por Antigüedad',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 300,
            child: ageData.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: const CategoryAxis(
                      labelStyle: TextStyle(fontSize: 11),
                      majorGridLines: MajorGridLines(width: 0),
                    ),
                    primaryYAxis: NumericAxis(
                      numberFormat: NumberFormat.compact(locale: 'es'),
                      majorGridLines: const MajorGridLines(
                        width: 0.5,
                        color: Color(0xFFE0E0E0),
                      ),
                    ),
                    tooltipBehavior: TooltipBehavior(enable: true),
                    series: <CartesianSeries<AgeData, String>>[
                      ColumnSeries<AgeData, String>(
                        dataSource: ageData,
                        xValueMapper: (item, _) => item.range,
                        yValueMapper: (item, _) => item.count,
                        color: AppTheme.primary,
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

  /// Build top consumers bar chart (matches React Top 10 Consumers)
  Widget _buildConsumptionChart(VehicleProvider provider) {
    final consumptionData = provider.consumptionData;

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
            'Top 10 Consumidores',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 300,
            child: consumptionData.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos de consumo disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: const CategoryAxis(
                      labelStyle: TextStyle(fontSize: 10),
                      majorGridLines: MajorGridLines(width: 0),
                      labelRotation: -45,
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
                    series: <CartesianSeries<ConsumptionData, String>>[
                      ColumnSeries<ConsumptionData, String>(
                        dataSource: consumptionData,
                        xValueMapper: (item, _) => item.placa,
                        yValueMapper: (item, _) => item.total,
                        color: const Color(0xFFF57C00),
                        borderRadius: BorderRadius.circular(4),
                        dataLabelSettings: const DataLabelSettings(
                          isVisible: true,
                          labelAlignment: ChartDataLabelAlignment.outer,
                          textStyle: TextStyle(fontSize: 9),
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


/// DataSource for SfDataGrid
/// Handles data mapping and cell rendering for vehicles
class VehicleDataSource extends DataGridSource {
  final List<VehicleEntity> vehicles;
  final void Function(VehicleEntity) onEdit;
  final Future<void> Function(String) onDelete;

  List<DataGridRow> _dataGridRows = [];

  VehicleDataSource({
    required this.vehicles,
    required this.onEdit,
    required this.onDelete,
  }) {
    _buildDataGridRows();
  }

  void _buildDataGridRows() {
    _dataGridRows = vehicles.map<DataGridRow>((vehicle) {
      return DataGridRow(cells: [
        DataGridCell<String>(columnName: 'placa', value: vehicle.placa),
        DataGridCell<String>(columnName: 'marca', value: vehicle.marca),
        DataGridCell<String>(columnName: 'modelo', value: vehicle.modelo),
        DataGridCell<int>(columnName: 'anio', value: vehicle.anio),
        DataGridCell<String>(columnName: 'category', value: vehicle.category),
        DataGridCell<bool>(columnName: 'isActive', value: vehicle.isActive),
        DataGridCell<String>(
          columnName: 'descripcion',
          value: vehicle.descripcion,
        ),
        DataGridCell<VehicleEntity>(columnName: 'actions', value: vehicle),
      ]);
    }).toList();
  }

  @override
  List<DataGridRow> get rows => _dataGridRows;

  @override
  DataGridRowAdapter buildRow(DataGridRow row) {
    return DataGridRowAdapter(
      cells: row.getCells().map<Widget>((cell) {
        if (cell.columnName == 'placa') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerLeft,
            child: Text(
              cell.value?.toString() ?? '',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
          );
        }

        if (cell.columnName == 'isActive') {
          final isActive = cell.value as bool;
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.center,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              decoration: BoxDecoration(
                color: isActive
                    ? Colors.green.withValues(alpha: 0.1)
                    : Colors.grey.withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text(
                isActive ? 'ACTIVO' : 'INACTIVO',
                style: TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  color: isActive ? Colors.green : Colors.grey,
                ),
              ),
            ),
          );
        }

        if (cell.columnName == 'actions') {
          final vehicle = cell.value as VehicleEntity;
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 8),
            alignment: Alignment.center,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                IconButton(
                  icon: const Icon(Icons.edit, size: 20),
                  onPressed: () => onEdit(vehicle),
                  tooltip: 'Editar',
                  color: Colors.blue,
                ),
                IconButton(
                  icon: const Icon(Icons.delete, size: 20),
                  onPressed: () => onDelete(vehicle.id),
                  tooltip: 'Eliminar',
                  color: Colors.red,
                ),
              ],
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

