package com.forestech.reports.service;

import com.forestech.reports.dto.MovementReportDTO;
import com.forestech.reports.dto.StockReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
                p.unit_price
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
            if (item.getUnitPrice() != null) {
                item.setValorTotal(stock.multiply(item.getUnitPrice()));
            } else {
                item.setValorTotal(BigDecimal.ZERO);
            }
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
        
        try {
            return inventoryJdbcTemplate.queryForObject(
                sql, 
                BigDecimal.class, 
                productId
            );
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    @Cacheable("movements-by-date")
    public List<MovementReportDTO> reporteMovimientosPorFecha(LocalDate from, LocalDate to) {
        String sql = """
            SELECT created_at, movement_type, product_id, quantity
            FROM movements
            WHERE created_at BETWEEN ? AND ?
        """;

        return inventoryJdbcTemplate.query(sql, (rs, rowNum) -> {
            MovementReportDTO dto = new MovementReportDTO();
            dto.setDate(rs.getDate("created_at").toLocalDate());
            dto.setType(rs.getString("movement_type"));
            dto.setProductId(rs.getString("product_id"));
            dto.setQuantity(rs.getBigDecimal("quantity"));
            return dto;
        }, from, to);
    }
}
