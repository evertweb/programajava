import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/update_provider.dart';

/// Diálogo de actualización disponible
/// 
/// Replica la funcionalidad del diálogo de electron-updater
class UpdateDialog extends StatelessWidget {
  const UpdateDialog({super.key});

  static Future<void> show(BuildContext context) {
    return showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const UpdateDialog(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<UpdateProvider>(
      builder: (context, updateProvider, _) {
        final releaseInfo = updateProvider.releaseInfo;
        
        return AlertDialog(
          icon: const Icon(
            Icons.system_update,
            size: 48,
            color: Colors.green,
          ),
          title: const Text('Actualización Disponible'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Hay una nueva versión disponible: ${releaseInfo?.version ?? ""}',
                style: Theme.of(context).textTheme.bodyLarge,
              ),
              const SizedBox(height: 12),
              Text(
                'Versión actual: ${updateProvider.currentVersion}',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Colors.grey[600],
                ),
              ),
              if (releaseInfo?.releaseDate.isNotEmpty ?? false) ...[
                const SizedBox(height: 8),
                Text(
                  'Fecha: ${_formatDate(releaseInfo!.releaseDate)}',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Colors.grey[500],
                  ),
                ),
              ],
              const SizedBox(height: 16),
              const Text(
                '¿Desea descargar la actualización ahora?',
                style: TextStyle(fontWeight: FontWeight.w500),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () {
                updateProvider.dismissUpdate();
                Navigator.of(context).pop();
              },
              child: const Text('Más Tarde'),
            ),
            TextButton(
              onPressed: () async {
                await updateProvider.openReleasePage();
                if (context.mounted) {
                  Navigator.of(context).pop();
                }
              },
              child: const Text('Ver Notas'),
            ),
            FilledButton.icon(
              onPressed: () async {
                final success = await updateProvider.downloadUpdate();
                if (context.mounted) {
                  Navigator.of(context).pop();
                  if (success) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Descarga iniciada en el navegador'),
                        backgroundColor: Colors.green,
                      ),
                    );
                  }
                }
              },
              icon: const Icon(Icons.download),
              label: const Text('Descargar Ahora'),
            ),
          ],
        );
      },
    );
  }

  String _formatDate(String isoDate) {
    try {
      final date = DateTime.parse(isoDate);
      return '${date.day}/${date.month}/${date.year}';
    } catch (e) {
      return isoDate;
    }
  }
}

/// Banner de actualización para mostrar en la parte superior de la app
class UpdateBanner extends StatelessWidget {
  const UpdateBanner({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<UpdateProvider>(
      builder: (context, updateProvider, _) {
        if (!updateProvider.shouldShowUpdateDialog) {
          return const SizedBox.shrink();
        }

        return MaterialBanner(
          padding: const EdgeInsets.all(12),
          leading: const Icon(Icons.system_update, color: Colors.green),
          content: Text(
            'Nueva versión disponible: ${updateProvider.releaseInfo?.version}',
          ),
          actions: [
            TextButton(
              onPressed: () => updateProvider.dismissUpdate(),
              child: const Text('Ignorar'),
            ),
            FilledButton(
              onPressed: () => UpdateDialog.show(context),
              child: const Text('Actualizar'),
            ),
          ],
        );
      },
    );
  }
}

/// Mixin para agregar verificación automática de actualizaciones
/// 
/// Uso en un StatefulWidget:
/// ```dart
/// class _MyHomePageState extends State<MyHomePage> with UpdateCheckerMixin {
///   @override
///   void initState() {
///     super.initState();
///     checkForUpdatesOnStart(context);
///   }
/// }
/// ```
mixin UpdateCheckerMixin<T extends StatefulWidget> on State<T> {
  /// Verifica actualizaciones 3 segundos después de iniciar
  void checkForUpdatesOnStart(BuildContext context, {int delaySeconds = 3}) {
    Future.delayed(Duration(seconds: delaySeconds), () {
      if (!mounted) return;
      
      final updateProvider = Provider.of<UpdateProvider>(context, listen: false);
      updateProvider.checkForUpdates().then((_) {
        if (!mounted) return;
        
        if (updateProvider.shouldShowUpdateDialog) {
          UpdateDialog.show(context);
        }
      });
    });
  }
}
