import 'package:flutter/material.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import '../../core/theme/app_theme.dart';
import '../providers/dashboard_provider.dart';

/// Dashboard Screen
/// Displays key metrics and system overview
/// Migrated from React Dashboard.tsx
class DashboardScreen extends StatefulWidget {
  const DashboardScreen({super.key});

  @override
  State<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen> {
  late DashboardProvider _provider;

  @override
  void initState() {
    super.initState();
    _provider = DashboardProvider();
    _fetchData();
  }

  Future<void> _fetchData() async {
    await _provider.fetchMetrics();
    if (mounted) setState(() {});
  }

  @override
  void dispose() {
    _provider.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: _provider,
      builder: (context, _) {
        // Loading state
        if (_provider.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }

        // Disconnected state (matching React's isDisconnected && !metrics)
        if (_provider.isDisconnected && !_provider.hasData) {
          return _buildDisconnectedState();
        }

        // Success state
        return _buildDashboardContent();
      },
    );
  }

  /// Disconnected state widget (matching React's disconnected UI)
  Widget _buildDisconnectedState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.cloud_off,
            size: 64,
            color: Theme.of(context).textTheme.bodyMedium?.color?.withValues(alpha: 0.5),
          ),
          const SizedBox(height: 16),
          Text(
            'Sin conexión al servidor',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: Theme.of(context).textTheme.bodyMedium?.color?.withValues(alpha: 0.7),
                ),
          ),
          const SizedBox(height: 8),
          Text(
            'No se pudo conectar con los microservicios.\nVerifique que el servidor esté activo.',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).textTheme.bodyMedium?.color?.withValues(alpha: 0.5),
                ),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: _fetchData,
            icon: const Icon(Icons.refresh),
            label: const Text('Reintentar'),
          ),
        ],
      ),
    );
  }

  /// Main dashboard content
  Widget _buildDashboardContent() {
    final metrics = _provider.metrics;

    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header (matching React's header)
          _buildHeader(),
          const SizedBox(height: 24),

          // Metric Cards Grid (matching React's Grid container)
          _buildMetricCardsGrid(metrics),
          const SizedBox(height: 24),

          // Charts Section (matching React's layout)
          _buildChartsSection(),
          const SizedBox(height: 24),

          // Welcome Section (matching React's gradient banner)
          _buildWelcomeSection(),
        ],
      ),
    );
  }

  /// Header section
  Widget _buildHeader() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Resumen General',
          style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                fontWeight: FontWeight.w600,
                letterSpacing: -0.5,
              ),
        ),
        const SizedBox(height: 4),
        Text(
          'Vista general del estado del sistema y métricas clave.',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: AppTheme.textSecondary,
              ),
        ),
      ],
    );
  }

  /// Metric cards grid (matching React's 5-column grid)
  Widget _buildMetricCardsGrid(DashboardMetrics metrics) {
    return LayoutBuilder(
      builder: (context, constraints) {
        // Calculate responsive columns
        int crossAxisCount = 5;
        if (constraints.maxWidth < 1200) crossAxisCount = 4;
        if (constraints.maxWidth < 900) crossAxisCount = 3;
        if (constraints.maxWidth < 600) crossAxisCount = 2;

        return GridView.count(
          crossAxisCount: crossAxisCount,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          mainAxisSpacing: 16,
          crossAxisSpacing: 16,
          childAspectRatio: 1.3,
          children: [
            _MetricCard(
              title: 'Productos Activos',
              value: metrics.productsCount,
              icon: Icons.inventory_2,
              color: const Color(0xFF009688), // Teal
            ),
            _MetricCard(
              title: 'Flota Vehicular',
              value: metrics.vehiclesCount,
              icon: Icons.local_shipping,
              color: const Color(0xFF455A64), // Slate
            ),
            _MetricCard(
              title: 'Movimientos',
              value: metrics.movementsCount,
              icon: Icons.move_to_inbox,
              color: const Color(0xFFF57C00), // Orange
            ),
            _MetricCard(
              title: 'Facturación',
              value: metrics.invoicesCount,
              icon: Icons.receipt,
              color: const Color(0xFF7B1FA2), // Purple
            ),
            _MetricCard(
              title: 'Proveedores',
              value: metrics.suppliersCount,
              icon: Icons.people,
              color: const Color(0xFFD32F2F), // Red
            ),
          ],
        );
      },
    );
  }

  /// Charts section (matching React's layout: 8/4 grid)
  Widget _buildChartsSection() {
    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide = constraints.maxWidth >= 900;

        if (isWide) {
          return Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Stock Chart (8/12 = 66.67%)
              Expanded(
                flex: 2,
                child: _buildStockChart(),
              ),
              const SizedBox(width: 24),
              // Pie Chart (4/12 = 33.33%)
              Expanded(
                flex: 1,
                child: _buildMovementsPieChart(),
              ),
            ],
          );
        } else {
          return Column(
            children: [
              _buildStockChart(),
              const SizedBox(height: 24),
              _buildMovementsPieChart(),
            ],
          );
        }
      },
    );
  }

  /// Stock levels bar chart (matching React's BarChart)
  Widget _buildStockChart() {
    final stockData = _provider.stockData;

    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: Colors.grey.shade200),
      ),
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header with toggle buttons
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Niveles de Stock (Top 7)',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                ),
                // Scale type toggle (matching React's ToggleButtonGroup)
                _buildScaleToggle(),
              ],
            ),
            const SizedBox(height: 12),

            // Info chip (matching React's Chip)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                border: Border.all(color: AppTheme.primary),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text(
                _provider.scaleInfoText,
                style: TextStyle(
                  fontSize: 12,
                  color: AppTheme.primary,
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Bar Chart
            SizedBox(
              height: 300,
              child: stockData.isEmpty
                  ? const Center(child: Text('Sin datos de stock'))
                  : SfCartesianChart(
                      primaryXAxis: const CategoryAxis(
                        labelRotation: -45,
                        labelStyle: TextStyle(fontSize: 10),
                      ),
                      primaryYAxis: NumericAxis(
                        title: AxisTitle(text: _provider.scaleLabel),
                      ),
                      tooltipBehavior: TooltipBehavior(
                        enable: true,
                        format: 'point.x: point.y',
                      ),
                      series: <CartesianSeries<StockDataItem, String>>[
                        ColumnSeries<StockDataItem, String>(
                          dataSource: stockData,
                          xValueMapper: (item, _) => item.name,
                          yValueMapper: (item, _) => item.stock,
                          color: AppTheme.primary,
                          borderRadius: const BorderRadius.only(
                            topLeft: Radius.circular(8),
                            topRight: Radius.circular(8),
                          ),
                          dataLabelSettings: const DataLabelSettings(
                            isVisible: false,
                          ),
                        ),
                      ],
                    ),
            ),

            // Real values reference (matching React's conditional display)
            if (_provider.scaleType != ScaleType.linear && stockData.isNotEmpty) ...[
              const SizedBox(height: 16),
              const Divider(),
              const SizedBox(height: 8),
              Text(
                'Valores reales (galones):',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: AppTheme.textSecondary,
                    ),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: stockData.map((item) {
                  return Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                    decoration: BoxDecoration(
                      border: Border.all(color: Colors.grey.shade300),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      '${item.name}: ${_formatNumber(item.originalStock)} gal',
                      style: const TextStyle(fontSize: 11),
                    ),
                  );
                }).toList(),
              ),
            ],
          ],
        ),
      ),
    );
  }

  /// Scale toggle buttons (matching React's ToggleButtonGroup)
  Widget _buildScaleToggle() {
    return SegmentedButton<ScaleType>(
      segments: const [
        ButtonSegment<ScaleType>(
          value: ScaleType.linear,
          label: Text('Lineal', style: TextStyle(fontSize: 12)),
        ),
        ButtonSegment<ScaleType>(
          value: ScaleType.log,
          label: Text('Logarítmica', style: TextStyle(fontSize: 12)),
        ),
        ButtonSegment<ScaleType>(
          value: ScaleType.percent,
          label: Text('Porcentaje', style: TextStyle(fontSize: 12)),
        ),
      ],
      selected: {_provider.scaleType},
      onSelectionChanged: (Set<ScaleType> selected) {
        _provider.setScaleType(selected.first);
      },
      style: ButtonStyle(
        visualDensity: VisualDensity.compact,
        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
      ),
    );
  }

  /// Movements pie chart (matching React's PieChart)
  Widget _buildMovementsPieChart() {
    final movementData = _provider.movementDistribution;

    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: Colors.grey.shade200),
      ),
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Distribución de Movimientos',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w600,
                  ),
            ),
            const SizedBox(height: 16),

            // Pie Chart
            SizedBox(
              height: 250,
              child: movementData.isEmpty || movementData.every((m) => m.value == 0)
                  ? const Center(child: Text('Sin movimientos'))
                  : SfCircularChart(
                      series: <CircularSeries<MovementDistributionItem, String>>[
                        DoughnutSeries<MovementDistributionItem, String>(
                          dataSource: movementData,
                          xValueMapper: (item, _) => item.label,
                          yValueMapper: (item, _) => item.value,
                          pointColorMapper: (item, _) => Color(item.color),
                          innerRadius: '60%',
                          cornerStyle: CornerStyle.bothFlat,
                          dataLabelSettings: const DataLabelSettings(
                            isVisible: false,
                          ),
                        ),
                      ],
                    ),
            ),
            const SizedBox(height: 16),

            // Legend (matching React's custom legend)
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: movementData.map((item) {
                return Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 12),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Container(
                        width: 12,
                        height: 12,
                        decoration: BoxDecoration(
                          color: Color(item.color),
                          shape: BoxShape.circle,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Text(
                        '${item.label}: ',
                        style: TextStyle(
                          fontSize: 13,
                          color: AppTheme.textSecondary,
                        ),
                      ),
                      Text(
                        '${item.value}',
                        style: const TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                );
              }).toList(),
            ),
          ],
        ),
      ),
    );
  }

  /// Welcome section with gradient (matching React's gradient Paper)
  Widget _buildWelcomeSection() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(32),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [
            Color(0xFF009688), // Teal
            Color(0xFF00695C), // Dark teal
          ],
        ),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Stack(
        children: [
          // Decorative circles (matching React's decorative elements)
          Positioned(
            top: -50,
            right: -50,
            child: Container(
              width: 200,
              height: 200,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: Colors.white.withValues(alpha: 0.1),
              ),
            ),
          ),
          Positioned(
            bottom: -30,
            right: 80,
            child: Container(
              width: 120,
              height: 120,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: Colors.white.withValues(alpha: 0.05),
              ),
            ),
          ),

          // Content
          Row(
            children: [
              // Icon container
              Container(
                width: 120,
                height: 120,
                decoration: BoxDecoration(
                  color: Colors.white.withValues(alpha: 0.15),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: const Icon(
                  Icons.local_shipping,
                  size: 64,
                  color: Colors.white,
                ),
              ),
              const SizedBox(width: 32),

              // Text content
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Sistema de Gestión de Combustibles',
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'Controle el inventario de combustibles, gestione la flota vehicular, '
                      'registre movimientos de entrada y salida, y administre la facturación '
                      'de proveedores desde un solo lugar.',
                      style: TextStyle(
                        fontSize: 15,
                        color: Colors.white.withValues(alpha: 0.9),
                        height: 1.5,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  /// Format number with locale separators
  String _formatNumber(double value) {
    if (value >= 1000000) {
      return '${(value / 1000000).toStringAsFixed(1)}M';
    } else if (value >= 1000) {
      return '${(value / 1000).toStringAsFixed(1)}K';
    }
    return value.toStringAsFixed(0);
  }
}

/// Metric Card Widget (matching React's MetricCard component)
class _MetricCard extends StatelessWidget {
  final String title;
  final int value;
  final IconData icon;
  final Color color;

  const _MetricCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: Colors.grey.shade200),
      ),
      child: InkWell(
        onTap: () {}, // Can add navigation here later
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Stack(
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Icon and Value Row
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      // Icon container (matching React's icon box)
                      Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: color.withValues(alpha: 0.15),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Icon(
                          icon,
                          size: 28,
                          color: color,
                        ),
                      ),
                      // Value
                      Text(
                        '$value',
                        style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                              fontWeight: FontWeight.w600,
                            ),
                      ),
                    ],
                  ),
                  const Spacer(),
                  // Title
                  Text(
                    title,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppTheme.textSecondary,
                          fontWeight: FontWeight.w500,
                          letterSpacing: 0.02,
                        ),
                  ),
                ],
              ),

              // Decorative background circle (matching React's decorative Box)
              Positioned(
                right: -20,
                bottom: -20,
                child: Container(
                  width: 100,
                  height: 100,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: color.withValues(alpha: 0.08),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
