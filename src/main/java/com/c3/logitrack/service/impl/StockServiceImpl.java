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

/**
 * Implementación del servicio para gestionar el stock de productos en bodegas.
 */
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

    /**
     * Lista todos los registros de stock.
     *
     * @return Lista de todos los stocks en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Stock> listarTodos() {
        return stockRepository.findAll();
    }

    /**
     * Obtiene un registro de stock por su ID.
     *
     * @param id Identificador del stock.
     * @return Optional con el stock si existe, o vacío si no.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Stock> obtenerPorId(Long id) {
        return stockRepository.findById(id);
    }

    /**
     * Busca todos los registros de stock asociados a una bodega.
     *
     * @param bodegaId Identificador de la bodega.
     * @return Lista de stocks para la bodega especificada.
     * @throws ResourceNotFoundException si la bodega no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Stock> buscarPorBodega(Long bodegaId) {
        if (!bodegaRepository.existsById(bodegaId)) {
            throw new ResourceNotFoundException("Bodega no encontrada con ID: " + bodegaId);
        }
        return stockRepository.findByBodegaId(bodegaId);
    }

    /**
     * Busca todos los registros de stock asociados a un producto.
     *
     * @param productoId Identificador del producto.
     * @return Lista de stocks para el producto especificado.
     * @throws ResourceNotFoundException si el producto no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Stock> buscarPorProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + productoId);
        }
        return stockRepository.findByProductoId(productoId);
    }

    /**
     * Guarda o actualiza un registro de stock.
     *
     * @param stock El objeto Stock a guardar.
     * @return El stock guardado.
     * @throws IllegalArgumentException si los datos son inválidos.
     * @throws ResourceNotFoundException si la bodega o producto no existen.
     */
    @Override
    @Transactional
    public Stock guardar(Stock stock) {
        if (stock == null) {
            throw new IllegalArgumentException("El registro de stock no puede ser nulo.");
        }
        if (stock.getBodega() == null || stock.getBodega().getId() == null) {
            throw new IllegalArgumentException("La bodega es obligatoria para registrar stock.");
        }
        if (stock.getProducto() == null || stock.getProducto().getId() == null) {
            throw new IllegalArgumentException("El producto es obligatorio para registrar stock.");
        }
        if (stock.getCantidad() == null || stock.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad de stock debe ser un valor no negativo.");
        }

        Bodega bodega = bodegaRepository.findById(stock.getBodega().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + stock.getBodega().getId()));
        Producto producto = productoRepository.findById(stock.getProducto().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + stock.getProducto().getId()));

        stock.setBodega(bodega);
        stock.setProducto(producto);
        stock.setFechaActualizacion(LocalDateTime.now());

        return stockRepository.save(stock);
    }

    /**
     * Elimina un registro de stock por su ID.
     *
     * @param id Identificador del stock.
     * @throws ResourceNotFoundException si el stock no existe.
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe un registro de stock con ID: " + id);
        }
        stockRepository.deleteById(id);
    }

    /**
     * Ajusta la cantidad de stock para un producto en una bodega.
     *
     * @param bodegaId   Identificador de la bodega.
     * @param productoId Identificador del producto.
     * @param cantidad   Cantidad a sumar o restar.
     * @param sumar      True para sumar, false para restar.
     * @return El stock actualizado.
     * @throws IllegalArgumentException si la cantidad es inválida.
     * @throws ResourceNotFoundException si la bodega o producto no existen.
     * @throws IllegalStateException si el stock es insuficiente o no existe para restar.
     */
    @Override
    @Transactional
    public Stock ajustarCantidad(Long bodegaId, Long productoId, int cantidad, boolean sumar) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        Bodega bodega = bodegaRepository.findById(bodegaId)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con ID: " + bodegaId));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        Stock stock = stockRepository.findByBodegaAndProducto(bodega, producto)
                .orElseGet(() -> {
                    if (!sumar) {
                        throw new IllegalStateException(
                                String.format("No existe stock para el producto %s en la bodega %s.",
                                        producto.getNombre(), bodega.getNombre()));
                    }
                    Stock nuevo = new Stock();
                    nuevo.setBodega(bodega);
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(0);
                    return nuevo;
                });

        int actual = stock.getCantidad() != null ? stock.getCantidad() : 0;
        int nuevoValor = sumar ? actual + cantidad : actual - cantidad;

        if (nuevoValor < 0) {
            throw new IllegalStateException(
                    String.format("Stock insuficiente para el producto %s en la bodega %s. Cantidad actual: %d, Intentando restar: %d.",
                            producto.getNombre(), bodega.getNombre(), actual, cantidad));
        }

        stock.setCantidad(nuevoValor);
        stock.setFechaActualizacion(LocalDateTime.now());
        return stockRepository.save(stock);
    }
}