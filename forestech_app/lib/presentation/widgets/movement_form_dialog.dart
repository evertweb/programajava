import 'package:flutter/material.dart';
import '../../core/theme/app_theme.dart';
import '../../data/models/movement_model.dart';
import '../../data/repositories/product_repository_impl.dart';
import '../../data/repositories/vehicle_repository_impl.dart';
import '../../domain/entities/movement_entity.dart';
import '../../domain/entities/product_entity.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../providers/movement_provider.dart';

/// MovementFormDialog - Dialog for creating movements
/// Matches React's MovementDialog component
/// NOTE: Only SALIDA mode is used as per business logic
/// ENTRADA movements are created automatically via invoice registration
class MovementFormDialog extends StatefulWidget {
  final MovementProvider provider;
  final MovementType mode;

  const MovementFormDialog({
    super.key,
    required this.provider,
    required this.mode,
  });

  @override
  State<MovementFormDialog> createState() => _MovementFormDialogState();
}

class _MovementFormDialogState extends State<MovementFormDialog> {
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;
  bool _isSubmitting = false;
  String? _error;

  // Form data matching React's MovementFormData
  ProductEntity? _selectedProduct;
  VehicleEntity? _selectedVehicle;
  double _quantity = 0;
  double _unitPrice = 0;
  String _description = '';

  // Data lists
  List<ProductEntity> _products = [];
  List<VehicleEntity> _vehicles = [];

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  /// Load products and vehicles (matches React's loadData)
  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final productRepo = ProductRepositoryImpl();
      final vehicleRepo = VehicleRepositoryImpl();

      // Parallel fetch matching React's Promise.all
      final results = await Future.wait([
        productRepo.getAll(),
        // Only load vehicles for SALIDA mode
        widget.mode == MovementType.salida
            ? vehicleRepo.getAll()
            : Future.value((const <VehicleEntity>[], null)),
      ]);

      final (products, productFailure) =
          results[0] as (List<ProductEntity>?, dynamic);
      final (vehicles, vehicleFailure) =
          results[1] as (List<VehicleEntity>?, dynamic);

      if (productFailure != null || vehicleFailure != null) {
        setState(() {
          _error = 'Error al cargar datos necesarios';
          _isLoading = false;
        });
        return;
      }

