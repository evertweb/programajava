import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class MainLayout extends StatelessWidget {
  final Widget child;
  final String location;

  const MainLayout({
    super.key,
    required this.child,
    required this.location,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Forestech Oil'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              context.go('/login');
            },
          ),
        ],
      ),
      body: Row(
        children: [
          NavigationRail(
            selectedIndex: _getSelectedIndex(location),
            onDestinationSelected: (int index) {
              _onDestinationSelected(context, index);
            },
            labelType: NavigationRailLabelType.all,
            destinations: const [
              NavigationRailDestination(
                icon: Icon(Icons.dashboard),
                label: Text('Dashboard'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.inventory),
                label: Text('Productos'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.local_shipping),
                label: Text('Flota'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.swap_horiz),
                label: Text('Movimientos'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.people),
                label: Text('Proveedores'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.receipt),
                label: Text('Facturación'),
              ),
            ],
          ),
          const VerticalDivider(thickness: 1, width: 1),
          Expanded(child: child),
        ],
      ),
    );
  }

  int _getSelectedIndex(String location) {
    if (location.startsWith('/dashboard')) return 0;
    if (location.startsWith('/products')) return 1;
    if (location.startsWith('/vehicles')) return 2;
    if (location.startsWith('/movements')) return 3;
    if (location.startsWith('/suppliers')) return 4;
    if (location.startsWith('/invoices')) return 5;
    return 0;
  }

  void _onDestinationSelected(BuildContext context, int index) {
    switch (index) {
      case 0:
        context.go('/dashboard');
        break;
      case 1:
        context.go('/products');
        break;
      case 2:
        context.go('/vehicles');
        break;
      case 3:
        context.go('/movements');
        break;
      case 4:
        context.go('/suppliers');
        break;
      case 5:
        context.go('/invoices');
        break;
    }
  }
}
