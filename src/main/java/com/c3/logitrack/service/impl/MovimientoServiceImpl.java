package com.c3.logitrack.service.impl;

import com.c3.logitrack.exeption.ResourceNotFoundException;
import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.model.MovimientoItem;
import com.c3.logitrack.model.Producto;
import com.c3.logitrack.model.Stock;
import com.c3.logitrack.model.enums.TipoMovimiento;
import com.c3.logitrack.repository.BodegaRepository;
import com.c3.logitrack.repository.MovimientoItemRepository;
import com.c3.logitrack.repository.MovimientoRepository;
import com.c3.logitrack.repository.ProductoRepository;
import com.c3.logitrack.repository.StockRepository;
import com.c3.logitrack.service.AuditoriaService;
import com.c3.logitrack.service.MovimientoService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Validaciones básicas
        if (movimiento.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        if (movimiento.getItems() == null || movimiento.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un producto en el movimiento.");
        }

        // Validar y cargar bodegas
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

        // Fecha por defecto
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(LocalDateTime.now());
        }

        // Guardar movimiento antes de items
        Movimiento saved = movimientoRepository.save(movimiento);

        // Usuario para auditoría
        String usuario = (movimiento.getUsuario() != null)
                ? movimiento.getUsuario().getUsername()
                : "SYSTEM";

        // Procesar items
        Set<Long> productosAfectados = new HashSet<>();
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
                    ajustarStock(saved.getBodegaDestino(), producto, item.getCantidad(), true, usuario);
                    break;

                case SALIDA:
                    if (saved.getBodegaOrigen() == null)
                        throw new IllegalArgumentException("Bodega origen requerida para SALIDA.");
                    ajustarStock(saved.getBodegaOrigen(), producto, item.getCantidad(), false, usuario);
                    break;

                case TRANSFERENCIA:
                    if (saved.getBodegaOrigen() == null || saved.getBodegaDestino() == null)
                        throw new IllegalArgumentException("Bodega origen y destino requeridas para TRANSFERENCIA.");
                    ajustarStock(saved.getBodegaOrigen(), producto, item.getCantidad(), false, usuario);
                    ajustarStock(saved.getBodegaDestino(), producto, item.getCantidad(), true, usuario);
                    break;
            }

            movimientoItemRepository.save(item);

            // Auditoría item
            auditar("MovimientoItem", item.getId(), usuario, "Registro de item de movimiento",
                    String.format("{\"productoId\":%d,\"cantidad\":%d}", producto.getId(), item.getCantidad()));

            productosAfectados.add(producto.getId());
        }

        // Auditoría general movimiento
        auditar("Movimiento", saved.getId(), usuario, "Registro de movimiento", "{}");

        // Recalcular stock total por producto
        productosAfectados.forEach(this::recomputeProductoStock);

        return saved;
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

        // Auditoría stock
        auditar("Stock", stock.getId(), usuario, "Actualización de stock",
                String.format("{\"antes\":%d,\"despues\":%d}", antes, despues));
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

    private void auditar(String entidad, Long entidadId, String usuario, String accion, String detalle) {
        auditoriaService.crearAuditoria(entidad, entidadId, usuario, accion, detalle, usuario);
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
}