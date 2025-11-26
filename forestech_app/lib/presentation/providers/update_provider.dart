import 'package:flutter/foundation.dart';
import '../../core/services/update_service.dart';

/// Estado de la verificación de actualizaciones
enum UpdateStatus {
  idle,
  checking,
  available,
  notAvailable,
  error,
}

/// Provider para manejar el estado de actualizaciones
/// 
/// Uso:
/// ```dart
/// final updateProvider = Provider.of<UpdateProvider>(context);
/// updateProvider.checkForUpdates();
/// if (updateProvider.status == UpdateStatus.available) {
///   // Mostrar diálogo
/// }
/// ```
class UpdateProvider extends ChangeNotifier {
  final UpdateService _updateService;
  
  UpdateStatus _status = UpdateStatus.idle;
  UpdateCheckResult? _result;
  bool _dismissed = false;
  
  UpdateProvider({UpdateService? updateService}) 
      : _updateService = updateService ?? UpdateService();
  
  // Getters
  UpdateStatus get status => _status;
  UpdateCheckResult? get result => _result;
  bool get isDismissed => _dismissed;
  String get currentVersion => _result?.currentVersion ?? '0.0.0';
  ReleaseInfo? get releaseInfo => _result?.releaseInfo;
  String? get error => _result?.error;
  
  /// Indica si hay una actualización disponible y no fue descartada
  bool get shouldShowUpdateDialog => 
      _status == UpdateStatus.available && !_dismissed;

  /// Verifica si hay actualizaciones disponibles
  Future<void> checkForUpdates() async {
    if (_status == UpdateStatus.checking) return;
    
    _status = UpdateStatus.checking;
    _dismissed = false;
    notifyListeners();
    
    try {
      _result = await _updateService.checkForUpdates();
      
      if (_result!.error != null) {
        _status = UpdateStatus.error;
      } else if (_result!.updateAvailable) {
        _status = UpdateStatus.available;
      } else {
        _status = UpdateStatus.notAvailable;
      }
    } catch (e) {
      _status = UpdateStatus.error;
      debugPrint('[UpdateProvider] Error: $e');
    }
    
    notifyListeners();
  }

  /// Descarta la notificación de actualización (hasta próximo check)
  void dismissUpdate() {
    _dismissed = true;
    notifyListeners();
  }

  /// Abre la URL de descarga para la plataforma actual
  Future<bool> downloadUpdate() async {
    if (_result?.releaseInfo == null) return false;
    
    final url = _result!.releaseInfo!.downloadUrl;
    if (url.isEmpty) return false;
    
    return await _updateService.openDownloadUrl(url);
  }

  /// Abre la página de releases en GitHub
  Future<bool> openReleasePage() async {
    return await _updateService.openReleasePage();
  }

  /// Resetea el estado para permitir nueva verificación
  void reset() {
    _status = UpdateStatus.idle;
    _result = null;
    _dismissed = false;
    notifyListeners();
  }
}
