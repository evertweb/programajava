import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

import '../../core/theme/app_theme.dart';
import '../../domain/entities/supplier_entity.dart';
import '../mixins/confirmation_dialog_mixin.dart';
import '../mixins/snackbar_mixin.dart';
import '../providers/supplier_provider.dart';
import '../widgets/common/data_grid_wrapper.dart';
import '../widgets/common/error_state_widget.dart';
import '../widgets/common/loading_state_widget.dart';
import '../widgets/supplier_form_dialog.dart';

class SuppliersScreen extends StatefulWidget {
  const SuppliersScreen({super.key});

  @override
  State<SuppliersScreen> createState() => _SuppliersScreenState();
}

class _SuppliersScreenState extends State<SuppliersScreen>
    with ConfirmationDialogMixin, SnackBarMixin {
  late SupplierProvider _provider;
  late SupplierDataSource _dataSource;
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _provider = SupplierProvider();
    _dataSource = SupplierDataSource(
      suppliers: [],
      onEdit: _handleEdit,
      onDelete: _handleDelete,
    );

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _provider.fetchSuppliers();
    });
  }

  @override
  void dispose() {
    _provider.dispose();
    _searchController.dispose();
    super.dispose();
  }

  void _handleRefresh() {
    _provider.fetchSuppliers();
  }

  void _handleSearch(String query) {
    _provider.searchSuppliers(query);
  }

  Future<void> _handleCreate() async {
    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => SupplierFormDialog(provider: _provider),
    );

    if (result == true) {
      _handleRefresh();
    }
  }

  Future<void> _handleEdit(SupplierEntity supplier) async {
    final result = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => SupplierFormDialog(
        provider: _provider,
        supplier: supplier,
      ),
    );

    if (result == true) {
      _handleRefresh();
    }
  }

  Future<void> _handleDelete(SupplierEntity supplier) async {
    // Using ConfirmationDialogMixin
    final confirmed = await showDeleteConfirmation(itemName: supplier.name);

    if (confirmed) {
      final success = await _provider.deleteSupplier(supplier.id);
      
      // Using SnackBarMixin
      if (success) {
        showSuccessMessage('Proveedor eliminado exitosamente');
      } else {
        showErrorMessage(_provider.errorMessage ?? 'Error al eliminar');
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: _provider,
      child: Consumer<SupplierProvider>(
        builder: (context, provider, _) {
          _dataSource = SupplierDataSource(
            suppliers: provider.items,
            onEdit: _handleEdit,
            onDelete: _handleDelete,
          );

          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _buildHeader(provider),
              const SizedBox(height: 16),
              Expanded(
                child: _buildTableView(provider),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildHeader(SupplierProvider provider) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Text(
          'Proveedores',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w600,
            color: AppTheme.textPrimary,
          ),
        ),
        Row(
          children: [
            SizedBox(
              width: 250,
              child: TextField(
                controller: _searchController,
                decoration: InputDecoration(
                  hintText: 'Buscar proveedor...',
                  prefixIcon: const Icon(Icons.search),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  contentPadding: const EdgeInsets.symmetric(horizontal: 16),
                ),
                onChanged: _handleSearch,
              ),
            ),
            const SizedBox(width: 16),
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
            const SizedBox(width: 16),
            ElevatedButton.icon(
              onPressed: _handleCreate,
              icon: const Icon(Icons.add, size: 18),
              label: const Text('Nuevo Proveedor'),
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


  Widget _buildTableView(SupplierProvider provider) {
    // Using LoadingStateWidget
    if (provider.isLoading && provider.items.isEmpty) {
      return const LoadingStateWidget(message: 'Cargando proveedores...');
    }

    // Using ErrorStateWidget
    if (provider.isError) {
      return ErrorStateWidget(
        errorMessage: provider.errorMessage ?? 'Error al cargar proveedores',
        onRetry: _handleRefresh,
      );
    }

    // Using DataGridWrapper
    return DataGridWrapper(
      source: _dataSource,
      columns: _buildColumns(),
    );
  }

  List<GridColumn> _buildColumns() {
    return [
      createGridColumn(
        columnName: 'name',
        headerText: 'Nombre / Razón Social',
        minimumWidth: 200,
      ),
      createGridColumn(
        columnName: 'nit',
        headerText: 'NIT',
        width: 150,
      ),
      createGridColumn(
        columnName: 'telephone',
        headerText: 'Teléfono',
        width: 150,
      ),
      createGridColumn(
        columnName: 'email',
        headerText: 'Email',
        width: 200,
      ),
      createGridColumn(
        columnName: 'actions',
        headerText: 'Acciones',
        width: 100,
        alignment: Alignment.center,
        allowSorting: false,
      ),
    ];
  }
}

class SupplierDataSource extends DataGridSource {
  final List<SupplierEntity> suppliers;
  final Function(SupplierEntity) onEdit;
  final Function(SupplierEntity) onDelete;

  List<DataGridRow> _dataGridRows = [];

  SupplierDataSource({
    required this.suppliers,
    required this.onEdit,
    required this.onDelete,
  }) {
    _buildDataGridRows();
  }

  void _buildDataGridRows() {
    _dataGridRows = suppliers.map<DataGridRow>((supplier) {
      return DataGridRow(cells: [
        DataGridCell<String>(columnName: 'name', value: supplier.name),
        DataGridCell<String>(columnName: 'nit', value: supplier.nit),
        DataGridCell<String>(columnName: 'telephone', value: supplier.telephone),
        DataGridCell<String>(columnName: 'email', value: supplier.email),
        DataGridCell<SupplierEntity>(columnName: 'actions', value: supplier),
      ]);
    }).toList();
  }

  @override
  List<DataGridRow> get rows => _dataGridRows;

  @override
  DataGridRowAdapter buildRow(DataGridRow row) {
    return DataGridRowAdapter(
      cells: row.getCells().map<Widget>((cell) {
        if (cell.columnName == 'actions') {
          final supplier = cell.value as SupplierEntity;
          return Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              IconButton(
                icon: const Icon(Icons.edit, size: 20, color: AppTheme.primary),
                onPressed: () => onEdit(supplier),
                tooltip: 'Editar',
              ),
              IconButton(
                icon: const Icon(Icons.delete, size: 20, color: Colors.red),
                onPressed: () => onDelete(supplier),
                tooltip: 'Eliminar',
              ),
            ],
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
