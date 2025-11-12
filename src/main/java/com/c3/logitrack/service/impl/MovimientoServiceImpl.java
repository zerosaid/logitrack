package com.c3.logitrack.service.impl;

import com.c3.logitrack.exeption.ResourceNotFoundException;
import com.c3.logitrack.model.*;
import com.c3.logitrack.repository.*;
import com.c3.logitrack.service.AuditoriaService;
import com.c3.logitrack.service.MovimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoItemRepository movimientoItemRepository;
    private final StockRepository stockRepository;
    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;
    private final BodegaRepository bodegaRepository;

    public MovimientoServiceImpl(
            MovimientoRepository movimientoRepository,
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
        if (movimiento.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        // Validar bodegas
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

        // Asegurar que tenga fecha
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(LocalDateTime.now());
        }

        Movimiento saved = movimientoRepository.save(movimiento);

        // Validar ítems
        if (movimiento.getItems() == null || movimiento.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un producto en el movimiento.");
        }

        for (MovimientoItem item : movimiento.getItems()) {
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado id=" + item.getProducto().getId()));

            item.setMovimiento(saved);
            item.setProducto(producto);

            switch (movimiento.getTipo()) {
                case ENTRADA:
                    if (saved.getBodegaDestino() == null)
                        throw new IllegalArgumentException("Bodega destino requerida para ENTRADA.");
                    ajustarStock(saved.getBodegaDestino(), producto, item.getCantidad(), true);
                    break;

                case SALIDA:
                    if (saved.getBodegaOrigen() == null)
                        throw new IllegalArgumentException("Bodega origen requerida para SALIDA.");
                    ajustarStock(saved.getBodegaOrigen(), producto, item.getCantidad(), false);
                    break;

                case TRANSFERENCIA:
                    if (saved.getBodegaOrigen() == null || saved.getBodegaDestino() == null)
                        throw new IllegalArgumentException("Bodega origen y destino requeridas para TRANSFERENCIA.");
                    ajustarStock(saved.getBodegaOrigen(), producto, item.getCantidad(), false);
                    ajustarStock(saved.getBodegaDestino(), producto, item.getCantidad(), true);
                    break;
            }

            movimientoItemRepository.save(item);

            // Auditoría
            String usuario = (movimiento.getUsuario() != null)
                    ? movimiento.getUsuario().getUsername()
                    : "SYSTEM";
            auditoriaService.crearAuditoria(
                    "MovimientoItem",
                    item.getId(),
                    usuario,
                    "Registro de item de movimiento",
                    String.format("{\"productoId\":%d,\"cantidad\":%d}", producto.getId(), item.getCantidad()), usuario
            );
        }

        // Auditoría general
        String usuarioMov = (movimiento.getUsuario() != null)
                ? movimiento.getUsuario().getUsername()
                : "SYSTEM";
        auditoriaService.crearAuditoria(
                "Movimiento",
                saved.getId(),
                usuarioMov,
                "Registro de movimiento",
                "{}", usuarioMov
        );

        // Recalcular stock total
        Set<Long> productosAfectados = new HashSet<>();
        movimiento.getItems().forEach(i -> productosAfectados.add(i.getProducto().getId()));
        for (Long pid : productosAfectados) {
            recomputeProductoStock(pid);
        }

        return saved;
    }

    private void ajustarStock(Bodega bodega, Producto producto, Integer cantidad, boolean sumar) {
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

        String usuario = Optional.ofNullable(movimientoUsuarioName()).orElse("SYSTEM");
        auditoriaService.crearAuditoria(
                "Stock",
                stock.getId(),
                usuario,
                "Actualización de stock",
                String.format("{\"antes\":%d,\"despues\":%d}", antes, despues), usuario
        );
    }

    private void recomputeProductoStock(Long productoId) {
        List<Stock> byProducto = stockRepository.findByProductoId(productoId);
        int total = byProducto.stream()
                .mapToInt(s -> Optional.ofNullable(s.getCantidad()).orElse(0))
                .sum();

        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado id=" + productoId));

        p.setStock(total);
        productoRepository.save(p);
    }

    private String movimientoUsuarioName() {
        return null;
    }

    @Override
    public List<Movimiento> buscarPorTipo(String tipo) {
        return movimientoRepository.findByTipoIgnoreCase(tipo);
    }
}