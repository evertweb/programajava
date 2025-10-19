package com.forestech;

import javax.xml.crypto.Data;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //condición? ValorSiTrue: valorSiFalse;

//
//        System.out.println("=================================");
//        System.out.println("Hello, Forestech CLI!");
//        System.out.println("=================================");
//        System.out.println("Proyecto: " + AppConfig.PROJECT_NAME);
//        System.out.println("Versión: " + AppConfig.VERSION);
//        System.out.println("Año: " + AppConfig.CURRENT_YEAR);
//        System.out.println("Base de datos: " + AppConfig.DATABASE);
//        System.out.println("Estado: " + (AppConfig.ACTIVE ? "Activo" : "Inactivo"));
//        System.out.println("=================================");



//        MenuHelper.displayMenu();
//        int opcioUsuario = 2;
//        MenuHelper.proccessMenuOption(opcioUsuario);
//        MenuHelper.validateMovementType("ENTRADA");
//
        DataDisplay.showFuelTypes();
        DataDisplay.showMenuWithForEach(new String[]{"ACPM", "GASOLINA"});
        DataDisplay.simulateProcessing(5);























    }
}
