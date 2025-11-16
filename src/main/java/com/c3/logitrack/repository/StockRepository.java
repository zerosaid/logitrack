package com.c3.logitrack.repository;

import com.c3.logitrack.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // ✅ Buscar todos los registros de stock por bodega
    List<Stock> findByBodegaId(Long bodegaId);

    // ✅ Buscar todos los registros de stock por producto
    List<Stock> findByProductoId(Long productoId);

    // ✅ Buscar un registro específico por bodega y producto (para actualizaciones de cantidad)
    Optional<Stock> findByBodegaIdAndProductoId(Long bodegaId, Long productoId);

    // ✅ Consulta personalizada: obtener productos con stock bajo (menor a un umbral)
    @Query("SELECT s FROM Stock s WHERE s.cantidad < :umbral")
    List<Stock> findProductosConStockBajo(@Param("umbral") int umbral);

    // ✅ Consulta agregada: stock total agrupado por bodega
    @Query("SELECT s.bodega.nombre, SUM(s.cantidad) FROM Stock s GROUP BY s.bodega.nombre")
    List<Object[]> obtenerStockTotalPorBodega();

    // ✅ Consulta agregada: stock total agrupado por producto
    @Query("SELECT s.producto.nombre, SUM(s.cantidad) FROM Stock s GROUP BY s.producto.nombre")
    List<Object[]> obtenerStockTotalPorProducto();
}
