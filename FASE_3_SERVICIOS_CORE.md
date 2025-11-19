# ⚙️ FASE 3: SERVICIOS CORE
> **Implementación de Inventory, Partners e Invoicing Services**

---

## 📋 Información de la Fase

| Atributo | Valor |
|----------|-------|
| **Fase** | 3 de 5 |
| **Nombre** | Servicios Core del Negocio |
| **Duración Estimada** | 20-26 horas |
| **Complejidad** | ⭐⭐⭐⭐ (Experto) |
| **Sesiones** | 3 (3.1, 3.2, 3.3) |
| **Dependencias** | Fases 1 y 2 completadas |
| **Desbloquea** | Fase 4 - API Gateway y Reportes |

---

## 🎯 Objetivos de la Fase

Al completar esta fase, tendrás:

- [x] **inventory-service** con lógica de movimientos y cálculo de stock
- [x] **partners-service** para gestión de proveedores
- [x] **invoicing-service** con transacciones distribuidas
- [x] Comunicación entre microservicios (Feign Clients)
- [x] Patrón SAGA para transacciones distribuidas
- [x] Circuit breakers para resiliencia
- [x] 5 microservicios funcionando de forma coordinada

---

## ✅ Pre-requisitos

```bash
cd ~/forestech-microservices

# Verificar que Fases 1 y 2 están completas
./scripts/health-check.sh

# Debe mostrar:
# ✅ Consul: UP
# ✅ Config Server: UP
# ✅ Catalog Service: UP
# ✅ Fleet Service: UP
# ✅ 5 MySQL databases: UP

# Verificar servicios en Consul
curl http://localhost:8500/v1/catalog/services | jq
# Debe incluir: catalog-service, fleet-service
```

---

## 🔨 SESIÓN 3.1: Inventory Service

**Duración:** 8-10 horas  
**Complejidad:** ⭐⭐⭐⭐ (más complejo, tiene dependencias)

### Tarea 3.1.1: Crear Proyecto con Dependencias de Comunicación

```bash
cd ~/forestech-microservices/services/inventory-service
mkdir -p src/main/java/com/forestech/inventory
mkdir -p src/main/resources
```

**pom.xml** - Agregar dependencias adicionales:

```xml
<!-- Spring Cloud OpenFeign (para llamadas a otros servicios) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Resilience4j Circuit Breaker -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

---

### Tarea 3.1.2: Crear Modelo Movement

```java
@Entity
@Table(name = "movements")
public class Movement {
    @Id
    private String id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type")
    private MovementType movementType;
    
    @Column(name = "product_id")
    private String productId;  // Referencia lógica, no FK
    
    @Column(name = "vehicle_id")
    private String vehicleId;  // Referencia lógica, no FK
    
    private BigDecimal quantity;
    
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    
    private BigDecimal subtotal;
    
    private String description;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum MovementType {
        ENTRADA, SALIDA
    }
}
```

---

### Tarea 3.1.3: Crear Feign Clients

**CatalogClient.java** - Cliente HTTP para catalog-service:

```java
@FeignClient(name = "catalog-service")
public interface CatalogClient {
    
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable String id);
}

@Data
class ProductDTO {
    private String id;
    private String name;
    private BigDecimal unitPrice;
    private String measurementUnit;
}
```

**FleetClient.java** - Cliente HTTP para fleet-service:

```java
@FeignClient(name = "fleet-service")
public interface FleetClient {
    
    @GetMapping("/api/vehicles/{id}")
    VehicleDTO getVehicleById(@PathVariable String id);
}
```

---

### Tarea 3.1.4: Implementar MovementService con Validaciones Remotas

```java
@Service
@RequiredArgsConstructor
public class MovementService {
    
    private final MovementRepository movementRepository;
    private final CatalogClient catalogClient;
    private final FleetClient fleetClient;
    
