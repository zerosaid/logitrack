package com.c3.logitrack.controller;

import com.c3.logitrack.dto.MovimientoCreateDTO;
import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.model.User;
import com.c3.logitrack.service.MovimientoService;
import com.c3.logitrack.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000" }, allowCredentials = "true")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final UserService userService;

    public MovimientoController(MovimientoService movimientoService, UserService userService) {
        this.movimientoService = movimientoService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Movimiento>> listarMovimientos() {
        List<Movimiento> movimientos = movimientoService.listarMovimientos();
        movimientos.forEach(this::limpiarRelaciones);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> obtenerPorId(@PathVariable Long id) {
        return movimientoService.obtenerPorId(id)
                .map(m -> {
                    limpiarRelaciones(m);
                    return ResponseEntity.ok(m);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Movimiento>> buscarPorRango(
            @RequestParam("desde") String desde,
            @RequestParam("hasta") String hasta) {
        try {
            LocalDateTime fechaDesde = LocalDateTime.parse(desde);
            LocalDateTime fechaHasta = LocalDateTime.parse(hasta);
            if (fechaHasta.isBefore(fechaDesde)) {
                return ResponseEntity.badRequest().body(null);
            }
            List<Movimiento> resultados = movimientoService.buscarPorRango(fechaDesde, fechaHasta);
            resultados.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(resultados);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Movimiento>> buscarPorTipo(@PathVariable String tipo) {
        try {
            List<Movimiento> resultados = movimientoService.buscarPorTipo(tipo.toUpperCase());
            resultados.forEach(this::limpiarRelaciones);
            return ResponseEntity.ok(resultados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarMovimiento(
            @RequestBody MovimientoCreateDTO movimientoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo administradores pueden registrar movimientos");
            }
            if (movimientoDTO == null || movimientoDTO.getItems() == null) {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud o los ítems no pueden ser nulos");
            }
            Movimiento nuevo = movimientoService.registrarMovimiento(movimientoDTO);
            limpiarRelaciones(nuevo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el movimiento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMovimiento(
            @PathVariable Long id,
            @RequestBody MovimientoCreateDTO movimientoDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo administradores pueden actualizar movimientos");
            }
            if (movimientoDTO == null || movimientoDTO.getItems() == null) {
                return ResponseEntity.badRequest().body("El cuerpo de la solicitud o los ítems no pueden ser nulos");
            }
            return movimientoService.obtenerPorId(id)
                    .map(m -> {
                        Movimiento actualizado = movimientoService.actualizarMovimiento(id, movimientoDTO);
                        limpiarRelaciones(actualizado);
                        return ResponseEntity.ok(actualizado);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el movimiento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMovimiento(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            User user = userService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            if (!"ADMIN".equals(user.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Solo administradores pueden eliminar movimientos");
            }
            boolean eliminado = movimientoService.eliminarMovimiento(id);
            return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el movimiento: " + e.getMessage());
        }
    }

    @GetMapping("/recientes")
    public ResponseEntity<Map<String, Object>> movimientosRecientes() {
        List<Movimiento> recientes = movimientoService.listarUltimos(5);
        recientes.forEach(this::limpiarRelaciones);
        Map<String, Object> response = new HashMap<>();
        response.put("totalRecientes", recientes.size());
        response.put("movimientos", recientes);
        return ResponseEntity.ok(response);
    }

    private void limpiarRelaciones(Movimiento m) {
        if (m == null)
            return;
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
                if (i != null && i.getProducto() != null) {
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