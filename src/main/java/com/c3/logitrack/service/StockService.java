package com.c3.logitrack.service;

import com.c3.logitrack.model.Stock;
import java.util.List;
import java.util.Optional;

public interface StockService {

    List<Stock> listarTodos();

    Optional<Stock> obtenerPorId(Long id);

    List<Stock> buscarPorBodega(Long bodegaId);

    List<Stock> buscarPorProducto(Long productoId);

    Stock guardar(Stock stock);

    void eliminar(Long id);

    Stock ajustarCantidad(Long bodegaId, Long productoId, int cantidad, boolean sumar);
}
