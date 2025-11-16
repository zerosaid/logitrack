package com.c3.logitrack.service.impl;

import com.c3.logitrack.model.Movimiento;
import com.c3.logitrack.model.Stock;
import com.c3.logitrack.model.Auditoria; // Asegúrate de tener esta entidad si la usas
import com.c3.logitrack.repository.MovimientoRepository;
import com.c3.logitrack.repository.StockRepository;
import com.c3.logitrack.repository.AuditoriaRepository; // Placeholder, crea si lo necesitas
import com.c3.logitrack.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository; // Placeholder, ajusta según tu proyecto

    @Override
    public List<Map<String, Object>> getMovimientosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("El rango de fechas es inválido: 'inicio' debe ser anterior a 'fin'.");
        }

        List<Movimiento> movimientos = movimientoRepository.findByFechaBetween(inicio, fin);
        return movimientos.stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", m.getId());
                    map.put("tipo", m.getTipo());
                    map.put("bodegaOrigen", m.getBodegaOrigen() != null ? m.getBodegaOrigen().getNombre() : "Sin Origen");
                    map.put("bodegaDestino", m.getBodegaDestino() != null ? m.getBodegaDestino().getNombre() : "Sin Destino");
                    map.put("usuario", m.getUsuario() != null ? m.getUsuario().getNombre() : "Sin Usuario");
                    map.put("fecha", m.getFecha());
                    return map;
                })
                .toList();
    }

    @Override
    public Map<String, Integer> getStockPorBodega() {
        List<Stock> stocks = stockRepository.findAll();
        Map<String, Integer> stockMap = new HashMap<>();
        for (Stock s : stocks) {
            String bodegaNombre = s.getBodega() != null ? s.getBodega().getNombre() : "Sin Bodega";
            stockMap.put(bodegaNombre, stockMap.getOrDefault(bodegaNombre, 0) + s.getCantidad());
        }
        return stockMap;
    }

    @Override
    public List<Map<String, Object>> getAuditoria() {
        // Placeholder: Implementa con tu repositorio de auditoría
        // Ejemplo: List<Auditoria> auditorias = auditoriaRepository.findAll();
        List<Auditoria> auditorias = auditoriaRepository.findAll(); // Ajusta según tu implementación
        return auditorias.stream()
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getId());
                    map.put("entidad", a.getEntidad());
                    map.put("operacion", a.getOperacion());
                    map.put("fecha", a.getFechaHora());
                    return map;
                })
                .toList();
    }
}