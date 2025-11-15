package com.forestech.models;
import com.forestech.AppConfig;
import com.forestech.utils.IdGenerator;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;


public class Movement {

    // ============================================================================
    // ATRIBUTOS
    // ============================================================================

    private final String id;
    private String movementType;
    private String productId;        // FK → oil_products.id
    private String vehicleId;        // FK → vehicles.id (NULL para ENTRADA)
    private String numeroFactura;    // FK → facturas.numero_factura (NULL para SALIDA)
    private String unidadDeMedida;
    private double quantity;
    private double unitPrice;
    private final String movementDate;

    // ============================================================================
    // CONSTRUCTORES
    // ============================================================================

    /**
     * Constructor para CREAR nuevos movimientos (genera ID automático).
     * Usa productId (FK) que apunta a oil_products.id
     *
     * @param movementType   Tipo: "ENTRADA" o "SALIDA"
     * @param productId      FK → oil_products.id (obligatorio)
     * @param vehicleId      FK → vehicles.id (opcional, NULL para ENTRADA)
     * @param numeroFactura  FK → facturas.numero_factura (opcional, NULL para SALIDA)
     * @param unidadDeMedida Unidad: GALON, GARRAFA, CUARTO, CANECA
     * @param quantity       Cantidad movida (debe ser > 0)
     * @param unitPrice      Precio unitario
     */
    public Movement(String movementType, String productId, String vehicleId, String numeroFactura,
                    String unidadDeMedida, double quantity, double unitPrice) {
        this.id = IdGenerator.generateMovementId();
        this.movementType = movementType;
        this.productId = productId;
        this.vehicleId = vehicleId;
        this.numeroFactura = numeroFactura;
        this.unidadDeMedida = unidadDeMedida;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.movementDate = LocalDateTime.now().toString();
    }

    /**
     * Constructor para CARGAR desde la base de datos (usa ID existente).
     * Este constructor es usado por MovementServices al hacer SELECT.
     *
     * @param id             ID existente del movimiento
     * @param movementType   Tipo: "ENTRADA" o "SALIDA"
     * @param productId      FK → oil_products.id
     * @param vehicleId      FK → vehicles.id (puede ser NULL)
     * @param numeroFactura  FK → facturas.numero_factura (puede ser NULL)
     * @param unidadDeMedida Unidad de medida
     * @param quantity       Cantidad
     * @param unitPrice      Precio unitario
     * @param movementDate   Fecha del movimiento (String)
     */
    public Movement(String id, String movementType, String productId,
                    String vehicleId, String numeroFactura, String unidadDeMedida,
                    double quantity, double unitPrice, String movementDate) {
        this.id = id;
        this.movementType = movementType;
        this.productId = productId;
        this.vehicleId = vehicleId;
        this.numeroFactura = numeroFactura;
        this.unidadDeMedida = unidadDeMedida;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.movementDate = movementDate;
    }

    /**
     * Constructor vacío para herramientas/testing.
     */
    public Movement() {
        this.id = IdGenerator.generateMovementId();
        this.movementDate = LocalDateTime.now().toString();
        this.movementType = null;
        this.productId = null;
        this.vehicleId = null;
        this.numeroFactura = null;
        this.unidadDeMedida = null;
        this.quantity = 0.0;
        this.unitPrice = 0.0;
    }


    //getters y setters


    public String getId() {
        return id;
    }


    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(@NotNull String movementType) {
        if (movementType.equals("ENTRADA") || movementType.equals("SALIDA")) {
            this.movementType = movementType;
        } else {
            System.out.println("TIPO DE MOVIMIENTO NO VALIDO");
        }
    }

    // ============================================================================
    // GETTERS Y SETTERS PARA LLAVES FORÁNEAS (FASE 4)
    // ============================================================================

    /**
     * Obtiene el ID del producto asociado a este movimiento.
     *
     * @return ID del producto (FK → oil_products.id)
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Establece el ID del producto para este movimiento.
     *
     * @param productId ID del producto (debe existir en oil_products)
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Obtiene el ID del vehículo asociado a este movimiento (solo para SALIDA).
     *
     * @return ID del vehículo (FK → vehicles.id), o NULL si es una ENTRADA
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * Establece el ID del vehículo para este movimiento.
     *
     * @param vehicleId ID del vehículo (debe existir en vehicles), o NULL
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * Obtiene el número de factura asociado a este movimiento (solo para ENTRADA).
     *
     * @return Número de factura (FK → facturas.numero_factura), o NULL si es una SALIDA
     */
    public String getNumeroFactura() {
        return numeroFactura;
    }

    /**
     * Establece el número de factura para este movimiento.
     *
     * @param numeroFactura Número de factura (debe existir en facturas), o NULL
     */
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }


    public String getUnidadDeMedida() {
        return unidadDeMedida;
    }

    public void setUnidadDeMedida(String unidad) {
        // Validar que sea una unidad válida
        if (unidad.equals("GALON") || 
            unidad.equals("GARRAFA") || 
            unidad.equals("CUARTO") || 
            unidad.equals("CANECA")) {
            this.unidadDeMedida = unidad;
        } else {
            throw new IllegalArgumentException("Unidad de medida no válida: " + unidad);
        }
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
    // Indica que sobrescribe un método de la clase padre (Object)
    @Override
    public String toString() {
        return "Movement{" +
                "id='" + id + '\'' +
                ", movementType='" + movementType + '\'' +
                ", productId='" + productId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", numeroFactura='" + numeroFactura + '\'' +
                ", unidadDeMedida='" + unidadDeMedida + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", movementDate='" + movementDate + '\'' +
                '}';
    }


}





