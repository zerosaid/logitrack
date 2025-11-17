package com.c3.logitrack.controller;

import com.c3.logitrack.dto.BodegaCreateDTO;
import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.model.User;
import com.c3.logitrack.service.BodegaService;
import com.c3.logitrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Bodegas", description = "Gestión de bodegas. Solo ADMIN puede crear, actualizar o eliminar.")
@SecurityRequirement(name = "bearerAuth")
public class BodegaController {

    private final BodegaService bodegaService;
    private final UserService userService;

    public BodegaController(BodegaService bodegaService, UserService userService) {
        this.bodegaService = bodegaService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las bodegas", description = "Accesible por ADMIN y EMPLEADO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de bodegas"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<Bodega>> listarTodas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        bodegas.forEach(this::limpiarBodega);
        return ResponseEntity.ok(bodegas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bodega por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bodega encontrada"),
        @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    })
    public ResponseEntity<Bodega> obtenerPorId(@Parameter(description = "ID de la bodega") @PathVariable Long id) {
        return bodegaService.obtenerBodegaPorId(id)
                .map(b -> {
                    limpiarBodega(b);
                    return ResponseEntity.ok(b);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva bodega", description = "Solo usuarios con rol ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o nulos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: solo ADMIN"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
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

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bodega existente", description = "Solo ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bodega actualizada"),
        @ApiResponse(responseCode = "404", description = "Bodega no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bodega", description = "Solo ADMIN")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Bodega eliminada"),
        @ApiResponse(responseCode = "404", description = "Bodega no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
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

    @GetMapping("/dashboard/total")
    @Operation(summary = "Total de bodegas", description = "Para dashboard")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Conteo total de bodegas"))
    public ResponseEntity<Map<String, Object>> totalBodegas() {
        List<Bodega> bodegas = bodegaService.listarBodegas();
        Map<String, Object> response = new HashMap<>();
        response.put("totalBodegas", bodegas.size());
        return ResponseEntity.ok(response);
    }

    // Método auxiliar para evitar ciclos en JSON
    private void limpiarBodega(Bodega b) {
        b.setStocks(null);
        b.setMovimientosOrigen(null);
        b.setMovimientosDestino(null);
    }
}