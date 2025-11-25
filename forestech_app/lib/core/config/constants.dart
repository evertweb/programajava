/// Core application constants
class AppConstants {
  // API Configuration
  static const String baseApiUrl = 'http://localhost:8080/api';
  
  // Timeout Configuration
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  static const Duration sendTimeout = Duration(seconds: 30);
  
  // App Information
  static const String appName = 'ForestechOil';
  static const String appVersion = '1.0.0';
  
  // Prevent instantiation
  AppConstants._();
}
