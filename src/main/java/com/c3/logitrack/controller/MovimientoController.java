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
@CrossOrigin(origins = "*")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    // === LISTAR TODOS LOS MOVIMIENTOS ===
    @GetMapping
    public ResponseEntity<List<Movimiento>> listarTodos() {
        List<Movimiento> movimientos = movimientoService.listarTodos();

        // Evitar ciclos JSON
        movimientos.forEach(this::limpiarRelaciones);

        return ResponseEntity.ok(movimientos);
    }

    // === OBTENER MOVIMIENTO POR ID ===
    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(@PathVariable Long id) {
        Optional<Movimiento> movimientoOpt = movimientoService.obtenerPorId(id);

        if (movimientoOpt.isPresent()) {
            Movimiento movimiento = movimientoOpt.get();
            limpiarRelaciones(movimiento);
            return ResponseEntity.ok(movimiento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // === BUSCAR POR RANGO DE FECHAS ===
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

    // === BUSCAR POR TIPO DE MOVIMIENTO ===
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Movimiento>> buscarPorTipo(@PathVariable String tipo) {
        List<Movimiento> resultados = movimientoService.buscarPorTipo(tipo.toUpperCase());
        resultados.forEach(this::limpiarRelaciones);

        return ResponseEntity.ok(resultados);
    }

    // === REGISTRAR NUEVO MOVIMIENTO ===
    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@RequestBody Movimiento movimiento) {
        try {
            Movimiento nuevo = movimientoService.registrarMovimiento(movimiento);
            limpiarRelaciones(nuevo);
            return ResponseEntity.ok(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al registrar el movimiento: " + e.getMessage());
        }
    }

    // === MÉTODO PRIVADO PARA EVITAR CICLOS JSON ===
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
                }
            });
        }
    }
}
