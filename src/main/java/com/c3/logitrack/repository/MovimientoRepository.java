package com.c3.logitrack.repository;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.model.enums.TipoMovimiento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    // Listar por tipo de movimiento
    List<Movimiento> findByTipo(TipoMovimiento tipo);

    // Listar movimientos entre dos fechas
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Obtener el último movimiento
    Movimiento findTopByOrderByFechaDesc();

    // Obtener los N últimos movimientos
    List<Movimiento> findAllByOrderByFechaDesc(Pageable pageable);
}
