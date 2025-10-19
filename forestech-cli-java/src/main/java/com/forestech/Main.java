package com.forestech;

import javax.xml.crypto.Data;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*
        ┌──────────────────┐
        │    Main.java     │  ← ORQUESTADOR (coordina el flujo)
        └────────┬─────────┘
                 │
                 ├─→ AppConfig.java          (constantes del sistema)
                 |-->BannerMenu.java              (Encabezado del programa)
                 ├─→ MovementCalculator.java (lógica de cálculos)
                 ├─→ MenuHelper.java         (lógica del menú)
                 ├─→ DataDisplay.java        (visualización de datos)
                 └─→ InputHelper.java        (captura de inputs)
         */
        BannerMenu.header();
        MenuHelper.displayMenu();
        int option = InputHelper.readInt("\n👉 SELECCIONE UNA OPCIÓN: ");
        MenuHelper.proccessMenuOption(option);
        if (option == 1 || option == 2) {

            //2. Capturar tipo de combustible:
            String fuelType = InputHelper.readFuelType();
            //3. Capturar cantidad:
            double quantity = InputHelper.readDouble("\n📦 INGRESE LA CANTIDAD DE GALONES: ");
            //4. Capturar precio:
            Double unitPrice = InputHelper.readDouble("\n💵 INGRESE PRECIO UNITARIO POR GALÓN: ");

            if (MovementCalculator.isValidMovement(fuelType, quantity, unitPrice)) {
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ✅ DATOS VÁLIDOS - Procesando movimiento...                │");
                System.out.println("└─────────────────────────────────────────────────────────────┘");
                MovementCalculator.printMovementSummary(fuelType, quantity, unitPrice);


            } else {
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ❌ MOVIMIENTO NO VÁLIDO - Verifique los datos              │");
                System.out.println("└─────────────────────────────────────────────────────────────┘");

            }

        } else {
            System.out.println("\n┌───────────────────────────────────────────────────────────┐");
            System.out.println("│  ⚠️  TIPO DE MOVIMIENTO NO VÁLIDO                           │");
            System.out.println("└─────────────────────────────────────────────────────────────┘");
        }


    }
}
