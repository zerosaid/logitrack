package com.c3.logitrack.controller;

import com.c3.logitrack.model.Producto;
import com.c3.logitrack.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            System.out.println("Productos encontrados (sin serializar): " + productos.size());
            // Limpiar relaciones antes de cualquier serialización
            productos.forEach(p -> {
                if (p.getStocks() != null)
                    p.setStocks(null);
                if (p.getMovimientoItems() != null)
                    p.setMovimientoItems(null);
            });
            String jsonResponse = objectMapper.writeValueAsString(productos);
            System.out.println("JSON generado (primeros 1000 chars): "
                    + jsonResponse.substring(0, Math.min(jsonResponse.length(), 1000)));
            if (productos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        try {
            return productoService.buscarPorId(id)
                    .map(p -> {
                        if (p.getStocks() != null)
                            p.setStocks(null);
                        if (p.getMovimientoItems() != null)
                            p.setMovimientoItems(null);
                        return ResponseEntity.ok(p);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error al buscar producto por ID " + id + ": " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Producto creado con ID: " + creado.getId());
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            System.err.println("Error al crear producto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            System.out.println(
                    "Datos recibidos para actualización (raw): " + new ObjectMapper().writeValueAsString(producto));
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            Producto actualizado = productoService.actualizar(id, producto);
            System.out.println("Producto actualizado con ID: " + actualizado.getId());
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            System.err.println("Error al actualizar producto con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminar(id);
            System.out.println("Producto eliminado con ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error al eliminar producto con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}