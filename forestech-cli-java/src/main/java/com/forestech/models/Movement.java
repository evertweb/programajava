package com.forestech.models;

import com.forestech.AppConfig;
import com.forestech.utils.IdGenerator;

import java.time.LocalDateTime;


public class Movement {

    // Atributos
    private final String id;
    private String movementType;
    private String fuelType;
    private double quantity;
    private double unitPrice;
    private final String movementDate;

    // Constructor completo
    public Movement(String movementType, String fuelType, double quantity, double unitPrice) {
        this.id = IdGenerator.generateMovementId();  // ✅ Ahora genera: MOV-A1B2C3D4
        this.movementType = movementType;
        this.fuelType = fuelType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.movementDate = LocalDateTime.now().toString();
    }

    // Contructor para herramientas
    public Movement() {
        this.id = IdGenerator.generateMovementId();  // ✅ Ahora genera: MOV-A1B2C3D4
        this.movementDate = LocalDateTime.now().toString();
        this.movementType = null;
        this.fuelType = null;
        this.quantity = 0.0;  // ✅ Mejor usar 0.0 que parsear null
        this.unitPrice = 0.0;  // ✅ Mejor usar 0.0 que parsear null
    }


    //getters y setters


    public String getId() {
        return id;
    }


    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        if (movementType.equals("ENTRADA") || movementType.equals("SALIDA")) {
            this.movementType = movementType;

        } else {
            System.out.println("TIPO DE MOVIMIENTO NO VALIDO");

        }

    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        } else {
            System.out.println("CANTIDAD NO VALIDA");
        }


    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice > 0) {
            this.unitPrice = unitPrice;
        } else {
            System.out.println("PRECIO NO VALIDO");
        }
    }

    public String getMovementDate() {
        return movementDate;
    }


    public double getSubtotalvalue() {
        return this.quantity * this.unitPrice;

    }

    public double getIva() {
        return getSubtotalvalue() * AppConfig.IVA_RATE;

    }

    public double getTotalWithIva() {
        return getSubtotalvalue() + getIva();
    }


    // Método toString() - Define cómo se muestra el objeto al imprimirlo
    //Indica que sobrescribe un método de la clase padre (Object)
    @Override
    public String toString() {
        return "┌─────────────────────────────────────────────────────┐\n" +
                "│              📋 DETALLE DEL MOVIMIENTO              │\n" +
                "├─────────────────────────────────────────────────────┤\n" +
                "│ 🆔 ID:           " + id + "\n" +
                "│ 📌 Tipo:         " + movementType + "\n" +
                "│ ⛽ Combustible:  " + fuelType + "\n" +
                "│ 📦 Cantidad:     " + quantity + " galones\n" +
                "│ 💵 Precio Unit:  $" + unitPrice + "\n" +
                "├─────────────────────────────────────────────────────┤\n" +
                "│ 💰 Subtotal:     $" + getSubtotalvalue() + "\n" +
                "│ 📊 IVA:          $" + getIva() + "\n" +
                "│ 🏦 TOTAL c/IVA:  $" + getTotalWithIva() + "\n" +
                "├─────────────────────────────────────────────────────┤\n" +
                "│ 📅 Fecha:        " + movementDate + "\n" +
                "└─────────────────────────────────────────────────────┘";
    }


}





