# 🌐 FASE 4: API GATEWAY Y REPORTES
> **Puerta de entrada única y servicio de analytics**

---

## 📋 Información de la Fase

| Atributo | Valor |
|----------|-------|
| **Fase** | 4 de 5 |
| **Nombre** | API Gateway y Reports Service |
| **Duración Estimada** | 12-16 horas |
| **Complejidad** | ⭐⭐⭐ (Avanzado) |
| **Sesiones** | 2 (4.1 y 4.2) |
| **Dependencias** | Fases 1, 2 y 3 completadas |
| **Desbloquea** | Fase 5 - Frontend y Finalización |

---

## 🎯 Objetivos de la Fase

- [x] **API Gateway** como punto de entrada único
- [x] Routing dinámico a todos los microservicios
- [x] Rate limiting y circuit breaker en gateway
- [x] **Reports Service** con datos agregados
- [x] Redis cache para reportes costosos
- [x] 7 microservicios completamente funcionales

---

## ✅ Pre-requisitos

```bash
# Verificar que 5 microservicios core están UP
./scripts/health-check.sh

curl http://localhost:8500/v1/catalog/services | jq
# Debe mostrar: catalog, fleet, inventory, partners, invoicing
```

---

## 🔨 SESIÓN 4.1: API Gateway

**Duración:** 6-8 horas

### Tarea 4.1.1: Crear Proyecto con Spring Cloud Gateway

```bash
cd ~/forestech-microservices/services/api-gateway
mkdir -p src/main/java/com/forestech/gateway
mkdir -p src/main/resources
```

**pom.xml:**

```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    
    <!-- Consul Discovery -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    </dependency>
    
    <!-- Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Resilience4j -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
    </dependency>
    
    <!-- Redis for Rate Limiting -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>
</dependencies>
```

---

