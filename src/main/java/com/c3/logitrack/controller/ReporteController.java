package com.c3.logitrack.controller;

import com.c3.logitrack.service.ReporteService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Reportes", description = "Reportes avanzados de inventario y auditoría")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // Reporte de movimientos por rango de fechas
    @GetMapping("/movimientos-por-fecha")
    public ResponseEntity<List<Map<String, Object>>> getMovimientosPorFecha(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(reporteService.getMovimientosPorFecha(inicio, fin));
    }

    // Reporte de stock por bodega
    @GetMapping("/stock-por-bodega")
    public ResponseEntity<Map<String, Integer>> getStockPorBodega() {
        return ResponseEntity.ok(reporteService.getStockPorBodega());
    }

    // Reporte de auditoría
    @GetMapping("/auditoria")
    public ResponseEntity<List<Map<String, Object>>> getAuditoria() {
        return ResponseEntity.ok(reporteService.getAuditoria());
    }
}