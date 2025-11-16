package com.c3.logitrack.controller;

import com.c3.logitrack.model.Producto;
import com.c3.logitrack.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoService.listarTodos();
        if (productos.isEmpty()) return ResponseEntity.noContent().build();
        productos.forEach(p -> {
            p.setStocks(null);
            p.setMovimientoItems(null);
        });
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(p -> {
                    p.setStocks(null);
                    p.setMovimientoItems(null);
                    return ResponseEntity.ok(p);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body(null);
        Producto creado = productoService.guardar(producto);
        creado.setStocks(null);
        creado.setMovimientoItems(null);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body(null);
        Producto actualizado = productoService.actualizar(id, producto);
        actualizado.setStocks(null);
        actualizado.setMovimientoItems(null);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}