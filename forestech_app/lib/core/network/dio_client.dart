import 'package:dio/dio.dart';
import '../config/constants.dart';
import '../errors/failures.dart';

/// DioClient is a singleton class that configures and provides
/// a Dio instance for making HTTP requests throughout the application
class DioClient {
  static final DioClient _instance = DioClient._internal();
  late final Dio _dio;

  factory DioClient() {
    return _instance;
  }

  DioClient._internal() {
    _dio = Dio(
      BaseOptions(
        baseUrl: AppConstants.baseApiUrl,
        connectTimeout: AppConstants.connectTimeout,
        receiveTimeout: AppConstants.receiveTimeout,
        sendTimeout: AppConstants.sendTimeout,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // Add interceptors for logging and error handling
    _dio.interceptors.add(
      LogInterceptor(
        requestBody: true,
        responseBody: true,
        error: true,
        requestHeader: true,
        responseHeader: false,
      ),
    );

    _dio.interceptors.add(
      InterceptorsWrapper(
        onError: (DioException error, ErrorInterceptorHandler handler) {
          // You can add custom error handling here
          // For example, refresh token logic
          handler.next(error);
        },
      ),
    );
  }

  /// Get the Dio instance
  Dio get dio => _dio;

  /// Set authentication token
  void setAuthToken(String token) {
    _dio.options.headers['Authorization'] = 'Bearer $token';
  }

  /// Remove authentication token
  void removeAuthToken() {
    _dio.options.headers.remove('Authorization');
  }

  /// Handle Dio errors and convert them to Failures
  Failure handleError(DioException error) {
    switch (error.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return const NetworkFailure(
          message: 'Connection timeout. Please check your internet connection.',
        );

      case DioExceptionType.badResponse:
        final statusCode = error.response?.statusCode;
        final message = error.response?.data['message'] ?? 
                       error.response?.statusMessage ?? 
                       'An error occurred';

        switch (statusCode) {
          case 400:
            return ValidationFailure(message: message);
          case 401:
            return AuthenticationFailure(message: message);
          case 403:
            return AuthorizationFailure(message: message);
          case 404:
            return NotFoundFailure(message: message);
          case 500:
          default:
            return ServerFailure(
              message: message,
              statusCode: statusCode,
            );
        }

      case DioExceptionType.cancel:
        return const NetworkFailure(message: 'Request cancelled');

      case DioExceptionType.connectionError:
        return const NetworkFailure(
          message: 'No internet connection. Please check your network.',
        );

      case DioExceptionType.badCertificate:
        return const NetworkFailure(
          message: 'Certificate verification failed',
        );

      case DioExceptionType.unknown:
        return NetworkFailure(
          message: error.message ?? 'An unexpected error occurred',
        );
    }
  }
}
