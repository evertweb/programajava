import '../../data/repositories/invoice_repository_impl.dart';
import '../../domain/entities/invoice_entity.dart';
import '../../domain/repositories/i_invoice_repository.dart';
import 'base_provider.dart';

/// Invoice statistics matching React's stats useMemo
class InvoiceStats {
  final double thisMonthTotal;
  final double lastMonthTotal;
  final double pendingTotal;
  final int thisMonthCount;
  final int lastMonthCount;
  final int pendingCount;
  final double maxInvoice;
  final double change; // Month-over-month percentage change

  const InvoiceStats({
    required this.thisMonthTotal,
    required this.lastMonthTotal,
    required this.pendingTotal,
    required this.thisMonthCount,
    required this.lastMonthCount,
    required this.pendingCount,
    required this.maxInvoice,
    required this.change,
  });

  factory InvoiceStats.empty() => const InvoiceStats(
        thisMonthTotal: 0,
        lastMonthTotal: 0,
        pendingTotal: 0,
        thisMonthCount: 0,
        lastMonthCount: 0,
        pendingCount: 0,
        maxInvoice: 0,
        change: 0,
      );
}

/// Timeline data point for charts
class TimelineDataPoint {
  final String month;
  final double total;

  const TimelineDataPoint({
    required this.month,
    required this.total,
  });
}

/// Supplier statistics for charts
class SupplierStatsItem {
  final String name;
  final double total;
  final int count;

  const SupplierStatsItem({
    required this.name,
    required this.total,
    required this.count,
  });
}

/// Provider for managing Invoice state
/// Matches React InvoicesPanel component logic
class InvoiceProvider extends BaseProvider<InvoiceEntity> {
  final IInvoiceRepository _repository;

  // Chart date range (6 or 12 months)
  int _dateRange = 12;

  InvoiceProvider({IInvoiceRepository? repository})
      : _repository = repository ?? InvoiceRepositoryImpl();

  // Getters
  int get dateRange => _dateRange;

  /// Sets the date range for timeline charts (6 or 12 months)
  void setDateRange(int range) {
    if (range == 6 || range == 12) {
      _dateRange = range;
      notifyListeners();
    }
  }

  /// Fetches all invoices from the repository
  Future<void> fetchInvoices() async {
    setLoading();

    final (invoices, failure) = await _repository.getAll();

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(invoices ?? []);
  }

  /// Gets an invoice by numero factura
  Future<void> getInvoiceByNumeroFactura(String numeroFactura) async {
    setLoading();

    final (invoice, failure) =
        await _repository.getByNumeroFactura(numeroFactura);

    if (failure != null) {
      setError(failure);
      return;
    }

    if (invoice != null) {
      setSelectedItem(invoice);
    }
  }

  /// Creates a new invoice
  Future<bool> createInvoice(InvoiceEntity invoice) async {
    setLoading();

    final (createdInvoice, failure) = await _repository.create(invoice);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (createdInvoice != null) {
      addItem(createdInvoice);
    }

    return true;
  }

  /// Updates an existing invoice
  Future<bool> updateInvoice(
    String numeroFactura,
    InvoiceEntity invoice,
  ) async {
    setLoading();

    final (updatedInvoice, failure) =
        await _repository.update(numeroFactura, invoice);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (updatedInvoice != null) {
      updateItem(updatedInvoice, (i) => i.numeroFactura == numeroFactura);
    }

    return true;
  }

  /// Deletes an invoice
  Future<bool> deleteInvoice(String numeroFactura) async {
    setLoading();

    final (success, failure) = await _repository.delete(numeroFactura);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (success) {
      removeItem((i) => i.numeroFactura == numeroFactura);
    }

    return success;
  }

  /// Cancels an invoice (anular)
  /// Matches React's handleCancel function
  Future<bool> cancelInvoice(String id) async {
    setLoading();

    final (success, failure) = await _repository.cancel(id);

    if (failure != null) {
      setError(failure);
      return false;
    }

    if (success) {
      // Reload invoices to get updated estado
      await fetchInvoices();
    }

    return success;
  }

  /// Fetches invoices by supplier ID
  Future<void> fetchBySupplierId(String supplierId) async {
    setLoading();

    final (invoices, failure) = await _repository.getBySupplierId(supplierId);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(invoices ?? []);
  }

  /// Fetches invoices by estado
  Future<void> fetchByEstado(String estado) async {
    setLoading();

    final (invoices, failure) = await _repository.getByEstado(estado);

    if (failure != null) {
      setError(failure);
      return;
    }

    setSuccess(invoices ?? []);
  }

  /// Gets pending invoices
  List<InvoiceEntity> get pendingInvoices =>
      items.where((i) => i.estado == 'PENDIENTE').toList();

