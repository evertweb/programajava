package com.forestech;

import java.security.PublicKey;

public class MovementCalculator {
    public static final double IVA = 0.19;
    public static  final String TXTCALCULO = "=====DESGLOSE DE LA OPERACION=====";



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


    public static void  printMovementSummary(String tipoDeCombustible, int quantity, double unitPrice){
        double subTotal = calculateSubtotal(quantity, unitPrice);
        double iva = calculateIVA(subTotal);
        double total = calculateTotal(subTotal, iva);


        System.out.println(TXTCALCULO);
        System.out.println("TIPO DE COMBUSTIBLE: " + tipoDeCombustible);
        System.out.println("CANTIDAD: " + quantity);
        System.out.println("EL SUBTOTAL DE TU COMPRA ES " + String.format("%.2f", subTotal));
        System.out.println("EL IVA DE TU COMPRA ES " + String.format("%.2f", iva));
        System.out.println("EL TOTAL DE TU COMPRA ES " + String.format("%.2f", total));
        System.out.println("=================================");
//        System.out.println(txtCalculo);


////        System.out.println("TU COMPRA FUE GRANDE " + isBigBuy);
////        System.out.println("LA COMPRA ES VALIDA: " + isValidPurchase);
//        System.out.println("=================================");




    }

}
