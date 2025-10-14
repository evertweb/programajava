package com.forestech;

public class Main {
    // Constantes de la aplicación
    private static final String PROJECT_NAME = "Gestion e inventario de combustibles FORESTECH";
    private static final String DATABASE = "DBforestech";
    private static final int CURRENT_YEAR = 2025;
    private static final double VERSION = 1.0;
    private static final boolean ACTIVE = true;












    public static void main(String[] args) {
        //variables para la clase movimientos
        //condición? ValorSiTrue: valorSiFalse;
        final String txtMovement = "=====REGISTRO DE MOVIMIENTO=====";
        final String txtCalculo = "=====DESGLOSE DE LA OPERACION=====";
        boolean isInput = true;
        String typeMovement = isInput ? "ENTRADA" : "SALIDA";
        String fuelType = "DIESEL";
        int fuelQuantity = 20;
        String textpresentation = fuelQuantity == 1 ? "GALON" : "GALONES";
        double priceGal = 10.000;
        double subTotalMovement = fuelQuantity * priceGal;
        double iva = 0.19 * subTotalMovement;
        double totalMovement = subTotalMovement + iva;
        boolean isBigBuy = fuelQuantity >= 100;
        boolean isValidPurchase = isInput && fuelType.equals("DIESEL") && fuelQuantity > 0;


        System.out.println("=================================");
        System.out.println("Hello, Forestech CLI!");
        System.out.println("=================================");
        System.out.println("Proyecto: " + PROJECT_NAME);
        System.out.println("Versión: " + VERSION);
        System.out.println("Año: " + CURRENT_YEAR);
        System.out.println("Base de datos: " + DATABASE);
        System.out.println("Estado: " + (ACTIVE ? "Activo" : "Inactivo"));
        System.out.println("=================================");
        System.out.println(txtMovement);
        System.out.println("TIPO DE MOVIMIENTO: " + typeMovement);
        System.out.println("TIPO DE COMBUSTIBLE: " + fuelType);
        System.out.println("CANTIDAD: " + fuelQuantity + " " + textpresentation);
        System.out.println("=================================");
        System.out.println(txtCalculo);
        System.out.println("EL SUBTOTAL DE TU COMPRA ES " + String.format("%.2f", subTotalMovement));
        System.out.println("EL IVA DE TU COMPRA ES " + String.format("%.2f", iva));
        System.out.println("EL TOTAL DE TU COMPRA ES " + String.format("%.2f", totalMovement));
        System.out.println("TU COMPRA FUE GRANDE " + isBigBuy);
        System.out.println("LA COMPRA ES VALIDA: " + isValidPurchase);
        System.out.println("=================================");





    }
}
