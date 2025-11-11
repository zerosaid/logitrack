package com.c3.logitrack.repository;

import com.c3.logitrack.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Filtrar por usuario
    List<Auditoria> findByUsuario(String usuario);

    // Filtrar por tipo de operación
    List<Auditoria> findByOperacion(String operacion);
}