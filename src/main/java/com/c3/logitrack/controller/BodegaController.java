package com.c3.logitrack.controller;

import com.c3.logitrack.dto.BodegaCreateDTO;
import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.model.User;
import com.c3.logitrack.service.BodegaService;
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
@RequestMapping("/api/bodegas")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"}, allowCredentials = "true")
public class BodegaController {

    private final BodegaService bodegaService;
    private final UserService userService;

    public BodegaController(BodegaService bodegaService, UserService userService) {
        this.bodegaService = bodegaService;
        this.userService = userService;
    }

    // Listar todas las bodegas
    @GetMapping
    public ResponseEntity<List<Bodega>> listarTodas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        bodegas.forEach(this::limpiarBodega);
        return ResponseEntity.ok(bodegas);
    }

    // Obtener bodega por ID
    @GetMapping("/{id}")
    public ResponseEntity<Bodega> obtenerPorId(@PathVariable Long id) {
        return bodegaService.obtenerBodegaPorId(id)
                .map(b -> {
                    limpiarBodega(b);
                    return ResponseEntity.ok(b);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva bodega (solo ADMIN)
    @PostMapping
    public ResponseEntity<?> crearBodega(@RequestBody BodegaCreateDTO bodegaDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden crear bodegas");
            }
            if (bodegaDTO == null) {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud no puede ser nulo");
            }
            Bodega nueva = bodegaService.crearBodega(bodegaDTO);
            limpiarBodega(nueva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la bodega: " + e.getMessage());
        }
    }

    // Actualizar bodega existente (solo ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarBodega(@PathVariable Long id, @RequestBody BodegaCreateDTO bodegaDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden actualizar bodegas");
            }
            return bodegaService.actualizarBodega(id, bodegaDTO)
                    .map(b -> {
                        limpiarBodega(b);
                        return ResponseEntity.ok(b);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la bodega: " + e.getMessage());
        }
    }

    // Eliminar bodega (solo ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarBodega(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden eliminar bodegas");
            }
            boolean eliminado = bodegaService.eliminarBodega(id);
            if (eliminado) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la bodega: " + e.getMessage());
        }
    }

    // Endpoint para dashboard: total de bodegas
    @GetMapping("/dashboard/total")
    public ResponseEntity<Map<String, Object>> totalBodegas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        Map<String, Object> response = new HashMap<>();
        response.put("totalBodegas", bodegas.size());
        return ResponseEntity.ok(response);
    }

    // Método auxiliar para limpiar relaciones ciclicas
    private void limpiarBodega(Bodega b) {
        b.setStocks(null);
        b.setMovimientosOrigen(null);
        b.setMovimientosDestino(null);
    }
}