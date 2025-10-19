package com.forestech;

import java.lang.reflect.Type;
import java.security.PublicKey;

public class MovementCalculator {
    public static final double IVA = 0.19;
    public static  final String TXTCALCULO = "╔═══════════════════════════════════════════════════════════╗\n" +
                                              "║            💰 DESGLOSE DE LA OPERACIÓN                   ║\n" +
                                              "╚═══════════════════════════════════════════════════════════╝";



    public static double calculateSubtotal(double quantity, double unitPrice) {
        // Por ahora solo métodos estáticos (aprenderás POO en Fase 2)
        return quantity * unitPrice;
    }
    public static double calculateIVA(double subtotal) {
        return subtotal * IVA;
    }
    public static double calculateTotal(double subtotal, double iva) {
        return subtotal + iva;
    }
    public static boolean isValidMovement(String fuelType, double quantity, double unitPrice) {
        // Valida que todos los datos sean correctos
        // Retorna true si es válido, false si no
        if (fuelType.equals(fuelType) && quantity > 0 && unitPrice > 0){
            return true;
        } else {
            return false;
        }


    }
    public static boolean isBigPurchase(double quantity) {
        return quantity >= 100;
    }
    public static boolean requiresApproval(String typeMovement, double quantity, double total) {
        // Combina varias condiciones con && y ||
        // Ej: requiere aprobación si es SALIDA Y (cantidad > 100 O total > 500)
        if (typeMovement.equals("SALIDA") &&  (quantity >100 || total >500 )){
            return true;
        }else {
            return false;
        }


    }


    public static void  printMovementSummary(String fuelType, double quantity, double unitPrice){
        double subTotal = calculateSubtotal(quantity, unitPrice);
        double iva = calculateIVA(subTotal);
        double total = calculateTotal(subTotal, iva);


        System.out.println("\n" + TXTCALCULO);
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│  ⛽ Tipo de combustible: " + fuelType);
        System.out.println("│  📦 Cantidad: " + quantity + " galones");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  💵 Subtotal: $" + String.format("%.2f", subTotal));
        System.out.println("│  📊 IVA (19%): $" + String.format("%.2f", iva));
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  💰 TOTAL A PAGAR: $" + String.format("%.2f", total));
        System.out.println("└───────────────────────────────────────────────────────────┘");
//        System.out.println(txtCalculo);


////        System.out.println("TU COMPRA FUE GRANDE " + isBigBuy);
////        System.out.println("LA COMPRA ES VALIDA: " + isValidPurchase);
//        System.out.println("=================================");




    }

}
