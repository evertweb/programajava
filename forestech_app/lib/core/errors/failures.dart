import 'package:equatable/equatable.dart';

/// Base class for all failures in the application
/// Using Equatable for value equality comparison
abstract class Failure extends Equatable {
  final String message;
  final int? statusCode;

  const Failure({
    required this.message,
    this.statusCode,
  });

  @override
  List<Object?> get props => [message, statusCode];
}

/// Failure when there's a server error
class ServerFailure extends Failure {
  const ServerFailure({
    required super.message,
    super.statusCode,
  });
}

/// Failure when there's a network/connection error
class NetworkFailure extends Failure {
  const NetworkFailure({
    required super.message,
  }) : super(statusCode: null);
}

/// Failure when there's a cache error
class CacheFailure extends Failure {
  const CacheFailure({
    required super.message,
  }) : super(statusCode: null);
}

/// Failure when there's a validation error
class ValidationFailure extends Failure {
  const ValidationFailure({
    required super.message,
  }) : super(statusCode: 400);
}

/// Failure when user is not authenticated
class AuthenticationFailure extends Failure {
  const AuthenticationFailure({
    required super.message,
  }) : super(statusCode: 401);
}

/// Failure when user is not authorized
class AuthorizationFailure extends Failure {
  const AuthorizationFailure({
    required super.message,
  }) : super(statusCode: 403);
}

/// Failure when a resource is not found
class NotFoundFailure extends Failure {
  const NotFoundFailure({
    required super.message,
  }) : super(statusCode: 404);
}
