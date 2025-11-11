package com.c3.logitrack.service;

import com.c3.logitrack.model.Movimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoService {
    Movimiento registrarMovimiento(Movimiento movimiento);
    Optional<Movimiento> obtenerPorId(Long id);
    List<Movimiento> buscarPorRango(LocalDateTime desde, LocalDateTime hasta);
    List<Movimiento> listarTodos();
}