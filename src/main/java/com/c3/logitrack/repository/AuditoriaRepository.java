package com.c3.logitrack.repository;

import com.c3.logitrack.model.Auditoria;
import com.c3.logitrack.model.enums.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuario(String usuario);

    List<Auditoria> findByEntidad(String entidad);

    List<Auditoria> findByFechaHoraBetween(LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    List<Auditoria> findByOperacion(TipoOperacion operacion);

    List<Auditoria> findByFechaHoraAfter(LocalDateTime limite);
}