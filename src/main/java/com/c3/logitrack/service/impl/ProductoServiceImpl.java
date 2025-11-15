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
        try {
            List<Producto> productos = productoRepository.findAll();
            System.out.println("Total de productos recuperados: " + productos.size()); // Depuración
            // Limpiar relaciones para evitar serialización cíclica
            productos.forEach(p -> {
                if (p.getStocks() != null) {
                    p.setStocks(null);
                }
                if (p.getMovimientoItems() != null) {
                    p.setMovimientoItems(null);
                }
            });
            return productos;
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            throw new RuntimeException("Error al cargar la lista de productos", e);
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            System.out.println("Producto encontrado con ID " + id + ": " + producto.map(Producto::getNombre).orElse("No encontrado")); // Depuración
            producto.ifPresent(p -> {
                if (p.getStocks() != null) p.setStocks(null);
                if (p.getMovimientoItems() != null) p.setMovimientoItems(null);
            });
            return producto;
        } catch (Exception e) {
            System.err.println("Error al buscar producto por ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Error al buscar producto", e);
        }
    }

    @Override
    public Producto guardar(Producto producto) {
        try {
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto es obligatorio");
            }
            if (producto.getPrecio() == null) {
                throw new IllegalArgumentException("El precio del producto es obligatorio");
            }
            Producto savedProducto = productoRepository.save(producto);
            System.out.println("Producto guardado con ID: " + savedProducto.getId());
            return savedProducto;
        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            throw new RuntimeException("Error al guardar el producto", e);
        }
    }

    @Override
    public Producto actualizar(Long id, Producto producto) {
        try {
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
                p.setStock(producto.getStock());
                Producto updatedProducto = productoRepository.save(p);
                System.out.println("Producto actualizado con ID: " + id);
                return updatedProducto;
            }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        } catch (Exception e) {
            System.err.println("Error al actualizar producto con ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Error al actualizar el producto", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        try {
            if (!productoRepository.existsById(id)) {
                throw new RuntimeException("Producto no encontrado con ID: " + id);
            }
            productoRepository.deleteById(id);
            System.out.println("Producto eliminado con ID: " + id);
        } catch (Exception e) {
            System.err.println("Error al eliminar producto con ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Error al eliminar el producto", e);
        }
    }
}