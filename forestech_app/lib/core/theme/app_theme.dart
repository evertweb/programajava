import 'package:flutter/material.dart';

class AppTheme {
  // Clinical Color Palette - Clean & Trustworthy
  static const Color primary = Color(0xFF009688);
  static const Color primaryLight = Color(0xFFB2DFDB);
  static const Color primaryDark = Color(0xFF00695C);

  static const Color secondary = Color(0xFF455A64);
  static const Color secondaryLight = Color(0xFFCFD8DC);
  static const Color secondaryDark = Color(0xFF263238);

  static const Color bgPrimary = Color(0xFFFFFFFF);
  static const Color bgSecondary = Color(0xFFF8F9FA);
  static const Color bgTertiary = Color(0xFFF1F3F4);

  static const Color error = Color(0xFFD32F2F);
  static const Color success = Color(0xFF388E3C);
  static const Color warning = Color(0xFFF57C00);
  static const Color info = Color(0xFF0288D1);

  static const Color textPrimary = Color(0xFF263238);
  static const Color textSecondary = Color(0xFF546E7A);
  static const Color textDisabled = Color(0xFF90A4AE);

  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.light(
        primary: primary,
        secondary: secondary,
        surface: bgPrimary,
        surfaceContainerHighest: bgSecondary,
        error: error,
        onPrimary: Colors.white,
        onSecondary: Colors.white,
        onSurface: textPrimary,
        onError: Colors.white,
      ),
      scaffoldBackgroundColor: bgSecondary,
      appBarTheme: const AppBarTheme(
        backgroundColor: primary,
        foregroundColor: Colors.white,
        elevation: 0,
        centerTitle: false,
      ),
      cardTheme: CardThemeData(
        color: bgPrimary,
        elevation: 1,
        margin: const EdgeInsets.all(8),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
      ),
      navigationRailTheme: const NavigationRailThemeData(
        backgroundColor: bgTertiary,
        selectedIconTheme: IconThemeData(color: primary),
        unselectedIconTheme: IconThemeData(color: textSecondary),
        selectedLabelTextStyle: TextStyle(color: primary, fontWeight: FontWeight.bold),
        unselectedLabelTextStyle: TextStyle(color: textSecondary),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: bgPrimary,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: const BorderSide(color: Color(0xFFE0E0E0)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: const BorderSide(color: Color(0xFFE0E0E0)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
          borderSide: const BorderSide(color: primary, width: 2),
        ),
      ),
    );
  }
}
