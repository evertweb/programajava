import 'package:flutter/material.dart';
import '../../../core/theme/app_theme.dart';

/// Reusable Error State Widget
/// Displays consistent error messages across the app
class ErrorStateWidget extends StatelessWidget {
  final String? errorMessage;
  final VoidCallback? onRetry;
  final String retryButtonText;

  const ErrorStateWidget({
    super.key,
    this.errorMessage,
    this.onRetry,
    this.retryButtonText = 'Reintentar',
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.error_outline,
            size: 48,
            color: AppTheme.error,
          ),
          const SizedBox(height: 16),
          Text(
            errorMessage ?? 'Ha ocurrido un error',
            style: const TextStyle(
              color: AppTheme.error,
              fontSize: 16,
            ),
            textAlign: TextAlign.center,
          ),
          if (onRetry != null) ...[
            const SizedBox(height: 16),
            ElevatedButton.icon(
              onPressed: onRetry,
              icon: const Icon(Icons.refresh),
              label: Text(retryButtonText),
            ),
          ],
        ],
      ),
    );
  }
}
