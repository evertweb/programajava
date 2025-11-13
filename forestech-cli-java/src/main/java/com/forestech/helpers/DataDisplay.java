package com.forestech;

public  class DataDisplay {
    public static void showFuelTypes(){
        String[] fuelTypes = {"DIESEL", "GASOLINA"};
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              ⛽ TIPOS DE COMBUSTIBLES                     ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        for (int i = 0; i < fuelTypes.length; i++) {
            System.out.println("║  " + (i + 1) + ". " + fuelTypes[i]);


        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    public static void showMenuWithForEach(String[] options){
        int counter = 1;
        for (String option : options){
            System.out.println("  " + counter + ". " + option);
            counter++;
        }
    }
    public static void simulateProcessing(int totalMovements){
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║            ⚙️  PROCESANDO MOVIMIENTOS...                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        int processed = 0;
        while (processed < totalMovements){
            processed++;
            System.out.println("  🔄 PROCESANDO MOVIMIENTO #" + processed);
        }
        System.out.println("\n┌───────────────────────────────────────────────────────────┐");
        System.out.println("│  ✅ Total procesado: " + processed + " movimientos               │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
    }

}
