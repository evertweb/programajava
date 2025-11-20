package com.forestech.invoicing.service;

import com.forestech.invoicing.client.*;
import com.forestech.invoicing.model.DetalleFactura;
import com.forestech.invoicing.model.Factura;
import com.forestech.invoicing.repository.FacturaRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final FacturaRepository facturaRepository;
    private final CatalogClient catalogClient;
    private final PartnersClient partnersClient;
    private final InventoryClient inventoryClient;

    @Transactional
    public Factura crearFactura(FacturaRequest request) {
        Factura factura = new Factura();
        List<String> movimientosCreados = new ArrayList<>();

        try {
            // PASO 1: Validar proveedor
            SupplierDTO supplier = partnersClient.getSupplierById(request.getSupplierId());
            if (supplier == null) {
                throw new RuntimeException("Proveedor no encontrado");
            }

            // PASO 2: Validar y calcular totales
            BigDecimal subtotal = BigDecimal.ZERO;
            for (DetalleRequest detalle : request.getDetalles()) {
                ProductDTO product = catalogClient.getProductById(detalle.getProductId());
                if (product == null) {
                    throw new RuntimeException("Producto no encontrado: " + detalle.getProductId());
                }

                BigDecimal lineSubtotal = detalle.getCantidad().multiply(detalle.getPrecioUnitario());
                subtotal = subtotal.add(lineSubtotal);
            }

            BigDecimal iva = subtotal.multiply(new BigDecimal("0.19"));
            BigDecimal total = subtotal.add(iva);

            // PASO 3: Crear factura
            factura.setNumeroFactura(generarNumeroFactura());
            factura.setSupplierId(request.getSupplierId());
            factura.setFechaEmision(LocalDate.now());
            factura.setFechaVencimiento(LocalDate.now().plusDays(30));
            factura.setSubtotal(subtotal);
            factura.setIva(iva);
            factura.setTotal(total);
            factura.setEstado(Factura.EstadoFactura.PENDIENTE);
            factura.setObservaciones("Generada automáticamente");

            // Crear detalles
            List<DetalleFactura> detalles = new ArrayList<>();
            for (DetalleRequest detalleReq : request.getDetalles()) {
                ProductDTO product = catalogClient.getProductById(detalleReq.getProductId());
                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setProductId(detalleReq.getProductId());
                detalle.setProducto(product.getName()); // Legacy field
                detalle.setCantidad(detalleReq.getCantidad());
                detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                detalles.add(detalle);
            }
            factura.setDetalles(detalles);

            // Guardar factura
            factura = facturaRepository.save(factura);

            // PASO 4: Registrar movimientos de entrada en Inventory Service
            for (DetalleFactura detalle : factura.getDetalles()) {
                MovementRequest movRequest = new MovementRequest();
                movRequest.setProductId(detalle.getProductId());
                movRequest.setQuantity(detalle.getCantidad());
                movRequest.setUnitPrice(detalle.getPrecioUnitario());
                movRequest.setDescription("Factura: " + factura.getNumeroFactura());

                MovementDTO movement = inventoryClient.registrarEntrada(movRequest);
                movimientosCreados.add(movement.getId());
                detalle.setMovementId(movement.getId());
            }

            return factura;

        } catch (Exception e) {
            // ROLLBACK: Eliminar movimientos creados
            log.error("Error creando factura, iniciando rollback...", e);

            for (String movementId : movimientosCreados) {
                try {
                    inventoryClient.deleteMovement(movementId);
                } catch (Exception rollbackError) {
                    log.error("Error en rollback de movimiento: " + movementId, rollbackError);
                }
            }

            throw new RuntimeException("Error creando factura: " + e.getMessage(), e);
        }
    }

    public Factura findById(String id) {
        return facturaRepository.findById(id).orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    public List<Factura> findAll() {
        return facturaRepository.findAll();
    }

    @Transactional
    public void cancelInvoice(String id) {
        Factura factura = findById(id);

        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new RuntimeException("La factura ya está anulada");
        }

        // Revertir movimientos de stock
        for (DetalleFactura detalle : factura.getDetalles()) {
            if (detalle.getMovementId() != null) {
                try {
                    inventoryClient.deleteMovement(detalle.getMovementId());
                } catch (Exception e) {
                    log.error("Error revirtiendo movimiento " + detalle.getMovementId(), e);
                    // Continuamos con los demás para intentar revertir lo posible
                }
            }
        }

        factura.setEstado(Factura.EstadoFactura.ANULADA);
        factura.setObservaciones(factura.getObservaciones() + " [ANULADA: " + LocalDateTime.now() + "]");
        facturaRepository.save(factura);
    }

    @Transactional
    public Factura updateInvoice(String id, FacturaRequest request) {
        Factura factura = findById(id);

        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new RuntimeException("No se puede editar una factura anulada");
        }

        // Solo permitimos editar metadatos no críticos
        if (request.getObservaciones() != null) {
            factura.setObservaciones(request.getObservaciones());
        }
        if (request.getFormaPago() != null) {
            factura.setFormaPago(request.getFormaPago());
        }
        // Fecha vencimiento podría ser editable
        // factura.setFechaVencimiento(...);

        return facturaRepository.save(factura);
    }

    private String generarNumeroFactura() {
        return "F-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", facturaRepository.count() + 1);
    }
}
