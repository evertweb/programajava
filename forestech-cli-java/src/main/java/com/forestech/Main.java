package com.forestech;

import com.forestech.models.Movement;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // ESTRUCTURA DEL PROGRAMA:
        // Main.java coordina el flujo general
        // - AppConfig: constantes del sistema
        // - BannerMenu: encabezado y presentación
        // - MovementCalculator: cálculos de movimientos
        // - MenuHelper: lógica de menú
        // - DataDisplay: mostrar datos
        // - InputHelper: captura de datos del usuario
//        BannerMenu.header();
//        MenuHelper.displayMenu();
//        int option = InputHelper.readInt("\n👉 SELECCIONE UNA OPCIÓN: ");
//        MenuHelper.proccessMenuOption(option);
//        if (option == 1 || option == 2) {
//
//            //2. Capturar tipo de combustible:
//            String fuelType = InputHelper.readFuelType();
//            //3. Capturar cantidad:
//            double quantity = InputHelper.readDouble("\n📦 INGRESE LA CANTIDAD DE GALONES: ");
//            //4. Capturar precio:
//            Double unitPrice = InputHelper.readDouble("\n💵 INGRESE PRECIO UNITARIO POR GALÓN: ");
//  System.out.println("└─────────────────────────────────────────────────────────────┘");
////                MovementCalculator.printMovementSummary(fuelType, quantity, unitPrice);
////
////
////            } else {
////                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
////                System.out.println("│  ❌ MOVIMIENTO NO VÁLIDO - Verifique los datos              │");
////                System.out.println("└─────────────────────────────────────────────────────────────┘");
////
////            }
////
////        } else {
////            System.out.println("\n┌───────────────────────────────────────────────────────────┐");
////            System.out.println("│  ⚠️  TIPO DE MOVIMIENTO NO VÁLIDO                           │");
////            System.out.println("└─────────────────────────────────────────────────────────────┘");
////        }
//            if (MovementCalculator.isValidMovement(fuelType, quantity, unitPrice)) {
//                System.out.println("\n┌───────────────────────────────────────────────────────────┐");
//                System.out.println("│  ✅ DATOS VÁLIDOS - Procesando movimiento...                │");
//
        // Crear movimiento de ENTRADA: 10 galones de ACPM a $20/galón
        Movement entrada1 = new Movement("ENTRADA", "ACPM", 10, 20);
        // Crear movimiento de SALIDA: 5 galones de GASOLINA a $25/galón
        Movement salida1 = new Movement("SALIDA", "GASOLINA", 5, 25);
        // Lista para almacenar todos los movimientos
        ArrayList<Movement> movements = new ArrayList<>();
        // Lista para filtrar solo movimientos de SALIDA
        ArrayList<Movement> resultadosSalidas = new ArrayList<>();

        // Agregar movimientos a la lista
        movements.add(salida1);
        movements.add(entrada1);

        // Variable para almacenar el primer movimiento de tipo ENTRADA
        Movement primeraEntrada = null;

        // Mostrar cantidad total de movimientos
        System.out.println("total de movimmientos: " + movements.size());
        // Variable para acumular el total de dinero
        double granTotal = 0;
        // Recorrer movimientos y sumar el valor total
        for (Movement m : movements) {
            granTotal += m.getTotalvalue();
            System.out.println("Gran total de movimientos: " + m.getTotalvalue());
        }

        // Buscar el primer movimiento de tipo ENTRADA
        for (Movement m : movements) {
            if (m.getMovementType().equals("ENTRADA")) {
                primeraEntrada = m;
                break;

            }
        }

        if (primeraEntrada != null) {
            System.out.println("  Primera entrada: " + "\n" + primeraEntrada);

        } else {
            System.out.println("No hay entradas");
        }


        // Filtrar solo los movimientos de tipo SALIDA
        for (Movement m : movements) {
            if (m.getMovementType().equals("SALIDA")) {
                resultadosSalidas.add(m);
            }

        }
        System.out.println("total de movimientos tipo salida: " + resultadosSalidas.size());
        for (Movement m : resultadosSalidas) {
            System.out.println(m);
        }


        // Encontrar el movimiento con la mayor cantidad
        Movement movimientoMayor = null;
        double cantidadMaxima = 0;
        for (Movement m : movements) {
            if (m.getQuantity() > cantidadMaxima) {
                cantidadMaxima = m.getQuantity();
                movimientoMayor = m;

            }
        }
        if (movimientoMayor != null){
            System.out.println("la mayor cantiad de este movimiento de tipo: " + movimientoMayor.getMovementType() + " es " + "\n" + movimientoMayor);
        }


        double l = total$PorTipo(movements, "ENTRADA");
        System.out.println("el total de movimientos de tipo entrada es: " + l);









    }
    /**
     * Calcula el total en dinero de todos los movimientos de un tipo específico
     *
     * @param lista ArrayList que contiene todos los movimientos
     * @param tipo String con el tipo a buscar ("ENTRADA" o "SALIDA")
     * @return double con la suma total de dinero de todos los movimientos del tipo especificado
     *
     * Ejemplo: Si tienes 2 ENTRADA de $200 y $300, retorna $500
     */
    public static double total$PorTipo(ArrayList<Movement> lista, String tipo){
        // PASO 1: Inicializar una variable para ACUMULAR la suma
        // Empieza en 0 porque aún no hemos sumado nada
        double totalTipo = 0;

        // PASO 2: Recorrer CADA movimiento de la lista uno por uno
        // 'l' es la variable que representa el movimiento actual en cada vuelta
        for (Movement l : lista){

            // PASO 3: FILTRAR - Verificar si este movimiento coincide con el tipo buscado
            // equals() compara Strings correctamente (no usar ==)
            if (l.getMovementType().equals(tipo)){

                // PASO 4: ACUMULAR - Si encontramos un movimiento del tipo correcto,
                // sumamos su valor total (incluyendo IVA) al acumulador
                // += significa: totalTipo = totalTipo + l.getTotalWithIva()
                totalTipo += l.getTotalWithIva();
            }
            // Si NO coincide, el bucle continúa sin hacer nada
        }

        // PASO 5: Retornar el GRAN TOTAL después de recorrer toda la lista
        // Nota: return está FUERA del bucle para no salir en la primera coincidencia
        return totalTipo;

    }



}
