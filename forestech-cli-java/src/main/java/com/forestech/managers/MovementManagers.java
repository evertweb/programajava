package com.forestech.managers;

import com.forestech.models.Movement;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

public class MovementManagers {

    private List<Movement> movements;

    /**
     * Constructor principal que recibe una lista  puede ser la base de datos o si no crea una nueva
     * @param movements
     */
    public MovementManagers(List<Movement> movements) {
        if (movements==null){
           this.movements = new ArrayList<>();
        }else {
            this.movements = new ArrayList<>(movements);

        }
    }
    // Constructor por defecto.
    // Crea la lista de movimientos vacía.
    // Listo para empezar a agregar movimientos.
    public MovementManagers (){
        this.movements = new ArrayList<>();
    }


    //metodos principales

    /**
     * → Crear, agregar movimiento a la lista y retornar el movimiento
     *
     * @param movementType tipo de movimiento Entrada o Salida
     * @param productType     tipo de combustible (ACPM, GASOLINA, 20W50, 15W40, VALVULINA, LIGA, GRASA)
     * @param quantity     cantidad de combustible
     * @param price        precio por galon
     * @return retorna el objecto movimiento
     */
    public Movement addMovements(String movementType, String productId , String unidadDeMedida,  double quantity, double price) {
        // Usar el nuevo constructor con productId, vehicleId=null, numeroFactura=null
        Movement newMovement = new Movement(movementType, productId, null, null, unidadDeMedida, quantity, price);
        movements.add(newMovement);
        return newMovement;

    }

    ;

    /**
     * Obtengo todos los movimientos almacenados en la lista para depues hacer operaciones con esta como mostrarlo
     *
     * @return todos los movimientos
     */

    public List<Movement> getAllMovements() {
        return movements;
    }

    ;


    /**
     * Busca movimiento por su id
     *
     * @param id id a buscar
     * @return retorna el movimiento que coincide con el id
     */
    public Movement findById(String id) {
        for (Movement m : movements) {

            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;

    }

    ;

    /**
     * Filtrar movimientos por ENTRADA/SALIDA
     *
     * @param typeOfMovement tipo de movimiento a filtrar puede ser entrada o salida
     * @return El o los tipos de movimiento filtrados
     */

    public List<Movement> getMovementsByType(String typeOfMovement) {
        List<Movement> result = new ArrayList<>();

        for (Movement m : movements) {

            if (m.getMovementType().equals(typeOfMovement)) {
                result.add(m);

            }
        }
        return new ArrayList<>(result);
    }

    ;

    /**
     * Obtener total de movimientos cantidad no valor
     * @return Total de movimientos
     */


    public int getTotalMovements() {
        return movements.size();

    }

    ;

    //metodos de calculo

    /**
     * Calcula el total de movimientos tipo entrada
     *
     * @return total movimiento tipo entrada
     */
    public int calculeTotalEntered() {
        int totalEntradas = 0;
        for (Movement m : movements) {
            if (m.getMovementType().equals("ENTRADA")) {
                totalEntradas += (int) m.getQuantity();

            }
        }
        return totalEntradas;
    }

    ;

    /**
     * Calcula el total de movimientos tipo salida
     * @return total movimiento tipo salida
     */
    public int calculateTotalExited() {
        int totalExcited = 0;
        for (Movement m : movements) {
            if (m.getMovementType().equals("SALIDAS")) {
                totalExcited += m.getQuantity();


            }
        }
        return totalExcited;

    }

    ;

    /**
     * Calcula el stock actual de combustible
     * @return stock actual
     */
    public double getCurrentStock() {
        return calculeTotalEntered() - calculateTotalExited();
    }

    ;


};