### Tarea 4.1.2: Configurar Routes en application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    consul:
      host: consul
      port: 8500
      discovery:
        instance-id: ${spring.application.name}:${random.value}
    gateway:
      discovery:
        locator:
          enabled: true  # Descubrimiento automático vía Consul
          lower-case-service-id: true
      routes:
        # Catalog Service
        - id: catalog-service
          uri: lb://catalog-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: catalogCircuitBreaker
                fallbackUri: forward:/fallback/catalog
            - name: RequestRateLimiter
              args:
                redis-rate-limiter:
                  replenishRate: 10  # requests por segundo
                  burstCapacity: 20
        
        # Fleet Service
        - id: fleet-service
          uri: lb://fleet-service
          predicates:
            - Path=/api/vehicles/**
          filters:
            - name: CircuitBreaker
              args:
                name: fleetCircuitBreaker
        
        # Inventory Service
        - id: inventory-service
          uri: lb://inventory-service
          predicates:
            - Path=/api/movements/**, /api/stock/**
          filters:
            - name: CircuitBreaker
              args:
                name: inventoryCircuitBreaker
        
        # Partners Service
        - id: partners-service
          uri: lb://partners-service
          predicates:
            - Path=/api/suppliers/**
        
        # Invoicing Service
        - id: invoicing-service
          uri: lb://invoicing-service
          predicates:
            - Path=/api/invoices/**
        
        # Reports Service (próxima sesión)
        - id: reports-service
          uri: lb://reports-service
          predicates:
            - Path=/api/reports/**

# Redis Configuration (para rate limiting)
  redis:
    host: redis
    port: 6379

# Circuit Breaker Configuration
resilience4j:
  circuitbreaker:
    instances:
      catalogCircuitBreaker:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
      fleetCircuitBreaker:
        sliding-window-size: 10
        failure-rate-threshold: 50
      inventoryCircuitBreaker:
        sliding-window-size: 10
        failure-rate-threshold: 50

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty: INFO
```

---

### Tarea 4.1.3: Crear Filtros Personalizados

**LoggingFilter.java** - Log de todas las requests:

```java
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        ServerHttpRequest request = exchange.getRequest();
        
        log.info("[{}] Request: {} {} from {}",
            requestId,
            request.getMethod(),
            request.getPath(),
            request.getRemoteAddress()
        );
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] Response: {} - {}ms",
                requestId,
                exchange.getResponse().getStatusCode(),
                duration
            );
        }));
    }
    
    @Override
    public int getOrder() {
        return -1; // Alta prioridad
    }
}
```

---

### Tarea 4.1.4: Crear Fallback Controller

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/catalog")
    public ResponseEntity<Map<String, String>> catalogFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error", "Catalog service temporalmente no disponible",
                "message", "Por favor intente más tarde"
            ));
    }
    
    @GetMapping("/default")
    public ResponseEntity<Map<String, String>> defaultFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("error", "Servicio no disponible"));
    }
}
```

---

### Tarea 4.1.5: Agregar Redis a Docker Compose

```yaml
# Agregar al docker-compose.yml
redis:
  image: redis:7-alpine
  container_name: redis
  ports:
    - "6379:6379"
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 5
  networks:
    - forestech-network
```

---

### Tarea 4.1.6: Aplicación Principal

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

---

### Tarea 4.1.7: Compilar y Desplegar

```bash
cd ~/forestech-microservices

# Levantar Redis primero
docker-compose up -d redis

# Compilar gateway
cd services/api-gateway
mvn clean package -DskipTests

# Agregar gateway al docker-compose.yml
# [Similar a microservicios anteriores]

# Desplegar
docker-compose up -d api-gateway

# Verificar
curl http://localhost:8080/api/products | jq
# Debe routear a catalog-service y retornar productos
```

---

### ✅ Verificación Sesión 4.1

```bash
# Todas las APIs deben ser accesibles via gateway (puerto 8080)
curl http://localhost:8080/api/products | jq
curl http://localhost:8080/api/vehicles | jq
curl http://localhost:8080/api/suppliers | jq
curl http://localhost:8080/api/movements | jq

# Verificar rate limiting (hacer 25 requests rápidas)
for i in {1..25}; do
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/products
done
# Las últimas deben retornar 429 (Too Many Requests)

# Verificar fallback (detener catalog-service)
docker-compose stop catalog-service
curl http://localhost:8080/api/products
# Debe retornar mensaje de fallback

# Reiniciar
docker-compose start catalog-service
```

---

## 🔨 SESIÓN 4.2: Reports Service

**Duración:** 6-8 horas

### Tarea 4.2.1: Crear Proyecto con Acceso Multi-Database

```bash
cd ~/forestech-microservices/services/reports-service
mkdir -p src/main/java/com/forestech/reports
```

**pom.xml** - Similar a otros servicios, pero SIN JPA (solo JdbcTemplate)

---

### Tarea 4.2.2: Configurar Múltiples DataSources

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "datasource.catalog")
    public DataSource catalogDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties(prefix = "datasource.fleet")
    public DataSource fleetDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties(prefix = "datasource.inventory")
    public DataSource inventoryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    public JdbcTemplate catalogJdbcTemplate(@Qualifier("catalogDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
    
    @Bean
    public JdbcTemplate inventoryJdbcTemplate(@Qualifier("inventoryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
```

**application.yml:**

```yaml
datasource:
  catalog:
    jdbc-url: jdbc:mysql://mysql-catalog:3306/catalog_db
    username: root
    password: ${MYSQL_ROOT_PASSWORD}
  fleet:
    jdbc-url: jdbc:mysql://mysql-fleet:3306/fleet_db
    username: root
    password: ${MYSQL_ROOT_PASSWORD}
  inventory:
    jdbc-url: jdbc:mysql://mysql-inventory:3306/inventory_db
    username: root
    password: ${MYSQL_ROOT_PASSWORD}

spring:
  redis:
    host: redis
    port: 6379
  cache:
    type: redis
    redis:
      time-to-live: 300000  # 5 minutos
```

---

### Tarea 4.2.3: Implementar ReportsService

```java
@Service
@RequiredArgsConstructor
public class ReportsService {
    
    private final JdbcTemplate catalogJdbcTemplate;
    private final JdbcTemplate inventoryJdbcTemplate;
    
    @Cacheable("stock-report")
    public List<StockReportDTO> reporteStockTodos() {
        String sql = """
            SELECT 
                p.id,
                p.name,
                p.unit_price,
                COALESCE(
                    (SELECT SUM(m.quantity) FROM movements m 
                     WHERE m.product_id = p.id AND m.movement_type = 'ENTRADA'), 0
                ) - COALESCE(
                    (SELECT SUM(m.quantity) FROM movements m 
                     WHERE m.product_id = p.id AND m.movement_type = 'SALIDA'), 0
                ) AS stock_actual
            FROM oil_products p
            WHERE p.is_active = true
        """;
        
        // Query en catalog DB para productos
        List<StockReportDTO> report = catalogJdbcTemplate.query(sql, (rs, rowNum) -> {
            StockReportDTO dto = new StockReportDTO();
            dto.setProductId(rs.getString("id"));
            dto.setProductName(rs.getString("name"));
            dto.setUnitPrice(rs.getBigDecimal("unit_price"));
            dto.setStock(BigDecimal.ZERO); // Calcularemos después
            return dto;
        });
        
        // Query en inventory DB para cada producto
        for (StockReportDTO item : report) {
            BigDecimal stock = calcularStockProducto(item.getProductId());
            item.setStock(stock);
            item.setValorTotal(stock.multiply(item.getUnitPrice()));
        }
        
        return report;
    }
    
    private BigDecimal calcularStockProducto(String productId) {
        String sql = """
            SELECT 
                COALESCE(SUM(CASE WHEN movement_type = 'ENTRADA' THEN quantity ELSE 0 END), 0) -
                COALESCE(SUM(CASE WHEN movement_type = 'SALIDA' THEN quantity ELSE 0 END), 0) as stock
            FROM movements
            WHERE product_id = ?
        """;
        
        return inventoryJdbcTemplate.queryForObject(
            sql, 
            BigDecimal.class, 
            productId
        );
    }
    
    @Cacheable("movements-by-date")
    public List<MovementReportDTO> reporteMovimientosPorFecha(LocalDate from, LocalDate to) {
        // Similar: query aggregado en inventory DB
    }
}
```

---

### Tarea 4.2.4: Controller con Endpoints de Reportes

```java
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    private final ReportsService reportsService;
    
    @GetMapping("/stock")
    public ResponseEntity<List<StockReportDTO>> reporteStock() {
        return ResponseEntity.ok(reportsService.reporteStockTodos());
    }
    
    @GetMapping("/movements")
    public ResponseEntity<List<MovementReportDTO>> reporteMovimientos(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(reportsService.reporteMovimientosPorFecha(from, to));
    }
    
    @DeleteMapping("/cache")
    public ResponseEntity<String> limpiarCache() {
        cacheManager.getCacheNames()
            .forEach(name -> cacheManager.getCache(name).clear());
        return ResponseEntity.ok("Cache limpiado");
    }
}
```

---

### Tarea 4.2.5: Compilar y Desplegar

```bash
mvn clean package -DskipTests
docker-compose up -d reports-service

# Verificar
curl http://localhost:8086/api/reports/stock | jq
curl http://localhost:8080/api/reports/stock | jq  # Via gateway
```

---

## ✅ VERIFICACIÓN FINAL FASE 4

```bash
# 1. Todos los servicios via Gateway
curl http://localhost:8080/api/products | jq
curl http://localhost:8080/api/vehicles | jq
curl http://localhost:8080/api/suppliers | jq
curl http://localhost:8080/api/movements | jq
curl http://localhost:8080/api/invoices | jq
curl http://localhost:8080/api/reports/stock | jq

# 2. Verificar servicios en Consul
curl http://localhost:8500/v1/catalog/services | jq
# Debe incluir: catalog, fleet, inventory, partners, invoicing, reports, api-gateway

# 3. Docker compose
docker-compose ps
# 14 contenedores: infraestructura (4) + databases (5) + microservicios (7)

# 4. Cache de Redis funcionando
curl http://localhost:8080/api/reports/stock | jq  # Primera llamada (lenta)
curl http://localhost:8080/api/reports/stock | jq  # Segunda llamada (rápida, desde cache)
```

---

## 📋 CRITERIOS DE ÉXITO FASE 4

- [x] API Gateway redirigiendo a todos los microservicios
- [x] Rate limiting funcionando
- [x] Circuit breakers configurados
- [x] Fallbacks implementados
- [x] Reports Service generando reportes
- [x] Redis cache funcionando
- [x] 7 microservicios + infraestructura operativos

---

**Fase Creada:** 2025-11-19  
**Versión:** 1.0  
**Status:** Lista para Ejecución
