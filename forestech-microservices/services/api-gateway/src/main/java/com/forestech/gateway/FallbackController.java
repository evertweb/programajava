package com.forestech.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador de fallback para Circuit Breakers del API Gateway.
 * Proporciona respuestas amigables cuando los servicios no están disponibles.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/catalog")
    public ResponseEntity<Map<String, Object>> catalogFallback() {
        return buildFallbackResponse("catalog-service", "Catálogo de productos");
    }
    
    @GetMapping("/fleet")
    public ResponseEntity<Map<String, Object>> fleetFallback() {
        return buildFallbackResponse("fleet-service", "Gestión de flota");
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> inventoryFallback() {
        return buildFallbackResponse("inventory-service", "Gestión de inventario");
    }
    
    @GetMapping("/partners")
    public ResponseEntity<Map<String, Object>> partnersFallback() {
        return buildFallbackResponse("partners-service", "Gestión de proveedores");
    }
    
    @GetMapping("/invoicing")
    public ResponseEntity<Map<String, Object>> invoicingFallback() {
        return buildFallbackResponse("invoicing-service", "Facturación");
    }
    
    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> reportsFallback() {
        return buildFallbackResponse("reports-service", "Reportes");
    }
    
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> defaultFallback() {
        return buildFallbackResponse("unknown-service", "Servicio");
    }
    
    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String serviceName, String serviceDescription) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "error", "SERVICE_UNAVAILABLE",
                "service", serviceName,
                "message", serviceDescription + " temporalmente no disponible. Por favor intente más tarde.",
                "timestamp", LocalDateTime.now().toString(),
                "retryAfter", 30
            ));
    }
}