  /// Gets paid invoices
  List<InvoiceEntity> get paidInvoices =>
      items.where((i) => i.estado == 'PAGADA').toList();

  /// Gets cancelled invoices
  List<InvoiceEntity> get cancelledInvoices =>
      items.where((i) => i.estado == 'ANULADA').toList();

  /// Gets active invoices (not cancelled)
  List<InvoiceEntity> get activeInvoices =>
      items.where((i) => i.estado != 'ANULADA').toList();

  /// Gets overdue invoices
  List<InvoiceEntity> get overdueInvoices =>
      items.where((i) => i.isOverdue).toList();

  /// Calculates total pending amount
  double get totalPendingAmount =>
      pendingInvoices.fold(0.0, (sum, i) => sum + i.total);

  /// Calculates total paid amount
  double get totalPaidAmount =>
      paidInvoices.fold(0.0, (sum, i) => sum + i.total);

  /// Calculates statistics (matches React's stats useMemo)
  InvoiceStats get stats {
    final now = DateTime.now();
    final thisMonth = DateTime(now.year, now.month, 1);
    final lastMonth = DateTime(now.year, now.month - 1, 1);

    double thisMonthTotal = 0;
    double lastMonthTotal = 0;
    double pendingTotal = 0;
    int thisMonthCount = 0;
    int lastMonthCount = 0;
    int pendingCount = 0;
    double maxInvoice = 0;

    for (final inv in items) {
      final invDate = inv.fechaEmision;
      final isThisMonth = invDate.isAfter(thisMonth) ||
          invDate.isAtSameMomentAs(thisMonth);
      final isLastMonth = (invDate.isAfter(lastMonth) ||
              invDate.isAtSameMomentAs(lastMonth)) &&
          invDate.isBefore(thisMonth);

      if (inv.estado != 'ANULADA') {
        if (isThisMonth) {
          thisMonthTotal += inv.total;
          thisMonthCount++;
        }
        if (isLastMonth) {
          lastMonthTotal += inv.total;
          lastMonthCount++;
        }
        if (inv.estado == 'PENDIENTE') {
          pendingTotal += inv.total;
          pendingCount++;
        }
        if (inv.total > maxInvoice) {
          maxInvoice = inv.total;
        }
      }
    }

    final change = lastMonthTotal > 0
        ? ((thisMonthTotal - lastMonthTotal) / lastMonthTotal) * 100
        : 0.0;

    return InvoiceStats(
      thisMonthTotal: thisMonthTotal,
      lastMonthTotal: lastMonthTotal,
      pendingTotal: pendingTotal,
      thisMonthCount: thisMonthCount,
      lastMonthCount: lastMonthCount,
      pendingCount: pendingCount,
      maxInvoice: maxInvoice,
      change: change,
    );
  }

  /// Generates timeline data for charts (matches React's timelineData useMemo)
  List<TimelineDataPoint> get timelineData {
    final now = DateTime.now();
    final monthlyData = <String, double>{};

    // Initialize months
    for (int i = _dateRange - 1; i >= 0; i--) {
      final date = DateTime(now.year, now.month - i, 1);
      final key =
          '${date.year}-${date.month.toString().padLeft(2, '0')}';
      monthlyData[key] = 0;
    }

    // Fill with actual data
    for (final inv in items) {
      if (inv.estado != 'ANULADA') {
        final invDate = inv.fechaEmision;
        final key =
            '${invDate.year}-${invDate.month.toString().padLeft(2, '0')}';
        if (monthlyData.containsKey(key)) {
          monthlyData[key] = (monthlyData[key] ?? 0) + inv.total;
        }
      }
    }

    // Convert to list and sort
    final entries = monthlyData.entries.toList()
      ..sort((a, b) => a.key.compareTo(b.key));

    return entries.map((e) {
      final date = DateTime.parse('${e.key}-01');
      final monthNames = [
        'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
        'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'
      ];
      final monthLabel =
          "${monthNames[date.month - 1]}'${date.year.toString().substring(2)}";
      return TimelineDataPoint(month: monthLabel, total: e.value);
    }).toList();
  }

  /// Generates supplier statistics (matches React's supplierStats useMemo)
  List<SupplierStatsItem> get supplierStats {
    final supplierMap = <String, ({double total, int count})>{};

    for (final inv in items) {
      if (inv.estado != 'ANULADA') {
        final current = supplierMap[inv.clienteNombre] ?? (total: 0.0, count: 0);
        supplierMap[inv.clienteNombre] = (
          total: current.total + inv.total,
          count: current.count + 1,
        );
      }
    }

    final result = supplierMap.entries
        .map((e) => SupplierStatsItem(
              name: e.key,
              total: e.value.total,
              count: e.value.count,
            ))
        .toList()
      ..sort((a, b) => b.total.compareTo(a.total));

    // Return top 5 suppliers
    return result.take(5).toList();
  }
}
