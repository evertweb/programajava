package com.forestech;

import com.forestech.managers.MovementManagers;
import com.forestech.models.Movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Ejemplo didáctico: diferencia entre los 2 constructores de MovementManagers

        List<Movement> datosInicialesDataBase = new ArrayList<>();
        datosInicialesDataBase.add(new Movement("ENTRADA", "ACPM", 20, 2));
        MovementManagers managerConCopia = new MovementManagers(datosInicialesDataBase);
        managerConCopia.getAllMovements();





    }

}
