package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Auditoria;
import com.c3.logitrack.model.enums.TipoOperacion;
import com.c3.logitrack.repository.AuditoriaRepository;
import com.c3.logitrack.service.AuditoriaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public Auditoria crearAuditoria(String entidad, Long entidadId, String usuario, String operacion, String valoresAntes, String valoresDespues) {
        try {
            Auditoria a = new Auditoria();
            a.setEntidad(entidad);
            a.setEntidadId(entidadId);
            a.setUsuario(usuario);
            a.setOperacion(TipoOperacion.valueOf(operacion.toUpperCase()));
            a.setFechaHora(LocalDateTime.now());
            a.setValoresAntes(valoresAntes != null ? valoresAntes : "");
            a.setValoresDespues(valoresDespues != null ? valoresDespues : "");
            return auditoriaRepository.save(a);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Operación inválida: " + operacion);
        }
    }

    @Override
    public List<Auditoria> listarTodos() {
        return auditoriaRepository.findAll();
    }

    @Override
    public Optional<Auditoria> obtenerPorId(Long id) {
        return auditoriaRepository.findById(id);
    }

    @Override
    public List<Auditoria> listarPorUsuario(String usuario) {
        return auditoriaRepository.findByUsuario(usuario);
    }

    @Override
    public List<Auditoria> listarPorOperacion(String operacion) {
        try {
            return auditoriaRepository.findByOperacion(TipoOperacion.valueOf(operacion.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Operación inválida: " + operacion);
        }
    }

    @Override
    public List<Auditoria> buscarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidad(entidad);
    }

    @Override
    public List<Auditoria> buscarPorRango(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        if (fechaDesde == null || fechaHasta == null || fechaHasta.isBefore(fechaDesde)) {
            throw new IllegalArgumentException("Rango de fechas inválido.");
        }
        return auditoriaRepository.findByFechaHoraBetween(fechaDesde, fechaHasta);
    }

    @Override
    public boolean eliminarAuditoria(Long id) {
        if (auditoriaRepository.existsById(id)) {
            auditoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Auditoria> listarPendientes() {
        // Lógica placeholder: Filtra auditorías sin procesar (ejemplo)
        LocalDateTime limite = LocalDateTime.now().minusDays(7); // Auditorías de la última semana
        return auditoriaRepository.findByFechaHoraAfter(limite); // Ajusta según tu definición de "pendiente"
    }
}