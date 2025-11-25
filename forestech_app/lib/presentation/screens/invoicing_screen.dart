import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

import '../../core/theme/app_theme.dart';
import '../../domain/entities/invoice_entity.dart';
import '../providers/invoice_provider.dart';
import '../widgets/invoice_create_dialog.dart';
import '../widgets/invoice_detail_dialog.dart';

/// ViewMode enum matching React's ViewMode type
enum ViewMode { table, analytics }

/// InvoicingScreen - Main invoicing management interface
/// Matches React InvoicesPanel component functionality
class InvoicingScreen extends StatefulWidget {
  const InvoicingScreen({super.key});

  @override
  State<InvoicingScreen> createState() => _InvoicingScreenState();
}

class _InvoicingScreenState extends State<InvoicingScreen> {
  ViewMode _viewMode = ViewMode.table;
  late InvoiceProvider _provider;
  late InvoiceDataSource _dataSource;

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
    _provider = InvoiceProvider();
    _dataSource = InvoiceDataSource(
      invoices: [],
      formatCurrency: _formatCurrency,
      onViewDetail: _handleViewDetail,
      onCancel: _handleCancel,
    );

    // Load invoices on mount (matches React useEffect)
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _provider.fetchInvoices();
    });
  }

  @override
  void dispose() {
    _provider.dispose();
    super.dispose();
  }

  void _handleRefresh() {
    _provider.fetchInvoices();
  }

  void _handleCreate() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => InvoiceCreateDialog(
        onClose: (success) {
          if (success) {
            _provider.fetchInvoices();
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Factura creada exitosamente'),
                backgroundColor: AppTheme.success,
              ),
            );
          }
        },
      ),
    );
  }

  void _handleViewDetail(InvoiceEntity invoice) {
    showDialog(
      context: context,
      builder: (context) => InvoiceDetailDialog(invoice: invoice),
    );
  }

  Future<void> _handleCancel(InvoiceEntity invoice) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirmar Anulación'),
        content: const Text(
          '¿Está seguro de anular esta factura? Esta acción no se puede deshacer.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppTheme.error,
            ),
            child: const Text('Anular'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      final success = await _provider.cancelInvoice(invoice.id);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              success
                  ? 'Factura anulada exitosamente'
                  : 'Error al anular factura',
            ),
            backgroundColor: success ? AppTheme.success : AppTheme.error,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _provider,
      child: Consumer<InvoiceProvider>(
        builder: (context, provider, _) {
          // Update data source when invoices change
          _dataSource = InvoiceDataSource(
            invoices: provider.items,
            formatCurrency: _formatCurrency,
            onViewDetail: _handleViewDetail,
            onCancel: _handleCancel,
          );

          return Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
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
            ),
          );
        },
      ),
    );
  }

  /// Build header with title and controls (matches React header)
  Widget _buildHeader(InvoiceProvider provider) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text(
          'Gestión de Facturación',
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
            // New invoice button
            ElevatedButton.icon(
              onPressed: _handleCreate,
              icon: const Icon(Icons.add, size: 18),
              label: const Text('Nueva Factura'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppTheme.primary,
                foregroundColor: Colors.white,
              ),
            ),
          ],
        ),
      ],
    );
  }

  /// Build table view with SfDataGrid
  Widget _buildTableView(InvoiceProvider provider) {
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
              provider.errorMessage ?? 'Error al cargar facturas',
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
          sortingGestureType: SortingGestureType.tap,
        ),
      ),
    );
  }

  /// Build data grid columns matching React columns definition
  List<GridColumn> _buildColumns() {
    return [
      GridColumn(
        columnName: 'numeroFactura',
        width: 140,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'No. Factura',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'fechaEmision',
        width: 160,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Fecha Emisión',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'clienteNombre',
        minimumWidth: 250,
        label: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: const Text(
            'Cliente / Proveedor',
            style: TextStyle(fontWeight: FontWeight.w600),
          ),
        ),
      ),
      GridColumn(
        columnName: 'total',
        width: 180,
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
        columnName: 'estado',
        width: 130,
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

  /// Build analytics view with stats cards and charts
  Widget _buildAnalyticsView(InvoiceProvider provider) {
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
              // Timeline Chart (Line)
              Expanded(
                flex: 7,
                child: _buildTimelineChart(provider),
              ),
              const SizedBox(width: 24),
              // Supplier Chart (Bar)
              Expanded(
                flex: 5,
                child: _buildSupplierChart(provider),
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
  Widget _buildStatsCards(InvoiceProvider provider) {
    final stats = provider.stats;

    return Row(
      children: [
        // Facturado Este Mes
        Expanded(
          child: _StatsCard(
            title: 'Facturado Este Mes',
            value: _formatCurrency(stats.thisMonthTotal),
            subtitle: '${stats.thisMonthCount} facturas',
            change: stats.change,
            icon: Icons.attach_money,
            iconColor: const Color(0xFF2E7D32),
            bgColor: const Color(0xFFE8F5E9),
          ),
        ),
        const SizedBox(width: 24),
        // Facturas Pendientes
        Expanded(
          child: _StatsCard(
            title: 'Facturas Pendientes',
            value: _formatCurrency(stats.pendingTotal),
            subtitle: '${stats.pendingCount} facturas',
            icon: Icons.pending_actions,
            iconColor: const Color(0xFFF57C00),
            bgColor: const Color(0xFFFFF3E0),
          ),
        ),
        const SizedBox(width: 24),
        // Factura Más Alta
        Expanded(
          child: _StatsCard(
            title: 'Factura Más Alta',
            value: _formatCurrency(stats.maxInvoice),
            subtitle: 'de este mes',
            icon: Icons.receipt,
            iconColor: const Color(0xFF1976D2),
            bgColor: const Color(0xFFE3F2FD),
          ),
        ),
        const SizedBox(width: 24),
        // Top Proveedores
        Expanded(
          child: _StatsCard(
            title: 'Top Proveedores',
            value: provider.supplierStats.length.toString(),
            subtitle: 'activos',
            icon: Icons.store,
            iconColor: const Color(0xFF7B1FA2),
            bgColor: const Color(0xFFF3E5F5),
          ),
        ),
      ],
    );
  }

  /// Build timeline line chart (Gastos por Mes)
  Widget _buildTimelineChart(InvoiceProvider provider) {
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
                'Gastos por Mes',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                  color: AppTheme.textPrimary,
                ),
              ),
              // Date range toggle
              ToggleButtons(
                isSelected: [
                  provider.dateRange == 6,
                  provider.dateRange == 12,
                ],
                onPressed: (index) {
                  provider.setDateRange(index == 0 ? 6 : 12);
                },
                borderRadius: BorderRadius.circular(8),
                constraints: const BoxConstraints(
                  minHeight: 32,
                  minWidth: 80,
                ),
                textStyle: const TextStyle(fontSize: 12),
                children: const [
                  Text('6 meses'),
                  Text('12 meses'),
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
                      'No hay datos disponibles',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: CategoryAxis(
                      labelStyle: const TextStyle(fontSize: 11),
                      majorGridLines: const MajorGridLines(width: 0),
                    ),
                    primaryYAxis: NumericAxis(
                      numberFormat: NumberFormat.compactCurrency(
                        locale: 'es_CO',
                        symbol: '\$',
                        decimalDigits: 0,
                      ),
                      majorGridLines: const MajorGridLines(
                        width: 0.5,
                        color: Color(0xFFE0E0E0),
                      ),
                    ),
                    tooltipBehavior: TooltipBehavior(
                      enable: true,
                      format: 'point.x: point.y',
                    ),
                    series: <CartesianSeries<TimelineDataPoint, String>>[
                      LineSeries<TimelineDataPoint, String>(
                        dataSource: provider.timelineData,
                        xValueMapper: (item, _) => item.month,
                        yValueMapper: (item, _) => item.total,
                        color: const Color(0xFF2E7D32),
                        width: 3,
                        markerSettings: const MarkerSettings(
                          isVisible: true,
                          height: 8,
                          width: 8,
                          color: Color(0xFF2E7D32),
                          borderColor: Colors.white,
                          borderWidth: 2,
                        ),
                        dataLabelSettings: const DataLabelSettings(
                          isVisible: false,
                        ),
                      ),
                    ],
                  ),
          ),
        ],
      ),
    );
  }

  /// Build supplier bar chart (Top 5 Proveedores)
  Widget _buildSupplierChart(InvoiceProvider provider) {
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
            'Top 5 Proveedores',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppTheme.textPrimary,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            'Por gasto total',
            style: TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            height: 350,
            child: provider.supplierStats.isEmpty
                ? const Center(
                    child: Text(
                      'No hay datos de proveedores',
                      style: TextStyle(color: AppTheme.textSecondary),
                    ),
                  )
                : SfCartesianChart(
                    primaryXAxis: NumericAxis(
                      numberFormat: NumberFormat.compactCurrency(
                        locale: 'es_CO',
                        symbol: '\$',
                        decimalDigits: 0,
                      ),
                      majorGridLines: const MajorGridLines(
                        width: 0.5,
                        color: Color(0xFFE0E0E0),
                      ),
                    ),
                    primaryYAxis: CategoryAxis(
                      labelStyle: const TextStyle(fontSize: 11),
                      majorGridLines: const MajorGridLines(width: 0),
                    ),
                    tooltipBehavior: TooltipBehavior(
                      enable: true,
                      format: 'point.y: point.x',
                    ),
                    series: <CartesianSeries<SupplierStatsItem, String>>[
                      BarSeries<SupplierStatsItem, String>(
                        dataSource: provider.supplierStats,
                        xValueMapper: (item, _) => item.name,
                        yValueMapper: (item, _) => item.total,
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
}

/// Stats Card Widget matching React's stats cards
class _StatsCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;
  final double? change;
  final IconData icon;
  final Color iconColor;
  final Color bgColor;

  const _StatsCard({
    required this.title,
    required this.value,
    required this.subtitle,
    this.change,
    required this.icon,
    required this.iconColor,
    required this.bgColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: bgColor,
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
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 2,
                        ),
                        decoration: BoxDecoration(
                          color: change! > 0 ? AppTheme.error : AppTheme.success,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(
                              change! > 0
                                  ? Icons.trending_up
                                  : Icons.trending_down,
                              size: 12,
                              color: Colors.white,
                            ),
                            const SizedBox(width: 2),
                            Text(
                              '${change! > 0 ? '+' : ''}${change!.toStringAsFixed(1)}%',
                              style: const TextStyle(
                                fontSize: 10,
                                fontWeight: FontWeight.w600,
                                color: Colors.white,
                              ),
                            ),
                          ],
                        ),
                      ),
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
}

/// DataSource for SfDataGrid
/// Handles data mapping and cell rendering for invoices
class InvoiceDataSource extends DataGridSource {
  final List<InvoiceEntity> invoices;
  final String Function(double) formatCurrency;
  final void Function(InvoiceEntity) onViewDetail;
  final void Function(InvoiceEntity) onCancel;

  List<DataGridRow> _dataGridRows = [];

  InvoiceDataSource({
    required this.invoices,
    required this.formatCurrency,
    required this.onViewDetail,
    required this.onCancel,
  }) {
    _buildDataGridRows();
  }

  void _buildDataGridRows() {
    _dataGridRows = invoices.map<DataGridRow>((invoice) {
      return DataGridRow(cells: [
        DataGridCell<String>(
          columnName: 'numeroFactura',
          value: invoice.numeroFactura,
        ),
        DataGridCell<String>(
          columnName: 'fechaEmision',
          value: DateFormat('yyyy-MM-dd').format(invoice.fechaEmision),
        ),
        DataGridCell<String>(
          columnName: 'clienteNombre',
          value: invoice.clienteNombre,
        ),
        DataGridCell<double>(
          columnName: 'total',
          value: invoice.total,
        ),
        DataGridCell<String>(
          columnName: 'estado',
          value: invoice.estado,
        ),
        DataGridCell<InvoiceEntity>(
          columnName: 'actions',
          value: invoice,
        ),
      ]);
    }).toList();
  }

  @override
  List<DataGridRow> get rows => _dataGridRows;

  @override
  DataGridRowAdapter buildRow(DataGridRow row) {
    return DataGridRowAdapter(
      cells: row.getCells().map<Widget>((cell) {
        if (cell.columnName == 'estado') {
          return _buildStatusChip(cell.value as String);
        }
        if (cell.columnName == 'total') {
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            alignment: Alignment.centerRight,
            child: Text(
              formatCurrency(cell.value as double),
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
          );
        }
        if (cell.columnName == 'actions') {
          return _buildActionsCell(cell.value as InvoiceEntity);
        }
        return Container(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          alignment: Alignment.centerLeft,
          child: Text(cell.value?.toString() ?? ''),
        );
      }).toList(),
    );
  }

  /// Build status chip (matches React's renderCell for estado)
  Widget _buildStatusChip(String estado) {
    Color chipColor;
    Color textColor;

    switch (estado) {
      case 'PAGADA':
        chipColor = const Color(0xFFE8F5E9);
        textColor = const Color(0xFF2E7D32);
        break;
      case 'PENDIENTE':
        chipColor = const Color(0xFFFFF3E0);
        textColor = const Color(0xFFF57C00);
        break;
      case 'ANULADA':
        chipColor = const Color(0xFFFFEBEE);
        textColor = const Color(0xFFD32F2F);
        break;
      default:
        chipColor = const Color(0xFFF5F5F5);
        textColor = const Color(0xFF757575);
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      alignment: Alignment.center,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        decoration: BoxDecoration(
          color: chipColor,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: textColor.withValues(alpha: 0.3)),
        ),
        child: Text(
          estado,
          style: TextStyle(
            fontSize: 12,
            fontWeight: FontWeight.w600,
            color: textColor,
          ),
        ),
      ),
    );
  }

  /// Build actions cell with view and cancel buttons
  Widget _buildActionsCell(InvoiceEntity invoice) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      alignment: Alignment.center,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        mainAxisSize: MainAxisSize.min,
        children: [
          // View Detail button
          IconButton(
            icon: const Icon(Icons.visibility, size: 20),
            tooltip: 'Ver Detalle',
            onPressed: () => onViewDetail(invoice),
            padding: const EdgeInsets.all(4),
            constraints: const BoxConstraints(),
          ),
          // Cancel button (only if not already cancelled)
          if (invoice.estado != 'ANULADA')
            IconButton(
              icon: const Icon(Icons.cancel, size: 20, color: AppTheme.error),
              tooltip: 'Anular',
              onPressed: () => onCancel(invoice),
              padding: const EdgeInsets.all(4),
              constraints: const BoxConstraints(),
            ),
        ],
      ),
    );
  }
}
