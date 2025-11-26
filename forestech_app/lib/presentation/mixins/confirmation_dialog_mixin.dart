import 'package:flutter/material.dart';

/// Mixin for showing confirmation dialogs consistently
/// Provides standardized delete and action confirmations
mixin ConfirmationDialogMixin<T extends StatefulWidget> on State<T> {
  /// Show delete confirmation dialog
  /// Returns true if confirmed, false if cancelled
  Future<bool> showDeleteConfirmation({
    required String itemName,
    String? customMessage,
  }) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirmar eliminación'),
        content: Text(
          customMessage ?? 
          '¿Está seguro que desea eliminar "$itemName"? Esta acción no se puede deshacer.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
              foregroundColor: Colors.white,
            ),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );

    return confirmed ?? false;
  }

  /// Show generic confirmation dialog
  /// Returns true if confirmed, false if cancelled
  Future<bool> showConfirmation({
    required String title,
    required String message,
    String confirmText = 'Confirmar',
    String cancelText = 'Cancelar',
    Color? confirmColor,
  }) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: Text(cancelText),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: confirmColor != null
                ? ElevatedButton.styleFrom(
                    backgroundColor: confirmColor,
                    foregroundColor: Colors.white,
                  )
                : null,
            child: Text(confirmText),
          ),
        ],
      ),
    );

    return confirmed ?? false;
  }
}
