package com.c3.logitrack.Tets.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.service.MovimientoService;

import io.swagger.v3.oas.annotations.Operation;

public class MovimientoControllerTest {

    @GetMapping("/movimientos/recientes")
    @Operation(summary = "Obtener los ultimos 10 movimientos registrados")
    public ResponseEntity<List<Movimiento>> listarRecientes() {
        try {
            List<Movimiento> recientes = MovimientoService.listarRecientes(10);
            recientes.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(recientes);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    private void limpiarRelaciones(Movimiento m) {
        if (m == null) return;
        if (m.getBodegaOrigen() != null) {
            m.getBodegaOrigen().setStocks(null);
            m.getBodegaOrigen().setMovimientosOrigen(null);
            m.getBodegaOrigen().setMovimientosDestino(null);
        }
        if (m.getBodegaDestino() != null) {
            m.getBodegaDestino().setStocks(null);
            m.getBodegaDestino().setMovimientosOrigen(null);
            m.getBodegaDestino().setMovimientosDestino(null);
        }
        if (m.getItems() != null) {
            m.getItems().forEach(i -> {
                if (i != null && i.getProducto() != null) {
                    i.getProducto().setStocks(null);
                    i.getProducto().setMovimientoItems(null);
                }
            });
        }
        if (m.getUsuario() != null) {
            m.getUsuario().setMovimientos(null);
        }
    }
}