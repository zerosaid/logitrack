package com.c3.logitrack.controller;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.service.MovimientoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarTodos() {
        try {
            List<Movimiento> movimientos = movimientoService.listarTodos();
            System.out.println("Movimientos encontrados (sin serializar): " + movimientos.size());
            String jsonResponse = objectMapper.writeValueAsString(movimientos);
            System.out.println("JSON generado: " + jsonResponse);
            movimientos.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            System.err.println("Error al listar movimientos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(@PathVariable Long id) {
        try {
            Optional<Movimiento> movimientoOpt = movimientoService.obtenerPorId(id);
            if (movimientoOpt.isPresent()) {
                Movimiento movimiento = movimientoOpt.get();
                limpiarRelaciones(movimiento);
                return ResponseEntity.ok(movimiento);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error al buscar movimiento por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Movimiento>> buscarPorRango(
            @RequestParam("desde") String desde,
            @RequestParam("hasta") String hasta) {
        try {
            LocalDateTime fechaDesde = LocalDateTime.parse(desde);
            LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
            List<Movimiento> resultados = movimientoService.buscarPorRango(fechaDesde, fechaHasta);
            resultados.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            System.err.println("Error al buscar por rango de fechas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Movimiento>> buscarPorTipo(@PathVariable String tipo) {
        try {
            List<Movimiento> resultados = movimientoService.buscarPorTipo(tipo.toUpperCase());
            resultados.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            System.err.println("Error al buscar por tipo de movimiento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@RequestBody Movimiento movimiento) {
        try {
            Movimiento nuevo = movimientoService.registrarMovimiento(movimiento);
            limpiarRelaciones(nuevo);
            return ResponseEntity.ok(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al registrar el movimiento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al registrar el movimiento: " + e.getMessage());
        }
    }

    private void limpiarRelaciones(Movimiento m) {
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
                if (i.getProducto() != null) {
                    i.getProducto().setStocks(null);
                    i.getProducto().setMovimientoItems(null);
                }
            });
        }
        if (m.getUsuario() != null) {
            m.getUsuario().setMovimientos(null); // Añadido para User si tiene relación
        }
    }
}
