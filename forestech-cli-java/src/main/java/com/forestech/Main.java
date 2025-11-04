package com.forestech;

import com.forestech.config.DatabaseConnection;
import com.forestech.managers.MovementManagers;
import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.services.ProductServices;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Ejemplo didáctico: diferencia entre los 2 constructores de MovementManagers

//        List<Movement> datosInicialesDataBase = new ArrayList<>();
//        datosInicialesDataBase.add(new Movement("ENTRADA", "ACPM", 20, 2));
//        MovementManagers managerConCopia = new MovementManagers(datosInicialesDataBase);
//
//
//
//
//        DatabaseConnection.testConnection();
//        List<Product> products = ProductServices.getAllProducts();
//        if (products.isEmpty()){
//            System.out.println("no hay productos");
//
//        }
//        for (Product product : products){
//            System.out.println(product);
//        }
//        System.out.println("Total de productos en la BD " + products.size() );

        List<Product> products1  = ProductServices.getProductsByUnidadDeMedida("LTS");

        if (products1.isEmpty()){
            System.out.println("No hay productos en litros");

        }
        for (Product p : products1){
            System.out.println(products1);
        }
        System.out.println("\nTotal: " + products1.size() + " productos");







    }

}
