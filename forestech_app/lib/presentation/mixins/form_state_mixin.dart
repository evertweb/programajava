import 'package:flutter/material.dart';

/// Mixin for managing form state consistently
/// Provides common form state management functionality
mixin FormStateMixin<T extends StatefulWidget> on State<T> {
  bool _isLoading = false;
  bool _hasChanges = false;

  /// Whether the form is currently submitting
  bool get isLoading => _isLoading;

  /// Whether the form has unsaved changes
  bool get hasChanges => _hasChanges;

  /// Set loading state
  @protected
  void setLoading(bool value) {
    setState(() {
      _isLoading = value;
    });
  }

  /// Mark form as changed
  @protected
  void markAsChanged() {
    if (!_hasChanges) {
      setState(() {
        _hasChanges = true;
      });
    }
  }

  /// Reset form state
  @protected
  void resetFormState() {
    setState(() {
      _isLoading = false;
      _hasChanges = false;
    });
  }

  /// Show unsaved changes warning if there are changes
  /// Returns true if user wants to proceed, false otherwise
  Future<bool> showUnsavedChangesWarning() async {
    if (!_hasChanges) return true;

    final shouldProceed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Cambios sin guardar'),
        content: const Text(
          '¿Está seguro que desea salir? Los cambios no guardados se perderán.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Quedarme'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Salir sin guardar'),
          ),
        ],
      ),
    );

    return shouldProceed ?? false;
  }
}
