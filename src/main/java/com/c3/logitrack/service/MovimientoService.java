package com.c3.logitrack.service;

import com.c3.logitrack.dto.MovimientoCreateDTO;
import com.c3.logitrack.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoService {
    /**
     * Obtiene una lista de todos los movimientos registrados.
     * @return Lista de objetos Movimiento.
     */
    List<Movimiento> listarMovimientos();

    /**
     * Obtiene un movimiento específico por su ID.
     * @param id El ID del movimiento a buscar.
     * @return Un Optional conteniendo el movimiento si existe, o vacío si no.
     */
    Optional<Movimiento> obtenerPorId(Long id);

    /**
     * Busca movimientos dentro de un rango de fechas especificado.
     * @param inicio Fecha y hora de inicio del rango.
     * @param fin Fecha y hora de fin del rango.
     * @return Lista de movimientos dentro del rango especificado.
     * @throws IllegalArgumentException Si las fechas son inválidas (fin antes de inicio).
     */
    List<Movimiento> buscarPorRango(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca movimientos por tipo.
     * @param tipo El tipo de movimiento (e.g., ENTRADA, SALIDA, TRANSFERENCIA).
     * @return Lista de movimientos que coinciden con el tipo especificado.
     * @throws IllegalArgumentException Si el tipo es inválido.
     */
    List<Movimiento> buscarPorTipo(String tipo);

    /**
     * Registra un nuevo movimiento en el sistema.
     * @param movimientoDTO Objeto DTO con los datos del movimiento a registrar.
     * @return El movimiento registrado.
     * @throws IllegalArgumentException Si los datos del DTO son inválidos o incompletos.
     */
    Movimiento registrarMovimiento(MovimientoCreateDTO movimientoDTO);

    /**
     * Actualiza un movimiento existente.
     * @param id El ID del movimiento a actualizar.
     * @param movimientoDTO Objeto DTO con los nuevos datos del movimiento.
     * @return El movimiento actualizado.
     * @throws IllegalArgumentException Si el movimiento no existe o los datos son inválidos.
     */
    Movimiento actualizarMovimiento(Long id, MovimientoCreateDTO movimientoDTO);

    /**
     * Elimina un movimiento por su ID.
     * @param id El ID del movimiento a eliminar.
     * @return true si se eliminó con éxito, false si no se encontró.
     */
    boolean eliminarMovimiento(Long id);

    /**
     * Obtiene una lista de los últimos movimientos según la cantidad especificada.
     * @param cantidad Número de movimientos a devolver.
     * @return Lista de los últimos movimientos ordenados por fecha descendente.
     * @throws IllegalArgumentException Si la cantidad es menor o igual a 0.
     */
    List<Movimiento> listarUltimos(int cantidad);
}