package com.forestech;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;

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
        final String logMovement = "=======REGISTRO DE MOVIMIENTO======";
        boolean esEntrada = true;
        String typeMovement = esEntrada ? "ENTRADA" : "SALIDA";
        String typeFuel = "ACPM";
        int cantFuel = 20;
        String textpresentation = cantFuel ==1 ?  "GALON" : "GALONES";
        double priceGal = 10.000;






        System.out.println("Hello, Forestech CLI!");
        System.out.println("=================================");
        System.out.println("Proyecto: " + PROJECT_NAME);
        System.out.println("Versión: " + VERSION);
        System.out.println("Año: " + CURRENT_YEAR);
        System.out.println("Base de datos: " + DATABASE);
        System.out.println("Estado: " + (ACTIVE ? "Activo" : "Inactivo"));
        System.out.println("=================================");



        System.out.println(logMovement);
        System.out.println("TIPO DE MOVIMIENTO " + typeMovement);
        System.out.println("TIPO DE COMBUSTIBLE " + typeFuel);
        System.out.println("CANTIDAD " + cantFuel + " " + textpresentation);


    }
}
