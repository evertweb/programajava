import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../core/theme/app_theme.dart';
import '../../data/models/invoice_model.dart';
import '../../data/models/product_model.dart';
import '../../data/repositories/invoice_repository_impl.dart';
import '../../data/repositories/product_repository_impl.dart';
import '../../data/repositories/supplier_repository_impl.dart';
import '../../domain/entities/product_entity.dart';
import '../../domain/entities/supplier_entity.dart';

/// Invoice line item for the form (matches React's InvoiceLine)
class InvoiceLine {
  final int tempId;
  String? productId;
  String producto;
  double cantidad;
  double precioUnitario;
  String presentation;
  bool isNewProduct;
  double ivaPercent;

  InvoiceLine({
    required this.tempId,
    this.productId,
    this.producto = '',
    this.cantidad = 1,
    this.precioUnitario = 0,
    this.presentation = 'UNIDAD',
    this.isNewProduct = false,
    this.ivaPercent = 13,
  });

  /// Calculate line subtotal
  double get subtotal => cantidad * precioUnitario;

  /// Calculate line IVA amount
  double get ivaAmount => subtotal * (ivaPercent / 100);

  /// Calculate line total with IVA
  double get total => subtotal + ivaAmount;
}

/// InvoiceCreateDialog - Dialog for creating new invoices
/// Matches React's InvoiceCreateDialog component functionality
class InvoiceCreateDialog extends StatefulWidget {
  final void Function(bool success) onClose;

  const InvoiceCreateDialog({
    super.key,
    required this.onClose,
  });

  @override
  State<InvoiceCreateDialog> createState() => _InvoiceCreateDialogState();
}

class _InvoiceCreateDialogState extends State<InvoiceCreateDialog> {
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = true;
  bool _isSubmitting = false;
  String? _error;

  // Form data matching React's formData state
  String _numeroFactura = '';
  DateTime _fechaEmision = DateTime.now();
  DateTime _fechaVencimiento = DateTime.now();
  String _formaPago = 'CONTADO';
  String _cuentaBancaria = '';
  String _observaciones = '';

  // Selected supplier
  SupplierEntity? _selectedSupplier;

  // Invoice lines
  final List<InvoiceLine> _lines = [];

  // Data lists
  List<SupplierEntity> _suppliers = [];
  List<ProductEntity> _products = [];

  // Currency formatter
  final _currencyFormat = NumberFormat.currency(
    locale: 'es_CO',
    symbol: '\$',
    decimalDigits: 0,
  );

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  /// Load suppliers and products (matches React's loadData)
  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final supplierRepo = SupplierRepositoryImpl();
      final productRepo = ProductRepositoryImpl();

      // Parallel fetch matching React's Promise.all
      final results = await Future.wait([
        supplierRepo.getAll(),
        productRepo.getAll(),
      ]);

      final (suppliers, supplierFailure) =
          results[0] as (List<SupplierEntity>?, dynamic);
      final (products, productFailure) =
          results[1] as (List<ProductEntity>?, dynamic);

      if (supplierFailure != null || productFailure != null) {
        setState(() {
          _error = 'Error al cargar datos necesarios';
          _isLoading = false;
        });
        return;
      }

