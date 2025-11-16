package com.c3.logitrack.service;

import com.c3.logitrack.model.Bodega;
import java.util.List;
import java.util.Optional;

public interface BodegaService {

    /**
     * Crea una nueva bodega.
     * @param bodega Objeto Bodega a crear
     * @return Bodega creada
     */
    Bodega crearBodega(Bodega bodega);

    /**
     * Obtiene la lista de todas las bodegas registradas.
     * @return Lista de bodegas
     */
    List<Bodega> listarBodegas();

    /**
     * Busca una bodega por su identificador.
     * @param id ID de la bodega
     * @return Optional con la bodega encontrada
     */
    Optional<Bodega> obtenerBodegaPorId(Long id);

    /**
     * Actualiza una bodega existente.
     * @param id ID de la bodega a actualizar
     * @param bodega Datos actualizados
     * @return Optional con la bodega actualizada
     */
    Optional<Bodega> actualizarBodega(Long id, Bodega bodega);

    /**
     * Elimina una bodega por su ID.
     * @param id ID de la bodega a eliminar
     * @return true si se eliminó correctamente, false si no existía
     */
    boolean eliminarBodega(Long id);
}
