package com.c3.logitrack.service;

import com.c3.logitrack.model.Auditoria;
import java.util.List;

public interface AuditoriaService {
    Auditoria crearAuditoria(String entidad, Long entidadId, String operacion, String usuario, String valoresAntes, String valoresDespues);
    List<Auditoria> listarPorUsuario(String usuario);
    List<Auditoria> listarPorOperacion(String operacion);
    List<Auditoria> listarTodos();
}
