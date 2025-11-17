package com.c3.logitrack.service;

import com.c3.logitrack.dto.BodegaCreateDTO;
import com.c3.logitrack.model.Bodega;
import java.util.List;
import java.util.Optional;

public interface BodegaService {
    Bodega crearBodega(BodegaCreateDTO bodegaDTO);
    List<Bodega> listarBodegas();
    Optional<Bodega> obtenerBodegaPorId(Long id);
    Optional<Bodega> actualizarBodega(Long id, BodegaCreateDTO bodegaDTO);
    boolean eliminarBodega(Long id);
}