package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Auditoria;
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
    public Auditoria crearAuditoria(String entidad, Long entidadId, String operacion, String usuario, String valoresAntes, String valoresDespues) {
        Auditoria a = new Auditoria();
        a.setEntidad(entidad);
        a.setEntidadId(entidadId);
        a.setOperacion(operacion);
        a.setUsuario(usuario);
        a.setFechaHora(LocalDateTime.now());
        a.setValoresAntes(valoresAntes);
        a.setValoresDespues(valoresDespues);
        return auditoriaRepository.save(a);
    }

    @Override
    public List<Auditoria> listarPorUsuario(String usuario) {
        return auditoriaRepository.findByUsuario(usuario);
    }

    @Override
    public List<Auditoria> listarPorOperacion(String operacion) {
        return auditoriaRepository.findByOperacion(operacion);
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
    public List<Auditoria> buscarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidad(entidad);
    }

    @Override
    public List<Auditoria> buscarPorRango(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return auditoriaRepository.findByFechaHoraBetween(fechaDesde, fechaHasta);
    }

    @Override
    public void eliminarAuditoria(Long id) {
        auditoriaRepository.deleteById(id);
    }
}