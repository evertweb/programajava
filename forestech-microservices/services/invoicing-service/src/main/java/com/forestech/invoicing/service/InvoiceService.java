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
            BigDecimal totalIva = BigDecimal.ZERO;
            BigDecimal defaultIvaPercent = new BigDecimal("13.0");

            for (DetalleRequest detalle : request.getDetalles()) {
                ProductDTO product = null;

                // Intentar buscar producto existente
                if (detalle.getProductId() != null && !detalle.getProductId().isEmpty()) {
                    try {
                        product = catalogClient.getProductById(detalle.getProductId());
                    } catch (Exception e) {
                        log.warn("Producto no encontrado por ID: " + detalle.getProductId());
                    }
                }

                // Si no existe, intentar crearlo si hay nombre
                if (product == null && detalle.getProductName() != null && !detalle.getProductName().isEmpty()) {
                    log.info("Creando nuevo producto automáticamente: " + detalle.getProductName());

                    // Extraer presentación del nombre del producto
                    String[] parsedProduct = parseProductNameAndPresentation(detalle.getProductName());
                    String cleanName = parsedProduct[0];
                    String presentation = parsedProduct[1];

                    ProductDTO newProduct = new ProductDTO();
                    newProduct.setName(cleanName);
                    newProduct.setUnitPrice(detalle.getPrecioUnitario());
                    newProduct.setPresentation(presentation);
                    newProduct.setMeasurementUnit(presentation); // Usar presentación como unidad de medida
                    product = catalogClient.createProduct(newProduct);

                    log.info("Producto creado: {} con presentación: {}", cleanName, presentation);

                    // Actualizar el ID en el request para referencias futuras
                    detalle.setProductId(product.getId());
                }

                if (product == null) {
                    throw new RuntimeException("Producto no encontrado y no se pudo crear (falta nombre): " + detalle.getProductId());
                }

                BigDecimal lineSubtotal = detalle.getCantidad().multiply(detalle.getPrecioUnitario());
                subtotal = subtotal.add(lineSubtotal);

                // Calculate IVA per line using the line's ivaPercent (default to 13.0 if null)
                BigDecimal lineIvaPercent = detalle.getIvaPercent() != null ? detalle.getIvaPercent() : defaultIvaPercent;
                BigDecimal lineIva = lineSubtotal.multiply(lineIvaPercent).divide(new BigDecimal("100"));
                totalIva = totalIva.add(lineIva);
            }

            BigDecimal total = subtotal.add(totalIva);

            // PASO 3: Crear factura
            factura.setNumeroFactura(generarNumeroFactura());
            factura.setSupplierId(request.getSupplierId());
            factura.setFechaEmision(LocalDate.now());
            factura.setFechaVencimiento(LocalDate.now().plusDays(30));
            factura.setSubtotal(subtotal);
            factura.setIva(totalIva);
            factura.setTotal(total);
            factura.setEstado(Factura.EstadoFactura.PENDIENTE);
            factura.setObservaciones("Generada automáticamente");

            // Crear detalles
            List<DetalleFactura> detalles = new ArrayList<>();
            for (DetalleRequest detalleReq : request.getDetalles()) {
                // El producto ya debe existir o haber sido creado en el paso 2
                ProductDTO product = catalogClient.getProductById(detalleReq.getProductId());

                DetalleFactura detalle = new DetalleFactura();
                detalle.setFactura(factura);
                detalle.setProductId(detalleReq.getProductId());
                detalle.setProducto(product.getName()); // Legacy field
                detalle.setCantidad(detalleReq.getCantidad());
                detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                // Set IVA percent per line (default to 13.0 if null)
                detalle.setIvaPercent(detalleReq.getIvaPercent() != null ? detalleReq.getIvaPercent() : defaultIvaPercent);
                detalles.add(detalle);
            }
            factura.setDetalles(detalles);

            // Guardar factura
            factura = facturaRepository.save(factura);

            // PASO 4: Registrar movimientos de entrada en Inventory Service
            for (DetalleFactura detalle : factura.getDetalles()) {
                MovementRequest movRequest = new MovementRequest();
                movRequest.setProductId(detalle.getProductId());
                movRequest.setInvoiceId(factura.getId());
                movRequest.setQuantity(detalle.getCantidad());
                movRequest.setUnitPrice(detalle.getPrecioUnitario());
                movRequest.setDescription("Factura: " + factura.getNumeroFactura());

                log.info("Creating movement for product: " + detalle.getProductId());
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

    @Transactional
    public void regenerateInventoryFromInvoices() {
        log.info("Iniciando regeneración de inventario...");
        List<Factura> facturas = facturaRepository.findAll();
        
        for (Factura factura : facturas) {
            if (factura.getEstado() == Factura.EstadoFactura.ANULADA) continue;

            log.info("Procesando factura: " + factura.getNumeroFactura());
            boolean needsSave = false;

            for (DetalleFactura detalle : factura.getDetalles()) {
                try {
                    String productId = detalle.getProductId();
                    ProductDTO product = null;

                    // 1. Intentar buscar por ID si existe
                    if (productId != null && !productId.isEmpty()) {
                        try {
                            product = catalogClient.getProductById(productId);
                        } catch (Exception e) {
                            log.warn("Producto ID {} no encontrado, intentando por nombre", productId);
                        }
                    }

                    // 2. Si no se encuentra, buscar por Nombre
                    if (product == null) {
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

                    // 3. Si sigue sin encontrarse, crear nuevo
                    if (product == null) {
                        log.info("Creando nuevo producto para regeneración: {}", detalle.getProducto());

                        // Extraer presentación del nombre del producto
                        String[] parsedProduct = parseProductNameAndPresentation(detalle.getProducto());
                        String cleanName = parsedProduct[0];
                        String presentation = parsedProduct[1];

                        ProductDTO newProduct = new ProductDTO();
                        newProduct.setName(cleanName);
                        newProduct.setUnitPrice(detalle.getPrecioUnitario());
                        newProduct.setPresentation(presentation);
                        newProduct.setMeasurementUnit(presentation);
                        product = catalogClient.createProduct(newProduct);

                        log.info("Producto creado: {} con presentación: {}", cleanName, presentation);
                    }

                    if (product == null) {
                        log.error("No se pudo resolver el producto para detalle: " + detalle.getProducto());
                        continue;
                    }

                    // 4. Actualizar Detalle con el ID correcto
                    if (product != null && !product.getId().equals(detalle.getProductId())) {
                        detalle.setProductId(product.getId());
                        needsSave = true;
                    }

                    // 5. Registrar movimiento de entrada
                    MovementRequest movRequest = new MovementRequest();
                    movRequest.setProductId(product.getId());
                    movRequest.setInvoiceId(factura.getId());
                    movRequest.setQuantity(detalle.getCantidad());
                    movRequest.setUnitPrice(detalle.getPrecioUnitario());
                    movRequest.setDescription("Regeneración: " + factura.getNumeroFactura());

                    MovementDTO movement = inventoryClient.registrarEntrada(movRequest);
                    
                    detalle.setMovementId(movement.getId());
                    needsSave = true;
                    
                } catch (Exception e) {
                    log.error("Error regenerando ítem de factura " + factura.getNumeroFactura(), e);
                }
            }

            if (needsSave) {
                facturaRepository.save(factura);
            }
        }
        log.info("Regeneración completada.");
    }

    /**
     * Extrae la presentación del nombre del producto.
     * Busca palabras clave al final del nombre como GALON, CANECA, CUARTO, GARRAFA, LITRO, UNIDAD.
     *
     * Ejemplos:
     * - "MOBIL SUPER MIL 20W50 GALON" -> ["MOBIL SUPER MIL 20W50", "GALON"]
     * - "LUBRIGRAS GRASA CHASIS ROJA X 16K CANECA" -> ["LUBRIGRAS GRASA CHASIS ROJA X 16K", "CANECA"]
     * - "BATERIA 31H MAC" -> ["BATERIA 31H MAC", "UNIDAD"]
     *
     * @param fullName Nombre completo del producto
     * @return Array con [nombreLimpio, presentación]
     */
    private String[] parseProductNameAndPresentation(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return new String[]{"", "UNIDAD"};
        }

        String upperName = fullName.toUpperCase().trim();

        // Lista de presentaciones conocidas (ordenadas por longitud descendente para evitar matches parciales)
        String[] presentations = {"KILOGRAMO", "GARRAFA", "CANECA", "CUARTO", "GALÓN", "GALON", "LITRO", "UNIDAD"};

        for (String presentation : presentations) {
            if (upperName.endsWith(" " + presentation)) {
                // Encontró la presentación al final
                String cleanName = fullName.substring(0, fullName.length() - presentation.length() - 1).trim();
                // Normalizar GALÓN a GALON
                String normalizedPresentation = presentation.equals("GALÓN") ? "GALON" : presentation;
                return new String[]{cleanName, normalizedPresentation};
            }
        }

        // No se encontró presentación conocida, usar UNIDAD por defecto
        return new String[]{fullName.trim(), "UNIDAD"};
    }
}
