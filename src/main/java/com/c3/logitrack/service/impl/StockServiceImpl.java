package com.c3.logitrack.service.impl;

import com.c3.logitrack.exeption.ResourceNotFoundException;
import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.model.Producto;
import com.c3.logitrack.model.Stock;
import com.c3.logitrack.repository.BodegaRepository;
import com.c3.logitrack.repository.ProductoRepository;
import com.c3.logitrack.repository.StockRepository;
import com.c3.logitrack.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;

    public StockServiceImpl(StockRepository stockRepository,
                            BodegaRepository bodegaRepository,
                            ProductoRepository productoRepository) {
        this.stockRepository = stockRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Stock> listarTodos() {
        return stockRepository.findAll();
    }

    @Override
    public Optional<Stock> obtenerPorId(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public List<Stock> buscarPorBodega(Long bodegaId) {
        return stockRepository.findByBodegaId(bodegaId);
    }

    @Override
    public List<Stock> buscarPorProducto(Long productoId) {
        return stockRepository.findByProductoId(productoId);
    }

    @Override
    public Stock guardar(Stock stock) {
        // Validar existencia de la bodega
        if (stock.getBodega() == null || stock.getBodega().getId() == null) {
            throw new IllegalArgumentException("La bodega es obligatoria para registrar stock.");
        }
        Bodega bodega = bodegaRepository.findById(stock.getBodega().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id " + stock.getBodega().getId()));

        // Validar existencia del producto
        if (stock.getProducto() == null || stock.getProducto().getId() == null) {
            throw new IllegalArgumentException("El producto es obligatorio para registrar stock.");
        }
        Producto producto = productoRepository.findById(stock.getProducto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + stock.getProducto().getId()));

        stock.setBodega(bodega);
        stock.setProducto(producto);
        stock.setFechaActualizacion(LocalDateTime.now());

        return stockRepository.save(stock);
    }

    @Override
    public void eliminar(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe un registro de stock con id " + id);
        }
        stockRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Stock ajustarCantidad(Long bodegaId, Long productoId, int cantidad, boolean sumar) {
        Stock stock = stockRepository.findByBodegaIdAndProductoId(bodegaId, productoId)
                .orElseGet(() -> {
                    if (!sumar) {
                        throw new IllegalStateException("No existe stock para reducir en la bodega indicada.");
                    }
                    Stock nuevo = new Stock();
                    nuevo.setBodega(bodegaRepository.findById(bodegaId)
                            .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada.")));
                    nuevo.setProducto(productoRepository.findById(productoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado.")));
                    nuevo.setCantidad(0);
                    return nuevo;
                });

        int actual = Optional.ofNullable(stock.getCantidad()).orElse(0);
        int nuevoValor = sumar ? actual + cantidad : actual - cantidad;

        if (nuevoValor < 0) {
            throw new IllegalStateException("La cantidad de stock no puede ser negativa.");
        }

        stock.setCantidad(nuevoValor);
        stock.setFechaActualizacion(LocalDateTime.now());

        return stockRepository.save(stock);
    }
}