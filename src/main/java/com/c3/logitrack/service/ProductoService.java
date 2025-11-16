package com.c3.logitrack.service;

import com.c3.logitrack.dto.ProductoCreateDTO;
import com.c3.logitrack.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    Producto crearProducto(ProductoCreateDTO productoDTO);
    List<Producto> listarProductos();
    Optional<Producto> obtenerProductoPorId(Long id);
    Optional<Producto> actualizarProducto(Long id, ProductoCreateDTO productoDTO);
    boolean eliminarProducto(Long id);
}