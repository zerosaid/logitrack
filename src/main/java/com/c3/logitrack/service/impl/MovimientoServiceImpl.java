package com.c3.logitrack.service.impl;

import com.c3.logitrack.exeption.ResourceNotFoundException;
import com.c3.logitrack.model.*;
import com.c3.logitrack.model.enums.TipoMovimiento;
import com.c3.logitrack.repository.*;
import com.c3.logitrack.service.AuditoriaService;
import com.c3.logitrack.service.MovimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoItemRepository movimientoItemRepository;
    private final StockRepository stockRepository;
    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;
    private final BodegaRepository bodegaRepository;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository,
                                 MovimientoItemRepository movimientoItemRepository,
                                 StockRepository stockRepository,
                                 ProductoRepository productoRepository,
                                 AuditoriaService auditoriaService,
                                 BodegaRepository bodegaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.movimientoItemRepository = movimientoItemRepository;
        this.stockRepository = stockRepository;
        this.productoRepository = productoRepository;
        this.auditoriaService = auditoriaService;
        this.bodegaRepository = bodegaRepository;
    }

    @Override
    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAll();
    }

    @Override
    public Optional<Movimiento> obtenerPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    @Override
    public List<Movimiento> buscarPorRango(LocalDateTime desde, LocalDateTime hasta) {
        return movimientoRepository.findByFechaBetween(desde, hasta);
    }

    @Override
    @Transactional
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        validarMovimiento(movimiento);

        Movimiento saved = movimientoRepository.save(movimiento);
        String usuario = movimiento.getUsuario() != null ? movimiento.getUsuario().getUsername() : "SYSTEM";

        procesarItems(saved, usuario);
        auditar("Movimiento", saved.getId(), usuario, "Registro de movimiento", "{}");

        return saved;
    }

    @Override
    @Transactional
    public Movimiento actualizarMovimiento(Long id, Movimiento movimiento) {
        Movimiento existente = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado id=" + id));

        validarMovimiento(movimiento);

        existente.setTipo(movimiento.getTipo());
        existente.setBodegaOrigen(movimiento.getBodegaOrigen());
        existente.setBodegaDestino(movimiento.getBodegaDestino());
        existente.setUsuario(movimiento.getUsuario());
        existente.setFecha(LocalDateTime.now());

        // Eliminar items antiguos
        if (existente.getItems() != null) {
            existente.getItems().forEach(i -> movimientoItemRepository.delete(i));
        }
        existente.setItems(movimiento.getItems());

        Movimiento actualizado = movimientoRepository.save(existente);
        String usuario = movimiento.getUsuario() != null ? movimiento.getUsuario().getUsername() : "SYSTEM";

        procesarItems(actualizado, usuario);
        auditar("Movimiento", actualizado.getId(), usuario, "Actualización de movimiento", "{}");

        return actualizado;
    }

    @Override
    @Transactional
    public boolean eliminarMovimiento(Long id) {
        Optional<Movimiento> movOpt = movimientoRepository.findById(id);
        if (movOpt.isEmpty()) return false;

        Movimiento mov = movOpt.get();
        String usuario = mov.getUsuario() != null ? mov.getUsuario().getUsername() : "SYSTEM";

        if (mov.getItems() != null) {
            for (MovimientoItem item : mov.getItems()) {
                Producto producto = item.getProducto();
                switch (mov.getTipo()) {
                    case ENTRADA -> ajustarStock(mov.getBodegaDestino(), producto, item.getCantidad(), false, usuario);
                    case SALIDA -> ajustarStock(mov.getBodegaOrigen(), producto, item.getCantidad(), true, usuario);
                    case TRANSFERENCIA -> {
                        ajustarStock(mov.getBodegaOrigen(), producto, item.getCantidad(), true, usuario);
                        ajustarStock(mov.getBodegaDestino(), producto, item.getCantidad(), false, usuario);
                    }
                }
            }
        }

        movimientoRepository.delete(mov);
        auditar("Movimiento", id, usuario, "Eliminación de movimiento", "{}");
        return true;
    }

    @Override
    public List<Movimiento> buscarPorTipo(String tipo) {
        try {
            TipoMovimiento tipoEnum = TipoMovimiento.valueOf(tipo.toUpperCase());
            return movimientoRepository.findByTipo(tipoEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de movimiento no válido: " + tipo);
        }
    }

    @Override
    public List<Movimiento> listarUltimos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        Pageable pageable = PageRequest.of(0, cantidad, Sort.by("fecha").descending());
        return movimientoRepository.findAllByOrderByFechaDesc(pageable);
    }

    // =================== MÉTODOS AUXILIARES ===================
    private void validarMovimiento(Movimiento movimiento) {
        if (movimiento.getTipo() == null)
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");

        if (movimiento.getItems() == null || movimiento.getItems().isEmpty())
            throw new IllegalArgumentException("Debe registrar al menos un producto en el movimiento.");

        if (movimiento.getBodegaOrigen() != null && movimiento.getBodegaOrigen().getId() != null) {
            Bodega origen = bodegaRepository.findById(movimiento.getBodegaOrigen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bodega origen no encontrada"));
            movimiento.setBodegaOrigen(origen);
        }

        if (movimiento.getBodegaDestino() != null && movimiento.getBodegaDestino().getId() != null) {
            Bodega destino = bodegaRepository.findById(movimiento.getBodegaDestino().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bodega destino no encontrada"));
            movimiento.setBodegaDestino(destino);
        }

        if (movimiento.getFecha() == null)
            movimiento.setFecha(LocalDateTime.now());
    }

    private void procesarItems(Movimiento saved, String usuario) {
        Set<Long> productosAfectados = new HashSet<>();

        for (MovimientoItem item : saved.getItems()) {
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado id=" + item.getProducto().getId()));

            item.setMovimiento(saved);
            item.setProducto(producto);

            switch (saved.getTipo()) {
                case ENTRADA -> {
                    if (saved.getBodegaDestino() == null)
                        throw new IllegalArgumentException("Bodega destino requerida para ENTRADA.");
                    ajustarStock(saved.getBodegaDestino(), producto, item.getCantidad(), true, usuario);
                }
                case SALIDA -> {
                    if (saved.getBodegaOrigen() == null)
                        throw new IllegalArgumentException("Bodega origen requerida para SALIDA.");
                    ajustarStock(saved.getBodegaOrigen(), producto, item.getCantidad(), false, usuario);
                }
                case TRANSFERENCIA -> {
                    if (saved.getBodegaOrigen() == null || saved.getBodegaDestino() == null)
                        throw new IllegalArgumentException("Bodega origen y destino requeridas para TRANSFERENCIA.");
                    ajustarStock(saved.getBodegaOrigen(), producto, item.getCantidad(), false, usuario);
                    ajustarStock(saved.getBodegaDestino(), producto, item.getCantidad(), true, usuario);
                }
            }

            movimientoItemRepository.save(item);
            auditar("MovimientoItem", item.getId(), usuario, "Registro de item de movimiento",
                    String.format("{\"productoId\":%d,\"cantidad\":%d}", producto.getId(), item.getCantidad()));
            productosAfectados.add(producto.getId());
        }

        productosAfectados.forEach(this::recomputeProductoStock);
    }

    private void ajustarStock(Bodega bodega, Producto producto, Integer cantidad, boolean sumar, String usuario) {
        Optional<Stock> opt = stockRepository.findByBodegaIdAndProductoId(bodega.getId(), producto.getId());

        Stock stock = opt.orElseGet(() -> {
            if (!sumar)
                throw new IllegalStateException("No hay stock del producto en la bodega origen.");
            Stock nuevo = new Stock();
            nuevo.setBodega(bodega);
            nuevo.setProducto(producto);
            nuevo.setCantidad(0);
            return nuevo;
        });

        int antes = Optional.ofNullable(stock.getCantidad()).orElse(0);
        int despues = sumar ? antes + cantidad : antes - cantidad;

        if (despues < 0)
            throw new IllegalStateException("Stock insuficiente en bodega " + bodega.getNombre() +
                    " para el producto " + producto.getNombre());

        stock.setCantidad(despues);
        stock.setFechaActualizacion(LocalDateTime.now());
        stockRepository.save(stock);

        auditar("Stock", stock.getId(), usuario, "Actualización de stock",
                String.format("{\"antes\":%d,\"despues\":%d}", antes, despues));
    }

    private void recomputeProductoStock(Long productoId) {
        List<Stock> byProducto = stockRepository.findByProductoId(productoId);
        int total = byProducto.stream().mapToInt(s -> Optional.ofNullable(s.getCantidad()).orElse(0)).sum();

        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado id=" + productoId));

        p.setStockMin(total);
        productoRepository.save(p);
    }

    private void auditar(String entidad, Long entidadId, String usuario, String accion, String detalle) {
        auditoriaService.crearAuditoria(entidad, entidadId, usuario, accion, detalle, usuario);
    }
}