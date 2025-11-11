package com.c3.logitrack.repository;

import com.c3.logitrack.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByBodegaId(Long bodegaId);

    List<Stock> findByProductoId(Long productoId);
}