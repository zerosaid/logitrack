package com.c3.logitrack.service.impl;

import com.c3.logitrack.dto.ProductoCreateDTO;
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
    public Producto crearProducto(ProductoCreateDTO productoDTO) {
        if (productoDTO.getCodigo() == null || productoDTO.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        if (productoDTO.getNombre() == null || productoDTO.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (productoDTO.getCategoria() == null || productoDTO.getCategoria().isBlank()) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria.");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a 0.");
        }
        if (productoDTO.getStockMin() == null || productoDTO.getStockMin() < 0) {
            throw new IllegalArgumentException("El stock mínimo debe ser mayor o igual a 0.");
        }

        Producto producto = new Producto();
        producto.setCodigo(productoDTO.getCodigo());
        producto.setNombre(productoDTO.getNombre());
        producto.setCategoria(productoDTO.getCategoria());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStockMin(productoDTO.getStockMin());
        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> actualizarProducto(Long id, ProductoCreateDTO productoDTO) {
        if (productoDTO.getCodigo() == null || productoDTO.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        if (productoDTO.getNombre() == null || productoDTO.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (productoDTO.getCategoria() == null || productoDTO.getCategoria().isBlank()) {
            throw new IllegalArgumentException("La categoría del producto es obligatoria.");
        }
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a 0.");
        }
        if (productoDTO.getStockMin() == null || productoDTO.getStockMin() < 0) {
            throw new IllegalArgumentException("El stock mínimo debe ser mayor o igual a 0.");
        }

        return productoRepository.findById(id).map(existente -> {
            existente.setCodigo(productoDTO.getCodigo());
            existente.setNombre(productoDTO.getNombre());
            existente.setCategoria(productoDTO.getCategoria());
            existente.setPrecio(productoDTO.getPrecio());
            existente.setStockMin(productoDTO.getStockMin());
            return productoRepository.save(existente);
        });
    }

    @Override
    public boolean eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}