    @Transactional
    public Movement createMovement(Movement movement) {
        // 1. Validar que el producto existe (llamada remota)
        ProductDTO product = catalogClient.getProductById(movement.getProductId());
        if (product == null) {
            throw new ProductNotFoundException("Producto no encontrado");
        }
        
        // 2. Si tiene vehículo, validar que existe
        if (movement.getVehicleId() != null) {
            VehicleDTO vehicle = fleetClient.getVehicleById(movement.getVehicleId());
            if (vehicle == null) {
                throw new VehicleNotFoundException("Vehículo no encontrado");
            }
        }
        
        // 3. Calcular subtotal
        movement.setSubtotal(
            movement.getQuantity().multiply(movement.getUnitPrice())
        );
        
        // 4. Generar ID
        if (movement.getId() == null) {
            movement.setId("MOV-" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        return movementRepository.save(movement);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal calculateStock(String productId) {
        BigDecimal entradas = movementRepository
            .sumQuantityByProductAndType(productId, MovementType.ENTRADA);
        
        BigDecimal salidas = movementRepository
            .sumQuantityByProductAndType(productId, MovementType.SALIDA);
        
        return entradas.subtract(salidas);
    }
}
```

---

### Tarea 3.1.5: Configurar Circuit Breaker

**application.yml:**

```yaml
resilience4j:
  circuitbreaker:
    instances:
      catalog-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
      fleet-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
```

**CatalogClient con fallback:**

```java
@FeignClient(name = "catalog-service", fallback = CatalogClientFallback.class)
public interface CatalogClient {
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable String id);
}

@Component
class CatalogClientFallback implements CatalogClient {
    @Override
    public ProductDTO getProductById(String id) {
        throw new ServiceUnavailableException("Catalog service no disponible");
    }
}
```

---

### Tarea 3.1.6: Habilitar Feign en Application

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // ← Importante
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
```

---

### Tarea 3.1.7: REST Controller con Endpoints Avanzados

```java
@RestController
@RequestMapping("/api/movements")
public class MovementController {
    
    @PostMapping("/entrada")
    public ResponseEntity<Movement> registrarEntrada(@RequestBody MovementRequest request) {
        Movement movement = new Movement();
        movement.setMovementType(Movement.MovementType.ENTRADA);
        movement.setProductId(request.getProductId());
        movement.setVehicleId(request.getVehicleId());
        movement.setQuantity(request.getQuantity());
        movement.setUnitPrice(request.getUnitPrice());
        
        Movement created = movementService.createMovement(movement);
        return ResponseEntity.created(URI.create("/api/movements/" + created.getId()))
            .body(created);
    }
    
    @GetMapping("/stock/{productId}")
    public ResponseEntity<StockResponse> getStock(@PathVariable String productId) {
        BigDecimal stock = movementService.calculateStock(productId);
        return ResponseEntity.ok(new StockResponse(productId, stock));
    }
    
    record StockResponse(String productId, BigDecimal stock) {}
}
```

---

### Tarea 3.1.8: Compilar, Dockerizar y Desplegar

*[Similar a Fase 2: crear Dockerfile, agregar a docker-compose.yml, compilar, desplegar]*

**Verificación:**
```bash
# Probar crear movimiento
curl -X POST http://localhost:8083/api/movements/entrada \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-001",
    "vehicleId": "VEH-001",
    "quantity": 100,
    "unitPrice": 8500
  }'

# Verificar stock
curl http://localhost:8083/api/stock/PROD-001
```

---

## 🔨 SESIÓN 3.2: Partners Service

**Duración:** 4-6 horas  
**Complejidad:** ⭐⭐⭐ (similar a Fase 2)

### Implementación

Seguir patrón similar a catalog-service y fleet-service:

1. Modelo `Supplier`
2. Repository
3. Service
4. Controller
5. Application
6. Dockerfile
7. Docker Compose
8. Deploy

**Endpoints:**
```
GET    /api/suppliers
GET    /api/suppliers/{id}
GET    /api/suppliers/nit/{nit}
POST   /api/suppliers
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}
```

---

## 🔨 SESIÓN 3.3: Invoicing Service (Transacciones Distribuidas)

**Duración:** 8-10 horas  
**Complejidad:** ⭐⭐⭐⭐⭐ (la más compleja)

### Tarea 3.3.1: Modelar Factura y DetalleFactura

```java
@Entity
@Table(name = "facturas")
public class Factura {
    @Id
    private String id;
    
    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;
    
    @Column(name = "supplier_id")
    private String supplierId;  // Referencia lógica
    
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<DetalleFactura> detalles;
    
    public enum EstadoFactura {
        PENDIENTE, PAGADA, ANULADA
    }
}

@Entity
@Table(name = "detalles_factura")
public class DetalleFactura {
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;
    
    @Column(name = "product_id")
    private String productId;
    
    private BigDecimal cantidad;
    
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    private BigDecimal subtotal;
}
```

---

### Tarea 3.3.2: Implementar Patrón SAGA

**InvoiceService con transacción distribuida:**

```java
@Service
@RequiredArgsConstructor
public class InvoiceService {
    
    private final FacturaRepository facturaRepository;
    private final CatalogClient catalogClient;
    private final PartnersClient partnersClient;
    private final InventoryClient inventoryClient;
    
    /**
     * SAGA: Crear factura con múltiples pasos
     * - Paso 1: Validar proveedor (Partners Service)
     * - Paso 2: Validar productos (Catalog Service)
     * - Paso 3: Crear factura local
     * - Paso 4: Registrar movimientos de entrada (Inventory Service)
     * - Rollback si falla cualquier paso
     */
    @Transactional
    public Factura crearFactura(FacturaRequest request) {
        Factura factura = new Factura();
        List<String> movimientosCreados = new ArrayList<>();
        
        try {
            // PASO 1: Validar proveedor
            SupplierDTO supplier = partnersClient.getSupplierById(request.getSupplierId());
            if (supplier == null) {
                throw new SupplierNotFoundException("Proveedor no encontrado");
            }
            
            // PASO 2: Validar y calcular totales
            BigDecimal subtotal = BigDecimal.ZERO;
            for (DetalleRequest detalle : request.getDetalles()) {
                ProductDTO product = catalogClient.getProductById(detalle.getProductId());
                if (product == null) {
                    throw new ProductNotFoundException("Producto no encontrado: " + detalle.getProductId());
                }
                
                BigDecimal lineSubtotal = detalle.getCantidad().multiply(detalle.getPrecioUnitario());
                subtotal = subtotal.add(lineSubtotal);
            }
            
            BigDecimal iva = subtotal.multiply(new BigDecimal("0.19"));
            BigDecimal total = subtotal.add(iva);
            
            // PASO 3: Crear factura
            factura.setId("FAC-" + UUID.randomUUID().toString().substring(0, 8));
            factura.setNumeroFactura(generarNumeroFactura());
            factura.setSupplierId(request.getSupplierId());
            factura.setFecha(LocalDateTime.now());
            factura.setSubtotal(subtotal);
            factura.setIva(iva);
            factura.setTotal(total);
            factura.setEstado(Factura.EstadoFactura.PENDIENTE);
            
            // Crear detalles
            List<DetalleFactura> detalles = new ArrayList<>();
            for (DetalleRequest detalleReq : request.getDetalles()) {
                DetalleFactura detalle = new DetalleFactura();
                detalle.setId("DET-" + UUID.randomUUID().toString().substring(0, 8));
                detalle.setFactura(factura);
                detalle.setProductId(detalleReq.getProductId());
                detalle.setCantidad(detalleReq.getCantidad());
                detalle.setPrecioUnitario(detalleReq.getPrecioUnitario());
                detalle.setSubtotal(detalleReq.getCantidad().multiply(detalleReq.getPrecioUnitario()));
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
            
            throw new FacturaCreationException("Error creando factura: " + e.getMessage(), e);
        }
    }
    
    private String generarNumeroFactura() {
        return "F-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
            + "-" + String.format("%04d", facturaRepository.count() + 1);
    }
}
```

---

### Tarea 3.3.3: Crear Feign Clients para Invoicing

```java
@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable String id);
}

@FeignClient(name = "partners-service")
public interface PartnersClient {
    @GetMapping("/api/suppliers/{id}")
    SupplierDTO getSupplierById(@PathVariable String id);
}

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @PostMapping("/api/movements/entrada")
    MovementDTO registrarEntrada(@RequestBody MovementRequest request);
    
    @DeleteMapping("/api/movements/{id}")
    void deleteMovement(@PathVariable String id);
}
```

---

### Tarea 3.3.4: Controller con Endpoint de Creación

```java
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @PostMapping
    public ResponseEntity<Factura> crearFactura(@Valid @RequestBody FacturaRequest request) {
        Factura factura = invoiceService.crearFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFactura(@PathVariable String id) {
        Factura factura = invoiceService.findById(id);
        return ResponseEntity.ok(factura);
    }
}

@Data
class FacturaRequest {
    private String supplierId;
    private List<DetalleRequest> detalles;
}

@Data
class DetalleRequest {
    private String productId;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}
```

---

### Tarea 3.3.5: Compilar y Desplegar

*[Similar a servicios anteriores]*

**Verificación COMPLEJA - Flujo E2E:**

```bash
# 1. Crear un proveedor
SUPPLIER_ID=$(curl -X POST http://localhost:8084/api/suppliers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Petrolera del Norte",
    "nit": "900111222-3",
    "contactPerson": "Carlos López",
    "phone": "3001112222"
  }' | jq -r '.id')

# 2. Obtener ID de producto existente
PRODUCT_ID=$(curl http://localhost:8081/api/products | jq -r '.[0].id')

# 3. Crear factura (debe crear movimientos automáticamente)
curl -X POST http://localhost:8085/api/invoices \
  -H "Content-Type: application/json" \
  -d "{
    \"supplierId\": \"$SUPPLIER_ID\",
    \"detalles\": [
      {
        \"productId\": \"$PRODUCT_ID\",
        \"cantidad\": 500,
        \"precioUnitario\": 8500
      }
    ]
  }" | jq

# 4. Verificar que el stock se actualizó
curl http://localhost:8083/api/stock/$PRODUCT_ID | jq
# Debe mostrar stock = 500 + entradas anteriores
```

---

## ✅ VERIFICACIÓN FINAL DE FASE 3

```bash
# 1. Health check de todos los servicios
./scripts/health-check.sh

# 2. Verificar servicios en Consul
curl http://localhost:8500/v1/catalog/services | jq
# Debe incluir: catalog, fleet, inventory, partners, invoicing

# 3. Docker status
docker-compose ps
# 10 contenedores UP: consul, config, 5 mysql, 5 microservicios

# 4. Test de comunicación entre servicios
curl -X POST http://localhost:8083/api/movements/entrada \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-001",
    "vehicleId": "VEH-001",
    "quantity": 100,
    "unitPrice": 8500
  }'
# Debe validar product y vehicle correctamente

# 5. Test de SAGA (factura)
[Ejecutar script de verificación compleja arriba]
```

---

## 📋 CRITERIOS DE ÉXITO FASE 3

- [x] inventory-service funcionando con comunicación a catalog y fleet
- [x] partners-service funcionando
- [x] invoicing-service funcionando con patrón SAGA
- [x] Feign Clients configurados y funcionando
- [x] Circuit breakers implementados
- [x] Transacciones distribuidas con rollback funcionando
- [x] 5 microservicios corriendo sin errores
- [x] Flujo E2E de factura completo

---

## 📝 REPORTE DE FASE 3

```markdown
# REPORTE - Fase 3 - Servicios Core

[Similar a reportes anteriores, documentar:]
- Sesiones completadas
- Problemas encontrados
- Artefactos generados
- Verificación de criterios
- Métricas (12 microservicios totales con infraestructura)

**Fase desbloqueada:** Fase 4 - API Gateway y Reportes
```

---

**Fase Creada:** 2025-11-19  
**Versión:** 1.0  
**Status:** Lista para Ejecución
