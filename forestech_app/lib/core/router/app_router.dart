import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../presentation/screens/login_screen.dart';
import '../../presentation/screens/dashboard_screen.dart';
import '../../presentation/screens/products_screen.dart';
import '../../presentation/screens/vehicles_screen.dart';
import '../../presentation/screens/movements_screen.dart';
import '../../presentation/screens/suppliers_screen.dart';
import '../../presentation/screens/invoicing_screen.dart';
import '../../presentation/widgets/main_layout.dart';

final GlobalKey<NavigatorState> _rootNavigatorKey = GlobalKey<NavigatorState>();
final GlobalKey<NavigatorState> _shellNavigatorKey = GlobalKey<NavigatorState>();

final appRouter = GoRouter(
  navigatorKey: _rootNavigatorKey,
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const LoginScreen(),
    ),
    ShellRoute(
      navigatorKey: _shellNavigatorKey,
      builder: (context, state, child) {
        return MainLayout(location: state.uri.toString(), child: child);
      },
      routes: [
        GoRoute(
          path: '/dashboard',
          builder: (context, state) => const DashboardScreen(),
        ),
        GoRoute(
          path: '/products',
          builder: (context, state) => const ProductsScreen(),
        ),
        GoRoute(
          path: '/vehicles',
          builder: (context, state) => const VehiclesScreen(),
        ),
        GoRoute(
          path: '/movements',
          builder: (context, state) => const MovementsScreen(),
        ),
        GoRoute(
          path: '/suppliers',
          builder: (context, state) => const SuppliersScreen(),
        ),
        GoRoute(
          path: '/invoices',
          builder: (context, state) => const InvoicingScreen(),
        ),
      ],
    ),
  ],
);
