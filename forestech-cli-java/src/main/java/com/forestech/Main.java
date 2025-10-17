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
        String movementType = "ENTRADA";
        int menu = 1;

















        System.out.println("=================================");
        System.out.println("Hello, Forestech CLI!");
        System.out.println("=================================");
        System.out.println("Proyecto: " + PROJECT_NAME);
        System.out.println("Versión: " + VERSION);
        System.out.println("Año: " + CURRENT_YEAR);
        System.out.println("Base de datos: " + DATABASE);
        System.out.println("Estado: " + (ACTIVE ? "Activo" : "Inactivo"));
        System.out.println("=================================");
//        System.out.println(txtMovement);
//        System.out.println("TIPO DE MOVIMIENTO: " + typeMovement);

        System.out.println("MENU DE OPCIONES");
        System.out.println("1. Registrar entrada de combustible");
        System.out.println("2. Registrar salida de combustible");
        System.out.println("3. Consultar inventario de combustible");
        System.out.println("4. Salir");
        System.out.println("TU SELECCIÓN: " + menu);

        switch (menu){
            case 1:
                System.out.println("Registrar entrada de combustible");
                break;
            case 2:
                System.out.println("Registrar salida de combustible");
                break;
            case 3:
                System.out.println("Consultar inventario de combustible");
                break;
            case 4:
                System.out.println("Salir");
                break;
            default:
                System.out.println("Opción inválida");
        }
        if (movementType.equals("ENTRADA")){
            System.out.println("INVENTARIO AUMENTARA ");

        } else if (movementType.equals("SALIDA")) {
            System.out.println("INVENTARIO DISMINUIRÁ");

        }else {
            System.out.println("VALOR INVALIDO");
        }

//        System.out.println("TIPO DE COMBUSTIBLE: " + fuelType);
//        System.out.println("CANTIDAD: " + fuelQuantity + " " + textpresentation);
// [MermaidChart: 8938b785-785e-4300-869e-4f053a1bf197]
//        System.out.println("=================================");
//        System.out.println(txtCalculo);
//        System.out.println("EL SUBTOTAL DE TU COMPRA ES " + String.format("%.2f", subTotalMovement));
//        System.out.println("EL IVA DE TU COMPRA ES " + String.format("%.2f", iva));
//        System.out.println("EL TOTAL DE TU COMPRA ES " + String.format("%.2f", totalMovement));
//        System.out.println("TU COMPRA FUE GRANDE " + isBigBuy);
//        System.out.println("LA COMPRA ES VALIDA: " + isValidPurchase);
//        System.out.println("=================================");





    }
}
