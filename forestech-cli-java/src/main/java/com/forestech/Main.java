package com.forestech;

/**
 * Punto de entrada principal del sistema Forestech CLI.
 * Orquesta el flujo de trabajo: renderizado UI, captura de datos y procesamiento de movimientos.
 */
public class Main {
    /**
     * Método main - ejecuta el flujo completo de la aplicación.
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Renderizar header y menú principal
        BannerMenu.header();
        MenuHelper.displayMenu();
        
        // Capturar selección del usuario (1: ENTRADA, 2: SALIDA)
        int option = InputHelper.readInt("\n👉 SELECCIONE UNA OPCIÓN: ");
        MenuHelper.proccessMenuOption(option);
        
        // Validar opción: solo procesar si es ENTRADA (1) o SALIDA (2)
        if (option == 1 || option == 2) {
            // Capturar datos del movimiento: tipo, cantidad y precio
            String fuelType = InputHelper.readFuelType();
            double quantity = InputHelper.readDouble("\n📦 INGRESE LA CANTIDAD DE GALONES: ");
            Double unitPrice = InputHelper.readDouble("\n💵 INGRESE PRECIO UNITARIO POR GALÓN: ");

            // Validar integridad de los datos ingresados
            if (MovementCalculator.isValidMovement(fuelType, quantity, unitPrice)) {
                // Datos válidos: mostrar confirmación y resumen del movimiento
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ✅ DATOS VÁLIDOS - Procesando movimiento...                │");
                System.out.println("└─────────────────────────────────────────────────────────────┘");
                MovementCalculator.printMovementSummary(fuelType, quantity, unitPrice);
            } else {
                // Datos inválidos: mostrar error
                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
                System.out.println("│  ❌ MOVIMIENTO NO VÁLIDO - Verifique los datos              │");
                System.out.println("└─────────────────────────────────────────────────────────────┘");
            }
        } else {
            // Opción inválida: mostrar advertencia
            System.out.println("\n┌───────────────────────────────────────────────────────────┐");
            System.out.println("│  ⚠️  TIPO DE MOVIMIENTO NO VÁLIDO                           │");
            System.out.println("└─────────────────────────────────────────────────────────────┘");
        }
    }
}
