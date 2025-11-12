package com.c3.logitrack.controller;

import com.c3.logitrack.model.Auditoria;
import com.c3.logitrack.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@CrossOrigin(origins = "*")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // === LISTAR TODAS LAS AUDITORÍAS ===
    @GetMapping
    public ResponseEntity<List<Auditoria>> listarTodas() {
        List<Auditoria> auditorias = auditoriaService.listarTodos();
        return ResponseEntity.ok(auditorias);
    }

    // === OBTENER AUDITORÍA POR ID ===
    @GetMapping("/{id}")
    public ResponseEntity<Auditoria> obtenerPorId(@PathVariable Long id) {
        return auditoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === BUSCAR POR USUARIO ===
    @GetMapping("/usuario/{username}")
    public ResponseEntity<List<Auditoria>> buscarPorUsuario(@PathVariable String username) {
        List<Auditoria> auditorias = auditoriaService.listarPorUsuario(username);
        return ResponseEntity.ok(auditorias);
    }

    // === BUSCAR POR ENTIDAD ===
    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<Auditoria>> buscarPorEntidad(@PathVariable String entidad) {
        List<Auditoria> auditorias = auditoriaService.buscarPorEntidad(entidad);
        return ResponseEntity.ok(auditorias);
    }

    // === BUSCAR POR RANGO DE FECHAS ===
    @GetMapping("/rango")
    public ResponseEntity<List<Auditoria>> buscarPorRango(
            @RequestParam("desde") String desde,
            @RequestParam("hasta") String hasta) {

        LocalDateTime fechaDesde = LocalDateTime.parse(desde);
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta);

        List<Auditoria> auditorias = auditoriaService.buscarPorRango(fechaDesde, fechaHasta);
        return ResponseEntity.ok(auditorias);
    }

    // === ELIMINAR AUDITORÍA === (opcional, útil para limpieza)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAuditoria(@PathVariable Long id) {
        if (auditoriaService.obtenerPorId(id).isPresent()) {
            auditoriaService.eliminarAuditoria(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
