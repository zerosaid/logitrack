package com.c3.logitrack.service;

import com.c3.logitrack.model.Movimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface MovimientoService {

    List<Movimiento> listarTodos();

    Optional<Movimiento> obtenerPorId(Long id);

    List<Movimiento> buscarPorRango(LocalDateTime desde, LocalDateTime hasta);

    Movimiento registrarMovimiento(Movimiento movimiento);

    List<Movimiento> buscarPorTipo(String upperCase);

    boolean eliminarMovimiento(Long id);

    Movimiento actualizarMovimiento(Long id, Movimiento movimiento);
}