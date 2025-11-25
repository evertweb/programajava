import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:package_info_plus/package_info_plus.dart';

/// Información de una release disponible
class ReleaseInfo {
  final String version;
  final String windowsUrl;
  final String linuxUrl;
  final String releaseDate;
  final String releaseNotesUrl;

  ReleaseInfo({
    required this.version,
    required this.windowsUrl,
    required this.linuxUrl,
    required this.releaseDate,
    required this.releaseNotesUrl,
  });

  factory ReleaseInfo.fromJson(Map<String, dynamic> json) {
    return ReleaseInfo(
      version: json['version'] ?? '',
      windowsUrl: json['windows'] ?? '',
      linuxUrl: json['linux'] ?? '',
      releaseDate: json['releaseDate'] ?? '',
      releaseNotesUrl: json['releaseNotes'] ?? '',
    );
  }

  /// Obtiene la URL de descarga según la plataforma actual
  String get downloadUrl {
    if (Platform.isWindows) return windowsUrl;
    if (Platform.isLinux) return linuxUrl;
    return '';
  }
}

/// Resultado de la verificación de actualizaciones
class UpdateCheckResult {
  final bool updateAvailable;
  final ReleaseInfo? releaseInfo;
  final String? error;
  final String currentVersion;

  UpdateCheckResult({
    required this.updateAvailable,
    this.releaseInfo,
    this.error,
    required this.currentVersion,
  });
}

/// Servicio de auto-actualización para ForestechOil
/// 
/// Replica la funcionalidad de electron-updater:
/// - Verifica GitHub Releases para nuevas versiones
/// - Compara versiones semánticas
/// - Proporciona URLs de descarga por plataforma
class UpdateService {
  static const String _owner = 'evertweb';
  static const String _repo = 'programajava';
  static const String _latestJsonUrl = 
      'https://github.com/$_owner/$_repo/releases/latest/download/latest.json';
  
  final Dio _dio;
  
  UpdateService({Dio? dio}) : _dio = dio ?? Dio();

  /// Obtiene la versión actual de la aplicación
  Future<String> getCurrentVersion() async {
    try {
      final packageInfo = await PackageInfo.fromPlatform();
      return packageInfo.version;
    } catch (e) {
      // Fallback para desarrollo
      return '0.0.0';
    }
  }

  /// Verifica si hay actualizaciones disponibles
  /// 
  /// Retorna [UpdateCheckResult] con la información de la actualización
  Future<UpdateCheckResult> checkForUpdates() async {
    final currentVersion = await getCurrentVersion();
    
    try {
      print('[UpdateService] Checking for updates...');
      print('[UpdateService] Current version: $currentVersion');
      print('[UpdateService] Fetching: $_latestJsonUrl');
      
      final response = await _dio.get(
        _latestJsonUrl,
        options: Options(
          followRedirects: true,
          receiveTimeout: const Duration(seconds: 10),
          sendTimeout: const Duration(seconds: 10),
        ),
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data;
        
        if (response.data is String) {
          data = jsonDecode(response.data);
        } else {
          data = response.data;
        }
        
        final releaseInfo = ReleaseInfo.fromJson(data);
        final isNewer = _isNewerVersion(currentVersion, releaseInfo.version);
        
        print('[UpdateService] Latest version: ${releaseInfo.version}');
        print('[UpdateService] Update available: $isNewer');
        
        return UpdateCheckResult(
          updateAvailable: isNewer,
          releaseInfo: releaseInfo,
          currentVersion: currentVersion,
        );
      } else {
        return UpdateCheckResult(
          updateAvailable: false,
          error: 'HTTP ${response.statusCode}',
          currentVersion: currentVersion,
        );
      }
    } on DioException catch (e) {
      print('[UpdateService] Dio error: ${e.message}');
      return UpdateCheckResult(
        updateAvailable: false,
        error: e.message ?? 'Network error',
        currentVersion: currentVersion,
      );
    } catch (e) {
      print('[UpdateService] Error: $e');
      return UpdateCheckResult(
        updateAvailable: false,
        error: e.toString(),
        currentVersion: currentVersion,
      );
    }
  }

  /// Compara dos versiones semánticas
  /// Retorna true si [latest] es mayor que [current]
  bool _isNewerVersion(String current, String latest) {
    try {
      final currentParts = current.split('.').map(int.parse).toList();
      final latestParts = latest.split('.').map(int.parse).toList();
      
      // Asegurar que ambas listas tengan 3 elementos
      while (currentParts.length < 3) currentParts.add(0);
      while (latestParts.length < 3) latestParts.add(0);
      
      // Comparar major.minor.patch
      for (int i = 0; i < 3; i++) {
        if (latestParts[i] > currentParts[i]) return true;
        if (latestParts[i] < currentParts[i]) return false;
      }
      
      return false; // Son iguales
    } catch (e) {
      print('[UpdateService] Error parsing versions: $e');
      return false;
    }
  }

  /// Abre la URL de descarga en el navegador del sistema
  Future<bool> openDownloadUrl(String url) async {
    try {
      if (Platform.isLinux) {
        await Process.run('xdg-open', [url]);
        return true;
      } else if (Platform.isWindows) {
        await Process.run('start', [url], runInShell: true);
        return true;
      } else if (Platform.isMacOS) {
        await Process.run('open', [url]);
        return true;
      }
      return false;
    } catch (e) {
      print('[UpdateService] Error opening URL: $e');
      return false;
    }
  }

  /// Abre la página de releases en GitHub
  Future<bool> openReleasePage() async {
    const url = 'https://github.com/$_owner/$_repo/releases/latest';
    return openDownloadUrl(url);
  }
}
