package com.c3.logitrack.controller;

import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.service.BodegaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        // Evitar ciclos infinitos al serializar
        bodegas.forEach(b -> {
            b.setStocks(null);
            b.setMovimientosOrigen(null);
            b.setMovimientosDestino(null);
        });

        return ResponseEntity.ok(bodegas);
    }

    // === OBTENER BODEGA POR ID ===
    @GetMapping("/{id}")
    public ResponseEntity<Bodega> obtenerPorId(@PathVariable Long id) {
        try {
            Bodega bodega = bodegaService.obtenerBodegaPorId(id);
            bodega.setStocks(null);
            bodega.setMovimientosOrigen(null);
            bodega.setMovimientosDestino(null);
            return ResponseEntity.ok(bodega);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // === CREAR NUEVA BODEGA ===
    @PostMapping
    public ResponseEntity<Bodega> crearBodega(@RequestBody Bodega bodega) {
        bodega.setId(null); // evitar sobrescribir por accidente
        Bodega nueva = bodegaService.crearBodega(bodega);
        return ResponseEntity.ok(nueva);
    }

    // === ACTUALIZAR BODEGA EXISTENTE ===
    @PutMapping("/{id}")
    public ResponseEntity<Bodega> actualizarBodega(@PathVariable Long id, @RequestBody Bodega detalles) {
        try {
            Bodega actualizada = bodegaService.actualizarBodega(id, detalles);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // === ELIMINAR BODEGA ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        try {
            bodegaService.eliminarBodega(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
