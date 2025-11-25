package com.forestech.invoicing.saga;

import com.forestech.invoicing.client.*;
import com.forestech.invoicing.model.DetalleFactura;
import com.forestech.invoicing.model.Factura;
import com.forestech.invoicing.repository.FacturaRepository;
import com.forestech.invoicing.service.DetalleRequest;
import com.forestech.invoicing.service.FacturaRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SAGA Orchestrator for Invoice Creation.
 * 
 * Implements the Orchestration-based SAGA pattern for distributed transactions.
 * The saga coordinates the following steps:
 * 1. Validate supplier exists
 * 2. Validate/Create products
 * 3. Create invoice (local transaction)
 * 4. Create inventory movements (external service)
 * 5. Link movements to invoice details
 * 
 * If any step fails, compensation actions are executed in reverse order.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceCreationSaga {

    private final FacturaRepository facturaRepository;
    private final CatalogClient catalogClient;
    private final PartnersClient partnersClient;
    private final InventoryClient inventoryClient;

    private static final BigDecimal DEFAULT_IVA_PERCENT = new BigDecimal("13.0");

    /**
     * Executes the complete invoice creation saga.
     * 
     * @param request The invoice creation request
     * @return The created invoice
     * @throws InvoiceSagaException if the saga fails and compensation is executed
     */
    public Factura execute(FacturaRequest request) {
        InvoiceSagaContext context = InvoiceSagaContext.builder().build();
        
        try {
            log.info("Starting Invoice Creation SAGA");
            
            // Step 1: Validate Supplier
            context.setState(InvoiceSagaContext.SagaState.VALIDATING_SUPPLIER);
            validateSupplier(request.getSupplierId());
            context.markStepCompleted(InvoiceSagaContext.SagaStep.VALIDATE_SUPPLIER);
            
            // Step 2: Validate and/or Create Products
            context.setState(InvoiceSagaContext.SagaState.VALIDATING_PRODUCTS);
            List<ProductDTO> products = validateAndPrepareProducts(request.getDetalles(), context);
            context.markStepCompleted(InvoiceSagaContext.SagaStep.VALIDATE_PRODUCTS);
            
            // Step 3: Calculate totals and create invoice
            context.setState(InvoiceSagaContext.SagaState.CREATING_INVOICE);
            Factura factura = createInvoice(request, products);
            context.setInvoiceId(factura.getId());
            context.setInvoiceNumber(factura.getNumeroFactura());
            context.markStepCompleted(InvoiceSagaContext.SagaStep.CREATE_INVOICE);
            
            // Step 4: Create inventory movements
            context.setState(InvoiceSagaContext.SagaState.CREATING_MOVEMENTS);
            createInventoryMovements(factura, context);
            context.markStepCompleted(InvoiceSagaContext.SagaStep.CREATE_MOVEMENTS);
            
            // Step 5: Save updated invoice with movement IDs
            factura = facturaRepository.save(factura);
            context.markStepCompleted(InvoiceSagaContext.SagaStep.LINK_MOVEMENTS);
            
            context.setState(InvoiceSagaContext.SagaState.COMPLETED);
            log.info("Invoice Creation SAGA completed successfully. Invoice: {}", factura.getNumeroFactura());
            
            return factura;
            
        } catch (Exception e) {
            log.error("Invoice Creation SAGA failed at state: {}. Starting compensation...", context.getState(), e);
            context.setFailureReason(e.getMessage());
            compensate(context);
            throw new InvoiceSagaException("Error creando factura: " + e.getMessage(), e, context);
        }
    }

    private void validateSupplier(String supplierId) {
        log.debug("Validating supplier: {}", supplierId);
        SupplierDTO supplier = partnersClient.getSupplierById(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Proveedor no encontrado: " + supplierId);
        }
        log.debug("Supplier validated: {}", supplier.getName());
    }

    private List<ProductDTO> validateAndPrepareProducts(List<DetalleRequest> detalles, InvoiceSagaContext context) {
        List<ProductDTO> products = new ArrayList<>();
        
        for (DetalleRequest detalle : detalles) {
            ProductDTO product = null;
            
            // Try to find existing product
            if (detalle.getProductId() != null && !detalle.getProductId().isEmpty()) {
                try {
                    product = catalogClient.getProductById(detalle.getProductId());
                    log.debug("Found existing product: {}", product.getName());
                } catch (Exception e) {
                    log.warn("Product not found by ID: {}", detalle.getProductId());
                }
            }
            
            // Create product if not found and name is provided
            if (product == null && detalle.getProductName() != null && !detalle.getProductName().isEmpty()) {
                log.info("Creating new product: {}", detalle.getProductName());
                product = createProduct(detalle);
                context.addCreatedProduct(product.getId());
                detalle.setProductId(product.getId());
            }
            
            if (product == null) {
                throw new IllegalArgumentException(
                    "Producto no encontrado y no se pudo crear (falta nombre): " + detalle.getProductId());
            }
            
            products.add(product);
        }
        
        return products;
    }

    private ProductDTO createProduct(DetalleRequest detalle) {
        String[] parsed = parseProductNameAndPresentation(detalle.getProductName());
        
        ProductDTO newProduct = new ProductDTO();
        newProduct.setName(parsed[0]);
        newProduct.setUnitPrice(detalle.getPrecioUnitario());
        newProduct.setPresentation(parsed[1]);
        newProduct.setMeasurementUnit(parsed[1]);
        
        return catalogClient.createProduct(newProduct);
    }

    @Transactional
    private Factura createInvoice(FacturaRequest request, List<ProductDTO> products) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;
        
        // Calculate totals
        List<DetalleRequest> detalles = request.getDetalles();
        for (int i = 0; i < detalles.size(); i++) {
            DetalleRequest detalle = detalles.get(i);
            BigDecimal lineSubtotal = detalle.getCantidad().multiply(detalle.getPrecioUnitario());
            subtotal = subtotal.add(lineSubtotal);
            
            BigDecimal ivaPercent = detalle.getIvaPercent() != null ? detalle.getIvaPercent() : DEFAULT_IVA_PERCENT;
            BigDecimal lineIva = lineSubtotal.multiply(ivaPercent).divide(new BigDecimal("100"));
            totalIva = totalIva.add(lineIva);
        }
        
        // Create invoice
        Factura factura = new Factura();
        factura.setNumeroFactura(generateInvoiceNumber());
        factura.setSupplierId(request.getSupplierId());
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(30));
        factura.setSubtotal(subtotal);
        factura.setIva(totalIva);
        factura.setTotal(subtotal.add(totalIva));
        factura.setEstado(Factura.EstadoFactura.PENDIENTE);
        factura.setObservaciones(request.getObservaciones() != null ? request.getObservaciones() : "Generada automáticamente");
        if (request.getFormaPago() != null) {
            factura.setFormaPago(request.getFormaPago());
        }
        
        // Create details
        List<DetalleFactura> facturaDetalles = new ArrayList<>();
        for (int i = 0; i < detalles.size(); i++) {
            DetalleRequest detalleReq = detalles.get(i);
            ProductDTO product = products.get(i);
            
            DetalleFactura detalle = new DetalleFactura();
            detalle.setFactura(factura);
            detalle.setProductId(detalleReq.getProductId());
            detalle.setProducto(product.getName());
            detalle.setCantidad(detalleReq.getCantidad());
            detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
            detalle.setIvaPercent(detalleReq.getIvaPercent() != null ? detalleReq.getIvaPercent() : DEFAULT_IVA_PERCENT);
            facturaDetalles.add(detalle);
        }
        factura.setDetalles(facturaDetalles);
        
        return facturaRepository.save(factura);
    }

    private void createInventoryMovements(Factura factura, InvoiceSagaContext context) {
        for (DetalleFactura detalle : factura.getDetalles()) {
            MovementRequest movRequest = new MovementRequest();
            movRequest.setProductId(detalle.getProductId());
            movRequest.setInvoiceId(factura.getId());
            movRequest.setQuantity(detalle.getCantidad());
            movRequest.setUnitPrice(detalle.getPrecioUnitario());
            movRequest.setDescription("Factura: " + factura.getNumeroFactura());
            
            log.debug("Creating movement for product: {}", detalle.getProductId());
            MovementDTO movement = inventoryClient.registrarEntrada(movRequest);
            
            context.addCreatedMovement(movement.getId());
            detalle.setMovementId(movement.getId());
            
            log.debug("Movement created: {}", movement.getId());
        }
    }

    /**
     * Executes compensation (rollback) actions for failed saga.
     * Actions are executed in reverse order of completion.
     */
    private void compensate(InvoiceSagaContext context) {
        context.setState(InvoiceSagaContext.SagaState.COMPENSATING);
        log.warn("Executing SAGA compensation for invoice: {}", context.getInvoiceNumber());
        
        // Compensate movements (reverse order)
        if (!context.getCreatedMovementIds().isEmpty()) {
            log.info("Compensating {} created movements", context.getCreatedMovementIds().size());
            for (String movementId : context.getCreatedMovementIds()) {
                try {
                    inventoryClient.deleteMovement(movementId);
                    log.debug("Compensated movement: {}", movementId);
                } catch (Exception e) {
                    log.error("Failed to compensate movement {}: {}", movementId, e.getMessage());
                    // Continue with other compensations
                }
            }
        }
        
        // Compensate invoice (delete if created)
        if (context.getInvoiceId() != null) {
            try {
                facturaRepository.deleteById(context.getInvoiceId());
                log.debug("Compensated invoice: {}", context.getInvoiceId());
            } catch (Exception e) {
                log.error("Failed to compensate invoice {}: {}", context.getInvoiceId(), e.getMessage());
            }
        }
        
        // Note: We intentionally do NOT delete created products as they may be useful
        // and don't affect inventory consistency
        
        context.setState(InvoiceSagaContext.SagaState.FAILED);
        log.info("SAGA compensation completed");
    }

    private String generateInvoiceNumber() {
        return "F-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%04d", facturaRepository.count() + 1);
    }

    private String[] parseProductNameAndPresentation(String productName) {
        // Default values
        String cleanName = productName;
        String presentation = "Unidad";
        
        // Try to extract presentation from parentheses
        if (productName.contains("(") && productName.contains(")")) {
            int start = productName.lastIndexOf("(");
            int end = productName.lastIndexOf(")");
            if (start < end) {
                presentation = productName.substring(start + 1, end).trim();
                cleanName = productName.substring(0, start).trim();
            }
        }
        
        return new String[]{cleanName, presentation};
    }
}
