package com.forestech;

import javax.naming.BinaryRefAddr;

public class MenuHelper {
    public static void displayMenu() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    📋 MENÚ PRINCIPAL                      ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. ⬇️  INGRESO DE COMBUSTIBLE                            ║");
        System.out.println("║  2. ⬆️  SALIDA DE COMBUSTIBLE                             ║");
        System.out.println("║  3. 📊 VER INVENTARIO                                     ║");
        System.out.println("║  4. 🚪 SALIR                                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");

    }

    public static void proccessMenuOption(int option) {

        switch (option) {
            case 1:
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ⬇️  MÓDULO DE ENTRADA DE COMBUSTIBLE SELECCIONADO       │");
                System.out.println("└───────────────────────────────────────────────────────────┘");
                break;
            case 2:
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ⬆️  MÓDULO SALIDA DE COMBUSTIBLE SELECCIONADO           │");
                System.out.println("└───────────────────────────────────────────────────────────┘");
                break;
            case 3:
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  📊 MÓDULO DE INVENTARIO SELECCIONADO                     │");
                System.out.println("└───────────────────────────────────────────────────────────┘");
                break;
            case 4:
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  🚪 SALIENDO DEL SISTEMA... ¡Hasta pronto! 👋            │");
                System.out.println("└───────────────────────────────────────────────────────────┘");
                break;
            default:
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ❌ OPCIÓN NO VÁLIDA - Intente nuevamente                 │");
                System.out.println("└───────────────────────────────────────────────────────────┘");


        }


    }

    public static void validateMovementType(String type) {
        if (type.equals("SALIDA")) {
            System.out.println("\n┌───────────────────────────────────────────────────────────┐");
            System.out.println("│  📉 INVENTARIO DISMINUIRÁ                                 │");
            System.out.println("└───────────────────────────────────────────────────────────┘");
        } else if (type.equals("ENTRADA")) {
            System.out.println("\n┌───────────────────────────────────────────────────────────┐");
            System.out.println("│  📈 INVENTARIO AUMENTARÁ                                  │");
            System.out.println("└───────────────────────────────────────────────────────────┘");

        } else {
            System.out.println("\n┌───────────────────────────────────────────────────────────┐");
            System.out.println("│  ❌ TIPO DE MOVIMIENTO INVÁLIDO                           │");
            System.out.println("└───────────────────────────────────────────────────────────┘");
        }
    }
}

