package com.c3.logitrack.Tetsjoshep.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c3.logitrack.service.ReporteService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@Tag(name = "ReportesTest", description = "Reportes avanzados dentro de Test")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/reportesTest")
public class ReporteController {

    @GetMapping("/movimientos-recientes")
    public ResponseEntity<Object> getMovimientoRecientes() {
        return ResponseEntity.ok(ReporteService.getMovimientoRecientes());
    }
}