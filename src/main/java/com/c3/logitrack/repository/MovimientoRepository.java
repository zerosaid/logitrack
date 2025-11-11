package com.c3.logitrack.repository;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Buscar movimientos por tipo
    List<Movimiento> findByTipo(TipoMovimiento tipo);

    // Buscar movimientos entre fechas
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
