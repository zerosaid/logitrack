package com.c3.logitrack.service;

import com.c3.logitrack.model.Bodega;
import java.util.List;

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
     * @return Bodega encontrada o null si no existe
     */
    Bodega obtenerBodegaPorId(Long id);

    /**
     * Actualiza una bodega existente.
     * @param id ID de la bodega a actualizar
     * @param bodega Datos actualizados
     * @return Bodega actualizada
     */
    Bodega actualizarBodega(Long id, Bodega bodega);

    /**
     * Elimina una bodega por su ID.
     * @param id ID de la bodega a eliminar
     */
    void eliminarBodega(Long id);
}
