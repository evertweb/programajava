package com.forestech.reports.controller;

import com.forestech.reports.dto.MovementReportDTO;
import com.forestech.reports.dto.StockReportDTO;
import com.forestech.reports.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    private final ReportsService reportsService;
    private final CacheManager cacheManager;
    
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
            .forEach(name -> {
                if (cacheManager.getCache(name) != null) {
                    cacheManager.getCache(name).clear();
                }
            });
        return ResponseEntity.ok("Cache limpiado");
    }
}
