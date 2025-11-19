package com.forestech.invoicing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "numero_factura", referencedColumnName = "numero_factura")
    @JsonIgnore
    private Factura factura;

    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(length = 200, nullable = false)
    private String producto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;
}
