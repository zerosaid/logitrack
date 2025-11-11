package com.c3.logitrack.service;

import com.c3.logitrack.model.Bodega;
import java.util.List;

public interface BodegaService {
    Bodega crearBodega(Bodega bodega);
    List<Bodega> listarBodegas();
    Bodega obtenerBodegaPorId(Long id);
    Bodega actualizarBodega(Long id, Bodega bodega);
    void eliminarBodega(Long id);
}