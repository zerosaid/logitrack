package com.c3.logitrack.controller;

import com.c3.logitrack.model.Producto;
import com.c3.logitrack.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class ProductoController {

    private final ProductoService productoService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductoController(ProductoService productoService, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        try {
            List<Producto> productos = productoService.listarTodos();
            if (productos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // Limpiar relaciones para evitar serialización circular
            productos.forEach(p -> {
                p.setStocks(null);
                p.setMovimientoItems(null);
            });
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        try {
            return productoService.buscarPorId(id)
                    .map(p -> {
                        p.setStocks(null);
                        p.setMovimientoItems(null);
                        return ResponseEntity.ok(p);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        try {
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            Producto creado = productoService.guardar(producto);
            creado.setStocks(null); // Evitar serialización circular
            creado.setMovimientoItems(null);
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            Producto actualizado = productoService.actualizar(id, producto);
            actualizado.setStocks(null);
            actualizado.setMovimientoItems(null);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}