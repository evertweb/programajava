    package com.forestech.models;

    public class Movement {
        // Atributos
        // Identificador único del movimiento
        private String id;
        // Tipo de movimiento (entrada/salida)
        private String movementType;
        // Tipo de combustible
        private String fuelType;
        // Cantidad de movimiento
        private double quantity;
        // Precio unitario
        private double unitPrice;

        // Fecha del movimiento
        private String moveDate;

        // Constructor
        public Movement(String id, String movementType,String fuelType,  double quantity, double unitPrice, String moveDate) {
            this.id = id;
            this.movementType = movementType;
            this.quantity = quantity;
            this.moveDate = moveDate;
        }
    }
    //getters y setters



