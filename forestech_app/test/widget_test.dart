// This is a basic Flutter widget test.

import 'package:flutter_test/flutter_test.dart';

import 'package:forestech_app/main.dart';

void main() {
  testWidgets('App loads correctly', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const ForestechApp());

    // Verify that our app title is present.
    expect(find.text('ForestechOil'), findsOneWidget);
    expect(find.text('Fase 1: Arquitectura Completada ✓'), findsOneWidget);
  });
}