      setState(() {
        _suppliers = suppliers ?? [];
        _products = products ?? [];
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Error al cargar datos necesarios';
        _isLoading = false;
      });
    }
  }

  /// Handle supplier selection (matches React's handleSupplierChange)
  void _handleSupplierChange(SupplierEntity? supplier) {
    setState(() {
      _selectedSupplier = supplier;
    });
  }

  /// Add a new line (matches React's addLine)
  void _addLine() {
    setState(() {
      _lines.add(InvoiceLine(
        tempId: DateTime.now().millisecondsSinceEpoch,
      ));
    });
  }

  /// Remove a line (matches React's removeLine)
  void _removeLine(int tempId) {
    setState(() {
      _lines.removeWhere((l) => l.tempId == tempId);
    });
  }

  /// Update a line field (matches React's updateLine)
  void _updateLine(int tempId, String field, dynamic value) {
    setState(() {
      final index = _lines.indexWhere((l) => l.tempId == tempId);
      if (index == -1) return;

      final line = _lines[index];

      switch (field) {
        case 'productId':
          line.productId = value as String?;
          // Auto-fill price and presentation if product changes
          if (value != null) {
            final product = _products.firstWhere(
              (p) => p.id == value,
              orElse: () => ProductEntity.empty(),
            );
            if (product.id.isNotEmpty) {
              line.producto = product.name;
              line.precioUnitario = product.unitPrice;
              line.presentation =
                  product.presentation ?? product.measurementUnit.value;
              line.isNewProduct = false;
            }
          }
          break;
        case 'producto':
          line.producto = value as String;
          break;
        case 'cantidad':
          line.cantidad = (value as num).toDouble();
          break;
        case 'precioUnitario':
          line.precioUnitario = (value as num).toDouble();
          break;
        case 'presentation':
          line.presentation = value as String;
          break;
        case 'isNewProduct':
          line.isNewProduct = value as bool;
          if (value) {
            line.productId = null;
            line.producto = '';
            line.precioUnitario = 0;
            line.presentation = 'UNIDAD';
          }
          break;
        case 'ivaPercent':
          line.ivaPercent = (value as num).toDouble();
          break;
      }
    });
  }

  /// Calculate totals (matches React's calculateTotals)
  ({double subtotal, double iva, double total}) _calculateTotals() {
    final subtotal =
        _lines.fold(0.0, (sum, line) => sum + line.subtotal);
    final iva = _lines.fold(0.0, (sum, line) => sum + line.ivaAmount);
    final total = subtotal + iva;
    return (subtotal: subtotal, iva: iva, total: total);
  }

  /// Validate and submit form (matches React's handleSubmit)
  Future<void> _handleSubmit() async {
    if (_numeroFactura.isEmpty ||
        _selectedSupplier == null ||
        _lines.isEmpty) {
      setState(() {
        _error =
            'Por favor complete los campos obligatorios y agregue al menos un ítem';
      });
      return;
    }

    setState(() {
      _isSubmitting = true;
      _error = null;
    });

    try {
      // Prepare lines for backend (matches React's processedLines)
      final detalles = _lines.map((line) {
        if (line.isNewProduct) {
          // New product: send name + presentation for backend to create
          return InvoiceDetailModel(
            productId: null,
            productName: line.producto +
                (line.presentation != 'UNIDAD' ? ' ${line.presentation}' : ''),
            producto: line.producto,
            cantidad: line.cantidad,
            precioUnitario: line.precioUnitario,
            ivaPercent: line.ivaPercent,
          );
        }

        // Existing product
        return InvoiceDetailModel(
          productId: line.productId,
          producto: line.producto,
          cantidad: line.cantidad,
          precioUnitario: line.precioUnitario,
          ivaPercent: line.ivaPercent,
        );
      }).toList();

      final totals = _calculateTotals();

      final invoice = InvoiceModel(
        id: '',
        numeroFactura: _numeroFactura,
        supplierId: _selectedSupplier!.id,
        fechaEmision: _fechaEmision,
        fechaVencimiento: _fechaVencimiento,
        clienteNombre: _selectedSupplier!.name,
        clienteNit: _selectedSupplier!.nit,
        subtotal: totals.subtotal,
        iva: totals.iva,
        total: totals.total,
        observaciones: _observaciones,
        formaPago: _formaPago,
        cuentaBancaria: _cuentaBancaria,
        estado: 'PENDIENTE',
        detalles: detalles,
      );

      final repository = InvoiceRepositoryImpl();
      final (_, failure) = await repository.create(invoice);

      if (failure != null) {
        setState(() {
          _error = failure.message;
          _isSubmitting = false;
        });
        return;
      }

      if (mounted) {
        Navigator.pop(context);
        widget.onClose(true);
      }
    } catch (e) {
      setState(() {
        _error = 'Error al crear la factura';
        _isSubmitting = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final totals = _calculateTotals();

    return Dialog(
      child: Container(
        width: 900,
        constraints: BoxConstraints(
          maxHeight: MediaQuery.of(context).size.height * 0.9,
        ),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Title
              Padding(
                padding: const EdgeInsets.all(24),
                child: Row(
                  children: [
                    const Expanded(
                      child: Text(
                        'Nueva Factura de Compra',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    IconButton(
                      onPressed: () {
                        Navigator.pop(context);
                        widget.onClose(false);
                      },
                      icon: const Icon(Icons.close),
                    ),
                  ],
                ),
              ),
              const Divider(height: 1),

              // Content
              Flexible(
                child: SingleChildScrollView(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      // Error alert
                      if (_error != null) _buildErrorAlert(),

                      // Loading state
                      if (_isLoading)
                        const Padding(
                          padding: EdgeInsets.all(32),
                          child: Center(child: CircularProgressIndicator()),
                        )
                      else ...[
                        // Header Info
                        _buildHeaderFields(),
                        const SizedBox(height: 24),

                        // Lines
                        _buildLinesSection(),
                        const SizedBox(height: 24),

                        // Footer Totals
                        _buildFooterTotals(totals),
                        const SizedBox(height: 24),

                        // Observaciones
                        TextFormField(
                          maxLines: 2,
                          decoration: const InputDecoration(
                            labelText: 'Observaciones',
                            border: OutlineInputBorder(),
                          ),
                          onChanged: (value) => _observaciones = value,
                        ),
                      ],
                    ],
                  ),
                ),
              ),

              // Actions
              const Divider(height: 1),
              Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    TextButton(
                      onPressed: () {
                        Navigator.pop(context);
                        widget.onClose(false);
                      },
                      child: const Text('Cancelar'),
                    ),
                    const SizedBox(width: 16),
                    ElevatedButton(
                      onPressed: _isSubmitting || _lines.isEmpty
                          ? null
                          : _handleSubmit,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppTheme.primary,
                        foregroundColor: Colors.white,
                      ),
                      child: Text(
                        _isSubmitting ? 'Guardando...' : 'Crear Factura',
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// Build error alert
  Widget _buildErrorAlert() {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppTheme.error.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: AppTheme.error),
      ),
      child: Row(
        children: [
          const Icon(Icons.error_outline, color: AppTheme.error),
          const SizedBox(width: 12),
          Expanded(child: Text(_error!)),
          IconButton(
            icon: const Icon(Icons.close, size: 18),
            onPressed: () => setState(() => _error = null),
            padding: EdgeInsets.zero,
            constraints: const BoxConstraints(),
          ),
        ],
      ),
    );
  }

  /// Build header form fields (matches React's Grid container)
  Widget _buildHeaderFields() {
    return Column(
      children: [
        Row(
          children: [
            // Proveedor Autocomplete
            Expanded(
              child: Autocomplete<SupplierEntity>(
                optionsBuilder: (textEditingValue) {
                  if (textEditingValue.text.isEmpty) {
                    return _suppliers;
                  }
                  final query = textEditingValue.text.toLowerCase();
                  return _suppliers.where((s) =>
                      s.name.toLowerCase().contains(query) ||
                      s.nit.toLowerCase().contains(query));
                },
                displayStringForOption: (s) => '${s.name} (${s.nit})',
                onSelected: _handleSupplierChange,
                fieldViewBuilder:
                    (context, controller, focusNode, onSubmitted) {
                  return TextFormField(
                    controller: controller,
                    focusNode: focusNode,
                    decoration: const InputDecoration(
                      labelText: 'Proveedor *',
                      border: OutlineInputBorder(),
                    ),
                  );
                },
              ),
            ),
            const SizedBox(width: 16),
            // Número de Factura
            Expanded(
              child: TextFormField(
                decoration: const InputDecoration(
                  labelText: 'Número de Factura *',
                  border: OutlineInputBorder(),
                ),
                onChanged: (value) => _numeroFactura = value,
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            // Fecha Emisión
            Expanded(
              child: InkWell(
                onTap: () async {
                  final date = await showDatePicker(
                    context: context,
                    initialDate: _fechaEmision,
                    firstDate: DateTime(2020),
                    lastDate: DateTime(2030),
                  );
                  if (date != null) {
                    setState(() => _fechaEmision = date);
                  }
                },
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Fecha Emisión',
                    border: OutlineInputBorder(),
                    suffixIcon: Icon(Icons.calendar_today),
                  ),
                  child: Text(
                    DateFormat('yyyy-MM-dd').format(_fechaEmision),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 16),
            // Fecha Vencimiento
            Expanded(
              child: InkWell(
                onTap: () async {
                  final date = await showDatePicker(
                    context: context,
                    initialDate: _fechaVencimiento,
                    firstDate: DateTime(2020),
                    lastDate: DateTime(2030),
                  );
                  if (date != null) {
                    setState(() => _fechaVencimiento = date);
                  }
                },
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Fecha Vencimiento',
                    border: OutlineInputBorder(),
                    suffixIcon: Icon(Icons.calendar_today),
                  ),
                  child: Text(
                    DateFormat('yyyy-MM-dd').format(_fechaVencimiento),
                  ),
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            // Forma de Pago
            Expanded(
              child: DropdownButtonFormField<String>(
                initialValue: _formaPago,
                decoration: const InputDecoration(
                  labelText: 'Forma de Pago',
                  border: OutlineInputBorder(),
                ),
                items: const [
                  DropdownMenuItem(value: 'CONTADO', child: Text('CONTADO')),
                  DropdownMenuItem(value: 'CREDITO', child: Text('CRÉDITO')),
                ],
                onChanged: (value) {
                  if (value != null) {
                    setState(() => _formaPago = value);
                  }
                },
              ),
            ),
            const SizedBox(width: 16),
            // Cuenta Bancaria
            Expanded(
              child: TextFormField(
                decoration: const InputDecoration(
                  labelText: 'Cuenta Bancaria',
                  hintText: 'Opcional',
                  border: OutlineInputBorder(),
                ),
                onChanged: (value) => _cuentaBancaria = value,
              ),
            ),
          ],
        ),
      ],
    );
  }

  /// Build lines section with table (matches React's Lines section)
  Widget _buildLinesSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Detalle de Items',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            OutlinedButton.icon(
              onPressed: _addLine,
              icon: const Icon(Icons.add, size: 18),
              label: const Text('Agregar Item'),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: const Color(0xFFE0E0E0)),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Column(
            children: [
              // Header
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: const BoxDecoration(
                  color: Color(0xFFF5F5F5),
                  borderRadius: BorderRadius.vertical(top: Radius.circular(8)),
                ),
                child: Row(
                  children: [
                    const Expanded(
                      flex: 3,
                      child: Text('Producto', style: TextStyle(fontWeight: FontWeight.w600)),
                    ),
                    SizedBox(
                      width: 100,
                      child: Text('Presentación', style: TextStyle(fontWeight: FontWeight.w600)),
                    ),
                    SizedBox(
                      width: 80,
                      child: Text('Cantidad', textAlign: TextAlign.right, style: TextStyle(fontWeight: FontWeight.w600)),
                    ),
                    SizedBox(
                      width: 100,
                      child: Text('Precio Unit.', textAlign: TextAlign.right, style: TextStyle(fontWeight: FontWeight.w600)),
                    ),
                    SizedBox(
                      width: 70,
                      child: Text('IVA %', textAlign: TextAlign.right, style: TextStyle(fontWeight: FontWeight.w600)),
                    ),
                    SizedBox(
                      width: 110,
                      child: Text('Total + IVA', textAlign: TextAlign.right, style: TextStyle(fontWeight: FontWeight.w600)),
                    ),
                    const SizedBox(width: 40),
                  ],
                ),
              ),
              const Divider(height: 1),
              // Lines
              if (_lines.isEmpty)
                Container(
                  padding: const EdgeInsets.all(24),
                  alignment: Alignment.center,
                  child: const Text(
                    'No hay items agregados. Haga clic en "Agregar Item" para comenzar.',
                    style: TextStyle(color: AppTheme.textSecondary),
                  ),
                )
              else
                ..._lines.map((line) => _buildLineRow(line)),
            ],
          ),
        ),
      ],
    );
  }

  /// Build a line row (matches React's TableRow for each line)
  Widget _buildLineRow(InvoiceLine line) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: const BoxDecoration(
        border: Border(bottom: BorderSide(color: Color(0xFFE0E0E0))),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Product column with toggle
          Expanded(
            flex: 3,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Toggle: Existente vs Nuevo
                ToggleButtons(
                  isSelected: [!line.isNewProduct, line.isNewProduct],
                  onPressed: (index) {
                    _updateLine(line.tempId, 'isNewProduct', index == 1);
                  },
                  borderRadius: BorderRadius.circular(8),
                  constraints: const BoxConstraints(
                    minHeight: 32,
                    minWidth: 80,
                  ),
                  textStyle: const TextStyle(fontSize: 11),
                  children: const [
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(Icons.list, size: 14),
                        SizedBox(width: 4),
                        Text('Existente'),
                      ],
                    ),
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(Icons.add_circle, size: 14),
                        SizedBox(width: 4),
                        Text('Nuevo'),
                      ],
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                // Product field
                if (line.isNewProduct)
                  TextFormField(
                    initialValue: line.producto,
                    decoration: const InputDecoration(
                      hintText: 'Nombre del nuevo producto',
                      border: OutlineInputBorder(),
                      contentPadding:
                          EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                      isDense: true,
                    ),
                    onChanged: (value) =>
                        _updateLine(line.tempId, 'producto', value),
                  )
                else
                  Autocomplete<ProductEntity>(
                    optionsBuilder: (textEditingValue) {
                      if (textEditingValue.text.isEmpty) {
                        return _products;
                      }
                      final query = textEditingValue.text.toLowerCase();
                      return _products.where((p) =>
                          p.name.toLowerCase().contains(query));
                    },
                    displayStringForOption: (p) => p.name,
                    onSelected: (product) {
                      _updateLine(line.tempId, 'productId', product.id);
                    },
                    fieldViewBuilder:
                        (context, controller, focusNode, onSubmitted) {
                      // Set initial value if product is selected
                      if (line.productId != null &&
                          line.productId!.isNotEmpty &&
                          controller.text.isEmpty) {
                        final product = _products.firstWhere(
                          (p) => p.id == line.productId,
                          orElse: () => ProductEntity.empty(),
                        );
                        if (product.id.isNotEmpty) {
                          controller.text = product.name;
                        }
                      }
                      return TextFormField(
                        controller: controller,
                        focusNode: focusNode,
                        decoration: const InputDecoration(
                          hintText: 'Seleccionar producto',
                          border: OutlineInputBorder(),
                          contentPadding:
                              EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                          isDense: true,
                        ),
                      );
                    },
                    optionsViewBuilder: (context, onSelected, options) {
                      return Align(
                        alignment: Alignment.topLeft,
                        child: Material(
                          elevation: 4,
                          child: ConstrainedBox(
                            constraints: const BoxConstraints(maxHeight: 200),
                            child: ListView.builder(
                              shrinkWrap: true,
                              itemCount: options.length,
                              itemBuilder: (context, index) {
                                final product = options.elementAt(index);
                                return ListTile(
                                  title: Text(product.name),
                                  subtitle: Text(
                                    '${product.presentation ?? product.measurementUnit.value} - ${_currencyFormat.format(product.unitPrice)}',
                                    style: const TextStyle(fontSize: 12),
                                  ),
                                  onTap: () => onSelected(product),
                                );
                              },
                            ),
                          ),
                        ),
                      );
                    },
                  ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          // Presentación
          SizedBox(
            width: 100,
            child: DropdownButtonFormField<String>(
              initialValue: line.presentation,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                isDense: true,
              ),
              items: MeasurementUnit.values.map((unit) {
                return DropdownMenuItem(
                  value: unit.value,
                  child: Text(unit.value, style: const TextStyle(fontSize: 12)),
                );
              }).toList(),
              onChanged: line.isNewProduct
                  ? (value) {
                      if (value != null) {
                        _updateLine(line.tempId, 'presentation', value);
                      }
                    }
                  : null,
            ),
          ),
          const SizedBox(width: 8),
          // Cantidad
          SizedBox(
            width: 80,
            child: TextFormField(
              initialValue: line.cantidad.toString(),
              keyboardType: TextInputType.number,
              textAlign: TextAlign.right,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                isDense: true,
              ),
              onChanged: (value) {
                final cantidad = double.tryParse(value) ?? 0;
                _updateLine(line.tempId, 'cantidad', cantidad);
              },
            ),
          ),
          const SizedBox(width: 8),
          // Precio Unitario
          SizedBox(
            width: 100,
            child: TextFormField(
              initialValue: line.precioUnitario.toString(),
              keyboardType: TextInputType.number,
              textAlign: TextAlign.right,
              decoration: const InputDecoration(
                prefixText: '\$ ',
                border: OutlineInputBorder(),
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                isDense: true,
              ),
              onChanged: (value) {
                final precio = double.tryParse(value) ?? 0;
                _updateLine(line.tempId, 'precioUnitario', precio);
              },
            ),
          ),
          const SizedBox(width: 8),
          // IVA %
          SizedBox(
            width: 70,
            child: TextFormField(
              initialValue: line.ivaPercent.toString(),
              keyboardType: TextInputType.number,
              textAlign: TextAlign.right,
              decoration: const InputDecoration(
                suffixText: '%',
                border: OutlineInputBorder(),
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                isDense: true,
              ),
              onChanged: (value) {
                final iva = double.tryParse(value) ?? 13;
                _updateLine(line.tempId, 'ivaPercent', iva);
              },
            ),
          ),
          const SizedBox(width: 8),
          // Total + IVA
          SizedBox(
            width: 110,
            child: Container(
              padding: const EdgeInsets.symmetric(vertical: 8),
              alignment: Alignment.centerRight,
              child: Text(
                _currencyFormat.format(line.total),
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
            ),
          ),
          // Delete button
          SizedBox(
            width: 40,
            child: IconButton(
              icon: const Icon(Icons.delete, color: AppTheme.error),
              onPressed: () => _removeLine(line.tempId),
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(),
            ),
          ),
        ],
      ),
    );
  }

  /// Build footer totals (matches React's Footer Totals section)
  Widget _buildFooterTotals(({double subtotal, double iva, double total}) totals) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        SizedBox(
          width: 300,
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Subtotal:'),
                  Text(_currencyFormat.format(totals.subtotal)),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('IVA (por ítem):'),
                  Text(_currencyFormat.format(totals.iva)),
                ],
              ),
              const SizedBox(height: 8),
              const Divider(),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text(
                    'Total:',
                    style: TextStyle(fontWeight: FontWeight.bold),
                  ),
                  Text(
                    _currencyFormat.format(totals.total),
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      color: AppTheme.primary,
                      fontSize: 18,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }
}
