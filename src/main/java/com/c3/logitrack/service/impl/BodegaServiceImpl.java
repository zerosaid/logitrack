package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Bodega;
import com.c3.logitrack.repository.BodegaRepository;
import com.c3.logitrack.service.BodegaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public Bodega obtenerBodegaPorId(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bodega no encontrada con ID: " + id));
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
        if (!bodegaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se puede eliminar: la bodega no existe.");
        }
        bodegaRepository.deleteById(id);
    }
}
