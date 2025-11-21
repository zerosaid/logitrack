package com.c3.logitrack.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReporteService {

    /**
     * Obtiene una lista de movimientos filtrados por un rango de fechas.
     * @param inicio Fecha y hora de inicio del rango.
     * @param fin Fecha y hora de fin del rango.
     * @return Lista de mapas con los detalles de los movimientos.
     */
    List<Map<String, Object>> getMovimientosPorFecha(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene un resumen del stock por bodega.
     * @return Mapa donde la clave es el nombre de la bodega y el valor es la cantidad total.
     */
    Map<String, Integer> getStockPorBodega();

    /**
     * Obtiene una lista de auditorías registradas.
     * @return Lista de mapas con los detalles de las auditorías.
     */
    List<Map<String, Object>> getAuditoria();

    static Object getMovimientoRecientes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMovimientoRecientes'");
    }
}