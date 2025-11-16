package com.c3.logitrack.controller;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.service.MovimientoService;
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

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarTodos() {
        List<Movimiento> movimientos = movimientoService.listarTodos();
        movimientos.forEach(this::limpiarRelaciones);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(@PathVariable Long id) {
        Optional<Movimiento> movimientoOpt = movimientoService.obtenerPorId(id);
        return movimientoOpt.map(m -> {
            limpiarRelaciones(m);
            return ResponseEntity.ok(m);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Movimiento>> buscarPorRango(
            @RequestParam("desde") String desde,
            @RequestParam("hasta") String hasta) {
        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
        List<Movimiento> resultados = movimientoService.buscarPorRango(fechaDesde, fechaHasta);
        resultados.forEach(this::limpiarRelaciones);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Movimiento>> buscarPorTipo(@PathVariable String tipo) {
        List<Movimiento> resultados = movimientoService.buscarPorTipo(tipo.toUpperCase());
        resultados.forEach(this::limpiarRelaciones);
        return ResponseEntity.ok(resultados);
    }

    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@RequestBody Movimiento movimiento) {
        Movimiento nuevo = movimientoService.registrarMovimiento(movimiento);
        limpiarRelaciones(nuevo);
        return ResponseEntity.ok(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMovimiento(@PathVariable Long id, @RequestBody Movimiento movimiento) {
        Optional<Movimiento> movExistenteOpt = movimientoService.obtenerPorId(id);
        if (movExistenteOpt.isPresent()) {
            Movimiento actualizado = movimientoService.actualizarMovimiento(id, movimiento);
            limpiarRelaciones(actualizado);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMovimiento(@PathVariable Long id) {
        boolean eliminado = movimientoService.eliminarMovimiento(id);
        if (eliminado) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
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
            m.getUsuario().setMovimientos(null);
        }
    }
}
