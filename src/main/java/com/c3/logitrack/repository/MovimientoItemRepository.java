package com.c3.logitrack.repository;

import com.c3.logitrack.model.MovimientoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoItemRepository extends JpaRepository<MovimientoItem, Long> {
}