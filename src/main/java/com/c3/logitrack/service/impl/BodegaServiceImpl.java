package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.repository.BodegaRepository;
import com.c3.logitrack.service.BodegaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;

    public BodegaServiceImpl(BodegaRepository bodegaRepository) {
        this.bodegaRepository = bodegaRepository;
    }

    @Override
    public Bodega crearBodega(Bodega bodega) {
        return bodegaRepository.save(bodega);
    }

    @Override
    public List<Bodega> listarBodegas() {
        return bodegaRepository.findAll();
    }

    @Override
    public Bodega obtenerBodegaPorId(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada con ID: " + id));
    }

    @Override
    public Bodega actualizarBodega(Long id, Bodega bodega) {
        Bodega existente = obtenerBodegaPorId(id);
        existente.setNombre(bodega.getNombre());
        existente.setUbicacion(bodega.getUbicacion());
        existente.setCapacidad(bodega.getCapacidad());
        existente.setEncargado(bodega.getEncargado());
        return bodegaRepository.save(existente);
    }

    @Override
    public void eliminarBodega(Long id) {
        bodegaRepository.deleteById(id);
    }
}
