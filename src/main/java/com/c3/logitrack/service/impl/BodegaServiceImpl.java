package com.c3.logitrack.service.impl;

import com.c3.logitrack.dto.BodegaCreateDTO;
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
    public Bodega crearBodega(BodegaCreateDTO bodegaDTO) {
        if (bodegaDTO.getNombre() == null || bodegaDTO.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la bodega es obligatorio.");
        }
        if (bodegaDTO.getUbicacion() == null || bodegaDTO.getUbicacion().isBlank()) {
            throw new IllegalArgumentException("La ubicación de la bodega es obligatoria.");
        }
        if (bodegaDTO.getCapacidad() == null || bodegaDTO.getCapacidad() < 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor o igual a 0.");
        }

        Bodega bodega = new Bodega();
        bodega.setNombre(bodegaDTO.getNombre());
        bodega.setUbicacion(bodegaDTO.getUbicacion());
        bodega.setCapacidad(bodegaDTO.getCapacidad());
        bodega.setEncargado(bodegaDTO.getEncargado());
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
    public Optional<Bodega> actualizarBodega(Long id, BodegaCreateDTO bodegaDTO) {
        if (bodegaDTO.getNombre() == null || bodegaDTO.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la bodega es obligatorio.");
        }
        if (bodegaDTO.getUbicacion() == null || bodegaDTO.getUbicacion().isBlank()) {
            throw new IllegalArgumentException("La ubicación de la bodega es obligatoria.");
        }
        if (bodegaDTO.getCapacidad() == null || bodegaDTO.getCapacidad() < 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor o igual a 0.");
        }

        return bodegaRepository.findById(id).map(existente -> {
            existente.setNombre(bodegaDTO.getNombre());
            existente.setUbicacion(bodegaDTO.getUbicacion());
            existente.setCapacidad(bodegaDTO.getCapacidad());
            existente.setEncargado(bodegaDTO.getEncargado());
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