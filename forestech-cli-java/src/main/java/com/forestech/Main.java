package com.forestech;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //condición? ValorSiTrue: valorSiFalse;
//        final String txtMovement = "=====REGISTRO DE MOVIMIENTO=====";
//
//        boolean isInput = true;
//        String typeMovement = isInput ? "ENTRADA" : "SALIDA";
//        String fuelType = "DIESEL";
//        int fuelQuantity = 20;
//        String textpresentation = fuelQuantity == 1 ? "GALON" : "GALONES";
//        boolean isBigBuy = fuelQuantity >= 100;
//        boolean isValidPurchase = isInput && fuelType.equals("DIESEL") && fuelQuantity > 0;
//        String movementType = "ENTRADA";
//        int menu = 1;

        System.out.println("=================================");
        System.out.println("Hello, Forestech CLI!");
        System.out.println("=================================");
        System.out.println("Proyecto: " + AppConfig.PROJECT_NAME);
        System.out.println("Versión: " + AppConfig.VERSION);
        System.out.println("Año: " + AppConfig.CURRENT_YEAR);
        System.out.println("Base de datos: " + AppConfig.DATABASE);
        System.out.println("Estado: " + (AppConfig.ACTIVE ? "Activo" : "Inactivo"));
        System.out.println("=================================");


        //VALIDAR SI UN MOVIMIENTO ES VALIDO
//        boolean movimiento1 = MovementCalculator.isValidMovement("DIESEL", 0, 10.6);
//        String isValid = movimiento1 ? "movimiento valido" : "movimiento invalido";
//        System.out.println("el movimiento es " + isValid);
//        //resumen del movimiento
//        MovementCalculator.printMovementSummary("Diesel", 3, 10.8);
//        boolean aprobacion1 = MovementCalculator.requiresApproval("SALIDA", 10000, 600);
//        String aprobacion = aprobacion1 ? "si" : "no";
//        System.out.println("esta compra requiere aprobacion "  + aprobacion);
//
        MenuHelper.displayMenu();
        int opcioUsuario = 2;
        MenuHelper.proccessMenuOption(opcioUsuario);
        MenuHelper.validateMovementType("ENTRADA");






















    }
}
