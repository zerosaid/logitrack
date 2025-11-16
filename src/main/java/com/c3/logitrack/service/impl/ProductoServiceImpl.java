package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Producto;
import com.c3.logitrack.repository.ProductoRepository;
import com.c3.logitrack.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> listarTodos() {
        List<Producto> productos = productoRepository.findAll();
        productos.forEach(p -> {
            p.setStocks(null);
            p.setMovimientoItems(null);
        });
        return productos;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        producto.ifPresent(p -> {
            p.setStocks(null);
            p.setMovimientoItems(null);
        });
        return producto;
    }

    @Override
    public Producto guardar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null) {
            throw new IllegalArgumentException("El precio del producto es obligatorio");
        }
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Long id, Producto producto) {
        return productoRepository.findById(id).map(p -> {
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto es obligatorio");
            }
            if (producto.getPrecio() == null) {
                throw new IllegalArgumentException("El precio del producto es obligatorio");
            }

            p.setNombre(producto.getNombre());
            p.setCategoria(producto.getCategoria());
            p.setPrecio(producto.getPrecio());
            p.setStocks(producto.getStocks());
            return productoRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Override
    public boolean eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }
}
