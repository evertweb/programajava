import 'package:flutter/material.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../providers/vehicle_provider.dart';

/// Categories matching React's CATEGORIES constant
const List<String> vehicleCategories = [
  'Camión Cisterna',
  'Camioneta',
  'Automóvil',
  'Motocicleta',
  'Maquinaria Pesada',
  'Otro',
];

/// VehicleFormDialog - Dialog for creating and editing vehicles
/// Matches React's VehicleDialog component
class VehicleFormDialog extends StatefulWidget {
  final VehicleEntity? vehicle;
  final VehicleProvider provider;

  const VehicleFormDialog({
    super.key,
    this.vehicle,
    required this.provider,
  });

  @override
  State<VehicleFormDialog> createState() => _VehicleFormDialogState();
}

class _VehicleFormDialogState extends State<VehicleFormDialog> {
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;

  // Form fields matching React's VehicleFormData
  late TextEditingController _placaController;
  late TextEditingController _marcaController;
  late TextEditingController _modeloController;
  late TextEditingController _anioController;
  late TextEditingController _descripcionController;
  String _category = '';
  bool _isActive = true;

  bool get isEditing => widget.vehicle != null;

  @override
  void initState() {
    super.initState();
    _initializeControllers();
  }

  void _initializeControllers() {
    final vehicle = widget.vehicle;
    _placaController = TextEditingController(text: vehicle?.placa ?? '');
    _marcaController = TextEditingController(text: vehicle?.marca ?? '');
    _modeloController = TextEditingController(text: vehicle?.modelo ?? '');
    _anioController = TextEditingController(
      text: (vehicle?.anio ?? DateTime.now().year).toString(),
    );
    _descripcionController = TextEditingController(
      text: vehicle?.descripcion ?? '',
    );
    _category = vehicle?.category ?? '';
    _isActive = vehicle?.isActive ?? true;
  }

  @override
  void dispose() {
    _placaController.dispose();
    _marcaController.dispose();
    _modeloController.dispose();
    _anioController.dispose();
    _descripcionController.dispose();
    super.dispose();
  }

  /// Validate the form (matches React's validate function)
  bool _validate() {
    return _formKey.currentState?.validate() ?? false;
  }

  /// Handle form submission (matches React's handleSubmit)
  Future<void> _handleSubmit() async {
    if (!_validate()) return;

    setState(() => _isLoading = true);

    final vehicleData = VehicleEntity(
      id: widget.vehicle?.id ?? '',
      placa: _placaController.text.trim(),
      marca: _marcaController.text.trim(),
      modelo: _modeloController.text.trim(),
      anio: int.tryParse(_anioController.text) ?? DateTime.now().year,
      category: _category,
      descripcion: _descripcionController.text.trim(),
      isActive: _isActive,
    );

    bool success;
    if (isEditing) {
      success = await widget.provider.updateVehicle(
        widget.vehicle!.id,
        vehicleData,
      );
    } else {
      success = await widget.provider.createVehicle(vehicleData);
    }

    setState(() => _isLoading = false);

    if (success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            isEditing
                ? 'Vehículo actualizado exitosamente'
                : 'Vehículo creado exitosamente',
          ),
          backgroundColor: Colors.green,
        ),
      );
      Navigator.of(context).pop(true);
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            widget.provider.errorMessage ?? 'Error al guardar vehículo',
          ),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 600),
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Title
                Text(
                  isEditing ? 'Editar Vehículo' : 'Nuevo Vehículo',
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                ),
                const SizedBox(height: 24),

                // Form Fields
                _buildFormFields(),

                const SizedBox(height: 24),

                // Actions
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    TextButton(
                      onPressed: _isLoading ? null : () => Navigator.pop(context),
                      child: const Text('Cancelar'),
                    ),
                    const SizedBox(width: 12),
                    ElevatedButton(
                      onPressed: _isLoading ? null : _handleSubmit,
                      child: _isLoading
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                                color: Colors.white,
                              ),
                            )
                          : Text(isEditing ? 'Guardar' : 'Crear'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildFormFields() {
    return SingleChildScrollView(
      child: Column(
        children: [
          // Row 1: Placa + Categoría
          Row(
            children: [
              Expanded(
                child: TextFormField(
                  controller: _placaController,
                  decoration: const InputDecoration(
                    labelText: 'Placa',
                    border: OutlineInputBorder(),
                  ),
                  enabled: !_isLoading,
                  validator: (value) {
                    if (value == null || value.trim().isEmpty) {
                      return 'La placa es requerida';
                    }
                    return null;
                  },
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: DropdownButtonFormField<String>(
                  initialValue: _category.isEmpty ? null : _category,
                  decoration: const InputDecoration(
                    labelText: 'Categoría',
                    border: OutlineInputBorder(),
                  ),
                  items: vehicleCategories
                      .map((cat) => DropdownMenuItem(
                            value: cat,
                            child: Text(cat),
                          ))
                      .toList(),
                  onChanged: _isLoading
                      ? null
                      : (value) {
                          setState(() => _category = value ?? '');
                        },
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'La categoría es requerida';
                    }
                    return null;
                  },
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),

          // Row 2: Marca + Modelo
          Row(
            children: [
              Expanded(
                child: TextFormField(
                  controller: _marcaController,
                  decoration: const InputDecoration(
                    labelText: 'Marca',
                    border: OutlineInputBorder(),
                  ),
                  enabled: !_isLoading,
                  validator: (value) {
                    if (value == null || value.trim().isEmpty) {
                      return 'La marca es requerida';
                    }
                    return null;
                  },
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: TextFormField(
                  controller: _modeloController,
                  decoration: const InputDecoration(
                    labelText: 'Modelo',
                    border: OutlineInputBorder(),
                  ),
                  enabled: !_isLoading,
                  validator: (value) {
                    if (value == null || value.trim().isEmpty) {
                      return 'El modelo es requerido';
                    }
                    return null;
                  },
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),

          // Row 3: Año + Activo
          Row(
            children: [
              Expanded(
                child: TextFormField(
                  controller: _anioController,
                  decoration: const InputDecoration(
                    labelText: 'Año',
                    border: OutlineInputBorder(),
                  ),
                  keyboardType: TextInputType.number,
                  enabled: !_isLoading,
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'El año es requerido';
                    }
                    final year = int.tryParse(value);
                    if (year == null || year < 1900 || year > DateTime.now().year + 1) {
                      return 'Año inválido';
                    }
                    return null;
                  },
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: SwitchListTile(
                  title: const Text('Activo'),
                  value: _isActive,
                  onChanged: _isLoading
                      ? null
                      : (value) {
                          setState(() => _isActive = value);
                        },
                  contentPadding: EdgeInsets.zero,
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),

          // Row 4: Descripción
          TextFormField(
            controller: _descripcionController,
            decoration: const InputDecoration(
              labelText: 'Descripción',
              border: OutlineInputBorder(),
            ),
            maxLines: 3,
            enabled: !_isLoading,
          ),
        ],
      ),
    );
  }
}
