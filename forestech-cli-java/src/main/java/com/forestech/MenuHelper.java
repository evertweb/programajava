package com.forestech;

import javax.naming.BinaryRefAddr;

public class MenuHelper {
    public static void  displayMenu(){
        System.out.println("=======MENU PRINCIPAL=======");
        System.out.println("1.INGRESO DE COMBUSTIBLE");
        System.out.println("2.SALIDA DE COMBUSTIBLE");
        System.out.println("3.VER INVENTARIO");
        System.out.println("4.SALIR");
        System.out.println("=============================");

    }
    public static void proccessMenuOption(int option){

        switch (option){
            case 1:
                System.out.println("MODULO DE ENTRDA DE COMBUSTIBLE SELECCIONADO");
                break;
            case 2:
                System.out.println("MODULO SALIDA DE COMBUSTIBLE SELECCIONADO");
                break;
            case 3:
                System.out.println("MODULO DE INVENTARIO SELECCIONADO");
                break;
            case 4:
                System.out.println("SALIENDO DEL SISTEMA");
                break;
            default:
                System.out.println("OPCIÓN NO VALIDA");


        }


    }
    public static void validateMovementType(String type){
        if (type.equals("SALIDA")){
            System.out.println("INVENTARIO DISMINUIRÁ");
        } else if (type.equals("ENTRADA")) {
            System.out.println("INVENTARIO AUMENTARA");

        }else {
            System.out.println("TIPO DE MOVIMIENTO INVALIDO");
        }
    }



}
