package com.forestech.invoicing.service;

import com.forestech.invoicing.client.*;
import com.forestech.invoicing.model.DetalleFactura;
import com.forestech.invoicing.model.Factura;
import com.forestech.invoicing.repository.FacturaRepository;
import com.forestech.invoicing.saga.InvoiceCreationSaga;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Invoice operations.
 * Uses SAGA pattern for distributed transaction handling during invoice creation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final FacturaRepository facturaRepository;
    private final CatalogClient catalogClient;
    private final InventoryClient inventoryClient;
    private final InvoiceCreationSaga invoiceCreationSaga;

    /**
     * Creates a new invoice using the SAGA pattern for distributed transaction management.
     * 
     * @param request The invoice creation request
     * @return The created invoice
     */
    public Factura crearFactura(FacturaRequest request) {
        return invoiceCreationSaga.execute(request);
    }

    public Factura findById(String id) {
        return facturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
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
        int revertedCount = 0;
        int errorCount = 0;
        
        for (DetalleFactura detalle : factura.getDetalles()) {
            if (detalle.getMovementId() != null) {
                try {
                    inventoryClient.deleteMovement(detalle.getMovementId());
                    revertedCount++;
                    log.debug("Movimiento revertido: {}", detalle.getMovementId());
                } catch (Exception e) {
                    errorCount++;
                    log.error("Error revirtiendo movimiento {}: {}", detalle.getMovementId(), e.getMessage());
                }
            }
        }
        
        log.info("Anulación de factura {}: {} movimientos revertidos, {} errores", 
            factura.getNumeroFactura(), revertedCount, errorCount);

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

        return facturaRepository.save(factura);
    }

    /**
     * Regenerates inventory movements from existing invoices.
     * Useful for data recovery or migration scenarios.
     */
    @Transactional
    public void regenerateInventoryFromInvoices() {
        log.info("Iniciando regeneración de inventario desde facturas...");
        List<Factura> facturas = facturaRepository.findAll();
        
        int processed = 0;
        int errors = 0;
        
        for (Factura factura : facturas) {
            if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
                continue;
            }

            log.info("Procesando factura: {}", factura.getNumeroFactura());
            boolean needsSave = false;

            for (DetalleFactura detalle : factura.getDetalles()) {
                try {
                    ProductDTO product = resolveProduct(detalle);
                    
                    if (product == null) {
                        log.error("No se pudo resolver el producto para detalle: {}", detalle.getProducto());
                        errors++;
                        continue;
                    }

                    // Actualizar Detalle con el ID correcto si cambió
                    if (!product.getId().equals(detalle.getProductId())) {
                        detalle.setProductId(product.getId());
                        needsSave = true;
                    }

                    // Registrar movimiento de entrada
                    MovementRequest movRequest = new MovementRequest();
                    movRequest.setProductId(product.getId());
                    movRequest.setInvoiceId(factura.getId());
                    movRequest.setQuantity(detalle.getCantidad());
                    movRequest.setUnitPrice(detalle.getPrecioUnitario());
                    movRequest.setDescription("Regeneración: " + factura.getNumeroFactura());

                    MovementDTO movement = inventoryClient.registrarEntrada(movRequest);
                    detalle.setMovementId(movement.getId());
                    needsSave = true;
                    processed++;
                    
                } catch (Exception e) {
                    log.error("Error regenerando ítem de factura {}: {}", factura.getNumeroFactura(), e.getMessage());
                    errors++;
                }
            }

            if (needsSave) {
                facturaRepository.save(factura);
            }
        }
        
        log.info("Regeneración completada. Procesados: {}, Errores: {}", processed, errors);
    }

    private ProductDTO resolveProduct(DetalleFactura detalle) {
        String productId = detalle.getProductId();
        ProductDTO product = null;

        // 1. Buscar por ID
        if (productId != null && !productId.isEmpty()) {
            try {
                product = catalogClient.getProductById(productId);
            } catch (Exception e) {
                log.warn("Producto ID {} no encontrado, intentando por nombre", productId);
            }
        }

        // 2. Buscar por nombre
        if (product == null && detalle.getProducto() != null) {
            try {
                List<ProductDTO> found = catalogClient.searchProducts(detalle.getProducto());
                if (found != null && !found.isEmpty()) {
                    product = found.get(0);
                    log.info("Producto encontrado por nombre: {} -> {}", detalle.getProducto(), product.getId());
                }
            } catch (Exception e) {
                log.warn("Error buscando producto por nombre: {}", detalle.getProducto());
            }
        }

        // 3. Crear nuevo si no existe
        if (product == null && detalle.getProducto() != null) {
            log.info("Creando nuevo producto: {}", detalle.getProducto());
            
            String[] parsed = parseProductNameAndPresentation(detalle.getProducto());
            
            ProductDTO newProduct = new ProductDTO();
            newProduct.setName(parsed[0]);
            newProduct.setUnitPrice(detalle.getPrecioUnitario());
            newProduct.setPresentation(parsed[1]);
            newProduct.setMeasurementUnit(parsed[1]);
            
            product = catalogClient.createProduct(newProduct);
        }

        return product;
    }

    private String[] parseProductNameAndPresentation(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return new String[]{"", "UNIDAD"};
        }

        String upperName = fullName.toUpperCase().trim();
        String[] presentations = {"KILOGRAMO", "GARRAFA", "CANECA", "CUARTO", "GALÓN", "GALON", "LITRO", "UNIDAD"};

        for (String presentation : presentations) {
            if (upperName.endsWith(" " + presentation)) {
                String cleanName = fullName.substring(0, fullName.length() - presentation.length() - 1).trim();
                String normalizedPresentation = presentation.equals("GALÓN") ? "GALON" : presentation;
                return new String[]{cleanName, normalizedPresentation};
            }
        }

        return new String[]{fullName.trim(), "UNIDAD"};
    }
}
