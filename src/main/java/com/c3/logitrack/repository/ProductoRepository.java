package com.c3.logitrack.repository;

import com.c3.logitrack.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Filtro para productos con stock bajo
    List<Producto> findByStockLessThan(int stock);
}
