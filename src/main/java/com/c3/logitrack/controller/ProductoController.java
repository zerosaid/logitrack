package com.c3.logitrack.controller;

import com.c3.logitrack.dto.ProductoCreateDTO;
import com.c3.logitrack.model.Producto;
import com.c3.logitrack.model.User;
import com.c3.logitrack.service.ProductoService;
import com.c3.logitrack.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"}, allowCredentials = "true")
public class ProductoController {

    private final ProductoService productoService;
    private final UserService userService;

    public ProductoController(ProductoService productoService, UserService userService) {
        this.productoService = productoService;
        this.userService = userService;
    }

    // Listar todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoService.listarProductos();
        productos.forEach(this::limpiarProducto);
        return ResponseEntity.ok(productos);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(p -> {
                    limpiarProducto(p);
                    return ResponseEntity.ok(p);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nuevo producto (solo ADMIN)
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody ProductoCreateDTO productoDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden crear productos");
            }
            if (productoDTO == null) {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud no puede ser nulo");
            }
            Producto nuevo = productoService.crearProducto(productoDTO);
            limpiarProducto(nuevo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el producto: " + e.getMessage());
        }
    }

    // Actualizar producto existente (solo ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody ProductoCreateDTO productoDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden actualizar productos");
            }
            return productoService.actualizarProducto(id, productoDTO)
                    .map(p -> {
                        limpiarProducto(p);
                        return ResponseEntity.ok(p);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el producto: " + e.getMessage());
        }
    }

    // Eliminar producto (solo ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden eliminar productos");
            }
            boolean eliminado = productoService.eliminarProducto(id);
            if (eliminado) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el producto: " + e.getMessage());
        }
    }

    // Endpoint para dashboard: total de productos
    @GetMapping("/dashboard/total")
    public ResponseEntity<Map<String, Object>> totalProductos() {
        List<Producto> productos = productoService.listarProductos();
        Map<String, Object> response = new HashMap<>();
        response.put("totalProductos", productos.size());
        return ResponseEntity.ok(response);
    }

    // Método auxiliar para limpiar relaciones cíclicas
    private void limpiarProducto(Producto p) {
        p.setStocks(null);
        p.setMovimientoItems(null);
    }
}