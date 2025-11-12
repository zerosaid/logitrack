package com.c3.logitrack.service;

import com.c3.logitrack.model.Auditoria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditoriaService {
    Auditoria crearAuditoria(String entidad, Long entidadId, String operacion, String usuario, String valoresAntes, String valoresDespues);
    List<Auditoria> listarPorUsuario(String usuario);
    List<Auditoria> listarPorOperacion(String operacion);
    List<Auditoria> listarTodos();
    Optional<Auditoria> obtenerPorId(Long id);
    List<Auditoria> buscarPorEntidad(String entidad);
    List<Auditoria> buscarPorRango(LocalDateTime fechaDesde, LocalDateTime fechaHasta);
    void eliminarAuditoria(Long id);
}
