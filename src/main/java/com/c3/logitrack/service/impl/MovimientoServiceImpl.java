package com.c3.logitrack.service.impl;

import com.c3.logitrack.dto.MovimientoCreateDTO;
import com.c3.logitrack.model.*;
import com.c3.logitrack.model.enums.TipoMovimiento;
import com.c3.logitrack.repository.BodegaRepository;
import com.c3.logitrack.repository.MovimientoRepository;
import com.c3.logitrack.repository.ProductoRepository;
import com.c3.logitrack.repository.UserRepository;
import com.c3.logitrack.service.MovimientoService;
import com.c3.logitrack.service.StockService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final StockService stockService;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository,
                                BodegaRepository bodegaRepository,
                                ProductoRepository productoRepository,
                                UserRepository userRepository,
                                StockService stockService) {
        this.movimientoRepository = movimientoRepository;
        this.bodegaRepository = bodegaRepository;
        this.productoRepository = productoRepository;
        this.userRepository = userRepository;
        this.stockService = stockService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Movimiento> obtenerPorId(Long id) {
        return movimientoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> buscarPorRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Fechas inválidas: 'inicio' debe ser anterior a 'fin'");
        }
        return movimientoRepository.findByFechaBetween(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> buscarPorTipo(String tipo) {
        try {
            TipoMovimiento tipoMovimiento = TipoMovimiento.valueOf(tipo.toUpperCase());
            return movimientoRepository.findByTipo(tipoMovimiento);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + tipo);
        }
    }

    @Override
    @Transactional
    public Movimiento registrarMovimiento(MovimientoCreateDTO movimientoDTO) {
        if (movimientoDTO.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }
        if (movimientoDTO.getUsuarioId() == null) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (movimientoDTO.getItems() == null || movimientoDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un ítem.");
        }
        if (movimientoDTO.getTipo() == TipoMovimiento.ENTRADA && movimientoDTO.getBodegaDestinoId() == null) {
            throw new IllegalArgumentException("La bodega destino es obligatoria para entradas.");
        }
        if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA && movimientoDTO.getBodegaOrigenId() == null) {
            throw new IllegalArgumentException("La bodega origen es obligatoria para salidas.");
        }
        if (movimientoDTO.getTipo() == TipoMovimiento.TRANSFERENCIA &&
                (movimientoDTO.getBodegaOrigenId() == null || movimientoDTO.getBodegaDestinoId() == null)) {
            throw new IllegalArgumentException("Ambas bodegas son obligatorias para transferencias.");
        }

        User usuario = userRepository.findById(movimientoDTO.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Bodega bodegaOrigen = movimientoDTO.getBodegaOrigenId() != null ?
                bodegaRepository.findById(movimientoDTO.getBodegaOrigenId())
                        .orElseThrow(() -> new IllegalArgumentException("Bodega origen no encontrada.")) : null;
        Bodega bodegaDestino = movimientoDTO.getBodegaDestinoId() != null ?
                bodegaRepository.findById(movimientoDTO.getBodegaDestinoId())
                        .orElseThrow(() -> new IllegalArgumentException("Bodega destino no encontrada.")) : null;

        // Crear y guardar el movimiento básico primero para obtener el ID
        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(movimientoDTO.getTipo());
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        movimiento = movimientoRepository.save(movimiento); // Guardar primero para obtener el ID

        // Procesar y asociar los ítems después de que el movimiento tenga un ID
        List<MovimientoItem> items = new ArrayList<>();
        for (MovimientoCreateDTO.MovimientoItemDTO itemDTO : movimientoDTO.getItems()) {
            if (itemDTO.getProductoId() == null || itemDTO.getCantidad() == null || itemDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("Producto y cantidad son obligatorios y deben ser mayores que 0.");
            }
            if (itemDTO.getPrecioUnitario() == null) {
                throw new IllegalArgumentException("El precio unitario es obligatorio.");
            }
            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
            MovimientoItem item = new MovimientoItem();
            item.setMovimiento(movimiento);
            item.setProducto(producto);
            item.setCantidad(itemDTO.getCantidad());
            item.setPrecioUnitario(itemDTO.getPrecioUnitario());
            items.add(item);

            try {
                if (movimientoDTO.getTipo() == TipoMovimiento.ENTRADA) {
                    stockService.ajustarCantidad(movimientoDTO.getBodegaDestinoId(), itemDTO.getProductoId(), itemDTO.getCantidad(), true);
                } else if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA) {
                    stockService.ajustarCantidad(movimientoDTO.getBodegaOrigenId(), itemDTO.getProductoId(), itemDTO.getCantidad(), false);
                } else if (movimientoDTO.getTipo() == TipoMovimiento.TRANSFERENCIA) {
                    stockService.ajustarCantidad(movimientoDTO.getBodegaOrigenId(), itemDTO.getProductoId(), itemDTO.getCantidad(), false);
                    stockService.ajustarCantidad(movimientoDTO.getBodegaDestinoId(), itemDTO.getProductoId(), itemDTO.getCantidad(), true);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error al ajustar el stock: " + e.getMessage());
            }
        }
        movimiento.setItems(items);
        return movimientoRepository.save(movimiento); // Guardar nuevamente con los ítems
    }

    @Override
    @Transactional
    public Movimiento actualizarMovimiento(Long id, MovimientoCreateDTO movimientoDTO) {
        Movimiento existente = movimientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado."));

        if (movimientoDTO.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }
        if (movimientoDTO.getUsuarioId() == null) {
            throw new IllegalArgumentException("El usuario es obligatorio.");
        }
        if (movimientoDTO.getItems() == null || movimientoDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un ítem.");
        }

        revertStock(existente);

        User usuario = userRepository.findById(movimientoDTO.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        Bodega bodegaOrigen = movimientoDTO.getBodegaOrigenId() != null ?
                bodegaRepository.findById(movimientoDTO.getBodegaOrigenId())
                        .orElseThrow(() -> new IllegalArgumentException("Bodega origen no encontrada.")) : null;
        Bodega bodegaDestino = movimientoDTO.getBodegaDestinoId() != null ?
                bodegaRepository.findById(movimientoDTO.getBodegaDestinoId())
                        .orElseThrow(() -> new IllegalArgumentException("Bodega destino no encontrada.")) : null;

        existente.setTipo(movimientoDTO.getTipo());
        existente.setBodegaOrigen(bodegaOrigen);
        existente.setBodegaDestino(bodegaDestino);
        existente.setUsuario(usuario);
        existente.setFecha(LocalDateTime.now());
        if (existente.getItems() == null) {
            existente.setItems(new ArrayList<>());
        }
        existente.getItems().clear();

        for (MovimientoCreateDTO.MovimientoItemDTO itemDTO : movimientoDTO.getItems()) {
            if (itemDTO.getProductoId() == null || itemDTO.getCantidad() == null || itemDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("Producto y cantidad son obligatorios y deben ser mayores que 0.");
            }
            if (itemDTO.getPrecioUnitario() == null) {
                throw new IllegalArgumentException("El precio unitario es obligatorio.");
            }
            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
            MovimientoItem item = new MovimientoItem();
            item.setMovimiento(existente);
            item.setProducto(producto);
            item.setCantidad(itemDTO.getCantidad());
            item.setPrecioUnitario(itemDTO.getPrecioUnitario());
            existente.getItems().add(item);

            try {
                if (movimientoDTO.getTipo() == TipoMovimiento.ENTRADA) {
                    stockService.ajustarCantidad(movimientoDTO.getBodegaDestinoId(), itemDTO.getProductoId(), itemDTO.getCantidad(), true);
                } else if (movimientoDTO.getTipo() == TipoMovimiento.SALIDA) {
                    stockService.ajustarCantidad(movimientoDTO.getBodegaOrigenId(), itemDTO.getProductoId(), itemDTO.getCantidad(), false);
                } else if (movimientoDTO.getTipo() == TipoMovimiento.TRANSFERENCIA) {
                    stockService.ajustarCantidad(movimientoDTO.getBodegaOrigenId(), itemDTO.getProductoId(), itemDTO.getCantidad(), false);
                    stockService.ajustarCantidad(movimientoDTO.getBodegaDestinoId(), itemDTO.getProductoId(), itemDTO.getCantidad(), true);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error al ajustar el stock: " + e.getMessage());
            }
        }

        return movimientoRepository.save(existente);
    }

    @Override
    @Transactional
    public boolean eliminarMovimiento(Long id) {
        Optional<Movimiento> movimientoOpt = movimientoRepository.findById(id);
        if (movimientoOpt.isPresent()) {
            revertStock(movimientoOpt.get());
            movimientoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> listarUltimos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        return movimientoRepository.findAllByOrderByFechaDesc(PageRequest.of(0, cantidad));
    }

    private void revertStock(Movimiento movimiento) {
        if (movimiento == null || movimiento.getItems() == null) return;
        for (MovimientoItem item : movimiento.getItems()) {
            if (item == null || item.getProducto() == null) continue;
            Long bodegaOrigenId = movimiento.getBodegaOrigen() != null ? movimiento.getBodegaOrigen().getId() : null;
            Long bodegaDestinoId = movimiento.getBodegaDestino() != null ? movimiento.getBodegaDestino().getId() : null;

            if (movimiento.getTipo() == TipoMovimiento.ENTRADA && bodegaDestinoId != null) {
                stockService.ajustarCantidad(bodegaDestinoId, item.getProducto().getId(), item.getCantidad(), false);
            } else if (movimiento.getTipo() == TipoMovimiento.SALIDA && bodegaOrigenId != null) {
                stockService.ajustarCantidad(bodegaOrigenId, item.getProducto().getId(), item.getCantidad(), true);
            } else if (movimiento.getTipo() == TipoMovimiento.TRANSFERENCIA && bodegaOrigenId != null && bodegaDestinoId != null) {
                stockService.ajustarCantidad(bodegaOrigenId, item.getProducto().getId(), item.getCantidad(), true);
                stockService.ajustarCantidad(bodegaDestinoId, item.getProducto().getId(), item.getCantidad(), false);
            }
        }
    }
}