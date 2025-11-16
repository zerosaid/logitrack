package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.repository.BodegaRepository;
import com.c3.logitrack.service.BodegaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        if (bodega.getNombre() == null || bodega.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la bodega es obligatorio.");
        }
        return bodegaRepository.save(bodega);
    }

    @Override
    public List<Bodega> listarBodegas() {
        return bodegaRepository.findAll();
    }

    @Override
    public Optional<Bodega> obtenerBodegaPorId(Long id) {
        return bodegaRepository.findById(id);
    }

    @Override
    public Optional<Bodega> actualizarBodega(Long id, Bodega bodega) {
        return bodegaRepository.findById(id).map(existente -> {
            existente.setNombre(bodega.getNombre());
            existente.setUbicacion(bodega.getUbicacion());
            existente.setCapacidad(bodega.getCapacidad());
            existente.setEncargado(bodega.getEncargado());
            return bodegaRepository.save(existente);
        });
    }

    @Override
    public boolean eliminarBodega(Long id) {
        if (bodegaRepository.existsById(id)) {
            bodegaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