      setState(() {
        _products = products ?? [];
        _vehicles = vehicles ?? [];
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Error al cargar datos necesarios';
        _isLoading = false;
      });
    }
  }

  /// Handle product selection (matches React's handleProductChange)
  void _handleProductChange(ProductEntity? product) {
    setState(() {
      _selectedProduct = product;
      if (product != null) {
        _unitPrice = product.unitPrice; // Auto-fill price
      } else {
        _unitPrice = 0;
      }
    });
  }

  /// Handle vehicle selection (matches React's handleVehicleChange)
  void _handleVehicleChange(VehicleEntity? vehicle) {
    setState(() {
      _selectedVehicle = vehicle;
    });
  }

  /// Validate form (matches React's validation)
  bool _validate() {
    if (_selectedProduct == null) {
      setState(() => _error = 'El producto es requerido');
      return false;
    }
    if (_quantity <= 0) {
      setState(() => _error = 'La cantidad debe ser mayor a 0');
      return false;
    }
    if (widget.mode == MovementType.salida && _selectedVehicle == null) {
      setState(() => _error = 'El vehículo es requerido para salidas');
      return false;
    }
    return true;
  }

  /// Handle form submission (matches React's handleSubmit)
  Future<void> _handleSubmit() async {
    if (!_validate()) return;
    if (!(_formKey.currentState?.validate() ?? false)) return;

    setState(() {
      _isSubmitting = true;
      _error = null;
    });

    final movement = MovementEntity(
      id: '',
      movementType: widget.mode,
      productId: _selectedProduct!.id,
      vehicleId: widget.mode == MovementType.salida
          ? _selectedVehicle?.id
          : null,
      quantity: _quantity,
      unitPrice: _unitPrice,
      subtotal: _quantity * _unitPrice,
      description: _description,
      createdAt: DateTime.now(),
    );

    final success = await widget.provider.createMovement(movement);

    if (mounted) {
      setState(() => _isSubmitting = false);

      if (success) {
        Navigator.pop(context);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              widget.mode == MovementType.entrada
                  ? 'Entrada registrada correctamente'
                  : 'Salida registrada correctamente',
            ),
            backgroundColor: AppTheme.success,
          ),
        );
        // Reload all data
        widget.provider.loadAllData();
      } else {
        setState(() {
          _error = widget.provider.errorMessage ?? 'Error al registrar el movimiento';
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      child: Container(
        width: 500,
        padding: const EdgeInsets.all(24),
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Title
              Text(
                widget.mode == MovementType.entrada
                    ? 'Registrar Entrada'
                    : 'Registrar Salida',
                style: const TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 24),

              // Error alert
              if (_error != null)
                Container(
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
                ),

              // Loading state
              if (_isLoading)
                const Padding(
                  padding: EdgeInsets.all(32),
                  child: Center(child: CircularProgressIndicator()),
                )
              else ...[
                // Product Autocomplete
                Autocomplete<ProductEntity>(
                  optionsBuilder: (textEditingValue) {
                    if (textEditingValue.text.isEmpty) {
                      return _products;
                    }
                    return _products.where((product) =>
                        product.name
                            .toLowerCase()
                            .contains(textEditingValue.text.toLowerCase()));
                  },
                  displayStringForOption: (product) => product.name,
                  onSelected: _handleProductChange,
                  fieldViewBuilder: (context, controller, focusNode, onSubmitted) {
                    return TextFormField(
                      controller: controller,
                      focusNode: focusNode,
                      decoration: const InputDecoration(
                        labelText: 'Producto *',
                        prefixIcon: Icon(Icons.local_gas_station),
                      ),
                      validator: (value) {
                        if (_selectedProduct == null) {
                          return 'Seleccione un producto';
                        }
                        return null;
                      },
                    );
                  },
                ),
                const SizedBox(height: 16),

                // Vehicle Autocomplete (only for SALIDA)
                if (widget.mode == MovementType.salida) ...[
                  Autocomplete<VehicleEntity>(
                    optionsBuilder: (textEditingValue) {
                      if (textEditingValue.text.isEmpty) {
                        return _vehicles;
                      }
                      final query = textEditingValue.text.toLowerCase();
                      return _vehicles.where((vehicle) =>
                          vehicle.placa.toLowerCase().contains(query) ||
                          vehicle.marca.toLowerCase().contains(query) ||
                          vehicle.modelo.toLowerCase().contains(query));
                    },
                    displayStringForOption: (vehicle) =>
                        '${vehicle.placa} - ${vehicle.marca} ${vehicle.modelo}',
                    onSelected: _handleVehicleChange,
                    fieldViewBuilder:
                        (context, controller, focusNode, onSubmitted) {
                      return TextFormField(
                        controller: controller,
                        focusNode: focusNode,
                        decoration: const InputDecoration(
                          labelText: 'Vehículo *',
                          prefixIcon: Icon(Icons.local_shipping),
                        ),
                        validator: (value) {
                          if (_selectedVehicle == null) {
                            return 'Seleccione un vehículo';
                          }
                          return null;
                        },
                      );
                    },
                  ),
                  const SizedBox(height: 16),
                ],

                // Quantity and Unit Price row
                Row(
                  children: [
                    Expanded(
                      child: TextFormField(
                        decoration: InputDecoration(
                          labelText: 'Cantidad *',
                          suffixText: _selectedProduct?.measurementUnit.value,
                        ),
                        keyboardType: const TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        initialValue: _quantity > 0 ? _quantity.toString() : '',
                        onChanged: (value) {
                          setState(() {
                            _quantity = double.tryParse(value) ?? 0;
                          });
                        },
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Ingrese cantidad';
                          }
                          final qty = double.tryParse(value);
                          if (qty == null || qty <= 0) {
                            return 'Cantidad inválida';
                          }
                          return null;
                        },
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: TextFormField(
                        decoration: const InputDecoration(
                          labelText: 'Precio Unitario',
                          prefixText: '\$ ',
                          helperText: 'Precio base del producto',
                        ),
                        keyboardType: TextInputType.number,
                        readOnly: true, // Price comes from product
                        controller: TextEditingController(
                          text: _unitPrice.toStringAsFixed(0),
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),

                // Description
                TextFormField(
                  decoration: const InputDecoration(
                    labelText: 'Descripción / Notas',
                    alignLabelWithHint: true,
                  ),
                  maxLines: 3,
                  onChanged: (value) {
                    _description = value;
                  },
                ),
              ],

              const SizedBox(height: 24),

              // Action buttons
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: _isSubmitting ? null : () => Navigator.pop(context),
                    child: const Text('Cancelar'),
                  ),
                  const SizedBox(width: 12),
                  ElevatedButton(
                    onPressed: _isSubmitting || _isLoading ? null : _handleSubmit,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: widget.mode == MovementType.entrada
                          ? AppTheme.primary
                          : AppTheme.warning,
                      foregroundColor: Colors.white,
                    ),
                    child: _isSubmitting
                        ? const SizedBox(
                            width: 20,
                            height: 20,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.white,
                            ),
                          )
                        : Text(
                            widget.mode == MovementType.entrada
                                ? 'Registrar Entrada'
                                : 'Registrar Salida',
                          ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
