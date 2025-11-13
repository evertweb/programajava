package com.forestech;

public class BannerMenu {
    public static void header() {

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          🌲 BIENVENIDO A FORESTECH CLI 🌲                   ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│  📋 Proyecto: " + AppConfig.PROJECT_NAME                   );
        System.out.println("│  🔖 Versión: " + AppConfig.VERSION                          );
        System.out.println("│  📅 Año: " + AppConfig.CURRENT_YEAR                         );
        System.out.println("│  💾 Base de datos: " + AppConfig.DATABASE                   );
        System.out.println("│  " + (AppConfig.ACTIVE ? "✅ Estado: Activo" : "❌ Estado: Inactivo"));
        System.out.println("└───────────────────────────────────────────────────────────┘\n");

    }
}
