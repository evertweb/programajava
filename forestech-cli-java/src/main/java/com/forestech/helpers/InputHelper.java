package com.forestech.helpers;
import java.util.Scanner;

public class InputHelper {
    private static Scanner scanner = new Scanner(System.in);
    public static int readInt(String prompt){
        System.out.print(prompt);
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }
    public static double readDouble(String prompt){
        System.out.print(prompt);
        double option = scanner.nextInt();
        scanner.nextLine();
        return option;

    }
    public static String readString(String prompt){
        System.out.print(prompt);
        return scanner.nextLine();


    }
    public static String readFuelType(){
        DataDisplay.showFuelTypes();
        int selection = readInt("SELECCIONE EL TIPO DE COMBUSTIBLE: ");

        switch (selection) {
            case 1:
                return "Diesel";
            case 2:
                return "Gasolina 93";
            case 3:
                return "Gasolina 95";
            case 4:
                return "ACPM";
            default:
                return "Diesel";
        }
    }






}
