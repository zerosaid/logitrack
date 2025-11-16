package com.c3.logitrack.controller;

import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.service.BodegaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bodegas")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class BodegaController {

    private final BodegaService bodegaService;

    public BodegaController(BodegaService bodegaService) {
        this.bodegaService = bodegaService;
    }

    // === LISTAR TODAS LAS BODEGAS ===
    @GetMapping
    public ResponseEntity<List<Bodega>> listarTodas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        bodegas.forEach(this::limpiarBodega);
        return ResponseEntity.ok(bodegas);
    }

    // === OBTENER BODEGA POR ID ===
    @GetMapping("/{id}")
    public ResponseEntity<Bodega> obtenerPorId(@PathVariable Long id) {
        return bodegaService.obtenerBodegaPorId(id)
                .map(b -> {
                    limpiarBodega(b);
                    return ResponseEntity.ok(b);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // === CREAR NUEVA BODEGA ===
    @PostMapping
    public ResponseEntity<Bodega> crearBodega(@RequestBody Bodega bodega) {
        bodega.setId(null); // evitar sobrescribir
        Bodega nueva = bodegaService.crearBodega(bodega);
        limpiarBodega(nueva);
        return ResponseEntity.ok(nueva);
    }

    // === ACTUALIZAR BODEGA EXISTENTE ===
    @PutMapping("/{id}")
    public ResponseEntity<Bodega> actualizarBodega(@PathVariable Long id, @RequestBody Bodega detalles) {
        return bodegaService.actualizarBodega(id, detalles)
                .map(b -> {
                    limpiarBodega(b);
                    return ResponseEntity.ok(b);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // === ELIMINAR BODEGA ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        boolean eliminado = bodegaService.eliminarBodega(id);
        if (eliminado) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // === ENDPOINT PARA DASHBOARD: TOTAL DE BODEGAS ===
    @GetMapping("/dashboard/total")
    public ResponseEntity<Map<String, Object>> totalBodegas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        Map<String, Object> response = new HashMap<>();
        response.put("totalBodegas", bodegas.size());
        return ResponseEntity.ok(response);
    }

    // === MÉTODO AUXILIAR PARA LIMPIAR RELACIONES CICLADAS ===
    private void limpiarBodega(Bodega b) {
        b.setStocks(null);
        b.setMovimientosOrigen(null);
        b.setMovimientosDestino(null);
    }
}
