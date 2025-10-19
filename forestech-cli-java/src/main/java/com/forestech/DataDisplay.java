package com.forestech;

public  class DataDisplay {
    public static void showFuelTypes(){
        String[] fuelTypes = {"DIESEL", "GASOLINA"};
        System.out.println("=======TIPOS DE COMBUSTIBLES=======");
        for (int i = 0; i < fuelTypes.length; i++) {
            System.out.println( i + 1 + " " + fuelTypes[i]);


        }
    }

    public static void showMenuWithForEach(String[] options){
        int counter = 1;
        for (String option : options){
            System.out.println(counter + ". " + option);
            counter++;
        }
    }
    public static void simulateProcessing(int totalMovements){
        System.out.println("=======PROCESANDO MOVIMIENTOS=======");
        int processed = 0;
        while (processed < totalMovements){
            processed++;
            System.out.println("PROCESANDO MOVIMIENTO #" + processed);
        }
        System.out.println( "✓ Total procesado: " + processed);
    }

}
